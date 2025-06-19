ALTER TABLE `groups`
    MODIFY COLUMN deleted BOOLEAN NOT NULL COMMENT 'Soft-delete indicator';

ALTER TABLE `users`
    MODIFY COLUMN deleted BOOLEAN NOT NULL COMMENT 'Soft-delete indicator';

ALTER TABLE `challenges`
    MODIFY COLUMN deleted BOOLEAN NOT NULL COMMENT 'Soft-delete indicator';

ALTER TABLE `group_members`
    MODIFY COLUMN deleted BOOLEAN NOT NULL COMMENT 'Soft-delete indicator';

ALTER TABLE `tasks`
    MODIFY COLUMN deleted BOOLEAN NOT NULL COMMENT 'Soft-delete indicator';

ALTER TABLE `huntings`
    MODIFY COLUMN deleted BOOLEAN NOT NULL COMMENT 'Soft-delete indicator';

ALTER TABLE `user_credentials`
    MODIFY COLUMN deleted BOOLEAN NOT NULL COMMENT 'Soft-delete indicator';
