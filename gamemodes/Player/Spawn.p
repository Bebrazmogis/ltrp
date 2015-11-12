/*
	* Death.p failas yra dalis LTRP modifikacijos. 
	* Failas atsakingas uþ:
	* 	• numatytàsias(default) reikðmes ávairiems atsiradimo parametrams,
	* 	• minëtø reikðmiø keitimo galimybæ ið kitø moduliø
	* 	• spawn vietos keitimo komandà
	*	• papildomø spawn vietø krovimà bei tvarkymà
	*
	*
	* Kiti moduliai keisti reikðmes gali hook'indami pateiktas funkcijas
	*
	* Kodo autorius: Bebras.
	*
	*
*/

#define MAX_SPAWN_NAME 					24
#define MAX_SPAWNS						5

enum E_SPAWN_DATA 
{
	Id,
	Name[ MAX_SPAWN_NAME ],
	Float:X,
	Float:Y,
	Float:Z,
	Float:R,
	Interior,
	VirtualWorld,
};

enum E_PLAYER_SPAWN_LOCATIONS 
{
	DefaultSpawn, 
	SpawnFaction,
	SpawnHouse,
	SpawnBusiness,
	SpawnGarage,
	SpawnList,
};

new static 
	E_PLAYER_SPAWN_LOCATIONS:PlayerSpawnLocation[ MAX_PLAYERS ],
	PlayerSpawnUI[ MAX_PLAYERS ],
	Spawns[ MAX_SPAWNS ][ E_SPAWN_DATA ],
	bool:SpawnLoaded = false,
	SpawnCount
	;



/*
	CREATE TABLE IF NOT EXISTS spawns 
	(
		id INT NOT NULL AUTO_INCREMENT,
		name VARCHAR(60) NOT NULL,
		x FLOAT NOT NULL,
		y FLOAT NOT NULL,
		z FLOAT NOT NULL,
		rotation FLOAT NOT NULL,
		interior SMALLINT UNSIGNED NOT NULL,
		virtual_world INT UNSIGNED NOT NULL,
		PRIMARY KEY(id)
	) ENGINE=INNODB DEFAULT COLLATE=cp1257_bin CHARSET=cp1257;
*/
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
	return mysql_pquery(DbHandle, "SELECT * FROM spawns", "OnSpawnLoad", "");
}

forward OnSpawnLoad();
public OnSpawnLoad()
{
	for(new i = 0; i < cache_get_row_count(); i++)
	{
		Spawns[ i ][ Id ] = cache_get_field_content_int(i, "id");
		cache_get_field_content(i, "name", Spawns[ i ][ Name ], DbHandle, MAX_SPAWN_NAME);
		Spawns[ i ][ X ] = cache_get_field_content_float(i, "x");
		Spawns[ i ][ Y ] = cache_get_field_content_float(i, "y");
		Spawns[ i ][ Z ] = cache_get_field_content_float(i, "z");
		Spawns[ i ][ R ] = cache_get_field_content_float(i, "rotation");
		Spawns[ i ][ Interior ] = cache_get_field_content_int(i, "interior");
		Spawns[ i ][ VirtualWorld ] = cache_get_field_content_int(i, "virtual_world");
		SpawnCount++;
	}
	SpawnLoaded = true;
	return 1;
}

hook OnPlayerConnect(playerid)
{
	SetUpSpawn(playerid);
	return 1;
}

hook OnPlayerDisconnect(playerid, reason)
{
	PlayerSpawnLocation[ playerid ] = DefaultSpawn;
	PlayerSpawnUI = 0;
}

hook OnPlayerRequestClass(playerid, classid)
{
	SpawnPlayer(playerid);
	return 1;
}





/*

						                                          
						                               ,,         
						 .M"""bgd mm            mm     db         
						,MI    "Y MM            MM                
						`MMb.   mmMMmm  ,6"Yb.mmMMmm `7MM  ,p6"bo 
						  `YMMNq. MM   8)   MM  MM     MM 6M'  OO 
						.     `MM MM    ,pm9MM  MM     MM 8M      
						Mb     dM MM   8M   MM  MM     MM YM.    ,
						P"Ybmmd"  `Mbmo`Moo9^Yo.`Mbmo.JMML.YMbmd' 


                                                      ,,                             
     `7MM"""YMM                                mm     db                             
       MM    `7                                MM                                    
       MM   d `7MM  `7MM  `7MMpMMMb.  ,p6"bo mmMMmm `7MM  ,pW"Wq.`7MMpMMMb.  ,pP"Ybd 
       MM""MM   MM    MM    MM    MM 6M'  OO   MM     MM 6W'   `Wb MM    MM  8I   `" 
       MM   Y   MM    MM    MM    MM 8M        MM     MM 8M     M8 MM    MM  `YMMMa. 
       MM       MM    MM    MM    MM YM.    ,  MM     MM YA.   ,A9 MM    MM  L.   I8 
     .JMML.     `Mbod"YML..JMML  JMML.YMbmd'   `Mbmo.JMML.`Ybmd9'.JMML  JMML.M9mmmP' 
                                                                                     
						                                          

		                                                                                
*/

