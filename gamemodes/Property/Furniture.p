#include <YSI\y_hooks>



#define MAX_FURNITURE_NAME 				64
#define MAX_FURNITURE_CATEGORY_NAME 	64
#define MAX_FURNITURE_OBJECTS 			2000
#define MAX_FURNITURE_CATEGORIES  		20
#define MAX_FURNITURE_TEXTURES 			300
#define MAX_TXD_FILE_NAME 				32
#define MAX_TEXTURE_NAME 				32


enum E_PROPERTY_FURNITURE_DATA {
    SqlId,
    FurnitureId,
    ObjectId,
    Name[ MAX_FURNITURE_NAME]
};

enum E_FURNITURE_DATA {
	Id,
	ObjectId,
	//Name[ MAX_FURNITURE_NAME ],
	Price,
	Category
};


new static FurnitureObjects[ MAX_FURNITURE_OBJECTS ][ E_FURNITURE_DATA ],
	FurnitureObjectNames[ MAX_FURNITURE_OBJECTS ][ MAX_FURNITURE_NAME ];

enum E_FURNITURE_CATEGORY_DATA {
	Id,
	Name[ MAX_FURNITURE_CATEGORY_NAME ]
};

static FurnitureCategories[ MAX_FURNITURE_CATEGORIES ][ E_FURNITURE_CATEGORY_DATA ];


enum E_FURNITURE_TEXTURE_DATA
{
	Id,
	ObjectModel,
	TxdName[ MAX_TXD_FILE_NAME ],
	TextureName[ MAX_TEXTURE_NAME ]
};

static FurnitureTextures[ MAX_FURNITURE_TEXTURES ][ E_FURNITURE_TEXTURE_DATA ];



enum E_TEXTURE_COLOR_DATA
{
	RGBA_Int,
	RGB_String[ 12 ],
	Name[ 32 ]
};

static const TextureColorlist[ ][ E_TEXTURE_COLOR_DATA ] =
{
	{0xE60000FF, "E60000","Raudona"},
    {0xCECECEFF, "CECECE", "Pilka"},
    {0x5807EDFF, "5807ED", "Violetinë"},
    {0x00FF00FF, "00FF00", "Þalia 1"},
    {0x008000FF, "008000", "Þalia 2"},
    {0x0000FFFF, "0000FF", "Mëlyna"},
    {0xFF8000FF, "FF8000", "Orandþinë"}
};


// Visada turi bûti nelyginis skaièius ir dviem didesnis nei bus rodoma objektu.
#define MAX_TEXTURES_IN_PREVIEW 		5
#define TEXTURE_PREVIEW_SPACING 		2.0
#define TEXTURE_PREVIEW_SPEED 			25.0



enum E_FURNITURE_TEXTURE_PREVIEW
{
    bool:IsInPreview,
   	ObjectIDs[ MAX_TEXTURES_IN_PREVIEW ], // Dar du papildomi slotai judinimui
   	NumberObjectIDs[ MAX_TEXTURES_IN_PREVIEW ], // Irgi objektai, tiesiog permatomi su skaièiukais
    FocusedOn,
    bool:ObjectsMoving,
    ExtraId, 										// Turëtø padëti atskiriant kada verslo objekto tekstûra, kada namo ir t.t. Bus pridëta á OnPlayerSelectTexture
    Float:Distance,
};

new static PlayerTexturePreview[ MAX_PLAYERS ][ E_FURNITURE_TEXTURE_PREVIEW ];













