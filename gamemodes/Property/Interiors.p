/*
	* Interiors.p failas yra dalis LTRP modifikacijos. 
	* Failas atsakingas uþ interjerø pakrvoimà ið duomenø bazës, kategorijø sukûrimà, bei perþiûros ir administravimo sistemas 
	* Kodas pagrástas buvusiu modifikacijos kodu raðytu Gedui.
	* Kodas perraðytas: Bebras.
	*
	*
*/


#include <YSI\y_hooks>

#define MAX_INTERIORS					200
#define INVALID_INTERIOR_ID             0
#define MAX_INTERIOR_CATEGORY_NAME 		32
#define MAX_INTERIOR_CATEGORIES 		10

#define DIALOG_INT_MENU_MAIN 			9501
#define DIALOG_INT_MENU_NEW_MAIN 		9502
#define DIALOG_INT_MENU_NEW_ENTRANCE	9503
#define DIALOG_INT_MENU_NEW_INTERIOR	9504
#define DIALOG_INT_MENU_NEW_DIMENSION 	9505
#define DIALOG_INT_MENU_NEW_CENTER 		9606
#define DIALOG_INT_MENU_NEW_CATEGORY 	9607
#define DIALOG_INT_MENU_NEW_VIRTUAL 	9608
#define DIALOG_INT_MENU_NEW_DELETE_CONF 9609
#define DIALOG_INT_MENU_DELETE_CONFIRM 	9610
#define DIALOG_INT_MENU_INPUT_ID 		9611

#define InteriorManagementDialog. 		I_M_


// Naudojami tik management lentelëms
enum E_INTERIOR_DIMENSIONS 
{
	Width,
	Length,
	Height,
};

enum E_INTERIOR_ID_USAGES
{
	InteriorRemove,
};

static PlayerUsedInteriorId[ MAX_PLAYERS ]; // Kalba eina apie SQL id.




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


stock DeleteInterior(interiorindex)
{
	new query[60];
	mysql_format(DbHandle, query, sizeof(query), "DELETE FROM interiors WHERE id = %d LIMIT 1",
		InteriorData[ interiorindex ][ Id ]);

	static nullData[ E_INTERIOR_DATA ];
	InteriorData[ interiorindex ] = nullData;

	return mysql_pquery(DbHandle, query);
}

