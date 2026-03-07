package com.ehtisham.splitsync.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateExpenseRequest {
    @NotBlank private String description;
    @NotNull @DecimalMin("0.01") private BigDecimal totalAmount;
    @NotNull private String splitType;

    @Valid private List<ExpenseParticipant> participants;
    private String category;
    private String notes;
}
