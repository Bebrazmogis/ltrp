
create table if not exists job_employees
(
    job_id int not null,
    player_id int not null,
    rank_id int not null,
    job_level tinyint unsigned not null,
    experience int not null,
    hours tinyint unsigned not null,
    remaining_contract tinyint unsigned not null,
    primary key(player_id),
    foreign key(job_id) references jobs(id),
    foreign key(player_id) references players(id),
    foreign key(rank_id) references job_ranks(id)
) engine=innodb default charset=utf8 collate=utf8_unicode_ci;
