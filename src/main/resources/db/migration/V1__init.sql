CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(150) UNIQUE NOT NULL,
                       password VARCHAR(255),                   -- optional for OAuth2
                       phone_number VARCHAR(20),                -- SMS/2FA
                       avatar_url TEXT,                         -- profile picture
                       role VARCHAR(50) DEFAULT 'USER',         -- USER / ADMIN / MANAGER
                       status VARCHAR(20) DEFAULT 'ACTIVE',     -- ACTIVE / INACTIVE / BANNED
                       last_login TIMESTAMP,                    -- last login
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       extra_json JSONB                         -- flexible storage for future attributes
);

CREATE TABLE expenses (
                          id BIGSERIAL PRIMARY KEY,
                          description VARCHAR(255),
                          amount NUMERIC(15,2) NOT NULL,
                          paid_by BIGINT REFERENCES users(id),
                          category VARCHAR(50),                     -- FOOD / RENT / UTILITIES / OTHER
                          notes TEXT,                               -- optional note
                          status VARCHAR(20) DEFAULT 'PENDING',    -- PENDING / PAID / CANCELLED
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          metadata JSONB                            -- extra info (currency, tags, etc.)
);

CREATE TABLE expense_splits (
                                id BIGSERIAL PRIMARY KEY,
                                expense_id BIGINT REFERENCES expenses(id),
                                user_id BIGINT REFERENCES users(id),
                                share NUMERIC(15,2) NOT NULL,
                                status VARCHAR(20) DEFAULT 'UNPAID',     -- UNPAID / PAID / CANCELLED
                                paid_at TIMESTAMP,                        -- optional timestamp when paid
                                metadata JSONB                            -- extra info (notes, partial payments)
);