SetUpSpawn(playerid)
{
	// Load values, provide defaults if neccesseray
	new query[80], Cache:result, skin, index = -1,
		Float:x, Float:y, Float:z, Float:rotation, virtualworld, interior;
	mysql_format(DbHandle, query, sizeof query, "SELECT spawn_type, spawn_ui, skin FROM players WHERE id = %d", GetPlayerSqlId(playerid));
	result = mysql_query(DbHandle, query);
	if(cache_get_row_count())
	{
		PlayerSpawnLocation[ playerid ] = E_PLAYER_SPAWN_LOCATIONS:cache_get_field_content_int(0, "spawn_type");
		PlayerSpawnUI[ playerid ] = cache_get_field_content_int(0, "spawn_ui");
		skin = cache_get_field_content_int(0, "skin");


		switch(pInfo[ playerid ][ pSpawn ])
        {
            case SpawnHouse:
            {
                index = GetHouseIndex(PlayerSpawnUI[ playerid ]);
                if(index != -1)
                {
                	GetHouseEntrancePos(index, x, y, z);
                	virtualworld = GetHouseEntranceVirtualWorld(index);
                	interior = GetHouseEntranceInteriorID(index);
                }
                else ErrorLog("Player %d has spawn set to house %d, but house does not exists or is not loaded.", GetPlayerSqlId(playerid), PlayerSpawnUI[ playerid ]);
            }
            case SpawnFaction:
            {

            	SetSpawnInfo( playerid, 0, pInfo[ playerid ][ pSkin ], fInfo[ PlayerFaction( playerid ) ][ fSpawn ][ 0 ],fInfo[ PlayerFaction( playerid ) ][ fSpawn ][ 1 ],fInfo[ PlayerFaction( playerid ) ][ fSpawn ][ 2 ], 0, 0, 0, 0, 0, 0, 0 );

            }
            case SpawnBusiness:
            {
                index = GetBusinessIndex(PlayerSpawnUI[ playerid ]);
                if(index != -1)
                {
                    GetBusinessEntrancePos(index, x, y, z);
                	virtualworld = GetBusinessEntranceVirtualWorld(index);
                	interior = GetBusinessEntranceVirtualWorld(index);
                }
                else ErrorLog("Player %d has spawn set to business %d, but business does not exists or ir is not loaded", GetPlayerSqlId(playerid), PlayerSpawnUI[ playerid ]);
            }
            case SpawnGarage:
            {
                new index = GetGarageIndex(pInfo[ playerid ][ pBSpawn ]);
                if(index != -1)
                {
                    GetGarageEntrancePos(index, x, y, z);
                    virtualworld = GetGarageEntranceVirtualWorld(index);
                    interior = GetGarageEntranceInteriorID(index);    
                }
                else ErrorLog("Player %d has spawn set to garage %d, but garage does not exists or ir is not loaded", GetPlayerSqlId(playerid), PlayerSpawnUI[ playerid ]);
            }
            case SpawnList:
            {
            	index = GetSpawnIndex(PlayerSpawnUI[ playerid ]);
            	if(index != -1)
            	{
            		GetSpawnPos(index, x, y, z);
            		interior = GetSpawnInterior(index);
            		virtualworld = GetSpawnVirtualWorld(index);
            	}
            	else ErrorLog("Player %d has spawn set to spawn %d, but spawn does not exists or ir is not loaded", GetPlayerSqlId(playerid), PlayerSpawnUI[ playerid ]);
            }
            default:
            {
                Data_GetCoordinates("default_spawn", x, y, z);
               	interior = Data_GetInterior("default_spawn"));
                virtualworld = Data_GetVirtualWorld("default_spawn"));
            }
        }
        SetSpawnInfo(playerid, NO_TEAM, skin, x, y, z, 0, 0, 0, 0, 0, 0);
        SetPlayerInterior(playerid, interior);
        SetPlayerVirtualWorld(playerid, virtualworld);
	}
	cache_delete(result);
	return 1;
}

