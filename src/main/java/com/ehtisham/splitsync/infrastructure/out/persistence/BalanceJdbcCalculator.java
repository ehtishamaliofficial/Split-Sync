package com.ehtisham.splitsync.infrastructure.out.persistence;

import com.ehtisham.splitsync.domain.port.out.BalanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Calculates net balance between two users using 4 SQL queries:
 *   1. Amount userA owes userB (B paid, A participates)
 *   2. Amount userB owes userA (A paid, B participates)
 *   3. Completed settlements from A to B
 *   4. Completed settlements from B to A
 *
 * Result is from userA's perspective:
 *   positive = userA owes userB
 *   negative = userB owes userA
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BalanceJdbcCalculator implements BalanceCalculator {

    private final JdbcTemplate jdbcTemplate;

    private static final String EXPENSE_OWED_SQL =
            "SELECT COALESCE(SUM(e.amount * es_a.share / totals.total_shares), 0) "
            + "FROM expenses e "
            + "JOIN expense_splits es_a ON es_a.expense_id = e.id AND es_a.user_id = ? "
            + "JOIN ("
            + "  SELECT expense_id, SUM(share) AS total_shares FROM expense_splits GROUP BY expense_id"
            + ") totals ON totals.expense_id = e.id "
            + "WHERE e.paid_by = ?";

    private static final String COMPLETED_PAYMENT_SQL =
            "SELECT COALESCE(SUM(amount), 0) FROM transactions "
            + "WHERE payer_id = ? AND payee_id = ? AND status = 'COMPLETED'";

    /**
     * Returns net amount from userA's perspective:
     *   positive → userA owes userB
     *   negative → userB owes userA
     */
    public BigDecimal calculateNet(Long userA, Long userB) {
        try {
            // Step 1: What A owes B (B paid, A participates)
            BigDecimal aOwesB = jdbcTemplate.queryForObject(EXPENSE_OWED_SQL, BigDecimal.class, userA, userB);
            if (aOwesB == null) aOwesB = BigDecimal.ZERO;

            // Step 2: What B owes A (A paid, B participates)
            BigDecimal bOwesA = jdbcTemplate.queryForObject(EXPENSE_OWED_SQL, BigDecimal.class, userB, userA);
            if (bOwesA == null) bOwesA = BigDecimal.ZERO;

            // Step 3: Completed settlements from A to B
            BigDecimal aPaidB = jdbcTemplate.queryForObject(COMPLETED_PAYMENT_SQL, BigDecimal.class, userA, userB);
            if (aPaidB == null) aPaidB = BigDecimal.ZERO;

            // Step 4: Completed settlements from B to A
            BigDecimal bPaidA = jdbcTemplate.queryForObject(COMPLETED_PAYMENT_SQL, BigDecimal.class, userB, userA);
            if (bPaidA == null) bPaidA = BigDecimal.ZERO;

            // net = (aOwesB - bPaidA) - (bOwesA - aPaidB)
            // positive → A still owes B
            // negative → B owes A
            return aOwesB.subtract(bPaidA).subtract(bOwesA.subtract(aPaidB));
        } catch (Exception e) {
            log.error("Error calculating balance between {} and {}", userA, userB, e);
            return BigDecimal.ZERO;
        }
    }
}
