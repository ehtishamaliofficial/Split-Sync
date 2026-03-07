package com.ehtisham.splitsync.infrastructure.in.rest;

import com.ehtisham.splitsync.application.dto.response.ApiResponse;
import com.ehtisham.splitsync.application.dto.response.UserSearchResponse;
import com.ehtisham.splitsync.application.port.input.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User search and management")
public class UserController {

    private final UserUseCase userUseCase;

    @GetMapping("/search")
    @Operation(summary = "Search users by username or email")
    public ResponseEntity<ApiResponse<List<UserSearchResponse>>> searchUsers(
            @Parameter(description = "Search query for username or email", required = true)
            @RequestParam String query,
            
            @Parameter(description = "Maximum number of results to return", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        
        List<UserSearchResponse> users = userUseCase.searchUsers(query, limit);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
}
