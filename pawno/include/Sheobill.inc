
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

CreateProperty(type[], uid, Float:enx, Float:eny, Float:enz, enint, enVirtual, Float:exx, Float:exy, Float:exz, exint, exVirtual)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling createProperty(%s, %d, %f, %f, %f, %d, %d, %f, %f, %f, %d. %d)", type, uid, Float:enx, Float:eny, Float:enz, enint, enVirtual, Float:exx, Float:exy, Float:exz, exint, exVirtual);
	#endif
	new returned = CallShoebillFunction("createProperty", type, uid, Float:enx, Float:eny, Float:enz, enint, enVirtual, Float:exx, Float:exy, Float:exz, exint, exVirtual);
	#if defined SHOEBILL_DEBUG
		printf("createProperty returned %d", returned);
	#else 
		#pragma unused returned
	#endif
}

DestroyProperty(type[], uid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling destroyProperty(%s, %d)", type, uid);
	#endif
	new returned = CallShoebillFunction("destroyProperty", type, uid);
	#if defined SHOEBILL_DEBUG
		printf("destroyProperty returned %d", returned);
	#else 
		#pragma unused returned
	#endif
}

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





GivePlayerPhoneItem(playerid, itemid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d, %d): GivePlayerPhoneItem", playerid, itemid);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 15, NULL, NULL, NULL, NULL);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d", returned);
	#else 
		#pragma unused itemid
	#endif
	return returned;
}


GivePlayerCigarettesItem(playerid, itemid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d, %d): GivePlayerCigarettesItem", playerid, itemid);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 14, NULL, NULL, NULL, NULL);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d", returned);
	#else 
		#pragma unused itemid
	#endif
	return returned;
}


GivePlayerFuelTankItem(playerid, itemid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d, %d): GivePlayerFuelTankItem", playerid, itemid);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 13, NULL, NULL, NULL, NULL);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d", returned);
	#else 
		#pragma unused itemid
	#endif
	return returned;
}

GivePlayerFishRodItem(playerid, itemid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d, %d): GivePlayerFishRodItem", playerid, itemid);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 9, NULL, NULL, NULL, NULL);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d", returned);
	#else 
		#pragma unused itemid
	#endif
	return returned;
}


GivePlayerFishingBait(playerid, itemid, durability, maxdurability)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d, %d): GivePlayerFishingBait", playerid, itemid, durability, maxdurability);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 10, 0, 1, durability, maxdurability);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d", returned);
	#else 
		#pragma unused itemid
	#endif
	return returned;
}



GivePlayerFishingBag(playerid, itemid, items, maxsize)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d, %d): GivePlayerFishingBag",playerid, itemid, items, maxsize);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 11, NULL, 1, items, maxsize);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d", returned);
	#else 
		#pragma unused itemid
	#endif
	return returned;
}


GivePlayerDrinkItem(playerid, itemid, doses, specaction)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d, %d): GivePlayerDrinkItem", playerid, itemid, doses, specaction);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 21, NULL, 1, doses, specaction);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d", returned);
	#else 
		#pragma unused itemid
	#endif
	return returned;
}


GivePlayerToolkit(playerid, itemid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d): GivePlayerToolkit", playerid, itemid);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 37, NULL, 1, NULL, NULL);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d", returned);
	#else 
		#pragma unused itemid
	#endif
	return returned;
}

GivePlayerMp3(playerid, itemid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d): GivePlayerMp3", playerid, itemid);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 38, NULL, 1, NULL, NULL);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d", returned);
	#else 
		#pragma unused itemid
	#endif
	return returned;
}

GivePlayerHouseAudio(playerid, itemid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d): GivePlayerHouseAudio", playerid, itemid);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 32, NULL, 1, false, NULL);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d", returned);
	#else 
		#pragma unused itemid
	#endif
	return returned;
}

GivePlayerBoomBox(playerid, itemid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d): GivePlayerBoomBox", playerid, itemid);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 39, NULL, 1, false, NULL);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d", returned);
	#else 
		#pragma unused itemid
	#endif
	return returned;
}




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

GivePlayerWeaponItem(playerid, weaponid, amount)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d): GivePlayerWeaponItem",playerid,weaponid, amount);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 20, weaponid, amount, NULL, NULL);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d", returned);
	#endif
	return returned;
}

GivePlayerToolItem(playerid, itemid, modelid, bone)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d): GivePlayerToolItem",playerid, itemid, modelid, bone);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 17, NULL, 1, modelid, bone);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d", returned);
	#else 
		#pragma unused itemid
	#endif
	return returned;
}

GivePlayerMaskItem(playerid, itemid, modelid, bone)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d): GivePlayerMaskItem",playerid, itemid, modelid, bone);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 19, NULL, 1, modelid, bone);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d", returned);
	#else 
		#pragma unused itemid
	#endif
	return returned;
}

GivePlayerSuitcaseItem(playerid, itemid, modelid, bone)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d): GivePlayerSuitcaseItem",playerid, itemid, modelid, bone);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 18, NULL, 1, modelid, bone);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d", returned);
	#else 
		#pragma unused itemid
	#endif
	return returned;
}

GivePlayerClothingItem(playerid, itemid, modelid, bone)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d): GivePlayerSuitcaseItem",playerid, itemid, modelid, bone);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 16, NULL, 1, modelid, bone);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d", returned);
	#else 
		#pragma unused itemid
	#endif
	return returned;
}

IsPlayerInventoryFull(playerid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling isPlayerInventoryFull",playerid);
	#endif
	new returned = CallShoebillFunction("isPlayerInventoryFull", playerid);
	#if defined SHOEBILL_DEBUG
		printf("isPlayerInventoryFull returned %d", returned);
	#endif
	return returned;
}

RemovePlayerItem(playerid, typeid, nothing, howmanytoremove)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling removePlayerItem",playerid, typeid, nothing, howmanytoremove);
	#endif
	new returned = CallShoebillFunction("removePlayerItem", playerid, typeid, NULL, howmanytoremove);
	#if defined SHOEBILL_DEBUG
		printf("removePlayerItem returned %d", returned);
	#else 
		#pragma unused nothing
	#endif
	return returned;
}

RemovePlayerDrugItems(playerid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling RemovePlayerDrugItems",playerid);
	#endif
	new returned = CallShoebillFunction("removePlayerDrugItems", playerid);
	#if defined SHOEBILL_DEBUG
		printf("RemovePlayerDrugItems returned %d", returned);
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

SetPlayerLevel(playerid, level)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling setPlayerLevel(%d, %d)",playerid, level);
	#endif
	new returned = CallShoebillFunction("setPlayerLevel", playerid,level);
	#if defined SHOEBILL_DEBUG
		printf("setPlayerLevel returned %d", returned);
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
