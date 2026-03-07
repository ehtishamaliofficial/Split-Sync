package com.ehtisham.splitsync.infrastructure.in.rest;

import com.ehtisham.splitsync.application.dto.response.ApiResponse;
import com.ehtisham.splitsync.application.port.input.BalanceUseCase;
import com.ehtisham.splitsync.application.port.input.CurrentUserProvider;
import com.ehtisham.splitsync.domain.model.UserBalance;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/balances")
@RequiredArgsConstructor
@Tag(name = "Balances", description = "Balance calculations between friends")
public class BalanceController {

    private final BalanceUseCase balanceUseCase;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    @Operation(summary = "Get balances with all friends")
    public ResponseEntity<ApiResponse<List<UserBalance>>> getMyBalances() {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        List<UserBalance> balances = balanceUseCase.getMyBalances(currentUserId);
        return ResponseEntity.ok(ApiResponse.success(balances));
    }

    @GetMapping("/{friendId}")
    @Operation(summary = "Get balance with a specific friend")
    public ResponseEntity<ApiResponse<UserBalance>> getBalanceWithFriend(@PathVariable Long friendId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        UserBalance balance = balanceUseCase.getBalanceWithFriend(currentUserId, friendId);
        return ResponseEntity.ok(ApiResponse.success(balance));
    }
}
