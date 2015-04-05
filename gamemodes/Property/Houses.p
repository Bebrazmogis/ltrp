#include <YSI\y_hooks>


/*
    


*/

#if !defined ismysqlnull
    #define ismysqlnull(%0)             (!strcmp(%0, "NULL"))
#endif


#define HOUSE_VIRTUAL_WORLD         (10000)


#define HOUSE_OWNER_NULL                0

#define DIALOG_HOUSE_RADIO              73
#define DIALOG_HOUSE_RADIO_LIST         74
#define DIALOG_HOUSE_RADIO_CUSTOM_URL   139
#define DIALOG_HOUSE_RADIO_VOLUME       75
#define DIALOG_HOUSE_ITEM_LIST          15

#define HOUSE_RADIO_VOLUME_OFF          -1
#define MAX_HOUSE_DEPOSIT               99999999999
#define MAX_HOUSE_RENT_PRICE            500
#define MAX_HOUSE_SELL_PRICE            10000000

#define MIN_HOUSE_WEED_YIELD            15
#define MAX_HOUSE_WEED_YIELD            22
#define MAX_HOUSE_WEED_GROWTH_LEVEL     24

#define HOUSE_WEED_PLANT_OBJECT         19473
#define HOUSE_WEED_PLANT_HEIGHT         1.744

#define MAX_HOUSE_ITEMS                 MAX_HOUSETRUNK_SLOTS
#define MAX_HOUSE_FURNITURE             400
#define MAX_HOUSE_WEED_SAPLINGS         1

#define HOUSE_WEED_GROW_TIME_MS         60*60*1000
#define HOUSE_WEED_GROW_TIME_S          (HOUSE_WEED_GROW_TIME_MS / 1000)

#define HouseManagementDialog.          houdiag

#define HOUSE_LABEL_DRAW_DISTANCE       20.0


enum E_HOUSE_ITEM_DATA
{
    SqlId,
    ItemId,
    Amount,
    ContentAmount,
    Durability,
};

enum E_HOUSE_WEED_DATA 
{
    Id,
    GrowthLevel,
    Timer:GrowTimer,
    ObjectId,
};

enum E_HOUSE_RADIO_DATA {
    StationURL[ 64 ],
    Volume
};

enum E_HOUSE_FURNITURE_DATA {
    SqlId,
    FurnitureId,
    ObjectId,
    Name[ MAX_FURNITURE_NAME]
};

enum E_HOUSE_UPGRADE_DATA {
    bool:Refrigerator,
    bool:Radio
};

enum E_HOUSE_DATA 
{
    hID,
    hOwner,
    hPrice,
    hRentPrice,
    Float:hEnter[3],
    Float:hExit[3],
    hMats,
    bool:hLocked,
    hBank,
    hEntranceInt,
    hEntranceVirw,
    hGar,
    hInteriorId,
    hUpgrades[ E_HOUSE_UPGRADE_DATA ],
    Text3D:hLabel,
    hPickup,
    hPickupModel,
};

new hInfo[ MAX_HOUSES ][ E_HOUSE_DATA ],
    HouseFurniture[MAX_HOUSES][ MAX_HOUSE_FURNITURE][ E_HOUSE_FURNITURE_DATA ],
    HouseRadio[ MAX_HOUSES ][ E_HOUSE_RADIO_DATA ],
    HouseWeed[ MAX_HOUSES ][ MAX_HOUSE_WEED_SAPLINGS ][ E_HOUSE_WEED_DATA ],
    HouseItems[ MAX_HOUSES ][ MAX_HOUSE_ITEMS ][ E_HOUSE_ITEM_DATA ];

new Iterator:Houses<MAX_HOUSES>;



forward OnHouseLoad();
forward OnHouseItemLoad();
forward OnHouseFurnitureLoad();
forward OnHouseWeedLoad();

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
    #if defined houses_OnGameModeInit
        houses_OnGameModeInit();
    #endif


    mysql_tquery(DbHandle, "SELECT * FROM `houses` ORDER BY `id` ASC", "OnHouseLoad", "");
    mysql_tquery(DbHandle, "SELECT * FROM `house_items` ORDER BY house_id, slot", "OnHouseItemLoad", "");
    // Tokia sudëtinga uþklausa tik tam kad paimti papildomai tekstûros duomenis
    // Bei tam kad bûtø iðrikiuota pagal namo ID, todël vieno namo baldai bus vienas ðalia kito.
    mysql_tquery(DbHandle, "SELECT house_furniture.*,house_furniture_textures.*, furniture.name AS default_name FROM house_furniture LEFT JOIN house_furniture_textures ON house_furniture.id = house_furniture_textures.furniture_id LEFT JOIN furniture ON furniture.id = house_furniture.furniture_id", "OnHouseFurnitureLoad", "");
    mysql_tquery(DbHandle, "SELECT * FROM house_weed WHERE harvested_by IS NULL", "OnHouseWeedLoad", "");
    return 1;
}
#if defined _ALS_OnGameModeInit
    #undef OnGameModeInit
#else 
    #define _ALS_OnGameModeInit
#endif
#define OnGameModeInit houses_OnGameModeInit
#if defined houses_OnGameModeInit
    forward houses_OnGameModeInit();
#endif


public OnHouseLoad()
{
    new
        tmpOwner[16],
        tmpInterior[16];

    for(new i = 0; i < cache_get_row_count(); i++)
    {
        hInfo[ i ][ hID] = cache_get_field_content_int(i, "id");
        cache_get_field_content(i, "owner", tmpOwner);
        hInfo[ i ][ hPrice ] = cache_get_field_content_int(i, "price");
        cache_get_field_content(i, "interior_id", tmpInterior);
        hInfo[ i ][ hEnter ][0] = cache_get_field_content_float(i, "entrance_x");
        hInfo[ i ][ hEnter ][1] = cache_get_field_content_float(i, "entrance_y");
        hInfo[ i ][ hEnter ][2] = cache_get_field_content_float(i, "entrance_z");
        hInfo[ i ][ hExit ][0] = cache_get_field_content_float(i, "exit_x");
        hInfo[ i ][ hExit ][1] = cache_get_field_content_float(i, "exit_y");
        hInfo[ i ][ hExit ][2] = cache_get_field_content_float(i, "exit_z");
        hInfo[ i ][ hEntranceInt ] = cache_get_field_content_int(i, "entrance_interior");
        hInfo[ i ][ hEntranceVirw ] = cache_get_field_content_int(i, "entrance_virtual");
        hInfo[ i ][ hLocked ] = bool:cache_get_field_content_int(i, "locked");
        hInfo[ i ][ hBank ] = cache_get_field_content_int(i, "bank");
        hInfo[ i ][ hRentPrice ] = cache_get_field_content_int(i, "rent_price");
        hInfo[ i ][ hPickupModel ] = cache_get_field_content_int(i, "pickup_model");
        hInfo[ i ][ hUpgrades ][ Refrigerator ] = bool:cache_get_field_content_int(i, "refrigerator");
        hInfo[ i ][ hUpgrades ][ Radio ] = bool:cache_get_field_content_int(i, "radio");

        if(ismysqlnull(tmpOwner))
            hInfo[ i ][ hOwner ] = HOUSE_OWNER_NULL;
        else 
            hInfo[ i ][ hOwner ] = strval(tmpOwner);

        hInfo[ i ][ hInteriorId ] = (ismysqlnull(tmpInterior)) ? (INVALID_INTERIOR_ID) : (strval(tmpInterior));

        UpdateHouseInfoText(i);
        Itter_Add(Houses,i);
    }
    printf("Serveryje ðiuo metu sukurtas (-i) %d namas (-ai)", cache_get_row_count());
    return 1;
}

public OnHouseItemLoad()
{
    new id, itemid, houseid, index, amount, contentamount, durability;
    for(new i = 0; i < cache_get_row_count(); i++)
    {
        id = cache_get_field_content_int(i, "id");
        houseid = cache_get_field_content_int(i, "house_id");
        itemid = cache_get_field_content_int(i, "item_id");
        index = cache_get_field_content_int(i, "slot");
        amount = cache_get_field_content_int(i, "amount");
        contentamount = cache_get_field_content_int(i, "content_amount");
        durability = cache_get_field_content_int(i, "durability");

        foreach(new j : Houses)
        {
            if(hInfo[ j ][ hID ] == houseid)
            {
                HouseItems[ j ][ index ][ SqlId ] = id;
                HouseItems[ j ][ index ][ ItemId ] = itemid;
                HouseItems[ j ][ index ][ Amount ] = amount;
                HouseItems[ j ][ index ][ ContentAmount ] = contentamount;
                HouseItems[ j ][ index ][ Durability ] = durability;
                break;
            }
        }
    }
    printf("Uþkrauti %d namø daiktai.", cache_get_row_count());
    return 1;
}


public OnHouseFurnitureLoad()
{
    new  
        id,
        hid,
        fid,
        name[ MAX_FURNITURE_NAME ],
        Float:pos[6],
        strFid[16],
        strIndex[16],
        strObjectModel[16],
        txdName[MAX_TXD_FILE_NAME],
        textureName[ MAX_TEXTURE_NAME ],
        strTextureColor[16],
        
        lastFurnitureId = 0,
        lastHouseId = 0,
        houseFurnitureCount = -1,
        houseIndex,
        furnitureIndex
    ;

    for(new i = 0; i < cache_get_row_count(); i++)
    {
        id = cache_get_field_content_int(i, "id");
        hid = cache_get_field_content_int(i, "house_id");
        fid = cache_get_field_content_int(i, "furniture_id");
        cache_get_field_content(i, "name", name);
        pos[ 0 ] = cache_get_field_content_float(i, "pos_x");
        pos[ 1 ] = cache_get_field_content_float(i, "pos_y");
        pos[ 2 ] = cache_get_field_content_float(i, "pos_z");
        pos[ 3 ] = cache_get_field_content_float(i, "rot_x");
        pos[ 4 ] = cache_get_field_content_float(i, "rot_y");
        pos[ 5 ] = cache_get_field_content_float(i, "rot_z");

        // house_furniture_textures data
        cache_get_field_content(i, "furniture_id", strFid);
        cache_get_field_content(i, "index", strIndex);
        cache_get_field_content(i, "object_model", strObjectModel);
        cache_get_field_content(i, "txd_name", txdName);
        cache_get_field_content(i, "texture_name", textureName);
        cache_get_field_content(i, "color", strTextureColor);

        houseIndex = GetHouseIndex(hid);
        if(houseIndex == -1)
            continue;

        // Jei namas kitas, krausime i kita HouseFurniture[]
        if(!lastHouseId || lastHouseId != hid)
        {
            lastHouseId = hid;
            houseFurnitureCount = -1;
        }

        // Jeigu lastFurnitureId nenustatytas arba nelygus kàtik uþkrautam, reiðkia ðio baldo dar nesukûrëm.
        if(!lastFurnitureId || lastFurnitureId != id)
        {
            houseFurnitureCount++;
            lastFurnitureId = id;

            HouseFurniture[ houseIndex ][ houseFurnitureCount ][ SqlId ] = id;
            HouseFurniture[ houseIndex ][ houseFurnitureCount ][ FurnitureId ] = fid;
            if(ismysqlnull(name))
                cache_get_field_content(i, "default_name", HouseFurniture[ houseIndex ][ houseFurnitureCount ][ Name ], DbHandle, MAX_FURNITURE_NAME);
            else 
                strcat(HouseFurniture[ houseIndex ][ houseFurnitureCount ][ Name ], name, MAX_FURNITURE_NAME);

            furnitureIndex = GetFurnitureIndex(fid);
            // Neturëtø bût niekada -1, nebent kokie pakeitimai furniture table vyko.
            if(furnitureIndex != -1)
                HouseFurniture[ houseIndex ][ houseFurnitureCount ][ ObjectId ] = CreateDynamicObject(GetFurnitureObjectId(furnitureIndex), pos[0], pos[1], pos[2], pos[3], pos[4], pos[5], .worldid=GetHouseVirtualWorld(houseIndex));

        }
        // Tas pats baldas vël result set'e. Reiðkia keièiam tekstûrà.
        if(!ismysqlnull(strIndex))
        {
            SetDynamicObjectMaterial(HouseFurniture[ houseIndex ][ houseFurnitureCount ][ ObjectId ], strval(strIndex), strval(strObjectModel), txdName, textureName, strval(strTextureColor));
        }
    }
    printf("Pakrauti %d namu objektai.", cache_get_row_count());
    return 1;
}


