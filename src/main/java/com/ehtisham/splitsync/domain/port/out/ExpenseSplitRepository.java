package com.ehtisham.splitsync.domain.port.out;

import com.ehtisham.splitsync.domain.model.ExpenseSplit;

import java.util.List;

public interface ExpenseSplitRepository {
    void saveAll(List<ExpenseSplit> splits);
    List<ExpenseSplit> findByExpenseId(Long expenseId);
    void deleteByExpenseId(Long expenseId);
}
