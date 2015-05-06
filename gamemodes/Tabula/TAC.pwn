/*--------------------------------------
*
*         Tabula antICheat
*		       TAC
*
---------------------------------------*/
#if defined _TAC_included
	#endinput
#endif
#define _TAC_included

#if !defined _samp_included
	#error "Issaugojai TAC faila, dabar gali kompiliuoti tr.pwn faila."
#endif

stock Tabu_AddStaticVehicle(model, Float:Spawn_x, Float:Spawn_y, Float:Spawn_z, Float:Spawn_a, color1, color2 )
{
	new vehicle = AddStaticVehicle( model, Spawn_x, Spawn_y, Spawn_z, Spawn_a, color1, color2 );
	VehicleRadio[ vehicle ] = 99;
	V_HP        [ vehicle ] = 1000.0;
	Itter_Add(Vehicles,vehicle);
	return vehicle;
}
stock Tabu_CreateVehicle( model, Float:Spawn_x, Float:Spawn_y, Float:Spawn_z, Float:Spawn_a, color1, color2, respawn )
{
	new vehicle = CreateVehicle( model, Spawn_x, Spawn_y, Spawn_z, Spawn_a, color1, color2, respawn );
	VehicleRadio[ vehicle ] = 99;
	V_HP        [ vehicle ] = 1000.0;
	Itter_Add(Vehicles,vehicle);
	return vehicle;
}

stock Tabu_AddStaticVehicleEx(model, Float:spawn_x, Float:spawn_y, Float:spawn_z, Float:spawn_a, color1, color2, respawn_delay)
{
	new vehicleid = AddStaticVehicleEx(model, spawn_x, spawn_y, spawn_z, spawn_a, color1, color2, respawn_delay);
	VehicleRadio[ vehicleid ] = 99;
	V_HP[ vehicleid ] = 1000.0;
	Itter_Add(Vehicles, vehicleid);
	return vehicleid;
}

stock Tabu_DestroyVehicle( vehicleid )
{
	DestroyVehicle( vehicleid );
	return Itter_Remove(Vehicles,vehicleid);
}
#if defined _ALS_DestroyVehicle 
	#undef DestroyVehicle
#else 
	#define _ALS_DestroyVehicle
#endif
#define DestroyVehicle Tabu_DestroyVehicle

stock Tabu_SetVehicleHealth( vehicleid, Float:Health )
{
	V_HP[ vehicleid ] = Health;
	return SetVehicleHealth( vehicleid, Health );
}
#define SetVehicleHealth Tabu_SetVehicleHealth
stock Tabu_SetVehicleToRespawn( vehicleid )
{
    V_HP[ vehicleid ] = 1000.0;
    return SetVehicleToRespawn( vehicleid );
}
#define SetVehicleToRespawn Tabu_SetVehicleToRespawn
stock Tabu_GiveMomey( playerid, ammount )
{
	PlayerMoney[ playerid ] += ammount;
 	return GivePlayerMoney( playerid, ammount );
}
stock Tabu_ResetMoney( playerid ) 
{
	PlayerMoney[ playerid ] = 0;
 	return ResetPlayerMoney( playerid );
}
stock Tabu_GetPlayerMoney(playerid)
	return PlayerMoney[ playerid ];
stock Tabu_SetPlayerVirtualWorld( playerid, virt )
{
	pInfo[ playerid ][ pVirWorld ] = virt;
	return SetPlayerVirtualWorld( playerid, virt );
}
#define SetPlayerVirtualWorld Tabu_SetPlayerVirtualWorld
stock Tabu_SetPlayerWeather(playerid, weather)
{
	SetPVarInt( playerid, "Weather", weather );
	return SetPlayerWeather(playerid, weather);
}
#define SetPlayerWeather Tabu_SetPlayerWeather


