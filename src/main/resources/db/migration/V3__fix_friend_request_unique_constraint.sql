-- ============================================================
-- V3: Fix friend request unique constraint to include status
-- ============================================================

-- Drop the existing unique constraint
ALTER TABLE friend_requests DROP CONSTRAINT IF EXISTS uq_friend_request;

-- Add new unique constraint that includes status
ALTER TABLE friend_requests 
ADD CONSTRAINT uq_friend_request UNIQUE (from_user, to_user, status);

-- This allows multiple requests between same users with different statuses
-- e.g., PENDING -> CANCELLED -> PENDING is now allowed
