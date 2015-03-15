

#include <YSI\y_hooks>

#if !defined MAX_BIZNES
    #define MAX_BIZNES                  200
#endif

#if !defined MAX_BUSINESS_PRODUCTS
    #define MAX_BUSINESS_PRODUCTS       2000
#endif

#define DIALOG_BIZ_WARE_LIST            49
#define DIALOG_BIZ_CLOTHESSHOP_LIST     105
#define DIALOG_BIS_OWNER_MENU_MAIN      83
#define DIALOG_BIZ_NAME_CHANGE          84
#define DIALOG_BIZ_ENTR_PRICE_CHANGE    85
#define DIALOG_BIZ_BANK_MAIN            86
#define DIALOG_BIZ_BANK_WITHDRAW        171
#define DIALOG_BIZ_BANK_DEPOSIT         172
#define DIALOG_BIZ_WARE_LIST_EDIT_OPTIO 51
#define DIALOG_BIZ_WARE_LIST_EDIT_PRICE 144
#define DIALOG_BIZ_WARE_LIST_EDIT_ENTER 155
#define DIALOG_BIZ_WARE_LIST_EDIT_SHOP  60
#define DIALOG_BIZ_WARE_LIST_EDIT       6416

#define BUSINESS_WARES_EMPTY_SLOT       "Tu��ia"

#define DEFAULT_BIZ_PICKUP_MODEL        1239

#define MIN_BUSINESS_NAME               1
#define MAX_BUSINESS_NAME 				101
#define MAX_SAVED_WARES 				10


#define BUSINESS_PRICE_MIN              1
#define BUSINESS_PRICE_MAX               100000000
#define BUSINESS_ENTRANCE_PRICE_MIN     0
#define BUSINESS_ENTRANCE_PRICE_MAX     100000
#define BUSINESS_WARE_PRICE_MAX         50000
#define BUSINESS_WARE_PRICE_MIN         1

#define MAX_BUSINESS_WARES              MAX_SAVED_WARES
#define MAX_BUSINESS_WARE_NAME          64
#define MAX_BUSINESS_FURNITURE          500

#define BUSINESS_LABEL_DRAW_DISTANCE    20.0


#define BUSINESS_OWNER_NULL 			0

#if !defined ismysqlnull
    #define ismysqlnull(%0)             (!strcmp(%0, "NULL"))
#endif


#define BizOwnerMenu::                  BiO0x
#define BusinessManagementDialog.       bizdial_


forward E_BUSINESS_TYPES:GetBusinessType(bizindex);

enum E_BUSINESS_TYPES 
{
    None,
    Supermarket,
    Cafe,
    Bar,
    ClothesShop,
    BarberShop
};


new Alloc:BizCargoTypes;


enum E_BUSINESS_WARES_DATA
{
    Id, 
    Name[ MAX_BUSINESS_WARE_NAME ],
    Price
};

enum E_BUSINESS_FURNITURE_DATA {
    SqlId,
    FurnitureId,
    ObjectId,
    Name[ MAX_FURNITURE_NAME]
};


enum E_BUSINESS_DATA
{
    bID,
    bOwner,
    bName[ MAX_BUSINESS_NAME ],
    bPrice,
    bInteriorId,
    Float:bEnter[3],
    Float:bExit[3],
    bBank,
    bEntPrice,
    bEntranceInt,
    bEntranceVirw,
    bool:bLocked,
    E_BUSINESS_TYPES:bType,
    bProducts,
    bPickupModel,
    bPickup,
    Text3D:bLabel
};
new bInfo[ MAX_BIZNES ][ E_BUSINESS_DATA ],
    BusinessWares[ MAX_BIZNES ][ MAX_BUSINESS_WARES ][ E_BUSINESS_WARES_DATA ],
    BusinessFurniture[ MAX_BIZNES ][ MAX_BUSINESS_FURNITURE ][ E_BUSINESS_FURNITURE_DATA ];

new Iterator:Business<MAX_BIZNES>;



enum E_CLOTHES_SHOP_CATEGORY_DATA
{
    Id,
    Name[32]
};
static const ClothesShopCategories[][ E_CLOTHES_SHOP_CATEGORY_DATA ] =
{
    {1, "Skarel�s ant galvos"},
    {2, "Kepur�s"},
    {3, "Skryb�l�s"},
    {4, "Akiniai"},
    {5, "�almai"},
    {6, "Kauk�s"},
    {7, "Skarel�s ant veido"},
    {8, "Kiti daiktai"}
};


enum E_CLOTHES_SHOP_ITEM_DATA
{
    CategoryId,
    Itemid,
    Modelid,
    Price
};

static const ClothesShopItems[][ E_CLOTHES_SHOP_ITEM_DATA ] =
{
    {1, ITEM_Bandana2, 18892, 50},
    {1, ITEM_Bandana4, 18894, 50},
    {1, ITEM_Bandana5, 18895, 50},
    {1, ITEM_Bandana6, 18896, 50},
    {1, ITEM_Bandana7, 18897, 50},
    {1, ITEM_Bandana8, 18898, 50},
    {1, ITEM_Bandana9, 18899, 50},

    {2, ITEM_CapBack3, 19200, 100},
    {2, ITEM_CapBack4, 18942, 100},
    {2, ITEM_CapBack5, 18943, 100},
    {2, ITEM_CapBack7, 18926, 100},
    {2, ITEM_CapBack8, 18927, 100},
    {2, ITEM_CapBack9, 18928, 100},
    {2, ITEM_CapBack10, 18929, 100},
    {2, ITEM_CapBack11, 18930, 100},
    {2, ITEM_CapBack12, 18931, 100},
    {2, ITEM_CapBack13, 18932, 100},
    {2, ITEM_CapBack14, 18933, 100},
    {2, ITEM_CapBack15, 18934, 100},
    {2, ITEM_CapBack16, 18935, 100},
    {2, ITEM_CapBack17, 19093, 100},
    {2, ITEM_CapBack18, 19160, 100},
    {2, ITEM_CapBack19, 18953, 100},
    {2, ITEM_CapBack20, 18954, 100},
    {2, ITEM_CapBack21, 18961, 100},
    {2, ITEM_SkullyCap1, 18964, 100},
    {2, ITEM_SkullyCap2, 18965, 100},
    {2, ITEM_HatMan1, 18967, 100},
    {2, ITEM_HatMan2, 18968, 100},
    {2, ITEM_SantaHat1, 19064, 100},
    {2, ITEM_SantaHat2, 19065, 100},
    {2, ITEM_HoodyHat3, 19069, 100},
    {2, ITEM_tophat01, 19352, 100},
    {2, ITEM_HatBowler6, 19488, 100},
    {2, ITEM_pilotHat01, 19520, 100},


    {3, ITEM_CowboyHat1, 19095, 50},
    {3, ITEM_CowboyHat2, 18962, 50},
    {3, ITEM_CowboyHat3, 19096, 50},
    {3, ITEM_CowboyHat4, 19097, 50},
    {3, ITEM_CowboyHat5, 19098, 50},
    {3, ITEM_HatBowler1, 18944, 50},
    {3, ITEM_HatBowler2, 18945, 50},
    {3, ITEM_HatBowler3, 18947, 50},
    {3, ITEM_Beret1, 18921, 50},
    {3, ITEM_Beret2, 18922, 50},
    {3, ITEM_Beret3, 18923, 50},
    {3, ITEM_Beret4, 18924, 50},
    {3, ITEM_Beret5, 18925, 50},
               

    {4, ITEM_GlassesType1, 19006, 100},
    {4, ITEM_GlassesType2, 19007, 100},
    {4, ITEM_GlassesType3, 19008, 100},
    {4, ITEM_GlassesType4, 19009, 100},
    {4, ITEM_GlassesType7, 19012, 100},
    {4, ITEM_GlassesType10, 19015, 100},
    {4, ITEM_GlassesType13, 19018, 100},
    {4, ITEM_GlassesType14, 19019, 100},
    {4, ITEM_GlassesType15, 19020, 100},
    {4, ITEM_GlassesType16, 19021, 100},
    {4, ITEM_GlassesType17, 19022, 100},
    {4, ITEM_GlassesType18, 19023, 100},
    {4, ITEM_GlassesType19, 19024, 100},
    {4, ITEM_GlassesType20, 19025, 100},
    {4, ITEM_GlassesType21, 19026, 100},
    {4, ITEM_GlassesType22, 19027, 100},
    {4, ITEM_GlassesType23, 19028, 100},
    {4, ITEM_GlassesType24, 19029, 100},
    {4, ITEM_GlassesType25, 19030, 100},
    {4, ITEM_GlassesType26, 19031, 100},
    {4, ITEM_GlassesType27, 19032, 100},
    {4, ITEM_GlassesType28, 19033, 100},
    {4, ITEM_PoliceGlasses2, 19139, 100},
    {4, ITEM_PoliceGlasses3, 19140, 100},
               
    {5, ITEM_MotorcycleHelmet4, 18978, 200},
    {5, ITEM_MotorcycleHelmet5, 18979, 200},
    {5, ITEM_MotorcycleHelmet6, 18977, 200},
    {5, ITEM_MotorcycleHelmet9, 18952, 200},
    {5, ITEM_SillyHelmet2, 19114, 200},
    {5, ITEM_SillyHelmet3, 19115, 200},
    {5, ITEM_PlainHelmet1, 19116, 200},

    {6, ITEM_HockeyMask1, 19036, 50},
    {6, ITEM_MaskZorro1, 18974, 50},

    {7, ITEM_Bandanaa2, 18911, 50},
    {7, ITEM_Bandanaa4, 18912, 50},
    {7, ITEM_Bandanaa5, 18913, 50},
    {7, ITEM_Bandanaa6, 18914, 50},
    {7, ITEM_Bandanaa7, 18915, 50},
    {7, ITEM_Bandanaa8, 18916, 50},
    {7, ITEM_Bandanaa9, 18917, 50},
    {7, ITEM_Bandanaa10, 18918, 50},
    {7, ITEM_Bandanaa11, 18919, 50},
    {7, ITEM_Bandanaa12, 18920, 50},


    {8, ITEM_KREPSYS, 2919, 20},
    {8, ITEM_LAGAMINAS, 1210, 20},
    {8, ITEM_KUPRINE, 371, 50},
    {8, ITEM_WatchType1,19039, 20},
    {8, ITEM_WatchType2,19040, 20},
    {8, ITEM_WatchType6,19044, 50},
    {8, ITEM_WatchType4,19042, 20}
};



enum E_SUPERMARKET_ITEM_DATA {
    Price,
    ItemId,
    Amount
};

new SupermarketItems[ ][ E_SUPERMARKET_ITEM_DATA ] = { // Parduotuv�s nustatymai
    {199,ITEM_PHONE,1 },
    {69, ITEM_MASK, 1 },
    {250,ITEM_RADIO,1 },
    {3,  ITEM_ZIB, 50 },
    {6,  ITEM_CIG, 20 },
    {59, ITEM_FUEL, 30 },
    {89, ITEM_TOLKIT, 1 },
    {139, ITEM_CLOCK, 1 },
    {2,  ITEM_DICE, 1 },
    {30, ITEM_VAISTAI, 1},
    {5,  ITEM_SVIRKSTAS, 1 },
    {15, ITEM_NOTE, 1 },
    {229, ITEM_HELMET, 1 },
    {50, ITEM_ROD, 1 },
    {5,  ITEM_RODTOOL, 20 },
    {2,  ITEM_FISH, 1 },
    {25, ITEM_MEDIC, 1 },
//  {50, ITEM_TICKET, 1 },
    {5,  ITEM_BEER, 1 },
    {2,  ITEM_SPRUNK, 1 },
    {6,  ITEM_VINE, 1 },
    {3, ITEM_PAPER, 1 },
    {179, ITEM_MP3, 1 },
    {250, ITEM_MAGNETOLA, 1 },
    {750, ITEM_AUDIO, 1 },
    {180, ITEM_BIGAUDIO, 1 },
    {20, 14, 1 },
    {230, 43, 50 }, // Fotoparatas
    {89, WEAPON_PARACHUTE, 50 }, // Para�iutas
    {10, ITEM_MATCHES, 20 }
};



forward OnBusinessLoad();
forward OnBusinessWareLoad();
forward OnBusinessFurnitureLoad();
forward OnBusinessAcceptedCargoLoad();


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
	#if defined business_OnGameModeInit
		business_OnGameModeInit();
	#endif

	mysql_tquery(DbHandle, "SELECT * FROM business ORDER BY id ASC", "OnBusinessLoad", "");
    mysql_tquery(DbHandle, "SELECT * FROM business_wares ORDER BY business_id ASC", "OnBusinessWareLoad", "");
    mysql_tquery(DbHandle, "SELECT business_furniture.*, business_furniture_textures.*, furniture.name AS default_name FROM business_furniture LEFT JOIN business_furniture_textures ON business_furniture.id = business_furniture_textures.furniture_id LEFT JOIN furniture ON furniture.id = business_furniture.furniture_id", "OnBusinessFurnitureLoad", "");
    mysql_tquery(DbHandle, "SELECT * FROM business_accepted_cargo", "OnBusinessAcceptedCargoLoad", "");
    return 1;

}
#if defined _ALS_OnGameModeInit
    #undef OnGameModeInit
#else 
    #define _ALS_OnGameModeInit
#endif
#define OnGameModeInit business_OnGameModeInit
#if defined business_OnGameModeInit
    forward business_OnGameModeInit();
#endif



