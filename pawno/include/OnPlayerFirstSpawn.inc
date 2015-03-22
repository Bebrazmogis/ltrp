
#include <YSI\y_hooks>



static bool:IsPlayerSpawned[ MAX_PLAYERS ]; 



hook OnPlayerConnect(playerid)
{
	IsPlayerSpawned[ playerid ] = false;
	return 1;
}

hook OnPlayerSpawn(playerid)
{
	if(!IsPlayerSpawned[ playerid ])
	{
		IsPlayerSpawned[ playerid ] = true;
		CallLocalFunction("OnPlayerFirstSpawn", "i", playerid);
	}
	return 1;
}

forward OnPlayerFirstSpawn(playerid);
