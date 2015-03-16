/*
	* Coordinates.p failas yra dalis LTRP modifikacijos. 
	* Failas atsakingas uþ koordinaèiø pakrovimà ið duomenø bazës ir jø administravimà bei priëjimà.
	* Kalbama apie ávairias koordinates kurios kitur netinka, pvz spawn, jail ir t.t. 
	*
	* Kodo autorius: Bebras.
	*
	*
*/


#include <YSI\y_hooks>

#define MAX_COORDINATE_KEY 				64
#define MAX_COORDINATE_COMMENT 			128

#define MAX_COORDINATES 				60

enum E_COORDINATE_DATA 
{
	Id,
	Float:PosX,
	Float:PosY,
	Float:PosZ,
	Float:PosAngle,
	Interior,
	VirtualWorld,
	Key[ MAX_COORDINATE_KEY ],
	Comment[ MAX_COORDINATE_COMMENT ]
};

static CoordinateData[ MAX_COORDINATES ][ E_COORDINATE_DATA ],
		LastCoordinateIndex = -1			// Mano paranoja dël greièio verèia sukurti mini-cachinima.
	;

public OnGameModeInit()
{
	#if defined coordinates_OnGameModeInit
		coordinates_OnGameModeInit();
	#endif

	new Cache:result = mysql_query(DbHandle, "SELECT * FROM coordinates"),
		ticks = GetTickCount();
	for(new i = 0; i < cache_get_row_count(); i++)
	{
		CoordinateData[ i ][ Id ] = cache_get_field_content_int(i, "id");
		CoordinateData[ i ][ PosX ] = cache_get_field_content_float(i, "x");
		CoordinateData[ i ][ PosY ] = cache_get_field_content_float(i, "y");
		CoordinateData[ i ][ PosZ ] = cache_get_field_content_float(i, "z");	
		CoordinateData[ i ][ PosAngle ] = cache_get_field_content_float(i, "angle");
		CoordinateData[ i ][ Interior ] = cache_get_field_content_int(i, "interior");
		CoordinateData[ i ][ VirtualWorld ] = cache_get_field_content_int(i, "virtual_world");
		cache_get_field_content(i, "key", CoordinateData[ i ][ Key ], DbHandle, MAX_COORDINATE_KEY);
		cache_get_field_content(i, "comment", CoordinateData[ i ][ Comment ], DbHandle, MAX_COORDINATE_COMMENT);
	}
	printf("[Load]Pakrautos %d koordinaèiø poros. Tai uþtruko %d MS", cache_get_row_count(), GetTickCount() - ticks);
	cache_delete(result);
	return 1;
}
#if defined _ALS_OnGameModeInit
	#undef OnGameModeInit
#else 
	#define _ALS_OnGameModeInit
#endif
#define OnGameModeInit coordinates_OnGameModeInit
#if defined coordinates_OnGameModeInit
	forward coordinates_OnGameModeInit();
#endif



static stock IsValidKey(key[])
{
	if(isnull(key))
		return false;

	for(new i = 0; i < MAX_COORDINATES; i++)
		if(CoordinateData[ i ][ Id ] && !strcmp(CoordinateData[ i ][ Key ], key))
			return true;
	return false;
}

static GetKeyIndex(key[])
{
	if(isnull(key))
		return -1;

	if(LastCoordinateIndex != -1 && !strcmp(CoordinateData[ LastCoordinateIndex ][ Key ], key))
		return LastCoordinateIndex;

	for(new i = 0; i < MAX_COORDINATES; i++)
		if(CoordinateData[ i ][ Id ] && !strcmp(CoordinateData[ i ][ Key ], key))
			return LastCoordinateIndex = i;

	printf("[ERROR]Coordinates.p : GetKeyIndex(%s). Key not found", key);
	return -1;
}

stock Data_GetCoordinates(key[], &Float:x, &Float:y, &Float:z)
{
	new index = GetKeyIndex(key);
	if(index == -1)
		return;

	x = CoordinateData[ index ][ PosX ];
	y = CoordinateData[ index ][ PosY ];
	z = CoordinateData[ index ][ PosZ ];
}

stock Data_GetInterior(key[])
{
	new index = GetKeyIndex(key);
	if(index == -1)
		return 0;
	else
		return CoordinateData[ index ][ Interior ];
}

stock Data_GetVirtualWorld(key[])
{
	new index = GetKeyIndex(key);
	if(index == -1)
		return 0;
	else
		return CoordinateData[ index ][ VirtualWorld ];
}



stock Data_SetPlayerLocation(playerid, key[])
{
	new Float:x, Float:y, Float:z;
	Data_GetCoordinates(key, x, y, z);
	SetPlayerVirtualWorld(playerid, Data_GetVirtualWorld(key));
	SetPlayerInterior(playerid, Data_GetInterior(key));
	return SetPlayerPos(playerid, x, y, z);
}

stock Data_IsPlayerInRangeOfCoords(playerid, Float:distance, key[])
{
	new Float:x, Float:y, Float:z;
	Data_GetCoordinates(key, x, y, z);
	return IsPlayerInRangeOfPoint(playerid, distance, x, y, z);
}


stock Data_SetPlayerCheckPointEx(playerid, checkpointid, key[], Float:size)
{
	// SetPlayerCheckPointEx NËRA native funkcija.
	new Float:x, Float:y, Float:z;
	Data_GetCoordinates(key, x, y, z);
	return SetPlayerCheckPointEx(playerid, checkpointid, x, y, z, size);
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


			               ,,             ,,                             
			`7MM"""Yb.     db           `7MM                             
			  MM    `Yb.                  MM                             
			  MM     `Mb `7MM   ,6"Yb.    MM  ,pW"Wq.   .P"Ybmmm ,pP"Ybd 
			  MM      MM   MM  8)   MM    MM 6W'   `Wb :MI  I8   8I   `" 
			  MM     ,MP   MM   ,pm9MM    MM 8M     M8  WmmmP"   `YMMMa. 
			  MM    ,dP'   MM  8M   MM    MM YA.   ,A9 8M        L.   I8 
			.JMMmmmdP'   .JMML.`Moo9^Yo..JMML.`Ybmd9'   YMMMMMb  M9mmmP' 
			                                           6'     dP         
			                                           Ybmmmd'           
*/