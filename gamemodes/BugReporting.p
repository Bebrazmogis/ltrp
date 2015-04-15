

/*
	CREATE TABLE IF NOT EXISTS bug_reports 
	(
		id INT AUTO_INCREMENT NOT NULL,
		reported_by INT NULL,
		`date` DATETIME NOT NULL,
		category VARCHAR(128) NOT NULL,
		content TEXT NOT NULL,
		status VARCHAR(32) NOT NULL DEFAULT 'neperþiûrëta',
		PRIMARY KEY(id),
		INDEX(reported_by)
	) ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;
		
	ALTER TABLE bug_reports ADD FOREIGN KEY(reported_by) REFERENCES players(id) ON DELETE SET NULL;

	CREATE TABLE IF NOT EXISTS bug_report_comments
	(
		id INT AUTO_INCREMENT NOT NULL,
		bug_report_id INT NOT NULL,
		author INT NOT NULL,
		`date` DATETIME NOT NULL,
		`comment` VARCHAR(128) NOT NULL,
		PRIMARY KEY(id),
		INDEX(bug_report_id),
		INDEX(author)
	) ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;

	ALTER TABLE bug_report_comments ADD FOREIGN KEY(bug_report_id) REFERENCES bug_reports(id) ON DELETE CASCADE;
	ALTER TABLE bug_report_comments ADD FOREIGN KEY(author) REFERENCES players(id) ON DELETE CASCADE;
*/

#include <YSI\y_hooks>


#define MAX_BUG_CATEGORY 				64
#define MAX_BUG_STATUS 					32
#define DIALOG_BUG_REPORT_MAIN 			4700
#define DIALOG_BUG_REPORT_CATEGORY 		4701
#define DIALOG_BUG_REPORT_PREVIEW 		4702
#define DIALOG_BUG_REPORT_ADD_TEXT 		4703
#define DIALOG_BUGREP_MENU_MAIN 		4750
#define DIALOG_BUGREP_MENU_ACTIONS 		4751
#define DIALOG_BUGREP_MENU_PREVIEW 		4752
#define DIALOG_BUGREP_MENU_COMMENTS 	4753
#define DIALOG_BUGREP_MENU_NEW_STATUS 	4754
#define DIALOG_BUGREP_MENU_NEW_COMMENT 	4755

#define BugReportManagementDialog. 		bgD_
#define MAX_BUGS_PER_PAGE 				10

enum E_PLAYER_BUG_REPORT 
{
	SqlId,
	Category[ MAX_BUG_CATEGORY ],
};

