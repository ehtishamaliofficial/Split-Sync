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
public class User {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String avatarUrl;

    @Builder.Default
    private String role="USER";
    @Builder.Default
    private String status="ACTIVE";

    private LocalDateTime lastLogin;

    @Builder.Default
    private LocalDateTime createdAt=LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt=LocalDateTime.now();
    private String extraJson;

}
