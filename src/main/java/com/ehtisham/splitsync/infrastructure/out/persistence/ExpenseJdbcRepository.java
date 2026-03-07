package com.ehtisham.splitsync.infrastructure.out.persistence;

import com.ehtisham.splitsync.domain.model.Expense;
import com.ehtisham.splitsync.domain.port.out.ExpenseRepository;
import com.ehtisham.splitsync.infrastructure.out.persistence.mapper.ExpenseRowMapper;
import com.ehtisham.splitsync.infrastructure.out.persistence.util.JdbcUtils;
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
public class ExpenseJdbcRepository implements ExpenseRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ExpenseRowMapper rowMapper;

    @Override
    public Expense save(Expense expense) {
        if (expense.getId() == null) {
            return insert(expense);
        }
        update(expense);
        return expense;
    }

    @Override
    public Optional<Expense> findById(Long id) {
        try {
            String sql = "SELECT * FROM expenses WHERE id = ?";
            return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
        } catch (Exception e) {
            log.error("Error finding expense by id {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Expense> findByCreatedBy(Long userId, int limit, int offset) {
        try {
            String sql = "SELECT * FROM expenses WHERE created_by = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
            return jdbcTemplate.query(sql, rowMapper, userId, limit, offset);
        } catch (Exception e) {
            log.error("Error finding expenses for user {}", userId, e);
            return List.of();
        }
    }

    @Override
    public List<Expense> findSharedBetween(Long userA, Long userB) {
        try {
            String sql = "SELECT DISTINCT e.* FROM expenses e "
                    + "JOIN expense_splits es ON es.expense_id = e.id "
                    + "WHERE (e.paid_by = ? AND es.user_id = ?) OR (e.paid_by = ? AND es.user_id = ?) "
                    + "ORDER BY e.created_at DESC";
            return jdbcTemplate.query(sql, rowMapper, userA, userB, userB, userA);
        } catch (Exception e) {
            log.error("Error finding expenses shared between {} and {}", userA, userB, e);
            return List.of();
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            String sql = "DELETE FROM expenses WHERE id = ?";
            jdbcTemplate.update(sql, id);
        } catch (Exception e) {
            log.error("Error deleting expense {}", id, e);
        }
    }

    private Expense insert(Expense expense) {
        String sql = "INSERT INTO expenses (description, amount, paid_by, created_by, split_type, category, notes, metadata, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = expense.getCreatedAt() != null ? expense.getCreatedAt() : now;
        LocalDateTime updatedAt = expense.getUpdatedAt() != null ? expense.getUpdatedAt() : now;

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, expense.getDescription());
            ps.setBigDecimal(2, expense.getTotalAmount());
            ps.setLong(3, expense.getPaidBy());
            ps.setObject(4, expense.getCreatedBy());
            ps.setString(5, expense.getSplitType());
            ps.setString(6, expense.getCategory());
            ps.setString(7, expense.getNotes());
            ps.setObject(8, JdbcUtils.toJsonbObject(expense.getMetadata()));
            ps.setTimestamp(9, toTimestamp(createdAt));
            ps.setTimestamp(10, toTimestamp(updatedAt));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            expense.setId(key.longValue());
        }
        expense.setCreatedAt(createdAt);
        expense.setUpdatedAt(updatedAt);
        return expense;
    }

    private void update(Expense expense) {
        String sql = "UPDATE expenses SET description = ?, amount = ?, split_type = ?, category = ?, notes = ?, "
                + "metadata = ?, updated_at = ? WHERE id = ?";
        LocalDateTime updatedAt = expense.getUpdatedAt() != null ? expense.getUpdatedAt() : LocalDateTime.now();
        jdbcTemplate.update(sql,
                expense.getDescription(),
                expense.getTotalAmount(),
                expense.getSplitType(),
                expense.getCategory(),
                expense.getNotes(),
                JdbcUtils.toJsonbObject(expense.getMetadata()),
                toTimestamp(updatedAt),
                expense.getId()
        );
        expense.setUpdatedAt(updatedAt);
    }
}
