



#include <YSI\y_hooks>

#define DIALOG_PLAYER_PHONE_MAIN 		8100
#define DIALOG_PLAYER_PHONE_PHONEBOOK 	8101
#define DIALOG_PLAYER_PHONE_BOOK_NEW_1 	8102
#define DIALOG_PLAYER_PHONE_BOOK_NEW_2	8103
#define DIALOG_PLAYER_PHONE_OPTIONS		8104
#define DIALOG_PLAYER_PHONE_NEW_SMS		8105
#define DIALOG_PLAYER_PHONE_SMS_RECEIVE 8106
#define DIALOG_PLAYER_PHONE_SMS_SENT 	8107
#define DIALOG_PLAYER_PHONE_SMS_CONTENT 8108


#define MAX_SMS_PER_PAGE 				20
#define PLAYER_PHONE_NUMBER_LENGTH		6
#define MAX_PLAYER_PHONES 				5


static PlayerPhones[ MAX_PLAYERS ][ MAX_PLAYER_PHONES ][ E_PRIVATE_PHONE_DATA ];


enum E_NEW_CONTACT_INPUT_TYPE 
{
	CONTACT_INPUT_TYPE_NUMBER = 1,
	CONTACT_INPUT_TYPE_NAME = 2,
};

static PlayerSMSListPage[ MAX_PLAYERS ];


forward OnPlayerReceivedMessageLoad(playerid, page);
forward OnPlayerSentMessageLoad(playerid, page);
forward OnPlayerPhoneLoad(playerid);

forward OnPlayerCallPlayer(playerid, targetplayer);
forward OnPlayerCallService(playerid, phonenumber);

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

public OnPlayerFirstSpawn(playerid)
{
	#if defined phone_OnPlayerFirstSpawn
		phone_OnPlayerFirstSpawn(playerid);
	#endif

	new pquery[ 80 ];
	mysql_format(DbHandle, query, "SELECT * FROM phones WHERE location_type = %d AND location_id = %d",
		PlayerInventory, GetPlayerSqlId(playerid));
	mysql_pquery(DbHandle, query, "OnPlayerPhoneLoad", "i", playerid);
	return 1;
}
#if defined _ALS_OnPlayerFirstSpawn
	#undef OnPlayerFirstSpawn
#else 
	#define _ALS_OnPlayerFirstSpawn
#endif
#define OnPlayerFirstSpawn 				phone_OnPlayerFirstSpawn
#if defined OnPlayerFirstSpawn
	forward OnPlayerFirstSpawn(playerid);
#endif

public OnPlayerPhoneLoad(playerid)
{
	for(new i = 0; i  < cache_get_row_count(); i++)
	{
		PlayerPhones[ playerid ][ i ][ Number ] = cache_get_field_content_int(i, "number");
		PlayerPhones[ playerid ][ i ][ Online ] = cache_get_field_content_int(i, "online");
	}
	return 1;
}

hook OnPlayerDisconnect(playerid, reason)
{
	static emptyPlayerPhone[ E_PRIVATE_PHONE_DATA ];
	
	for(new i = 0; i < MAX_PLAYER_PHONES; i++)
        PlayerPhones[ playerid ][ i ] = emptyPlayerPhone;
}


