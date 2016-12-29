CREATE TABLE IF NOT EXISTS vehicles
(
    id int auto_increment primary key unsigned not null,
    created_at timestamp not null default now()
)engine=INNODB;