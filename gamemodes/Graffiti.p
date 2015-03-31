#include <YSI\y_hooks>


#define GRAFFITI_BACKROUND_OBJECT		8330
#define MAX_GRAFFITI 					100
#define GRAFITTI_SPRAY_RIGHT_TIME_MIN 	5
#define GRAFFITI_OBJECT_MATERIAL_SIZE 	OBJECT_MATERIAL_SIZE_512x256
#define GRAFFITI_TEXT_BOLD 				true
#define MAX_GRAFFITI_TEXT 				16

#define DIALOG_GRAFFITI_TEXT 			7500
#define DIALOG_GRAFFITI_FONT_NAME 		7501
#define DIALOG_GRAFFITI_FONT_SIZE 		7502
#define DIALOG_GRAFFITI_COLOUR 			7503


/*
	CREATE TABLE IF NOT EXISTS graffiti_fonts 
	(
		id INT AUTO_INCREMENT NOT NULL,
		name VARCHAR(64) NOT NULL,
		size TINYINT UNSIGNED NOT NULL,
		PRIMARY KEY(id)
	) ENGINE=INNODB DEFAULT CHARSET cp1257 COLLATE=cp1257_bin; 	

	CREATE TABLE IF NOT EXISTS graffiti_colours 
	(
		id INT AUTO_INCREMENT NOT NULL,
		name VARCHAR(32) NOT NULL,
		argb VARCHAR(8) NOT NULL,
		PRIMARY KEY(id)
	) ENGINE=INNODB DEFAULT CHARSET cp1257 COLLATE=cp1257_bin; 	

	CREATE TABLE IF NOT EXISTS graffiti (
		id INT AUTO_INCREMENT NOT NULL,
		author INT NOT NULL,
		`text` VARCHAR(128) NOT NULL,
		pos_x FLOAT NOT NULL,
		pos_y FLOAT NOT NULL,
		pos_z FLOAT NOT NULL,
		rot_x FLOAT NOT NULL,
		rot_y FLOAT NOT NULL,
		rot_z FLOAT NOT NULL,
		font_id INT NOT NULL,
		colour_id INT NOT NULL,
		PRIMARY KEY(id),
		INDEX(author),
		INDEX(font_id),
		INDEX(colour_id)
	) ENGINE=INNODB DEFAULT CHARSET cp1257 COLLATE=cp1257_bin; 	

	ALTER TABLE graffiti ADD FOREIGN KEY(author) REFERENCES players(id) ON DELETE CASCADE;
	ALTER TABLE graffiti ADD FOREIGN KEY(font_id) REFERENCES graffiti_fonts(id) ON DELETE CASCADE;
	ALTER TABLE graffiti ADD FOREIGN KEY(colour_id) REFERENCES graffiti_colours(id) ON DELETE CASCADE;

*/

enum E_PLAYER_GRAFFITI_DATA 
{
	Text[ MAX_GRAFFITI_TEXT ],
	FontName[ 32 ],
	FontSize,
	Colour,
	ColourId,
	ObjectId,
};

enum E_GRAFITTI_DATA 
{
	SqlId,
	ObjectId,
};	

static GraffitiData[ MAX_GRAFFITI ][ E_GRAFITTI_DATA ],
	Timer:AllowGraffitiTimer[ MAX_PLAYERS ],
	bool:IsGraffitiAllowed[ MAX_PLAYERS ],
	PlayerGraffiti[ MAX_PLAYERS ][ E_PLAYER_GRAFFITI_DATA ];


forward OnGraffitiLoad();
forward OnGraffitiFontNameListLoad(playerid);
forward OnGraffitiFontSizeListLoad(playerid);
forward OnGraffitiColourListLoad(playerid);

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
	#if defined graffiti_OnGameModeInit
		graffiti_OnGameModeInit();
	#endif
	mysql_pquery(DbHandle, 
		"SELECT \
			graffiti.*, graffiti_fonts.name, graffiti_fonts.size, CONV(graffiti_colours.argb, 16, 10) AS argb \
		FROM graffiti \
		JOIN graffiti_fonts ON graffiti.font_id = graffiti_fonts.id \
		JOIN graffiti_colours ON graffiti.colour_id = graffiti_colours.id", 
		"OnGraffitiLoad");
}
#if defined _ALS_OnGameModeInit
	#undef OnGameModeInit
#else 
	#define _ALS_OnGameModeInit
#endif
#define OnGameModeInit 					graffiti_OnGameModeInit
#if defined graffiti_OnGameModeInit
	forward graffiti_OnGameModeInit();
#endif