hook OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
	switch(dialogid)
	{
		case DIALOG_PLAYER_PHONE_MAIN:
		{
			if(!response)
				return 1;

			switch(listitem)
			{
				// Laikas
				case 0:
				{
					new Hour,
			        Min,
			        Sec,
			        string[90];

				    gettime(Hour, Min, Sec);
				    format(string, sizeof(string), "Laikrodis Jûsø telefone ðiuo metu rodo toká laikà: %d:%d", Hour, Min);
				    SendClientMessage(playerid, COLOR_FADE1, string);
				    format(string, sizeof(string), "* %s iðsitraukia mobiløjá telefonà ir pasiþiûri dabartiná laikà.." , GetPlayerNameEx(playerid));
				    ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
				}
				// Telefonu knyga
				case 1:
				{
					ShowPlayerPhoneBookMenu(playerid);
				}
				// Gautos þinutës 
				case 2:
				{
					PlayerSMSListPage[ playerid ] = 0;
					ShowPlayerReceivedMessages(playerid);
				}
				// Siøstos þinutës
				case 3:
				{
					PlayerSMSListPage[ playerid ] = 0;
					ShowPlayerSentMessages(playerid);
				}
			}
		}
		case DIALOG_PLAYER_PHONE_PHONEBOOK:
		{
			if(!response)
				return 1;

			// Dël \n prie "Naujas kontaktas" taip bûti gali.
			if(isnull(inputtext))
				return ShowPlayerPhoneBookMenu(playerid);

			// Naujo kontakto pridëjimas
			if(!listitem)
			{
				if(IsPlayerPhonebookFull(playerid))
					SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûsø telefonø knyga pilna.");
				else 
					ShowPlayerNewPhoneContactDialog(playerid, CONTACT_INPUT_TYPE_NUMBER);
			}
			else 
			{
				// listitem-1, todël nes 0 yra naujas kontaktas. 
				// Taip pat juo galima pasitikëti nes visada ið eilës kontaktai masyve eis.
				SetPVarInt(playerid, "UsedContact", listitem-1);
				ShowPlayerDialog(playerid, DIALOG_PLAYER_PHONE_OPTIONS, DIALOG_STYLE_LIST, "Pasirinikite veiksmà", "Skambinti\nRaðyti þinutæ\n{AA1111}Iðtrinti", "Pasirinkti", "Iðeiti");
			}
			return 1;
		}
		case DIALOG_PLAYER_PHONE_BOOK_NEW_1:
		{
			if(!response)
				ShowPlayerPhoneBookMenu(playerid);
			else 
			{
				new phonenumber; 
				if(sscanf(inputtext, "i", phonenumber) || phonenumber < 0)
					ShowPlayerNewPhoneContactDialog(playerid, CONTACT_INPUT_TYPE_NUMBER, "Telefono numerá gali sudaryti tik skaièiai.");
				else 
				{
					SetPVarInt(playerid, "Tmp.Phonenumber", phonenumber);
					ShowPlayerNewPhoneContactDialog(playerid, CONTACT_INPUT_TYPE_NAME);
				}
			}
			return 1;
		}
		case DIALOG_PLAYER_PHONE_BOOK_NEW_2:
		{
			if(!response)
				ShowPlayerNewPhoneContactDialog(playerid, CONTACT_INPUT_TYPE_NUMBER);
			else 
			{
				if(isnull(inputtext) || strlen(inputtext) >= MAX_PHONEBOOK_CONTACT_NAME)
					ShowPlayerNewPhoneContactDialog(playerid, CONTACT_INPUT_TYPE_NAME, "Kontakto vardà gali sudaryti nuo 1 " #MAX_PHONEBOOK_CONTACT_NAME " iki simboliø."); 
				
				else 
				{
					AddPlayerContact(playerid, GetPVarInt(playerid, "Tmp.Phonenumber"), inputtext);
					SendClientMessage(playerid, COLOR_NEWS, "Kontaktas sëkmingai pridëtas.");
				}
			}
			DeletePVar(playerid, "Tmp.Phonenumber");
			return 1;
		}
		case DIALOG_PLAYER_PHONE_OPTIONS:
		{
			if(!response)	
				ShowPlayerPhoneBookMenu(playerid);
			else 
			{
				new bookindex = GetPVarInt(playerid, "UsedContact");
				switch(listitem)
				{
					// Skambinti
					case 0:
					{
		//				PlayerCallNumber(playerid, PlayerPhoneBook[ playerid ][ bookindex ][ PhoneNumber ]);
						new phonenumber = PlayerPhoneBook[ playerid ][ bookindex ][ PhoneNumber ];

						if(IsServicePhoneNumber(phonenumber))
						{
							OnPlayerCallService(playerid, phonenumber);
						}
						else 
						{

							new targetid = FindPlayerByPhoneNumber(phonenumber);
							if(targetid == INVALID_PLAYER_ID || GetPVarInt(targetid, "PHONE_STATUS"))
								SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: su abonentu susisiekti neámanoma, praðome pabandyti vëliau.");

							else if(MobilePhone[ targetid ] != INVALID_PLAYER_ID)
								SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: numeris uþimtas.");

							else 
								OnPlayerCallPlayer(playerid, targetid);
						}	
					}
					// SMS 
					case 1:
					{
						ShowPlayerNewSMSDialog(playerid);
					}
					// Delete
					case 2:
					{
						RemovePlayerContact(playerid, bookindex);
						SendClientMessage(playerid, COLOR_NEWS, "Kontaktas iðtrintas ið telefono atminties.");
					}
				}
			}
			return 1;
		}
		case DIALOG_PLAYER_PHONE_NEW_SMS:
		{
			if(response)
			{
				if(isnull(inputtext))
					ShowPlayerNewSMSDialog(playerid, "Neávedëte teksto.");
				else 
				{
					new phonenumber = PlayerPhoneBook[ playerid ][ GetPVarInt(playerid, "UsedContact") ][ PhoneNumber ];

					if(!IsServicePhoneNumber(phonenumber) && !IsValidPlayerNumber(phonenumber))
						ShowPlayerNewSMSDialog(playerid, "Þinutës iðsiøsti nepavyko.");

					else 
						PlayerSendSms(playerid, phonenumber, inputtext);
					DeletePVar(playerid, "UsedContact");
				}
			}
			return 1;
		}
		case DIALOG_PLAYER_PHONE_SMS_RECEIVE:
		{
			if(!response)
				ShowPlayerPhoneMenu(playerid);
			else 
			{
				// Jei ne pirmas puslapis, gali pasirinkti "Atgal"
				if(PlayerSMSListPage[ playerid ] && !listitem)
				{
					PlayerSMSListPage[ playerid ]--;
					ShowPlayerReceivedMessages(playerid, PlayerSMSListPage[ playerid ]);
				}
				else if((PlayerSMSListPage[ playerid ] && listitem == MAX_SMS_PER_PAGE+2) || (!PlayerSMSListPage[ playerid ] && listitem == MAX_SMS_PER_PAGE+1))
				{
					PlayerSMSListPage[ playerid ]++;
					ShowPlayerReceivedMessages(playerid, PlayerSMSListPage[ playerid ]);
				}
				else 
				{
					new tmp[ 16 ];
					strmid(tmp, inputtext, 0, strfind(inputtext, "."));
					ShowPlayerSMS(playerid, strval(tmp));
				}
			}	
			return 1;
		}
		case DIALOG_PLAYER_PHONE_SMS_SENT:
		{
			if(!response)
				ShowPlayerPhoneMenu(playerid);
			else 
			{
				// Jei ne pirmas puslapis, gali pasirinkti "Atgal"
				if(PlayerSMSListPage[ playerid ] && !listitem)
				{
					PlayerSMSListPage[ playerid ]--;
					ShowPlayerSentMessages(playerid, PlayerSMSListPage[ playerid ]);
				}
				else if((PlayerSMSListPage[ playerid ] && listitem == MAX_SMS_PER_PAGE+2) || (!PlayerSMSListPage[ playerid ] && listitem == MAX_SMS_PER_PAGE+1))
				{
					PlayerSMSListPage[ playerid ]++;
					ShowPlayerSentMessages(playerid, PlayerSMSListPage[ playerid ]);
				}
				else 
				{
					new tmp[ 16 ];
					strmid(tmp, inputtext, 0, strfind(inputtext, "."));
					ShowPlayerSMS(playerid, strval(tmp));
				}
			}	
			return 1;
		}
		case DIALOG_PLAYER_PHONE_SMS_CONTENT:
		{
			ShowPlayerPhoneMenu(playerid);
			return 1;
		}
	}
	return 0;
}

