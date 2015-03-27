#include <YSI\y_hooks>


#define GRAFFITI_BACKROUND_OBJECT		8330
#define MAX_GRAFFITI 					100
#define GRAFITTI_SPRAY_RIGHT_TIME 		5*60*1000
#define GRAFITTI_SPRAY_RIGHT_TIME_MIN 	GRAFITTI_SPRAY_RIGHT_TIME / 1000 / 60
#define MAX_GRAFFITI_TEXT 				128

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
		poz_z FLOAT NOT NULL,
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


enum E_GRAFITTI_DATA 
{
	SqlId,
	ObjectId,
};	

static GraffitiData[ MAX_GRAFFITI ][ E_GRAFITTI_DATA ],
	Timer:AllowGraffitiTimer[ MAX_PLAYERS ],
	bool:IsGraffitiAllowed[ MAX_PLAYERS ];


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


hook OnGameModeInit()
{
	mysql_pquery(DbHandle, 
		"SELECT \
			graffiti.*, graffiti_fonts.name, graffiti_fonts.size, CONV(graffiti_colours.argb, 16, 10) AS argb \
		FROM graffiti \
		JOIN graffiti_fonts ON graffiti.font_id = graffiti_fonts.id \
		JOIN graffiti_colours ON graffiti.colour_id = graffiti_colours.id", 
		"OnGraffitiLoad");
}


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
				OBJECT_MATERIAL_SIZE_256x128,
				fontname,
				cache_get_field_content_int(i, "size"),
				0,
				colour,
				0x00000000,
				OBJECT_MATERIAL_TEXT_ALIGN_CENTER
			);
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

			SetPVarString(playerid, "NewGraffiti.Colour", inputtext);
			ShowPlayerGraffitiNameDialog(playerid);
			return 1;
		}
		case DIALOG_GRAFFITI_TEXT:
		{
			if(!response)
				return 1;
			
			if(isnull(inputtext))
				return ShowPlayerGraffitiNameDialog(playerid, "Tekstas negali bûti tuðèias.");

			SetPVarString(playerid, "NewGraffiti.Name", inputtext);
			ShowPlayerGraffitiFontNamDialog(playerid);
			return 1;
		}
		case DIALOG_GRAFFITI_FONT_NAME:
		{
			if(!response)
				return 1;

			SetPVarString(playerid, "NewGraffiti.FontName", inputtext);
			ShowPlayerGraffitiFontSizDialog(playerid, inputtext);
			return 1;
		}
		case DIALOG_GRAFFITI_FONT_SIZE:
		{
			if(!response)
				return 1;

			new Float:x, Float:y, Float:z,
				fontname[ 64 ],
				fontsize = strval(inputtext),
				freeindex = GetFreeGraffitiIndex(),
				text[ MAX_GRAFFITI_TEXT ],
				colour,
				colourname[ 32 ]
				;
			GetPlayerPos(playerid, x, y, z);
			GetXYInFrontOfPlayer(playerid, x, y, 2.5);

			GetPVarString(playerid, "NewGraffiti.Name", text, sizeof(text));
			GetPVarString(playerid, "NewGraffiti.FontName", fontname, sizeof(fontname));
			GetPVarString(playerid, "NewGraffiti.Colour", colourname, sizeof(colourname));

			colour = GetGraffitiColourARGB(colourname);

			if(freeindex == -1)
			{
				ErrorLog("Graffiti limit(" #MAX_GRAFFITI ") reached.");
				SendClientMessage(playerid, COLOR_LIGHTRED, "Ávyko klaida. Ðiuo metu sukurti grafiti neámanoma.");
				return 1;
			}

			GraffitiData[ freeindex ][ SqlId ] = AddGraffiti(GetPlayerSqlId(playerid), text, x, y, z, GetGraffitiFontId(fontname, fontsize), GetGraffitiColourId(colourname));
			GraffitiData[ freeindex ][ ObjectId ] = CreateDynamicObject(
					GRAFFITI_BACKROUND_OBJECT,
					x,
					y,
					z,
					0.0, 0.0, 0.0 
				);
			SetDynamicObjectMaterialText(GraffitiData[ freeindex ][ ObjectId ], 0, text, OBJECT_MATERIAL_SIZE_256x128, fontname, fontsize, 0, colour, 0x00000000, OBJECT_MATERIAL_TEXT_ALIGN_CENTER);
			EditDynamicObject(playerid, GraffitiData[ freeindex ][ ObjectId ]);
			SendClientMessage(playerid, COLOR_NEWS, "Grafiti sëkmingai sukurtas. Dabar galite já nustatyti á vietà.");
			return 1;
		}
	}
	return 0;
}


