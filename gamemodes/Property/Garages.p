
forward AFunction(thing[], &Float:x, &Float:y, &Float:z);
public AFunction(thing[], &Float:x, &Float:y, &Float:z)
{
    x = 1242.0;
    y = 2334.5;
    z = 75.32;
    printf("ltrp.pwn : AFunction returning %f %f %f", x ,y ,z);
}




/*
        ALTER TABLE garages DROP exit_angle;
        ALTER TABLE garages
            ADD COLUMN vehicle_exit_x FLOAT NOT NULL AFTER exit_z,
            ADD COLUMN vehicle_exit_y FLOAT NOT NULL AFTER vehicle_exit_x,
            ADD COLUMN vehicle_exit_z FLOAT NOT NULL AFTER vehicle_exit_y,
            ADD COLUMN vehicle_exit_angle FLOAT NOT NULL AFTER vehicle_exit_z;
*/

#include <YSI\y_hooks>

#if !defined MAX_GARAGES
	#define MAX_GARAGES 				100
#endif

#if !defined MAX_GARAGE_ITEMS
	#define MAX_GARAGE_ITEMS 			10
#endif


#if !defined MAX_GARAGE_FURNITURE
    #define MAX_GARAGE_FURNITURE        400
#endif

#define MAX_GARAGE_PHONES               3

#define MIN_GARAGE_PRICE                0
#define MAX_GARAGE_PRICE                999999

#define GARAGE_VIRTUAL_WORLD        (40000)

#define GARAGE_LABEL_DRAW_DISTANCE      20.0


#define GarageManagementDialog.          G_M_D_

#define GARAGE_OWNER_NULL               (0)



#define DIALOG_GARAGE_INVENTORY         156

#define DIALOG_GMENU_MAIN               87
#define DIALOG_GMENU_INPUT_INDEX        6467
#define DIALOG_GMENU_NEW_PRICE          90
#define DIALOG_GMENU_DELETE             461


enum E_GARAGE_ITEM_DATA
{
    SqlId,
    ItemId,
    Amount,
    ContentAmount,
    Durability,
};


enum E_GARAGE_DATA
{
    gID,
    gPrice,
    gOwner,
    gInteriorId,            // NE samp interjero ID, o "interiors" lenteles id.
    bool:gLocked,
    Float:gEntrance[ 3 ],
    Float:gVehicleEnter[ 4 ],
    Float:gVehicleExit[ 4 ],
    Float:gExit[ 3 ],
    Text3D:gLabel,
};
new gInfo[ MAX_GARAGES ][ E_GARAGE_DATA ],
	GarageItems[ MAX_GARAGES ][ MAX_GARAGE_ITEMS ][ E_GARAGE_ITEM_DATA ],
	GarageFurniture[ MAX_GARAGES ][ MAX_GARAGE_FURNITURE ][ E_PROPERTY_FURNITURE_DATA ],
    GaragePhones[ MAX_GARAGES ][ MAX_GARAGE_PHONES ][ E_PRIVATE_PHONE_DATA ];

static PlayerUsedGarageIndex[ MAX_PLAYERS ];


new Iterator:Garages<MAX_GARAGES>;


forward OnGarageLoad();


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

public OnGameModeInit()
{
	#if defined garages_OnGameModeInit
		garages_OnGameModeInit();
	#endif

    new query[2048] = "SELECT ";
    strcat(query, "garages.*, ");
    strcat(query, "garage_items.id AS `garage_items.id`, ");
    strcat(query, "garage_items.garage_id AS `garage_items.garage_id`, ");
    strcat(query, "garage_items.item_id AS `garage_items.item_id`, ");
    strcat(query, "garage_items.slot AS `garage_items.slot`, ");
    strcat(query, "garage_items.amount AS `garage_items.amount`, ");
    strcat(query, "garage_items.content_amount AS `garage_items.content_amount`, ");
    strcat(query, "garage_items.durability AS `garage_items.durability`, ");
    strcat(query, "garage_furniture.id AS `garage_furniture.id`, ");
    strcat(query, "garage_furniture.garage_id AS `garage_furniture.garage_id`,");
    strcat(query, "garage_furniture.furniture_id AS `garage_furniture.furniture_id`, ");
    strcat(query, "garage_furniture.name AS `garage_furniture.name`,");
    strcat(query, "garage_furniture.pos_x AS `garage_furniture.pos_x`, ");
    strcat(query, "garage_furniture.pos_y AS `garage_furniture.pos_y`,");
    strcat(query, "garage_furniture.pos_z AS `garage_furniture.pos_z`, ");
    strcat(query, "garage_furniture.rot_x AS `garage_furniture.rot_y`,");
    strcat(query, "garage_furniture.rot_y AS `garage_furniture.rot_x`,");
    strcat(query, "garage_furniture.rot_z AS `garage_furniture.rot_z`,");
    strcat(query, "garage_furniture_textures.furniture_id AS `garage_furniture_textures.furniture_id`, ");
    strcat(query, "garage_furniture_textures.index AS `garage_furniture_textures.index`, ");
    strcat(query, "garage_furniture_textures.object_model AS `garage_furniture_textures.object_model`,");
    strcat(query, "garage_furniture_textures.txd_name AS `garage_furniture_textures.txd_name`, ");
    strcat(query, "garage_furniture_textures.texture_name AS `garage_furniture_textures.texture_name`, ");
    strcat(query, "garage_furniture_textures.color AS `garage_furniture_textures.color`,");
    strcat(query, "furniture.name AS `default_furniture_name`, ");
    strcat(query, "phones.number AS `phones.number`, ");
    strcat(query, "phones.online AS `phones.online` ");
    strcat(query, "FROM `garages`  ");
    strcat(query, "LEFT JOIN garage_items ON garages.id = garage_items.garage_id ");
    strcat(query, "LEFT JOIN garage_furniture ON garages.id = garage_furniture.garage_id ");
    strcat(query, "LEFT JOIN garage_furniture_textures ON garage_furniture.id = garage_furniture_textures.furniture_id ");
    strcat(query, "LEFT JOIN furniture ON garage_furniture.furniture_id = furniture.id ");
    strcat(query, "LEFT JOIN phones ON phones.location_id = garages.id");
    mysql_format(DbHandle, query, sizeof(query),"%s WHERE location_type = %d ", query, _:GarageInventory);
    strcat(query, "ORDER BY garages.id, garage_items.garage_id, garage_furniture.garage_id, garage_furniture_textures.furniture_id");

    mysql_pquery(DbHandle, query, "OnGarageLoad", "");
	return 1;
}
#if defined _ALS_OnGameModeInit
	#undef OnGameModeInit
#else
	#define _ALS_OnGameModeInit
#endif
#define OnGameModeInit garages_OnGameModeInit
#if defined garages_OnGameModeInit
	forward garages_OnGameModeInit();
#endif








