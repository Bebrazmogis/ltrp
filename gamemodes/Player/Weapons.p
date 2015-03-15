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

stock wep_ResetPlayerWeapons(playerid, bool:update_db = true)
{
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

    new weapons[ 13 ][ 2 ];

    for(new i = 0; i < 13; i++)
    {
    	GetPlayerWeaponData(playerid, i, weapons[ i ][ 0 ], weapons[ i ][ 1 ]);

    	// Jei ginklui reikalingos kulkos, ir tai netas kurá norim paðalinti, progra patikrinti ar ne cheatintas ginklas
    	if(weapons[ i ][ 0 ] != weaponid && IsWeaponHasAmmo(weapons[ i ][ 0 ]))
    		// CheckWeaponCheat reikiant uþblokuos þaidëjà.
    		CheckWeaponCheat(playerid, weapons[ i ][ 0 ], 0);
    }

    ResetPlayerWeapons(playerid, false);

    for(new i = 0; i < 13; i++)	
    	if(weapons[ i ][ 0 ] != weaponid)
    		GivePlayerWeapon(playerid, weapons[ i ][ 0 ], weapons[ i ][ 1 ], false);
    		/*
    new
        weapons[ 13 ][ 2 ],
        eile[ 128 ],
        eile2[ 128 ],
        weap,
        ammo;

    for ( new i = 0; i < 13; i++ )
    {
        GetPlayerWeaponData(playerid, i, weapons[ i ][ 0 ], weapons[ i ][ 1 ]);
        weap = weapons[ i ][ 0 ],
        ammo = weapons[ i ][ 1 ];
        if(wepid != weap)
        {
            if(weap > 0 && ammo > 0)
            {
                if(IsWeaponHasAmmo(weap))
                    CheckWeaponCheat(playerid, weap, 0);
            }
        }
    }

    ResetPlayerWeapons(playerid);

    for(new i = 0; i < 13; i++)
    {
        if(weapons[ i ][ 0 ] > 0 && weapons[ i ][ 1 ] > 0 && weapons[ i ][ 0 ] != wepid)
        {
            format(eile, sizeof(eile), "%dbone2", weapons[ i ][ 0 ]);
            format(eile2, sizeof(eile2), "%dbone", weapons[ i ][ 0 ]);
            SetPVarInt(playerid, eile2, GetPVarInt (playerid, eile));
            SetPVarInt(playerid, eile, 0);
            GivePlayerWeapon(playerid, weapons[ i ][ 0 ], weapons[ i ][ 1 ]);
        }
    }
    */
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

		printf("OnPlayerDisconnect : weapons.p i:%d weaponi:%d ammo: %d", i, PlayerWeapons[ playerid ][ i ][ WeaponId ], PlayerWeapons[ playerid ][ i ][ Ammo ]);

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

/*
9mm - 30
Silenced 9mm - 30
Desert Eagle - 70
Tec-9 - 28
Micro SMG - 28
SMG - 35
Shotgun - 10
Sawnoff Shotgun - 10
Combat Shotgun - 15
M4 - 35
AK47 - 35
Rifle - 100
Sniper Rifle - 250
*/
hook OnPlayerTakeDamage(playerid, issuerid, Float:amount, weaponid,  bodypart)
{
	new Float:newhealth, bool:custom_damage = true;
	GetPlayerHealth(playerid, newhealth);
	printf("GetPlayerHealth():%f", newhealth);
	newhealth += amount; // Dabar health kiek turëjo prieð ðûvá.
	printf("OnPlayerTakeDamage : Weapons.p newhealth+amount:%f", newhealth);
	if(issuerid != INVALID_PLAYER_ID)
	{
		switch(weaponid)
		{
			case WEAPON_COLT45: newhealth -= 30.0;
			case WEAPON_SILENCED: newhealth -= 30.0;
			case WEAPON_DEAGLE: newhealth -= 70.0;
			case WEAPON_TEC9: newhealth -= 28.0;
			case WEAPON_UZI: newhealth -= 28.0;
			case WEAPON_MP5: newhealth -= 35.0;
			case WEAPON_SHOTGUN: newhealth -= 10.0;
			case WEAPON_SAWEDOFF: newhealth -= 10.0;
			case WEAPON_SHOTGSPA: newhealth -= 15.0;
			case WEAPON_M4: newhealth -= 35.0;
			case WEAPON_AK47: newhealth -= 35.0;
			case WEAPON_RIFLE: newhealth -= 100.0;
			case WEAPON_SNIPER: newhealth -= 250.0;
			default: custom_damage = false;
		}
		if(custom_damage)
		{
			printf("Custom damage yes. Weaponid:%d Real damage done:%f newhealth:%f",weaponid, amount, newhealth);
			SetPlayerHealth(playerid, newhealth);
		}
	}
	return 1;
}