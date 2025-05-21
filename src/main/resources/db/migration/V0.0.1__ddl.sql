-- 1. Create points table
CREATE TABLE points
(
    id           bigint auto_increment primary key,
    amount       bigint      not null,
    user_id      bigint      null,
    challenge_id bigint      null,
    deleted      bit         not null COMMENT 'Soft-delete indicator',
    created_at   datetime(6) not null,
    created_by   bigint      null,
    updated_at   datetime(6) not null,
    updated_by   bigint      null
);

-- 2.1. For each existing user, create a point record with amount=1000
INSERT INTO points (amount, user_id, deleted, created_at, updated_at)
SELECT 1000, u.id, 0, now(), now()
FROM users u;

-- 2.2. Link points to users
ALTER TABLE points
    ADD CONSTRAINT UKswg8y3uo5dm5psbnesgeu1my
        UNIQUE (user_id);
ALTER TABLE points
    ADD CONSTRAINT FKr49p6spgfhomh11aksag7al0d
        FOREIGN KEY (user_id) REFERENCES users (id);

-- 3.1. For each existing challenge, create a point record with amount=10
INSERT INTO points (amount, challenge_id, deleted, created_at, updated_at)
SELECT 10, c.id, 0, now(), now()
FROM challenges c;

-- 3.2. Link points to challenges
ALTER TABLE points
    ADD CONSTRAINT UKjhf7wr0qgjhk4pdb03qlqswa3
        UNIQUE (challenge_id);
ALTER TABLE points
    ADD CONSTRAINT FKp2dsitp5jnw7xutfrblif2fuu
        FOREIGN KEY (challenge_id) REFERENCES challenges (id);
