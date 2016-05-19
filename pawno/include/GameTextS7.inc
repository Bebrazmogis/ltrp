
/*
	Biblioteka paraðyta padidinti teksto limitui naudojant 3 GameText stiliø.
	
	Autorius: Bebras
*/


#if !defined isnull
	#define isnull(%1) \
				((!(%1[0])) || (((%1[0]) == '\1') && (!(%1[1]))))
#endif

static 	PlayerText:GameTextTD[MAX_PLAYERS] = {PlayerText:INVALID_TEXT_DRAW, ...},
		GameTextTimer[ MAX_PLAYERS ] = {-1, ...};


stock GameTextForPlayerEx(playerid, string[], time, style)
{
	if(style != 7)
		return GameTextForPlayer(playerid, string, time, style);

	if(!IsPlayerConnected(playerid) || isnull(string))
		return 0;
	PlayerTextDrawSetString(playerid, GameTextTD[playerid], string);
	PlayerTextDrawShow(playerid, GameTextTD[playerid]);
	if(GameTextTimer[playerid] != -1) 
		KillTimer(GameTextTimer[playerid]);
	GameTextTimer[playerid] = SetTimerEx("GameTextEx_HideTD",time, false, "i", playerid);
	return 1;
}
#if defined _ALS_GameTextForPlayer
	#undef GameTextForPlayer
#else 
	#define _ALS_GameTextForPlayer
#endif
#define GameTextForPlayer GameTextForPlayerEx

forward GameTextEx_HideTD(playerid);
public GameTextEx_HideTD(playerid)
{
	GameTextTimer[playerid] = -1;
	return PlayerTextDrawHide(playerid, GameTextTD[playerid]);
}




public OnPlayerConnect(playerid)
{
	#if defined GameTextEx_OnPlayerConnect
		GameTextEx_OnPlayerConnect(playerid);
	#endif
	GameTextTD[playerid] = CreatePlayerTextDraw(playerid, 320.000, 175.0, "Type 7");
	PlayerTextDrawLetterSize(playerid, GameTextTD[playerid], 0.5, 1.5);
	PlayerTextDrawAlignment(playerid, GameTextTD[playerid], 2);
	PlayerTextDrawSetOutline(playerid, GameTextTD[playerid], 1);
	PlayerTextDrawBackgroundColor(playerid, GameTextTD[playerid], 51);
	PlayerTextDrawFont(playerid, GameTextTD[playerid], 2);
	PlayerTextDrawSetProportional(playerid, GameTextTD[playerid], 1);
	return 1;
}
#if defined _ALS_OnPlayerConnect
	#undef OnPlayerConnect
#else
	#define _ALS_OnPlayerConnect
#endif
#define OnPlayerConnect GameTextEx_OnPlayerConnect
#if defined GameTextEx_OnPlayerConnect
	forward GameTextEx_OnPlayerConnect(playerid);
#endif


public OnPlayerDisconnect(playerid, reason)
{
	#if defined GameTextEx_OnPlayerDisconnect
		GameTextEx_OnPlayerDisconnect(playerid, reason);
	#endif
	PlayerTextDrawDestroy(playerid, GameTextTD[playerid]);
	GameTextTD[playerid] = PlayerText:INVALID_TEXT_DRAW;
	if(GameTextTimer[playerid] != -1) 
		KillTimer(GameTextTimer[playerid]);
	GameTextTimer[playerid] = -1;

	return 1;
}
#if defined _ALS_OnPlayerDisconnect
	#undef OnPlayerDisconnect
#else
	#define _ALS_OnPlayerDisconnect
#endif
#define OnPlayerDisconnect GameTextEx_OnPlayerDisconnect
#if defined GameTextEx_OnPlayerDisconnect
	forward GameTextEx_OnPlayerDisconnect(playerid, reason);
#endif