public OnHouseWeedLoad()
{
    new hindex, lastHouseId, plantcount, planttime, Float:x, Float:y, Float:z;

    for(new i = 0; i < cache_get_row_count(); i++)
    {
        new houseid = cache_get_field_content_int(i, "house_id");
        if(!lastHouseId || lastHouseId != houseid)
        {
            lastHouseId = houseid;
            hindex = GetHouseIndex(houseid);
            plantcount = 0;
        }

        
        if(hindex == -1)
            continue;

        HouseWeed[ hindex ][ plantcount ][ Id ] = cache_get_field_content_int(i, "id");
        planttime = cache_get_field_content_int(i, "plant_timestamp");
        HouseWeed[ hindex ][ plantcount ][ GrowthLevel ] = cache_get_field_content_int(i, "growth_level");

        // Jei jau uþaugæs
        if(HouseWeed[ hindex ][ plantcount ][ GrowthLevel ] < MAX_HOUSE_WEED_GROWTH_LEVEL)
        {
            x = cache_get_field_content_float(i, "x");
            y = cache_get_field_content_float(i, "y");
            z = cache_get_field_content_float(i, "z");

            if(!HouseWeed[ hindex ][ plantcount ][ GrowthLevel ])
            {
                HouseWeed[ hindex ][ plantcount ][ GrowTimer ] = defer WeedGrowTime((HOUSE_WEED_GROW_TIME_S - (gettime()-planttime)) * 1000, hindex, plantcount);
            }
            else
            {
                HouseWeed[ hindex ][ plantcount ][ GrowTimer ] = defer WeedGrowTime((HOUSE_WEED_GROW_TIME_S - (gettime()-cache_get_field_content_int(i, "growth_timestamp"))) * 1000, hindex, plantcount);
                x += HouseWeed[ hindex ][ plantcount ][ GrowthLevel ] * (HOUSE_WEED_PLANT_HEIGHT / MAX_HOUSE_WEED_GROWTH_LEVEL);
                y += HouseWeed[ hindex ][ plantcount ][ GrowthLevel ] * (HOUSE_WEED_PLANT_HEIGHT / MAX_HOUSE_WEED_GROWTH_LEVEL);
                z += HouseWeed[ hindex ][ plantcount ][ GrowthLevel ] * (HOUSE_WEED_PLANT_HEIGHT / MAX_HOUSE_WEED_GROWTH_LEVEL);
            }
            HouseWeed[ hindex ][ plantcount ][ ObjectId ] = CreateDynamicObject(HOUSE_WEED_PLANT_OBJECT, x, y, z, random(20), random(20), random(360), GetHouseVirtualWorld(hindex), GetInteriorInteriorId(GetHouseInteriorID(hindex)));

        }
        plantcount++;
    }
    printf("Pakrauti %d þolës augalai.", cache_get_row_count());
    return 1;
}

hook OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
    HouseManagementDialog.OnDialogResponse(playerid, dialogid, response, listitem, inputtext);
    switch(dialogid)
    {
        case DIALOG_HOUSE_RADIO:
        {
            if(!response)
                return 1;

            new string[128],
                house_index = GetPVarInt(playerid, "HouseIndex");
            switch(listitem)
            {
                // Sàraðas stoèiø
                case 0:
                {
                    for(new i = 0; i < STATIONS; i++ )
                    {
                        format(string, sizeof(string), "%s%s\n",
                                string,
                                RadioStations [ i ][ rName ]);
                    }
                    strcat(string, "Ávesti savo stotá");
                    ShowPlayerDialog(playerid, DIALOG_HOUSE_RADIO_LIST, DIALOG_STYLE_LIST, "Radijo stotys", string, "Rinktis", "Atðaukti");
                    return 1;
                }
                // Garso valdymas
                case 1:
                    return ShowPlayerDialog(playerid, DIALOG_HOUSE_RADIO_VOLUME, DIALOG_STYLE_INPUT, "Garso sistemos valdymas", "þemiau áraðykite norimá  garsumá  nuo 0 iki 100 ","Rinktis", "Atðaukti");

                // Iðjungimas
                case 2:
                {
                    HouseRadio[ house_index ][ Volume ] = HOUSE_RADIO_VOLUME_OFF;
                    format(string, sizeof(string), "* %s iðjungia garso sistema." ,GetPlayerNameEx(playerid));
                    ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);

                    foreach(new i : Player)
                    {
                        if(IsPlayerInInterior(i, hInfo[ house_index ][ hInteriorId ]))
                            StopPlayerRadio(i);
                    }
                    return 1;
                }
            }
            return 1;
        }
        case DIALOG_HOUSE_RADIO_VOLUME:
        {
            if(!response)
                return 1;

            new house_index = GetPVarInt(playerid, "HouseIndex"),
                string[80],
                volume = strval(inputtext);

            if(volume < 0 || volume > 101) 
                return 1;

            HouseRadio[ house_index ][ Volume ] = volume;
            format(string, sizeof(string), "Garso sistemos garas buvo nustatyas á %d", volume);
            SendClientMessage( playerid, COLOR_WHITE, string );
        
            foreach(new i : Player)
            {
                if(IsPlayerInHouse(i, house_index))
                    SetPlayerRadioVolume(i, volume);
            }
            format(string, sizeof(string), "* %s pakeièia garso sistemos garsá .", GetPlayerNameEx(playerid));
            ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
            return 1;

        }
        case DIALOG_HOUSE_RADIO_LIST:
        {
            if(!response)
                return 1;

            new string[ 70];
            if(listitem < STATIONS)
            {
                new house_index = GetPVarInt(playerid, "HouseIndex");
                foreach(new i : Player)
                {
                    if(IsPlayerInHouse(i, house_index))
                    {
                        format(HouseRadio[ house_index ][ StationURL ], MAX_RADIO_STATION_URL, RadioStations[ listitem ][ rUrl ]);
                        SetPlayerRadio(i, RadioStations [ listitem ][ rUrl ]);
                        SetPlayerRadioVolume(i, 50);
                        HouseRadio[ house_index ][ Volume ] = 50;
                    }
                }
                format(string, sizeof(string), "* %s nustato garso sistemos daþná." ,GetPlayerNameEx(playerid));
                ProxDetector (20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
            }
            else
                ShowPlayerDialog(playerid, DIALOG_HOUSE_RADIO_CUSTOM_URL, DIALOG_STYLE_INPUT, "Radijas", "Áveskite radijos stotá", "OK", "Atðaukti");
            return 1;
        }
        case DIALOG_HOUSE_RADIO_CUSTOM_URL:
        {
            if(!response)
                return 1;

            new house_index = GetPVarInt(playerid, "HouseIndex"),
                string[80];
            
            foreach(new i : Player)
            {
                if(IsPlayerInHouse(i, house_index))
                {
                    format(HouseRadio[ house_index ][ StationURL ], MAX_RADIO_STATION_URL, inputtext);
                    SetPlayerRadio(i, inputtext);
                }
            }
            format(string, sizeof(string), "* %s pakeièia audio sistemos daþná á kità." ,GetPlayerNameEx(playerid));
            ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
        }
        case DIALOG_HOUSE_ITEM_LIST:
        {

            if(!response)
                return 1;

            new house_index = GetPVarInt(playerid, "HouseIndex"),
                itemid = HouseItems[ house_index ][ listitem ][ ItemId ],
                string[90];

            // Jei itemid maþesnis uþ 50, tai reiðkia kad tai ginklas
            if(itemid < 50 && !IsPlayerHaveManyGuns(playerid, itemid))
            {
                // Jei tai ðaunamasis ginklas
                if(itemid > 21)
                {
                    if(pInfo[ playerid ][ pLevel ] < 2)
                    return SendClientMessage( playerid, COLOR_RED, "Klaida, Jûs privalote bøti pasiekæs 2 lygá, kad naudotumëtës ðia galimybæ.");
                }
                GunLog(GetPlayerSqlId(playerid), 2, hInfo[ house_index ][ hOwner ], GetItemName(itemid), HouseItems[ house_index ][ listitem ][ Amount ]);
                GivePlayerWeapon(playerid, itemid, HouseItems[ house_index ][ listitem ][ Amount ]);
            }
            // Jei tai áprastas daiktas
            else if(itemid > 50)
            {
                if(IsItemDrug(itemid))
                    NarkLog(GetPlayerSqlId(playerid), 2, hInfo[ house_index ][ hOwner ], GetItemName(itemid), HouseItems[ house_index ][ listitem ][ Amount ]);

                if(IsPlayerInventoryFull(playerid))
                    return SendClientMessage(playerid, COLOR_FADE2, "{FF6347}Klaida, bet Jûsø inventoriuje nepakanka laisvos vietos ðiam daiktui.");

                GivePlayerItem(playerid, itemid, HouseItems[ house_index ][ listitem ][ Amount ], HouseItems[ house_index ][ listitem ][ ContentAmount ], HouseItems[ house_index ][ listitem ][ Durability ]);
            }

            // Jei pasiekëm ðià vietà, daiktas jau duotas reikia já paðalinti ið namo atminties
            RemoveHouseItem(house_index, listitem);
            format(string, sizeof(string), "* %s pasiemà daiktà ið spintelës, kuris atrodo kaip %s ", GetPlayerNameEx(playerid), GetItemName(itemid));
            ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
            return 1;
        }
    }
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


