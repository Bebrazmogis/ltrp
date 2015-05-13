



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
#define DIALOG_PLAYER_PHONE_LIST 		8109



#define MAX_SMS_PER_PAGE 				20
#define PLAYER_PHONE_NUMBER_LENGTH		6
#define MAX_PLAYER_PHONES 				5



enum E_PLAYER_TALK_SESSION
{
	bool:CallOwner,
	bool:ContactAnswered,
	PhoneIndex,
	ContactNumber,
	AnswerTimestamp,
	CallTimestamp,
	Timer:TalkTimer,
	Speaker,
};

static PlayerPhones[ MAX_PLAYERS ][ MAX_PLAYER_PHONES ][ E_PRIVATE_PHONE_DATA ],
	PlayerUsedPhone[ MAX_PLAYERS ],
	PlayerTalkSession[ MAX_PLAYERS ][ E_PLAYER_TALK_SESSION ];


enum E_NEW_CONTACT_INPUT_TYPE 
{
	CONTACT_INPUT_TYPE_NUMBER = 1,
	CONTACT_INPUT_TYPE_NAME = 2,
};

static PlayerSMSListPage[ MAX_PLAYERS ];

forward bool:IsPlayerCallOwner(playerid);

forward OnPlayerReceivedMessageLoad(playerid, phoneindex, page);
forward OnPlayerSentMessageLoad(playerid, phoneindex, page);
forward OnPlayerPhoneLoad(playerid);

forward OnPlayerCallNumber(playerid, callerphoneindex, phonenumber);
forward OnPlayerTalkSessionEnd(callownerid, contactid, callerphonenumber, contactnumber, talklength, bool:endedbyowner);
//forward OnPlayerCallPlayer(playerid, targetplayer);
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

	new query[ 80 ];
	mysql_format(DbHandle, query, sizeof(query), "SELECT * FROM phones WHERE location_type = %d AND location_id = %d", _:PlayerInventory, GetPlayerSqlId(playerid));
	mysql_pquery(DbHandle, query, "OnPlayerPhoneLoad", "i", playerid);
	return 1;
}
#if defined _ALS_OnPlayerFirstSpawn
	#undef OnPlayerFirstSpawn
#else 
	#define _ALS_OnPlayerFirstSpawn
#endif
#define OnPlayerFirstSpawn 				phone_OnPlayerFirstSpawn
#if defined phone_OnPlayerFirstSpawn
	forward phone_OnPlayerFirstSpawn(playerid);
#endif

public OnPlayerPhoneLoad(playerid)
{
	for(new i = 0; i  < cache_get_row_count(); i++)
	{
		PlayerPhones[ playerid ][ i ][ Number ] = cache_get_field_content_int(i, "number");
		PlayerPhones[ playerid ][ i ][ Online ] = (cache_get_field_content_int(i, "online"))?(true):(false);
	}
	return 1;
}

hook OnPlayerDisconnect(playerid, reason)
{
	static emptyPlayerPhone[ E_PRIVATE_PHONE_DATA ], emptyPlayerSession[ E_PLAYER_TALK_SESSION ];

	if(IsPlayerInTalkSession(playerid))
	{
		SendClientMessage(FindPlayerByPhoneNumber(PlayerTalkSession[ playerid ][ ContactNumber ]), COLOR_WHITE, "Staiga telefone nutrûko ryðys.");
		EndPlayerTalkSession(playerid, IsPlayerCallOwner(playerid));
	}
	
	for(new i = 0; i < MAX_PLAYER_PHONES; i++)
        PlayerPhones[ playerid ][ i ] = emptyPlayerPhone;

    PlayerUsedPhone[ playerid ] = -1;

    PlayerTalkSession[ playerid ] = emptyPlayerSession;
}

hook OnPlayerDeath(playerid, killerid, reason)
{
	if(IsPlayerInTalkSession(playerid))
	{
		SendClientMessage(FindPlayerByPhoneNumber(PlayerTalkSession[ playerid ][ ContactNumber ]), COLOR_WHITE, "Staiga telefone nutrûko ryðys.");
		EndPlayerTalkSession(playerid, IsPlayerCallOwner(playerid));
	}
	return 1;
}

