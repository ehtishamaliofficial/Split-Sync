package com.ehtisham.splitsync.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private Long id;
    private Long payerId;
    private String payerName;
    private Long payeeId;
    private String payeeName;
    private BigDecimal amount;
    private Long expenseId;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
