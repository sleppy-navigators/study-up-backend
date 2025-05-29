-- 1.1. Add initial_amount column to challenges table (allow NULL initially)
ALTER TABLE challenges
    ADD COLUMN initial_amount bigint null;

-- 1.2. Update existing challenges to have default values
UPDATE challenges
SET initial_amount = amount;

-- 1.3. Add NOT NULL constraints to challenges columns
ALTER TABLE challenges
    MODIFY COLUMN initial_amount bigint not null;

-- 2. Create huntings table
CREATE TABLE huntings
(
    id         bigint auto_increment primary key,
    amount     bigint      not null,
    hunter_id  bigint      not null,
    task_id    bigint      not null,
    deleted    bit         not null comment 'Soft-delete indicator',
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint FKmfgv5v293958k0kkog8hj24qr
        foreign key (hunter_id) references users (id),
    constraint FKnqgdpqd6yclj3ul8sourki49i
        foreign key (task_id) references tasks (id)
);
