#include <YSI\y_hooks>








/*

	CREATE TABLE IF NOT EXISTS player_weapons (
		player_id INT NOT NULL,
		weapon_id TINYINT UNSIGNED NOT NULL,
		ammo SMALLINT UNSIGNED NOT NULL,
		PRIMARY KEY(player_id, weapon_id, is_job_weapon)
	) ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;
	ALTER TABLE player_weapons ADD FOREIGN KEY(player_id) REFERENCES players(id) ON DELETE CASCADE;	
*/


#define MAX_PLAYER_WEAPONS 				46

enum E_PLAYER_WEAPON_DATA 
{
	WeaponId,
	Ammo,
	bool:IsJob,
};

static PlayerWeapons[ MAX_PLAYERS ][ MAX_PLAYER_WEAPONS ][ E_PLAYER_WEAPON_DATA ];



forward OnPlayerWeaponLoad(playerid);

#define GivePlayerJobWeapon(%0,%1,%2) wep_GivePlayerWeapon(%0, %1, %2, false, true)

stock wep_GivePlayerWeapon(playerid, weaponid, ammo, bool:update_db = true, bool:job_weapon = false)
{
	printf("wep_GivePlayerWeapon(%s, %d, %d, %d, %d)", GetName(playerid), weaponid, ammo, update_db, job_weapon);
	if(update_db && !job_weapon)
	{
		new query[170];
		mysql_format(DbHandle, query, sizeof(query), "INSERT INTO player_weapons (player_id, weapon_id, ammo) VALUES (%d, %d, %d) ON DUPLICATE KEY UPDATE ammo = ammo + VALUES(ammo)",
			GetPlayerSqlId(playerid), weaponid, ammo);
		mysql_pquery(DbHandle, query);
	}
	new freeindex = -1, bool:found = false;
	for(new i = 0; i < MAX_PLAYER_WEAPONS; i++)
	{
		if(!PlayerWeapons[ playerid ][ i ][ WeaponId ])
		{
			if(freeindex == -1)
				freeindex = i;
			continue;
		}
		if(PlayerWeapons[ playerid ][ i ][ WeaponId ] != weaponid)
			continue;

		if(PlayerWeapons[ playerid ][ i ][ IsJob ] != job_weapon)
			continue;

		PlayerWeapons[ playerid ][ i ][ Ammo ] += ammo;
		found = true;
		break;
	}
	if(!found && freeindex != -1)
	{
		PlayerWeapons[ playerid ][ freeindex ][ WeaponId ] = weaponid;
		PlayerWeapons[ playerid ][ freeindex ][ Ammo ] = ammo;
		PlayerWeapons[ playerid ][ freeindex ][ IsJob ] = job_weapon;
	}
	return GivePlayerWeapon(playerid, weaponid, ammo);
}
#if defined _ALS_GivePlayerWeapon
	#undef GivePlayerWeapon
#else 
	#define _ALS_GivePlayerWeapon
#endif
#define GivePlayerWeapon wep_GivePlayerWeapon



stock wep_ResetPlayerWeapons(playerid, bool:update_db = true)
{
	printf("wep_ResetPlayerWeapons(%s, %d)", GetName(playerid), update_db);
	if(update_db)
	{
		new query[60];
		mysql_format(DbHandle, query, sizeof(query), "DELETE FROM player_weapons WHERE player_id = %d",
			GetPlayerSqlId(playerid));
		mysql_pquery(DbHandle, query);
	}
	for(new i = 0; i < MAX_PLAYER_WEAPONS; i++)
	{
		PlayerWeapons[ playerid ][ i ][ WeaponId ] = 0;
		PlayerWeapons[ playerid ][ i ][ Ammo ] = 0;
		PlayerWeapons[ playerid ][ i ][ IsJob ] = false;
	}
	return ResetPlayerWeapons(playerid);
}
#if defined _ALS_ResetPlayerWeapons
	#undef ResetPlayerWeapons 
#else 
	#define _ALS_ResetPlayerWeapons
#endif
#define ResetPlayerWeapons wep_ResetPlayerWeapons

