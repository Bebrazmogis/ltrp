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
	Float:X,
	Float:Y,
	Float:Z,
	Float:Angle,
	Interior,
	VirtualWorld,
	Key[ MAX_COORDINATE_KEY ],
	Comment[ MAX_COORDINATE_COMMENT ]
};

static CoordinateData[ MAX_COORDINATES ][ E_COORDINATE_DATA ],
		LastCoordinateIndex 			// Mano paranoja dël greièio verèia sukurti mini-cachinima.
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
		CoordinateData[ i ][ X ] = cache_get_field_content_float(i, "x");
		CoordinateData[ i ][ Y ] = cache_get_field_content_float(i, "y");
		CoordinateData[ i ][ Z ] = cache_get_field_content_float(i, "z");	
		CoordinateData[ i ][ Angle ] = cache_get_field_content_float(i, "angle");
		CoordinateData[ i ][ Interior ] = cache_get_field_content_int(i, "interior");
		CoordinateData[ i ][ VirtualWorld ] = cache_get_field_content_int(i, "virtual_world");
		cache_get_field_content(i, "key", CoordinateData[ i ][ Key ], DbHandle, MAX_COORDINATE_KEY);
		cache_get_field_content(i, "comment", CoordinateData[ i ][ Comment ], DbHandleb, MAX_COORDINATE_COMMENT);
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

	if(!strcmp(CoordinateData[ LastCoordinateIndex ][ Key ], key))
		return LastCoordinateIndex;

	for(new i = 0; i < MAX_COORDINATES; i++)
		if(CoordinateData[ i ][ Id ] && !strcmp(CoordinateData[ i ][ Key ], key))
			return LasstIndex = i;
	return -1;
}

stock Data_GetCoordinates(key[], &Float:x, &Float:y, &Float:z)
{
	new index = GetKeyIndex(key);
	if(index == -1)
		return;

	x = CoordinateData[ index ][ X ];
	y = CoordinateData[ index ][ Y ];
	z = CoordinateData[ index ][ Z ];
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