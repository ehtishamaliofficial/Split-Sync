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
public class UserBalance {
    private Long friendId;
    private String friendName;
    private String friendAvatarUrl;
    private BigDecimal netAmount;    // positive = friend owes you, negative = you owe friend
    private String currency;
}