/*
Tabu_SetPlayerAttachedObject(playerid, index, modelid, bone, Float:fOffsetX = 0.0, Float:fOffsetY = 0.0, Float:fOffsetZ = 0.0, Float:fRotX = 0.0, Float:fRotY = 0.0, Float:fRotZ = 0.0, Float:fScaleX = 1.0, Float:fScaleY = 1.0, Float:fScaleZ = 1.0, materialcolor1 = 0, materialcolor2 = 0)
{
	new
		string[ 128 ],
		Float:pos[ 3 ],
		Float:rot[ 3 ],
		Float:scale[ 3 ],
		time;

  	format(string, 128, "%dTime", modelid );
 	time = GetPVarInt ( playerid, string );
  	format(string, 128, "%dPosX", modelid );
 	pos[ 0 ] = GetPVarFloat ( playerid, string );
    format(string, 128, "%dPosY", modelid );
	pos[ 1 ] = GetPVarFloat ( playerid, string );
	format(string, 128, "%dPosZ", modelid );
	pos[ 2 ] = GetPVarFloat ( playerid, string );

	format(string, 128, "%dRotX", modelid );
	rot[ 0 ] = GetPVarFloat ( playerid, string );
    format(string, 128, "%dRotY", modelid );
	rot[ 1 ] = GetPVarFloat ( playerid, string );
	format(string, 128, "%dRotZ", modelid );
	rot[ 2 ] = GetPVarFloat ( playerid, string );
	
	format(string, 128, "%dScaleX", modelid );
	scale[ 0 ] = GetPVarFloat ( playerid, string );
    format(string, 128, "%dScaleY", modelid );
	scale[ 1 ] = GetPVarFloat ( playerid, string );
	format(string, 128, "%dScaleZ", modelid );
	scale[ 2 ] = GetPVarFloat ( playerid, string );
	
    //printf("[DEBUG] Objektas: %d, X: %f, Y: %f, Z: %f, RX: %f, RY: %f, RZ: %f, Scale X: %f, Scale Y: %f, Scale Z: %f", modelid, pos[ 0 ], pos[ 1 ], pos[ 2 ], rot[ 0 ], rot[ 1 ], rot[ 2 ], scale[ 0 ], scale[ 1 ], scale[ 2 ] );
	
	if( time > 0 )
		return SetPlayerAttachedObject(playerid, index, modelid, bone, pos[ 0 ], pos[ 1 ],pos[ 2 ], rot[ 0 ], rot[ 1 ], rot[ 2 ], scale[ 0 ], scale[ 1 ], scale[ 2 ], materialcolor1, materialcolor2);
		
    format(string, 128, "%dTime", modelid );
   	SetPVarInt ( playerid, string, 1 );
	return SetPlayerAttachedObject(playerid, index, modelid, bone, fOffsetX, fOffsetY,fOffsetZ, fRotX, fRotY, fRotZ, fScaleX, fScaleY, fScaleZ, materialcolor1, materialcolor2);
}
*/
stock Tabu_SetPlayerSpecialAction( playerid, actionid )
{
	if( actionid == SPECIAL_ACTION_USECELLPHONE )
	{
	    SetPVarInt( playerid, "NearPhone", true );
	    if(IsPlayerAttachedObjectSlotUsed(playerid, 3))
	    {
	        RemovePlayerAttachedObject(playerid, 3);
	    	SetPlayerAttachedObject(playerid, 3, 330, 5, -0.405256, -0.000577, 0.107980, 38.940883, 339.253723, 264.400329);
	    }
	    else
	        SetPlayerAttachedObject(playerid, 3, 330, 6);
	}
	else if( actionid == SPECIAL_ACTION_STOPUSECELLPHONE )
	{
	    SetPVarInt( playerid, "NearPhone", false );
	    if(IsPlayerAttachedObjectSlotUsed(playerid, 3))
	        RemovePlayerAttachedObject(playerid, 3);
	}
	else if( actionid == SPECIAL_ACTION_CARRY )
	{
	    if(IsPlayerAttachedObjectSlotUsed(playerid, 7))
	    {
	        RemovePlayerAttachedObject(playerid, 7);
	        if(GetPVarInt(playerid, "Tipas2") == 3)
				SetPlayerAttachedObject(playerid,7 ,1265,1, 0.100000, 0.553958, -0.024002, 356.860290, 269.945068, 0.000000, 0.834606, 1.000000, 0.889027 );
	        else if( GetPVarInt(playerid, "Tipas2") == 1)
	    		SetPlayerAttachedObject(playerid, 7, 1264, 1, 0.064699, 0.426247, 0.000000, 259.531341, 80.949592, 0.000000, 0.776124, 0.768181, 0.770769);
			else
				SetPlayerAttachedObject(playerid, 7, 2912, 1, 0.064699, 0.426247, 0.000000, 259.531341, 80.949592, 0.000000, 0.776124, 0.768181, 0.770769);
	    }
	    else
	    {
	    	if(GetPVarInt(playerid, "Tipas2") == 3)
				SetPlayerAttachedObject(playerid,7 ,1265,1, 0.100000, 0.553958, -0.024002, 356.860290, 269.945068, 0.000000, 0.834606, 1.000000, 0.889027 );
	        else if( GetPVarInt(playerid, "Tipas2") )
	    		SetPlayerAttachedObject(playerid, 7, 1264, 1, 0.064699, 0.426247, 0.000000, 259.531341, 80.949592, 0.000000, 0.776124, 0.768181, 0.770769);
			else
				SetPlayerAttachedObject(playerid, 7, 2912, 1, 0.064699, 0.426247, 0.000000, 259.531341, 80.949592, 0.000000, 0.776124, 0.768181, 0.770769);
	    }
	}
	else if( actionid == SPECIAL_ACTION_NONE )
	{
	    if(IsPlayerAttachedObjectSlotUsed(playerid, 7))
	        RemovePlayerAttachedObject(playerid, 7);
	}
	return SetPlayerSpecialAction( playerid, actionid );
}
#define SetPlayerSpecialAction Tabu_SetPlayerSpecialAction
//#define SetPlayerAttachedObject Tabu_SetPlayerAttachedObject



