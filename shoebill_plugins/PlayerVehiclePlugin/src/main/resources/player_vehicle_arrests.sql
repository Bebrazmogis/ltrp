CREATE TABLE IF NOT EXISTS player_vehicle_arrests (
  id INT AUTO_INCREMENT NOT NULL ,
  vehicle_id INT NOT NULL,
  arrested_by INT NOT NULL,
  reason VARCHAR(128) NOT NULL ,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP NULL,
  PRIMARY KEY (id),
  INDEX (vehicle_id),
  INDEX(arrested_by),
  FOREIGN KEY (vehicle_id) REFERENCES player_vehicles(id) ON DELETE CASCADE ,
  FOREIGN KEY (arrested_by) REFERENCES players(id) ON DELETE CASCADE
)ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