public OnPlayerCallPlayer(playerid, targetplayer)
{
	new string[ 128 ];

    SetPVarInt(playerid, "CallOwner", true);
    MobilePhone[playerid] = targetplayer;

    cmd_ame(playerid, "iðsitraukia telefonà ið kiðenës ir surenka numerá.");

    if(IsNumberInPlayerPhonebook(targetplayer, GetPlayerPhoneNumber(playerid))) 
    	format(string, sizeof(string), "Jûsø telefonas skamba (/p)ickup, skambina: %s", 
    		GetPlayerPhonebookName(targetplayer, GetPlayerPhoneNumber(playerid)));
    else 
    	format(string, sizeof(string), "Jûsø telefonas skamba (/p)ickup, skambina: %d", GetPlayerPhoneNumber(playerid));
    SendClientMessage(targetplayer, COLOR_LIGHTRED, string);

    GetPlayerName(targetplayer, string, MAX_PLAYER_NAME);
    SetPlayerSpecialAction(playerid, SPECIAL_ACTION_USECELLPHONE);

    format(string, sizeof(string), "* %s skamba ir vibruoja kisenëje telefonas.", string);
    SendClientMessage(playerid, COLOR_WHITE, "Naudokite T, kad kalbëtumete telefonu. (/h)angup kad padëtumete ragelá");
    ProxDetector(0.0, targetplayer, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);

    new Float:x, Float:y, Float:z;
    GetPlayerPos(targetplayer, x, y, z);

    PlayerPlaySound(playerid, 3600, 0,0,0);
    PlaySoundForPlayersInRange(20600, 10.5, x,y,z);
    RingTone[ targetplayer ] = 10;
}


