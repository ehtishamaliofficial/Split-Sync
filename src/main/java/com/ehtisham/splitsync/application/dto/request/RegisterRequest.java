package com.ehtisham.splitsync.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "{register.username.notBlank}")
    private String username;

    @NotBlank(message = "{register.email.notBlank}")
    @Email(message = "{register.email.invalid}")
    private String email;

    @NotBlank(message = "{register.password.notBlank}")
    private String password;

    private String phoneNumber;

    private String avatarUrl;
}