stock Tabu_SetPlayerInt( playerid, intas )
{
	pInfo[ playerid ][ pInt ] = intas;
	return SetPlayerInterior( playerid, intas );
}
#define SetPlayerInterior Tabu_SetPlayerInt


stock ac_GetPlayerSpeed(playerid,get3d)
{
	new Float:x,Float:y,Float:z;
	if(IsPlayerInAnyVehicle(playerid))
	    GetVehicleVelocity(GetPlayerVehicleID(playerid),x,y,z);
	else
	    GetPlayerVelocity(playerid,x,y,z);

	return AC_SPEED(x,y,z,100.0,get3d);
}

stock IsPlayerWeaponInDB(playerid, weaponid)
{
	new query[70], bool:registered = false, Cache:result;
	format(query, sizeof(query), "SELECT * FROM AC WHERE WeaponID = %d AND ID = %d", weaponid, pInfo[ playerid ][ pMySQLID ] );
	result = mysql_query(DbHandle, query);
	if(cache_get_row_count())
		registered = true;
	cache_delete(result);
	return registered;
}

stock CheckWeaponCheat(playerid, weaponid, type)
{
	new
		eile[ 256 ],
		rows,
		wepname[ 24 ],
		Cache:result;

	if(type == 0)
	{
		printf("CheckWeaponCheat(%s, %d ,%d) type 0 called.", GetName(playerid), weaponid, type);
		if(IsPlayerInAnyVehicle(playerid))
		    return false;

		if(IsPlayerWeaponInMemory(playerid, weaponid))
		 	return false;

		printf("Player weapon was hacked. banning.");
		GetWeaponName(weaponid, wepname, sizeof(wepname));
		TogglePlayerControllable(playerid, 0);

		ResetPlayerWeapons(playerid);
		ClearWeaponsFromPlayerInventory(playerid);

		format(eile, sizeof(eile), "Neleistinai gautas ginklas (%s)", wepname);
		BanPlayer("AC", playerid, eile);
		return true;

		 /*
	    format( eile, sizeof( eile ), "SELECT * FROM `AC` WHERE `WeaponID` = %d AND `ID` = %d LIMIT 0,1", weaponid, pInfo[ playerid ][ pMySQLID ] );
	 	result = mysql_query(DbHandle, eile );
	    rows = cache_get_row_count();
	    cache_delete(result);
		if( !rows )
		{
			switch( GetVehicleModel( OldCar	[ playerid ] ) )
			{
				case 592,577,511,512,520,593,553,476,519,460,513,548,425,417,487,488,497,563,447,469:
				{
			 		RemovePlayerWeapon( playerid, 46 );
			 		return false;
				}
				case 457:
				{
			 		RemovePlayerWeapon( playerid, 2 );
			 		return false;
				}
				case 596,597,598,599:
				{
			 		RemovePlayerWeapon( playerid, 25 );
			 		return false;
				}
			}
			
			GetWeaponName( weaponid, wepname, sizeof(wepname) );
			TogglePlayerControllable(playerid, 0);

			ResetPlayerWeapons( playerid );
			ClearWeaponsFromInv( playerid );

			format   ( eile, sizeof(eile), "Neleistinai gautas ginklas (%s)", wepname);
			BanPlayer( "AC", playerid, eile );
			return true;
		}
		*/
	}
	else
	{
	    for ( new gun = 0; gun < MAX_SAVED_WEAPONS; gun++ )
		{
		    if( pInfo[ playerid ][ pGun ][ gun ] > 0 )
		    {
			    format( eile, sizeof( eile ), "SELECT * FROM `AC` WHERE `WeaponID` = %d AND `ID` = %d LIMIT 0,1", pInfo[ playerid ][ pGun ][ gun ], pInfo[ playerid ][ pMySQLID ] );
			 	result = mysql_query(DbHandle, eile );
			    rows = cache_get_row_count();

	   			if( !rows )
				{
			        pInfo[ playerid ][ pGun ][ gun ] = 0;
			        pInfo[ playerid ][ pAmmo ][ gun ] = 0;
				}
				cache_delete(result);
			}
	    }
	}
	return false;
}

