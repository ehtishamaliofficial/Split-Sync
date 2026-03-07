package com.ehtisham.splitsync.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseSplit {
    private Long id;
    private Long expenseId;
    private Long userId;
    private BigDecimal share;  // raw share value (NOT calculated amount)
    private String metadata;
}
