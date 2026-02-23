-- ============================================================
-- V2: Friend + Expense Module (Friend Mode)
-- ============================================================

-- 1. Update expenses table
ALTER TABLE expenses
    ADD COLUMN IF NOT EXISTS split_type VARCHAR(20) NOT NULL DEFAULT 'EQUAL',
    ADD COLUMN IF NOT EXISTS created_by BIGINT REFERENCES users(id);

ALTER TABLE expenses
    DROP COLUMN IF EXISTS status;

-- 2. Update expense_splits table
ALTER TABLE expense_splits
    DROP COLUMN IF EXISTS status,
    DROP COLUMN IF EXISTS paid_at;

-- 3. Friend requests table (PENDING → ACCEPTED or REJECTED)
CREATE TABLE friend_requests (
    id          BIGSERIAL PRIMARY KEY,
    from_user   BIGINT NOT NULL REFERENCES users(id),
    to_user     BIGINT NOT NULL REFERENCES users(id),
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',   -- PENDING / ACCEPTED / REJECTED
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_friend_request UNIQUE (from_user, to_user),
    CONSTRAINT chk_no_self_request CHECK (from_user <> to_user)
);

-- 4. Friendships table (created on accept, bidirectional by convention: user_id < friend_id)
CREATE TABLE friendships (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id),
    friend_id   BIGINT NOT NULL REFERENCES users(id),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_friendship UNIQUE (user_id, friend_id),
    CONSTRAINT chk_ordered CHECK (user_id < friend_id)
);

-- 5. Transactions table (settled amounts — immutable once COMPLETED)
CREATE TABLE transactions (
    id           BIGSERIAL PRIMARY KEY,
    payer_id     BIGINT NOT NULL REFERENCES users(id),   -- who paid
    payee_id     BIGINT NOT NULL REFERENCES users(id),   -- who received
    amount       NUMERIC(15,2) NOT NULL,                 -- exact settled amount, stored permanently
    expense_id   BIGINT REFERENCES expenses(id),         -- optional link to a specific expense
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING / COMPLETED
    notes        TEXT,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    CONSTRAINT chk_transaction_amount CHECK (amount > 0),
    CONSTRAINT chk_no_self_transaction CHECK (payer_id <> payee_id)
);

-- 6. Performance indexes
CREATE INDEX idx_expenses_created_by    ON expenses(created_by);
CREATE INDEX idx_expenses_paid_by       ON expenses(paid_by);
CREATE INDEX idx_expense_splits_expense ON expense_splits(expense_id);
CREATE INDEX idx_expense_splits_user    ON expense_splits(user_id);
CREATE INDEX idx_friend_requests_to     ON friend_requests(to_user, status);
CREATE INDEX idx_friend_requests_from   ON friend_requests(from_user, status);
CREATE INDEX idx_friendships_user       ON friendships(user_id);
CREATE INDEX idx_friendships_friend     ON friendships(friend_id);
CREATE INDEX idx_transactions_payer     ON transactions(payer_id, status);
CREATE INDEX idx_transactions_payee     ON transactions(payee_id, status);
