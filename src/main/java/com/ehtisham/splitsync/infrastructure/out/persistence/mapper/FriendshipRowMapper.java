package com.ehtisham.splitsync.infrastructure.out.persistence.mapper;

import com.ehtisham.splitsync.domain.model.Friendship;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.ehtisham.splitsync.infrastructure.out.persistence.util.JdbcUtils.toLocalDateTime;

@Component
public class FriendshipRowMapper implements RowMapper<Friendship> {

    @Override
    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Friendship.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .friendId(rs.getLong("friend_id"))
                .createdAt(toLocalDateTime(rs.getTimestamp("created_at")))
                .build();
    }
}