public OnPlayerCallService(playerid, phonenumber)
{
	switch(phonenumber)
	{
		case 911:
		{
			SendClientMessage(playerid, GRAD, "INFORMACIJA: Naudokite T, kad kalbëtumëte á telefonà. (/h)angup kad padëtumëte rageli.");
	        SendClientMessage(playerid, COLOR_FADE1, "PAGALBOS LINIJA: Su kuo jus sujungti? Policija, medikais? Ar su abu?");
	        MobilePhone[playerid] = 911;
		}
		case 816:
		{
			SendClientMessage(playerid, GRAD, "INFORMACIJA: Naudokite T, kad kalbëtumëte á telefonà. (/h)angup kad padëtumëte ragelá.");
	        SendClientMessage(playerid, COLOR_FADE1, "Moteriðkas balsas: Laba diena. Mechanikø dirbtuvës klauso. Pasakykite vietà  kur jums reikia mechaniko.");
	        MobilePhone[playerid] = 816;
		}
		case 817:
		{
			SendClientMessage(playerid, GRAD, "INFORMACIJA: Naudokite T, kad kalbëtumëte i telefonà. (/h)angup kad padëtumëte rageli.");
	        SendClientMessage(playerid, COLOR_FADE1, "Moteriðkas balsas: Laba diena, taksi dispeøterë klauso, pasakykite vieta kur jums reikia taksi.");
	        MobilePhone[playerid] = 817;
		}
		case 999:
		{
			MobilePhone[playerid] = 999;
		}
	}
	SetPlayerSpecialAction(playerid, SPECIAL_ACTION_USECELLPHONE);
    cmd_ame(playerid, "iðsitraukia telefonà ið kiðenës ir surenka numerá.");
    PlayerPlaySound(playerid, 3600, 0,0,0);
	return 1;
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

stock ShowPlayerPhoneMenu(playerid)
{
    ShowPlayerDialog(playerid, DIALOG_PLAYER_PHONE_MAIN, DIALOG_STYLE_LIST, "Telefono meniu", 
    	"{FFFFFF}Laikas\n\
    	Telefono knyga\n\
    	Gautos þinutës\n\
    	Iðsiøstos þinutës", 
    	"Pasirinkti", "Iðeiti");
   	return 1;
}

stock ShowPlayerPhoneBookMenu(playerid)
{
	new string[ 1024 ];
	string = "Pridëti naujà kontaktà\n{FFFFFF}";

	for(new i = 0; i < sizeof PlayerPhoneBook[]; i++)
	{
		if(PlayerPhoneBook[ playerid ][ i ][ PhoneNumber ])
			format(string, sizeof(string), "%s%s\t%d\n",
				string,
				PlayerPhoneBook[ playerid ][ i ][ Name ],
				PlayerPhoneBook[ playerid ][ i ][ PhoneNumber ]);
	}

	ShowPlayerDialog(playerid, DIALOG_PLAYER_PHONE_PHONEBOOK, DIALOG_STYLE_LIST, "Telefonø knyga", string, "Pasirinkti", "Atgal");
	return 1;
}


stock ShowPlayerNewPhoneContactDialog(playerid, E_NEW_CONTACT_INPUT_TYPE:type, errorstr[] = "")
{
	new string[ 128 ];
	if(type == CONTACT_INPUT_TYPE_NUMBER)
	{
		format(string, sizeof(string), "{AA1111}%s\n{FFFFFF}Áveskite naujo kontakto numerá.", errorstr);
		ShowPlayerDialog(playerid, DIALOG_PLAYER_PHONE_BOOK_NEW_1, DIALOG_STYLE_INPUT, "Naujas kontaktas: numeris", string, "Toliau", "Atgal");
	}
	else if(type == CONTACT_INPUT_TYPE_NAME)
	{
		format(string, sizeof(string), "{AA1111}%s\n{FFFFFF}Áveskite naujo kontakto vardà.", errorstr);
		ShowPlayerDialog(playerid, DIALOG_PLAYER_PHONE_BOOK_NEW_2, DIALOG_STYLE_INPUT, "Naujas kontaktas: vardas", string, "Toliau", "Atgal");
	}
	return 1;
}

stock ShowPlayerNewSMSDialog(playerid, errorstr[] = "")
{
	new string[ 100 ];
	format(string, sizeof(string), "{AA1111}%s\n{FFFFFF}Áveskite þinutës tekstà", errorstr);
	ShowPlayerDialog(playerid, DIALOG_PLAYER_PHONE_NEW_SMS, DIALOG_STYLE_INPUT, "Nauja SMS þinutë", string, "Siøsti", "Atgal");
	return 1;
}


stock IsPlayerPhonebookFull(playerid)
{
	if(PlayerPhoneBook[ playerid ][ sizeof PlayerPhoneBook[] -1 ][ PhoneNumber ])
		return true;
	else 
		return false;
}

stock IsNumberInPlayerPhonebook(playerid, number)
{
	for(new i = 0; i < sizeof PlayerPhoneBook[]; i++)
		if(PlayerPhoneBook[ playerid ][ i ][ PhoneNumber ] == number)
			return true;
	return false;
}
stock IsPlayerPhoneOff(playerid)
{
	if(GetPVarInt(playerid, "PHONE_STATUS") == 1)
		return true;
	else 
		return false;
}
stock TurnPlayerPhoneOn(playerid)
{
	return DeletePVar(playerid, "PHONE_STATUS");
}

GetPlayerPhonebookNumber(playerid, name[])
{
    for(new i = 0; i < MAX_PHONEBOOK_ENTRIES; i++)
        if(!isnull(PlayerPhoneBook[ playerid ][ i ][ Name ]) && !strcmp(PlayerPhoneBook[ playerid ][ i ][ Name ], name, true))
            return PlayerPhoneBook[ playerid ][ i ][ PhoneNumber ];
    return 0;
}
GetPlayerPhonebookName(playerid, number)
{
    new s[ MAX_PLAYER_NAME ];
    for(new i = 0; i < MAX_PHONEBOOK_ENTRIES; i++)
        if(PlayerPhoneBook[ playerid ][ i ][ PhoneNumber ] == number)
        {
            strcat(s, PlayerPhoneBook[ playerid ][ i ][ Name ]);
            break;
        }
    return s;
}

stock ShowPlayerReceivedMessages(playerid, page = 0)
{
	new query[ 110 ];
	mysql_format(DbHandle, query, sizeof(query), "SELECT * FROM player_phone_sms WHERE recipient_number = %d ORDER BY `date` ASC LIMIT %d, %d",
		GetPlayerPhoneNumber(playerid), 
		page * MAX_SMS_PER_PAGE, 
		((page+1) * MAX_SMS_PER_PAGE)+1); // Imam viena daugiau, kad þinutume ar bus kitas puslapis.
	return mysql_pquery(DbHandle, query, "OnPlayerReceivedMessageLoad", "ii", playerid, page);
}

stock ShowPlayerSentMessages(playerid, page = 0)
{
	new query[ 110 ];
	mysql_format(DbHandle, query, sizeof(query), "SELECT * FROM player_phone_sms WHERE sender_number = %d ORDER BY `date` ASC LIMIT %d, %d", 
		GetPlayerPhoneNumber(playerid), 
		page * MAX_SMS_PER_PAGE, 
		((page+1) * MAX_SMS_PER_PAGE)+1); // Imam viena daugiau, kad þinutume ar bus kitas puslapis.
	return mysql_pquery(DbHandle, query, "OnPlayerSentMessageLoad", "ii", playerid, page);
}


stock PlayerSendSms(playerid, phonenumber, text[])
{
	new string[ 256 ], targetplayer, 
		sendernumber = GetPlayerPhoneNumber(playerid);
	mysql_format(DbHandle, string, sizeof(string), "INSERT INTO player_phone_sms (sender_number, recipient_number, `text`) VALUES (%d, %d, '%e')",
		GetPlayerPhoneNumber(playerid),
		phonenumber, 
		text);
	mysql_pquery(DbHandle, string);

	format(string, sizeof(string), "* %s iðsitraukia mobilujá telefonà, paraðæs SMS þinutæ, iðsiunèia jà.", GetPlayerNameEx(playerid));
    ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);

   	targetplayer = FindPlayerByPhoneNumber(phonenumber);
   	if(targetplayer != INVALID_PLAYER_ID)
   	{
   		if(IsNumberInPlayerPhonebook(targetplayer, sendernumber))
   			format(string, sizeof(string), "SMS: %s, siuntëjas: %s", 
   				text, GetPlayerPhonebookName(targetplayer, sendernumber));
   		else 
   			format(string, sizeof(string), "SMS: %s, siuntëjas: %d", text, sendernumber);

	    SendClientMessage(playerid, COLOR_LIGHTRED2, "Trumpoji þinutë buvo sëkmingai nusiûsta adresatui.");
	    SendChatMessage(targetplayer, COLOR_LIGHTRED2, string);
	    format(string, sizeof(string), "SMS: %s", text);
	    SendChatMessage(playerid, COLOR_WHITE, string);
		PlayerPlaySound(targetplayer, 1052, 0.0, 0.0, 0.0);
	}
    ShowInfoText(playerid, "~w~ SMS kaina $1", 5000);
    GivePlayerMoney(playerid,-1);
    return 1;
}

