
create table if not exists graffiti_objects
(
    id int unsigned auto_increment not null,
    model_id smallint unsigned not null,
    material_size tinyint unsigned not null,
    primary key(id)
) engine=innodb default charset=utf8 collate=utf8_unicode_ci;

create table if not exists graffiti_colors
(
    id int unsigned auto_increment not null,
    color int not null,
    primary key(id)
) engine=innodb default charset=utf8 collate=utf8_unicode_ci;

create table if not exists graffiti_fonts
(
    id int unsigned auto_increment not null,
    name varchar(32) not null,
    size tinyint unsigned not null,
    primary key(id)
) engine=innodb default charset=utf8 collate=utf8_unicode_ci;


create table if not exists graffiti
(
    id int unsigned auto_increment not null,
    author int not null,
    object_id int unsigned not null,
    `text` varchar(128) not null,
    pos_x float not null,
    pos_y float not null,
    poz_z float not null,
    rot_x float not null,
    rot_y float not null,
    rot_z float not null,
    font_id int unsigned not null,
    color_id int unsigned not null,
    approved_by int not null,
    created_at timestamp not null default current_timestamp,
    primary key(id),
    foreign key(author) references players(id) on delete cascade,
    foreign key(approved_by) references players(id) on delete cascade,
    foreign key(object_id) references graffiti_objects(id) on delete cascade,
    foreign key(font_id) references graffiti_fonts(id) on delete cascade,
    foreign key(color_id) references graffiti_colors(id) on delete cascade
) engine=innodb default charset=utf8 collate=utf8_unicode_ci;