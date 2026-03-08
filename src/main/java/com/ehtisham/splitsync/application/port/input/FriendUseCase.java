package com.ehtisham.splitsync.application.port.input;

import com.ehtisham.splitsync.application.dto.response.FriendRequestResponse;
import com.ehtisham.splitsync.application.dto.response.FriendSummaryResponse;

import java.util.List;

public interface FriendUseCase {
    void sendFriendRequest(Long fromUserId, String email);
    void respondToRequest(Long requestId, Long respondingUserId, boolean accept);
    List<FriendRequestResponse> getPendingIncoming(Long userId);
    List<FriendRequestResponse> getPendingOutgoing(Long userId);
    void cancelFriendRequest(Long requestId, Long fromUserId);
    List<FriendSummaryResponse> getFriends(Long userId);
}
