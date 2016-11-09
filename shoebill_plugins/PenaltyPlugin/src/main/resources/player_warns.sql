
create table if not exists player_warns
(
    id int unsigned auto_increment not null,
    player_id int not null,
    warned_by int not null,
    reason varchar(64) not null,
    created_at timestamp not null default current_timestamp,
    deleted_at timestamp null,
    primary key(id),
    foreign key(player_id) references players(id) on delete cascade,
    foreign key(warned_by) references players(id) on delete cascade
) engine=innodb default charset=utf8 collate=utf8_unicode_ci;