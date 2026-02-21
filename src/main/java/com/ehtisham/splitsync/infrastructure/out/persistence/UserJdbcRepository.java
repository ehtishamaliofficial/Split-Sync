package com.ehtisham.splitsync.infrastructure.out.persistence;

import com.ehtisham.splitsync.domain.model.User;
import com.ehtisham.splitsync.domain.port.out.UserRepository;
import com.ehtisham.splitsync.infrastructure.out.persistence.mapper.UserRowMapper;
import com.ehtisham.splitsync.infrastructure.out.persistence.util.JdbcTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.postgresql.util.PGobject;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.ehtisham.splitsync.infrastructure.out.persistence.util.JdbcTimeUtils.toTimestamp;


@Repository
@Slf4j
@RequiredArgsConstructor
public class UserJdbcRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    @Override
    public Optional<User> findById(Long id) {
        try {
            String query = "select * from users where id = ?";
            return jdbcTemplate.query(query, userRowMapper, id).stream().findFirst();
        } catch (Exception e) {
            log.error("Error while trying to find User with id {}", id);
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            String query = "select * from users where name = ?";
            return jdbcTemplate.query(query, userRowMapper, username).stream().findFirst();
        } catch (Exception e) {
            log.error("Error while trying to find User with username {}", username);
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            String query = "select * from users where email = ?";
            return jdbcTemplate.query(query, userRowMapper, email).stream().findFirst();
        } catch (Exception e) {
            log.error("Error while trying to find User with email {}", email);
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public List<User> searchByUsernameOrEmail(String query, Long excludeUserId, int limit) {
        try {
            if (query == null || query.isBlank()) {
                return List.of();
            }

            int safeLimit = limit > 0 ? Math.min(limit, 50) : 10;
            String q = "%" + query.trim() + "%";

            String sql = "select * from users "
                    + "where (name ilike ? or email ilike ?) "
                    + "and (? is null or id <> ?) "
                    + "order by name asc "
                    + "limit ?";

            return jdbcTemplate.query(sql, userRowMapper, q, q, excludeUserId, excludeUserId, safeLimit);
        } catch (Exception e) {
            log.error("Error while trying to search users by query {}", query);
            log.error(e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        try {
            String query = "select count(1) from users where name = ?";
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, username);
            return count != null && count > 0;
        } catch (Exception e) {
            log.error("Error while trying to check existence by username {}", username);
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try {
            String query = "select count(1) from users where email = ?";
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, email);
            return count != null && count > 0;
        } catch (Exception e) {
            log.error("Error while trying to check existence by email {}", email);
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            return insert(user);
        }

        update(user);
        return user;
    }

    @Override
    public void deleteById(Long id) {
        try {
            String query = "delete from users where id = ?";
            jdbcTemplate.update(query, id);
        } catch (Exception e) {
            log.error("Error while trying to delete User with id {}", id);
            log.error(e.getMessage(), e);
        }
    }

    private User insert(User user) {
        String query = "insert into users (name, email, password, phone_number, avatar_url, role, status, last_login, created_at, updated_at, extra_json) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = user.getCreatedAt() != null ? user.getCreatedAt() : now;
        LocalDateTime updatedAt = user.getUpdatedAt() != null ? user.getUpdatedAt() : now;

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhoneNumber());
            ps.setString(5, user.getAvatarUrl());
            ps.setString(6, user.getRole());
            ps.setString(7, user.getStatus());
            ps.setTimestamp(8, toTimestamp(user.getLastLogin()));
            ps.setTimestamp(9, toTimestamp(createdAt));
            ps.setTimestamp(10, toTimestamp(updatedAt));
            ps.setObject(11, JdbcTimeUtils.toJsonbObject(user.getExtraJson()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            user.setId(key.longValue());
        }
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(updatedAt);
        return user;
    }

    private void update(User user) {
        String query = "update users set name = ?, email = ?, password = ?, phone_number = ?, avatar_url = ?, "
                + "role = ?, status = ?, last_login = ?, updated_at = ?, extra_json = ? where id = ?";
        LocalDateTime updatedAt = user.getUpdatedAt() != null ? user.getUpdatedAt() : LocalDateTime.now();
        jdbcTemplate.update(query,
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getPhoneNumber(),
                user.getAvatarUrl(),
                user.getRole(),
                user.getStatus(),
                toTimestamp(user.getLastLogin()),
                toTimestamp(updatedAt),
                JdbcTimeUtils.toJsonbObject(user.getExtraJson()),
                user.getId()
        );
        user.setUpdatedAt(updatedAt);
    }

}