public OnGarageLoad()
{
    new garageCount = -1,
        garageItemCount,
        garageFurnitureCount,
        garagePhoneCount,
        lastGarageId = -1,
        lastFurnitureId = -1,
        lastItemId = -1,
        itemid,
        garageid,
        furnituresqlid,
        lastphonenumber,
        tmp[32],
        ticks = GetTickCount()
    ;

    for(new i = 0; i < cache_get_row_count(); i++)
    {
        garageid = cache_get_field_content_int(i, "id");

        // Jei ðito garaþo duomenø dar nekrovëm.
        if(lastGarageId == -1 || garageid != lastGarageId)
        {
            garageItemCount = 0;
            garageFurnitureCount = -1;
            garagePhoneCount = -1;
            garageCount++;

            lastGarageId = garageid;
            gInfo[ garageCount ][ gID ] = garageid;

            cache_get_field_content(i, "owner", tmp);
            gInfo[ garageCount ][ gOwner ] = (ismysqlnull(tmp)) ? (GARAGE_OWNER_NULL) : (strval(tmp));

            gInfo[ garageCount ][ gPrice ] = cache_get_field_content_int(i, "price");

            cache_get_field_content(i, "interior_id", tmp);
            gInfo[ garageCount ][ gInteriorId ] = (ismysqlnull(tmp)) ? (INVALID_INTERIOR_ID) : (strval(tmp));

            gInfo[ garageCount ][ gLocked ] = (cache_get_field_content_int(i, "locked")) ? (true) : (false);

            gInfo[ garageCount ][ gEntrance ][ 0 ] = cache_get_field_content_float(i, "entrance_x");
            gInfo[ garageCount ][ gEntrance ][ 1 ] = cache_get_field_content_float(i, "entrance_y");
            gInfo[ garageCount ][ gEntrance ][ 2 ] = cache_get_field_content_float(i, "entrance_z");

            gInfo[ garageCount ][ gVehicleEnter ][ 0 ] = cache_get_field_content_float(i, "vehicle_entrance_x");
            gInfo[ garageCount ][ gVehicleEnter ][ 1 ] = cache_get_field_content_float(i, "vehicle_entrance_y");
            gInfo[ garageCount ][ gVehicleEnter ][ 2 ] = cache_get_field_content_float(i, "vehicle_entrance_z");
            gInfo[ garageCount ][ gVehicleEnter ][ 3 ] = cache_get_field_content_float(i, "vehicle_entrance_angle");

            gInfo[ garageCount ][ gExit ][ 0 ] = cache_get_field_content_float(i, "exit_x");
            gInfo[ garageCount ][ gExit ][ 1 ] = cache_get_field_content_float(i, "exit_y");
            gInfo[ garageCount ][ gExit ][ 2 ] = cache_get_field_content_float(i, "exit_z");

            gInfo[ garageCount ][ gVehicleExit ][ 0 ] = cache_get_field_content_float(i, "vehicle_exit_x");
            gInfo[ garageCount ][ gVehicleExit ][ 1 ] = cache_get_field_content_float(i, "vehicle_exit_y");
            gInfo[ garageCount ][ gVehicleExit ][ 2 ] = cache_get_field_content_float(i, "vehicle_exit_z");
            gInfo[ garageCount ][ gVehicleExit ][ 3 ] = cache_get_field_content_float(i, "vehicle_exit_angle");

            CreateProperty("garagE", gInfo[ garageCount ][ gID ],
        	gInfo[ garageCount ][ gEntrance ][ 0 ],
        	gInfo[ garageCount ][ gEntrance ][ 0 ],
        	gInfo[ garageCount ][ gEntrance ][ 0 ],
        	0,0,
			gInfo[ garageCount ][ gExit ][ 0 ],
			gInfo[ garageCount ][ gExit ][ 1 ],
			gInfo[ garageCount ][ gExit ][ 2 ],
			GetInteriorInteriorId(gInfo[ garageCount ][ gInteriorId ]),
			GetGarageVirtualWorld(garageCount)
			);

            UpdateGarageEntrance(garageCount);
            Itter_Add(Garages, garageCount);
        }

        // Tvarkomës su namø daiktais
        cache_get_field_content(i, "garage_items.id", tmp);
        itemid = (ismysqlnull(tmp)) ? (0) : (strval(tmp));

        // Jei itemid bus 0, reiðkia to daikto nëra ir tai tik null placeholderis resultset'e.
        if(itemid && (lastItemId == -1 || itemid != lastItemId))
        {
            lastItemId = itemid;
            new slot = cache_get_field_content_int(i, "garage_items.slot");
            if(slot >= 0 && slot < MAX_GARAGE_ITEMS)
            {
                GarageItems[ garageCount ][ slot ][ SqlId ] = itemid;
                GarageItems[ garageCount ][ slot ][ ItemId ] = cache_get_field_content_int(i, "garage_items.item_id");
                GarageItems[ garageCount ][ slot ][ Amount ] = cache_get_field_content_int(i, "garage_items.amount");
                GarageItems[ garageCount ][ slot ][ ContentAmount ] = cache_get_field_content_int(i, "garage_items.content_amount");
                GarageItems[ garageCount ][ slot ][ Durability ] = cache_get_field_content_int(i, "garage_items.durability");
                garageItemCount++;
            }
            else
                printf("Error. Namas %d turi daiktà su netinkamu indeksu %d. Tai galëjo sukelti namø talpos keitimas", gInfo[ garageCount ][ gID ], slot);

        }


        // Pradedam tvarkytis su namø baldais...
        cache_get_field_content(i, "garage_furniture.id", tmp);
        if((furnituresqlid = strval(tmp)) && (lastFurnitureId == -1 || lastFurnitureId != furnituresqlid))
        {
            garageFurnitureCount++;
            lastFurnitureId = furnituresqlid;

            GarageFurniture[ garageCount ][ garageFurnitureCount ][ SqlId ] = furnituresqlid;
            GarageFurniture[ garageCount ][ garageFurnitureCount ][ FurnitureId ] = cache_get_field_content_int(i, "garage_furniture.furniture_id");
            cache_get_field_content(i, "garage_furniture.name", GarageFurniture[ garageCount ][ garageFurnitureCount ][ Name ], DbHandle, MAX_FURNITURE_NAME);

            new furnitureIndex = GetFurnitureIndex(GarageFurniture[ garageCount ][ garageFurnitureCount ][ FurnitureId ]);
            if(furnitureIndex != -1)
            {
                if(GarageFurniture[ garageCount ][ garageFurnitureCount ][ ObjectId ])
                    ErrorLog("Over writing garage furniture object.");
                GarageFurniture[ garageCount ][ garageFurnitureCount ][ ObjectId ] = CreateDynamicObject(
                    GetFurnitureObjectId(furnitureIndex),
                    cache_get_field_content_float(i, "garage_furniture.pos_x"),
                    cache_get_field_content_float(i, "garage_furniture.pos_y"),
                    cache_get_field_content_float(i, "garage_furniture.pos_z"),
                    cache_get_field_content_float(i, "garage_furniture.rot_x"),
                    cache_get_field_content_float(i, "garage_furniture.rot_y"),
                    cache_get_field_content_float(i, "garage_furniture.rot_z"),
                    .worldid=GetGarageVirtualWorld(garageCount)
                );
            }
            else
                printf("Error. Baldo ID %d nerastas atmintyje. Patikrinkite ar baldai kraunami ankðèiau nei garaþai.", GarageFurniture[ garageCount ][ garageFurnitureCount ][ FurnitureId ]);
        }

        // Ir galiausiai sutvarkom tekstûras...
        cache_get_field_content(i, "garage_furniture_textures.furniture_id", tmp);
        if(!ismysqlnull(tmp))
        {
            new index, object, txd[ MAX_TXD_FILE_NAME ], texture[ MAX_TEXTURE_NAME ], color;
            index = cache_get_field_content_int(i, "garage_furniture_textures.index");
            object = cache_get_field_content_int(i, "garage_furniture_textures.object_model");
            cache_get_field_content(i, "garage_furniture_textures.txd_name", txd);
            cache_get_field_content(i, "garage_furniture_textures.texture_name", texture);
            color = cache_get_field_content_int(i, "garage_furniture_textures.color");
            SetDynamicObjectMaterial(GarageFurniture[ garageCount ][ garageFurnitureCount ][ ObjectId ], index, object, txd, texture, color);
        }

        // Telefonai.
        cache_get_field_content(i, "phones.number", tmp);
        // Jei ne null ir tokio telefono dar nekrovëm.
        if(!ismysqlnull(tmp) && (lastphonenumber != strval(tmp) || !lastphonenumber))
        {
            lastphonenumber = strval(tmp);
            garagePhoneCount++;
            GaragePhones[ garageCount ][ garagePhoneCount ][ Number ] = lastphonenumber;
            GaragePhones[ garageCount ][ garagePhoneCount ][ Online ] = (cache_get_field_content_int(i, "online")) ? (true) : (false);
        }
    }
    printf("Serveryje yra sukurti %d garazai. Ju krovimas uztruko %d MS",garageCount+1, GetTickCount() - ticks);
    return 1;
}




hook OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
    GarageManagementDialog.OnDialogResponse(playerid, dialogid, response, listitem, inputtext);
    /*switch(dialogid)
    {
        case DIALOG_GARAGE_INVENTORY:
        {
            if(!response)
                return 1;

            // Paskutinis pasirinkimas yra lentelës uþdarymas
            if(listitem == MAX_GARAGE_ITEMS)
                return 1;

            if(!GarageItems[ PlayerUsedGarageIndex[ playerid ] ][ listitem ][ SqlId ])
                return 1;

            new string[ 80 ],
                itemid = GarageItems[ PlayerUsedGarageIndex[ playerid ] ][ listitem ][ ItemId ],
                amount = GarageItems[ PlayerUsedGarageIndex[ playerid ] ][ listitem ][ Amount ],
                contentamount = GarageItems[ PlayerUsedGarageIndex[ playerid ] ][ listitem ][ ContentAmount ],
                durability = GarageItems[ PlayerUsedGarageIndex[ playerid ] ][ listitem ][ Durability]
            ;

            if(itemid < 50 && !IsPlayerHaveManyGuns(playerid, itemid))
            {
                if(itemid > 21)
                {
                    if(pInfo[ playerid ][ pLevel ] < 2)
                        return SendClientMessage(playerid, COLOR_RED, "Klaida, Jûs privalote bûti pasiekæs 2 lygá, kad naudotumëtës ðia galimybæ.");
                }
                GunLog(GetPlayerSqlId(playerid), 8, gInfo[ PlayerUsedGarageIndex[ playerid ] ][ gOwner ], GetItemName(itemid), amount);
                GivePlayerWeapon(playerid, itemid, amount);
                RemoveGarageItem(PlayerUsedGarageIndex[ playerid ], listitem);
            }
            else if(itemid > 50)
            {
                if(IsItemDrug(itemid))
                    NarkLog(GetPlayerSqlId(playerid), 8, gInfo[ PlayerUsedGarageIndex[ playerid ] ][ gOwner ], GetItemName(itemid), amount);

                if(IsPlayerInventoryFull(playerid))
                    return SendClientMessage(playerid, COLOR_FADE2, "{FF6347}Klaida, bet Jûsø inventoriuje nepakanka laisvos vietos ðiam daiktui.");

                GivePlayerItem(playerid, itemid, amount, contentamount, durability);
                RemoveGarageItem(PlayerUsedGarageIndex[ playerid ], listitem);
            }
            format(string, sizeof(string), "* %s pasiemà daiktà ið spintelës, kuris atrodo kaip %s ", GetPlayerNameEx(playerid), GetItemName(itemid));
            ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
            return 1;
        }
    }
    */
    return 0;
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


