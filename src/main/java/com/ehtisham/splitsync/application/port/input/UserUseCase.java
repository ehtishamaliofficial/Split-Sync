package com.ehtisham.splitsync.application.port.input;

import com.ehtisham.splitsync.application.dto.response.UserSearchResponse;

import java.util.List;

public interface UserUseCase {
    List<UserSearchResponse> searchUsers(String query, int limit);
}
