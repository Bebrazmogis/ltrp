


/*
	CREATE TABLE IF NOT EXISTS ac_weapons(
		id INT AUTO_INCREMENT NOT NULL,
		weapon_id TINYINT UNSIGNED NOT NULL,
		ammo SMALLINT NOT NULL,
		owner_type ENUM('player', 'house', 'vehicle'),
		owner_id INT NOT NULL,
		PRIMARY KEY(id)
	) ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;
	
*/


#define MAX_WEAPONS 					5000

#define WEAPON_OWNER_TYPE_PLAYER 		1
#define WEAPON_OWNER_TYPE_HOUSE 		2
#define WEAPON_OWNER_TYPE_VEHICLE 		3

#define WEAPON_OWNER_NULL 				(0)

#define INVALID_WEAPON_ID 				Weapon:-1


enum E_GENERAL_WEAPON_DATA 
{
	SqlId,
	WeaponId,
	Ammo,
	OwnerType,
	Owner
};

static AC_WeaponData[ MAX_WEAPONS ][ E_GENERAL_WEAPON_DATA ];


stock Weapon:CreateWeapon(weaponid, ammo)
{
	new query[100], Cache:result;
	for(new i = 0; i < MAX_WEAPONS; i++)
	{
		if(!AC_WeaponData[ i ][ SqlId ])
		{
			mysql_format(DbHandle, query, sizeof(query), "INSERT INTO ac_weapons (weapon_id, ammo) VALUES (%d, %d)",
				weaponid, ammo);
			result = mysql_query(DbHandle, query);
			AC_WeaponData[ i ][ SqlId ] = cache_insert_id();
			cache_delete(result);
			AC_WeaponData[ i ][ WeaponId ] = weaponid;
			AC_WeaponData[ i ][ Ammo ] = ammo;
			AC_WeaponData[ i ][ OwnerType ] = 0;
			AC_WeaponData[ i ][ Owner ] = WEAPON_OWNER_NULL;
			return Weapon:i;
		}
	}
	return INVALID_WEAPON_ID;
}

stock SetWeaponOwner(Weapon:weapon, ownertype, ownersqlid)
{
	new query[120];
	mysql_format(DbHandle, query, sizeof(query), "UPDATE ac_weapons SET owner_type = '%e', owner_id = %d WHERE id = %d",
		GetWeaponOwnerTypeString(ownertype), ownersqlid, AC_WeaponData[ _:weapon ][ SqlId ]);
	mysql_tquery(DbHandle, query);


	AC_WeaponData[ _:weapon ][ OwnerType ] = ownertype;
	AC_WeaponData[ _:weapon ][ Owner ] = ownersqlid;
	return 1;
}


static GetWeaponOwnerTypeString(ownertype)
{
	new s[16];
	switch(ownertype)
	{
		case WEAPON_OWNER_TYPE_PLAYER: s = 'player';
		case WEAPON_OWNER_TYPE_VEHICLE: s = 'vehicle';
		case WEAPON_OWNER_TYPE_HOUSE: s = 'house';
	}
	return s;
}




stock OnPlayerRunOutOfAmmo(playerid, weaponid)
{
    format(query, sizeof(query), "UPDATE ")
}