stock ShowHouseInv(playerid, hindex)
{
    new string[ 5120 ];
    for(new i = 0; i < MAX_HOUSE_ITEMS; i++)
    {
        if(!HouseItems[ hindex ][ i ][ SqlId ])
            format(string, sizeof(string), "%s%d. Nëra\n", string, i+1);
        else
            format(string, sizeof(string), "%s%d. %s %d\n", string, i+1, 
                GetItemName(HouseItems[ hindex ][ i ][ ItemId ]), 
                HouseItems[ hindex ][ i ][ Amount ]);
    }
    SetPVarInt(playerid, "HouseIndex", hindex);
    ShowPlayerDialog(playerid, DIALOG_HOUSE_ITEM_LIST, DIALOG_STYLE_LIST, "Spintelë", string, "Paimti", "Iðeiti");
    return 1;
}

stock UpdateHouseInfoText(index)
{
    if(IsValidDynamic3DTextLabel(hInfo[ index ][ hLabel ]))
        DestroyDynamic3DTextLabel(hInfo[ index ][ hLabel ]);
    if(IsValidDynamicPickup(hInfo[ index ][ hPickup ]))
        DestroyDynamicPickup(hInfo[ index ][ hPickup ]);

    if(!hInfo[ index ][ hPickupModel ])
        hInfo[ index ][ hPickupModel ] = DEFAULT_HOUSE_PICKUP_MODEL;

    new string[ 126 ];
    if(IsHouseOwned(index))
    {
        hInfo[ index ][ hPickup ] = CreateDynamicPickup(hInfo[ index ][ hPickupModel ], 1,
            hInfo[ index] [hEnter][0], hInfo[index][ hEnter ][1], hInfo[index][hEnter][2],
            hInfo[ index ][ hEntranceVirw ], hInfo[ index ][ hEntranceInt ] );
        return 1;
    }
    else
    {
        format(string,sizeof(string),"Parduodamas namas\nPardavimo kaina: %d$\nÁsigijimui - /buyhouse", hInfo[ index ][ hPrice ]);
        hInfo[ index ][ hLabel ] = CreateDynamic3DTextLabel( string,COLOR_NEWS,hInfo[index][hEnter][0],hInfo[index][hEnter][1],hInfo[index][hEnter][2], HOUSE_LABEL_DRAW_DISTANCE, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, 0, hInfo[ index ][ hEntranceVirw ], hInfo[ index ][ hEntranceInt ], -1, 15.0);
        return 1;
    }
}

stock IsHouseRadioTurnedOn(houseindex)
    return (HouseRadio[ houseindex ][ Volume ] == HOUSE_RADIO_VOLUME_OFF) ? (false) : (true);

stock GetHouseRadioStation(hindex)
{
    new station[ MAX_RADIO_STATION_URL ];
    strcat(station, HouseRadio[ hindex ][ StationURL ], MAX_RADIO_STATION_URL);
    return station;
}

stock GetHouseFreeitemSlot(hindex)
{
    for(new i = 0; i < MAX_HOUSE_ITEMS; i++)    
        if(!HouseItems[ hindex ][ i ][ SqlId ])
            return i;
    return -1;
}   


stock GetHouseExitPos(hindex, &Float:x, &Float:y, &Float:z)
{
    x = hInfo[ hindex ][ hExit ][ 0 ];
    y = hInfo[ hindex ][ hExit ][ 1 ];
    z = hInfo[ hindex ][ hExit ][ 2 ];
}

stock GetHouseEntrancePos(hindex, &Float:x, &Float:y, &Float:z)
{
    x = hInfo[ hindex ][ hEnter ][ 0 ];
    y = hInfo[ hindex ][ hEnter ][ 1 ];
    z = hInfo[ hindex ][ hEnter ][ 2 ];
}

stock GetHouseEntranceVirtualWorld(hindex)
    return hInfo[ hindex ][ hEntranceVirw ];

stock GetHouseEntranceInteriorID(hindex)
    return hInfo[ hindex ][ hEntranceInt ];

stock GetHouseFurnitureCount(hindex)
{
    new count = 0;
    for(new i = 0; i < MAX_HOUSE_FURNITURE; i++)
        if(HouseFurniture[ hindex ][ i ][ SqlId ])
            count++;
    return count;
}
stock GetHouseFurniturePrice(hindex, findex)
    return GetFurniturePrice(GetFurnitureIndex(HouseFurniture[ hindex ][ findex ][ FurnitureId ]));

stock GetHouseFurnitureObjectModel(hindex, findex)
    return GetFurnitureObjectId(HouseFurniture[ hindex ][ findex ][ FurnitureId ]);

stock GetHouseFurnitureId(hindex, findex)
    return HouseFurniture[ hindex ][ findex ][ FurnitureId ];

stock GetHouseFurnitureObjectId(hindex, findex)
    return HouseFurniture[ hindex ][ findex ][ ObjectId ];


stock GetHouseFurnitureIndex(hindex, objectid)
{
    for(new i = 0; i < MAX_HOUSE_FURNITURE; i++)
    {
        if(HouseFurniture[ hindex ][ i ][ ObjectId ] == objectid)
            return i;
    }
    return false;
}

stock IsAnyHouseFurnitureObject(objectid)
{
    foreach(new i : Houses)
        for(new j = 0; j < MAX_HOUSE_FURNITURE; j++)
            if(HouseFurniture[ i ][ j ][ SqlId ] && HouseFurniture[ i ][ j ][ ObjectId ] == objectid)
                return true;
    return false;
}

stock IsValidHouse(hindex)
{
    if(hindex < 0 || hindex >= MAX_HOUSES)
        return false;

    if(hInfo[ hindex ][ hID ])
        return true;
    else 
        return false;
}

stock IsHouseUpgradeInstalled(houseindex, E_HOUSE_UPGRADE_DATA:upgrade)
{
    return hInfo[ houseindex ][ hUpgrades ][ upgrade ];
}

stock IsPlayerInRangeOfHouseFurniture(playerid, hindex, findex, Float:distance)
{
    new Float:pos[3];
    GetDynamicObjectPos(HouseFurniture[ hindex ][ findex ][ ObjectId ], pos[ 0 ], pos[ 1 ], pos[ 2 ]);
    return IsPlayerInRangeOfPoint(playerid, distance, pos[ 0 ], pos[ 1 ], pos[ 2 ]);
}

stock IsPlayerInRangeOfHouseExit(playerid, hindex, Float:distance)
{
    return IsPlayerInRangeOfPoint(playerid, distance, hInfo[ hindex ][ hExit ][ 0 ], hInfo[ hindex ][ hExit ][ 1 ], hInfo[ hindex ][ hExit ][ 2 ]);
}

stock GetHouseFurnitureName(hindex, findex)
{
    new s[MAX_FURNITURE_NAME ];
    strcat(s, HouseFurniture[ hindex ][ findex ][ Name ]);
    return s;
}

stock GetHousePickupHouseIndex(pickupid)
{
    foreach(new i : Houses)
        if(hInfo[ i ][ hPickup ] == pickupid)
            return i;
    return -1;
}

stock GetHouseIndex(hid)
{
    foreach(new i : Houses)
        if(hInfo[ i ][ hID ] == hid)
            return i;
    return -1;
}
stock GetHouseID(hindex)
    return hInfo[ hindex ][ hID ];

stock GetHouseRent(hindex)
    return hInfo[ hindex ][ hRentPrice ];

stock GetHouseInteriorID(hindex)
    return hInfo[ hindex ][ hInteriorId ];

stock GetHouseVirtualWorld(hindex)
    return hInfo[ hindex ][ hID ] + HOUSE_VIRTUAL_WORLD;
 
stock IsHouseLocked(hindex)
    return hInfo[ hindex ][ hLocked ];

stock GetHouseOwner(hindex)
    return hInfo[ hindex ][ hOwner ];

stock ToggleHouseLock(hindex)
{
    new query[80];
    hInfo[ hindex ][ hLocked ] = ! hInfo[ hindex ][ hLocked ];
    format(query, sizeof(query), "UPDATE houses SET locked = %d WHERE id = %d", hInfo[ hindex ][ hLocked ], hInfo[ hindex ][ hID ]);
    mysql_pquery(DbHandle, query);
    return 1;
}

stock GetPlayerHouseIndex(playerid, outside=false)
{
    foreach(new i : Houses)
    {
        if(IsPlayerInInterior(playerid, hInfo[ i ][ hInteriorId ])
            && GetPlayerVirtualWorld(playerid) == HOUSE_VIRTUAL_WORLD+hInfo[ i ][ hID ])
            return i;
        if(outside && IsPlayerInRangeOfPoint(playerid, 3.0, hInfo[ i ][ hEnter ][ 0 ], hInfo[ i ][ hEnter ][ 1 ], hInfo[ i ][ hEnter ][ 2 ]))
            return i;
    }
    return -1;
}


stock IsPlayerInHouse(playerid, house_index)
{
    if(IsPlayerInInterior(playerid, hInfo[ house_index ][ hInteriorId ]) && GetPlayerVirtualWorld(playerid) == GetHouseVirtualWorld(house_index))
        return true;
    else 
        return false;
}

stock IsPlayerInAnyHouse(playerid)
{
    foreach(new i : Houses)
        if(IsPlayerInHouse(playerid, i))
            return true;
    return false;
}

stock IsHouseOwned(index)
{
    if(hInfo[ index ][ hOwner ] != HOUSE_OWNER_NULL)
        return true;
    else 
        return false;
}

stock IsPlayerHouseOwner(playerid, hindex)
{
    if(GetPlayerSqlId(playerid) == hInfo[ hindex ][ hOwner ])
        return true;
    else
        return false;
}

stock IsPlayerRentingAnyHouse(playerid)
{
    foreach(new i : Houses) 
        if(IsPlayerRentingHouse(playerid, i))
            return true;
    return false;
}

stock IsPlayerRentingHouse(playerid, index)
{
    if(GetPlayerHouseKey(playerid) == hInfo[ index ][ hID ])
        return true;
    else 
        return false;
}

stock GetHouseFreeWeedSlotCount(hindex)
{
    new count = 0;
    for(new i = 0; i < MAX_HOUSE_WEED_SAPLINGS; i++)
        if(!HouseWeed[ hindex ][ i ][ Id ])
            count++;
    return count;   
}

stock GetHouseWeedPlantCount(houseindex)
{
    new count = 0;
    for(new i = 0; i < MAX_HOUSE_WEED_SAPLINGS; i++)
        if(HouseWeed[ houseindex ][ i ][ Id ])
            count++;
    return count;   
}


stock HarvestHouseWeedPlant(playerid, house_index, weedindex)
{
    new query[80],
        yield = random(MAX_HOUSE_WEED_YIELD - MIN_HOUSE_WEED_YIELD) + MIN_HOUSE_WEED_YIELD;

    mysql_format(DbHandle, query, sizeof(query), "UPDATE house_weed SET harvested_by = %d, yield = %d WHERE id = %d", 
        GetPlayerSqlId(playerid),
        yield,
        HouseWeed[ house_index ][ weedindex ][ Id ]);
    mysql_pquery(DbHandle, query);
    HouseWeed[ house_index ][ weedindex ][ Id ] = 0;
    DestroyDynamicObject(HouseWeed[ house_index ][ weedindex ][ ObjectId ]);
    return yield;
}


