/*
CREATE TABLE IF NOT EXISTS fishing_spots (
	id INT NOT NULL AUTO_INCREMENT,
	min_x FLOAT NOT NULL,
	min_y FLOAT NOT NULL,
	max_x FLOAT NOT NULL,
	max_y FLOAT NOT NULL,
	max_fish SMALLINT UNSIGNED NOT NULL DEFAULT '100',
	area_color VARCHAR(16) NOT NULL DEFAULT 'FFFFFFAA',
	replenish_time SMALLINT NOT NULL DEFAULT '180',
	PRIMARY KEY(id)
) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS vehicle_fish (
	vehicle_id INT NOT NULL,
	amount SMALLINT UNSIGNED NOT NULL,
	PRIMARY KEY(vehicle_id)
) ENGINE=INNODB;

ALTER TABLE vehicles ENGINE=INNODB;
ALTER TABLE vehicle_fish ADD FOREIGN KEY(vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE;

*/

#include <YSI\y_hooks>

enum E_FISHING_SPOT_DATA {
	Id,
	MaxFish,
	Color,
	ReplenishTime,
	ReplenishTimer,
	Gangzone,
    AreaId,
	CurrentFish,
    Buoy[4]
};

new FishingSpotData[ MAX_FISHING_SPOTS ][ E_FISHING_SPOT_DATA ],
	bool:IsSeeingFishingSpots[ MAX_PLAYERS ],
	VehicleFish[MAX_VEHICLES];

new Iterator:FishingSpotIterator<MAX_FISHING_SPOTS>;

