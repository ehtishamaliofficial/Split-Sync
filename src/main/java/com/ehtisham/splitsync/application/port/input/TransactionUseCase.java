package com.ehtisham.splitsync.application.port.input;

import com.ehtisham.splitsync.application.dto.request.CreateTransactionRequest;
import com.ehtisham.splitsync.application.dto.response.TransactionResponse;

import java.util.List;

public interface TransactionUseCase {
    TransactionResponse createSettlement(Long currentUserId, CreateTransactionRequest request);
    TransactionResponse completeSettlement(Long currentUserId, Long transactionId);
    List<TransactionResponse> getTransactionsBetween(Long currentUserId, Long friendId);
}
