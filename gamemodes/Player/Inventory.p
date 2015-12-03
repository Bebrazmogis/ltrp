
#include <YSI\y_hooks>

/*

	CREATE TABLE IF NOT EXISTS player_items (
		id INT AUTO_INCREMENT NOT NULL,
		player_id INT NOT NULL,
		item_id SMALLINT NOT NULL,
		amount SMALLINT UNSIGNED NOT NULL,
		content_amount SMALLINT UNSIGNED NOT NULL,
		durability SMALLINT UNSIGNED NOT NULL,
		slot TINYINT NOT NULL,
		PRIMARY KEY(id)
	) ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;

*/


#define MAX_PLAYER_ITEMS 				8

#define DIALOG_PLAYER_INVENTORY 		17
#define DIALOG_PLAYER_INVENTORY_OPTIONS 18
#define DIALOG_PLAYER_INVENTORY_AMOUNT 	10050

#define INVENTORY_EMPTY_SLOT_TEXT 		"{BBBBBB}Tu��ia vieta{FFFFFF}\n"



enum E_PLAYER_INVENTORY_DATA 
{
	Id, 
	ItemId,
	Amount,
	ContentAmount,
	Durability,
};

static PlayerItems[ MAX_PLAYERS ][ MAX_PLAYER_ITEMS ][ E_PLAYER_INVENTORY_DATA ],
	bool:IsPlayerInventoryLoaded[ MAX_PLAYERS ],
	PlayerUsedItemIndex[ MAX_PLAYERS ];



forward OnPlayerItemLoad(playerid);
forward OnPlayerItemLoaded(playerid, loadeditemcount);


/*
			                                                   
			                       ,,        ,,    ,,          
			`7MM"""Mq.            *MM      `7MM    db          
			  MM   `MM.            MM        MM                
			  MM   ,M9 `7MM  `7MM  MM,dMMb.  MM  `7MM  ,p6"bo  
			  MMmmdM9    MM    MM  MM    `Mb MM    MM 6M'  OO  
			  MM         MM    MM  MM     M8 MM    MM 8M       
			  MM         MM    MM  MM.   ,M9 MM    MM YM.    , 
			.JMML.       `Mbod"YML.P^YbmdP'.JMML..JMML.YMbmd'  
			                                                   
			                                                    


			                                                 ,,                             
			`7MM"""YMM                                mm     db                             
			  MM    `7                                MM                                    
			  MM   d `7MM  `7MM  `7MMpMMMb.  ,p6"bo mmMMmm `7MM  ,pW"Wq.`7MMpMMMb.  ,pP"Ybd 
			  MM""MM   MM    MM    MM    MM 6M'  OO   MM     MM 6W'   `Wb MM    MM  8I   `" 
			  MM   Y   MM    MM    MM    MM 8M        MM     MM 8M     M8 MM    MM  `YMMMa. 
			  MM       MM    MM    MM    MM YM.    ,  MM     MM YA.   ,A9 MM    MM  L.   I8 
			.JMML.     `Mbod"YML..JMML  JMML.YMbmd'   `Mbmo.JMML.`Ybmd9'.JMML  JMML.M9mmmP' 
			                                                                                
			                                                                                
*/

hook OnPlayerConnect(playerid)
{
	new query[120];
	GetPlayerName(playerid, query, sizeof(query));
	mysql_format(DbHandle, query, sizeof(query), "SELECT * FROM player_items WHERE player_id = (SELECT id FROM players WHERE name = '%e')", query);
	mysql_pquery(DbHandle, query, "OnPlayerItemLoad", "i", playerid);
}


hook OnPlayerDisconnect(playerid, reason)
{
	if(IsPlayerInventoryLoaded[ playerid ])
	{
		IsPlayerInventoryLoaded[ playerid ] = false;

		static EmptyPlayerItems[ E_PLAYER_INVENTORY_DATA ];
		for(new i = 0; i < MAX_PLAYER_ITEMS; i++)
			PlayerItems[ playerid ][ i ] = EmptyPlayerItems;
	}
	return 1;
}

public OnPlayerItemLoad(playerid)
{
	new index, itemid, loadeditemcount;
	for(new i = 0; i < cache_get_row_count(); i++)
	{
		index = cache_get_field_content_int(i, "slot");
		if(index < 0 || index >= MAX_PLAYER_ITEMS)
			continue;

		itemid = cache_get_field_content_int(i, "item_id");
		if(!IsValidItem(itemid))
		{
			ErrorLog("Invalid item ID(%d) in player %s inventory.", itemid, GetName(playerid));
			continue;
		}

		PlayerItems[ playerid ][ index ][ Id ] = cache_get_field_content_int(i, "id");
		PlayerItems[ playerid ][ index ][ ItemId ] = itemid;
		PlayerItems[ playerid ][ index ][ Amount ] = cache_get_field_content_int(i, "amount");
		PlayerItems[ playerid ][ index ][ ContentAmount ] = cache_get_field_content_int(i, "content_amount");
		PlayerItems[ playerid ][ index ][ Durability ] = cache_get_field_content_int(i, "durability");
		loadeditemcount++;
	}
	IsPlayerInventoryLoaded[ playerid ] = true;
	CallLocalFunction("OnPlayerItemLoaded", "ii", playerid, loadeditemcount);
	return 1;
}

public OnPlayerItemLoaded(playerid, loadeditemcount)
{
	#if defined inv_OnPlayerItemLoaded
		inv_OnPlayerItemLoaded(playerid, loadeditemcount);
	#endif
	return printf("[%d]Loaded %d items.", GetPlayerSqlId(playerid), loadeditemcount);
}

#if defined _ALS_OnPlayerItemLoaded
	#undef OnPlayerItemLoaded
#else 
	#define _ALS_OnPlayerItemLoaded
#endif
#define OnPlayerItemLoaded 				inv_OnPlayerItemLoaded
#if defined OnPlayerItemLoaded
	forward OnPlayerItemLoaded(playerid, loadeditemcount);
#endif