UpdateGarageEntrance(garageindex)
{
    new string[ 140 ];
    if(IsGarageOwned(garageindex))
        format(string, sizeof(string), "{FFBB00}Garaþas\nNorëdami áeiti raðykite /enter");
    else
        format(string, sizeof(string), "{FFFFFF}Ðis garaþas yra parduodamas\nPardavimo kainà: {FFBB00}%d\n{FFFFFF}Norëdami pirkti raðykite {FFBB00}/buygarage", gInfo[ garageindex ][ gPrice ]);

    if(IsValidDynamic3DTextLabel(gInfo[ garageindex ][ gLabel ]))
        DestroyDynamic3DTextLabel(gInfo[ garageindex ][ gLabel ]);

    gInfo[ garageindex ][ gLabel ] = CreateDynamic3DTextLabel(string, COLOR_WHITE, gInfo[ garageindex ][ gEntrance ][ 0 ], gInfo[ garageindex ][ gEntrance ][ 1 ], gInfo[ garageindex ][ gEntrance ][ 2 ],
        GARAGE_LABEL_DRAW_DISTANCE, .testlos = 0, .worldid = 0, .interiorid = 0, .streamdistance = 20.0);
    return 1;
}

stock ShowGarageInv(playerid, garageindex)
{
    new string[ 512 ];
    for(new slot = 0; slot < MAX_GARAGE_ITEMS; slot++)
    {
        if(!GarageItems[ garageindex ][ slot ][ SqlId ])
            format(string, sizeof(string), "%s%d\tNëra\n", string,slot+1);
        // Su telefonais kiek kitaip formatuojam
        else if(GarageItems[ garageindex ][ slot ][ ItemId ] == ITEM_PHONE)
            format(string, sizeof(string), "%s%d. %s\t%d\n", string, slot+1, GetItemName(GarageItems[ garageindex ][ slot ][ ItemId ]), GetPlayerPhoneNumber(playerid, GetPlayerPhoneIndexFromInvIndex(playerid, slot)));
        else
            format(string, sizeof(string), "%s%d. %s\t%d\n", string, slot+1, GetItemName(GarageItems[ garageindex ][ slot ][ ItemId ]) ,GarageItems[ garageindex ][ slot ][ Amount ]);
    }
    strcat(string, "\nIðjungti");
    PlayerUsedGarageIndex[ playerid ] = garageindex;
    ShowPlayerDialog(playerid, DIALOG_GARAGE_INVENTORY, DIALOG_STYLE_TABLIST, "Inventorius", string, "Paimti", "Atgal");
    return 1;
}

stock GetPlayerGarageIndex(playerid)
{
    foreach(new i : Garages)
    {
        if(IsPlayerInGarage(playerid, i))
            return i;

        if(IsPlayerInRangeOfPoint(playerid, 5.0, gInfo[ i ][ gEntrance ][ 0 ], gInfo[ i ][ gEntrance ][ 1 ], gInfo[ i ][ gEntrance ][ 2 ])
            && GetPlayerVirtualWorld(playerid) == GetGarageEntranceVirtualWorld(i))
            return i;

        if(IsPlayerInAnyVehicle(playerid))
        {
            new vehicleid = GetPlayerVehicleID(playerid);
            new Float:distance = GetVehicleDistanceFromPoint(vehicleid, gInfo[ i ][ gVehicleEnter ][ 0 ], gInfo[ i ][ gVehicleEnter ][ 1 ], gInfo[ i ][ gVehicleEnter ][ 2 ]);
            if(distance <= 5.0 && GetVehicleVirtualWorld(vehicleid) == GetGarageEntranceVirtualWorld(i))
                return i;
        }
    }
    return -1;
}
stock IsPlayerInGarage(playerid, garageindex)
{
    if(IsPlayerInInterior(playerid, gInfo[ garageindex ][ gInteriorId ])
        && GetPlayerVirtualWorld(playerid) == GetGarageVirtualWorld(garageindex))
        return true;
    else
        return false;
}

stock IsGarageOwned(garageindex)
{
    if(gInfo[ garageindex ][ gOwner ] == GARAGE_OWNER_NULL)
        return false;
    else
        return true;
}

stock IsPlayerGarageOwner(playerid, garageindex)
{
    if(gInfo[ garageindex ][ gOwner ] == GetPlayerSqlId(playerid))
        return true;
    else
        return false;
}

stock IsValidGarage(garageindex)
{
    if(garageindex >= 0 && garageindex < MAX_GARAGES && gInfo[ garageindex ][ gID ])
        return true;
    else
        return false;
}

stock GetGarageFurnitureCount(garageindex)
{
    new count = 0;
    for(new i = 0; i < MAX_GARAGE_FURNITURE; i++)
        if(GarageFurniture[ garageindex ][ i ][ SqlId ])
            count++;
    return count;
}



stock IsAnyGarageFurnitureObject(objectid)
{
    foreach(new i : Garages)
        for(new j = 0; j < MAX_GARAGE_FURNITURE; j++)
            if(GarageFurniture[ i ][ j ][ SqlId ] && GarageFurniture[ i ][ j ][ ObjectId ] == objectid)
                return true;
    return false;
}

stock IsPlayerInAnyGarage(playerid)
{
    foreach(new i : Garages)
        if(IsPlayerInGarage(playerid, i))
            return true;
    return false;
}

stock IsPlayerInRangeOfGarageExit(playerid, garageindex, Float:distance)
{
    return IsPlayerInRangeOfPoint(playerid, distance, gInfo[ garageindex ][ gExit ][ 0 ], gInfo[ garageindex ][ gExit ][ 1 ], gInfo[ garageindex ][ gExit ][ 2 ]);
}

stock IsGarageLocked(garageindex)
{
    return gInfo[ garageindex ][ gLocked ];
}

stock GetGarageOwner(garageindex)
{
    return gInfo[ garageindex ][ gOwner ];
}

stock GetGarageInteriorID(garageindex)
{
    return gInfo[ garageindex ][ gInteriorId ];
}

stock GetGarageID(garageindex)
{
    return gInfo[ garageindex ][ gID ];
}

forward GetGarageIndex(garageid);
public GetGarageIndex(garageid)
{
    foreach(new i : Garages)
    {
        if(gInfo[ i ][ gID ] == garageid)
            return i;
    }
    return -1;
}

stock GetGarageFurnitureIndex(garageindex, objectid)
{
    for(new i = 0; i < MAX_GARAGE_FURNITURE; i++)
        if(GarageFurniture[ garageindex ][ i ][ ObjectId ] == objectid)
            return i;
    return false;
}


stock GetGarageFurniturePrice(garageindex, furnitureindex)
{
    return GetFurniturePrice(GetFurnitureIndex(GarageFurniture[ garageindex ][ furnitureindex ][ FurnitureId ]));
}

stock GetGarageFurnitureId(garageindex, furnitureindex)
{
    return GarageFurniture[ garageindex ][ furnitureindex ][ FurnitureId ];
}

stock GetGarageFurnitureName(garageindex, furnitureindex)
{
    new s[ MAX_FURNITURE_NAME ];
    strcat(s, GarageFurniture[ garageindex ][ furnitureindex ][ Name ]);
    return s;
}

stock GetGarageFurnitureObjectId(garageindex, furnitureindex)
{
    return GarageFurniture[ garageindex ][ furnitureindex ][ ObjectId ];
}

stock IsPlayerInRangeOfGarageFurnitur(playerid, garageindex, furnitureindex, Float:distance)
{
    new Float:x, Float:y, Float:z;
    GetDynamicObjectPos(GarageFurniture[ garageindex ][ furnitureindex ][ ObjectId ], x ,y, z);
    return IsPlayerInRangeOfPoint(playerid, distance, x, y, z);
}

stock GetGarageVirtualWorld(garageindex)
{
    return gInfo[ garageindex ][ gID ] + GARAGE_VIRTUAL_WORLD;
}

stock GetGarageExitPos(garageindex, &Float:x, &Float:y, &Float:z)
{
    x = gInfo[ garageindex ][ gExit ][ 0 ];
    y = gInfo[ garageindex ][ gExit ][ 1 ];
    z = gInfo[ garageindex ][ gExit ][ 2 ];
}

stock GetGarageVehicleExitPos(garageindex, &Float:x, &Float:y, &Float:z, &Float:a)
{
    x = gInfo[ garageindex ][ gVehicleExit ][ 0 ];
    y = gInfo[ garageindex ][ gVehicleExit ][ 1 ];
    z = gInfo[ garageindex ][ gVehicleExit ][ 2 ];
    a = gInfo[ garageindex ][ gVehicleExit ][ 3 ];
}

