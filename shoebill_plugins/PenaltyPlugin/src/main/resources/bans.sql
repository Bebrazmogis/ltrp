
create table if not exists bans
(
    id int unsigned auto_increment not null,
    player_id int null,
    ip varchar(15) not null,
    banned_by int null,
    reason varchar(128) not null,
    duration int null,
    created_at timestamp not null,
    deleted_at timestamp null,
    primary key(id),
    foreign key(player_id) references players(id) on delete set null,
    foreign key(banned_by) references players(id) on delete set null
) engine=innodb default charset=utf8 collate=utf8_unicode_ci;