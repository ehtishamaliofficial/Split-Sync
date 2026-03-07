package com.ehtisham.splitsync.domain.port.out;

import java.math.BigDecimal;

public interface BalanceCalculator {
    /**
     * Returns net amount from userA's perspective:
     *   positive → userA owes userB
     *   negative → userB owes userA
     */
    BigDecimal calculateNet(Long userA, Long userB);
}
