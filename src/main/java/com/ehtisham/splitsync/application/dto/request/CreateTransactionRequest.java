package com.ehtisham.splitsync.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTransactionRequest {
    @NotNull private Long payeeId;
    @NotNull @DecimalMin("0.01") private BigDecimal amount;
    private Long expenseId;
    private String notes;
}
