package com.ehtisham.splitsync.application.service;

import com.ehtisham.splitsync.application.dto.response.FriendRequestResponse;
import com.ehtisham.splitsync.application.dto.response.FriendSummaryResponse;
import com.ehtisham.splitsync.application.port.input.FriendUseCase;
import com.ehtisham.splitsync.domain.exception.InvalidRequestException;
import com.ehtisham.splitsync.domain.model.FriendRequest;
import com.ehtisham.splitsync.domain.model.User;
import com.ehtisham.splitsync.domain.port.out.FriendRequestRepository;
import com.ehtisham.splitsync.domain.port.out.FriendshipRepository;
import com.ehtisham.splitsync.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendService implements FriendUseCase {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    @Override
    public void sendFriendRequest(Long fromUserId, String email) {
        // Find target user by email
        User toUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidRequestException("friend.not.found", HttpStatus.NOT_FOUND));
        
        Long toUserId = toUser.getId();

        if (fromUserId.equals(toUserId)) {
            throw new InvalidRequestException("friend.request.self");
        }

        // Check if already friends
        if (friendshipRepository.exists(fromUserId, toUserId)) {
            throw new InvalidRequestException("friend.already.exists");
        }

        // Check if a pending request already exists (either direction)
        Optional<FriendRequest> existing = friendRequestRepository.findPending(fromUserId, toUserId);
        if (existing.isPresent()) {
            throw new InvalidRequestException("friend.request.exists");
        }
        Optional<FriendRequest> reverse = friendRequestRepository.findPending(toUserId, fromUserId);
        if (reverse.isPresent()) {
            throw new InvalidRequestException("friend.request.exists");
        }

        FriendRequest request = FriendRequest.builder()
                .fromUser(fromUserId)
                .toUser(toUserId)
                .build();
        friendRequestRepository.save(request);
    }

    @Override
    public void respondToRequest(Long requestId, Long respondingUserId, boolean accept) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new InvalidRequestException("friend.request.not.found", HttpStatus.NOT_FOUND));

        if (!request.getToUser().equals(respondingUserId)) {
            throw new InvalidRequestException("friend.request.unauthorized", HttpStatus.FORBIDDEN);
        }

        String newStatus = accept ? "ACCEPTED" : "REJECTED";
        friendRequestRepository.updateStatus(requestId, newStatus, LocalDateTime.now());

        if (accept) {
            friendshipRepository.save(request.getFromUser(), request.getToUser());
        }
    }

    @Override
    public List<FriendRequestResponse> getPendingIncoming(Long userId) {
        List<FriendRequest> requests = friendRequestRepository.findIncomingPending(userId);
        return requests.stream()
                .map(req -> {
                    User fromUser = userRepository.findById(req.getFromUser()).orElse(null);
                    User toUser = userRepository.findById(req.getToUser()).orElse(null);
                    return FriendRequestResponse.builder()
                            .id(req.getId())
                            .fromUserId(req.getFromUser())
                            .fromUserName(fromUser != null ? fromUser.getName() : null)
                            .fromUserAvatarUrl(fromUser != null ? fromUser.getAvatarUrl() : null)
                            .toUserId(req.getToUser())
                            .toUserName(toUser != null ? toUser.getName() : null)
                            .status(req.getStatus())
                            .createdAt(req.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRequestResponse> getPendingOutgoing(Long userId) {
        List<FriendRequest> requests = friendRequestRepository.findOutgoingPending(userId);
        return requests.stream()
                .map(req -> {
                    User fromUser = userRepository.findById(req.getFromUser()).orElse(null);
                    User toUser = userRepository.findById(req.getToUser()).orElse(null);
                    return FriendRequestResponse.builder()
                            .id(req.getId())
                            .fromUserId(req.getFromUser())
                            .fromUserName(fromUser != null ? fromUser.getName() : null)
                            .fromUserAvatarUrl(fromUser != null ? fromUser.getAvatarUrl() : null)
                            .toUserId(req.getToUser())
                            .toUserName(toUser != null ? toUser.getName() : null)
                            .status(req.getStatus())
                            .createdAt(req.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public void cancelFriendRequest(Long requestId, Long fromUserId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new InvalidRequestException("friend.request.not.found", HttpStatus.NOT_FOUND));

        if (!request.getFromUser().equals(fromUserId)) {
            throw new InvalidRequestException("friend.request.unauthorized", HttpStatus.FORBIDDEN);
        }

        if (!"PENDING".equals(request.getStatus())) {
            throw new InvalidRequestException("friend.request.not.pending", HttpStatus.BAD_REQUEST);
        }

        friendRequestRepository.updateStatus(requestId, "CANCELLED", LocalDateTime.now());
    }

    @Override
    public List<FriendSummaryResponse> getFriends(Long userId) {
        List<Long> friendIds = friendshipRepository.findFriendIds(userId);
        return friendIds.stream()
                .map(friendId -> userRepository.findById(friendId).orElse(null))
                .filter(Objects::nonNull)
                .map(user -> FriendSummaryResponse.builder()
                        .userId(user.getId())
                        .userName(user.getName())
                        .email(user.getEmail())
                        .avatarUrl(user.getAvatarUrl())
                        .build())
                .collect(Collectors.toList());
    }
}