public OnGraffitiLoad()
{
	new text[ MAX_GRAFFITI_TEXT ], fontname[ 64 ], colour;
	for(new i = 0; i < cache_get_row_count(); i++)
	{
		if(i >= MAX_GRAFFITI)
		{
			ErrorLog("Number of rows(%d) in table 'graffiti' exceeds MAX_GRAFFITI(" #MAX_GRAFFITI ")", cache_get_row_count());
			break;
		}

		GraffitiData[ i ][ SqlId ] = cache_get_field_content_int(i, "id");
		GraffitiData[ i ][ ObjectId ] = CreateDynamicObject(
				GRAFFITI_BACKROUND_OBJECT,
				cache_get_field_content_float(i, "pos_x"),
				cache_get_field_content_float(i, "pos_y"),
				cache_get_field_content_float(i, "pos_z"),
				cache_get_field_content_float(i, "rot_x"),
				cache_get_field_content_float(i, "rot_y"),
				cache_get_field_content_float(i, "rot_z")
			);

		cache_get_field_content(i, "text", text);
		cache_get_field_content(i, "argb", fontname);
		colour = strval(fontname);
		cache_get_field_content(i, "name", fontname);

		SetDynamicObjectMaterialText(
				GraffitiData[ i ][ ObjectId ], 
				0,
				text,
				GRAFFITI_OBJECT_MATERIAL_SIZE,
				fontname,
				cache_get_field_content_int(i, "size"),
				GRAFFITI_TEXT_BOLD,
				colour,
				0x00000000,
				OBJECT_MATERIAL_TEXT_ALIGN_CENTER
			);
		SetDynamicObjectMaterialText(GraffitiData[ i ][ ObjectId ], 1, " ");
	}
	return 1;
}


hook OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
	switch(dialogid)
	{
		case DIALOG_GRAFFITI_COLOUR:
		{
			if(!response)
				return 1;

			new query[110], Cache:result;
			mysql_format(DbHandle, query, sizeof(query), "SELECT id, CONVERT(CONV(argb, 16, 10), SIGNED) AS argb FROM graffiti_colours WHERE name = '%e'", inputtext);
			result = mysql_query(DbHandle, query);
			if(cache_get_row_count())
			{
				cache_get_field_content(0, "argb", query);
				PlayerGraffiti[ playerid ][ Colour ] = strval(query);
				//PlayerGraffiti[ playerid ][ Colour ] = cache_get_field_content_int(0, "argb");
				PlayerGraffiti[ playerid ][ ColourId ] = cache_get_field_content_int(0, "id");
			}
			cache_delete(result);
			UpdatePlayerGraffiti(playerid);
			return 1;
		}
		case DIALOG_GRAFFITI_TEXT:
		{
			if(!response)
				return 1;
		
			if(isnull(inputtext) || strlen(inputtext) >= MAX_GRAFFITI_TEXT)
				return ShowPlayerGraffitiNameDialog(playerid, "Tekstas sudaryti negali maþiau nei 1 simbolis ar daugiau nei " #MAX_GRAFFITI_TEXT);

			format(PlayerGraffiti[ playerid ][ Text ], MAX_GRAFFITI_TEXT, inputtext);
			UpdatePlayerGraffiti(playerid);
			return 1;
		}
		case DIALOG_GRAFFITI_FONT_NAME:
		{
			if(!response)
				return 1;

			format(PlayerGraffiti[ playerid ][ FontName ], 32, inputtext);

			// Pakeitus ðriftà reikia rasti jam tinkamà dydá.
			new query[80], Cache:result;
			mysql_format(DbHandle, query, sizeof(query), "SELECT size FROM graffiti_fonts WHERE name = '%e'", inputtext);
			result = mysql_query(DbHandle, query);
			if(cache_get_row_count())
				PlayerGraffiti[ playerid ][ FontSize ] = cache_get_field_content_int(0, "size");
			cache_delete(result);

			UpdatePlayerGraffiti(playerid);
			return 1;
		}
		case DIALOG_GRAFFITI_FONT_SIZE:
		{
			if(!response)
				return 1;

			// Èia GUI yra sàraðas skaièiø ið duombazës, todël strval
			new fontsize = strval(inputtext);

			PlayerGraffiti[ playerid ][ FontSize ] = fontsize;
			UpdatePlayerGraffiti(playerid);
			return 1;
		}
	}
	GraffitiManagementDialog.OnDialogResponse(playerid, dialogid, response, listitem, inputtext);
	return 0;
}


public OnPlayerEditDynamicObject(playerid, objectid, response, Float:x, Float:y, Float:z, Float:rx, Float:ry, Float:rz)
{
	#if defined graff_OnPlayerEditDynamicObject
		graff_OnPlayerEditDynamicObject(playerid, objectid, response, x, y, z, rx, ry, rz);
	#endif

	if(PlayerGraffiti[ playerid ][ ObjectId ] == objectid && response == EDIT_RESPONSE_FINAL)
	{
		SetDynamicObjectPos(PlayerGraffiti[ playerid ][ ObjectId ], x ,y ,z);
		SetDynamicObjectRot(PlayerGraffiti[ playerid ][ ObjectId ], rx, ry, rz);
	}
	/*
	new query[200];
	for(new i = 0; i < MAX_GRAFFITI; i++)
	{
		if(GraffitiData[ i ][ ObjectId ] == objectid)
		{
			// Atnaujinam pozicijà ir baigiam kûrimà þaidëjui ðitø visø.
			if(response == EDIT_RESPONSE_FINAL)
			{
				mysql_format(DbHandle, query, sizeof(query), "UPDATE graffiti SET pos_x = %f, pos_y = %f, pos_z = %f, rot_x = %f, rot_y = %f, rot_z = %f WHERE id = %d",
					x, y, z, rx, ry, rz, GraffitiData[ i ][ SqlId ]);
				mysql_pquery(DbHandle, query);

				IsGraffitiAllowed[ playerid ] = false;
				stop AllowGraffitiTimer[ playerid ];
				SendClientMessage(playerid, COLOR_NEWS, "Grafiti sëkmingai sukurtas.");
			}
			// Iðtrinam
			else if(response == EDIT_RESPONSE_CANCEL)
			{
				mysql_format(DbHandle, query, sizeof(query), "DELETE FROM graffiti WHERE id = %d LIMIT 1", GraffitiData[ i ][ SqlId ]);
				mysql_pquery(DbHandle, query);
				DestroyDynamicObject(GraffitiData[ i ][ ObjectId ]);
				GraffitiData[ i ][ ObjectId ] = 0;
				GraffitiData[ i ][ SqlId ] = 0;
				SendClientMessage(playerid, COLOR_NEWS, "Grafiti sëkmingai paðalintas.");
			}
			DeletePVar(playerid, "NewGraffiti.Name");
			DeletePVar(playerid, "NewGraffiti.Colour");
			DeletePVar(playerid, "NewGraffiti.FontName");
		}
	}
	*/
	return 0;
}
#if defined _ALS_OnPlayerEditDynamicObject
	#undef OnPlayerEditDynamicObject
#else 
	#define _ALS_OnPlayerEditDynamicObject
#endif
#define OnPlayerEditDynamicObject graff_OnPlayerEditDynamicObject
#if defined graff_OnPlayerEditDynamicObject
	forward graff_OnPlayerEditDynamicObject(playerid, objectid, response, Float:x, Float:y, Float:z, Float:rx, Float:ry, Float:rz);
#endif



hook OnPlayerDisconnect(playerid)
{
	EndPlayerGraffitiSpray(playerid);
}

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


static stock GetFreeGraffitiIndex()
{
	for(new i = 0; i < MAX_GRAFFITI; i++)	
		if(!GraffitiData[ i ][ SqlId ])
			return i;
	return -1;
}


stock GetGraffitiColourARGB(colourname[])
{
	new query[120], Cache:result, colour;
	mysql_format(DbHandle, query, sizeof(query), "SELECT CONV(argb, 16, 10) AS argb FROM graffiti_colours WHERE name = '%e'", colourname);
	result = mysql_query(DbHandle, query);
	if(cache_get_row_count())
	{
		cache_get_field_content(0, "argb", query);
		colour = strval(query);
	}
	cache_delete(result);
	return colour;
}


stock GetGraffitiFontId(fontname[], fontsize)
{
	new query[120], Cache:result, fontid;
	mysql_format(DbHandle, query, sizeof(query), "SELECT id FROM graffiti_fonts WHERE name = '%e' AND size = %d", fontname, fontsize);
	result = mysql_query(DbHandle, query);
	if(cache_get_row_count())
		fontid = cache_get_field_content_int(0, "id");
	cache_delete(result);
	return fontid;
}


AddGraffiti(sqlid, text[], Float:x, Float:y, Float:z, Float:rotx, Float:roty, Float:rotz, fontid, colourid)
{
	new query[256], Cache:result;

	mysql_format(DbHandle, query, sizeof(query), "INSERT INTO graffiti (author, `text`, pos_x, pos_y, pos_z, rot_x, rot_y, rot_z, font_id, colour_id) VALUES (%d, '%e', %f, %f, %f, %f, %f, %f, %d, %d)",
		sqlid, text, x, y, z, rotx, roty, rotz, fontid, colourid);

	result = mysql_query(DbHandle, query);
	sqlid = cache_insert_id();
	cache_delete(result);
	return sqlid;
}

stock IsPlayerGraffitiAuthor(playerid, graffitiindex)
{
	new query[60], Cache:result, bool:isauthor = false;

	mysql_format(DbHandle, query, sizeof(query), "SELECT author FROM graffiti WHERE id = %d",
		GraffitiData[ graffitiindex ][ SqlId ]);

	result = mysql_query(DbHandle, query);
	if(cache_get_row_count())
		if(cache_get_field_content_int(0, "author") == GetPlayerSqlId(playerid))
			isauthor = true;
	cache_delete(result);
	return isauthor;
}

stock UpdatePlayerGraffiti(playerid)
{
	// Jei dar nebuvo sukurtas objektas, reiðkia kad tik pradëta.
	if(!IsValidDynamicObject(PlayerGraffiti[ playerid ][ ObjectId ]))
	{
		new Float:x, Float:y, Float:z, Float:angle, Cache:result, tmp[16];
		GetPlayerPos(playerid, x, y, z);
		GetXYInFrontOfPlayer(playerid, x, y, 2.5);
		GetPlayerFacingAngle(playerid, angle);

		PlayerGraffiti[ playerid ][ ObjectId ] = CreateDynamicObject(GRAFFITI_BACKROUND_OBJECT, x, y, z, 0.0, 0.0, angle, .playerid = playerid);
		SetDynamicObjectMaterialText(PlayerGraffiti[ playerid ][ ObjectId ], 1, " ");

		if(isnull(PlayerGraffiti[ playerid ][ FontName ]))
		{
			result = mysql_query(DbHandle,"SELECT * FROM graffiti_fonts LIMIT 1");
			if(cache_get_row_count())
			{
				cache_get_field_content(0, "name", PlayerGraffiti[ playerid ][ FontName ], DbHandle, 32);
				PlayerGraffiti[ playerid ][ FontSize ] = cache_get_field_content_int(0, "size");
			}
			cache_delete(result);
		}

		if(isnull(PlayerGraffiti[ playerid ][ Text ]))
			strcat(PlayerGraffiti[ playerid ][ Text ], "Tekstas", 8);

		if(!PlayerGraffiti[ playerid ][ Colour ])
		{
			PlayerGraffiti[ playerid ][ Colour ] = 0xFFFFFFFF;
			result = mysql_query(DbHandle, "SELECT id, CONVERT(CONV(argb, 16, 10), SIGNED) AS argb FROM graffiti_colours LIMIT 1");
			if(cache_get_row_count())
			{
				PlayerGraffiti[ playerid ][ ColourId ] = cache_get_field_content_int(0, "id");
				cache_get_field_content(0, "argb", tmp);
				PlayerGraffiti[ playerid ][ Colour ] = strval(tmp);
				printf("ERROR?%d", cache_get_field_content_int(0, "argb"));
			}
			cache_delete(result);
		}
	}

	SetDynamicObjectMaterialText(PlayerGraffiti[ playerid ][ ObjectId ], 0, 
		PlayerGraffiti[ playerid ][ Text ], 
		GRAFFITI_OBJECT_MATERIAL_SIZE, 
		PlayerGraffiti[ playerid ][ FontName ], 
		PlayerGraffiti[ playerid ][ FontSize ], 
		GRAFFITI_TEXT_BOLD,
		PlayerGraffiti[ playerid ][ Colour ], 
		0x00000000, 
		OBJECT_MATERIAL_TEXT_ALIGN_CENTER);

	Streamer_Update(playerid);
	cmd_ame(playerid, "kaþkà paiðo ant sienos");
}

stock EndPlayerGraffitiSpray(playerid)
{
	stop AllowGraffitiTimer[ playerid ];
	IsGraffitiAllowed[ playerid ] = false;

	if(IsValidDynamicObject(PlayerGraffiti[ playerid ][ ObjectId ]))
		DestroyDynamicObject(PlayerGraffiti[ playerid ][ ObjectId ]);

	static EmptyPlayerGraffiti[ E_PLAYER_GRAFFITI_DATA ];
	PlayerGraffiti[ playerid ] = EmptyPlayerGraffiti;
}

/* 			                                                                                                                                      
	               ,,             ,,                             ,...                                      ,,                             
	`7MM"""Yb.     db           `7MM                           .d' ""                               mm     db                             
	  MM    `Yb.                  MM                           dM`                                  MM                                    
	  MM     `Mb `7MM   ,6"Yb.    MM  ,pW"Wq.   .P"Ybmmm      mMMmm`7MM  `7MM  `7MMpMMMb.  ,p6"bo mmMMmm `7MM  ,pW"Wq.`7MMpMMMb.  ,pP"Ybd 
	  MM      MM   MM  8)   MM    MM 6W'   `Wb :MI  I8         MM    MM    MM    MM    MM 6M'  OO   MM     MM 6W'   `Wb MM    MM  8I   `" 
	  MM     ,MP   MM   ,pm9MM    MM 8M     M8  WmmmP"         MM    MM    MM    MM    MM 8M        MM     MM 8M     M8 MM    MM  `YMMMa. 
	  MM    ,dP'   MM  8M   MM    MM YA.   ,A9 8M              MM    MM    MM    MM    MM YM.    ,  MM     MM YA.   ,A9 MM    MM  L.   I8 
	.JMMmmmdP'   .JMML.`Moo9^Yo..JMML.`Ybmd9'   YMMMMMb      .JMML.  `Mbod"YML..JMML  JMML.YMbmd'   `Mbmo.JMML.`Ybmd9'.JMML  JMML.M9mmmP' 
	                                           6'     dP                                                                                  
	                                           Ybmmmd'                                                                                     */


ShowPlayerGraffitiColourDialog(playerid)
{
	return mysql_pquery(DbHandle, "SELECT name, argb FROM graffiti_colours", "OnGraffitiColourListLoad", "i", playerid);
}	                   

public OnGraffitiColourListLoad(playerid)
{
	new string[ 1024 ], name[ 32 ], colour[ 9 ];
	for(new i = 0; i < cache_get_row_count(); i++)
	{
		cache_get_field_content(i, "name", name);
		cache_get_field_content(i, "argb", colour);

		format(string, sizeof(string),"%s{%s}%s\n", string, colour[ 2 ], name);
	}
	ShowPlayerDialog(playerid, DIALOG_GRAFFITI_COLOUR, DIALOG_STYLE_LIST, "Grafiti teksto spalva", string, "Tæsti", "Iðeiti");
	return 1;
}                     

ShowPlayerGraffitiNameDialog(playerid, errostr[] = "")
{
	new string[128];
	format(string, sizeof(string),"{661111}%s\n{FFFFFF}Áveskite grafiti tekstà.", errostr);
	ShowPlayerDialog(playerid, DIALOG_GRAFFITI_TEXT, DIALOG_STYLE_INPUT, "Grafiti tekstas", string, "Tæsti", "Iðeiti");
	return 1;
}


ShowPlayerGraffitiFontNamDialog(playerid)
{
	return mysql_pquery(DbHandle, "SELECT DISTINCT name FROM graffiti_fonts", "OnGraffitiFontNameListLoad", "i", playerid);
}

public OnGraffitiFontNameListLoad(playerid)
{
	new string[ 1024 ], fontname[ 64 ];
	for(new i = 0; i < cache_get_row_count(); i++)
	{
		cache_get_field_content(i, "name", fontname);
		strcat(string, fontname);
		strcat(string, "\n");
	}
	ShowPlayerDialog(playerid, DIALOG_GRAFFITI_FONT_NAME, DIALOG_STYLE_LIST, "Grafiti teksto ðriftas", string, "Tæsti", "Iðeiti");
	return 1;
}

ShowPlayerGraffitiFontSizDialog(playerid, fontname[])
{
	new query[120];
	mysql_format(DbHandle, query, sizeof(query), "SELECT size FROM graffiti_fonts WHERE name = '%e'", fontname);
	return mysql_pquery(DbHandle, query, "OnGraffitiFontSizeListLoad", "i", playerid);
}

public OnGraffitiFontSizeListLoad(playerid)
{
	new string[ 1024 ];
	for(new i = 0; i < cache_get_row_count(); i++)
	{
		format(string, sizeof(string), "%s%d\n", string, cache_get_field_content_int(i, "size"));
	}
	ShowPlayerDialog(playerid, DIALOG_GRAFFITI_FONT_SIZE, DIALOG_STYLE_LIST, "Grafiti teksto ðrifto dydis", string, "Tæsti", "Iðeiti");
	return 1;
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

CMD:spray(playerid, params[])
{
	if(isnull(params))
    {
        spray_help:
        SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /spray [veiksmas]");
        SendClientMessage(playerid, COLOR_WHITE, "GALIMI VEIKSMAI: create, delete, pos, text, font, size, colour, save");
        if(IsPlayerAdmin(playerid) || GetPlayerAdminLevel(playerid) >= 3)
        	SendClientMessage(playerid, COLOR_FADE2, "Galimi administratoriaus veiksmai: allow, dissallow");
        return 1;
    }

    new action[16],
    	string[128];
    
    if(strfind(params, " ") != -1)
        strmid(action, params, 0, strfind(params, " "));
    else 
        strmid(action, params, 0, strlen(params));
    strdel(params, 0, strlen(action));


    if(!strcmp(action, "allow", true))
    {
    	new targetid;
    	if(!IsPlayerAdmin(playerid) && GetPlayerAdminLevel(playerid) < 4)
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðià komandà galima naudoti tik nuo 4 administratoriaus lygio.");

    	else if(sscanf(params, "u", targetid))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /spray allow [Þaidëjo ID/Dalis vardo] ");
    	else if(!IsPlayerConnected(targetid))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Tokio þaidëjo nëra!");
    	else if(IsGraffitiAllowed[ targetid ])
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðiam þaidëjui jau leista kurti grafiti. Norëdami paðalinti ðá leidimà naudokite /spray dissallow");

    	else 
    	{
    		IsGraffitiAllowed[ targetid ] = true;
    		AllowGraffitiTimer[ targetid ] = defer GraffitiSprayTimeEnd(targetid);

    		format(string, sizeof(string), "Administratorius %s suteikë jums leidimà pieðti grafiti. Ðis leidimas galios " #GRAFITTI_SPRAY_RIGHT_TIME_MIN " minutes.", GetName(playerid));
    		SendClientMessage(targetid, COLOR_WHITE, string);

    		format(string, sizeof(string), "Þaidëjui %s leidimas pieðti grafiti suteiktas. Nepamirðkite patikrinti jo darbo.", GetName(targetid));
    		SendClientMessage(playerid, COLOR_NEWS, string);
    	}
    }
    else if(!strcmp(action, "dissallow"))
    {
    	new targetid;

    	if(!IsPlayerAdmin(playerid) && GetPlayerAdminLevel(playerid) < 4)
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðià komandà galima naudoti tik nuo 4 administratoriaus lygio.");
    	else if(sscanf(params, "u", targetid))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /spray dissallow [Þaidëjo ID/Dalis vardo] ");
    	else if(!IsPlayerConnected(targetid))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, tokio þaidëjo nëra!");
    	else if(!IsGraffitiAllowed[ targetid ])
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðis þaidëjas neturi teisës kurti grafiti, todël nëra kà atimti.");

    	else 
    	{
    		IsGraffitiAllowed[ targetid ] = false;
    		stop AllowGraffitiTimer[ targetid ];

    		format(string, sizeof(string), "Administratorius %s atëmë ið jûsø galimybæ pieðti grafiti", GetName(playerid));
    		SendClientMessage(targetid, COLOR_WHITE, string);

    		format(string, sizeof(string), "Þaidëjo %s leidimas pieðti grafiti atimtas.", GetName(targetid));
    		SendClientMessage(playerid, COLOR_NEWS, string);
    	}
    }
    else if(!strcmp(action, "create", true))
    {
    	if(IsValidDynamicObject(PlayerGraffiti[ playerid ][ ObjectId ]))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs jau pradëjote pieðti grafiti.");

    	else if(!IsGraffitiAllowed[ playerid ])
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jums nëra suteikta galimybë kurti grafiti. Jà suteikti gali administratorius.");

    	else if(GetPlayerWeapon(playerid) != WEAPON_SPRAYCAN && !IsItemInPlayerInventory(playerid, WEAPON_SPRAYCAN))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neturite daþø.");

    	else if(IsPlayerInAnyInterior(playerid))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, grafiti pieðti galima tik lauke.");

    	else ShowPlayerGraffitiNameDialog(playerid);
    }
    else if(!strcmp(action, "text", true))
    {
    	if(!IsValidDynamicObject(PlayerGraffiti[ playerid ][ ObjectId ]))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neesate pradëjæs pieðti grafiti.");

    	else if(!IsGraffitiAllowed[ playerid ])
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jums nëra suteikta galimybë kurti grafiti. Jà suteikti gali administratorius.");

    	else if(GetPlayerWeapon(playerid) != WEAPON_SPRAYCAN && !IsItemInPlayerInventory(playerid, WEAPON_SPRAYCAN))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neturite daþø.");

    	else
    		ShowPlayerGraffitiNameDialog(playerid);
    }
    else if(!strcmp(action, "font", true))
    {
    	if(!IsValidDynamicObject(PlayerGraffiti[ playerid ][ ObjectId ]))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neesate pradëjæs pieðti grafiti.");

    	else if(!IsGraffitiAllowed[ playerid ])
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jums nëra suteikta galimybë kurti grafiti. Jà suteikti gali administratorius.");

    	else if(GetPlayerWeapon(playerid) != WEAPON_SPRAYCAN && !IsItemInPlayerInventory(playerid, WEAPON_SPRAYCAN))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neturite daþø.");

    	else 
    		ShowPlayerGraffitiFontNamDialog(playerid);
    }
    else if(!strcmp(action, "size", true))
    {
    	if(!IsValidDynamicObject(PlayerGraffiti[ playerid ][ ObjectId ]))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neesate pradëjæs pieðti grafiti.");

    	else if(!IsGraffitiAllowed[ playerid ])
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jums nëra suteikta galimybë kurti grafiti. Jà suteikti gali administratorius.");

    	else if(GetPlayerWeapon(playerid) != WEAPON_SPRAYCAN && !IsItemInPlayerInventory(playerid, WEAPON_SPRAYCAN))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neturite daþø.");

    	else if(isnull(PlayerGraffiti[ playerid ][ FontName ]))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, pirmiausia pasirinktite ðriftà.");

    	else 
    		ShowPlayerGraffitiFontSizDialog(playerid, PlayerGraffiti[ playerid ][ FontName ]);
    }
    else if(!strcmp(action, "color", true) || !strcmp(action, "colour", true))
    {
    	if(!IsValidDynamicObject(PlayerGraffiti[ playerid ][ ObjectId ]))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neesate pradëjæs pieðti grafiti.");

    	else if(!IsGraffitiAllowed[ playerid ])
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jums nëra suteikta galimybë kurti grafiti. Jà suteikti gali administratorius.");

    	else if(GetPlayerWeapon(playerid) != WEAPON_SPRAYCAN && !IsItemInPlayerInventory(playerid, WEAPON_SPRAYCAN))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neturite daþø.");

    	else 
    		ShowPlayerGraffitiColourDialog(playerid);
    }
    else if(!strcmp(action, "pos"))
    {
    	if(!IsValidDynamicObject(PlayerGraffiti[ playerid ][ ObjectId ]))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neesate pradëjæs pieðti grafiti.");

    	else if(!IsGraffitiAllowed[ playerid ])
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jums nëra suteikta galimybë kurti grafiti. Jà suteikti gali administratorius.");

    	else if(GetPlayerWeapon(playerid) != WEAPON_SPRAYCAN && !IsItemInPlayerInventory(playerid, WEAPON_SPRAYCAN))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neturite daþø.");

    	else 
    		EditDynamicObject(playerid, PlayerGraffiti[ playerid ][ ObjectId ]);
    }
    else if(!strcmp(action, "save", true))
    {
    	if(!IsValidDynamicObject(PlayerGraffiti[ playerid ][ ObjectId ]))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neesate pradëjæs pieðti grafiti.");

    	else
    	{
    		new Float:x, Float:y, Float:z,
				Float:rx, Float:ry, Float:rz,
				freeindex = GetFreeGraffitiIndex()
			;

			if(freeindex == -1)
			{
				ErrorLog("Graffiti limit(" #MAX_GRAFFITI ") reached.");
				SendClientMessage(playerid, COLOR_LIGHTRED, "Ávyko klaida. Ðiuo metu sukurti grafiti neámanoma.");
			}
			else 
			{
				GetDynamicObjectPos(PlayerGraffiti[ playerid ][ ObjectId ], x, y, z);
				GetDynamicObjectRot(PlayerGraffiti[ playerid ][ ObjectId ], rx, ry, rz);

				GraffitiData[ freeindex ][ ObjectId ] = CreateDynamicObject(GRAFFITI_BACKROUND_OBJECT,x, y, z,rx, ry, rz);
				SetDynamicObjectMaterialText(
					GraffitiData[ freeindex ][ ObjectId ], 
					0,
					PlayerGraffiti[ playerid ][ Text ],
					GRAFFITI_OBJECT_MATERIAL_SIZE,
					PlayerGraffiti[ playerid ][ FontName ],
					PlayerGraffiti[ playerid ][ FontSize ],
					GRAFFITI_TEXT_BOLD,
					PlayerGraffiti[ playerid ][ Colour ],
					0x00000000,
					OBJECT_MATERIAL_TEXT_ALIGN_CENTER
				);
				SetDynamicObjectMaterialText(GraffitiData[ freeindex ][ ObjectId ], 1, " ");

				GraffitiData[ freeindex ][ SqlId ] = AddGraffiti(GetPlayerSqlId(playerid), 
					PlayerGraffiti[ playerid ][ Text ],
					x, y, z, 
					rx, ry, rz,
					GetGraffitiFontId(PlayerGraffiti[ playerid ][ FontName ], PlayerGraffiti[ playerid ][ FontSize ]),
					PlayerGraffiti[ playerid ][ ColourId ]);

				Streamer_Update(playerid);
				cmd_ame(playerid, "baigia kaþkà paiðyti ant sienos su daþø flakonu.");
			}
			EndPlayerGraffitiSpray(playerid);
    	}
    }
    else if(!strcmp(action, "delete", true))
    {
    	if(!IsValidDynamicObject(PlayerGraffiti[ playerid ][ ObjectId ]))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neesate pradëjæs pieðti grafiti.");

    	else if(!IsGraffitiAllowed[ playerid ])
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jums nëra suteikta galimybë kurti grafiti. Jà suteikti gali administratorius.");

    	else 
    	{	
    		EndPlayerGraffitiSpray(playerid);
    	}

    }
    else 	
    	goto spray_help;
    return 1;
}


/*
			                                                                                                      
			                                               AW       ,,                                            
			MMP""MM""YMM             `7MM                 ,M'mm     db                                            
			P'   MM   `7               MM                 MV MM                                                   
			     MM   ,6"Yb.  ,pP"Ybd  MM  ,MP',pP"Ybd   AWmmMMmm `7MM  `7MMpMMMb.pMMMb.  .gP"Ya `7Mb,od8 ,pP"Ybd 
			     MM  8)   MM  8I   `"  MM ;Y   8I   `"  ,M'  MM     MM    MM    MM    MM ,M'   Yb  MM' "' 8I   `" 
			     MM   ,pm9MM  `YMMMa.  MM;Mm   `YMMMa.  MV   MM     MM    MM    MM    MM 8M""""""  MM     `YMMMa. 
			     MM  8M   MM  L.   I8  MM `Mb. L.   I8 AW    MM     MM    MM    MM    MM YM.    ,  MM     L.   I8 
			   .JMML.`Moo9^Yo.M9mmmP'.JMML. YA.M9mmmP',M'    `Mbmo.JMML..JMML  JMML  JMML.`Mbmmd'.JMML.   M9mmmP' 
			                                          MV                                                          
			                                         AW                                                           
*/

timer GraffitiSprayTimeEnd[GRAFITTI_SPRAY_RIGHT_TIME_MIN*60*1000](playerid)
{
	SendClientMessage(playerid, COLOR_LIGHTRED, "Baigësi laikas per kurá galite pieðti grafiti.");
	EndPlayerGraffitiSpray(playerid);	
}


/*
			                                                                                                                                                                         
			                    ,,                      ,,                                                                                                                           
			      db          `7MM                      db                  `7MMM.     ,MMF'                                                                                   mm    
			     ;MM:           MM                                            MMMb    dPMM                                                                                     MM    
			    ,V^MM.     ,M""bMM  `7MMpMMMb.pMMMb.  `7MM  `7MMpMMMb.        M YM   ,M MM   ,6"Yb.  `7MMpMMMb.   ,6"Yb.  .P"Ybmmm .gP"Ya `7MMpMMMb.pMMMb.  .gP"Ya `7MMpMMMb.mmMMmm  
			   ,M  `MM   ,AP    MM    MM    MM    MM    MM    MM    MM        M  Mb  M' MM  8)   MM    MM    MM  8)   MM :MI  I8  ,M'   Yb  MM    MM    MM ,M'   Yb  MM    MM  MM    
			   AbmmmqMA  8MI    MM    MM    MM    MM    MM    MM    MM        M  YM.P'  MM   ,pm9MM    MM    MM   ,pm9MM  WmmmP"  8M""""""  MM    MM    MM 8M""""""  MM    MM  MM    
			  A'     VML `Mb    MM    MM    MM    MM    MM    MM    MM        M  `YM'   MM  8M   MM    MM    MM  8M   MM 8M       YM.    ,  MM    MM    MM YM.    ,  MM    MM  MM    
			.AMA.   .AMMA.`Wbmd"MML..JMML  JMML  JMML..JMML..JMML  JMML.    .JML. `'  .JMML.`Moo9^Yo..JMML  JMML.`Moo9^Yo.YMMMMMb  `Mbmmd'.JMML  JMML  JMML.`Mbmmd'.JMML  JMML.`Mbmo 
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


#define GraffitiManagementDialog.		@graf_

static PlayerUsedGraffitiIndex[ MAX_PLAYERS ],
		PlayerGraffitiListPage[ MAX_PLAYERS ];

stock GraffitiManagementDialog.ShowMain(playerid)
{
	ShowPlayerDialog(playerid, DIALOG_GRAFFITIMENU_MAIN, DIALOG_STYLE_LIST, 
		"Visi serverio grafiti\n\
		Aplink mane esantys grafiti\n\
		Grafiti paieðka pagal tekstà.",
		"Pasirinkti", "Iðeiti");
	return 1;
}

stock GraffitiManagementDialog.ShowFullList(playerid)
{
	new query[], Cache:result, string[ 1024 ];
	mysql_format(DbHandle, query, sizeof(query), "SELECT * FROM graffiti LIMIT %d, %d", 
		PlayerGraffitiListPage[ playerid ] * GRAFFITI_LIST_ITEMS_PER_PAGE, 
		(PlayerGraffitiListPage[ playerid ]+1) * GRAFFITI_LIST_ITEMS_PER_PAGE);
	result = mysql_query(DbHandle, query);

	for(new i = 0; i < cache_get_row_count(); i++)
	{
		
	}
}

stock GraffitiManagementDialog.ShowNearestList(playerid, Float:distance)
{

}


stock GraffitiManagementDialog.ShowTextInput(playerid, errostr[] = "")
{

}

GraffitiManagementDialog.OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
	switch(dialogid)
	{
		case DIALOG_GRAFFITIMENU_MAIN:
		{
			if(!response)
				return 1;

			switch(listitem)
			{
				case 0:
				{
					PlayerGraffitiListPage[ playerid ] = 0;
					GraffitiManagementDialog.ShowFullList(playerid);
				}
				case 1:
				{
					GraffitiManagementDialog.ShowNearestList(playerid, 30.0);
				}
				case 2:
				{
					GraffitiManagementDialog.ShowTextInput(playerid);
				}
			}
			return 1;
		}
	}
	return 0;
}