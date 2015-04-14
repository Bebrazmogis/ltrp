

/*
	CREATE TABLE IF NOT EXISTS bug_reports 
	(
		id INT AUTO_INCREMENT NOT NULL,
		reported_by INT NULL,
		`datetime` DATE NOT NULL,
		category VARCHAR(128) NOT NULL,
		content TEXT NOT NULL,
		status VARCHAR(32) NOT NULL DEFAULT 'neperþiûrëta',
		PRIMARY KEY(id),
		INDEX(reported_by)
	) ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;
		
	ALTER TABLE bug_reports ADD FOREIGN KEY(reported_by) REFERENCES players(id) ON DELETE SET NULL;
*/

#include <YSI\y_hooks>


#define MAX_BUG_CATEGORY 				64
#define DIALOG_BUG_REPORT_MAIN 			4700
#define DIALOG_BUG_REPORT_CATEGORY 		4701
#define DIALOG_BUG_REPORT_PREVIEW 		4702
#define DIALOG_BUG_REPORT_ADD_TEXT 		4703

enum E_PLAYER_BUG_REPORT 
{
	SqlId,
	Category[ MAX_BUG_CATEGORY ],
};

static PlayerBugReport[ MAX_PLAYERS ][ E_PLAYER_BUG_REPORT ];





CMD:bugreport(playerid)
{
	ShowPlayerBugReportCategory(playerid);
	return 1;
}

CMD:mybugreports(playerid)
{
	new query[ 60 ];
	mysql_format(DbHandle, query, sizeof(query), "SELECT * FROM bug_reports WHERE reported_by = %d", GetPlayerSqlId(playerid));
	return mysql_pquery(DbHandle, query, "OnPlayerBugReportListLoad", "i", playerid);
}


forward OnPlayerBugReportListLoad(playerid);
public OnPlayerBugReportListLoad(playerid)
{
	if(!cache_get_row_count())
		SendClientMessage(playerid, COLOR_NEWS, "Jûs neesate praneðæs nei vienos klaidos. Tai padaryti galite su komanda /bugreport");
	else 
	{	
		new category[ MAX_BUG_CATEGORY ], status[ 32 ], string[ 1024 ];
		if(cache_get_row_count() > 10)
		{
			for(new i = 0; i < cache_get_row_count(); i++)
			{
				cache_get_field_content(i, "category", category);
				cache_get_field_content(i, "status", status);
				format(string, sizeof(string), "%s%s: %s\n", string, category, status);
			}
			ShowPlayerDialog(playerid, 9999, DIALOG_STYLE_MSGBOX, "Jûsø praneðtos klaidos.", string, "Gerai", "");
		}
		else 
		{
			SendClientMessage(playerid, COLOR_NEWS, "________________Jûsø praneðtos klaidos______________");
			for(new i = 0; i < cache_get_row_count(); i++)
			{
				cache_get_field_content(i, "category", category);
				cache_get_field_content(i, "status", status);
				format(string, sizeof(string), "%s: %s", category, status);
				SendClientMessage(playerid, COLOR_NEWS, string);
			}
		}
	}
	return 1;
}