/*
				                                                                                                                          
				                                                 ,...                                      ,,                             
				          mm                   `7MM            .d' ""                               mm     db                             
				          MM                     MM            dM`                                  MM                                    
				,pP"Ybd mmMMmm ,pW"Wq.   ,p6"bo  MM  ,MP'     mMMmm`7MM  `7MM  `7MMpMMMb.  ,p6"bo mmMMmm `7MM  ,pW"Wq.`7MMpMMMb.  ,pP"Ybd 
				8I   `"   MM  6W'   `Wb 6M'  OO  MM ;Y         MM    MM    MM    MM    MM 6M'  OO   MM     MM 6W'   `Wb MM    MM  8I   `" 
				`YMMMa.   MM  8M     M8 8M       MM;Mm         MM    MM    MM    MM    MM 8M        MM     MM 8M     M8 MM    MM  `YMMMa. 
				L.   I8   MM  YA.   ,A9 YM.    , MM `Mb.       MM    MM    MM    MM    MM YM.    ,  MM     MM YA.   ,A9 MM    MM  L.   I8 
				M9mmmP'   `Mbmo`Ybmd9'   YMbmd'.JMML. YA.    .JMML.  `Mbod"YML..JMML  JMML.YMbmd'   `Mbmo.JMML.`Ybmd9'.JMML  JMML.M9mmmP' 
				                                                                                                                          
				                                                                                                                          
*/


stock GetSpawnIndex(spawnid)
{
	for(new i = 0; i < MAX_SPAWNS; i++)
		if(IsValidSpawn(i))
			if(Spawns[ i ][ Id ] == spawnid)
				return i;
	return -1;
}

stock IsValidSpawn(spawnindex)
{
	if(0 > spawnindex >= MAX_SPAWNS)
		return false;

	if(!Spawns[ spawnindex ][ Id ])
		return false;

	return true;
}

stock GetSpawnPos(spawnindex, &Float:x, &Float:y, &Float:z)
{
	if(IsValidSpawn(spawnindex))
	{
		x = Spawns[ spawnindex ][ X ];
		y = Spawns[ spawnindex ][ Y ];
		z = Spawns[ spawnindex ][ Z ];
	}
}

stock GetSpawnInterior(spawnindex)
{
	if(IsValidSpawn(spawnindex))
	{
		return Spawns[ spawnindex ][ Interior ];
	}
}

stock GetSpawnVirtualWorld(spawnindex)
{
	if(IsValidSpawn(spawnindex))
	{
		return Spawns[ spawnindex ][ VirtualWorld ];
	}
}


