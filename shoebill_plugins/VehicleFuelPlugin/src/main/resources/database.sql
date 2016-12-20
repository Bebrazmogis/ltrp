

create table if not exists fuel_stations
(
    id int unsigned auto_increment not null,
    x float not null,
    y float not null,
    z float not null,
    interior int not null,
    virtual_world int not null,
    radius float not null,
    primary key(id)
) engine=innodb default charset=utf8 collate=utf8_unicode_ci;