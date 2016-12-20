

create table if not exists player_jailtime
(
    id int unsigned auto_increment not null,
    player_id int not null,
    seconds_total int unsigned not null,
    seconds_remaining int unsigned not null,
    type varchar(8) not null,
    jailer int not null,
    created_at timestamp not null,
    primary key(id),
    foreign key(player_id) references players(id) on delete cascade,
    foreign key(jailer) references players(id) on delete cascade
) engine=innodb default charset=utf8 collate=utf8_unicode_ci;