stock SaveInterior(interiorindex)
{
	new query[256];
	mysql_format(DbHandle, query, sizeof(query), "UPDATE interiors SET x = %f, y = %f, z = %f, interior = %d, virtual_world = %d, category = '%e' WHERE id = %d",
		InteriorData[ interiorindex ][ EnX ],
		InteriorData[ interiorindex ][ EnY ],
		InteriorData[ interiorindex ][ EnZ ],
		InteriorData[ interiorindex ][ Interior ],
		InteriorData[ interiorindex ][ VirtualWorld ],
		InteriorCategories[ InteriorData[ interiorindex ][ CategoryIndex ] ][ Name ],
		InteriorData[ interiorindex ][ Id ]);
	return mysql_pquery(DbHandle, query);
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
	IsPlayerInPreview[ playerid ] = false;
	
	SetPlayerPos(playerid, 
		GetPVarFloat(playerid, "PreviewStartX"),
		GetPVarFloat(playerid, "PreviewStartY"),
		GetPVarFloat(playerid, "PreviewStartZ")
	);
	SetPlayerInterior(playerid, GetPVarInt(playerid, "PreviewStartInterior"));
	SetPlayerVirtualWorld(playerid, GetPVarInt(playerid, "PreviewStartWorld"));

	DeletePVar(playerid, "PreviewStartX");
	DeletePVar(playerid, "PreviewStartY");
	DeletePVar(playerid, "PreviewStartZ");
	DeletePVar(playerid, "PreviewStartInterior");
	DeletePVar(playerid, "PreviewStartWorld");

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
				{
					// Duodam ðiek tiek laiko gráþti á interjerà. 
					// Nes atsittiktinai pagal streamer, gali pirma kviest OnPlayerLeaveDynamicArea(playerid, areaid)
					defer ReturnToInteriorTimer(playerid);
					return 1;
				}

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


timer ReturnToInteriorTimer[1000](playerid)
{
	if(!IsPlayerInAnyInterior(playerid))	
		EndInteriorPreviewForPlayer(playerid);
}

CMD:stoppreview(playerid)
{
	if(!IsPlayerInPreview[ playerid ] )
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neperþiûrinëjote interjerø.");

	EndInteriorPreviewForPlayer(playerid);
	SendClientMessage(playerid, COLOR_NEWS, "Interjerø perþiûra baigta.");
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




stock InteriorManagementDialog.ShowMain(playerid)
{
	ShowPlayerDialog(playerid, DIALOG_INT_MENU_MAIN, DIALOG_STYLE_LIST, "Interjerø valdymas", "Pridëti interjerà\nPaðalinti interjerà\nInterjerø perþiûra\nDabartinio interjero informacija", "Pasirinkti", "Iðeiti");
	return 1;
}

stock InteriorManagementDialog.Information(playerid, interiorid)
{
	new string[256], index = -1;
	for(new i = 0; i < MAX_INTERIORS; i++)
		if(InteriorData[ i ][ Id ] == interiorid)
		{
			index = i;
			break;
		}

	format(string, sizeof(string), "Unikalus interjero ID: %d\n\
		GTA SA interjero ID: %d\n\
		Virtualus pasaulis: %d\n\
		Kategorijos pavadinimas: %s\n\
		Kategorijos ID: %s\n",
		interiorid,
		InteriorData[ index ][ Interior ],
		InteriorData[ index ][ VirtualWorld ],
		InteriorCategories[ InteriorData[ index ][ CategoryIndex ] ][ Name ],
		InteriorCategories[ InteriorData[ index ][ CategoryIndex ] ][ Id ]);
	ShowPlayerDialog(playerid, 9999, DIALOG_STYLE_MSGBOX, "Interjero informacija", string, "Gerai", "");
	return 1;
}

stock InteriorManagementDialog.RemoveInterior(playerid)
{
	ShowPlayerDialog(playerid, DIALOG_INT_MENU_DELETE_CONFIRM, DIALOG_STYLE_MSGBOX, "{FF0000}Dëmesio!", "Ar tikrai norite iðtrinti ðá interjerà? Sugràþinti já nebus ámanoma.", "Tæsti", "Iðeiti");
	return 1;
}

stock InteriorManagementDialog.InputId(playerid, E_INTERIOR_ID_USAGES:usage, errostr[] = "")
{
	new string[80];
	if(!isnull(errostr))
		format(string,sizeof(string), "{AA1100}%s\n{FFFFFF}", errostr);
	
	strcat(string, "Áveskite interjero sql ID");
	SetPVarInt(playerid, "InteriorIdUsage", _:usage);
	ShowPlayerDialog(playerid, DIALOG_INT_MENU_INPUT_ID, DIALOG_STYLE_INPUT, "ID ávedimas", string, "Tæsti", "Atgal");
	return 1;
}

stock InteriorManagementDialog.NewInteriorMain(playerid, interiorsqlid)
{
	new string[1024], Float:x, Float:y, Float:z,
		index = -1
	;

	// Jei netinkamas ID, sukuriam nauja interjera
	if(interiorsqlid == -1)
	{
		for(new i = 0; i < MAX_INTERIORS; i++)
			if(!InteriorData[ i ][ Id ])
			{
				index = i;
				break;
			}

		// Pasiektas limitas?
		if(index == -1)
			return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, pasiektas intejerø limitas(" #MAX_INTERIORS ").");

		mysql_format(DbHandle, string, sizeof(string), "INSERT INTO interiors (x, y, z) VALUES (%f, %f, %f)",
			x, y, z);
		new Cache:result = mysql_query(DbHandle, string);

		InteriorData[ index ][ Id ] = cache_insert_id();
		cache_delete(result);
		InteriorData[ index ][ EnX ] = x;
		InteriorData[ index ][ EnY ] = y;
		InteriorData[ index ][ EnZ ] = z;
	}
	// Jei jau duotas sqlid, susirandam interjero indeksa
	else 
	{
		for(new i = 0; i < MAX_INTERIORS; i++)
			if(InteriorData[ i ][ Id ] == interiorsqlid)
			{
				index = i;
				break;
			}
	}
	GetPVarString(playerid, "NewInterior.Category", string, sizeof(string));
	// Galim parodyti informacija ir veiksmus pagaliau.
	format(string, sizeof(string), 
		"Keisti áëjimo koordinates\n\
		Keisti centro koordinates\n\
		Keisti interjero ID(%d)\n\
		Keisti virtualø pasaulá(%d)\n\
		Keisti plotá(%f)\n\
		Keisti ilgá(%d)\n\
		Keisti aukðtá(%f)\n\
		Keisti kategorijà(%s)\n\
		{11AA00}Iðsaugoti\n\
		{990022}Iðtrinti",
		InteriorData[ index ][ Interior ],
		InteriorData[ index ][ VirtualWorld ],
		GetPVarFloat(playerid, "NewInterior.Width"),
		GetPVarFloat(playerid, "NewInterior.Length"),
		GetPVarFloat(playerid, "NewInterior.Height"),
		string);
	SetPVarInt(playerid, "NewInterior.Index", index);
	ShowPlayerDialog(playerid, DIALOG_INT_MENU_NEW_MAIN, DIALOG_STYLE_LIST, "Naujo intejero kûrimas", string, "Pasirinkti", "Iðeiti");
	return 1;
}
stock InteriorManagementDialog.NewInteriorChangeEnPos(playerid, interiorindex)
{
	new string[400], Float:x, Float:y, Float:z;
	GetPlayerPos(playerid, x, y, z);
	format(string, sizeof(string),"%s{FFFFFF}Senosios koordinatës:x:%f y:%f z:%f\n\n\
		Jûsø dabartinës koordinatës yra: x: %f y: %f z:%f.\n\
		Jei norite naudoti ðias koordinates raðykite \"Naudoti Mano\"\n\n\
		Arba áveskite visas tris koordinates pats skirdami jas tarpu.\n\
		Pvz.: 0.0 0.0 0.0",
		string, 
		InteriorData[ interiorindex ][ EnX ],
		InteriorData[ interiorindex ][ EnY ],
		InteriorData[ interiorindex ][ EnZ ],
		x, y, z);
	SetPVarFloat(playerid, "TmpX", x);
	SetPVarFloat(playerid, "TmpY", y);
	SetPVarFloat(playerid, "TmpZ", z);
	ShowPlayerDialog(playerid, DIALOG_INT_MENU_NEW_ENTRANCE, DIALOG_STYLE_INPUT, "Áëjimo pozicijos keitimas", string, "Tæsti", "Atgal");
	return 1;
}

stock InteriorManagementDialog.NewInteriorChangeCenterPos(playerid, interiorindex)
{
	// Sakykim kad tas parametras ateièiai...
	#pragma unused interiorindex
	new string[400], Float:x, Float:y, Float:z;
	GetPlayerPos(playerid, x, y, z);
	format(string, sizeof(string),"%s{FFFFFF}Centro koordinatës tai yra keturkampio, á kurá telpa visas interjeras centras\n\n\
		Senosios koordinatës:x:%f y:%f z:%f\n\n\
		Jûsø dabartinës koordinatës yra: x: %f y: %f z:%f.\n\
		Jei norite naudoti ðias koordinates raðykite \"Naudoti Mano\"\n\n\
		Arba áveskite visas tris koordinates pats skirdami jas tarpu.\n\
		Pvz.: 0.0 0.0 0.0",
		string, 
		GetPVarFloat(playerid, "NewInterior.CenterX"),
		GetPVarFloat(playerid, "NewInterior.CenterY"),
		GetPVarFloat(playerid, "NewInterior.CenterZ"),
		x, y, z);
	SetPVarFloat(playerid, "TmpX", x);
	SetPVarFloat(playerid, "TmpY", y);
	SetPVarFloat(playerid, "TmpZ", z);
	ShowPlayerDialog(playerid, DIALOG_INT_MENU_NEW_CENTER, DIALOG_STYLE_INPUT, "Centro pozicijos keitimas", string, "Tæsti", "Atgal");
	return 1;
}

stock InteriorManagementDialog.NewInteriorChangeInteriorId(playerid, interiorindex)
{
	new string[100];
	format(string, sizeof(string), "Áveskite intejero ID.\nSenojo intejero ID:%d\nJûs ðiuo metu esate %d interjere.", 
		InteriorData[ interiorindex ][ Interior ], GetPlayerInterior(playerid));
	ShowPlayerDialog(playerid, DIALOG_INT_MENU_NEW_INTERIOR, DIALOG_STYLE_INPUT, "Interjero ID keitimas", string, "Tæsti", "Atgal");
	return 1;
}

stock InteriorManagementDialog.NewInteriorChangeVWorld(playerid, interiorindex)
{
	new string[110];
	format(string, sizeof(string), "Áveskite virtualø pasaulá.\nSenojo pasaulio ID:%d\nJûs ðiuo metu esate %d pasaulyje.", 
		InteriorData[ interiorindex ][ VirtualWorld ], GetPlayerVirtualWorld(playerid));
	ShowPlayerDialog(playerid, DIALOG_INT_MENU_NEW_VIRTUAL, DIALOG_STYLE_INPUT, "Interjero ID keitimas", string, "Tæsti", "Atgal");
	return 1;
}

stock InteriorManagementDialog.NewInteriorChangeDimension(playerid, E_INTERIOR_DIMENSIONS: dimension, interiorindex)
{
	// Sakykim kad tas parametras ateièiai...
	#pragma unused interiorindex
	new string[100], dimensionName[ 16 ], Float:oldDimension;
	switch(dimension)
	{
		case Width: { dimensionName = "plotis"; oldDimension = GetPVarFloat(playerid, "NewInterior.Width"); }
		case Length: { dimensionName = "ilgis"; oldDimension = GetPVarFloat(playerid, "NewInterior.Length"); }
		case Height: { dimensionName = "aukðtis"; oldDimension = GetPVarFloat(playerid, "NewInterior.Height"); }
	}

	format(string, sizeof(string), "Senasis %s: %f. Áraðykite koks turi bûti naujasis %s",
		dimensionName, oldDimension, dimensionName);
	SetPVarInt(playerid, "NewInterior.Dimension", _:dimension);
	ShowPlayerDialog(playerid, DIALOG_INT_MENU_NEW_DIMENSION, DIALOG_STYLE_INPUT, "Dimensijø keitimas", string, "Tæsti", "Atgal");
	return 1;
}

stock InteriorManagementDialog.NewInteriorChangeCategory(playerid, interiorindex, errostr[] = "")
{
	// Sakykim kad tas parametras ateièiai...
	#pragma unused interiorindex
	new string[140 + MAX_INTERIOR_CATEGORY_NAME];
	GetPVarString(playerid, "NewInterior.Category", string, sizeof(string));
	format(string, sizeof(string),"Sena kategorija: %s. Áveskite kategorijos kuriai norite priskirti ðá interjerà pavadinimà",
		string);

	if(!isnull(errostr))
	{
		strins(string, "\n{FFFFFF}", 0);
		strins(string, errostr, 0);
		strins(string, "{FF2200}", 0);
	}
	ShowPlayerDialog(playerid, DIALOG_INT_MENU_NEW_CATEGORY, DIALOG_STYLE_INPUT, "Kategorijos keitimas", string, "Tæsti", "Atgal");
	return 1;
}

stock InteriorManagementDialog.NewInteriorUpdateVisualArea(playerid, interiorindex, bool:destroy = false)
{
	// Sakykim kad tas parametras ateièiai...
	#pragma unused interiorindex
	// Kad lengviau bûtø þaidëjams kurti interjerus, bus rodomas interjero dydis su gangzone.
	// Ði funckija tà gangzone sukurs ir/ar atnaujins
	static gangzones[ MAX_PLAYERS ] = {-1, ... };

	if(gangzones[ playerid ] != -1)
	{
		GangZoneDestroy(gangzones[ playerid ]);
		if(destroy)
		{
			gangzones[ playerid ] = -1;
			return 1;
		}
	}

	new Float:minx, Float:miny, Float:maxx, Float:maxy;
	minx = GetPVarFloat(playerid, "NewInterior.CenterX") - (GetPVarFloat(playerid, "NewInterior.Width") / 2);
	maxx = GetPVarFloat(playerid, "NewInterior.CenterX") + (GetPVarFloat(playerid, "NewInterior.Width") / 2);

	miny = GetPVarFloat(playerid, "NewInterior.CenterY") - (GetPVarFloat(playerid, "NewInterior.Length") / 2);
	maxy = GetPVarFloat(playerid, "NewInterior.CenterY") + (GetPVarFloat(playerid, "NewInterior.Length") / 2);

	gangzones[ playerid ] = GangZoneCreate(minx, miny, maxx, maxy);
	GangZoneShowForPlayer(playerid, gangzones[ playerid ], 0xFF0000AA);
	return 1;
}


stock InteriorManagementDialog.NewInteriorSave(playerid, interiorindex)
{
	if(InteriorData[ interiorindex ][ EnX ] == 0.0 && 
		InteriorData[ interiorindex ][ EnY ] == 0.0 &&
		InteriorData[ interiorindex ][ EnZ ] == 0.0)
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, visos áëjimo koordinatës negali bûti 0.");

	if(GetPVarFloat(playerid, "NewInterior.CenterX") == 0.0 &&
		GetPVarFloat(playerid, "NewInterior.CenterY") == 0.0 &&
		GetPVarFloat(playerid, "NewInterior.CenterZ") == 0.0)
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, visos centro koordinatës negali bûti 0.");

	if(GetPVarFloat(playerid, "NewInterior.Width") == 0.0)
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, plotis negali bûti lygus 0.");

	if(GetPVarFloat(playerid, "NewInterior.Length") == 0.0)
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ilgis negali bûti lygus 0.");

	if(GetPVarFloat(playerid, "NewInterior.Height") == 0.0)
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, aukðtis negali bûti lygus 0.");

	new category[ MAX_INTERIOR_CATEGORY_NAME ];
	GetPVarString(playerid, "NewInterior.Category", category, sizeof(category));
	
	if(isnull(category))
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, privalote áraðyti kategorijos pavadinimà.");

	// Tikriname kategorija 
	// Reikia suþinoti ar tokia jau yra, o jei ne ar yra vietos.
	new freeindex = -1, lastCategoryId, bool:found = false;
	for(new i = 0; i < MAX_INTERIOR_CATEGORIES; i++)
	{
		if(!InteriorCategories[ i ][ Id ])
		{
			if(freeindex == -1)
				freeindex = i;
			continue;
		}
		if(InteriorCategories[ i ][ Id ] >= lastCategoryId)
			lastCategoryId = InteriorCategories[ i ][ Id ];

		if(strcmp(InteriorCategories[ i ][ Name ], category))
			continue;

		InteriorData[ interiorindex ][ CategoryIndex ] = i;
		found = true;
	}
	if(!found)
	{
		if(freeindex == -1)
			return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, pasiektas kategorijø limitas(" #MAX_INTERIOR_CATEGORIES ")");
	
		format(InteriorCategories[ freeindex ][ Name ], MAX_INTERIOR_CATEGORY_NAME, category);
		InteriorCategories[ freeindex ][ Id ] = ++lastCategoryId;
		InteriorData[ interiorindex ][ CategoryIndex ] = freeindex;
	}

	// Na jei pasiekëm ðià vietà, reiðkia VISKAS, galima iðsaugot ir susitvarkyt.

	new query[300];
	mysql_format(DbHandle, query, sizeof(query), "UPDATE interiors SET x = %f, y = %f, z = %f, center_x = %f \
		center_y = %f, center_z = %f, interior = %d, virtual_world = %d, width = %f, length = %f, height = %f \
		category = '%e' WHERE id = %d",
		InteriorData[ interiorindex ][ EnX ],
		InteriorData[ interiorindex ][ EnY ],
		InteriorData[ interiorindex ][ EnZ ],
		GetPVarFloat(playerid, "NewInterior.CenterX"),
		GetPVarFloat(playerid, "NewInterior.CenterY"),
		GetPVarFloat(playerid, "NewInterior.CenterZ"),
		InteriorData[ interiorindex ][ Interior ],
		InteriorData[ interiorindex ][ VirtualWorld ],
		GetPVarFloat(playerid, "NewInterior.Width"),
		GetPVarFloat(playerid, "NewInterior.Length"),
		GetPVarFloat(playerid, "NewInterior.Height"),
		category,
		InteriorData[ interiorindex ][ Id ]
	);
	mysql_pquery(DbHandle, query);

	DeletePVar(playerid, "NewInterior.CenterX");
	DeletePVar(playerid, "NewInterior.CenterY");
	DeletePVar(playerid, "NewInterior.CenterZ");
	DeletePVar(playerid, "NewInterior.Width");
	DeletePVar(playerid, "NewInterior.Length");
	DeletePVar(playerid, "NewInterior.Height");
	DeletePVar(playerid, "NewInterior.TmpX");
	DeletePVar(playerid, "NewInterior.TmpY");
	DeletePVar(playerid, "NewInterior.TmpZ");
	DeletePVar(playerid, "NewInterior.Index");
	InteriorManagementDialog.NewInteriorUpdateVisualArea(playerid, interiorindex, .destroy = true);

	SendClientMessage(playerid, COLOR_NEWS, "Interjeras sëkmingai sukurtas ir iðsaugotas.");
	InteriorManagementDialog.ShowMain(playerid);
	return 1;
}


stock InteriorManagementDialog.NewInteriorConfirmDelete(playerid, interiorindex)
{
	// Sakykim kad tas parametras ateièiai...
	#pragma unused interiorindex
	ShowPlayerDialog(playerid, DIALOG_INT_MENU_NEW_DELETE_CONF, DIALOG_STYLE_MSGBOX, "{FF0000}Dëmesio!", "Ar tikrai norite paðalinti ðá interjerà? Visi jûsø áraðyti duomenys bus paðalinti. Ðio proceso atstatyti neámanoma.", "Iðtrinti", "Atgal");
	return 1;
}



hook OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
	switch(dialogid)
	{
		case DIALOG_INT_MENU_MAIN:
		{
			if(!response)			
				return 1;

			new interiorid = GetPlayerInteriorId(playerid);
			PlayerUsedInteriorId[ playerid ] = interiorid;
			switch(listitem)
			{
				// Naujas interjeras 
				case 0: InteriorManagementDialog.NewInteriorMain(playerid, -1);
				// Paðalinti interjerà
				case 1: 
				{
					if(interiorid)
						InteriorManagementDialog.RemoveInterior(playerid);
					else 
						InteriorManagementDialog.InputId(playerid, InteriorRemove);
				}
				// Interjerø perþiûra
				case 2: ShowInteriorPreviewForPlayer(playerid);
				// Dabartinio interjero informacija
				case 3: 
				{
					if(!IsPlayerInAnyInterior(playerid))
						return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neesate interjere.");

					InteriorManagementDialog.Information(playerid, interiorid);
				}
			}
			return 1;
		}
		case DIALOG_INT_MENU_NEW_MAIN:
		{
			new index =  GetPVarInt(playerid, "NewInterior.Index");


			if(!response)
				return InteriorManagementDialog.NewInteriorConfirmDelete(playerid, index);
		
			switch(listitem)
			{
				case 0: InteriorManagementDialog.NewInteriorChangeEnPos(playerid, index);
				case 1: InteriorManagementDialog.NewInteriorChangeCenterPos(playerid, index);
				case 2: InteriorManagementDialog.NewInteriorChangeInteriorId(playerid, index);
				case 3: InteriorManagementDialog.NewInteriorChangeVWorld(playerid, index);
				case 4: InteriorManagementDialog.NewInteriorChangeDimension(playerid, Width, index);
				case 5: InteriorManagementDialog.NewInteriorChangeDimension(playerid, Length, index);
				case 6: InteriorManagementDialog.NewInteriorChangeDimension(playerid, Height, index);
				case 7: InteriorManagementDialog.NewInteriorChangeCategory(playerid, index);
				case 8: InteriorManagementDialog.NewInteriorSave(playerid, index);
				case 9: InteriorManagementDialog.NewInteriorConfirmDelete(playerid, index);
			}
			return 1;
		}
		case DIALOG_INT_MENU_NEW_ENTRANCE:
		{
			new index =  GetPVarInt(playerid, "NewInterior.Index");

			if(!response)	
				return InteriorManagementDialog.NewInteriorMain(playerid, InteriorData[ index ][ Id ]);

			new Float:x, Float:y, Float:z;

			if(!isnull(inputtext) && !strcmp(inputtext, "Naudoti mano", true))
			{
				x = GetPVarFloat(playerid, "TmpX");
				y = GetPVarFloat(playerid, "TmpY");
				z = GetPVarFloat(playerid, "TmpZ");
			}
			else 
			{
				if(sscanf(inputtext, "fff", x, y, z))
					return InteriorManagementDialog.NewInteriorChangeEnPos(playerid, index);
			}

			InteriorData[ index ][ EnX ] = x;
			InteriorData[ index ][ EnY ] = y;
			InteriorData[ index ][ EnZ ] = z;
			
			SaveInterior(index);
			SendClientMessage(playerid, COLOR_NEWS, "Intejero áëjimo koordinatës sëkmingai pakeistos.");
			InteriorManagementDialog.NewInteriorMain(playerid, InteriorData[ index ][ Id ]);
			return 1;
		}
		case DIALOG_INT_MENU_NEW_CENTER:
		{
			new index =  GetPVarInt(playerid, "NewInterior.Index");

			if(!response)	
				return InteriorManagementDialog.NewInteriorMain(playerid, InteriorData[ index ][ Id ]);

			new Float:x, Float:y, Float:z;

			if(!isnull(inputtext) && !strcmp(inputtext, "Naudoti mano", true))
			{
				x = GetPVarFloat(playerid, "TmpX");
				y = GetPVarFloat(playerid, "TmpY");
				z = GetPVarFloat(playerid, "TmpZ");
			}
			else 
			{
				if(sscanf(inputtext, "fff", x, y, z))
					return InteriorManagementDialog.NewInteriorChangeCenterPos(playerid, index);
			}

			SetPVarFloat(playerid, "NewInterior.CenterX", x);
			SetPVarFloat(playerid, "NewInterior.CenterY", y);
			SetPVarFloat(playerid, "NewInterior.CenterZ", z);
			
			SendClientMessage(playerid, COLOR_NEWS, "Intejero centro koordinatës sëkmingai pakeistos.");
			InteriorManagementDialog.NewInteriorUpdateVisualArea(playerid, index);
			InteriorManagementDialog.NewInteriorMain(playerid, InteriorData[ index ][ Id ]);
			return 1;
		}
		case DIALOG_INT_MENU_NEW_INTERIOR:
		{
			new index =  GetPVarInt(playerid, "NewInterior.Index");

			if(!response)	
				return InteriorManagementDialog.NewInteriorMain(playerid, InteriorData[ index ][ Id ]);

			if(sscanf(inputtext, "i", InteriorData[ index ][ Interior ]))
				return InteriorManagementDialog.NewInteriorChangeInteriorId(playerid, index);

			SaveInterior(index);
			InteriorManagementDialog.NewInteriorMain(playerid, InteriorData[ index ][ Id ]);
			SendClientMessage(playerid, COLOR_NEWS, "Interjero ID sëkmingai pakeistas.");
			return 1;
		}
		case DIALOG_INT_MENU_NEW_VIRTUAL:
		{
			new index =  GetPVarInt(playerid, "NewInterior.Index");

			if(!response)	
				return InteriorManagementDialog.NewInteriorMain(playerid, InteriorData[ index ][ Id ]);

			if(sscanf(inputtext, "i", InteriorData[ index ][ VirtualWorld ]))
				return InteriorManagementDialog.NewInteriorChangeVWorld(playerid, index);

			SaveInterior(index);
			InteriorManagementDialog.NewInteriorMain(playerid, InteriorData[ index ][ Id ]);
			SendClientMessage(playerid, COLOR_NEWS, "Virtualaus pasaulio ID sëkmingai pakeistas.");
			return 1;
		}
		case DIALOG_INT_MENU_NEW_DIMENSION:
		{
			new index =  GetPVarInt(playerid, "NewInterior.Index"),
				E_INTERIOR_DIMENSIONS:dimensionType = E_INTERIOR_DIMENSIONS:GetPVarInt(playerid, "NewInterior.Dimension");

			if(!response)	
				return InteriorManagementDialog.NewInteriorMain(playerid, InteriorData[ index ][ Id ]);

			new Float:dimension;
			if(sscanf(inputtext, "f"))
				return InteriorManagementDialog.NewInteriorChangeDimension(playerid, dimensionType, index);

			switch(dimensionType)
			{
				case Width: SetPVarFloat(playerid, "NewInterior.Width", dimension);
				case Length: SetPVarFloat(playerid, "NewInterior.Length", dimension);
				case Height: SetPVarFloat(playerid, "NewInterior.Height", dimension);
			}
			SendClientMessage(playerid, COLOR_NEWS, "Duomuo sëkmingai atnaujintas");
			InteriorManagementDialog.NewInteriorUpdateVisualArea(playerid, index);
			InteriorManagementDialog.NewInteriorMain(playerid, InteriorData[ index ][ Id ]);
			return 1;
		}
		case DIALOG_INT_MENU_NEW_CATEGORY:
		{
			new index =  GetPVarInt(playerid, "NewInterior.Index");

			if(!response)	
				return InteriorManagementDialog.NewInteriorMain(playerid, InteriorData[ index ][ Id ]);

			if(isnull(inputtext) || strlen(inputtext) >= MAX_INTERIOR_CATEGORY_NAME)
				return InteriorManagementDialog.NewInteriorChangeCategory(playerid, index, "Kategorijos pavadinimà gali sudaryti nuo 1 iki " #MAX_INTERIOR_CATEGORY_NAME " simboliø.");
			
			SetPVarString(playerid, "NewInterior.Category", inputtext);
			SendClientMessage(playerid, COLOR_NEWS, "Kategorijos pavadinimas sëkmingai atnaujintas.");
			InteriorManagementDialog.NewInteriorMain(playerid, InteriorData[ index ][ Id ]);
			return 1;
		}
		case DIALOG_INT_MENU_NEW_DELETE_CONF:
		{
			new index =  GetPVarInt(playerid, "NewInterior.Index");

			if(!response)	
				return InteriorManagementDialog.NewInteriorMain(playerid, InteriorData[ index ][ Id ]);

			DeleteInterior(index);

			static EmptyInteriorData[ E_INTERIOR_DATA ];
			InteriorData[ index ] = EmptyInteriorData;

			DeletePVar(playerid, "NewInterior.CenterX");
			DeletePVar(playerid, "NewInterior.CenterY");
			DeletePVar(playerid, "NewInterior.CenterZ");
			DeletePVar(playerid, "NewInterior.Width");
			DeletePVar(playerid, "NewInterior.Length");
			DeletePVar(playerid, "NewInterior.Height");
			DeletePVar(playerid, "NewInterior.TmpX");
			DeletePVar(playerid, "NewInterior.TmpY");
			DeletePVar(playerid, "NewInterior.TmpZ");
			DeletePVar(playerid, "NewInterior.Index");
			InteriorManagementDialog.NewInteriorUpdateVisualArea(playerid, index, .destroy = true);

			SendClientMessage(playerid, COLOR_NEWS, "Interjeras sëkmingai paðalintas.");
			InteriorManagementDialog.ShowMain(playerid);
			return 1;
		}
		case DIALOG_INT_MENU_DELETE_CONFIRM:
		{
			if(!response)
				return InteriorManagementDialog.ShowMain(playerid);

			DeleteInterior(PlayerUsedInteriorId[ playerid ]);
			SendClientMessage(playerid, COLOR_NEWS, "Interjeras sëkmingai paðalintas.");
			return 1;
		}
		case DIALOG_INT_MENU_INPUT_ID:
		{
			if(!response)
				return InteriorManagementDialog.ShowMain(playerid);

			new E_INTERIOR_ID_USAGES:usage = E_INTERIOR_ID_USAGES:GetPVarInt(playerid, "InteriorIdUsage");

			if(inputtext, "i", PlayerUsedInteriorId[ playerid ])
				return InteriorManagementDialog.InputId(playerid, usage);
			
			if(!IsValidInterior(PlayerUsedInteriorId[ playerid ]))
				return InteriorManagementDialog.InputId(playerid, usage, "Tokio intejero ID nëra.");

			switch(usage) 
			{
				case InteriorRemove: InteriorManagementDialog.RemoveInterior(playerid);
			}
			DeletePVar(playerid, "InteriorIdUsage");
			return 1;
		}
	}
	return 0;
}