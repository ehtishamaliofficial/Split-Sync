package com.ehtisham.splitsync.infrastructure.out.persistence.mapper;

import com.ehtisham.splitsync.domain.model.ExpenseSplit;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ExpenseSplitRowMapper implements RowMapper<ExpenseSplit> {

    @Override
    public ExpenseSplit mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ExpenseSplit.builder()
                .id(rs.getLong("id"))
                .expenseId(rs.getLong("expense_id"))
                .userId(rs.getLong("user_id"))
                .share(rs.getBigDecimal("share"))
                .metadata(rs.getString("metadata"))
                .build();
    }
}