stock GetGarageVehicleEntrancePos(garageindex, &Float:x, &Float:y, &Float:z, &Float:a)
{
    x = gInfo[ garageindex ][ gVehicleEnter ][ 0 ];
    y = gInfo[ garageindex ][ gVehicleEnter ][ 1 ];
    z = gInfo[ garageindex ][ gVehicleEnter ][ 2 ];
    a = gInfo[ garageindex ][ gVehicleEnter ][ 3 ];
}

forward GetGarageEntrancePos(garageindex, &Float:x, &Float:y, &Float:z);
public GetGarageEntrancePos(garageindex, &Float:x, &Float:y, &Float:z)
{
    x = gInfo[ garageindex ][ gEntrance ][ 0 ];
    y = gInfo[ garageindex ][ gEntrance ][ 1 ];
    z = gInfo[ garageindex ][ gEntrance ][ 2 ];
}
forward GetGarageEntranceVirtualWorld(garageindex);
public GetGarageEntranceVirtualWorld(garageindex)
{
    #pragma unused garageindex
    return 0;
}
forward GetGarageEntranceInteriorID(garageindex);
public GetGarageEntranceInteriorID(garageindex)
{
    #pragma unused garageindex
    return 0;
}

stock ToggleGarageLock(garageindex)
{
    new query[80];
    gInfo[ garageindex ][ gLocked ] = !gInfo[ garageindex ][ gLocked ];
    mysql_format(DbHandle, query, sizeof(query), "UPDATE garages SET locked = %d WHERE id = %d", gInfo[ garageindex ][ gLocked ], gInfo[ garageindex ][ gID ]);
    return mysql_pquery(DbHandle, query);
}

stock GetGarageFreeItemSlot(garageindex)
{
    for(new i = 0; i < MAX_GARAGE_FURNITURE; i++)
        if(!GarageItems[ garageindex ][ i ][ SqlId ])
            return i;
    return -1;
}

GetGaragePhonenumberGarageIndex(phonenumber)
{
    foreach(new i : Garages)
        for(new j = 0; j < MAX_GARAGE_PHONES; j++)
            if(GaragePhones[ i ][ j ][ Number ] == phonenumber)
                return i;
    return -1;
}

IsPhoneInAnyGarage(phonenumber)
{
    foreach(new i : Garages)
        for(new j = 0; j < MAX_GARAGE_PHONES; j++)
            if(GaragePhones[ i ][ j ][ Number ] == phonenumber)
                return true;
    return false;
}

IsGaragePhonenumberOnline(phonenumber)
{
    foreach(new i : Garages)
        for(new j = 0; j < MAX_GARAGE_PHONES; j++)
            if(GaragePhones[ i ][ j ][ Number ] == phonenumber && GaragePhones[ i ][ j ][ Online ])
                return true;
    return false;
}



/*
                                                                                                                                                                       ,,
    `7MMF'`7MN.   `7MF'.M"""bgd `7MM"""YMM  `7MM"""Mq. MMP""MM""YMM     `7MMF'`7MN.   `7MF'MMP""MM""YMM   .g8""8q.                                                   `7MM
      MM    MMN.    M ,MI    "Y   MM    `7    MM   `MM.P'   MM   `7       MM    MMN.    M  P'   MM   `7 .dP'    `YM.                                                   MM
      MM    M YMb   M `MMb.       MM   d      MM   ,M9      MM            MM    M YMb   M       MM      dM'      `MM     `7MMpMMMb.pMMMb.`7M'   `MF',pP"Ybd  ,dW"Yvd   MM
      MM    M  `MN. M   `YMMNq.   MMmmMM      MMmmdM9       MM            MM    M  `MN. M       MM      MM        MM       MM    MM    MM  VA   ,V  8I   `" ,W'   MM   MM
      MM    M   `MM.M .     `MM   MM   Y  ,   MM  YM.       MM            MM    M   `MM.M       MM      MM.      ,MP       MM    MM    MM   VA ,V   `YMMMa. 8M    MM   MM
      MM    M     YMM Mb     dM   MM     ,M   MM   `Mb.     MM            MM    M     YMM       MM      `Mb.    ,dP'       MM    MM    MM    VVV    L.   I8 YA.   MM   MM
    .JMML..JML.    YM P"Ybmmd"  .JMMmmmmMMM .JMML. .JMM.  .JMML.        .JMML..JML.    YM     .JMML.      `"bmmd"'       .JMML  JMML  JMML.  ,V     M9mmmP'  `MbmdMM .JMML.
                                                                                                                                            ,V                    MM
                                                                                                                                         OOb"                   .JMML.      */

stock AddGarage(Float:x, Float:y, Float:z, Float:angle)
{
    new index = Itter_Free(Garages), query[256];
    if(index == -1)
        return -1;

    format(query, sizeof(query), "INSERT INTO garages (entrance_x, entrance_y, entrance_z, vehicle_entrance_x, vehicle_entrance_y, vehicle_entrance_z, vehicle_entrance_angle) VALUES (%f, %f, %f, %f, %f, %f, %f)",
        x, y, z, x, y, z, angle);
    new Cache:result = mysql_query(DbHandle, query);

    gInfo[ index ][ gID ] = cache_insert_id();
    cache_delete(result);

    gInfo[ index ][ gEntrance ][ 0 ] = x;
    gInfo[ index ][ gEntrance ][ 1 ] = y;
    gInfo[ index ][ gEntrance ][ 2 ] = z;
    gInfo[ index ][ gVehicleEnter ][ 0 ] = x;
    gInfo[ index ][ gVehicleEnter ][ 1 ] = y;
    gInfo[ index ][ gVehicleEnter ][ 2 ] = z;
    UpdateGarageEntrance(index);

    Itter_Add(Garages, index);
    return index;
}



stock AddGarageFurniture(garageindex, findex, Float:posx, Float:posy, Float:posz, Float:rotx = 0.0, Float:roty = 0.0 , Float:rotz = 0.0)
{
    new
        query[256],
        i;

    mysql_format(DbHandle, query, sizeof(query), "INSERT INTO garage_furniture (garage_id, furniture_id, pos_x, pos_y, pos_z, rot_x, rot_y, rot_z) VALUES (%d, %d, %f, %f, %f, %f, %f, %f)",
        gInfo[ garageindex ][ gID ], GetFurnitureID(findex), posx, posy, posz, rotx, roty, rotz);
    new Cache:result = mysql_query(DbHandle, query);

    for(new j = 0; j < MAX_GARAGE_FURNITURE; j++)
        if(!GarageFurniture[ garageindex ][ j ][ SqlId ])
        {
            i = j;
            break;
        }
    GarageFurniture[ garageindex ][ i ][ SqlId ] = cache_insert_id();
    cache_delete(result);
    GarageFurniture[ garageindex ][ i ][ FurnitureId ] = GetFurnitureID(findex);
    GarageFurniture[ garageindex ][ i ][ ObjectId ] = CreateDynamicObject(GetFurnitureObjectId(findex),
        posx, posy, posz,
        rotx, roty, rotz,
        GetGarageVirtualWorld(garageindex),
        .streamdistance = 35.0);
    format(GarageFurniture[ garageindex ][ i ][ Name ], MAX_FURNITURE_NAME, GetFurnitureName(findex));


    return GarageFurniture[ garageindex ][ i ][ ObjectId ];
}



/*

                                                                                                                                        ,,
                `7MMF'   `7MF'`7MM"""Mq.`7MM"""Yb.      db   MMP""MM""YMM `7MM"""YMM                                                  `7MM
                  MM       M    MM   `MM. MM    `Yb.   ;MM:  P'   MM   `7   MM    `7                                                    MM
                  MM       M    MM   ,M9  MM     `Mb  ,V^MM.      MM        MM   d        `7MMpMMMb.pMMMb.`7M'   `MF',pP"Ybd  ,dW"Yvd   MM
                  MM       M    MMmmdM9   MM      MM ,M  `MM      MM        MMmmMM          MM    MM    MM  VA   ,V  8I   `" ,W'   MM   MM
                  MM       M    MM        MM     ,MP AbmmmqMA     MM        MM   Y  ,       MM    MM    MM   VA ,V   `YMMMa. 8M    MM   MM
                  YM.     ,M    MM        MM    ,dP'A'     VML    MM        MM     ,M       MM    MM    MM    VVV    L.   I8 YA.   MM   MM
                   `bmmmmd"'  .JMML.    .JMMmmmdP'.AMA.   .AMMA..JMML.    .JMMmmmmMMM     .JMML  JMML  JMML.  ,V     M9mmmP'  `MbmdMM .JMML.
                                                                                                             ,V                    MM
                                                                                                          OOb"                   .JMML.
*/