/*
                                                      
                                        ,,        ,,  
                          db          `7MM      `7MM  
                         ;MM:           MM        MM  
                        ,V^MM.     ,M""bMM   ,M""bMM  
                       ,M  `MM   ,AP    MM ,AP    MM  
                       AbmmmqMA  8MI    MM 8MI    MM  
                      A'     VML `Mb    MM `Mb    MM  
                    .AMA.   .AMMA.`Wbmd"MML.`Wbmd"MML.
                                                      
                                                      
*/


stock AddHouse(Float:enx, Float:eny, Float:enz, entrance_interior, entrance_virtual)
{
    new query[256],
        index = -1;
    
    static EmptyHouse[ E_HOUSE_DATA ], FurnitureData[MAX_HOUSE_FURNITURE ][ E_HOUSE_FURNITURE_DATA ];

    for(new i = 0; i < MAX_HOUSES; i++) 
    {
        if(!hInfo[ i ][ hID ])
        {
            index = i;
            // Nustatom á tuðèius masyvus, just in case.
            hInfo[ i ] = EmptyHouse;
            HouseFurniture[ i ] = FurnitureData;
            break;
        }
    }
    if(index == -1)
        return index;

    hInfo[ index ][ hEnter ][ 0 ] = enx;
    hInfo[ index ][ hEnter ][ 1 ] = eny;
    hInfo[ index ][ hEnter ][ 2 ] = enz;
    hInfo[ index ][ hEntranceInt ] = entrance_interior;
    hInfo[ index ][ hEntranceVirw ] = entrance_virtual;
    Itter_Add(Houses, index);

    format(query, sizeof(query), "INSERT INTO houses (entrance_x, entrance_y, entrance_z, entrance_interior, entrance_virtual) VALUES (%f, %f, %f, %d, %d) ",
        enx, eny, enz, entrance_interior, entrance_virtual);

    new Cache:result;
    if(!(result = mysql_query(DbHandle, query, true))) 
        return -1;

    hInfo[ index ][ hID ] = cache_insert_id();
    cache_delete(result);
    UpdateHouseInfoText(index);
    return index;
}


stock AddHouseUpgrade(hindex, E_HOUSE_UPGRADE_DATA:upgrade)
{
    if(hInfo[ hindex ][ hUpgrades ][ upgrade ])
        return 0;

    new query[100],
        column[32];

    switch(upgrade)
    {
        case Refrigerator: column = "refrigerator";
        case Radio: column = "radio";
    }
    if(isnull(column))
        return 0;

    hInfo[ hindex ][ hUpgrades ][ upgrade ] = true;
    format(query, sizeof(query), "UPDATE houses SET %s = 1 WHERE id = %d", column, hInfo[ hindex ][ hID ]);
    return mysql_pquery(DbHandle, query);
}


stock AddHouseFurniture(hindex, furniture_index, Float:posx, Float:posy, Float:posz, Float:rotx = 0.0, Float:roty = 0.0 , Float:rotz = 0.0)
{
    new
        query[256],
        i;

    mysql_format(DbHandle, query, sizeof(query), "INSERT INTO house_furniture (house_id, furniture_id, pos_x, pos_y, pos_z, rot_x, rot_y, rot_z) VALUES (%d, %d, %f, %f, %f, %f, %f, %f)",
        GetHouseID(hindex), GetFurnitureID(furniture_index), posx, posy, posz, rotx, roty, rotz);
    new Cache:result = mysql_query(DbHandle, query);
    
    for(new j = 0; j < MAX_HOUSE_FURNITURE; j++)
        if(!HouseFurniture[ hindex ][ j ][ SqlId ])
        {
            i = j;
            break;
        }
    HouseFurniture[ hindex ][ i ][ SqlId ] = cache_insert_id();
    cache_delete(result);
    HouseFurniture[ hindex ][ i ][ FurnitureId ] = GetFurnitureID(furniture_index);
    format(HouseFurniture[ hindex ][ i ][ Name ], MAX_FURNITURE_NAME, GetFurnitureName(furniture_index));
    HouseFurniture[ hindex ][ i ][ ObjectId ] = CreateDynamicObject(GetFurnitureObjectId(furniture_index), 
        posx, posy, posz, 
        rotx, roty, rotz, 
        HOUSE_VIRTUAL_WORLD+hInfo[ hindex ][ hID ],
        .streamdistance = 35.0);

    return HouseFurniture[ hindex ][ i ][ ObjectId ];
}


stock AddHouseWeedSapling(playerid, hindex)
{
    new query[140], index = -1, Float:x, Float:y, Float:z;

    for(new i = 0; i < MAX_HOUSE_WEED_SAPLINGS; i++)
        if(!HouseWeed[ hindex ][ i ][ Id ])
        {
            index = i;
            break;
        }
    if(index == -1)
        return 0;

    GetPlayerPos(playerid, x, y, z);
    z -= 2.7;

    HouseWeed[ hindex ][ index ][ GrowthLevel ] = 0;
    HouseWeed[ hindex ][ index ][ ObjectId ] = CreateDynamicObject(HOUSE_WEED_PLANT_OBJECT, x, y, z, random(20), random(20), random(360), GetHouseVirtualWorld(hindex), GetInteriorInteriorId(GetHouseInteriorID(hindex)));

    mysql_format(DbHandle, query, sizeof(query), "INSERT INTO house_weed (planted_by, house_id, plant_timestamp, x, y, z) VALUES (%d, %d, %d, %f, %f, %f)",
        GetPlayerSqlId(playerid), hInfo[ hindex ][ hID ], gettime(), x, y, z);
    new Cache:result = mysql_query(DbHandle, query);
    HouseWeed[ hindex ][ index ][ Id ] = cache_insert_id();
    cache_delete(result);

    HouseWeed[ hindex ][ index ][ GrowTimer ] = defer WeedGrowTime(HOUSE_WEED_GROW_TIME_MS, hindex, index);
    return 1;
}

/*
                                                    
                 .M"""bgd                           
                ,MI    "Y                           
                `MMb.      ,6"Yb.`7M'   `MF'.gP"Ya  
                  `YMMNq. 8)   MM  VA   ,V ,M'   Yb 
                .     `MM  ,pm9MM   VA ,V  8M"""""" 
                Mb     dM 8M   MM    VVV   YM.    , 
                P"Ybmmd"  `Moo9^Yo.   W     `Mbmmd'                            
*/



stock SaveHouseFurnitureObject(hindex, furniture_index, Float:fX, Float:fY, Float:fZ, Float:fRX, Float:fRY, Float:fRZ)
{
    new query[ 256 ];

    mysql_format(DbHandle, query, sizeof(query), "UPDATE house_furniture SET pos_x = %f, pos_y = %f, pos_z = %f, rot_x = %f, rot_y = %f, rot_z = %f WHERE id = %d",
        fX, fY, fZ, fRX, fRY, fRZ, HouseFurniture[ hindex ][ furniture_index ][ SqlId ]);
    mysql_pquery(DbHandle, query);

    SetDynamicObjectPos(HouseFurniture[ hindex ][ furniture_index ][ ObjectId ], fX, fY, fZ);
    SetDynamicObjectRot(HouseFurniture[ hindex ][ furniture_index ][ ObjectId ], fRX, fRY, fRZ);
}

stock RemoveHouseItem(houseindex, itemindex)
{
    new query[60];
    mysql_format(DbHandle, query, sizeof(query), "DELETE FROM house_items WHERE id = %d",
        HouseItems[ houseindex ][ itemindex ][ SqlId ]);
    mysql_pquery(DbHandle, query);

    HouseItems[ houseindex ][ itemindex ][ SqlId ] = 0;
    HouseItems[ houseindex ][ itemindex ][ ItemId ] = 0;
    HouseItems[ houseindex ][ itemindex ][ Amount ] = 0;
    HouseItems[ houseindex ][ itemindex ][ ContentAmount ] = 0;
    HouseItems[ houseindex ][ itemindex ][ Durability ] = 0;
}


stock SaveHouse(hindex)
{
    new query[350], owner[16], interior[16];

    if(hInfo[ hindex ][ hOwner ] == HOUSE_OWNER_NULL) 
        owner = "NULL";
    else 
        format(owner, sizeof(owner), "\'%d\'", hInfo[ hindex ][ hOwner ]);


    if(hInfo[ hindex ][ hInteriorId ] == INVALID_INTERIOR_ID) 
        interior = "NULL";
    else 
        format(interior, sizeof(interior), "\'%d\'", hInfo[ hindex ][ hInteriorId ]);

    mysql_format(DbHandle, query, sizeof(query), "UPDATE houses SET owner = %s, price = %d, interior_id = %s, entrance_x = %f, entrance_y = %f, entrance_z = %f, \
        entrance_interior = %d, entrance_virtual = %d, locked = %d, bank = %d, rent_price = %d, pickup_model = %d WHERE id = %d",
        owner,
        hInfo[ hindex ][ hPrice ],
        interior,
        hInfo[ hindex ][ hEnter ][ 0 ],
        hInfo[ hindex ][ hEnter ][ 1 ],
        hInfo[ hindex ][ hEnter ][ 2 ],
        hInfo[ hindex ][ hEntranceInt ],
        hInfo[ hindex ][ hEntranceVirw ],
        hInfo[ hindex ][ hLocked ],
        hInfo[ hindex ][ hBank ],
        hInfo[ hindex ][ hRentPrice ],
        hInfo[ hindex ][ hPickupModel ],
        hInfo[ hindex ][ hID ]
    );
    return mysql_pquery(DbHandle, query);
}


stock SetHouseFurnitureTexture(hindex, findex, materialindex, model, txdname[], texture[], color = 0)
{
    new query[512];
    format(query, sizeof(query),"INSERT INTO house_furniture_textures (furniture_id, `index`, object_model, txd_name, texture_name, color) VALUES(%d, %d, %d, '%s', '%s', %d) \
        ON DUPLICATE KEY UPDATE object_model = VALUES(object_model), txd_name = VALUES(txd_name), texture_name = VALUES(texture_name), color = VALUES(color)",
        HouseFurniture[ hindex ][ findex ][ SqlId ], materialindex, model, txdname, texture, color);
    return mysql_pquery(DbHandle, query);
}
stock SetHouseFurnitureTextureColor(hindex, findex, materialindex, color)
{
    new query[160], txd[MAX_TXD_FILE_NAME], texture[ MAX_TEXTURE_NAME ], placeholder;
    GetDynamicObjectMaterial(HouseFurniture[ hindex ][ findex ][ ObjectId ], materialindex, placeholder, txd, texture, placeholder);
    SetDynamicObjectMaterial(HouseFurniture[ hindex ][ findex ][ ObjectId ], materialindex, GetHouseFurnitureObjectModel(hindex, findex), txd, texture, color);

    format(query, sizeof(query), "INSERT INTO house_furniture_textures (furniture_id, `index`, color) VALUES (%d,%d, %d) ON DUPLICATE KEY UPDATE color = VALUES(color)",
        HouseFurniture[ hindex ][ findex ][ SqlId ], materialindex, color);
    return mysql_pquery(DbHandle, query);
}