stock BanPlayer(kas[],playerid,kodel[])
{
    new string[216],
	    name[MAX_PLAYER_NAME],
	    ip[16];
	    
	GetPlayerName(playerid,name,MAX_PLAYER_NAME);
	
    format(string, 216, "AdmCmd %s uþdraudë þaisti þaidëjui %s, prieþastis: %s", kas, name, kodel);
    SendClientMessageToAll(COLOR_LIGHTRED, string);
    GetPlayerIp(playerid,ip,16);
    
 	foreach(Player,i)
	{
	    if( !strcmp(kas,GetName(i),true) )
	    {
		   	format(string,216, "INSERT INTO `nuobaudos` (Kas, Ka, Kam, Priezastis) VALUES('%d', 'uþdraudë þaisti', '%d', '%s')", pInfo[ i ][ pMySQLID ], pInfo[ playerid ][ pMySQLID ], kodel);
			mysql_pquery(DbHandle, string);
			break;
		}
	}
    
    format(string, 216, "INSERT INTO `bans` (ip,name,admin,reason,Banned) VALUES ('%s','%s','%s','%s',1)", ip,name,kas,kodel);
    mysql_pquery(DbHandle, string);
	SetTimerEx("KicknPlayer", 100, false, "d", playerid );
    return 1;
}
stock CheckIfAFKing( playerid )
{
 	static
	    Float:pX[ MAX_PLAYERS ],
	    Float:pY[ MAX_PLAYERS ],
	    Float:pZ[ MAX_PLAYERS ];

	new Float:NewpX,
	    Float:NewpY,
	    Float:NewpZ;

	GetPlayerPos( playerid, NewpX, NewpY, NewpZ );

	if ( pX[ playerid ] == NewpX && pY[ playerid ] == NewpY && pZ[ playerid ] == NewpZ )
	{
        AfkCheck[ playerid ]++;
        
	    if ( AfkCheck[ playerid ] > 10 )
	        KickPlayer( "AC", playerid, "AFK" );
	}
	else
	    AfkCheck[ playerid ] = 0;

	pX[ playerid ] = NewpX;
	pY[ playerid ] = NewpY;
	pZ[ playerid ] = NewpZ;
}

