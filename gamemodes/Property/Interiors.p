


#include <YSI\y_hooks>

#define MAX_INTERIORS					200
#define INVALID_INTERIOR_ID             0
#define MAX_INTERIOR_CATEGORY_NAME 		32
#define MAX_INTERIOR_CATEGORIES 		10


enum E_INTERIOR_CATEGORY_DATA 
{
	Id,
	Name[ MAX_INTERIOR_CATEGORY_NAME ]
};

new static InteriorCategories[ MAX_INTERIOR_CATEGORIES ][ E_INTERIOR_CATEGORY_DATA ];

enum E_INTERIOR_DATA {
	Id,
	Float:EnX,
	Float:EnY, 
	Float:EnZ,
	Interior,
	VirtualWorld,
	AreaId,
	CategoryIndex
};


new static InteriorData[ MAX_INTERIORS ][ E_INTERIOR_DATA ];


new bool:IsPlayerInPreview[ MAX_PLAYERS ], PlayerPreviewCategoryIndex[ MAX_PLAYERS ], PlayerPreviewCurrentInterior[ MAX_PLAYERS ];



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
	#if defined interiors_OnGameModeInit
		interiors_OnGameModeInit();
	#endif

	for(new i = 0; i < sizeof(InteriorData); i++)
		InteriorData[ i ][ CategoryIndex ] = -1;

	new
		Float:centerx, Float:centery, Float:centerz,
		Float:width, Float:length, Float:height,
		category[ MAX_INTERIOR_CATEGORY_NAME ],
		categoryIDs = 1,
		Cache:result;

	result = mysql_query(DbHandle, "SELECT * FROM interiors");
	for(new i = 0; i < cache_get_row_count(); i++)
	{
		if(i >= MAX_INTERIORS)
		{
			printf("KLAIDA. 'interiors' lenteleje interjeru skaicius(%d) virsija leistina(%d)", cache_get_row_count(), sizeof InteriorData);
			break;
		}

		InteriorData[ i ][ Id ] = cache_get_field_content_int(i, "id");
		InteriorData[ i ][ EnX ] = cache_get_field_content_float(i, "x");
		InteriorData[ i ][ EnY ] = cache_get_field_content_float(i, "y");
		InteriorData[ i ][ EnZ ] = cache_get_field_content_float(i, "z");
		centerx = cache_get_field_content_float(i, "center_x");
		centery = cache_get_field_content_float(i, "center_y");
		centerz = cache_get_field_content_float(i, "center_z");
		InteriorData[ i ][ Interior ] = cache_get_field_content_int(i, "interior");
		InteriorData[ i ][ VirtualWorld ] = cache_get_field_content_int(i, "virtual_world");
		width = cache_get_field_content_float(i, "width");
		length = cache_get_field_content_float(i, "length");
		height = cache_get_field_content_float(i, "height");
		cache_get_field_content(i, "category", category);

		InteriorData[ i ][ AreaId ] = CreateDynamicCuboid(centerx - width / 2, 
			centery - length / 2, centerz - height / 2, 
			centerx + width / 2, centery + length / 2, 
			centerz + height / 2, 
			-1, 
			InteriorData[ i ][ Interior ]);


		// Susitvarkom kategorija
		new freeCategoryIndex = -1, bool:found = false;
		for(new j = 0; j < MAX_INTERIOR_CATEGORIES; j++)
		{
			if(!InteriorCategories[ j ][ Id ] && freeCategoryIndex == -1)
				freeCategoryIndex = j;
			
			if(isnull(InteriorCategories[ j ][ Name ]))
				continue;

			else if(!strcmp(InteriorCategories[ j ][ Name ], category))
			{
				InteriorData[ i ][ CategoryIndex ] = j;
				found = true;
				break;
			}
		}
		// Kategorija nebuvo rasta, mes jà sukursime.
		if(!found)
		{
			InteriorCategories[ freeCategoryIndex ][ Id ] = categoryIDs++;
			InteriorCategories[ freeCategoryIndex ][ Name ] = category;
			InteriorData[ i ][ CategoryIndex ] = freeCategoryIndex;
		}
	}
	printf("Serveryje yra sukurti %d interjerai", cache_get_row_count());
	cache_delete(result);
}
#if defined _ALS_OnGameModeInit
	#undef OnGameModeInit
#else 
	#define _ALS_OnGameModeInit
#endif
#define OnGameModeInit interiors_OnGameModeInit
#if defined interiors_OnGameModeInit
	forward interiors_OnGameModeInit();
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

