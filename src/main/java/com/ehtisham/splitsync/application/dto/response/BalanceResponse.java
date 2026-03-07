package com.ehtisham.splitsync.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BalanceResponse {
    private Long friendId;
    private String friendName;
    private String friendAvatarUrl;
    private BigDecimal netAmount;    // positive = friend owes you, negative = you owe friend
    private String currency;
}