public OnPlayerEditDynamicObject(playerid, objectid, response, Float:x, Float:y, Float:z, Float:rx, Float:ry, Float:rz)
{
	#if defined graff_OnPlayerEditDynamicObject
		graff_OnPlayerEditDynamicObject(playerid, objectid, response, x, y, z, rx, ry, rz);
	#endif

	new query[200];
	for(new i = 0; i < MAX_GRAFFITI; i++)
	{
		if(GraffitiData[ i ][ ObjectId ] == objectid)
		{
			// Atnaujinam pozicijà ir baigiam kûrimà þaidëjui ðitø visø.
			if(response)
			{
				mysql_format(DbHandle, query, sizeof(query), "UPDATE graffiti SET pos_x = %f, pos_y = %f, pos_z = %f, rot_x = %f, rot_y = %f, rot_z = %f WHERE id = %d",
					x, y, z, rx, ry, rz, GraffitiData[ i ][ SqlId ]);
				mysql_pquery(DbHandle, query);

				IsGraffitiAllowed[ playerid ] = false;
				stop AllowGraffitiTimer[ playerid ];
				SendClientMessage(playerid, COLOR_NEWS, "Grafiti sëkmingai sukurtas.");
			}
			// Iðtrinam
			else 
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
	IsGraffitiAllowed[ playerid ] = false;
	stop AllowGraffitiTimer[ playerid ];
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

stock GetGraffitiColourId(colourname[])
{
	new query[80], Cache:result, colourid;
	mysql_format(DbHandle, query, sizeof(query), "SELECT id FROM graffiti_colours WHERE name = '%e'", colourname);
	result = mysql_query(DbHandle, query);
	if(cache_get_row_count())
		colourid = cache_get_field_content_int(0, "id");
	cache_delete(result);
	return colourid;
}

AddGraffiti(sqlid, text[], Float:x, Float:y, Float:z, fontid, colourid)
{
	new query[256], Cache:result;

	mysql_format(DbHandle, query, sizeof(query), "INSERT INTO graffiti (author, `text`, pos_x, pos_y, pos_z, font_id, colour_id) VALUES (%d, '%e', %f, %f, %f, %d, %d)",
		sqlid, text, x, y, z, fontid, colourid);

	result = mysql_query(DbHandle, query);
	sqlid = cache_insert_id();
	cache_delete(result);
	return sqlid;
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
	return mysql_pquery(DbHandle, "SELECT name, CONV(argb, 16, 10) AS argb FROM graffiti_colours", "OnGraffitiColourListLoad", "i", playerid);
}	                   

public OnGraffitiColourListLoad(playerid)
{
	new string[ 1024 ], name[ 32 ], colour[ 8 ];
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
        SendClientMessage(playerid, COLOR_WHITE, "GALIMI VEIKSMAI: create, edit");
        if(IsPlayerAdmin(playerid) || GetPlayerAdminLevel(playerid))
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

    	if(sscanf(params, "u", targetid))
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
    	if(sscanf(params, "u", targetid))
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
    	if(!IsGraffitiAllowed[ playerid ])
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jums nëra suteikta galimybë kurti grafiti. Jà suteikti gali administratorius.");

    	if(IsPlayerInAnyInterior(playerid))
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, grafiti pieðti galima tik lauke.");

    	ShowPlayerGraffitiColourDialog(playerid);
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

timer GraffitiSprayTimeEnd[GRAFITTI_SPRAY_RIGHT_TIME](playerid)
{
	stop AllowGraffitiTimer[ playerid ];
	IsGraffitiAllowed[ playerid ] = false;
	SendClientMessage(playerid, COLOR_LIGHTRED, "Baigësi laikas per kurá galite pieðti grafiti.");
}