stock RemovePlayerWeapon(playerid, weaponid)
{
    // Funkcija: RemovePlayerWeapon(playerid, wepid)
    // Panaikins tik vienà þaidëjo ginklà 

    printf("RemovePlayerWeapon(%s, %d) called", GetName(playerid), weaponid);
    new weapons[ 13 ][ 3 ], query[90];

    for(new i = 0; i < 13; i++)
    {
    	GetPlayerWeaponData(playerid, i, weapons[ i ][ 0 ], weapons[ i ][ 1 ]);
    	weapons[ i ][ 2 ] = IsPlayerWeaponJobWeapon(playerid, weapons[ i ][ 0 ]);

    	// Jei ginklui reikalingos kulkos, ir tai netas kurá norim paðalinti, progra patikrinti ar ne cheatintas ginklas
    	if(weapons[ i ][ 0 ] != weaponid && IsWeaponHasAmmo(weapons[ i ][ 0 ]))
    	{
    		printf("RemovePlayerWeapon : Checking for cheated weapon id:%d ammo:%d is job:%d", weapons[ i ][ 0 ], weapons[ i ][ 1 ], weapons[ i ][ 2]);
    		// CheckWeaponCheat reikiant uþblokuos þaidëjà.
    		CheckWeaponCheat(playerid, weapons[ i ][ 0 ], 0);
    	}
    }
    mysql_format(DbHandle, query, sizeof(query), "DELETE FROM player_weapons WHERE weapon_id = %d AND player_id = %d",
    	weaponid, GetPlayerSqlId(playerid));
    mysql_pquery(DbHandle, query);

    ResetPlayerWeapons(playerid, false);

    for(new i = 0; i < 13; i++)	
    	if(weapons[ i ][ 0 ] != weaponid && weapons[ i ][ 0 ] != 0)
    		GivePlayerWeapon(playerid, weapons[ i ][ 0 ], weapons[ i ][ 1 ], false, bool:weapons[ i ][ 2 ]);
}



stock RemovePlayerJobWeapons(playerid)
{
	printf("RemovePlayerJobWeapons(%s) called", GetName(playerid));
	new weapons[ 13 ][ 3 ];

	for(new i = 0; i < 13; i++)
    {
    	GetPlayerWeaponData(playerid, i, weapons[ i ][ 0 ], weapons[ i ][ 1 ]);
    	weapons[ i ][ 2 ] = IsPlayerWeaponJobWeapon(playerid, weapons[ i ][ 0 ]);
    }

    ResetPlayerWeapons(playerid, false);


    for(new i = 0; i < 13; i++)
    	if(weapons[ i ][ 0 ] && !weapons[ i ][ 2 ])
    		GivePlayerWeapon(playerid, weapons[ i ][ 0 ], weapons[ i ][ 1 ], false, false);
	/*
	for(new i = 0; i < MAX_PLAYER_WEAPONS; i++)
		if(PlayerWeapons[ playerid ][ i ][ WeaponId ] && PlayerWeapons[ playerid ][ i ][ IsJob ])
			RemovePlayerWeapon(playerid, PlayerWeapons[ playerid ][ i ][ WeaponId ]);
	*/
	return 1;
}





stock LoadPlayerWeapons(playerid)
{
	new query[80];
	mysql_format(DbHandle, query, sizeof(query), "SELECT weapon_id, ammo FROM player_weapons WHERE player_id = %d", GetPlayerSqlId(playerid));
	mysql_pquery(DbHandle, query, "OnPlayerWeaponLoad", "i", playerid);
}

public OnPlayerWeaponLoad(playerid)
{
	ResetPlayerWeapons(playerid, false);
	for(new i = 0; i < cache_get_row_count(); i++)
	{
		PlayerWeapons[ playerid ][ i ][ WeaponId ] = cache_get_field_content_int(i, "weapon_id");
		PlayerWeapons[ playerid ][ i ][ Ammo ] = cache_get_field_content_int(i, "ammo");
		GivePlayerWeapon(playerid, PlayerWeapons[ playerid ][ i ][ WeaponId ], PlayerWeapons[ playerid ][ i ][ Ammo ], false);
	}
	return 1;
}


stock IsPlayerWeaponJobWeapon(playerid, weaponid)
{
	printf("IsPlayerWeaponJobWeapon(%s, %d) called", GetName(playerid), weaponid);
	for(new i = 0; i < MAX_PLAYER_WEAPONS; i++)
	{
		if(PlayerWeapons[ playerid ][ i ][ WeaponId ] == weaponid)
		{
			printf("IsPlayerWeaponJobWeapon returning true");
			return PlayerWeapons[ playerid ][ i ][ IsJob ];
		}
	}
	printf("IsPlayerWeaponInMemory returning false.");
	return false;
}

stock IsPlayerWeaponInMemory(playerid, weaponid)
{
	printf("IsPlayerWeaponInMemory(%s, %d) called", GetName(playerid), weaponid);
	for(new i = 0; i < MAX_PLAYER_WEAPONS; i++)
	{
		if(PlayerWeapons[ playerid ][ i ][ WeaponId ] == weaponid)
		{
			printf("IsPlayerWeaponInMemory is true");
			return true;
		}
	}
	printf("IsPlayerWeaponInMemory is false");
	return false;
}