stock KickPlayer(kas[],playerid,kodel[])
{
    new string[ 256 ],
	    name[ MAX_PLAYER_NAME ];

	GetPlayerName( playerid, name, MAX_PLAYER_NAME );

 	foreach(Player,i)
	{
	    if( !strcmp(kas,GetName(i),true) )
	    {
		   	format(string,sizeof(string), "INSERT INTO `nuobaudos` (Kas, Ka, Kam, Priezastis) VALUES('%d', 'iðmetë ið serverio', '%d', '%s')", pInfo[ i ][ pMySQLID ], pInfo[ playerid ][ pMySQLID ], kodel);
			mysql_pquery(DbHandle,string);
			break;
		}
	}

    format( string, sizeof(string), "AdmCmd %s iðspyrë þaidëjà  %s ið serverio, prieþastis: %s", kas, name, kodel);
    SendClientMessageToAll( COLOR_LIGHTRED, string);
	SetTimerEx("KicknPlayer", 100, false, "d", playerid );
    return 1;
}
stock CheckLock( playerid )
{
    new
		query[ 256 ],
		reason[ 256 ],
	  	laikas[ 54 ],
	  	bool:isLocked = false,
	  	Cache:result;

	format(query, sizeof(query), "SELECT reason,laikas FROM `acclock` WHERE `id` = %d ORDER BY `laikas` DESC", pInfo[ playerid ][ pMySQLID ]);
	result = mysql_query(DbHandle, query);
    if(cache_get_row_count())
    {

		SendClientMessage(playerid,COLOR_LIGHTRED,"Dëmesio, Jûsø veikëjo sàskaita yra uþrakinta ir jo negalite naudoti");
		SendClientMessage(playerid,COLOR_LIGHTRED2,"Daugiau informacijos dël draudimo lankytis paðalinimo galite rasti forum.ltrp.lt");

	    cache_get_field_content(0, "reason", reason);
	    cache_get_field_content(0, "laikas", laikas);

		format(query, sizeof(query), "Sàskaitos uþrakinimo prieþastis: %s ", reason);
	    SendClientMessage( playerid, COLOR_FADE1, query );

	    format(query, sizeof(query), "Laikas kada buvo uþrakinta sàskaita: %s ", laikas);
	    SendClientMessage(playerid, COLOR_WHITE, query);
		SetTimerEx("KicknPlayer", 100, false, "d", playerid);
		isLocked = true;
	}
	cache_delete(result);
	return isLocked;
}
stock CheckBan(playerid)
{
    new string[ 256 ],
	    ip[ 16 ],
		realname[ 24 ],
		bool:isBanned = false,
		Cache:result;

	GetPlayerName(playerid, realname, sizeof(realname));
	GetPlayerIp(playerid, ip, sizeof(ip));
	format(string, sizeof(string), "SELECT admin,reason,Banned,name FROM `bans` WHERE `ip` = '%s' OR name = '%s' LIMIT 1", ip, realname);
	result = mysql_query(DbHandle, string);
	if(cache_get_row_count())
	{

  		new Admin[24],
    		Reason[256],
			banned,
			name[24];

		cache_get_field_content(0, "admin", Admin);
		cache_get_field_content(0, "reason", Reason);
		banned = cache_get_field_content_int(0, "Banned");
		cache_get_field_content(0, "name", name);
  		if(banned)
  		{
    		SendClientMessage(playerid,COLOR_LIGHTRED,"Dëmesio, Jûsø veikëjui ir IP adresui yra uþdrausta jungtis á ðá serverá");
			SendClientMessage(playerid,COLOR_LIGHTRED2,"Daugiau informacijos dël draudimo lankytis paðalinimo galite rasti forum.ltrp.lt");

	  		format(string, sizeof(string),"Uþdraustas veikëjas: %s",realname);
	     	SendClientMessage(playerid,COLOR_FADE1,string);

	    	format(string, sizeof(string),"Nurodyta prieþastis: %s",Reason);
	     	SendClientMessage(playerid,COLOR_FADE1,string);

	     	format(string, sizeof(string),"Blokuojamas IP adresas: %s | Blokuojami veikëjai: %s",ip, name);
	     	SendClientMessage(playerid,COLOR_FADE1,string);

	      	format(string, sizeof(string),"Draudimà lankytis suteikë: %s",Admin);
	       	SendClientMessage(playerid,COLOR_FADE1,string);

			SetTimerEx("KicknPlayer", 100, false, "d", playerid );
			isBanned = true;
		}	
	}
	cache_delete(result);
	return isBanned;
	/*
    if ( mysql_num_rows( ) == 0 )
    {
        mysql_free_result( );

        format( string, 100, "SELECT admin,reason,Banned,name FROM `bans` WHERE `name` = '%s' LIMIT 1", realname );
		mysql_query(DbHandle,  string );
		mysql_store_result( );
		if ( mysql_num_rows( ) != 0 )
		{
 			mysql_fetch_row(string);
			new Admin[24],
    			Reason[256],
				True,
				name[24];
			sscanf( string, "p<|>s[24]s[256]ds[24]" ,Admin, Reason, True, name );
		    BanPlayer( "AC", playerid, "Ban Evade" );
            mysql_free_result( );
		    return true;
		}
		else
		{
            mysql_free_result( );
		    return false;
		}
	}
 	else
  	{
		mysql_fetch_row(string);
  		new Admin[24],
    		Reason[256],
			True,
			name[24];
		sscanf( string, "p<|>s[24]s[256]ds[24]" ,Admin, Reason, True, name );
  		if(True == 0)
  		{
    		mysql_free_result();
      		return false;
		}
  		SendClientMessage(playerid,COLOR_LIGHTRED,"JUMS DRAUDÞIAMA JUNGTIS Á SERVERÁ");
  		format(string, 74,"Vardas: %s",realname);
     	SendClientMessage(playerid,COLOR_FADE1,string);
    	format(string, 74,"PrieÃ¾astis: %s",Reason);
     	SendClientMessage(playerid,COLOR_FADE1,string);
     	format(string, 74,"Blokuojamas IP: %s    Blokuojamas Vardas: %s",ip, name);
     	SendClientMessage(playerid,COLOR_FADE1,string);
      	format(string, 74,"Administratorius: %s",Admin);
       	SendClientMessage(playerid,COLOR_FADE1,string);
       	mysql_free_result();
		SetTimerEx("KicknPlayer", 100, false, "d", playerid );
		return true;
  	}
  	*/
}
stock LoadPlayerVehicles( playerid )
{
	new string[ 126 ], Cache:result;
	format( string, 126, "SELECT `id` FROM `vehicles` WHERE `cOwner` = %d", pInfo[ playerid ][ pMySQLID ] );
	result = mysql_query(DbHandle,  string);
   	new slot = 1;
    for(new i = 0; i < cache_get_row_count(); i++)
	{
    	pInfo[playerid][pCar][slot] = cache_get_row_int(i, 0);
    	slot ++;
	}
	while( slot < 20 )
	{
	    pInfo[ playerid ][ pCar ][ slot ] = 0;
	    slot ++;
	}
	cache_delete(result);
	return 1;
}