stock SaveGarage(garageindex)
{
    new query[ 600 ], owner[16], interior[16];

    if(gInfo[ garageindex ][ gOwner ] == GARAGE_OWNER_NULL)
        owner = "NULL";
    else
        format(owner, sizeof(owner), "\'%d\'", gInfo[ garageindex ][ gOwner ]);

    if(gInfo[ garageindex ][ gInteriorId ] == INVALID_INTERIOR_ID)
        interior = "NULL";
    else
        format(interior, sizeof(interior), "\'%d\'", gInfo[ garageindex ][ gInteriorId ]);


    mysql_format(DbHandle, query, sizeof(query), "UPDATE garages SET  price = %d, owner = %s, interior_id = %s, locked = %d, entrance_x = %f, entrance_y = %f, entrance_z = %f, \
        vehicle_entrance_x = %f, vehicle_entrance_y = %f, vehicle_entrance_z = %f, vehicle_entrance_angle = %f, exit_x = %f, exit_y = %f, exit_z = %f, vehicle_exit_x = %f, \
        vehicle_exit_y = %f, vehicle_exit_z = %f, vehicle_exit_angle = %f WHERE `id` = %d",
        gInfo[ garageindex ][ gPrice ],
        owner,
        interior,
        gInfo[ garageindex ][ gLocked  ],
        gInfo[ garageindex ][ gEntrance ][ 0 ],
        gInfo[ garageindex ][ gEntrance ][ 1 ],
        gInfo[ garageindex ][ gEntrance ][ 2 ],
        gInfo[ garageindex ][ gVehicleEnter ][ 0 ],
        gInfo[ garageindex ][ gVehicleEnter ][ 1 ],
        gInfo[ garageindex ][ gVehicleEnter ][ 2 ],
        gInfo[ garageindex ][ gVehicleEnter ][ 3 ],
        gInfo[ garageindex ][ gExit ][ 0 ],
        gInfo[ garageindex ][ gExit ][ 1 ],
        gInfo[ garageindex ][ gExit ][ 2 ],
        gInfo[ garageindex ][ gVehicleExit ][ 0 ],
        gInfo[ garageindex ][ gVehicleExit ][ 1 ],
        gInfo[ garageindex ][ gVehicleExit ][ 2 ],
        gInfo[ garageindex ][ gVehicleExit ][ 3 ],
        gInfo[ garageindex ][ gID ] );

    mysql_pquery(DbHandle,  query);
    return 1;
}

IsGarageVehicleEntrancePosSet(garageindex)
{
    if(gInfo[ garageindex ][ gVehicleEnter ][ 0 ] == 0.0 && gInfo[ garageindex ][ gVehicleEnter ][ 1 ] == 0.0 && gInfo[ garageindex ][ gVehicleEnter ][ 2 ] == 0.0 && gInfo[ garageindex ][ gVehicleEnter ][ 3 ] == 0.0)
        return false;
    else
        return true;
}

IsGarageVehicleExitPosSet(garageindex)
{
    if(gInfo[ garageindex ][ gVehicleExit ][ 0 ] == 0.0 && gInfo[ garageindex ][ gVehicleExit ][ 1 ] == 0.0 && gInfo[ garageindex ][ gVehicleExit ][ 2 ] == 0.0 && gInfo[ garageindex ][ gVehicleExit ][ 3 ] == 0.0)
        return false;
    else
        return true;
}

stock SetGarageEntrancePos(garageindex, Float:x, Float:y, Float:z)
{
    new query[140];
    mysql_format(DbHandle, query, sizeof(query), "UPDATE garages SET entrance_x = %f, entrance_y = %f, entrance_z = %f WHERE id = %d",
        x, y, z, gInfo[ garageindex ][ gID ]);
    mysql_pquery(DbHandle, query);

    gInfo[ garageindex ][ gEntrance ][ 0 ] = x;
    gInfo[ garageindex ][ gEntrance ][ 1 ] = y;
    gInfo[ garageindex ][ gEntrance ][ 2 ] = z;
    if(!IsGarageVehicleEntrancePosSet(garageindex))
    {
        gInfo[ garageindex ][ gVehicleEnter ][ 0 ] = x;
        gInfo[ garageindex ][ gVehicleEnter ][ 1 ] = y;
        gInfo[ garageindex ][ gVehicleEnter ][ 2 ] = z+1.0;
    }
    UpdateGarageEntrance(garageindex);
    return 1;
}
stock SetGarageExitLocation(garageindex, interiorid, Float:x, Float:y, Float:z)
{
    // interiorid parametras èia yra NE GTA SA interjero ID, o interjero SQL ID(interiors.p).
    new query[120];

    gInfo[ garageindex][ gInteriorId ] = interiorid;
    gInfo[ garageindex][ gExit ][ 0 ] = x;
    gInfo[ garageindex][ gExit ][ 1 ] = y;
    gInfo[ garageindex][ gExit ][ 2 ] = z;
    if(!IsGarageVehicleExitPosSet(garageindex))
    {
        gInfo[ garageindex][ gVehicleExit ][ 0 ] = x;
        gInfo[ garageindex][ gVehicleExit ][ 1 ] = y;
        gInfo[ garageindex][ gVehicleExit ][ 2 ] = z+1.0;
        SaveGarage(garageindex);
    }
    else
    {
        mysql_format(DbHandle, query, sizeof(query), "UPDATE garages SET interior_id = %d, exit_x = %f, exit_y = %f, exit_z = %f WHERE id = %d",
        interiorid, x, y, z, gInfo[ garageindex ][ gID ]);
        mysql_pquery(DbHandle, query);
    }
    return 1;
}

stock SetGarageVehicleEntrance(garageindex, Float:x, Float:y, Float:z, Float:angle)
{
    new query[220];

    gInfo[ garageindex ][ gVehicleEnter ][ 0 ] = x;
    gInfo[ garageindex ][ gVehicleEnter ][ 1 ] = y;
    gInfo[ garageindex ][ gVehicleEnter ][ 2 ] = z;
    gInfo[ garageindex ][ gVehicleEnter ][ 3 ] = angle;

    mysql_format(DbHandle, query, sizeof(query), "UPDATE garages SET vehicle_entrance_x = %f, vehicle_entrance_y = %f, vehicle_entrance_z = %f, vehicle_entrance_angle = %f WHERE id = %d",
        x, y, z, angle, gInfo[ garageindex ][ gID ]);
    return mysql_pquery(DbHandle, query);
}

stock SetGarageVehicleExit(garageindex, Float:x, Float:y, Float:z, Float:angle)
{
    new query[220];

    gInfo[ garageindex ][ gVehicleExit ][ 0 ] = x;
    gInfo[ garageindex ][ gVehicleExit ][ 1 ] = y;
    gInfo[ garageindex ][ gVehicleExit ][ 2 ] = z;
    gInfo[ garageindex ][ gVehicleExit ][ 3 ] = angle;

    mysql_format(DbHandle, query, sizeof(query), "UPDATE garages SET vehicle_exit_x = %f, vehicle_exit_y = %f, vehicle_exit_z = %f, vehicle_exit_angle = %f WHERE id = %d",
        x, y, z, angle, gInfo[ garageindex ][ gID ]);
    return mysql_pquery(DbHandle, query);
}


stock SetGarageFurnitureName(garageindex, furnitureindex, name[])
{
    new query[100];

    format(GarageFurniture[ garageindex ][ furnitureindex ][ Name ], MAX_FURNITURE_NAME, name);
    mysql_format(DbHandle, query, sizeof(query), "UPDATE garage_furniture SET name = '%e' WHERE id = %d",
        name, GarageFurniture[ garageindex ][ furnitureindex ][ SqlId ]);
    return mysql_pquery(DbHandle, query);
}

stock SetGarageFurnitureTexture(garageindex, furnitureindex, materialindex, modelid, txdname[], texturename[])
{
    new query[512], color, placeholder;

    GetDynamicObjectMaterial(GarageFurniture[ garageindex ][ furnitureindex ][ ObjectId ], materialindex, placeholder, query, query, color);
    SetDynamicObjectMaterial(GarageFurniture[ garageindex ][ furnitureindex ][ ObjectId ], materialindex, modelid, txdname, texturename, color);

    mysql_format(DbHandle, query, sizeof(query), "INSERT INTO garage_furniture_textures (furniture_id, `index`, object_model, txd_name, texture_name) VALUES \
        (%d, %d, %d, '%e', '%e') ON DUPLICATE KEY UPDATE object_model = VALUES(object_model), txd_name = VALUES(txd_name), texture_name = VALUES(texture_name)",
        GarageFurniture[ garageindex ][ furnitureindex ][ SqlId ], materialindex, modelid, txdname, texturename);
    return mysql_pquery(DbHandle, query);
}

stock SetGarageFurnitureTextureColor(garageindex, furnitureindex, materialindex, color)
{
    new query[170], object, txd[ MAX_TXD_FILE_NAME ], texture[ MAX_TEXTURE_NAME ], placeholder;

    GetDynamicObjectMaterial(GarageFurniture[ garageindex ][ furnitureindex ][ ObjectId ], materialindex, object, txd, texture, placeholder);
    SetDynamicObjectMaterial(GarageFurniture[ garageindex ][ furnitureindex ][ ObjectId ], materialindex, object, txd, texture, color);

    mysql_format(DbHandle, query, sizeof(query), "INSERT INTO garage_furniture_textures (furniture_id, `index`, color) VALUES (%d, %d, %d) ON DUPLICATE KEY UPDATE color = VALUES(color)",
        GarageFurniture[ garageindex ][ furnitureindex ][ SqlId ], materialindex, color);
    return mysql_pquery(DbHandle, query);
}


