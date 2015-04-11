/*
 * 	Failas Entrances.p yra dalis LTRP modifikacijos
 *	
*/
#include <YSI\y_hooks>

/*

		CREATE TABLE IF NOT EXISTS entrances_vehicle (
			id INT AUTO_INCREMENT NOT NULL,
			entrance_x FLOAT NOT NULL,
			entrance_y FLOAT NOT NULL,
			entrance_z FLOAT NOT NULL,
			entrance_angle FLOAT NOT NULL,
			exit_x FLOAT NOT NULL,
			exit_y FLOAT NOT NULL,
			exit_z FLOAT NOT NULL,
			exit_angle FLOAT NOT NULL,
			PRIMARY KEY(id)
		) ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;

		CREATE TABLE IF NOT EXISTS entrances (
			id INT AUTO_INCREMENT NOT NULL,
			`entrance_text` VARCHAR(128) NOT NULL,
			exit_text VARCHAR(128) NOT NULL,
			entrance_text_colour INT NOT NULL,
			entrance_x FLOAT NOT NULL,
			entrance_y FLOAT NOT NULL,
			entrance_z FLOAT NOT NULL,
			entrance_virtual_world INT NOT NULL,
			entrance_interior INT NOT NULL,
			entrance_pickup_model SMALLINT UNSIGNED NOT NULL DEFAULT '0',
			entrance_label BOOLEAN NOT NULL DEFAULT '0',
			exit_text_colour INT NOT NULL,
			exit_x FLOAT NOT NULL,
			exit_y FLOAT NOT NULL,
			exit_z FLOAT NOT NULL,
			exit_virtual_world INT NOT NULL,
			exit_interior INT NOT NULL,
			exit_pickup_model SMALLINT UNSIGNED NOT NULL DEFAULT '0',
			exit_label BOOLEAN NOT NULL DEFAULT '0',
			vehicle_entrance INT NULL,
			faction_entrance INT NULL,
			PRIMARY KEY(id),
			INDEX(vehicle_entrance),
			INDEX(faction_entrance)
		) ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;
	
		ALTER TABLE entrances ADD FOREIGN KEY(vehicle_entrance) REFERENCES entrances_vehicle(id) ON DELETE SET NULL;
		ALTER TABLE entrances ADD FOREIGN KEY(faction_entrance) REFERENCES factions(id) ON DELETE SET NULL;


		ALTER TABLE entrances ADD COLUMN entrance_text_colour INT NOT NULL AFTER exit_text,
			ADD COLUMN exit_text_colour INT NOT NULL AFTER entrance_label;
		
*/	
#define DEFAULT_ENTRANCE_PICKUP_MODEL     1239

#define MAX_ENTRANCES 					100
#define MAX_ENTRANCE_VEHICLES 			MAX_ENTRANCES / 2
#define MAX_ENTRANCE_TEXT				128
#define ENTRANCE_LABEL_COLOUR			0x1299A2AA
#define DIALOG_ENTRANCE_MENU_TYPE_SELEC 1500
#define DIALOG_ENTRANCE_MENU_DELETE		1501
#define DIALOG_ENTRANCE_MENU_INFO		1502
#define DIALOG_ENTRANCE_MENU_SETTINGS	1503
#define DIALOG_ENTRANCE_MENU_TEXT		1504
#define DIALOG_ENTRANCE_MENU_ALPHA 		1505
#define DIALOG_ENTRANCE_MENU_TEXT_COLOR 1506
#define DIALOG_ENTRANCE_MENU_COLOUR_CUS 1507

#define EntranceManagementDialog. 		fa_

#define DIALOG_ENTRANCE_MENU_MAIN 		53
#define DIALOG_ENTRANCE_MENU_INPUT_INDE 5463

#define ENTRANCE_TYPE_ENTRANCE 			1
#define ENTRANCE_TYPE_EXIT 				2


enum E_ENTRANCE_VEHICLE_DATA 
{
	SqlId,
	Float:EnX,
	Float:EnY,
	Float:EnZ,
	Float:EnA,
	Float:ExX,
	Float:ExY,
	Float:ExZ,
	Float:ExA,
};

enum E_ENTRANCE_DATA
{
	SqlId,
	EnText[ MAX_ENTRANCE_TEXT ],
	EnTextColour,
	Float:EnX,
	Float:EnY,
	Float:EnZ,
	EnVW,
	EnInt,
	ExText[ MAX_ENTRANCE_TEXT ],
	ExTextColour,
	Float:ExX,
	Float:ExY,
	Float:ExZ,
	ExVW,
	ExInt,
	EnPickup,
	EnPickupModel,
	ExPickup,
	ExPickupModel,
	bool:EnLabel,
	bool:ExLabel,
	Text3D:EnLabelId,
	Text3D:ExLabelId,
	VehicleEntrance,
	FactionSqlId,
};

static EntranceData[ MAX_ENTRANCES ][ E_ENTRANCE_DATA ],
		EntranceVehicleData[ MAX_ENTRANCE_VEHICLES ][ E_ENTRANCE_VEHICLE_DATA ],
		PlayerInEntrance[ MAX_PLAYERS ] = {-1, ... };

enum E_TEXT_COLOUR_DATA 
{
	RGB,
	Name[ 16 ],
};

static const TextColours[ ][ E_TEXT_COLOUR_DATA ] = 
{
	{0xFFFFFF00, "Balta"},
	{0x00000000, "Juoda"},
	{0xFF000000, "Raudona"},
	{0x00FF0000, "Þalia"},
	{0x0000FF00, "Mëlyna"},
	{0xFFFF0000, "Geltona"},
	{0xFF800000, "Oranþinë"}
};


forward OnEntranceVehicleDataLoad();
forward OnStaticEntranceLoad();

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

public OnGameModeInit()
{
	#if defined entrance_OnGameModeInit
		entrance_OnGameModeInit();
	#endif
	// Pirmiausia reikia pakrauti Transporto priemoniø áëjimø pozicijas.
	mysql_tquery(DbHandle, "SELECT * FROM entrances_vehicle", "OnEntranceVehicleDataLoad", "");
	mysql_tquery(DbHandle, "SELECT * FROM entrances", "OnStaticEntranceLoad","");
}
#if defined _ALS_OnGameModeInit
	#undef OnGameModeInit
#else 	
	#define _ALS_OnGameModeInit
#endif
#define OnGameModeInit 					entrance_OnGameModeInit
#if defined entrance_OnGameModeInit	
	forward entrance_OnGameModeInit();
#endif

public OnEntranceVehicleDataLoad()
{
	for(new i = 0; i < cache_get_row_count(); i++)
	{
		if(i >= sizeof EntranceVehicleData)
		{
			ErrorLog("Table entrances_vehicle data amount exceeds MAX_ENTRANCE_VEHICLES");
			break;
		}
		EntranceVehicleData[ i ][ SqlId ] = cache_get_field_content_int(i, "id");

		EntranceVehicleData[ i ][ EnX ] = cache_get_field_content_float(i, "entrance_x");
		EntranceVehicleData[ i ][ EnY ] = cache_get_field_content_float(i, "entrance_y");
		EntranceVehicleData[ i ][ EnZ ] = cache_get_field_content_float(i, "entrance_z");
		EntranceVehicleData[ i ][ EnA ] = cache_get_field_content_float(i, "entrance_angle");

		EntranceVehicleData[ i ][ ExX ] = cache_get_field_content_float(i, "exit_x");
		EntranceVehicleData[ i ][ ExY ] = cache_get_field_content_float(i, "exit_y");
		EntranceVehicleData[ i ][ ExZ ] = cache_get_field_content_float(i, "exit_z");
		EntranceVehicleData[ i ][ ExA ] = cache_get_field_content_float(i, "exit_angle");
	}
	return 1;
}


