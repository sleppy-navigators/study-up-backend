create table `groups`
(
    id            bigint auto_increment primary key,
    name          varchar(255) not null,
    description   varchar(255) not null,
    thumbnail_url varchar(255) null,
    deleted       bit          not null comment 'Soft-delete indicator',
    created_at    datetime(6)  not null,
    created_by    bigint       null,
    updated_at    datetime(6)  not null,
    updated_by    bigint       null
);

create table bots
(
    id         bigint auto_increment primary key,
    name       varchar(255) not null,
    group_id   bigint       not null,
    created_at datetime(6)  not null,
    updated_at datetime(6)  not null,
    constraint UKbl66pntl1ns8c1wowc4yqv9yd
        unique (group_id),
    constraint FK5frfnih7gr71yusbn2221m4nx
        foreign key (group_id) references `groups` (id)
);

create table group_invitations
(
    id             bigint auto_increment primary key,
    group_id       bigint       not null,
    invitation_key varchar(255) not null,
    created_at     datetime(6)  not null,
    created_by     bigint       null,
    updated_at     datetime(6)  not null,
    updated_by     bigint       null,
    constraint FKkjm6h04g1d3bf3iinku0wagf5
        foreign key (group_id) references `groups` (id)
);

create table users
(
    id         bigint auto_increment primary key,
    username   varchar(255) not null,
    email      varchar(255) not null,
    deleted    bit          not null comment 'Soft-delete indicator',
    created_at datetime(6)  not null,
    updated_at datetime(6)  not null
);

create table challenges
(
    id          bigint auto_increment primary key,
    title       varchar(255) not null,
    description varchar(255) null,
    deadline    datetime(6)  not null,
    owner_id    bigint       not null,
    group_id    bigint       not null,
    deleted     bit          not null comment 'Soft-delete indicator',
    created_at  datetime(6)  not null,
    updated_at  datetime(6)  not null,
    constraint FK6phslw6i958x3hgubwoglpsgh
        foreign key (group_id) references `groups` (id),
    constraint FKbudvg0p3ffvgvbeyf9l8nume3
        foreign key (owner_id) references users (id)
);

create table fcm_tokens
(
    id          bigint auto_increment primary key,
    user_id     bigint                         not null,
    device_id   varchar(255)                   not null,
    token       varchar(255)                   not null,
    device_type enum ('ANDROID', 'IOS', 'WEB') not null,
    created_at  datetime(6)                    not null,
    updated_at  datetime(6)                    not null,
    constraint UKnbv2435ks37v3744qget6y41m
        unique (device_id),
    constraint FKj2kob865pl9dv5vwrs2pmshjv
        foreign key (user_id) references users (id)
);

create table group_members
(
    id         bigint auto_increment primary key,
    user_id    bigint      not null,
    group_id   bigint      not null,
    deleted    bit         not null comment 'Soft-delete indicator',
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint FKnr9qg33qt2ovmv29g4vc3gtdx
        foreign key (user_id) references users (id),
    constraint FKrpgq4bl4kui39wk9mlkl26ib
        foreign key (group_id) references `groups` (id)
);

create table tasks
(
    id             bigint auto_increment primary key,
    title          varchar(255) not null,
    external_links varchar(255) not null,
    image_urls     varchar(255) not null,
    deadline       datetime(6)  not null,
    challenge_id   bigint       not null,
    deleted        bit          not null comment 'Soft-delete indicator',
    certified_at   datetime(6)  null,
    created_at     datetime(6)  not null,
    updated_at     datetime(6)  not null,
    constraint FKj2p2ll95ivoc2w5o6xk8gv19n
        foreign key (challenge_id) references challenges (id)
);

create table user_credentials
(
    id         bigint auto_increment primary key,
    user_id    bigint       not null,
    subject    varchar(255) not null,
    provider   varchar(255) not null,
    deleted    bit          not null comment 'Soft-delete indicator',
    created_at datetime(6)  not null,
    updated_at datetime(6)  not null,
    constraint UKthx1lw5kg5ygi8d8b90gv2ha3
        unique (user_id),
    constraint FK98kxj78ausx1xo94eq4mkjm9q
        foreign key (user_id) references users (id)
);

create table user_sessions
(
    id            bigint auto_increment primary key,
    user_id       bigint       not null,
    access_token  varchar(512) null,
    refresh_token varchar(255) null,
    expiration    datetime(6)  null,
    created_at    datetime(6)  not null,
    updated_at    datetime(6)  not null,
    constraint UKs53lpnfnkol367c935m8ue3fc
        unique (user_id),
    constraint FK8klxsgb8dcjjklmqebqp1twd5
        foreign key (user_id) references users (id)
);