stock GetWeaponSlotByID(weaponid)
	return GetSlotByID(weaponid);

stock GetSlotByID(weaponid)
{
	switch (weaponid)
	{
	    case 0, 1      : return 0;
	    case 2..9      : return 1;
		case 22..24    : return 2;
		case 25..27    : return 3;
		case 28, 29, 32: return 4;
		case 30, 31    : return 5;
		case 33, 34    : return 6;
		case 35..38    : return 7;
		case 16..19, 39: return 8;
		case 41..43    : return 9;
	    case 10..15    : return 10;
	    case 44..46    : return 11;
	    case 40        : return 12;
	}
	return -1;
}
stock Convert(playerid, file[])
{
	if (!fexist(file))
	{
	    printf("Zemelapis %s Negali buti uzkratas", file);
		return 0;
	}
	new File:MapFile=fopen(file),
	n, string[1024],
	Float:x, Float:y, Float:z, modelid, radius,
	t = GetTickCount();
	
	while(fread(MapFile, string))
	{
		if(!sscanf(string, "p<\">'removeWorldObject''radius='f'model='d'posX='f'posY='f'posZ='f", radius, modelid, x, y, z))
		{
			RemoveBuildingForPlayer(playerid, modelid, x, y, z, radius);
			n++;
		}
	}
	fclose(MapFile);
	printf("%d uzkrauta objektu istrinimu is failo '%s' per %dms", n, file, GetTickCount()-t);
	return n;
}

