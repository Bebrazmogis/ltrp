/**
 * Phones.p yra LTRP modifikacijos dalis
 * Kodo autorius: Bebras 2015
 *
 * Apra�ymas
 *
 * Telefonai n�ra �aid�jo ar kieno kito duomen� dalis. Telefonai gali b�ti laikomi �aid�jo inventoriuje, namusoe, gara�e ar transproto priemon�je.
 * Duomen� baz�je telefonus identifikuoja jo numeris kuris taip pat naudojamas skambinimui �aidime
 * �is skriptas taip pat atsakingas u� taksofon� krovim�, paruo�im� bei administravim�. 
 * Visi taksofonai taip pat turi savo numer� kuris NEGALI b�ti naudojamas �aid�jo telefonui.
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
) ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;


CREATE TABLE IF NOT EXISTS phone_contacts (
  number int(11) NOT NULL,
  contact_number int(11) NOT NULL,
  name varchar(24) NOT NULL,
  entry_date int(11) NOT NULL,
  PRIMARY KEY (number, contact_number)
) ENGINE=InnoDB DEFAULT CHARSET=cp1257;

ALTER TABLE phone_contacts ADD FOREIGN KEY(number) REFERENCES phones(number) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS phone_sms (
	sender_number INT NOT NULL,
	recipient_number INT NOT NULL,
	`date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`text` VARCHAR(128) NOT NULL,
	`read` BOOLEAN NOT NULL DEFAULT '0',
	PRIMARY KEY(sender_number)
) ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;

ALTER TABLE phone_sms ADD FOREIGN KEY(sender_number) REFERENCES phones(number) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS phone_conversation_logs (
	id INT AUTO_INCREMENT NOT NULL,
	from_number INT NOT NULL,
	to_number INT NOT NULL,
	`text` VARCHAR(128),
	date DATETIME NOT NULL,
	PRIMARY KEY(id)
) ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;

ALTER TABLE phone_conversation_logs ADD FOREIGN KEY(from_number) REFERENCES phones(number) ON DELETE CASCADE;
ALTER TABLE phone_conversation_logs ADD FOREIGN KEY(to_number) REFERENCES phones(number) ON DELETE CASCADE;


CREATE TABLE IF NOT EXISTS payphones (
	number INT NOT NULL,
	pos_x FLOAT NOT NULL,
	pos_y FLOAT NOT NULL,
	pos_z FLOAT NOT NULL,
	virtual_world INT NOT NULL,
	interior INT NOT NULL,
	PRIMARY KEY(number)
) ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;



	


**/

#include <YSI\y_hooks>

#define MAX_PAYPHONES 					40
#define MAX_PHONEBOOK_CONTACT_NAME		64
#define MAX_PHONES 						700
#define MAX_PHONEBOOK_ENTRIES           10

#define PHONE_PRICE_PER_SECOND 			2

#define GetPhoneTalkPrice(%0)			(PHONE_PRICE_PER_SECOND*%0)

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


// Vietos kur gali b�ti telefonas
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
	Float:PosX,
	Float:PosY,
	Float:PosZ,
	Interior,
	VirtualWorld,
};


static PayphoneData[ MAX_PAYPHONES ][ E_PAYPHONE_DATA ];



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
		PayphoneData[ i ][ PosX ] = cache_get_field_content_float(i, "pos_x");
		PayphoneData[ i ][ PosY ] = cache_get_field_content_float(i, "pos_y");
		PayphoneData[ i ][ PosZ ] = cache_get_field_content_float(i, "pos_z");
		PayphoneData[ i ][ Interior ] = cache_get_field_content_int(i, "interior");
		PayphoneData[ i ][ VirtualWorld ] = cache_get_field_content_int(i, "virtual_world");
	}
	printf("Loaded %d payphones.", cache_get_row_count());
	return 1;
}






IsValidPhoneNumber(phonenumber)
{
	new query[60], Cache:result, bool:valid = false;

	mysql_format(DbHandle, query, sizeof(query), "SELECT number FROM phones WHERE number = %d", phonenumber);
	result = mysql_query(DbHandle, query);
	if(cache_get_row_count())
		valid = true;
	cache_delete(result);

	return valid;
}


IsValidPlayerNumber(phonenumber)
{
	new query[90], Cache:result, bool:valid = false;

	mysql_format(DbHandle, query, sizeof(query), "SELECT number FROM phones WHERE number = %d AND location_type = %d", phonenumber, _:PlayerInventory);
	result = mysql_query(DbHandle, query);
	if(cache_get_row_count())
		valid = true;
	cache_delete(result);

	return valid;
}





E_PRIVATE_PHONE_LOCATIONS:GetPhoneNumberLocation(phonenumber)
{
	foreach(new i : Player)
		if(IsPlayerPhonenumber(i, phonenumber))
			return PlayerInventory;
	if(IsPhoneInAnyHouse(phonenumber))
		return HouseInventory;

	if(IsPhoneInAnyGarage(phonenumber))
		return GarageInventory;

	if(IsPhoneInAnyVehicle(phonenumber))
		return VehicleTrunk;

	return E_PRIVATE_PHONE_LOCATIONS:-1;
}

IsPhoneNumberOnline(phonenumber)
{
	new E_PRIVATE_PHONE_LOCATIONS:location = GetPhoneNumberLocation(phonenumber);

	switch(location)
	{
		case PlayerInventory:
			return IsPlayerPhonenumberOnline(phonenumber);
		case HouseInventory:
			return IsHousePhonenumberOnline(phonenumber);
		case GarageInventory:
			return IsGaragePhonenumberOnline(phonenumber);
		case VehicleTrunk:
			return IsVehiclePhonenumberOnline(phonenumber);
	}
	return false;
}

IsPayphoneNumber(phonenumber)
{
	if(!phonenumber)
		return 0;

	for(new i = 0; i < MAX_PAYPHONES; i++)	
		if(PayphoneData[ i ][ Number ] == phonenumber)
			return true;

	return false;
}

AddPhonebookContact(phonenumber, contactnumber, contactname[])
{
	new query[ 256 ];
	mysql_format(DbHandle, query, sizeof(query), "INSERT INTO phone_contacts (number, contact_number, name, entry_date) VALUES (%d, %d, '%e', %d) \ 
		ON DUPLICATE KEY UPDATE name = VALUES(name), entry_date = VALUES(entry_date)",
		phonenumber, contactnumber, contactname, gettime());
	return mysql_pquery(DbHandle, query);
}


RemovePhonebookContact(phonenumber, contactnumber)
{
	new query[ 100 ];
	mysql_format(DbHandle, query, sizeof(query), "DELETE FROM phone_contacts WHERE number = %d AND contact_number = %d",
		phonenumber, contactnumber);
	return mysql_pquery(DbHandle, query);
}


stock LogPhoneConversation(fromnumber, tonumber, const text[])
{
    new query[ 256 ];
    mysql_format(DbHandle, query, sizeof(query), "INSERT INTO phone_conversation_logs (from_number, to_number, `text`, date) VALUES (%d, %d, '%e', %d)",
    	fromnumber, tonumber, text);
    return mysql_pquery(DbHandle, query);
}