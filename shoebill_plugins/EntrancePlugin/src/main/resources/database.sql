

create table if not exists entrances
(
    id int unsigned auto_increment not null,
    name varchar(128) not null,
    x float not null,
    y float not null,
    z float not null,
    angle float not null,
    interior int not null,
    virtual_world int not null,
    label_color int not null,
    pickup_model smallint unsigned not null,
    job_id int null,
    is_vehicle tinyint unsigned not null,
    exit_id int null,
    primary key(id)
) engine=innodb default charset=utf8 collate=utf8_unicode_ci;
