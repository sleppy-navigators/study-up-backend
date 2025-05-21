-- 1. Create points table
CREATE TABLE points
(
    id         bigint auto_increment primary key,
    amount     bigint      not null,
    deleted    bit         not null COMMENT 'Soft-delete indicator',
    created_at datetime(6) not null,
    created_by bigint      null,
    updated_at datetime(6) not null,
    updated_by bigint      null
);

-- 2.1. Add point_id column to users table
ALTER TABLE users
    ADD COLUMN point_id bigint AFTER email;

-- 2.2. For each existing user, create a point record with amount=1000 and link it
INSERT INTO points (amount, deleted, created_at, updated_at)
SELECT 1000, 0, now(), now()
FROM users;

-- 2.3. Set variables for point_id assignment
SET @last_point_id = last_insert_id();
SET @num_users = (SELECT count(*)
                  FROM users);
SET @first_point_id = @last_point_id - @num_users + 1;
SET @min_user_id = (SELECT min(id)
                    FROM users);

-- 2.4. Link users to points
UPDATE users
SET point_id = (@first_point_id + (id - @min_user_id));

-- 2.5. Make point_id NOT NULL now that values are assigned
ALTER TABLE users
    MODIFY COLUMN point_id bigint not null;

-- 2.6. Add constraints to users table
ALTER TABLE users
    ADD CONSTRAINT UK56x4abdls4l57k8egw0tixjb4
        unique (point_id);
ALTER TABLE users
    ADD CONSTRAINT FKi18e4c3wpobkjovtxdkjbvo7w
        foreign key (point_id) references points (id);

-- 3.1. Add point_id column to challenges table
ALTER TABLE challenges
    ADD COLUMN point_id bigint AFTER deadline;

-- 3.2. For each existing challenge, create a point record with amount=10 and link it
INSERT INTO points (amount, deleted, created_at, updated_at)
SELECT 10, 0, now(), now()
FROM challenges;

-- 3.3. Set variables for point_id assignment
SET @last_point_id = last_insert_id();
SET @num_challenges = (SELECT count(*)
                       FROM challenges);
SET @first_point_id = @last_point_id - @num_challenges + 1;
SET @min_challenge_id = (SELECT min(id)
                         FROM challenges);

-- 3.4. Link challenges to points
UPDATE challenges
SET point_id = (@first_point_id + (id - @min_challenge_id));

-- 3.5. Make point_id NOT NULL now that values are assigned
ALTER TABLE challenges
    MODIFY COLUMN point_id bigint not null;

-- 3.6. Add constraints to challenges table
ALTER TABLE challenges
    ADD CONSTRAINT UKb7uglhw69isctctsll89mwvf1
        unique (point_id);
ALTER TABLE challenges
    ADD CONSTRAINT FK1ggcyp9wxsth6kl9pgfe0xp3h
        foreign key (point_id) references points (id);