stock SavePlayerSpawnData(playerid)
{
	new query[130];
	mysql_format(DbHandle, query, sizeof query, "UPDATE players SET spawn_type = %d, spawn_ui = %d, skin = %d WHERE id = %d", 
		_:PlayerSpawnLocation[ playerid ], PlayerSpawnUI[ playerid ], GetPlayerSkin(playerid), GetPlayerSqlId(playerid));
	return mysql_pquery(DbHandle, query);
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



CMD:setspawn (playerid, params[])
{
	new string[100];
    if(isnull(params))
    {
        SetSpawnInfo:
        SendClientMessage(playerid, COLOR_LIGHTRED, "|________VEIKËJO ATSIRADIMO VIETA PRISIJUNGUS________|");
        SendClientMessage(playerid, COLOR_LIGHTRED2, "Atsiradimo vietà taip pat galite redaguoti vartotojo valdymo pulte: ltrp.lt");
        SendClientMessage(playerid, COLOR_WHITE, "KOMANDOS NAUDOJIMAS: /setspawn [VIETA]");
        SendClientMessage(playerid, COLOR_WHITE, "VIETOS: Numatytasis, Namas, Frakcija, Verslas, Garaþas");
        string = "VIETOS";
        for(new i = 0; i < SpawnCount; i++)
        	if(!isnull(Spawns[ i ][ Name ]))
        	{
        		format(string, sizeof(string), "%s, %s", string, Spawns[ i ][ Name ]);
        		if(strlen(string) > 60)
        		{
        			SendClientMessage(playerid, COLOR_WHITE, string);
        			string = "VIETOS";
        		}
        	}
        if(strlen(string))
        	SendClientMessage(playerid, COLOR_WHITE, string);

        SendClientMessage(playerid, COLOR_LIGHTRED2, "_______________________________");
    }
    else
    {
        if(!strcmp(params, "Numatytasis", true))
        {
            PlayerSpawnLocation[ playerid ] = DefaultSpawn;
            SendClientMessage( playerid, COLOR_NEWS,"Vieta sëkmingai nustatyta, dabar prisijungæ á serverá kità kartà atsirasite Idlewood rajone..");
        }
        else if(!strcmp(params, "Namas", true))
        {
            new index = GetPlayerHouseIndex(playerid, true);
            if(index == -1)
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, turite stovëti prie namo kurá norite pasirinkti kaip atsiradimo vietà.");

            if(!IsPlayerHouseOwner(playerid, index) && !IsPlayerHouseTenant(playerid, index))
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, turite bûti namo savininkas arba já nuomotis kad galëtumëte já pasirinkti kaip atsiradimo vietà.");

            PlayerSpawnUI[ playerid ] = GetHouseID(index);
            PlayerSpawnLocation[ playerid ] = SpawnHouse;
            SendClientMessage(playerid, COLOR_NEWS,"Vieta sëkmingai nustatyta, dabar prisijungæ á serverá kità kartà atsirasite ðalia savo namo.");
        }
        else if(!strcmp(params, "Frakcija", true))
        {
            if( PlayerFaction( playerid ) > 0 )
            {
                pInfo[ playerid ][ pSpawn ] = SpawnFaction;
                SendClientMessage( playerid, COLOR_NEWS,"Vieta sëkmingai nustatyta, dabar prisijungæ á serverá kità kartà atsirasite frakcijos nustatytoje atsiradimo vietoje.");
            }
            else
                SendClientMessage( playerid, COLOR_LIGHTRED,"Klaida, Jûs nepriklausote jokiai frakcijai. Pasitikrinkite veikëjo informacija komanda /stats.");

        }
        else if(!strcmp(params, "Verslas", true))
        {
            new index = GetPlayerBusinessIndex(playerid);
            if(index == -1)
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Turite stovëti prie verslo kurá norite pasirinkti kaip atsiradimo vietà.");
           	if(!IsPlayerBusinessOwner(playerid, index))
        		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðis verslas jums nepriklauso.");

            PlayerSpawnUI[ playerid ] = GetBusinessID(index);
            PlayerSpawnLocation[ playerid ] = SpawnBusiness;
            SendClientMessage( playerid, COLOR_NEWS,"Vieta sëkmingai nustatyta, dabar prisijungæ á serverá kità kartà atsirasite ðalia savo verslo.");
        }
        else if(!strcmp(params, "Garazas", true) || !strcmp(params, "Garaþas", true))
        {
        	new index = GetPlayerGarageIndex(playerid);
        	if(index == -1)
        		return SendClientMessage(playerid, COLOR_LIGHTRED, "Turite stovëti prie garaþo prie kurio norite atsirasti.");
        	if(!IsPlayerGarageOwner(playerid, index))
        		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðis garaþas jums nepriklauso.");

        	PlayerSpawnUI[ playerid ] = GetGarageID(index);
        	PlayerSpawnLocation[ playerid ] = SpawnGarage;
        	SendClientMessage(playerid, COLOR_NEWS, "Vieta sëkmingai pakeista. Kità kartà prisijungæ á serverá atsirasite prie garaþo.");
        }
        else 
        {
        	new bool:spawnfound = false;
        	for(new i = 0; i < SpawnCount; i++)
        		if(!isnull(Spawns[ i ][ Name ]) && !strcmp(Spawns[ i ][ Name ], params, true))
        		{
        			format(string, sizeof(string), "Vieta sëkmingai pakeista. Kità kartà prisijungæ á serverá atsirasite prie %s", Spawns[ i ][ Name ]);
        			SendClientMessage(playerid, COLOR_NEWS, string);

        			PlayerSpawnUI[ playerid ] = Spawns[ i ][ Id ];
        			PlayerSpawnLocation[ playerid ] = SpawnList;

        			spawnfound = true;
        			break;
        		}

        	if(!spawnfound)
        		goto SetSpawnInfo;
        }
        SavePlayerSpawnData(playerid);
    }
    return true;
}