/*
GivePlayerPhone(playerid, phonenumber)
{
	new slot = -1;
	for(new i = 0; i < MAX_PLAYER_PHONES; i++)
		if(!PlayerPhoneNumbers[ playerid ][ i ])
		{
			slot = i ;
			break;
		}

	if(slot == -1)
		return 0;

	PlayerPhoneNumbers[ playerid ][ slot ] = phonenumber;

	new query[];
	mysql_format(DbHandle, query, "INSERT INTO ")
}
*/
stock IsServicePhoneNumber(phonenumber)
{
	switch(phonenumber)
	{
		case 911, 816, 817, 999:
			return true;
		default:
			return false;
	}
	return false;
}

stock ShowPlayerSMS(playerid, smssqlid)
{
	new string[ 1024 ], date[ 32 ], sender[ MAX_PHONEBOOK_CONTACT_NAME ], reciever[ MAX_PHONEBOOK_CONTACT_NAME ], 
		Cache:result, number;

	mysql_format(DbHandle, string, sizeof(string), "SELECT * FROM player_phone_sms WHERE id = %d", smssqlid);
	result = mysql_query(DbHandle, string);
	if(cache_get_row_count())
	{
		number = cache_get_field_content_int(0, "sender_number");
		if(number == GetPlayerPhoneNumber(playerid))
		{
			GetPlayerName(playerid, sender, MAX_PLAYER_NAME);
		}
		else if(IsNumberInPlayerPhonebook(playerid, number))
			strcat(sender, GetPlayerPhonebookName(playerid, number));
		else 
			format(sender, sizeof(sender), "%d", number);

		number = cache_get_field_content_int(0, "recipient_number");
		if(number == GetPlayerPhoneNumber(playerid))
		{
			GetPlayerName(playerid, reciever, MAX_PLAYER_NAME);
			if(!cache_get_field_content_int(0, "read"))
			{
				mysql_format(DbHandle, string, sizeof(string), "UPDATE player_phone_sms SET `read` = 1 WHERE id = %d", smssqlid);
				mysql_pquery(DbHandle, string);
			}
		}
		else if(IsNumberInPlayerPhonebook(playerid, number))
			strcat(reciever, GetPlayerPhonebookName(playerid, number));
		else 
			format(reciever, sizeof(reciever), "%d", number);


		cache_get_field_content(0, "date", date);
		cache_get_field_content(0, "text", string);

		format(string, sizeof(string), "Siuntëjas: %s\nGavëjas: %s\nData: %s\n\n\n%s",
			sender,
			reciever,
			date,
			string);
		ShowPlayerDialog(playerid, DIALOG_PLAYER_PHONE_SMS_CONTENT, DIALOG_STYLE_MSGBOX, "SMS", string, "Gerai", "");
	}	
	cache_delete(result);
	return 1;
}


FindPlayerByPhoneNumber(phonenumber)
{
    foreach(new i : Player)
        if(GetPlayerPhoneNumber(i) == phonenumber)  
            return i;
    return INVALID_PLAYER_ID;
}

IsValidPlayerNumber(phonenumber)
{
    if(FindPlayerByPhoneNumber(phonenumber) != INVALID_PLAYER_ID)   
        return true;

    else 
    {
        new query[70], Cache:result, bool:isValid;
        mysql_format(DbHandle, query, sizeof(query), "SELECT id FROM players WHERE PhoneNr = %d", phonenumber);
        result = mysql_query(DbHandle, query);
        if(cache_get_row_count())
            isValid = true;
        cache_delete(result);
        return isValid;
    }
}



/* MySQL */

stock AddPlayerContact(playerid, phonenumber, name[])
{
	new freeindex = -1, query[ 256 ];
	for(new i = 0; i < sizeof PlayerPhoneBook[]; i++)
	{
		if(!PlayerPhoneBook[ playerid ][ i ][ PhoneNumber ])
		{
			freeindex = i;
			break;
		}
	}
	if(freeindex == -1)
		return 0;

	mysql_format(DbHandle, query, sizeof(query), "INSERT INTO player_phone_contacts (player_id, phone_number, name, entry_date) VALUES (%d, %d, '%e', %d) \ 
		ON DUPLICATE KEY UPDATE name = VALUES(name), entry_date = VALUES(entry_date)",
		GetPlayerSqlId(playerid), phonenumber, name, gettime());

	PlayerPhoneBook[ playerid ][ freeindex ][ PhoneNumber ] = phonenumber;
	format(PlayerPhoneBook[ playerid ][ freeindex ][ Name ], MAX_PHONEBOOK_CONTACT_NAME, name);
	return mysql_pquery(DbHandle, query);
}


stock RemovePlayerContact(playerid, phonebookindex)
{
	new query[ 100 ];
	mysql_format(DbHandle, query, sizeof(query), "DELETE FROM player_phone_contacts WHERE player_id = %d AND phone_number = %d",
		GetPlayerSqlId(playerid), PlayerPhoneBook[ playerid ][ phonebookindex ][ PhoneNumber ]);

	new i =0;
	for(i = phonebookindex; i < sizeof PlayerPhoneBook[] - 1; i++)
	{
		PlayerPhoneBook[ playerid ][ i ][ PhoneNumber ] = PlayerPhoneBook[ playerid ][ i + 1][ PhoneNumber ];
		format(PlayerPhoneBook[ playerid ][ i ][ Name ], MAX_PHONEBOOK_CONTACT_NAME, PlayerPhoneBook[ playerid ][ i + 1][ Name ]);
	}
	PlayerPhoneBook[ playerid ][ i ][ PhoneNumber ] = 0;
	strdel(PlayerPhoneBook[ playerid ][ i ][ Name ], 0, strlen(PlayerPhoneBook[ playerid ][ i ][ Name ]));
	return mysql_pquery(DbHandle, query);
}

