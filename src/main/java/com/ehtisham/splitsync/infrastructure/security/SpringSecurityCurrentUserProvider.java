package com.ehtisham.splitsync.infrastructure.security;

import com.ehtisham.splitsync.application.port.input.CurrentUserProvider;
import com.ehtisham.splitsync.domain.exception.InvalidRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityCurrentUserProvider implements CurrentUserProvider {

    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            Long userId = jwt.getClaim("userId");
            if(userId == null){
                throw new InvalidRequestException("auth.unauthorized", HttpStatus.UNAUTHORIZED);
            }
            return userId;
        }

        throw new IllegalStateException("Invalid authentication principal");
    }
}

