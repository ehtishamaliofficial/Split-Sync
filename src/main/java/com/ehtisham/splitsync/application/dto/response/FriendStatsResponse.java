package com.ehtisham.splitsync.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FriendStatsResponse {
    private BigDecimal totalYouOwe;      // Sum of all negative balances (absolute)
    private BigDecimal totalOwedToYou;   // Sum of all positive balances
    private BigDecimal totalNetBalance;  // Net balance
    private String currency;
}
