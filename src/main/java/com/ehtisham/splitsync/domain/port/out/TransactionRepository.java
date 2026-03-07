package com.ehtisham.splitsync.domain.port.out;

import com.ehtisham.splitsync.domain.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(Long id);
    List<Transaction> findBetweenUsers(Long userA, Long userB);
    void markCompleted(Long id, LocalDateTime completedAt);
}
