package com.ehtisham.splitsync.domain.port.out;

import com.ehtisham.splitsync.domain.model.FriendRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository {
    FriendRequest save(FriendRequest request);
    Optional<FriendRequest> findById(Long id);
    Optional<FriendRequest> findPending(Long fromUser, Long toUser);
    List<FriendRequest> findIncomingPending(Long toUser);
    List<FriendRequest> findOutgoingPending(Long fromUser);
    void updateStatus(Long id, String status, LocalDateTime updatedAt);
}