stock SaveGarageFurnitureObject(garageindex, furnitureindex, Float:x, Float:y, Float:z, Float:rx, Float:ry, Float:rz)
{
    new query[200];

    SetDynamicObjectPos(GarageFurniture[ garageindex ][ furnitureindex ][ ObjectId ], x, y, z);
    SetDynamicObjectRot(GarageFurniture[ garageindex ][ furnitureindex ][ ObjectId ], rx, ry, rz);

    mysql_format(DbHandle, query, sizeof(query), "UPDATE garage_furniture SET pos_x = %f, pos_y = %f, pos_z = %f, rot_x = %f, rot_y = %f, rot_z = %f WHERE id = %d",
        x, y, z, rx, ry, rz, GarageFurniture[ garageindex ][ furnitureindex ][ SqlId ]);
    return mysql_pquery(DbHandle, query);
}


stock SetGarageItem(garageindex, slot, itemid, amount, contentamount, durability)
{
    new query[180];

    GarageItems[ garageindex ][ slot ][ ItemId ] = itemid;
    GarageItems[ garageindex ][ slot ][ Amount ] = amount;
    GarageItems[ garageindex ][ slot ][ ContentAmount ] = contentamount;
    GarageItems[ garageindex ][ slot ][ Durability ] = durability;

    if(GarageItems[ garageindex ][ slot ][ SqlId ])
    {
        mysql_format(DbHandle, query, sizeof(query), "UPDATE garage_items SET item_id = %d, amount = %d, content_amount = %d, durability = %d WHERE id = %d",
            itemid, amount, contentamount, durability, GarageItems[ garageindex ][ slot ][ SqlId ]);
        return mysql_pquery(DbHandle, query);
    }
    else
    {
        mysql_format(DbHandle, query, sizeof(query),"INSERT INTO garage_items (garage_id, item_id, slot, amount, content_amount, durability) VALUES(%d, %d, %d, %d, %d, %d)",
            gInfo[ garageindex ][ gID ], itemid, slot, amount, contentamount, durability);
        new Cache:result = mysql_query(DbHandle, query);
        GarageItems[ garageindex ][ slot ][ SqlId ] = cache_insert_id();
        cache_delete(result);
        return 1;
    }

}

/*


        `7MMM.     ,MMF'`YMM'   `MM'.M"""bgd   .g8""8q. `7MMF'                          `7MM"""Yb. `7MM"""YMM  `7MMF'      `7MM"""YMM MMP""MM""YMM `7MM"""YMM
          MMMb    dPMM    VMA   ,V ,MI    "Y .dP'    `YM. MM                              MM    `Yb. MM    `7    MM          MM    `7 P'   MM   `7   MM    `7
          M YM   ,M MM     VMA ,V  `MMb.     dM'      `MM MM                              MM     `Mb MM   d      MM          MM   d        MM        MM   d
          M  Mb  M' MM      VMMP     `YMMNq. MM        MM MM                              MM      MM MMmmMM      MM          MMmmMM        MM        MMmmMM
          M  YM.P'  MM       MM    .     `MM MM.      ,MP MM      ,                       MM     ,MP MM   Y  ,   MM      ,   MM   Y  ,     MM        MM   Y  ,
          M  `YM'   MM       MM    Mb     dM `Mb.    ,dP' MM     ,M                       MM    ,dP' MM     ,M   MM     ,M   MM     ,M     MM        MM     ,M
        .JML. `'  .JMML.   .JMML.  P"Ybmmd"    `"bmmd"' .JMMmmmmMMM                     .JMMmmmdP' .JMMmmmmMMM .JMMmmmmMMM .JMMmmmmMMM   .JMML.    .JMMmmmmMMM
                                                   MMb
                                                    `bood'
*/


stock RemoveGarageItem(garageindex, itemslot)
{
    new query[60];
    mysql_format(DbHandle, query, sizeof(query), "DELETE FROM garage_items WHERE id = %d LIMIT 1",
        GarageItems[ garageindex ][ itemslot ][ SqlId ]);
    mysql_pquery(DbHandle, query);

    GarageItems[ garageindex ][ itemslot ][ SqlId ] = 0;
    GarageItems[ garageindex ][ itemslot ][ ItemId ] = 0;
    GarageItems[ garageindex ][ itemslot ][ Amount ] = 0;
    GarageItems[ garageindex ][ itemslot ][ ContentAmount ] = 0;
    GarageItems[ garageindex ][ itemslot ][ Durability ] = 0;
    return 1;
}



stock RemoveGarageOwner(garageindex)
{
    new query[70];

    gInfo[ garageindex ][ gOwner ] = GARAGE_OWNER_NULL;
    UpdateGarageEntrance(garageindex);

    mysql_format(DbHandle, query, sizeof(query), "UPDATE garages SET owner = NULL WHERE id = %d",
        gInfo[ garageindex ][ gID ]);
    return mysql_pquery(DbHandle, query);
}

stock RemoveGarage(garageindex)
{
    new query[60];

    Itter_Remove(Garages, garageindex);

    DestroyDynamic3DTextLabel(gInfo[ garageindex ][ gLabel ]);

    mysql_format(DbHandle, query, sizeof(query), "DELETE FROM garages WHERE id = %d LIMIT 1",
        gInfo[ garageindex ][ gID ]);
    mysql_pquery(DbHandle, query);

    static EmptyGarage[ E_GARAGE_DATA ], EmptyItems[ E_GARAGE_ITEM_DATA ], EmptyGFurniture[ E_PROPERTY_FURNITURE_DATA ];
    gInfo[ garageindex ] = EmptyGarage;
    for(new i = 0; i < MAX_GARAGE_ITEMS; i++)
        GarageItems[ garageindex ][ i ] = EmptyItems;
    for(new i = 0; i < MAX_GARAGE_FURNITURE; i++)
        GarageFurniture[ garageindex ][ i ] = EmptyGFurniture;

    return 1;
}

stock RemoveGarageFurnitureTexture(garageindex, furnitureindex, materialindex)
{
    new query[180], placeholder, color;

    GetDynamicObjectMaterial(GarageFurniture[ garageindex ][ furnitureindex ][ ObjectId ], materialindex, placeholder, query, query, color);
    SetDynamicObjectMaterial(GarageFurniture[ garageindex ][ furnitureindex ][ ObjectId ], materialindex, 0, "none", "none", color);

    // Jeigu spalvos nëra, galima visà eilutæ iðtrinti, o jei spalva keista buvo reikia tik paðalinti tekstûrà.
    if(!color)
        mysql_format(DbHandle, query, sizeof(query), "DELETE FROM garage_furniture_textures WHERE furniture_id = %d AND `index` = %d", GarageFurniture[ garageindex ][ furnitureindex ][ SqlId ], materialindex);
    else
        mysql_format(DbHandle, query, sizeof(query), "UPDATE garage_furniture_textures SET object_model = 0, txd_name = 'none', texture_name = 'none' WHERE furniture_id = %d AND `index` = %d",
            GarageFurniture[ garageindex ][ furnitureindex ][ SqlId ], materialindex);

    return mysql_pquery(DbHandle, query);
}

