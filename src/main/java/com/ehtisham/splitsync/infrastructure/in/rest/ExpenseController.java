package com.ehtisham.splitsync.infrastructure.in.rest;

import com.ehtisham.splitsync.application.dto.request.CreateExpenseRequest;
import com.ehtisham.splitsync.application.dto.request.UpdateExpenseRequest;
import com.ehtisham.splitsync.application.dto.response.ApiResponse;
import com.ehtisham.splitsync.application.dto.response.ExpenseResponse;
import com.ehtisham.splitsync.application.port.input.CurrentUserProvider;
import com.ehtisham.splitsync.application.port.input.ExpenseUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "Expense creation and management")
public class ExpenseController {

    private final ExpenseUseCase expenseUseCase;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    @Operation(summary = "Create a new expense")
    public ResponseEntity<ApiResponse<Void>> createExpense(
            @Valid @RequestBody CreateExpenseRequest request) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        expenseUseCase.createExpense(currentUserId, request);
        return ResponseEntity.ok(ApiResponse.success("Expense created"));
    }

    @GetMapping
    @Operation(summary = "Get my expenses (paginated)")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getMyExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        List<ExpenseResponse> expenses = expenseUseCase.getMyExpenses(currentUserId, page, size);
        return ResponseEntity.ok(ApiResponse.success(expenses));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get expense by ID with calculated amounts")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpense(@PathVariable Long id) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        ExpenseResponse response = expenseUseCase.getExpense(currentUserId, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an expense")
    public ResponseEntity<ApiResponse<Void>> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody CreateExpenseRequest request) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        expenseUseCase.updateExpense(currentUserId, id, request);
        return ResponseEntity.ok(ApiResponse.success("Expense updated"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an expense")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable Long id) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        expenseUseCase.deleteExpense(currentUserId, id);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted"));
    }
}
