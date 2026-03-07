package com.ehtisham.splitsync.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateExpenseRequest {
    private String description;
    @DecimalMin("0.01") private BigDecimal totalAmount;
    private String splitType;
    private List<Long> participantIds;
    private List<BigDecimal> shares;
    private String category;
    private String notes;
}
