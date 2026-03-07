package com.ehtisham.splitsync.infrastructure.out.persistence;

import com.ehtisham.splitsync.domain.model.Transaction;
import com.ehtisham.splitsync.domain.port.out.TransactionRepository;
import com.ehtisham.splitsync.infrastructure.out.persistence.mapper.TransactionRowMapper;
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
public class TransactionJdbcRepository implements TransactionRepository {

    private final JdbcTemplate jdbcTemplate;
    private final TransactionRowMapper rowMapper;

    @Override
    public Transaction save(Transaction transaction) {
        String sql = "INSERT INTO transactions (payer_id, payee_id, amount, expense_id, status, notes, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = transaction.getCreatedAt() != null ? transaction.getCreatedAt() : now;

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, transaction.getPayerId());
            ps.setLong(2, transaction.getPayeeId());
            ps.setBigDecimal(3, transaction.getAmount());
            ps.setObject(4, transaction.getExpenseId());
            ps.setString(5, transaction.getStatus());
            ps.setString(6, transaction.getNotes());
            ps.setTimestamp(7, toTimestamp(createdAt));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            transaction.setId(key.longValue());
        }
        transaction.setCreatedAt(createdAt);
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        try {
            String sql = "SELECT * FROM transactions WHERE id = ?";
            return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
        } catch (Exception e) {
            log.error("Error finding transaction {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Transaction> findBetweenUsers(Long userA, Long userB) {
        try {
            String sql = "SELECT * FROM transactions "
                    + "WHERE (payer_id = ? AND payee_id = ?) OR (payer_id = ? AND payee_id = ?) "
                    + "ORDER BY created_at DESC";
            return jdbcTemplate.query(sql, rowMapper, userA, userB, userB, userA);
        } catch (Exception e) {
            log.error("Error finding transactions between {} and {}", userA, userB, e);
            return List.of();
        }
    }

    @Override
    public void markCompleted(Long id, LocalDateTime completedAt) {
        try {
            String sql = "UPDATE transactions SET status = 'COMPLETED', completed_at = ? WHERE id = ?";
            jdbcTemplate.update(sql, toTimestamp(completedAt), id);
        } catch (Exception e) {
            log.error("Error marking transaction {} as completed", id, e);
        }
    }
}
