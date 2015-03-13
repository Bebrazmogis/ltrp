





	forward OnPlayerRunOutOfAmmo(playerid, weaponid);

	public OnPlayerUpdate(playerid)
	{
		new weaponid = GetPlayerWeapon(playerid);
		if(GetPVarInt(playerid, "OPROA_Called_Wep") != weaponid)
			DeletePVar(playerid, "OPROA_Called_Wep");
		if(weaponid != 0 && !GetPVarInt(playerid, "OPROA_Called_Wep"))
		{
			if(!GetPlayerAmmo(playerid))
			{
				SetPVarInt(playerid, "OPROA_Called_Wep", weaponid);
				OnPlayerRunOutOfAmmo(playerid, weaponid);
			}
		}
		#if defined ammo_OnPlayerUpdate
			ammo_OnPlayerUpdate(playerid);
		#endif
		return 1;
	}
	#if defined _ALS_OnPlayerUpdate
		#undef OnPlayerUpdate
	#else 
		#define _ALS_OnPlayerUpdate
	#endif
	#define OnPlayerUpdate ammo_OnPlayerUpdate
	#if defined ammo_OnPlayerUpdate
		forward ammo_OnPlayerUpdate(playerid);
	#endif

