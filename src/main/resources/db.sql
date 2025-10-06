create s4_tracking;

use s4_tracking;

create table SequelizeMeta
(
    name varchar(255) not null
        primary key,
    constraint name
        unique (name)
)
    collate = utf8mb3_unicode_ci;

create table t_space
(
    space_id    bigint auto_increment
        primary key,
    space_name  varchar(255) charset utf8mb3 not null,
    space_label varchar(255) charset utf8mb3 null,
    active      tinyint(1) default 1         null,
    constraint space_name
        unique (space_name)
);

create table t_notification
(
    notification_id bigint auto_increment
        primary key,
    message         varchar(255) charset utf8mb3                           not null,
    level           varchar(255) charset utf8mb3 default 'INFO'            not null,
    read_status     tinyint(1)                   default 0                 null,
    insert_time     datetime                     default CURRENT_TIMESTAMP null,
    sns_delivery_id varchar(255) charset utf8mb3                           null,
    space_id        bigint                       default 1                 not null,
    constraint t_notification_space_id_foreign_idx
        foreign key (space_id) references t_space (space_id)
            on update cascade
);

create index idx_insert_time
    on t_notification (insert_time);

create index idx_level
    on t_notification (level);

create index idx_read_status
    on t_notification (read_status);

create index idx_sns_delivery_id
    on t_notification (sns_delivery_id);

create index t_notification_level
    on t_notification (level);

create index t_notification_read_status
    on t_notification (read_status);

create table t_spot
(
    spot_id     bigint auto_increment
        primary key,
    spot_name   varchar(255) charset utf8mb3 not null,
    spot_label  varchar(255) charset utf8mb3 null,
    hardware_id varchar(255) charset utf8mb3 null,
    active      tinyint(1) default 1         null,
    space_id    bigint                       not null,
    critical    tinyint(1) default 0         not null,
    constraint spot_name
        unique (spot_name),
    constraint fk_spot_space
        foreign key (space_id) references t_space (space_id)
);

create table t_tag
(
    tag_id      bigint auto_increment
        primary key,
    tag_name    varchar(255) charset utf8mb3 not null,
    tag_label   varchar(255) charset utf8mb3 null,
    hardware_id varchar(255) charset utf8mb3 null,
    active      tinyint(1) default 1         null,
    constraint tag_name
        unique (tag_name)
);

create table t_tracking
(
    tracking_id   bigint auto_increment
        primary key,
    spot_id       bigint                             not null,
    tag_id        bigint                             not null,
    movement_time datetime default CURRENT_TIMESTAMP null,
    constraint t_tracking_ibfk_1
        foreign key (spot_id) references t_spot (spot_id),
    constraint t_tracking_ibfk_2
        foreign key (tag_id) references t_tag (tag_id)
);

create index idx_movement_time
    on t_tracking (movement_time);

create index idx_spot_tag
    on t_tracking (spot_id, tag_id);

create index t_tracking_movement_time
    on t_tracking (movement_time);

create index t_tracking_spot_id_tag_id
    on t_tracking (spot_id, tag_id);

create index tag_id
    on t_tracking (tag_id);

create table t_users
(
    user_id  bigint auto_increment
        primary key,
    name     varchar(255) charset utf8mb3 not null,
    login    varchar(255) charset utf8mb3 not null,
    password text                         not null,
    active   tinyint(1) default 1         null,
    constraint login
        unique (login)
);

create table if not exists t_ctl_extraction
(
    extraction_id bigint auto_increment primary key,
    last_position bigint not null,
    extraction_time datetime default CURRENT_TIMESTAMP null,
    spot_id bigint not null,
    constraint t_extraction_spot_id_foreign_idx
    foreign key (spot_id) references t_spot (spot_id)
    on update cascade
    );