public OnPlayerReceivedMessageLoad(playerid, page)
{
	if(!cache_get_row_count())
	{
		SendClientMessage(playerid, COLOR_LIGHTRED, "Jûs dar negavote nei vienos þinutës.");
		ShowPlayerPhoneMenu(playerid);
	}
	else
	{
		new string[ 1024 ], text[ 32 ], date[ 32 ], sender[ MAX_PHONEBOOK_CONTACT_NAME ], read, number;
		if(page)
			strcat(string, "{002200}Atgal\n{FFFFFF}");
		else 
			strcat(string, "{FFFFFF}");

		for(new i = 0; i < cache_get_row_count(); i++)
		{
			number = cache_get_field_content_int(i, "sender_number");
			if(IsNumberInPlayerPhonebook(playerid, number))
			{
				strcat(sender, GetPlayerPhonebookName(playerid, number));
			}
			else 
			{
				format(sender, sizeof(sender), "%" #PLAYER_PHONE_NUMBER_LENGTH "d", number);
			}

			cache_get_field_content(i, "date", date);
			// Trumpinam tekstà, nes èia rodysim tik dalá.
			cache_get_field_content(i, "text", text, DbHandle, sizeof(text) - 4);
			strcat(text, "...");

			read = (cache_get_field_content_int(i, "read")) ? ('+') : ('-');

			// Eilutë turi prasidëti "{sqlid}.". Ta ID naudojamas OndialogResponse
			format(string, sizeof(string), "%s%d. [%c]%s: %s\n",
				string, 
				cache_get_field_content_int(i, "id"),
				read,
				sender,
				text);
		}

		if(cache_get_row_count() > MAX_SMS_PER_PAGE)
			strcat(string, "{220000}Toliau");

		ShowPlayerDialog(playerid, DIALOG_PLAYER_PHONE_SMS_RECEIVE, DIALOG_STYLE_LIST, "Gautos þinutës", string, "Pasirinkti", "Iðeiti");
	}
	return 1;
}

public OnPlayerSentMessageLoad(playerid, page)
{
	if(!cache_get_row_count())
	{
		SendClientMessage(playerid, COLOR_LIGHTRED, "Jûs dar neiðsiuntëtë nei vienos þinutës.");
		ShowPlayerPhoneMenu(playerid);
	}
	else 
	{
		new string[ 1024 ], text[ 32 ], date[ 32 ], sender[ MAX_PHONEBOOK_CONTACT_NAME ], read, number;
		if(page)
			strcat(string, "{002200}Atgal\n{FFFFFF}");
		else 
			strcat(string, "{FFFFFF}");

		for(new i = 0; i < cache_get_row_count(); i++)
		{
			number = cache_get_field_content_int(i, "recipient_number");
			if(IsNumberInPlayerPhonebook(playerid, number))
			{
				strcat(sender, GetPlayerPhonebookName(playerid, number));
			}
			else 
			{
				format(sender, sizeof(sender), "%" #PLAYER_PHONE_NUMBER_LENGTH "d", number);
			}

			cache_get_field_content(i, "date", date);
			// Trumpinam tekstà, nes èia rodysim tik dalá.
			cache_get_field_content(i, "text", text, DbHandle, sizeof(text) - 4);
			strcat(text, "...");

			// Eilutë turi prasidëti "{sqlid}.". Ta ID naudojamas OndialogResponse
			format(string, sizeof(string), "%s%d. [+]%s: %s\n",
				string, 
				cache_get_field_content_int(i, "id"),
				read,
				sender,
				text);
		}

		if(cache_get_row_count() > MAX_SMS_PER_PAGE)
			strcat(string, "{220000}Toliau");

		ShowPlayerDialog(playerid, DIALOG_PLAYER_PHONE_SMS_SENT, DIALOG_STYLE_LIST, "Iðsiøstos þinutës", string, "Pasirinkti", "Iðeiti");
	}
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



CMD:addcontact(playerid, params[])
{
    new number, name[ MAX_PHONEBOOK_CONTACT_NAME ], query[256];
    if(PlayerPhoneBook[ playerid ][ sizeof(PlayerPhoneBook[])-1][ PhoneNumber ])
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, daugiau konktaktø netelpa á Jûsø knygute.");
    if(sscanf(params,"is[" #MAX_PHONEBOOK_CONTACT_NAME "]", number, name))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Komandos naudojimas: /addcontact [NUMERIS] [VARDAS]");
    query = GetPlayerPhonebookName(playerid, number);
    if(!isnull(query))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Ðis numeris jau yra jûsø kontaktuose.");

    AddPlayerContact(playerid, number, name);
    SendClientMessage(playerid, COLOR_NEWS, "Kontaktas buvo sëkmingas pridëtas á Jûsø adresø knygutæ.");
    return 1;
}

CMD:deletecontact(playerid, params[])
{
    new id;
    if(sscanf(params,"i", id))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Komandos naudojimas: /deletecontact [Eiles Numeris]");
    if(id < 1 || id >= MAX_PHONEBOOK_ENTRIES)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio, kontaktø knygoje galimi numeriai yra nuo 1 iki " #MAX_PHONEBOOK_ENTRIES);
    if(!PlayerPhoneBook[ playerid ][ id-1 ][ PhoneNumber ])
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, pasirinktoje vietoje nëra jokio kontakto");

    RemovePlayerContact(playerid, id-1);
    SendClientMessage(playerid, COLOR_NEWS, "Kontaktas buvo sëkmingas paðalintas ið adresø knygutës.");
    return 1;
}

CMD:phonebook(playerid)
{
    new query[180],name[MAX_PLAYER_NAME], date[32], number, count = 1, Cache:result;
    format(query, sizeof(query), "SELECT phone_number,name,FROM_UNIXTIME(entry_date) AS date FROM player_phone_contacts WHERE player_id = %d ORDER BY entry_date ASC", pInfo[ playerid ][ pMySQLID ]);
    result = mysql_query(DbHandle, query);
    SendClientMessage(playerid, COLOR_GREEN, "|_____________KONTAKTØ SÀRAÐAS_____________|");
    for(new i = 0; i < cache_get_row_count(); i++)
    {
        number = cache_get_field_content_int(i, "phone_number");
        cache_get_field_content(i, "name", name);
        cache_get_field_content(i, "date", date);
        format(query, sizeof(query), "%d. Vardas: %s Numeris: %d Pridëtas: %s",count++, name, number, date);
        SendClientMessage(playerid, COLOR_WHITE, query);

    }
    cache_delete(result);
    return 1;
}




CMD:call(playerid, params[ ])
{
    if(!IsItemInPlayerInventory(playerid, ITEM_PHONE))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Tu neturi mobilaus telefono.");
    if(Mires[ playerid ] > 0)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûsø veikëjas ðiuo metu yra kritinëje arba komos bûsenoje.");
    if(Mute[ playerid ] == true)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðiuo metu Jums yra uþdrausta kalbëtis (/mute), norëdami paðalinti draudimà susisiekite su Administratoriumi.");
    if(MobilePhone[ playerid ] != INVALID_PLAYER_ID)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Pirma padëkite ragelá /h ");
    if(GetPVarInt(playerid, "PHONE_STATUS") == 1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Jûsø telefonas yra iðjungtas. ");
    if(pInfo[playerid][pJail] > 0)
        return SendClientMessage(playerid, COLOR_GREY, "   Telefono ryðys blokuojamas.");
    
    new phonenumb;

    if(sscanf(params, "i", phonenumb))
    {
        if(!isnull(params))
        {
            // Patikrinam gal ávedë vardà ið kontaktø sàraðo.
            phonenumb = GetPlayerPhonebookNumber(playerid, params);
        }
        if(!phonenumb || isnull(params))
        {
            SendClientMessage(playerid, GRAD,        "KOMANDOS NAUDOJIMAS: /call [telefono numeris/kontakto vardas]");
            SendClientMessage(playerid, COLOR_GREEN2, "____________Los Santos paslaugos____________");
            SendClientMessage(playerid, COLOR_WHITE, "911 - Pagalbos linija");
            SendClientMessage(playerid, COLOR_WHITE, "816 - Mechanikai, 817 - Taksi, 999 - San News");
            SendClientMessage(playerid, COLOR_GREEN2, "____________________________________________");
            return 1;
        }
    }
    if(phonenumb == pInfo[ playerid ][ pPhone ])
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs negalite skambinti sau!");

    if(IsServicePhoneNumber(phonenumb))
    	OnPlayerCallService(playerid, phonenumb);

    else if(IsValidPlayerNumber(phonenumb))
    {
    	new targetid = FindPlayerByPhoneNumber(phonenumb);

		if(targetid == INVALID_PLAYER_ID)
			return 0;
		
		if(MobilePhone[ targetid ] != INVALID_PLAYER_ID)
        	return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Numeris ðiuo metu uþimtas. ");

		if(GetPVarInt(targetid, "PHONE_STATUS") == 1)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Telefonas yra iðjungtas. ");

        if(pInfo[ targetid ][ pJail ] > 0)
            return SendClientMessage(playerid, COLOR_GREY, "   Telefono ryðys blokuojamas.");

        OnPlayerCallPlayer(playerid, targetid);
    }
    else 
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Numeris ðiuo metu uþimtas. ");
    return 1;
}
CMD:pickup(playerid)
{
    if(MobilePhone[ playerid ] != INVALID_PLAYER_ID)
        return SendClientMessage(playerid, GRAD, "Jums niekas neskambina.");
    if(pInfo[ playerid ][ pPhone ] == 0)
        return SendClientMessage(playerid, GRAD, "KLAIDAI: Tu neturi mobilaus telefono.");
    if(Mires[ playerid ] > 0)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûsø veikëjas ðiuo metu yra kritinëje arba komos bûsenoje.");
    if(Mute[ playerid ] == true)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðiuo metu Jums yra uþdrausta kalbëtis (/mute), norëdami paðalinti draudimà susisiekite su Administratoriumi.");

    foreach(Player,i)
    {
        if(MobilePhone[ i ] == playerid)
        {
            MobilePhone[ playerid ] = i;
            RingTone   [ playerid ] = 0;

            SendClientMessage(i,  GRAD, "Jis pakëlë telefono ragelá.");
            cmd_ame(playerid, "iðtraukia telefona ið kiðenës ir atsiliepia.");

            SetPlayerSpecialAction(playerid, SPECIAL_ACTION_USECELLPHONE);
            return 1;
        }
    }
    return SendClientMessage(playerid, GRAD, "Jums pasivaideno, kad jums kaðkas skambino.");
}
CMD:p(playerid)
    return cmd_pickup(playerid);

CMD:hangup(playerid)
{
    if(Mires[ playerid ] > 0)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûsø veikëjas ðiuo metu yra kritinëje arba komos bûsenoje.");
    if(Mute[ playerid ] == true)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðiuo metu Jums yra uþdrausta kalbëtis (/mute), norëdami paðalinti draudimà susisiekite su Administratoriumi.");

    if(MobilePhone[ playerid ] > MAX_PLAYERS)
    {
        SetPlayerSpecialAction(playerid, SPECIAL_ACTION_STOPUSECELLPHONE);
        RemovePlayerAttachedObject(playerid, 3);
        MobilePhone[ playerid ] = INVALID_PLAYER_ID;
        RingTone   [ playerid ] = 0;
        if(NearPhone(playerid))
            TogglePlayerControllable(playerid, true);
    }
    else if(MobilePhone[ playerid ] != INVALID_PLAYER_ID && MobilePhone[ playerid ] < MAX_PLAYERS)
    {
        if(MobilePhone[ MobilePhone[ playerid ] ] == playerid)
        {
            SendClientMessage(MobilePhone[ playerid ], GRAD, "Jis/Ji padëjo telefono ragelá.");

            SetPlayerSpecialAction(playerid, SPECIAL_ACTION_STOPUSECELLPHONE);
            RemovePlayerAttachedObject(playerid, 3);
            SetPlayerSpecialAction(MobilePhone[ playerid ], SPECIAL_ACTION_STOPUSECELLPHONE);
            RemovePlayerAttachedObject(MobilePhone[ playerid ], 3);

            SetPVarInt(MobilePhone[ playerid ], "CallOwner", false);
            MobilePhone[ MobilePhone[ playerid ] ] = INVALID_PLAYER_ID;
        }
        SetPVarInt(playerid, "CallOwner", false);
        RingTone   [ MobilePhone[ playerid ] ] = 0;
        MobilePhone[ playerid ] = INVALID_PLAYER_ID;
        RingTone   [ playerid ] = 0;

        if(NearPhone(playerid))
            TogglePlayerControllable(playerid, true);

        if(NearPhone(MobilePhone[ playerid ]))
            TogglePlayerControllable(MobilePhone[ playerid ], true);
    }
    else if(RingTone[ playerid ] != 0)
    {
        foreach (Player, i)
        {
            if(PlayerOn[ i ] == true) continue;
            if(MobilePhone[ i ] == playerid)
            {
                SendClientMessage(i, GRAD, "Jis/Ji padëjo telefono ragelá.");
                if(NearPhone(playerid))
                    TogglePlayerControllable(playerid , true);

                SetPVarInt(i, "CallOwner", false);
                SetPVarInt(playerid, "CallOwner", false);

                MobilePhone[ i ] = INVALID_PLAYER_ID;
                RingTone   [ i ] = 0;
                RingTone   [ playerid ] = 0;
                break;
            }
        }
    }
    return 1;
}
CMD:h(playerid)
    return cmd_hangup(playerid);



CMD:sms(playerid, params[ ])
{
    if(pInfo[playerid][pPhone] == 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Tu neturi mobilaus telefono.");
    else if(Mires[playerid] > 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûsø veikëjas ðiuo metu yra kritinëje arba komos bûsenoje.");
    else if(Mute[playerid] == true) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðiuo metu Jums yra uþdrausta kalbëtis (/mute), norëdami paðalinti draudimà susisiekite su Administratoriumi.");
    else if(PlayerMoney[ playerid ] < 1) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs neturite $1 ");
    else if(GetPVarInt(playerid, "PHONE_STATUS") == 1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, turimas mobilusis telefonas iðjungtas.");
    else if(pInfo[playerid][pJail] > 0)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, èia mobilaus telefono ryðys yra blokuojamas.");
    new 
        phonenumb,
        gMessage[ 256 ];

    if(sscanf(params, "ds[240]", phonenumb, gMessage))
    {
        new target[ MAX_PLAYER_NAME ];
        if(strfind(params, " ") != -1) // Jei yra bent du string'ai 
        {
            strmid(target, params, 0, strfind(params, " "));
            phonenumb = GetPlayerPhonebookNumber(playerid, target);
            strmid(gMessage, params, strfind(params, " "), strlen(params));
        }   
    }
    if(phonenumb == 0)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /sms [ADRESATO NUMERIS][ÞINUTË]");
    
    new targetid = FindPlayerByPhoneNumber(phonenumb);

    if(targetid == INVALID_PLAYER_ID)
    	return 1;



    if(GetPVarInt(targetid, "PHONE_STATUS") == 1)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, turimas mobilusis telefonas iðjungtas.");

    PlayerSendSms(playerid, phonenumb, gMessage);
    return 1;
}

CMD:turnphone(playerid)
{
    new string[ 126 ];
    if(!IsItemInPlayerInventory(playerid, ITEM_PHONE)) 
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs neturite su savimi telefono. ");

    if(!GetPVarInt(playerid, "PHONE_STATUS"))
    {
        SetPVarInt(playerid, "PHONE_STATUS", 1);
        format(string, sizeof(string), "* %s iðjungia telefonà." ,GetPlayerNameEx(playerid));
        ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);

    }
    else
    {
        SetPVarInt(playerid, "PHONE_STATUS", 0);
        format(string, sizeof(string), "* %s ájungia telefonà." ,GetPlayerNameEx(playerid));
        ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
    }
    return 1;
}
CMD:speaker(playerid)
{
    new string[ 126 ];
    if(pInfo[ playerid ][ pPhone ] == 0 ) 
    	return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs neturite su savimi telefono. ");

    if(NearPhone(playerid)) 
   		return SendClientMessage( playerid, COLOR_GREY, "   Jûs dabar kalbat per taksofonà!");

    if(MobilePhone[ playerid ] == INVALID_PLAYER_ID)
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Jûs su niekuo nekalbate.");

    if(GetPVarInt(playerid, "SPEAKER") == 0)
    {
        SetPVarInt(playerid, "SPEAKER", 1);
        format(string, sizeof(string), "* %s ájungia telefono garsiakalbá.", GetPlayerNameEx(playerid));
        ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
    }
    else
    {
        SetPVarInt(playerid, "SPEAKER", 0);
        format(string, sizeof(string), "* %s áðjungia telefono garsiakalbá." ,GetPlayerNameEx(playerid));
        ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
    }
    return 1;
}








