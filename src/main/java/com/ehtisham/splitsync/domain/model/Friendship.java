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
public class Friendship {
    private Long id;
    private Long userId;    // always the lower ID
    private Long friendId;  // always the higher ID
    @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
}
