package com.ehtisham.splitsync.infrastructure.out.persistence.mapper;

import com.ehtisham.splitsync.domain.model.Transaction;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.ehtisham.splitsync.infrastructure.out.persistence.util.JdbcUtils.toLocalDateTime;

@Component
public class TransactionRowMapper implements RowMapper<Transaction> {

    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        long expenseIdRaw = rs.getLong("expense_id");
        return Transaction.builder()
                .id(rs.getLong("id"))
                .payerId(rs.getLong("payer_id"))
                .payeeId(rs.getLong("payee_id"))
                .amount(rs.getBigDecimal("amount"))
                .expenseId(rs.wasNull() ? null : expenseIdRaw)
                .status(rs.getString("status"))
                .notes(rs.getString("notes"))
                .createdAt(toLocalDateTime(rs.getTimestamp("created_at")))
                .completedAt(toLocalDateTime(rs.getTimestamp("completed_at")))
                .build();
    }
}
