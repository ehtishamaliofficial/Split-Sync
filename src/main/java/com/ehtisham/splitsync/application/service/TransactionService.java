package com.ehtisham.splitsync.application.service;

import com.ehtisham.splitsync.application.dto.request.CreateTransactionRequest;
import com.ehtisham.splitsync.application.dto.response.TransactionResponse;
import com.ehtisham.splitsync.application.port.input.TransactionUseCase;
import com.ehtisham.splitsync.domain.exception.InvalidRequestException;
import com.ehtisham.splitsync.domain.model.Transaction;
import com.ehtisham.splitsync.domain.model.User;
import com.ehtisham.splitsync.domain.port.out.FriendshipRepository;
import com.ehtisham.splitsync.domain.port.out.TransactionRepository;
import com.ehtisham.splitsync.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService implements TransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    @Override
    public TransactionResponse createSettlement(Long currentUserId, CreateTransactionRequest request) {
        if (!friendshipRepository.exists(currentUserId, request.getPayeeId())) {
            throw new InvalidRequestException("friend.not.found", HttpStatus.NOT_FOUND);
        }

        Transaction transaction = Transaction.builder()
                .payerId(currentUserId)
                .payeeId(request.getPayeeId())
                .amount(request.getAmount())
                .expenseId(request.getExpenseId())
                .notes(request.getNotes())
                .status("PENDING")
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return toResponse(saved);
    }

    @Override
    public TransactionResponse completeSettlement(Long currentUserId, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new InvalidRequestException("transaction.not.found", HttpStatus.NOT_FOUND));

        if (!transaction.getPayerId().equals(currentUserId)) {
            throw new InvalidRequestException("transaction.unauthorized", HttpStatus.FORBIDDEN);
        }

        if ("COMPLETED".equals(transaction.getStatus())) {
            throw new InvalidRequestException("transaction.already.completed");
        }

        LocalDateTime completedAt = LocalDateTime.now();
        transactionRepository.markCompleted(transactionId, completedAt);
        transaction.setStatus("COMPLETED");
        transaction.setCompletedAt(completedAt);
        return toResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getTransactionsBetween(Long currentUserId, Long friendId) {
        return transactionRepository.findBetweenUsers(currentUserId, friendId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse toResponse(Transaction t) {
        User payer = userRepository.findById(t.getPayerId()).orElse(null);
        User payee = userRepository.findById(t.getPayeeId()).orElse(null);
        return TransactionResponse.builder()
                .id(t.getId())
                .payerId(t.getPayerId())
                .payerName(payer != null ? payer.getName() : null)
                .payeeId(t.getPayeeId())
                .payeeName(payee != null ? payee.getName() : null)
                .amount(t.getAmount())
                .expenseId(t.getExpenseId())
                .status(t.getStatus())
                .notes(t.getNotes())
                .createdAt(t.getCreatedAt())
                .completedAt(t.getCompletedAt())
                .build();
    }
}
