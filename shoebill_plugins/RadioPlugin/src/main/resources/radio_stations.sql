CREATE TABLE IF NOT EXISTS radio_stations
(
    id int auto_increment not null unsigned primary,
    name varchar(64) not null,
    url varchar(128) not null
)ENGINE=INNODB;