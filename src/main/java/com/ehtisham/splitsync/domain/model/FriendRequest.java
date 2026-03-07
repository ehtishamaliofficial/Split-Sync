package com.ehtisham.splitsync.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendRequest {
    private Long id;
    private Long fromUser;
    private Long toUser;
    @Builder.Default private String status = "PENDING"; // PENDING / ACCEPTED / REJECTED
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default private LocalDateTime updatedAt = LocalDateTime.now();
}
