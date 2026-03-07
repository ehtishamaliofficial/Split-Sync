package com.ehtisham.splitsync.domain.port.out;

import java.util.List;

public interface FriendshipRepository {
    void save(Long userA, Long userB);   // enforces userA < userB internally
    boolean exists(Long userA, Long userB);
    List<Long> findFriendIds(Long userId);
    void delete(Long userA, Long userB);
}