public OnStaticEntranceLoad()
{
	new text[ MAX_ENTRANCE_TEXT ], vehicleentrancesqlid;
	for(new i = 0; i < cache_get_row_count(); i++)
	{
		if(i >= MAX_ENTRANCES)
		{
			ErrorLog("Amount of rows in table entrances exceeds MAX_ENTARNCES(" #MAX_ENTRANCES ")");
			break;
		}

		EntranceData[ i ][ SqlId ] = cache_get_field_content_int(i, "id");

		EntranceData[ i ][ EnX ] = cache_get_field_content_float(i, "entrance_x");
		EntranceData[ i ][ EnY ] = cache_get_field_content_float(i, "entrance_y");
		EntranceData[ i ][ EnZ ] = cache_get_field_content_float(i, "entrance_z");
		EntranceData[ i ][ EnVW ] = cache_get_field_content_int(i, "entrance_virtual_world");
		EntranceData[ i ][ EnInt ] = cache_get_field_content_int(i, "entrance_interior");
		EntranceData[ i ][ EnTextColour ] = cache_get_field_content_int(i, "entrance_text_colour");

		EntranceData[ i ][ ExX ] = cache_get_field_content_float(i, "exit_x");
		EntranceData[ i ][ ExY ] = cache_get_field_content_float(i, "exit_y");
		EntranceData[ i ][ ExZ ] = cache_get_field_content_float(i, "exit_z");
		EntranceData[ i ][ ExVW ] = cache_get_field_content_int(i, "exit_virtual_world");
		EntranceData[ i ][ ExInt ] = cache_get_field_content_int(i, "exit_interior");
		EntranceData[ i ][ ExTextColour ] = cache_get_field_content_int(i, "exit_text_colour");

		EntranceData[ i ][ EnPickupModel ] = cache_get_field_content_int(i, "entrance_pickup_model");
		EntranceData[ i ][ ExPickupModel ] = cache_get_field_content_int(i, "exit_pickup_model");

		EntranceData[ i ][ EnLabel ] = (cache_get_field_content_int(i, "entrance_label")) ? (true) : (false);
		EntranceData[ i ][ ExLabel ] = (cache_get_field_content_int(i, "exit_label")) ? (true) : (false);

		cache_get_field_content(i, "entrance_text", EntranceData[ i ][ EnText ], DbHandle, MAX_ENTRANCE_TEXT);
		cache_get_field_content(i, "exit_text", EntranceData[ i ][ ExText ], DbHandle, MAX_ENTRANCE_TEXT);
		UpdateEntrance(i);

		// Tvarkom maðinos koordinates.
		cache_get_field_content(i, "vehicle_entrance", text);
		if(!ismysqlnull(text)) 
		{
			vehicleentrancesqlid = strval(text);
			for(new j = 0; j < sizeof EntranceVehicleData; j++)
				if(EntranceVehicleData[ j ][ SqlId ] == vehicleentrancesqlid)
				{
					EntranceData[ i ][ VehicleEntrance ] = j;
				}
		}
		else 
		{
			EntranceData[ i ][ VehicleEntrance ] = -1;
		}

		// Tvarkom frakcija.
		cache_get_field_content(i, "faction_entrance", text);
		if(!ismysqlnull(text))
		{
			EntranceData[ i ][ FactionSqlId ] = strval(text);
		}
	}
	printf("Loaded %d entrances.", cache_get_row_count());
	return 1;
}

public OnPlayerPickUpDynamicPickup(playerid, pickupid)
{
	new string[ MAX_ENTRANCE_TEXT + 60 ];
	for(new i = 0; i < MAX_ENTRANCES; i++)
	{
		if(!EntranceData[ i ][ SqlId ])
			continue;

		if(EntranceData[ i ][ EnPickup ] == pickupid)
		{
			format(string, sizeof(string), "~g~~h~~h~%s~n~~w~Noredami ieiti - Rasykite ~y~/enter", EntranceData[ i ][ EnText ]);
        	GameTextForPlayer(playerid, string, 4000, 7);
		}
		else if(EntranceData[ i ][ ExPickup ] == pickupid)
		{
			format(string, sizeof(string), "~g~~h~~h~%s~n~~w~Noredami iseiti - Rasykite ~y~/exit", EntranceData[ i ][ ExText ]);
        	GameTextForPlayer(playerid, string, 4000, 7);
		}

	}
    #if defined ent_OnPlayerPickUpDynamicPickup
    	ent_OnPlayerPickUpDynamicPickup(playerid, pickupid);
    #endif
    return 1;
}
#if defined _ALS_OnPlayerPickUpDynamicPUp
    #undef OnPlayerPickUpDynamicPickup
#else 
    #define _ALS_OnPlayerPickUpDynamicPUp
#endif
#define OnPlayerPickUpDynamicPickup ent_OnPlayerPickUpDynamicPickup
#if defined ent_OnPlayerPickUpDynamicPickup 
    forward ent_OnPlayerPickUpDynamicPickup(playerid, pickupid);
#endif
/*
                                                                                                                                         
                                                                            ,,                                 
                           `7MM"""YMM                                mm     db                                 
                             MM    `7                                MM                                        
                             MM   d `7MM  `7MM  `7MMpMMMb.  ,p6"bo mmMMmm `7MM  ,pW"Wq.`7MMpMMMb.  ,pP"Ybd     
                             MM""MM   MM    MM    MM    MM 6M'  OO   MM     MM 6W'   `Wb MM    MM  8I   `"     
                             MM   Y   MM    MM    MM    MM 8M        MM     MM 8M     M8 MM    MM  `YMMMa.     
                             MM       MM    MM    MM    MM YM.    ,  MM     MM YA.   ,A9 MM    MM  L.   I8     
                           .JMML.     `Mbod"YML..JMML  JMML.YMbmd'   `Mbmo.JMML.`Ybmd9'.JMML  JMML.M9mmmP'     
                                                                                                               
                                                                                                                                         
*/

stock GetPlayerEntrance(playerid)
{
	new winner = -1, Float:lowestDistance = -1.0;
	for(new i = 0; i < MAX_ENTRANCES; i++)
	{
		if(!EntranceData[ i ][ SqlId ])
			continue;

		new Float:tmpDistance = GetPlayerDistanceFromPoint(playerid, EntranceData[ i ][ EnX ], EntranceData[ i ][ EnY ], EntranceData[ i ][ EnZ ]);
		if((tmpDistance < lowestDistance || lowestDistance == -1.0) 
			&& GetPlayerVirtualWorld(playerid) == EntranceData[ i ][ EnVW ] && GetPlayerInterior(playerid) == EntranceData[ i ][ EnInt ]
			&& tmpDistance < 5.0)
		{
			winner = i;
			lowestDistance = tmpDistance;
		}
	}
	return winner;
}

stock GetPlayerExit(playerid)
{
	new winner = -1, Float:lowestDistance = -1.0;
	for(new i = 0; i < MAX_ENTRANCES; i++)
	{
		if(!EntranceData[ i ][ SqlId ])
			continue;

		new Float:tmpDistance = GetPlayerDistanceFromPoint(playerid, EntranceData[ i ][ ExX ], EntranceData[ i ][ ExY ], EntranceData[ i ][ ExZ ]);
		if((tmpDistance < lowestDistance || lowestDistance == -1.0) 
			&& tmpDistance < 5.0
			&& GetPlayerVirtualWorld(playerid) == EntranceData[ i ][ ExVW ] 
			&& GetPlayerInterior(playerid) == EntranceData[ i ][ ExInt ])
		{
			winner = i;
			lowestDistance = tmpDistance;
		}
	}
	return winner;
}

stock IsFactionEntrance(entranceindex)
{
	if(EntranceData[ entranceindex ][ FactionSqlId ])
		return true;
	else 
		return false;
}

stock GetEntranceFaction(entranceindex)
{
	return EntranceData[ entranceindex ][ FactionSqlId ];
}

stock IsVehicleEntrance(entranceindex)
{
	if(EntranceData[ entranceindex ][ VehicleEntrance ] != -1)
		return true;
	else 
		return false;
}

stock IsValidEntrance(entranceindex)
{
	if(entranceindex < 0 || entranceindex >= MAX_ENTRANCES)
		return 0;
	if(!EntranceData[ entranceindex ][ SqlId ])
		return 0;
	else
		return 1;
}

stock UpdateEntrance(entranceindex)
{
	if(!IsValidEntrance(entranceindex))
		return 0;

	if(IsValidDynamicPickup(EntranceData[ entranceindex ][ EnPickup ]))
		DestroyDynamicPickup(EntranceData[ entranceindex ][ EnPickup ]);
	if(IsValidDynamicPickup(EntranceData[ entranceindex ][ ExPickup ]))
		DestroyDynamicPickup(EntranceData[ entranceindex ][ ExPickup ]);

	if(IsValidDynamic3DTextLabel(EntranceData[ entranceindex ][ EnLabelId ]))
		DestroyDynamic3DTextLabel(EntranceData[ entranceindex ][ EnLabelId ]);

	if(IsValidDynamic3DTextLabel(EntranceData[ entranceindex ][ ExLabelId ]))
		DestroyDynamic3DTextLabel(EntranceData[ entranceindex ][ ExLabelId ]);

	if(EntranceData[ entranceindex ][ EnPickupModel ] != -1)
	{
		EntranceData[ entranceindex ][ EnPickup ] = CreateDynamicPickup(
			EntranceData[ entranceindex ][ EnPickupModel ], 
			1,
			EntranceData[ entranceindex ][ EnX ], 
			EntranceData[ entranceindex ][ EnY ],
			EntranceData[ entranceindex ][ EnZ ], 
			EntranceData[ entranceindex ][ EnVW ]);
	}
	else 
		EntranceData[ entranceindex ][ EnPickupModel ] = -1;

	if(EntranceData[ entranceindex ][ EnLabel ])
		EntranceData[ entranceindex ][ EnLabelId ] = CreateDynamic3DTextLabel(
			EntranceData[ entranceindex ][ EnText ], 
			EntranceData[ entranceindex ][ EnTextColour ], 
			EntranceData[ entranceindex ][ EnX ], 
			EntranceData[ entranceindex ][ EnY ],
			EntranceData[ entranceindex ][ EnZ ], 
			20.0, 
			.testlos=true,
			.worldid  = EntranceData[ entranceindex ][ EnVW ],
			.interiorid = EntranceData[ entranceindex ][ EnInt ]);
	else 
		EntranceData[ entranceindex ][ EnLabelId ] = INVALID_3DTEXT_ID;

	if(EntranceData[ entranceindex ][ ExPickupModel ] != -1)
	{
		EntranceData[ entranceindex ][ ExPickup ] = CreateDynamicPickup(
			EntranceData[ entranceindex ][ ExPickupModel ], 
			1,
			EntranceData[ entranceindex ][ ExX ], 
			EntranceData[ entranceindex ][ ExY ],
			EntranceData[ entranceindex ][ ExZ ], 
			EntranceData[ entranceindex ][ ExVW ]);
	}
	else 
		EntranceData[ entranceindex ][ ExPickupModel ] = -1;

	if(EntranceData[ entranceindex ][ ExLabel ])
		EntranceData[ entranceindex ][ ExLabelId ] = CreateDynamic3DTextLabel(
			EntranceData[ entranceindex ][ ExText ], 
			EntranceData[ entranceindex ][ ExTextColour ], 
			EntranceData[ entranceindex ][ ExX ], 
			EntranceData[ entranceindex ][ ExY ],
			EntranceData[ entranceindex ][ ExZ ], 
			20.0, 
			.testlos=true,
			.worldid  = EntranceData[ entranceindex ][ ExVW ],
			.interiorid = EntranceData[ entranceindex ][ ExInt ]);
	else 
		EntranceData[ entranceindex ][ ExLabelId ] = INVALID_3DTEXT_ID;
	return 1;
}


stock SetEntranceText(entranceindex, text[])
{
	if(!IsValidEntrance(entranceindex))
		return 0;

	new query[80 + MAX_ENTRANCE_TEXT ];
	mysql_format(DbHandle, query, sizeof(query), "UPDATE entrances SET entrance_text = '%e' WHERE id = %d",
		text, EntranceData[ entranceindex ][ SqlId ]);

	format(EntranceData[ entranceindex ][ EnText ], MAX_ENTRANCE_TEXT, text);
	UpdateEntrance(entranceindex);
	return mysql_pquery(DbHandle, query);
}

stock SetExitText(entranceindex, text[])
{
	if(!IsValidEntrance(entranceindex))
		return 0;

	new query[80 + MAX_ENTRANCE_TEXT ];
	mysql_format(DbHandle, query, sizeof(query), "UPDATE entrances SET exit_text = '%e' WHERE id = %d",
		text, EntranceData[ entranceindex ][ SqlId ]);

	format(EntranceData[ entranceindex ][ ExText ], MAX_ENTRANCE_TEXT, text);
	UpdateEntrance(entranceindex);
	return mysql_pquery(DbHandle, query);
}

stock SetEntranceLocation(entranceindex, Float:x, Float:y, Float:z, interiorid, worldid)
{
	new query[ 220 ];

	EntranceData[ entranceindex ][ EnX ] = x;
	EntranceData[ entranceindex ][ EnY ] = y;
	EntranceData[ entranceindex ][ EnZ ] = z;
	EntranceData[ entranceindex ][ EnInt ] = interiorid;
	EntranceData[ entranceindex ][ EnVW ] = worldid;

	mysql_format(DbHandle, query, sizeof(query), "UPDATE entrances SET entrance_x = %f, entrance_y = %f, entrance_z = %f, entrance_interior = %d, entrance_virtual_world = %d WHERE id = %d",
		x, y, z, interiorid, worldid, EntranceData[ entranceindex ][ SqlId ]);
	UpdateEntrance(entranceindex);
	return mysql_pquery(DbHandle, query);
}

stock SetExitLocation(entranceindex, Float:x, Float:y, Float:z, interiorid, worldid)
{
	new query[ 220 ];

	EntranceData[ entranceindex ][ ExX ] = x;
	EntranceData[ entranceindex ][ ExY ] = y;
	EntranceData[ entranceindex ][ ExZ ] = z;
	EntranceData[ entranceindex ][ ExInt ] = interiorid;
	EntranceData[ entranceindex ][ ExVW ] = worldid;

	mysql_format(DbHandle, query, sizeof(query), "UPDATE entrances SET exit_x = %f, exit_y = %f, exit_z = %f, exit_interior = %d, exit_virtual_world = %d WHERE id = %d",
		x, y, z, interiorid, worldid, EntranceData[ entranceindex ][ SqlId ]);
	UpdateEntrance(entranceindex);
	return mysql_pquery(DbHandle, query);
}

stock SetEntranceVehicleLocation(entranceindex, Float:x, Float:y, Float:z, Float:angle)
{
	new index = -1, query[ 200 ], Cache:result;
	if(EntranceData[ entranceindex ][ VehicleEntrance ] != -1)
	{
		index = EntranceData[ entranceindex ][ VehicleEntrance ];
		mysql_format(DbHandle, query, sizeof(query), "UPDATE entrances_vehicle SET entrance_x = %f, entrance_y = %f, entrance_z = %f, entrance_angle = %f WHERE id = %d",
			x, y, z, angle, EntranceVehicleData[ index ][ SqlId ]);
		mysql_pquery(DbHandle, query);
	}
	else 
	{
		for(new i = 0; i < MAX_ENTRANCE_VEHICLES; i++)
		{
			if(EntranceVehicleData[ i ][ SqlId ])	
				continue;

			index = i;
			break;
		}
		if(index == -1)
			return 1;

		mysql_format(DbHandle, query, sizeof(query), "INSERT INTO entrances_vehicle (entrance_x, entrance_y, entrance_z, entrance_angle) VALUES (%f, %f, %f, %f)",
			x, y, z, angle);
		result = mysql_query(DbHandle, query);
		EntranceVehicleData[ index ][ SqlId ] = cache_insert_id();
		cache_delete(result);

		mysql_format(DbHandle, query, sizeof(query), "UPDATE entrances SET vehicle_entrance = %d WHERE id = %d",
			EntranceVehicleData[ index ][ SqlId ], EntranceData[ entranceindex ][ SqlId ]);
		mysql_pquery(DbHandle, query);
	}

	EntranceData[ entranceindex ][ VehicleEntrance ] = index;
	EntranceVehicleData[ index ][ EnX ] = x;
	EntranceVehicleData[ index ][ EnY ] = y;
	EntranceVehicleData[ index ][ EnZ ] = z;
	EntranceVehicleData[ index ][ EnA ] = angle;
	return 1;
}

stock SetExitVehicleLocation(entranceindex, Float:x, Float:y, Float:z, Float:angle)
{
	new index = -1, query[ 200 ], Cache:result;
	if(EntranceData[ entranceindex ][ VehicleEntrance ] != -1)
	{
		index = EntranceData[ entranceindex ][ VehicleEntrance ];
		mysql_format(DbHandle, query, sizeof(query), "UPDATE entrances_vehicle SET exit_x = %f, exit_y = %f, exit_z = %f, exit_angle = %f WHERE id = %d",
			x, y, z, angle, EntranceVehicleData[ index ][ SqlId ]);
		mysql_pquery(DbHandle, query);
	}
	else 
	{
		for(new i = 0; i < MAX_ENTRANCE_VEHICLES; i++)
		{
			if(EntranceVehicleData[ i ][ SqlId ])	
				continue;

			index = i;
			break;
		}
		if(index == -1)
			return 1;

		mysql_format(DbHandle, query, sizeof(query), "INSERT INTO entrances_vehicle (exit_x, exit_y, exit_z, exit_angle) VALUES (%f, %f, %f, %f)",
			x, y, z, angle);
		result = mysql_query(DbHandle, query);
		EntranceVehicleData[ index ][ SqlId ] = cache_insert_id();
		cache_delete(result);

		mysql_format(DbHandle, query, sizeof(query), "UPDATE entrances SET vehicle_entrance = %d WHERE id = %d",
			EntranceVehicleData[ index ][ SqlId ], EntranceData[ entranceindex ][ SqlId ]);
		mysql_pquery(DbHandle, query);
	}

	EntranceData[ entranceindex ][ VehicleEntrance ] = index;
	EntranceVehicleData[ index ][ ExX ] = x;
	EntranceVehicleData[ index ][ ExY ] = y;
	EntranceVehicleData[ index ][ ExZ ] = z;
	EntranceVehicleData[ index ][ ExA ] = angle;
	return 1;
}

stock SetEntranceTextColour(entranceindex, type, rgb)
{
	// Pirmi 8 bitai turi bût tuðti.
	for(new i = 0; i < 8; i++)
		if(rgb & (0b1 << i))
		{
			return 0;
		}

	if(type ==  ENTRANCE_TYPE_ENTRANCE)
	{
		for(new i = 8; i < 32; i++)
		if((EntranceData[ entranceindex ][ EnTextColour ] & (0b1 << i)) && !(rgb & (0b1 << i)))
			EntranceData[ entranceindex ][ EnTextColour ] ^= (0b1 << i);

		EntranceData[ entranceindex ][ EnTextColour ] |= rgb;
	}
	else if(type == ENTRANCE_TYPE_EXIT)
	{
		for(new i = 8; i < 32; i++)
		if((EntranceData[ entranceindex ][ ExTextColour ] & (0b1 << i)) && !(rgb & (0b1 << i)))
			EntranceData[ entranceindex ][ ExTextColour ] ^= (0b1 << i);

		EntranceData[ entranceindex ][ ExTextColour ] |= rgb;
	}


	UpdateEntrance(entranceindex);
	return SaveEntranceTextColours(entranceindex);
}

stock SetEntranceTextColourAlpha(entranceindex, type, alpha)
{
	if(type != ENTRANCE_TYPE_EXIT && type != ENTRANCE_TYPE_ENTRANCE)
		return 0;

	// Alpha turi bût 0-255
	if(alpha < 0 || alpha > 255)
		return 0;

	if(type == ENTRANCE_TYPE_EXIT)
	{
		for(new i = 0; i < 8; i++)
		{
			if((EntranceData[ entranceindex ][ ExTextColour ] & (0b1 << i)) && !(alpha & (0b1 << i)))
				EntranceData[ entranceindex ][ ExTextColour ] ^= (0b1 << i);
		}
		EntranceData[ entranceindex ][ ExTextColour ] |= alpha;
	}
	else if(type == ENTRANCE_TYPE_ENTRANCE)
	{
		for(new i = 0; i < 8; i++)
		{
			if((EntranceData[ entranceindex ][ EnTextColour ] & (0b1 << i)) && !(alpha & (0b1 << i)))
				EntranceData[ entranceindex ][ EnTextColour ] ^= (0b1 << i);
		}
		EntranceData[ entranceindex ][ EnTextColour ] |= alpha;
	}
	UpdateEntrance(entranceindex);
	SaveEntranceTextColours(entranceindex);
	return 1;
}

stock SaveEntranceTextColours(entranceindex)
{
	new query[110];
	mysql_format(DbHandle, query, sizeof(query), "UPDATE entrances SET entrance_text_colour = %d, exit_text_colour = %d WHERE id = %d",
		EntranceData[ entranceindex ][ EnTextColour ],
		EntranceData[ entranceindex ][ ExTextColour ],
		EntranceData[ entranceindex ][ SqlId ]);
	return mysql_pquery(DbHandle, query);
}


stock SetEntrancePickupData(entranceindex, pickupmodel)
{
	new query[ 90 ];
	mysql_format(DbHandle, query, sizeof(query), "UPDATE entrances SET entrance_pickup_model = %d WHERE id = %d",
		pickupmodel, EntranceData[ entranceindex ][ SqlId ]);

	EntranceData[ entranceindex ][ EnPickupModel ] = pickupmodel;
	UpdateEntrance(entranceindex);

	return mysql_pquery(DbHandle, query);
}

stock SetExitPickupData(entranceindex, pickupmodel)
{
	new query[ 90 ];
	mysql_format(DbHandle, query, sizeof(query), "UPDATE entrances SET exit_pickup_model = %d WHERE id = %d",
		pickupmodel, EntranceData[ entranceindex ][ SqlId ]);

	EntranceData[ entranceindex ][ ExPickupModel ] = pickupmodel;
	UpdateEntrance(entranceindex);

	return mysql_pquery(DbHandle, query);
}

stock SetEntranceLabel(entranceindex, bool:label)
{
	new query[ 70 ];

	EntranceData[ entranceindex ][ EnLabel ] = label;
	mysql_format(DbHandle, query, sizeof(query), "UPDATE entrances SET entrance_label = %d WHERE id = %d",
		_:label, EntranceData[ entranceindex ][ SqlId ]);
	UpdateEntrance(entranceindex);
	return mysql_pquery(DbHandle, query);
}

stock SetExitLabel(entranceindex, bool:label)
{
	new query[ 70 ];

	EntranceData[ entranceindex ][ ExLabel ] = label;
	mysql_format(DbHandle, query, sizeof(query), "UPDATE entrances SET entrance_label = %d WHERE id = %d",
		_:label, EntranceData[ entranceindex ][ SqlId ]);
	UpdateEntrance(entranceindex);
	return mysql_pquery(DbHandle, query);
}

stock RemoveEntrance(entranceindex)
{
	new query[ 60 ];
	if(EntranceData[ entranceindex ][ VehicleEntrance ] != -1)
	{
		RemoveEntranceVehicleData(entranceindex);
	}
	mysql_format(DbHandle, query, sizeof(query), "DELETE FROM entrances WHERE id = %d",
		EntranceData[ entranceindex ][ SqlId ]);
	mysql_pquery(DbHandle, query);

	if(IsValidDynamicPickup(EntranceData[ entranceindex ][ EnPickup ]))
		DestroyDynamicPickup(EntranceData[ entranceindex ][ EnPickup ]);
	if(IsValidDynamicPickup(EntranceData[ entranceindex ][ ExPickup ]))
		DestroyDynamicPickup(EntranceData[ entranceindex ][ ExPickup ]);

	if(IsValidDynamic3DTextLabel(EntranceData[ entranceindex ][ EnLabelId ]))
		DestroyDynamic3DTextLabel(EntranceData[ entranceindex ][ EnLabelId ]);

	if(IsValidDynamic3DTextLabel(EntranceData[ entranceindex ][ ExLabelId ]))
		DestroyDynamic3DTextLabel(EntranceData[ entranceindex ][ ExLabelId ]);

	static EmptyEnData[ E_ENTRANCE_DATA ];
	EntranceData[ entranceindex ] = EmptyEnData;
	return 1;
}

stock RemoveEntranceVehicleData(entranceindex)
{
	new query[ 60 ];
	mysql_format(DbHandle, query, sizeof(query), "DELETE FROM entrances_vehicle WHERE id = %d",
		EntranceVehicleData[ EntranceData[ entranceindex ][ VehicleEntrance ] ][ SqlId ]);
	mysql_pquery(DbHandle, query);

	static EmptyEnVehData[ E_ENTRANCE_VEHICLE_DATA ];
	EntranceVehicleData[ EntranceData[ entranceindex ][ VehicleEntrance ] ] = EmptyEnVehData;
	return 1;
}

stock AddNewEntrance(Float:x, Float:y, Float:z, interiorid, worldid)
{
	new query[ 180 ], index = -1, Cache:result;
	for(new i = 0; i < MAX_ENTRANCES; i++)
		if(!EntranceData[ i ][ SqlId ])
		{
			index = i;
			break;
		}
	if(index == -1)
		return 0;

	mysql_format(DbHandle, query, sizeof(query), "INSERT INTO entrances (entrance_x, entrance_y, entrance_z, entrance_interior, entrance_virtual_world, entrance_pickup_model) VALUES (%f, %f, %f, %d, %d, %d)",
		x, y, z, interiorid, worldid, DEFAULT_ENTRANCE_PICKUP_MODEL);
	result = mysql_query(DbHandle, query);

	EntranceData[ index ][ SqlId ] = cache_insert_id();
	EntranceData[ index ][ EnX ] = x;
	EntranceData[ index ][ EnY ] = y;
	EntranceData[ index ][ EnZ ] = z;
	EntranceData[ index ][ EnInt ] = interiorid;
	EntranceData[ index ][ EnVW ] = worldid;
	EntranceData[ index ][ VehicleEntrance ] = -1;
	EntranceData[ index ][ EnPickupModel ] = DEFAULT_ENTRANCE_PICKUP_MODEL;
	cache_delete(result);

	EntranceData[ index ][ EnTextColour ] = 0xFFFFFFFF;
	EntranceData[ index ][ ExTextColour ] = 0xFFFFFFFF;
	SaveEntranceTextColours(index);

	return index;
}



/*
                                                                                                          
                                                                                              ,,          
              .g8"""bgd                                                                     `7MM          
            .dP'     `M                                                                       MM          
            dM'       ` ,pW"Wq.`7MMpMMMb.pMMMb.  `7MMpMMMb.pMMMb.   ,6"Yb.  `7MMpMMMb.   ,M""bMM  ,pP"Ybd 
            MM         6W'   `Wb MM    MM    MM    MM    MM    MM  8)   MM    MM    MM ,AP    MM  8I   `" 
            MM.        8M     M8 MM    MM    MM    MM    MM    MM   ,pm9MM    MM    MM 8MI    MM  `YMMMa. 
            `Mb.     ,'YA.   ,A9 MM    MM    MM    MM    MM    MM  8M   MM    MM    MM `Mb    MM  L.   I8 
              `"bmmmd'  `Ybmd9'.JMML  JMML  JMML..JMML  JMML  JMML.`Moo9^Yo..JMML  JMML.`Wbmd"MML.M9mmmP' 
                                                                                                          
                                                                                                          
*/




CMD:enter(playerid)
{
	new vehicleid = GetPlayerVehicleID(playerid),
		entrindex = GetPlayerEntrance(playerid);

	if(entrindex == -1)
		return 0;
		//SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neesate prie áëjimo.");

	else if(vehicleid && !IsVehicleEntrance(entrindex))
		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, su transporto priemone ávaþiuoti èia negalite.");

	else if(IsFactionEntrance(entrindex) && GetPlayerFactionId(playerid) != GetEntranceFaction(entrindex))
		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðis áëjimas tik darbuotojams.");

	else 
	{
		Unfreeze[ playerid ] = 2;
        TogglePlayerControllable(playerid, false);
		if(vehicleid)
		{
			SetVehiclePos(vehicleid, 
				EntranceVehicleData[ EntranceData[ entrindex ][ VehicleEntrance ] ][ ExX ],
				EntranceVehicleData[ EntranceData[ entrindex ][ VehicleEntrance ] ][ ExY ],
				EntranceVehicleData[ EntranceData[ entrindex ][ VehicleEntrance ] ][ ExZ ]);
            SetVehicleVirtualWorld(vehicleid, EntranceData[ entrindex ][ ExVW ]);
            LinkVehicleToInterior(vehicleid, EntranceData[ entrindex ][ ExInt ]);
            SetVehicleZAngle(vehicleid, EntranceVehicleData[ EntranceData[ entrindex ][ VehicleEntrance ] ][ ExA ]);
            VGaraze[ vehicleid ] = true;
            foreach(new i : Player)
            	if(IsPlayerInVehicle(i, vehicleid))
            	{
            		SetPlayerVirtualWorld(i, EntranceData[ entrindex ][ ExVW ]);
        			SetPlayerInterior(i, EntranceData[ entrindex ][ ExInt ]);
        			PlayerInEntrance[ i ] = entrindex;
            	}
		}
		else 
		{
			SetPlayerPos(playerid, EntranceData[ entrindex ][ ExX ], EntranceData[ entrindex ][ ExY ], EntranceData[ entrindex ][ ExZ ]);
			SetPlayerVirtualWorld(playerid, EntranceData[ entrindex ][ ExVW ]);
        	SetPlayerInterior(playerid, EntranceData[ entrindex ][ ExInt ]);
        	PlayerInEntrance[ playerid ] = entrindex;
		}
	}
    return 1;
}
CMD:exit(playerid)
{
    new vehicleid = GetPlayerVehicleID(playerid),
		entrindex = GetPlayerExit(playerid);

	if(entrindex == -1)
		return 0;
		//SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neesate prie iðëjimo.");
	else if(PlayerInEntrance[ playerid ] != entrindex)
		ErrorLog("Player %s is not in entrance to which he is near %d its sqlid:%d", GetName(playerid), entrindex, EntranceData[ entrindex ][ SqlId ]);
	else 
	{
		Unfreeze[ playerid ] = 2;
        TogglePlayerControllable(playerid, false);
		if(vehicleid)
		{
			SetVehiclePos(vehicleid, 
				EntranceVehicleData[ EntranceData[ entrindex ][ VehicleEntrance ] ][ EnX ],
				EntranceVehicleData[ EntranceData[ entrindex ][ VehicleEntrance ] ][ EnY ],
				EntranceVehicleData[ EntranceData[ entrindex ][ VehicleEntrance ] ][ EnZ ]);
            SetVehicleVirtualWorld(vehicleid, EntranceData[ entrindex ][ EnVW ]);
            LinkVehicleToInterior(vehicleid, EntranceData[ entrindex ][ EnInt ]);
            SetVehicleZAngle(vehicleid, EntranceVehicleData[ EntranceData[ entrindex ][ VehicleEntrance ] ][ EnA ]);
            VGaraze[ vehicleid ] = true;
            foreach(new i : Player)
            	if(IsPlayerInVehicle(i, vehicleid))
            	{
            		SetPlayerVirtualWorld(i, EntranceData[ entrindex ][ EnVW ]);
        			SetPlayerInterior(i, EntranceData[ entrindex ][ EnInt ]);
        			PlayerInEntrance[ i ] = -1;
            	}
		}
		else 
		{
			SetPlayerPos(playerid, EntranceData[ entrindex ][ EnX ], EntranceData[ entrindex ][ EnY ], EntranceData[ entrindex ][ EnZ ]);
			SetPlayerVirtualWorld(playerid, EntranceData[ entrindex ][ EnVW ]);
        	SetPlayerInterior(playerid, EntranceData[ entrindex ][ EnInt ]);
        	PlayerInEntrance[ playerid ] = -1;
		}
	}
    return 1;
}



/*
                    

                                                                                                                                 
                        `7MMM.     ,MMF'                                                                                   mm    
                          MMMb    dPMM                                                                                     MM    
                          M YM   ,M MM   ,6"Yb.  `7MMpMMMb.   ,6"Yb.  .P"Ybmmm .gP"Ya `7MMpMMMb.pMMMb.  .gP"Ya `7MMpMMMb.mmMMmm  
                          M  Mb  M' MM  8)   MM    MM    MM  8)   MM :MI  I8  ,M'   Yb  MM    MM    MM ,M'   Yb  MM    MM  MM    
                          M  YM.P'  MM   ,pm9MM    MM    MM   ,pm9MM  WmmmP"  8M""""""  MM    MM    MM 8M""""""  MM    MM  MM    
                          M  `YM'   MM  8M   MM    MM    MM  8M   MM 8M       YM.    ,  MM    MM    MM YM.    ,  MM    MM  MM    
                        .JML. `'  .JMML.`Moo9^Yo..JMML  JMML.`Moo9^Yo.YMMMMMb  `Mbmmd'.JMML  JMML  JMML.`Mbmmd'.JMML  JMML.`Mbmo 
                                                                     6'     dP                                                   
                                                                     Ybmmmd'                                                     

                                                  ,,    ,,             ,,                             
                                                `7MM    db           `7MM                             
                                                  MM                   MM                             
                                             ,M""bMM  `7MM   ,6"Yb.    MM  ,pW"Wq.   .P"Ybmmm ,pP"Ybd 
                                           ,AP    MM    MM  8)   MM    MM 6W'   `Wb :MI  I8   8I   `" 
                                           8MI    MM    MM   ,pm9MM    MM 8M     M8  WmmmP"   `YMMMa. 
                                           `Mb    MM    MM  8M   MM    MM YA.   ,A9 8M        L.   I8 
                                            `Wbmd"MML..JMML.`Moo9^Yo..JMML.`Ybmd9'   YMMMMMb  M9mmmP' 
                                                                                    6'     dP         
                                                                                    Ybmmmd'           

*/



enum E_ENTRANCE_INDEX_INPUT_USAGE
{
	NewText,
	Position,
	VehiclePosition,
	Settings,
	Information,
	Delete,
	TextColour,
	TextAlpha,
};

EntranceManagementDialog.ShowMain(playerid)
{
	ShowPlayerDialog(playerid, DIALOG_ENTRANCE_MENU_MAIN, DIALOG_STYLE_LIST, "Áëjimø valdymas", 
		"- Kurti naujà\n\
		- Keisti tekstà\n\
		- Keisti pozicijà\n\
		- Keisti transporto priemonës pozicijà\n\
		- Keisti nustatymus\n\
		- Þiûrëti informacijà\n\
		- Keisti teksto spalvà\n\
		- Keisti teksto ryðkumà\n\
		- Paðalinti áëjimà", 
		"Pasirinkti", "Iðeiti");
	return 1;
}

hook OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
	switch(dialogid)
	{
		case DIALOG_ENTRANCE_MENU_MAIN:
		{
			if(!response)
				return 1;

			new entrindex = GetPlayerEntrance(playerid);

			switch(listitem)
			{
				// Kurti naujà
				case 0:
				{
					new Float:x, Float:y, Float:z, string[ 120 ];
					GetPlayerPos(playerid, x, y, z);
					entrindex = AddNewEntrance(x, y, z, GetPlayerInterior(playerid), GetPlayerVirtualWorld(playerid));
					format(string, sizeof(string), "Sukûrëte áëjimà. Jo ID: %d. Jo tikriausiai nematysite kol nepakeisite nustatymø/nepridësite teksto.", entrindex);
					SendClientMessage(playerid, COLOR_NEWS, string);
					EntranceManagementDialog.ShowMain(playerid);
					//EntranceManagementDialog.ChangeText(playerid, entrindex);
				}
				// Keisti tekstà
				case 1: EntranceManagementDialog.ChangeText(playerid, entrindex);
				// Keisti pozicijà
				case 2: EntranceManagementDialog.ChangeLocation(playerid, entrindex);
				// Keisti auto pozicija
				case 3: EntranceManagementDialog.ChangeVehicleLocation(playerid, entrindex);
				// Keisti nustatymus
				case 4: EntranceManagementDialog.ChangeSettings(playerid, entrindex);
				// Ziureti informacijà
				case 5: EntranceManagementDialog.Information(playerid, entrindex);
				// Teksto spalva 
				case 6: EntranceManagementDialog.ChangeTextColour(playerid, entrindex);
				// Teksto ryskumas (ALPHA)
				case 7: EntranceManagementDialog.ChangeTextAlpha(playerid, entrindex);
				// Delete entrance 
				case 8: EntranceManagementDialog.Delete(playerid, entrindex);

			}
			return 1;
		}
		case DIALOG_ENTRANCE_MENU_INPUT_INDE:
		{
			if(!response)
				EntranceManagementDialog.ShowMain(playerid);
			else 
			{
				new index, 
					E_ENTRANCE_INDEX_INPUT_USAGE:usage = E_ENTRANCE_INDEX_INPUT_USAGE:GetPVarInt(playerid, "IndexUsage");
				if(sscanf(inputtext, "i", index))
					EntranceManagementDialog.ShowIndexInput(playerid, usage);

				else if(!IsValidEntrance(index))
					EntranceManagementDialog.ShowIndexInput(playerid, usage, "Tokio áëjimo nëra.");

				else 
				{
					switch(usage)
					{
						case NewText, Position, VehiclePosition, TextColour, TextAlpha: EntranceManagementDialog.ShowTypeSelect(playerid, index, usage);
						case Settings: EntranceManagementDialog.ChangeSettings(playerid, index);
						case Information: EntranceManagementDialog.Information(playerid, index);
						case Delete: EntranceManagementDialog.Delete(playerid, index);
					}
				}
			}
			return 1;
		}
		case DIALOG_ENTRANCE_MENU_TYPE_SELEC:
		{
			new E_ENTRANCE_INDEX_INPUT_USAGE:usage = E_ENTRANCE_INDEX_INPUT_USAGE:GetPVarInt(playerid, "IndexUsage"),
				index = GetPVarInt(playerid, "Index"),
				type = 0;

			if(response)
				type = ENTRANCE_TYPE_ENTRANCE;
			else 	
				type = ENTRANCE_TYPE_EXIT;

			switch(usage)
			{
				case NewText: EntranceManagementDialog.ChangeText(playerid, index, type);
				case Position: EntranceManagementDialog.ChangeLocation(playerid, index, type);
				case VehiclePosition: EntranceManagementDialog.ChangeVehicleLocation(playerid, index, type);
				case TextColour: EntranceManagementDialog.ChangeTextColour(playerid, index, type);
				case TextAlpha: EntranceManagementDialog.ChangeTextAlpha(playerid, index, type);
			}
			DeletePVar(playerid, "IndexUsage");
			DeletePVar(playerid, "Index");
			return 1;
		}
		case DIALOG_ENTRANCE_MENU_INFO:
		{
			EntranceManagementDialog.ShowMain(playerid);
			return 1;
		}
		case DIALOG_ENTRANCE_MENU_TEXT:
		{
			if(!response)
				return 1;

			new entrindex = GetPVarInt(playerid, "En.Index"),
				exitenterstatus = GetPVarInt(playerid, "En.Status");

			if(isnull(inputtext) || strlen(inputtext) >= MAX_ENTRANCE_TEXT)
			{
				SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, tekstà gali sudaryti nuo 1 iki " #MAX_ENTRANCE_TEXT " simboliø.");
				EntranceManagementDialog.ChangeText(playerid, entrindex, exitenterstatus);
			}
			else 
			{
				if(exitenterstatus == ENTRANCE_TYPE_ENTRANCE)
				{
					SetEntranceText(entrindex, inputtext);
					SendClientMessage(playerid, COLOR_NEWS, "Áëjimo tekstas sëkmingai pakeistas.");
				}
				else if(exitenterstatus == ENTRANCE_TYPE_EXIT)
				{
					SetExitText(entrindex, inputtext);
					SendClientMessage(playerid, COLOR_NEWS, "Iðëjimo tekstas sëkmingai pakeistas.");
				}
				else 
					SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida.");
				EntranceManagementDialog.ShowMain(playerid);
			}
			return 1;
		}
		case DIALOG_ENTRANCE_MENU_ALPHA:
		{
			if(!response)
				EntranceManagementDialog.ShowMain(playerid);
			else 
			{
				new alpha, 
					index = GetPVarInt(playerid, "En.Index"),
					status = GetPVarInt(playerid, "En.Status");
				if(sscanf(inputtext, "i", alpha) || alpha < 1 || alpha > 100)
				{
					SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida skaièius negali bûti maþesnis uþ 1 ar didesnis uþ 100.");
					EntranceManagementDialog.ChangeTextAlpha(playerid, index, status);
				}
				else 
				{
					// Mums ávedë nuo 1 iki 100. Bet mums reikia nuo 0 iki 255.
					// Kitaip tariant gavom procentus :)
					alpha = 255 / 100 * alpha;
					
					SetEntranceTextColourAlpha(index, status, alpha);
					EntranceManagementDialog.ShowMain(playerid);
				}
				DeletePVar(playerid, "En.Index");
				DeletePVar(playerid, "En.Status");
			}
			return 1;
		}
		case DIALOG_ENTRANCE_MENU_TEXT_COLOR:
		{
			if(!response)
				EntranceManagementDialog.ShowMain(playerid);

			else 
			{
				new index = GetPVarInt(playerid, "En.Index"),
					status = GetPVarInt(playerid, "En.Status");

				if(!listitem)
				{
					EntranceManagementDialog.CustomColour(playerid, index, status);
				}
				else 
				{
					for(new i = 0; i < sizeof TextColours; i++)
						if(!strcmp(inputtext, TextColours[ i ][ Name ]))
						{
							printf("Found colour at i %d. Name:%s binary:%b", i, TextColours[ i ][ Name ], TextColours[ i ][ RGB]);
							SetEntranceTextColour(index, status, TextColours[ i ][ RGB ]);
							break;
						}
					EntranceManagementDialog.ShowMain(playerid);
				}
			}
			return 1;
		}
		case DIALOG_ENTRANCE_MENU_COLOUR_CUS:
		{
			new colour,
				index = GetPVarInt(playerid, "En.Index"),
				status = GetPVarInt(playerid, "En.Status");

			if(!response)
				EntranceManagementDialog.ChangeTextColour(playerid, index, status);
			else 
			{
				printf("inputtext2:%s len:%d", inputtext, strlen(inputtext));
				if(strlen(inputtext) != 6) 
					EntranceManagementDialog.CustomColour(playerid, index, status);
				else 
				{
					for(new i = 0; i < strlen(inputtext); i++)
						if((inputtext[ i ] >= '0' && inputtext[ i ] <= '9') || (inputtext[ i ] >= 'A' && inputtext[ i ] <= 'F'))
						{
							//tmp[ i ] = inputtext2[ i ];
						}
						else 
							EntranceManagementDialog.CustomColour(playerid, index, status);

					new val = sscanf(inputtext, "h", colour);
					printf("Sscanf returned:%d", val);
					val = SetEntranceTextColour(index, status, colour);
					printf("SetEntranceTextColour returned:%d binary colour:%b", val, colour);
					EntranceManagementDialog.ShowMain(playerid);
				}

			}
			return 1;
		}
		case DIALOG_ENTRANCE_MENU_SETTINGS:
		{
			if(!response)
				EntranceManagementDialog.ShowMain(playerid);
			else 
			{
				new entrindex = GetPVarInt(playerid, "En.Index");
				switch(listitem)
				{
					case 0:
					{
						if(EntranceData[ entrindex ][ EnPickupModel ] == -1)
						{
							SetEntrancePickupData(entrindex, DEFAULT_ENTRANCE_PICKUP_MODEL);
						}
						else 
							SetEntrancePickupData(entrindex, -1);
					}
					case 1:
					{
						if(EntranceData[ entrindex ][ ExPickupModel ] == -1)
							SetExitPickupData(entrindex, DEFAULT_ENTRANCE_PICKUP_MODEL);
						else 
							SetExitPickupData(entrindex, -1);
					}
					case 2:
					{
						SetEntranceLabel(entrindex, !EntranceData[ entrindex ][ EnLabel ]);
					}
					case 3:
					{
						SetExitLabel(entrindex, !EntranceData[ entrindex ][ ExLabel ]);
					}
					case 4:
					{
						if(EntranceData[ entrindex ][ VehicleEntrance ] == -1)
						{
							SendClientMessage(playerid, COLOR_NEWS, "Nustatytas ávaþiavimas su transporto priemonëmis. Jis neveiks kol nepridësite koorrdinaèiø.");
						}
						else 
						{
							RemoveEntranceVehicleData(entrindex);
						}
					}
				}
				EntranceManagementDialog.ChangeSettings(playerid, entrindex);
				return 1;
			}
			return 1;
		}
	}
	return 1;
}

