package com.ehtisham.splitsync.infrastructure.in.rest;

import com.ehtisham.splitsync.application.dto.request.CreateTransactionRequest;
import com.ehtisham.splitsync.application.dto.response.ApiResponse;
import com.ehtisham.splitsync.application.dto.response.TransactionResponse;
import com.ehtisham.splitsync.application.port.input.CurrentUserProvider;
import com.ehtisham.splitsync.application.port.input.TransactionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Settlement transaction management")
public class TransactionController {

    private final TransactionUseCase transactionUseCase;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    @Operation(summary = "Create a settlement transaction")
    public ResponseEntity<ApiResponse<TransactionResponse>> createSettlement(
            @Valid @RequestBody CreateTransactionRequest request) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        TransactionResponse response = transactionUseCase.createSettlement(currentUserId, request);
        return ResponseEntity.ok(ApiResponse.success("Settlement created", response));
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Mark a settlement as completed")
    public ResponseEntity<ApiResponse<TransactionResponse>> completeSettlement(@PathVariable Long id) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        TransactionResponse response = transactionUseCase.completeSettlement(currentUserId, id);
        return ResponseEntity.ok(ApiResponse.success("Settlement completed", response));
    }

    @GetMapping("/with/{friendId}")
    @Operation(summary = "Get all settlements between current user and a friend")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsWith(
            @PathVariable Long friendId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        List<TransactionResponse> transactions = transactionUseCase.getTransactionsBetween(currentUserId, friendId);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }
}
