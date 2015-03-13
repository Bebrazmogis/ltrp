#include <YSI\y_hooks>








/*

	CREATE TABLE IF NOT EXISTS player_weapons (
		player_id INT NOT NULL,
		weapon_id TINYINT UNSIGNED NOT NULL,
		ammo SMALLINT UNSIGNED NOT NULL,
		PRIMARY KEY(player_id, weapon_id)
	) ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;
	ALTER TABLE player_weapons ADD FOREIGN KEY(player_id) REFERENCES players(id) ON DELETE CASCADE;	
*/


#define MAX_PLAYER_WEAPONS 				46

enum E_PLAYER_WEAPON_DATA 
{
	WeaponId,
	Ammo
};

static PlayerWeapons[ MAX_PLAYERS ][ MAX_PLAYER_WEAPONS ][ E_PLAYER_WEAPON_DATA ];



forward OnPlayerWeaponLoad(playerid);


stock wep_GivePlayerWeapon(playerid, weaponid, ammo, bool:update_db = true)
{
	if(update_db)
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

		PlayerWeapons[ playerid ][ i ][ Ammo ] += ammo;
		found = true;
		break;
	}
	if(!found)
	{
		PlayerWeapons[ playerid ][ freeindex ][ WeaponId ] = weaponid;
		PlayerWeapons[ playerid ][ freeindex ][ Ammo ] = ammo;
	}
	return GivePlayerWeapon(playerid, weaponid, ammo);
}
#if defined _ALS_GivePlayerWeapon
	#undef GivePlayerWeapon
#else 
	#define _ALS_GivePlayerWeapon
#endif
#define GivePlayerWeapon wep_GivePlayerWeapon


stock LoadPlayerWeapons(playerid)
{
	new query[80];
	mysql_format(DbHandle, query, sizeof(query), "SELECT weapon_id, ammo FROM player_weapons WHERE player_id = %d", GetPlayerSqlId(playerid));
	mysql_pquery(DbHandle, query, "OnPlayerWeaponLoad", "i", playerid);
}

public OnPlayerWeaponLoad(playerid)
{
	ResetPlayerWeapons(playerid);
	for(new i = 0; i < cache_get_row_count(); i++)
	{
		PlayerWeapons[ playerid ][ i ][ WeaponId ] = cache_get_field_content_int(i, "weapon_id");
		PlayerWeapons[ playerid ][ i ][ Ammo ] = cache_get_field_content_int(i, "ammo");
		GivePlayerWeapon(playerid, PlayerWeapons[ playerid ][ i ][ WeaponId ], PlayerWeapons[ playerid ][ i ][ Ammo ], false);
	}
	return 1;
}




stock IsPlayerWeaponInMemory(playerid, weaponid)
{
	for(new i = 0; i < MAX_PLAYER_WEAPONS; i++)
	{
		if(PlayerWeapons[ playerid ][ i ][ WeaponId ] == weaponid)
		{
			return true;
		}
	}
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

		GetPlayerWeaponData(playerid, GetSlotByID(PlayerWeapons[ playerid ][ i ][ WeaponId ]), PlayerWeapons[ playerid ][ i ][ WeaponId ], newammo);
		mysql_format(DbHandle, query, sizeof(query), "UPDATE player_weapons SET ammo = %d WHERE player_id = %d AND weapon_id = %d",
			newammo, GetPlayerSqlId(playerid), PlayerWeapons[ playerid ][ i ][ WeaponId ]);
		mysql_pquery(DbHandle, query);

		format(wepstodelete, sizeof(wepstodelete), "%s%d,", wepstodelete, PlayerWeapons[ playerid ][ i ][ WeaponId ]);

		PlayerWeapons[ playerid ][ i ][ WeaponId ] = 0;
		PlayerWeapons[ playerid ][ i ][ Ammo ] = 0;
	}
	if(isnull(wepstodelete))
		return 1;
		
	wepstodelete[ strlen(wepstodelete) - 1] = '\0';
	mysql_format(DbHandle, query, sizeof(query), "DELETE FROM player_weapons WHERE player_id = %d AND weapon_id NOT IN (%s)", GetPlayerSqlId(playerid), wepstodelete);
	mysql_pquery(DbHandle, query);
	printf("Weapons delete query:%s", query);
	return 1;
}


hook OnPlayerWeaponShot(playerid, weaponid, hittype, hitid, Float:fX, Float:fY, Float:fZ)
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
	return 1;
}