stock SetHouseFurnitureName(hindex, findex, name[])
{
    new query[100];
    format(HouseFurniture[ hindex ][ findex ][ Name ], MAX_FURNITURE_NAME, name);
    mysql_format(DbHandle, query, sizeof(query), "UPDATE house_furniture SET name = '%e' WHERE id = %d", 
        name, HouseFurniture[ hindex ][ findex ][ SqlId ]);
    return mysql_pquery(DbHandle, query);
}


stock SetHouseEntrancePos(hindex, Float:x, Float:y, Float:z, interior, virtualworld)
{
    new query[170];
    mysql_format(DbHandle, query, sizeof(query), "UPDATE houses SET entrance_x = %f, entrance_y = %f, entrance_z = %f, entrance_interior = %d, entrance_virtual = %d WHERE id = %d",
        x, y, z, interior, virtualworld, hInfo[ hindex ][ hID ]);
    mysql_pquery(DbHandle, query);

    hInfo[ hindex ][ hEnter ][ 0 ] = x;
    hInfo[ hindex ][ hEnter ][ 1 ] = y;
    hInfo[ hindex ][ hEnter ][ 2 ] = z;
    hInfo[ hindex ][ hEntranceInt ] = interior;
    hInfo[ hindex ][ hEntranceVirw ] = virtualworld;
    UpdateHouseInfoText(hindex);
    return 1;
}

stock SetHouseExitLocation(hindex, Float:x, Float:y, Float:z, interiorid)
{
    // interiorid parametras èia yra NE GTA SA interjero ID, o interjero SQL ID(interiors.p).
    new query[120];
    mysql_format(DbHandle, query, sizeof(query), "UPDATE houses SET interior_id = %d, exit_x = %f, exit_y = %f, exit_z = %f WHERE id = %d",
        interiorid, x, y, z, hInfo[ hindex ][ hID ]);
    mysql_pquery(DbHandle, query);

    hInfo[ hindex ][ hInteriorId ] = interiorid;
    hInfo[ hindex ][ hExit ][ 0 ] = x;
    hInfo[ hindex ][ hExit ][ 1 ] = y;
    hInfo[ hindex ][ hExit ][ 2 ] = z;
}
stock SetHouseInteriorId(hindex, interiorid)
{
    // interiorid parametras èia yra NE GTA SA interjero ID, o interjero SQL ID(interiors.p).
    new query[120], Float:x, Float:y, Float:z;
    GetInteriorEntrancePos(interiorid, x, y, z);
    mysql_format(DbHandle, query, sizeof(query), "UPDATE houses SET interior_id = %d, exit_x = %f, exit_y = %f, exit_z = %f WHERE id = %d",
        interiorid, x, y, z, hInfo[ hindex ][ hID ]);
    mysql_pquery(DbHandle, query);

    hInfo[ hindex ][ hInteriorId ] = interiorid;
    hInfo[ hindex ][ hExit ][ 0 ] = x;
    hInfo[ hindex ][ hExit ][ 1 ] = y;
    hInfo[ hindex ][ hExit ][ 2 ] = z;
    return 1;
}



stock SetHousePickupModel(hindex, modelid)
{
    new query[80];
    mysql_format(DbHandle, query, sizeof(query), "UPDATE houses SET pickup_model = %d WHERE id = %d",
        modelid, hInfo[ hindex ][ hID ]);
    mysql_pquery(DbHandle, query);

    hInfo[ hindex ][ hPickupModel ] = modelid;
    UpdateHouseInfoText(hindex);
    return 1;
}


stock SetHouseItem(hindex, slot, itemid, amount, contentamount, durability)
{
    new query[180];

    HouseItems[ hindex ][ slot ][ ItemId ] = itemid;
    HouseItems[ hindex ][ slot ][ Amount ] = amount;
    HouseItems[ hindex ][ slot ][ ContentAmount ] = contentamount;
    HouseItems[ hindex ][ slot ][ Durability ] = durability;

    if(HouseItems[ hindex ][ slot ][ SqlId ])
    {
        mysql_format(DbHandle, query, sizeof(query), "UPDATE house_items SET item_id = %d, amount = %f, content_amount = %d, durability = %d WHERE id = %d",
            itemid, amount, contentamount, durability, HouseItems[ hindex ][ slot ][ SqlId ]);
        return mysql_pquery(DbHandle, query);
    }
    else 
    {
        mysql_format(DbHandle, query, sizeof(query),"INSERT INTO house_items (house_id, item_id, slot, amount, content_amount, durability) VALUES(%d, %d, %d, %d, %d, %d)",
            hInfo[ hindex ][ hID ], itemid, slot, amount, contentamount, durability);
        new Cache:result = mysql_query(DbHandle, query);
        HouseItems[ hindex ][ slot ][ SqlId ] = cache_insert_id();
        cache_delete(result);
        return 1;
    }
}

/*
                                                                                   
                                                                                   
                    `7MM"""Mq.                                                     
                      MM   `MM.                                                    
                      MM   ,M9  .gP"Ya `7MMpMMMb.pMMMb.  ,pW"Wq.`7M'   `MF'.gP"Ya  
                      MMmmdM9  ,M'   Yb  MM    MM    MM 6W'   `Wb VA   ,V ,M'   Yb 
                      MM  YM.  8M""""""  MM    MM    MM 8M     M8  VA ,V  8M"""""" 
                      MM   `Mb.YM.    ,  MM    MM    MM YA.   ,A9   VVV   YM.    , 
                    .JMML. .JMM.`Mbmmd'.JMML  JMML  JMML.`Ybmd9'     W     `Mbmmd' 
                                                                                   
                                                                                   
*/


stock DeleteHouseFurniture(hindex, furniture_index)
{
    new string[ 64 ];
    format( string, sizeof string, "DELETE FROM `house_furniture` WHERE `id` = %d",
        HouseFurniture[ hindex ][ furniture_index ][ SqlId ] );
    mysql_pquery(DbHandle, string);

    DestroyDynamicObject(HouseFurniture[ hindex ][ furniture_index ][ ObjectId]);
    HouseFurniture[ hindex ][ furniture_index ][ SqlId ] = 0;
    HouseFurniture[ hindex ][ furniture_index ][ FurnitureId ] = 0;
    return 1;
}


stock DeleteHouse(hindex)
{
    new query[ 128 ];
    format(query, sizeof(query), "DELETE FROM houses WHERE id = %d",
        hInfo[ hindex ][ hID ]);
    mysql_pquery(DbHandle, query);

    Itter_Remove(Houses, hindex);
    hInfo[ hindex ][ hID ] = 0;
    DestroyDynamicPickup(hInfo[ hindex ][ hPickup ]);
    DestroyDynamic3DTextLabel(hInfo[ hindex ][ hLabel ]);
    return 1;
}

stock DeleteHouses()
{
    new string[ 128 ];
    
    new Cache:result = mysql_query(DbHandle, "SELECT id, owner, price FROM houses WHERE owner IS NOT NULL");
    for(new i = 0; i < cache_get_row_count(); i++)
    {
        mysql_format(DbHandle, string, sizeof(string), "INSERT INTO `komp`(kam, ka, kiek) VALUES(%d, %d, %d)",
            cache_get_field_content_int(i, "owner"), cache_get_field_content_int(i, "id"), cache_get_field_content_int(i, "price"));
        mysql_pquery(DbHandle, string);
    }
    cache_delete(result);
    mysql_pquery(DbHandle, "DELETE FROM houses");

    static EmptyHouse[ E_HOUSE_DATA ]; // Tuðti namo duomenys.

    foreach(new i : Houses) 
    {
        if(IsValidDynamicPickup(hInfo[ i ][ hPickup ]))
            DestroyDynamicPickup(hInfo[ i ][ hPickup ]);
        if(IsValidDynamic3DTextLabel(hInfo[ i ][ hLabel ]))
            DestroyDynamic3DTextLabel(hInfo[ i ][ hLabel ]);

        hInfo[ i ] = EmptyHouse;
        new next;
        Itter_SafeRemove(Houses, i, next);
        i = next;
    }
    return 1;
}

stock RemoveHouseFurnitureTexture(hindex, findex, materialindex)
{
    new query[100], color, model;
    GetDynamicObjectMaterial(HouseFurniture[ hindex ][ findex ][ ObjectId ], materialindex, model, query, query, color);
    SetDynamicObjectMaterial(HouseFurniture[ hindex ][ findex ][ ObjectId ], materialindex, GetHouseFurnitureObjectModel(hindex, findex), "none", "none", color);

    format(query, sizeof(query), "DELETE FROM house_furniture_textures WHERE furniture_id = %d AND `index` = %d",
        HouseFurniture[ hindex ][ findex ][ SqlId ], materialindex);
    return mysql_pquery(DbHandle, query);
}

stock RemoveHouseOwner(hindex)
{
    new query[60];
    format(query, sizeof(query), "UPDATE houses SET owner = NULL WHERE id = %d",
        hInfo[ hindex ][ hID ]);
    mysql_pquery(DbHandle, query);
    hInfo[ hindex ][ hOwner ] = HOUSE_OWNER_NULL;
    hInfo[ hindex ][ hRentPrice ] = 0;
    UpdateHouseInfoText(hindex);
    return 1;
}

stock DestroyHouseWeed(playerid, houseindex)
{
    new query[100];

    for(new i = 0; i < MAX_HOUSE_WEED_SAPLINGS; i++)
    {
        if(!HouseWeed[ houseindex ][ i ][ Id ])
            continue;

        stop HouseWeed[ houseindex ][ i ][ GrowTimer ];
        HouseWeed[ houseindex ][ i ][ GrowthLevel ] = 0;
    }

    mysql_format(DbHandle, query, sizeof(query), "UPDATE house_weed SET harvested_by = %d, yield = 0 WHERE house_id = %d",
        GetPlayerSqlId(playerid), hInfo[ houseindex ][ hID ]);
    return mysql_pquery(DbHandle, query);
}

/*
                                                                           
                             ,,                                            
                MMP""MM""YMM db                                            
                P'   MM   `7                                               
                     MM    `7MM  `7MMpMMMb.pMMMb.  .gP"Ya `7Mb,od8 ,pP"Ybd 
                     MM      MM    MM    MM    MM ,M'   Yb  MM' "' 8I   `" 
                     MM      MM    MM    MM    MM 8M""""""  MM     `YMMMa. 
                     MM      MM    MM    MM    MM YM.    ,  MM     L.   I8 
                   .JMML.  .JMML..JMML  JMML  JMML.`Mbmmd'.JMML.   M9mmmP' 
                                                                           
                                                                           

*/

timer WeedGrowTime[grow_time](grow_time, houseindex, weedindex)
{
    new query[100], Float:x, Float:y, Float:z;
    grow_time = 0;
    HouseWeed[ houseindex ][ weedindex ][ GrowthLevel ]++;
    mysql_format(DbHandle, query, sizeof(query), "UPDATE house_weed SET growth_timestamp = %d, growth_level = %d WHERE id = %d",
        gettime(), HouseWeed[ houseindex ][ weedindex ][ GrowthLevel ], HouseWeed[ houseindex ][ weedindex ][ Id ]);
    mysql_pquery(DbHandle, query);

    if(HouseWeed[ houseindex ][ weedindex ][ GrowthLevel ] < MAX_HOUSE_WEED_GROWTH_LEVEL)
        HouseWeed[ houseindex ][ weedindex ][ GrowTimer ] = defer WeedGrowTime(HOUSE_WEED_GROW_TIME_MS, houseindex, weedindex);

    GetDynamicObjectPos(HouseWeed[ houseindex ][ weedindex ][ ObjectId ], x, y, z);
    SetDynamicObjectPos(HouseWeed[ houseindex ][ weedindex ][ ObjectId ], x, y, z+(HOUSE_WEED_PLANT_HEIGHT / MAX_HOUSE_WEED_GROWTH_LEVEL));
}


