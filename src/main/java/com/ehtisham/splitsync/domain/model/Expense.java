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
public class Expense {
    private Long id;
    private String description;
    private BigDecimal totalAmount;   // stored as NUMERIC(15,2)
    private Long paidBy;             // always = createdBy (creator is payer)
    private Long createdBy;
    private String splitType;        // EQUAL / PERCENTAGE / RATIO
    private String category;         // FOOD / RENT / UTILITIES / OTHER / TRAVEL / ENTERTAINMENT
    private String notes;
    private String metadata;         // JSONB string
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default private LocalDateTime updatedAt = LocalDateTime.now();
}