hook OnPlayerDisconnect(playerid, reason)
{
	new query[160],
		wepstodelete[64], // Kableliais atskirtas string sàraðas numeriø kurie nebus iðtrinti
		newammo
	;
	for(new i = 0; i < MAX_PLAYER_WEAPONS; i++)
	{
		if(!PlayerWeapons[ playerid ][ i ][ WeaponId ])
			continue;

		if(!PlayerWeapons[ playerid ][ i ][ IsJob ])
		{
			printf("OnPlayerDisconnect : weapons.p i:%d weaponi:%d ammo: %d", i, PlayerWeapons[ playerid ][ i ][ WeaponId ], PlayerWeapons[ playerid ][ i ][ Ammo ]);

			GetPlayerWeaponData(playerid, GetSlotByID(PlayerWeapons[ playerid ][ i ][ WeaponId ]), PlayerWeapons[ playerid ][ i ][ WeaponId ], newammo);

			mysql_format(DbHandle, query, sizeof(query), "UPDATE player_weapons SET ammo = %d WHERE player_id = %d AND weapon_id = %d",
				newammo, GetPlayerSqlId(playerid), PlayerWeapons[ playerid ][ i ][ WeaponId ]);
			mysql_pquery(DbHandle, query);

			format(wepstodelete, sizeof(wepstodelete), "%s%d,", wepstodelete, PlayerWeapons[ playerid ][ i ][ WeaponId ]);
		}
		PlayerWeapons[ playerid ][ i ][ WeaponId ] = 0;
		PlayerWeapons[ playerid ][ i ][ Ammo ] = 0;
		PlayerWeapons[ playerid ][ i ][ IsJob ] = false;
	}
	if(isnull(wepstodelete))
		return 1;
		
	wepstodelete[ strlen(wepstodelete) - 1] = '\0';
	mysql_format(DbHandle, query, sizeof(query), "DELETE FROM player_weapons WHERE player_id = %d AND weapon_id NOT IN (%s)", GetPlayerSqlId(playerid), wepstodelete);
	mysql_pquery(DbHandle, query);
	printf("Weapons delete query:%s", query);
	return 1;
}


public OnPlayerWeaponShot(playerid, weaponid, hittype, hitid, Float:fX, Float:fY, Float:fZ)
{
	for(new i = 0; i < MAX_PLAYER_WEAPONS; i++)
		if(PlayerWeapons[ playerid ][ i ][ WeaponId ] == weaponid)
		{
			PlayerWeapons[ playerid ][ i ][ Ammo ] = GetPlayerAmmo(playerid);
			
			if(!PlayerWeapons[ playerid ][ i ][ Ammo ])
			{
				PlayerWeapons[ playerid ][ i ][ WeaponId ] = 0;
			}
			break;
		}

	if(hittype == BULLET_HIT_TYPE_PLAYER)
	{
		new Float:damage, bool:custom_damage = true;
		switch(weaponid)
		{
			case WEAPON_COLT45: damage = 30.0;
			case WEAPON_SILENCED: damage = 30.0;
			case WEAPON_DEAGLE: damage = 70.0;
			case WEAPON_TEC9: damage = 28.0;
			case WEAPON_UZI: damage = 28.0;
			case WEAPON_MP5: damage = 35.0;
			case WEAPON_SHOTGUN: damage = 50.0;
			case WEAPON_SAWEDOFF: damage = 50.0;
			case WEAPON_SHOTGSPA: damage = 50.0;
			case WEAPON_M4: damage = 35.0;
			case WEAPON_AK47: damage = 35.0;
			case WEAPON_RIFLE: damage = 100.0;
			case WEAPON_SNIPER: damage = 250.0;
			default: custom_damage = false;
		}
		if(custom_damage)
		{
			new Float:health, Float:armour;
			GetPlayerHealth(hitid, health);
			GetPlayerArmour(hitid, armour);
			printf("Custom_damage yes. Player:%s Armor:%f health:%f newdamage:%f", GetName(hitid), armour, health, damage);
			if(armour > 0.0)
			{
				if(armour > damage)
					SetPlayerArmour(hitid, armour-damage);
				else
				{
					SetPlayerArmour(hitid, 0.0);
					damage -= armour;
				}
			}
			if(damage > 0.0)
				SetPlayerHealth(hitid, health-damage);
			return 0;
		}
	}
	#if defined weapons_OnPlayerWeaponShot
		weapons_OnPlayerWeaponShot(playerid, weaponid, hittype, hitid, fX, fY, fZ);
	#endif
	return 1;
}
#if defined _ALS_OnPlayerWeaponShot
	#undef OnPlayerWeaponShot
#else
	#define _ALS_OnPlayerWeaponShot
#endif
#define OnPlayerWeaponShot weapons_OnPlayerWeaponShot
#if defined weapons_OnPlayerWeaponShot
	forward weapons_OnPlayerWeaponShot(playerid, weaponid, hittype, hitid, Float:fX, Float:fY, Float:fZ);
#endif
