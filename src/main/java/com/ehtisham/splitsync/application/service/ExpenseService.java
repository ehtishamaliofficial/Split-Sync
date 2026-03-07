package com.ehtisham.splitsync.application.service;

import com.ehtisham.splitsync.application.dto.request.CreateExpenseRequest;
import com.ehtisham.splitsync.application.dto.request.ExpenseParticipant;
import com.ehtisham.splitsync.application.dto.response.ExpenseResponse;
import com.ehtisham.splitsync.application.dto.response.ParticipantShareResponse;
import com.ehtisham.splitsync.application.port.input.ExpenseUseCase;
import com.ehtisham.splitsync.domain.exception.InvalidRequestException;
import com.ehtisham.splitsync.domain.model.Expense;
import com.ehtisham.splitsync.domain.model.ExpenseSplit;
import com.ehtisham.splitsync.domain.model.User;
import com.ehtisham.splitsync.domain.port.out.ExpenseRepository;
import com.ehtisham.splitsync.domain.port.out.ExpenseSplitRepository;
import com.ehtisham.splitsync.domain.port.out.FriendshipRepository;
import com.ehtisham.splitsync.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService implements ExpenseUseCase {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    @Override
    public void createExpense(Long currentUserId, CreateExpenseRequest request) {

        // Validate all participants are friends
        for (var participant : request.getParticipants()) {
            if (!friendshipRepository.exists(currentUserId, participant.getParticipantId()) && !participant.getParticipantId().equals(currentUserId)) {
                throw new InvalidRequestException("expense.split.participant_not_friend");
            }
        }

        Expense expense = Expense.builder()
                .description(request.getDescription())
                .totalAmount(request.getTotalAmount())
                .paidBy(currentUserId)
                .createdBy(currentUserId)
                .splitType(request.getSplitType())
                .category(request.getCategory())
                .notes(request.getNotes())
                .build();

        Expense saved = expenseRepository.save(expense);

        List<ExpenseSplit> splits = buildSplits(saved.getId(),request.getParticipants());

        expenseSplitRepository.saveAll(splits);
    }

    @Override
    public ExpenseResponse getExpense(Long currentUserId, Long expenseId) {
        Expense expense = getExpenseById(expenseId);
        List<ExpenseSplit> splits = expenseSplitRepository.findByExpenseId(expenseId);
        return buildExpenseResponse(expense, splits);
    }

    @Override
    public void updateExpense(Long currentUserId, Long expenseId, CreateExpenseRequest request) {
        Expense expense = getExpenseById(expenseId);

        if (!expense.getCreatedBy().equals(currentUserId)) {
            throw new InvalidRequestException("expense.unauthorized", HttpStatus.FORBIDDEN);
        }

        expenseSplitRepository.deleteByExpenseId(expense.getId());
        expense.setUpdatedAt(java.time.LocalDateTime.now());
        Expense saved = expenseRepository.save(expense);
        List<ExpenseSplit> splits = buildSplits(saved.getId(),request.getParticipants());
        expenseSplitRepository.saveAll(splits);
    }

    @Override
    public void deleteExpense(Long currentUserId, Long expenseId) {
        Expense expense = getExpenseById(expenseId);

        if (!expense.getCreatedBy().equals(currentUserId)) {
            throw new InvalidRequestException("expense.unauthorized", HttpStatus.FORBIDDEN);
        }

        expenseSplitRepository.deleteByExpenseId(expenseId);
        expenseRepository.deleteById(expenseId);
    }

    @Override
    public List<ExpenseResponse> getMyExpenses(Long currentUserId, int page, int size)  {
        int safeSize = Math.min(size, 100);
        int offset = page * safeSize;
        List<Expense> expenses = expenseRepository.findByCreatedBy(currentUserId, safeSize, offset);
        return expenses.stream()
                .map(expense -> {
                    List<ExpenseSplit> splits = expenseSplitRepository.findByExpenseId(expense.getId());
                    return buildExpenseResponse(expense, splits);
                })
                .toList();
    }


    // ──────────────────────────────────────────────
    // Private helpers
    // ──────────────────────────────────────────────

    private List<ExpenseSplit> buildSplits(Long expenseId, List<ExpenseParticipant> participants) {
        List<ExpenseSplit> splits = new ArrayList<>();

        for (var participant : participants){
            splits.add(ExpenseSplit.builder()
                    .expenseId(expenseId)
                    .userId(participant.getParticipantId())
                    .share(participant.getShare())
                    .build());
        }
        return splits;
    }

    private ExpenseResponse buildExpenseResponse(Expense expense, List<ExpenseSplit> splits) {

        BigDecimal totalShares = splits.stream()
                .map(ExpenseSplit::getShare)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // High precision division
        BigDecimal perShareAmount = expense.getTotalAmount()
                .divide(totalShares, 10, RoundingMode.HALF_UP);

        // Fetch users in single query (avoid N+1 problem)
        List<Long> userIds = splits.stream()
                .map(ExpenseSplit::getUserId)
                .distinct()
                .toList();

        Map<Long, User> userMap = userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        User paidByUser = userRepository.findById(expense.getPaidBy()).orElse(null);

        List<ParticipantShareResponse> participants = new ArrayList<>();
        BigDecimal runningTotal = BigDecimal.ZERO;

        for (int i = 0; i < splits.size(); i++) {
            ExpenseSplit split = splits.get(i);

            BigDecimal calculatedAmount;

            if (i == splits.size() - 1) {
                // Adjust last participant to match total exactly
                calculatedAmount = expense.getTotalAmount().subtract(runningTotal);
            } else {
                calculatedAmount = perShareAmount
                        .multiply(split.getShare())
                        .setScale(2, RoundingMode.HALF_UP);

                runningTotal = runningTotal.add(calculatedAmount);
            }

            User user = userMap.get(split.getUserId());

            participants.add(
                    ParticipantShareResponse.builder()
                            .userId(split.getUserId())
                            .userName(user != null ? user.getName() : null)
                            .share(split.getShare())
                            .calculatedAmount(calculatedAmount)
                            .build()
            );
        }

        return ExpenseResponse.builder()
                .id(expense.getId())
                .description(expense.getDescription())
                .totalAmount(expense.getTotalAmount())
                .paidById(expense.getPaidBy())
                .paidByName(paidByUser != null ? paidByUser.getName() : null)
                .splitType(expense.getSplitType())
                .category(expense.getCategory())
                .notes(expense.getNotes())
                .participants(participants)
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }

    private Expense getExpenseById(Long expenseId){
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new InvalidRequestException("expense.not.found", HttpStatus.NOT_FOUND));
    }

}