stock LoadMap(file[]) // by Mick88
{
	if (!fexist(file))
	{
	    printf("Zemelapis %s Negali buti uzkratas", file);
		return 0;
	}
	new File:MapFile=fopen(file),
	n, string[1024],
	Float:x, Float:y, Float:z, Float:rx, Float:ry, Float:rz,
	modelid, interior, world,
	t = GetTickCount();

	while(fread(MapFile, string))
	{
	    if (!sscanf(string, "p<\">'object''model='d'interior='d'dimension='d'posX='f'posY='f'posZ='f'rotX='f'rotY='f'rotZ='f", modelid, interior, world, x, y, z, rx, ry, rz))
		{
	        //modelid x y z rx ry rz interior world
	        new objectid = CreateDynamicObject(modelid, x, y, z, rx, ry, rz);
	        if (objectid != INVALID_OBJECT_ID)
	        {
	            n++;
	            Streamer_SetFloatData(STREAMER_TYPE_OBJECT, objectid, E_STREAMER_DRAW_DISTANCE, 400.0);
			}
	    }
	}
	fclose(MapFile);
	printf("%d uzkrauta objektu is failo '%s' per %dms", n, file, GetTickCount()-t);
	return n;
}
/*
#if defined _ALS_GivePlayerWeapon
	#undef GivePlayerWeapon
#else 
	#define _ALS_GivePlayerWeapon
#endif
#define GivePlayerWeapon   Tabu_GivePlayerWeapon
*/

#define GivePlayerMoney    Tabu_GiveMomey
#define ResetPlayerMoney   Tabu_ResetMoney
//#define ResetPlayerWeapons Tabu_ResetPlayerWeapons
#define CreateVehicle      Tabu_CreateVehicle
#define AddStaticVehicle 	Tabu_AddStaticVehicle
#define AddStaticVehicleEx 				Tabu_AddStaticVehicleEx
#define DestroyVehicle     Tabu_DestroyVehicle



stock TabCanPlayerEnterFactionVehicle(playerid, vehicleid)
{
	// Jei ji darbinë.
	if(sVehicles[ vehicleid ][ Id ])
	{
		if(sVehicles[ vehicleid ][ Job ] > 0 && pInfo[ playerid ][ pJob ] != sVehicles[ vehicleid ][ Job ])
			return false;
		if(sVehicles[ vehicleid ][ Faction ] > 0 && fInfo[ PlayerFaction(playerid) ][ fID ] != sVehicles[ vehicleid ][ Faction ])
			return false; 
	}

	return true;
}

stock ACTestLog(const string[])
{
	new year,month, day, hour, minute, second, tmp[64];
	getdate(year, month, day);
	gettime(hour, minute ,second);

	new File: file = fopen("AcTest.ini", io_append);
	if(!file)
		return 0;

	format(tmp, sizeof(tmp),"[%d.%2d.%2d %2d:%2d:%2d]", year, month, day, hour, minute ,second);
	fwrite(file, tmp);
	fwrite(file, string);
	fwrite(file, "\r\n");
	fclose(file);
	return 1;
}