package com.ehtisham.splitsync.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ExpenseResponse {
    private Long id;
    private String description;
    private BigDecimal totalAmount;
    private Long paidById;
    private String paidByName;
    private String splitType;
    private String category;
    private String notes;
    private List<ParticipantShareResponse> participants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
