create table if not exists player_vehicle_permissions
(
    id int auto_increment primary not null,
    player_id int unsigned not null,
    vehicle_id int unsigned not null,
    permission_id int unsigned not null,
    index(player_id),
    index(vehicle_id),
    index(permission_id)
)engine=innodb;