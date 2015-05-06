
#include <YSI\y_hooks>

/**

 * Ði biblioteka yra skirta LTRP modifikacijai
 * Kodo autorius: Bebras
 * Apraðymas:
 * 	Ði biblioteka duoda dvi funkcijas: MySQL_FreezePlayer ir MySQL_UnfreezePlayer. Jos skirtos uþðaldyti þaidëjà trumpam. Pavyzdþiui siunèiant threaded uþklausà
 * 	Skirtumas tas, jog dingus ryðiui su duomenø baze þaidëjas bus atðaldytas. 
**/

static bool:gPlayerFreezed[ MAX_PLAYERS ];


hook OnPlayerDisconnect(playerid, reason)
{
	gPlayerFreezed[ playerid ] = false;
}


stock MySQL_FreezePlayer(playerid)
{
	gPlayerFreezed[ playerid ] = true;
	TogglePlayerControllable(playerid, false);
}

stock MySQL_UnfreezePlayer(playerid)
{
	gPlayerFreezed[ playerid ] = false;
	TogglePlayerControllable(playerid, true);
}


public OnQueryError(errorid, error[], callback[], query[], connectionHandle)
{
	switch(errorid)
	{
		case 2003, 2013:
		{
			for(new i = 0; i < MAX_PLAYERS; i++)
				if(IsPlayerConnected(i) && gPlayerFreezed[ i ] )
				{
					gPlayerFreezed[ i ] = false;
					TogglePlayerControllable(i, true);
				}
		}
	}
	#if defined mysqlpause_OnQueryError
		mysqlpause_OnQueryError(errorid, error, callback, query, connectionHandle);
	#endif
	return 1;
}
#if defined _ALS_OnQueryError
	#undef OnQueryError
#else 
	#define _ALS_OnQueryError
#endif
#define OnQueryError 					mysqlpause_OnQueryError
#if defined mysqlpause_OnQueryError
	forward mysqlpause_OnQueryError(errorid, error[], callback[], query[], connectionHandle);
#endif