hook OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
	switch(dialogid)
	{
		case DIALOG_BUG_REPORT_CATEGORY:
		{
			if(response)
			{
				if(isnull(inputtext))
					ShowPlayerBugReportCategory(playerid);
				else if(strlen(inputtext) >= MAX_BUG_CATEGORY)
				{
					SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida. Kategorijos pavadinimo negali sudaryti daugiau nei " #MAX_BUG_CATEGORY " simboliai");
					ShowPlayerBugReportCategory(playerid);
				}
				else 
				{
					new query[ 190 ], Cache:result;
					format(PlayerBugReport[ playerid ][ Category ], MAX_BUG_CATEGORY, inputtext);

					mysql_format(DbHandle, query, sizeof(query), "INSERT INTO bug_reports (reported_by, `datetime`, category) VALUES (%d, FROM_UNIXTIME(%d), '%e')",
						GetPlayerSqlId(playerid), gettime(), inputtext);
					result = mysql_query(DbHandle, query);
					PlayerBugReport[ playerid ][ SqlId ] = cache_insert_id();
					cache_delete(result);

					ShowPlayerBugReportMain(playerid);
				}

			}
			return 1;
		}
		case DIALOG_BUG_REPORT_MAIN:
		{
			if(!response)
				ShowPlayerBugReportMain(playerid);
			else 
			{
				switch(listitem)
				{
					case 0:
					{
						ShowPlayerBugReportPreview(playerid);
					}
					case 1:
					{
						ShowPlayerBugReportAddText(playerid);
					}
					case 2:
					{
						SendClientMessage(playerid, COLOR_NEWS, "Klaidos praneðimas sëkmingai iðsaugotas. Aèiû kad prisdedate prie serverio gerovës.");
					}
					case 3:
					{
						new query[ 60 ];
						mysql_format(DbHandle, query, sizeof(query), "DELETE FROM bug_reports WHERE id = %d",
							PlayerBugReport[ playerid ][ SqlId ]);
						mysql_pquery(DbHandle, query);
						SendClientMessage(playerid, COLOR_NEWS, "Klaidos praneðimas sëkmingai atðauktas.");
					}
				}
			}
			return 1;
		}
		case DIALOG_BUG_REPORT_PREVIEW:
		{
			ShowPlayerBugReportMain(playerid);
			return 1;
		}
		case DIALOG_BUG_REPORT_ADD_TEXT:
		{
			if(!response)
				ShowPlayerBugReportMain(playerid);
			else 
			{
				new query[ 256 ]; 
				mysql_format(DbHandle, query, sizeof(query), "UPDATE bug_reports SET content = CONCAT(content, '%e') WHERE id = %d",
					inputtext, PlayerBugReport[ playerid ][ SqlId ]);
				mysql_pquery(DbHandle, query);

				ShowPlayerBugReportMain(playerid);
				return 1;
			}
			return 1;
		}
	}
	return 0;
}




stock ShowPlayerBugReportCategory(playerid)
{
	return ShowPlayerDialog(playerid, DIALOG_BUG_REPORT_CATEGORY, DIALOG_STYLE_INPUT, "Klaidos praneðimas.", "Áveskite kategorijà kuriai priklauso klaida.\nPavyzdþiui:\n\tNamai - baldai\n\tMaðinos bagaþinë", "Tæsti", "Iðeiti");
}

stock ShowPlayerBugReportMain(playerid)
{
	return ShowPlayerDialog(playerid, DIALOG_BUG_REPORT_MAIN, DIALOG_STYLE_LIST, "Klaidos praneðimas", "Perþiûrëti esamà tekstà\nPridëti teksto\n{00AA00}Iðsaugoti\n{AA0000}Paðalinti", "Pasirinkti", "-");
}

stock ShowPlayerBugReportPreview(playerid)
{
	new query[60];
	mysql_format(DbHandle, query, sizeof(query), "SELECT content FROM bug_reports WHERE id = %d",
		PlayerBugReport[ playerid ][ SqlId ]);
	mysql_pquery(DbHandle, query, "OnBugContentLoad", "i", playerid);
}

forward OnBugContentLoad(playerid);
public OnBugContentLoad(playerid)
{
	new text[ 2048 ], lines;
	if(!cache_get_row_count())
		ShowPlayerBugReportMain(playerid);
	else 
	{
		cache_get_field_content(0, "content", text);
		if(isnull(text))
			ShowPlayerBugReportMain(playerid);
		
		else 
		{
			lines = strlen(text) / 64;

			for(new i = 1; i <= lines; i++)
			{
				strins(text, "-\n", i*64);
			}

			ShowPlayerDialog(playerid, DIALOG_BUG_REPORT_PREVIEW, DIALOG_STYLE_MSGBOX, "Klaidos perþiûra.", text, "Gerai", "");
		}
	}
	return 1;
}

stock ShowPlayerBugReportAddText(playerid)
{
	ShowPlayerDialog(playerid, DIALOG_BUG_REPORT_ADD_TEXT, DIALOG_STYLE_INPUT, "Klaidos praneðimas", "Raðykite informacijà apie klaidà. Jeigu þinote, paminëkite kaip atkartoti ðià klaidà. Kokios sàlygomis ji pastebima", "Tæsti", "Atgal");
	return 1;
}