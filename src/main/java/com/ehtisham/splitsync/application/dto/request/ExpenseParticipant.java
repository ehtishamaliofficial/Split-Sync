package com.ehtisham.splitsync.application.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExpenseParticipant {
    @NotNull private Long participantId;
    @NotNull private BigDecimal share;
}