stock GetInteriorEntrancePos(id, &Float:x, &Float:y, &Float:z)
{
	for(new i = 0; i < sizeof InteriorData; i++)
	{
		if(InteriorData[ i ][ Id ] != id)
			continue;
		x = InteriorData[ i ][ EnX ];
		y = InteriorData[ i ][ EnY ];
		z = InteriorData[ i ][ EnZ ];
		break;
	}
}

stock GetInteriorInteriorId(id)
{
	for(new i = 0; i < sizeof InteriorData; i++)
	{
		if(InteriorData[ i ][ Id ] != id)
			continue;
		return InteriorData[ i ][ Interior ];
	}
	return 0;
}
stock GetInteriorVirtualWorld(id)
{
	for(new i = 0; i < sizeof InteriorData; i++)
	{
		if(InteriorData[ i ][ Id ] != id)
			continue;
		return InteriorData[ i ][ VirtualWorld ];
	}
	return 0;
}

stock GetPlayerInteriorId(playerid)
{
	for(new i = 0; i < sizeof InteriorData; i++)
		if(IsPlayerInDynamicArea(playerid, InteriorData[ i ][ AreaId ]))
			return InteriorData[ i ][ Id ];
	return 0;
}

stock IsValidInterior(id)
{
	if(id == INVALID_INTERIOR_ID)
		return false;
	for(new i = 0; i < sizeof(InteriorData); i++)
		if(InteriorData[ i ][ Id ] == id)
			return true;
	return false;
}


stock IsPlayerInInterior(playerid, id)
{
	new Float:x, Float:y, Float:z;
	GetPlayerPos(playerid, x, y, z);
	for(new i = 0; i < sizeof InteriorData; i++)
	{
		if(InteriorData[ i ][ Id ] != id)
			continue;
		return IsPlayerInDynamicArea(playerid, InteriorData[ i ][ AreaId ]);
	}
	return false;
}

stock IsPointInInterior(id, Float:x, Float:y, Float:z)
{
	for(new i = 0; i < sizeof InteriorData; i++)
	{
		if(InteriorData[ i ][ Id ] != id)
			continue;
		return IsPointInDynamicArea(InteriorData[ i ][ AreaId ], x, y, z);
	}
	return false;
}

stock IsPlayerInAnyInterior(playerid)
{
	for(new i = 0; i < sizeof(InteriorData); i++)
		if(IsPlayerInDynamicArea(playerid, InteriorData[ i ][ AreaId ]))
			return true;
	return false;
}

/*
 		 ______              __                          __                                                                     __                               
 		/      |            /  |                        /  |                                                                   /  |                              
 		$$$$$$/  _______   _$$ |_     ______    ______  $$/   ______    ______          ______    ______    ______   __     __ $$/   ______   __   __   __       
 		  $$ |  /       \ / $$   |   /      \  /      \ /  | /      \  /      \        /      \  /      \  /      \ /  \   /  |/  | /      \ /  | /  | /  |      
 		  $$ |  $$$$$$$  |$$$$$$/   /$$$$$$  |/$$$$$$  |$$ |/$$$$$$  |/$$$$$$  |      /$$$$$$  |/$$$$$$  |/$$$$$$  |$$  \ /$$/ $$ |/$$$$$$  |$$ | $$ | $$ |      
 		  $$ |  $$ |  $$ |  $$ | __ $$    $$ |$$ |  $$/ $$ |$$ |  $$ |$$ |  $$/       $$ |  $$ |$$ |  $$/ $$    $$ | $$  /$$/  $$ |$$    $$ |$$ | $$ | $$ |      
 		 _$$ |_ $$ |  $$ |  $$ |/  |$$$$$$$$/ $$ |      $$ |$$ \__$$ |$$ |            $$ |__$$ |$$ |      $$$$$$$$/   $$ $$/   $$ |$$$$$$$$/ $$ \_$$ \_$$ |      
 		/ $$   |$$ |  $$ |  $$  $$/ $$       |$$ |      $$ |$$    $$/ $$ |            $$    $$/ $$ |      $$       |   $$$/    $$ |$$       |$$   $$   $$/       
 		$$$$$$/ $$/   $$/    $$$$/   $$$$$$$/ $$/       $$/  $$$$$$/  $$/             $$$$$$$/  $$/        $$$$$$$/     $/     $$/  $$$$$$$/  $$$$$/$$$$/        
 		                                                                              $$ |                                                                       
 		                                                                              $$ |                                                                       
 		                                                                              $$/                                                                        
*/



