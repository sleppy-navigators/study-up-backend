-- 1. Create the followings table
CREATE TABLE followings
(
    id          bigint auto_increment primary key,
    followee_id bigint      not null,
    follower_id bigint      not null,
    created_at  datetime(6) not null,
    updated_at  datetime(6) not null,
    constraint FKjj18b1cko9b560nabxxm6s8s
        foreign key (follower_id) references users (id),
    constraint FKk7ut35l9qvaqrd7xlwiumdrmc
        foreign key (followee_id) references users (id)
);
