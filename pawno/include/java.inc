


// tryGivePlayerItemType(int playerid, int ItemType, int itemid, int amount, args...);
// For item type ids see ItemType class

stock GivePlayerClothingItem(playerid, itemid, modelid, slot)
{
	printf("GivePlayerClothingItem(%d, %d, %d, %d) called", playerid, itemid, modelid, slot);
	new val = CallShoebillFunction("tryGivePlayerItemType", playerid, 16,  itemid, 1, modelid, slot);
	printf("GivePlayerClothingItem returned %d", val);
	return val;
}


GivePlayerToolItem(playerid, itemid, modelid, slot)
{
	printf("GivePlayerToolItem(%d, %d, %d, %d) called", playerid, itemid, modelid, slot);
	new val = CallShoebillFunction("tryGivePlayerItemType", playerid, 17,  itemid, 1, modelid, slot);
	printf("GivePlayerToolItem returned %d", val);
	return val;
}


GivePlayerSuitcaseItem(playerid, itemid, modelid, slot)
{
	printf("GivePlayerSuitcaseItem(%d, %d, %d, %d) called", playerid, itemid, modelid, slot);
	new val = CallShoebillFunction("tryGivePlayerItemType", playerid, 18, itemid, 1, modelid, slot);
	printf("GivePlayerSuitcaseItem returned %d", val);
	return val;
}


GivePlayerMaskItem(playerid, itemid, modelid, slot)
{
	printf("GivePlayerMaskItem(%d, %d, %d, %d) called", playerid, itemid, modelid, slot);
	new val = CallShoebillFunction("tryGivePlayerItemType", playerid, 19, itemid, 1, modelid, slot);
	printf("GivePlayerMaskItem returned %d", val);
	return val;
}

GivePlayerPhoneItem(playerid, itemid)
{
	printf("GivePlayerPhoneItem(%d) called", playerid);
	new val = CallShoebillFunction("tryGivePlayerItemType", playerid, 15, itemid, 1);
	printf("GivePlayerPhoneItem returned %d", val);
	return val;
}


GivePlayerCigarettesItem(playerid, itemid)
{
	printf("GivePlayerCigarettesItem(%d) called", playerid);
	new val = CallShoebillFunction("tryGivePlayerItemType", playerid, 14, itemid, 1);
	printf("GivePlayerCigarettesItem returned %d", val);
	return val;
}

GivePlayerFuelTankItem(playerid, itemid, durability)
{
	printf("GivePlayerFuelTankItem(%d, %d) called", playerid, durability);
	new val = CallShoebillFunction("tryGivePlayerItemType", playerid, 13, itemid, 1, durability, durability);
	printf("GivePlayerFuelTankItem returned %d", val);
	return val;
}

GivePlayerFishRodItem(playerid, itemid)
{
	printf("GivePlayerFishRodItem(%d) called", playerid);
	new val = CallShoebillFunction("tryGivePlayerItemType", playerid, 9, itemid, 1);
	printf("GivePlayerFishRodItem returned %d", val);
	return val;
}

GivePlayerDurableItem(playerid, itemid, durability, maxdurability, stackable, itemtype)
{
	printf("GivePlayerDurableItem(%d, %d, %d, %d, %d, %d) called", playerid, itemid, durability, maxdurability, stackable, itemtype);
	new val = CallShoebillFunction("tryGivePlayerItemType", playerid, itemtype, itemid, 1, durability, maxdurability, stackable);
	printf("GivePlayerDurableItem returned %d", val);
	return val;
}


GivePlayerContainerItem(playerid, itemid, itemcount, size, stackable, itemtype)
{
	printf("GivePlayerContainerItem(%d, %d, %d, %d, %d, %d) called", playerid, itemid, itemcount, size, stackable, itemtype);
	new val = CallShoebillFunction("tryGivePlayerItemType", playerid, itemtype, itemid, 1, itemcount, size, stackable);
	printf("GivePlayerContainerItem returned %d", val);
	return val;
}


GivePlayerDrinkItem(playerid, itemid, doses, specialaction)
{
	printf("GivePlayerDrinkItem(%d, %d, %d, %d, %d, %d) called", playerid, itemid,  doses, specialaction);
	new val = CallShoebillFunction("tryGivePlayerItemType", playerid, 21, itemid, 1, doses, specialaction);
	printf("GivePlayerDrinkItem returned %d", val);
	return val;
}

GivePlayerBasicItem(playerid, itemid, amount, itemtype, stackable)
{
	printf("GivePlayerBasicItem(%d, %d, %d, %d, %d) called", playerid, itemid, amount,  itemtype, stackable);
	new val = CallShoebillFunction("tryGivePlayerItemType", playerid, itemtype, itemid, amount,  itemtype, stackable);
	printf("GivePlayerBasicItem returned %d", val);
	return val;
}


GivePlayerWeaponItem(playerid, itemid, amount)
{
	printf("GivePlayerWeaponItem(%d, %d, %d) called", playerid, itemid,amount);
	new val = CallShoebillFunction("tryGivePlayerItemType", playerid, 20, itemid, amount);
	printf("GivePlayerWeaponItem returned %d", val);
	return val;
}



stock IsPlayerInventoryFull(playerid)
{
	printf("IsPlayerInventoryFull(%d) called", playerid);
	new val = CallShoebillFunction("isPlayerInventoryFull", playerid);
	printf("IsPlayerInventoryFull returned %d", val);
	return val;
}




// CreatePoperty(string type, int uid, float enx, float eny, float enz, int enint, int enVirtual, float exx, float exy, float exz, float exint, exVirtual);
CreateProperty(type[], uid, Float:enx, Float:eny, Float:enz, enint, envw, Float:exx, Float:exy, Float:exz, exint, exvw)
{
	printf("CreateProperty(%s, %d, %f, %f, %f, %d, %d, %f, %f, %f, %d, %d) called", type, uid, enx, eny, enz, enint, envw, exx, exy, exz, exint, exvw);
	new val = CallShoebillFunction("createProperty", type, uid, enx, eny, enz, enint, envw, exx, exy, exz, exint, exvw);
	printf("CreateProperty returned %d", val);
	return val;
}