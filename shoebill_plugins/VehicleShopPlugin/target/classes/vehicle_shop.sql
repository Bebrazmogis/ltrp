CREATE TABLE IF NOT EXISTS vehicle_shops (
  id INT AUTO_INCREMENT NOT NULL,
  name VARCHAR (255) NOT NULL,
  x FLOAT NOT NULL,
  y FLOAT  NOT NULL,
  z FLOAT NOT NULL,
  interior TINYINT UNSIGNED NOT NULL,
  virtual_world SMALLINT UNSIGNED NOT NULL,
  PRIMARY KEY(id)
)ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;

cREATE TABLE IF NOT EXISTS vehicle_shop_spawns (
  id INT AUTO_INCREMENT NOT NULL,
  shop_id INT NOT NULL,
  x FLOAT NOT NULL,
  y FLOAT NOT NULL,
  z FLOAT NOT NULL,
  angle FLOAT NOT NULL,
  interior TINYINT UNSIGNED NOT NULL,
  virtual_world SMALLINT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  INDEX(shop_id),
  FOREIGN KEY(shop_id) REFERENCES vehicle_shops(id) ON DELETE CASCADE
)ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;