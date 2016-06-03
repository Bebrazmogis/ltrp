
#define NULL ""
#define SHOEBILL_DEBUG
#define SHOEBILL_DEBUG_LEVEL 1

java_GetPlayerMoney(playerid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling getPlayerMoney(%d)", playerid);
	#endif
	new returned = CallShoebillFunction("getPlayerMoney", playerid);
	#if defined SHOEBILL_DEBUG
		printf("getPlayerMoney returned %d", returned);
	#endif
	return returned;
}
#if defined _ALS_GetPlayerMoney
	#undef GetPlayerMoney
#else 
	#define _ALS_GetPlayerMoney
#endif
#define GetPlayerMoney java_GetPlayerMoney


ShowJobVehManagementDialog(playerid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling showJobVehManagementDialog(%s)", playerid);
	#endif
	new returned = CallShoebillFunction("showJobVehManagementDialog", playerid);
	#if defined SHOEBILL_DEBUG
		printf("showJobVehManagementDialog returned %d", returned);
	#else 
		#pragma unused returned
	#endif
}


/*
forward StopAcceptingCargo(businessuuid, type);
public StopAcceptingCargo(businessuuid, type)
{
    StopBusinessBuyingCargo(GetBusinessIndex(businessuuid), GetBusinessCargo(type));
}

forward GetMinCommodityPrice(businessuuid, type);
public GetMinCommodityPrice(businessuuid, type)
{
    new averagePrice, sellerCount,bizCargo = GetBusinessCargo(type);
    foreach(CommodityIterator, i)
        if(Commodities[ i ][ CargoId ] == bizCargo && Commodities[ i ][ SellBuyStatus ] == Selling)
        {
            sellerCount++;
            averagePrice += Commodities[ i ][ Price ];
        }

    // Tikimes kad taip nebus
    if(!sellerCount)
    {
        ErrorLog(" sellerCount is:%d. Commodity name:%s", sellerCount, GetCargoName(bizCargo));
        return 1;
    }

    averagePrice /= sellerCount;
    return averagePrice - averagePrice / 100 * 20;
}

forward GetMaxCommodityPrice(businessuuid, type);
public GetMaxCommodityPrice(businessuuid, type)
{
    new averagePrice, sellerCount, bizCargo = GetBusinessCargo(type);
    foreach(CommodityIterator, i)
        if(Commodities[ i ][ CargoId ] == bizCargo && Commodities[ i ][ SellBuyStatus ] == Selling)
        {
            sellerCount++;
            averagePrice += Commodities[ i ][ Price ];
        }

     // Tikimes kad taip nebus
    if(!sellerCount)
    {
        ErrorLog(" sellerCount is:%d. Commodity name:%s", sellerCount, GetCargoName(bizCargo));
        return 1;
    }


    averagePrice /= sellerCount;
    return averagePrice + averagePrice / 100 * 20;
}
*/

/*

OnPlayerEnterHouse(playerid, housesqlid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling OnPlayerEnterHouse(%d, %d)", playerid, housesqlid;
	#endif
	new returned = CallShoebillFunction("OnPlayerEnterHouse", playerid, housesqlid);
	#if defined SHOEBILL_DEBUG
		printf("OnPlayerEnterHouse returned %d", returned);
	#else 
		#pragma unused returned
	#endif
}

OnPlayerEnterBusiness(playerid, businessqlid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling OnPlayerEnterBusiness(%d, %d)", playerid, businessqlid;
	#endif
	new returned = CallShoebillFunction("OnPlayerEnterBusiness", playerid, businessqlid);
	#if defined SHOEBILL_DEBUG
		printf("OnPlayerEnterBusiness returned %d", returned);
	#else 
		#pragma unused returned
	#endif
}


OnPlayerEnterGarage(playerid, garagesqlid, vehicleid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling OnPlayerEnterGarage(%d, %d, %d)", playerid, garagesqlid, vehicleid;
	#endif
	new returned = CallShoebillFunction("OnPlayerEnterGarage", playerid, garagesqlid, vehicleid);
	#if defined SHOEBILL_DEBUG
		printf("OnPlayerEnterGarage returned %d", returned);
	#else 
		#pragma unused returned
	#endif
}

OnPlayerExitGarage(playerid, garagesqlid, vehicleid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling OnPlayerExitGarage(%d, %d, %d)", playerid, garagesqlid, vehicleid;
	#endif
	new returned = CallShoebillFunction("OnPlayerExitGarage", playerid, garagesqlid, vehicleid);
	#if defined SHOEBILL_DEBUG
		printf("OnPlayerExitGarage returned %d", returned);
	#else 
		#pragma unused returned
	#endif
}

OnPlayerExitBusiness(playerid, businessqlid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling OnPlayerExitBusiness(%d, %d)", playerid, businessqlid;
	#endif
	new returned = CallShoebillFunction("OnPlayerExitBusiness", playerid, businessqlid);
	#if defined SHOEBILL_DEBUG
		printf("OnPlayerExitBusiness returned %d", returned);
	#else 
		#pragma unused returned
	#endif
}


OnPlayerExitHouse(playerid, housesqlid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling OnPlayerExitHouse(%d, %d)", playerid, housesqlid;
	#endif
	new returned = CallShoebillFunction("OnPlayerExitHouse", playerid, housesqlid);
	#if defined SHOEBILL_DEBUG
		printf("OnPlayerExitHouse returned %d", returned);
	#else 
		#pragma unused returned
	#endif
}
*/