EntranceManagementDialog.ShowIndexInput(playerid, E_ENTRANCE_INDEX_INPUT_USAGE:usage, errostr[] = "")
{
	new string[ 80 ];
	format(string, sizeof(string), "{AA1100}%s\n{FFFFFF}Áveskite áëjimo ID.", errostr);
	SetPVarInt(playerid, "IndexUsage", _:usage);
	ShowPlayerDialog(playerid, DIALOG_ENTRANCE_MENU_INPUT_INDE, DIALOG_STYLE_INPUT, "Áëjimo ID", string, "Tæsti", "Atgal");
	return 1;
}

EntranceManagementDialog.ShowTypeSelect(playerid, entranceindex, E_ENTRANCE_INDEX_INPUT_USAGE:usage)
{
	//new E_ENTRANCE_INDEX_INPUT_USAGE:usage = E_ENTRANCE_INDEX_INPUT_USAGE:GetPVarInt(playerid, "IndexUsage");
	SetPVarInt(playerid, "IndexUsage", _:usage);
	SetPVarInt(playerid, "Index", entranceindex);
	/*inline TypeSelect(pid, dialogid, response, listitem, string:inputtext[])
	{
		#pragma unused pid, dialogid, listitem, inputtext
		
	}
	*/
	//Dialog_ShowCallback(playerid, using inline TypeSelect, DIALOG_STYLE_MSGBOX, " ", "Kà norite redaguoti. Áëjimà ar iðëjimà?", "Áëjimà", "Iðëjimà", DIALOG_ENTRANCE_MENU_TYPE_SELEC);
	ShowPlayerDialog(playerid, DIALOG_ENTRANCE_MENU_TYPE_SELEC, DIALOG_STYLE_MSGBOX, " ", "Kà norite redaguoti. Áëjimà ar iðëjimà?", "Áëjimà", "Iðëjimà");
}

