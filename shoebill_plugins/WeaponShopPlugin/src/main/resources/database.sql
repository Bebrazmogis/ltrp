

create table if not exists weapon_shops
(
    id int unsigned auto_increment not null,
    name varchar(32) not null,
    x float not null,
    y float not null,
    z float not null,
    world_id int not null,
    interior_id int not null,
    label_color int null,
    label_text varchar(64) null,
    pickup_model smallint unsigned null,
    pickup_text varchar(64) null,
    primary key(id)
) engine=innodb default charset=utf8 collate=utf8_unicode_ci;


create table if not exists weapon_shop_weapons
(
    id int unsigned auto_increment not null,
    shop_id int unsigned not null,
    name varchar(32) not null,
    model_id tinyint unsigned not null,
    ammo smallint unsigned not null,
    price int unsigned not null,
    primary key(id),
    foreign key(shop_id) references weapon_shops(id) on delete cascade
) engine=innodb default charset=utf8 collate=utf8_unicode_ci;