public OnGameModeInit()
{
	#if defined fishing_OnGameModeInit
		fishing_OnGameModeInit();
	#endif
	new count, Float:pos[4], tmp[32];
	new Cache:result = mysql_query(DbHandle, "SELECT id,min_x, min_y, max_x, max_y, max_fish, CONV(area_color, 16, 10) AS area_color, replenish_time FROM fishing_spots");
	for(new i = 0; i < cache_get_row_count(); i++)
	{
        FishingSpotData[ count ][ Id ] = cache_get_field_content_int(i, "id");
        pos[ 0 ] = cache_get_field_content_float(i, "min_x");
        pos[ 1 ] = cache_get_field_content_float(i, "min_y");
        pos[ 2 ] = cache_get_field_content_float(i, "max_x");
        pos[ 3 ] = cache_get_field_content_float(i, "max_y");
        FishingSpotData[ count ][ MaxFish ] = cache_get_field_content_int(i, "max_fish");
        cache_get_field_content(i, "area_color", tmp);
        FishingSpotData[ count ][ Color ] = strval(tmp);
        FishingSpotData[ count ][ ReplenishTime ] = cache_get_field_content_int(i, "replenish_time");

		FishingSpotData[ count ][ Gangzone ] = GangZoneCreate(pos[0], pos[ 1 ], pos[ 2 ], pos[ 3 ]);
        FishingSpotData[ count ][ AreaId ] = CreateDynamicRectangle(pos[0],pos[1],pos[2],pos[3]);

        FishingSpotData[ count ][ Buoy ][ 0 ] = CreateDynamicObject(1243, pos[0], pos[1], -2.5, 0.0, 0.0, 0.0);
        FishingSpotData[ count ][ Buoy ][ 1 ] = CreateDynamicObject(1243, pos[0], pos[3], -2.5, 0.0, 0.0, 0.0);
        FishingSpotData[ count ][ Buoy ][ 2 ] = CreateDynamicObject(1243, pos[2], pos[1], -2.5, 0.0, 0.0, 0.0);
        FishingSpotData[ count ][ Buoy ][ 3 ] = CreateDynamicObject(1243, pos[2], pos[3], -2.5, 0.0, 0.0, 0.0);

		FishingSpotData[ count ][ CurrentFish ] = FishingSpotData[ count ][ MaxFish ];
		Itter_Add(FishingSpotIterator, count);
		count++;

		if(count == MAX_FISHING_SPOTS)
		{
			printf("Lenteleje 'fishing_spots' eiluciu skaicius virsija(%d) limita(" #MAX_FISHING_SPOTS ") ",count);
			break;
		}
	}
	cache_delete(result);
	printf("Sukurtos %d zvejybos vietos.", count);
}

#if defined _ALS_OnGameModeInit
	#undef OnGameModeInit
#else 
	#define _ALS_OnGameModeInit
#endif
#define OnGameModeInit fishing_OnGameModeInit
#if defined fishing_OnGameModeInit
	forward fishing_OnGameModeInit();
#endif



hook OnPlayerStateChange(playerid, newstate, oldstate)
{
	if((newstate == PLAYER_STATE_DRIVER || newstate == PLAYER_STATE_PASSENGER) && IsABoat(GetVehicleModel(GetPlayerVehicleID(playerid))))
		ShowFishingSpotsForPlayer(playerid);
	else if(newstate == PLAYER_STATE_ONFOOT && IsSeeingFishingSpots[ playerid ])
		HideFishingSpotsForPlayer(playerid);
	return 1;
}


hook OnPlayerEnterCheckpoint(playerid)
{
    switch(Checkpoint[playerid])
    {
    	case CHECKPOINT_FISH:
        {
            DisablePlayerCheckpoint( playerid );
            Checkpoint[ playerid ] = CHECKPOINT_NONE;
            
            new pay = 0,
                string[ 130 ];
            
            if (!IsPlayerOnFishingBoat(playerid))
            {
                pay = GetPlayerItemContentAmount(playerid, ITEM_FISH);
                if ( !IsItemInPlayerInventory( playerid, ITEM_FISH ) )
                    return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: neturite krepðio þuvims.");
                format( string, sizeof(string), "Sveikiname, þuvies supirkimo punktas nupirko Jûsø krepðyje esanèià þuvá ir sumokëjo Jums %d$", pay * 4);
                SendClientMessage( playerid, COLOR_GREEN, string );
                AddPlayerItemContentAmount(playerid, ITEM_FISH, -pay);
                GivePlayerMoney( playerid, ( pay * 4 ) );

            }
            else
            {
            	new vehicleid = GetPlayerVehicleID(playerid);
                pay = VehicleFish[ vehicleid ];
                format( string, sizeof(string), "Sveikiname, þuvies supirkimo punktas nupirko Jûsø krepðyje esanèià þuvá ir sumokëjo Jums %d$", pay );
                SendClientMessage( playerid, COLOR_GREEN, string );
                GivePlayerMoney( playerid, ( pay ) );
                VehicleFish[ vehicleid ] = 0;
                format(string, sizeof(string), "DELETE FROM vehicle_fish WHERE vehicle_id = %d", cInfo[ vehicleid ][ cID ]);
                mysql_pquery(DbHandle, string);
            }
            return 1;
        }
    }
    return 0;
}


public OnPlayerLeaveDynamicArea(playerid, areaid)
{
    // Iðplaukiant þaidëjui ið zonos, iðsaugom þuvis. Just in case.
    foreach(FishingSpotIterator, i)
        if(FishingSpotData[ i ][ AreaId ] == areaid && IsPlayerInAnyVehicle(playerid))
            SaveVehicleFish(GetPlayerVehicleID(playerid));
    #if defined fishing_OnPlayerLeaveDynamicAre
        fishing_OnPlayerLeaveDynamicAre(playerid, areaid);
    #endif
}
#if defined _ALS_OnPlayerLeaveDynamicArea
    #undef OnPlayerLeaveDynamicArea
#else 
    #define _ALS_OnPlayerLeaveDynamicArea
#endif
#define OnPlayerLeaveDynamicArea fishing_OnPlayerLeaveDynamicAre
#if defined fishing_OnPlayerLeaveDynamicAre
    forward fishing_OnPlayerLeaveDynamicAre(playerid, areaid);
#endif

CMD:fishinghelp(playerid)
{
    SendClientMessage( playerid, COLOR_GREEN, "|__________________ÞVEJOJIMO INFORMACIJA__________________|");
    SendClientMessage( playerid, COLOR_WHITE, "  /fish - komanda su kuria þvejojate þuvis." );
    SendClientMessage( playerid, COLOR_FADE1, "  /unloadfish - nustato þemëlapyje kordinates, kur yra þuvies supirkimo punktas." );
    SendClientMessage( playerid, COLOR_GREEN, "__________________________________________________________________" );	
    return 1;
}

CMD:checkfish(playerid)
{
    new vehicleid = GetNearestVehicle(playerid, 10.0);
    if(!IsPlayerOnFishingBoat(playerid) || vehicleid == INVALID_VEHICLE_ID)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neesate valtyje.");

    new string[64];
    format(string, sizeof(string), "Jûsø valtyje yra %d KG þuvies", GetFishInVehicle(vehicleid));
    SendClientMessage(playerid, COLOR_NEWS, string);
    return 1;
}

CMD:unloadfish( playerid)
{
    if(GetPVarInt( playerid, "FISHING") == 1 ) 
    	return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti ðio veiksmo kai Jûsø meðkerë yra uþmesta.." );
    if(IsPlayerAttachedObjectSlotUsed( playerid, 4 ) ) 
    	return SendClientMessage( playerid, COLOR_LIGHTRED, "Pasidëkite meðkeræ prieð baigdami þvejybà." );

    if(!IsPlayerOnFishingBoat(playerid))
    {
        if(!IsItemInPlayerInventory(playerid, ITEM_FISH))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite krepðio þuvims.");
        if(IsPlayerInAnyVehicle(playerid)) 
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, turite iðlipti ið transporto priemonës.");
        SetPlayerCheckPointEx( playerid, CHECKPOINT_FISH, FISH_SHOP_LAND_POS_X, FISH_SHOP_LAND_POS_Y, FISH_SHOP_LAND_POS_Z, 1.0 );
    }
    else
        SetPlayerCheckPointEx( playerid, CHECKPOINT_FISH, FISH_SHOP_WATER_POS_X, FISH_SHOP_WATER_POS_Y, FISH_SHOP_WATER_POS_Z, 3.0 );
    return 1;
}
CMD:fish( playerid)
{
    if(IsPlayerInAnyVehicle(playerid)) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite þvejoti ið transporto priemonës!");

    if(!IsItemInPlayerInventory(playerid, ITEM_ROD)) 
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite pradëti þvejoti neturëdami þvejybai skirtos meðkerës.");

    if(!IsItemInPlayerInventory(playerid, ITEM_RODTOOL)) 
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite þuvies masalo, kad galëtumët pradëti þvejoti. Apsilankykite parduotuvëje.");

    if(!IsItemInPlayerInventory(playerid, ITEM_FISH) && !IsPlayerOnFishingBoat(playerid)) 
    	return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite pradëti þvejoti neturëdami krepðio þuvims dëti.");

    if(!isAtFishPlace( playerid ) && !IsPlayerInAnyFishingSpot(playerid)) 
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, norëdami pradëti þvejoti privalote bûti tam skirtose vietose, pvz: papludimyje prie apþvalgos rato." );
    if(GetPVarInt( playerid, "FISHING") == 1 ) 
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Dëmesio, negalite naudoti ðio veiksmo, kadangi jau uþmetëte meðkeræ" );
    if(!IsPlayerAttachedObjectSlotUsed( playerid, 4 ) ) 
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Dëmesio, prieð pradëdami þvejoti iðlankstykite meðkeræ ((/inv ir spauskite naudoti ant meðkerës))." );
    if(IsPlayerInAnyFishingSpot(playerid) && !FishingSpotData[ GetPlayerFishingSpot(playerid) ][ CurrentFish ])
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðioje vietoje nebëra þuvø...");

    new vehicleid = GetNearestVehicle(playerid, 7.0);
    if(isAtFishPlace(playerid) && GetPlayerItemContentAmount(playerid, ITEM_FISH) >= MAX_FISH_IN_BAG)
    {
        SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmësio, Jûsø þuvies krepðys prisipildë, daugiau negalite naudoti komandos /fish. Dabar veþkite þuvis á supirkimo punktà: /unloadfish");
        SetPlayerCheckPointEx(playerid, CHECKPOINT_FISH, FISH_SHOP_LAND_POS_X, FISH_SHOP_LAND_POS_Y, FISH_SHOP_LAND_POS_Z, 1.0 );
        return 1;

    }
    else if(vehicleid != INVALID_VEHICLE_ID && IsABoat(GetVehicleModel(vehicleid)) && GetFishInVehicle(vehicleid) >= GetVehicleFishCapacity(GetVehicleModel(vehicleid)))
    {
        SetPlayerCheckPointEx( playerid, CHECKPOINT_FISH, FISH_SHOP_WATER_POS_X, FISH_SHOP_WATER_POS_Y, FISH_SHOP_WATER_POS_Z, 3.0 );
        SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmësio, Jûsø laivas prisipildë, daugiau negalite naudoti komandos /fish. Dabar veþkite þuvis á supirkimo punktà: /unloadfish");
        return 1;
    }   


    new string[ 126 ];
    format(string, sizeof(string), "** %s uþsimoja su meðkere ir uþmeta á vandená." ,GetPlayerNameEx( playerid ));
    ProxDetector( 0.1, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );

    new time = 3000 + (random(3) * 1000 );
    SetPVarInt( playerid, "FISHING", 1 );
    SetTimerEx("Zvejyba", time, false, "dd", playerid, vehicleid);
    return 1;
}
forward Zvejyba(playerid, vehicleid);
public Zvejyba(playerid, vehicleid)
{
    SetPVarInt( playerid, "FISHING", 0 );
    new succ = random(5);
    if ( succ == 0 )
    {

        new string[ 126 ];
        format(string, sizeof(string), "** Staiga kaþkas uþkimba ir sujudina meðkeræ, bet meðkerë vël nurimsta. (( %s ))" ,GetPlayerNameEx(playerid));
        ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );

        AddPlayerItemContentAmount(playerid, ITEM_RODTOOL, -1);
        return 1;
    }
    else
    {
        new string[ 126 ],
            svoris,
            zuvys[ ][ 8 ] = {
                {"Silkæ"  },
                {"Karpá"  },
                {"Stintà " },
                {"Upetaká"},
                {"Lydekà" },
                {"Karosà" },
                {"Menkæ"  },
                {"Karðá"  },
                {"Vëgelæ" },
                {"Kuojà "  },
                {"Raudæ"  },
                {"Eðerá"  },
                {"Pûgþlá" }
                };


        AddPlayerItemContentAmount(playerid, ITEM_RODTOOL, -1);

        if( isAtFishPlace( playerid ) )
        {
        	svoris = random( 3 ) + 1;
            AddPlayerItemContentAmount(playerid, ITEM_FISH, svoris);
            if(GetPlayerItemContentAmount(playerid, ITEM_FISH) >= MAX_FISH_IN_BAG)
            {
                SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmësio, Jûsø þuvies krepðys prisipildë, daugiau negalite naudoti komandos /fish. Dabar veþkite þuvis á supirkimo punktà: /unloadfish");
               	SetPlayerCheckPointEx( playerid, CHECKPOINT_FISH, FISH_SHOP_LAND_POS_X, FISH_SHOP_LAND_POS_Y, FISH_SHOP_LAND_POS_Z, 1.0 );
            }
        }
        else if(IsPlayerOnFishingBoat(playerid))
        {
        	new index = GetPlayerFishingSpot(playerid);
            if(vehicleid == INVALID_VEHICLE_ID)
                return 1;
            if(index == -1)
                return 1;
                
        	svoris = random( 13 - 2) + 2;
            VehicleFish[ vehicleid ] += svoris;
            FishingSpotData[ index ][ CurrentFish ] -= svoris;
            if(FishingSpotData[ index ][ CurrentFish ] <= 0)
            {
            	FishingSpotData[ index ][ CurrentFish ] = 0;
            	FishingSpotData[ index ][ ReplenishTimer ] = SetTimerEx("OnFishSpotReplenish", FishingSpotData[ index ][ ReplenishTime ] * 1000, false, "i", index);
            }
            if(VehicleFish[ vehicleid ] >= GetVehicleFishCapacity(GetVehicleModel(vehicleid)))
            {
                SetPlayerCheckPointEx( playerid, CHECKPOINT_FISH, FISH_SHOP_WATER_POS_X, FISH_SHOP_WATER_POS_Y, FISH_SHOP_WATER_POS_Z, 3.0 );
                SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmësio, Jûsø laivas prisipildë, daugiau negalite naudoti komandos /fish. Dabar veþkite þuvis á supirkimo punktà: /unloadfish");
            }
        }   

        format( string, sizeof(string), "* %s sukdamas valà ið vandens iðtraukia uþkibusæ %s, kurios svoris %d kilogramas (-ai)" ,
        	GetPlayerNameEx( playerid ), zuvys[ random( sizeof( zuvys ) ) ], svoris );
        ProxDetector( 0.1, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
        return 1;
    }
}

forward OnFishSpotReplenish(index);
public OnFishSpotReplenish(index)
	FishingSpotData[ index ][ CurrentFish ] = FishingSpotData[ index ][ MaxFish ];



stock IsPlayerInAnyFishingSpot(playerid)
{
	new Float:x, Float:y, Float:z;
	GetPlayerPos(playerid, x, y, z);
	foreach(FishingSpotIterator, i)
		if(IsPlayerInDynamicArea(playerid, FishingSpotData[ i ][ AreaId ]))
			return true;
	return false;
}

stock IsPlayerInFishingSpot(playerid, index)
    return IsPlayerInDynamicArea(playerid, FishingSpotData[ index ][ AreaId ]);

stock GetPlayerFishingSpot(playerid)
{
	foreach(FishingSpotIterator, i)
		if(IsPlayerInFishingSpot(playerid, i))
			return i;
	return -1;
}

stock ShowFishingSpotsForPlayer(playerid)
{
	IsSeeingFishingSpots[ playerid ] = true;
	foreach(FishingSpotIterator, i)
		GangZoneShowForPlayer(playerid, FishingSpotData[ i ][ Gangzone ], FishingSpotData[ i ][ Color ]);
}

stock HideFishingSpotsForPlayer(playerid)
{
	IsSeeingFishingSpots[ playerid ] = false;
	foreach(FishingSpotIterator, i)
		GangZoneHideForPlayer(playerid, FishingSpotData[ i ][ Gangzone ]);
}

stock GetVehicleFishCapacity(model)
{
	switch(model)
	{
		 case 472: return 140;
		 case 473: return 140;
		 case 493: return 140;
		 case 595: return 140;
		 case 484: return 140;
		 case 430: return 140;
		 case 453: return 140;
		 case 452: return 140;
		 case 446: return 140;
		 case 454: return 140;
	}
	return 0;
}

stock GetFishInVehicle(vehicleid)
	return VehicleFish[ vehicleid ];

stock IsPlayerOnFishingBoat(playerid)
{
	new vehicleid = GetNearestVehicle(playerid, 30.0),
        anim = GetPlayerAnimationIndex(playerid),
		model = GetVehicleModel(vehicleid),
	//	Float:width, Float:height, Float:length,
		Float:x, Float:y, Float:z;
	//	Float:px, Float:py, Float:pz;

	if(!IsABoat(model))
		return false;

    // Ávairios plaukimo animacijos, jei þaidëjas plaukia, Jis tikrai nelaive.
    if(anim == 1250 || anim == 1541 || anim == 1539 || anim == 1540 || anim == 1538)
        return false;

    GetVehiclePos(vehicleid, x, y, z);
    if(IsPlayerInRangeOfPoint(playerid, 10.0, x, y, z))
        return true;

    /*
	switch(model)
	{
		case 472: {width = 2.6; length = 9.6; height = 3.0; }
		case 473: {width = 2.2; length = 3.8; height = 3.0; }
		case 493: {width = 4.0; length = 13.0; height = 3.0; }
		case 595: {width = 3.5; length = 11.0; height = 5.0; }
		case 484: {width = 5.5; length = 21.0; height = 5.0; }
		case 430: {width = 3.6; length = 12.0; height = 4.0; }
		case 453: {width = 4.8; length = 16.0; height = 5.0; }
		case 452: {width = 2.7; length = 11.6; height = 3.0; }
		case 446: {width = 4.6; length = 12.6; height = 4.0; }
		case 454: {width = 5.8; length = 15.0; height = 6.0; }
	}
	GetVehiclePos(vehicleid, x, y, z);
	GetPlayerPos(playerid, px, py, pz);

	// Po valtim blogai.
	if(pz < z)
	{
        SendClientMessage(playerid,-1, "Per zemai");
        return false;	
    }

	// Jei kaþkokiu bûdu virð valties atsirado
	if(pz > z + height)
	{
        SendClientMessage(playerid, -1, "Per aukstai");
        return false;
    }

	// O dabar 2D plokstumoj ziurim ar vietjo.
	if(x - width / 2 <= px <= x + width / 2
		&& y - length / 2 <= py <= y + length / 2)
		return true;
    else 
    {
        new string[127];
        format(string, sizeof(string),"X-width/2:%f px:%f x+widht/2:%f",x-width/2, px, x+width/2);
        SendClientMessage(playerid, -1, string);
        format(string,sizeof(string),"y-length/2:%f py:%f y+length/2:%f", y-length/2, py, y+length/2);
        SendClientMessage(playerid, -1, string);
    }
    */
	return false;
}


stock SaveVehicleFish(vehicleid)
{
    // Jei transporto priemonë neturi MySQLID, nëra kà ir saugot.
    // Apskritai, saugom tik nuosavø.
    if(!cInfo[ vehicleid ][ cID ])
        return 0;

	new query[140];
	format(query, sizeof(query),"INSERT INTO vehicle_fish (vehicle_id, amount) VALUES (%d, %d) ON DUPLICATE KEY UPDATE amount = VALUES(amount)",
		cInfo[ vehicleid ][ cID ], GetFishInVehicle(vehicleid));
    return mysql_pquery(DbHandle, query);
}
stock LoadVehicleFish(sqlid, vehicleid)
{
	new query[80];
	format(query, sizeof(query), "SELECT amount FROM vehicle_fish WHERE vehicle_id = %d", sqlid, vehicleid);
	new Cache:result = mysql_query(DbHandle, query);
	if(cache_get_row_count())
        VehicleFish[ vehicleid ] = cache_get_row_int(0,0);
	cache_delete(result);
	return 1;
}