GivePlayerBasicItem(playerid, itemid, amount, typeid, stackable)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d, %d,%d): GivePlayerBasicItem",playerid, itemid, amount, typeid, stackable);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, typeid, NULL, amount, stackable, NULL);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d", returned);
	#else 
		#pragma unused itemid
	#endif
	return returned;
}


GetPlayerLevel(playerid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling GetPlayerLevel(%d)",playerid);
	#endif
	new returned = CallShoebillFunction("getPlayerLevel", playerid);
	#if defined SHOEBILL_DEBUG
		printf("getPlayerLevel returned %d", returned);
	#endif
	return returned;
}

SaveAccount(playerid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling SaveAccount(%d, %d)",playerid);
	#endif
	new returned = CallShoebillFunction("saveAccount", playerid);
	#if defined SHOEBILL_DEBUG
		printf("SaveAccount returned %d", returned);
	#endif
	return returned;
}

AddJobExp(playerid, xp)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling addJobExp(%d, %d)",playerid, xp);
	#endif
	new returned = CallShoebillFunction("addJobExp", playerid, xp);
	#if defined SHOEBILL_DEBUG
		printf("addJobExp returned %d", returned);
	#endif
	return returned;
}

GetPlayerBankMoney(playerid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling GetPlayerBankMoney(%d)",playerid);
	#endif
	new returned = CallShoebillFunction("getPlayerBankMoney", playerid);
	#if defined SHOEBILL_DEBUG
		printf("GetPlayerBankMoney returned %d", returned);
	#endif
	return returned;
}

AddPlayerBankMoney(playerid, moeny)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling addPlayerBankMoney(%d, %d)",playerid, moeny);
	#endif
	new returned = CallShoebillFunction("addPlayerBankMoney", playerid, moeny);
	#if defined SHOEBILL_DEBUG
		printf("addPlayerBankMoney returned %d", returned);
	#endif
	return returned;
}


GetPlayerDeaths(playerid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling getPlayerDeaths(%d)",playerid);
	#endif
	new returned = CallShoebillFunction("getPlayerDeaths", playerid);
	#if defined SHOEBILL_DEBUG
		printf("getPlayerDeaths returned %d", returned);
	#endif
	return returned;
}


GetPlayerAdminLevel(playerid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling getPlayerAdminLevel(%d)",playerid);
	#endif
	new returned = CallShoebillFunction("getPlayerAdminLevel", playerid);
	#if defined SHOEBILL_DEBUG
		printf("getPlayerAdminLevel returned %d", returned);
	#else 
		#pragma unused returned
	#endif
	return returned;
}
/*
GetPlayerConnectedTime(playerid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling getPlayerConnectedTime(%d)",playerid);
	#endif
	new returned = CallShoebillFunction("getPlayerConnectedTime", playerid);
	#if defined SHOEBILL_DEBUG
		printf("getPlayerConnectedTime returned %d", returned);
	#endif
	return returned;
}*/

Shoebill_SetPlayerSkin(playerid, skinid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling setPlayerSkin(%d, %d)",playerid, skinid);
	#endif
	CallShoebillFunction("setPlayerSkin", playerid, skinid);
}
#if defined _ALS_SetPlayerSkin
	#undef SetPlayerSkin
#else 
	#define _ALS_SetPlayerSkin
#endif
#define SetPlayerSkin Shoebill_SetPlayerSkin

SetPlayerDeaths(playerid, deaths)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling setPlayerDeaths(%d, %d)",playerid, deaths);
	#endif
	new returned = CallShoebillFunction("setPlayerDeaths", playerid, deaths);
	#if defined SHOEBILL_DEBUG
		printf("setPlayerDeaths returned %d", returned);
	#else 
		#pragma unused returned
	#endif
	return returned;
}


GetPlayerConnectedTime(playerid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling getPlayerConnectedTime(%d)",playerid);
	#endif
	new returned = CallShoebillFunction("getPlayerConnectedTime", playerid);
	#if defined SHOEBILL_DEBUG
		printf("getPlayerConnectedTime returned %d", returned);
	#else 
		#pragma unused returned
	#endif
	return returned;
}

GetPlayerSqlId(playerid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling GetPlayerSqlId(%d)",playerid);
	#endif
	new returned = CallShoebillFunction("getPlayerSqlId", playerid);
	#if defined SHOEBILL_DEBUG
		printf("GetPlayerSqlId returned %d", returned);
	#else 
		#pragma unused returned
	#endif
	return returned;
}


IsPlayerLoggedIn(playerid)
{
	#if SHOEBILL_DEBUG_LEVEL==2
	printf("Calling isPlayerLoggedIn(%d)",playerid);
	#endif
	new returned = CallShoebillFunction("isPlayerLoggedIn", playerid);
	#if SHOEBILL_DEBUG_LEVEL==2
		printf("isPlayerLoggedIn returned %d", returned);
	#endif
	return returned;
}
