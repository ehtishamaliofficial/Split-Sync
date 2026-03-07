package com.ehtisham.splitsync.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FriendRequestResponse {
    private Long id;
    private Long fromUserId;
    private String fromUserName;
    private String fromUserAvatarUrl;
    private Long toUserId;
    private String toUserName;
    private String status;
    private LocalDateTime createdAt;
}
