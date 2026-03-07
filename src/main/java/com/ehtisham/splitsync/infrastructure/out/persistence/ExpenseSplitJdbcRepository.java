package com.ehtisham.splitsync.infrastructure.out.persistence;

import com.ehtisham.splitsync.domain.model.ExpenseSplit;
import com.ehtisham.splitsync.domain.port.out.ExpenseSplitRepository;
import com.ehtisham.splitsync.infrastructure.out.persistence.mapper.ExpenseSplitRowMapper;
import com.ehtisham.splitsync.infrastructure.out.persistence.util.JdbcUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ExpenseSplitJdbcRepository implements ExpenseSplitRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ExpenseSplitRowMapper rowMapper;

    @Override
    public void saveAll(List<ExpenseSplit> splits) {
        if (splits == null || splits.isEmpty()) return;
        String sql = "INSERT INTO expense_splits (expense_id, user_id, share, metadata) VALUES (?, ?, ?, ?)";
        try {
            jdbcTemplate.batchUpdate(sql, splits, splits.size(), (ps, split) -> {
                ps.setLong(1, split.getExpenseId());
                ps.setLong(2, split.getUserId());
                ps.setBigDecimal(3, split.getShare());
                ps.setObject(4, JdbcUtils.toJsonbObject(split.getMetadata()));
            });
        } catch (Exception e) {
            log.error("Error batch saving expense splits", e);
            throw e;
        }
    }

    @Override
    public List<ExpenseSplit> findByExpenseId(Long expenseId) {
        try {
            String sql = "SELECT * FROM expense_splits WHERE expense_id = ?";
            return jdbcTemplate.query(sql, rowMapper, expenseId);
        } catch (Exception e) {
            log.error("Error finding splits for expense {}", expenseId, e);
            return List.of();
        }
    }

    @Override
    public void deleteByExpenseId(Long expenseId) {
        try {
            String sql = "DELETE FROM expense_splits WHERE expense_id = ?";
            jdbcTemplate.update(sql, expenseId);
        } catch (Exception e) {
            log.error("Error deleting splits for expense {}", expenseId, e);
        }
    }
}
