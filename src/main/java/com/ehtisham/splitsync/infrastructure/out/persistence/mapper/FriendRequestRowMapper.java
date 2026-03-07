package com.ehtisham.splitsync.infrastructure.out.persistence.mapper;

import com.ehtisham.splitsync.domain.model.FriendRequest;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.ehtisham.splitsync.infrastructure.out.persistence.util.JdbcUtils.toLocalDateTime;

@Component
public class FriendRequestRowMapper implements RowMapper<FriendRequest> {

    @Override
    public FriendRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        return FriendRequest.builder()
                .id(rs.getLong("id"))
                .fromUser(rs.getLong("from_user"))
                .toUser(rs.getLong("to_user"))
                .status(rs.getString("status"))
                .createdAt(toLocalDateTime(rs.getTimestamp("created_at")))
                .updatedAt(toLocalDateTime(rs.getTimestamp("updated_at")))
                .build();
    }
}