public OnGameModeInit()
{
	#if defined furniture_OnGameModeInit
		furniture_OnGameModeInit();
	#endif

	new
		category[MAX_FURNITURE_CATEGORY_NAME],
		categoryIdCount = 1,
		freeIndex = -1,
		ticks = GetTickCount();

	new Cache:result = mysql_query(DbHandle, "SELECT * FROM furniture");
	for(new i = 0; i < cache_get_row_count(); i++)
	{
		if(i >= MAX_FURNITURE_OBJECTS)
		{
			printf("Klaida. Duomenø bazëjë yra daugiau baldø(%d) nei leidþia limitas(" #MAX_FURNITURE_OBJECTS ")", cache_get_row_count());
			break;
		}
		FurnitureObjects[ i ][ Id ] = cache_get_field_content_int(i, "id");
		FurnitureObjects[ i ][ ObjectId ] = cache_get_field_content_int(i, "object_id");
		//cache_get_field_content(i, "name", FurnitureObjects[ i ][ Name ], DbHandle, MAX_FURNITURE_NAME);
		cache_get_field_content(i, "name", FurnitureObjectNames[ i ], DbHandle, MAX_FURNITURE_NAME);
		FurnitureObjects[ i ][ Price ] = cache_get_field_content_int(i, "price");
		cache_get_field_content(i, "category", category);

		for(new j = 0; j < sizeof(FurnitureCategories); j++)
		{
			if(!FurnitureCategories[ j ][ Id ] && freeIndex == -1)
				freeIndex = j;

			if(!isnull(FurnitureCategories[ j ][ Name ]) && !strcmp(FurnitureCategories[ j ][ Name ], category, true))
			{
				FurnitureObjects[ i ][ Category ] = FurnitureCategories[ j ][ Id ];
				break;
			}
		}
		if(!FurnitureObjects[ i ][ Category ])
		{
			if(freeIndex == -1)
			{
				printf("Baldai. Skirtingø kategorijø skaièius(%d) virðijà limità(" #MAX_FURNITURE_CATEGORIES ")", categoryIdCount);
				continue;
			}
			FurnitureCategories[ freeIndex ][ Id ] = categoryIdCount++;
			strcat(FurnitureCategories[ freeIndex ][ Name ], category, MAX_FURNITURE_CATEGORY_NAME);
			FurnitureObjects[ i ][ Category ] = FurnitureCategories[ freeIndex ][ Id ];
			freeIndex = -1;
		}
	}
	printf("Pakrauti %d baldai ir %d jø kategorijos. Tai uztruko %d MS", cache_get_row_count(), categoryIdCount, GetTickCount() - ticks);
	cache_delete(result);


	ticks = GetTickCount();
	result = mysql_query(DbHandle, "SELECT * FROM furniture_textures");
	for(new i = 0; i < cache_get_row_count(); i++)
	{
		FurnitureTextures[ i ][ Id ] = cache_get_field_content_int(i, "id");
		FurnitureTextures[ i ][ ObjectModel ] = cache_get_field_content_int(i, "object_model");
		cache_get_field_content(i, "txd_name", FurnitureTextures[ i ][ TxdName ], DbHandle, MAX_TXD_FILE_NAME);
		cache_get_field_content(i, "texture_name", FurnitureTextures[ i ][ TextureName ], DbHandle, MAX_TEXTURE_NAME);
	}
	printf("Pakrautos %d baldu teksturos. Tai uztruko %d MS", cache_get_row_count(), GetTickCount() - ticks);
	cache_delete(result);
}
#if defined _ALS_OnGameModeInit
	#undef OnGameModeInit
#else
	#define _ALS_OnGameModeInit
#endif
#define OnGameModeInit furniture_OnGameModeInit
#if defined furniture_OnGameModeInit
	forward furniture_OnGameModeInit();
#endif


stock GetFurniturePrice(index)
	return FurnitureObjects[ index ][ Price ];

stock GetFurnitureObjectId(index)
	return FurnitureObjects[ index ][ ObjectId ];

stock GetFurnitureID(index)
	return FurnitureObjects[ index ][ Id ];

stock GetFurnitureIndex(id)
{
	for(new i = 0; i < sizeof FurnitureObjects; i++)
		if(FurnitureObjects[ i ][ Id ] == id)
			return i;
	return -1;
}
stock GetFurnitureName(index)
{
	return FurnitureObjectNames[ index ];
}

stock GetObjectFurnitureIndex(objectid, categoryid = -1)
{
	for(new i = 0; i < sizeof(FurnitureObjects); i++)
	{
		if(FurnitureObjects[ i ][ ObjectId ] == objectid)
		{
			if(categoryid != -1 && FurnitureObjects[ i ][ Category ] != categoryid)
				continue;
			return i;
		}
	}
	return -1;
}






stock GetFurnitureCategoryCount()
{
	new count = 0;
	for(new i = 0; i < sizeof(FurnitureCategories); i++)
		if(FurnitureCategories[ i ][ Id ])
			count++;
	return count;
}

stock GetFurnitureCategoryIndex(id)
{
	for(new i = 0; i < sizeof(FurnitureCategories); i++)
		if(FurnitureCategories[ i ][ Id ] == id)
			return i;
	return -1;
}
stock GetFurnitureCategoryName(findex)
{
	new string[ MAX_FURNITURE_CATEGORY_NAME ];
	strcat(string, FurnitureCategories[ findex ][ Name ]);
	return string;
}

stock GetFurnitureCategoryId(findex)
	return FurnitureCategories[ findex ][ Id ];

stock GetCategoryFurnitureObjects(categoryid, array[], &furnitureCount, len)
{
	// Ðita funkcija pripildys duotà masyvà nurodytos kategorijos duomenis
	// Taip pat graþins elementø kieká
	// Ne pati fainiausia funkcija, bet oh well it works.
	new arrayIndex = 0;
	furnitureCount = 0;
	for(new i = 0; i < sizeof FurnitureObjects; i++)
	{
		if(FurnitureObjects[ i ][ Category ] != categoryid)
			continue;

		array[ arrayIndex++ ] = FurnitureObjects[ i ][ ObjectId ];
		furnitureCount++;
		if(arrayIndex == len)
			return;
	}
}


/*

		                                            ,,
		`7MM"""YMM                                  db   mm                                      mm                       mm
		  MM    `7                                       MM                                      MM                       MM
		  MM   d `7MM  `7MM  `7Mb,od8 `7MMpMMMb.  `7MM mmMMmm `7MM  `7MM  `7Mb,od8 .gP"Ya      mmMMmm .gP"Ya `7M'   `MF'mmMMmm `7MM  `7MM  `7Mb,od8 .gP"Ya  ,pP"Ybd
		  MM""MM   MM    MM    MM' "'   MM    MM    MM   MM     MM    MM    MM' "',M'   Yb       MM  ,M'   Yb  `VA ,V'    MM     MM    MM    MM' "',M'   Yb 8I   `"
		  MM   Y   MM    MM    MM       MM    MM    MM   MM     MM    MM    MM    8M""""""       MM  8M""""""    XMX      MM     MM    MM    MM    8M"""""" `YMMMa.
		  MM       MM    MM    MM       MM    MM    MM   MM     MM    MM    MM    YM.    ,       MM  YM.    ,  ,V' VA.    MM     MM    MM    MM    YM.    , L.   I8
		.JMML.     `Mbod"YML..JMML.   .JMML  JMML..JMML. `Mbmo  `Mbod"YML..JMML.   `Mbmmd'       `Mbmo`Mbmmd'.AM.   .MA.  `Mbmo  `Mbod"YML..JMML.   `Mbmmd' M9mmmP'


*/

stock GetFurnitureTextureCount()
{
	new count = 0;
	for(new i = 0; i < MAX_FURNITURE_TEXTURES; i++)
		if(FurnitureTextures[ i ][ Id ])
			count++;
	return count;
}

stock GetFurnitureTextureId(index)
	return FurnitureTextures[ index ][ Id ];

stock GetFurnitureTextureTxdName(index)
{
	new s[ MAX_TXD_FILE_NAME ];
	strcat(s, FurnitureTextures[ index ][ TxdName ]);
	return s;
}

stock GetFurnitureTextureName(index)
{
	new s[ MAX_TXD_FILE_NAME ];
	strcat(s, FurnitureTextures[ index ][ TextureName ]);
	return s;
}
stock GetFurnitureTextureObjectModel(index)
	return FurnitureTextures[ index ][ ObjectModel ];






/*

		                                                                                      ,,
		MMP""MM""YMM                 mm                                                     `7MM
		P'   MM   `7                 MM                                                       MM
		     MM  .gP"Ya `7M'   `MF'mmMMmm `7MM  `7MM  `7Mb,od8 .gP"Ya       ,p6"bo   ,pW"Wq.  MM  ,pW"Wq.`7Mb,od8 ,pP"Ybd
		     MM ,M'   Yb  `VA ,V'    MM     MM    MM    MM' "',M'   Yb     6M'  OO  6W'   `Wb MM 6W'   `Wb MM' "' 8I   `"
		     MM 8M""""""    XMX      MM     MM    MM    MM    8M""""""     8M       8M     M8 MM 8M     M8 MM     `YMMMa.
		     MM YM.    ,  ,V' VA.    MM     MM    MM    MM    YM.    ,     YM.    , YA.   ,A9 MM YA.   ,A9 MM     L.   I8
		   .JMML.`Mbmmd'.AM.   .MA.  `Mbmo  `Mbod"YML..JMML.   `Mbmmd'      YMbmd'   `Ybmd9'.JMML.`Ybmd9'.JMML.   M9mmmP'


*/

stock GetFurnitureTextureColorCount()
	return sizeof(TextureColorlist);

stock GetFurnitureTextureColorRGB(index)
{
	new s[12];
	strcat(s, TextureColorlist[ index ][ RGB_String ]);
	return s;
}

stock GetFurnitureTextureColorRGBA(index)
	return TextureColorlist[ index ][ RGBA_Int ];

stock GetFurnitureTextureColorName(index)
{
	new s[16];
	strcat(s, TextureColorlist[ index ][ Name ]);
	return s;
}





/*

							                                            ,,
							`7MM"""YMM                                  db   mm
							  MM    `7                                       MM
							  MM   d `7MM  `7MM  `7Mb,od8 `7MMpMMMb.  `7MM mmMMmm `7MM  `7MM  `7Mb,od8 .gP"Ya
							  MM""MM   MM    MM    MM' "'   MM    MM    MM   MM     MM    MM    MM' "',M'   Yb
							  MM   Y   MM    MM    MM       MM    MM    MM   MM     MM    MM    MM    8M""""""
							  MM       MM    MM    MM       MM    MM    MM   MM     MM    MM    MM    YM.    ,
							.JMML.     `Mbod"YML..JMML.   .JMML  JMML..JMML. `Mbmo  `Mbod"YML..JMML.   `Mbmmd'



					   mm                       mm                                                                               db
					   MM                       MM
					 mmMMmm .gP"Ya `7M'   `MF'mmMMmm `7MM  `7MM  `7Mb,od8 .gP"Ya          `7MMpdMAo.`7Mb,od8 .gP"Ya `7M'   `MF'`7MM  .gP"Ya `7M'    ,A    `MF'
					   MM  ,M'   Yb  `VA ,V'    MM     MM    MM    MM' "',M'   Yb           MM   `Wb  MM' "',M'   Yb  VA   ,V    MM ,M'   Yb  VA   ,VAA   ,V
					   MM  8M""""""    XMX      MM     MM    MM    MM    8M""""""           MM    M8  MM    8M""""""   VA ,V     MM 8M""""""   VA ,V  VA ,V
					   MM  YM.    ,  ,V' VA.    MM     MM    MM    MM    YM.    ,           MM   ,AP  MM    YM.    ,    VVV      MM YM.    ,    VVV    VVV
					   `Mbmo`Mbmmd'.AM.   .MA.  `Mbmo  `Mbod"YML..JMML.   `Mbmmd'           MMbmmd' .JMML.   `Mbmmd'     W     .JMML.`Mbmmd'     W      W
					                                                                        MM
					                                                                      .JMML.

*/


stock StartFurnitureTexturePreview(playerid, extraid)
{
	if(PlayerTexturePreview[ playerid ][ IsInPreview ])
		return false;

	TogglePlayerControllable(playerid, false);
	PlayerTexturePreview[ playerid ][ Distance ] = 5.0;
	PlayerTexturePreview[ playerid ][ IsInPreview ] = true;
	PlayerTexturePreview[ playerid ][ ExtraId ] = extraid;


	// Nustatymas FocusedOn á nerealø indeksà leis pasinaudot FurnitureTexturesFocusOn
	PlayerTexturePreview[ playerid ][ FocusedOn ] = -1000;
	FurnitureTexturesFocusOn(playerid, 0);

	new Float:x, Float:y, Float:z;
	GetPlayerPos(playerid, x, y, z);
	SetPlayerCameraPos(playerid, x, y, z+1.0);


	GetXYInFrontOfPlayer(playerid, x, y, PlayerTexturePreview[ playerid ][ Distance ]);
	SetPlayerCameraLookAt(playerid, x, y, z+1.0);
	return 1;
}

stock IsPlayerInTexturePreview(playerid)
	return PlayerTexturePreview[ playerid ][ IsInPreview ];

stock StopFurnitureTexturePreview(playerid)
{
	TogglePlayerControllable(playerid, true);
	SetCameraBehindPlayer(playerid);
	PlayerTexturePreview[ playerid ][ ObjectsMoving ] = false;
	PlayerTexturePreview[ playerid ][ IsInPreview ] = false;
	PlayerTexturePreview[ playerid ][ ExtraId ] = -1;
	for(new i = 0; i < MAX_TEXTURES_IN_PREVIEW; i++)
	{
		if(IsValidDynamicObject(PlayerTexturePreview[ playerid ][ ObjectIDs ][ i ]))
			DestroyDynamicObject(PlayerTexturePreview[ playerid ][ ObjectIDs ][ i ]);
		if(IsValidDynamicObject(PlayerTexturePreview[ playerid ][ NumberObjectIDs ][ i ]))
			DestroyDynamicObject(PlayerTexturePreview[ playerid ][ NumberObjectIDs ][ i ]);
		PlayerTexturePreview[ playerid ][ ObjectIDs ][ i ] = -1;
		PlayerTexturePreview[ playerid ][ NumberObjectIDs ][ i ] = -1;
	}
}

stock FurnitureTextureReshow(playerid)
{
	new focused = PlayerTexturePreview[ playerid ][ FocusedOn ];
	PlayerTexturePreview[ playerid ][ FocusedOn ] = -2;
	FurnitureTexturesFocusOn(playerid, focused);
}

stock FurnitureTexturesFocusOn(playerid, focus_on)
{
	if(focus_on < 0 || focus_on >= GetFurnitureTextureCount())
		return 0;

	new Float:x, Float:y, Float:z, Float:angle, Float:rotZ, s[16];
	GetPlayerFacingAngle(playerid, rotZ);
	angle = 360 - rotZ;


	// Jei keièiasi viskas daugiau nei vienetu...
	if(abs(PlayerTexturePreview[ playerid ][ FocusedOn ] - focus_on) > 1 && !PlayerTexturePreview[ playerid ][ ObjectsMoving ])
	{
		for(new i = 1; i < MAX_TEXTURES_IN_PREVIEW - 1; i++)
		{
			if(IsValidDynamicObject(PlayerTexturePreview[ playerid ][ ObjectIDs ][ i ]))
				DestroyDynamicObject(PlayerTexturePreview[ playerid ][ ObjectIDs ][ i ]);
			if(IsValidDynamicObject(PlayerTexturePreview[ playerid ][ NumberObjectIDs ][ i ]))
				DestroyDynamicObject(PlayerTexturePreview[ playerid ][ NumberObjectIDs ][ i ]);

			PlayerTexturePreview[ playerid ][ ObjectIDs ][ i ] = -1;
			PlayerTexturePreview[ playerid ][ NumberObjectIDs ][ i ] = -1;
		}

		GetPlayerPos(playerid, x, y, z);
		z += 1.0;

		// Surandam koordinates prieð nurodytà taðkà.
		GetXYInFrontOfPlayer(playerid, x, y, PlayerTexturePreview[ playerid ][ Distance ]);
		new Float:tx, Float:ty;
		GetXYInFrontOfPlayer(playerid, x, y, PlayerTexturePreview[ playerid ][ Distance ] - 0.1);

		new textureIndex = focus_on - (MAX_TEXTURES_IN_PREVIEW-2) / 2 ;
		for(new i = 1; i < MAX_TEXTURES_IN_PREVIEW - 1; i++)
		{
			// Kadangi nori pradët rodyt nuo 0 arba paskutinës, gali nebûti tinkami tekstûros indeksai.
			if(textureIndex < 0 || textureIndex >= GetFurnitureTextureCount())
			{
				textureIndex++;
				continue;
			}

			if(i == MAX_TEXTURES_IN_PREVIEW / 2)
			{
				GetXYInFrontOfPlayer(playerid, x, y, PlayerTexturePreview[ playerid ][ Distance ]);
				GetXYInFrontOfPlayer(playerid, tx, ty, PlayerTexturePreview[ playerid ][ Distance ]-0.1);
			}
			else if(i > MAX_TEXTURES_IN_PREVIEW / 2)
			{
				x = floatsin(angle, degrees) * 0.0 + floatcos(angle, degrees) * TEXTURE_PREVIEW_SPACING + x;
				y = floatcos(angle, degrees) * 0.0 - floatsin(angle, degrees) * TEXTURE_PREVIEW_SPACING + y;

				tx = floatsin(angle, degrees) * 0.0 + floatcos(angle, degrees) * TEXTURE_PREVIEW_SPACING + tx;
				ty = floatcos(angle, degrees) * 0.0 - floatsin(angle, degrees) * TEXTURE_PREVIEW_SPACING + ty;
			}
			else
			{
				x = floatsin(angle, degrees) * 0.0 + floatcos(angle, degrees) * -TEXTURE_PREVIEW_SPACING + x;
				y = floatcos(angle, degrees) * 0.0 - floatsin(angle, degrees) * -TEXTURE_PREVIEW_SPACING + y;

				tx = floatsin(angle, degrees) * 0.0 + floatcos(angle, degrees) * -TEXTURE_PREVIEW_SPACING + tx;
				ty = floatcos(angle, degrees) * 0.0 - floatsin(angle, degrees) * -TEXTURE_PREVIEW_SPACING + ty;
			}
			PlayerTexturePreview[ playerid ][ ObjectIDs ][ i ] = CreateDynamicObject(2257, x, y, z, 0.0, 0.0, rotZ, GetPlayerVirtualWorld(playerid), .playerid = playerid);
			SetDynamicObjectMaterial(PlayerTexturePreview[ playerid ][ ObjectIDs ][ i ], 1, FurnitureTextures[ textureIndex ][ ObjectModel ], FurnitureTextures[ textureIndex ][ TxdName ], FurnitureTextures[ textureIndex ][ TextureName ]);

			PlayerTexturePreview[ playerid ][ NumberObjectIDs ][ i ] = CreateDynamicObject(2257, tx, ty, z, 0.0, 0.0, rotZ, GetPlayerVirtualWorld(playerid), .playerid = playerid);
			format(s, sizeof(s), "%d.", textureIndex);
			SetDynamicObjectMaterialText(PlayerTexturePreview[ playerid ][ NumberObjectIDs ][ i ], 1, s, .fontcolor=0xFF000000);
			textureIndex++;
		}

		Streamer_Update(playerid);
		PlayerTexturePreview[ playerid ][ FocusedOn ] = focus_on;
		return 1;
	}

	// Jei einam toliau á deðinæ.
	if(focus_on > PlayerTexturePreview[ playerid ][ FocusedOn ])
	{
		// Jei focus_on lygus Count-1, reiðkia nori paskutinio objekto o tai reiðkia kad nebëra kà sukurti deðinëje
		if(focus_on < GetFurnitureTextureCount()-1)
		{
			GetDynamicObjectPos(PlayerTexturePreview[ playerid ][ ObjectIDs ][ MAX_TEXTURES_IN_PREVIEW-2 ], x, y, z);
			x = floatsin(angle, degrees) * 0.0 + floatcos(angle, degrees) * TEXTURE_PREVIEW_SPACING + x;
			y = floatcos(angle, degrees) * 0.0 - floatsin(angle, degrees) * TEXTURE_PREVIEW_SPACING + y;

			PlayerTexturePreview[ playerid ][ ObjectIDs ][ MAX_TEXTURES_IN_PREVIEW-1 ] = CreateDynamicObject(2257, x, y, z, 0.0, 0.0, rotZ, GetPlayerVirtualWorld(playerid), .playerid = playerid);
			SetDynamicObjectMaterial(PlayerTexturePreview[ playerid ][ ObjectIDs ][ MAX_TEXTURES_IN_PREVIEW-1 ], 1, FurnitureTextures[ focus_on+1 ][ ObjectModel ], FurnitureTextures[ focus_on+1 ][ TxdName ], FurnitureTextures[ focus_on+1 ][ TextureName ]);

			GetDynamicObjectPos(PlayerTexturePreview[ playerid ][ NumberObjectIDs ][ MAX_TEXTURES_IN_PREVIEW-2 ], x, y, z);
			x = floatsin(angle, degrees) * 0.0 + floatcos(angle, degrees) * TEXTURE_PREVIEW_SPACING + x;
			y = floatcos(angle, degrees) * 0.0 - floatsin(angle, degrees) * TEXTURE_PREVIEW_SPACING + y;

			PlayerTexturePreview[ playerid ][ NumberObjectIDs ][ MAX_TEXTURES_IN_PREVIEW-1 ] = CreateDynamicObject(2257, x, y, z, 0.0, 0.0, rotZ, GetPlayerVirtualWorld(playerid), .playerid = playerid);
			format(s, sizeof(s), "%d.", focus_on+1);
			SetDynamicObjectMaterialText(PlayerTexturePreview[ playerid ][ NumberObjectIDs ][ MAX_TEXTURES_IN_PREVIEW-1 ], 1, s, .fontcolor=0xFF000000);
		}

		for(new i = 1; i < MAX_TEXTURES_IN_PREVIEW; i++)
		{
			GetDynamicObjectPos(PlayerTexturePreview[ playerid ][ ObjectIDs ][ i ], x, y, z);
			x = floatsin(angle, degrees) * 0.0 + floatcos(angle, degrees) * -TEXTURE_PREVIEW_SPACING + x;
			y = floatcos(angle, degrees) * 0.0 - floatsin(angle, degrees) * -TEXTURE_PREVIEW_SPACING + y;
			MoveDynamicObject(PlayerTexturePreview[ playerid ][ ObjectIDs ][ i ], x, y, z, TEXTURE_PREVIEW_SPEED);

			GetDynamicObjectPos(PlayerTexturePreview[ playerid ][ NumberObjectIDs ][ i ], x, y, z);
			x = floatsin(angle, degrees) * 0.0 + floatcos(angle, degrees) * -TEXTURE_PREVIEW_SPACING + x;
			y = floatcos(angle, degrees) * 0.0 - floatsin(angle, degrees) * -TEXTURE_PREVIEW_SPACING + y;
			MoveDynamicObject(PlayerTexturePreview[ playerid ][ NumberObjectIDs ][ i ], x, y, z, TEXTURE_PREVIEW_SPEED);
		}
	}
	else if(focus_on < PlayerTexturePreview[ playerid ][ FocusedOn ])
	{
		// Jei focus_on lygus 0, tada tiesiog nekuriam naujo objekto kaireje.
		if(focus_on > 0)
		{
			GetDynamicObjectPos(PlayerTexturePreview[ playerid ][ ObjectIDs ][ 1 ], x, y, z);
			x = floatsin(angle, degrees) * 0.0 + floatcos(angle, degrees) * -TEXTURE_PREVIEW_SPACING + x;
			y = floatcos(angle, degrees) * 0.0 - floatsin(angle, degrees) * -TEXTURE_PREVIEW_SPACING + y;
			PlayerTexturePreview[ playerid ][ ObjectIDs ][ 0 ] = CreateDynamicObject(2257, x, y, z, 0.0, 0.0, rotZ, GetPlayerVirtualWorld(playerid), .playerid = playerid);
			SetDynamicObjectMaterial(PlayerTexturePreview[ playerid ][ ObjectIDs ][ 0 ], 1, FurnitureTextures[ focus_on-1 ][ ObjectModel ], FurnitureTextures[ focus_on-1 ][ TxdName ], FurnitureTextures[ focus_on-1 ][ TextureName ]);

			GetDynamicObjectPos(PlayerTexturePreview[ playerid ][ NumberObjectIDs ][ 1 ], x, y, z);
			x = floatsin(angle, degrees) * 0.0 + floatcos(angle, degrees) * -TEXTURE_PREVIEW_SPACING + x;
			y = floatcos(angle, degrees) * 0.0 - floatsin(angle, degrees) * -TEXTURE_PREVIEW_SPACING + y;
			PlayerTexturePreview[ playerid ][ NumberObjectIDs ][ 0 ] = CreateDynamicObject(2257, x, y, z, 0.0, 0.0, rotZ, GetPlayerVirtualWorld(playerid), .playerid = playerid);
			format(s, sizeof(s), "%d.", focus_on-1);
			SetDynamicObjectMaterialText(PlayerTexturePreview[ playerid ][ NumberObjectIDs ][ 0 ], 1, s, .fontcolor=0xFF000000);
		}

		for(new i = 0; i < MAX_TEXTURES_IN_PREVIEW-1; i++)
		{
			GetDynamicObjectPos(PlayerTexturePreview[ playerid ][ ObjectIDs ][ i ], x, y, z);
			x = floatsin(angle, degrees) * 0.0 + floatcos(angle, degrees) * TEXTURE_PREVIEW_SPACING + x;
			y = floatcos(angle, degrees) * 0.0 - floatsin(angle, degrees) * TEXTURE_PREVIEW_SPACING + y;
			MoveDynamicObject(PlayerTexturePreview[ playerid ][ ObjectIDs ][ i ], x, y, z, TEXTURE_PREVIEW_SPEED);

			GetDynamicObjectPos(PlayerTexturePreview[ playerid ][ NumberObjectIDs ][ i ], x, y, z);
			x = floatsin(angle, degrees) * 0.0 + floatcos(angle, degrees) * TEXTURE_PREVIEW_SPACING + x;
			y = floatcos(angle, degrees) * 0.0 - floatsin(angle, degrees) * TEXTURE_PREVIEW_SPACING + y;
			MoveDynamicObject(PlayerTexturePreview[ playerid ][ NumberObjectIDs ][ i ], x, y, z, TEXTURE_PREVIEW_SPEED);
		}
	}
	Streamer_Update(playerid);
	PlayerTexturePreview[ playerid ][ FocusedOn ] = focus_on;
	PlayerTexturePreview[ playerid ][ ObjectsMoving ] = true;
	return 1;
}

public OnDynamicObjectMoved(objectid)
{
	foreach(new i : Player)
	{
		if(!PlayerTexturePreview[ i ][ IsInPreview ] || !PlayerTexturePreview[ i ][ ObjectsMoving ])
			continue;


		// Jeigu baigë judët deiðinysis.(rodomi sekantys deðinëje þaidëjui)
		if(PlayerTexturePreview[ i ][ ObjectIDs ][ MAX_TEXTURES_IN_PREVIEW-1 ] == objectid || PlayerTexturePreview[ i ][ FocusedOn ] == GetFurnitureTextureCount()-1)
		{
			DestroyDynamicObject(PlayerTexturePreview[ i ][ ObjectIDs ][ 1 ]);
			DestroyDynamicObject(PlayerTexturePreview[ i ][ NumberObjectIDs ][ 1 ]);
			for(new j = 1; j < MAX_TEXTURES_IN_PREVIEW-1; j++)
			{
				PlayerTexturePreview[ i ][ ObjectIDs ][ j ] = PlayerTexturePreview[ i ][ ObjectIDs ][ j + 1];
				PlayerTexturePreview[ i ][ NumberObjectIDs ][ j ] = PlayerTexturePreview[ i ][ NumberObjectIDs ][ j + 1];
			}


			PlayerTexturePreview[ i ][ ObjectIDs ][ MAX_TEXTURES_IN_PREVIEW-1 ] = -1;
			PlayerTexturePreview[ i ][ NumberObjectIDs ][ MAX_TEXTURES_IN_PREVIEW-1 ] = -1;
			PlayerTexturePreview[ i ][ ObjectsMoving ] = false;
		}
		if(PlayerTexturePreview[ i ][ ObjectIDs ][ 0 ] == objectid || !PlayerTexturePreview[ i ][ FocusedOn ])
		{
			DestroyDynamicObject(PlayerTexturePreview[ i ][ ObjectIDs ][ MAX_TEXTURES_IN_PREVIEW-2 ]);
			DestroyDynamicObject(PlayerTexturePreview[ i ][ NumberObjectIDs ][ MAX_TEXTURES_IN_PREVIEW-2 ]);
			for(new j = MAX_TEXTURES_IN_PREVIEW-2; j != 0; j--)
			{
				PlayerTexturePreview[ i ][ ObjectIDs ][ j ] = PlayerTexturePreview[ i ][ ObjectIDs ][ j - 1];
				PlayerTexturePreview[ i ][ NumberObjectIDs ][ j ] = PlayerTexturePreview[ i ][ NumberObjectIDs ][ j - 1];
			}

			PlayerTexturePreview[ i ][ ObjectIDs ][ 0 ] = -1;
			PlayerTexturePreview[ i ][ NumberObjectIDs ][ 0 ] = -1;
			PlayerTexturePreview[ i ][ ObjectsMoving ] = false;
		}
		return 1;
	}
	#if defined furniture_OnDynamicObjectMoved
		furniture_OnDynamicObjectMoved(objectid);
	#endif
	return 0;
}
#if defined _ALS_OnDynamicObjectMoved
	#undef OnDynamicObjectMoved
#else
	#define _ALS_OnDynamicObjectMoved
#endif
#define OnDynamicObjectMoved furniture_OnDynamicObjectMoved
#if defined furniture_OnDynamicObjectMoved
	forward furniture_OnDynamicObjectMoved(objectid);
#endif

hook OnPlayerKeyStateChange(playerid, newkeys, oldkeys)
{
	if(!PlayerTexturePreview[ playerid ][ IsInPreview ] || PlayerTexturePreview[ playerid ][ ObjectsMoving ])
		return 1;

	if((newkeys & KEY_ANALOG_LEFT) && !(oldkeys & KEY_ANALOG_LEFT))
	{
		FurnitureTexturesFocusOn(playerid, PlayerTexturePreview[ playerid ][ FocusedOn ] - 1);
		return 1;
	}
	if((newkeys & KEY_ANALOG_RIGHT) && !(oldkeys & KEY_ANALOG_RIGHT))
	{
		FurnitureTexturesFocusOn(playerid, PlayerTexturePreview[ playerid ][ FocusedOn ] + 1);
		return 1;
	}
	return 0;
}

hook OnPlayerDisconnect(playerid, reason)
{
	static null[ E_FURNITURE_TEXTURE_PREVIEW ];
	PlayerTexturePreview[ playerid ] = null;
	return 1;
}


CMD:ftexture(playerid, params[])
{
	if(isnull(params))
    {
        ftexture_help:
        SendClientMessage(playerid, COLOR_LIGHTRED, "_________________________Baldø tekstûrø komandos__________________________");
        SendClientMessage(playerid,COLOR_LIGHTRED2, "  KOMANDOS NAUDOJIMAS: /ftexture [komanda], pavyzdþiui: /ftexture goto");
        SendClientMessage(playerid,GRAD,"  PAGRINDINES: goto, select, cancel, distance");
        return 1;
    }

    if(!PlayerTexturePreview[ playerid ][ IsInPreview ])
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðia komandà galite naudoti tik perþiûrinëdami tekstûras.");

    // Su dideliu TEXTURE_PREVIEW_SPEED abejoju ar spës paraðyti komandà kol ObjectsMoving=true
    // "Didelis greitis" > 10.0
    if(PlayerTexturePreview[ playerid ][ ObjectsMoving ])
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalima naudoti ðios komandos kol juda objektai.");

    new action[ 32 ];
    if(strfind(params, " ") != -1)
    {
    	strmid(action, params, 0, strfind(params, " "));
    	strdel(params, 0, strfind(params, " "));
    }
    else
    	strcat(action, params);

    if(!strcmp(action, "select", true))
    {
    	new index = PlayerTexturePreview[ playerid ][ FocusedOn ];
    	CallLocalFunction("OnPlayerSelectTexture", "iissi", playerid, FurnitureTextures[ index ][ ObjectModel ], FurnitureTextures[ index ][ TxdName ], FurnitureTextures[ index ][ TextureName ],
    		PlayerTexturePreview[ playerid ][ ExtraId ]);
    	StopFurnitureTexturePreview(playerid);
    }
    else if(!strcmp(action, "cancel", true))
    {
    	CallLocalFunction("OnPlayerCancelTexturePreview", "ii", playerid, PlayerTexturePreview[ playerid ][ ExtraId ]);
    	StopFurnitureTexturePreview(playerid);
    }
    else if(!strcmp(action, "goto", true))
    {
    	new index, string[80];
    	if(sscanf(params, "i", index))
    		return SendClientMessage(playerid, COLOR_LIGHTRED, "Naudojimas /ftexture goto [Teksturos numeris]");
    	if(index < 0 || index > GetFurnitureTextureCount()-1)
    	{
    		format(string, sizeof(string),"Tekstûros numeris negali bûti maþesnis uþ 0 ar didesnis uþ %d", GetFurnitureTextureCount()-1);
    		SendClientMessage(playerid, COLOR_LIGHTRED, string);
    		return 1;
    	}
    	FurnitureTexturesFocusOn(playerid, index);
    }
    else if(!strcmp(action, "distance"))
    {
    	new Float:distance;
    	if(sscanf(params,"f", distance))
    		return SendClientMessage(playerid, COLOR_LIGHTRED, "Naudojimas /ftexture distance [Atstumas]");
    	if(distance < 1.0 || distance > 10.0)
    		return SendClientMessage(playerid, COLOR_LIGHTRED, "Atstumas negali bûti maþesnis uþ 1.0 ar didesnis uþ 10.0");

    	PlayerTexturePreview[ playerid ][ Distance ] = distance;
    	FurnitureTextureReshow(playerid);
    }
    else
    	goto ftexture_help;
    return 1;
}

forward OnPlayerSelectTexture(playerid, modelid, txdname[], texturename[], extraid);
forward OnPlayerCancelTexturePreview(playerid, extraid);