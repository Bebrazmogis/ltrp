CREATE TABLE IF NOT EXISTS bank_wire_transfers (
  id INT AUTO_INCREMENT NOT NULL ,
  from_account_id INT NOT NULL ,
  to_account_id INT NOT NULL ,
  amount INT NOT NULL ,
  date TIMESTAMP NOT NULL ,
  PRIMARY KEY (id),
  FOREIGN KEY (from_account_id) REFERENCES bank_accounts(id) ON DELETE CASCADE ,
  FOREIGN KEY (to_account_id) REFERENCES bank_accounts(id) ON DELETE CASCADE
)ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;