hook OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
	switch(dialogid)
	{
		case DIALOG_PLAYER_INVENTORY:
		{
			if(!response)
				return 1;

			// Jeigu ant tu��io sloto paspaud�, n�ra k� daryti daugiau.
			if(!strcmp(inputtext, #INVENTORY_EMPTY_SLOT_TEXT))
				return 1;

			PlayerUsedItemIndex[ playerid ] = listitem;
			printf("OnDialogResponse : DIALOG_PLAYER_INVENTORY. Item name:%s itemid:%d listitem:%d",  GetItemName(PlayerItems[ playerid ][ listitem ][ ItemId ]),  PlayerItems[ playerid ][ listitem ][ ItemId ], listitem);

			ShowPlayerDialog(playerid, DIALOG_PLAYER_INVENTORY_OPTIONS, DIALOG_STYLE_LIST, GetItemName(PlayerItems[ playerid ][ listitem ][ ItemId ]),
                                "\n Panaudoti pagal paskirt�\
                                \n Perduoti kitam veik�jui\
                                \n Pad�ti � tr. priemon�\
                                \n Pad�ti � namo seif�\
                                \n Pad�ti � gara��\
                                \n Gri�ti\
                                \n {CC0000}I�mesti", "Naudoti", "I�jungti");
			return 1;
		}
		case DIALOG_PLAYER_INVENTORY_OPTIONS:
		{
			if(!response)
				return 1;


			new itemid = PlayerItems[ playerid ][ PlayerUsedItemIndex[ playerid ] ][ ItemId ],
				amount = PlayerItems[ playerid ][ PlayerUsedItemIndex[ playerid ] ][ Amount ],
				contentamount = PlayerItems[ playerid ][ PlayerUsedItemIndex[ playerid ] ][ ContentAmount ],
				durability = PlayerItems[ playerid ][ PlayerUsedItemIndex[ playerid ] ][ Durability ],
				itemname[ MAX_ITEM_NAME ],
				string[128];

			itemname = GetItemName(itemid);

	        switch(listitem)
	        {
	            case 0:
	            {
	                if(IsPlayerInAnyVehicle(playerid) && IsItemWeapon(itemid)) 
	                	SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: b�damas transporto priemon�je ginklo i�traukti negalite.");
	                else 
	                {
	                	SelectPlayerItem(playerid, PlayerUsedItemIndex[ playerid ]);
	                }
	            }
	            /*
	            case 1:
	            {
	                new
	                    id = GetNearestPlayer(playerid, 5.0),
	                    IP[ 16 ],
	                    IP2[ 16 ];

	                if(id == INVALID_PLAYER_ID) 
	                	return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: norint perduoti daikt�turite stov�ti �alia to �aid�jo.");
	                if(GetPlayerVirtualWorld(playerid) != GetPlayerVirtualWorld(id)) 
	                	return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: aplink Jus n�ra nei vieno �aid�jo.");

	                switch(itemid)
                    {
                    	// �it� galb�t keisime.
                        case ITEM_RADIO, ITEM_FISH, ITEM_TEORIJA: 
                        	return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: �io daikto negalima atiduoti kitam �aid�jui.");

                        case ITEM_ROD:
                        {
                            if(IsPlayerAttachedObjectSlotUsed(playerid, 4))
                                RemovePlayerAttachedObject(playerid, 4);
                        }
                        case ITEM_PHONE:
                        {
                        	new phonenumber = GetPlayerPhoneNumber(playerid, GetPlayerPhoneIndexFromInvIndex(playerid, PlayerUsedItemIndex[ playerid ]));
                        	OnPlayerPhoneLocationChange(playerid, PlayerInventory, PlayerInventory, phonenumber, id);
                        }
                    }
                    if(IsItemDrug(itemid))
                    {
                        NarkLog(GetPlayerSqlId(playerid), 6, GetPlayerSqlId(playerid), GetItemName(itemid), amount);
                        NarkLog(GetPlayerSqlId(id), 5,GetPlayerSqlId(playerid), GetItemName(itemid), amount);
                    }
                    if(itemid > 21 && itemid < 50)
                    {
                        if(pInfo[ playerid ][ pLevel ] < 2)
                            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo netur�dami antro lygio.");
                        if(pInfo[ id ][ pLevel ] < 2)
                            return SendClientMessage(playerid, COLOR_GREY, "   �aid�jas turi b�ti 2 arba auk�tesnio lygio.");
                        GunLog(GetPlayerSqlId(playerid), 6, GetPlayerSqlId(id), GetItemName(itemid), amount);
                        GunLog(GetPlayerSqlId(id), 5, GetPlayerSqlId(playerid), GetItemName(itemid), amount);
                    }

                    GetPlayerIp(playerid, IP, 16);
                    GetPlayerIp(id, IP2, 16);

                    if(!strcmp(IP, IP2, true) || pInfo[ playerid ][ pUcpID ] == pInfo[ id ][ pUcpID ])
                        return true;

                    if(IsPlayerInventoryFull(id)) 
                    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: �aid�jas neturi laisvos vietos inventoriuje.");
    

                    if(IsItemStackable(itemid))
                    {
                    	SetPVarInt(playerid, "ItemId", itemid);
                    	SetPVarInt(playerid, "Player", id);
                    	ShowPlayerInventoryAmountInput(playerid);
                    }
                    else 
                    {
                    	LoopingAnim(playerid, "DEALER", "shop_pay", 4.0, 0, 1, 1, 1, 0);
	                    format(string, sizeof(string), "** %s perduod� �alia stovin�iam %s rankoje laikom� daikt� %s.", GetPlayerNameEx(playerid), GetPlayerNameEx(id), itemname);
	                    ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
	                    format(string, sizeof(string), "Veik�jas %s Jums perdav� daikt� pavadinim� %s, kur� rasite para�� /inv.", GetPlayerNameEx(playerid), itemname);
	                    SendClientMessage(id, COLOR_WHITE, string);
	                    format(string, sizeof(string), "J�s s�kmingai perdav�t� %s �alia stovin�iui veik�jui %s", itemname, GetPlayerNameEx(id));
	                    SendClientMessage(playerid, COLOR_WHITE, string);

	                  
	                    GivePlayerItem(id, itemid, amount, PlayerItems[ playerid ][ PlayerUsedItemIndex[ playerid ] ][ ContentAmount ], PlayerItems[ playerid ][ PlayerUsedItemIndex[ playerid ] ][ ContentAmount ]);
	                    RemovePlayerItemAtIndex(playerid, PlayerUsedItemIndex[ playerid ]);
	                    PlayerUsedItemIndex[ playerid ] = -1;
                    }
                    return 1;
	            }
	            */
	            /*
	            case 2:
	            {
	                new car = GetNearestVehicle(playerid, 10.0);
	                if(car == INVALID_VEHICLE_ID) 
	                	return 1;
	                if(cInfo[ car ][ cLock ] == 1) 
	                	return SendClientMessage(playerid , COLOR_LIGHTRED, "Persp�jimas: baga�in� u�rakinta.");

	                if(GetVehicleTrunkSlots(GetVehicleModel(car))  < 1) 
	                	return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Transporto priemon� neturi baga�in�s.");

	                if(sVehicles[ car ][ Faction ] > 0 || sVehicles[ car ][ Job ] > 0) 
	                	return true;

	                for(new slot = 0; slot < GetVehicleTrunkSlots(GetVehicleModel(car)); slot ++)
	                {
	                    if(cInfo[ car ][ cTrunkWeapon ][ slot ] == 0)
	                    {
	                        switch(itemid)
	                        {
	                            case ITEM_RADIO, ITEM_FISH, ITEM_TEORIJA: 
	                            	return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: �io daikto negalima d�ti � automobil�.");
	                            case ITEM_ROD:
	                            {
	                                if(IsPlayerAttachedObjectSlotUsed(playerid, 4))
	                                    RemovePlayerAttachedObject(playerid, 4);
	                            }
	                            case ITEM_PHONE:
		                        {
		                        	new phonenumber = GetPlayerPhoneNumber(playerid, GetPlayerPhoneIndexFromInvIndex(playerid, PlayerUsedItemIndex[ playerid ]));
		                        	OnPlayerPhoneLocationChange(playerid, PlayerInventory, VehicleTrunk, phonenumber, car);
		                        }
	                        }
	                        new wep = GetPlayerWeapon(playerid);
	                        if(wep)
	                            CheckWeaponCheat(playerid, wep, 0);

	                        if(IsItemDrug(itemid))
	                            NarkLog(GetPlayerSqlId(playerid), 3, cInfo[ car ][ cOwner ], itemname, amount);



	                        if(itemid > 21 && itemid < 50)
	                        {
	                            if(pInfo[ playerid ][ pLevel ] < 2)
	                                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo netur�dami antro lygio.");

	                            GunLog(GetPlayerSqlId(playerid), 3, cInfo[ car ][ cOwner ], itemname, amount);
	                        }
	                        
	                        format(string, sizeof(string), "* %s � �alia esan�ios transporto priemon�s baga�in� �ded� %s", GetPlayerNameEx(playerid), itemname);
	                        ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);

	                        cInfo[ car ][ cTrunkWeapon ][ slot ] = itemid;
	                        cInfo[ car ][ cTrunkAmmo   ][ slot ] = amount;
	                        cInfo[ car ][ cTrunkItemContent ][ slot ] = contentamount;
	                        cInfo[ car ][ cTrunkItemDurability ][ slot ] = durability;


	                        SaveCar(car);
	                        RemovePlayerItemAtIndex(playerid, PlayerUsedItemIndex[ playerid ]);
	                        return 1;
	                    }
	                }
	                return SendClientMessage(playerid , COLOR_LIGHTRED, "Klaida, �ios tr. priemon�s baga�in� pilna.");
	            }
	            */
	            /*
	            case 3:
	            {
	                new index = GetPlayerHouseIndex(playerid);
	                if(index == -1)
	                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s neesate name.");

	                if(!IsPlayerInHouse(playerid, index))
	                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalit daikto �d�ti � nam� stov�damas prie jo.");

	                if(!IsPlayerHouseOwner(playerid, index))
	                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �is namas jums nepriklauso.");

	                new slot = GetHouseFreeitemSlot(index);

	                if(slot == -1)
	                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, daugiau daikt� nebetelpa.");

	                switch(itemid)
	                {
	                    case ITEM_RADIO, ITEM_FISH, ITEM_TEORIJA: return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: �io daikto negalima d�ti � spintel�.");
	                    case ITEM_ROD:
	                    {
	                        if(IsPlayerAttachedObjectSlotUsed(playerid, 4))
	                            RemovePlayerAttachedObject(playerid, 4);
	                    }
	                    case ITEM_PHONE:
                        {
                        	new phonenumber = GetPlayerPhoneNumber(playerid, GetPlayerPhoneIndexFromInvIndex(playerid, PlayerUsedItemIndex[ playerid ]));
                        	OnPlayerPhoneLocationChange(playerid, PlayerInventory, HouseInventory, phonenumber, index);
                        }
	                }
	                if(IsItemDrug(itemid))
	                    NarkLog(GetPlayerSqlId(playerid), 1, GetHouseOwner(index), itemname, amount);
	                if(itemid > 21 && itemid < 50)
	                {
	                    if(pInfo[ playerid ][ pLevel ] < 2)
	                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo netur�dami antro lygio.");

	                    GunLog(GetPlayerSqlId(playerid), 1, GetHouseOwner(index), itemname, amount);
	                }
	                format(string, sizeof(string), "* %s atidaro spintel� ir �deda � ja daikt�, kuris atrodo kaip %s", GetPlayerNameEx(playerid), itemname);
	                ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);


	                RemovePlayerItemAtIndex(playerid, PlayerUsedItemIndex[ playerid ]);
	                SetHouseItem(index, slot, itemid, amount, contentamount, durability);
	                return 1;
	            }
	            */
	            /*
	            case 4:
	            {
	                new index = GetPlayerGarageIndex(playerid);
	                if(index == -1)
	                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s neesate prie gara�o.");

	                if(!IsPlayerInGarage(playerid, index))
	                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalit daikto �d�ti � gara�� stov�damas prie jo.");

	                if(!IsPlayerGarageOwner(playerid, index))
	                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �is gara�as jums nepriklauso.");

	                new slot = GetGarageFreeItemSlot(index);

	                if(slot == -1)
	                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, daugiau daikt� nebetelpa.");

	                switch(itemid)
	                {
	                    case ITEM_RADIO, ITEM_FISH, ITEM_TEORIJA: return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: �io daikto negalima d�ti � spintel�.");
	                    case ITEM_ROD:
	                    {
	                        if(IsPlayerAttachedObjectSlotUsed(playerid, 4))
	                            RemovePlayerAttachedObject(playerid, 4);
	                    }
	                    case ITEM_PHONE:
                        {
                        	new phonenumber = GetPlayerPhoneNumber(playerid, GetPlayerPhoneIndexFromInvIndex(playerid, PlayerUsedItemIndex[ playerid ]));
                        	OnPlayerPhoneLocationChange(playerid, PlayerInventory, GarageInventory, phonenumber, index);
                        }
	                }
	                if(IsItemDrug(itemid))
	                    NarkLog(GetPlayerSqlId(playerid), 7, GetGarageOwner(index), itemname, amount);
	                if(itemid > 21 && itemid < 50)
	                {
	                    if(pInfo[ playerid ][ pLevel ] < 2)
	                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo netur�dami antro lygio.");

	                    GunLog(GetPlayerSqlId(playerid), 7, GetGarageOwner(index), itemname, amount);
	                }
	                
	                format(string, sizeof(string), "* %s atidar�s spintel� � j� �deda %s", GetPlayerNameEx(playerid), itemname);
	                ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);

	                SetGarageItem(index, slot, itemid, amount, contentamount, durability);

	               	RemovePlayerItemAtIndex(playerid, PlayerUsedItemIndex[ playerid ]);
	                return 1;

	            }
	            */
	            case 5: ShowPlayerInventoryDialog(playerid);
	            /*
	            case 6:
	            {
	                format(string, sizeof(string), "* %s ant �em�s i�met� %s.", GetPlayerNameEx(playerid), itemname);
	                ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
	                RemovePlayerItemAtIndex(playerid, PlayerUsedItemIndex[ playerid ]);
	                return 1;
	            }*/
	        }
	        return 1;
		}
		case DIALOG_PLAYER_INVENTORY_AMOUNT:
		{
			if(!response)
				return 1;

			new itemid = GetPVarInt(playerid, "ItemId"),
        		targetid = GetPVarInt(playerid, "Player"),
        		itemname[ MAX_ITEM_NAME ],
        		string[120],
        		giveamount;

        	if(sscanf(inputtext, "i", giveamount))
        		return ShowPlayerInventoryAmountInput(playerid);
        	
        	if(giveamount < 0 || giveamount > GetPlayerItemAmount(playerid, itemid))
        		return ShowPlayerInventoryAmountInput(playerid, "J�s tiek neturite.");

        	itemname = GetItemName(itemid);

        	LoopingAnim(playerid, "DEALER", "shop_pay", 4.0, 0, 1, 1, 1, 0);
            format(string, sizeof(string), "** %s perduod� �alia stovin�iam %s rankoje laikom� daikt� %s.", GetPlayerNameEx(playerid), GetPlayerNameEx(targetid), itemname);
            ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
            format(string, sizeof(string), "Veik�jas %s Jums perdav� daikt� pavadinim� %s, kur� rasite para�� /inv.", GetPlayerNameEx(playerid), itemname);
            SendClientMessage(targetid, COLOR_WHITE, string);
            format(string, sizeof(string), "J�s s�kmingai perdav�t� %s �alia stovin�iui veik�jui %s", itemname, GetPlayerNameEx(targetid));
            SendClientMessage(playerid, COLOR_WHITE, string);

          
            GivePlayerItem(targetid, itemid, giveamount, GetPlayerItemContentAmount(playerid, itemid), GetPlayerItemDurability(playerid, itemid));
            GivePlayerItem(playerid, itemid, -giveamount);
            PlayerUsedItemIndex[ playerid ] = -1;
            return 1;
		}
	}
	return 0;
}


public OnPlayerUseItem(playerid, itemid, success)
{
	printf("OnPlayerUseItem(%s, %d, %d)", GetName(playerid), itemid, success);
}

stock OnPlayerItemRemoved(playerid, itemid)
{
	#if defined DEBUG 	
		printf("OnPlayerItemRemoved(%d, %d)", playerid, itemid);
	#endif
	if(GetAttachedItemSlot(itemid) != -1) 
	{
		if(IsPlayerWearingItem(playerid, itemid))
		{
			DeletePlayerAttachedItem(playerid, itemid);
		}
	}
    switch ( itemid )
    {
        case ITEM_PHONE: pInfo[ playerid ][ pPhone ] = 0;
        case ITEM_RADIO:
        {
            pInfo[ playerid ][ pRChannel ] = 0;
            if(GetPlayerState(playerid) == PLAYER_STATE_DRIVER)
            	UpdatePlayerInfoText(playerid, PLAYER_STATE_DRIVER);
            else 
            	UpdatePlayerInfoText(playerid);

        }
        case ITEM_MASK,ITEM_MaskZorro1, ITEM_HockeyMask1:
        {
            if(IsItemInPlayerInventory( playerid, ITEM_MASK ) && pInfo[playerid][pMask] == 0)
            {
                pInfo[playerid][pMask] = 1;
                foreach(Player,i)
                {
                    ShowPlayerNameTagForPlayer(i, playerid, pInfo[playerid][pMask]);
                }
            }
        }
        case ITEM_MP3:
        {
        	StopPlayerRadio(playerid);
        }
        case ITEM_TOLKIT:
        {
            if ( Laikas[playerid] > 0 )
            {
                Laikas[playerid] = 0;
                LaikoTipas[playerid] = 0;
                if(CheckUnfreeze(playerid))
                    TogglePlayerControllable(playerid,true);
            }
        }
        case ITEM_ROD:
        {
            RemovePlayerAttachedObject( playerid, 4 );
        }
        case ITEM_BIGAUDIO:
        {
        	foreach(Player,id)
            {
                if(IsPlayerInDynamicArea(id, aInfo[ playerid ][ aArea ]))
                    Set3DAudioForPlayer( id, "", playerid);
            }
            format(aInfo[ playerid ][ aStation ], 128, "");
            aInfo[ playerid ][ aKords ][ 0 ] = 0.0;
            aInfo[ playerid ][ aKords ][ 1 ] = 0.0;
            aInfo[ playerid ][ aKords ][ 2 ] = 0.0;

            DestroyDynamicObject(aInfo[ playerid ][ aObjekt ]);

            if (IsValidDynamicArea(aInfo[ playerid ][ aArea ]))
                DestroyDynamicArea(aInfo[ playerid ][ aArea ]);

            aInfo[ playerid ][ aObjekt ] = 0;
            Itter_Remove(Audio3D,playerid);
        }
    }
    return 1;
}

Item:OnPlayerUsePhone(playerid, itemid, invindex)
{
	new phoneindex = GetPlayerPhoneIndexFromInvIndex(playerid, invindex);
	if(phoneindex == -1)
		return 0;

	ShowPlayerPhoneMenu(playerid, phoneindex);
    return 1;
}

forward OnPlayerUseMask(playerid, itemid);
public OnPlayerUseMask(playerid, itemid)
{
	new bool:found, string[90];

    if(pInfo[ playerid ][ pLevel ] <= 2) 
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, veido kauk� galite u�sid�ti tik pasiek� 3 lyg�.");

    foreach(Player, x)
    {
        if(pInfo[ x ][ pAdmin ] >= 1 && AdminDuty[ x ])
        {
            found = true;
            break;
        }
    }

    if(pInfo[playerid][pMask] == 1)
    {
        if(!found) 
        	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, veido kauk� galite u�sid�ti tik tada kada serveryje yra prisijungusiu Administratori�.");

        if(GetItemObjectModel(itemid))
        {
        	AddPlayerAttachedItem(playerid, itemid, 2, 0.0, 0.0, 0.0, 0.0, 90.0, 90.0);
        	EditAttachedObject(playerid, GetAttachedItemSlot(itemid));
        }
        format(string, sizeof(string), "* %s i�sitraukia ir ant galvos u�simaun� veido kauk�.", GetPlayerNameEx(playerid));
        ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
        pInfo[ playerid ][ pMask ] = 0;

        format(string, 30, "Kauk�tasis ((ID: %d))", GetPlayerSqlId(playerid));

        foreach(Player,i)
        {
            ShowPlayerNameTagForPlayer(i, playerid, pInfo[playerid][pMask]);
        }
    }
    else
    {
        format(string, sizeof(string), "* %s nusimauna veido kauk� sau nuo veido.", GetPlayerNameEx(playerid));
        ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
        pInfo[ playerid ][ pMask ] = 1;
        DeletePlayerAttachedItem(playerid, itemid);
        foreach(Player,i)
        {
            ShowPlayerNameTagForPlayer(i, playerid, pInfo[playerid][pMask]);
        }
    }
    return 1;
}
/*
Item:OnPlayerStartSmoking(playerid, itemid, invindex)
{
    if(!IsItemInPlayerInventory(playerid, ITEM_ZIB) && !IsItemInPlayerInventory(playerid, ITEM_MATCHES)) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite prad�ti rukyti netur�dami kuo prisidegti.");

    if(Ruko[playerid] > 0) 
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �iuo metu J�s jau r�kote.");

	if(Mires[playerid] > 0) 
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas, kad J�s �iuo metu esate kritin�je komos b�senoje.");

	if(IsItemInPlayerInventory(playerid, ITEM_ZIB))
    {
    	AddPlayerItemDurability(playerid, ITEM_ZIB, -1);
    }
    else if(IsItemInPlayerInventory(playerid, ITEM_MATCHES))
    {
    	AddPlayerItemContentAmount(playerid, ITEM_MATCHES, -1);
    }

	switch(itemid)
	{
		case ITEM_CIG:
		{	
			AddPlayerItemContentAmountIndex(playerid, invindex, ITEM_CIG, -1);
            //SetPlayerItem(playerid, invindex, ITEM_CIG, GetPlayerItemAmount(ITEM_CIG)-1);

            cmd_ame(playerid, "i�sitraukia cigaret� i� pakelio ir u�sik�r�s j� �iebtuveliu pradeda r�kyti.");
            Ruko[playerid] = 180;
            if(!IsPlayerInAnyVehicle(playerid))
            {
                SetPlayerSpecialAction(playerid,SPECIAL_ACTION_SMOKE_CIGGY);
                OnePlayAnim(playerid,"SMOKING","M_smk_in",3.0,0,0,0,0,0);
            }
            return 1;
		}
		case ITEM_WEED:
        {
     
            cmd_ame(playerid, "laikydamas suktin� pridega j� �iebtuveliu.");
            GivePlayerItem(playerid, itemid, -1);

            Ruko[ playerid ] = 180;
            SetPlayerSpecialAction(playerid, SPECIAL_ACTION_SMOKE_CIGGY);
            LoopingAnim(playerid, "PAULNMAC", "pnm_loop_a", 3.0, 1, 0, 0, 0, 0);
            
            DrugTimer[ playerid ] = SetTimerEx("DrugsEffects", 15000, false, "i", playerid);
            
            SetPVarInt(playerid, "DrugHP", 3);
            SetPVarInt(playerid, "DrugHPLimit", 45);
        }
        case ITEM_OPIUM:
        {
            cmd_ame(playerid, "laikydamas suktin� pridega j� �iebtuveliu.");
            GivePlayerItem(playerid, itemid, -1);

            Ruko     [ playerid ] = 180;
            SetPlayerSpecialAction(playerid, SPECIAL_ACTION_SMOKE_CIGGY);
            LoopingAnim(playerid, "PAULNMAC", "pnm_loop_a", 3.0, 1, 0, 0, 0, 0);
            new Float: Health;
            GetPlayerHealth(playerid, Health);
            if(pInfo[ playerid ][ pOpiumAddict ] == 0)
                pInfo[ playerid ][ pOpiumAddict ] += 2;
            else
            {
                if(!GetPVarInt(playerid, "Addicted"))
                    pInfo[ playerid ][ pOpiumAddict ] += 1+random(2);
                else
                {
                    new
                        rand = 1+random(2);
                    if(pInfo[ playerid ][ pOpiumAddict ] - rand > 0)
                        pInfo[ playerid ][ pOpiumAddict] -= rand;
                    else
                        pInfo[ playerid ][ pOpiumAddict ] = 1;
                    SetPVarInt(playerid, "Addicted", false);
                }
            }

            DrugTimer[ playerid ] = SetTimerEx("DrugsEffects", 13000, false, "i", playerid);

            SetPVarInt(playerid, "DrugHP", 10);
            SetPVarInt(playerid, "DrugHPLimit", 50);
        }
        case ITEM_CRACK:
        {
        	cmd_ame(playerid, "laikydamas kreko suktin� rankose j� prisidega �iebtuveliu.");
            GivePlayerItem(playerid, itemid, -1);

            Ruko     [ playerid ] = 180;
            SetPlayerSpecialAction(playerid, SPECIAL_ACTION_SMOKE_CIGGY);
            SetPlayerDrunkLevel(playerid, 5 * 10000);
            new Float: Health;
            GetPlayerHealth(playerid, Health);
            if(pInfo[ playerid ][ pCrackAddict ] == 0)
                pInfo[ playerid ][ pCrackAddict ] += 4;
            else
            {
                if(!GetPVarInt(playerid, "Addicted"))
                    pInfo[ playerid ][ pCrackAddict ] += 4+random(6);
                else
                {
                    new
                        rand = 4+random(6);
                    if(pInfo[ playerid ][ pCrackAddict ] - rand > 0)
                        pInfo[ playerid ][ pCrackAddict ] -= rand;
                    else
                        pInfo[ playerid ][ pCrackAddict ] = 1;
                    SetPVarInt(playerid, "Addicted", false);
                }
            }
                
            SetPlayerWeather (playerid, 250);

            DrugTimer[ playerid ] = SetTimerEx("DrugsEffects", 5000, false, "i", playerid);

            SetPVarInt(playerid, "DrugHP", 15);
            SetPVarInt(playerid, "DrugHPLimit", 75);
        }
	}
	return 1;
}

*/
Item:OnPlayerUsePhoneCredit(playerid, itemid)
{
	SendClientMessage(playerid, COLOR_NEWS, "Nor�dami pasipildyti telefono s�skait�, telefono meniu pasirinkite \"Papildyti s�skait�\"");
	return 1;
}

Item:OnPlayerUseFuelTank(playerid, itemid, invindex, amount)
{
	new car = GetNearestVehicle(playerid, 10.0),
	 	string[92];
    if(car == INVALID_VEHICLE_ID) 
    	return 1;

    if(IsPlayerInVehicle(playerid, car))
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite �pilti kuro b�damas transporto priemon�je.");

    new maxfuel = GetVehicleFuelTank(GetVehicleModel(car));

    if(cInfo[car][cFuel] == maxfuel)
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "�ios transporto priemon�s bakas yra pilnas.");

    new fuel = GetPlayerItemContentAmountIndex(playerid, invindex);

    if(cInfo[car][cFuel] + fuel > maxfuel)
    {

        AddPlayerItemContentAmountIndex(playerid, invindex, ITEM_FUEL, -(maxfuel - cInfo[ car ][ cFuel ]));
        cInfo[car][cFuel] = maxfuel;
    }
    else
    {
        cInfo[ car ][ cFuel ] += fuel;
        RemovePlayerItemAtIndex(playerid, invindex);
    }
    format(string, sizeof(string), "* %s palenk�s degal� bakel� link bako pripil� � tr. priemon� kuro.", GetPlayerNameEx(playerid));
    ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
    return 1;
}
	

