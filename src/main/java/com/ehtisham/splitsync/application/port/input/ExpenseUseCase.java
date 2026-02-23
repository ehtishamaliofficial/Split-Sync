package com.ehtisham.splitsync.application.port.input;

import com.ehtisham.splitsync.application.dto.request.CreateExpenseRequest;
import com.ehtisham.splitsync.application.dto.request.UpdateExpenseRequest;
import com.ehtisham.splitsync.application.dto.response.ExpenseResponse;

import java.util.List;

public interface ExpenseUseCase {
    ExpenseResponse createExpense(Long currentUserId, CreateExpenseRequest request);
    ExpenseResponse getExpense(Long currentUserId, Long expenseId);
    ExpenseResponse updateExpense(Long currentUserId, Long expenseId, UpdateExpenseRequest request);
    void deleteExpense(Long currentUserId, Long expenseId);
    List<ExpenseResponse> getMyExpenses(Long currentUserId, int page, int size);
}
