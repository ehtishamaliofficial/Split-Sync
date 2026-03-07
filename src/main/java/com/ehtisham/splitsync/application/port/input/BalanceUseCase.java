package com.ehtisham.splitsync.application.port.input;

import com.ehtisham.splitsync.domain.model.UserBalance;

import java.util.List;

public interface BalanceUseCase {
    List<UserBalance> getMyBalances(Long currentUserId);           // all friends
    UserBalance getBalanceWithFriend(Long currentUserId, Long friendId);
}
