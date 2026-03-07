package com.ehtisham.splitsync.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSearchResponse {
    private Long userId;
    private String userName;
    private String email;
    private String avatarUrl;
}
