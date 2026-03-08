package com.ehtisham.splitsync.application.service;

import com.ehtisham.splitsync.application.dto.response.UserSearchResponse;
import com.ehtisham.splitsync.application.port.input.CurrentUserProvider;
import com.ehtisham.splitsync.application.port.input.UserUseCase;
import com.ehtisham.splitsync.domain.model.User;
import com.ehtisham.splitsync.domain.port.out.UserRepository;
import com.ehtisham.splitsync.domain.port.out.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserUseCase {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public List<UserSearchResponse> searchUsers(String query, int limit) {
        try {
            Long currentUserId = currentUserProvider.getCurrentUserId();
            List<Long> friendIds = friendshipRepository.findFriendIds(currentUserId);
            List<User> users = userRepository.searchByUsernameOrEmail(query, currentUserId, limit);
            
            return users.stream()
                    .filter(user -> !friendIds.contains(user.getId()))
                    .map(user -> UserSearchResponse.builder()
                            .userId(user.getId())
                            .userName(user.getName())
                            .email(user.getEmail())
                            .avatarUrl(user.getAvatarUrl())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching users with query: {}", query, e);
            return List.of();
        }
    }
}
