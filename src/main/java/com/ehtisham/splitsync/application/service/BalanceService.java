package com.ehtisham.splitsync.application.service;

import com.ehtisham.splitsync.application.port.input.BalanceUseCase;
import com.ehtisham.splitsync.domain.model.User;
import com.ehtisham.splitsync.domain.model.UserBalance;
import com.ehtisham.splitsync.domain.port.out.BalanceCalculator;
import java.math.BigDecimal;
import com.ehtisham.splitsync.domain.port.out.FriendshipRepository;
import com.ehtisham.splitsync.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService implements BalanceUseCase {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final BalanceCalculator balanceCalculator;

    @Override
    public List<UserBalance> getMyBalances(Long currentUserId) {
        List<Long> friendIds = friendshipRepository.findFriendIds(currentUserId);
        return friendIds.stream()
                .map(friendId -> computeBalance(currentUserId, friendId))
                .collect(Collectors.toList());
    }

    @Override
    public UserBalance getBalanceWithFriend(Long currentUserId, Long friendId) {
        return computeBalance(currentUserId, friendId);
    }

    private UserBalance computeBalance(Long currentUserId, Long friendId) {
        BigDecimal net = balanceCalculator.calculateNet(currentUserId, friendId);
        User friend = userRepository.findById(friendId).orElse(null);
        return UserBalance.builder()
                .friendId(friendId)
                .friendName(friend != null ? friend.getName() : null)
                .friendAvatarUrl(friend != null ? friend.getAvatarUrl() : null)
                .netAmount(net)
                .currency("USD")
                .build();
    }
}
