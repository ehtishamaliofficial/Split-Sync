package com.ehtisham.splitsync.application.port.input;

import com.ehtisham.splitsync.application.dto.request.LoginRequest;
import com.ehtisham.splitsync.application.dto.request.RegisterRequest;
import com.ehtisham.splitsync.application.dto.response.AuthResponse;

public interface AuthUseCase {
    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);
}