hook OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
	switch(dialogid)
	{
		case DIALOG_PLAYER_PHONE_LIST:
		{
			if(response)
			{
				new tmp[16];
				strmid(tmp, inputtext, 0, strfind(inputtext, "\t"));
				PlayerUsedPhone[ playerid ] = GetPlayerPhoneIndex(playerid, strval(tmp));
				ShowPlayerPhoneMenu(playerid);
			}	
			return 1;
		}
		case DIALOG_PLAYER_PHONE_MAIN:
		{
			if(!response)
				return 1;

			if(IsPlayerInTalkSession(playerid) && !listitem)
				SendClientMessage(playerid, COLOR_LIGHTRED, "Kalbantis telefonu negalite áeiti jo á meniu.");
			else 
			{
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
						ShowPlayerPhoneBookMenu(playerid, GetPlayerPhoneNumber(playerid, PlayerUsedPhone[ playerid ]));
					}
					// Gautos þinutës 
					case 2:
					{
						PlayerSMSListPage[ playerid ] = 0;
						ShowPlayerReceivedMessages(playerid, PlayerUsedPhone[ playerid ]);
					}
					// Siøstos þinutës
					case 3:
					{
						PlayerSMSListPage[ playerid ] = 0;
						ShowPlayerSentMessages(playerid, PlayerUsedPhone[ playerid ]);
					}
				}
			}
			return 1;
		}
		case DIALOG_PLAYER_PHONE_PHONEBOOK:
		{
			if(!response)
				return 1;

			// Dël \n prie "Naujas kontaktas" taip bûti gali.
			if(isnull(inputtext))
				return ShowPlayerPhoneBookMenu(playerid, GetPlayerPhoneNumber(playerid, PlayerUsedPhone[ playerid ]));

			// Naujo kontakto pridëjimas
			if(!listitem)
			{
				if(IsPlayerPhonePhonebookFull(playerid, PlayerUsedPhone[ playerid ]))
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
				ShowPlayerPhoneBookMenu(playerid, GetPlayerPhoneNumber(playerid, PlayerUsedPhone[ playerid ]));
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
					AddPhonebookContact(GetPlayerPhoneNumber(playerid, PlayerUsedPhone[ playerid ]), GetPVarInt(playerid, "Tmp.Phonenumber"), inputtext);
					SendClientMessage(playerid, COLOR_NEWS, "Kontaktas sëkmingai pridëtas.");
				}
			}
			DeletePVar(playerid, "Tmp.Phonenumber");
			return 1;
		}
		case DIALOG_PLAYER_PHONE_OPTIONS:
		{
			if(!response)	
				ShowPlayerPhoneBookMenu(playerid, GetPlayerPhoneNumber(playerid, PlayerUsedPhone[ playerid ]));
			else 
			{
				new bookindex = GetPVarInt(playerid, "UsedContact");
				switch(listitem)
				{
					// Skambinti
					case 0:
					{
		//				PlayerCallNumber(playerid, PlayerPhoneBook[ playerid ][ bookindex ][ PhoneNumber ]);
						new phonenumber = GetPlayerPhonebookContactNumber(playerid, PlayerUsedPhone[ playerid ], bookindex);

						if(IsPlayerInTalkSession(playerid))
								SendClientMessage(playerid, COLOR_LIGHTRED, "Jûs jau kalbate telefonu!");

						else if(IsServicePhoneNumber(phonenumber))
						{
							OnPlayerCallService(playerid, phonenumber);
						}
						else 
						{
							if(!IsValidPhoneNumber(phonenumber) || !IsPhoneNumberOnline(phonenumber))
								SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: su abonentu susisiekti neámanoma, praðome pabandyti vëliau.");

							else if(IsPhoneNumberInCall(phonenumber))
								SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: numeris uþimtas.");

							else 
								OnPlayerCallNumber(playerid, PlayerUsedPhone[ playerid ],phonenumber);
						}	
					}
					// SMS 
					case 1:
					{
						ShowPlayerNewSMSDialog(playerid, PlayerUsedPhone[ playerid ]);
					}
					// Delete
					case 2:
					{
						RemovePhonebookContact(GetPlayerPhoneNumber(playerid, PlayerUsedPhone[ playerid ]), bookindex);
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
					ShowPlayerNewSMSDialog(playerid, PlayerUsedPhone[ playerid ], "Neávedëte teksto.");
				else 
				{
					new phonenumber = GetPlayerPhonebookContactNumber(playerid, PlayerUsedPhone[ playerid ], GetPVarInt(playerid, "UsedContact"));

					if(!IsServicePhoneNumber(phonenumber) && !IsValidPlayerNumber(phonenumber))
						ShowPlayerNewSMSDialog(playerid, PlayerUsedPhone[ playerid ], "Þinutës iðsiøsti nepavyko.");

					else 
						PlayerSendSms(playerid, PlayerUsedPhone[ playerid ], phonenumber, inputtext);
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
					ShowPlayerReceivedMessages(playerid, PlayerUsedPhone[ playerid ], PlayerSMSListPage[ playerid ]);
				}
				else if((PlayerSMSListPage[ playerid ] && listitem == MAX_SMS_PER_PAGE+2) || (!PlayerSMSListPage[ playerid ] && listitem == MAX_SMS_PER_PAGE+1))
				{
					PlayerSMSListPage[ playerid ]++;
					ShowPlayerReceivedMessages(playerid, PlayerUsedPhone[ playerid ], PlayerSMSListPage[ playerid ]);
				}
				else 
				{
					new tmp[ 16 ];
					strmid(tmp, inputtext, 0, strfind(inputtext, "."));
					ShowPlayerSMS(playerid, PlayerUsedPhone[ playerid ], strval(tmp));
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
					ShowPlayerSentMessages(playerid, PlayerUsedPhone[ playerid ], PlayerSMSListPage[ playerid ]);
				}
				else if((PlayerSMSListPage[ playerid ] && listitem == MAX_SMS_PER_PAGE+2) || (!PlayerSMSListPage[ playerid ] && listitem == MAX_SMS_PER_PAGE+1))
				{
					PlayerSMSListPage[ playerid ]++;
					ShowPlayerSentMessages(playerid, PlayerUsedPhone[ playerid ], PlayerSMSListPage[ playerid ]);
				}
				else 
				{
					new tmp[ 16 ];
					strmid(tmp, inputtext, 0, strfind(inputtext, "."));
					ShowPlayerSMS(playerid, PlayerUsedPhone[ playerid ], strval(tmp));
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


hook OnPlayerText(playerid, text[])
{
	new string[180], contactid, contactphoneindex, zone[30], phonenumber = GetPlayerPhoneNumber(playerid, PlayerTalkSession[ playerid ][ PhoneIndex ]);
	GetPlayer2DZone(playerid, zone, sizeof(zone));
	if(IsPlayerInTalkSession(playerid))
	{
		LogPhoneConversation(phonenumber, PlayerTalkSession[ playerid ][ ContactNumber ], text);
		if(IsServicePhoneNumber(PlayerTalkSession[ playerid ][ ContactNumber ]))
		{
			switch(PlayerTalkSession[ playerid ][ ContactNumber ])
	        {
	        	case 911:
	        	{
	        		if(strcmp( "policija", text, true, strlen(text)) == 0 ) // PD kvietimas
		            {
		                SendClientMessage(playerid, COLOR_FADE2, "LOS SANTOS pagalbos linija: tuojaus sujungsime Jus su policijos departamentu.");
		                PlayerTalkSession[ playerid ][ ContactNumber ] = 912;
		                SendClientMessage(playerid, COLOR_WHITE, "LOS SANTOS POLICIJOS DEPARTAMENTAS: Los Santos policija klauso, koks Jûsø praneðimas ir vieta?");
		                return 0;
		            }
		            else if(strcmp("medikais", text, true, strlen(text)) == 0) // MD kvietimas
		            {
		                SendClientMessage(playerid, COLOR_FADE2, "LOS SANTOS pagalbos linija: tuojaus sujungsime Jus su ligonine ar kita medicinos ástaiga.");
		                PlayerTalkSession[ playerid ][ ContactNumber ] = 913;
		                SendClientMessage(playerid, COLOR_WHITE, "LOS SANTOS ligoninë: Los Santos ligoninë klauso, apibûdinkite kas nutiko ir kur nutiko.");
		                return 0;
		            }
		            else if(strcmp( "abu", text, true, strlen( text ) ) == 0 ) // Abiejø kvietimas
		            {
		                SendClientMessage(playerid, COLOR_FADE2, "LOS SANTOS pagalbos linija: tuojaus sujungsime su bendros pagalbos centru.");
		                PlayerTalkSession[ playerid ][ ContactNumber ] = 914;
		                SendClientMessage(playerid, COLOR_WHITE, "LOS SANTOS bendra pagalbos linija: apibûdinkite savo ávyki, bei ávykio vietà.");
		                return 0;
		            }
		            else
		            {
		                SendClientMessage(playerid, COLOR_FADE2, "LOS SANTOS pagalbos linija: Atleiskite, bet að nesuprantu su kuo Jûs reikia sujungti: Policija ar medikais?");
		                return 0;
		            }
	        	}
	        	case 912:
	        	{
	        		SendClientMessage(playerid, COLOR_FADE2, "LOS SANTOS POLICIJOS DEPARTAMENTAS: Jûsø praneðimas uþfiksuotas ir praneðtas pareigûnams.");
		            SendClientMessage(playerid, COLOR_FADE1, "Aèiø Jums, kad praneðëte apie incidentà, pasistengsime Jums padëti.");
		            SendTeamMessage(1, COLOR_LIGHTRED, "|________________Gautas praneðimas apie ávyki________________|");
		            SendTeamMessage(1, COLOR_WHITE, string);
		            format(string, sizeof(string), "| ávykis: %s",text);
		            SendTeamMessage(1, COLOR_WHITE, string);
		            EndPlayerTalkSession(playerid, IsPlayerCallOwner(playerid));
		            return false;
	        	}
	        	case 999:
		        {
		            SendTeamMessage(4, COLOR_LIGHTRED, "|_____________Gautas praneðimas apie ávyki_____________|");
		            format(string, sizeof(string), "| ávyki praneðë | %d", phonenumber);
		            SendTeamMessage(4, COLOR_WHITE, string);
		            format(string, sizeof(string), "| ávykio praneðimas | %s", text);
		            SendTeamMessage(4, COLOR_WHITE, string);
		           	EndPlayerTalkSession(playerid, IsPlayerCallOwner(playerid));
		        }
		       	case 913: // Iðkvietimo parodymas MD
		        {
		            SendClientMessage(playerid, COLOR_FADE2, "LOS SANTOS LIGONINë: Jûsø praneðimas uþfiksuotas ir praneðtas mûsø medikams.");
		            SendClientMessage(playerid, COLOR_FADE1, "Aèiø Jums, kad praneðëte apie insidentà, pasistengsime Jums padëti, medikai jau atvyksta");
		            SendTeamMessage(2, COLOR_LIGHTRED, "|________________Gautas praneðimas apie ávyki________________|");

		            format(string, sizeof(string), "| ávyki praneðë | %d, nustatyti vieta: %s", phonenumber, zone);
		            SendTeamMessage(2, COLOR_WHITE, string);
		            format(string, sizeof(string), "| ávykio praneðimas | %s", text);
		            SendTeamMessage(2, COLOR_WHITE, string);
		            EndPlayerTalkSession(playerid, IsPlayerCallOwner(playerid));
		            return false;
		        }
		       	case 914: // Iðkvietimo parodymas MD/PD
		        {
		            SendClientMessage(playerid, COLOR_FADE2, "LOS SANTOS pagalbos linija Jûsø ávykis buvo praneðtas visiems departamentams.");
		            SendClientMessage(playerid, COLOR_FADE1, "Aèiø Jums, kad praneðëte apie insidentà, pasistengsime Jums padëti.");
		            SendTeamMessage(2, COLOR_LIGHTRED, "|________________Gautas praneðimas apie ávyki________________|");
		            SendTeamMessage(1, COLOR_LIGHTRED, "|________________Gautas praneðimas apie ávyki________________|");
		           
					format(string, sizeof(string), "| ávyki praneðë | %d, nustatyta vieta: %s", phonenumber, zone);
		            SendTeamMessage(2, COLOR_WHITE, string);
		            SendTeamMessage(1, COLOR_WHITE, string);
		            format(string, sizeof(string), "| ávykio praneðimas | %s", text);
		            SendTeamMessage(2, COLOR_WHITE, string);
		            SendTeamMessage(1, COLOR_WHITE, string);
		            EndPlayerTalkSession(playerid, IsPlayerCallOwner(playerid));
		            return false;
		        }
		        case 816: // Mechanikø iðkvietimo parodymas
		        {
		            SendClientMessage(playerid, COLOR_FADE2, "Automatinis atsakiklis: Dëkojame uþ tai, kad naudojatës Los Santos serviso paslaugomis.");
		            SendJobMessage(JOB_MECHANIC, COLOR_LIGHTRED, "|________________Gautas iðkvietimas mechanikui________________|");
		        	format(string, sizeof(string), "| Iðkvietimas gautas nuo | %d", phonenumber);   
		            SendJobMessage(JOB_MECHANIC, COLOR_WHITE, string);
		            format(string, sizeof(string), "| Iðkvietimo vieta|  %s", text);
		            SendJobMessage(JOB_MECHANIC, COLOR_WHITE, string);
		            EndPlayerTalkSession(playerid, IsPlayerCallOwner(playerid));
		            return false;
		        }
		        case 817: // Taksi iðkvietimo parodymas
		        {
		            foreach(Player, mech)
		            {
		                if(GetVehicleModel(GetPlayerVehicleID(mech)) == 420 || GetVehicleModel(GetPlayerVehicleID(mech)) == 438)
		                {
		                    SendClientMessage(mech, COLOR_YELLOW, "|________________Gautas TAXI iðkvietimas________________|");
		                    
		                    format(string, sizeof(string), "| TAXI iðkvietëjas | %d, nurodyta iðkvietimo vieta: %s", phonenumber, zone);
		                    SendClientMessage(mech, COLOR_WHITE, string);
		                    format(string, sizeof(string), "| Iðkvietimo vieta | %s", text);
		                    SendClientMessage(mech, COLOR_WHITE, string);
		                }
		            }
		            SendClientMessage(playerid, COLOR_FADE2, "Automatinis atsakiklis: Jûs sëkmingai iðsikvietët TAXI, dëkojame, kad naudojatës møsø paslaugomis.");
		            EndPlayerTalkSession(playerid, IsPlayerCallOwner(playerid));
		            return false;
		        }
	        }
		}
		else 
		{
			contactid = FindPlayerByPhoneNumber(PlayerTalkSession[ playerid ][ ContactNumber ]);
			contactphoneindex = GetPlayerPhoneIndex(contactid, PlayerTalkSession[ playerid ][ ContactNumber ]);

			if(IsPayphoneNumber(PlayerTalkSession[ playerid ][ ContactNumber ]))
			{
				format(string, sizeof(string), "%s sako (taksofonu): %s", GetPlayerNameEx(playerid), text);
				ProxDetector(10.0, playerid, string,COLOR_FADE1,COLOR_FADE2,COLOR_FADE3,COLOR_FADE4,COLOR_FADE5);

				format(string, sizeof(string), "Anonimas sako (taksofonu): %s", text);
	            SendClientMessage(contactid, COLOR_LIGHTRED2, string);
			}
			else 
			{
				format(string, sizeof(string), "%s sako (telefonu): %s", GetPlayerNameEx(playerid), text);
				ProxDetector(10.0, playerid, string,COLOR_FADE1,COLOR_FADE2,COLOR_FADE3,COLOR_FADE4,COLOR_FADE5);



				if(IsNumberInPlayerPhonebook(contactid, contactphoneindex, PlayerTalkSession[ contactid ][ ContactNumber ]))
				{
					string = GetPlayerPhonebookName(contactid, contactphoneindex, PlayerTalkSession[ contactid ][ ContactNumber ]);
					format(string, sizeof(string), "%s sako (telefonu): %s", string, text);
					SendClientMessage(contactid, COLOR_LIGHTRED2, string);
				}
				else 
				{
					format(string, sizeof(string), "%d sako (telefonu): %s", PlayerTalkSession[ contactid ][ ContactNumber ], text);
					SendClientMessage(contactid, COLOR_LIGHTRED2, string);
				}
				// Jei pas já ájungtas garsiakalbis
				if(PlayerTalkSession[ contactid ][ Speaker ])
				{
					format(string, sizeof(string), "%d sako (telefonu): %s", PlayerTalkSession[ contactid ][ ContactNumber ], text);
					ProxDetector2(10.0, contactid, string,COLOR_FADE1,COLOR_FADE2,COLOR_FADE3,COLOR_FADE4,COLOR_FADE5);
				}
			}
			SetPlayerChatBubble(playerid, text, COLOR_FADE1, 20.0, 10000);
		}
	}
	return 1;
}


public OnPlayerCallNumber(playerid, callerphoneindex, phonenumber)
{
	new string[ 128 ], E_PRIVATE_PHONE_LOCATIONS:phonelocation = GetPhoneNumberLocation(phonenumber);

    SetPVarInt(playerid, "CallOwner", true);
    cmd_ame(playerid, "iðsitraukia telefonà ið kiðenës ir surenka numerá.");
    SendClientMessage(playerid, COLOR_WHITE, "TELEFONAS: kvieèiama...");
    defer PlayerCallNumb(playerid, 1, phonenumber, phonelocation);

    PlayerTalkSession[ playerid ][ CallTimestamp ] = gettime();
    PlayerTalkSession[ playerid ][ CallOwner ] = true;
    PlayerTalkSession[ playerid ][ ContactNumber ] = phonenumber;
    PlayerTalkSession[ playerid ][ PhoneIndex ] = callerphoneindex;

    if(phonelocation == PlayerInventory)
    {
    	new targetplayer = FindPlayerByPhoneNumber(phonenumber),
    		targetphoneindex = GetPlayerPhoneIndex(targetplayer, phonenumber);

    	// Jei skaminantis þaidëjas yra adresato telefonø knygoje
	    if(IsNumberInPlayerPhonebook(targetplayer, targetphoneindex, GetPlayerPhoneNumber(playerid, callerphoneindex))) 
	    	format(string, sizeof(string), "Jûsø telefonas skamba (/p)ickup, skambina: %s", 
	    		GetPlayerPhonebookName(targetplayer, targetphoneindex, GetPlayerPhoneNumber(playerid, callerphoneindex)));
	    else 
	    	format(string, sizeof(string), "Jûsø telefonas skamba (/p)ickup, skambina: %d", GetPlayerPhoneNumber(playerid, callerphoneindex));
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
    }
  	return 1;
}

public OnPlayerCallService(playerid, phonenumber)
{
	PlayerTalkSession[ playerid ][ CallOwner ] = true;
	PlayerTalkSession[ playerid ][ CallTimestamp ] = gettime();
	PlayerTalkSession[ playerid ][ ContactNumber ] = phonenumber;
	PlayerTalkSession[ playerid ][ PhoneIndex ] = GetPlayerPhoneIndex(playerid, phonenumber);

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


public OnPlayerTalkSessionEnd(callownerid, contactid, callerphonenumber, contactnumber, talklength, bool:endedbyowner)
{
	if(endedbyowner)
		SendClientMessage(contactid, COLOR_LIGHTRED, "Jis/Ji padëjo telefono ragelá.");
	else 
		SendClientMessage(callownerid, COLOR_LIGHTRED, "Jis/Ji padëjo telefono ragelá.");

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

stock ShowPlayerPhoneBookMenu(playerid, number)
{
	new query[ 90 ];
	mysql_format(DbHandle, query, sizeof(query), "SELECT phone_number, name FROM phone_contacts WHERE number = %d", number);
	MySQL_FreezePlayer(playerid);
	PlayerUsedPhone[ playerid ] = GetPlayerPhoneIndex(playerid, number);

	inline ContactListLoad()
	{
		new string[ 1024 ], name[  MAX_PHONEBOOK_CONTACT_NAME ], contactnumber;
		string = "Pridëti naujà kontaktà\n{FFFFFF}";
		for(new i = 0; i < cache_get_row_count(); i++)
		{
			cache_get_field_content(i, "name", name);
			contactnumber = cache_get_field_content_int(i, "phone_number");
			format(string, sizeof(string), "%s%s\t%d\n",
				string, name, contactnumber);
		}
		MySQL_UnfreezePlayer(playerid);
		ShowPlayerDialog(playerid, DIALOG_PLAYER_PHONE_PHONEBOOK, DIALOG_STYLE_LIST, "Telefonø knyga", string, "Pasirinkti", "Atgal");
		return 1;
	}
	mysql_pquery_inline(DbHandle, query, using inline ContactListLoad, "");
	return 1;
}

ShowPlayerPhoneList(playerid)
{
	new query[ 140 ];
	for(new i = 0; i < MAX_PLAYER_PHONES; i++)
		if(PlayerPhones[ playerid ][ i ][ Number ])
			format(query, sizeof(query), "%d,", PlayerPhones[ playerid ][ i ][ Number ]);

	query[ strlen(query)-1 ] = ' '; 

	mysql_format(DbHandle, query, sizeof(query), "SELECT recipient_number FROM phone_sms WHERE recipient_number = IN (%s) AND read = 0 ORDER BY recipient_number", query);

	MySQL_FreezePlayer(playerid);

	inline PhoneList()
	{
		new oldnumber, count, i = 0, number, string[256];
		while(i < cache_get_row_count())
		{
			number = cache_get_field_content_int(i, "recipient_number");
			while(number == oldnumber || !oldnumber)
			{
				oldnumber = number;
				count++;
				i++;
			}
			format(string, sizeof(string), "%s%d\t(%d)", string, number, count);
			count = 0;
			i++;
		}
		MySQL_UnfreezePlayer(playerid);
		ShowPlayerDialog(playerid, DIALOG_PLAYER_PHONE_LIST, DIALOG_STYLE_TABLIST, "Turimi telefonai", string, "Pasirinkti", "Iðeiti");
		return 1;
	}
	return mysql_pquery_inline(DbHandle, query, using inline PhoneList, "");
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

stock ShowPlayerNewSMSDialog(playerid, phoneindex, errorstr[] = "")
{
	new string[ 100 ];
	PlayerUsedPhone[ playerid ] = phoneindex;
	format(string, sizeof(string), "{AA1111}%s\n{FFFFFF}Áveskite þinutës tekstà", errorstr);
	ShowPlayerDialog(playerid, DIALOG_PLAYER_PHONE_NEW_SMS, DIALOG_STYLE_INPUT, "Nauja SMS þinutë", string, "Siøsti", "Atgal");
	return 1;
}


IsPlayerPhonePhonebookFull(playerid, phoneindex)
{
	new query[ 70 ], Cache:result, bool:full = false;
	mysql_format(DbHandle, query, sizeof(query), "SELECT number FROM phone_contacts WHERE number = %d", GetPlayerPhoneNumber(playerid, phoneindex));
	result = mysql_query(DbHandle, query);
	if(cache_get_row_count() >= MAX_PHONEBOOK_ENTRIES)
		full = true;
	cache_delete(result);
	return full;
}

GetCallerPhoneNumber(calleeid)
{
	foreach(new i : Player)
		if(IsPlayerInTalkSession(i))
			for(new j = 0; j < MAX_PLAYER_PHONES; j++)
				if(PlayerTalkSession[ i ][ ContactNumber ] == PlayerPhones[ calleeid ][ j ][ Number ])
					return PlayerPhones[ calleeid ][ j ][ Number ];
	return 0;
}

IsPlayerCalled(playerid)
{
	foreach(new i : Player)
		if(IsPlayerInTalkSession(i))
			for(new j = 0; j < MAX_PLAYER_PHONES; j++)
				if(PlayerTalkSession[ i ][ ContactNumber ] == PlayerPhones[ playerid ][ j ][ Number ])
					return true;
	return false;
}



bool:IsPlayerCallOwner(playerid)
{
	return PlayerTalkSession[ playerid ][ CallOwner ];
}

IsContactInPlayerPhonebook(playerid, phoneindex, contactname[])
{
	new query[ 120 ], Cache:result, bool:exists = false;
	mysql_format(DbHandle, query, sizeof(query), "SELECT number FROM phone_contacts WHERE number = %d AND name = '%e'", GetPlayerPhoneNumber(playerid, phoneindex), contactname);
	result = mysql_query(DbHandle, query);
	if(cache_get_row_count())
		exists = true;
	cache_delete(result);
	return exists;
}

GetPlayerPhonebookName(playerid, phoneindex, contactnumber)
{
	new query[ 90 ], Cache:result, name[ MAX_PHONEBOOK_CONTACT_NAME ];
	mysql_format(DbHandle, query, sizeof(query), "SELECT name FROM phone_contacts WHERE number = %d AND contact_number = %d", GetPlayerPhoneNumber(playerid, phoneindex), contactnumber);
	result = mysql_query(DbHandle, query);
	if(cache_get_row_count())
		cache_get_field_content(0, "name", name);
	cache_delete(result);
	return name;
}



IsNumberInPlayerPhonebook(playerid, phoneindex, number)
{
	new query[ 90 ], Cache:result, bool:response = false;
	mysql_format(DbHandle, query, sizeof(query), "SELECT number FROM phone_contacts WHERE number = %d AND contact_number = %d", GetPlayerPhoneNumber(playerid, phoneindex), number);
	result = mysql_query(DbHandle, query);
	if(cache_get_row_count())
		response = true;
	cache_delete(result);
	return response;
}

IsPlayerInTalkSession(playerid)
{
	if(PlayerTalkSession[ playerid ][ CallTimestamp ])
		return true;
	else 
		return false;
}


IsPlayerPhoneOnline(playerid, phoneindex)
{
	return PlayerPhones[ playerid ][ phoneindex ][ Online ];
}

FindPlayerByPhoneNumber(phonenumber)
{
    foreach(new i : Player)
        for(new j = 0; j < MAX_PLAYER_PHONES; j++)
            if(PlayerPhones[ i ][ j ][ Number ] == phonenumber)
            	return i;
    return INVALID_PLAYER_ID;
}


GetPlayerPhoneIndex(playerid, phonenumber)
{
	for(new i = 0; i < MAX_PLAYER_PHONES; i++)
		if(PlayerPhones[ playerid ][ i ][ Number ] == phonenumber)
			return i;
	return -1;
}


GetPlayerPhoneNumber(playerid, phoneindex)
	return PlayerPhones[ playerid ][ phoneindex ][ Number ];

IsPlayerPhoneOff(playerid, phoneindex)
{
	return !PlayerPhones[ playerid ][ phoneindex ][ Online ];
}

SetPlayerPhoneOnlineStatus(playerid, phoneindex, bool:set)
{
	new query[70];
	mysql_format(DbHandle, query, sizeof(query), "UPDATE phones SET online = %d WHERE number = %d", set, PlayerPhones[ playerid ][ phoneindex ][ Number ]);
	PlayerPhones[ playerid ][ phoneindex ][ Online ] = set;
	return mysql_pquery(DbHandle, query);
}

GetPlayerContactNumberByName(playerid, contactname[])
{
	new query[140], number, Cache:result;
	for(new i = 0; i < MAX_PLAYER_PHONES; i++)
		if(PlayerPhones[ playerid ][ i ][ Number ])
			format(query, sizeof(query), "%s%d,", query, PlayerPhones[ playerid ][ i ][ Number ]);
	query[ strlen(query) -1 ] = ' ';

	mysql_format(DbHandle, query, sizeof(query), "SELECT contact_number FROM phone_contacts WHERE name = '%e' AND number IN (%s)", contactname, query);
	result = mysql_query(DbHandle, query);
	if(cache_get_row_count())	
		number = cache_get_field_content_int(0, "contact_number");
	cache_delete(result);

	return number;
}

IsPhoneNumberInCall(phonenumber)
{
	foreach(new i : Player)
		if(PlayerTalkSession[ i ][ ContactNumber ] == phonenumber)
			return true;
	return false;	
}

IsPlayerPhonenumber(playerid, phonenumber)
{
	for(new j = 0; j < MAX_PLAYER_PHONES; j++)
		if(PlayerPhones[ playerid ][ j ][ Number ] == phonenumber)
			return true;
	return false;	
}

IsPlayerPhonenumberOnline(phonenumber)
{
	foreach(new i : Player)
		for(new j = 0; j < MAX_PLAYER_PHONES; j++)
			if(PlayerPhones[ i ][ j ][ Number ] == phonenumber)
				return PlayerPhones[ i ][ j ][ Online ];
	return false;	
}

GetPlayerPhonebookContactNumber(playerid, phoneindex, contactindex)
{
	new query[ 110 ], Cache:result, number;
	mysql_format(DbHandle, query, sizeof(query), "SELECT contact_number FROM phone_contacts WHERE number = %d LIMIT %d, %d", GetPlayerPhoneNumber(playerid, phoneindex), contactindex+1, contactindex+2);
	result = mysql_query(DbHandle, query);
	if(cache_get_row_count())
		number = cache_get_field_content_int(0, "contact_number");
	cache_delete(result);
	return number;
}

EndPlayerTalkSession(playerid, bool:endedbyowner)
{
	new callownerid = (PlayerTalkSession[ playerid ][ CallOwner ]) ? (playerid) : (FindPlayerByPhoneNumber(PlayerTalkSession[ playerid ][ ContactNumber ])),
		contactid = (callownerid == playerid) ? (FindPlayerByPhoneNumber(PlayerTalkSession[ playerid ][ ContactNumber ])) : (playerid),
		callownernumber = PlayerTalkSession[ callownerid ][ ContactNumber ],
		contactnumber = PlayerTalkSession[ callownerid ][ ContactNumber ],
		talklength = gettime() - PlayerTalkSession[ playerid ][ CallTimestamp ];

	// Clear their variables and stop the timer
	stop PlayerTalkSession[ callownerid ][ TalkTimer ];
	static emptyPlayerSession[ E_PLAYER_TALK_SESSION ];
	PlayerTalkSession[ callownerid ] = emptyPlayerSession;
	PlayerTalkSession[ contactid ] = emptyPlayerSession;

	// Naudojantis taksofonu þaidëjas uþðaldomas, taigi reikia leisti vël judëti.
	if(IsPayphoneNumber(contactnumber))
		TogglePlayerControllable(callownerid, true);

	SetPlayerSpecialAction(callownerid, SPECIAL_ACTION_STOPUSECELLPHONE);
    RemovePlayerAttachedObject(callownerid, 3);
    SetPlayerSpecialAction(contactid, SPECIAL_ACTION_STOPUSECELLPHONE);
    RemovePlayerAttachedObject(contactid, 3);

	return CallLocalFunction("OnPlayerTalkSessionEnd", "iiiiii", callownerid, contactid, callownernumber, contactnumber, talklength, endedbyowner);
}

stock ShowPlayerReceivedMessages(playerid, phoneindex, page = 0)
{
	new query[ 110 ];
	mysql_format(DbHandle, query, sizeof(query), "SELECT * FROM phone_sms WHERE recipient_number = %d ORDER BY `date` ASC LIMIT %d, %d",
		GetPlayerPhoneNumber(playerid, phoneindex), 
		page * MAX_SMS_PER_PAGE, 
		((page+1) * MAX_SMS_PER_PAGE)+1); // Imam viena daugiau, kad þinutume ar bus kitas puslapis.
	return mysql_pquery(DbHandle, query, "OnPlayerReceivedMessageLoad", "iii", playerid, phoneindex, page);
}

stock ShowPlayerSentMessages(playerid, phoneindex, page = 0)
{
	new query[ 110 ];
	mysql_format(DbHandle, query, sizeof(query), "SELECT * FROM phone_sms WHERE sender_number = %d ORDER BY `date` ASC LIMIT %d, %d", 
		GetPlayerPhoneNumber(playerid, phoneindex), 
		page * MAX_SMS_PER_PAGE, 
		((page+1) * MAX_SMS_PER_PAGE)+1); // Imam viena daugiau, kad þinutume ar bus kitas puslapis.
	return mysql_pquery(DbHandle, query, "OnPlayerSentMessageLoad", "iii", playerid, phoneindex, page);
}


stock PlayerSendSms(playerid, phoneindex, phonenumber, text[])
{
	new string[ 256 ], targetplayer, 
		sendernumber = GetPlayerPhoneNumber(playerid, phoneindex),
		E_PRIVATE_PHONE_LOCATIONS:phonelocation = GetPhoneNumberLocation(phonenumber),
		targetphoneindex;

	mysql_format(DbHandle, string, sizeof(string), "INSERT INTO phone_sms (sender_number, recipient_number, `text`) VALUES (%d, %d, '%e')",
		sendernumber,
		phonenumber, 
		text);
	mysql_pquery(DbHandle, string);

	format(string, sizeof(string), "* %s iðsitraukia mobilujá telefonà, paraðæs SMS þinutæ, iðsiunèia jà.", GetPlayerNameEx(playerid));
    ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);

    if(phonelocation == PlayerInventory)
    {
    	targetplayer = FindPlayerByPhoneNumber(phonenumber);
    	targetphoneindex = GetPlayerPhoneIndex(targetplayer, phonenumber);
	   	if(targetplayer != INVALID_PLAYER_ID)
	   	{
	   		if(IsNumberInPlayerPhonebook(targetplayer, targetphoneindex, sendernumber))
	   			format(string, sizeof(string), "SMS: %s, siuntëjas: %s", 
	   				text, GetPlayerPhonebookName(targetplayer, targetphoneindex, sendernumber));
	   		else 
	   			format(string, sizeof(string), "SMS: %s, siuntëjas: %d", text, sendernumber);

		    SendClientMessage(playerid, COLOR_LIGHTRED2, "Trumpoji þinutë buvo sëkmingai nusiûsta adresatui.");
		    SendChatMessage(targetplayer, COLOR_LIGHTRED2, string);
		    format(string, sizeof(string), "SMS: %s", text);
		    SendChatMessage(playerid, COLOR_WHITE, string);
			PlayerPlaySound(targetplayer, 1052, 0.0, 0.0, 0.0);
		}
    }
    else if(phonelocation == HouseInventory)
    {
    	new houseindex = GetHousePhonenumberHouseIndex(phonenumber);
    	
    	foreach(new i : Player)
    		if(IsPlayerInHouse(i, houseindex))
    			SendClientMessage(i, COLOR_WHITE, "* Kaþkur namuose suskamba telefonas");
    }
    else if(phonelocation == GarageInventory)
    {
    	new garageindex = GetGaragePhonenumberGarageIndex(phonenumber);

    	foreach(new i : Player)
    		if(IsPlayerInGarage(i, garageindex))
    			SendClientMessage(i, COLOR_WHITE, "* Kaþkur garaþe suskamba telefonas");
    }
    else if(phonelocation == VehicleTrunk)
    {
    	new vehicleid = GetVehiclePhonenumberVehicleId(phonenumber);

    	foreach(new i : Player)
    		if(IsPlayerInVehicle(i, vehicleid))
    			SendClientMessage(i, COLOR_WHITE, "* Kaþkur transporto priemonëje suskamba telefonas");
    }
    ShowInfoText(playerid, "~w~ SMS kaina $1", 5000);
    GivePlayerMoney(playerid,-1);
    return 1;
}


GetPlayerPhoneCount(playerid)
{
	new count = 0;
	for(new i = 0; i < MAX_PLAYER_PHONES; i++)
		if(PlayerPhones[ playerid ][ i ][ Number ])
			count++;
	return count;
}

GetFirstPlayerPhoneIndex(playerid)
{
	for(new i = 0; i < MAX_PLAYER_PHONES; i++)
		if(PlayerPhones[ playerid ][ i ][ Number ])
			return i;
	return -1;
}


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

stock ShowPlayerSMS(playerid, phoneindex, smssqlid)
{
	new string[ 1024 ], date[ 32 ], sender[ MAX_PHONEBOOK_CONTACT_NAME ], reciever[ MAX_PHONEBOOK_CONTACT_NAME ], 
		Cache:result, number;

	mysql_format(DbHandle, string, sizeof(string), "SELECT * FROM phone_sms WHERE id = %d", smssqlid);
	result = mysql_query(DbHandle, string);
	if(cache_get_row_count())
	{
		number = cache_get_field_content_int(0, "sender_number");
		if(number == GetPlayerPhoneNumber(playerid, phoneindex))
		{
			GetPlayerName(playerid, sender, MAX_PLAYER_NAME);
		}
		else if(IsNumberInPlayerPhonebook(playerid, phoneindex, number))
			strcat(sender, GetPlayerPhonebookName(playerid, phoneindex, number));
		else 
			format(sender, sizeof(sender), "%d", number);

		number = cache_get_field_content_int(0, "recipient_number");
		if(number == GetPlayerPhoneNumber(playerid, phoneindex))
		{
			GetPlayerName(playerid, reciever, MAX_PLAYER_NAME);
			if(!cache_get_field_content_int(0, "read"))
			{
				mysql_format(DbHandle, string, sizeof(string), "UPDATE phone_sms SET `read` = 1 WHERE id = %d", smssqlid);
				mysql_pquery(DbHandle, string);
			}
		}
		else if(IsNumberInPlayerPhonebook(playerid, phoneindex, number))
			strcat(reciever, GetPlayerPhonebookName(playerid, phoneindex, number));
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







/* 	                                                           
	                                                           
	`7MMM.     ,MMF'           .M"""bgd   .g8""8q. `7MMF'      
	  MMMb    dPMM            ,MI    "Y .dP'    `YM. MM        
	  M YM   ,M MM `7M'   `MF'`MMb.     dM'      `MM MM        
	  M  Mb  M' MM   VA   ,V    `YMMNq. MM        MM MM        
	  M  YM.P'  MM    VA ,V   .     `MM MM.      ,MP MM      , 
	  M  `YM'   MM     VVV    Mb     dM `Mb.    ,dP' MM     ,M 
	.JML. `'  .JMML.   ,V     P"Ybmmd"    `"bmmd"' .JMMmmmmMMM 
	                  ,V                      MMb              
	               OOb"                        `bood'           */


public OnPlayerReceivedMessageLoad(playerid, phoneindex, page)
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
			if(IsNumberInPlayerPhonebook(playerid, phoneindex, number))
			{
				strcat(sender, GetPlayerPhonebookName(playerid, phoneindex, number));
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

public OnPlayerSentMessageLoad(playerid, phoneindex, page)
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
			if(IsNumberInPlayerPhonebook(playerid, phoneindex, number))
			{
				strcat(sender, GetPlayerPhonebookName(playerid, phoneindex, number));
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
			                                                                                                                 
			             ,,                                                AW                                                
			MMP""MM""YMM db                                               ,M'     MMP""MM""YMM             `7MM              
			P'   MM   `7                                                  MV      P'   MM   `7               MM              
			     MM    `7MM  `7MMpMMMb.pMMMb.  .gP"Ya `7Mb,od8 ,pP"Ybd   AW            MM   ,6"Yb.  ,pP"Ybd  MM  ,MP',pP"Ybd 
			     MM      MM    MM    MM    MM ,M'   Yb  MM' "' 8I   `"  ,M'            MM  8)   MM  8I   `"  MM ;Y   8I   `" 
			     MM      MM    MM    MM    MM 8M""""""  MM     `YMMMa.  MV             MM   ,pm9MM  `YMMMa.  MM;Mm   `YMMMa. 
			     MM      MM    MM    MM    MM YM.    ,  MM     L.   I8 AW              MM  8M   MM  L.   I8  MM `Mb. L.   I8 
			   .JMML.  .JMML..JMML  JMML  JMML.`Mbmmd'.JMML.   M9mmmP',M'            .JMML.`Moo9^Yo.M9mmmP'.JMML. YA.M9mmmP' 
			                                                          MV                                                     
			                                                         AW                                                      
*/



timer PlayerCallNumb[2000](pid, att, ph, E_PRIVATE_PHONE_LOCATIONS:plo)
{
	// Kalba telefonu, reiðkia atsakë..
	if(IsPlayerInTalkSession(pid))
		return 1;
	
	SendClientMessage(pid, COLOR_WHITE, "TELEFONAS: kvieèiama...");

	switch(plo)
	{
		case PlayerInventory:
		{
			new string[ 60 ], Float:x, Float:y, Float:z;
			GetPlayerPos(pid, x, y, z);
			format(string, sizeof(string), "* Telefonas skamba (( %s ))",GetPlayerNameEx(pid));
			ProxDetector(20.0, pid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
            PlayerPlaySound(pid, 20600, x, y, z);
		}
		case HouseInventory:
	    {
	    	new houseindex = GetHousePhonenumberHouseIndex(ph);
	    	
	    	foreach(new i : Player)
	    		if(IsPlayerInHouse(i, houseindex))
	    			SendClientMessage(i, COLOR_WHITE, "* Kaþkur namuose girdisi telefono skambutis");
	    }
	    case GarageInventory:
	    {
	    	new garageindex = GetGaragePhonenumberGarageIndex(ph);

	    	foreach(new i : Player)
	    		if(IsPlayerInGarage(i, garageindex))
	    			SendClientMessage(i, COLOR_WHITE, "* Kaþkur garaþe girdisi telefono skambutis");
	    }
	    case VehicleTrunk:
	    {
	    	new vehicleid = GetVehiclePhonenumberVehicleId(ph);

	    	foreach(new i : Player)
	    		if(IsPlayerInVehicle(i, vehicleid))
	    			SendClientMessage(i, COLOR_WHITE, "* Kaþkur transporto priemonëje girdisi telefono skambutis");
	    }

	}
	
	att++;

	// Po keturiu bandymø, t.y. 12sec iðvalom kintamuosius.
	// Jei þaidëjas atsijungë, irgi baigiam viskà.
	if(att == 6 || !IsPlayerConnected(pid))
	{
		EndPlayerTalkSession(pid, IsPlayerCallOwner(pid));
		return 1;
	}

	defer PlayerCallNumb(pid, att, ph, plo);
	
	return 1;
}



timer PhoneTalkTimer[1000](callerid, contactid)
{
	new timetalking = gettime() - PlayerTalkSession[ callerid ][ AnswerTimestamp ],
		price = GetPhoneTalkPrice(timetalking);

	if(price + 10 * PHONE_PRICE_PER_SECOND >= GetPlayerBankMoney(callerid))
	{
		SendClientMessage(callerid, COLOR_WHITE, "Operatorë: jûsø sàskaita tuðèia. Galite tæsti ðá pokalbá neilgiau kaip 10 sekundþiø.");
		SendClientMessage(contactid, COLOR_WHITE, "Operatorë: jûsø sàskaita tuðèia. Galite tæsti ðá pokalbá neilgiau kaip 10 sekundþiø.");
	}
	if(price >= GetPlayerBankMoney(callerid))
	{
		EndPlayerTalkSession(callerid, true);
		SendClientMessage(contactid, COLOR_WHITE, "Jûsø paðnekovui baigësi sàskaita.");
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
    new number, 
    name[ MAX_PHONEBOOK_CONTACT_NAME ], 
    phonecount = GetPlayerPhoneCount(playerid),
    phoneindex;

    if(!phonecount)
    	SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neturite nei vieno telefono.");

    else if(phonecount == 1 && sscanf(params, "is[ " #MAX_PHONEBOOK_CONTACT_NAME "]", number, name))
    	SendClientMessage(playerid, COLOR_LIGHTRED, "Komandos naudojimas: /addcontact [NUMERIS] [VARDAS]");

    else if(phonecount > 1 && sscanf(params,"iis[" #MAX_PHONEBOOK_CONTACT_NAME "]", phoneindex, number, name))
        SendClientMessage(playerid, COLOR_LIGHTRED, "Komandos naudojimas: /addcontact [Telefono eil. nr. ] [NUMERIS] [VARDAS]");

    else 
    {
    	// Jei turi tik 1 telefonà, reikia surasti kuriam indekse jis laikomas.
    	if(phonecount == 1)
    	{
    		for(new i = 0; i < MAX_PLAYER_PHONES; i++)
    			if(PlayerPhones[ playerid ][ i ][ Number ])
    			{
    				phoneindex = i;
    				break;
    			}
    	}

    	if(!PlayerPhones[ playerid ][ phoneindex ][ Online ])
    		SendClientMessage(playerid, COLOR_LIGHTRED, "Ðis jûsø telefonas yra iðjungtas, todël pridëti kontaktø negalite.");

	    else 
	    {
	    	new tmp[MAX_PHONEBOOK_CONTACT_NAME ];
	    	tmp = GetPlayerPhonebookName(playerid, phoneindex, number);
	    	if(!isnull(tmp))
	        	SendClientMessage(playerid, COLOR_LIGHTRED, "Ðis numeris jau buvo jûsø telefono atmintyje, jis buvo atnaujintas.");
	        else 
	        	SendClientMessage(playerid, COLOR_NEWS, "Kontaktas buvo sëkmingas pridëtas á Jûsø adresø knygutæ.");

	    	AddPhonebookContact(GetPlayerPhoneNumber(playerid, phoneindex), number, name);
	    }
    }
    return 1;
}

CMD:deletecontact(playerid, params[])
{
    new id,
    	phonecount = GetPlayerPhoneCount(playerid),
    	phoneindex;

    if(!phonecount)
    	SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neturite nei vieno telefono.");

    else if(phonecount == 1 && sscanf(params,"i", id))
        SendClientMessage(playerid, COLOR_LIGHTRED, "Komandos naudojimas: /deletecontact [Eiles Numeris]");
    
    else if(phonecount > 1 && sscanf(params, "ii", phoneindex, id))
    	SendClientMessage(playerid, COLOR_LIGHTRED, "Komandos naudojimas: /deletecontact [Telefono eil. nr. ] [Kontakto eiles numeris]");

    else if(id < 1 || id >= MAX_PHONEBOOK_ENTRIES)
        SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio, kontaktø knygoje galimi numeriai yra nuo 1 iki " #MAX_PHONEBOOK_ENTRIES);
    
    else 
    {
    	// Jei turi tik 1 telefonà, reikia surasti kuriam indekse jis laikomas.
		if(phonecount == 1)
		{
			for(new i = 0; i < MAX_PLAYER_PHONES; i++)
				if(PlayerPhones[ playerid ][ i ][ Number ])
				{
					phoneindex = i;
					break;
				}
		}

		if(!PlayerPhones[ playerid ][ phoneindex ][ Online ])
	    	SendClientMessage(playerid, COLOR_LIGHTRED, "Ðis jûsø telefonas yra iðjungtas, todël pridëti kontaktø negalite.");
	    else 
	    {
	    	RemovePhonebookContact(GetPlayerPhoneNumber(playerid, phoneindex), GetPlayerPhonebookContactNumber(playerid, phoneindex, id));
	    	SendClientMessage(playerid, COLOR_NEWS, "Kontaktas buvo sëkmingas paðalintas ið adresø knygutës.");
	    }
    }
    return 1;
}

CMD:phonebook(playerid, params[])
{
    new query[180], phonecount = GetPlayerPhoneCount(playerid), phoneindex;

    if(!phonecount)
    	SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neturite nei vieno telefono.");
    
	else if(phonecount == 1)
	{
		phoneindex = GetFirstPlayerPhoneIndex(playerid);
	}

	else if(phonecount > 1 && sscanf(params, "i", phoneindex))
		SendClientMessage(playerid, COLOR_LIGHTRED, "Komandoas naudojimas: /phonebook [Telefono eilës numeris]");

   	else 
   	{
   		mysql_format(DbHandle, query, sizeof(query), "SELECT contact_number,name,FROM_UNIXTIME(entry_date) AS date FROM phone_contacts WHERE number = %d ORDER BY entry_date ASC", PlayerPhones[ playerid ][ phoneindex ][ Number ]);
	    
   		inline ContactListLoad()
   		{
   			new name[MAX_PLAYER_NAME], date[32], number, count = 1;
   			if(cache_get_row_count())
   				SendClientMessage(playerid, COLOR_GREEN, "|_____________KONTAKTØ SÀRAÐAS_____________|");

		    for(new i = 0; i < cache_get_row_count(); i++)
		    {
		        number = cache_get_field_content_int(i, "contact_number");
		        cache_get_field_content(i, "name", name);
		        cache_get_field_content(i, "date", date);
		        format(query, sizeof(query), "%d. Vardas: %s Numeris: %d Pridëtas: %s", count++, name, number, date);
		        SendClientMessage(playerid, COLOR_WHITE, query);

		    }
		    return 1;
   		}
	    mysql_pquery_inline(DbHandle, query, using inline ContactListLoad, "");
   	}
    return 1;
}




CMD:call(playerid, params[ ])
{
	new phonecount = GetPlayerPhoneCount(playerid),
		phoneindex = -1,
		contactnumber, 
		string[ MAX_PHONEBOOK_CONTACT_NAME ];

	if(Mires[playerid] > 0) 
		SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûsø veikëjas ðiuo metu yra kritinëje arba komos bûsenoje.");

    else if(Mute[playerid] == true)
    	SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðiuo metu Jums yra uþdrausta kalbëtis (/mute), norëdami paðalinti draudimà susisiekite su Administratoriumi.");

    else if(IsPlayerInTalkSession(playerid))
    	SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs jau ðnekate telefonu. Naudokite /h baigti pokalbiui");

    else if(pInfo[ playerid ][ pJail ] > 0)
        SendClientMessage(playerid, COLOR_GREY, "   Telefono ryðys blokuojamas.");

    else if(!phonecount)
		SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Tu neturi mobilaus telefono.");

	else if(isnull(params))
	{
		call_help:
		if(phonecount == 1)
			SendClientMessage(playerid, GRAD,        "KOMANDOS NAUDOJIMAS: /call [telefono numeris/kontakto vardas]");
		else 
			SendClientMessage(playerid, GRAD,        "KOMANDOS NAUDOJIMAS: /call [Telefono eilës numeris] [telefono numeris/kontakto vardas]");

        SendClientMessage(playerid, COLOR_GREEN2, "____________Los Santos paslaugos____________");
        SendClientMessage(playerid, COLOR_WHITE, "911 - Pagalbos linija");
        SendClientMessage(playerid, COLOR_WHITE, "816 - Mechanikai, 817 - Taksi, 999 - San News");
        SendClientMessage(playerid, COLOR_GREEN2, "____________________________________________");
	}

	else if(phonecount == 1)
	{
		phoneindex = GetFirstPlayerPhoneIndex(playerid);
		contactnumber = strval(params);
	}
	// Turi daugiau nei 1 telefonà, reiðkia privalo ávesti ir indeksà.
	else 
	{
		if(sscanf(params, "is[" #MAX_PHONEBOOK_CONTACT_NAME "]", phoneindex--, string))
			goto call_help;

		else if(phoneindex < 0 || phoneindex >= MAX_PLAYER_PHONES || !PlayerPhones[ playerid ][ phoneindex ][ Number ])
			SendClientMessage(playerid, COLOR_LIGHTRED, "Jûs neturite telefono su ðiuo eilës numeriu.");

		else if((IsNumeric(string)) ? (!IsValidPhoneNumber(strval(string))) : (!IsContactInPlayerPhonebook(playerid, phoneindex, string)))
			SendClientMessage(playerid, COLOR_LIGHTRED, "Nepavyko susisiekti su abonentu.");

		else 
		{
			contactnumber = (IsNumeric(string)) ? (strval(string)) : (GetPlayerContactNumberByName(playerid, string));
		}
	}

	// Jeigu viskas gerai.
	if(phoneindex != -1 && contactnumber)
	{
		if(!IsPlayerPhoneOnline(playerid, phoneindex))
			SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûsø telefonas iðjungtas.");

		else if(!IsValidPhoneNumber(contactnumber))
			SendClientMessage(playerid, COLOR_LIGHTRED, "Þinutës iðsiøsti nepavyko!");

        else 
        {
        	OnPlayerCallNumber(playerid, phoneindex, contactnumber);
        }
	}
    return 1;
}
CMD:pickup(playerid)
{
    if(!IsPlayerCalled(playerid))
        SendClientMessage(playerid, GRAD, "Jums niekas neskambina.");

    else if(Mires[ playerid ] > 0)
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûsø veikëjas ðiuo metu yra kritinëje arba komos bûsenoje.");

    else if(Mute[ playerid ] == true)
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðiuo metu Jums yra uþdrausta kalbëtis (/mute), norëdami paðalinti draudimà susisiekite su Administratoriumi.");


    else
    {
    	new phonenumber = GetCallerPhoneNumber(playerid),
    	callerid = FindPlayerByPhoneNumber(phonenumber);

	    PlayerTalkSession[ playerid ][ CallOwner ] = false;
	    PlayerTalkSession[ playerid ][ CallTimestamp ] = PlayerTalkSession[ callerid ][ CallTimestamp ];
	    PlayerTalkSession[ playerid ][ ContactNumber ] = phonenumber;
	    PlayerTalkSession[ playerid ][ PhoneIndex ] = GetPlayerPhoneIndex(playerid, PlayerTalkSession[ callerid ][ ContactNumber ]);

	    PlayerTalkSession[ callerid ][ ContactAnswered ] = PlayerTalkSession[ playerid ][ ContactAnswered ] = true;
	    PlayerTalkSession[ callerid ][ AnswerTimestamp ] = PlayerTalkSession[ playerid ][ AnswerTimestamp ] = gettime();

	    PlayerTalkSession[ playerid ][ TalkTimer ] = PlayerTalkSession[ callerid ][ TalkTimer ] = repeat PhoneTalkTimer(callerid, playerid);


	    SendClientMessage(callerid,  GRAD, "Jis pakëlë telefono ragelá.");
	    cmd_ame(playerid, "iðtraukia telefona ið kiðenës ir atsiliepia.");

	    SetPlayerSpecialAction(playerid, SPECIAL_ACTION_USECELLPHONE);

    }
    return 1;   
}
CMD:p(playerid)
    return cmd_pickup(playerid);

CMD:hangup(playerid)
{
    if(Mires[ playerid ] > 0)
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûsø veikëjas ðiuo metu yra kritinëje arba komos bûsenoje.");

    else if(Mute[ playerid ] == true)
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðiuo metu Jums yra uþdrausta kalbëtis (/mute), norëdami paðalinti draudimà susisiekite su Administratoriumi.");

    else if(!IsPlayerInTalkSession(playerid))
    	SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs nekalbate telefonu.");

    else 
    {
    	EndPlayerTalkSession(playerid, IsPlayerCallOwner(playerid));
    }
    return 1;
}
CMD:h(playerid)
    return cmd_hangup(playerid);



CMD:sms(playerid, params[ ])
{
    new phonecount = GetPlayerPhoneCount(playerid),
    	phoneindex = -1,
    	contactnumber, 
    	string[256];

    if(Mires[playerid] > 0) 
    	SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûsø veikëjas ðiuo metu yra kritinëje arba komos bûsenoje.");

    else if(Mute[playerid] == true)
    	SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðiuo metu Jums yra uþdrausta kalbëtis (/mute), norëdami paðalinti draudimà susisiekite su Administratoriumi.");

    else if(PlayerMoney[ playerid ] < 1)
    	SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs neturite $1 ");

    else if(!phonecount)
		SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Tu neturi mobilaus telefono.");

	else if(phonecount == 1)
	{
		if(sscanf(params, "is[128]", contactnumber, string))
			SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas naudojimas: /sms [Telefono numeris] [Þinutës tekstas]");
		else 
			phoneindex = GetFirstPlayerPhoneIndex(playerid);
	}
	// Turi daugiau nei 1 telefonà, reiðkia privalo ávesti ir indeksà.
	else 
	{
		if(sscanf(params, "iis[128]", phoneindex--, contactnumber, string))
			SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas naudojimas: /sms [Telefono numeris] [Þinutës tekstas]");

		else if(phoneindex < 0 || phoneindex >= MAX_PLAYER_PHONES || !PlayerPhones[ playerid ][ phoneindex ][ Number ])
			SendClientMessage(playerid, COLOR_LIGHTRED, "Jûs neturite telefono su ðiuo eilës numeriu.");
	}

	// Jeigu viskas gerai.
	if(phoneindex != -1 && !isnull(string) && contactnumber)
	{
		if(!IsPlayerPhoneOnline(playerid, phoneindex))
			SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûsø telefonas iðjungtas.");

		else if(!IsValidPhoneNumber(contactnumber))
			SendClientMessage(playerid, COLOR_LIGHTRED, "Þinutës iðsiøsti nepavyko!");

		else if(pInfo[playerid][pJail] > 0)
        	SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, èia mobilaus telefono ryðys yra blokuojamas.");

        else 
        {
        	PlayerSendSms(playerid, phoneindex, contactnumber, string);
        }
	}
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








