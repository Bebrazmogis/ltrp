
#define NULL ""


CreateProperty(type[], uid, Float:enx, Float:eny, Float:enz, enint, enVirtual, Float:exx, Float:exy, Float:exz, exint, exVirtual)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling createProperty(%s, %d, %f, %f, %f, %d, %d, %f, %f, %f, %d. %d)", type, uid, Float:enx, Float:eny, Float:enz, enint, enVirtual, Float:exx, Float:exy, Float:exz, exint, exVirtual);
	#endif
	new returned = CallShoebillFunction("createProperty", type, uid, Float:enx, Float:eny, Float:enz, enint, enVirtual, Float:exx, Float:exy, Float:exz, exint, exVirtual);
	#if defined SHOEBILL_DEBUG
		printf("createProperty returned %d" returned);
	#else 
		#pragma unused returned
	#endif
}

DestroyProperty(type[], uid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling destroyProperty(%s, %d)", type, uid;
	#endif
	new returned = CallShoebillFunction("destroyProperty", type, uid);
	#if defined SHOEBILL_DEBUG
		printf("destroyProperty returned %d" returned);
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
		printf("OnPlayerEnterHouse returned %d" returned);
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
		printf("OnPlayerEnterBusiness returned %d" returned);
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
		printf("OnPlayerEnterGarage returned %d" returned);
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
		printf("OnPlayerExitGarage returned %d" returned);
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
		printf("OnPlayerExitBusiness returned %d" returned);
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
		printf("OnPlayerExitHouse returned %d" returned);
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
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 15);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d" returned);
	#else 
		#pragma unused returned
	#endif
	return returned;
}


GivePlayerCigarettesItem(playerid, itemid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d, %d): GivePlayerCigarettesItem", playerid, itemid);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 14);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d" returned);
	#else 
		#pragma unused returned
	#endif
	return returned;
}


GivePlayerFuelTankItem(playerid, itemid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d, %d): GivePlayerFuelTankItem", playerid, itemid);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 13);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d" returned);
	#else 
		#pragma unused returned
	#endif
	return returned;
}

GivePlayerFishRodItem(playerid, itemid)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d, %d): GivePlayerFishRodItem", playerid, itemid);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 9);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d" returned);
	#else 
		#pragma unused returned
	#endif
	return returned;
}


GivePlayerFishingBait(playerid, itemid, durability, maxdurability)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d, %d): GivePlayerFishingBait", playerid, itemid, durability, maxdurability);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 10, NULL, 1, durability, maxdurability, 0);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d" returned);
	#else 
		#pragma unused returned
	#endif
	return returned;
}



GivePlayerFishingBag(playerid, itemid, items, maxsize)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d, %d): GivePlayerFishingBag",playerid, itemid, items, maxsize);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 11, NULL, 1, items, maxsize, 0);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d" returned);
	#else 
		#pragma unused returned
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
		printf("tryGivePlayerItemType returned %d" returned);
	#else 
		#pragma unused returned
	#endif
	return returned;
}


GivePlayerBasicItem(playerid, itemid, amount, typeid, stackable)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d, %d,%d): GivePlayerBasicItem",playerid, itemid, amount, typeid, stackable);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, typeid, NULL, amount, stackable);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d" returned);
	#else 
		#pragma unused returned
	#endif
	return returned;
}

GivePlayerWeaponItem(playerid, weaponid, amount)
{
	#if defined SHOEBILL_DEBUG
	printf("Calling tryGivePlayerItemType(%d, %d, %d): GivePlayerWeaponItem",playerid,weaponid, amount);
	#endif
	new returned = CallShoebillFunction("tryGivePlayerItemType", playerid, 20, weaponid, amount);
	#if defined SHOEBILL_DEBUG
		printf("tryGivePlayerItemType returned %d" returned);
	#else 
		#pragma unused returned
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
		printf("tryGivePlayerItemType returned %d" returned);
	#else 
		#pragma unused returned
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
		printf("tryGivePlayerItemType returned %d" returned);
	#else 
		#pragma unused returned
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
		printf("tryGivePlayerItemType returned %d" returned);
	#else 
		#pragma unused returned
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
		printf("tryGivePlayerItemType returned %d" returned);
	#else 
		#pragma unused returned
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
		printf("isPlayerInventoryFull returned %d" returned);
	#else 
		#pragma unused returned
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
		printf("removePlayerItem returned %d" returned);
	#else 
		#pragma unused returned
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
		printf("RemovePlayerDrugItems returned %d" returned);
	#else 
		#pragma unused returned
	#endif
	return returned;
}