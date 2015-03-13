


	CREATE TABLE IF NOT EXISTS industries (
		id INT(11) NOT NULL AUTO_INCREMENT,
		name VARCHAR(64) NOT NULL,
		x FLOAT NOT NULL,
		y FLOAT NOT NULL,
		z FLOAT NOT NULL,
        PRIMARY KEY(id)
	);


    CREATE TABLE IF NOT EXISTS biznes_accepted_cargo (
        biz_type TINYINT(3) UNSIGNED NOT NULL,
        cargo_id INT(11) NOT NULL,
        PRIMARY KEY(biz_type, cargo_id)
    );  
    ALTER TABLE biznes_accepted_cargo ADD FOREIGN KEY (cargo_id) REFERENCES trucker_cargo(id) ON DELETE CASCADE ON UPDATE CASCADE;


    CREATE TABLE IF NOT EXISTS commodities (
        industry_id INT(11) NOT NULL,
        cargo_id INT(11) NOT NULL,
        sell_buy_status VARCHAR(8) NOT NULL,
        current_stock INT(11) NOT NULL,
        type VARCHAR(9) NOT NULL,
        price INT(11) UNSIGNED NOT NULL,
        PRIMARY KEY(industry_id, cargo_id, type)
    );
    ALTER TABLE commodities ADD FOREIGN KEY (cargo_id) REFERENCES trucker_cargo(id) ON DELETE CASCADE ON UPDATE CASCADE;



    CREATE TABLE IF NOT EXISTS trucker_cargo (
        id INT(11) NOT NULL AUTO_INCREMENT,
        name VARCHAR(24) NOT NULL,
        `limit` SMALLINT(5) UNSIGNED NOT NULL,
        production SMALLINT(5) UNSIGNED NOT NULL,
        consumption TINYINT(3) UNSIGNED NOT NULL,
        slot SMALLINT(5) UNSIGNED NOT NULL,
        type TINYINT(3) UNSIGNED NOT NULL,
        PRIMARY KEY(id)
    );


CREATE TABLE IF NOT EXISTS vehicle_shops (
    id INT(11) NOT NULL AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    x FLOAT NOT NULL,
    y FLOAT NOT NULL,
    z FLOAT NOT NULL,
    PRIMARY KEY(id)
) ENGINE=INNODB; 

CREATE TABLE IF NOT EXISTS vehicle_shop_spawns (
    id INT(11) NOT NULL AUTO_INCREMENT,
    shop_id INT(11) NOT NULL,
    x FLOAT NOT NULL,
    y FLOAT NOT NULL,
    z FLOAT NOT NULL,
    angle FLOAT NOT NULL,
    PRIMARY KEY(`id`)
) ENGINE=INNODB;

ALTER TABLE vehicle_shop_spawns ADD FOREIGN KEY(shop_id) REFERENCES vehicle_shops(id) ON DELETE CASCADE;



CREATE TABLE IF NOT EXISTS vehicle_shop_vehicles (
    shop_id INT(11) NOT NULL,
    model SMALLINT(5) NOT NULL,
    price INT(11) UNSIGNED NOT NULL,
    PRIMARY KEY(shop_id, model)
) ENGINE = INNODB;

ALTER TABLE vehicle_shop_vehicles ADD FOREIGN KEY(shop_id) REFERENCES vehicle_shops(id) ON DELETE CASCADE;


CREATE TABLE IF NOT EXISTS player_phone_contacts (
    player_id INT(11) NOT NULL,
    phone_number INT(11) NOT NULL,
    name VARCHAR(24) CHARACTER SET cp1257 NOT NULL,
    entry_date INT(11) NOT NULL,
    PRIMARY KEY(player_id, phone_number)
) ENGINE=INNODB;

ALTER TABLE `players` ENGINE = InnoDB;
ALTER TABLE player_phone_contacts ADD FOREIGN KEY(player_id) REFERENCES players(id) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS fishing_spots (
    id INT NOT NULL AUTO_INCREMENT,
    min_x FLOAT NOT NULL,
    min_y FLOAT NOT NULL,
    max_x FLOAT NOT NULL,
    max_y FLOAT NOT NULL,
    max_fish SMALLINT UNSIGNED NOT NULL DEFAULT '100',
    area_color VARCHAR(16) NOT NULL DEFAULT 'FFFFFFAA',
    replenish_time SMALLINT NOT NULL DEFAULT '180',
    PRIMARY KEY(id)
) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS vehicle_fish (
    vehicle_id INT NOT NULL,
    amount SMALLINT UNSIGNED NOT NULL,
    PRIMARY KEY(vehicle_id)
) ENGINE=INNODB;

ALTER TABLE vehicles ENGINE=INNODB;
ALTER TABLE vehicle_fish ADD FOREIGN KEY(vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE;


ALTER TABLE players ADD COLUMN Hunger TINYINT(3) UNSIGNED NOT NULL DEFAULT '0';