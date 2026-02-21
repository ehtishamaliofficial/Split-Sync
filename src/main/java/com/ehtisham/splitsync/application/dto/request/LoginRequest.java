package com.ehtisham.splitsync.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "{login.usernameOrEmail.notBlank}")
    private String usernameOrEmail;

    @NotBlank(message = "{login.password.notBlank}")
    private String password;
}
