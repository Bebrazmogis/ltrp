CREATE TABLE IF NOT EXISTS player_vehicles
(
  id INT NOT unsigned NULL,
  owner_id INT NOT NULL,
  deaths INT NOT NULL,
  alarm VARCHAR(30) NULL,
  lock_name VARCHAR(100) NULL,
  lock_cracktime INT NULL,
  lock_price INT NULL,
  insurance TINYINT NOT NULL,
  doors INT NOT NULL,
  panels INT NOT NULL,
  lights INT NOT NULL,
  tires INT NOT NULL,
  health FLOAT NOT NULL DEFAULT 1000,
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES vehicles(id) ON DELETE CASCADE,
  FOREIGN KEY (owner_id) REFERENCES players(id) ON DELETE CASCADE
)ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


