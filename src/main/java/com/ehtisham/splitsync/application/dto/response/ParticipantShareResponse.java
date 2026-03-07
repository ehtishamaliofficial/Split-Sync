package com.ehtisham.splitsync.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ParticipantShareResponse {
    private Long userId;
    private String userName;
    private BigDecimal share;           // raw stored share
    private BigDecimal calculatedAmount; // (totalAmount × share) / totalShares
}
