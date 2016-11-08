

create table if not exists trucker_cargo
(
    id int unsigned auto_increment not null,
    name varchar(64) not null,
    type varchar(32) not null,
    primary key(id)
) engine=innodb default charset=utf8 collate=utf8_unicode_ci;

create table if not exists trucker_industries
(
    id int unsigned auto_increment not null,
    name varchar(128) not null,
    x float not null,
    y float not null,
    z float not null,
    virtual_world int not null,
    interior_id int not null,
    primary key(id)
) engine=innodb default charset=utf8 collate=utf8_unicode_ci;

create table if not exists trucker_industry_stock
(
    id int unsigned auto_increment not null,
    industry_id int unsigned not null,
    cargo_id int unsigned not null,
    price int unsigned not null,
    current_stock smallint unsigned not null,
    max_stock smallint unsigned not null,
    primary key(id),
    foreign key(industry_id) references trucker_industries(id) on delete cascade,
    foreign key(cargo_id) references trucker_cargo(id) on delete cascade
)engine=innodb default charset=utf8 collate=utf8_unicode_ci;

create table if not exists trucker_industry_productions
(
    id int unsigned auto_increment not null,
    industry_id int unsigned not null,
    primary key(id),
    foreign key(industry_id) references trucker_industries(id) on delete cascade
)engine=innodb default charset=utf8 collate=utf8_unicode_ci;

create table if not exists trucker_industry_production_materials
(
    id int unsigned auto_increment not null,
    production_id int unsigned not null,
    cargo_id int unsigned not null,
    amount smallint unsigned not null,
    type varchar(16) not null,
    primary key(id),
    foreign key(cargo_id) references trucker_cargo(id) on delete cascade,
    foreign key(production_id) references trucker_industry_productions(id) on delete cascade
)engine=innodb default charset=utf8 collate=utf8_unicode_ci;