EntranceManagementDialog.ChangeText(playerid, entrindex, exitenter = 0)
{
	if(!IsValidEntrance(entrindex))
		EntranceManagementDialog.ShowIndexInput(playerid, NewText);
	else if(!exitenter)
		EntranceManagementDialog.ShowTypeSelect(playerid, entrindex, NewText);
	else 
	{
		SetPVarInt(playerid, "En.Index", entrindex);
		SetPVarInt(playerid, "En.Status", exitenter);
		ShowPlayerDialog(playerid, DIALOG_ENTRANCE_MENU_TEXT, DIALOG_STYLE_INPUT, "Teksto keitimas.", "Áveskite naujà tekstà", "Tæsti", "Iðeiti");
	}
	return 1;
}
EntranceManagementDialog.ChangeLocation(playerid, entrindex, type = 0)
{
	printf("EntranceManagementDialog.ChangeLocation(%d, %d, %d)", playerid, entrindex, type);
	if(!IsValidEntrance(entrindex))
		EntranceManagementDialog.ShowIndexInput(playerid, Position);
	else if(!type)
		EntranceManagementDialog.ShowTypeSelect(playerid, entrindex, Position);
	else 
	{
		new Float:x, Float:y, Float:z, 
			interiorid = GetPlayerInterior(playerid),
			worldid = GetPlayerVirtualWorld(playerid);
		GetPlayerPos(playerid, x, y, z);


		if(type == ENTRANCE_TYPE_ENTRANCE)
		{
			SetEntranceLocation(entrindex, x, y, z, interiorid, worldid);
			SendClientMessage(playerid, COLOR_NEWS, "Iëjimo pozicija pakeista.");
		}
		else if(type == ENTRANCE_TYPE_EXIT)
		{
			SetExitLocation(entrindex, x, y, z, interiorid, worldid);
			SendClientMessage(playerid, COLOR_NEWS, "Iðëjimo pozicija sëkmingai pakeista.");
		}
		EntranceManagementDialog.ShowMain(playerid);
	}
	return 1;
}