hook OnPlayerKeyStateChange(playerid, newkeys, oldkeys)
{
	if(!IsPlayerInPreview[ playerid ])
		return 0;

	new interiorIndex, string[128];
	if((newkeys & KEY_ANALOG_LEFT) && !(oldkeys & KEY_ANALOG_LEFT))
	{
		interiorIndex = GetPreviousInteriorInCategory(PlayerPreviewCategoryIndex[ playerid ], PlayerPreviewCurrentInterior[ playerid ]);
		if(interiorIndex == -1)
			return SendClientMessage(playerid, -1, "Nebëra interjerø.");

		PlayerPreviewCurrentInterior[ playerid ] = interiorIndex;

		SetPlayerPos(playerid, InteriorData[ interiorIndex ][ EnX ],
								InteriorData[ interiorIndex ][ EnY ],
								InteriorData[ interiorIndex ][ EnZ ]);
		SetPlayerInterior(playerid, InteriorData[ interiorIndex ][ Interior ]);
		SetPlayerVirtualWorld(playerid, InteriorData[ interiorIndex ][ VirtualWorld ]);
		format(string, sizeof(string),"Dabartinio interjero ID: %d Pasirinkti sekantá ar praeità galite su NUM 4 ar NUM 6, iðeiti galite paraðæ /stoppreview", InteriorData[ PlayerPreviewCurrentInterior[ playerid ] ][ Id ]);
		SendClientMessage(playerid, -1, string);
		return 1;
	}
	if((newkeys & KEY_ANALOG_RIGHT) && !(oldkeys & KEY_ANALOG_RIGHT))
	{
		interiorIndex = GetNextInteriorInCategory(PlayerPreviewCategoryIndex[ playerid ], PlayerPreviewCurrentInterior[ playerid ]);
		if(interiorIndex == -1)
			return SendClientMessage(playerid, -1, "Nebëra interjerø.");

		PlayerPreviewCurrentInterior[ playerid ] = interiorIndex;

		SetPlayerPos(playerid, InteriorData[ interiorIndex ][ EnX ],
								InteriorData[ interiorIndex ][ EnY ],
								InteriorData[ interiorIndex ][ EnZ ]);
		SetPlayerInterior(playerid, InteriorData[ interiorIndex ][ Interior ]);
		SetPlayerVirtualWorld(playerid, InteriorData[ interiorIndex ][ VirtualWorld ]);
		format(string, sizeof(string),"Dabartinio interjero ID: %d Pasirinkti sekantá ar praeità galite su NUM 4 ar NUM 6, iðeiti galite paraðæ /stoppreview", InteriorData[ PlayerPreviewCurrentInterior[ playerid ] ][ Id ]);
		SendClientMessage(playerid, -1, string);
		return 1;
	}
	return 1;
}

hook OnPlayerDisconnect(playerid, reason)
{
	IsPlayerInPreview[ playerid ] = false;
}

stock EndInteriorPreviewForPlayer(playerid)
{
	SetPlayerPos(playerid, 
		GetPVarFloat(playerid, "PreviewStartX"),
		GetPVarFloat(playerid, "PreviewStartY"),
		GetPVarFloat(playerid, "PreviewStartZ")
	);
	printf("EndInteriorPreviewForPlayer: startx:%f",GetPVarFloat(playerid, "PreviewStartX"));
	SetPlayerInterior(playerid, GetPVarInt(playerid, "PreviewStartInterior"));
	SetPlayerVirtualWorld(playerid, GetPVarInt(playerid, "PreviewStartWorld"));

	DeletePVar(playerid, "PreviewStartX");
	DeletePVar(playerid, "PreviewStartY");
	DeletePVar(playerid, "PreviewStartZ");
	DeletePVar(playerid, "PreviewStartInterior");
	DeletePVar(playerid, "PreviewStartWorld");

	IsPlayerInPreview[ playerid ] = false;
	return 1;
}

