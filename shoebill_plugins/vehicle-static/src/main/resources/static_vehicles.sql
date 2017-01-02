CREATE TABLE IF NOT EXISTS static_vehicles
(
    id int primary key unsigned not null,
    model_id smallint unsigned not null,
    x float not null,
    y float not null,
    z float not null,
    angle float not null,
    interior_id smallint unsigned not null,
    world_id smallint unsigned not null,
    color1 tinyint unsigned not null,
    color2 tinyint unsigned not null,
    fuel_max float not null,
    fuel float not null,
    license varchar(32) not null,
    mileage float not null,
    foreign key(id) references vehicles(id) on delete cascade
)engine=innodb;