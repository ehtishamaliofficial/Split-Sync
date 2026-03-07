package com.ehtisham.splitsync.application.service;

import com.ehtisham.splitsync.application.dto.request.LoginRequest;
import com.ehtisham.splitsync.application.dto.request.RegisterRequest;
import com.ehtisham.splitsync.application.dto.response.AuthResponse;
import com.ehtisham.splitsync.application.port.input.AuthUseCase;
import com.ehtisham.splitsync.domain.model.User;
import com.ehtisham.splitsync.domain.port.out.UserRepository;
import com.ehtisham.splitsync.domain.exception.InvalidRequestException;
import com.ehtisham.splitsync.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements AuthUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse login(LoginRequest request) {
        if (request == null) {
            throw new InvalidRequestException("request.invalid");
        }

        String identifier = request.getUsernameOrEmail().trim();
        Optional<User> userOpt = identifier.contains("@")
                ? userRepository.findByEmail(identifier.toLowerCase(Locale.ROOT))
                : userRepository.findByUsername(identifier);

        User user = userOpt.orElseThrow(() -> new InvalidRequestException("auth.invalid_credentials", HttpStatus.UNAUTHORIZED));
        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidRequestException("auth.invalid_credentials", HttpStatus.UNAUTHORIZED);
        }

        String accessToken = jwtService.generateToken(user.getName(), user.getEmail(), user.getId());
        String refreshToken = jwtService.generateRefreshToken(user.getName(), user.getEmail(), user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (request == null) {
            throw new InvalidRequestException("request.invalid");
        }

        String username = request.getUsername() != null ? request.getUsername().trim() : null;
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase(Locale.ROOT) : null;

        if (username != null && userRepository.existsByUsername(username)) {
            throw new InvalidRequestException("register.username.exists", HttpStatus.CONFLICT);
        }
        if (email != null && userRepository.existsByEmail(email)) {
            throw new InvalidRequestException("register.email.exists", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .name(username)
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .avatarUrl(request.getAvatarUrl())
                .build();

        User saved = userRepository.save(user);

        String accessToken = jwtService.generateToken(saved.getName(), saved.getEmail(), saved.getId());
        String refreshToken = jwtService.generateRefreshToken(saved.getName(), saved.getEmail(), saved.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(saved.getId())
                .username(saved.getName())
                .email(saved.getEmail())
                .build();
    }

}
