package com.ehtisham.splitsync.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
    private Long id;
    private Long payerId;
    private Long payeeId;
    private BigDecimal amount;       // exact settled amount, immutable once COMPLETED
    private Long expenseId;          // optional
    private String status;           // PENDING / COMPLETED
    private String notes;
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime completedAt;
}