EntranceManagementDialog.ChangeVehicleLocation(playerid, entrindex, type = 0)
{
	if(!IsValidEntrance(entrindex))
		EntranceManagementDialog.ShowIndexInput(playerid, VehiclePosition);
	else if(!type)
		EntranceManagementDialog.ShowTypeSelect(playerid, entrindex, VehiclePosition);
	else 
	{
		new Float:x, Float:y, Float:z, Float:angle, 
			vehicleid = GetPlayerVehicleID(playerid);

		if(vehicleid)
		{
			GetVehiclePos(vehicleid, x, y, z);
			GetVehicleZAngle(vehicleid, angle);
		}
		else 
		{
			GetPlayerPos(playerid, x, y, z);
			GetPlayerFacingAngle(playerid, angle);
		}

		if(type == ENTRANCE_TYPE_ENTRANCE)
		{
			SetEntranceVehicleLocation(entrindex, x, y, z, angle);
			SendClientMessage(playerid, COLOR_NEWS, "Iëjimo pozicija pakeista.");
		}
		else if(type == ENTRANCE_TYPE_EXIT)
		{
			SetExitVehicleLocation(entrindex, x, y, z, angle);
			SendClientMessage(playerid, COLOR_NEWS, "Iðëjimo pozicija sëkmingai pakeista.");
		}
		EntranceManagementDialog.ShowMain(playerid);
	}
	return 1;
}

