package com.ehtisham.splitsync.infrastructure.out.persistence;

import com.ehtisham.splitsync.domain.model.FriendRequest;
import com.ehtisham.splitsync.domain.port.out.FriendRequestRepository;
import com.ehtisham.splitsync.infrastructure.out.persistence.mapper.FriendRequestRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.ehtisham.splitsync.infrastructure.out.persistence.util.JdbcUtils.toTimestamp;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FriendRequestJdbcRepository implements FriendRequestRepository {

    private final JdbcTemplate jdbcTemplate;
    private final FriendRequestRowMapper rowMapper;

    @Override
    public FriendRequest save(FriendRequest request) {
        String sql = "INSERT INTO friend_requests (from_user, to_user, status, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = request.getCreatedAt() != null ? request.getCreatedAt() : now;
        LocalDateTime updatedAt = request.getUpdatedAt() != null ? request.getUpdatedAt() : now;

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, request.getFromUser());
            ps.setLong(2, request.getToUser());
            ps.setString(3, request.getStatus());
            ps.setTimestamp(4, toTimestamp(createdAt));
            ps.setTimestamp(5, toTimestamp(updatedAt));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            request.setId(key.longValue());
        }
        request.setCreatedAt(createdAt);
        request.setUpdatedAt(updatedAt);
        return request;
    }

    @Override
    public Optional<FriendRequest> findById(Long id) {
        try {
            String sql = "SELECT * FROM friend_requests WHERE id = ?";
            return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
        } catch (Exception e) {
            log.error("Error finding friend request by id {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<FriendRequest> findPending(Long fromUser, Long toUser) {
        try {
            String sql = "SELECT * FROM friend_requests WHERE from_user = ? AND to_user = ? AND status = 'PENDING'";
            return jdbcTemplate.query(sql, rowMapper, fromUser, toUser).stream().findFirst();
        } catch (Exception e) {
            log.error("Error finding pending request from {} to {}", fromUser, toUser, e);
            return Optional.empty();
        }
    }

    @Override
    public List<FriendRequest> findIncomingPending(Long toUser) {
        try {
            String sql = "SELECT * FROM friend_requests WHERE to_user = ? AND status = 'PENDING' ORDER BY created_at DESC";
            return jdbcTemplate.query(sql, rowMapper, toUser);
        } catch (Exception e) {
            log.error("Error finding incoming pending requests for user {}", toUser, e);
            return List.of();
        }
    }

    @Override
    public List<FriendRequest> findOutgoingPending(Long fromUser) {
        try {
            String sql = "SELECT * FROM friend_requests WHERE from_user = ? AND status = 'PENDING' ORDER BY created_at DESC";
            return jdbcTemplate.query(sql, rowMapper, fromUser);
        } catch (Exception e) {
            log.error("Error finding outgoing pending requests for user {}", fromUser, e);
            return List.of();
        }
    }

    @Override
    public void updateStatus(Long id, String status, LocalDateTime updatedAt) {
        try {
            String sql = "UPDATE friend_requests SET status = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql, status, toTimestamp(updatedAt), id);
        } catch (Exception e) {
            log.error("Error updating status of friend request {}", id, e);
        }
    }
}
