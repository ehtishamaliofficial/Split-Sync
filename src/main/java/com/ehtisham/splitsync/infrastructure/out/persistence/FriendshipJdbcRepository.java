package com.ehtisham.splitsync.infrastructure.out.persistence;

import com.ehtisham.splitsync.domain.port.out.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FriendshipJdbcRepository implements FriendshipRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void save(Long userA, Long userB) {
        // Enforce ordering: lower ID first
        long low = Math.min(userA, userB);
        long high = Math.max(userA, userB);
        try {
            String sql = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
            jdbcTemplate.update(sql, low, high);
        } catch (Exception e) {
            log.error("Error saving friendship between {} and {}", userA, userB, e);
        }
    }

    @Override
    public boolean exists(Long userA, Long userB) {
        try {
            long low = Math.min(userA, userB);
            long high = Math.max(userA, userB);
            String sql = "SELECT COUNT(1) FROM friendships WHERE user_id = ? AND friend_id = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, low, high);
            return count != null && count > 0;
        } catch (Exception e) {
            log.error("Error checking friendship between {} and {}", userA, userB, e);
            return false;
        }
    }

    @Override
    public List<Long> findFriendIds(Long userId) {
        try {
            // A user can be on either side of a friendship (user_id < friend_id)
            String sql = "SELECT CASE WHEN user_id = ? THEN friend_id ELSE user_id END AS friend_id "
                    + "FROM friendships WHERE user_id = ? OR friend_id = ?";
            return jdbcTemplate.queryForList(sql, Long.class, userId, userId, userId);
        } catch (Exception e) {
            log.error("Error finding friend ids for user {}", userId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public void delete(Long userA, Long userB) {
        try {
            long low = Math.min(userA, userB);
            long high = Math.max(userA, userB);
            String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sql, low, high);
        } catch (Exception e) {
            log.error("Error deleting friendship between {} and {}", userA, userB, e);
        }
    }
}
