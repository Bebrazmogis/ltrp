/**
 * Phones.p yra LTRP modifikacijos dalis
 * Kodo autorius: Bebras 2015
 *
 * Aprašymas
 *
 * Telefonai nėra žaidėjo ar kieno kito duomenų dalis. Telefonai gali būti laikomi žaidėjo inventoriuje, namusoe, garaže ar transproto priemonėje.
 * Duomenų bazėje telefonus identifikuoja jo numeris kuris taip pat naudojamas skambinimui žaidime
 * Šis skriptas taip pat atsakingas už taksofonų krovimą, paruošimą bei administravimą. 
 * Visi taksofonai taip pat turi savo numerį kuris NEGALI būti naudojamas žaidėjo telefonui.
 *
**/

/** MySQL database tables:

CREATE TABLE IF NOT EXISTS phones (
	number INT NOT NULL,
	online TINYINT NOT NULL DEFAULT '1',
	added_on DATETIME NOT NULL,
	location_type TINYINT NOT NULL,
	location_id INT NOT NULL,
	PRIMARY KEY(number),
	INDEX(location_type),
	INDEX(location_id)
) ENGINE=INNODB DEAFULT CHARSET=cp1257 COLLATE=cp1257_bin;


CREATE TABLE IF NOT EXISTS phone_contacts (
  number int(11) NOT NULL,
  phone_number int(11) NOT NULL,
  name varchar(24) NOT NULL,
  entry_date int(11) NOT NULL,
  PRIMARY KEY (number, phone_number)
) ENGINE=InnoDB DEFAULT CHARSET=cp1257;

ALTER TABLE phone_contacts ADD FOREIGN KEY(number) REFERENCES phones(number) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS phone_sms (
	number INT AUTO_INCREMENT NOT NULL,
	recipient_number INT NOT NULL,
	sender_number INT NOT NULL,
	`date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`text` VARCHAR(128) NOT NULL,
	`read` BOOLEAN NOT NULL DEFAULT '0',
	PRIMARY KEY(number)
) ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;

ALTER TABLE phone_contacts ADD FOREIGN KEY(number) REFERENCES phones(number) ON DELETE CASCADE;


CREATE TABLE IF NOT EXISTS payphones (
	number INT NOT NULL,
	pos_x FLOAT NOT NULL,
	pos_y FLOAT NTO NULL,
	pos_z FLOAT NOT NULL,
	virtual_world INT NOT NULL,
	interior INT NOT NULL,
	PRIMARY KEY(number)
) ENGINE=INNODB DEAFULT CHARSET=cp1257 COLLATE=cp1257_bin;



	


**/

#include <YSI\y_hooks>

#define MAX_PAYPHONES 					40
#define MAX_PHONEBOOK_CONTACT_NAME		64
#define MAX_PHONES 						700
#define MAX_PHONEBOOK_ENTRIES           10

/* 	                                                                          
	                               ,,           ,,        ,,                  
	`7MMF'   `7MF'                 db          *MM      `7MM                  
	  `MA     ,V                                MM        MM                  
	   VM:   ,V ,6"Yb.  `7Mb,od8 `7MM   ,6"Yb.  MM,dMMb.  MM  .gP"Ya  ,pP"Ybd 
	    MM.  M'8)   MM    MM' "'   MM  8)   MM  MM    `Mb MM ,M'   Yb 8I   `" 
	    `MM A'  ,pm9MM    MM       MM   ,pm9MM  MM     M8 MM 8M"""""" `YMMMa. 
	     :MM;  8M   MM    MM       MM  8M   MM  MM.   ,M9 MM YM.    , L.   I8 
	      VF   `Moo9^Yo..JMML.   .JMML.`Moo9^Yo.P^YbmdP'.JMML.`Mbmmd' M9mmmP' 
	                                                                          
	                                                                           */


// Vietos kur gali būti telefonas
enum E_PRIVATE_PHONE_LOCATIONS 
{
	PlayerInventory,
	HouseInventory,
	GarageInventory,
	VehicleTrunk,
};

// Nuosavo telefono duomenys
enum E_PRIVATE_PHONE_DATA
{
	Number,
	bool:Online,
};

// Taksofonai
enum E_PAYPHONE_DATA 
{
	Number,
	Float:X,
	Float:Y,
	Float:Z,
	Interior,
	VirtualWorld,
};

// Telefonų knygų duomenys
enum E_PHONEBOOK_DATA 
{
	OwnerNumber,
    ContactNumber,
    Name[ MAX_PHONEBOOK_CONTACT_NAME ]
};

static PlayerPhoneBook[ MAX_PHONES ][ MAX_PHONEBOOK_ENTRIES ][ E_PHONEBOOK_DATA ],
	PayphoneData[ MAX_PAYPHONES ][ E_PAYPHONE_DATA ];



forward OnPayphoneLoad();


/* 	                                                                           
	                      ,,    ,,  ,,                                         
	  .g8"""bgd         `7MM  `7MM *MM                       `7MM              
	.dP'     `M           MM    MM  MM                         MM              
	dM'       ` ,6"Yb.    MM    MM  MM,dMMb.   ,6"Yb.  ,p6"bo  MM  ,MP',pP"Ybd 
	MM         8)   MM    MM    MM  MM    `Mb 8)   MM 6M'  OO  MM ;Y   8I   `" 
	MM.         ,pm9MM    MM    MM  MM     M8  ,pm9MM 8M       MM;Mm   `YMMMa. 
	`Mb.     ,'8M   MM    MM    MM  MM.   ,M9 8M   MM YM.    , MM `Mb. L.   I8 
	  `"bmmmd' `Moo9^Yo..JMML..JMML.P^YbmdP'  `Moo9^Yo.YMbmd'.JMML. YA.M9mmmP' 
	                                                                           
	                                                                            */

hook OnGameModeInit()
{
	mysql_pquery(DbHandle, "SELECT * FROM payphones", "OnPayphoneLoad", "");
	return 1;
}

public OnPayphoneLoad()
{
	for(new i = 0; i < cache_get_row_count(); i++)
	{
		if(i >= MAX_PAYPHONES)
		{
			ErrorLog("Number of payphones(%d) in table 'payphones' exceeds MAX_PAYPHONES(" #MAX_PAYPHONES ")", cache_get_row_count());
			break;
		}
		PayphoneData[ i ][ Number ] = cache_get_field_content_int(i, "number");
		PayphoneData[ i ][ X ] = cache_get_field_content_float(i, "pos_x");
		PayphoneData[ i ][ Y ] = cache_get_field_content_float(i, "pos_y");
		PayphoneData[ i ][ Z ] = cache_get_field_content_float(i, "pos_z");
		PayphoneData[ i ][ Interior ] = cache_get_field_content_int(i, "interior");
		PayphoneData[ i ][ VirtualWorld ] = cache_get_field_content_int(i, "virtual_world");
	}
	printf("Loaded %d payphones.", cache_get_row_count());
	return 1;
}
