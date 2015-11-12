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

	#if defined coordinates_OnGameModeInit
		coordinates_OnGameModeInit();
	#endif
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

	ErrorLog("[ERROR]Coordinates.p : GetKeyIndex(%s). Key not found", key);
	return -1;
}
stock AFunction()
{
	return 1;
}

forward Data_GetCoordinates(key[], &Float:x, &Float:y, &Float:z);
public Data_GetCoordinates(key[], &Float:x, &Float:y, &Float:z)
{
	printf("Data_GetCoordinates");
	new index = GetKeyIndex(key);
	if(index == -1)
		return;

	x = CoordinateData[ index ][ PosX ];
	y = CoordinateData[ index ][ PosY ];
	z = CoordinateData[ index ][ PosZ ];
}

forward Data_GetInterior(key[]);
public Data_GetInterior(key[])
{
	new index = GetKeyIndex(key);
	if(index == -1)
		return 0;
	else
		return CoordinateData[ index ][ Interior ];
}

forward Data_GetVirtualWorld(key[]);
public Data_GetVirtualWorld(key[])
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


static UpdateCoordinateData(coordindex)
{
	new query[256];
	mysql_format(DbHandle, query, sizeof(query), "UPDATE coordinates SET x = %f, y = %f, z = %f, angle = %f, interior = %d, virtual_world = %d, `key` = '%s', comment = '%e' WHERE id = %d",
		CoordinateData[ coordindex ][ PosX ],
		CoordinateData[ coordindex ][ PosY ],
		CoordinateData[ coordindex ][ PosZ ],
		CoordinateData[ coordindex ][ PosAngle ],
		CoordinateData[ coordindex ][ Interior ],
		CoordinateData[ coordindex ][ VirtualWorld ],
		CoordinateData[ coordindex ][ Key ],
		CoordinateData[ coordindex ][ Comment ],
		CoordinateData[ coordindex ][ Id ]
	);
	return mysql_pquery(DbHandle, query);
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



#define CoordinateManagementDialog. 	CM_D_

#define DIALOG_COORD_MENU_INPUT_COMMENT 9801
#define DIALOG_COORD_MENU_MAIN			9800
#define DIALOG_COORD_MENU_INPUT_COORDS 	9802
#define DIALOG_COORD_MENU_INPUT_INT_VW 	9803
#define DIALOG_COORD_MENU_OPTIONS 		9004


static PlayerUsedCoordinateIndex[ MAX_PLAYERS ];


stock CoordinateManagementDialog.ShowMain(playerid)
{	
	new string[2048];
	for(new i = 0; i < sizeof(CoordinateData); i++)
	{
		if(isnull(CoordinateData[ i ][ Comment ]))
			format(string, sizeof(string), "%s%s\n",
				string, CoordinateData[ i ][ Key ]);
		else 
			format(string, sizeof(string), "%s%s\n",
				string, CoordinateData[ i ][ Comment ]);
	}
	ShowPlayerDialog(playerid, DIALOG_COORD_MENU_MAIN, DIALOG_STYLE_LIST, "Koordinaèiø valdymas", string, "Pasirinkti", "Iðeiti");
	return 1;
}

stock CoordinateManagementDialog.InputCoordinates(playerid, errostr[] = "")
{
	new Float:x, Float:y, Float:z, string[256];
	GetPlayerPos(playerid, x, y, z);
	SetPVarFloat(playerid, "Coords.X", x);
	SetPVarFloat(playerid, "Coords.Y", y);
	SetPVarFloat(playerid, "Coords.Z", z);
	format(string, sizeof(string), "{FF0000}%s\n{FFFFFF}Jûsø dabartinës koordinatës X:%f Y:%f Z:%f\nNorëdami naudoti jas raðykite \"Naudoti mano\"\n\nArba áveskit naujas formatu: x y z", errostr, x, y, z);
	ShowPlayerDialog(playerid, DIALOG_COORD_MENU_INPUT_COORDS, DIALOG_STYLE_INPUT, "Koordinatës", string, "Tæsti", "Iðeiti");
	return 1;
}

stock CoordinateManagementDialog.InputInteriorWorld(playerid, errostr[] = "")
{
	new string[170];
	format(string, sizeof(string), "{FF0000}%s\n{FFFFFF}Jûsø dabartinis interjeras: %d.\nJûsø dabartinis virtualus pasaulis: %d.\n\nÁveskite naujà interjerà ir virtualøjá pasaulá", 
		errostr, GetPlayerInterior(playerid), GetPlayerVirtualWorld(playerid));
	ShowPlayerDialog(playerid, DIALOG_COORD_MENU_INPUT_INT_VW, DIALOG_STYLE_INPUT, "Interjeras & virtualus pasaulis", string, "Tæsti", "Iðeiti");
	return 1;
}

stock CoordinateManagementDialog.InputComment(playerid, errostr[] = "")
{
	new string[180];
	format(string, sizeof(string), "{FF0000}%s\n{FFFFFF}Ðis tekstas skirti padëti skirti koordinaèiø paskirtá.\nÁveskite tai kas bus aiðku jums ir kitiems administratoriams.", errostr);
	ShowPlayerDialog(playerid, DIALOG_COORD_MENU_INPUT_COMMENT, DIALOG_STYLE_INPUT, "Komentaras", string, "Tæsti", "Iðeiti");
	return 1;
}

hook OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
	switch(dialogid)
	{
		case DIALOG_COORD_MENU_MAIN:
		{
			if(!response)
				return 1;

			PlayerUsedCoordinateIndex[ playerid ] = listitem;
			ShowPlayerDialog(playerid, DIALOG_COORD_MENU_OPTIONS, DIALOG_STYLE_LIST, "Pasirinkite veiksma", "Eiti á pozicijà\nKeisti pozicijà\nKeisti interjerà ir virtualø pasaulá\nKeisti komentarà", "Pasirinkti", "Iðeiti");
			return 1;	
		}
		case DIALOG_COORD_MENU_OPTIONS:
		{
			if(!response)
				return CoordinateManagementDialog.ShowMain(playerid);

			switch(listitem)
			{
				case 0: 
				{
					SetPlayerInterior(playerid, CoordinateData[ PlayerUsedCoordinateIndex[ playerid ] ][ Interior ]);
					SetPlayerPos(playerid, CoordinateData[ PlayerUsedCoordinateIndex[ playerid ] ][ PosX ],
						CoordinateData[ PlayerUsedCoordinateIndex[ playerid ] ][ PosY ],
						CoordinateData[ PlayerUsedCoordinateIndex[ playerid ] ][ PosZ ]);
					SetPlayerVirtualWorld(playerid, CoordinateData[ PlayerUsedCoordinateIndex[ playerid ] ][ VirtualWorld ]);
				}
				// Keisti pozicijà
				case 1:
				{
					CoordinateManagementDialog.InputCoordinates(playerid);
				}
				// Keisti interjerà ir virtualø pasaulá
				case 2:
				{
					CoordinateManagementDialog.InputInteriorWorld(playerid);
				}
				// Keisti komentarà
				case 3:
				{
					CoordinateManagementDialog.InputComment(playerid);
				}
			}
			return 1;
		}
		case DIALOG_COORD_MENU_INPUT_COORDS:
		{
			if(!response)
				return CoordinateManagementDialog.ShowMain(playerid);

			
			new Float:x, Float:y, Float:z;
			if(!isnull(inputtext) && !strcmp(inputtext, "Naudoti mano", true))
			{
				x = GetPVarFloat(playerid, "Coords.X");
				y = GetPVarFloat(playerid, "Coords.Y");
				z = GetPVarFloat(playerid, "Coords.Z");
			}
			else if(sscanf(inputtext, "fff", x, y, z))
				return CoordinateManagementDialog.InputCoordinates(playerid, "Áveskite tris skaièius.");

			CoordinateData[ PlayerUsedCoordinateIndex[ playerid ] ][ PosX ] = x;
			CoordinateData[ PlayerUsedCoordinateIndex[ playerid ] ][ PosY ] = y;
			CoordinateData[ PlayerUsedCoordinateIndex[ playerid ] ][ PosZ ] = z;
			UpdateCoordinateData(PlayerUsedCoordinateIndex[ playerid ]);
			SendClientMessage(playerid, COLOR_NEWS, "Koordinatës sëkmingai atnaujintos.");
			return 1;
		}
		case DIALOG_COORD_MENU_INPUT_INT_VW:
		{
			if(!response)
				return CoordinateManagementDialog.ShowMain(playerid);

			new interior, world;
			if(sscanf(inputtext, "ii", interior, world))
				return CoordinateManagementDialog.InputInteriorWorld(playerid, "Ávedimo formatas: [Intereras ] [Virtualus pasaulis]");

			if(interior < 0 || world < 0)
				return CoordinateManagementDialog.InputInteriorWorld(playerid, "Interjeras ir virtualus pasaulis negali bûti neigiami.");

			CoordinateData[ PlayerUsedCoordinateIndex[ playerid ] ][ Interior ] = interior;
			CoordinateData[ PlayerUsedCoordinateIndex[ playerid ] ][ VirtualWorld ] = world;
			UpdateCoordinateData(PlayerUsedCoordinateIndex[ playerid ]);
			SendClientMessage(playerid, COLOR_NEWS, "Duomenys sëkmingai atnaujinti.");
			return 1;
		}
		case DIALOG_COORD_MENU_INPUT_COMMENT:
		{
			if(!response)
				return CoordinateManagementDialog.ShowMain(playerid);

			if(isnull(inputtext) || strlen(inputtext) >= MAX_COORDINATE_COMMENT)
				return CoordinateManagementDialog.InputComment(playerid, "Komentarà gali sudaryti nuo 1 iki " #MAX_COORDINATE_COMMENT " simboliø.");

			format(CoordinateData[ PlayerUsedCoordinateIndex[ playerid ] ][ Comment ], MAX_COORDINATE_COMMENT, inputtext);
			UpdateCoordinateData(PlayerUsedCoordinateIndex[ playerid ]);
			SendClientMessage(playerid, COLOR_NEWS, "Komentaras sëkmingai atnaujintas.");
			return 1;
		}
	}
	return 0;
}