Item:OnPlayerUseToolkit(playerid, itemid)
{
	new
        veh = GetNearestVehicle(playerid, 2.5),
        bool:found = false,
        string[90];
        
    foreach(Player, x)
    {
        if(pInfo[ x ][ pAdmin ] >= 1 && AdminDuty[ x ])
        {
            found = true;
            break;
        }
    }
        
    if(veh == INVALID_VEHICLE_ID) 
    	return SendClientMessage(playerid, GRAD, "Aplink Jus �iuo metu n�ra jokio automobilio, kad atliktum�t veiksm�.");

    if(pInfo[ playerid ][ pJob ] != JOB_JACKER)
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite u�kurti/pavogti tr. priemon�s neb�damo vagimi.");

    if(!found)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, serveryje turi b�ti bent vienas administratorius kad gal�tumete tai atlikti.");
        
    if(!IsItemInPlayerInventory(playerid, ITEM_TOLKIT))
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s neturite repli�, kad atliktum�t �� veiksm�. ");

    if(!isLicCar(veh) && VehicleHasEngine(GetVehicleModel(veh)) && GetPlayerState(playerid) == PLAYER_STATE_ONFOOT)
    {
        if(cInfo[ veh ][ cLock ] == 1 && cInfo[ veh ][ cOwner ] > 0 && CheckCarKeys(playerid,veh) == 0)
        {
            SendAdminWarningMessage("%s bando pavogti transporto priemon�.", GetName(playerid));
            SetPVarInt(playerid, "CAR_JACK", veh);
            if(cInfo[veh][cLockType] == 0)
                StartTimer(playerid,60,5);
            else
                StartTimer(playerid,120*cInfo[veh][cLockType],5);
            format(string, sizeof(string), "* %s i�sitrauk�s �rankius bando atrakinti tr. priemon�s spinel�.",GetPlayerNameEx(playerid));
            ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
            CJLog(pInfo[ playerid ][ pMySQLID ], cInfo[veh][cID], "Bando atrakinti tr. priemon�");
            if(cInfo[veh][cAlarm] == 1 || cInfo[veh][cAlarm] == 2)
            {
                format(string,sizeof(string),"* Garsiai pypsi tr. priemon�s signalizacija ((%s))",cInfo[veh][cName]);
                ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
                VehicleAlarm(veh, 1);
            }
            else if(cInfo[veh][cAlarm] == 3)
            {
                new zone[30];
                GetPlayer2DZone(playerid, zone, 30);
                format(string,sizeof(string),"* Garsiai pypsi tr. priemon�s signalizacija ((%s))",cInfo[veh][cName]);
                ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
                SendTeamMessage(1, COLOR_LIGHTRED, "|________________�vykio reportavimas________________|");
                SendTeamMessage(1, COLOR_WHITE, "|Di�pe�erin� prane�a| Tr. priemon�s apsaugos sistema prane�a apie bandym� �slau�ti.");
                format(string, sizeof(string), "|Nustatyt� �silau�imo vieta|: Tr. priemon�s apsaugos sistema nurodo, kad �silau�imas vyksta: %s",zone);
                SendTeamMessage(1, COLOR_WHITE, string);
                VehicleAlarm(veh, 1);
            }
            else if(cInfo[veh][cAlarm] == 4)
            {
                new zone[30],
                    CarOwner = GetCarOwner(veh);
                GetPlayer2DZone(playerid, zone, 30);
                format(string,sizeof(string),"* Garsiai pypsi tr. priemon�s signalizacija ((%s))",cInfo[veh][cName]);
                ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
                SendTeamMessage(1, COLOR_LIGHTRED, "|________________Tr. priemon�s apsauga________________|");
                SendTeamMessage(1, COLOR_WHITE, "|Di�pe�erin� prane�a| Tr. priemon�s apsaugos sistema prane�a apie bandym� �slau�ti..");
                format(string, sizeof(string), "|Nustatyt� �silau�imo vieta|: Tr. priemon�s apsaugos sistema nurodo, kad �silau�imas vyksta: %s",zone);
                SendTeamMessage(1, COLOR_WHITE, string);
                
                if(!IsPlayerConnected(CarOwner)) 
                    return 1;
                SendClientMessage(CarOwner, COLOR_WHITE, "________________ Tr. priemon�s signalizacija _____________");
                SendClientMessage(CarOwner, COLOR_WHITE, "SMS �inut�: � J�s� transporto priemon� bandoma �silau�ti, pa�eista signalizacija. ");
                SetVehicleParamsForPlayer(veh,CarOwner,1,cInfo[veh][cLock]);
                PlayerPlaySound(CarOwner, 1052, 0.0, 0.0, 0.0);
                VehicleAlarm(veh, 1);
            }
        }
    }
    return 1;
}    