stock ShowInteriorPreviewForPlayer(playerid, category[] = "")
{
	new Float:x, Float:y, Float:z, string[128];
	GetPlayerPos(playerid, x, y, z);

	SetPVarFloat(playerid, "PreviewStartX", x);
	SetPVarFloat(playerid, "PreviewStartY", y);
	SetPVarFloat(playerid, "PreviewStartZ", z);
	SetPVarInt(playerid, "PreviewStartWorld", GetPlayerVirtualWorld(playerid));
	SetPVarInt(playerid, "PreviewStartInterior", GetPlayerInterior(playerid));

	IsPlayerInPreview[ playerid ] = true;
	PlayerPreviewCategoryIndex[ playerid ] = -1;

	if(!isnull(category))
	{
		for(new i = 0; i < sizeof(InteriorCategories); i++)
			if(!strcmp(InteriorCategories[ i ][ Name ], category))
			{
				PlayerPreviewCategoryIndex[ playerid ] = i;
				break;
			}
		new interiorIndex = GetNextInteriorInCategory(PlayerPreviewCategoryIndex[ playerid ]);
		PlayerPreviewCurrentInterior[ playerid ] = interiorIndex;
		SetPlayerPos(playerid, InteriorData[ interiorIndex ][ EnX ],
								InteriorData[ interiorIndex ][ EnY ],
								InteriorData[ interiorIndex ][ EnZ ]);
		SetPlayerInterior(playerid, InteriorData[ interiorIndex ][ Interior ]);
		SetPlayerVirtualWorld(playerid, InteriorData[ interiorIndex ][ VirtualWorld ]);
	}
	else 
	{
		PlayerPreviewCurrentInterior[ playerid ] = 0;
		SetPlayerPos(playerid, InteriorData[ 0 ][ EnX ],
								InteriorData[ 0 ][ EnY ],
								InteriorData[ 0 ][ EnZ ]);
		SetPlayerInterior(playerid, InteriorData[ 0 ][ Interior ]);
		SetPlayerVirtualWorld(playerid, InteriorData[ 0 ][ VirtualWorld ]);
	}
	format(string, sizeof(string),"Dabartinio interjero ID: %d Pasirinkti sekantá ar praeità galite su NUM 4 ar NUM 6, iðeiti galite paraðæ /stoppreview", InteriorData[ PlayerPreviewCurrentInterior[ playerid ] ][ Id ]);
	SendClientMessage(playerid, -1, string);
	return 1;
}



stock GetNextInteriorInCategory(categoryindex, start = -1)
{
	for(new i = start+1; i < sizeof InteriorData; i++)
		if(InteriorData[ i ][ CategoryIndex ] == categoryindex)
			return i;
	return -1;
}


stock GetPreviousInteriorInCategory(categoryindex, start = 1)
{
	for(new i = start - 1; i != -1; i--)
		if(InteriorData[ i ][ CategoryIndex ] == categoryindex)
			return i;
	return -1;
}

public OnPlayerEnterDynamicArea(playerid, areaid)
{
	#if defined interi_OnPlayerEnterDynamicarea
		interi_OnPlayerEnterDynamicarea(playerid, areaid);
	#endif
	return 0;
}
#if defined _ALS_OnPlayerEnterDynamicArea
	#undef OnPlayerEnterDynamicArea
#else 
	#define _ALS_OnPlayerEnterDynamicArea
#endif
#define OnPlayerEnterDynamicArea interi_OnPlayerEnterDynamicarea
#if defined interi_OnPlayerEnterDynamicarea
	forward interi_OnPlayerEnterDynamicarea(playerid, areaid);
#endif

public OnPlayerLeaveDynamicArea(playerid, areaid)
{
	// Jei þaidëjas iðeina ið interjero, bet ne á kità, reiðkia jo perþiûra baigta(iðsiteleportavo ar pnð).
	if(IsPlayerInPreview[ playerid ])
		for(new i = 0; i < sizeof(InteriorData); i++)
			if(InteriorData[ i ][ AreaId ] == areaid)
				if(!IsPlayerInAnyInterior(playerid))	
					return EndInteriorPreviewForPlayer(playerid);

	#if defined interi_OnPlayerLeaveDynamicArea
		interi_OnPlayerLeaveDynamicArea(playerid, areaid);
	#endif
	return 0;
}
#if defined _ALS_OnPlayerLeaveDynamicArea
	#undef OnPlayerLeaveDynamicArea
#else 
	#define _ALS_OnPlayerLeaveDynamicArea
#endif
#define OnPlayerLeaveDynamicArea interi_OnPlayerLeaveDynamicArea
#if defined interi_OnPlayerLeaveDynamicArea
	forward interi_OnPlayerLeaveDynamicArea(playerid, areaid);
#endif

CMD:stoppreview(playerid)
{
	if(!IsPlayerInPreview[ playerid ] )
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neperþiûrinëjote interjerø.");

	EndInteriorPreviewForPlayer(playerid);
	SendClientMessage(playerid, COLOR_NEWS, "Interjerø perþiûra baigta.");
	return 1;
}