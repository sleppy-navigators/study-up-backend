ALTER TABLE `bots`
    DROP INDEX `UKbl66pntl1ns8c1wowc4yqv9yd`,
    ADD CONSTRAINT `uk_bots_group_id` UNIQUE (`group_id`),

    DROP FOREIGN KEY `FK5frfnih7gr71yusbn2221m4nx`,
    ADD CONSTRAINT `fk_bots_group_id` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`);

ALTER TABLE `group_invitations`
    RENAME INDEX `FKkjm6h04g1d3bf3iinku0wagf5` TO `fk_group_invitations_group_id`,
    DROP FOREIGN KEY `FKkjm6h04g1d3bf3iinku0wagf5`,
    ADD CONSTRAINT `fk_group_invitations_group_id` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`);

ALTER TABLE `challenges`
    RENAME INDEX `FK6phslw6i958x3hgubwoglpsgh` TO `fk_challenges_group_id`,
    DROP FOREIGN KEY `FK6phslw6i958x3hgubwoglpsgh`,
    ADD CONSTRAINT `fk_challenges_group_id` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`),

    RENAME INDEX `FKbudvg0p3ffvgvbeyf9l8nume3` TO `fk_challenges_owner_id`,
    DROP FOREIGN KEY `FKbudvg0p3ffvgvbeyf9l8nume3`,
    ADD CONSTRAINT `fk_challenges_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`);

ALTER TABLE `fcm_tokens`
    DROP INDEX `UKnbv2435ks37v3744qget6y41m`,
    ADD CONSTRAINT `uk_fcm_tokens_device_id` UNIQUE (`device_id`),

    RENAME INDEX `FKj2kob865pl9dv5vwrs2pmshjv` TO `fk_fcm_tokens_user_id`,
    DROP FOREIGN KEY `FKj2kob865pl9dv5vwrs2pmshjv`,
    ADD CONSTRAINT `fk_fcm_tokens_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `followings`
    RENAME INDEX `FKjj18b1cko9b560nabxxm6s8s` TO `fk_followings_follower_id`,
    DROP FOREIGN KEY `FKjj18b1cko9b560nabxxm6s8s`,
    ADD CONSTRAINT `fk_followings_follower_id` FOREIGN KEY (`follower_id`) REFERENCES `users` (`id`),

    RENAME INDEX `FKk7ut35l9qvaqrd7xlwiumdrmc` TO `fk_followings_followee_id`,
    DROP FOREIGN KEY `FKk7ut35l9qvaqrd7xlwiumdrmc`,
    ADD CONSTRAINT `fk_followings_followee_id` FOREIGN KEY (`followee_id`) REFERENCES `users` (`id`);

ALTER TABLE `group_members`
    RENAME INDEX `FKnr9qg33qt2ovmv29g4vc3gtdx` TO `fk_group_members_user_id`,
    DROP FOREIGN KEY `FKnr9qg33qt2ovmv29g4vc3gtdx`,
    ADD CONSTRAINT `fk_group_members_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),

    RENAME INDEX `FKrpgq4bl4kui39wk9mlkl26ib` TO `fk_group_members_group_id`,
    DROP FOREIGN KEY `FKrpgq4bl4kui39wk9mlkl26ib`,
    ADD CONSTRAINT `fk_group_members_group_id` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`);

ALTER TABLE `tasks`
    RENAME INDEX `FKj2p2ll95ivoc2w5o6xk8gv19n` TO `fk_tasks_challenge_id`,
    DROP FOREIGN KEY `FKj2p2ll95ivoc2w5o6xk8gv19n`,
    ADD CONSTRAINT `fk_tasks_challenge_id` FOREIGN KEY (`challenge_id`) REFERENCES `challenges` (`id`);

ALTER TABLE `huntings`
    RENAME INDEX `FKmfgv5v293958k0kkog8hj24qr` TO `fk_huntings_hunter_id`,
    DROP FOREIGN KEY `FKmfgv5v293958k0kkog8hj24qr`,
    ADD CONSTRAINT `fk_huntings_hunter_id` FOREIGN KEY (`hunter_id`) REFERENCES `users` (`id`),

    RENAME INDEX `FKnqgdpqd6yclj3ul8sourki49i` TO `fk_huntings_task_id`,
    DROP FOREIGN KEY `FKnqgdpqd6yclj3ul8sourki49i`,
    ADD CONSTRAINT `fk_huntings_task_id` FOREIGN KEY (`task_id`) REFERENCES `tasks` (`id`);

ALTER TABLE `user_credentials`
    DROP INDEX `UKthx1lw5kg5ygi8d8b90gv2ha3`,
    ADD CONSTRAINT `uk_user_credentials_user_id` UNIQUE (`user_id`),

    DROP FOREIGN KEY `FK98kxj78ausx1xo94eq4mkjm9q`,
    ADD CONSTRAINT `fk_user_credentials_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `user_sessions`
    DROP INDEX `UKs53lpnfnkol367c935m8ue3fc`,
    ADD CONSTRAINT `uk_user_sessions_user_id` UNIQUE (`user_id`),

    DROP FOREIGN KEY `FK8klxsgb8dcjjklmqebqp1twd5`,
    ADD CONSTRAINT `fk_user_sessions_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