stock EntranceManagementDialog.ChangeSettings(playerid, index)
{
	new entrindex = index;
	if(!IsValidEntrance(entrindex))
		EntranceManagementDialog.ShowIndexInput(playerid, Settings);
	else 
	{
		new string[ 256 ];
		format(string, sizeof(string), "{FFFFFF}\
			Áëjimo pickup[%s]\n\
			Iðëjimo pickup[%s]\n\
			Áëjimo 3D label[%s]\n\
			Iðëjimo 3D label[%s]\n\
			Leisti ávaþiuoti su tr. priemonëmis[%s]",
			(EntranceData[ index ][ EnPickupModel ] == -1) ? ("{FF0000}-{FFFFFF}") : ("{00FF00}+{FFFFFF}"),
			(EntranceData[ index ][ ExPickupModel ] == -1) ? ("{FF0000}-{FFFFFF}") : ("{00FF00}+{FFFFFF}"),
			(!EntranceData[ index ][ EnLabel ]) ? ("{FF0000}-{FFFFFF}") : ("{00FF00}+{FFFFFF}"),
			(!EntranceData[ index ][ ExLabel ]) ? ("{FF0000}-{FFFFFF}") : ("{00FF00}+{FFFFFF}"),
			(EntranceData[ index ][ VehicleEntrance ] == -1) ? ("{FF0000}Ne{FFFFFF}") : ("{00FF00}Taip{FFFFFF}")
		);

		SetPVarInt(playerid, "En.Index", index);
		ShowPlayerDialog(playerid, DIALOG_ENTRANCE_MENU_SETTINGS, DIALOG_STYLE_LIST, "Áëjimo/iðëjimo nustatymai", string, "Pasirinkti", "Iðeiti");
	}
	return 1;
}

