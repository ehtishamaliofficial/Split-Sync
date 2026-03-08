package com.ehtisham.splitsync.infrastructure.in.rest;

import com.ehtisham.splitsync.application.dto.response.ApiResponse;
import com.ehtisham.splitsync.application.dto.response.FriendRequestResponse;
import com.ehtisham.splitsync.application.dto.response.FriendSummaryResponse;
import com.ehtisham.splitsync.application.port.input.CurrentUserProvider;
import com.ehtisham.splitsync.application.port.input.FriendUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
@Tag(name = "Friends", description = "Friend request and friendship management")
public class FriendController {

    private final FriendUseCase friendUseCase;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping("/request/{email}")
    @Operation(summary = "Send friend request")
    public ResponseEntity<ApiResponse<Void>> sendRequest(@PathVariable String email) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        friendUseCase.sendFriendRequest(currentUserId, email);
        return ResponseEntity.ok(ApiResponse.success("Friend request sent"));
    }

    @PostMapping("/request/{requestId}/accept")
    @Operation(summary = "Accept friend request")
    public ResponseEntity<ApiResponse<Void>> acceptRequest(@PathVariable Long requestId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        friendUseCase.respondToRequest(requestId, currentUserId, true);
        return ResponseEntity.ok(ApiResponse.success("Friend request accepted"));
    }

    @PostMapping("/request/{requestId}/reject")
    @Operation(summary = "Reject friend request")
    public ResponseEntity<ApiResponse<Void>> rejectRequest(@PathVariable Long requestId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        friendUseCase.respondToRequest(requestId, currentUserId, false);
        return ResponseEntity.ok(ApiResponse.success("Friend request rejected"));
    }

    @GetMapping("/requests/incoming")
    @Operation(summary = "Get pending incoming friend requests")
    public ResponseEntity<ApiResponse<List<FriendRequestResponse>>> getIncomingRequests() {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        List<FriendRequestResponse> requests = friendUseCase.getPendingIncoming(currentUserId);
        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    @GetMapping("/requests/outgoing")
    @Operation(summary = "Get pending outgoing friend requests")
    public ResponseEntity<ApiResponse<List<FriendRequestResponse>>> getOutgoingRequests() {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        List<FriendRequestResponse> requests = friendUseCase.getPendingOutgoing(currentUserId);
        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    @PostMapping("/request/{requestId}/cancel")
    @Operation(summary = "Cancel sent friend request")
    public ResponseEntity<ApiResponse<Void>> cancelRequest(@PathVariable Long requestId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        friendUseCase.cancelFriendRequest(requestId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Friend request cancelled"));
    }

    @GetMapping
    @Operation(summary = "Get my friends list")
    public ResponseEntity<ApiResponse<List<FriendSummaryResponse>>> getFriends() {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        List<FriendSummaryResponse> friends = friendUseCase.getFriends(currentUserId);
        return ResponseEntity.ok(ApiResponse.success(friends));
    }
}