public OnBusinessLoad()
{
    new 
        ownerStr[16],
        interioridStr[16];

    for(new i = 0; i < cache_get_row_count(); i++)
    {
        if(i >= MAX_BIZNES)
        {
            printf("Versl� kiekis(%d) vir�ija versl� limit�(" #MAX_BIZNES ").", cache_get_row_count());
            break;
        }

        bInfo[ i ][ bID ] = cache_get_field_content_int(i, "id");
        cache_get_field_content(i, "owner", ownerStr);
        cache_get_field_content(i, "name", bInfo[ i ][ bName ], DbHandle, MAX_BUSINESS_NAME);
        bInfo[ i ][ bPrice ] = cache_get_field_content_int(i, "price");
        bInfo[ i ][ bEntPrice ] = cache_get_field_content_int(i, "entrance_price");
        cache_get_field_content(i, "interior_id", interioridStr);
        bInfo[ i ][ bEnter ][ 0 ] = cache_get_field_content_float(i, "entrance_x");
        bInfo[ i ][ bEnter ][ 1 ] = cache_get_field_content_float(i, "entrance_y");
        bInfo[ i ][ bEnter ][ 2 ] = cache_get_field_content_float(i, "entrance_z");
        bInfo[ i ][ bEntranceInt ] = cache_get_field_content_int(i, "entrance_interior");
        bInfo[ i ][ bEntranceVirw ] = cache_get_field_content_int(i, "entrance_virtual");
        bInfo[ i ][ bExit ][ 0 ] = cache_get_field_content_float(i, "exit_x");
        bInfo[ i ][ bExit ][ 1 ] = cache_get_field_content_float(i, "exit_y");
        bInfo[ i ][ bExit ][ 2 ] = cache_get_field_content_float(i, "exit_z");
        bInfo[ i ][ bLocked ] = (cache_get_field_content_int(i, "locked")) ? (true) : (false);
        bInfo[ i ][ bBank ] = cache_get_field_content_int(i, "bank");
        bInfo[ i ][ bType ] = E_BUSINESS_TYPES:cache_get_field_content_int(i, "type");
        bInfo[ i ][ bProducts ] = cache_get_field_content_int(i, "products");
        bInfo[ i ][ bPickupModel ] = cache_get_field_content_int(i, "pickup_model");

        bInfo[ i ][ bOwner ] = (ismysqlnull(ownerStr) ? (BUSINESS_OWNER_NULL) : (strval(ownerStr)));
        bInfo[ i ][ bInteriorId ] = (ismysqlnull(interioridStr) ? (INVALID_INTERIOR_ID) : (strval(interioridStr)));


        UpdateBusinessEntrance(i);
        Itter_Add(Business, i);
    }
    printf("Serveryje �iuo metu sukurtas (-i) %d verslas (-ai)", cache_get_row_count());
    return 1;
}

public OnBusinessWareLoad()
{
    new id,
        bizid,
        name[ MAX_BUSINESS_WARE_NAME ],
        price,
        lastbid,
        bizindex,
        count;
    
    for(new i = 0; i < cache_get_row_count(); i++)
    {
        id = cache_get_field_content_int(i, "id");
        bizid = cache_get_field_content_int(i, "business_id");
        cache_get_field_content(i, "name", name);
        price = cache_get_field_content_int(i, "price");

        // Jei �itam vferslui dar nekrov�m, reikia susirast jo indeks�.
        if(!lastbid || bizid != lastbid)
        {
            count = 0;
            lastbid = bizid;
        }
        bizindex = GetBusinessIndex(bizid);
        if(bizindex == -1)
            continue;

        BusinessWares[ bizindex ][ count ][ Id ] = id;
        strcat(BusinessWares[ bizindex ][ count ][ Name ], name, MAX_BUSINESS_WARE_NAME);
        BusinessWares[ bizindex ][ count ][ Price ] = price;
        count++;
    }
    return 1;
}

public OnBusinessFurnitureLoad()
{
    printf("OnBusinessFurnitureLoad");
    new 
        id,
        bizindex,
        name[ MAX_BUSINESS_NAME ],
        txd_name[ MAX_TXD_FILE_NAME ],
        texture_name[ MAX_TEXTURE_NAME ],
        strTextureColor[16],
        strObjectModel[16],
        lastBizId, 
        bid,
        fid,
        lastFurnitureId,
        bizFurnitureCount = -1,
        Float:pos[6],
        furnitureIndex,
        strFid[16],
        strIndex[16];

    for(new i = 0; i < cache_get_row_count(); i++)
    {   
        id = cache_get_field_content_int(i, "id");
        bid = cache_get_field_content_int(i, "business_id");
        fid = cache_get_field_content_int(i, "furniture_id");
        cache_get_field_content(i, "name", name);
        pos[ 0 ] = cache_get_field_content_float(i, "pos_x");
        pos[ 1 ] = cache_get_field_content_float(i, "pos_y");
        pos[ 2 ] = cache_get_field_content_float(i, "pos_z");
        pos[ 3 ] = cache_get_field_content_float(i, "rot_x");
        pos[ 4 ] = cache_get_field_content_float(i, "rot_y");
        pos[ 5 ] = cache_get_field_content_float(i, "rot_z");

        cache_get_field_content(i, "furniture_id", strFid);
        cache_get_field_content(i, "index", strIndex);
        cache_get_field_content(i, "object_model", strObjectModel);
        cache_get_field_content(i, "txd_name", txd_name);
        cache_get_field_content(i, "texture_name", texture_name);
        cache_get_field_content(i, "color", strTextureColor);

        bizindex = GetBusinessIndex(bid);
        if(bizindex == -1)
            continue;

        // Jei namas kitas, krausime i kita BusinessFurniture[]
        if(!lastBizId || lastBizId != bid)
        {
            lastBizId = bid;
            bizFurnitureCount = -1;
        }

        // Jeigu lastFurnitureId nenustatytas arba nelygus k�tik u�krautam, rei�kia �io baldo dar nesuk�r�m.
        if(!lastFurnitureId || lastFurnitureId != id)
        {
            bizFurnitureCount++;
            lastFurnitureId = id;

            BusinessFurniture[ bizindex ][ bizFurnitureCount ][ SqlId ] = id;
            BusinessFurniture[ bizindex ][ bizFurnitureCount ][ FurnitureId ] = fid;
            if(ismysqlnull(name))
                cache_get_field_content(i, "default_name", BusinessFurniture[ bizindex ][ bizFurnitureCount ][ Name ], DbHandle, MAX_FURNITURE_NAME);
            else 
                strcat(BusinessFurniture[ bizindex ][ bizFurnitureCount ][ Name ], name, MAX_FURNITURE_NAME);

            furnitureIndex = GetFurnitureIndex(fid);
            // Netur�t� b�t niekada -1, nebent kokie pakeitimai furniture table vyko.
            if(furnitureIndex != -1)
                BusinessFurniture[ bizindex ][ bizFurnitureCount ][ ObjectId ] = CreateDynamicObject(GetFurnitureObjectId(furnitureIndex), pos[0], pos[1], pos[2], pos[3], pos[4], pos[5], .worldid = GetBusinessVirtualWorld(bizindex));
        }
        // Tas pats baldas v�l result set'e. Rei�kia kei�iam tekst�r�.
        if(!ismysqlnull(strIndex))
            SetDynamicObjectMaterial(BusinessFurniture[ bizindex ][ bizFurnitureCount ][ ObjectId ], strval(strIndex), strval(strObjectModel), txd_name, texture_name, strval(strTextureColor));
    }   
    printf("Pakrauti %d verslu objektai.", cache_get_row_count());
    return 1;
}


public OnBusinessAcceptedCargoLoad()
{
    new rows = cache_get_row_count(),
        count;
    if(rows)
    {
        // Pavojingi dalykai :s
        BizCargoTypes = malloc(rows*2);
        if(!BizCargoTypes)
            print("ERROR Allocating memory in Businesses.p : OnBusinessAcceptedCargoLoad()");
        else 
        {
            new type, cargoid;
            for(new i = 0; i < cache_get_row_count(); i++)
            {
                type = cache_get_field_content_int(i, "business_type");
                cargoid = cache_get_field_content_int(i, "cargo_id");
                mset(BizCargoTypes, count, type);
                mset(BizCargoTypes, count+1, cargoid);
                count += 2;
            }
        }
    }
    else 
        printf("An invalid number of biznes accepted cargo:%d",cache_get_row_count());
    return 1;
}





hook OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
    BusinessManagementDialog.OnDialogResponse(playerid, dialogid, response, listitem, inputtext);
    switch(dialogid)
    {
        case DIALOG_BIZ_WARE_LIST:
        {
            if(!response)
                return 1;

            new bizIndex = GetPVarInt(playerid, "BizIndex"),
                string[100];


            if(!strcmp(BusinessWares[ bizIndex ][ listitem ][ Name ], BUSINESS_WARES_EMPTY_SLOT))
                return SendClientMessage(playerid, COLOR_LIGHTRED, "�i prek� neprid�ta.");
            
            if (PlayerMoney[ playerid ] < BusinessWares[ bizIndex ][ listitem ][ Price ])
                return SendClientMessage(playerid, COLOR_LIGHTRED, "{FF6347}Persp�jimas: Nepakankamai pinig�.");
            
            if(bInfo[ bizIndex ][ bProducts ] <= 0)
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, pasirinktos prek�s jau n�ra sandelyje.");



            // Jei perka i� parduotuv�s, daiktams yra papildom� s�lyg� ir veiksm�.
            if(bInfo[ bizIndex ][ bType ] == Supermarket)
            {
                new itemid = GetInvItemID(BusinessWares[ bizIndex ][ listitem ][ Name ]);
                switch(itemid)
                {
                    case ITEM_RODTOOL:
                    {
                        new tmpid = PlayerHasItemInInvEx(playerid, ITEM_RODTOOL);
                        if(tmpid < INVENTORY_SLOTS)
                            InvInfo[ playerid ][ tmpid ][ iAmmount ] += 20;
                        else
                        {
                            if(!AddItemToInventory(playerid, itemid, GetItemAmount(itemid)))
                                return SendClientMessage(playerid, COLOR_LIGHTRED, "{FF6347}Persp�jimas: j�s� inventoriuje nepakanka vietos, atsilaisvinkite ir bandykite dar kart.");
                        }
                    }
                    case ITEM_MEDIC:
                    {
                        if (!PlayerHasItemInInv(playerid, ITEM_MEDLIC))
                            return SendClientMessage(playerid, COLOR_LIGHTRED, "{FF6347}Persp�jimas: Neturite recepto. ");
                        if(!AddItemToInventory( playerid, ITEM_MEDIC, 1))
                            return SendClientMessage(playerid, COLOR_LIGHTRED, "{FF6347}Persp�jimas: Nepakanka vietos inventoryje.");
                        RemoveItemFromInv(playerid, ITEM_MEDLIC);
                    }
                    case ITEM_PHONE:
                    {
                        if(PlayerHasItemInInv(playerid, ITEM_PHONE))
                            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s jau turite telefon�.");
                    }
                    case ITEM_FISH:
                    {
                        if(PlayerHasItemInInv(playerid, ITEM_FISH))
                            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s jau turite �uvies krep��.");
                    }
                    case ITEM_RADIO:
                    {

                        if(PlayerHasItemInInv(playerid, ITEM_FISH))
                            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s jau turite racij�.");
                    }
                }
                if(!AddItemToInventory(playerid, itemid, GetItemAmount(itemid)))
                    return SendClientMessage(playerid, COLOR_LIGHTRED, "{FF6347}Persp�jimas: j�s� inventoriuje nepakanka vietos, atsilaisvinkite ir bandykite dar kart.");

                if (itemid == ITEM_PHONE )
                {
                    new more = random(32) * 1000;
                    pInfo[playerid][pPhone] = 110000 + GetPlayerSqlId(playerid) + more;
                    format(string, sizeof(string)," ** Mobilus telefonas nupirktas, jo numeris: %d",pInfo[playerid][pPhone]);
                    SendClientMessage(playerid,COLOR_FADE3,string);
                    return 1;
                }
                else if (itemid == ITEM_RADIO )
                {
                    pInfo[playerid][pRChannel] = 1;
                    ShowPlayerInfoText( playerid );
                    UpdatePlayerInfoText( playerid );
                    return 1;
                }
                format(string, sizeof(string)," ** Daiktas %s nupirktas, u� $%d.", BusinessWares[ bizIndex ][ listitem ][ Name ], BusinessWares[ bizIndex ][ listitem ][ Price ]);
                SendClientMessage(playerid, COLOR_FADE3,string);
                SendClientMessage(playerid, COLOR_FADE2, "PAGALBA: Jeigu reik�s daugiau pagalbos, para�ykite /help");
            }

            else if(bInfo[ bizIndex ][ bType ] == Cafe || bInfo[ bizIndex ][ bType ] == Bar)
            {
                // Kiek pavalgys
                // Reik�t� koki� mandr� formul� para�yt, bet....
                new hunger;
                if(BusinessWares[ bizIndex ][ listitem ][ Price ] < 5)
                    hunger = 2;
                else if(BusinessWares[ bizIndex ][ listitem ][ Price ] < 10)
                    hunger = 3;
                else if(BusinessWares[ bizIndex ][ listitem ][ Price ] < 20)
                    hunger = 4;
                else if(BusinessWares[ bizIndex ][ listitem ][ Price ] < 40)
                    hunger = 5;
                else if(BusinessWares[ bizIndex ][ listitem ][ Price ] < 80)
                    hunger = 6;
                else if(BusinessWares[ bizIndex ][ listitem ][ Price ] < 160)
                    hunger = 7;
                else if(BusinessWares[ bizIndex ][ listitem ][ Price ] < 320)
                    hunger = 8;



                pInfo[ playerid ][ pHunger ] -= hunger;

                // Persisteng�, vems.
                if(pInfo[ playerid ][ pHunger ] <= -7)
                {
                    new Float:health;
                    GetPlayerHealth(playerid, health);
                    if(health <= 50)
                        SetPlayerHealth(playerid, 10.0);
                    else 
                        SetPlayerHealth(playerid, health - 50.0);
                    pInfo[ playerid ][ pHunger ] = 0;
                    OnePlayAnim(playerid, "FOOD", "EAT_Vomit_P", 3.0, 0, 0, 0, 0, 0); 
                    SendClientMessage(playerid, COLOR_NEWS, "D�m�sio, J�s� veik�jas persivalg� per daug ir apsiv�m�.");
                }

                format(string, sizeof(string),"U�sisak�te/nusipirkote prek� %s, kuri Jums kainavo %d$", BusinessWares[ bizIndex ][ listitem ][ Name ], BusinessWares[ bizIndex ][ listitem ][ Price ]);
                if(!strcmp(BusinessWares[ bizIndex ][ listitem ][ Name ],"Alus",true))
                    SetPlayerSpecialAction(playerid, SPECIAL_ACTION_DRINK_BEER);
                else if(!strcmp(BusinessWares[ bizIndex ][ listitem ][ Name ],"Vynas",true))
                    SetPlayerSpecialAction(playerid, SPECIAL_ACTION_DRINK_WINE );
                else if(!strcmp(BusinessWares[ bizIndex ][ listitem ][ Name ],"Sprunk",true))
                    SetPlayerSpecialAction(playerid, SPECIAL_ACTION_DRINK_SPRUNK);
                else
                {
                    new Float:health;
                    GetPlayerHealth(playerid, health);
                    SetPlayerHealth(playerid, health + 20);
                }
                SendClientMessage(playerid, COLOR_FADE2, string);
            }

            // �itas kodas galioja visiems verslams kurie parduoda prekes, nesvarbu ar tai parduotuv�, baras ar kavin�.
            // Tod�l auk��iau neturi b�ti joki� nereikaling� return.
            PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
            GivePlayerMoney(playerid, -BusinessWares[ bizIndex ][ listitem ][ Price ]);
            UpdatePlayerInfoText(playerid);
            bInfo[ bizIndex ][ bBank ] += BusinessWares[ bizIndex ][ listitem ][ Price ];
            bInfo[ bizIndex ][ bProducts ]--;
            return 1;
        }
        case DIALOG_BIZ_CLOTHESSHOP_LIST:
        {
            if(!response)
                return 1;

            ShowPlayerClothesShopWares(playerid, listitem);
            return 1;
        }


        case DIALOG_BIS_OWNER_MENU_MAIN:
        {
            if(!response)
                return 1;

            new bizIndex = GetPVarInt(playerid, "BizIndex"),
                string[80];

            switch(listitem)
            {
                case 0:
                {
                    BizOwnerMenu::NameChangeDialog(playerid);
                    return 1;
                }
                case 1:
                {
                    BizOwnerMenu::EntrancePriceChangeDialog(playerid);
                    return 1;
                }
                case 2:
                {
                    format(string, sizeof(string),"J�s� verslo banke yra $%d\nJ�s rankose turite $%d",
                        bInfo[ bizIndex ][ bBank ],
                        GetPlayerMoney(playerid));
                    ShowPlayerDialog(playerid, DIALOG_BIZ_BANK_MAIN, DIALOG_STYLE_MSGBOX, "BIZNIO BANKAS", string, "Nuimti", "Pad�ti" );
                }
                case 3:
                {
                    if(IsBusinessLocked(bizIndex))
                    {
                        ShowInfoText(playerid, "~w~DURYS ~g~ATRAKINTOS", 1000);
                        PlayerPlaySound(playerid, 1145, 0.0, 0.0, 0.0);
                    }
                    else 
                    {
                        ShowInfoText(playerid, "~w~DURYS ~r~UZRAKINTOS", 1000);
                        PlayerPlaySound(playerid, 1145, 0.0, 0.0, 0.0);
                    }
                    ToggleBusinessLock(bizIndex);
                }
                case 4: 
                    return SendClientMessage(playerid, COLOR_NEWS, "INFORMACIJA: verslo tipas yra kei�iamas tik Administratoriaus.");
                case 5: 
                    return SendClientMessage(playerid, COLOR_NEWS, "INFORMACIJA: prekes � J�s� versl�/bizn� gali atve�ti tik krovini� i�ve�iotojai.");
                case 6:
                {
                    BizOwnerMenu::WareListEditMain(playerid);
                }
                case 7:
                    return SendClientMessage(playerid,COLOR_NEWS, "INFORMACIJA: Prdukt� pirkimo kaina galite keisti su /cargoprice. Nebepirkti j� galite nustat� kain� � 0.");
            }
        }

        case DIALOG_BIZ_NAME_CHANGE:
        {
            if(!response)
                return 1;

            new bizIndex = GetPVarInt(playerid, "BizIndex");

            if(strlen(inputtext) < MIN_BUSINESS_NAME || strlen(inputtext) >= MAX_BUSINESS_NAME)
                return BizOwnerMenu::NameChangeDialog(playerid, "Netinkamas pavadinimo ilgis. J� sudaryti turi nuo " #MIN_BUSINESS_NAME " iki " #MAX_BUSINESS_NAME " simboli�.");

            format(bInfo[ bizIndex ][ bName ], MAX_BUSINESS_NAME, inputtext);
            SendClientMessage(playerid, COLOR_NEWS, "Verslo pavadinimas s�kmingai pakeistas.");
            SaveBusiness(bizIndex);
            UpdateBusinessEntrance(bizIndex);
            return 1;
        }
        case DIALOG_BIZ_ENTR_PRICE_CHANGE:
        {
            if(!response)
                return 1;

            new bizIndex = GetPVarInt(playerid, "BizIndex"),
                price,
                string[128];

            if(sscanf(inputtext, "i", price))
                return BizOwnerMenu::EntrancePriceChangeDialog(playerid, "�veskite skai�i�.");
            if(price < BUSINESS_ENTRANCE_PRICE_MIN || price > BUSINESS_ENTRANCE_PRICE_MAX)
                return BizOwnerMenu::EntrancePriceChangeDialog(playerid, "Minimali ��jimo kaina: " #BUSINESS_ENTRANCE_PRICE_MIN ". Maksimali ��jimo kaina: " #BUSINESS_ENTRANCE_PRICE_MAX ".");

            bInfo[ bizIndex ][ bEntPrice ] = price;
            SaveBusiness(bizIndex);
            format(string, sizeof(string), "Verslo ��jimo mokestis buvo pakeistas � %d", bInfo[ bizIndex ][ bEntPrice ]);
            SendClientMessage(playerid, COLOR_NEWS, string);
            return 1;
        }

        case DIALOG_BIZ_BANK_MAIN:
        {
            // Respose == 1: nuimti. Response == 0: pad�ti.
            if(response)
                BizOwnerMenu::MoneyWithdrawDialog(playerid);
            else 
                BizOwnerMenu::MoneyDepositDialog(playerid);
        }
        case DIALOG_BIZ_BANK_WITHDRAW:
        {
            if(!response)
                return 1;

            new amount,
                bizIndex = GetPVarInt(playerid, "BizIndex"),
                string[80];

            if(sscanf(inputtext, "i", amount))
                return BizOwnerMenu::MoneyWithdrawDialog(playerid, "�veskite skai�i�.");

            if(amount < 0 || amount > bInfo[ bizIndex ][ bBank ])
                return BizOwnerMenu::MoneyWithdrawDialog(playerid, "Tiek pinig� versle n�ra.");

            bInfo[ bizIndex ][ bBank ] -= amount;
            GivePlayerMoney(playerid, amount);
            format(string, sizeof(string),"Pa�m�te %d i� verslo banke. Jame liko $%d", amount, bInfo[ bizIndex ][ bBank ]);
            SendClientMessage(playerid, COLOR_NEWS, string);
            SaveBusiness(bizIndex);
            SaveAccount(playerid);
            return 1;
        }
        case DIALOG_BIZ_BANK_DEPOSIT:
        {
            if(!response)
                return 1;

            new amount,
                bizIndex = GetPVarInt(playerid, "BizIndex"),
                string[80];

            if(sscanf(inputtext, "i", amount))
                return BizOwnerMenu::MoneyWithdrawDialog(playerid, "�veskite skai�i�.");

            if(amount < 0 || amount > PlayerMoney[ playerid ])
                return BizOwnerMenu::MoneyWithdrawDialog(playerid, "Tiek pinig� j�s neturite!");

            bInfo[ bizIndex ][ bBank ] += amount;
            GivePlayerMoney(playerid, -amount);
            format(string, sizeof(string),"Pad�jote %d � verslo bank�. Dabar jame yra $%d", amount, bInfo[ bizIndex ][ bBank ]);
            SendClientMessage(playerid, COLOR_NEWS, string);
            SaveBusiness(bizIndex);
            SaveAccount(playerid);
            return 1;
        }
        case DIALOG_BIZ_WARE_LIST_EDIT:
        {
            if(!response)
                return 1;

            SetPVarInt(playerid, "WareIndex", listitem);

            // Kyla klausimas kod�l ra�au � string, o ne tiesiai � dialog?
            // Na turiu id�j� kada nors prid�ti Bar/Cafe tipams galimyb� "Keisti Efekta".
            // Efektas tur�t� leisti keisti tai kas �vyksta nusipirkus g�rim�/maist�.
            // Bet tam reik�t� modifikuot business_wares ir t.t.....
            new string[64];
            string = "Keisti prek�\nKeisti kain�\n{FF2200}Pa�alinti prek�";
            
            ShowPlayerDialog(playerid, DIALOG_BIZ_WARE_LIST_EDIT_OPTIO, DIALOG_STYLE_LIST, "Preki� s�ra�o redagavimas", string, "Pasirinkti", "I�eiti");
            return 1;
        }
        case DIALOG_BIZ_WARE_LIST_EDIT_OPTIO:
        {
            if(!response)
                return 1;

            new bizIndex = GetPVarInt(playerid, "BizIndex"),
                wareIndex = GetPVarInt(playerid, "WareIndex"),
                string[1024];

            switch(listitem)
            {
                case 0:
                {
                    // Neleid�iam parduotuv�ms i� betko rinktis.
                    if(bInfo[ bizIndex ][ bType ] == Supermarket)
                    {
                        for(new i = 0; i < sizeof(SupermarketItems); i++)
                            format(string, sizeof(string), "%s%s\n",string, GetInvNameByID(SupermarketItems[ i ][ ItemId ]));
                        ShowPlayerDialog(playerid, DIALOG_BIZ_WARE_LIST_EDIT_SHOP, DIALOG_STYLE_LIST, "Pasirinkite prek�", string, "Pasirinkti", "I�eiti");
                    }
                    else 
                        BizOwnerMenu::WareEnterName(playerid);
                }
                case 1:
                    BizOwnerMenu::WareBusinessPriceChange(playerid);

                // Pa�alinam prek�.
                case 3:
                {
                    RemoveBusinessWare(bizIndex, wareIndex);
                    BizOwnerMenu::WareListEditMain(playerid);
                    SendClientMessage(playerid, COLOR_NEWS, "Prek� s�kmingai pa�alinta.");
                }
            }
            return 1;
        }
        case DIALOG_BIZ_WARE_LIST_EDIT_SHOP:
        {
            if(!response)
                return 1;
            new bizIndex = GetPVarInt(playerid, "BizIndex"),
                wareIndex = GetPVarInt(playerid, "WareIndex"),
                itemIndex;

            for(new i = 0; i < sizeof(SupermarketItems); i++)
                if(!strcmp(GetInvNameByID(SupermarketItems[ i ][ ItemId ]), inputtext))
                {
                    itemIndex = i;
                    break;
                }

            format(BusinessWares[ bizIndex ][ wareIndex ][ Name ], MAX_BUSINESS_WARE_NAME, GetInvNameByID(SupermarketItems[ itemIndex ][ ItemId ]));
            SaveBusinessWare(bizIndex, wareIndex);
            BizOwnerMenu::WareListEditMain(playerid);
            SendClientMessage(playerid, COLOR_NEWS, "Prek� s�kmingai pakeista.");
            return 1;
        }
        case DIALOG_BIZ_WARE_LIST_EDIT_ENTER:
        {
            if(!response)
                return 1;

            new bizIndex = GetPVarInt(playerid, "BizIndex"),
                wareIndex = GetPVarInt(playerid, "WareIndex");

            if(isnull(inputtext))
                return BizOwnerMenu::WareEnterName(playerid, "�veskite prek�s pavadinim�");
            if(strlen(inputtext) >= MAX_BUSINESS_WARE_NAME) 
                return BizOwnerMenu::WareEnterName(playerid, "Prek�s pavadinimo negali sudaryti daugiau nei " #MAX_BUSINESS_WARE_NAME "simboliai.");

            format(BusinessWares[ bizIndex ][ wareIndex ][ Name ], MAX_BUSINESS_WARE_NAME, inputtext);
            SaveBusinessWare(bizIndex, wareIndex);
            BizOwnerMenu::WareListEditMain(playerid);
            SendClientMessage(playerid, COLOR_NEWS, "Prek� s�kmingai pakeista.");
            return 1;
        }
        case DIALOG_BIZ_WARE_LIST_EDIT_PRICE:
        {
            if(!response)
                return 1;

            new bizIndex = GetPVarInt(playerid, "BizIndex"),
                wareIndex = GetPVarInt(playerid, "WareIndex"),
                price;

            if(sscanf(inputtext, "i", price))
                return BizOwnerMenu::WareBusinessPriceChange(playerid, "Kaina turi b�ti skai�ius.");
            if(price < BUSINESS_WARE_PRICE_MIN)
                return BizOwnerMenu::WareBusinessPriceChange(playerid, "Kaina negali b�ti ma�esn� nei $" #BUSINESS_WARE_PRICE_MIN);
            if(price > BUSINESS_WARE_PRICE_MAX)
                return BizOwnerMenu::WareBusinessPriceChange(playerid, "Kaina negali b�ti didesn� nei $" #BUSINESS_WARE_PRICE_MAX);

            BusinessWares[ bizIndex ][ wareIndex ][ Price]  = price;
            SaveBusinessWare(bizIndex, wareIndex);
            BizOwnerMenu::WareListEditMain(playerid);
            SendClientMessage(playerid, COLOR_NEWS, "Prek�s kaina s�kmingai pakeista.");
            return 1;
        }
    }
    return 0;
}   


public OnPlayerModelSelectionEx(playerid, response, extraid, modelid)
{
    if(extraid == 106)
    {
        if(!response)
            return ShowClothesShopDialog(playerid);
    
        new
            bizIndex = GetPVarInt(playerid, "BizIndex"), // �itas vis dar turi b�ti nustatytas i� /buy.
            shopCategoryIndex = GetPVarInt(playerid, "ClothesShopCategoryIndex"),
            itemid,
            price;

        if(bInfo[ bizIndex ][ bProducts ] <= 0)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, pasirinktos prek�s jau n�ra sandelyje.");
        
        for(new i = 0; i < sizeof(ClothesShopItems); i++)
            if(ClothesShopItems[ i ][ CategoryId ] == ClothesShopCategories[ shopCategoryIndex ][ Id ] && ClothesShopItems[ i ][ Modelid ] == modelid)
            {
                itemid = ClothesShopItems[ i ][ Itemid ];
                price = ClothesShopItems[ i ][ Price ];
                break;
            }

    
        if(PlayerMoney[ playerid ] < price)
            return SendClientMessage(playerid, COLOR_LIGHTRED,"Klaida, Jums nepakanka gryn�j� pinig� �iam veiksmui");

        if(!AddItemToInventory( playerid, itemid, 1))
            return SendClientMessage(playerid, COLOR_LIGHTRED,"Klaida, J�s� veik�jo inventoriuje n�ra laisvos vietos.");

        GivePlayerMoney(playerid, -price);
        SendClientMessage(playerid, COLOR_WHITE,"Prek� s�kmingai �sigyta!");
        bInfo[ bizIndex ][ bBank ] += price;
        bInfo[ bizIndex ][ bProducts ]--;
        return 1;

    }
    if(extraid == 115)
    {
        if(!response)
            return 1;

        new
            bizIndex = GetPVarInt(playerid, "BizIndex"), // �itas vis dar turi b�ti nustatytas i� /buy.
            itemid;

        switch(modelid)
        {
            case 19516: itemid = ITEM_HAIR1;
            case 19518: itemid = ITEM_HAIR2;
            case 19274: itemid = ITEM_HAIR5;
        }

        if(bInfo[ bizIndex ][ bProducts ] <= 0)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, pasirinktos prek�s jau n�ra sandelyje.");
            

        if(PlayerMoney[ playerid ] < 50)
            return SendClientMessage(playerid, COLOR_LIGHTRED,"Klaida, Jums nepakanka gryn�j� pinig� �iam veiksmui");

        if(!AddItemToInventory(playerid, itemid, 1))
            return SendClientMessage( playerid, COLOR_LIGHTRED,"Klaida, J�s� veik�jo inventoriuje n�ra laisvos vietos.");
           
        GivePlayerMoney(playerid, -50);
        SendClientMessage(playerid, COLOR_LIGHTRED2,"Sveikiname, J�s �sigijote peruk�, dabar galite u�sid�ti per /inv.");
        bInfo[ bizIndex ][ bBank ] += 50;
        bInfo[ bizIndex ][ bProducts ]--;
    }
    #if defined busine_OnPlayerModelSelectionEx
        busine_OnPlayerModelSelectionEx(playerid, response, extraid, modelid);
    #endif
    return 0;
}
#if defined _ALS_OnPlayerModelSelectionEx
    #undef OnPlayerModelSelectionEx
#else 
    #define _ALS_OnPlayerModelSelectionEx
#endif
#define OnPlayerModelSelectionEx busine_OnPlayerModelSelectionEx
#if defined busine_OnPlayerModelSelectionEx
    forward busine_OnPlayerModelSelectionEx(playerid, response, extraid, modelid);
#endif









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



stock UpdateBusinessEntrance(bindex)
{
	new string[64 + MAX_BUSINESS_NAME];
	if(IsValidDynamicPickup(bInfo[ bindex ][ bPickup ]))
		DestroyDynamicPickup(bInfo[ bindex ][ bPickup ]);

    if(IsValidDynamic3DTextLabel(bInfo[ bindex ][ bLabel ]))
        DestroyDynamic3DTextLabel(bInfo[ bindex ][ bLabel ]);

	if(!bInfo[ bindex ][ bPickupModel ] )
        bInfo[ bindex ][ bPickupModel ] = DEFAULT_BIZ_PICKUP_MODEL;

    if(IsBusinessOwned(bindex))
    {
        bInfo[ bindex ][ bPickup ] = CreateDynamicPickup(bInfo[ bindex ][ bPickupModel ],
            1,
            bInfo[ bindex ][ bEnter ][ 0 ],
            bInfo[ bindex ][ bEnter ][ 1 ], 
            bInfo[ bindex ][ bEnter ][ 2 ]);
        return 1;
    }
    else
    {
        format(string,sizeof(string), "%s\nPardavimo kaina: %d$\n�sigijimui - /buybiz", bInfo[ bindex ][ bName ],bInfo[ bindex ][ bPrice ]);
        bInfo[ bindex ][ bLabel ] = CreateDynamic3DTextLabel(string, 
        	COLOR_WHITE, 
        	bInfo[ bindex ][ bEnter ][ 0 ],
        	bInfo[ bindex ][ bEnter ][ 1 ],
        	bInfo[ bindex ][ bEnter ][ 2 ],
        	BUSINESS_LABEL_DRAW_DISTANCE, 
        	.worldid = bInfo[ bindex ][ bEntranceVirw ],
        	.interiorid = bInfo[ bindex ][ bEntranceInt ],
        	.streamdistance = 15.0);
        return 1;
    }
}

stock IsBusinessOwned(bindex)
	return (bInfo[ bindex ][ bOwner ] == BUSINESS_OWNER_NULL) ? (false) : (true);

stock E_BUSINESS_TYPES:GetBusinessType(bizindex)
    return bInfo[ bizindex ][ bType ];

stock GetBusinessTypeName(E_BUSINESS_TYPES:type)
{
    new s[ 20 ];
    switch(type)
    {   
        case None: s = "Joks";
        case Supermarket: s = "Parduotuv�";
        case Cafe: s = "Kavin�";
        case Bar: s = "Baras/Klubas";
        case ClothesShop: s = "Drabu�i� parduotuv�";
        case BarberShop: s = "Kirpykla";
    }
    return s;
}

stock GetBusinessBoughtCommodityCount(business_index)
{
    new count = 0;
    foreach(CommodityIterator, i)
        if(Commodities[ i ][ IndustryId ] == bInfo[ business_index ][ bID ] 
            && Commodities[ i ][ SellBuyStatus ] == Buying
            && Commodities[ i ][ IsBusinessCommodity ])
            count++;
    return count;
}

stock IsBusinessAcceptingCargo(business_index, cargoid)
{
	foreach(CommodityIterator, i)
        if(Commodities[ i ][ IndustryId ] == bInfo[ business_index ][ bID ] 
            && Commodities[ i ][ SellBuyStatus ] == Buying
            && Commodities[ i ][ CargoId ] == cargoid
            && Commodities[ i ][ IsBusinessCommodity ])
            return true;
    return false;
}

AddCargoToBusiness(business_index, cargoid, amount = 1)
{
	new query[160];
    foreach(CommodityIterator, i)
        if(Commodities[ i ][ IndustryId ] == bInfo[ business_index ][ bID ]
            && Commodities[ i ][ CargoId ] == cargoid
            && Commodities[ i ][ IsBusinessCommodity ])
            {
                UpdateBusinessProducts(business_index, GetBusinessProductCount(business_index) + amount  * 50);
                Commodities[ i ][ CurrentStock ] += amount;
				format(query,sizeof(query),"UPDATE commodities SET current_stock = %d WHERE industry_id = %d AND cargo_id = %d AND type = 'Business'",
					Commodities[ i ][ CurrentStock ], bInfo[ business_index ][ bID ], cargoid);
				mysql_pquery(DbHandle, query);
            }
    return false;
}


stock GetBusinessCargoIndex(biz_index, cargoid)
{
	foreach(new i : CommodityIterator)
		if(Commodities[ i ][ IndustryId ] == bInfo[ biz_index ][ bID ]
			&& Commodities[ i ][ CargoId ] == cargoid 
			&& Commodities[ i ][ IsBusinessCommodity ])
				return i;
	return -1;
}



stock GetBusinessIndex(bizid)
{
    foreach(new i : Business)
        if(bInfo[ i ][ bID ] == bizid)
            return i;
    return -1;
}
stock GetBusinessCount()
{
    new count;
    foreach(new i : Business)
        count++;
    return count;
}
stock GetBusinessProductCount(bizindex)
    return bInfo[ bizindex ][ bProducts ];
stock GetBusinessFurnitureCount(bizindex)
{
    new count = 0;
    for(new i = 0; i < MAX_BUSINESS_FURNITURE; i++)
        if(BusinessFurniture[ bizindex ][ i ][ SqlId ])
            count++;
    return count;
}
stock GetBusinessFurnitureIndex(bizindex, objectid)
{
    for(new i = 0; i < MAX_BUSINESS_FURNITURE; i++)
    {
        if(BusinessFurniture[ bizindex ][ i ][ ObjectId ] == objectid)
            return i;
    }
    return -1;
}

stock GetBusinessPickupIndex(pickupid)
{
    foreach(new i : Business)     
        if(bInfo[ i ][ bPickup ] == pickupid)
            return i;
    return -1;
}

stock GetBusinessName(bizindex)
{
    new name[ MAX_BUSINESS_NAME ];
    strcat(name, bInfo[ bizindex][ bName ]);
    return name;
}

stock GetBusinessExitPos(bizindex, &Float:x, &Float:y, &Float:z)
{
    x = bInfo[ bizindex ][ bExit ][ 0 ];
    y = bInfo[ bizindex ][ bExit ][ 1 ];
    z = bInfo[ bizindex ][ bExit ][ 2 ];
}

stock GetBusinessEntrancePos(bizindex, &Float:x, &Float:y, &Float:z)
{
    x = bInfo[ bizindex ][ bEnter ][ 0 ];
    y = bInfo[ bizindex ][ bEnter ][ 1 ];
    z = bInfo[ bizindex ][ bEnter ][ 2 ];
}


stock GetBusinessOwner(bizindex)
    return bInfo[ bizindex ][ bOwner ];

stock GetBusinessEntrancePrice(bizindex)
    return bInfo[ bizindex ][ bEntPrice ];

stock IsPlayerInBusiness(playerid, bizindex)
{
    if(bInfo[ bizindex ][ bInteriorId ] == INVALID_INTERIOR_ID)
        return false;
    return IsPlayerInInterior(playerid, bInfo[ bizindex ][ bInteriorId ]);
}
stock IsPlayerInAnyBusiness(playerid)
{
    foreach(new i : Business)
        if(IsPlayerInBusiness(playerid, i))
            return true;
    return false;
}



stock GetPlayerBusinessIndex(playerid)
{
    foreach(new i : Business)
    {
        if(IsPlayerInBusiness(playerid, i))
            return i;

        if(IsPlayerInRangeOfPoint(playerid, 5.0, bInfo[ i ][ bEnter ][ 0 ], bInfo[ i ][ bEnter ][ 1 ], bInfo[ i ][ bEnter ][ 2 ]))
            return i;
    }
    return -1;
}


stock IsPlayerBusinessOwner(playerid, bizindex)
{
    if(bInfo[ bizindex][ bOwner ] == GetPlayerSqlId(playerid))
        return true;
    else 
        return false;
}

stock AddBusiness(Float:enx, Float:eny, Float:enz, entrance_interior, entrance_virtual)
{
    new query[256],
        index = -1;
    
    static EmptyBusiness[ E_BUSINESS_DATA ], FurnitureData[ MAX_BUSINESS_FURNITURE ][ E_BUSINESS_FURNITURE_DATA ];

    for(new i = 0; i < MAX_BIZNES; i++) 
    {
        if(!bInfo[ i ][ bID ])
        {
            index = i;
            // Nustatom � tu��ius masyvus, just in case.
            bInfo[ i ] = EmptyBusiness;
            BusinessFurniture[ i ] = FurnitureData;
            break;
        }
    }
    if(index == -1)
        return index;

    bInfo[ index ][ bEnter ][ 0 ] = enx;
    bInfo[ index ][ bEnter ][ 1 ] = eny;
    bInfo[ index ][ bEnter ][ 2 ] = enz;
    bInfo[ index ][ bEntranceInt ] = entrance_interior;
    bInfo[ index ][ bEntranceVirw ] = entrance_virtual;
    Itter_Add(Business, index);

    format(query, sizeof(query), "INSERT INTO business (entrance_x, entrance_y, entrance_z, entrance_interior, entrance_virtual) VALUES (%f, %f, %f, %d, %d) ",
        enx, eny, enz, entrance_interior, entrance_virtual);

    new Cache:result;
    if(!(result = mysql_query(DbHandle, query))) 
        return -1;

    bInfo[ index ][ bID ] = cache_insert_id();
    cache_delete(result);
    UpdateBusinessEntrance(index);
    return index;
}


stock UpdateBusinessType(bizindex, E_BUSINESS_TYPES:type)
{
    new query[70];
    mysql_format(DbHandle, query, sizeof(query), "UPDATE business SET type = %d WHERE id = %d",
        _:type, bInfo[ bizindex ][ bID ]);
    mysql_pquery(DbHandle, query);

    bInfo[ bizindex ][ bType ] = type;
    return 1;
}


stock RemoveBusinessOwner(bizindex)
{
    new query[60];
    format(query, sizeof(query), "UPDATE business SET owner = 'NULL' WHERE id = %d",
        bInfo[ bizindex ][ bID ]);
    mysql_pquery(DbHandle, query);
    bInfo[ bizindex ][ bOwner ] = BUSINESS_OWNER_NULL;
    UpdateBusinessEntrance(bizindex);
    return 1;
}

stock DeleteBusiness(index)
{
    new query[60];
    mysql_format(DbHandle ,query, sizeof(query), "DELETE FROM business WHERE id = %d LIMIT 1",
        bInfo[ index ][ bID ]);
    mysql_pquery(DbHandle, query);

    DestroyDynamicPickup(bInfo[ index ][ bPickup ]);
    DestroyDynamic3DTextLabel(bInfo[ index ][ bLabel ]);

    static EmptyBusiness[ E_BUSINESS_DATA ], EmptyWares[ E_BUSINESS_WARES_DATA ], EmptyFurniture[ E_BUSINESS_FURNITURE_DATA ];
    bInfo[ index ] = EmptyBusiness;
    for(new i = 0; i < MAX_BUSINESS_WARES; i++)
        BusinessWares[ index ][ i ] = EmptyWares;
    for(new i = 0; i < MAX_BUSINESS_FURNITURE; i++)
    {
        DestroyDynamicObject(BusinessFurniture[ index ][ i ][ ObjectId ]);
        BusinessFurniture[ index ][ i ] = EmptyFurniture;
    }
    return 1;
}

stock IsValidBusiness(bizindex)
{
    if(bInfo[ bizindex ][ bID ])
        return true;
    return false;
}

stock SaveBusiness(bizindex)
{
    new query[512 + MAX_BUSINESS_NAME], owner[16], interior[16];

    if(bInfo[ bizindex ][ bOwner ] == BUSINESS_OWNER_NULL) 
        owner = "NULL";
    else 
        format(owner,sizeof(owner), "'%d'", bInfo[ bizindex][ bOwner ]);


    if(bInfo[ bizindex ][ bInteriorId ] == INVALID_INTERIOR_ID) 
        interior = "NULL";
    else 
        format(interior, sizeof(interior),"'%d'", bInfo[ bizindex][ bInteriorId ]);

    mysql_format(DbHandle, query, sizeof(query), "UPDATE `business` SET owner = %s, name = '%e', price = %d, entrance_price = %d, interior_id = %s, entrance_x = %f, entrance_y = %f, entrance_z = %f, \
        entrance_interior = %d, entrance_virtual = %d, exit_x = %f, exit_y = %f, exit_z = %f, locked = %d, bank = %d, type = %d, products = %d, pickup_model = %d \
        WHERE id = %d",
        owner,
        bInfo[ bizindex ][ bName ],
        bInfo[ bizindex ][ bPrice ],
        bInfo[ bizindex ][ bEntPrice ],
        interior,
        bInfo[ bizindex ][ bEnter ][ 0 ],
        bInfo[ bizindex ][ bEnter ][ 1 ], 
        bInfo[ bizindex ][ bEnter ][ 2 ],
        bInfo[ bizindex ][ bEntranceInt ],
        bInfo[ bizindex ][ bEntranceVirw ],
        bInfo[ bizindex ][ bExit ][ 0 ],
        bInfo[ bizindex ][ bExit ][ 1 ],
        bInfo[ bizindex ][ bExit ][ 2 ],
        bInfo[ bizindex ][ bLocked ],
        bInfo[ bizindex ][ bBank ],
        _:bInfo[ bizindex ][ bType ],
        bInfo[ bizindex ][ bProducts ],
        bInfo[ bizindex ][ bPickupModel ],
        bInfo[ bizindex ][ bID ]);
    return mysql_pquery(DbHandle, query);
}

stock AddBusinessFurniture(bizindex, findex, Float:posx, Float:posy, Float:posz, Float:rotx = 0.0, Float:roty = 0.0 , Float:rotz = 0.0)
{
    new
        query[256],
        i;

    mysql_format(DbHandle, query, sizeof(query), "INSERT INTO business_furniture (business_id, furniture_id, pos_x, pos_y, pos_z, rot_x, rot_y, rot_z) VALUES (%d, %d, %f, %f, %f, %f, %f, %f)",
        GetBusinessID(bizindex), GetFurnitureID(findex), posx, posy, posz, rotx, roty, rotz);
    new Cache:result = mysql_query(DbHandle, query);
    
    for(new j = 0; j < MAX_BUSINESS_FURNITURE; j++)
        if(!BusinessFurniture[ bizindex ][ j ][ SqlId ])
        {
            i = j;
            break;
        }
    BusinessFurniture[ bizindex ][ i ][ SqlId ] = cache_insert_id();
    cache_delete(result);
    BusinessFurniture[ bizindex ][ i ][ FurnitureId ] = GetFurnitureID(findex);
    BusinessFurniture[ bizindex ][ i ][ ObjectId ] = CreateDynamicObject(GetFurnitureObjectId(findex), 
        posx, posy, posz, 
        rotx, roty, rotz, 
        BUSINESS_VIRTUAL_WORLD+bInfo[ bizindex ][ bID ],
        .streamdistance = 35.0);

    return BusinessFurniture[ bizindex ][ i ][ ObjectId ];
}

stock SaveBusinessFurnitureObject(bizindex, furniture_index, Float:fX, Float:fY, Float:fZ, Float:fRX, Float:fRY, Float:fRZ)
{
    new query[ 256 ];

    format(query, sizeof(query), "UPDATE business_furniture SET pos_x = %f, pos_y = %f, pos_z = %f, rot_x = %f, rot_y = %f, rot_z = %f WHERE id = %d",
        fX, fY, fZ, fRX, fRY, fRZ, BusinessFurniture[ bizindex ][ furniture_index ][ SqlId ]);
    mysql_pquery(DbHandle, query);

    SetDynamicObjectPos(BusinessFurniture[ bizindex ][ furniture_index ][ ObjectId ], fX, fY, fZ);
    SetDynamicObjectRot(BusinessFurniture[ bizindex ][ furniture_index ][ ObjectId ], fRX, fRY, fRZ);
}

stock GetBusinessFurniturePrice(bizindex, furnitureindex)
    return GetFurniturePrice(GetFurnitureIndex(BusinessFurniture[ bizindex ][ furnitureindex ][ FurnitureId ]));

stock GetBusinessFurnitureId(bizindex, furnitureindex)
    return BusinessFurniture[ bizindex ][ furnitureindex ][ FurnitureId ];

stock GetBusinessFurnitureName(bizindex, furnitureIndex)
{
    new s[MAX_FURNITURE_NAME ];
    if(!isnull(BusinessFurniture[ bizindex ][ furnitureIndex ][ Name ]))
        strcat(s, BusinessFurniture[ bizindex ][ furnitureIndex ][ Name ]);
    else 
        strcat(s, GetFurnitureName(GetFurnitureIndex(BusinessFurniture[ bizindex ][ furnitureIndex ][ FurnitureId ])));
    return s;
}

stock GetBusinessID(index)
    return bInfo[ index ][ bID ];

stock GetBusinessInteriorID(bizindex)
    return bInfo[ bizindex ][ bInteriorId ];


stock GetBusinessFurnitureObjectId(bizindex, furnitureindex)
    return BusinessFurniture[ bizindex ][ furnitureindex ][ ObjectId ];

stock GetBusinessVirtualWorld(bizindex)
    return bInfo[ bizindex ][ bID ] + BUSINESS_VIRTUAL_WORLD;

stock GetBusinessFurnitureObjectModel(bizindex, furnitureIndex)
    return GetFurnitureObjectId(BusinessFurniture[ bizindex ][ furnitureIndex ][ FurnitureId ]);

stock GetBusinessEntranceVirtualWorld(bizindex)
    return bInfo[ bizindex ][ bEntranceVirw ];

stock GetBusinessEntranceInteriorID(bizindex)
    return bInfo[ bizindex][ bEntranceInt ];

stock IsPlayerInRangeOfBizFurniture(playerid, bizindex, furnitureindex, Float:distance)
{
    new Float:pos[3];
    GetDynamicObjectPos(BusinessFurniture[ bizindex ][ furnitureindex ][ ObjectId ], pos[ 0 ], pos[ 1 ], pos[ 2 ]);
    return IsPlayerInRangeOfPoint(playerid, distance, pos[ 0 ], pos[ 1 ], pos[ 2 ]);
}

stock RemoveBusinessFurnitureTexture(bizindex, furnitureIndex, materialindex)
{
    new query[110], color, model;
    GetDynamicObjectMaterial(BusinessFurniture[ bizindex ][ furnitureIndex ][ ObjectId ], materialindex, model, query, query, color);
    SetDynamicObjectMaterial(BusinessFurniture[ bizindex ][ furnitureIndex ][ ObjectId ], materialindex, GetBusinessFurnitureObjectModel(bizindex, furnitureIndex), "none", "none", color);

    format(query, sizeof(query), "DELETE FROM business_furniture_textures WHERE furniture_id = %d AND `index` = %d",
        BusinessFurniture[ bizindex ][ furnitureIndex ][ SqlId ], materialindex);
    return mysql_pquery(DbHandle, query);
}


stock SetBusinessInteriorId(bizindex, interiorid)
{
        // interiorid parametras �ia yra NE GTA SA interjero ID, o interjero SQL ID(interiors.p).
    new query[120], Float:x, Float:y, Float:z;
    GetInteriorEntrancePos(interiorid, x, y, z);
    mysql_format(DbHandle, query, sizeof(query), "UPDATE business SET interior_id = %d, exit_x = %f, exit_y = %f, exit_z = %f WHERE id = %d",
        interiorid, x, y, z, bInfo[ bizindex ][ bID ]);
    mysql_pquery(DbHandle, query);

    bInfo[ bizindex ][ bInteriorId ] = interiorid;
    bInfo[ bizindex] [ bExit ][ 0 ] = x;
    bInfo[ bizindex] [ bExit ][ 1 ] = y;
    bInfo[ bizindex] [ bExit ][ 2 ] = z;
    return 1;
}

stock SetBusinessFurnitureTextureColo(bizindex, furnitureindex, materialindex, color)
{
    new query[180], txd[MAX_TXD_FILE_NAME], texture[ MAX_TEXTURE_NAME ], placeholder;
    GetDynamicObjectMaterial(BusinessFurniture[ bizindex ][ furnitureindex ][ ObjectId ], materialindex, placeholder, txd, texture, placeholder);
    SetDynamicObjectMaterial(BusinessFurniture[ bizindex ][ furnitureindex ][ ObjectId ], materialindex, GetBusinessFurnitureObjectModel(bizindex, furnitureindex), txd, texture, color);

    format(query, sizeof(query), "INSERT INTO business_furniture_textures (furniture_id, `index`, color) VALUES (%d,%d, %d) ON DUPLICATE KEY UPDATE color = VALUES(color)",
        BusinessFurniture[ bizindex ][ furnitureindex ][ SqlId ], materialindex, color);
    return mysql_pquery(DbHandle, query);
}

stock SetBusinessFurnitureTexture(bizindex, findex, materialindex, model, txdname[], texture[], color = 0)
{
    new query[400];
    format(query, sizeof(query),"INSERT INTO business_furniture_textures (furniture_id, `index`, object_model, txd_name, texture_name, color) VALUES(%d, %d, %d, '%s', '%s', %d) \
        ON DUPLICATE KEY UPDATE txd_name = VALUES(txd_name), texture_name = VALUES(texture_name), color = VALUES(color)",
        BusinessFurniture[ bizindex ][ findex ][ SqlId ], materialindex, model, txdname, texture, color);
    return mysql_pquery(DbHandle, query);
}

stock SetBusinessEntrancePos(bizindex, Float:x, Float:y, Float:z, interior, virtualworld)
{
    new query[170];
    mysql_format(DbHandle, query, sizeof(query), "UPDATE business SET entrance_x = %f, entrance_y = %f, entrance_z = %f, entrance_interior = %d, entrance_virtual = %d WHERE id = %d",
        x, y, z, interior, virtualworld, bInfo[ bizindex ][ bID ]);
    mysql_pquery(DbHandle, query);

    bInfo[ bizindex ][ bEnter ][ 0 ] = x;
    bInfo[ bizindex ][ bEnter ][ 1 ] = y;
    bInfo[ bizindex ][ bEnter ][ 2 ] = z;
    bInfo[ bizindex ][ bEntranceInt ] = interior;
    bInfo[ bizindex ][ bEntranceVirw ] = virtualworld;
    UpdateBusinessEntrance(bizindex);
    return 1;
}

stock SetBusinessPickupModel(bizindex, modelid)
{
    new query[80];
    mysql_format(DbHandle, query, sizeof(query), "UPDATE business SET pickup_model = %d WHERE id = %d",
        modelid, bInfo[ bizindex ][ bID ]);
    mysql_pquery(DbHandle, query);

    bInfo[ bizindex ][ bPickupModel ] = modelid;
    UpdateBusinessEntrance(bizindex);
    return 1;
}

stock SetBusinessFurnitureName(bizindex, furnitureindex, name[])
{
    new query[100];
    format(BusinessFurniture[ bizindex ][ furnitureindex ][ Name ], MAX_FURNITURE_NAME, name);
    mysql_format(DbHandle, query, sizeof(query), "UPDATE business_furniture SET name = '%e' WHERE id = %d", 
        name, BusinessFurniture[ bizindex ][ furnitureindex ][ SqlId ]);
    return mysql_pquery(DbHandle, query);
}

stock UpdateBusinessProducts(bizindex, amount)
{
    new query[80];

    bInfo[ bizindex][ bProducts ] = amount;
    format(query, sizeof(query),"UPDATE business SET products = %d WHERE id = %d",
        bInfo[ bizindex ][ bProducts ], bInfo[ bizindex ][ bID ]);
    return mysql_pquery(DbHandle, query);
}
stock DeleteBusinessFurniture(bizindex, furniture_index)
{
    new string[ 64 ];
    format( string, sizeof string, "DELETE FROM `business_furniture` WHERE `id` = %d",
        BusinessFurniture[ bizindex ][ furniture_index ][ SqlId ] );
    mysql_pquery(DbHandle, string);

    DestroyDynamicObject(BusinessFurniture[ bizindex ][ furniture_index ][ ObjectId]);
    BusinessFurniture[ bizindex ][ furniture_index ][ SqlId ] = 0;
    BusinessFurniture[ bizindex ][ furniture_index ][ FurnitureId ] = 0;
    return 1;
}

stock ToggleBusinessLock(bizindex)
{
    new query[80];
    bInfo[ bizindex ][ bLocked ] = !bInfo[ bizindex ][ bLocked ];
    format(query, sizeof(query), "UPDATE business SET locked = %d WHERE id = %d",
        bInfo[ bizindex ][ bLocked ], bInfo[ bizindex ][ bID ]);
    return mysql_pquery(DbHandle, query);
}

stock IsBusinessLocked(bizindex)
    return bInfo[ bizindex ][ bLocked ];

stock SaveBusinessWare(bizindex, wareindex)
{
    new query[128 + MAX_BUSINESS_WARE_NAME];


    // Jei dar n�ra sql id, rei�kia k� tik sukurta prek�.
    if(!BusinessWares[ bizindex ][ wareindex ][ Id ])
    {
        mysql_format(DbHandle, query, sizeof(query), "INSERT INTO business_wares (business_id, name, price) VALUES(%d, '%e', %d)",
            bInfo[ bizindex ][ bID ], BusinessWares[ bizindex ][ wareindex ][ Name ], BusinessWares[ bizindex ][ wareindex ][ Price ]);
        new Cache:result = mysql_query(DbHandle, query);
        BusinessWares[ bizindex ][ wareindex ][ Id ] = cache_insert_id();
        cache_delete(result);
        return 1;
    }
    else 
    {
        mysql_format(DbHandle, query, sizeof(query), "UPDATE business_wares SET name = '%e', price = %d WHERE id = %d",
            BusinessWares[ bizindex ][ wareindex ][ Name ], BusinessWares[ bizindex ][ wareindex ][ Price ], BusinessWares[ bizindex ][ wareindex ][ Id ]);
        return mysql_pquery(DbHandle, query);
    }
}

stock RemoveBusinessWare(bizindex, wareindex)
{
    new query[60];

    format(query, sizeof(query), "DELETE FROM business_wares WHERE id = %d LIMIT 1",
        BusinessWares[ bizindex ][ wareindex ][ Id ]);

    strdel(BusinessWares[ bizindex ][ wareindex ][ Name ], 0, MAX_BUSINESS_WARE_NAME);
    BusinessWares[ bizindex ][ wareindex ][ Id ] = 0;
    BusinessWares[ bizindex ][ wareindex ][ Price ] = 0;
    return mysql_pquery(DbHandle, query);
}

stock RemoveBusinessWares(bizindex)
{
    new query[80];
    mysql_format(DbHandle, query, sizeof(query), "DELETE FROM business_wares WHERE id = %d LIMIT " #MAX_BUSINESS_WARES ,
        bInfo[ bizindex ][ bID ]);
    mysql_pquery(DbHandle, query);

    static EmptyWares[ E_BUSINESS_WARES_DATA ];
    for(new i = 0; i < MAX_BUSINESS_WARES; i++)
        BusinessWares[ bizindex ][ i ] = EmptyWares;
    return 1;
}





/*
                                                                                                                                                  
                           ,,             ,,                             ,...                                      ,,                             
            `7MM"""Yb.     db           `7MM                           .d' ""                               mm     db                             
              MM    `Yb.                  MM                           dM`                                  MM                                    
              MM     `Mb `7MM   ,6"Yb.    MM  ,pW"Wq.   .P"Ybmmm      mMMmm`7MM  `7MM  `7MMpMMMb.  ,p6"bo mmMMmm `7MM  ,pW"Wq.`7MMpMMMb.  ,pP"Ybd 
              MM      MM   MM  8)   MM    MM 6W'   `Wb :MI  I8         MM    MM    MM    MM    MM 6M'  OO   MM     MM 6W'   `Wb MM    MM  8I   `" 
              MM     ,MP   MM   ,pm9MM    MM 8M     M8  WmmmP"         MM    MM    MM    MM    MM 8M        MM     MM 8M     M8 MM    MM  `YMMMa. 
              MM    ,dP'   MM  8M   MM    MM YA.   ,A9 8M              MM    MM    MM    MM    MM YM.    ,  MM     MM YA.   ,A9 MM    MM  L.   I8 
            .JMMmmmdP'   .JMML.`Moo9^Yo..JMML.`Ybmd9'   YMMMMMb      .JMML.  `Mbod"YML..JMML  JMML.YMbmd'   `Mbmo.JMML.`Ybmd9'.JMML  JMML.M9mmmP' 
                                                       6'     dP                                                                                  
                                                       Ybmmmd'                                                                                    
*/

stock ShowClothesShopDialog(playerid)
{
    new string[256];
    for(new i = 0; i < sizeof(ClothesShopCategories); i++)
    {
        strcat(string, ClothesShopCategories[ i ][ Name ]);
        strcat(string, "\n");
    }
    //ShowPlayerDialog(playerid, DIALOG_BIZ_CLOTHESSHOP_LIST, DIALOG_STYLE_LIST, "Daikt� pirkimas", "Skarel�s ant galvos - $50\nKepur�s - $100\nSkryb�l�s - $50\nAkiniai - $100\n�almai - $200\nKauk�s - $50\nSkarel�s ant veido - 50$\nKiti daiktai", "Pasirinkti", "U�daryti" );
    ShowPlayerDialog(playerid, DIALOG_BIZ_CLOTHESSHOP_LIST, DIALOG_STYLE_LIST, "Daikt� pirkimas", string, "Pasirinkti", "U�daryti" );
    return 1;
}


stock ShowPlayerClothesShopWares(playerid, index)
{
    new models[ 100 ], modelCount;
    SetPVarInt(playerid, "ClothesShopCategoryIndex", index);
    for(new i = 0; i < sizeof(ClothesShopItems); i++)
        if(ClothesShopItems[ i ][ CategoryId ] == ClothesShopCategories[ index ][ Id ])
            models[ modelCount ] = ClothesShopItems[ i ][ Modelid ];

    ShowModelSelectionMenuEx(playerid, models, modelCount, ClothesShopCategories[ index ][ Name ], 106);
}


BizOwnerMenu::NameChangeDialog(playerid, errorstr[] = "")
{
    new string[128];
    if(!isnull(errorstr))
    {
        strcat(string, "{AA1100}");
        strcat(string, errorstr);
        strcat(string, "\n");
    }
    strcat(string, "{FFFFFF}�emiau �ra�ykite biznio pavadinim�:");
    ShowPlayerDialog(playerid, DIALOG_BIZ_NAME_CHANGE, DIALOG_STYLE_INPUT,"Biznio pavadinimo keitimas", string, "Patvirtinti", "At�aukti");
    return 1;
}

BizOwnerMenu::EntrancePriceChangeDialog(playerid, errorstr[] = "")
{
    new string[256];
    if(!isnull(errorstr))
    {
        strcat(string, "{AA1100}");
        strcat(string, errorstr);
        strcat(string, "\n");
    }
    strcat(string, "{FFFFFF}�emiau �ra�ykite nauj� verslo ��jimo kain�:");
    ShowPlayerDialog(playerid, DIALOG_BIZ_ENTR_PRICE_CHANGE, DIALOG_STYLE_INPUT,"Biznio ��jimo kainos keitimas", string, "Patvirtinti", "At�aukti");
    return 1;
}

BizOwnerMenu::MoneyWithdrawDialog(playerid, errorstr[] = "")
{
    new string[256], 
        bizIndex = GetPVarInt(playerid, "BizIndex");
    if(!isnull(errorstr))
    {
        strcat(string, "{AA1100}");
        strcat(string, errorstr);
        strcat(string, "\n");
    }
    format(string, sizeof(string), "%s{FFFFFF}J�s� verslo banko s�skaitoje yra: %d\n �ra�ykite sum�, kuri�norite nusiimti.", string, bInfo[ bizIndex ][ bBank ]);
    ShowPlayerDialog(playerid, DIALOG_BIZ_BANK_WITHDRAW, DIALOG_STYLE_INPUT, "Verslo bankas - nu�mimas", string, "Nuimti", "I�eiti");
    return 1;
}


BizOwnerMenu::MoneyDepositDialog(playerid, errorstr[] = "")
{
    new string[256];
    if(!isnull(errorstr))
    {
        strcat(string, "{AA1100}");
        strcat(string, errorstr);
        strcat(string, "\n");
    }
    format(string, sizeof(string), "%s{FFFFFF}J�s turite pinig�: $%d\n �ra�ykite sum�, kuri� norite pad�ti", string, GetPlayerMoney(playerid));
    ShowPlayerDialog(playerid, DIALOG_BIZ_BANK_DEPOSIT, DIALOG_STYLE_INPUT, "Verslo bankas - pad�jimas", string, "Pad�ti", "I�eiti");
    return 1;
}

BizOwnerMenu::WareListEditMain(playerid)
{
    new bizIndex = GetPVarInt(playerid, "bizIndex"),
        string[512],
        header[MAX_BUSINESS_NAME ];

    if(isnull(bInfo[ bizIndex ][ bName ]))
        strcat(header, "�");
    else 
        strcat(header, bInfo[ bizIndex ][ bName ]);

    for(new i = 0; i < MAX_BUSINESS_WARES; i++)
    {
        if(isnull(BusinessWares[ bizIndex ][ i ][ Name ]))
            strcat(string, #BUSINESS_WARES_EMPTY_SLOT "\n");
        else    
            format(string, sizeof(string),"%s%s\t%d\n",
                string,
                BusinessWares[ bizIndex ][ i ][ Name ],
                BusinessWares[ bizIndex ][ i ][ Price ]);
    }
    ShowPlayerDialog(playerid, DIALOG_BIZ_WARE_LIST_EDIT, DIALOG_STYLE_LIST, header, string, "Pirkti", "I�eiti");
    return 1;
}

BizOwnerMenu::WareEnterName(playerid, errorstr[] = "")
{   
    new string[128];
    if(!isnull(errorstr))
    {
        strcat(string, "{AA1100}");
        strcat(string, errorstr);
        strcat(string, "\n");
    }
    strcat(string, "{FFFFFF}�ra�ykite prek�s pavadinim�:");
    ShowPlayerDialog(playerid, DIALOG_BIZ_WARE_LIST_EDIT_ENTER, DIALOG_STYLE_INPUT, "Verslo preki� keitimas", string, "Gerai", "I�eiti");
    return 1;
}
BizOwnerMenu::WareBusinessPriceChange(playerid, errorstr[] = "")
{
    new string[128];
    if(!isnull(errorstr))
    {
        strcat(string, "{AA1100}");
        strcat(string, errorstr);
        strcat(string, "\n");
    }
    strcat(string, "{FFFFFF}�ra�ykite prek�s kain�:");
    ShowPlayerDialog(playerid, DIALOG_BIZ_WARE_LIST_EDIT_PRICE, DIALOG_STYLE_INPUT, "Verslo preki� keitimas", string, "Gerai", "I�eiti");
    return 1;
}




StopBusinessBuyingCargo(index, cargoid)
{
    new query[160];
    foreach(CommodityIterator, i)
    {
        if(Commodities[ i ][ IndustryId ] == bInfo[ index ][ bID ]
            && Commodities[ i ][ CargoId] == cargoid
            && Commodities[ i ][ IsBusinessCommodity ])
        {
            format(query,sizeof(query), "DELETE FROM commodities WHERE industry_id = %d AND cargo_id = %d AND sell_buy_status = 'Buying' AND Type = 'Business' LIMIT 1",
                bInfo[ index ][ bID ], cargoid);
            mysql_pquery(DbHandle, query);
            Commodities[ i ][ IndustryId ] = 0;
            Commodities[ i ][ CargoId ] = 0;
            Commodities[ i ][ Price ] = 0;
            new next;
            Iter_SafeRemove(CommodityIterator, i, next);
            i = next;
            SaveBusiness(index);
            return 1;
        }
    }
    return 0;
}


GetBusinessCargoStock(index)
    return bInfo[ index ][ bProducts ];
	
stock GetBusinessCargo(E_BUSINESS_TYPES:biz_type)
{
    new type, cargoid;
    if(Malloc_SlotSize(BizCargoTypes) == -1)
        return 0;

    for(new i = 0; i < Malloc_SlotSize(BizCargoTypes); i += 2)
    {
        type = mget(BizCargoTypes, i);
        cargoid = mget(BizCargoTypes, i+1);
        if(type == _:biz_type)
            return cargoid;
    }
    return 0;
}

stock OnPlayerEnterBiz(playerid,biz)
{
    GivePlayerMoney(playerid,-bInfo[biz][bEntPrice]);
    new biudzetui = floatround( (bInfo[ biz ][ bEntPrice ] * 20)/100 );
    Biudzetas += biudzetui;

    bInfo[biz][bBank] += (bInfo[biz][bEntPrice] - biudzetui );
    if ( bInfo[biz][bEntPrice] > 11 )
        PayLog( pInfo[ playerid ][ pMySQLID ],11, bInfo[ biz ][ bID ]-5000, bInfo[ biz ][ bEntPrice ] );

    SaveBusiness(biz);
    SaveAccount( playerid );
    return 1;
}


/*
                                                                                                                  
                                                                                             ,,                   
                            `7MMF' `YMM'                                                   `7MM                   
                              MM   .M'                                                       MM                   
                              MM .d"     ,pW"Wq.`7MMpMMMb.pMMMb.   ,6"Yb.  `7MMpMMMb.   ,M""bMM  ,pW"Wq.  ,pP"Ybd 
                              MMMMM.    6W'   `Wb MM    MM    MM  8)   MM    MM    MM ,AP    MM 6W'   `Wb 8I   `" 
                              MM  VMA   8M     M8 MM    MM    MM   ,pm9MM    MM    MM 8MI    MM 8M     M8 `YMMMa. 
                              MM   `MM. YA.   ,A9 MM    MM    MM  8M   MM    MM    MM `Mb    MM YA.   ,A9 L.   I8 
                            .JMML.   MMb.`Ybmd9'.JMML  JMML  JMML.`Moo9^Yo..JMML  JMML.`Wbmd"MML.`Ybmd9'  M9mmmP' 
                                                                                                                  
                                                                                                                  
*/

CMD:cargoprice(playerid,params[])
{
    new price,string[220];
    foreach(new h : Business)
    {
        if(IsPlayerInRangeOfPoint(playerid, 2.0, bInfo[h][bEnter][0],bInfo[h][bEnter][1],bInfo[h][bEnter][2]))
        {
            new bizCargo = GetBusinessCargo(bInfo[ h ][ bType ]);

            if(!IsPlayerBusinessOwner(playerid, h)) 
                return SendClientMessage(playerid,GRAD,"Verslas n�ra j�s�, todel J�s negalite keisti produktu kainos");
            if(sscanf( params, "d", price ) || price < 0) 
                return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /cargoprice [kaina]");
            if(price > bInfo[ h ][ bBank ])
                return SendClientMessage(playerid, GRAD, "[ Klaida ] J�s� versle n�ra pakankamai pinig� net vienam produktui!");
            if(!GetBusinessCargo(bInfo[ h ][ bType ]))
                return SendClientMessage(playerid, GRAD, " [ Klaida !] J�s� verslas nereikalauja joki� produkt�!");
            if(bInfo[ h ][ bProducts ] >= MAX_BUSINESS_PRODUCTS)
                return SendClientMessage(playerid, GRAD, "Klaida, j�s� versle daugiau produkt� nebetelpa.");

            if(price == 0)
            {
                StopBusinessBuyingCargo(h, GetBusinessCargo(bInfo[ h ][ bType ]));
                SendClientMessage(playerid, COLOR_NEWS, "J�s� verslas nebepriims produkt�.");
                return 1;
            }

			new averagePrice, sellerCount;
            foreach(CommodityIterator, i)
                if(Commodities[ i ][ CargoId ] == bizCargo && Commodities[ i ][ SellBuyStatus ] == Selling)
                {
					sellerCount++;
					averagePrice += Commodities[ i ][ Price ];
                }
			averagePrice /= sellerCount;	
			// Jei kaina ma�esn� u� vidurk�
			if(price < averagePrice)
			{
				format(string, sizeof(string), "Klaida, minimali kaina yra %d.", averagePrice);
				return SendClientMessage(playerid, GRAD, string);
			}
			// Jei kaina didesn� u� vidurk�+20%
			if(price > averagePrice + averagePrice / 100 * 20)
			{
				format(string, sizeof(string), "Klaida, maksimali kaina yra %d.", averagePrice + averagePrice / 100 * 20);
				return SendClientMessage(playerid, GRAD, string);
			}
            for(new i = 0; i < sizeof(Commodities); i++)
            {
                // Jei jau kazka pirko anksciau.
                if(Commodities[ i ][ IndustryId ] == bInfo[ h ][ bID ] && Commodities[ i ][ IsBusinessCommodity ])
                {
                    Commodities[ i ][ Price ] = price;
                    format(string, sizeof(string),"UPDATE commodities SET Price = %d WHERE industry_id = %d AND type = 'Business' AND cargo_id = %d",
                        price, bInfo[ h ][ bID ], GetBusinessCargo(bInfo[ h ][ bType ]));
                    mysql_pquery(DbHandle, string);
                    break;
                }
                if(Commodities[ i ][ IndustryId ] || Commodities[ i ][ CargoId ])
                    continue;


                Commodities[ i ][ IndustryId ] = bInfo[ h ][ bID ];
                Commodities[ i ][ CargoId ] = GetBusinessCargo(bInfo[ h ][ bType ]);
                Commodities[ i ][ CurrentStock ] = 0;
                Commodities[ i ][ SellBuyStatus ] = Buying;
                Commodities[ i ][ IsBusinessCommodity ] = true;
                Commodities[ i ][ Price ] = price;
                Commodities[ i ][ CurrentStock ] = bInfo[ h ][ bProducts ];
                Itter_Add(CommodityIterator, i);
                format(string,sizeof(string),"INSERT INTO commodities (industry_id, cargo_id, sell_buy_status, current_stock, type, price) VALUES (%d,%d,'Buying', %d, 'Business', %d) ON DUPLICATE KEY UPDATE price = VALUES(price)",
                    bInfo[ h ][ bID ], GetBusinessCargo(bInfo[ h ][ bType ]), bInfo[ h ][ bProducts ] / 50, price);
                mysql_pquery(DbHandle, string);
                break;
            }
            SaveBusiness(h);
            format(string,sizeof(string),"Verslo produktu kaina nustatyta � %d",price);
            SendClientMessage(playerid, COLOR_NEWS, string);
            return 1;
        }
    }
    SendClientMessage(playerid, GRAD, "[ Klaida ] J�s neesate prie verslo ��jimo!");
    return 1;
}


CMD:biz(playerid)
{
    new string[ 512 ],
        bizIndex = GetPlayerBusinessIndex(playerid),
        lockstatus[ 16 ],
        biztype [ 32 ];

    if(bizIndex == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s turite b�ti verslo viduje arba prie ��jimo.");

    if(!IsPlayerBusinessOwner(playerid, bizIndex)) 
        return SendClientMessage(playerid,GRAD,"Verslas n�ra j�s�, todel J�s jo valdyti");

    lockstatus = (IsBusinessLocked(bizIndex)) ? ("U�rakinta") : ("Atrakinta");
    switch(bInfo[ bizIndex ][ bType ])
    {
        case None: biztype = "Joks";
        case Supermarket: biztype = "Parduotuv�";
        case Cafe: biztype = "Kavin�";
        case Bar: biztype = "Baras/Klubas";
        case ClothesShop: biztype = "Drabu�i� parduotuv�";
        case BarberShop: biztype = "Kirpykla";
    }

    // Jei reikalingos jam prek�s, su�inom po kiek pardavin�ja tas prekes.
    new price, bizCargo = GetBusinessCargo(bInfo[ bizIndex ][ bType ]), cargoPrice;
    if(GetBusinessCargoIndex(bizIndex, GetBusinessCargo(bInfo[ bizIndex ][ bType ])) != -1)
        price = Commodities[ GetBusinessCargoIndex(bizIndex, GetBusinessCargo(bInfo[ bizIndex ][ bType ])) ][ Price ];
    foreach(CommodityIterator, i)
        if(Commodities[ i ][ CargoId ] == bizCargo && Commodities[ i ][ SellBuyStatus ] == Selling)
        {
            cargoPrice = Commodities[ i ][ Price ];
            break;
        }


    format( string, sizeof(string), "- Pavadinimas \t[ %s ]\n\
                                  - ��jimo kaina \t[ %d ]\n\
                                  - Biznio banke \t[ %d ]\n\
                                  - Durys \t\t[ %s ]\n\
                                  - Tipas \t\t[ %s ]\n\
                                  - Preki� kiekis \t[ %d ]\n\
                                  - Prek�s ir j� kainos\n\
                                  - Preki� kaina[ %d ]( Industrij� pardavimo kaina: %d)", 
                                  bInfo[ bizIndex ][ bName ], 
                                  bInfo[ bizIndex ][ bEntPrice ], 
                                  bInfo[ bizIndex ][ bBank ], 
                                  lockstatus, 
                                  biztype, 
                                  bInfo[ bizIndex ][ bProducts ],
                                  price,
                                  cargoPrice);
    SetPVarInt(playerid, "BizIndex", bizIndex);
    ShowPlayerDialog(playerid, DIALOG_BIS_OWNER_MENU_MAIN, DIALOG_STYLE_LIST, "Biznio meniu",string, "Rinktis", "I�jungti");
    return 1;
}


CMD:buy(playerid, params[])
{
    new bizIndex = GetPlayerBusinessIndex(playerid);

    if(bizIndex == -1 || !IsPlayerInBusiness(playerid, bizIndex))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s turite b�ti verslo viduje kad gal�tum�te naudoti �i� komand�.");

        // Teori�kai, jei verlas niekam nepriklauso netur�t� b�ti galima � j� �eiti.
    if(!IsBusinessOwned(bizIndex))
        return 1;

    new string[1024];
    SetPVarInt(playerid, "BizIndex", bizIndex);
    switch(bInfo[ bizIndex ][ bType ])
    {
        case Supermarket, Bar, Cafe:
        {
            new header[MAX_BUSINESS_NAME ];
            if(!isnull(bInfo[ bizIndex ][ bName ]))
                strcat(header, bInfo[ bizIndex ][ bName ]);
            else 
                strcat(header, "-");

            for(new i = 0; i < MAX_BUSINESS_WARES; i++)
            {
                if(isnull(BusinessWares[ bizIndex ][ i ][ Name ]))
                    strcat(string, #BUSINESS_WARES_EMPTY_SLOT "\n");
                else    
                    format(string, sizeof(string),"%s%s%d\n",
                        string,
                        BusinessWares[ bizIndex ][ i ][ Name ],
                        BusinessWares[ bizIndex ][ i ][ Price ]);
            }
            ShowPlayerDialog(playerid, DIALOG_BIZ_WARE_LIST, DIALOG_STYLE_LIST, header, string, "Pirkti", "I�eiti");
        } 
        case ClothesShop:
        {
            ShowClothesShopDialog(playerid);
        }
        case BarberShop:
        {
            new models[ 3 ];
            models[0] = 19516;
            models[1] = 19518;
            models[2] = 19274;
            ShowModelSelectionMenuEx(playerid, models, 3, "Perukai", 115);
        }
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "�is verslas nieko neparduoda.");
    }
    return 1;
}

CMD:buybiz(playerid)
{
    new bizIndex = GetPlayerBusinessIndex(playerid);

    if(bizIndex == -1 || IsPlayerInBusiness(playerid, bizIndex))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, turite stov�ti prie verslo ��jimo.");


    if(pInfo[ playerid ][ pLevel ] < 4) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nusipirkti bizn�/versl� gal�site tik pasiek�s 4 lyg�.");

    if(IsBusinessOwned(bizIndex))
        return SendClientMessage(playerid,COLOR_LIGHTRED,"D�mesio, verslas prie kurio esate jau turi savo savinink� ir n�ra parduodamas.");

    if(PlayerMoney[ playerid ] < bInfo[ bizIndex ][ bPrice ]) 
        return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, Jums nepakanka gryn�j� pinig�, kad gal�tumete nusipirkti versl� prie kurio esate.");

    bInfo[ bizIndex ][ bOwner ] = GetPlayerSqlId(playerid);
    GivePlayerMoney(playerid, -bInfo[ bizIndex ][ bPrice ]);
    UpdateBusinessEntrance(bizIndex);
    PayLog(GetPlayerSqlId(playerid), 2, -1, -bInfo[ bizIndex ][ bPrice ]);
    SaveBusiness(bizIndex);
    return 1;
}
CMD:sellbiz(playerid, params[])
{
    new bizIndex = GetPlayerBusinessIndex(playerid),
        giveplayerid,
        price,
        IP[ 16 ],
        IP2[ 16 ],
        string[130];

    if(bizIndex == -1 || IsPlayerInBusiness(playerid, bizIndex))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, turite stov�ti prie verslo ��jimo.");

    if(!IsPlayerBusinessOwner(playerid, bizIndex)) 
        return SendClientMessage(playerid,GRAD,"Verslas n�ra j�s�, todel J�s jo negalite parduoti");

    if(sscanf(params, "ud", giveplayerid, price))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /sellbiz [�aid�jo id/Dalis vardo] [Kaina]");

    if(!IsPlayerConnected(giveplayerid))  
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, tokio �aid�jo n�ra.");

    if(!IsPlayerInRangeOfPlayer(playerid, giveplayerid, 10.0)) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti �io veiksmo su veik�ju, kuris n�ra �alia J�s�.");

    if(price < BUSINESS_PRICE_MIN || price > BUSINESS_PRICE_MAX) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Kaina negali buti ma�esn� negu " #BUSINESS_PRICE_MIN " ir didesn� negu " #BUSINESS_PRICE_MAX ".");


    GetPlayerIp(playerid, IP, sizeof(IP));
    GetPlayerIp(giveplayerid, IP2, sizeof(IP2));

    if(!strcmp(IP, IP2, true) || pInfo[ playerid ][ pUcpID ] == pInfo[ giveplayerid ][ pUcpID ])
        return true;

    format(string,sizeof(string),"J�s siulote jam %s,kad jis nupirktu j�s� versl� u�: $%d.", GetPlayerNameEx(giveplayerid), price);
    SendClientMessage(playerid,COLOR_WHITE,string);
    format(string,sizeof(string),"Verslo savininkas %s si�lo jums nupirkti jo versl�  u�: $%d, jeigu sutinkate, ra�ykite /accept biz.", GetPlayerNameEx(playerid),price);
    SendClientMessage(giveplayerid,COLOR_WHITE,string);
    Offer[ giveplayerid ][ 2 ] = playerid;
    OfferPrice[ giveplayerid ][ 2 ] = price;
    OfferID[ giveplayerid ][ 2 ] = bizIndex;
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





#define DIALOG_BMENU_MAIN               25
#define DIALOG_BMENU_NEW_PRICE          6001
#define DIALOG_BMENU_INPUT_INDEX        6002
#define DIALOG_BMENU_SELECT_TYPE        6003
#define DIALOG_BMENU_NEW_PICKUP_MODEL   6004
#define DIALOG_BMENU_CONFIRM_WIPE       6005

static PlayerUsedBusinessIndex[ MAX_PLAYERS ] = {-1, ... };


enum E_BUSINESS_INDEX_USAGE 
{
    BusinessPriceChange,
    BusinessRemoveOwner,
    BusinessRemoveBusiness,
    BusinessEntrancePositionChange,
    BusinessInteriorChange,
    BusinessInformation,
    BusinessInputNewPickupModel,
    BusinessSelectNewType
};


stock BusinessManagementDialog.ShowMain(playerid)
{
    ShowPlayerDialog(playerid, DIALOG_BMENU_MAIN, DIALOG_STYLE_LIST,"Bizni� meniu",
        "- Kurti nauj� \n\
        - Keisti kaina\n\
        - Pa�alinti savinink�\n\
        - Pa�alinti\n\
        - Perkelti ��jim� pagal ID\n\
        - �i�r�ti interjerus\n\
        - Keisti interjera pagal biznio ID\n\
        - Verslo informacija\n\
        - Keisti tip�\n\
        - Keisti pickup model�\n\
        - I�trinti ir kompensuoti visus biznius",
        "Rinktis", "At�aukti" );
    return 1;
}

stock BusinessManagementDialog.InputNewPrice(playerid, errostr[] = "")
{
    new string[64];
    if(!isnull(errostr))
    {
        strcat(string, "{AA0000}");
        strcat(string, errostr);
        strcat(string,"\n{FFFFFF}");

    }
    strcat(string, "�ra�ykite norima verslo kain�:");
    ShowPlayerDialog(playerid, DIALOG_BMENU_NEW_PRICE, DIALOG_STYLE_INPUT,"Verslo kainos pakeitimas", string, "Patvirtinti", "At�aukti");
    return 1;
}

stock BusinessManagementDialog.InputIndex(playerid, E_BUSINESS_INDEX_USAGE:usage, errostr[] = "")
{
    new string[64];
    if(!isnull(errostr))
    {
        strcat(string, "{AA0000}");
        strcat(string, errostr);
        strcat(string,"\n{FFFFFF}");

    }
    strcat(string, "�ra�ykite norimo redaguoti verslo ID:");
    SetPVarInt(playerid, "IndexUsage", _:usage);
    ShowPlayerDialog(playerid, DIALOG_BMENU_INPUT_INDEX, DIALOG_STYLE_INPUT,"Verslo ID �ra�ymas", string,"Patvirtinti", "Atgal");
    return 1;
}

stock BusinessManagementDialog.Information(playerid, index)
{
    new string[ 2048 ];
    string = GetSqlIdName(bInfo[ index ][ bOwner ]);

    format(string, sizeof(string),"Verslo ID serveryje: %d\n\
        Verslo ID duomen� baz�je: %d\n\
        Pavadinimas: %s\n\
        Verslo savininkas: %s\n\
        Verslo u�rakintas: %s\n\
        Interjero ID: %d\n\
        Kaina: %d\n\
        ��jimo kaina: %d\n\
        Tipas: %s\n\
        Produkt� kiekis: %d\n\
        Pickup modelis: %d\n\
        \n\
        Bald� skai�ius: %d\n",
        index,
        bInfo[ index][ bID ],
        bInfo[ index ][ bName ],
        (bInfo[ index ][ bOwner ] == BUSINESS_OWNER_NULL) ? ("n�ra") : (string),
        (bInfo[ index] [ bLocked ]) ? ("Taip") : ("Ne"),
        bInfo[ index ][ bInteriorId ],
        bInfo[ index ][ bPrice ],
        bInfo[ index ][ bEntPrice ],
        GetBusinessTypeName(bInfo[ index ][ bType ]),
        bInfo[ index ][ bProducts ],
        bInfo[ index ][ bPickupModel ],
        GetBusinessFurnitureCount(index)
    );

    if(bInfo[ index ][ bType ] == Supermarket || bInfo[ index ][ bType ] == Cafe || bInfo[ index ][ bType ] == Bar)
    {
        strcat(string, "\n\n___Verslo parduodamos prek�s___\n\n");

        for(new i = 0; i < MAX_BUSINESS_WARES; i++)
            if(strcmp(BusinessWares[ index ][ i ][ Name ], BUSINESS_WARES_EMPTY_SLOT))
                format(string, sizeof(string), "%s%s %d",string, BusinessWares[ index ][ i ][ Name ], BusinessWares[ index ][ i ][ Price ]);
    }
    

    ShowPlayerDialog(playerid, 9999, DIALOG_STYLE_MSGBOX, "Verslo informcija", string, "Gerai", "");
    return 1;
}

stock BusinessManagementDialog.SelectNewType(playerid)
{
    new string[512];
    for(new E_BUSINESS_TYPES:i; i < E_BUSINESS_TYPES; i++)
        format(string, sizeof(string),"%s%s\n", string, GetBusinessTypeName(i));

    ShowPlayerDialog(playerid, DIALOG_BMENU_SELECT_TYPE, DIALOG_STYLE_LIST, "Verslo tipo pasirinkimas", string, "Pasirinkti", "I�eiti");
    return 1;
}


stock BusinessManagementDialog.InputNewPickupModel(playerid, errostr[] = "")
{
    new string[64];
    if(!isnull(errostr))
    {
        strcat(string, "{AA0000}");
        strcat(string, errostr);
        strcat(string,"\n{FFFFFF}");

    }
    strcat(string, "�ra�ykite norima verslo pickup model�:");
    ShowPlayerDialog(playerid, DIALOG_BMENU_NEW_PICKUP_MODEL, DIALOG_STYLE_INPUT,"Verslo pickup modelio pakeitimas", string, "Patvirtinti", "At�aukti");
    return 1;
}

stock BusinessManagementDialog.ConfirmWipe(playerid)
{
    ShowPlayerDialog(playerid, DIALOG_BMENU_CONFIRM_WIPE, DIALOG_STYLE_MSGBOX, "{FF0000}D�mesio!", "�is veiksmas pa�alins visus serveryje esan�ius verslus ir j� informacij�\nVerslo kaina bus gra�inta savininkui.\n�io proceso atstatyti ne�manoma.", "T�sti", "I�eiti");
    return 1;
}

BusinessManagementDialog.OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
    switch(dialogid)
    {
        case DIALOG_BMENU_MAIN:
        {
            if(!response)
                return 1;

            new index = GetPlayerBusinessIndex(playerid);
            PlayerUsedBusinessIndex[ playerid ] = index;
            switch(listitem)
            {
                // Verslo suk�rimas
                case 0:
                {
                    new Float:x, Float:y, Float:z;
                    GetPlayerPos(playerid, x, y, z);

                    index = AddBusiness(x, y, z, GetPlayerInterior(playerid), GetPlayerVirtualWorld(playerid));

                    if(index == -1)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, verslo sukurti nepavyko.");

                    new string[60];
                    format(string, sizeof(string), "Verslas buvo s�kmingai sukurtas, jo id: %d", index);
                    SendClientMessage(playerid, COLOR_LIGHTRED, string);
                    Streamer_Update(playerid);
                }
                // Kainos keitimas
                case 1:
                {
                    if(index == -1)
                    {
                        BusinessManagementDialog.InputIndex(playerid, BusinessPriceChange);
                    }
                    else 
                    {
                        BusinessManagementDialog.InputNewPrice(playerid);
                    }
                }
                // Pa�alinti savinink�
                case 2:
                {
                    if(index == -1)
                    {
                        BusinessManagementDialog.InputIndex(playerid, BusinessRemoveOwner);
                    }
                    else 
                    {
                        RemoveBusinessOwner(index);
                        SendClientMessage(playerid, COLOR_NEWS, "Verslo savininkas s�kmingai pa�alintas.");
                    }
                }
                // Verslo pa�alinimas
                case 3:
                {
                    if(index == -1)
                    {
                        BusinessManagementDialog.InputIndex(playerid, BusinessRemoveBusiness);
                    }
                    else 
                    {
                        DeleteBusiness(index);
                        SendClientMessage(playerid, COLOR_NEWS, "Verslas s�kmingai pa�alintas.");
                    }
                }
                // ��jimo perk�limas
                case 4:
                {
                    BusinessManagementDialog.InputIndex(playerid, BusinessEntrancePositionChange);
                }
                // Interjer� per�i�ra.
                case 5:
                {
                    ShowInteriorPreviewForPlayer(playerid, "business");
                }
                // Interjero keitimas
                case 6:
                {
                    if(!IsPlayerInAnyInterior(playerid))
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s turite b�ti interjere.");

                    BusinessManagementDialog.InputIndex(playerid, BusinessInteriorChange);
                }
                // Verslo informacija
                case 7:
                {
                    if(index == -1)
                    {
                        BusinessManagementDialog.InputIndex(playerid, BusinessInformation);
                    }
                    else 
                    {
                        BusinessManagementDialog.Information(playerid, index);
                    }
                }
                // Tipo keitimas
                case 8:
                {
                    if(index == -1)
                    {
                        BusinessManagementDialog.InputIndex(playerid, BusinessSelectNewType);
                    }
                    else 
                    {
                        BusinessManagementDialog.SelectNewType(playerid);
                    }
                }
                // Keisti pickup modeli
                case 9:
                {
                    if(index == -1)
                    {
                        BusinessManagementDialog.InputIndex(playerid, BusinessInputNewPickupModel);
                    }
                    else 
                    {
                        BusinessManagementDialog.InputNewPickupModel(playerid);
                    }
                }
                // Vis� versl� pa�alinimas ir �aid�jams pinig� atidavimas.
                case 10:
                {
                    BusinessManagementDialog.ConfirmWipe(playerid);
                }
            }
            return 1;
        }
        case DIALOG_BMENU_INPUT_INDEX:
        {
            if(!response)
                return BusinessManagementDialog.ShowMain(playerid);

            new index,
                E_BUSINESS_INDEX_USAGE:usage = E_BUSINESS_INDEX_USAGE: GetPVarInt(playerid, "IndexUsage");

            if(sscanf(inputtext, "i", index))
                return BusinessManagementDialog.InputIndex(playerid, usage, "�veskite skai�i�.");
            if(index < 0 || index >= MAX_BIZNES || !IsValidBusiness(index))
                return BusinessManagementDialog.InputIndex(playerid, usage, "Verslo su tokiu ID n�ra.");

            PlayerUsedBusinessIndex[ playerid ] = index;
            switch(usage)
            {
                case BusinessPriceChange: BusinessManagementDialog.InputNewPrice(playerid);
                case BusinessRemoveOwner:
                {
                    RemoveBusinessOwner(index);
                    SendClientMessage(playerid, COLOR_NEWS, "Verslo savininkas s�kmingai pa�alintas.");
                }
                case BusinessRemoveBusiness:
                {
                    DeleteBusiness(index);
                    SendClientMessage(playerid, COLOR_NEWS, "Verslo pa�alintas s�kmingai.");
                }
                case BusinessEntrancePositionChange:
                {
                    new Float:x, Float:y, Float:z;
                    GetPlayerPos(playerid, x, y, z);
                    SetBusinessEntrancePos(index, x, y, z, GetPlayerInterior(playerid), GetPlayerVirtualWorld(playerid));
                    SendClientMessage(playerid, COLOR_NEWS, "Verslo ��jimas s�kmingai perkeltas.");
                }
                case BusinessInteriorChange:
                {
                    SetBusinessInteriorId(index, GetPlayerInteriorId(playerid));
                    SendClientMessage(playerid, COLOR_NEWS, "Verslo interjeras s�kmingai pakeistas");
                }
                case BusinessInformation: BusinessManagementDialog.Information(playerid, index);
                case BusinessInputNewPickupModel: BusinessManagementDialog.InputNewPickupModel(playerid);
                case BusinessSelectNewType: BusinessManagementDialog.SelectNewType(playerid);
            }
            DeletePVar(playerid, "IndexUsage");
            return 1;
        }
        case DIALOG_BMENU_NEW_PRICE:
        {
            if(!response)
                return BusinessManagementDialog.ShowMain(playerid);

            if(PlayerUsedBusinessIndex[ playerid ] == -1)
                return 0;

            new price, string[70];

            if(sscanf(inputtext, "i", price))
                return BusinessManagementDialog.InputNewPrice(playerid, "Pra�ome �vesti skai�i�.");

            if(price < 0)
                return BusinessManagementDialog.InputNewPrice(playerid, "Kaina turi b�ti didesn� nei 0.");

            bInfo[ PlayerUsedBusinessIndex[ playerid ] ][ bPrice ] = price;
            UpdateBusinessEntrance(PlayerUsedBusinessIndex[ playerid ]);
            SaveBusiness(PlayerUsedBusinessIndex[ playerid ]);

            format(string, sizeof(string), "Verslo kurio ID: %d, kaina buvo pakeista �: %d ", PlayerUsedBusinessIndex[ playerid ], price);
            SendClientMessage(playerid, COLOR_WHITE, string);
            return 1;
        }
        case DIALOG_BMENU_NEW_PICKUP_MODEL:
        {
            if(!response)
                return BusinessManagementDialog.ShowMain(playerid);

            new model, string[90];
            if(sscanf(inputtext, "i", model))
                return BusinessManagementDialog.InputNewPickupModel(playerid, "Pra�ome �vesti skai�i�.");

            if(model < 1 || model > 20000)
                return BusinessManagementDialog.InputNewPickupModel(playerid, "Negalimas modelio ID.");

            SetBusinessPickupModel(PlayerUsedBusinessIndex[ playerid ], model);
            Streamer_Update(playerid);

            format(string, sizeof(string),"Verslo pickup modelis s�kmingai pakeistas � %d. Jau tur�tum�te j� matyti", model);
            SendClientMessage(playerid, COLOR_NEWS, string);
            return 1;
        }
        case DIALOG_BMENU_SELECT_TYPE:
        {
            if(!response)
                return BusinessManagementDialog.ShowMain(playerid);

            new E_BUSINESS_TYPES:type, string[60];
            for(new E_BUSINESS_TYPES:i; i < E_BUSINESS_TYPES; i++)
                if(!strcmp(GetBusinessTypeName(i), inputtext))
                {
                    type = i;
                    break;
                }

            if(type == None)
            {
                RemoveBusinessWares(PlayerUsedBusinessIndex[ playerid ]);
                SendClientMessage(playerid, COLOR_NEWS, "Verslo tipas pakeistas � tu��i�. Pa�alintos buvusios parduodamos prek�s.");
            }
            else 
            {
                format(string, sizeof(string), "Verslo tipas pakeistas � %s", GetBusinessTypeName(type));
                SendClientMessage(playerid, COLOR_NEWS, string);
            }
            UpdateBusinessType(PlayerUsedBusinessIndex[ playerid ], type);
            return 1;
        }
        case DIALOG_BMENU_CONFIRM_WIPE:
        {
            if(!response)
                return BusinessManagementDialog.ShowMain(playerid);

            new string[128];
            new Cache:result = mysql_query(DbHandle, "SELECT id, owner, price FROM business WHERE IS NOT NULL owner");
            for(new i = 0; i < cache_get_row_count(); i++)
            {
                mysql_format(DbHandle, string, sizeof(string), "INSERT INTO `komp`(kam, ka, kiek) VALUES(%d, %d, %d)",
                    cache_get_field_content_int(i, "owner"), cache_get_field_content_int(i, "id"), cache_get_field_content_int(i, "price"));
                mysql_pquery(DbHandle, string);
            }
            cache_delete(result);

            // I� kit� table tur�t� duomenys patys dingti.
            mysql_pquery(DbHandle, "DELETE FROM business");

            static EmptyBusiness[ E_BUSINESS_DATA ], EmptyFurniture[ E_BUSINESS_FURNITURE_DATA ], EmptyWares[ E_BUSINESS_WARES_DATA ];
            foreach(new i : Business)
            {
                DestroyDynamicPickup(bInfo[ i ][ bPickup ]);
                DestroyDynamic3DTextLabel(bInfo[ i ][ bLabel ]);
                bInfo[ i ] = EmptyBusiness;

                for(new j = 0; j < MAX_BUSINESS_WARES; j++)
                    BusinessWares[ i ][ j ] = EmptyWares;
                for(new j = 0; j < MAX_BUSINESS_FURNITURE; j++)
                {
                    DestroyDynamicObject(BusinessFurniture[ i ][ j ][ ObjectId ]);
                    BusinessFurniture[ i ][ j ] = EmptyFurniture;
                }

                new next;
                Iter_SafeRemove(Business, i, next);
                i = next;
            }
            
            Streamer_Update(playerid);
            SendClientMessage(playerid, COLOR_NEWS, "Visi verslai pa�alinti s�kmingai.");
            return 1;
        }
    }
    return 0;
}