Item:OnPlayerUseWatch(playerid, itemid)
{
	new string[80];
	if(itemid == ITEM_CLOCK)
	{
		new Hour,
	        Min,
	        Sec;

	    gettime(Hour, Min, Sec);
	    format(string, sizeof(string), "Laikrodis rodo, kad �iuo metu yra %d:%d:%d",Hour,Min,Sec);
	    SendClientMessage(playerid, COLOR_FADE1, string);
	    format(string, sizeof(string), "* %s pasi�i�r� � ant rankos esant� laiktrod�.", GetPlayerNameEx(playerid));
	    ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
	    ApplyAnimation(playerid, "COP_AMBIENT", "Coplook_watch", 4.1, 0, 0, 0, 0, 0);
	}
	else 
	{
		if(!IsPlayerAttachedObjectSlotUsed(playerid, GetAttachedItemSlot(itemid)))
        {
            AddPlayerAttachedItem(playerid, itemid, 5, 0.000000, -0.007722, -0.011143, 9.279358, 270.517852, 190.637268);
            EditAttachedObject(playerid, 6);
            SendClientMessage(playerid, 0xFFFFFFFF, "Nor�dami pasukti/pakeisti kamer� laikykite klavi�us: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
        }
        else
        {
            SendClientMessage(playerid, COLOR_WHITE,"Daiktas buvo s�kmingai panaikintas/nuimtas.");
            DeletePlayerAttachedItem(playerid, itemid);
        }
	}
	return 1;
}


Item:OnPlayerUseDice(playerid, itemid)
{
	new spin,string[120];
    spin = random(6) + 1;
    format(string, sizeof(string), "** %s i�meta lo�imo kauliukus, kurie i�sriden� skai�i� - %d", GetPlayerNameEx(playerid),spin);
    ProxDetector(10.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
    return 1;
}
/*
Item:OnPlayerUseWeedSeeds(playerid, itemid)
{
    //if(pInfo[ playerid ][ pJob ] != JOB_DRUGS) 
    //	return SendClientMessage(playerid ,GRAD, "{FF6347}Klaida, negalite atlikti �io veiksmo neb�dami narkotik� prekeiviu.");
    	
    new index = GetPlayerHouseIndex(playerid);
    if(index == -1)
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s neesate namuose.");

    if(!IsPlayerHouseOwner(playerid, index))
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �is namas ne j�s�.");


    if(!GetHouseFreeWeedSlotCount(index))
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "�iame name daugiau �ol�s auginti nebegalite.");

    AddHouseWeedSapling(playerid, index);

    GivePlayerItem(playerid, itemid, -1);
    SendClientMessage(playerid, COLOR_WHITE, "Jums s�kmingai pavyko pas�ti �ol�s s�klas, dabar beliek� laukti kol augalas pilnai u�augs.");
    return 1;
}
*/

Item:OnPlayerUseDrugIngredient(playerid)
{
	SendClientMessage(playerid, GRAD, "Pasigaminimui naudojama komanda /make ");
	return 1;
}
/*
Item:OnPlayerUseMetaAmphetamine(playerid, itemid)
{
	GivePlayerItem(playerid, itemid, -1);
	if(pInfo[ playerid ][ pMetaAmfaineAddict ] == 0)
	    pInfo[ playerid ][ pMetaAmfaineAddict ] += 3;
	else
	{
	    if(!GetPVarInt(playerid, "Addicted"))
	        pInfo[ playerid ][ pMetaAmfaineAddict ] += 3+random(3);
	    else
	    {
	        new
	            rand = 3+random(3);
	        if(pInfo[ playerid ][ pMetaAmfaineAddict ] - rand > 0)
	            pInfo[ playerid ][ pMetaAmfaineAddict ] -= rand;
	        else
	            pInfo[ playerid ][ pMetaAmfaineAddict ] = 1;
	        SetPVarInt(playerid, "Addicted", false);
	    }
	}
	    
	SetPlayerWeather (playerid, 141);

	DrugTimer[ playerid ] = SetTimerEx("DrugsEffects", 13000, false, "i", playerid);

	SetPVarInt(playerid, "DrugHP", 10);
	SetPVarInt(playerid, "DrugHPLimit", 50);
    return 1;
}*/

/*
Item:OnPlayerUseAmphetamine(playerid, itemid)
{
	new string[80];
	GivePlayerItem(playerid, itemid, -1);
    SetPlayerDrunkLevel(playerid, 5 * 3000);

    format(string, sizeof(string), "* %s staigiai �traukia amfetamino doz� per nos�." ,GetPlayerNameEx(playerid));
    ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
    if(pInfo[ playerid ][ pAmfaAddict ] == 0)
        pInfo[ playerid ][ pAmfaAddict ] += 3;
    else
    {
        if(!GetPVarInt(playerid, "Addicted"))
            pInfo[ playerid ][ pAmfaAddict ] += 3+random(3);
        else
        {
            new
                rand = 3+random(3);
            if(pInfo[ playerid ][ pAmfaAddict ] - rand > 0)
                pInfo[ playerid ][ pAmfaAddict ] -= rand;
            else
                pInfo[ playerid ][ pAmfaAddict ] = 1;
            SetPVarInt(playerid, "Addicted", false);
        }
    }

    SetPlayerWeather (playerid, -68);

    DrugTimer[ playerid ] = SetTimerEx("DrugsEffects", 13000, false, "i", playerid);

    SetPVarInt(playerid, "DrugHP", 10);
    SetPVarInt(playerid, "DrugHPLimit", 50);
    return 1;
}
*/
/*
Item:OnPlayerUseCocaine(playerid, itemid)
{
	new string[90];

    format(string, sizeof(string), "* %s staigiai �traukia kokaino miltelius per nos�." ,GetPlayerNameEx(playerid));
    ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
    GivePlayerItem(playerid, itemid, -1);
    if(pInfo[ playerid ][ pCocaineAddict ] == 0)
        pInfo[ playerid ][ pCocaineAddict ] += 3;
    else
    {
        if(!GetPVarInt(playerid, "Addicted"))
            pInfo[ playerid ][ pCocaineAddict ] += 3+random(6);
        else
        {
            new
                rand = 3+random(6);
            if(pInfo[ playerid ][ pCocaineAddict ] - rand > 0)
                pInfo[ playerid ][ pCocaineAddict ] -= rand;
            else
                pInfo[ playerid ][ pCocaineAddict ] = 1;
            SetPVarInt(playerid, "Addicted", false);
        }
    }

    SetPlayerWeather (playerid, -68);

    DrugTimer[ playerid ] = SetTimerEx("DrugsEffects", 10000, false, "i", playerid);

    SetPVarInt(playerid, "DrugHP", 7);
    SetPVarInt(playerid, "DrugHPLimit", 70);

    return 1;
}

*/
/*
Item:OnPlayerUseEctazy(playerid)
{
	new string[100];
	GivePlayerItem(playerid, ITEM_EXTAZY, -1);
    SetPlayerDrunkLevel(playerid, 5 * 4000);
    format(string, sizeof(string), "* %s �sideda saujoje laikomas tabletes � burn� ir jas nuryj�." ,GetPlayerNameEx(playerid));
    ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
    if(pInfo[ playerid ][ pExtazyAddict ] == 0)
			pInfo[ playerid ][ pExtazyAddict ] += 5;
    else
    {
        if(!GetPVarInt(playerid, "Addicted"))
            pInfo[ playerid ][ pExtazyAddict ] += 5+random(5);
        else
        {
            new
                rand = 5+random(5);
            if(pInfo[ playerid ][ pExtazyAddict ] - rand > 0)
                pInfo[ playerid ][ pExtazyAddict ] -= rand;
            else
                pInfo[ playerid ][ pExtazyAddict ] = 1;
            SetPVarInt(playerid, "Addicted", false);
        }
    }

    DrugTimer[ playerid ] = SetTimerEx("DrugsEffects", 8000, false, "i", playerid);

    SetPVarInt(playerid, "DrugHP", 5);
    SetPVarInt(playerid, "DrugHPLimit", 50);
    return 1;
}
*/
/*
Item:OnPlayerUsePCP(playerid)
{
	new string[70];
	GivePlayerItem(playerid, ITEM_PCP, -1);
	SetPlayerDrunkLevel(playerid, 5 * 6000);
	format(string, sizeof(string), "* %s �sideda saujoje laikomas PCP tabletes � burn�." ,GetPlayerNameEx(playerid));
	ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
	if(pInfo[ playerid ][ pPCPAddict ] == 0)
	    pInfo[ playerid ][ pPCPAddict ] += 3;
	else
	{
	    if(!GetPVarInt(playerid, "Addicted"))
	        pInfo[ playerid ][ pPCPAddict ] += 2+random(3);
	    else
	    {
	        new
	            rand = 2+random(3);
	        if(pInfo[ playerid ][ pPCPAddict ] - rand > 0)
	            pInfo[ playerid ][ pPCPAddict ] -= rand;
	        else
	            pInfo[ playerid ][ pPCPAddict ] = 1;
	        SetPVarInt(playerid, "Addicted", false);
	    }
	}

	SetPlayerWeather (playerid, 250);

	DrugTimer[ playerid ] = SetTimerEx("DrugsEffects", 20000, false, "i", playerid);

	SetPVarInt(playerid, "DrugHP", 10);
	SetPVarInt(playerid, "DrugHPLimit", 50);
	return 1;
}*/


Item:OnPlayerUseBackpack(playerid)
{
	if(!IsPlayerAttachedObjectSlotUsed(playerid, GetAttachedItemSlot(ITEM_KUPRINE)))
    {
        AddPlayerAttachedItem(playerid, ITEM_KUPRINE, 1, -0.1,-0.0,0.0,0.0,0.0,0.0);
        EditAttachedObject(playerid, 6);
        SendClientMessage(playerid, 0xFFFFFFFF, "Nor�dami pasukti/pakeisti kamer� laikykite klavi�us: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
    }
    else
    {
        SendClientMessage(playerid, COLOR_WHITE,"Daiktas buvo s�kmingai panaikintas/nuimtas.");
        DeletePlayerAttachedItem(playerid, ITEM_KUPRINE);
    }
    return 1;
}


Item:OnPlayerUseBandana(playerid, itemid)
{
	if(!IsPlayerAttachedObjectSlotUsed(playerid, GetAttachedItemSlot(itemid)))
    {
        AddPlayerAttachedItem(playerid, itemid, 2, -0.08, 0.03, 0.0, 90, -180, -90);
        EditAttachedObject(playerid, GetAttachedItemSlot(itemid));
        SendClientMessage(playerid, 0xFFFFFFFF, "Nor�dami pasukti/pakeisti kamer� laikykite klavi�us: {FFFF00}~k~~PED_SPRINT~{FFFFFF}..");
    }
    else
    {
        SendClientMessage(playerid, COLOR_WHITE,"Daiktas buvo s�kmingai panaikintas/nuimtas.");
        DeletePlayerAttachedItem(playerid, itemid);
    }
    return 1;
}


Item:OnPlayerUseSuitcase(playerid, itemid)
{
	if(!IsPlayerAttachedObjectSlotUsed(playerid, GetAttachedItemSlot(itemid)))
    {
        AddPlayerAttachedItem(playerid, itemid, 6, 0.306118, -0.054140, 0.000000, 0.000000, 282.887756, 167.944808, 0.369485, 0.239421, 0.403359);
        EditAttachedObject(playerid, GetAttachedItemSlot(itemid));
        SendClientMessage(playerid, 0xFFFFFFFF, "Nor�dami pasukti/pakeisti kamer� laikykite klavi�us: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
    }
    else
    {
        SendClientMessage(playerid, COLOR_WHITE,"Daiktas buvo s�kmingai panaikintas/nuimtas.");
        DeletePlayerAttachedItem(playerid, itemid);
    }
    return 1;
}

Item:OnPlayerUseHat(playerid, itemid, invindex)
{
	if(!IsPlayerAttachedObjectSlotUsed(playerid, GetAttachedItemSlot(itemid)))
    {
        AddPlayerAttachedItem(playerid, itemid, 2, 0.118000,0.013000,0.002999,-94.299880,8.799993,-98.400047);
        EditAttachedObject(playerid, GetAttachedItemSlot(itemid));
        SendClientMessage(playerid, 0xFFFFFFFF, "Nor�dami pasukti/pakeisti kamer� laikykite klavi�us: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
    }
    else
    {
        SendClientMessage(playerid, COLOR_WHITE,"Daiktas buvo s�kmingai panaikintas/nuimtas.");
        DeletePlayerAttachedItem(playerid, itemid);
    }
    return 1;
}


Item:OnPlayerUseGlasses(playerid, itemid)
{
	if(!IsPlayerAttachedObjectSlotUsed(playerid, GetAttachedItemSlot(itemid)))
    {
        AddPlayerAttachedItem(playerid, itemid, 2, 0.086000,0.024999,0.001000,85.600021,82.900001,5.199999);
        EditAttachedObject(playerid, GetAttachedItemSlot(itemid));
        SendClientMessage(playerid, 0xFFFFFFFF, "Nor�dami pasukti/pakeisti kamer� laikykite klavi�us: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
    }
    else
    {
        SendClientMessage(playerid, COLOR_WHITE,"Daiktas buvo s�kmingai panaikintas/nuimtas.");
        DeletePlayerAttachedItem(playerid, itemid);
    }
    return 1;
}



Item:OnPlayerUseHelmet(playerid, itemid)
{
	new string[80];
	printf("OnPlayerUseHelmet(%d, %d) IsPlayerAttachedObjectSlotUsed:%d", playerid, itemid, GetAttachedItemSlot(itemid));
	if(IsPlayerAttachedObjectSlotUsed(playerid, GetAttachedItemSlot(itemid)))
    {
        DeletePlayerAttachedItem(playerid, itemid);
        format(string, sizeof(string), "* %s nuo galvos atsiseg� ir nusiem� �alm�." ,GetPlayerNameEx(playerid));
        ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
    }
    else
    {
        AddPlayerAttachedItem(playerid, itemid, 2, 0.07, 0.017, 0.0, 88.0, 75.0, 0.0);
        EditAttachedObject(playerid, GetAttachedItemSlot(itemid));
        format(string, sizeof(string), "* %s ant galvos u�sideda ir u�siseg� �alm�.", GetPlayerNameEx(playerid));
        ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
    }
	return 1;
}

Item:OnPlayerUseTool(playerid, itemid)
{
	if(!IsPlayerAttachedObjectSlotUsed(playerid, GetAttachedItemSlot(itemid)))
    {
        AddPlayerAttachedItem(playerid, itemid, 5, 0.086000,0.024999,0.001000,85.600021,82.900001,5.199999);
        EditAttachedObject(playerid, GetAttachedItemSlot(itemid));
        SendClientMessage(playerid, 0xFFFFFFFF, "Nor�dami pasukti/pakeisti kamer� laikykite klavi�us: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
    }
    else
    {
        SendClientMessage(playerid, COLOR_WHITE,"Daiktas buvo s�kmingai panaikintas/nuimtas.");
        DeletePlayerAttachedItem(playerid, itemid);
    }
    return 1;
}

/*
Item:OnPlayerUseHeroin(playerid, itemid)
{
	new string[100];
	if(!IsItemInPlayerInventory(playerid, ITEM_SVIRKSTAS)) 
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo, kartu netur�dami �virk�to.");


    format(string, sizeof(string), "* %s pasiem�s �virkst� �stato j� � ven� ant rankos ir susileid�ia heroin�." ,GetPlayerNameEx(playerid));
    ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
    GivePlayerItem(playerid, ITEM_SVIRKSTAS, -1);
    GivePlayerItem(playerid, itemid, -1);
    if(pInfo[ playerid ][ pHeroineAddict ] == 0)
        pInfo[ playerid ][ pHeroineAddict ] += 3;
    else
    {
        if(!GetPVarInt(playerid, "Addicted"))
            pInfo[ playerid ][ pHeroineAddict ] += 3+random(4);
        else
        {
            new
                rand = 3+random(4);
            if(pInfo[ playerid ][ pHeroineAddict ] - rand > 0)
                pInfo[ playerid ][ pHeroineAddict ] -= rand;
            else
                pInfo[ playerid ][ pHeroineAddict ] = 1;
            SetPVarInt(playerid, "Addicted", false);
        }
    }

    SetPlayerWeather (playerid, -64);

    DrugTimer[ playerid ] = SetTimerEx("DrugsEffects", 12000, false, "i", playerid);

    SetPVarInt(playerid, "DrugHP", 5);
    SetPVarInt(playerid, "DrugHPLimit", 65);
    return 1;
}
*/

Item:OnPlayerUseFishingRod(playerid, itemid)
{	
	new string[90];
	if(IsPlayerAttachedObjectSlotUsed(playerid, 4))
    {
        DeletePlayerAttachedItem(playerid, itemid);

        format(string, sizeof(string), "* %s sulankso rankose turim� me�ker� ir �sideda j�." ,GetPlayerNameEx(playerid));
        ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
    }
    else
    {
        AddPlayerAttachedItem(playerid, ITEM_ROD, 5, 0.111337, 0.019614, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        EditAttachedObject(playerid, 4);

        format(string, sizeof(string), "* %s i�sitraukia turim� sulankstom� me�ker� ir i�lanksto j�." ,GetPlayerNameEx(playerid));
        ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
    }
    return 1;
}
/*

Item:OnPlayerUseBeer(playerid, itemid)
{
	new string[60];
	SetPlayerSpecialAction(playerid, SPECIAL_ACTION_DRINK_BEER);
    format(string, sizeof(string), "* %s atkem�� alaus butel�." ,GetPlayerNameEx(playerid));
    cmd_ame(playerid, "atidaro alaus butel�.");
    GivePlayerItem(playerid, itemid, -1);
    return 1;
}

Item:OnPlayerUseSprunk(playerid, itemid)
{
	SetPlayerSpecialAction(playerid, SPECIAL_ACTION_DRINK_SPRUNK);
    cmd_ame(playerid, "atidaro sprunk butel�.");
    GivePlayerItem(playerid, itemid, -1);
    return 1;
}

Item:OnPlayerUseVine(playerid, itemid)
{
	SetPlayerSpecialAction(playerid, SPECIAL_ACTION_DRINK_WINE);
	cmd_ame(playerid, "atidaro vyno butel�.");
	GivePlayerItem(playerid, itemid, -1);
	return 1;
}
*/

Item:OnPlayerUseMolotov(playerid, itemid)
{
	new otherplaya = GetNearestPlayer(playerid, 5.0),
		string[100];
    if(pInfo[ playerid ][ pAdmin ] < 2 || (otherplaya != INVALID_PLAYER_ID && pInfo[ otherplaya ][ pAdmin ] < 2))
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo jei �alia J�s� n�ra budintis Administratorius. ");


    
    if(!IsItemInPlayerInventory(playerid, ITEM_ZIB) && !IsItemInPlayerInventory(playerid, ITEM_MATCHES)) 
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: j�s neturite su kuo u�kurti molotov'�.");

    if(IsItemInPlayerInventory(playerid, ITEM_ZIB))
    {
    	AddPlayerItemDurability(playerid, ITEM_ZIB, -1);
    }
    else if(IsItemInPlayerInventory(playerid, ITEM_MATCHES))
    {
    	AddPlayerItemContentAmount(playerid, ITEM_MATCHES, -1);
    }
    
    new Float:kords[ 3 ],
        Zona[ 30 ];
    GetPlayerPos(playerid, kords[ 0 ], kords[ 1 ], kords[ 2 ]);
    
    for (new i = 0; i < MAX_FIRE; i++)
    {
        if(!IsValidDynamicObject(Fire[ i ][ smoke ]) && !Fire[ i ][ active ])
        {
            Fire[ i ][ smoke ] = CreateDynamicObject(18715, kords[ 0 ], kords[ 1 ], kords[ 2 ]-0.7, 0, 0, 0, GetPlayerVirtualWorld(playerid), GetPlayerInterior(playerid), -1, 500.0);
            SetTimerEx("Explosion", 10000, 0, "fffddidd", kords[ 0 ], kords[ 1 ], kords[ 2 ]-0.7, i, 0, false, GetPlayerVirtualWorld(playerid), GetPlayerInterior(playerid));
            SetTimerEx("Explosion", 60*1000, 0, "fffddidd", kords[ 0 ], kords[ 1 ], kords[ 2 ]-0.7, i, 1, false, GetPlayerVirtualWorld(playerid), GetPlayerInterior(playerid));
            SetTimerEx("Explosion", 5*60*1000, 0, "fffddidd", kords[ 0 ], kords[ 1 ], kords[ 2 ]-0.7, i, 2, false, GetPlayerVirtualWorld(playerid), GetPlayerInterior(playerid));
            SetTimerEx("Explosion", 20*60*1000, 0, "fffddidd", kords[ 0 ], kords[ 1 ], kords[ 2 ]-0.7, i, 3, true, GetPlayerVirtualWorld(playerid), GetPlayerInterior(playerid));
            Fire[ i ][ active ] = true;
            break;
        }
    }

    format(string, sizeof(string), "* %s numeta degal� bakel� ant �em�s ir atsargiai padeg� j�. " ,GetPlayerNameEx(playerid));
    ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);

    GetPlayer2DZone(playerid, Zona, 30);

    Tlc[ 0 ] = kords[ 0 ];
    Tlc[ 1 ] = kords[ 1 ];
    Tlc[ 2 ] = kords[ 2 ];

    SendClientMessage(playerid, COLOR_RED, "D�mesio, b�kite kuo toliau nuo bakelio, kadangi galite nukent�ti nuo sprogimo.");

    format(string, 126, "AdmWarn: ([%d]%s) buvo sukurtas sprogimas naudojant degal� bakel�.", playerid, GetName(playerid));
    SendAdminMessage(COLOR_ADM, string);

    SendTeamMessage(2, COLOR_LIGHTRED, "|________________�vykio prane�imas________________|");
    SendTeamMessage(2, COLOR_WHITE, "|Di�pe�erin� prane�a| Buvo gautas prane�imas apie kilus�/sukelt� sprogim�.");
    format(string, sizeof(string),    "|Nustatyt� vieta| Nustatyta, kad �vykis �vyko: %s ((/tlc))",Zona);
    SendTeamMessage(2, COLOR_WHITE, string);

    GivePlayerItem(playerid, itemid, -1);
    return 1;
}

Item:OnPlayerUseMP3Player(playerid, itemid)
{
	new string[64];
	if(IsPlayerInAnyVehicle(playerid))
    {
        new veh = GetPlayerVehicleID(playerid);
        if(VehicleRadio[ veh ] != 99)
            RadioName[ playerid ] = 99;
    }
    format(string, sizeof(string), "- Radijo stotys\
                        \n- Garsumas \t[ %d ]\
                        \n- I�jungti", GetRadioVolume(playerid));
    ShowPlayerDialog(playerid, 67, DIALOG_STYLE_LIST,"MP3 Grotuvas", string, "Rinktis", "Atsaukti");
    return 1;
}

Item:OnPlayerUseHouseAudio(playerid, itemid)
{
	new house_index = GetPlayerHouseIndex(playerid);
	if(house_index == -1)
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s neesate namuose.");
	
	if(!IsPlayerHouseOwner(playerid, house_index))
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo ne Jums priklausan�ioje nuosavyb�je.");

	if(IsHouseUpgradeInstalled(house_index, Radio))
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �iame name jau yra �diegta garso sistema.");

	SendClientMessage(playerid, COLOR_WHITE, "Sveikiname s�kmingai instaliavus garso sistem� � savo nam�. Gero klausymosi!");
    GivePlayerItem(playerid, itemid, -1);
    AddHouseUpgrade(house_index, Radio);
    return 1;
}



Item:OnPlayerUseAudioPlayer(playerid, itemid)
{
	new string[64];
	format(string, sizeof(string), "- Radijo stotis\
                        \n- Pad�ti\
                        \n- Paimti");
    ShowPlayerDialog(playerid, 76, DIALOG_STYLE_LIST,"Magas", string, "Rinktis", "Atsaukti");
    return 1;
}

Item:OnPlayerUseBeret(playerid, itemid)
{
	if(!IsPlayerAttachedObjectSlotUsed(playerid, GetAttachedItemSlot(itemid)))
    {
        AddPlayerAttachedItem(playerid, itemid, 2, 0.118000,0.013000,0.002999,-94.299880,8.799993,-98.400047);
        EditAttachedObject(playerid, GetAttachedItemSlot(itemid));
        SendClientMessage(playerid, 0xFFFFFFFF, "Nor�dami pasukti/pakeisti kamer� laikykite klavi�us: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
    }
    else
    {
        SendClientMessage(playerid, COLOR_WHITE,"Daiktas buvo s�kmingai panaikintas/nuimtas.");
        DeletePlayerAttachedItem(playerid, itemid);
    }
    return 1;
}


Item:OnPlayerUseWeapon(playerid, weaponid, invindex)
{
	new string[90],
		amount = GetPlayerItemAmount(playerid, weaponid);
	if(!IsPlayerHaveManyGuns(playerid, weaponid))
    {
       
        string = GetItemName(weaponid);
        format(string, sizeof(string), "S�kmingai i�sitrauk�t� %s, kuris turi %d kulk�(-as).", string, amount);
        SendClientMessage(playerid, GRAD, string);

        RemovePlayerItemAtIndex(playerid, invindex);
    	GivePlayerWeapon(playerid, weaponid, amount);
    }
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
/*
stock GivePlayerItem(playerid, itemid, amount = 1, contentamount = 0, durability = 0, invindex = 0)
{
	CallShoebillFunction("givePlayerItem", playerid, itemid, amount);
	new query[160], freeindex = -1;

	for(new i = invindex; i < MAX_PLAYER_ITEMS; i++)
	{
		if(!PlayerItems[ playerid ][ i ][ Id ])
		{
			if(freeindex == -1)
				freeindex = i;
			continue;
		}

		if((IsItemStackable(itemid) || amount < 0) && PlayerItems[ playerid ][ i ][ ItemId ] == itemid)
		{
			printf("Item is stackalbe or amount is less than 0. Item IDs are equal.They're:%d. Adding at index:%d", itemid, i);
			PlayerItems[ playerid ][ i ][ Amount ] += amount;
			PlayerItems[ playerid ][ i ][ ContentAmount ] += contentamount;
			PlayerItems[ playerid ][ i ][ ContentAmount ] += durability;

			if(PlayerItems[ playerid ][ i ][ Amount ] <= 0)
			{
				RemovePlayerItemAtIndex(playerid, i);
			}
			else
			{
				mysql_format(DbHandle, query, sizeof(query), "UPDATE player_items SET amount = %d, content_amount = %d, durability = %d WHERE id = %d", 
					PlayerItems[ playerid ][ i ][ Amount ],
					PlayerItems[ playerid ][ i ][ ContentAmount ],
					PlayerItems[ playerid ][ i ][ Durability ],
					PlayerItems[ playerid ][ i ][ Id ]);
			}
			mysql_pquery(DbHandle, query);

			return 1;
		}
	}
	// Nebetelpa daiktai
	if(freeindex == -1)
		return 0;

	printf("Giving player new item %d at index:%d", itemid, freeindex);

	PlayerItems[ playerid ][ freeindex ][ ItemId ] = itemid;
	PlayerItems[ playerid ][ freeindex ][ Amount ] = amount;
	PlayerItems[ playerid ][ freeindex ][ ContentAmount ] = contentamount;
	PlayerItems[ playerid ][ freeindex ][ Durability ] = durability;

	mysql_format(DbHandle, query, sizeof(query), "INSERT INTO player_items (player_id, item_id, amount, content_amount, durability, slot) VALUES (%d, %d, %d, %d, %d, %d)",
		GetPlayerSqlId(playerid),
		itemid,
		amount,
		contentamount,
		durability,
		freeindex);
	new Cache:result = mysql_query(DbHandle, query);
	PlayerItems[ playerid ][ freeindex ][ Id ] = cache_insert_id();
	cache_delete(result);
	return 1;
}
*/
stock SetPlayerItem(playerid, invindex, itemid, amount, contentamount = 0, durability = 0)
{
	new newamount, oldamount = GetPlayerItemAmountAtIndex(playerid, invindex);
	if(amount > oldamount)
		newamount = amount - oldamount;
	else if(amount < oldamount)
		newamount = -(amount - oldamount);
	else 
		newamount = amount;

	return GivePlayerItem(playerid, itemid, newamount, contentamount, durability, invindex);
}


stock RemovePlayerItemAtIndex(playerid, invindex)
{
	new query[60];
	mysql_format(DbHandle, query, sizeof(query), "DELETE FROM player_items WHERE id = %d", PlayerItems[ playerid ][ invindex ][ Id ]);

	OnPlayerItemRemoved(playerid, PlayerItems[ playerid ][ invindex ][ ItemId ]);
	PlayerItems[ playerid ][ invindex ][ Id ] = 0;
	PlayerItems[ playerid ][ invindex ][ ItemId ] = 0;
	PlayerItems[ playerid ][ invindex ][ Amount ] = 0;
	PlayerItems[ playerid ][ invindex ][ ContentAmount ] = 0;
	PlayerItems[ playerid ][ invindex ][ Durability ] = 0;

	return mysql_pquery(DbHandle, query);
}

stock IsItemInPlayerInventory(playerid, itemid)
{
	for(new i = 0; i < MAX_PLAYER_ITEMS; i++)
		if(PlayerItems[ playerid ][ i ][ Id ])
			if(PlayerItems[ playerid ][ i ][ ItemId ] == itemid)
				return true;
	return false;
}

stock GetPlayerItemAmount(playerid, itemid)
{
	for(new i = 0; i < MAX_PLAYER_ITEMS; i++)
		if(PlayerItems[ playerid ][ i ][ Id ])
			if(PlayerItems[ playerid ][ i ][ ItemId ] == itemid)
				return PlayerItems[ playerid ][ i ][ Amount ];
	return 0;
}


stock SetPlayerItemDurability(playerid, itemid, value)
{
	new index = GetPlayerItemIndex(playerid, itemid);
	if(index == -1)
		return 0;
	else
	{
		SetPlayerItemDurabilityAtIndex(playerid, index, itemid, value);
	}
	return 0;
}

stock SetPlayerItemDurabilityAtIndex(playerid, index, itemid, value)
{
	new query[80];
	PlayerItems[ playerid ][ index ][ Durability ] = value;
	if(PlayerItems[ playerid ][ index ][ Durability ] <= 0)
		return GivePlayerItem(playerid, itemid, -1);
	else 
	{
		mysql_format(DbHandle, query, sizeof(query), "UPDATE player_items SET durability = %d WHERE id = %d",
			PlayerItems[ playerid ][ index ][ Durability ],
			PlayerItems[ playerid ][ index ][ Id ]);
		mysql_pquery(DbHandle, query);
	}
	return 1;
}

stock AddPlayerItemDurability(playerid, itemid, value)
{
	SetPlayerItemDurability(playerid, itemid, GetPlayerItemDurability(playerid, itemid)+value);
}

stock AddPlayerItemDurabilityAtIndex(playerid, invindex, itemid, value)
{
	SetPlayerItemDurabilityAtIndex(playerid, invindex, itemid, GetPlayerItemDurability(playerid, itemid)+value);
}


stock GetPlayerItemDurability(playerid, itemid)
{
	new index = GetPlayerItemIndex(playerid, itemid);
	if(index == -1)
		return 0;
	else
		return PlayerItems[ playerid ][ index ][ Durability ];
}

stock AddPlayerItemContentAmountIndex(playerid, invindex, itemid, value)
{
	printf("AddPlayerItemContentAmountIndex(%d, %d, %d, %d) GetPlayerItemContentAmount:%d", 
		playerid, invindex, itemid, value, GetPlayerItemContentAmount(playerid, itemid));
	SetPlayerItemContentAmountIndex(playerid, invindex, itemid, PlayerItems[ playerid ][ invindex ][ ContentAmount ] + value);
}

stock AddPlayerItemContentAmount(playerid, itemid, value)
{
	SetPlayerItemContentAmount(playerid, itemid, GetPlayerItemContentAmount(playerid, itemid) + value);
}

stock SetPlayerItemContentAmount(playerid, itemid, value)
{
	new index = GetPlayerItemIndex(playerid, itemid);
	if(index == -1)
		return 0;
	else
	{
		return SetPlayerItemContentAmountIndex(playerid, index, itemid, value);
	}
}

stock SetPlayerItemContentAmountIndex(playerid, invindex, itemid, value)
{
	// Nesuprantu kam �ia tas itemid parametras :S
	new query[100];
	PlayerItems[ playerid ][ invindex ][ ContentAmount ] = value;

	if(GetItemMaxCapacity(itemid)*PlayerItems[ playerid ][ invindex ][ Amount ] != PlayerItems[ playerid ][ invindex ][ ContentAmount ])
	{
		new perteklius = (GetItemMaxCapacity(itemid)*PlayerItems[ playerid ][ invindex ][ Amount ] - PlayerItems[ playerid ][ invindex ][ ContentAmount ]) / GetItemMaxCapacity(itemid);
		printf("Perteklius:%d", perteklius);
		PlayerItems[ playerid ][ invindex ][ Amount ] -= perteklius;
		if(!PlayerItems[ playerid ][ invindex ][ Amount ])
		{
			RemovePlayerItemAtIndex(playerid, invindex);
		}
	}

	mysql_format(DbHandle, query, sizeof(query), "UPDATE player_items SET content_amount = %d, amount = %d WHERE id = %d",
		PlayerItems[ playerid ][ invindex ][ ContentAmount ],
		PlayerItems[ playerid ][ invindex ][ Amount ],
		PlayerItems[ playerid ][ invindex ][ Id ]);
	mysql_pquery(DbHandle, query);
	return 0;
}
stock GetPlayerItemContentAmountIndex(playerid, invindex)
{
	return PlayerItems[ playerid ][ invindex ][ ContentAmount ];
}

stock GetPlayerItemContentAmount(playerid, itemid)
{
	new index = GetPlayerItemIndex(playerid, itemid);
	if(index == -1)
		return 0;
	else
		return PlayerItems[ playerid ][ index ][ ContentAmount ];
}

stock ClearWeaponsFromPlayerInventory(playerid)
{
	for(new i = 0; i < MAX_PLAYER_ITEMS; i++)
	{
		if(PlayerItems[ playerid ][ i ][ Id ] && IsItemWeapon(PlayerItems[ playerid ][ i ][ ItemId ]))
		{
			GivePlayerItem(playerid, PlayerItems[ playerid ][ i ][ ItemId ], -PlayerItems[ playerid ][ i ][ Amount ]);
		}
	}
	return 1;
}

stock RemovePlayerDrugItems(playerid)
{
	for(new i = 0; i < MAX_PLAYER_ITEMS; i++)
	{
		if(PlayerItems[ playerid ][ i ][ Id ] && IsItemDrug(PlayerItems[ playerid ][ i ][ ItemId ]))
		{
			GivePlayerItem(playerid, PlayerItems[ playerid ][ i ][ ItemId ], -PlayerItems[ playerid ][ i ][ Amount ]);
		}
	}
	return 1;
}

// Re-implemented in java
/*
stock IsPlayerInventoryFull(playerid)
{
	if(GetPlayerItemCount(playerid) == MAX_PLAYER_ITEMS)
		return true;
	else
		return false;
}
*/


stock IsPlayerInventoryEmpty(playerid)
{
	if(GetPlayerItemCount(playerid))
		return false;
	else 
		return true;
}

stock GetPlayerItemCount(playerid, itemid = INVALID_ITEM_ID)
{
	new count = 0;
	for(new i = 0; i < MAX_PLAYER_ITEMS; i++)
		if((itemid == INVALID_ITEM_ID && PlayerItems[ playerid ][ i ][ Id ]) || (itemid != INVALID_ITEM_ID && PlayerItems[ playerid ][ i ][ ItemId ] == itemid))
			count++;
	return count;	
}

static stock GetPlayerItemIndex(playerid, itemid)
{
	for(new i = 0; i < MAX_PLAYER_ITEMS; i++)
		if(PlayerItems[ playerid ][ i ][ ItemId ] == itemid)
			return i;
	return -1;
}


stock GetPlayerItemAtIndex(playerid, index)
{
	return PlayerItems[ playerid ][ index ][ ItemId ];
}
stock GetPlayerItemAmountAtIndex(playerid, index)
{
	return PlayerItems[ playerid ][ index ][ Amount ];
} 

stock ShowPlayerInvInfoForPlayer(playerid, targetid)
{
	if(IsPlayerInventoryEmpty(playerid))
		return 0;

	new string[32 + MAX_ITEM_NAME ];
	SendClientMessage(targetid, COLOR_GREEN2, "_____________________ Turimi daiktai __________________");
	for(new i = 0; i < MAX_PLAYER_ITEMS; i++)
	{
		if(PlayerItems[ playerid ][ i ][ Id ])
		{
			format(string, sizeof(string),"%s\tKiekis:%d", GetItemName(PlayerItems[ playerid ][ i ][ ItemId ]),
				PlayerItems[ playerid ][ i ][ Amount ]);
			SendClientMessage(targetid, COLOR_WHITE, string);
		}
	}
	return 1;
}

stock ShowPlayerInventoryDialog(playerid)
{
	new string[ 280 ], 
		itemname[ MAX_ITEM_NAME ];
    for(new i = 0; i < MAX_PLAYER_ITEMS; i++)
    {
        if(!PlayerItems[ playerid ][ i ][ Id ])
            strcat(string, #INVENTORY_EMPTY_SLOT_TEXT);

        else 
        {

        	itemname = GetItemName(PlayerItems[ playerid ][ i ][ ItemId ]);

        	// Jei telefonas rodom ne kiek�, o telefono numer�.
        	if(PlayerItems[ playerid ][ i ][ ItemId ] == ITEM_PHONE)
        		format(string, sizeof(string), "%s%s\t%d", string, itemname, GetPlayerPhoneNumber(playerid, GetPlayerPhoneIndexFromInvIndex(playerid, i)));

        	else if(PlayerItems[ playerid ][ i ][ Amount ] == 1)
        		format(string, sizeof(string), "%s%s", string, itemname);
        	else 
        	{
        		strcat(string, itemname);
        		if(strlen(itemname) < 7)
              		strcat(string, "\t\t");
	            else
	                strcat(string, "\t");

	           	format(string, sizeof(string),"%s [ {66EE00}%d{FFFFFF} ]",
	           		string,
	           		PlayerItems[ playerid ][ i ][ Amount ]);
        	}
        	if(IsItemContainer(PlayerItems[ playerid] [ i ][ ItemId ]))
        	{
        		format(string, sizeof(string), "%s\t%d%%", 
        			string,
        			floatround(100.0 * float(PlayerItems[ playerid ][ i ][ ContentAmount ]) / GetItemMaxCapacity(PlayerItems[ playerid ][ i ][ ItemId ])));
        	}
        	else if(IsItemDegradable(PlayerItems[ playerid ][ i ][ ItemId ]))
        	{
        		format(string, sizeof(string), "%s\t%d%%", 
        			string,
        			floatround(100.0 * float(PlayerItems[ playerid ][ i ][ Durability ]) / GetItemMaxDurability(PlayerItems[ playerid ][ i ][ ItemId ])));
        	}
        	strcat(string, "\n");

        }
    }
    ShowPlayerDialog(playerid, DIALOG_PLAYER_INVENTORY, DIALOG_STYLE_TABLIST, "Inventorius", string, "Naudoti", "I�jungti");
    return 1;
}

stock ShowPlayerInventoryAmountInput(playerid, errostr[] = "")
{
	new string[128];
	format(string, sizeof(string),"{AA0022}%s\n{FFFFFF}�veskite kiek� kur� norite atiduoti.",errostr);
	ShowPlayerDialog(playerid, DIALOG_PLAYER_INVENTORY_AMOUNT, DIALOG_STYLE_INPUT, "Perduodamo daikto kiekis", string, "Naudoti", "I�eiti");
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

CMD:inv(playerid, params[])
{
    if(Mires[ playerid ] > 0)   
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje.");

    if(Mute[ playerid ] == true) 
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �iuo metu Jums yra u�drausta kalb�tis (/mute), nor�dami pa�alinti draudim� susisiekite su Administratoriumi.");

    if(pInfo[ playerid ][ pCuffs ] == 1) 
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite naudotis inventoriumi kada esate surankintas antrankiais..");

    ShowPlayerInventoryDialog(playerid);
    return 1;
}

/*
CMD:invweapon(playerid)
{
    new currgun = GetPlayerWeapon(playerid),
        ammo = GetPlayerAmmo(playerid);

    if(IsPlayerInAnyVehicle(playerid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite d�ti ginklo � inventori� sed�damas tr. priemon�je.");
    if(Mires[ playerid ] > 0)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite d�ti ginklo � inventori�, kada esate mir�s ");
    if(GetPVarInt(playerid, "TAZER_MODE") == 1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite d�ti ginklo � inventori�, jei esate nukratytas tazerio. ");
    if(ammo < 1 || currgun < 1) return true;
        
    CheckWeaponCheat(playerid, currgun, 0);

    if(IsPlayerWeaponJobWeapon(playerid, currgun))
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "�is ginklas yra registruotas frakcijai.");
    if(currgun > 0 && ammo > 0)
    {
    	if(IsPlayerInventoryFull(playerid))
    		return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: j�s� inventoriuje nepakanka vietos, atsilaisvinkite ir bandykite dar kart.");

        GivePlayerItem(playerid, currgun, ammo);
        RemovePlayerWeapon(playerid, currgun);
        SendClientMessage(playerid, COLOR_WHITE, " Ginklas s�kmingai �d�tas � inventori�. ");
        PlayerPlaySound(playerid, 1057, 0.0, 0.0, 0.0);
        return 1;

    }
    return 1;
}
*/