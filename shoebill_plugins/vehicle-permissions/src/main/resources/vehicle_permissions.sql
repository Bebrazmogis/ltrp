create table if not exists vehicle_permissions
(
    id int auto_increment primary unsigned not null,
    name varchar(32) not null,
    identifier varchar(32) not null,
)engine=innodb