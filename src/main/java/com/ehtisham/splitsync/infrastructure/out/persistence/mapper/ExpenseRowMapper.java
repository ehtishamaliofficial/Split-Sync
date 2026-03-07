package com.ehtisham.splitsync.infrastructure.out.persistence.mapper;

import com.ehtisham.splitsync.domain.model.Expense;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.ehtisham.splitsync.infrastructure.out.persistence.util.JdbcUtils.toLocalDateTime;

@Component
public class ExpenseRowMapper implements RowMapper<Expense> {

    @Override
    public Expense mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Expense.builder()
                .id(rs.getLong("id"))
                .description(rs.getString("description"))
                .totalAmount(rs.getBigDecimal("amount"))
                .paidBy(rs.getLong("paid_by"))
                .createdBy(rs.getObject("created_by") != null ? rs.getLong("created_by") : null)
                .splitType(rs.getString("split_type"))
                .category(rs.getString("category"))
                .notes(rs.getString("notes"))
                .metadata(rs.getString("metadata"))
                .createdAt(toLocalDateTime(rs.getTimestamp("created_at")))
                .updatedAt(toLocalDateTime(rs.getTimestamp("updated_at")))
                .build();
    }
}