stock EntranceManagementDialog.Information(playerid, index)
{
	if(!IsValidEntrance(index))
		EntranceManagementDialog.ShowIndexInput(playerid, Settings);
	else 
	{
		new string[ 128 ];
		format(string, sizeof(string), "ID serveryje: %d\n\
			ID duomenø bazëjë: %d\n\
			Transporto priemonës: %s",
			index,
			EntranceData[ index ][ SqlId ],
			(EntranceData[ index ][ VehicleEntrance ] == -1) ? ("neáleidþiamos"):("áleidþiamos")
		);
		ShowPlayerDialog(playerid, DIALOG_ENTRANCE_MENU_INFO, DIALOG_STYLE_MSGBOX, "Informacija", string, "Gerai", "");
	}
	return 1;
}

stock EntranceManagementDialog.Delete(playerid, index)
{
	new entrindex = index;
	if(!IsValidEntrance(entrindex))
		EntranceManagementDialog.ShowIndexInput(playerid, Delete);
	else 
	{
		inline DeleteConfirm(pid, dialogid, response, listitem, string:inputtext[])
		{
			#pragma unused pid, dialogid, listitem, inputtext
			if(!response)
				EntranceManagementDialog.ShowMain(playerid);
			else 
			{
				AdminLog(GetPlayerSqlId(playerid), EntranceData[ entrindex ][ SqlId ], "Sunaikino áëjimà");
				RemoveEntrance(entrindex);
				SendClientMessage(playerid, COLOR_NEWS, "Áëjimas sëkmingai paðalintas.");
			}
		}
		Dialog_ShowCallback(playerid, using inline DeleteConfirm, DIALOG_STYLE_MSGBOX, "{FF0000}Dëmesio.", "Ar tikrai norite paðalinti ðá iðëjimà?", "Taip", "Ne", DIALOG_ENTRANCE_MENU_DELETE);
	}
	return 1;
}

