package com.ehtisham.splitsync.application.port.input;

import com.ehtisham.splitsync.domain.model.UserBalance;

import java.util.List;

public interface BalanceUseCase {
    List<UserBalance> getMyBalances(Long currentUserId);           // all friends
    UserBalance getBalanceWithFriend(Long currentUserId, Long friendId);
    com.ehtisham.splitsync.application.dto.response.FriendStatsResponse getFriendStats(Long currentUserId);
    java.util.List<com.ehtisham.splitsync.application.dto.response.FriendBalanceResponse> getFriendBalances(Long currentUserId);
}
