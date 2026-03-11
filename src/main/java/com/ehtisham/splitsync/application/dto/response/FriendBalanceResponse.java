package com.ehtisham.splitsync.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FriendBalanceResponse {
    private Long id;
    private String name;
    private BigDecimal amount;
    private boolean settledUp;
}