EntranceManagementDialog.ChangeTextColour(playerid, entrindex, type = 0)
{

	if(!IsValidEntrance(entrindex))
		EntranceManagementDialog.ShowIndexInput(playerid, TextColour);
	else if(!type)
		EntranceManagementDialog.ShowTypeSelect(playerid, entrindex, TextColour);
	else 
	{
		SetPVarInt(playerid, "En.Index", entrindex);
		SetPVarInt(playerid, "En.Status", type);
		new string[ 1024 ];
		string = "Ávesti savo spalvà";
		for(new i = 0; i < sizeof TextColours; i++)
			format(string, sizeof(string), "%s\n%s", string, TextColours[ i ][ Name ]);

		ShowPlayerDialog(playerid, DIALOG_ENTRANCE_MENU_TEXT_COLOR, DIALOG_STYLE_LIST, "Teksto spalva", string, "Pasirinkti", "Iðeiti");
	}
	return 1;
}

EntranceManagementDialog.ChangeTextAlpha(playerid, entrindex, type = 0)
{
	if(!IsValidEntrance(entrindex))
		EntranceManagementDialog.ShowIndexInput(playerid, TextAlpha);
	else if(!type)
		EntranceManagementDialog.ShowTypeSelect(playerid, entrindex, TextAlpha);
	else 
	{
		SetPVarInt(playerid, "En.Index", entrindex);
		SetPVarInt(playerid, "En.Status", type);
		ShowPlayerDialog(playerid, DIALOG_ENTRANCE_MENU_ALPHA, DIALOG_STYLE_INPUT, "Teksto ryðkumas", "Áveskite teksto permatomumà iðreikðta skaièiumi nuo 1 iki 00.\nVisiðkai permatomas tekstas 1\nVisiðkai nepermatomas tekstas: 100", "Testi", "Iðeiti");
	}
	return 1;
}

EntranceManagementDialog.CustomColour(playerid, entrindex, type)
{
	SetPVarInt(playerid, "En.Index", entrindex);
	SetPVarInt(playerid, "En.Status", type);
	ShowPlayerDialog(playerid, DIALOG_ENTRANCE_MENU_COLOUR_CUS, DIALOG_STYLE_INPUT, "Teksto spalva", "Áraðykite formatu: RRGGBB\nSkaièiai gali bûti nuo 0 iki F", "Tæsti", "Atgal");
}