CMD:lygiai(playerid)
{

    new hindex = GetPlayerHouseIndex(playerid), string[126];
    if(hindex == -1)
        return 0;

    for(new i = 0; i < MAX_HOUSE_WEED_SAPLINGS; i++)
    {
        if(!HouseWeed[ hindex ][ i ][ Id ])
            continue;

        format(string, sizeof(string), "Augalo lygis:%d jo sqlid:%d. I:%d", HouseWeed[ hindex ][ i ][ GrowthLevel ], HouseWeed[ hindex ][ i ][ Id ],
            i);
        SendClientMessage(playerid, COLOR_PURPLE, string);
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

CMD:housewithdraw(playerid, params[])
{
    new house_index = GetPlayerHouseIndex(playerid, true),
        money,
        string[100];

    if(house_index == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs turite bûti prie namo áëjimo arba jo viduje.");

    if(!IsPlayerHouseOwner(playerid, house_index))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, namas turi priklausyti Jums, kad atliktumët ðá veiksmà.");

    if(sscanf(params, "d", money)) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /housewithdraw [SUMA]");

    if(hInfo[ house_index ][ hBank ] < money || money < 0 )
    {
        format(string, sizeof(string), "{FF6347}Klaida, negalite iðimti tokios sumos, kadangi namo seife yra tik %d$", hInfo[ house_index ][ hBank ] );
        SendClientMessage(playerid, GRAD, string );
        return 1;
    }

    GivePlayerMoney( playerid, money );
    hInfo[ house_index ][ hBank ] -= money;
    PayLog(GetPlayerSqlId(playerid), 17, hInfo[ house_index ][ hOwner ], money);
    SaveHouse(house_index);
    SaveAccount(playerid);
    return 1;
}

CMD:housedeposit(playerid, params[])
{
    new house_index = GetPlayerHouseIndex(playerid, true),
        money,
        string[70];

    if(house_index == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs turite bûti prie namo áëjimo arba jo viduje.");

    if(!IsPlayerHouseOwner(playerid, house_index))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, namas turi priklausyti Jums, kad atliktumët ðá veiksmà.");

    if(sscanf(params, "d", money)) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /housedeposit [SUMA]");

    if(money < 0 || money > MAX_HOUSE_DEPOSIT ) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodyta suma negali bøti maþesnë uþ 1$ arba didesnë uþ " #MAX_HOUSE_DEPOSIT "$" );

    if(PlayerMoney[ playerid ] < money) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite dëti sumos, kurios neturite.");

    format(string, sizeof(string),"Padëjote %d á namo seifà, dabar jame yra $%d", money, hInfo[ house_index ][ hBank ]);
    SendClientMessage(playerid, COLOR_NEWS, string);

    GivePlayerMoney( playerid, -money );
    hInfo[ house_index ][ hBank ] += money;
    PayLog(GetPlayerSqlId(playerid), 19, hInfo[ house_index ][ hOwner ], money );
    SaveHouse(house_index);
    SaveAccount(playerid);
    return 1;
}

CMD:houseinfo(playerid)
{
    new house_index = GetPlayerHouseIndex(playerid, true),
        string[60];

    if(house_index == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs turite bûti prie namo áëjimo arba jo viduje.");

    if(!IsPlayerHouseOwner(playerid, house_index))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, namas turi priklausyti Jums, kad atliktumët ðá veiksmà.");

    format(string, sizeof(string), "Ðiuo metu namo banke yra: %d$", hInfo[ house_index ][ hBank ] );
    SendClientMessage(playerid, GRAD, string );
    return 1;
}

CMD:hinv(playerid, params[])
{
    if(Mires[ playerid ] > 0 )   
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, Jûsø veikëjas ðiuo metu yra kritinëje arba komos bûsenoje." );

    new house_index = GetPlayerHouseIndex(playerid);
    if(house_index == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs turite bûti namo viduje.");

    if(!IsPlayerHouseOwner(playerid, house_index))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, namas turi priklausyti Jums, kad atliktumët ðá veiksmà.");

    ShowHouseInv(playerid, house_index);
    return 1;
}

CMD:hradio(playerid)
{
    new sendername[ MAX_PLAYER_NAME ];
    GetPlayerName( playerid, sendername, MAX_PLAYER_NAME );

    new house_index = GetPlayerHouseIndex(playerid),
        string[100];

    if(house_index == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs turite bûti namo viduje.");

    if(!IsPlayerHouseOwner(playerid, house_index))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, namas turi priklausyti Jums, kad atliktumët ðá veiksmà.");

    if(!IsHouseUpgradeInstalled(house_index, Radio)) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Name nëra garso sistemos.");

    format(string, sizeof(string), "- Radijo stotys\
                                \n- Garsumas \t[ %d ]\
                                \n- Iðjungti", GetRadioVolume(playerid));
    SetPVarInt(playerid, "HouseIndex", house_index);
    ShowPlayerDialog(playerid, DIALOG_HOUSE_RADIO, DIALOG_STYLE_LIST,"Garso sistema", string, "Rinktis", "Atsaukti");
    return 1;
}

CMD:tenantry(playerid, params[])
{
    new house_index = GetPlayerHouseIndex(playerid, true),
        string[80],
        count = 0,
        Cache:result;

    if(house_index == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs turite bûti prie namo áëjimo arba jo viduje.");

    if(!IsPlayerHouseOwner(playerid, house_index))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, namas turi priklausyti Jums, kad atliktumët ðá veiksmà.");

    format(string, sizeof(string), "SELECT Name FROM players WHERE House = %d AND id != %d", hInfo[ house_index ][ hID ], GetPlayerSqlId(playerid));
    result = mysql_query(DbHandle, string);

    if(cache_get_row_count())
        SendClientMessage(playerid, COLOR_GREEN2, "__________Nuomininkai__________");
    else 
        SendClientMessage(playerid, COLOR_GREEN2, "Jûsø namo niekas nesinuomoja.");
    
    for(new i = 0; i < cache_get_row_count(); i++)
    {
        cache_get_field_content(i, "Name", string);
        format(string, sizeof(string), "%d. %s", count++, string);
        SendClientMessage(playerid, GRAD, string);
    }
    cache_delete(result);
    return 1;
}
CMD:evict(playerid, params[])
{
    new house_index = GetPlayerHouseIndex(playerid, true),
        targetid;

    if(house_index)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs turite bûti prie namo áëjimo arba jo viduje.");

    if(!IsPlayerHouseOwner(playerid, house_index))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, namas turi priklausyti Jums, kad atliktumët ðá veiksmà.");

    if(sscanf(params, "u", targetid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /evict [Þaidëjo ID/ Dalis vardo] ");
    if(!IsPlayerConnected(targetid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");
    if(targetid == playerid)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite iðmesti savæs ið savo paties namø.");
    if(pInfo[ playerid ][ pHouseKey ] != hInfo[ house_index ][ hID ])
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: þmogus nesinuomoja jûsø name.");
            
    pInfo[ targetid ][ pSpawn ] = DefaultSpawn;
    pInfo[ targetid ][ pHouseKey ] = 0;
    SendClientMessage(targetid, COLOR_WHITE, " * Jûs buvote iðkeldintas ið nuomojamo namo.");
    SendClientMessage(playerid, COLOR_WHITE, " * Nuomininkas buvo sëkmingai iðkeldintas. ");
    return 1;
}
CMD:evictall(playerid)
{
    new house_index = GetPlayerHouseIndex(playerid, true),
        string[70];

    if(house_index == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs turite bûti prie namo áëjimo arba jo viduje.");

    if(!IsPlayerHouseOwner(playerid, house_index))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, namas turi priklausyti Jums, kad atliktumët ðá veiksmà.");

    foreach(new i : Player)
    {
        if(pInfo[ i ][ pHouseKey ] == hInfo [ house_index ][ hID ])
        {
            pInfo[ i ][ pSpawn ] = DefaultSpawn;
            pInfo[ i ][ pHouseKey ] = 0;
            SendClientMessage(i, COLOR_WHITE, " * Jûs buvote iðkeldintas ið nuomojamo namo.");
        }
    }

    format(string, sizeof(string), "UPDATE `players` SET `House` = 0 WHERE `House` = %d", hInfo [ house_index ][ hID ]);
    new Cache:result = mysql_query(DbHandle, string);
    format(string, sizeof(string), "Viso nuomininkø buvo iðmesta: %d", cache_affected_rows());
    cache_delete(result);
    SendClientMessage( playerid, GRAD, string);
    return 1;
}

CMD:eat(playerid)
{
    new house_index = GetPlayerHouseIndex(playerid),
        string[100],
        Float:Health;

    if(house_index == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs turite bûti namo viduje.");

    if(!hInfo[ house_index ][ hUpgrades ][ Refrigerator ])
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Jûsø name nëra irengta buitinë technika.");

    if(pInfo[ playerid ][ pLevel ] < 2)
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Jûsø Lygis per maþas, minimalus 2 Lygis.");

    if(GetPVarInt(playerid, "EATING" ) > 1 )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs jau valgote. ");

    format(string, sizeof(string), "* %s paima valgio ið ðaldytuvo ir pradeda valgyti.", GetPlayerNameEx(playerid));
    ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
    GetPlayerHealth( playerid, Health );
    if (Health < 100 - 10)
    {
        SetPlayerHealth(playerid, Health + 10);
        pInfo[ playerid ][ pHunger ] -= 5;
    }
    else if(Health > 100 - 10 )
        SetPlayerHealth( playerid, 100);
    
    return 1;
}

CMD:hu(playerid, params[])
{
    new house_index = GetPlayerHouseIndex(playerid, true);

    if(isnull(params))
    {
        hu_help:
        SendClientMessage( playerid, COLOR_GREEN2, "__________ Namo patobulinimas __________" );
        SendClientMessage( playerid, COLOR_FADE1, "Maistas, kaina: $8000 (( /eat ))");
        SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /hu maistas");
        return 1;
    }
    if(house_index == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs turite bûti prie namo áëjimo arba jo viduje.");

    if(!IsPlayerHouseOwner(playerid, house_index))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, namas turi priklausyti Jums, kad atliktumët ðá veiksmà.");

    if(!strcmp("maistas",params,true))
    {
        if(hInfo[ house_index ][ hUpgrades ][ Refrigerator ])
            return SendClientMessage(playerid, GRAD, "Name jau yra ðis patobulinimas. ");

        if(PlayerMoney[ playerid ] < 8000)
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Neturite pakankamai pingø.");

        GivePlayerMoney(playerid, -8000 );
        SendClientMessage(playerid, COLOR_GREEN, "Á nama sëkimngai buvo árengta maisto ruoðimo galimybë.");
        AddHouseUpgrade(house_index, Refrigerator);
        SaveAccount(playerid);
        return 1;
    }
    else 
        goto hu_help;
    return 1;
}

CMD:buyhouse(playerid, params[])
{
    new house_index = GetPlayerHouseIndex(playerid, true),
        string[70];

    if(pInfo[ playerid ][ pLevel ] < 3) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nusipirkti namà galësite tik pasiekæs 3 lygá.");

    if(house_index == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida. Jûs turite bûti prie namo áëjimo.");

    if(IsHouseOwned(house_index))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida. Ðis namas neparduodamas.");

    if(PlayerMoney[ playerid ] < hInfo[ house_index ][ hPrice ]) 
        return SendClientMessage(playerid, COLOR_LIGHTRED,"Klaida, Jums nepakanka grynøjø pinigø, kad galëtumete nusipirkti namà prie kurio esate.");

    format(string, sizeof(string), "Sëkmingai ásigijote namà uþ %d.", hInfo[ house_index ][ hPrice ]);
    SendClientMessage(playerid, COLOR_NEWS, string);
    pInfo[ playerid ][ pHouseKey ] = hInfo[ house_index ][ hID ];
    hInfo[ house_index ][ hOwner ] = GetPlayerSqlId(playerid);
    GivePlayerMoney(playerid, -hInfo[ house_index ][ hPrice ]);
    PayLog(GetPlayerSqlId(playerid), 1, -1, -hInfo[ house_index ][ hPrice ]);
    UpdateHouseInfoText(house_index);
    SaveHouse(house_index);
    return 1;
}

CMD:setrent(playerid, params[])
{
    new house_index = GetPlayerHouseIndex(playerid, true),
        price,
        string[50];

    if(house_index == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs turite bûti prie namo áëjimo arba jo viduje.");

    if(!IsPlayerHouseOwner(playerid, house_index))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, namas turi priklausyti Jums, kad atliktumët ðá veiksmà.");

    if(sscanf(params, "d", price)) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /setrent [NUOMOS KAINÀ NUOMININKAMS]");
    
    if(price < 0 || price > MAX_HOUSE_RENT_PRICE) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio, nuomos kainà gali bûti nustatyta nuo 1$ iki " #MAX_HOUSE_RENT_PRICE "$");

    hInfo[ house_index ][ hRentPrice ] = price;
    format(string, sizeof(string),"Namo nuomos mokestis pakeistas á: %d", price);
    SendClientMessage(playerid,COLOR_NEWS,string);
    UpdateHouseInfoText(house_index);
    SaveHouse(house_index);
    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
    return 1;
}

CMD:rentroom(playerid, params[])
{
    new house_index = GetPlayerHouseIndex(playerid, true);

    if(house_index == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida. Jûs turite bûti prie namo áëjimo.");

    if(!IsHouseOwned(house_index))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida. Ðis nepriema nuomininkø.");

    if(!hInfo[ house_index ][ hRentPrice ]) 
        return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, ðis namas nenomuoja jokiø kambariø.");

    if(hInfo[ house_index ][ hRentPrice ] > PlayerMoney[ playerid ]) 
        return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, neturite pakankamai pinigø, kad nomuotumëtës ðá namà. Paþiûrëkite á nustatytà nuomos kainà.");
   
    pInfo[playerid][pHouseKey] = hInfo[ house_index ][ hID ];
    GivePlayerMoney(playerid, -hInfo[ house_index ][ hRentPrice]);
    PayLog(GetPlayerSqlId(playerid), 10, hInfo[ house_index ][ hOwner ], hInfo[ house_index ][ hRentPrice ] );
    hInfo[ house_index ][ hBank ] += hInfo[ house_index ][ hRentPrice ];
    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
    SendClientMessage(playerid,COLOR_NEWS,"Sveikiname, sëkmingai iðsinuomavote kambará ðiame name. Nusitatykite atsiradimo vietà su komanda /setspawn.");
    SaveHouse(house_index);
    SaveAccount(playerid);
    return 1;
}


CMD:unrent(playerid)
{
    if(!IsPlayerRentingAnyHouse(playerid)) 
        return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, negalite atsisakyti namo nuomos, jei nesinuomuojate jokio namo.");

    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
    pInfo[playerid][pHouseKey] = 0;
    pInfo[playerid][pSpawn] = DefaultSpawn;
    SendClientMessage(playerid,COLOR_NEWS,"Sveikiname, sëkmingai atsisakëte dabartinio gyvenamojo namo nuomos. Nuo ðiol atsirasite nebe ðiame name.");
    SaveAccount(playerid);
    return 1;
}

CMD:sellhouse(playerid, params[])
{

    new house_index = GetPlayerHouseIndex(playerid, true),
        string[50],
        giveplayerid,
        price,
        IP[ 16 ],
        IP2[ 16 ];

    if(house_index == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs turite bûti prie namo áëjimo arba jo viduje.");

    if(!IsPlayerHouseOwner(playerid, house_index))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, namas turi priklausyti Jums, kad atliktumët ðá veiksmà.");

    if(sscanf(params, "ud", giveplayerid, price)) 
        return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /sellhouse [þaidëjo id][kaina]");

    if(!IsPlayerConnected(giveplayerid))  
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");

    if (!IsPlayerInRangeOfPlayer(playerid, giveplayerid, 10.0))  
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti ðio veiksmo su veikëju, kuris nëra ðalia Jûsø.");

    if(price < 0 || price > MAX_HOUSE_SELL_PRICE) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Kaina negali buti maþesnë negu 0 ir didesnë negu " #MAX_HOUSE_SELL_PRICE ".");


    GetPlayerIp( playerid, IP, 16 );
    GetPlayerIp( giveplayerid, IP2, 16 );

    if(!strcmp(IP, IP2, true ) || pInfo[ playerid ][ pUcpID ] == pInfo[ giveplayerid ][ pUcpID ] )
        return true;


    format(string,sizeof(string),"Jûs siulote jam %s, kad jis nupirktu Jûsø namà uþ: $%d.",GetPlayerNameEx(giveplayerid),price);
    SendClientMessage(playerid,COLOR_WHITE,string);
    format(string,sizeof(string),"Namo savininkas %s siølo jums nupirkti jo namà  uþ: $%d, jeigu sutinkate, raðykite /accept house.",GetPlayerNameEx(playerid),price);
    SendClientMessage(giveplayerid,COLOR_WHITE,string);
    Offer[giveplayerid][1] = playerid;
    OfferPrice[giveplayerid][1] = price;
    OfferID[giveplayerid][1] = house_index;
    return 1;
}


CMD:cutweed(playerid)
{
    //if(pInfo[ playerid ][ pJob ] != JOB_DRUGS) 
    //    return SendClientMessage(playerid, COLOR_LIGHTRED, "{FF6347}Klaida, negalite naudotis ðia galimybe nebûdamas narkotiku prekeiviu.");

    new house_index = GetPlayerHouseIndex(playerid),
        string[50],
        yield,
        plantcount;

    if(house_index == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs turite bûti namo viduje.");

    if(!IsPlayerHouseOwner(playerid, house_index))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, namas turi priklausyti Jums, kad atliktumët ðá veiksmà.");

    for(new i = 0; i < MAX_HOUSE_WEED_SAPLINGS; i++)
        if(HouseWeed[ house_index ][ i ][ Id ] && HouseWeed[ house_index ][ i ][ GrowthLevel ] >= MAX_HOUSE_WEED_GROWTH_LEVEL)
        {
            plantcount++;
            yield += HarvestHouseWeedPlant(playerid, house_index, i);
        }

    if(!plantcount)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûsø namuose nëra uþaugusios þolës.");

    if(IsPlayerInventoryFull(playerid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûsø inventoriuje nebëra vietos.");


    format(string, sizeof(string), "Nuëmëte derliø ið %d augalo(ø) ir gavote %d gramø þolës.", plantcount, yield);
    SendClientMessage(playerid, COLOR_WHITE, string);

    GivePlayerItem(playerid, ITEM_WEED, yield);
    return 1;
}

CMD:cutdownweed(playerid)
{
    if(!UsePDCMD(playerid)) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûs nesate pareigûnas, kad naudotumëtës ðia komanda..");

    new house_index = GetPlayerHouseIndex(playerid),
        string[50];

    if(house_index == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs turite bûti prie namo áëjimo arba jo viduje.");

    if(!GetHouseWeedPlantCount(house_index))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðiame name neauga þolë.");

    format(string, sizeof(string), "Sunaikinote %d þolës augalus.", GetHouseWeedPlantCount(house_index));
    SendClientMessage(playerid, COLOR_POLICE, string);

    DestroyHouseWeed(playerid, house_index);
    return 1;

}

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




#define DIALOG_HMENU_MAIN               20
#define DIALOG_HMENU_NEW_PRICE          21
#define DIALOG_HMENU_INPUT_INDEX        13717
#define DIALOG_HMENU_NEW_PICKUP_MODEL   13718
#define DIALOG_HMENU_CONFIRM_WIPE       13719

static PlayerUsedHouseIndex[ MAX_PLAYERS ] = {-1, ... };


enum E_HOUSE_INDEX_USAGE 
{
    HousePriceChange,
    HouseRemoveOwner,
    HouseRemoveHouse,
    HouseEntrancePositionChange,
    HouseInteriorChange,
    HouseInfo,
    HouseInputNewPickupModel,
};


stock HouseManagementDialog.ShowMain(playerid)
{
    ShowPlayerDialog(playerid, DIALOG_HMENU_MAIN, DIALOG_STYLE_LIST,"Namø meniu",
        "- Kurti naujà \n\
        - Keisti kaina\n\
        - Paðalinti savininkà\n\
        - Paðalinti\n\
        - Perkelti áëjimà pagal ID\n\
        - Þiûrëti namo interjerus\n\
        - Keisti iðëjimo pozicijà pagal namo ID\n\
        - Þiûrëti namo informacijà\n\
        - Keisti pickup modelá\n\
        - Iðtrinti ir kompensuoti visus namus\n", "Rinktis", "Atðaukti" );
    return 1;
}

stock HouseManagementDialog.InputNewPrice(playerid, errostr[] = "")
{
    new string[64];
    if(!isnull(errostr))
    {
        strcat(string, "{AA0000}");
        strcat(string, errostr);
        strcat(string,"\n{FFFFFF}");

    }
    strcat(string, "Áraðykite norima namo kainà:");
    ShowPlayerDialog(playerid, DIALOG_HMENU_NEW_PRICE, DIALOG_STYLE_INPUT,"Namo kainos pakeitimas", string, "Patvirtinti", "Atðaukti");
    return 1;
}

stock HouseManagementDialog.InputIndex(playerid, E_HOUSE_INDEX_USAGE:usage, errostr[] = "")
{
    new string[128];
    if(!isnull(errostr))
    {
        strcat(string, "{AA0000}");
        strcat(string, errostr);
        strcat(string,"\n{FFFFFF}");

    }
    strcat(string, "Áraðykite norimo redaguoti namo ID:");
    SetPVarInt(playerid, "IndexUsage", _:usage);
    ShowPlayerDialog(playerid, DIALOG_HMENU_INPUT_INDEX, DIALOG_STYLE_INPUT,"Namo ID áraðmas", string,"Patvirtinti", "Atgal");
    return 1;
}

stock HouseManagementDialog.InputNewPickupModel(playerid, errostr[] = "")
{
    new string[128];
    if(!isnull(errostr))
    {
        strcat(string, "{AA0000}");
        strcat(string, errostr);
        strcat(string,"\n{FFFFFF}");

    }
    strcat(string, "Áraðykite norima namo pickup modelá:");
    ShowPlayerDialog(playerid, DIALOG_HMENU_NEW_PICKUP_MODEL, DIALOG_STYLE_INPUT,"Namo pickup modelio pakeitimas", string, "Patvirtinti", "Atðaukti");
    return 1;
}

stock HouseManagementDialog.Information(playerid, hindex)
{
    new string[ 2048 ];
    format(string, sizeof(string),"Namo ID serveryje: %d\n\
        Namo ID duomenø bazëje: %d\n\
        Namo savininkas: %s\n\
        Namas uþrakintas: %s\n\
        Interjero ID: %d\n\
        Kaina: %d\n\
        Nuomos kaina: %d\n\
        Pickup modelis: %d\n\
        Ðaldytuvas: %s\n\
        Radijas: %s\n\n\
        Baldø skaièius: %d",
        hindex,
        hInfo[ hindex][ hID ],
        (hInfo[ hindex ][ hOwner ] == HOUSE_OWNER_NULL) ? ("nëra") : (GetSqlIdName(hInfo[ hindex ][ hOwner ])),
        (hInfo[ hindex] [ hLocked ]) ? ("Taip") : ("Ne"),
        hInfo[ hindex ][ hInteriorId ],
        hInfo[ hindex ][ hPrice ],
        hInfo[ hindex ][ hRentPrice ],
        hInfo[ hindex ][ hPickupModel ],
        (hInfo[ hindex ][ hUpgrades ][ Refrigerator ]) ? ("Yra") : ("Nëra"),
        (hInfo[ hindex ][ hUpgrades ][ Radio ]) ? ("Yra") : ("Nëra"),
        GetHouseFurnitureCount(hindex)
    );
    strcat(string, "\n\n___Name esantys daiktai___\n\n");

    for(new i = 0; i < MAX_HOUSE_ITEMS; i++)
        if(HouseItems[ hindex ][ i ][ SqlId ])
            format(string, sizeof(string),"%s%s %d\n",
                string,
                GetItemName(HouseItems[ hindex ][ i ][ ItemId ]),
                HouseItems[ hindex ][ i ][ Amount ]);

    ShowPlayerDialog(playerid, 9999, DIALOG_STYLE_MSGBOX, "Namo informcija", string, "Gerai", "");
    return 1;
}

stock HouseManagementDialog.ConfirmWhipe(playerid)
{
    ShowPlayerDialog(playerid, DIALOG_HMENU_CONFIRM_WIPE, DIALOG_STYLE_MSGBOX, "{FF0000}Dëmesio!", "Ðis veiksmas paðalins visus serveryje esanèius namus ir jø informacijà\nVerslo kaina bus graþinta savininkui.\nÐio proceso atstatyti neámanoma.", "Tæsti", "Iðeiti");
    return 1;
}

stock HouseManagementDialog.OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
    switch(dialogid)
    {
        case DIALOG_HMENU_MAIN:
        {
            if(!response)
                return 1;

            new index = GetPlayerHouseIndex(playerid, true);
            PlayerUsedHouseIndex[ playerid ] = index;
            switch(listitem)
            {
                // Naujas namas 
                case 0:
                {
                    new Float:x, Float:y, Float:z;
                    GetPlayerPos(playerid, x, y, z);

                    new house = AddHouse(x, y, z, GetPlayerInterior(playerid), GetPlayerVirtualWorld(playerid));
                    if(house == -1)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "KLAIDA. Namo sukurti nepavyko.");

                    new string[70];
                    format(string, sizeof(string), "Namas buvo sëkmingai sukurtas, jo id: %d", house);
                    SendClientMessage(playerid, COLOR_LIGHTRED, string);
                    Streamer_Update(playerid);
                }

                case 1: // Namo kainos keitimas
                {
                    if(index == -1)
                    {
                        HouseManagementDialog.InputIndex(playerid, HousePriceChange);
                    }
                    else 
                    {
                        HouseManagementDialog.InputNewPrice(playerid);
                    }
                }
                // Namo savininko paðalinimas 
                case 2:
                {
                    if(index == -1)
                    {
                        HouseManagementDialog.InputIndex(playerid, HouseRemoveOwner);
                    }
                    else 
                    {
                        RemoveHouseOwner(index);
                        SendClientMessage(playerid, COLOR_NEWS, "Namo savininkas sëkmingai paðalintas.");
                    }
                }
                // Namo paðalinimas
                case 3:
                {
                    if(index == -1)
                        HouseManagementDialog.InputIndex(playerid, HouseRemoveHouse);
                    else 
                    {
                        DeleteHouse(index);
                        SendClientMessage(playerid, COLOR_NEWS, "Namas paðalintas sëkmingai.");
                    }
                }
                // Namo áëjimo keitimas ávedant indeksà
                case 4:
                {
                    HouseManagementDialog.InputIndex(playerid, HouseEntrancePositionChange);
                }
                // Namø interjerø perþiûra 
                case 5:
                {   
                    ShowInteriorPreviewForPlayer(playerid, "house");
                }   
                // Interjero pozicijos keitimas
                case 6:
                {
                    HouseManagementDialog.InputIndex(playerid, HouseInteriorChange);
                }
                // Namo informacijos þiûrëjimas
                case 7:
                {
                    if(index == -1)
                    {
                        HouseManagementDialog.InputIndex(playerid, HouseInfo);
                    }
                    else 
                    {
                        HouseManagementDialog.Information(playerid, index);
                    }
                }
                // Keisti pickup modeli 
                case 8:
                {
                    if(index == -1)
                    {
                        HouseManagementDialog.InputIndex(playerid, HouseInputNewPickupModel);
                    }
                    else 
                    {
                        HouseManagementDialog.InputNewPickupModel(playerid);
                    }
                }
                // Paðalinti visus egzistuojanèius namus
                case 9:
                {   
                    HouseManagementDialog.ConfirmWhipe(playerid);
                }
            }
            return 1;
        }
        case DIALOG_HMENU_INPUT_INDEX:
        {
            if(!response)
                return HouseManagementDialog.ShowMain(playerid);

            new index,
                E_HOUSE_INDEX_USAGE:usage = E_HOUSE_INDEX_USAGE: GetPVarInt(playerid, "IndexUsage");

            if(sscanf(inputtext, "i", index))
                return HouseManagementDialog.InputIndex(playerid, usage, "Áveskite skaièiø.");
            if(index < 0 || index >= MAX_HOUSES || !IsValidHouse(index))
                return HouseManagementDialog.InputIndex(playerid, usage, "Namo su tokiu ID nëra.");

            PlayerUsedHouseIndex[ playerid ] = index;
            switch(usage)
            {
                case HousePriceChange: HouseManagementDialog.InputNewPrice(playerid);
                case HouseRemoveOwner:
                {
                    RemoveHouseOwner(index);
                    SendClientMessage(playerid, COLOR_NEWS, "Namo savininkas sëkmingai paðalintas.");
                }
                case HouseRemoveHouse:
                {
                    DeleteHouse(index);
                    SendClientMessage(playerid, COLOR_NEWS, "Namas paðalintas sëkmingai.");
                }
                case HouseEntrancePositionChange:
                {
                    new Float:x, Float:y, Float:z;
                    GetPlayerPos(playerid, x, y, z);
                    SetHouseEntrancePos(index, x, y, z, GetPlayerInterior(playerid), GetPlayerVirtualWorld(playerid));
                    SendClientMessage(playerid, COLOR_NEWS, "Namo áëjimas sëkmingai perkeltas.");
                }
                case HouseInteriorChange:
                {
                    if(!IsPlayerInAnyInterior(playerid))
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs turite bûti interjere.");

                    new Float:x, Float:y, Float:z;
                    GetPlayerPos(playerid, x, y ,z);
                    SetHouseExitLocation(index, x, y, z, GetPlayerInteriorId(playerid));
                    SendClientMessage(playerid, COLOR_NEWS, "Namo interjeras sëkmingai pakeistas");
                }
                case HouseInfo: HouseManagementDialog.Information(playerid, index);
                case HouseInputNewPickupModel: HouseManagementDialog.InputNewPickupModel(playerid);
            }
            DeletePVar(playerid, "IndexUsage");
            return 1;
        }
        case DIALOG_HMENU_NEW_PRICE:
        {
            if(!response)
                return HouseManagementDialog.ShowMain(playerid);

            if(PlayerUsedHouseIndex[ playerid ] == -1)
                return 0;

            new price, string[70];

            if(sscanf(inputtext, "i", price))
                return HouseManagementDialog.InputNewPrice(playerid, "Praðome ávesti skaièiø.");

            if(price < 0)
                return HouseManagementDialog.InputNewPrice(playerid, "Kaina turi bûti didesnë nei 0.");

            hInfo[ PlayerUsedHouseIndex[ playerid ] ][ hPrice ] = price;
            UpdateHouseInfoText(PlayerUsedHouseIndex[ playerid ]);
            SaveHouse(PlayerUsedHouseIndex[ playerid ]);
            Streamer_Update(playerid);

            format(string, sizeof(string), "Namo kurio ID: %d, kaina buvo pakeista á: %d ", PlayerUsedHouseIndex[ playerid ], price);
            SendClientMessage(playerid, COLOR_WHITE, string);
            return 1;
        }
        case DIALOG_HMENU_NEW_PICKUP_MODEL:
        {
            if(!response)
                return HouseManagementDialog.ShowMain(playerid);

            new model, string[90];
            if(sscanf(inputtext, "i", model))
                return HouseManagementDialog.InputNewPickupModel(playerid, "Praðome ávesti skaièiø.");

            if(model < 1 || model > 20000)
                return HouseManagementDialog.InputNewPickupModel(playerid, "Negalimas modelio ID.");

            SetHousePickupModel(PlayerUsedHouseIndex[ playerid ], model);
            Streamer_Update(playerid);

            format(string, sizeof(string),"Namo pickup modelis sëkmingai pakeistas á %d. Jau turëtumëte já matyti", model);
            SendClientMessage(playerid, COLOR_NEWS, string);
            return 1;
        }
        case DIALOG_HMENU_CONFIRM_WIPE:
        {
            if(!response)
                return HouseManagementDialog.ShowMain(playerid);

            DeleteHouses();
            SendClientMessage(playerid, COLOR_NEWS, "Visi namai sëkmingai paðalinti.");
            return 1;
        }
    }
    return 0;
}