static PlayerBugReport[ MAX_PLAYERS ][ E_PLAYER_BUG_REPORT ],
	AdminBugPage[ MAX_PLAYERS ],
	AdminBugSqlId[ MAX_PLAYERS ];




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
		new category[ MAX_BUG_CATEGORY ], status[ MAX_BUG_STATUS ], string[ 1024 ];
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

					mysql_format(DbHandle, query, sizeof(query), "INSERT INTO bug_reports (reported_by, `date`, category) VALUES (%d, FROM_UNIXTIME(%d), '%e')",
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

		case DIALOG_BUGREP_MENU_MAIN:
		{
			if(response)
			{
				new index = strfind(inputtext, ".");
				// Galbût pasirinko praeità puslapá
				if(index == -1 && !listitem)
				{	
					AdminBugPage[ playerid ]--;
					BugReportManagementDialog.ShowMain(playerid, AdminBugPage[ playerid ]);
				}
				// O galbût pasirinko sekantá puslapá
				else if(index == -1 && listitem == MAX_BUGS_PER_PAGE + 1)
				{
					AdminBugPage[ playerid ]++;
					BugReportManagementDialog.ShowMain(playerid, AdminBugPage[ playerid ]);
				}
				else 
				{
					new tmp[ 10 ];
					strmid(tmp, inputtext, 0, index);
					AdminBugSqlId[ playerid ] = strval(tmp);

					BugReportManagementDialog.ShowBugActions(playerid);
				}
			}
			return 1;
		}
		case DIALOG_BUGREP_MENU_ACTIONS:
		{
			if(!response)	
				BugReportManagementDialog.ShowMain(playerid, AdminBugPage[ playerid ]);
			else 
			{
				switch(listitem)
				{
					// Perþiûrëjimas
					case 0:
					{
						BugReportManagementDialog.ShowPreview(playerid, AdminBugSqlId[ playerid ]);
					}
					// Þiûrëti komentarus
					case 1:
					{
						BugReportManagementDialog.ShowComments(playerid, AdminBugSqlId[ playerid ]);
					}
					// Keisti bûsenà
					case 2:
					{
						BugReportManagementDialog.ShowNewStatus(playerid);
					}
					// Raðyti komentarà
					case 3:
					{
						BugReportManagementDialog.ShowNewComment(playerid);
					}
				}
			}
			return 1;
		}
		case DIALOG_BUGREP_MENU_PREVIEW:
		{
			BugReportManagementDialog.ShowBugActions(playerid);
			return 1;
		}
		case DIALOG_BUGREP_MENU_COMMENTS:
		{
			BugReportManagementDialog.ShowBugActions(playerid);
			return 1;
		}
		case DIALOG_BUGREP_MENU_NEW_STATUS:
		{
			if(!response)
				BugReportManagementDialog.ShowBugActions(playerid);
			else 
			{
				if(isnull(inputtext) || strlen(inputtext) >= MAX_BUG_STATUS)
				{
					SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida. Klaidos statusà gali sudaryti nuo 1 iki " #MAX_BUG_STATUS " simboliø");
					BugReportManagementDialog.ShowNewStatus(playerid);
				}
				else 
				{
					SetBugReportStatus(AdminBugSqlId[ playerid ], inputtext);
					SendClientMessage(playerid, COLOR_NEWS, "Klaidos praneðimo bûsena sëkmingai atnaujinta.");
					BugReportManagementDialog.ShowBugActions(playerid);
				}
			}	
			return 1;
		}
		case DIALOG_BUGREP_MENU_NEW_COMMENT:
		{
			if(!response)
				BugReportManagementDialog.ShowBugActions(playerid);
			else 
			{
				if(isnull(inputtext))
					BugReportManagementDialog.ShowNewComment(playerid);
				else 
				{
					AddBugReportComment(GetPlayerSqlId(playerid), AdminBugSqlId[ playerid ], inputtext);
					SendClientMessage(playerid, COLOR_NEWS, "Klaidos praneðimo komentaras sëkmingai pridëtas.");
					BugReportManagementDialog.ShowBugActions(playerid);
				}
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

stock SetBugReportStatus(bugsqlid, status[])
{
	new query[ 70 + MAX_BUG_STATUS ];
	mysql_format(DbHandle, query, sizeof(query), "UPDATE bug_reports SET status = '%e' WHERE id = %d",
		status, bugsqlid);
	return mysql_pquery(DbHandle, query);
}

stock AddBugReportComment(authorsqlid, bugsqlid, comment[])
{
	new query[ 256 ];
	mysql_format(DbHandle, query, sizeof(query), "INSERT INTO bug_report_comments (bug_report_id, `date`, author, `comment`) VALUES (%d, FROM_UNIXTIME(%d), %d, '%e')",
		bugsqlid, gettime(), authorsqlid, comment);
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



BugReportManagementDialog.ShowMain(playerid, page = 0)
{
	new query[220];
	mysql_format(DbHandle, query, sizeof(query), "SELECT bug_reports.id, bug_reports.category, bug_reports.date, players.name FROM bug_reports LEFT JOIN players ON players.id = bug_reports.reported_by ORDER BY date DESC LIMIT %d, %d",
		page * MAX_BUGS_PER_PAGE, ((page+1) * MAX_BUGS_PER_PAGE)+1);
	return mysql_pquery(DbHandle, query, "OnBugReportListLoad", "ii", playerid, page);
}

forward OnBugReportListLoad(playerid, page);
public OnBugReportListLoad(playerid, page)
{
	if(!cache_get_row_count())
		SendClientMessage(playerid, COLOR_NEWS, "Nëra nei vienos praneðtos klaidos.");
	else 
	{
		new id, name[ MAX_PLAYER_NAME ], category[ MAX_BUG_CATEGORY ], date[ 32 ], string[ 1500 ];
		
		if(page)
			strcat(string, "{007700}Atgal{FFFFFF}\n");

		for(new i = 0; i < cache_get_row_count(); i++)
		{
			if(i == MAX_BUGS_PER_PAGE)
			{
				strcat(string, "{007700}Toliau{FFFFFF}");
				break;
			}
			id = cache_get_field_content_int(i, "id");
			cache_get_field_content(i, "category", category);
			cache_get_field_content(i, "date", date);
			cache_get_field_content(i, "name", name);
			format(string, sizeof(string), "%s%d. %s\t%s\t(%s)\n",
				string, id, category, date, name);
		}
		ShowPlayerDialog(playerid, DIALOG_BUGREP_MENU_MAIN, DIALOG_STYLE_LIST, "Klaidø praneðimø valdmyas", string, "Pasirinkti", "Iðeiti");
	}
	return 1;
}

stock BugReportManagementDialog.ShowBugActions(playerid)
{
	ShowPlayerDialog(playerid, DIALOG_BUGREP_MENU_ACTIONS, DIALOG_STYLE_LIST, "Klaidos praneðimo valdymas", "Perþiûrëti\nÞiûrëti komentarus\nKeisti bûsenà\nRaðyti komentarà", "Pasirinkti", "Atgal");
	return 1;
}

stock BugReportManagementDialog.ShowPreview(playerid, bugsqlid)
{
	new query[ 200 ];
	mysql_format(DbHandle, query, sizeof(query), "SELECT bug_reports.content, bug_reports.date, bug_reports.category, players.name FROM bug_reports LEFT JOIN players ON bug_reports.reported_by = players.id WHERE bug_reports.id = %d",
		bugsqlid);

	inline AdminBugPreview()
	{
		if(!cache_get_row_count())
		{
			SendClientMessage(playerid, COLOR_LIGHTRED, "Nëra teksto.");
			BugReportManagementDialog.ShowBugActions(playerid);
		}
		else 
		{
			new string[ 1800 ], date[ 32 ], name[ MAX_PLAYER_NAME ], category[ MAX_BUG_CATEGORY ], lines;
			
			cache_get_field_content(0, "content", string);
			cache_get_field_content(0, "date", date);
			cache_get_field_content(0, "category", category);
			cache_get_field_content(0, "name", name);

			lines = strlen(string) / 64;
			for(new i = 1; i <= lines; i++)
				strins(string, "-\n", i*64);
			

			format(string, sizeof(string), "{11CC22}%s{FFFFFF}\nPraneðë: %s\n\n%s",
				date, name, string);
			ShowPlayerDialog(playerid, DIALOG_BUGREP_MENU_PREVIEW, DIALOG_STYLE_MSGBOX, category, string, "Gerai", "");
		}
		return 1;
	}
	mysql_pquery_inline(DbHandle, query, using inline AdminBugPreview, "");
	return 1;
}


stock BugReportManagementDialog.ShowComments(playerid, bugsqlid)
{
	new query[ 200 ];
	mysql_format(DbHandle, query, sizeof(query), "SELECT bug_report_comments.*, players.name FROM bug_report_comments LEFT JOIN players ON bug_report_comments.author = players.id WHERE bug_report_id = %d ORDER BY `date` DESC LIMIT 10",
		bugsqlid);

	inline AdminBugComments()
	{
		if(!cache_get_row_count())
		{
			SendClientMessage(playerid, COLOR_LIGHTRED, "Ðios klaidos dar niekas nepakomentavo.");
			BugReportManagementDialog.ShowBugActions(playerid);
		}
		else 
		{
			new string[ 2048 ], name[ MAX_PLAYER_NAME ], date[ 32 ], comment[ 129 ];

			for(new i = 0; i < cache_get_row_count(); i++)
			{
				cache_get_field_content(i, "comment", comment);
				cache_get_field_content(i, "date", date);
				cache_get_field_content(i, "name", name);

				if(strlen(comment) > 64)
					strins(comment, "-\n", 64);

				format(string, sizeof(string), "%sData: %s\nPraneðë: %s\n%s\n\n",
					string,
					date,
					name,
					comment);
			}
			ShowPlayerDialog(playerid, DIALOG_BUGREP_MENU_COMMENTS, DIALOG_STYLE_MSGBOX, "Klaidos komentarai", string, "Gerai", "");
		}
		return 1;
	}
	return mysql_pquery_inline(DbHandle, query, using inline AdminBugComments, "");
}

stock BugReportManagementDialog.ShowNewStatus(playerid)
{
	ShowPlayerDialog(playerid, DIALOG_BUGREP_MENU_NEW_STATUS, DIALOG_STYLE_INPUT, "Klaidos bûsena", "Áraðykite naujà klaidos bûsenà.\nPavyzdþiui \"Patvirtinta\", \"Atmesta\" ar panaðiai", "Iðsaugoti", "Atgal");
	return 1;
}

stock BugReportManagementDialog.ShowNewComment(playerid)
{
	ShowPlayerDialog(playerid, DIALOG_BUGREP_MENU_NEW_COMMENT, DIALOG_STYLE_INPUT, "Klaidos komentaras", "Áraðykite komentarà kurá matys kiti administratoriai.", "Iðsaugoti", "Atgal");
	return 1;
}