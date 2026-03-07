package com.ehtisham.splitsync.domain.port.out;

import com.ehtisham.splitsync.domain.model.Expense;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository {
    Expense save(Expense expense);
    Optional<Expense> findById(Long id);
    List<Expense> findByCreatedBy(Long userId, int limit, int offset);
    List<Expense> findSharedBetween(Long userA, Long userB);  // for balance calc
    void deleteById(Long id);
}
