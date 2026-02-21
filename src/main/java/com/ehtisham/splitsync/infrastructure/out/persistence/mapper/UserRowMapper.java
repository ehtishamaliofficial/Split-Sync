package com.ehtisham.splitsync.infrastructure.out.persistence.mapper;

import com.ehtisham.splitsync.domain.model.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import static com.ehtisham.splitsync.infrastructure.out.persistence.util.JdbcUtils.toLocalDateTime;

@Component
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .password(rs.getString("password"))
                .phoneNumber(rs.getString("phone_number"))
                .avatarUrl(rs.getString("avatar_url"))
                .role(rs.getString("role"))
                .status(rs.getString("status"))
                .lastLogin(toLocalDateTime(rs.getTimestamp("last_login")))
                .createdAt(toLocalDateTime(rs.getTimestamp("created_at")))
                .updatedAt(toLocalDateTime(rs.getTimestamp("updated_at")))
                .extraJson(rs.getString("extra_json"))
                .build();
    }
}
