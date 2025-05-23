-- 1.1. Add amount column to users table (allow NULL initially)
ALTER TABLE users
    ADD COLUMN amount bigint null;

-- 1.2. Update existing users to have amount = 1000
UPDATE users
SET amount = 1000;

-- 1.3. Add NOT NULL constraint to users.amount
ALTER TABLE users
    MODIFY COLUMN amount bigint not null;

-- 2.1. Add amount column to challenges table (allow NULL initially)
ALTER TABLE challenges
    ADD COLUMN amount bigint null;

-- 2.2. Update existing challenges to have amount = 10
UPDATE challenges
SET amount = 10;

-- 2.3. Add NOT NULL constraint to challenges.amount
ALTER TABLE challenges
    MODIFY COLUMN amount bigint not null;