stock DeleteGarageFurniture(garageindex, furnitureindex)
{
    new query[60];

    DestroyDynamicObject(GarageFurniture[ garageindex ][ furnitureindex ][ ObjectId ]);

    mysql_format(DbHandle, query, sizeof(query), "DELETE FROM garage_furniture WHERE id = %d", GarageFurniture[ garageindex ][ furnitureindex ][ SqlId ]);

    // Move stuff to fill the space;
    new i;
    for(i = furnitureindex; i < MAX_GARAGE_FURNITURE-1; i++)
    {
        GarageFurniture[ garageindex ][ i ] = GarageFurniture[ garageindex ][ i + 1];
    }

    // Clean the last one.
    static furn[ E_PROPERTY_FURNITURE_DATA ];
    GarageFurniture[ garageindex ][ i ] = furn;

    return mysql_pquery(DbHandle, query);
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


CMD:garagehelp(playerid)
{
    SendClientMessage(playerid, COLOR_GREEN,"|__________________GARAÞO VALDYMO INFORMACIJA__________________|");
	SendClientMessage(playerid,COLOR_FADE1,"  /ginv - garaþe laikomi daiktai.");
	SendClientMessage(playerid,COLOR_WHITE,"  /buygarage - naudojamas garaþo pirkimui.");
	SendClientMessage(playerid,COLOR_WHITE,"  /lock - garaþo uþrakinimas/atrakinimas.");
	SendClientMessage(playerid,COLOR_FADE1,"  /sellgarage [Þaidëjo ID/ Dalis vardo] [SUMA] - parduoti savo garaþà");
    SendClientMessage(playerid, COLOR_GREEN, "__________________________________________________________________");
    return 1;
}


CMD:buygarage(playerid)
{
    new garageIndex = GetPlayerGarageIndex(playerid);
    if(garageIndex == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Jûs neesate prie garaþo.");

    if(IsGarageOwned(garageIndex))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Garaþas neparduodamas");

    if(PlayerMoney[ playerid ] < gInfo[ garageIndex ][ gPrice ])
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai grynøjø pinigø su savimi, kad atliktumëte ðá veiksmà. ");


    GivePlayerMoney(playerid, -gInfo[ garageIndex ][ gPrice ]);
    gInfo[ garageIndex ][ gOwner ] = GetPlayerSqlId(playerid);
    SaveGarage(garageIndex);
    UpdateGarageEntrance(garageIndex);
    SendClientMessage(playerid, COLOR_NEWS, "Sveikiname! Garaþas buvo sëkmingai nupirktas. ");
    PayLog(GetPlayerSqlId(playerid), 12, -2, -gInfo[ garageIndex ][ gPrice ] );
    SaveAccount(playerid);
    return 1;
}
CMD:sellgarage(playerid, params[])
{
    new price,
        giveplayerid,
        string[ 126 ],
        IP[ 16 ],
        IP2[ 16 ],
        garageIndex = GetPlayerGarageIndex(playerid);

    if(garageIndex == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Jûs neesate prie garaþo arba jo viduje.");

    if(!IsPlayerGarageOwner(playerid, garageIndex))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, garaþas jums nepriklauso.");

    if(sscanf(params, "ud", giveplayerid, price))
        return SendClientMessage(playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /sellgarage [veikëjo id] [kaina]");

    if(!IsPlayerConnected(giveplayerid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");

    if (!IsPlayerInRangeOfPlayer(playerid, giveplayerid, 10.0))
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti ðio veiksmo su veikëju, kuris nëra ðalia Jûsø.");

    if(price < MIN_GARAGE_PRICE || price > MAX_GARAGE_PRICE)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Kaina negali buti maþesnë negu " #MIN_GARAGE_PRICE " ir didesnë negu " #MIN_GARAGE_PRICE ".");


    GetPlayerIp(playerid, IP, sizeof(IP));
    GetPlayerIp(giveplayerid, IP2, sizeof(IP2));

    if(!strcmp(IP, IP2, true) || pInfo[ playerid ][ pUcpID ] == pInfo[ giveplayerid ][ pUcpID ] )
        return true;

    format(string,sizeof(string),"Jûs siulote %s, pirkti savo garaþà uþ: $%d.", GetPlayerNameEx(giveplayerid), price);
    SendClientMessage(playerid, COLOR_WHITE, string);
    format(string,sizeof(string),"Garaþo savininkas %s siølo jums nupirkti jo garaþà uþ: $%d, jeigu sutinkate, raðykite /accept garage.", GetPlayerNameEx(playerid), price);
    SendClientMessage(giveplayerid, COLOR_WHITE, string);
    Offer[giveplayerid][7] = playerid;
    OfferPrice[giveplayerid][7] = price;
    OfferID[giveplayerid][7] = garageIndex;
    return 1;
}

/*

CMD:ginv(playerid)
{
    if(Mires[ playerid ] > 0)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûsø veikëjas ðiuo metu yra kritinëje arba komos bûsenoje.");

    new garageIndex = GetPlayerGarageIndex(playerid);

    if(garageIndex == -1 || !IsPlayerInGarage(playerid, garageIndex))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Jûs neesate garaþo viduje kad galëtumete naudoti ðià komandà.");

    if(!IsPlayerGarageOwner(playerid, garageIndex))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, garaþas jums nepriklauso.");

    ShowGarageInv(playerid, garageIndex);
    return 1;
}*/



/*



                        `7MMM.     ,MMF'                                                                                   mm
                          MMMb    dPMM                                                                                     MM
                          M YM   ,M MM   ,6"Yb.  `7MMpMMMb.   ,6"Yb.  .P"Ybmmm .gP"Ya `7MMpMMMb.pMMMb.  .gP"Ya `7MMpMMMb.mmMMmm
                          M  Mb  M' MM  8)   MM    MM    MM  8)   MM :MI  I8  ,M'   Yb  MM    MM    MM ,M'   Yb  MM    MM  MM
                          M  YM.P'  MM   ,pm9MM    MM    MM   ,pm9MM  WmmmP"  8M""""""  MM    MM    MM 8M""""""  MM    MM  MM
                          M  `YM'   MM  8M   MM    MM    MM  8M   MM 8M       YM.    ,  MM    MM    MM YM.    ,  MM    MM  MM
                        .JML. `'  .JMML.`Moo9^Yo..JMML  JMML.`Moo9^Yo.YMMMMMb  `Mbmmd'.JMML  JMML  JMML.`Mbmmd'.JMML  JMML.`Mbmo
                                                                     6'     dP
                                                                     Ybmmmd'

                                                  ,,    ,,             ,,
                                                `7MM    db           `7MM
                                                  MM                   MM
                                             ,M""bMM  `7MM   ,6"Yb.    MM  ,pW"Wq.   .P"Ybmmm ,pP"Ybd
                                           ,AP    MM    MM  8)   MM    MM 6W'   `Wb :MI  I8   8I   `"
                                           8MI    MM    MM   ,pm9MM    MM 8M     M8  WmmmP"   `YMMMa.
                                           `Mb    MM    MM  8M   MM    MM YA.   ,A9 8M        L.   I8
                                            `Wbmd"MML..JMML.`Moo9^Yo..JMML.`Ybmd9'   YMMMMMb  M9mmmP'
                                                                                    6'     dP
                                                                                    Ybmmmd'

*/


enum E_GARAGE_INDEX_USAGE
{
    GarageEntranceChange,
    GarageExitChange,
    GarageVehicleSpawnChange,
    GaragePriceChange,
    GarageInformation,
    GarageRemoveOwner,
    GarageRemove,
    GarageVehicleExitChange,
};



stock GarageManagementDialog.ShowMain(playerid)
{
    ShowPlayerDialog(playerid, DIALOG_GMENU_MAIN, DIALOG_STYLE_LIST,"Serverio garaþài",
        "- Kurti naujà \n\
        - Keisti áëjimà\n\
        - Keisti iðëjimà\n\
        - Keisti automobilio spawn vietà lauke\n\
        - Keisti automobilio spawn vietà viduje\n\
        - Keisti kainà\n\
        - Þiûrëti informacijà\n\
        - Paðalinti savininkà \n\
        - Peþiûrëti interjerus\n\
        - Iðtrinti", "Rinktis", "Atðaukti" );
    return 1;
}


stock GarageManagementDialog.InputIndex(playerid, E_GARAGE_INDEX_USAGE:usage, errostr[] = "")
{
    new string[128];
    if(!isnull(errostr))
    {
        strcat(string, "{AA0000}");
        strcat(string, errostr);
        strcat(string,"\n{FFFFFF}");

    }
    strcat(string, "Áraðykite norimo redaguoti garaþo ID:");
    SetPVarInt(playerid, "IndexUsage", _:usage);
    ShowPlayerDialog(playerid, DIALOG_GMENU_INPUT_INDEX, DIALOG_STYLE_INPUT,"Garaþo ID áraðmas", string, "Patvirtinti", "Atgal");
    return 1;
}


stock GarageManagementDialog.InputNewPrice(playerid, errostr[] = "")
{
    new string[64];
    if(!isnull(errostr))
    {
        strcat(string, "{AA0000}");
        strcat(string, errostr);
        strcat(string,"\n{FFFFFF}");

    }
    strcat(string, "Áraðykite norima garaþo kainà:");
    ShowPlayerDialog(playerid, DIALOG_GMENU_NEW_PRICE, DIALOG_STYLE_INPUT,"Garaþo kainos pakeitimas", string, "Patvirtinti", "Atðaukti");
    return 1;
}

stock GarageManagementDialog.ConfirmRemove(playerid)
{
    ShowPlayerDialog(playerid, DIALOG_GMENU_DELETE, DIALOG_STYLE_MSGBOX, "{FF0000}Dëmesio!", "Ar tikrai norite paðalinti ðá garaþà? Sugraþinti jo nebebus ámanoma.", "Tæsti", "Atðaukti");
    return 1;
}

stock GarageManagementDialog.ShowInformation(playerid, garageindex)
{
    new string[512];

    // Kaþkodël jei GetSqlIdName raðyèiau apaèioje atsiranda Stack underflow
    string = GetSqlIdName(gInfo[ garageindex ][ gOwner ]);


    format(string, sizeof(string), "Garaþo ID serveryje: %d\n\
        Garaþo ID duomenø bazëje: %d\n\
        Garaþo savininkas: %s\n\
        Garaþo uþrakintas: %s\n\
        Interjero ID: %d\n\
        Kaina: %d\nBaldø skaièius: %d",
        garageindex,
        gInfo[ garageindex ][ gID ],
        (gInfo[ garageindex ][ gOwner ] == GARAGE_OWNER_NULL) ? ("nëra") : (string),
        (gInfo[ garageindex ][ gLocked ]) ? ("Taip") : ("Ne"),
        gInfo[ garageindex ][ gInteriorId ],
        gInfo[ garageindex ][ gPrice ],
        GetGarageFurnitureCount(garageindex)
    );

    strcat(string, "\n\n___Garaþe esantys daiktai___\n\n");
    for(new i = 0; i < MAX_GARAGE_ITEMS; i++)
        format(string, sizeof(string),"%s%s %d\n",
            string,
            GetItemName(GarageItems[ garageindex ][ i ][ ItemId ]),
            GarageItems[ garageindex ][ i ][ Amount ]);
    ShowPlayerDialog(playerid, 9999, DIALOG_STYLE_MSGBOX, "Garaþo informacija", string, "Gerai", "");
    return 1;
}

stock GarageManagementDialog.OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
    switch(dialogid)
    {
        case DIALOG_GMENU_MAIN:
        {
            if(!response)
                return 1;

            new index = GetPlayerGarageIndex(playerid);
            PlayerUsedGarageIndex[ playerid ] = index;
            switch(listitem)
            {
                // Kurti naujà
                case 0:
                {
                    if(GetPlayerVirtualWorld(playerid))
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, garaþas gali bûti tik pradiniam virtualiame pasaulyje.");

                    new Float:x, Float:y, Float:z, Float:angle, string[70];
                    if(!IsPlayerInAnyVehicle(playerid))
                    {
                        GetPlayerPos(playerid, x, y, z);
                        GetPlayerFacingAngle(playerid, angle);
                    }
                    else
                    {
                        GetVehiclePos(GetPlayerVehicleID(playerid), x, y, z);
                        GetVehicleZAngle(GetPlayerVehicleID(playerid), angle);
                    }

                    index = AddGarage(x, y, z, angle);
                    format(string, sizeof(string), "Garaþas buvo sëkmingai sukurtas, ðio garaþo ID: %d", index);
                    SendClientMessage(playerid, GRAD, string);
                }
                // Keisti áëjimà
                case 1:
                {
                    GarageManagementDialog.InputIndex(playerid, GarageEntranceChange);
                }
                // Keisti iðëjimà
                case 2:
                {
                    GarageManagementDialog.InputIndex(playerid, GarageExitChange);
                }
                // Keisti automobilio spawn vietà lauke
                case 3:
                {
                    GarageManagementDialog.InputIndex(playerid, GarageVehicleSpawnChange);

                }
                // Keisti automobilio spawn vietà viduje
                case 4:
                {
                    GarageManagementDialog.InputIndex(playerid, GarageVehicleExitChange);
                }
                // Keisti kainà
                case 5:
                {
                    if(index == -1)
                    {
                        GarageManagementDialog.InputIndex(playerid, GaragePriceChange);
                    }
                    else
                    {
                        GarageManagementDialog.InputNewPrice(playerid);
                    }
                }
                // Þiûrëti informacijà
                case 6:
                {
                    if(index == -1)
                    {
                        GarageManagementDialog.InputIndex(playerid, GarageInformation);
                    }
                    else
                    {
                        GarageManagementDialog.ShowInformation(playerid, index);
                    }
                }
                // Paðalinti savininkà
                case 7:
                {
                    if(index == -1)
                    {
                        GarageManagementDialog.InputIndex(playerid, GarageRemoveOwner);
                    }
                    else
                    {
                        RemoveGarageOwner(index);
                        SendClientMessage(playerid, COLOR_NEWS, "Garaþo savininkas sëkmingai paðalintas.");
                    }
                }
                // Interjerø perþiûra
                case 8:
                {
                    ShowInteriorPreviewForPlayer(playerid, "garage");
                }
                // Iðtrinti garaþà
                case 9:
                {
                    if(index == -1)
                        GarageManagementDialog.InputIndex(playerid, GarageRemove);
                    else
                        GarageManagementDialog.ConfirmRemove(playerid);
                }
            }
        }
        case DIALOG_GMENU_INPUT_INDEX:
        {
            if(!response)
                return GarageManagementDialog.ShowMain(playerid);

            new index,
                E_GARAGE_INDEX_USAGE:usage = E_GARAGE_INDEX_USAGE: GetPVarInt(playerid, "IndexUsage");

            if(sscanf(inputtext, "i", index))
                return GarageManagementDialog.InputIndex(playerid, usage, "Áveskite skaièiø.");
            if(index < 0 || index >= MAX_GARAGES || !IsValidGarage(index))
                return GarageManagementDialog.InputIndex(playerid, usage, "Garaþo su tokiu ID nëra.");

            PlayerUsedGarageIndex[ playerid ] = index;
            switch(usage)
            {
                case GarageEntranceChange:
                {
                    new Float:x, Float:y, Float:z;
                    GetPlayerPos(playerid, x, y, z);
                    SetGarageEntrancePos(index, x, y, z);
                    SendClientMessage(playerid, COLOR_GREEN, "Garaþo áëjimo pozicija sëkmingai atnaujinta.");
                }
                case GarageExitChange:
                {
                    new Float:x, Float:y, Float:z;
                    if(!IsPlayerInAnyInterior(playerid))
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs turite bûti interjere.");

                    GetPlayerPos(playerid, x, y, z);
                    SetGarageExitLocation(index, GetPlayerInteriorId(playerid), x, y, z);
                    SendClientMessage(playerid, COLOR_GREEN, "Garaþo iðëjimo pozicija sëkmingai atnaujinta.");
                }
                case GarageVehicleSpawnChange:
                {
                    new Float:x, Float:y, Float:z, Float:angle;
                    if(!IsPlayerInAnyVehicle(playerid))
                    {
                        SendClientMessage(playerid, COLOR_GREEN, "Rekomenduojama transporto priemonës pozicijà keisti bûnant joje.");
                        GetPlayerPos(playerid, x, y, z);
                        GetPlayerFacingAngle(playerid, angle);
                    }
                    else
                    {
                        GetVehiclePos(GetPlayerVehicleID(playerid), x, y, z);
                        GetVehicleZAngle(GetPlayerVehicleID(playerid), angle);
                    }

                    SetGarageVehicleEntrance(index, x, y, z, angle);
                    SendClientMessage(playerid, COLOR_GREEN, "Garaþo maðinos atsiradimo pozicija lauke sëkmingai atnaujinta.");
                }
                case GarageVehicleExitChange:
                {
                    new Float:x, Float:y, Float:z, Float:angle;
                    if(!IsPlayerInAnyVehicle(playerid))
                    {
                        SendClientMessage(playerid, COLOR_GREEN, "Rekomenduojama transporto priemonës pozicijà keisti bûnant joje.");
                        GetPlayerPos(playerid, x, y, z);
                        GetPlayerFacingAngle(playerid, angle);
                    }
                    else
                    {
                        GetVehiclePos(GetPlayerVehicleID(playerid), x, y, z);
                        GetVehicleZAngle(GetPlayerVehicleID(playerid), angle);
                    }

                    SetGarageVehicleExit(index, x, y, z, angle);
                    SendClientMessage(playerid, COLOR_GREEN, "Garaþo maðinos atsiradimo pozicija viduje sëkmingai atnaujinta.");
                }
                case GaragePriceChange: GarageManagementDialog.InputNewPrice(playerid);
                case GarageInformation: GarageManagementDialog.ShowInformation(playerid, index);
                case GarageRemoveOwner:
                {
                    RemoveGarageOwner(index);
                    SendClientMessage(playerid, COLOR_NEWS, "Garaþo savininkas sëkmingai paðalintas.");
                }
                case GarageRemove: GarageManagementDialog.ConfirmRemove(playerid);

            }
            DeletePVar(playerid, "IndexUsage");
            return 1;
        }
        case DIALOG_GMENU_NEW_PRICE:
        {
            if(!response)
                return GarageManagementDialog.ShowMain(playerid);

            new price;
            if(sscanf(inputtext, "i", price))
                return GarageManagementDialog.InputNewPrice(playerid, "Klaida, kaina turi bûti skaièius.");

            if(price < 0)
                return GarageManagementDialog.InputNewPrice(playerid, "Kaina negali bûti neigiama.");

            printf("DIALOG_GMENU_NEW_PRICE. Used garage index:%d Its ID:%d new price:%d", PlayerUsedGarageIndex[ playerid ], gInfo[ PlayerUsedGarageIndex[ playerid ] ][ gID ], price);
            gInfo[ PlayerUsedGarageIndex[ playerid ] ][ gPrice ] = price;
            SaveGarage(PlayerUsedGarageIndex[ playerid ]);
            UpdateGarageEntrance(PlayerUsedGarageIndex[ playerid ]);
            SendClientMessage(playerid, COLOR_NEWS, "Kaina sëkmingai pakeista.");
            Streamer_Update(playerid);
            return 1;
        }
        case DIALOG_GMENU_DELETE:
        {
            if(!response)
                return 1;

            RemoveGarage(PlayerUsedGarageIndex[ playerid ]);
            SendClientMessage(playerid, COLOR_NEWS, "Garaþas sëkmingai paðalintas.");
            return 1;
        }
    }
    return 0;
}

