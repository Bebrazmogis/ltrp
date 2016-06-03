/*
    ---------------------------------------------------------------------------
     Lithuanian Role Play - 2015 m.
    ---------------------------------------------------------------------------
    
    Gamemode sukurtas 2009m.
    Gamemode kurëjas: Gedaas (Warrior).
    Legalus gamemode savininkas: Nova.
    
    ---------------------------------------------------------------------------
     
    This script is copyrighted to Gedas, Nova.
    It may be edited, hosted, renamed, redistributed, etc. as long as credit is given to him.
     
    ---------------------------------------------------------------------------
    
    Skripto autorinës teisës priklauso kurëjui Gedas, Nova.
    Skriptas gali bûti redaguojamas, naudojamas ar kitaip keièiamas jei tik nebus paþeidþiamos ðios teisës.


*/



#define VERSION                         2.2.0
#define BUILD_DATE                      2015-05.13

#define MYSQL_USE_YINLINE

#include <a_samp>
native IsValidVehicle(vehicleid);
native WP_Hash(buffer[], len, const str[]);
native CallShoebillFunction(name[], {Float,_}:...);
//#include <mSelection>
//#define  TIMER_FIX_TIMER_SLOTS          512
#include <foreach>
#include <a_mysql>
#include <sscanf2> 
#include <streamer>
#include <zcmd>
#include <mapandreas>
#include <Sheobill>
#include <crashdetect>
#include <filemanager>


#include <YSI\y_dialog>
#include <YSI\y_malloc>
#include <YSI\y_hooks>
#include <YSI\y_timers>
#include <YSI\y_va>



/*#if !defined abs 
    #define abs(%0) ((%0 > 0)?(%0):(-%0))
#endif
*/

 abs(value)
    return (value > 0) ? (value) : (-value);

forward Float:GetPlayerMaxHealth(playerid);
forward OnPlayerLoginEx(playerid, sqlid);


#pragma dynamic 108920
#define DEBUG

#define BENZO_KAINA 5
#define AC_MAX_CHECKS           (2)         // Kiek kartø patikrinama, prieð imantis veiksmø
#define AC_MAX_SPEED            (230)       // Didþiausias veikëjo / maðinos judëjimo greitis
#define AC_SPEED(%0,%1,%2,%3,%4) floatround(floatsqroot(%4?(%0*%0+%1*%1+%2*%2):(%0*%0+%1*%1))*%3*1.6)


#undef  MAX_PLAYERS
#define MAX_PLAYERS 256


#include "ErrorLog"
#include "Config/mysql"


#include "Tabula/liftas.pwn"

#undef  MAX_VEHICLES
#define MAX_VEHICLES 500

#undef  INVALID_TEXT_DRAW
#define INVALID_TEXT_DRAW   Text:0xFFFF

#undef  INVALID_MENU
#define INVALID_MENU        Menu:0xFF

#undef INVALID_3DTEXT_ID
#define INVALID_3DTEXT_ID   Text3D:0xFFFF

#define INTERIORMENU 4337
#define INTERIORMENU2 5337

#define PlayerToPoint(%1,%2,%3,%4,%5) IsPlayerInRangeOfPoint(%2,%1,%3,%4,%5)
#define FactionMySQLID(%1) fInfo[ %1 ][ fID ]
#define FUNKCIJA:%1(%2)     \
            forward %1(%2); \
            public %1(%2)
// Spalvos
#define COLOR_GREY 0xC8C8C8C8
#define COLOR_TEAL 0x67AAB1FF
#define COLOR_YELLOW 0xFFFF00AA
#define COLOR_LIGHTRED 0xFF6347AA
#define COLOR_NEWS 0xFFA500FF
#define GRAD 0xDCDCDCDD
#define COLOR_GRAD                      GRAD
#define GRAD2 0xDCDCDC99
#define COLOR_PURPLE 0xC2A2DAAA
#define COLOR_FADE1 0xE6E6E6E6
#define COLOR_FADE2 0xC8C8C8C8
#define COLOR_FADE3 0xAAAAAAAA
#define COLOR_FADE4 0x8C8C8C8C
#define COLOR_FADE5 0x6E6E6E6E
#define COLOR_RED 0xFE2E2EFF
#define COLOR_GREEN 0x16961AFF
#define COLOR_AD 0x00D900FF
#define COLOR_GREEN2 0x00D900CC
#define COLOR_WHITE 0xFFFFFFFF
#define COLOR_BLUE 0x2641FEAA
#define COLOR_ADM 0xE0C183FF
#define COLOR_OOC 0xB1C8FBAA
#define TEAM_HIT_COLOR 0xFFFFFF00
#define COLOR_LIGHTRED2 0xF5DEB3AA
#define COLOR_BLUE2 0x01FCFFC8
#define COLOR_GREEN_NEW 0x00FF6611
#define TEAM_TESTER_COLOR 0xFF003300
#define TEAM_ADMIN_COLOR 0x33CC0000
#define COLOR_CYAN 0x00FFFFFF
#define COLOR_POLICE 0x35A5CAFF
#define COLOR_FCHAT 0x31B5AEFF
#define COLOR_POLICEM 0x4261CCFF
#define COLOR_MODERATOR 0xA4DE63FF

// MAX Nustatymai
#define MAX_ROADBLOCKS 50
#define MAX_HOUSES 300
#define MAX_BIZNES 200
#define MAX_GARAGES                     100
#define MAX_FACTION_RANKS  14
#define MAX_FIRE  10
#define MAX_FISH_IN_BAG                 350
#define MAX_BUSINESS_PRODUCTS           2000
#define MAX_TRUCKER_CARGO_NAME          32

#define MAX_DROPPED_WEAPONS            50
#define MAX_FISHING_SPOTS               20

// Yra tik 13 slotø GTA SA. Todël uþsidëti daugiau ginklø þaidëjas negali.
#define MAX_PLAYER_ATTACHED_WEAPONS     13



// Procentas ant kiek laivas pirks prekes pigiau nei industriju vidurkis. ARBA kiek daugiau jei prekes niekas neperka.
#define SHIP_BUY_PRICE_MARGIN           10 

/// Visokiu dalyku positions 
#define FISH_SHOP_LAND_POS_X                 2414.6655
#define FISH_SHOP_LAND_POS_Y                 -1426.3036
#define FISH_SHOP_LAND_POS_Z                 23.9858

#define FISH_SHOP_WATER_POS_X                 2926.8704
#define FISH_SHOP_WATER_POS_Y                 -2040.7826
#define FISH_SHOP_WATER_POS_Z                 -0.2065


#define JOB_TRUCKER   6

// Daugiausiai ginklø turimø rankose
#define MAX_SAVED_WEAPONS 2
#define MAX_SAVED_WARES 10

// Checkpointai
#define CHECKPOINT_NONE  0
#define CHECKPOINT_CAR   1
#define CHECKPOINT_LIC   2
#define CHECKPOINT_TLC   3
#define CHECKPOINT_FISH  4
#define CHECKPOINT_BACKUP 5
#define CHECKPOINT_TRASH                6
#define CHECKPOINT_TRASH_DROPOFF        7
#define CHECKPOINT_SHIP					8

// Kiti nustatymai
#define MAX_WEED_SEEDS  200 // Kiek daugiausiai þolës seeds gali laikyti þmogus
#define MAX_TRUNK_SLOTS 12 // Maðinos bagaþinës vietos

// Namo inventoriaus vietø kiekis

// Dëmesio. Pakeitus ðá skaièiø reikia keisti ir LoadHouseInv
#define MAX_HOUSETRUNK_SLOTS 30 
#define MAX_GARAGETRUNK_SLOTS 10 // Garaþo inventoriaus vietø kiekis

// Meniu ID
#define MENU_INV        1
#define MENU_INV2       2

// Inventoriaus nustatymai
#define INVENTORY_SLOTS 8 // Inventoriaus vietos
#define iID             0
#define iAmmount        1


// Modinimo id'ai
#define MOD_URANUS1  1
#define MOD_URANUS2  2
#define MOD_JESTER   3
#define MOD_JESTER2  4
#define MOD_SULTAN   5
#define MOD_SULTAN2  6
#define MOD_STRATUM  7
#define MOD_STRATUM2 8
#define MOD_ELEGY    9
#define MOD_ELEGY2   10
#define MOD_FLASH    11
#define MOD_FLASH2   12
#define MOD_BLADE1   13
#define MOD_BLADE2   14
#define MOD_BROADWAY1 15
#define MOD_BROADWAY2 16
#define MOD_REMINGTON1 17
#define MOD_REMINGTON2 18
#define MOD_SAVANA1    19
#define MOD_SAVANA2    20
#define MOD_SLAMVAN1   21
#define MOD_SLAMVAN2   22
#define MOD_TORNADO1   23
#define MOD_TORNADO2   24

// GUI lenteliu ID.
#define DIALOG_SENTER_INPUT_ID          5463
#define DIALOG_SENTER_CHANGE_MODEL      5464
#define DIALOG_HOUSE_INPUT_NEW_MODEL    5465
#define DIALOG_HOUSE_INPUT_ID           5466
#define DIALOG_BUSINES_INPUT_NEW_MODEL  5467
#define DIALOG_BUSINESS_INPUT_ID        5468
#define DIALOG_TPDA_INDUSTRY            5500
#define DIALOG_TPDA_BUSINESS            5501
#define DIALOG_VEHICLE_CARGO_LIST       5502
#define DIALOG_INDUSTRY_INFO            5503
#define DIALOG_SOLD_COMMODITY_LIST      5504
#define DIALOG_COMMODITY_SELL			5505
#define DIALOG_TPDA_MAIN				5506
#define DIALOG_SHIP_INFO				5507 
#define DIALOG_VEHICLE_SHOPS_LIST       63
#define DIALOG_VEHICLE_SHOPS_DELETE_CON 5508
#define DIALOG_VEHICLE_SHOPS_NEW_NAME   5509
#define DIALOG_VEHICLE_SHOPS_MENU       5510    
#define DIALOG_VEHICLE_SHOPS_VEH_LIST   5511
#define DIALOG_VEHICLE_SHOPS_VEH_MAIN   5512   
#define DIALOG_VEHICLE_SHOPS_VEH_PRICE  5513
#define DIALOG_VEHICLE_SHOPS_VEH_NEW    5514
#define DIALOG_VEHICLE_SHOP_NEW         5515
#define DIALOG_VEHICLE_SHOP             5516
#define DIALOG_SECRET_QUESTION          5517
#define DIALOG_SECRET_QUESTION_SET      5518
#define DIALOG_SECRET_ANSWER_SET        5519
#define DIALOG_VEHICLE_SCRAP_CONFIRM    5520


#define DEFAULT_HOUSE_PICKUP_MODEL      1273
#define DEFAULT_BIZ_PICKUP_MODEL        1239


// Mygtukø nustatymai
#define HOLDING(%0) \
    ((newkeys & (%0)) == (%0))
#define PRESSED(%0) \
    (((newkeys & (%0)) == (%0)) && ((oldkeys & (%0)) != (%0)))
#define RELEASED(%0) \
    (((newkeys & (%0)) != (%0)) && ((oldkeys & (%0)) == (%0)))


// UnixTimestamp kada buvo ájungtas serveris.
new ServerStartTimestamp;

enum radioInfo
{
    rName [ 28 ],
    rType [ 20 ],
    rUrl  [ 62 ]
};

#define STATIONS 18

new GunObjects[47] = {
    0,331,333,334,335,336,337,338,339,341,321,322,323,324,325,326,342,343,344,
    0,0,0,346,347,348,349,350,351,352,353,355,356,372,357,358,359,360,361,362,
    363,364,365,366,367,368,368,371
};

new PlayerFading = true;


// Trucker system
#define MAX_INDUSTRIES                  40
#define MAX_TRUCKER_CARGO               40
#define MAX_TRUCKER_CARGO_OBJECTS       40 // kiek daugiausiai objektu bus
#define MAX_COMMODITIES                 MAX_INDUSTRIES*MAX_TRUCKER_CARGO
#define MAX_BOXES                       60
#define MAX_INDUSTRY_NAME               64

// LS dokai. 2808.9011,-2438.3188,13.6285
#define SHIP_POS_X						2808.9011
#define SHIP_POS_Y						-2438.3188
#define SHIP_POS_Z						13.6285


enum E_CARGO_BOX_DATA
{
    CargoId,
    ObjectId,
    Float:PosX,
    Float:PosY,
    Float:PosZ,
    DissapearTimer,
    bool:CanBePickedUp // Visikai useless but nesvarbu...
};
new CargoBox[ MAX_BOXES ][ E_CARGO_BOX_DATA ];


enum E_INDUSTRIES_DATA {
    Id,
    Name[ MAX_INDUSTRY_NAME ],
    Float:PosX,
    Float:PosY,
    Float:PosZ,
    Text3D:Label,
	Pickup,
    bool:IsBuyingCargo
};

new Industries[ MAX_INDUSTRIES ][ E_INDUSTRIES_DATA ];


enum E_CARGO_DATA {
    Id,
    Name[ MAX_TRUCKER_CARGO_NAME ],
    Limit,
    Production,
    Consumption,
    Slot,
    Type
};

new TruckerCargo[ MAX_TRUCKER_CARGO ][ E_CARGO_DATA ];


enum E_COMMODITY_SELL_BUY_STATUS {
    Buying,
    Selling
};

enum E_COMMODITY_DATA {
    IndustryId,
    CargoId,
    E_COMMODITY_SELL_BUY_STATUS:SellBuyStatus,
    CurrentStock,
    bool:IsBusinessCommodity,
    Price
};

new Commodities[ MAX_COMMODITIES ][ E_COMMODITY_DATA ];

enum E_VEHICLE_CARGO {
    CargoId,
    Amount
};

enum E_SHIP_STATUS {
	Docked, // Stovi uoste, priema krovinius
	Moving, // Iðplaukia
    Arriving // Atplaukia. Rodomi judantys objektai.
};

#define CARGOSHIP_DOCKED_INTERVAL       40*60*1000
#define CARGOSHIP_MOVING_INTERVAL       5*60*1000


enum E_SHIP_DATA {
	E_SHIP_STATUS:Status,
	LastDepartureTimestamp,
    LastArrivalTimestamp,
	CurrentStock, // Skaièiuojamas cargo slotais, ne vienetais. 
    ObjectIDs[ 11 ] // Hard-coded nes tai yra CreateDynamicObject skaièius.
};

new ShipInfo[ E_SHIP_DATA ];

enum E_CARGOSHIP_OBJECT_DATA {
    ObjectModel,
    Float:PosX,
    Float:PosY,
    Float:PosZ,
    Float:RotX,
    Float:RotY,
    Float:RotZ  
};

new const CargoShipObjectPositions[ ][ E_CARGOSHIP_OBJECT_DATA ] = 
{
    {5160, 2829.95313, -2479.57031, 5.26560,   0.00000, 0.00000, 270.00000},
    {5156, 2838.03906, -2423.88281, 10.96090,   0.00000, 0.00000, 270.00000},
    {3724, 2838.19067, -2407.12109, 29.31250,   0.00000, 0.00000, 270.00000},
    {5167, 2838.03125, -2371.95313, 7.29690,   0.00000, 0.00000, 270.00000},
    {5166, 2829.95313, -2479.57031, 5.26560,   0.00000, 0.00000, 270.00000},
    {3724, 2838.21411, -2489.00000, 29.31250,   0.00000, 0.00000, 90.00000},
    {5155, 2838.02344, -2358.47656, 21.31250,   0.00000, 0.00000, 270.00000},
    {5158, 2837.77344, -2334.47656, 11.99220,   0.00000, 0.00000, 0.00000},
    {5154, 2838.14063, -2447.84375, 15.75000,   0.00000, 0.00000, 270.00000},
    {5157, 2838.03906, -2532.77344, 17.02340,   0.00000, 0.00000, 270.00000},
    {5165, 2838.03125, -2520.18750, 18.41406,   0.00000, 0.00000, 0.00000}
};

new VehicleCargo[ MAX_VEHICLES ][ MAX_TRUCKER_CARGO ][ E_VEHICLE_CARGO ],
    IsVehicleLoaded[ MAX_VEHICLES ],
    VehicleLoadTimer[ MAX_PLAYERS ] = {-1, ...},
	VehicleLoadTime[ MAX_PLAYERS ];

new Iterator:IndustryIterator<MAX_INDUSTRIES>;
new Iterator:TruckerCargoIterator<MAX_TRUCKER_CARGO>;
new Iterator:CommodityIterator<MAX_COMMODITIES>;


#include "IndustryManagementGUI"

// Sekundëmis
#define DROPPED_WEAPON_DESTROY_DELAY            10*60

enum E_DROPPED_WEAPON_DATA {
    ObjectId, 
    DissapearTimer,
    WeaponId,
    Ammo,
    bool:CanBePickedUp
};

new DroppedWeapons[ MAX_DROPPED_WEAPONS ][ E_DROPPED_WEAPON_DATA ];



#define MAX_AD_TEXT                     170

    // Kintamieji
new 
    bool:OOCDisabled = false,
    Checkpoint[MAX_PLAYERS],
    bool:FirstSpawn[ MAX_PLAYERS ] = {true, ... },
    Offer[MAX_PLAYERS][9],
    OfferPrice[MAX_PLAYERS][8],
    OfferID[MAX_PLAYERS][8],
    bool:PlayersBlocked[ MAX_PLAYERS ][ MAX_PLAYERS ],
    bool:Engine[MAX_VEHICLES] = { false, ... },
    bool:StartingEngine[MAX_PLAYERS] = { false, ... },
    PlayerText:InfoText[MAX_PLAYERS],
    PlayerText:Greitis[MAX_PLAYERS],
    Text:BlindfoldTextdraw,
    bool:IsBlindfolded[ MAX_PLAYERS ],
    Text3D:AdminON[MAX_PLAYERS],
    bool:AdminDuty[MAX_PLAYERS] = { false, ... },
    TalkingLive[MAX_PLAYERS],
    Ruko[MAX_PLAYERS],
    Laikas[MAX_PLAYERS],
    LaikoTipas[MAX_PLAYERS],
    bool:Freezed[MAX_PLAYERS] = { false, ... },
    MobilePhone[MAX_PLAYERS],
    RingTone[MAX_PLAYERS],
    bool:Boxing[MAX_PLAYERS],
    BoxStart,
    Mires[MAX_PLAYERS],
    OldHour,
    bool:Mute[MAX_PLAYERS] = { false, ... },
    Mats = 300,
    bool:gPlayerUsingLoopingAnim[MAX_PLAYERS],
    bool:IsOnePlayAnim[MAX_PLAYERS],
    BackOut[MAX_PLAYERS],
    Unfreeze[MAX_PLAYERS],
    OldCar[MAX_PLAYERS],
    tmpinteger[MAX_PLAYERS],
    bool:Voted[MAX_PLAYERS] = { true, ... },
    Votes[ 2 ],
    bool:VGaraze[ MAX_VEHICLES ] = { false, ... },
    Camera      [ MAX_PLAYERS  ] = { -1, ... },
    PlayerSpeed [ MAX_PLAYERS  ],
    Pickups[ 5 ],
    szMessage[256],
    timeris                 [ MAX_PLAYERS  ],
    Meter                   [ MAX_VEHICLES ],
    Kils                    [ MAX_VEHICLES ],
    bool:UsingLoopAnim[ MAX_PLAYERS ],
    bool:AnimsPrelo   [ MAX_PLAYERS ],
        Float:V_HP[ MAX_VEHICLES ],
    vehview[ MAX_PLAYERS ],
    skinlist,
    Text3D:DeathLabel[MAX_PLAYERS],
    LastVehicleDriverSqlId[ MAX_VEHICLES ],
    bool:IsPlayerDataRecorded[ MAX_PLAYERS ],
    Text3D:SpecCommandLabel[ MAX_PLAYERS ] = {INVALID_3DTEXT_ID, ... },
    SpecCommandTimer[ MAX_PLAYERS ] = {-1, ... },
    PlayerSpectatedPlayer[ MAX_PLAYERS ] = {INVALID_PLAYER_ID, ... }, 
    DrugTimer[MAX_PLAYERS],
    Text3D:Units[ MAX_VEHICLES ],
    bool:IsFillingFuel[ MAX_PLAYERS ],
    PlayerFillUpTimer[ MAX_PLAYERS ],
    InfoTextTimer[ MAX_PLAYERS ] = {-1, ...}
    ;
    //PVarning                [  MAX_PLAYERS ];//Login
    
new Iterator:Audio3D<MAX_PLAYERS>;

new Iterator:Vehicles<MAX_VEHICLES>;




enum DAHX
{
    Float:aKords[ 3 ],
    aStation[ 128 ],
    aObjekt,
    aArea
};

new aInfo[ MAX_PLAYERS ][ DAHX ];

enum E_PLAYER_SPAWN_LOCATIONS 
{
	DefaultSpawn, 
	SpawnFaction,
	SpawnHouse,
	SpawnBusiness,
	SpawnGarage,
	SpawnLosSantos,
};

enum players
{
    pExp,
    pMask,
    pWarn,
    pJailTime,
    pJail,
    pLead,
    pMember,
    pRank,
    pCar[22],
    pCarGet,
    pHouseKey,
    pPhone,
    pRChannel,
    pRSlot,
    Float:pCrashPos[3],
    pInt,
    pVirWorld,
    pCrash,
    pJobDuty,
    pCuffs,
    pAge,
    pBackup,
    pWantedLevel,
    pRoadBlock,
    pLiga,
    pJob,
    pJobContr,
    pPayCheck,
    pPayDayHad,
    pGun[MAX_SAVED_WEAPONS],
    pAmmo[MAX_SAVED_WEAPONS],
    pOrigin[MAX_PLAYER_NAME],
    pSex[15],
    pSavings,
    pMySQLID,
    pUcpID,
    pDubKey,
    pJobSkill,
    pJobLevel,
    pLeftTime,
    pDriverWarn,
    pTester,
    pFines,
    pPaydFines,
    pDonator,
    pWalkStyle,
    pTalkStyle,
    pHeroineAddict,
    pAmfaAddict,
    pCocaineAddict,
    pMetaAmfaineAddict,
    pExtazyAddict,
    pPCPAddict,
    pCrackAddict,
    pOpiumAddict,
    pConnectionIP[ 18 ],
    E_PLAYER_SPAWN_LOCATIONS:pSpawn,
    pBSpawn,
    pCard[ 256 ],
    pForumName[ 256 ],
    pPoints,
    pHealthLevel,
    pStrengthLevel,
	pJobHours,
    pHunger,
    pTotalPaycheck,
    bool:pFactionManager,
};
new pInfo[MAX_PLAYERS][players];


new bool:ShowACTestMsg[ MAX_PLAYERS ];

 SetPlayerHealthBonus(playerid, Float:health)
{
    if(health >= 100.0)
        health += float(pInfo[ playerid ][ pHealthLevel ]) * 3;
    return SetPlayerHealth(playerid, health);
}
#if defined _ALS_SetPlayerHealth
    #undef SetPlayerHealth
#else 
    #define _ALS_SetPlayerHealth
#endif
#define SetPlayerHealth SetPlayerHealthBonus

enum cars
{
    cName[20],
    cOwner,
    cModel,
    Float:cSpawn[4],
    cColor[2],
    cLock,
    cFuel,
    cNumbers[24],
    cFaction,
    cWheels,
    cTuning,
    cDuzimai,
    cID,
    cLockType,
    cAlarm,
    cInsurance,
    cTrunkWeapon[MAX_TRUNK_SLOTS],
    cTrunkAmmo[MAX_TRUNK_SLOTS],
    cTrunkItemContent[ MAX_TRUNK_SLOTS ],
    cTrunkItemDurability[ MAX_TRUNK_SLOTS ],
    cTicket,
    cHidraulik,
    cCrimes,
    cVehID,
    cDamage[40],
    Float:cKM,
    cDub,
    cVirtWorld,
    objectai[ MAX_TRUCKER_CARGO_OBJECTS ]
};
new cInfo[MAX_VEHICLES][cars];




trucker_DestroyVehicle(vehicleid)
{
    if(vehicleid > 0 && vehicleid <= MAX_VEHICLES)
        for(new j = 0; j < MAX_TRUCKER_CARGO_OBJECTS; j++)
        {
            if(cInfo[ vehicleid ][ objectai ][ j ] != -1)
                DestroyObject(cInfo[ vehicleid ][ objectai ][ j ]);
            cInfo[ vehicleid ][ objectai ][ j ] = -1;
        }
    return DestroyVehicle(vehicleid);
}
#if defined _ALS_DestroyVehicle
    #undef DestroyVehicle 
#else
    #define _ALS_DestroyVehicle
#endif
#define DestroyVehicle trucker_DestroyVehicle


enum E_STATIC_VEHICLE_DATA 
{
    Id,
    Model,
    Float:SpawnX,
    Float:SpawnY,
    Float:SpawnZ,
    Float:SpawnA,
    Color1,
    Color2,
    Faction,
    Rang,
    Job
};
new sVehicles[ MAX_VEHICLES ][ E_STATIC_VEHICLE_DATA ];


#include "BugReporting"

new RoadBlocks[MAX_ROADBLOCKS];
new RID[MAX_ROADBLOCKS];


enum Drg
{
   dOwner,
   dItemID,
   dLaikas,
   bool:dMade
}
new DrugMake[MAX_WEED_SEEDS][Drg];
new vartai[ 16 ][ 2 ];

new const  // CCTV koordinatës
Float:CCTV[ ][ 4 ] = {
    { 1525.9738,-1688.2424,21.6822, 11.0 }, //
    { 1484.9551,-1728.3623,21.6822, 11.0 }, //
    { 1747.0881,-1847.5023,21.6822, 11.0 }, //
    { 1893.2758,-2042.8265,22.6208, 11.0 }, //
    { 2442.9839,-1660.9250,24.6212, 13.0 }, //
    { 1808.0552,-1672.2999,24.6212, 13.0 }, //
    { 1691.9740,-1490.6073,18.2193, 9.0 }, //
    { 1779.1565,-1279.0680,23.0045, 12.0 }, //
    { 1902.7317,-1145.2634,33.4001, 17.0 }, //
    { 2209.5779,-1137.7407,33.4001, 17.0 }, //
    { 2152.7297,-1014.2031,75.4673, 40.0 }, //
    { 2405.9502,-1245.6781,29.7572, 17.0 }, //
    { 2465.5908,-1514.7914,29.7572, 17.0 }, //
    { 2461.4946,-1356.7466,37.3127, 18.0 }, //
    { 2156.0454,-1790.7332,21.2839, 11.0 }, //
    { 2086.9709,-1824.0341,19.4909, 10.5 } //
};

enum businfo
{
    objectas,
    Float:X4,
    Float:Y4,
    Float:Z4,
};

new RandBus[12][businfo] = {
{0, -178.7006,1044.9867,19.7422}, // ðiûkðlë1
{0, -167.4145,1086.3560,19.7422}, // ðiûkðlë2
{0, -135.5973,1086.2920,19.7422}, // ðiûkðlë3
{0, -105.6469,1113.8851,19.7422}, // ðiûkðlë4
{0, -88.2529,1128.5619,19.7422}, // ðiûkðlë5
{0, -87.8232,1163.3447,19.7422}, // ðiûkðlë6
{0, -170.2490,1214.6812,19.7422}, // ðiûkðlë7
{0, -854.4310,1539.2550,22.5638}, // ðiûkðlë8
{0, -890.0706,1544.4739,25.9505}, // ðiûkðlë9
{0, -784.9171,1621.5551,27.1172}, // ðiûkðlë10
{0, -336.4170,1162.9825,19.7301}, // ðiûkðlë11
{0, 76.6545,1211.3668,18.8376} // ðiûkðlëend
};



enum jobs
{
    Float:Job_x,
    Float:Job_y,
    Float:Job_z,
    PayCheck,
    Contr,
    MaxPayday,
    Name[42]
}

//Nefrakciniai darbai ir nustatymai

#define MAX_JOBS 8

new const pJobs[ MAX_JOBS ][ jobs ] = { // Darbø (ásidarbinimo) koordinatës, alga, kontraktas, maksimali alga
    { 99999.0, 99999.0, 8888.0, 50, 0, 2400,         "Bedarbis" },
    { 1657.9742,-1817.6954,13.6508, 600, 6, 900,         "Mechanikas" },
    { 99999.0, 99999.0, 8888.0, 0, 5, 750,        "Gatviø valytojas" },
//    { 758.1678, -77.3299, 1000.6499, 150, 5, 500,     "Kovos menø treneris" },
    { 2195.7881,-1973.2227,13.5589, 400, 5, 2200,         "Ðiûkðlininkas" },
    { 99999.0, 99999.0, 8888.0, 350, 5, 150,  "Gatviø ðlavëjas." },
    { 99999.0, 99999.0, 8888.0, 0, 5, 150,  "Mechanikas." },
    { 2281.1189,-2365.0647,13.5469, 400, 5, 900,  "Kroviniø perveþimø vairuotojas" },
    { 99999.0, 99999.0, 8888.0, 400, 10, 2200,      "Automobiliu vagis (nelegalus)" }
};

new Ligos[7][25] = { // Ligos
{"Neturi"},
{"Bronhitu"},
{"Plauøiu uþdegimu"},
{"Gripu"},
{"Raupais"},
{"Apendicitu"},
{"Sloga"}
};



#define MAX_VEHICLE_SHOP_NAME           64
#define MAX_VEHICLE_SHOP_VEHICLES       50
#define MAX_VEHICLE_SHOPS               8

enum E_VEHICLE_SHOP_DATA {
    Id, 
    Name[ MAX_VEHICLE_SHOP_NAME ],
    Float:PosX,
    Float:PosY,
    Float:PosZ,
    Text3D:Label,
    VehicleModels[ MAX_VEHICLE_SHOP_VEHICLES ],
    VehiclePrices[ MAX_VEHICLE_SHOP_VEHICLES ]
};

new VehicleShops[ MAX_VEHICLE_SHOPS ][ E_VEHICLE_SHOP_DATA ];
new Iterator:VehicleShopIterator<MAX_VEHICLE_SHOPS>;


/*Uþkraunamas tr. priemoniø turgus sellcars, sellbikes, sellsport pabaiga*/


/*  Nefrakcinio darbo ðiûkðlininkai nustatymai */

#define TRASH_MISSION_NONE 0
#define TRASH_MISSION_MONTGOMERY 1
#define TRASH_MISSION_DILIMORE 2
#define TRASH_MISSION_POLOMINO_CREEK 3
#define TRASH_MISSION_JEFFERSON 4
#define TRASH_MISSION_IDLEWOOD 5

#define MAX_GARBAGE_CANS                200
#define TRASH_MISSION_COMPLETED_BONUS   380
#define TRASH_OBJECT_INDEX              4

/*  Nefrakcinio darbo ðiûkðlininkai nustatymai pabaiga*/


enum E_GARBAGE_CANS {
    gModel,
    gMission,
    gObjectId
};




 IsPlayerAddicted( playerid )
{
    if( pInfo[ playerid ][ pHeroineAddict ] > 3 )
        return true;
    if( pInfo[ playerid ][ pAmfaAddict ] > 3 )
        return true;
    if( pInfo[ playerid ][ pCocaineAddict ] > 3 )
        return true;
    if( pInfo[ playerid ][ pMetaAmfaineAddict ] > 3 )
        return true;
    if( pInfo[ playerid ][ pExtazyAddict ] > 5 )
        return true;
    if( pInfo[ playerid ][ pPCPAddict ] > 3 )
        return true;
    if( pInfo[ playerid ][ pCrackAddict ] > 4 )
        return true;
    if( pInfo[ playerid ][ pOpiumAddict ] > 2 )
        return true;
    return false;
}

 GetVehiclePrice( model )
{
    new count, sum;
    foreach(VehicleShopIterator, i)
    {
        for(new  j = 0; j < MAX_VEHICLE_SHOP_VEHICLES; j++)
            if(VehicleShops[ i ][ VehicleModels ][ j ] == model)
            {
                sum += VehicleShops[ i ][ VehiclePrices ][ j ];
                count++;
            }
    }
    if(count && sum)
        return sum / count;
   
    if( model == 473 )
        return 5000;
    if( model == 453 )
        return 12000;
    if( model == 454 )
        return 24000;
    if( model == 493 )
        return 22000;
    if( model == 484 )
        return 27000;
    return 0;
}

 Check_VHP( vehicleid, mode = 0, Float:HP = 1000.0 )
{
    if ( mode == 0 )
    {
        if ( HP < V_HP[ vehicleid ] )
            V_HP[ vehicleid ] = HP;
        return 1;
    }
    else if ( mode == 1 )
    {
        new Float:TMPVHP;
        GetVehicleHealth( vehicleid, TMPVHP );
        if ( V_HP[ vehicleid ] < TMPVHP )
            return SetVehicleHealth( vehicleid, V_HP[ vehicleid ] );
    }
    return 1;
}

 PDJOBPlace( playerid )
{
    if( IsPlayerInRangeOfPoint(playerid, 40.0, -983.2907,-2424.8074,2233.5059 ) || IsPlayerInRangeOfPoint(playerid, 40.0, 65.7139,1966.2054,431.8035 ) || IsPlayerInRangeOfPoint(playerid, 40.0, 245.9104,114.2515,1003.2188 ) || IsPlayerInRangeOfPoint(playerid, 80.0, -23.8720,2066.4060,2130.0222 ) || IsPlayerInRangeOfPoint(playerid, 80.0, 1803.4606,-1520.4922,5700.4302 ))
        return true;
    return false;
}


FUNKCIJA:NullWeapons( playerid )
{
    new string[ 32 ];
    for(new i = 0; i < 47; i++)
    {
        format(string, 32, "%d", i);
        if( GetPVarInt( playerid, string ) == 0 ) continue;

        SetPVarInt( playerid, string, 0 );
    }
}

 StopLoopingAnim( playerid, bool:  setvar = true )
{
    // Funkcija: StopLoopingAnim( playerid )

    if ( setvar )
        UsingLoopAnim[ playerid ] = false;

    ApplyAnimation( playerid, "CARRY", "crry_prtial", 4.0, 0, 0, 0, 0, 0 );
}

 NullAnimVariables( playerid )
{
    // Funkcija: NullAnimVariables( playerid );

    UsingLoopAnim[ playerid ] = false;
    AnimsPrelo   [ playerid ] = false;
}

 RemoveRoadBlock( id )
{
    // Funkcija: RemoveRoadBlock( id )

    if ( -1 < id < MAX_ROADBLOCKS )
    {
        if ( RoadBlocks[ id ] > 0 )
        {
            DestroyDynamicObject( RoadBlocks[ id ] );
            RoadBlocks[ id ] = 0;
            RID[ id ] = 0;
        }
    }
}

 NullRoadBlocks( )
{
    // Funkcija: NullRoadBlocks( )

    for ( new i = 0; i < MAX_ROADBLOCKS; i++ )
    {
        RoadBlocks[ i ] = 0;
        RID[ i ] = 0;
    }
}

 GetFreeBoxSlot( )
{
    // Funkcija: GetFreeBoxSlot( )
    for ( new i = 0; i < MAX_BOXES; i++ )
    {
        if(!IsValidDynamicObject(CargoBox[ i ][ ObjectId ]))
            return i;
    }

    return -1;
}

 GetFreeRoadBlockSlot( )
{
    // Funkcija: GetFreeRoadBlockSlot( )

    for ( new i = 0; i < MAX_ROADBLOCKS; i++ )
    {
        if ( RoadBlocks[ i ] == 0 )
            return i;
    }

    return MAX_ROADBLOCKS;
}


 GetAdminRank( playerid )
{
    // Funkcija: GetAdminRank( playerid )
    // Returnins veikëjo admin ranká 

    new
        admrank[ 32 ];

    switch ( GetPlayerAdminLevel(playerid) )
    {
        case 1,2: admrank = "Administratorius";
        case 3: admrank = "Vyr. Administratorius";
        case 4: admrank = "Pagr. Administratorius";
        case 5: admrank = "Projekto savininkas";
        case 6: admrank = "Skripto priþiûrëtojas";
        default: admrank = "";
    }

    return admrank;
}

 GetXYInFrontOfPlayer(playerid, &Float:x, &Float:y, Float:distance)
{
    // Created by Y_Less

    new Float:a;

    GetPlayerPos(playerid, x, y, a);
    GetPlayerFacingAngle(playerid, a);

    if (GetPlayerVehicleID(playerid)) {
        GetVehicleZAngle(GetPlayerVehicleID(playerid), a);
    }

    x += (distance * floatsin(-a, degrees));
    y += (distance * floatcos(-a, degrees));
}

 GetXYBehindPoint(Float:x,Float:y,&Float:x2,&Float:y2,Float:A,Float:distance)
{
    x2 = x - (distance * floatsin(-A,degrees));
    y2 = y - (distance * floatcos(-A,degrees));
}

 GetXYBehindVehicle(vehicleid, &Float:x2, &Float:y2, Float:dist)
{
    new Float:x,Float:y,Float:a;
    GetVehiclePos(vehicleid, x,y,a);
    GetVehicleZAngle(vehicleid, a);
    GetXYBehindPoint(x, y, x2, y2, a, dist);
}

 GetXYJudgedByAngle( Float:distance, Float:x, Float:y, Float:angle, &Float:rx, &Float:ry )
{
    // Funkcija: GetXYJudgedByAngle( distance, Float:x, Float:y Float:angle, &Float:rx, &Float:ry )

    rx = x + distance * floatsin( -angle, degrees );
    ry = y + distance * floatcos( -angle, degrees );
}

 doesVehicleExist(const vehicleid) {
    if(GetVehicleModel(vehicleid) >= 400) {
        return 1;
    }
    return 0;
}


new aVehicleNames[ 212 ][ ] = { // Credit: BetaMaster
    {"Landstalker"},
    {"Bravura"},
    {"Buffalo"},
    {"Linerunner"},
    {"Perrenial"},
    {"Sentinel"},
    {"Dumper"},
    {"Firetruck"},
    {"Trashmaster"},
    {"Stretch"},
    {"Manana"},
    {"Infernus"},
    {"Voodoo"},
    {"Pony"},
    {"Mule"},
    {"Cheetah"},
    {"Ambulance"},
    {"Leviathan"},
    {"Moonbeam"},
    {"Esperanto"},
    {"Taxi"},
    {"Washington"},
    {"Bobcat"},
    {"Mr Whoopee"},
    {"BF Injection"},
    {"Hunter"},
    {"Premier"},
    {"Enforcer"},
    {"Securicar"},
    {"Banshee"},
    {"Predator"},
    {"Bus"},
    {"Rhino"},
    {"Barracks"},
    {"Hotknife"},
    {"Ðaldoma priekaba"},
    {"Previon"},
    {"Coach"},
    {"Cabbie"},
    {"Stallion"},
    {"Rumpo"},
    {"RC Bandit"},
    {"Romero"},
    {"Packer"},
    {"Monster"},
    {"Admiral"},
    {"Squalo"},
    {"Seasparrow"},
    {"Pizzaboy"},
    {"Tram"},
    {"Atvira priekaba"},
    {"Turismo"},
    {"Speeder"},
    {"Reefer"},
    {"Tropic"},
    {"Flatbed"},
    {"Yankee"},
    {"Caddy"},
    {"Solair"},
    {"RC Van"},
    {"Skimmer"},
    {"PCJ-600"},
    {"Faggio"},
    {"Freeway"},
    {"RC Baron"},
    {"RC Raider"},
    {"Glendale"},
    {"Oceanic"},
    {"Sanchez"},
    {"Sparrow"},
    {"Patriot"},
    {"Quad"},
    {"Coastguard"},
    {"Dinghy"},
    {"Hermes"},
    {"Sabre"},
    {"Rustler"},
    {"ZR-350"},
    {"Walton"},
    {"Regina"},
    {"Comet"},
    {"BMX"},
    {"Burrito"},
    {"Camper"},
    {"Marquis"},
    {"Baggage"},
    {"Dozer"},
    {"Maverick"},
    {"News Chopper"},
    {"Rancher"},
    {"FBI Rancher"},
    {"Virgo"},
    {"Greenwood"},
    {"Jetmax"},
    {"Hotring"},
    {"Sandking"},
    {"Blista Compact"},
    {"Police Maverick"},
    {"Boxville"},
    {"Benson"},
    {"Mesa"},
    {"RC Goblin"},
    {"Hotring Racer A"},
    {"Hotring Racer B"},
    {"Bloodring Banger"},
    {"Rancher"},
    {"Super GT"},
    {"Elegant"},
    {"Journey"},
    {"Bike"},
    {"Mountain Bike"},
    {"Beagle"},
    {"Cropdust"},
    {"Stunt"},
    {"Tanker"},
    {"Roadtrain"},
    {"Nebula"},
    {"Majestic"},
    {"Buccaneer"},
    {"Shamal"},
    {"Hydra"},
    {"FCR-900"},
    {"NRG-500"},
    {"HPV1000"},
    {"Cement Truck"},
    {"Tow Truck"},
    {"Fortune"},
    {"Cadrona"},
    {"FBI Truck"},
    {"Willard"},
    {"Forklift"},
    {"Tractor"},
    {"Combine"},
    {"Feltzer"},
    {"Remington"},
    {"Slamvan"},
    {"Blade"},
    {"Freight"},
    {"Streak"},
    {"Vortex"},
    {"Vincent"},
    {"Bullet"},
    {"Clover"},
    {"Sadler"},
    {"Firetruck LA"},
    {"Hustler"},
    {"Intruder"},
    {"Primo"},
    {"Cargobob"},
    {"Tampa"},
    {"Sunrise"},
    {"Merit"},
    {"Utility"},
    {"Nevada"},
    {"Yosemite"},
    {"Windsor"},
    {"Monster A"},
    {"Monster B"},
    {"Uranus"},
    {"Jester"},
    {"Sultan"},
    {"Stratum"},
    {"Elegy"},
    {"Raindance"},
    {"RC Tiger"},
    {"Flash"},
    {"Tahoma"},
    {"Savanna"},
    {"Bandito"},
    {"Freight Flat"},
    {"Streak Carriage"},
    {"Kart"},
    {"Mower"},
    {"Duneride"},
    {"Sweeper"},
    {"Broadway"},
    {"Tornado"},
    {"AT-400"},
    {"DFT-30"},
    {"Huntley"},
    {"Stafford"},
    {"BF-400"},
    {"Newsvan"},
    {"Tug"},
    {"Cisterna"},
    {"Emperor"},
    {"Wayfarer"},
    {"Euros"},
    {"Hotdog"},
    {"Club"},
    {"Freight Carriage"},
    {"Ðaldoma priekaba 2"},
    {"Andromada"},
    {"Dodo"},
    {"RC Cam"},
    {"Launch"},
    {"Police Car"},
    {"Police Car"},
    {"Police Car"},
    {"Police Ranger"},
    {"Picador"},
    {"S.W.A.T. Van"},
    {"Alpha"},
    {"Phoenix"},
    {"Glendale Shit"},
    {"Sadler Shit"},
    {"Luggage Trailer A"},
    {"Luggage Trailer B"},
    {"Stair Trailer"},
    {"Boxville"},
    {"Farm Plow"},
    {"Utility Trailer"}
};
new vBakas[212],
    vValgo[212],
    vSlotai[212];

 SyncFuel( vehicleid )
{
    new model = GetVehicleModel( vehicleid ),
        talpa = GetVehicleFuelTank( model );
    if(Engine[vehicleid] == true)
        cInfo[vehicleid][cFuel] -= vValgo[ model - 400 ];
    if(cInfo[vehicleid][cFuel] > talpa)
        cInfo[vehicleid][cFuel] = talpa;
    if(cInfo[vehicleid][cFuel] < 0)
        cInfo[vehicleid][cFuel] = 0;
    if(cInfo[vehicleid][cFuel] <= 0)
    {
        new
            engine, lights, alarm, doors, bonnet, boot, objective;

        GetVehicleParamsEx ( vehicleid, engine, lights, alarm, doors, bonnet, boot, objective             );
        SetVehicleParamsEx ( vehicleid, VEHICLE_PARAMS_OFF, lights, alarm, doors, bonnet, boot, objective );
    }
    return true;
}

GetIndustryCargoStock(index, cargoid)
{
    // Graþina industrijai priklausantá nurodytos prekës kieká.
    foreach(CommodityIterator, i)
        if(Commodities[ i ][ IndustryId ] == Industries[ index ][ Id ]
            && Commodities[ i ][ CargoId ] == cargoid
            && !Commodities[ i ][ IsBusinessCommodity ])
            return Commodities[ i ][ CurrentStock ];
    return 0;
}

 ShowTrunk( playerid, veh )
{
    new string[ 1028 ];
    for( new slot = 0; slot < GetVehicleTrunkSlots( GetVehicleModel( veh ) ); slot ++)
    {
        if ( cInfo[ veh ][ cTrunkWeapon ][ slot ] == 0)
            format( string, sizeof(string), "%s%d. Nëra\n", string,slot+1);
        else if ( cInfo[ veh ][ cTrunkWeapon ][ slot ] > 0 )
        {
            format( string, sizeof(string), "%s%d. %s %d\n", string, slot+1, GetItemName(cInfo[ veh ][ cTrunkWeapon ][ slot ]) , cInfo[ veh ][ cTrunkAmmo ][ slot ] );
        }
    }
    format( string, sizeof(string), "%s\nIðjunkti", string);
    ShowPlayerDialog(playerid,13,DIALOG_STYLE_LIST,"Bagaþinë",string,"Paimti","Atgal");
    return 1;
}


 IsItemInTrunk( vehicle, item )
{
    for( new slot = 0; slot < GetVehicleTrunkSlots( GetVehicleModel( vehicle ) ); slot ++)
    {
        if ( cInfo[ vehicle ][ cTrunkWeapon ][ slot ] == item)
            return slot;
    }
    return MAX_TRUNK_SLOTS;
}

FUNKCIJA:CreateAllTrash( )
{
    for( new i = 0; i < 12; i++ )
    {
        if( !IsValidDynamicObject(RandBus[ i ][ objectas ]) )
            RandBus[ i ][ objectas ] = CreateDynamicObject(1264, RandBus[ i ][ X4 ], RandBus[ i ][ Y4 ], RandBus[ i ][ Z4 ]-0.5, 0.0, 0.0, 0.0);
    }
    return 1;
}



 RemoveAllCargoFromVehicle(vehicleid)
{
    for(new i = 0; i < sizeof VehicleCargo[]; i++)
        if(VehicleCargo[ vehicleid ][ i ][ Amount])
            RemoveCargoFromVehicle(vehicleid, VehicleCargo[ vehicleid ][ i ][ CargoId ], VehicleCargo[ vehicleid ][ i ][ Amount]);
    return 1;
}

 RemoveCargoFromVehicle(vehicleid, cargoid, amount = 1)
{
    new query[140];
    for(new i = 0; i < sizeof(VehicleCargo[]); i++)
    {
        if(!VehicleCargo[ vehicleid ][ i ][ Amount ]) 
            continue;
        if(VehicleCargo[ vehicleid ][ i ][ CargoId ] != cargoid)
            continue;

        VehicleCargo[ vehicleid ][ i ][ Amount ] -= amount;
        // Jei nebëra kroviniø, paðalinam ir jo ID ið maðinos ir DB
        if(VehicleCargo[ vehicleid ][ i ][ Amount ] == 0)
        {
            VehicleCargo[ vehicleid ][ i ][ CargoId ] = 0;
            if(cInfo[ vehicleid ] [ cID ])
            {
                format(query,sizeof(query), "DELETE FROM vehicle_cargo WHERE vehicle_id= %d AND cargo_id = %d AND is_static = 0",
                    cInfo[ vehicleid ][ cID ], cargoid);
                mysql_query(DbHandle, query, false);
            }
			else 
			{
				format(query,sizeof(query), "DELETE FROM vehicle_cargo WHERE vehicle_id= %d AND cargo_id = %d AND is_static = 1",
                    sVehicles[ vehicleid ][ Id ], cargoid);
                mysql_query(DbHandle, query, false);
			}
        }
        else 
        {
            if(cInfo[ vehicleid ][ cID ])
            {
                format(query,sizeof(query),"UPDATE vehicle_cargo SET amount = amount - %d WHERE vehicle_id = %d AND cargo_id = %d AND is_static = 0",
                    amount, cInfo[vehicleid ][ cID ], cargoid);
                mysql_query(DbHandle, query, false);
            }
			else 
			{
				format(query,sizeof(query),"UPDATE vehicle_cargo SET amount = amount - %d WHERE vehicle_id = %d AND cargo_id = %d AND is_static = 1",
                    amount, sVehicles[vehicleid ][ Id ], cargoid);
                mysql_query(DbHandle, query, false);
			}
        }

        // Su forklift kiek kitaip
        if(GetVehicleModel(vehicleid) == 530)
        {
            if(VehicleCargo[ vehicleid ][ i ][ Amount ] == 0)
            {
				for(new j = 0; j < 4; j++)
					DestroyVehicleObject(vehicleid, j);
            }
            else 
            {
                // Buvo 3 dezes
                // isima 2
                // Turi likt 1.

                // Naudojami slotai: 0, 1, 2, 3
                // Turi b8t naudojami: 0,1 
                for(new j = VehicleCargo[ vehicleid ][ i ][ Amount ] + amount; j != VehicleCargo[ vehicleid ][ i ][ Amount ]; j--)
                    DestroyVehicleObject(vehicleid, j);
            }
        }
		else 
		{
			switch(GetCargoType(cargoid))
			{
				case 1: DestroyVehicleObject(vehicleid, 0);
				case 2,5:
				{
                    new index = GetLastUsedVehicleObjectSlot(vehicleid);
					for(new j = index; j >= 0; j--)
						DestroyVehicleObject(vehicleid,  j);
				}
			}
		}
        return 1;
    }
    return 0;
}
 DestroyVehicleObject(vehicleid, index)
{
    if(index < 0 || index >= MAX_TRUCKER_CARGO_OBJECTS)
        return printf("Error. Klaida. DestroyVehicleObject(%d,%d) index invalid", vehicleid, index);
    DestroyObject(cInfo[ vehicleid ][ objectai ][ index ]);
    cInfo[ vehicleid ][ objectai ][ index ] = -1;
    return 1;
}
 AddCargoToVehicle(vehicleid, cargoid, bool:ignore_sql = false)
{
    new bool:found,freeSlot = -1, amount;
    for(new i = 0; i < sizeof(VehicleCargo[]); i++)
    {
        if(!VehicleCargo[ vehicleid ][ i ][ Amount ] && !VehicleCargo[ vehicleid ][ i ][ CargoId ]) 
        {
            if(freeSlot == -1)
                freeSlot = i;
            continue;
        }
        if(VehicleCargo[vehicleid ][ i ][ CargoId ] != cargoid) continue;
    

        amount = VehicleCargo[ vehicleid ][ i ][ Amount ]; // slotas objektui.
        VehicleCargo[ vehicleid ][ i ][ Amount ]++;
        found = true;
        break;
    }

    if(!found)
    {
        if(freeSlot == -1)
            printf("ERROR. Klaida. AddCargoToVehicle(%d, %d, %b) freeSlot:%d",vehicleid, cargoid, ignore_sql, freeSlot);
        VehicleCargo[ vehicleid ][ freeSlot ][ CargoId ] = cargoid;
        VehicleCargo[ vehicleid ][ freeSlot ][ Amount ] = 1;
    }

    // Irasom krovinio egzistavima i db.
	if(!ignore_sql)
	{
		new query[180];
		if(cInfo[ vehicleid ][ cID ])
			format(query,sizeof(query) ,"INSERT INTO vehicle_cargo (vehicle_id, cargo_id, amount,is_static) VALUES(%d,%d,%d,0)\
				ON DUPLICATE KEY UPDATE amount = VALUES(amount)",
				cInfo[ vehicleid ][ cID ], cargoid, amount+1);
		else 
			format(query,sizeof(query) ,"INSERT INTO vehicle_cargo (vehicle_id, cargo_id, amount,is_static) VALUES(%d,%d,%d,1)\
				ON DUPLICATE KEY UPDATE amount = VALUES(amount)",
				sVehicles[ vehicleid ][ Id ], cargoid, amount+1);
		mysql_query(DbHandle, query, false);

    }

    // Sukuriam ir primontuojam objekta.

    // A forklift. Su jais kiek kitaip.
    new index = GetFreeVehicleObjectSlot(vehicleid);
    if(index == -1)
        printf("Vehicle Id=%d SqlId=%d has more cargo objects than possible(" #MAX_TRUCKER_CARGO_OBJECTS ")", vehicleid, cInfo[ vehicleid ][ cID ]);
    if(GetVehicleModel(vehicleid) == 530)
    {
        switch(amount)
        {
            case 0:
            {
                cInfo[ vehicleid ][ objectai ][ index ] = CreateObject(1448, 0.0,0.0,0.0,0.0,0.0,0.0);
                AttachObjectToVehicle(cInfo[ vehicleid ][ objectai ][ index ], vehicleid, -0.030395508, 0.7, 0.0, 0.0,0.0,0.0);
                cInfo[ vehicleid ][ objectai ][ index+1 ] = CreateObject(2912, 0.0,0.0,0.0,0.0,0.0,0.0);
                AttachObjectToVehicle(cInfo[ vehicleid ][ objectai ][ index+1 ], vehicleid,  0.3079834, 0.7, 0.1, 0.0,0.0,0.0);
            }
            case 1:
            {
                cInfo[ vehicleid ][ objectai ][ index ] = CreateObject(2912, 0.0,0.0,0.0,0.0,0.0,0.0);
                AttachObjectToVehicle(cInfo[ vehicleid ][ objectai ][ index ], vehicleid,  -0.38916016, 0.7, 0.1, 0.0,0.0,0.0);
            }
            case 2:
            {
                cInfo[ vehicleid ][ objectai ][ index ] = CreateObject(2912, 0.0,0.0,0.0,0.0,0.0,0.0);
                AttachObjectToVehicle(cInfo[ vehicleid ][ objectai ][ index ], vehicleid,  -0.045166016, 0.7, 0.8, 0.0,0.0,0.0);
            }
        }
    }
    else 
    {
        new type = GetCargoType(cargoid);
        // 4 tipas nereikalauja objekto.
        if(type != 4)
        {
            new objectid = GetTruckerCargoObject(GetCargoType(cargoid));
            cInfo[ vehicleid ][ objectai ][ index ] = CreateObject(objectid, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000);
            
            new Float:offX,Float:offY, Float:offZ, Float:rotx,Float:roty,Float:rotz;
            GetTruckerCargoOffsets(GetVehicleModel(vehicleid), GetCargoType(cargoid), index, offX, offY, offZ, rotx, roty, rotz);
            AttachObjectToVehicle(cInfo[ vehicleid ][ objectai ][ index ], vehicleid, offX, offY, offZ, rotx, roty, rotz);
        }
    }

}

 GetFreeVehicleObjectSlot(vehicleid)
{
    for(new i = 0; i < MAX_TRUCKER_CARGO_OBJECTS; i++)
    {
        if(cInfo[ vehicleid ][ objectai ][ i ] == -1)
        {
            return i;
        }
    }
    return -1;
}
 GetLastUsedVehicleObjectSlot(vehicleid)
{
    for(new i = MAX_TRUCKER_CARGO_OBJECTS-1; i >= 0; i--)
        if(cInfo[ vehicleid ][ objectai ][ i ] != -1)
            return i;
    return -1;
}

 GetTruckerCargoOffsets(model, cargo_type, number, &Float:x, &Float:y, &Float:z, &Float:rotx, &Float:roty, &Float:rotz)
{
    switch(model)
    {
		case 428: //securicar
		{
			switch(cargo_type)
			{
				case 2: // dezes 
				{
					switch(number)
					{
						case 0: { x = 0.7293701;	y = -0.4281006;	z = -0.15219975; }
						case 1: { x = 0.025268555;	y = -0.4281006;	z = -0.15219975; }
						case 2: { x = -0.6697998;	y = -0.4281006;	z = -0.15219975; }
						case 3: { x = -0.6697998;	y = -1.1330566;	z = -0.15219975; }
						case 4: { x = 0.020263672;	y = -1.1330566;	z = -0.15219975; }
						case 5: { x = 0.7102051;	y = -1.1330566;	z = -0.15219975; }
						case 6: { x = -0.66955566;	y = -1.8280029;	z = -0.15219975; }
						case 7: { x = 0.020385742;	y = -1.8280029;	z = -0.15219975; }
						case 8: { x = 0.7104492;	y = -1.8280029; z = -0.15219975; }
						case 9: { x = -0.66955566;	y = -2.5179443;	z = -0.15219975; }
						case 10: { x = 0.020385742;	y = -2.5179443;	z = -0.15219975; }
						case 11: { x = 0.7104492;	y = -2.5179443;	z = -0.15219975; }
						case 12: { x = 0.7293701;	y = -0.4281006;	z = 0.53779984; }
						case 13: { x = 0.03930664;	y = -0.4281006; z = 0.53779984; }
						case 14: { x = -0.65063477;	y = -0.4281006;	z = 0.53779984; }
						case 15: { x = 0.03930664;	y = -1.1330566;	z = 0.53779984; }
					}
				}
				case 6: // Brangios dezes 
				{
					switch(number)
					{
						case 0 .. 7:
							{ x = 0.0013427734;	y = -0.9185791;	z = -0.17819977; }
						case 8 .. 15: 
							{ x = 0.0013427734;	y = -2.1785889;	z = -0.17819977; }

					}
				}
			}
		}
		case 578: //DFT-30
		{
			switch(cargo_type)
			{
				case 1: // malkos
					{x = 0.203125;	y = -4.814087;	z = 0.82102966; rotx = 0.00000; roty = 0.00000; rotz = 4.53463;}
				case 5: // plytos
				{
					switch(number)
					{
						case 0: { x = -0.068603516;	y = 1.3687744;	z = 0.49217987; }
						case 1: { x = -0.068603516;	y = -0.9831543;	z = 0.4921999; }
						case 2: { x = -0.068603516;	y = -3.2302246;	z = 0.4921999; }
					}
				}
			}
		}
        case 600: // Picador
        {
            switch(number)
            {
                case 0: {x = 0.00865;  y = -0.97901;    z = -0.13754; }
                case 1: {x = 0.00865;  y = -1.68238;    z = -0.13754; }
            }
        }
        case 543, 605: // Sadler ir sadler shit
        {
            switch(number)
            {
                case 0: {x = -0.33167;    y = -2.18864; z = -0.10093; }
                case 1: {x = 0.34769;   y = -2.18864; z = -0.10093; }
            }
        } 
        case 422:   //bobcat 
        {
            switch(number)
            {
                case 0: { x = -0.33313;  y = -0.78529;    z = -0.28743; }
                case 1: { x = 0.40613; y = -0.76478;    z = -0.30857; }
                case 2: { x = 0.02588; y = -0.76478;    z = -0.30857; }
            }
        }
        case 478:   // walton
        {
            switch(number)
            {
                case 0: { x = 0.62209; y = -2.19438; z = -0.03901; }
                case 1: { x = -0.64597;  y = -2.15849; z = -0.03901; }
                case 2: { x = -0.64353;  y = -1.39652; z = -0.03901; }
                case 3: { x = 0.18288; y = -1.47685; z = -0.03901; }
            }
        }
        case 554: // Yosemite
        {
            switch(cargo_type)
            {
                case 5: // plytos
                    { x = -0.01735;  y = -1.68338; z = 0.48368; }
                case 2: // dezes
                {
                    switch(number)
                    {
                        case 0: { x = -0.39235;  y = -0.96633;    z = -0.24251; }
                        case 1: { x = 0.3314;  y = -0.97988;    z = -0.24251; }
                        case 2: { x = -0.39235;  y = -1.66616;    z = -0.24251; }
                        case 3: { x = 0.3314;  y = -1.65933;    z = -0.24251; }
                        case 4: { x = -0.39235;  y = -2.37844;    z = -0.24251; }
                        case 5: { x = 0.3314;  y = -2.39163;    z = -0.24251; }
                    }
                }
            }
        }
        case 413: // pony
        {
            switch(number)
            {
                case 0: { x = 0.40747;  y = 0.06923; z = -0.25233; }
                case 1: { x = -0.3114;  y = 0.06923; z = -0.25233; }
                case 2: { x = 0.40747;  y = -0.63732;   z = -0.25233; }
                case 3: { x = -0.3114;  y = -0.63732;   z = -0.25233; }
                case 4: { x = 0.40747;  y = -1.33959;   x = -0.25233; }
                case 5: { x = -0.3114;  y = -1.33959;   z = -0.25233; }
                case 6: { x = 0.40747;  y = -2.08287;   z = -0.25233; }
                case 7: { x = -0.3114;  y = -2.08287;   z = -0.25233; }
                case 8: { x = 0.05444;  y = -0.00341;   z = 0.38135; }
                case 9: { x = 0.05444;  y = -0.75621;   z = 0.38135; }
            }
        }
        case 459: // topfun
        {
            switch(number)
            {
                case 0: { x = 0.5128174;    y = 0.0670166;  z = -0.26280022; }
                case 1: { x = -0.37316895;  y = 0.0670166;  z = -0.26282024; }
                case 2: { x = 0.5062256;    y = -0.6333008; z = -0.26280022; }
                case 3: { x = -0.375;       y = -0.6333008; z = -0.26282024; }
                case 4: { x = 0.5062256;    y = -1.3354492; z = -0.26280022; }
                case 5: { x = -0.375;       y = -1.3354492; z = -0.26280022; }
                case 6: { x = 0.5062256;    y = -2.0534668; z = -0.26280022; }
                case 7: { x = -0.375;       y = -2.0534668; z = -0.26280022; }
                case 8: { x = 0.076538086;  y = 0.008422852; z = 0.36553955; }
                case 9: { x = 0.057617188;  y = -0.7651367; z = 0.37178993; }
            }
        }
        case 482: // burrito
        {
            switch(number)
            {
                case 0: { x = 0.51293945;   y = -0.23730469;    z = -0.48169994; }
                case 1: { x = -0.24816895;  y = -0.23730469;    z = -0.48169994; }
                case 2: { x = 0.51293945;   y = -0.9420166;     z = -0.48174; }
                case 3: { x = -0.24816895;  y = -0.9420166;     z = -0.48169994; }
                case 4: { x = 0.31293945;   y = -1.5184555;     z = -0.48169994; }
                case 5: { x = -0.24816895;  y = -1.6518555;     z = -0.48169994; }
                case 6: { x = 0.4729004;    y = -0.23730469;    z = 0.024100304; }
                case 7: { x = -0.24719238;  y = -0.23730469;    z = 0.024100304; }
                case 8: { x = 0.4729004;    y = -0.9572754;     z = 0.024100304; }
                case 9: { x = -0.24719238;  y = -0.9572754;     z = 0.024100304; }
            }
        }
        case 440: // Rumpo
        {
            switch(number)
            {
                case 0: { x = 0.37438965;   y = 0.01586914;     z = -0.4368; }
                case 1: { x = -0.3656006;   y = 0.01586914;     z = -0.4368; }
                case 2: { x = 0.37438965;   y = -0.70422363;    z = -0.4368; }
                case 3: { x = -0.3656006;   y = -0.70422363;    z = -0.4368; }
                case 4: { x = 0.37438965;   y = -1.4241943;     z = -0.4368; }
                case 5: { x = -0.3656006;   y = -1.4241943;     z = -0.4368; }
                case 6: { x = 0.37438965;   y = -2.144165;      z = -0.4368; }
                case 7: { x = -0.3656006;   y = -2.144165;      z = -0.4368; }
                case 8: { x = 0.37438965;   y = 0.01586914;     z = 0.26609993; }
                case 9: { x = -0.3656006;   y = 0.01586914;     z = 0.26609993; }
                case 10: { x = 0.37438965;  y = -0.70422363;    z = 0.26609993; }
                case 11: { x = -0.3656006;  y = -0.70422363;    z = 0.26609993; }
            }
        }
        case 498: // Boxville
        {
            switch(number)
            {
                case 0: { x = 0.039331;     y = -0.6726074; z = -0.43187046; }
                case 1: { x = 0.21936035;   y = -0.6726074; z = -0.43190002; }
                case 2: { x = -0.1035156;   y = -0.6726074; z = -0.43190002; }
                case 3: { x = 0.239331;     y = -1.3725586; z = -0.43190002; }
                case 4: { x = 0.1936035;   y = -1.3725586; z = -0.43190002; }
                case 5: { x = -0.1035156;   y = -1.3725586; z = -0.43190002; }
                case 6: { x = -0.1035156;   y = -0.6726074; z = 0.26809978; }
                case 7: { x = 0.21936035;   y = -0.6726074; z = 0.26809978; }
                case 8: { x = 0.239331;     y = -0.6726074; z = 0.26809978; }
                case 9: { x = -0.1035156;   y = -1.3725586; z = 0.26809978; }
                case 10: { x = 0.1936035;  y = -1.3725586; z = 0.26809978; }
                case 11: { x = 0.039331;        y = -1.3725586; z = 0.26809978; }
            }
        }
        case 499: // benson 
        {
            switch(cargo_type)
            {
                case 2: // dezes
                {
					switch(number)
					{
						case 0: { x = 0.5916748;		y = 0.2919922;	z = 1.0380993; }
						case 1: { x = -0.13635254;		y = 0.2919922;	z = 1.0380993; }
						case 2: { x = -0.8084717;		y = 0.18005371;	z = 1.0380993; }
						case 3: { x = -0.7453613;		y = -0.9719238;	z = -0.10730076; }
						case 4: { x = -0.045288086;		y = -0.9719238;	z = -0.10730076; }
						case 5: { x = 0.6826172;		y = -0.9719238;	z = -0.10730076; }
						case 6: { x = 0.6826172;		y = -1.7279053;	z = -0.10730076; }
						case 7: { x = -0.045288086;		y = -1.7279053;	z = -0.10730076; }
						case 8: { x = -0.7453613;		y = -1.7279053;	z = -0.10730076; }
						case 9: { x = -0.7453613;		y = -2.4278564;	z = -0.10730076; }
						case 10: { x = -0.045288086;	y = -2.4278564;	z = -0.10730076; }
						case 11: { x = 0.6826172;		y = -2.4278564;	z = -0.10730076; }
						case 12: { x = 0.6826172;		y = -2.4278564;	z = 0.5647001; }
						case 13: { x = -0.045410156;	y = -2.4278564;	z = 0.5927; }
						case 14: { x = -0.7454834;		y = -2.4278564;	z = 0.5927; }
						case 15: { x = -0.045288086;	y = -1.7279053;	z = 0.5927; }
					}
				}
                case 5: // plytos
                {
					switch(number)
					{
						case 0: { x = -0.011474609;	y = -1.2894287;	z = 0.5930004; }
						case 1: { x = -0.021728516;	y = -2.4053955;	z = 0.5930004; }
					}	
				}
            }
        
        }
        case 414: // mule 
        {
            switch(cargo_type)
            {
                case 2: // dezes 
                {
					switch(number)
					{
						case 0: { x = -0.7192383;	y = 1.5007324;	z = 1.3246002; }
						case 1: { x = -0.019165039;	y = 1.5007324;	z = 1.3246002; }
						case 2: { x = 0.65283203;	y = 1.5007324;	z = 1.3246002; }
						case 3: { x = -0.663208;	y = 0.10070801;	z = -0.10340023; }
						case 4: { x = 0.036743164;	y = 0.10070801;	z = -0.10340023; }
						case 5: { x = 0.7368164;	y = 0.10070801;	z = -0.10340023; }
						case 6: { x = -0.663208;	y = -0.59924316;	z = -0.10340023; }
						case 7: { x = 0.036743164;	y = -0.59924316;	z = -0.10340023; }
						case 8: { x = 0.70874023;	y = -0.59924316;	z = -0.10340023; }
						case 9: { x = -0.663208;	y = -1.2993164;	z = -0.10340023; }
						case 10: { x = 0.036743164;	y = -1.2993164;	z = -0.10340023; }
						case 11: { x = 0.68078613;	y = -1.2993164;	z = -0.10340023; }
						case 12: { x = -0.663208;	y = -1.9992676;	z = -0.10340023; }
						case 13: { x = 0.036743164;	y = -1.9992676;	z = -0.10340023; }
						case 14: { x = 0.68078613;	y = -1.9992676;	z = -0.10340023; }
						case 15: { x = 0.7368164;	y = 0.10070801;	z = 0.5965996; }
						case 16: { x = 0.036743164;	y = 0.10070801;	z = 0.5965996; }
						case 17: { x = -0.6352539;	y = 0.10070801;	z = 0.5965996; }
					}
				}
                case 5: // plytos
                {
					switch(number)
					{
						case 0: {x = 0.06567383;	y = -0.48010254;	z = 0.6166992; }
						case 1: {x = 0.06567383;	y = -2.2441406;	z = 0.6166992; }
						case 2: {x = 0.06567383;	y = -1.2081299;	z = 1.5407; }
					}
				}
            }
        }
        case 456: // yankee
        {
            switch(cargo_type)
            {
                case 2: //dezes 
                {
                    switch(number) 
                    {
                        case 0: { x = 0.8388672;    y = -0.15039062;    z = 0.058169365; }
                        case 1: { x = 0.12792969;   y = -0.15039062;    z = 0.058199883; }
                        case 2: { x = -0.5831299;   y = -0.15039062;    z = 0.058199883; }
                        case 3: { x = 0.8388672;    y = -0.8613281;     z = 0.058199883; }
                        case 4: { x = 0.12792969;   y = -0.8613281;     z = 0.058199883; }
                        case 5: { x = -0.5831299;   y = -0.8613281;     z = 0.058199883; }
                        case 6: { x = 0.8388672;    y = -1.5723877;     z = 0.058199883; }
                        case 7: { x = 0.12792969;   y = -1.5723877;     z = 0.058199883; }
                        case 8: { x = -0.5831299;   y = -1.5723877;     z = 0.058199883; }
                        case 9: { x = 0.8388672;    y = -2.2833252;     z = 0.058199883; }
                        case 10: { x = 0.12792969;  y = -2.2833252;     z = 0.058199883; }
                        case 11: { x = -0.5831299;  y = -2.2833252;     z = 0.058199883; }
                        case 12: { x = 0.8388672;   y = -2.9943848;     z = 0.058199883; }
                        case 13: { x = 0.12792969;  y = -2.9943848;     z = 0.058199883; }
                        case 14: { x = -0.5831299;  y = -2.9943848;     z = 0.058199883; }
                        case 15: { x = -0.5831299;  y = -3.7053223;     z = 0.058199883; }
                        case 16: { x = 0.12792969;  y = -3.7053223;     z = 0.058199883; }
                        case 17: { x = 0.8388672;   y = -3.7053223;     z = 0.058199883; }
                        case 18: { x = 0.8388672;   y = -0.15039062;    z = 0.7691994; }
                        case 19: { x = 0.12792969;  y = -0.15039062;    z = 0.7691994; }
                        case 20: { x = -0.5831299;  y = -0.15039062;    z = 0.7691994; }
                        case 21: { x = 0.8388672;   y = -0.8613281;     z = 0.7691994; }
                        case 22: { x = 0.12792969;  y = -0.8613281;     z = 0.7691994; }
                        case 23: { x = -0.5831299;  y = -0.8613281;     z = 0.7691994; }
                    }
                }
                case 5: // plytos 
                {
                    switch(number)
                    {
                        case 0, 1: { x = -0.045654297;  y = -0.5404053; z = 0.8616495; }
                        case 2, 3: { x = -0.045654297;  y = -3.2263184; z = 0.8616991; }
                    }
                }
            }
        }
        case 435, 591: // article trailers
        {
            switch(cargo_type)
            {
                case 2: //deze 
                    { x = -0.087890625; y = -0.17773438; z = -0.12885952; }
                case 5: // Plytos 
                    { x = -0.114868164; y = -0.5235596; z = 0.46541977; }
            }
        }
    }
    return 0;
}
 GetTruckerCargoObject(cargo_type)
{
    switch(cargo_type)
    {
        case 1: // Malkos
            return 18609;
        case 2: // Deze 
            return 2912;
        case 5: // plyots
            return 1685;
		case 6: // Brangi deze
			return 964;
    }
    return 0;
}

 CanPlayerUseTruckerVehicle(playerid, model)
{
    printf("CanPlayerUseTruckerVehicle(%d, %d) : IsVehicleTrucker:%d IsVehicleTrailer:%d", playerid, model, IsVehicleTrucker(model), IsVehicleTrailer(model));
	if(!IsVehicleTrucker(model) && !IsVehicleTrailer(model))
		return false;
	if(pInfo[ playerid ][ pJob ] != JOB_TRUCKER)
		return false;

	new hours = pInfo[ playerid ][ pJobHours ];
	
	
	if(hours >= 48)
		return true;
	
	switch(model)
	{
		case 530:
			return true; //forklift gali imt visi.
		case 600, 543, 605, 422, 478, 554:
			return true;

		case 413, 459, 482:
			if(hours >= 12)
				return true;

		case 440, 498, 499:
			if(hours >= 24)
				return true;

		case 414, 578, 428, 455:
			if(hours >= 32)
				return true;
                
		case 456:
			if(hours >= 48)
				return true;
	}
	return false;
}
 HasVehicleSpaceForCargo(vehicleid, cargoid)
{
    if(!IsCargoCompatibleWithVehicle(cargoid,GetVehicleModel(vehicleid)))
        return 0;
    new slotsUsed;
    for(new i = 0; i < sizeof VehicleCargo[]; i++)
    {
        if(!VehicleCargo[ vehicleid ][ i ][ Amount ]) 
            continue;
		slotsUsed += GetCargoSlot(VehicleCargo[ vehicleid ][ i ][ CargoId ]) * VehicleCargo[ vehicleid ][ i ][ Amount ];
		/*
        if(VehicleCargo[ vehicleid ][ i ][ CargoId ] == cargoid)
        {
            if(VehicleCargo[ vehicleid ][ i ][ Amount ] >= GetVehicleCargoLimit(GetVehicleModel(vehicleid)) / GetCargoSlot(VehicleCargo[ vehicleid ][ i ][ CargoId ]))
                return false;
            else 
                return true;
        }
		*/
    }
	if(GetVehicleCargoLimit(GetVehicleModel(vehicleid)) - slotsUsed >= GetCargoSlot(cargoid))
		return true;
	else 
		return false;
	/*
    if(freeSlotFound)
        return true;
    return false;
	*/
}
 GetVehicleCargoLimit(model)
{
    switch(model)
    {
        case 600, 605, 543: return 2;
        case 422: return 3;
        case 478: return 4;
        case 554: return 6;
        case 413, 459, 482: return 10;
        case 440, 498: return 12;
        case 499: return 16;
        case 414: return 18;
        case 456: return 24;
        case 435, 591: return 36;

        //Forklift
        case 530: return 3;

        // Liquids.
        case 584: return 40;

        // Loose material
        case 455: return 16;
        case 450: return 30;

        // Large cargos
        case 578: return 18; 
    }
    return 0;
}
 ShowVehicleCargo(playerid, vehicleid)
{
    new string[512];
    for(new i = 0; i < sizeof(VehicleCargo[]); i++)
    {
        if(VehicleCargo[ vehicleid ][ i ][ Amount ] == 0) continue;
        format(string,sizeof(string),"%s{000000}%d{D7D7DA}\t%s\t\t%d {FFFFFF}vienetai\n",
            string, 
            VehicleCargo[ vehicleid ][ i ][ CargoId ],
            GetCargoName(VehicleCargo[ vehicleid ][ i ][ CargoId ]), 
            VehicleCargo[ vehicleid ][ i ][ Amount]);
    }
    if(isnull(string)) 
        ShowPlayerDialog( playerid, 9999, DIALOG_STYLE_MSGBOX, "Vehicle Cargo", "Jûsø transporto priemonëje nëra jokio krovinio", "Gerai", "" );
        
    else 
    {
        ShowPlayerDialog( playerid, DIALOG_VEHICLE_CARGO_LIST, DIALOG_STYLE_LIST, "Vehicle Cargo", string, "Pasiimti", "Atgal" );
        SetPVarInt(playerid, "vehicleid", vehicleid);
    }
    return 1;
}

 ShowTPDA( playerid )
{
	ShowPlayerDialog(playerid, DIALOG_TPDA_MAIN, DIALOG_STYLE_LIST, "TPDA", "{C0C0C0}Perþiûrëti{FFFFFF} Visas industrijas\n{C0C0C0}Perþiûrëti{FFFFFF} Verslus perkanèius prekes\n{C0C0C0}Perþiûrëti{FFFFFF} Laivo informacijà", "Pasirinkti","Iðeiti");
    return 1;
}

 GetIndustrySectorName(industry_index)
{
	new name[24];
	new bought = GetIndustryBoughtCommodityCount(industry_index);
	new sold;
	foreach(CommodityIterator, i)
        if(Commodities[ i ][ IndustryId ] == Industries[ industry_index ][ Id ] 
            && Commodities[ i ][ SellBuyStatus ] == Selling
            && !Commodities[ i ][ IsBusinessCommodity ])
            sold++;
	
	// Jei pardavineja bet nieko neperka, pirmas sektorius
	if(sold && !bought)
		name = "Pirminë";
	else if(sold && bought)
		name = "Antrinë";
	else 
		name = "Paslauginë";
	return name;
}


 GetIndustryBoughtCommodityCount(industry_index)
{
    new count = 0;
    foreach(CommodityIterator, i)
        if(Commodities[ i ][ IndustryId ] == Industries[ industry_index ][ Id ] 
            && Commodities[ i ][ SellBuyStatus ] == Buying
            && !Commodities[ i ][ IsBusinessCommodity ])
            count++;
    return count;
}
 GetIndustrySoldCommodityCount(industry_index)
{
    new count = 0;
    foreach(CommodityIterator, i)
        if(Commodities[ i ][ IndustryId ] == Industries[ industry_index ][ Id ] 
            && Commodities[ i ][ SellBuyStatus ] == Selling
            && !Commodities[ i ][ IsBusinessCommodity ])
            count++;
    return count;
}

 IsShipAcceptingCargo(cargoid)
{
    #pragma unused cargoid
	// Laivas superka visas prekes kaip ir verslai...
	// Kitaip tariant, jeigu jokia industrija to neperka - laivas perka.
	//if(IsAnyIndustryBuyingCargo(cargoid))
	//	return false;
    // Nuo 2015.01.04 laivas superka VISKA.
	return true;
}

 IsAnyIndustryBuyingCargo(cargoid)
{
    if(!cargoid)
        return false;
	foreach(IndustryIterator, i)
		foreach(CommodityIterator,j)
			if(Commodities[ j ][ CargoId ] == cargoid
			&& Commodities[ j ][ IndustryId ] == Industries [ i ][ Id ]
			&& !Commodities[ j ][ IsBusinessCommodity ]
			&&	Commodities[ j ][ SellBuyStatus ] == Buying)
				return true;
	return false;
}
 HasIndustryRoomForCargo(industry_index, cargoid)
{
    if(!cargoid)
        return false;
    foreach(CommodityIterator, i)
        if(Commodities[ i ][ IndustryId ] == Industries[ industry_index ][ Id ] 
            && Commodities[ i ][ CargoId ] == cargoid
            && !Commodities[ i ][ IsBusinessCommodity ])
                if(Commodities[ i ][ CurrentStock ] < GetCargoLimit(cargoid))
                    return true;
    return false;
}
 IsIndustryAcceptingCargo(industry_index, cargoid)
{
    if(!cargoid)
        return false;
    foreach(CommodityIterator, i)
        if(Commodities[ i ][ IndustryId ] == Industries[ industry_index ][ Id ] 
            && Commodities[ i ][ CargoId ] == cargoid
            && !Commodities[ i ][ IsBusinessCommodity ])
            return true;
    return false;
}
 IsIndustrySellingCargo(industry_index, cargoid)
{
    if(!cargoid)
        return false;
    foreach(CommodityIterator, i)
        if(Commodities[ i ][ IndustryId ] == Industries[ industry_index ][ Id ] 
            && Commodities[ i ][ SellBuyStatus ] == Selling
            && Commodities[ i ][ CargoId ] == cargoid
            && !Commodities[ i ][ IsBusinessCommodity ])
            return true;
    return false;
}

 SaveIndustryCommodities(industry_index)
{
	new query[140];
	foreach(CommodityIterator, i)
    {
		if(Commodities[ i ][ IndustryId ] == Industries[ industry_index ][ Id ]
            && !Commodities[ i ][ IsBusinessCommodity ])
        {
			format(query,sizeof(query), "UPDATE commodities SET current_ = %d WHERE cargo_id = %d AND industry_id = %d AND Type = 'Industry'",
				Commodities[ i ][ CurrentStock ], Commodities[ i ][ CargoId ], Industries[ industry_index ][ Id ]); 
			mysql_query(DbHandle, query, false);
		}
	}
}

 AddCargoToIndustry(industry_index, cargoid, amount = 1)
{
    new query[160];
    foreach(CommodityIterator, i)
    {
		if(Commodities[ i ][ IndustryId ] == Industries[ industry_index ][ Id ]
            && Commodities[ i ][ CargoId ] == cargoid
            && !Commodities[ i ][ IsBusinessCommodity ])
        {
            Commodities[ i ][ CurrentStock ] += amount;
            format(query,sizeof(query),"UPDATE commodities SET current_ = %d WHERE industry_id = %d AND cargo_id = %d AND type = 'Industry'",
                Commodities[ i ][ CurrentStock ], Industries[ industry_index ][ Id ] ,Commodities[ i ][ CargoId ]);
            mysql_query(DbHandle, query, false);
			UpdateIndustryInfo(industry_index);
            return 1;
        }
	}
    return false;
}

 RemoveCargoFromIndustry(industry_index, cargoid, amount = 1)
{
	new query[160];
	foreach(CommodityIterator, i)
        if(Commodities[ i ][ IndustryId ] == Industries[ industry_index ][ Id ]
            && Commodities[ i ][ CargoId ] == cargoid
            && !Commodities[ i ][ IsBusinessCommodity ])
        {
			Commodities[ i ][ CurrentStock ] -= amount;
            format(query,sizeof(query),"UPDATE commodities SET current_ = %d WHERE industry_id = %d AND cargo_id = %d AND type = 'Industry'",
                Commodities[ i ][ CurrentStock ], Industries[ industry_index ][ Id ] ,Commodities[ i ][ CargoId ]);
            mysql_query(DbHandle, query, false);
			UpdateIndustryInfo(industry_index);
            return 1;
		}
	return false;
}


 GetCargoName(cargoid) 
{
    new str[64];
    foreach(TruckerCargoIterator, i)
    {
        if(TruckerCargo[ i ][ Id ] == cargoid)
        {
            strcat(str, TruckerCargo[ i ][ Name ]);
            return str;
        }   
    }
    return str;
}
 IsPlayerInRangeOfAnyIndustry(playerid, Float:distance)
{
    if(GetPlayerIndustryInRange(playerid,distance) == -1)
        return false;
    return true;
}

 GetPlayerIndustryInRange(playerid,Float:distance)
{
    new Float:winDistance = 999999, winIndex = -1;
    foreach(IndustryIterator, i)
    {
        new Float:tmpDis = GetPlayerDistanceFromPoint(playerid, Industries[ i ][ PosX ], Industries[ i ][ PosY ], Industries[ i ][ PosZ ]);
        if(tmpDis <= winDistance && tmpDis <= distance)
        {
            winDistance = tmpDis;
            winIndex = i;
        }
    }
    return winIndex;
}

// Useless. Legacy code.
 GetCommoditySellPrice(commodity_index)
    return Commodities[ commodity_index ][ Price ];

 GetCargoProduction(cargoid)
{
    foreach(TruckerCargoIterator, i)
        if(TruckerCargo[ i ][ Id ] == cargoid)
            return TruckerCargo[ i ][ Production ];
    return 0;
}

 GetCargoConsumption(cargoid)
{
    foreach(TruckerCargoIterator, i)
        if(TruckerCargo[ i ][ Id ] == cargoid)
            return TruckerCargo[ i ][ Consumption ];
    return 0;
}

 GetCargoLimit(cargoid)
{
    foreach(TruckerCargoIterator, i)
        if(TruckerCargo[ i ][ Id ] == cargoid)
            return TruckerCargo[ i ][ Limit ];
    return 0;
}

 GetCargoType(cargoid)
{
    foreach(TruckerCargoIterator, i)
        if(TruckerCargo[ i ][ Id ] == cargoid)
            return TruckerCargo[ i ][ Type ];
    return 0;
}

 GetCargoSlot(cargoid)
{
    foreach(TruckerCargoIterator, i)
        if(TruckerCargo[ i ][ Id ] == cargoid)
            return TruckerCargo[ i ][ Slot ];
    return 0;
}
 IsCargoInVehicle(vehicleid, cargoid)
{
    for(new i = 0; i < MAX_TRUCKER_CARGO; i++)
        if(VehicleCargo[ vehicleid ][ i ][ Amount ] && VehicleCargo[ vehicleid ][ i ][ CargoId ] == cargoid)
            return true;
    return false;
}
 IsCargoCarryable(cargoid)
{
    switch(GetCargoType(cargoid))
    {
        case 2: return true;
        default: return false;
    }
    return false;
}

 IsCargoCompatibleWithVehicle(cargoid, model)
{
    switch(GetCargoType(cargoid))
    {
        case 1:
            if(model == 578)
                return true;
        case 2:
        {
            switch(model)
            {
                case 600, 605, 543, 422, 478, 554, 413, 459, 482, 440, 498, 499, 414, 456, 435, 591, 530 : return true;
                default: return false;
            }
        }
        case 3:
            if(model == 584)
                return true;
        case 4:
            if(model == 455 || model == 450)
                return true;
        case 5:
        {
            switch(model)
            {
                case 499, 554, 414, 456, 591, 435, 578: return true;
                default: return false;
            }
        }
    }
    return false;
}

 WepNames[][24] = { // Ginklø pavadinimai
        {"Niekas"},
        {"Kastetas"},
        {"Golfo lazda"},
        {"Bananas"},
        {"Peilis"},
        {"Beisbolo lazda"},
        {"Kastuvas"},
        {"Bilijardo lazda"},
        {"Katana"},
        {"Benzininis pjøklas"},
        {"Purpurinis vibratorius"},
        {"Baltas vibratorius"},
        {"Baltas vibratorius"},
        {"Sidabrinis vibratorius"},
        {"Gëliø puokðtë"},
        {"Lazda"},
        {"Granatos"},
        {"Dujinë granata"},
        {"Molotovo kokteilis"},
        {"Vehicle Missile"},
        {"Hydra Flare"},
        {"Jetpack"},
        {"9mm pistoletas"},
        {"9mm su duslintuvu"},
        {"Desert Eagle"},
        {"Shotgun"},
        {"Sawnoff Shotgun"},
        {"Combat Shotgun"},
        {"Micro SMG"},
        {"MP5"},
        {"AK-47"},
        {"G36-C SD"},
        {"Tec-9"},
        {"Country Rifle"},
        {"Sniper Rifle"},
        {"Rocket Launcher"},
        {"HS Rocket Launcher"},
        {"Ugniasvaidis"},
        {"Minigun"},
        {"Nuotolinë bomba"},
        {"Detonatorius"},
        {"Balionëlis"},
        {"Gesintuvas"},
        {"Fotoaparatas"},
        {"Naktiniai akiniai"},
        {"Infrar. akiniai"},
        {"Paraðiutas"},
        {"Netikras pistoletas"}
    };




main()
{
    ServerStartTimestamp = gettime();
    print( "\n----------------------- Lithuanian ROLE-PLAY ---------------------" );
    print( "| GameMode creator: WARRIOR, Gedas" );
    print( "| GameMode translated by: Nova" );
    print( "-----------------------------------------------------------------\n" );
}



 LoadServer( )
{
    LoadIndustries();
    LoadTruckerCargo();
    LoadCommodities();
    // Turi bûti PO to kai uþkrautos IR prekës IR industrijos.
    foreach(IndustryIterator, i)
        UpdateIndustryInfo(i);
    return 1;
}
public OnPlayerEditAttachedObject( playerid, response, index, modelid, boneid,
                                   Float:fOffsetX, Float:fOffsetY, Float:fOffsetZ,
                                   Float:fRotX, Float:fRotY, Float:fRotZ,
                                   Float:fScaleX, Float:fScaleY, Float:fScaleZ )
{
    SendClientMessage(playerid, 0xFFFFFFFF, "Objekto redagavimas sëkmingai baigtas.");

    new
        string[ 128 ];

    format(string, 128, "%dPosX", modelid );
    SetPVarFloat ( playerid, string, fOffsetX );
    format(string, 128, "%dPosY", modelid );
    SetPVarFloat ( playerid, string, fOffsetY );
    format(string, 128, "%dPosZ", modelid );
    SetPVarFloat ( playerid, string, fOffsetZ );

    format(string, 128, "%dRotX", modelid );
    SetPVarFloat ( playerid, string, fRotX );
    format(string, 128, "%dRotY", modelid );
    SetPVarFloat ( playerid, string, fRotY );
    format(string, 128, "%dRotZ", modelid );
    SetPVarFloat ( playerid, string, fRotZ );

    format(string, 128, "%dScaleX", modelid );
    SetPVarFloat ( playerid, string, fScaleX );
    format(string, 128, "%dScaleY", modelid );
    SetPVarFloat ( playerid, string, fScaleY );
    format(string, 128, "%dScaleZ", modelid );
    SetPVarFloat ( playerid, string, fScaleZ );

    /*if(response == EDIT_RESPONSE_FINAL)
    {
        if(IsItemWearable(GetItemIdFromModel(modelid)))
           AddPlayerAttachedItem(playerid,GetItemIdFromModel(modelid),boneid,fOffsetX,fOffsetY,fOffsetZ,fRotX,fRotY,fRotZ,fScaleX,fScaleY,fScaleZ);
        else 
            SetPlayerAttachedObject(playerid,index,modelid,boneid,fOffsetX,fOffsetY,fOffsetZ,fRotX,fRotY,fRotZ,fScaleX,fScaleY,fScaleZ);
    }
    */
    return 1;
}



public OnPlayerPickUpDynamicPickup( playerid, pickupid )
{
    if ( pickupid == Pickups[ 1 ] )
        SetPlayerHealth( playerid, 100);
    return 1;
}
//new NPCTrain[2];
public OnGameModeInit()
{
    AntiDeAMX();
    SetGameModeText("LTRP " #VERSION);
    SendRconCommand("mapname ROLE-PLAY" );
    // Timeriø nustatymai
    SetTimer("Sekunde", 1000, 1);
    SetTimer("MinTime", 60000, 1);
	SetTimer("IndustryUpdate", 60*60*1000, true);
	SetTimer("CargoShipDeparture",CARGOSHIP_DOCKED_INTERVAL, false);
	ShipInfo[ LastDepartureTimestamp ] = gettime();
    //mysql_tquery(DbHandle, "UPDATE vehicles SET cVehID = 0 WHERE cVehID > 0");
//=============================[ Pagr. Serverio nustatymai ]================================
    ShowPlayerMarkers(0);
    AllowInteriorWeapons(1);
    DisableInteriorEnterExits();
    EnableStuntBonusForAll(0);
    SetNameTagDrawDistance(13.0);
    ShowNameTags(1);
    ManualVehicleEngineAndLights();
    NullRoadBlocks( );
//    skinlist = LoadModelSelectionMenu("skins.txt");
//=============================[ Liftas ]================================
    ResetElevatorQueue();
    Elevator_Initialize();

    //=============================[ Uþraunam serverio nekilnojamá ji turtá  ir kt. kas susijà su tuo]================================
    LoadServer( );
    //=============================[ Uþkraunam transporto priemoniø vagimá  ]================================
 
    CreateAllTrash( );
    //Produkcija( );
    SetTimer( "CreateAllTrash", 5*60000, 1 );


    //==============================[ PDr ir k.t Vartai ] ============================
    vartai[ 0 ][ 0 ] = CreateObject(968,595.11328, 353.75290, 17.93230,   0.00000, 0.00000, 215.06400);
    vartai[ 0 ][ 1 ] = 0;
    vartai[ 1 ][ 0 ] = CreateObject(968,606.71307, 361.89679, 17.93230,   0.00000, 0.00000, 35.09920);
    vartai[ 1 ][ 1 ] = 0;
    vartai[ 2 ][ 0 ] = CreateObject(1495, 225.07645, 115.93130, 1002.21564, 0, 0, 0);
    vartai[ 2 ][ 1 ] = 0;
    vartai[ 3 ][ 0 ] = CreateObject(1495, 239.64650, 118.66280, 1002.21570, 0, 0, -90);
    vartai[ 3 ][ 1 ] = 0;
    vartai[ 4 ][ 0 ] = CreateObject(1495, 217.56250, 120.77590, 1002.20752, 0, 0, -90);
    vartai[ 4 ][ 1 ] = 0;
    vartai[ 5 ][ 0 ] = CreateObject(1495, 266.45758, 115.84705, 1003.61621, 0, 0, 180);
    vartai[ 5 ][ 1 ] = 0;
    //=============================[ Sutvarkome narkotikus transporto priemonëje ]================================
    foreach(Vehicles,car)
    {
        if ( sVehicles[ car ][ Faction ] == 2 )
        {
            if( sVehicles[ car ][ Model ] == 598 || sVehicles[ car ][ Model ] == 596 || sVehicles[ car ][ Model ] == 599 || sVehicles[ car ][ Model ] == 490 || sVehicles[ car ][ Model ] == 426 || sVehicles[ car ][ Model ] == 427 )
            {
                cInfo[ car ][ cTrunkWeapon ][ 0 ] = 29;
                cInfo[ car ][ cTrunkAmmo   ][ 0 ] = 300;
                cInfo[ car ][ cTrunkWeapon ][ 1 ] = 31;
                cInfo[ car ][ cTrunkAmmo   ][ 1 ] = 300;
                cInfo[ car ][ cTrunkWeapon ][ 2 ] = 25;
                cInfo[ car ][ cTrunkAmmo   ][ 2 ] = 50;
            }
        }
        else if ( sVehicles[ car ][ Faction ] == 3 )
        {
            if( sVehicles[ car ][ Model ] == 407 || sVehicles[ car ][ Model ] == 544 )
            {
                cInfo[ car ][ cTrunkWeapon ][ 0 ] = 9;
                cInfo[ car ][ cTrunkAmmo   ][ 0 ] = 1;
                cInfo[ car ][ cTrunkWeapon ][ 1 ] = 42;
                cInfo[ car ][ cTrunkAmmo   ][ 1 ] = 1000;
            }
        }
        cInfo[ car ][ cFuel ] = GetVehicleFuelTank( sVehicles[ car ][ Model ] );
    }


    //=============================[ Sutvarkome transporto priemoniø degalus ir savininkus ]================================
    for( new car = 0; car < MAX_VEHICLES; car ++ )
    {
        cInfo[ car ][ cOwner] = 0;
        strmid( cInfo[ car ][ cNumbers ], "J", 0, 1, 2 );
    }
    printf( "Serveryje sukurtas objektø skaièius: %d", CountDynamicObjects( ) );
    printf( "Serveryje sukurtø 3D Text skaièius: %d", CountDynamic3DTextLabels( ) );
    printf( "Serveryje sukurtø dinaminiø arenø skaièius: %d", CountDynamicAreas( ) );
    printf( "Visi ðlaubaumai nuleisti ir atrakinti.");


    BlindfoldTextdraw = TextDrawCreate(0.0, 0.0, "box");
    TextDrawTextSize(BlindfoldTextdraw, 640.0, 0.0);
    TextDrawLetterSize(BlindfoldTextdraw, 0.0, 100.0);
    TextDrawBoxColor(BlindfoldTextdraw, 0x000000FF);
    TextDrawUseBox(BlindfoldTextdraw, 1);

    //=============================[ Serverio laiko ir atlyginimø nustatymø sutvarkymas ]================================
    new laikas[3];
    gettime(laikas[0],laikas[1], laikas[2]);
    SetWorldTime(laikas[0]+1);
    OldHour = laikas[0];
    printf("Serveryje senoji valanda: %d ", OldHour );

    return 1;
}


AntiDeAMX() {
    new a[][] = {
        "Unarmed (Fist)",
        "Brass K"
    };
    #pragma unused a
}

public OnGameModeExit()
{
    #if defined DEBUG
        print("[debug] OnGameModeExit()");
    #endif
    SendClientMessageToAll(0xFF0000FF,"Serveris perkraunamas.");
    mysql_close( );
    return 1;
}

public OnPlayerRequestClass(playerid, classid)
{
    #if defined DEBUG
        printf("OnPlayerRequestClass(%s, %d)", GetName(playerid), classid);
    #endif
   
    #if defined DEBUG
        printf("OnPlayerRequestClass(%s, %d) end", GetName(playerid), classid);
    #endif
    return 1;
}

/* GetPlayerSqlId(playerid)
    return pInfo[ playerid ][ pMySQLID ];
*/

 GetPlayerHouseKey(playerid)
    return pInfo[ playerid ][ pHouseKey ];

 Float:GetPlayerMaxHealth(playerid)
    return 100.0 + pInfo[ playerid ][ pHealthLevel ] * 3; 

 GetPlayerSavings(playerid)
    return pInfo[ playerid ][ pSavings ];

 GetPlayerTotalPaycheck(playerid)
    return pInfo[ playerid ][ pTotalPaycheck ];

 SetPlayerTotalPaycheck(playerid, value)
    return pInfo[ playerid ][ pTotalPaycheck ] = value;



 GetPlayerIP(playerid)
{
    new
        ip[16];
    GetPlayerIp(playerid, ip, sizeof (ip));
    new
        ipv = strval(ip) << 24,
        pos = 0;
    while (pos < 15 && ip[pos++] != '.') {}
    ipv += strval(ip[pos]) << 16;
    while (pos < 15 && ip[pos++] != '.') {}
    ipv += strval(ip[pos]) << 8;
    while (pos < 15 && ip[pos++] != '.') {}
    ipv += strval(ip[pos]);
    return ipv;
}
public OnPlayerConnect(playerid)
{
    #if defined DEBUG
        printf("[debug] OnPlayerConnect(%s)", GetName(playerid));
    #endif

	
    for(new car = 0; car < 21; car++)
        pInfo[ playerid ][ pCar ][ car ] = 0;



    NullPlayerInfo   ( playerid );

    //=============================[ Informacijos tekstas ]================================
    InfoText[ playerid ] = CreatePlayerTextDraw( playerid, 13, 150, "_" );
    PlayerTextDrawUseBox          ( playerid, InfoText[ playerid ], 1 );
    PlayerTextDrawBoxColor        ( playerid, InfoText[ playerid ], 0x00000066 );
    PlayerTextDrawTextSize        ( playerid, InfoText[ playerid ], 159, 91 );
    PlayerTextDrawBackgroundColor ( playerid, InfoText[ playerid ], 0x000000ff );
    PlayerTextDrawFont            ( playerid, InfoText[ playerid ], 1 );
    PlayerTextDrawLetterSize      ( playerid, InfoText[ playerid ], 0.36, 1.5 );
    PlayerTextDrawColor           ( playerid, InfoText[ playerid ], 0xffffffff );
    PlayerTextDrawSetProportional ( playerid, InfoText[ playerid ], 1 );
    PlayerTextDrawSetShadow       ( playerid, InfoText[ playerid ], 0 );

    RemoveBuildingForPlayer(playerid, 5156, 2838.0391, -2423.8828, 10.9609, 0.25);
    RemoveBuildingForPlayer(playerid, 5159, 2838.0313, -2371.9531, 7.2969, 0.25);
    RemoveBuildingForPlayer(playerid, 5160, 2829.9531, -2479.5703, 5.2656, 0.25);
    RemoveBuildingForPlayer(playerid, 5161, 2838.0234, -2358.4766, 21.3125, 0.25);
    RemoveBuildingForPlayer(playerid, 5162, 2838.0391, -2423.8828, 10.9609, 0.25);
    RemoveBuildingForPlayer(playerid, 5163, 2838.0391, -2532.7734, 17.0234, 0.25);
    RemoveBuildingForPlayer(playerid, 5164, 2838.1406, -2447.8438, 15.7266, 0.25);
    RemoveBuildingForPlayer(playerid, 5165, 2838.0313, -2520.1875, 18.4141, 0.25);
    RemoveBuildingForPlayer(playerid, 5166, 2829.9531, -2479.5703, 5.2656, 0.25);
    RemoveBuildingForPlayer(playerid, 5167, 2838.0313, -2371.9531, 7.2969, 0.25);
    RemoveBuildingForPlayer(playerid, 5335, 2829.9531, -2479.5703, 5.2656, 0.25);
    RemoveBuildingForPlayer(playerid, 5336, 2829.9531, -2479.5703, 5.2656, 0.25);
    RemoveBuildingForPlayer(playerid, 5352, 2838.1953, -2488.6641, 29.3125, 0.25);
    RemoveBuildingForPlayer(playerid, 5157, 2838.0391, -2532.7734, 17.0234, 0.25);
    RemoveBuildingForPlayer(playerid, 5154, 2838.1406, -2447.8438, 15.7500, 0.25);
    RemoveBuildingForPlayer(playerid, 3724, 2838.1953, -2488.6641, 29.3125, 0.25);
    RemoveBuildingForPlayer(playerid, 5155, 2838.0234, -2358.4766, 21.3125, 0.25);
    RemoveBuildingForPlayer(playerid, 3724, 2838.1953, -2407.1406, 29.3125, 0.25);
    RemoveBuildingForPlayer(playerid, 5158, 2837.7734, -2334.4766, 11.9922, 0.25);
    
    //SetPlayerColor(playerid,TEAM_HIT_COLOR);
    //ShowPlayerLoginDialog(playerid);
    #if defined DEBUG
        printf("[debug] OnPlayerConnect(%s) end", GetName(playerid));
    #endif
    return 1;
}

 NullPlayerInfo( playerid )
{

    pInfo[ playerid ][ pExp      ] = 0;

    pInfo[ playerid ][ pMask     ] = 1;
    pInfo[ playerid ][ pJailTime ] = 0;
    pInfo[ playerid ][ pWarn     ] = 0;
    pInfo[ playerid ][ pJail     ] = 0;
    pInfo[ playerid ][ pLead     ] = 0;
    pInfo[ playerid ][ pMember   ] = 0;
    pInfo[ playerid ][ pCarGet   ] = 0;
    pInfo[ playerid ][ pHouseKey ] = 0;
    pInfo[ playerid ][ pPhone    ] = 0;
    pInfo[ playerid ][ pRSlot ] = 0;

    SetPVarInt( playerid, "P_SMONEY", 0 );// Serverio puses pinigeliai.
    SetPVarInt( playerid, "TOG_FAMILY", 1 );
    SetPVarInt( playerid, "PDTYPE", 0 );
    SetPVarInt( playerid, "BACKUP", INVALID_PLAYER_ID );
    SetPVarInt( playerid, "CallOwner", false );
    SetPVarInt( playerid, "Addicted", false );

    Offer[ playerid ][ 0 ] = 255;
    Offer[ playerid ][ 1 ] = 255;
    Offer[ playerid ][ 2 ] = 255;
    Offer[ playerid ][ 3 ] = 255;
    Offer[ playerid ][ 4 ] = 255;
    Offer[ playerid ][ 5 ] = 255;
    Offer[ playerid ][ 8 ] = INVALID_PLAYER_ID;


    Boxing[ playerid ] = false;
    Voted [ playerid ] = true;

    pInfo[ playerid ][ pVirWorld ] = 0;
    pInfo[ playerid ][ pInt      ] = 0;
    pInfo[ playerid ][ pCuffs    ] = 0;
    pInfo[ playerid ][ pDonator    ] = 0;

    Checkpoint[ playerid ] = CHECKPOINT_NONE;
    pInfo[ playerid ][ pJobDuty ] = 0;

    TalkingLive     [ playerid ] = 255;
    Ruko            [ playerid ] = 0;
    Laikas          [ playerid ] = 0;
    LaikoTipas      [ playerid ] = 0;
    MobilePhone     [ playerid ] = INVALID_PLAYER_ID;
    RingTone        [ playerid ] = 0;
    Mires           [ playerid ] = 0;
    Mute            [ playerid ] = false;
    Camera          [ playerid ] = -1;
    PlayerSpeed     [ playerid ] = 0;
    OldCar  [ playerid ] = 0;
    FirstSpawn[ playerid ] = true;
    for ( new i = 1; i <= 7; i++ )
    {
        new ministr[ 8 ];

        format           ( ministr, 8, "NOTE_%d", i );
        SetPVarString    ( playerid, ministr, "" );
        format           ( ministr, 8, "NOTE2_%d", i );
        SetPVarInt       ( playerid, ministr, 0 );
    }
    gPlayerUsingLoopingAnim [ playerid ] = false;
    IsOnePlayAnim           [ playerid ] = false;
    BackOut                 [ playerid ] = 0;


    InfoTextTimer[ playerid ] = -1;

    VehicleLoadTimer[ playerid ] = -1;
	VehicleLoadTime[ playerid ] = 0;

    IsFillingFuel[ playerid ] = false;


    PlayerSpectatedPlayer[ playerid ] = INVALID_PLAYER_ID;
    ShowACTestMsg[ playerid ] = true;

    if(IsBlindfolded[ playerid ])
        TextDrawHideForPlayer(playerid, BlindfoldTextdraw);
    IsBlindfolded[ playerid ] = false;

    for(new i = 0; i < MAX_PLAYERS; i++)
        PlayersBlocked[ playerid ][ i ] = false;
    return 1;
}
public OnPlayerDisconnect(playerid, reason)
{
    #if defined DEBUG
        printf("[debug] OnPlayerDisconnect(%s, %d)", GetName(playerid), reason);
    #endif
    new string[225],name[MAX_PLAYER_NAME+1];
    GetPlayerName(playerid,name,MAX_PLAYER_NAME+1);
    HidePlayerInfoText( playerid );
    HideInfoText( playerid );
    DestroyDynamic3DTextLabel(DeathLabel[playerid]);
    DestroyDynamic3DTextLabel(SpecCommandLabel[ playerid ]);

    if(IsPlayerDataRecorded[ playerid ])
    {
        IsPlayerDataRecorded[ playerid ] = false;
        StopRecordingPlayerData(playerid);
    }


    KillTimer(SpecCommandTimer[ playerid ]);
    SpecCommandTimer[ playerid ] = -1;

    if(GetPVarInt(playerid, "FineOfferMemory"))
        free(Alloc:GetPVarInt(playerid, "FineOfferMemory"));
    
    switch(reason)
    {
        case 0:
        {
            format(string,sizeof(string),"%s paliko serverá (ávyko kliento klaida/nutrøko ryðys).",name);
            GetPlayerPos(playerid,pInfo[playerid][pCrashPos][0],pInfo[playerid][pCrashPos][1],pInfo[playerid][pCrashPos][2]);

            pInfo[playerid][pVirWorld] = GetPlayerVirtualWorld(playerid);

            pInfo[playerid][pInt] = GetPlayerInterior(playerid);
            pInfo[playerid][pCrash] = 1;
        }
        case 1: format(string,64,"%s paliko serverá (Klientas atsijungë).",name);
        case 2: format(string,64,"%s paliko serverá (Klientas iðmestas).",name);
    }
    ProxDetector(20.0, playerid, string,COLOR_FADE1,COLOR_FADE2,COLOR_FADE3,COLOR_FADE4,COLOR_FADE5);
    if(AdminDuty[playerid] == true)
    {
//        cmd_aduty(playerid);
        Delete3DTextLabel(AdminON[playerid]);
        AdminDuty[playerid] = false;
    }

    if(Mires[playerid] > 0 )
        SetPlayerDeaths(playerid, GetPlayerDeaths(playerid) + 1);

    if( Mires[ playerid ] == 0 && !IsPlayerInAnyVehicle( playerid ) )
    {
        for ( new slot = 0; slot < 12; slot++ )
        {
            new wep,
                ammo;

            GetPlayerWeaponData( playerid, slot, wep, ammo );
            if ( wep > 1 && wep != 19 && wep != 20 && wep < 39 && ammo > 65000)
            {
                new count;
                while ( ammo > 65000 )
                {
                    count ++;
                    GetPlayerWeaponData( playerid, slot, wep, ammo );
                    if ( count > 20 )
                        break;
                }
            }
            if ( wep > 0 && ammo > 0 )
            {
                for ( new i = 0; i < MAX_SAVED_WEAPONS; i++ )
                {
                    if ( pInfo[ playerid ][ pGun ][ i ] == 0 )
                    {
                        pInfo[ playerid ][ pGun  ][ i ] = wep;
                        pInfo[ playerid ][ pAmmo ][ i ] = ammo;
                        break;
                    }
                }
            }
        }
    }

    if ( TalkingLive[ playerid ] != 255 )
    {
        if (IsPlayerConnected(TalkingLive[playerid]))
            TalkingLive[TalkingLive[playerid]] = 255;
        TalkingLive[playerid] = 255;
    }
    if ( aInfo[ playerid ][ aObjekt ] > 0 )
    {
      
        aInfo[ playerid ][ aKords ][ 0 ] = 0.0;
        aInfo[ playerid ][ aKords ][ 1 ] = 0.0;
        aInfo[ playerid ][ aKords ][ 2 ] = 0.0;
        format(aInfo[ playerid ][ aStation ], 128, "");
        DestroyDynamicObject( aInfo[ playerid ][ aObjekt ] );
        DestroyDynamicArea( aInfo[ playerid ][ aArea ] );

        aInfo[ playerid ][ aObjekt ] = 0;
        Itter_Remove(Audio3D,playerid);
    }
    if(Boxing[playerid] == true)
        BoxEnd(playerid);
        

    if (IsPlayerLoggedIn(playerid) )
    {
        SaveAccount( playerid );
    }

    if ( pInfo[ playerid ][ pBackup ] == 1 )
    {
        foreach(Player,i)
        {
            if ( GetPVarInt( i, "BACKUP") == playerid )
            {
                DisablePlayerCheckpoint( i );
                Checkpoint[ i ] = CHECKPOINT_NONE;
                SetPVarInt( i, "BACKUP", INVALID_PLAYER_ID );
            }
        }
        pInfo[ playerid ][ pBackup ] = 0;
    }


    Laikas[playerid] = 0;
    LaikoTipas[playerid] = 0;
    Ruko[playerid] = 0;
    SetPVarInt( playerid, "P_SMONEY", 0 );

    PlayerTextDrawHide   ( playerid, InfoText[ playerid ] );
    PlayerTextDrawDestroy( playerid, InfoText[ playerid ] );
    PlayerTextDrawHide   ( playerid, Greitis[ playerid ] );
    PlayerTextDrawDestroy( playerid, Greitis[ playerid ] );

    KillTimer(VehicleLoadTimer[playerid]);
    // Jei pylësi kurà, nemokamo jam neduosim
    if(IsFillingFuel[ playerid ])
    {
        KillTimer(PlayerFillUpTimer[ playerid ]);
        cInfo[ GetPlayerVehicleID(playerid) ][ cFuel ] = GetPVarInt( playerid, "FILLED" );
    }
    return 1;
}

public OnPlayerSpawn(playerid)
{
    #if defined DEBUG
        printf("[debug] OnPlayerSpawn(%s)", GetName(playerid));
    #endif
  
    #if defined DEBUG
        printf("[debug] OnPlayerSpawn(%s) end", GetName(playerid));
    #endif
    return 1;
}


public OnPlayerDeath(playerid, killerid, reason)
{
    #if defined DEBUG
        printf("[debug] OnPlayerDeath(%s, %d, %d)", GetName(playerid), killerid, reason);
    #endif

    if(gPlayerUsingLoopingAnim[playerid] == true)
    {
        gPlayerUsingLoopingAnim[playerid] = false;
    }
    if(IsOnePlayAnim[playerid] == true)
    {
        IsOnePlayAnim[playerid] = false;
    }
    if(BackOut[playerid] == 1)
    {
        BackOut[playerid] = 0;
    }
    return 1;
}

public OnVehicleSpawn( vehicleid )
{
    #if defined DEBUG
        printf("[debug] OnVehicleSpawn(%d)", vehicleid);
    #endif
    
    switch(GetVehicleModel(vehicleid)) {
        case 427, 428, 432, 601, 528: SetVehicleHealth(vehicleid, 5000.0); // Enforcer, Securicar, Rhino, SWAT Tank, FBI truck - this is the armour plating dream come true.
    }
    
    Engine[ vehicleid ] = false;
    if( cInfo[ vehicleid ][ cOwner ] == 0 )
        ChangeVehicleColor( vehicleid, sVehicles[ vehicleid ][ Color1    ],
                                       sVehicles[ vehicleid ][ Color2    ] );
    AddVehicleComponent( vehicleid, cInfo[ vehicleid ][ cWheels    ] );
    AddVehicleComponent( vehicleid, cInfo[ vehicleid ][ cHidraulik ] );
    TuneCarMods( vehicleid );
    return 1;
}

public OnVehicleDeath(vehicleid, killerid)
{
    #if defined DEBUG
        printf("[debug] OnVehicleDeath(%d, %s)", vehicleid, GetName( killerid ));
    #endif
    new time  [ 6 ],
        string[ 126 ];
    cInfo[ vehicleid ][ cDuzimai ] ++;
    
    if(IsVehicleLoaded[ vehicleid ])
        IsVehicleLoaded[vehicleid ] = false;

    if( killerid != INVALID_PLAYER_ID && cInfo[ vehicleid ][ cOwner ] > 0 )
    {
        printf( "Tr. priemonës MySQL ID: %d (%s), sunaikinusiojo ID: %d (%s)", cInfo[ vehicleid ][ cID ], GetVehicleName( GetVehicleModel( vehicleid ) ), killerid, GetName( killerid ));
        format( string, 126, "AdmWarn: ([%d]%s[%d]) sunaikino tr. priemonæ.", killerid, GetName( killerid ) );
        SendAdminMessage( COLOR_ADM, string );
    }
    // Jei kaþkà veþë, krovinys prarastas :(
    RemoveAllCargoFromVehicle(vehicleid);

    for(new i = 0; i < MAX_TRUCKER_CARGO_OBJECTS; i++)
        if(cInfo[ vehicleid ][ objectai ][ i ] != -1)
            DestroyObject( cInfo[ vehicleid ][ objectai ][ i ] );
    
    SetVehicleToRespawn( vehicleid );
    if (cInfo[ vehicleid ][ cOwner ] > 0)
    {
        new carowner = GetCarOwner( vehicleid );
        if ( cInfo[ vehicleid ][ cInsurance ] >= 0)
        {
            cInfo[ vehicleid ][ cInsurance ] --;
            cInfo[ vehicleid ][ cVehID     ] = 0;
            cInfo[ vehicleid ][ cTuning     ] = 0;
            cInfo[ vehicleid ][ cWheels     ] = 0;
            cInfo[ vehicleid ][ cHidraulik     ] = 0;
            //SaveCar       ( vehicleid );
            DestroyVehicle( vehicleid );
        }
        if(IsPlayerConnected(carowner))
        {
            gettime( time[ 0 ], time[ 1 ], time[ 2 ] );
            getdate( time[ 3 ], time[ 4 ], time[ 5 ] );

            format           ( string, 126,"* Dëmesio, Tr. priemonë buvo sunaikinta, duomenys: %s, laikas ir data: %d-%d-%d %d:%d:%d, sunaikinusiojo ID: %d (%s)",cInfo[ vehicleid ][ cName ], time[ 3 ], time[ 4 ], time[ 5 ],time[ 0 ], time[ 1 ], time[ 2 ], killerid, GetName( killerid ));
            SendClientMessage( carowner, COLOR_FADE1, string);
            pInfo[ carowner ][ pCarGet ] --;
        }
        nullVehicle   ( vehicleid );
    }
    return 1;
}

FUNKCIJA:UnChat(playerid)
{
    StopLoopingAnim(playerid);
    return true;
}


CMD:fpv(playerid,params[])
{
    #pragma unused params
    if(!IsPlayerInAnyVehicle(playerid)) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, norëdami naudoti ðá veiksmà, privalote sedëti/bûtii tr. priemonëje. ");
    if(GetPVarInt(playerid,"used") == 0)
    {
        new p = GetPlayerVehicleID(playerid);
        vehview[playerid] = CreatePlayerObject(playerid,19300, 0.0000, -1282.9984, 10.1493, 0.0000, -1, -1, 100);
        AttachPlayerObjectToVehicle(playerid,vehview[playerid],p,-0.314999, -0.195000, 0.510000, 0.000000, 0.000000, 0.000000);
        AttachCameraToPlayerObject(playerid,vehview[playerid]);
        SetPVarInt(playerid,"used",1);
        PlayerPlaySound( playerid, 1057, 0.0, 0.0, 0.0 );
    }
    else if(GetPVarInt(playerid,"used") == 1)
    {
        SetCameraBehindPlayer(playerid);
        DestroyPlayerObject(playerid,vehview[playerid]);
        SetPVarInt(playerid,"used",0);
        PlayerPlaySound( playerid, 1057, 0.0, 0.0, 0.0 );
    }
    return 1;
}

CMD:help(playerid)
{
    SendClientMessage( playerid, COLOR_GREEN, "|________________________________Serverio komandø sàraðas________________________________|");
    SendClientMessage( playerid, COLOR_FADE2, "  ROLE-PLAY VEIKSMAI: /me /do /try");
    SendClientMessage( playerid, COLOR_FADE1, "  IC IR OOC KALBËJIMO KANALAI: IC - /f /r /s /low /g /ame /w /cw /ds. OOC - /b (/o)oc /pm.");
    SendClientMessage( playerid, COLOR_FADE2, "  VEIKËJO VALDYMAS: /levelup /fpv /stats /inv /invweapon /transfer /pay /anims /learnfight /stop /die /(lic)ences");
    SendClientMessage( playerid, COLOR_FADE1, "  DARBAS IR FRAKCIJA: /leavefaction /takejob /leavejob");	
    SendClientMessage( playerid, COLOR_FADE2, "  ADMINISTRACIJA IR PAGALBA: /admins /moderators /(re)port /askq");
    SendClientMessage( playerid, COLOR_FADE1, "  TURTO PIRKIMAS: /buy /buygun /buyhouse /buybiz");
    SendClientMessage( playerid, COLOR_FADE2, "  KITOS KOMANDOS: /bail /id /make /bank /note /knock /maxspeed /charity /lock /accept /fines /vehiclefines /seatbelt");
    SendClientMessage( playerid, COLOR_FADE1, "  KITOS KOMANDOS: /lastad /bell /setcard /ccard /windows /trunk /bonnet /sid /savings");
//    if ( pInfo[ playerid ][ pJob ] == JOB_MECHANIC )
//    SendClientMessage( playerid, COLOR_LIGHTRED2, "  MECHANIKO KOMANDOS: /repair /repaint /addwheels");
    //if ( pInfo[ playerid ][ pJob ] == JOB_TRASH )
    //  SendClientMessage( playerid, COLOR_LIGHTRED2, "  ÐIÛKÐLININKO: /startmission /endmission /takegarbage /throwgarbage");
//    if ( pInfo[ playerid ][ pJob ] == JOB_DRUGS )
 //     SendClientMessage( playerid, COLOR_LIGHTRED2, "  NARKOTIKØ PREKEIVIO KOMANDOS: /buyseeds /cutweed");
//    if ( pInfo[ playerid ][ pJob ] == JOB_GUN )
//      SendClientMessage( playerid, COLOR_LIGHTRED2, "  GINKLØ GAMINTOJO KOMANDOS: /weaponlist /make");
    if ( pInfo[ playerid ][ pJob ] == JOB_TRUCKER )
	{ 
      SendClientMessage( playerid, COLOR_LIGHTRED2, "  KROVINIØ PERVEÞIMO VAIRUOTOJO KOMANDOS: /tpda /cargo /killcheckpoint"),
      SendClientMessage( playerid, COLOR_LIGHTRED2, "  /tpda - kroviniø tvarkaraðtis | /cargo - kroviniø valdymas | /killcheckpoint - esame CP panaikinimas.");	
	}
   // if ( pInfo[ playerid ][ pJob ] == JOB_JACKER )
//		SendClientMessage( playerid, COLOR_LIGHTRED2, "  TR. PRIEMONËS VOGIMO KOMANDOS: /sellcar /info /spots");
//	if ( PlayerFaction( playerid ) == 1 )
		//SendClientMessage( playerid, COLOR_POLICE, "  LOS SANTOS POLICIJOS DEPARTAMENTAS: /policehelp");
   /* if ( PlayerFaction( playerid ) == 2 ) 
	{
        SendClientMessage( playerid, COLOR_LIGHTRED2, "|________________________________Mediko komandos________________________________|"),
		SendClientMessage( playerid, COLOR_WHITE, "  /rb /rrb /drag /fdgear /duty /heal /takefmoney /checfkbudget /flist /tlc");     
	}
    if ( PlayerFaction( playerid ) == 5 ) 
	{
        SendClientMessage( playerid, COLOR_GREEN2, "|________________________Savivaldybës darbuotojo komandos________________________|"),
		SendClientMessage( playerid, COLOR_WHITE, "  /duty /takemoney /takefmoney /checfkbudget /checkbudget /flist");     
	}*/
    if ( pInfo[ playerid ][ pLead ] > 1 )
        SendClientMessage( playerid, COLOR_WHITE, "  FRAKCIJOS VALDYMAS: /invite /uninvite /setrank /flist /nof /togf" );
    if ( GetPlayerAdminLevel(playerid) > 0 )
    SendClientMessage( playerid, COLOR_WHITE, "  ADMINISTRATORIUS: Pokalbiai - /a, darbas - /aduty, komandos - /ahelp /togadmin");
    if ( pInfo[ playerid ][ pDonator ] > 0 )
        SendClientMessage( playerid, COLOR_FADE1, "  REMËJAS: /blockpm /togpm /walkstyle /talkstyle /mask");
    SendClientMessage( playerid, COLOR_FADE2, "  SISTEMØ PAGALBA/KOMANDOS: /v /radiohelp /phonehelp /phonebookhelp /bizhelp /househelp ");
    SendClientMessage( playerid, COLOR_FADE1, "  SISTEMØ PAGALBA/KOMANDOS: /vradio /garagehelp /fishinghelp /toghelp /gunhelp");	
    SendClientMessage( playerid, COLOR_GREEN2, "________________________Daugiau informacijos________________________");
    SendClientMessage( playerid, COLOR_FADE1, "  Vis informacija pateikta møsø diskusijø forume forum.ltrp.lt");
    SendClientMessage( playerid, COLOR_FADE2, "  Jei prireikë pagalbos, visados galite klausti naudodami komanda /askq");
    //Produkcija( );
    return 1;
}
CMD:gunhelp(playerid)
{
    SendClientMessage( playerid, COLOR_GREEN, "|__________________GINKLØ KOMANDOS__________________|");
    SendClientMessage( playerid, COLOR_WHITE, "  /leavegun - numeta ant þemës rankoje laikomà ginklà, kuri galite paiimti su /grabgun.");
    SendClientMessage( playerid, COLOR_FADE1, "  /grabgun - paiiima ant þemës rodomà ginklà");
    SendClientMessage( playerid, COLOR_WHITE, "  /buygun - naudojama ginklø parduotuvëje");
    SendClientMessage( playerid, COLOR_GREEN, "__________________________________________________________________");	
    return 1;
}
CMD:phonebookhelp(playerid)
{
    SendClientMessage( playerid, COLOR_GREEN, "|__________________TELEFONØ KNYGOS KOMANDOS__________________|");
    SendClientMessage( playerid, COLOR_WHITE, "  /phonebook - Jûsø telefonø adresø sàraðas.");
    SendClientMessage( playerid, COLOR_FADE1, "  /addcontact - pridësite kontaktà á telefonø knygà");
    SendClientMessage( playerid, COLOR_WHITE, "  /deletecontact - iðtrinsite kontaktà ið telefonø knygos");
    SendClientMessage( playerid, COLOR_GREEN, "__________________________________________________________________");	
    return 1;
}
CMD:toghelp(playerid)
{
    SendClientMessage( playerid, COLOR_GREEN, "|__________________KANALØ IÐJUNGIMO KOMANDOS__________________|");
    SendClientMessage( playerid, COLOR_WHITE, "  /togooc - nebematysite OOC kanalø ir informacijos");
    SendClientMessage( playerid, COLOR_FADE1, "  /tognames - iðjungsite rodomus veikëjø vardus");
    SendClientMessage( playerid, COLOR_WHITE, "  /tognews - nebematysite SAN News kanalo skelbiamø naujienø");
    SendClientMessage( playerid, COLOR_FADE1, "  /togpm - nebegalësite gauti privaèiø þinuèiø");
    SendClientMessage( playerid, COLOR_FADE1, "  KITOS KOMANDOS: /togf /togq /togadmin");
    SendClientMessage( playerid, COLOR_GREEN, "__________________________________________________________________");	
    return 1;
}

CMD:radiohelp(playerid)
{
    SendClientMessage( playerid, COLOR_GREEN, "|__________________RACIJOS NAUDOJIMO KOMANDOS__________________|");
    SendClientMessage( playerid, COLOR_FADE1, "  /r [TEKSTAS] - IC pokalbiø per racijà kanalas." );
    SendClientMessage( playerid, COLOR_WHITE, "  /rlow [TEKSTAS] - IC pokalbiø per racijà kanalas kalbant tyliai" );
    SendClientMessage( playerid, COLOR_FADE1, "  /setchannel [1-3] [RACIJOS KANALAS] - racijos kanalo nustatymas/keitimas./setslot" );
    SendClientMessage( playerid, COLOR_WHITE, "  /setslot [1-3] - nustatyti vietà kanalui." );
    SendClientMessage( playerid, COLOR_GREEN, "__________________________________________________________________" );	
    return 1;
}
CMD:phonehelp(playerid)
{
    SendClientMessage( playerid, COLOR_GREEN, "|__________________MOBILAUS TELEFONO NAUDOJIMAS__________________|");
    SendClientMessage( playerid, COLOR_LIGHTRED2, "Norëdami suþinoti specialiuosius numerius paraðykite komandà /call");	
    SendClientMessage( playerid, COLOR_FADE1, "  /call [NUMERIS] - skambinti á pasirinktà numerá." );
    SendClientMessage( playerid, COLOR_WHITE, "  (/h)angup - padëti telefonà pokalbio metu ir nutraukti pokalbá." );	
    SendClientMessage( playerid, COLOR_FADE1, "  (/p)ickup - atsiliepti á ateinantá skambutá." );	
    SendClientMessage( playerid, COLOR_WHITE, "  /sms [NUMERIS] [TEKSTAS] - paraðyti trumpaja þinutæ á pasirinktà numerá." );
    SendClientMessage( playerid, COLOR_FADE1, "  /turnphone - iðjungti/ájungti telefonà." );	
    SendClientMessage( playerid, COLOR_WHITE, "  /speaker - Ájungti garsiakalbá telefone." );	
    SendClientMessage( playerid, COLOR_FADE1, "  /ucall - komanda skirta taksafonams.." );	
    SendClientMessage( playerid, COLOR_GREEN, "___________________________________________________________________" );		
    return 1;
}
CMD:bizhelp(playerid)
{
    SendClientMessage(playerid,COLOR_GREEN,"|__________________BIZNIO VALDYMO INFORMACIJA__________________|");
	SendClientMessage(playerid,COLOR_WHITE,"  /furniture - komandoje naudojama viduje biznio, su kuria galite pirktis baldus á savo bizná");
	SendClientMessage(playerid,COLOR_FADE1,"  /buybiz - jei esate ðalia parduodamo biznio, su ðia komandà galite já nusipirkti.");	
	SendClientMessage(playerid,COLOR_WHITE,"  /sellbiz [VEIKËJO ID] [KAINA] - galite parduoti savo turimà verslà.");	
	SendClientMessage(playerid,COLOR_FADE1,"  /biz - pagrindinis biznio valdymas, nustatymai ir kt. Komanda veikia prie biznio áëjimo.");
	SendClientMessage(playerid,COLOR_WHITE,"  /cargoprice - naudojama norint pateikti uþsakymà perkant prekes..");		
    SendClientMessage( playerid, COLOR_GREEN, "__________________________________________________________________" );	
    return 1;
}

/*
CMD:leavegun(playerid)
{
    static LastUsed[ MAX_PLAYERS ];

    if(gettime() <= LastUsed[ playerid ])
        return 0;
    LastUsed[ playerid ] = gettime();

	if(Mires[ playerid ] > 0)
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite iðmesti ginklo bûdamas komos bûsenoje.");

    if(!GetPlayerWeapon(playerid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs nelaikote ginklo.");
    if(IsPlayerInAnyVehicle(playerid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite iðmesti ginklo bûdami transporto priemonëje.");

    // MD ir PD negali iðmest.
    if(PlayerFaction(playerid) == 1 || PlayerFaction(playerid) == 2 || IsPlayerWeaponJobWeapon(playerid, GetPlayerWeapon(playerid)))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs negalite iðmesti ginklo.");

    new index = -1;
    for(new i = 0; i < MAX_DROPPED_WEAPONS; i++)
        if(!IsValidDynamicObject(DroppedWeapons[ i ][ ObjectId ]) && !DroppedWeapons[ i ][ DissapearTimer ])
        {
            index = i;
            break;
        }
    if(index == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, pasiektas ant þemës gulinèiø ginklø limitas. Pabandykite vëliau.");


    new Float:x, Float:y, Float:pz, Float:z,
        weaponid = GetPlayerWeapon(playerid);
    CheckWeaponCheat(playerid, weaponid, 0);
    if(!IsPlayerWeaponInDB(playerid, weaponid))
    {
        new s[128];
        format(s, sizeof(s),"ÞAidëjo %d(%s) ginklas %d neregistruotas DB.", playerid, GetName(playerid), weaponid);
        ACTestLog(s);
    }
    GetPlayerPos(playerid, x, y, pz);
    MapAndreas_FindZ_For2DCoord(x, y, z); // Magija

    // Taigi. Jei þmogus po tilto ar pnð, MapAndreas graþins VIRÐ tilto koordinates. 
    // Todël jeigu jis duoda mums koordinates aukðèiau nei þaidëjo Z, atimam ið þaidëjo Z ðiektiek.
    if(pz < z)
        DroppedWeapons[ index ][ ObjectId ] = CreateDynamicObject(GetWeaponObjectModel(weaponid), x, y, pz-1.0, 81.9390, -29.4950, random(360), .worldid = GetPlayerVirtualWorld(playerid), .interiorid = GetPlayerInterior(playerid));
    else 
        DroppedWeapons[ index ][ ObjectId ] = CreateDynamicObject(GetWeaponObjectModel(weaponid), x, y, z, 81.9390, -29.4950, random(360),  .worldid = GetPlayerVirtualWorld(playerid), .interiorid = GetPlayerInterior(playerid));

    DroppedWeapons[ index ][ DissapearTimer ] = SetTimerEx("OnDroppedWeaponDestroyed", DROPPED_WEAPON_DESTROY_DELAY*1000, false, "i", index);
    DroppedWeapons[ index ][ WeaponId ] = weaponid;
    DroppedWeapons[ index ][ Ammo ] = GetPlayerAmmo(playerid);
    DroppedWeapons[ index ][ CanBePickedUp ] = true;

    new string[60];
    GetWeaponName(weaponid, string, sizeof(string));
    format(string, sizeof(string), "iðmeta ginklà kuris atrodo kaip %s", string);
    cmd_ame(playerid, string);
    RemovePlayerWeapon(playerid, weaponid); // Yay jis ir ið DB iðtrins (sun)
    return 1;
}
*/
/*
CMD:grabgun(playerid)
{
	if(Mires[ playerid ] > 0)
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite iðmesti ginklo bûdamas komos bûsenoje.");

    for(new i = 0; i < MAX_DROPPED_WEAPONS; i++)
    {
        if(!DroppedWeapons[ i ][ CanBePickedUp ])
            continue;
        if(!IsPlayerInRangeOfDynamicObject(playerid, 2.0, DroppedWeapons[ i ][ ObjectId ]))
            continue;

        if(IsPlayerInventoryFull(playerid))
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: jûsø inventoriuje nepakanka vietos, atsilaisvinkite ir bandykite dar kart." );
    

        GivePlayerItem(playerid, DroppedWeapons[ i ][ WeaponId ], DroppedWeapons[ i ][ Ammo ]);
        SendClientMessage ( playerid, COLOR_WHITE, " Ginklas sëkmingai ádëtas á inventoriø. ");
        PlayerPlaySound   ( playerid, 1057, 0.0, 0.0, 0.0);
        OnDroppedWeaponDestroyed(i);
        return 1;

    }
    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, prie jûsø nëra jokio ginklo.");
}*/

forward OnDroppedWeaponDestroyed(index);
public OnDroppedWeaponDestroyed(index)
{
    DroppedWeapons[ index ][ CanBePickedUp ] = false;
    DestroyDynamicObject(DroppedWeapons[ index ][ ObjectId ]);
    KillTimer(DroppedWeapons[ index ][ DissapearTimer ]);
    DroppedWeapons[ index ][ ObjectId ] = -1;
    DroppedWeapons[ index ][ DissapearTimer ] = 0;
}

 IsPlayerInRangeOfDynamicObject(playerid, Float:distance, objectid)
{
    if(!IsValidDynamicObject(objectid))
        return false;

    new Float:x, Float:y, Float:z;
    GetDynamicObjectPos(objectid, x, y, z);
    return IsPlayerInRangeOfPoint(playerid, distance, x, y, z);
}


/*
CMD:sumtogether( playerid, params [ ] )
{
    new
        drug [ 15 ],
        drug_id,
        drug_amount,
        drug_curramount,
        drug_hasdrug,
        drug_hasdrug2;

    if( sscanf( params, "s[15]d", drug, drug_amount ) )
    {
        SendClientMessage( playerid, COLOR_WHITE, " /sumtogether [NARKOTIKAS] [KIEKIS]" );
        SendClientMessage ( playerid, COLOR_WHITE, " Galimi narkotikai: Metamfetaminas, Kokainas, Zole, Heroinas, Amfa, Opijus, PCP, Extazy, Krekas" );
    }
        else
    {

        if( drug_amount < 1 ) return true;

        new
            array [ 9 ][ 2 ][ 15 ] =
        {
            { "Metamfetaminas", "87"    },
            { "Amfa", "86"      },
            { "Kokainas", "88"  },
            { "Zole", "61"      },
            { "Heroinas", "64"  },
            { "Opijus", "210"   },
            { "PCP", "208"      },
            { "Extazy", "207"   },
            { "Krekas", "209"   }
        };

        for ( new n = 0; n < 9; n++ )
        {
            if( !strcmp( drug, array [ n ][ 0 ], true ) )
            {
                drug_id = strval( array [ n ][ 1 ] );
                drug_hasdrug = PlayerHasItemInInvEx( playerid, drug_id ),
                drug_hasdrug2 = PlayerHasItemInInvExDrugs( playerid, drug_id, drug_hasdrug );

                if( drug_hasdrug < INVENTORY_SLOTS && drug_hasdrug2 < INVENTORY_SLOTS )
                {
                    drug_curramount = InvInfo [ playerid ][ drug_hasdrug ][ iAmmount ];

                    if( drug_curramount >= drug_amount )
                    {
                        if(drug_curramount == drug_amount)
                        ClearInvSlot ( playerid, drug_hasdrug );
                        else
                        InvInfo [ playerid ][ drug_hasdrug ][ iAmmount ] -= drug_amount;

                        InvInfo [ playerid ][ drug_hasdrug2 ][ iAmmount ] += drug_amount;
                        SendClientMessage( playerid, COLOR_WHITE, "Pasirinktas narkotikas buvo sëkmingai sujungtas." );
                    }
                        else
                    {
                        SendClientMessage ( playerid, COLOR_GRAD, "{FF6347}Klaida, Jûs neturite tiek narkotikø kiek nurodëte" );
                    }
                    }
                        else
                    {
                        SendClientMessage( playerid, COLOR_GRAD, "{FF6347}Klaida, Jûs nurodëte narkotikà, kurio neturite." );
                    }
                }
            }

    }

    return true;
}
*/
/*
CMD:make( playerid, params[ ] )
{
    new item,
        item2,
        string[ 126 ];
    if ( sscanf( params, "dd", item, item2 ) )
    {
        SendClientMessage( playerid ,COLOR_GREEN, " |_____________________/make informacija________________|");        
        SendClientMessage( playerid ,GRAD, "  KOMANDOS NAUDOJIMAS: /make [GAMINAMAS PRODUKTAS][KIEKIS]");
        SendClientMessage( playerid ,GRAD, "  GAMINAMI PRODUKTAI: 1 - Ginklas | 2 - Degusis skystis");
        return 1;
    }
    if ( item2 < 1 ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Gaminamas kiekis negali bøti maþesnis negu 1.");
    switch( item )
    {
        case 1:
        {
            if(pInfo[ playerid ][ pJob ] != JOB_GUN) 
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûs neturite galimybës gamintis ðaunamojo ginklo..");

            if(!IsItemInPlayerInventory(playerid, ITEM_MATS)) 
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto neturëdami atitinkamø daliø.");

            if(IsPlayerHaveManyGuns(playerid, item2)) 
                return true;

            new bool:pasigamino = true,
                tikimybe = random( 100 );
            switch( tikimybe )
            {
                case 20..30: pasigamino = false;
                case 60..70: pasigamino = false;
                case 90..100: pasigamino = false;
            }

            switch( item2 )
            {
                case 23:
                {
                    if(GetPlayerItemAmount(playerid, ITEM_MATS) < 150 )
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto neturëdami atitinkamø daliø.");

                    if ( pasigamino == false )
                    {
                        SendClientMessage(playerid, COLOR_RED, "Klaida, ginklo pagaminimas buvo atðauktas, kadangi dalys ir netinkamos." );
                        GivePlayerItem(playerid, ITEM_MATS, -75);
                        PlayerPlaySound(playerid, 34042, 0.0, 0.0, 0.0);
                        return 1;
                    }
                    GivePlayerItem(playerid, ITEM_MATS, -150);

                    GivePlayerWeapon( playerid, 23, 64 ); // Silenced
                }
                case 24:
                {
                    if(GetPlayerItemAmount(playerid, ITEM_MATS) < 200 )
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto neturëdami atitinkamø daliø.");
                    if ( pasigamino == false )
                    {
                        SendClientMessage( playerid, COLOR_RED, "Klaida, ginklo pagaminimas buvo atðauktas, kadangi dalys ir netinkamos." );
                        GivePlayerItem(playerid, ITEM_MATS, -100);
                        PlayerPlaySound( playerid, 34042, 0.0, 0.0, 0.0);
                        return 1;
                    }
                    GivePlayerItem(playerid, ITEM_MATS, -200);

                    GivePlayerWeapon( playerid, 24, 70 ); // Deagle
                }
                case 25:
                {
                    if(GetPlayerItemAmount(playerid, ITEM_MATS) < 400 )
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto neturëdami atitinkamø daliø.");
                    if ( pasigamino == false )
                    {
                        SendClientMessage( playerid, COLOR_RED, "Klaida, ginklo pagaminimas buvo atðauktas, kadangi dalys ir netinkamos." );
                        GivePlayerItem(playerid, ITEM_MATS, -200);
                        PlayerPlaySound( playerid, 34042, 0.0, 0.0, 0.0);
                        return 1;
                    }
                    GivePlayerItem(playerid, ITEM_MATS, -400);

                    GivePlayerWeapon( playerid, 25, 50 ); //  Shotgun
                }
                case 28:
                {
                    if(GetPlayerItemAmount(playerid, ITEM_MATS) < 350 )
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto neturëdami atitinkamø daliø.");
                    if( pasigamino == false )
                    {
                        SendClientMessage( playerid, COLOR_RED, "Klaida, ginklo pagaminimas buvo atðauktas, kadangi dalys ir netinkamos." );
                        GivePlayerItem(playerid, ITEM_MATS, -175);
                        PlayerPlaySound( playerid, 34042, 0.0, 0.0, 0.0);
                        return 1;
                    }
                    GivePlayerItem(playerid, ITEM_MATS, -350);

                    GivePlayerWeapon( playerid, 28, 300 ); // UZI
                }
                case 29:
                {
                    if (GetPlayerItemAmount(playerid, ITEM_MATS) < 500 )
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto neturëdami atitinkamø daliø.");
                    if ( pasigamino == false )
                    {
                        SendClientMessage( playerid, COLOR_RED, "Klaida, ginklo pagaminimas buvo atðauktas, kadangi dalys ir netinkamos." );
                        GivePlayerItem(playerid, ITEM_MATS, -250);
                        PlayerPlaySound( playerid, 34042, 0.0, 0.0, 0.0);
                        return 1;
                    }
                    GivePlayerItem(playerid, ITEM_MATS, -500);

                    GivePlayerWeapon( playerid, 29, 300 ); // MP5
                }
                case 30:
                {
                    if (GetPlayerItemAmount(playerid, ITEM_MATS) < 700 )
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto neturëdami atitinkamø daliø.");
                    if ( pasigamino == false )
                    {
                        SendClientMessage( playerid, COLOR_RED, "Klaida, ginklo pagaminimas buvo atðauktas, kadangi dalys ir netinkamos." );
                        GivePlayerItem(playerid, ITEM_MATS, -350);
                        PlayerPlaySound( playerid, 34042, 0.0, 0.0, 0.0);
                        return 1;
                    }
                    GivePlayerItem(playerid, ITEM_MATS, -700);

                    GivePlayerWeapon( playerid, 30, 300 ); // AK-47
                }
                case 32:
                {
                    if(GetPlayerItemAmount(playerid, ITEM_MATS) < 400 )
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto neturëdami atitinkamø daliø.");
                    if ( pasigamino == false )
                    {
                        SendClientMessage( playerid, COLOR_RED, "Klaida, ginklo pagaminimas buvo atðauktas, kadangi dalys ir netinkamos." );
                        GivePlayerItem(playerid, ITEM_MATS, -200);
                        PlayerPlaySound( playerid, 34042, 0.0, 0.0, 0.0);
                        return 1;
                    }
                    GivePlayerItem(playerid, ITEM_MATS, -400);

                    GivePlayerWeapon( playerid, 32, 300 ); // Tec-9
                }
                case 33:
                {
                    if (GetPlayerItemAmount(playerid, ITEM_MATS) < 700 )
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto neturëdami atitinkamø daliø.");
                    if ( pasigamino == false )
                    {
                        SendClientMessage( playerid, COLOR_RED, "Klaida, ginklo pagaminimas buvo atðauktas, kadangi dalys ir netinkamos." );
                        GivePlayerItem(playerid, ITEM_MATS, -350);
                        PlayerPlaySound( playerid, 34042, 0.0, 0.0, 0.0);
                        return 1;
                    }
                    GivePlayerItem(playerid, ITEM_MATS, -700);

                    GivePlayerWeapon( playerid, 33, 20 ); // Country Rifle
                }
                case 34:
                {
                    if (GetPlayerItemAmount(playerid, ITEM_MATS) < 1500 )
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto neturëdami atitinkamø daliø.");
                    if ( pasigamino == false )
                    {
                        SendClientMessage( playerid, COLOR_RED, "Klaida, ginklo pagaminimas buvo atðauktas, kadangi dalys ir netinkamos." );
                        GivePlayerItem(playerid, ITEM_MATS, -750);
                        PlayerPlaySound( playerid, 34042, 0.0, 0.0, 0.0);
                        return 1;
                    }
                    GivePlayerItem(playerid, ITEM_MATS, -1500);
                    GivePlayerWeapon( playerid, 34, 20 ); // Sniper
                }
                default: return
                SendClientMessage( playerid, COLOR_GRAD, "{FF6347} Nurodytas klaidingas ginklo ID.");
            }
            new wepname[ MAX_PLAYER_NAME ];
            GetWeaponName    ( item2, wepname, MAX_PLAYER_NAME );
            format           ( string, 43, "Sveikinome, Jums sëkmingai pavyko pasigaminti %s", wepname );
            SendClientMessage( playerid, COLOR_WHITE, string       );
        }
        case 2:
            return SendClientMessage(playerid, -1, "Komanda nenaudojama. Naudokite /makemolotov");
        

        
    }
    return 1;
}
*/

/*
CMD:buymats( playerid, params[ ] )
{
    if(!Data_IsPlayerInRangeOfCoords(playerid, 5.0, "job_dealer_material_buy")) 
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs nesate paketø pirkimo vietoje. " );
    if(pInfo[ playerid ][ pJob ] != JOB_GUN ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs nesate ginklø prekeivis. " );
    new mat,
        string[ 70 ];

    if(sscanf( params, "d", mat) ) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /buymats [kiekis]");
    if(Mats < mat ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: ðiuo metu tiek materijø neturime, bandykite vëliau.");
    if(GetPlayerMoney(playerid) < mat * 5 ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: neturite pakankamai pinigø.");
    if(mat < 0 ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Negalima pirkti maþiau negu 0 ");

    if(IsPlayerInventoryFull(playerid))
        return SendClientMessage(playerid, COLOR_FADE2, "{FF6347}Perspëjimas: jûsø inventoriuje nepakanka vietos, atsilaisvinkite ir bandykite dar kart.");

    GivePlayerItem(playerid, ITEM_MATS, mat);
    GivePlayerMoney( playerid, - mat * 2 );
    Mats -= mat;
    format          ( string, 70, " Nusipirkai %d paketø, bûk atsargus kad policija nepagautu. ", mat );
	SendClientMessage( playerid, COLOR_WHITE, string );
    SaveAccount( playerid );
    return 1;
}
CMD:weaponlist( playerid, params[ ] )
{
    #pragma unused params
	SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /make [2][GINKLO ID]");
	SendClientMessage( playerid ,GRAD, "GINKLAI: ID:23 (9mm su duslintuvu, 150 kulkø) | ID:24 (Desert Eagle,200 kulkø)");
	SendClientMessage( playerid ,GRAD, "GINKLAI: ID:25 (Shotgun, 400 kulkø) | ID:28 (UZI, 350 kulkø) | ID:29 (MP5, 500kulkø)");
	SendClientMessage( playerid ,GRAD, "GINKLAI: ID:30 (AK-47, 700 kulkø)");
	SendClientMessage( playerid ,GRAD, "GINKLAI: ID:32 (Tec9, 400 kulkø) | ID:33 (Rifle 1300 kulkø) | ID:34 (Sniper,1500 kulkø)");
    return 1;
}
*/

/*
CMD:nof( playerid, params[ ] )
{
    #pragma unused params
    if ( pInfo[ playerid ][ pMember ] > 1 && pInfo[playerid][pLead] == pInfo[ playerid ][ pMember ] )
    {
        new string[ 126 ];
        if ( fInfo[ PlayerFaction( playerid ) ][ fChat ] == 0 )
        {
            format         ( string, 126, " %s %s iðjungë privatø frakcijos kanalà (/f). " ,GetPlayerRangName( playerid ), GetName( playerid ) );
            fInfo[ PlayerFaction( playerid ) ][ fChat ] = 1;
            SendTeamMessage( PlayerFaction( playerid ), COLOR_NEWS, string );
            return 1;
        }
        else
        {
            format         ( string, 126, " %s %s ájungë privatø frakcijos kanalà (/f). " ,GetPlayerRangName( playerid ), GetName( playerid ) );
            fInfo[ PlayerFaction( playerid ) ][ fChat ] = 0;
            SendTeamMessage( PlayerFaction( playerid ), COLOR_NEWS, string );
            return 1;
        }
    }
    return 1;
}

*/

/*
CMD:setswat( playerid, params[ ] )
{
	new giveplayerid,
		type;
	if ( sscanf( params, "ud", giveplayerid, type ) )
		return SendClientMessage( playerid ,COLOR_LIGHTRED, "KOMANDOS NAUDOJIMAS: /setswat [VEIKËJO ID][1-3]"), SendClientMessage( playerid ,COLOR_LIGHTRED, "BÛRIAI: 1 - Marksman | 2 - Elite | 3 - Enforcer");
	if( UsePDCMD(playerid) != 1)
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti ðio veiksmo nedirbdami policijos departamente.");
    if ( pInfo[playerid][pRank] < 10 )
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðià komandà gali naudoti tik aukðto rango pareigûnai.");
	if ( !PlayerToPlayer( 10.0, playerid, giveplayerid ) )
		return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjas nëra ðalia Jûsø.");
 	if( !PDJOBPlace(playerid)) return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, norëdami atlikti ðá veiksmà, privalote bûti policijos departamente");
	{
		if ( type == 1 )
		{
			SetPlayerSkin   ( giveplayerid, 285 );
			SetPlayerArmour(playerid, 150.0);
			GivePlayerJobWeapon(giveplayerid, 34, 20 );
			GivePlayerJobWeapon(giveplayerid, 29, 200 );			
			SetPVarInt      (giveplayerid, "PDTYPE", 1 );
			return 1;
		}
		else if ( type == 2 )
		{
			SetPlayerSkin   ( giveplayerid, 285 );		
			SetPlayerArmour(playerid, 170.0);		
			GivePlayerJobWeapon( giveplayerid, 31, 200 );
			GivePlayerJobWeapon( giveplayerid, 24, 150 );
			SetPVarInt      ( giveplayerid, "PDTYPE", 2 );
			return 1;
		}
		else if ( type == 3 )
		{
			SetPlayerSkin   ( giveplayerid, 285 );		
			SetPlayerArmour(playerid, 200.0);			
			GivePlayerJobWeapon( giveplayerid, 25, 40 );
			GivePlayerJobWeapon( giveplayerid, 29, 200 );
			SetPVarInt      ( giveplayerid, "PDTYPE", 3 );
			return 1;
		}		
	}
	return 1;
}
*/


CMD:crouch( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "CAMERA", "camcrch_idleloop", 4.0, 0, 1, 1, 1, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}

CMD:yes( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "GANGS", "Invite_Yes", 4.0, 0, 1, 1, 1, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}

CMD:no( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "GANGS", "Invite_No", 4.0, 0, 1, 1, 1, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}

CMD:chand( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_DRIVER )
        LoopingAnim( playerid, "CAR", "Tap_hand", 4.0, 0, 1, 1, 1, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra tr. priemonëje. ");

    return true;
}

CMD:bag( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "BASEBALL", "Bat_IDLE", 4.0, 1, 1, 1, 1, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}

CMD:riot( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "RIOT", "RIOT_ANGRY", 4.0, 1, 0, 0, 0, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}

CMD:place( playerid, params[ ] )
{
    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
    {
        new
            id;

        if ( sscanf( params, "d", id ) ) SendClientMessage( playerid, COLOR_GREY, "/place [1-2]" );
        else
        {
            switch ( id )
            {
                case 1 : LoopingAnim( playerid, "CARRY", "putdwn", 4.0, 0, 1, 1, 1, 0 );
                case 2 : LoopingAnim( playerid, "CARRY", "putdwn05", 4.0, 0, 1, 1, 1, 0 );
                default: SendClientMessage( playerid, COLOR_GREY, "/place [1-2]" );
            }
        }
    }
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}

CMD:lift( playerid, params[ ] )
{
    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
    {
        new
            id;

        if ( sscanf( params, "d", id ) ) SendClientMessage( playerid, COLOR_GREY, "/lift [1-2]" );
        else
        {
            switch ( id )
            {
                case 1 : LoopingAnim( playerid, "CARRY", "liftup", 4.0, 0, 1, 1, 1, 0 );
                case 2 : LoopingAnim( playerid, "CARRY", "liftup05", 4.0, 0, 1, 1, 1, 0 );
                default: SendClientMessage( playerid, COLOR_GREY, "/lift [1-2]" );
            }
        }
    }
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");
    return true;
}

CMD:rem( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "MUSCULAR", "MuscleIdle", 4.0, 0, 1, 1, 1, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}




CMD:towup( playerid, params[ ] )
{
    if ( GetPlayerState( playerid ) != PLAYER_STATE_DRIVER ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: turite bûti automobilyje Town Truck. " );
    new veh = GetPlayerVehicleID( playerid ),
        veh2;
    if ( GetVehicleModel( veh ) != 525 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: turite bûti automobilyje Town Truck. " );
    if ( sscanf ( params, "d", veh2 ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /towup [Automobilio ID matomas /dl] ");
    new Float: Car_X,
        Float: Car_Y,
        Float: Car_Z;
    GetVehiclePos( veh2, Car_X, Car_Y, Car_Z );
    if ( !PlayerToPoint( 10, playerid, Car_X, Car_Y, Car_Z ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs nesate ðalia to automobilio ");

    if ( IsTrailerAttachedToVehicle( veh ) )
        DetachTrailerFromVehicle( veh );
    else
    {
        StartTimer( playerid, 15, 10);
        SetPVarInt( playerid, "TOWING", veh2 );
    }
    return 1;
}


CMD:levelup(playerid,params[])
{
    if(!pInfo[ playerid ][ pPoints ])
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite laisvø taðkø prisidëti prie veikëjo savybiø..");

    new type;
    if(sscanf(params,"i",type) || (type != 1 && type != 2)) 
    {
        SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /levelup [1/2]");
        SendClientMessage(playerid, COLOR_LIGHTRED, "Pasirinkimas: Papildomos gyvybës - 1, papildoma jëga - 2");
        return 1;
    }

    new string[70];
    switch(type)
    {
        case 1: 
        {
            pInfo[ playerid ][ pHealthLevel ]++;
            format(string,sizeof(string), "[LevelUp] Sëkmingai pasikëlëtæ veikëjo gyvybiø skaièiø. Dabar Jûsø veikëjas turës %.2f gyvybes.",pInfo[ playerid ][ pHealthLevel ] * 3 + 100.0);
            SendClientMessage(playerid, COLOR_NEWS, string);
        }
        case 2:
        {
            pInfo[ playerid ][ pStrengthLevel ] ++;
            SendClientMessage(playerid, COLOR_NEWS, "[LevelUp] Sëkmingai pasikëlëtæ veikëjo fizinæ jëgà. Daugiau informacijos komandoje /stats.");
        }
    }
    pInfo[ playerid ][ pPoints ]--;
    SaveAccount(playerid);

    format(string,sizeof(string),"Jums liko %d taðkai.", pInfo[ playerid ][ pPoints ]);
    SendClientMessage(playerid, COLOR_NEWS, string);
    return 1;
}
/*
CMD:o( playerid, params[ ] )
{
    new string[ 256 ];
    if ( sscanf( params, "s[256]", string ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /o [tekstas]" );
    if ( Mute[ playerid ] == true ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, ðiuo metu Jums yra uþdrausta kalbëtis (/mute), norëdami paðalinti draudimà susisiekite su Administratoriumi." );
    if ( OOCDisabled == false ) return SendClientMessage( playerid, GRAD, "OOC kanalas yra uþdraustas administratoriaus." );
    format ( string, 256,"(( %s[%d] sako: %s ))", GetName( playerid ), playerid, string );
    SendOOC( COLOR_OOC, string );
    return 1;
}
CMD:ao( playerid, params[ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 1)
    {
        new string[ 256 ];

        if ( sscanf( params, "s[256]", string ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /ao [tekstas]" );
        format ( string, 256, "(( Adm %s[%d]: %s ))", GetName( playerid ), playerid, string );
        SendOOC( COLOR_OOC, string );
        return 1;
    }
    return 1;
}
*/
/*
CMD:ado( playerid, params[ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 1)
    {
        new string[ 256 ];

        if ( sscanf( params, "s[256]", string ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /ado [tekstas]" );
        format ( string, 256, "%s", string);
        SendOOC( COLOR_NEWS, string );
        return 1;
    }
    return 1;
}
*/

 checkVehicleByNumbers( numbers[ ] )
{
    foreach(Vehicles,i)
    {
        if ( !strcmp( numbers, cInfo[ i ][ cNumbers ], true ) ) return i;
    }
    return INVALID_VEHICLE_ID;
}


CMD:buygun( playerid, params[ ] )
{
    #pragma unused params
    if(!PlayerToPoint(10.0,playerid,296.7012,-37.4115,1001.5156)) return 1;
    ShowPlayerDialog(playerid,6,DIALOG_STYLE_LIST ,"Ammu-nation parduotuvë","\
      1. Kastetas \t150$\
    \n2. Profesonali golfo lazda \t498$\
    \n3. Kiðeninis peilis \t89$\
    \n4. Medinë beisbolo lazda \t91$\
    \n5. Kastuvas \t75$\
    \n6. Bilijardo lazda \t344$\
    \n7. Paprasta lazda rankai \t43$\
	\n8. Daþø balionëlis (80) \t110$\
	\n9. Japoniðkas kalavijas - katana \t720$","Pirkti","Atðaukti");
    return 1;
}



CMD:pickuptrash( playerid, params[ ] )
{
    #pragma unused params
    for( new i = 0; i < 12; i++ )
    {
        if( IsValidDynamicObject( RandBus[ i ][ objectas ] ) && IsPlayerInRangeOfPoint(playerid, 3.0, RandBus[ i ][ X4 ], RandBus[ i ][ Y4 ], RandBus[ i ][ Z4 ] ) )
        {
            DestroyDynamicObject( RandBus[ i ][ objectas ] );
            ApplyAnimation( playerid, "CARRY", "liftup", 4.0, 0, 1, 0, 0, 0 );
            SetPVarInt( playerid, "Tipas2", true );
            SetPlayerSpecialAction(playerid,SPECIAL_ACTION_CARRY);
        }
    }
    return 1;
}
CMD:placetrash( playerid, params[ ] )
{
    #pragma unused params
    new idcar = INVALID_VEHICLE_ID,
        string[54];
    if(GetPlayerState(playerid) == PLAYER_STATE_DRIVER)
        idcar = GetPlayerVehicleID( playerid );
    else
        idcar = GetNearestVehicle( playerid, 5.0 );
    if ( idcar == INVALID_VEHICLE_ID ) return SendClientMessage( playerid, GRAD, "Tai kad aplink tave nëra jokio automobilio...");
    if ( GetVehicleModel(idcar) == 408 && IsPlayerAttachedObjectSlotUsed(playerid, 7) )
    {
        ApplyAnimation(playerid,"CARRY","putdwn", 4.0, 0, 1, 0, 0, 0 );
        SetPlayerSpecialAction(playerid,SPECIAL_ACTION_NONE);
        RemovePlayerAttachedObject(playerid, 7);
        idcar = random(50)+1;
        pInfo[playerid][pPayCheck] += idcar;
        AddJobExp( playerid, 1 );
        format(string, 54, "~g~%d$ ~w~prideda prie jusu atlyginimo.",idcar);
        ShowInfoText(playerid, string, 5000);
    }
    return 1;
}
/*
CMD:giverec( playerid, params[ ] )
{
    new giveplayerid,
        string[ 126 ];

    if ( sscanf( params, "u", giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /giverec [veikëjo id]");
    if ( !PlayerToPlayer( 5.0, playerid, giveplayerid ) )  return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs nesate ðalia veikëjo. ");
    if ( PlayerFaction( playerid ) == 2 )
    {
        if(IsPlayerInventoryFull(playerid))
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Nepakanka vietos jo inventoriuje" );
       // GivePlayerItem(giveplayerid, ITEM_MEDLIC, 1); 
        GivePlayerBasicItem(giveplayerid, ITEM_MEDLIC, 1, 35, 0);
        format      (string, 126, "* %s iðraðo vaistø receptá  ir paduoda %s ", GetPlayerNameEx( playerid ), GetPlayerNameEx( giveplayerid ) );
        ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
        return 1;
    }
    return 1;
}*/


CMD:tpda(playerid)
{
    if ( pInfo[ playerid ][ pJob ] != JOB_TRUCKER )
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti ðios komandos nedirbdami kroviniø perveþimø vairuotoju.");
    if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, komanda galite naudoti tik sedëdami tr. priemonës vairuotojo vietoje");
    if(!IsVehicleTrucker(GetVehicleModel(GetPlayerVehicleID(playerid))))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ði tr. priemonë negalima kroviniø perveþimams.");

    ShowTPDA(playerid);
    return 1;
}


CMD:cargo(playerid, params[])
{
    if ( pInfo[ playerid ][ pJob ] != JOB_TRUCKER )
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti ðios komandos nedirbdami kroviniø perveþimø vairuotoju.");
    
    if(isnull(params) || strfind(params," ") != -1)
    {
        cargo_help:
        SendClientMessage(playerid, COLOR_LIGHTRED, "__________________________Kroviniø valdymas ir komandos__________________________");
        SendClientMessage(playerid,COLOR_LIGHTRED2, "  TEISINGAS KOMANDOS NAUDOJIMAS: /cargo [KOMANDA], pavyzdþiui: /cargo list");
        SendClientMessage(playerid,GRAD,"  PAGRINDINËS KOMANDOS: list, place, fork, unfork, putdown, pickup, buy, sell");
        SendClientMessage(playerid,GRAD,"  KITOS KOMANDOS: /trailer - priekabø valdymas");		
        return 1;
    }
    // Sàraðas turimo krovinio
    if(!strcmp(params, "list",true))
    {
        if(IsPlayerInAnyVehicle(playerid))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, turite bûti prie savo sunkveþimio norëdami atlikti ðá veiksmà.");

        new vehicleid = GetNearestVehicle( playerid, 5.0 );
        if(vehicleid == INVALID_VEHICLE_ID)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðalia Jûsø nëra jokios tr. priemonës.");
        new model = GetVehicleModel(vehicleid);
        if(!IsVehicleTrucker(model) && !IsVehicleTrailer(model))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ði tr. priemonë yra tuðèia.");
        if(cInfo[ vehicleid ][ cLock ])
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðalia Jûsø esanti tr. priemonë yra uþrakinta. Atrakinkite ir bandykite dar kartà.");
        
        ShowVehicleCargo(playerid, vehicleid);
        return 1;
    }
    if(!strcmp("buy",params,true))
    {
        new string[512];
        // Jei ne prie industrijos, nëra kà pikrt.
        if(!IsPlayerInRangeOfAnyIndustry(playerid, 4.0))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, norëdami pirkti prekes turite bûti/stovëti ðalia kompanijos, kuri parduoda juos.");

        new index = GetPlayerIndustryInRange(playerid, 4.0);
        foreach(CommodityIterator, i)
        {
            if(Commodities[ i ][ IndustryId ] == Industries[ index ][ Id ] 
                && Commodities[ i ][ SellBuyStatus ] == Selling
                && !Commodities[ i ][ IsBusinessCommodity ])
                format(string,sizeof(string),"%s%s\n",string, GetCargoName(Commodities[ i ][ CargoId ]));
            SetPVarInt(playerid, "Industry_Index", index);
            ShowPlayerDialog( playerid, DIALOG_SOLD_COMMODITY_LIST, DIALOG_STYLE_LIST, Industries[ index ][ Name ], string, "Pirkti", "Atgal" );
        }
        return 1;
    }
    if(!strcmp(params, "sell", true))
    {
        new cargoid = GetPVarInt(playerid, "CargoId"),
            string[1024],
            bool:sellToBusines,
            bool:sellToIndustry,
            bool:sellToShip;

        new index;
        foreach(IndustryIterator, i)
        {
            if(!IsPlayerInRangeOfPoint(playerid, 3.0, Industries[ i ][ PosX ], Industries[ i ][ PosY ], Industries[ i ][ PosZ ])) continue;
            if(!IsIndustryAcceptingCargo(i, cargoid) && cargoid)
            {
                sellToIndustry = false;
                break;
            }
            sellToIndustry = true;
            index = i;
            break;
        }
/*        new bizindex;
        if((bizindex = GetPlayerBusinessIndex(playerid)) != -1)
        {
            if(IsBusinessAcceptingCargo(bizindex, cargoid) && cargoid)
            {
                sellToBusines = true;
            }
        }
*/
        if(IsPlayerInRangeOfCargoShip(playerid,4.0))
            sellToShip = true;

        /// Jei þaidëjas laiko dëþæ su prekëm
        if(cargoid)
        {
            if(!sellToBusines && !sellToIndustry && !sellToShip)
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, turite bûti ðalia kompanijos arba verslo arba ði kompanija/verslas neperka jokiø prekiø.");

            new price;
            if(sellToIndustry)
            {
                if(!Industries[ index ][ IsBuyingCargo ])
                    return SendClientMessage(playerid, GRAD, "Klaida, dël produktø pertekliaus ði industrija nedirba.");
                if(!HasIndustryRoomForCargo(index, cargoid))
                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, industrija ðiai prekei nebeturi vietos.");
                // Jei parduoda ta krovini kuri pirko ten pat.
                if(IsIndustrySellingCargo(index, cargoid))
                    price = Commodities[ GetIndustryCargoIndex(index, cargoid) ][ Price ];
                else 
                    price = GetCommoditySellPrice(GetIndustryCargoIndex(index,cargoid));
                AddCargoToIndustry(index, cargoid);
            }
         /*   else if(sellToBusines)
            {
                price = Commodities[ GetBusinessCargoIndex(index, cargoid) ][ Price ];
                if(price > bInfo[ index ][ bBank ])
                {
                    SendClientMessage(playerid, COLOR_LIGHTRED, "Verslas nebeturi pakankamai lëðø, kad nupirktu ðià prekæ. Susisiekite su savininku.");
                    // reik surast savininkà ir pasakyk kad kapeikos baigës :(
                    foreach(Player,i)
                        if(pInfo[ i ][ pMySQLID ] == bInfo[ index ][ bOwner ])
                        {
                            SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio, Jûsø verslo banke pasibaigë pinigai, visi prekiø pirkimai buvo atðaukti/nutraukti.");
                            StopBusinessBuyingCargo(index, cargoid);
                            break;
                        }
                }
                bInfo[ index ][ bBank ] -= price;
                AddCargoToBusiness(index, cargoid);
                
                // Jei verslas pasiekë limità, jis daugiau pirkti nebegali.
                if(bInfo[ index ][ bProducts ] >= MAX_BUSINESS_PRODUCTS)
                    StopBusinessBuyingCargo(index, cargoid);
            }
            */
            else if(sellToShip)
            {
                if(ShipInfo[ Status ] != Docked)
                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, laivas ðiuo metu yra iðplaukæs. Naudokite komanda /tpda daugiau informacijos.");
                
                price = GetShipCargoPrice(cargoid);
                ShipInfo[ CurrentStock ] += GetCargoSlot(cargoid);
            }
              
            GivePlayerMoney(playerid, price);
            DeletePVar(playerid, "CargoId");
            ApplyAnimation(playerid,"CARRY","putdwn", 4.0, 0, 1, 0, 0, 0 );
            RemovePlayerAttachedObject(playerid, 7);
            SetPlayerSpecialAction(playerid,SPECIAL_ACTION_NONE);
            format(string,sizeof(string)," Jûsø veþamà kroviná/prekes pavadinimu:  {97cd17}%s{FFFFFF}, sëkmingai nupirko uþ{97cd17}%d${FFFFFF}",GetCargoName(cargoid), price);
            SendClientMessage(playerid, COLOR_WHITE, string);
        }
        // Jei þaidëjas nelaiko prekës, bandom ieðkot transporto piremoniø.
        else 
        {
            if(!sellToBusines && !sellToIndustry && !sellToShip)
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûs neesate prie kompanijos arba verslo arba jie neperka ðios prekës.");
            
            new vehicleid = GetNearestVehicle(playerid, 5.0);
        
            
            if(IsTrailerAttachedToVehicle(vehicleid))
                vehicleid = GetVehicleTrailer(vehicleid);

            if(!CanPlayerUseTruckerVehicle(playerid,GetVehicleModel(vehicleid)))
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio, neturite pakankamai patirties taðkø, kad galëtumëte veþti krovinius su ðia tr. priemone.");

            // Jei maðina yra ðalia, ir tai fûristø transporto priemonë ARBA priekaba.
            if(vehicleid != INVALID_VEHICLE_ID
                && (IsVehicleTrucker(GetVehicleModel(vehicleid)) || IsVehicleTrailer(GetVehicleModel(vehicleid))))
            {
                if(cInfo[ vehicleid ][ cLock ])
                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ði tr. priemonë yra uþrakinta.");
                new cargocount = 0;
                for(new i = 0; i < sizeof VehicleCargo[]; i++)
                {
                    if(!VehicleCargo[ vehicleid ][ i ][ Amount ])
                        continue;
//                    if(sellToBusines && !IsBusinessAcceptingCargo(index, VehicleCargo[ vehicleid ][ i ][ CargoId ]))
 //                       continue;
                    else if(sellToIndustry && !IsIndustryAcceptingCargo(index, VehicleCargo[ vehicleid ][ i ][ CargoId ] ))
                        continue;
                    else if(sellToShip && !IsShipAcceptingCargo(VehicleCargo[ vehicleid ][ i ][ CargoId ]))
                        continue;
                        
                    cargocount++;   
                    format(string,sizeof(string),"%s%d\t%s\t%d vienetai\n",
                        string,
                        VehicleCargo[ vehicleid ][ i ][ CargoId ],
                        GetCargoName(VehicleCargo[ vehicleid ][ i ][ CargoId ]),
                        VehicleCargo[ vehicleid ][ i ][ Amount ]);
                }
                if(!cargocount)
                    ShowPlayerDialog(playerid, 9999, DIALOG_STYLE_MSGBOX, "Prekiø pardavimas","Jûsø automobilyje nëra prekiø kurias perka ði kompanija.","Gerai","");
                else 
                {
                    SetPVarInt(playerid, "IndustryIndex", index);
                    SetPVarInt(playerid, "vehicleid", vehicleid);
                    if(sellToBusines)
                        SetPVarInt(playerid, "CommoditySellTo",1);
                    else if(sellToIndustry)
                        SetPVarInt(playerid, "CommoditySellTo",2);
                    else if(sellToShip)
                        SetPVarInt(playerid, "CommoditySellTo",3);
                    ShowPlayerDialog(playerid, DIALOG_COMMODITY_SELL, DIALOG_STYLE_LIST, "Prekiø pardavimas",string, "Parduoti", "Iðeiti");
                }
            }
            else 
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûs neturite rankoje prekiø arba ðalia Jûsø nëra tr. priemonës su prekëmis.");
        }   
        DeletePVar(playerid, "CargoId");
        return 1;
    }
    if(!strcmp(params, "fork", true))
    {
        if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER) 
            return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, privalotë sedëti savo tr. priemonëje.");
        
        new vehicleid = GetPlayerVehicleID( playerid );
        if( GetVehicleModel( vehicleid ) != 530 )
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, komanda /fork galima tik sëdint specialioje tam skirtoje tr. priemonëje.");

        vehicleid = GetClosestVehicleToVehicle(vehicleid, 8.0);
        if(vehicleid == INVALID_VEHICLE_ID)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðalia Jûsø nëra jokios tr. priemonës.");
        if(!CanPlayerUseTruckerVehicle(playerid,GetVehicleModel(GetPlayerVehicleID(playerid))))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio, neturite pakankamai patirties taðkø, kad galëtumëte veþti krovinius su ðia tr. priemone.");
        if(cInfo[ vehicleid ][ cLock ])
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðalia esanti tr. priemonë yra uþrakinta.");
        ShowVehicleCargo( playerid, vehicleid);
        return 1;
    }
    if(!strcmp(params, "unfork", true))
    {
        new vehicleid = GetPlayerVehicleID( playerid );
        if( GetVehicleModel( vehicleid ) != 530 )
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, komanda /fork galima tik sëdint specialioje tam skirtoje tr. priemonëje.");
        vehicleid = GetClosestVehicleToVehicle(vehicleid, 8.0);
        if(vehicleid == INVALID_VEHICLE_ID)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðalia Jûsø nëra jokios tr. priemonës.");
        if(cInfo[ vehicleid ][ cLock ])
            return SendClientMessage(playerid,  COLOR_LIGHTRED, "Klaida, ðalia esanti tr. priemonë yra uþrakinta.");
        if(!CanPlayerUseTruckerVehicle(playerid,GetVehicleModel(vehicleid)))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio, neturite pakankamai patirties taðkø, kad galëtumëte veþti krovinius su ðia tr. priemone.");
        new cargoid;
        for(new i = 0; i < sizeof VehicleCargo[]; i++)
        {
            if(!VehicleCargo[ vehicleid ][ i ][ Amount ])
                continue;
            cargoid = VehicleCargo[ vehicleid ][ i ][ CargoId ];
            break;
        }
        if(!cargoid)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûsø transporto priemonëje nëra jokio krovinio!");
        if(!HasVehicleSpaceForCargo(vehicleid, cargoid))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Ðioje transporto primonëje nebëra vietos!");
        
        RemoveCargoFromVehicle(GetPlayerVehicleID(playerid), cargoid);
        AddCargoToVehicle(vehicleid, cargoid);
        SendClientMessage(playerid, COLOR_WHITE, " Sveikiname,  {97cd17}sëkmingai {FFFFFF} pakrovëtæ tr. priemonæ prekëmis.");
        return 1;
    }
    if(!strcmp(params, "putdown", true))
    {
        new slotid = GetFreeBoxSlot( ),
            cargoid = GetPVarInt(playerid, "CargoId");
        if(slotid == -1)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite padëti ðios deþës, kadangi serveryje negali bûtø daugiau nei " #MAX_BOXES " dëþiø");
 
        if(!cargoid)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite padëti dëþës, kurios nelaikote rankoje.");

        if ( slotid < MAX_BOXES)
        {
            new Float:x, Float:y, Float:z;
            GetPlayerPos        ( playerid, x, y, z);
            GetXYInFrontOfPlayer( playerid, x, y, 1.0 );
            CargoBox[ slotid ][ CargoId ] = cargoid;
            CargoBox[ slotid ][ ObjectId ] = CreateDynamicObject( 2912, x, y, z-1, 0, 0, 0 );
            CargoBox[ slotid ][ DissapearTimer ] = SetTimerEx("OnCargoBoxDestroy",5*60*1000, false, "i", slotid);
            CargoBox[ slotid ][ CanBePickedUp ] = true;
            ApplyAnimation(playerid,"CARRY","putdwn", 4.0, 0, 1, 0, 0, 0 );
            RemovePlayerAttachedObject(playerid, 7);
            SetPlayerSpecialAction(playerid,SPECIAL_ACTION_NONE);
            DeletePVar(playerid, "CargoId");
        }
        return 1;
    }
    if(!strcmp(params, "pickup", true))
    {
        new cargoid = GetPVarInt(playerid, "CargoId"),
            Float:x,Float:y, Float:z, string[64];
        if(cargoid)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite paiimti dar vienos dëþës, kada rankose jau kaþkà laikote.");

        for(new i = 0; i < MAX_BOXES; i++)
        {
            GetDynamicObjectPos(CargoBox[ i ][ ObjectId ], x, y, z);
            if (IsValidDynamicObject( CargoBox[ i ][ ObjectId ] ) && IsPlayerInRangeOfPoint(playerid, 3.0, x, y, z) && CargoBox[ i ][ CanBePickedUp ])
            {
                CargoBox[ i ][ CanBePickedUp ] = false;
                DestroyDynamicObject( CargoBox[ i ][ ObjectId ] );
                KillTimer(CargoBox[ i ][ DissapearTimer ]);
                ApplyAnimation( playerid, "CARRY", "liftup", 4.0, 0, 1, 0, 0, 0 );
                SetPVarInt( playerid, "Tipas2", false );
                SetPlayerSpecialAction(playerid,SPECIAL_ACTION_CARRY);
                SetPVarInt(playerid, "CargoId", CargoBox[ i ][ CargoId ]);
                format(string,sizeof(string),"Sëkmingai pakëlëtæ dëþæ ant kurios etiketës yra paraðytas prekës pavadinimas: {97cd17} %s", GetCargoName( CargoBox[ i ][ CargoId ]));
                SendClientMessage(playerid, COLOR_WHITE, string);
                CargoBox[ i ][ CargoId ] = 0;
                return 1;
            }
        }
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, prie Jûsø nëra jokios dëþës, kad galëtumëte paiimti.");
        return 1;
    }
    if(!strcmp(params,"place", true))
    {
        new vehicleid = GetNearestVehicle(playerid, 5.0),
            cargoid = GetPVarInt(playerid, "CargoId");
        if(vehicleid == INVALID_VEHICLE_ID)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðalia Jûsø turi bûti tr. priemonë, kurioje padësitæ kroviná");

        if(IsTrailerAttachedToVehicle(vehicleid))
            vehicleid = GetVehicleTrailer(vehicleid);
            
        if(!CanPlayerUseTruckerVehicle(playerid,GetVehicleModel(vehicleid)))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio, neturite pakankamai patirties taðkø, kad galëtumëte veþti krovinius su ðia tr. priemone.");
        if(!cargoid)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûs nelaikote dëþës rankose, kad galëtumëte ádëti.");
        if(!IsCargoCompatibleWithVehicle(cargoid, GetVehicleModel(vehicleid)))
            return SendClientMessage(playerid, COLOR_LIGHTRED, " Klaida, ði tr. priemonë nëra pritaikyta tokio tipo kroviniui.");
        if(!HasVehicleSpaceForCargo(vehicleid, cargoid))
            return SendClientMessage(playerid, COLOR_LIGHTRED, " Dëmesio, ðá tr. priemonë jau yra pakrauta ir pilna.");
        if(cInfo[ vehicleid ][ cLock ])
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðalia esanti tr. priemonë yra uþrakinta.");

        AddCargoToVehicle(vehicleid, cargoid);
        ApplyAnimation(playerid,"CARRY","putdwn", 4.0, 0, 1, 0, 0, 0 );
        RemovePlayerAttachedObject(playerid, 7);
        SetPlayerSpecialAction(playerid,SPECIAL_ACTION_NONE);
        DeletePVar(playerid, "CargoId");
        return 1;
    }
    if(GetPlayerAdminLevel(playerid) >= 4 && !strcmp(params,"boxinfo", true))
    {
        new Float:dist, Float:x,Float:y,Float:z, string[1024];
        for(new i = 0; i < MAX_BOXES; i++)
        {
            if(!IsValidDynamicObject(CargoBox[ i ][ ObjectId ]))
                continue;
            GetDynamicObjectPos(CargoBox[ i ][ ObjectId ], x, y, z);
            dist = GetPlayerDistanceFromPoint(playerid, x, y, z);
            format(string, sizeof(string),"%sIndeksas:%d X:%.2f Y:%.2f Z:%.2f Atstumas:%.2f cargoid:%d Pavadinimas:%s\n",
                string,
                x,y,z,
                dist, 
                CargoBox[ i ][ CargoId],
                GetCargoName(CargoBox[ i ][ CargoId]));
        }
        if(isnull(string))
            return SendClientMessage(playerid,GRAD, "Ðiuo metu nëra ant þemës padëtø dëþiø.");
        ShowPlayerDialog(playerid, 9999, DIALOG_STYLE_MSGBOX, "Deziu, padetu ant zemes sarasas.", string, "Gerai", "");
        return 1;
    }
    else 
        goto cargo_help;
    return 1;
}


 GetIndustryCount()
{
    new count = 0;
    for(new i = 0; i < sizeof Industries; i++)
        if(Industries[ i ][ Id ])
            count++;
    return count;
}
 GetIndustryCargoIndex(index,cargoid)
{
	foreach(CommodityIterator, i)
		if(Commodities[ i ][ IndustryId ] == Industries[ index ][ Id ]
		&& Commodities[ i ][ CargoId] == cargoid
		&& !Commodities[ i ][ IsBusinessCommodity ])
			return i;
	return -1;
}

CMD:trailer(playerid, params[])
{
    if(isnull(params))
    {
        trailer_help:
        SendClientMessage(playerid, COLOR_LIGHTRED, "__________________________Trailer Cargo komandos ir naudojimas__________________________");
        SendClientMessage(playerid,COLOR_LIGHTRED2, "  KOMANDOS NAUDOJIMAS: /trailer [komanda], pavyzdþiui: /trailer cargo");
        SendClientMessage(playerid,GRAD,"  PAGRINDINES: lock, detach, lights, cargo sellto");
        return 1;
    }
    new vehicleid = GetPlayerVehicleID(playerid);
    new trailerid = GetVehicleTrailer(vehicleid),
        action[ 32 ];

    strmid(action, params, 0, strfind(params, " "));
    strdel(params, 0, strfind(params, " "));

    if(!IsValidVehicle(trailerid) || !IsVehicleTrailer(GetVehicleModel(trailerid)))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Prie jûsø transporto priemonës nëra prikabinta priekaba");

    if(!strcmp(action, "sellto", true))
    {
        if(pInfo[ playerid ][ pMySQLID ] != cInfo[ trailerid ][ cOwner ])
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neesate ðios priekabos savininkas.");

        new target, price;
        if(sscanf(params, "ui", target, price))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Komandos naudojimas: /trailer sellto [Þaidëjo ID/Dalis vardo] [ Kaina ]");
        if(!IsPlayerConnected(target))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, tokio þaidëjo nëra.");
        if(!IsPlayerInRangeOfPlayer(playerid, target, 4.0)) 
            return SendClientMessage(playerid, COLOR_WHITE,"[ KLAIDA! ] þaidëjas per toli nuo jûsø. ");

        SellVehicleToPlayer(playerid, trailerid, target, price);
        return 1;
    }
    if(!strcmp(action,"lock",true))
    {
        if(CheckCarKeys(playerid, trailerid))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ði priekaba jums nepriklauso.");
        if(cInfo[ trailerid ][ cLock ])
            ShowInfoText(playerid,"~w~AUTOMOBILIS ~g~ATRAKINTAS", 1000);
        else 
            ShowInfoText(playerid,"~w~AUTOMOBILIS ~r~UZRAKINTAS", 1000);
        LockVehicle(trailerid, !cInfo[ trailerid ][ cLock ]);
        PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
        return 1;
    }
    if(!strcmp(action, "detach",true))
    {
        DetachTrailerFromVehicle(vehicleid);
        SendClientMessage(playerid, COLOR_NEWS, "Priekaba atkabinta!");
        return 1;
    }
    if(!strcmp(action, "lights",true))
    {
        new engine,lights,alarm,doors,bonnet,boot,objective;
        GetVehicleParamsEx(trailerid, engine, lights, alarm, doors, bonnet, boot, objective);
        if(lights == VEHICLE_PARAMS_ON)
            SetVehicleParamsEx(trailerid, engine, VEHICLE_PARAMS_OFF, alarm, doors, bonnet, boot, objective);
        else 
            SetVehicleParamsEx(trailerid, engine, VEHICLE_PARAMS_ON, alarm, doors, bonnet, boot, objective);
        return 1;
    }
    if(!strcmp(action,"cargo", true))
    {
        ShowVehicleCargo(playerid, trailerid);
        return 1;
    }
    else 
        goto trailer_help;
    return 1;
}

forward OnCargoBoxDestroy(index);
public OnCargoBoxDestroy(index)
{
    if(IsValidDynamicObject(CargoBox[ index ][ ObjectId ]))
    {
        CargoBox[ index ][ CanBePickedUp ] = false;
        DestroyDynamicObject(CargoBox[ index ][ ObjectId ]);
    }
    else 
        printf("KLAIDA. OnCargoBoxDestroy(%d) objektas NERA tinkamas objektas.", index);
}

CMD:taip( playerid, params[ ] )
{
    #pragma unused params
    if ( Voted[ playerid ] == true ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Balsavimas nepradëtas, arba jau balsavoje. " );
    SendClientMessage( playerid, COLOR_WHITE, "Jûsø balsas sëkmingai áskaièiuotas. " );
    Votes[ 0 ] ++;
    Voted[ playerid ] = true;
    return 1;
}
CMD:ne( playerid, params[ ] )
{
    #pragma unused params
    if ( Voted[ playerid ] == true ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Balsavimas nepradëtas, arba jau balsavoje. " );
    SendClientMessage( playerid, COLOR_WHITE, "Jûsø balsas sëkmingai áskaièiuotas. " );
    Votes[ 1 ] ++;
    Voted[ playerid ] = true;
    return 1;
}


 SellVehicleToPlayer(owner_playerid, vehicleid, buyer_playerid, price)
{
    new
        string[ 128 ],
        IP[ 16 ],
        IP2[ 16 ];

    GetPlayerIp( owner_playerid, IP, 16 );
    GetPlayerIp( buyer_playerid, IP2, 16 );

    if( !strcmp( IP, IP2, true ) || pInfo[ owner_playerid ][ pUcpID ] == pInfo[ buyer_playerid ][ pUcpID ] )
        return true;
        
    format(string,sizeof(string),"Jûs siulote jam %s,kad jis nupirktu jûsø automobilá uþ: $%d.",GetPlayerNameEx(buyer_playerid),price);
    SendClientMessage(owner_playerid,COLOR_WHITE,string);
    format(string,sizeof(string),"Automobilio savininkas %s siûlo jums nupirkti jo automobilá uþ: $%d, jeigu sutinkate,raðykite /accept car.",GetPlayerNameEx(owner_playerid),price);
    SendClientMessage(buyer_playerid,COLOR_WHITE,string);
    Offer[buyer_playerid][0] = owner_playerid;
    OfferPrice[buyer_playerid][0] = price;
    OfferID[ buyer_playerid ][ 0 ] = vehicleid;
    return 1;
}


/*
CMD:weapon(playerid, params[])
{
    if(isnull(params))
    {
        weapon_help:
        SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /weapon [veiksmas]");
        SendClientMessage(playerid, COLOR_WHITE, "GALIMI VEIKSMAI: adjust, show, hide");
        return 1;
    }

    new action[16],
        weaponid = GetPlayerWeapon(playerid);

    if(!weaponid)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neesate iðsitraukæs ginklo.");
    
    if(strfind(params, " ") != -1)
        strmid(action, params, 0, strfind(params, " "));
    else 
        strmid(action, params, 0, strlen(params));
    strdel(params, 0, strlen(action));

    if(!strcmp(action, "adjust", true))
    {
        if(GetWeaponSlotByID(weaponid) < 2 || GetWeaponSlotByID(weaponid) > 6)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðio ginklo pozicijos keisti negalite.");

        for(new i = 0; i < sizeof(PlayerAttachedWeapons[]); i++)
        {
            if(PlayerAttachedWeapons[ playerid ][ i ][ WeaponId ] == weaponid)
            {
                // Should NOT be an issue. Jsut to be sure.
                if(IsPlayerAttachedObjectSlotUsed(playerid, 8))
                {
                    EditAttachedObject(playerid, PlayerAttachedWeapons[ playerid ][ i ][ ObjectSlot ]);
                }
            }
            return 1;
        }

        // Jei èia pasiekëm, reiðkia nëra PlayerAttachedWeapons to ginklo+
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neesate uþsidëjæ ðio ginklo.");
    }
    else if(!strcmp(action, "show", true))
    {
        if(GetWeaponSlotByID(weaponid) < 2 || GetWeaponSlotByID(weaponid) > 6)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðio ginklo uþsidëti negalite.");

        for(new i = 0; i < MAX_PLAYER_ATTACHED_WEAPONS; i++)
        {
            if(PlayerAttachedWeapons[ playerid ][ i ][ WeaponId ])
                continue;

            PlayerAttachedWeapons[ playerid ][ i ][WeaponId ] = weaponid;
            if(!IsPlayerAttachedObjectSlotUsed(playerid, 8))
            {
                SetPlayerAttachedObject(playerid, 8, GunObjects[ weaponid ], 1, 0.199999, -0.139999, 0.030000, 0.500007, -115.000000, 0.000000, 1.000000, 1.000000, 1.000000);
                EditAttachedObject(playerid, 8);
                PlayerAttachedWeapons[ playerid ][ i ][ ObjectSlot ] = 8;
            }
            else if(!IsPlayerAttachedObjectSlotUsed(playerid, 9))
            {
                SetPlayerAttachedObject(playerid, 9, GunObjects[ weaponid ], 1, 0.199999, -0.139999, 0.030000, 0.500007, -115.000000, 0.000000, 1.000000, 1.000000, 1.000000);
                EditAttachedObject(playerid, 9);
                PlayerAttachedWeapons[ playerid ][ i ][ ObjectSlot ] = 9;
            }
            SendClientMessage(playerid, COLOR_NEWS, "Sëkmingai uþsidëjote ginklà. Jo pozicijà galite keisti su /weapon adjust");
            return 1;
        }
    }
    else if(!strcmp(action, "hide", true))
    {
        for(new i = 0; i < MAX_PLAYER_ATTACHED_WEAPONS; i++)
        {
            if(PlayerAttachedWeapons[ playerid ][ i ][ WeaponId ] != weaponid)
                continue;

            RemovePlayerAttachedObject(playerid, PlayerAttachedWeapons[ playerid ][ i ][ ObjectSlot ]);
            PlayerAttachedWeapons[ playerid ][ i ][ ObjectSlot ] = 0;
            PlayerAttachedWeapons[ playerid ][ i ][ WeaponId ] = 0;
            SendClientMessage(playerid, COLOR_NEWS, "Ginklas nebebus rodomas.");
            return 1;
        }
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðis ginklas nebuvo rodomas. Neávykdyti jokie pokyèiai.");
    }
    else 
        goto weapon_help;
    return 1;
}*/


/*
CMD:police( playerid, params[ ] )
{
    #pragma unused params
    if ( GetPlayerVehicleSeat( playerid ) == 0 || GetPlayerVehicleSeat( playerid ) == 1 )
    {
        new vehicleid = GetPlayerVehicleID( playerid ),
            string[ 123 ];
        if ( VehicleHasWindows( GetVehicleModel( vehicleid ) ) && Windows[ vehicleid ] == false )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Automobilio langas uþdarytas. " );
        if ( Police[ vehicleid ] == 0 )
        {
            if ( sVehicles[ vehicleid ][ Faction ] == 2 )
            {
                new Float:Z;
                switch( GetVehicleModel( vehicleid ) )
                {
                    case 596: Z = 0.9; // PD
                    case 597: Z = 0.9; // PD
                    case 598: Z = 0.9; // PD
                    case 599: Z = 1.1; // PD Rancher
                    case 541: Z = 0.65; // Bullet
                    case 560: Z = 0.85; // Sultan
                    case 566: Z = 0.9; // Tahoma
                    case 490: Z = 1.1; // FBI Rancher
                    case 426: Z = 0.9; // Premier
                    case 558: Z = 0.9; // Uranus
                    case 559: Z = 0.65; // Jester
                    default: Z = 0.85; // Default
                }
                Police[ vehicleid ] = CreateDynamicObject(18646,0,0,0,0,0,0);
                AttachDynamicObjectToVehicle( Police[ vehicleid ], vehicleid, -0.5, -0.2, Z, 2.0, 2.0, 3.0);
                format      ( string, 126, "* %s atsidaræs langà uþdeda policijos perspëjàmàjá ðvyturëli ant stogo ir ájungià já. ", GetPlayerNameEx( playerid ) );
                ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
                return 1;
            }
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Tai ne policijos automobilis. " );
        }
        else if ( Police[ vehicleid ] > 0 )
        {
            DestroyDynamicObject( Police[ vehicleid ] );
            Police[ vehicleid ] = 0;
            format      ( string, 126, "* %s iðkiða rankà ir nuiima policijos perspëjàmàjá ðvyturëlá nuo stogo. ", GetPlayerNameEx( playerid ) );
            ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
            return 1;
        }
    }
    return 1;

}
*/

/*
CMD:info(playerid)
{
    if(pInfo[ playerid ][ pJob ] != JOB_JACKER)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, tik dirbdamas automobiliø vagies darbà galite naudotis ðiuo veiksmu. ");
    new string[ 160 ];
    if(Data_IsPlayerInRangeOfCoords(playerid, 10.0, "jacker_info_1"))
        format(string, sizeof(string), "SMS: Girdëjau ieðkai darbelio, o að ieðkausi %s, pasistengsiu gerai atsilyginti jei tik tinka ir nebijai rizikuoti -Neþinomas siuntëjas.", GetVehicleName(JackerBoughtVehicles[ 0 ][ VehicleModel ]));
    else if(Data_IsPlayerInRangeOfCoords(playerid, 10.0, "jacker_info_2"))
        format(string, sizeof(string), "SMS: Skubiai ieðkausi %s, visados moku daugiausia uþ kitus, tad manau nenuvilsi manæs. Siuntëjas: Nenustatytas numeris", GetVehicleName( JackerBoughtVehicles[ 1 ][ VehicleModel ] ));
    else if(Data_IsPlayerInRangeOfCoords(playerid, 10.0, "jacker_info_3"))
        format(string, sizeof(string), "SMS: Turiu klausimà, apsiimsi %s nuvarymø? Pasirûpinsiu, kad rizika bûtø apmokëta Siuntëjas: Nenustatytas numeris", GetVehicleName( JackerBoughtVehicles[ 2 ][ VehicleModel ] ));
    else
        format(string, sizeof(string), "SMS: Kodël vis dar negaunu þiniø? Atsisakai darbo? Nelabai patinka man tokie þmonës -Neþinomas siuntëjas");
    SendClientMessage(playerid, COLOR_LIGHTRED2, string);
    return 1;
}
CMD:spots( playerid, params[ ] )
{
    #pragma unused params
    if ( pInfo[ playerid ][ pJob ] != JOB_JACKER )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, tik dirbdamas automobiliø vagies darbà galite naudotis ðiuo veiksmu. " );
    new zone1[ MAX_ZONE_NAME ],
        zone2[ MAX_ZONE_NAME ],
        zone3[ MAX_ZONE_NAME ],
        string[ 128 ];

    Get2DZone( 868.8514,-30.3725, zone1, 28 );
    Get2DZone( 2827.3010,896.9294, zone2, 28 );
    Get2DZone( 2207.4143,-2296.2839, zone3, 28 );

    format( string, sizeof(string), "SMS: Tiesiog atveðk automobilius á ðiuos garaþus %s, %s, %s, ir baigiam reikalus. -Neþinomas siuntëjas", zone1, zone2, zone3 );
    SendClientMessage( playerid, COLOR_LIGHTRED2, string);
    return 1;
}
*/
CMD:sup( playerid, params[ ] )
{
    new giveplayerid,
        type,
        string[ 126 ];
    if ( sscanf( params, "ud", giveplayerid, type ) )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /sup [ veikëjo id ] [ 1-3 ]");

    if( GetPlayerState( playerid ) != PLAYER_STATE_ONFOOT ) return 1;
    if ( !PlayerToPlayer( 1.0, playerid, giveplayerid ) )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: norëdami naudoti ðiá  komandá  turite bûti ðalia veikëjo. " );
    if ( type < 0 || type > 4 )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /sup [ veikëjo id ] [ 1 - 3 ]");

    SendClientMessage( playerid, COLOR_WHITE, "Jûs pasiûlëte pasisveikinimá  þaidëjui, laukite jo patvirtinimo. " );
    format( string, sizeof(string), "PASIûLIMAS: þaidëjas %s siûlo jums pasisvekinima, jeigu sutinkate raðykite: /accept sup %d)",GetPlayerNameEx( playerid ), playerid );
    SendClientMessage( giveplayerid, COLOR_LIGHTRED2, string );
    SetPVarInt( playerid, "OFER_SUP", type );
    return 1;
}

FUNKCIJA:CameraMove( playerid, camera )
{
    if ( Camera[ playerid ] >=0 )
    {
        new keys,
            updown,
            leftright;
        static Float:Degres[ MAX_PLAYERS ],
               Float:Radius[ MAX_PLAYERS ];

        GetPlayerKeys(playerid, keys, updown, leftright);
        if ( leftright == KEY_RIGHT )
        {
            Degres[ playerid ] = Degres[ playerid ] - 5.0;
            if ( Degres[ playerid ] < 0 )
                Degres[ playerid ] = 359;
            MoveCamera( playerid, Degres[ playerid ], Radius[ playerid ], camera );
        }
        if ( leftright == KEY_LEFT )
        {
            Degres[ playerid ] = Degres[ playerid ] + 5.0;
            if ( Degres[ playerid ] >= 360 )
                Degres[ playerid ] = 0;
            MoveCamera( playerid, Degres[ playerid ], Radius[ playerid ], camera );
        }
        if ( updown == KEY_UP )
        {
            if ( Radius[ playerid ] < 36 )
            {
                Radius[ playerid ] = Radius[ playerid ] + 0.5;
                MoveCamera( playerid, Degres[ playerid ], Radius[ playerid ], camera );
            }
        }
        if ( updown == KEY_DOWN )
        {
            if ( Radius[ playerid ] >= 1.5 )
            {
                Radius[ playerid ] = Radius[ playerid ] - 0.5;
                MoveCamera( playerid, Degres[ playerid ], Radius[ playerid ], camera );
            }
        }
        SetTimerEx( "CameraMove", 100, false, "dd", playerid, Camera[ playerid ] );
    }
    return 1;
}
 MoveCamera( playerid, Float:degres, Float:radius, camera )
{
    static Float:WachX[ MAX_PLAYERS ],
           Float:WachY[ MAX_PLAYERS ];
    WachX[ playerid ] = CCTV[ camera ][ 0 ] + ( floatmul(radius, floatsin(-degres, degrees)));
    WachY[ playerid ] = CCTV[ camera ][ 1 ] + ( floatmul(radius, floatcos(-degres, degrees)));
    SetPlayerCameraLookAt(playerid, WachX[ playerid ], WachY[ playerid ], CCTV[ camera ][ 3 ]);
    return 1;
}

CMD:stopsmoke( playerid, params[ ] )
{
    #pragma unused params
    if ( Ruko[ playerid ] > 0 )
    {
        SetPlayerSpecialAction( playerid, SPECIAL_ACTION_NONE);
        Ruko[ playerid ] = 0;
        return 1;
    }
    return 1;
}

FUNKCIJA:PD_BELL( playerid )
    return DeletePVar( playerid, "PD_BELL" );

CMD:blockpm( playerid, params[ ] )
{
    if(pInfo[ playerid ][ pDonator ] < 2  && !GetPlayerAdminLevel(playerid))
        return 0;

    new string[ 126 ],
        giveplayerid;

    if(sscanf( params, "u", giveplayerid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /blockpm [ veikëjo id/vardo dalis ]");

    if(!IsPlayerConnected(giveplayerid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /blockpm [ veikëjo id/vardo dalis ]");


    if(PlayersBlocked[ playerid ][ giveplayerid ])
    {
        format(string, sizeof(string), "Þaidëjas %s buvo atblokuotas, dabar gausite ið jo þinutes. ", GetName(giveplayerid));
        SendClientMessage(playerid, COLOR_WHITE, string);
    }
    else
    {
        format(string, sizeof(string), "þaidëjas %s buvo uþblokuotas, dabar nebegausite ið jo privaøiø þinuøiø.", GetName(giveplayerid));
        SendClientMessage(playerid, COLOR_WHITE, string);
    }
    PlayersBlocked[ playerid ][ giveplayerid ] = !PlayersBlocked[ playerid ][ giveplayerid ];
    return 1;
}


CMD:drivebyoff( playerid, params[ ] )
{
    #pragma unused params
    if ( GetPlayerState( playerid ) == PLAYER_STATE_PASSENGER )
    {
        SetPlayerArmedWeapon( playerid , 0 );
        return 1;
    }
    return 1;
}
CMD:roof( playerid, params[ ] )
{
    if ( !IsPlayerInAnyVehicle( playerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, norëdami atlikti ðá veiksmà privalote bøti tr. priemonëje. " );
    new vehicle = GetPlayerVehicleID( playerid ),
        model = GetVehicleModel( vehicle );
    if ( GetPlayerState( playerid ) != PLAYER_STATE_DRIVER ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs nesate ðio automobilio vairuotojas. " );

    if ( model == 536 || model == 567 )
    {
        new type;
        if ( sscanf( params, "d", type ) )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /roof [ 1 tipas | 2 tipas | 3 Nuimti ] ");
        if ( type == 1 )
        {
            if ( model == 536 )
                return AddVehicleComponent( vehicle, 1103 );
            else if ( model == 567 )
                return AddVehicleComponent( vehicle, 1130 );
        }
        else if ( type == 2 )
        {
            if ( model == 536 )
                return AddVehicleComponent( vehicle, 1128 );
            else if ( model == 567 )
                return AddVehicleComponent( vehicle, 1131 );
        }
        else if ( type == 3 )
        {
            new comp = GetVehicleComponentInSlot( vehicle, CARMODTYPE_ROOF);
            if ( comp == 1103 || comp == 1130 || comp == 1128 || comp == 1131 )
                return RemoveVehicleComponent( vehicle , comp );
        }
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /roof [ 1 tipas | 2 tipas | 3 Nuimti ] ");
    }
    return SendClientMessage( playerid, GRAD, "Apgailestaujame, ði maðina neturi pakeliamo stogo. " );
}

CMD:nofuel(playerid)
{
    cInfo [ GetPlayerVehicleID(playerid) ] [ cFuel ] = 0;
    return 1;
}
CMD:fill(playerid)
{
    if(!IsAtGasStation(playerid))
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs nesate degalinëje. " );
    if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, kurà gali pilti tik vairuotojas");
    if(IsFillingFuel[ playerid ])
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs jau pilatës kurà.");
    new veh = GetPlayerVehicleID(playerid);
    if(Engine[ veh ] == true ) 
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Iðjunkite variklá" );

    IsFillingFuel[ playerid ] = true;
    PlayerFillUpTimer[ playerid ] = SetTimerEx( "FillUp", 200, true, "dd", playerid, veh );
    SetPVarInt( playerid, "FILLED", cInfo[ veh ][ cFuel ] );
    return 1;
}

FUNKCIJA:FillUp( playerid, vehicle )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER)
        return StopFillUp(playerid);
    if(!IsAtGasStation( playerid ))
        return StopFillUp(playerid);
    if(cInfo[ vehicle ][ cFuel ] >= GetVehicleFuelTank(GetVehicleModel(vehicle)))
        return StopFillUp(playerid);
    if(Engine[ vehicle ])
        return StopFillUp(playerid);

    cInfo[ vehicle ][ cFuel ] += 2;
    SetPVarInt( playerid, "MOKESTIS", GetPVarInt( playerid, "MOKESTIS" ) + BENZO_KAINA );
    if ( cInfo[ vehicle ][ cFuel ] == GetVehicleFuelTank( GetVehicleModel(vehicle) ) )
        StopFillUp(playerid);
    return 1;
}
 StopFillUp(playerid)
{
    new string[ 126 ];
    format          ( string, sizeof(string), "DEGALINË\nMokestis uþ degalus: %d\nKuo atsikaitysite? spustelkite migtuka", GetPVarInt( playerid, "MOKESTIS" ) );
    ShowPlayerDialog( playerid ,5, DIALOG_STYLE_MSGBOX, "DEGALINË", string, "Grynais", "Banku" );
    KillTimer(PlayerFillUpTimer[ playerid ]);
    IsFillingFuel[ playerid ] = false;
    return 1;
}
/*
CMD:checkspeed( playerid, params[ ] )
{
    new
        string[ 64 ];
    if(PlayerFaction( playerid ) != 1) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti ðio veiksmo nedirbdami policijos departamente.");

    new count;
    foreach(Vehicles,car)
    {
        if ( GetVehicleSpeed2( car ) < 1 || sVehicles[ car ][ Faction ] == 2 )
            continue;
            
        new Float: Car_X,
            Float: Car_Y,
            Float: Car_Z;
        GetVehiclePos( car, Car_X, Car_Y, Car_Z );
        if ( PlayerToPoint( 20, playerid, Car_X, Car_Y, Car_Z ) )
        {
            format(string, sizeof(string), "[LSPD] Pravaþiuojanèios tr. priemonës greitis yra: %dkm/h (( %s ))", GetVehicleSpeed2( car ), GetVehicleName( GetVehicleModel( car ) ) );
            SendClientMessage(playerid, COLOR_POLICE, string );
            count++;
        }
    }
    if(!count)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalimas veiksmas, kadangi aplink Jus nëra pravaþiuojanèiø tr. priemoniø");
    return true;
}
*/
/*
CMD:mdc( playerid, params[] )
{
    new
        string[ 64 ];
    if(PlayerFaction( playerid ) != 1) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti ðio veiksmo nedirbdami policijos departamente.");
    new idcar = GetPlayerVehicleID( playerid );
    if( PDJOBPlace(playerid) || ( IsPlayerInAnyVehicle( playerid ) && sVehicles[ idcar ][ Faction ] == 2 ) )
    {
        format( string, 64, "Policijos duomenø bazë - Prisijungta: %s", GetName( playerid ) );
        ShowPlayerDialog(playerid, 128, DIALOG_STYLE_LIST,string,
        "Surasti asmená\n\
        Ieðkoti tr. priemonës ((Numeris))\n\
        Paieðkomø sàraðas\n\
        Kalëjimo duomenø bazë\n\
        Pridëti prie paieðkomø asmenø \n\
        Paskelbti tr. priemonæ paieðkomà\n\
        Paieðkomø tr. priemoniø sàraðas\n\
        Areðtuotu tr. priemoniø sàraðas\n\
        Iðkvietimø registras", "Pasirinkti", "Atðaukti" );
    }
    else
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, norëdami atlikti ðá veiksmà privalote sedëti policijos tr. priemonëje arba bûdami nuovadoje.");
    return 1;
}
*/
/*
CMD:bail( playerid, params[ ] )
{
    if(!Data_IsPlayerInRangeOfCoords(playerid, 30.0, "prison_bail_spot"))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, privalote bûti ðalia kalëjimo priemamojo langelio.");
    if( GetPVarInt(playerid, "BailTime") < 1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jums nëra uþ kà mokëti.");
        
    new string[ 256 ],
        rows,
        Cache:result;

    format( string, 256, "SELECT * FROM `tickets` WHERE `name` = '%s' AND `paid` = 0", GetPlayerNameEx(playerid) );
    //result = mysql_query(DbHandle,  string );
    rows = cache_get_row_count();

    cache_delete(result);

    if( rows )
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti iðpirkos turëdami nesumokëtø baudø.");

    if ( GetPlayerBankMoney(playerid) < GetPVarInt(playerid, "Bail") )
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûsø banko sàskaitoje nëra pakankamai pinigø iðpirkai..");

    AddPlayerBankMoney(playerid,  - GetPVarInt(playerid, "Bail"));
    pInfo[playerid][pJailTime] = GetPVarInt(playerid, "BailTime");
    SaveAccount( playerid );
    ShowPlayerInfoText( playerid );
    DeletePVar(playerid, "Bail");
    DeletePVar(playerid, "BailTime");
    return 1;
}
*/


FUNKCIJA:ClosePVartai( id )
{
    switch ( id )
    {
        case 0:
        {
            MoveObject( vartai[ 0 ][ 0 ], 595.28222656,353.47207642,18.69028854, 0.1,0.00000000,90.00000000,34.96582031 );
            MoveObject( vartai[ 1 ][ 0 ], 606.92742920,361.62338257,18.69028854, 0.1,0.00000000,90.00000000,215.04956055 );
            vartai[ 0 ][ 1 ] = false;
            vartai[ 1 ][ 1 ] = false;
        }
        case 1:
        {
            MoveObject( vartai[ 6 ][ 0 ], -202.20063782,265.40649414,11.85065079, 0.1,0.00000000,90.00000000,345.41210938 );
            MoveObject( vartai[ 7 ][ 0 ], -188.38629150,261.80456543,11.85366344, 0.1,0.00000000,90.00000000,165.41015625 );
            vartai[ 6 ][ 1 ] = false;
            vartai[ 7 ][ 1 ] = false;

        }
        case 2:
        {
            MoveObject( vartai[ 8 ][ 0 ], 1200.39172363,-620.98559570,56.12424469, 0.1,0.00000000,90.00000000,337.97341919 );
            MoveObject( vartai[ 9 ][ 0 ], 1213.55517578,-626.29931641,56.12844849, 0.1,0.00000000,90.00000000,158.01440430 );
            vartai[ 8 ][ 1 ] = false;
            vartai[ 9 ][ 1 ] = false;
        }
        case 3:
        {
            MoveObject( vartai[ 10 ][ 0 ], -949.18688965,-285.37277222,36.20511246, 0.1,0.00000000,270.00000000,349.62197876 );
            MoveObject( vartai[ 11 ][ 0 ], -963.11804199,-282.82247925,36.20383453, 0.1,0.00000000,270.00000000,169.61791992 );
            vartai[ 10 ][ 1 ] = false;
            vartai[ 11 ][ 1 ] = false;
        }
        case 4:
        {
            MoveObject( vartai[ 12 ][ 0 ], -1397.50427, 825.34515, 47.23720, 0.1,0.00000000,270.00000000,137.00000 );
            MoveObject( vartai[ 13 ][ 0 ], -1400.92200, 828.56818, 47.23720, 0.1,0.00000000,270.00000000,-44.00000 );
            vartai[ 12 ][ 1 ] = false;
            vartai[ 13 ][ 1 ] = false;
        }
        case 5:
        {
            MoveObject( vartai[ 14 ][ 0 ], 1695.20422, 443.20917, 30.81520, 0.1,0.00000000,270.00000000,-201.00000 );
            MoveObject( vartai[ 15 ][ 0 ], 1722.53088, 432.85577, 30.81520, 0.1,0.00000000,270.00000000,-21.00000 );
            vartai[ 14 ][ 1 ] = false;
            vartai[ 15 ][ 1 ] = false;
        }
    }
}

/*
CMD:togooc( playerid, params[] )
{
    #pragma unused params
    if(TogChat[playerid][0] == true)
    {
        TogChat[playerid][0] = false;
        SendClientMessage(playerid,COLOR_LIGHTRED,"[TOGooc] Dabar nebematysite jokiø OOC praneðimø savo pokalbiø kanale");
        return 1;
    }
    else if(TogChat[playerid][0] == false)
    {
        TogChat[playerid][0] = true;
        SendClientMessage(playerid,COLOR_LIGHTRED,"[TOGooc] OOC praneðimø rodymas buvo ájungtas ");
        return 1;
    }
    return 1;
}
CMD:togpm(playerid)
{
    if ( pInfo[ playerid ][ pDonator ] >= 2 || GetPlayerAdminLevel(playerid) >= 1 )
    {
        if(TogChat[playerid][2] == true)
        {
            TogChat[playerid][2] = false;
            SendClientMessage(playerid,COLOR_LIGHTRED,"[TOGpm] Dabar nebegausite privaèiø þinuèiø, galësite tik siûsti kitiems.");
            return 1;
        }
        else if(TogChat[playerid][2] == false)
        {
            TogChat[playerid][2] = true;
            SendClientMessage(playerid,COLOR_LIGHTRED,"[TOGpm] Privaèiø þinuèiø gaviklis buvo ájungtas.");
            return 1;
        }
    }
    return 1;
}
CMD:togadmin(playerid)
{
    if(TogChat[playerid][3] == true)
    {
        TogChat[playerid][3] = false;
        SendClientMessage(playerid,COLOR_LIGHTRED,"[TOGadmin] Iðjungëte praneðimus apie Administratoriaus veiksmus serveryje.");
        return 1;
    }
    else if(TogChat[playerid][3] == false)
    {
        TogChat[playerid][3] = true;
        SendClientMessage(playerid,COLOR_LIGHTRED,"[TOGadmin] Administratoriø veiksmø praneðimai ájungti.");
        return 1;
    }
    return 1;
}
CMD:tognews(playerid, params[ ] )
{
    #pragma unused params
    if(TogChat[playerid][1] == true)
    {
        TogChat[playerid][1] = false;
        SendClientMessage(playerid,COLOR_LIGHTRED,"[TOGnews] Praneðamø naujienø praneðimai buvo iðjungti.");
        return 1;
    }
    else if(TogChat[playerid][1] == false)
    {
        TogChat[playerid][1] = true;
        SendClientMessage(playerid,COLOR_LIGHTRED,"[TOGnews] Naujienø praneðimai ájungti.");
        return 1;
    }
    return 1;
}
*/
CMD:buysex( playerid, params[ ] )
{
    #pragma unused params
    if(PlayerToPoint(10.0,playerid,-103.9604,-22.6792,1000.7188))
        ShowPlayerDialog(playerid,7,DIALOG_STYLE_LIST,"SEX PREKIØ MENU","\
            1.Roþinis vibratorius \t$300\
            \n2.Maþas baltas vibratorius \t$250\
            \n3.Didelis baltas vibratorius \t$330\
            \n4.Blizgantis vibratorius \t$260"
            ,"Pirkti","Iðjungti");
    return 1;
}



public OnPlayerCommandReceived(playerid, cmdtext[]) {
    #if defined DEBUG
        printf("[debug] OnPlayerCommandReceived(%s, %s)", GetName(playerid), cmdtext);
    #endif

    if(!IsPlayerLoggedIn(playerid))
    {
        SendClientMessage( playerid, GRAD, " Jûs nesate prisijungæs, praðome prisijungti.");
        return 0;
    }
    SetPVarInt( playerid, "Is_AFK", 0 );
    if( GetPVarInt( playerid, "Anti_Spam" ) > 0 )
    {
        //SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Labai jûsø praðome, nenaudokite komandø taip greit. " );
        //return 0;
    }
    SetPVarInt( playerid, "Anti_Spam", 3 );
    return 1;
}


forward SpecLabelDissapear(playerid);
public SpecLabelDissapear(playerid)
{
    foreach(new i : Player)
        if(IsPlayerSpectatingPlayer(i, playerid))
            UpdateDynamic3DTextLabelText(SpecCommandLabel[ i ], 0x00AA00FF, " ");
}



CMD:lock(playerid)
{
    new car = GetNearestVehicle(playerid,10.0);
    if(car == INVALID_VEHICLE_ID || cInfo[ car ][ cOwner ] == 0) return 1;
    if(cInfo[car][cLock] == 0 && CheckCarKeys(playerid,car) == 1)
    {
        LockVehicle(car, 1);
        VehicleAlarm(car, 0);
        ShowInfoText(playerid,"~w~AUTOMOBILIS ~r~UZRAKINTAS", 1000);
        PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
        return 1;
    }
    else if(cInfo[car][cLock] == 1 && CheckCarKeys(playerid,car) == 1)
    {
        LockVehicle(car, 0);
        VehicleAlarm(car, 0);
        ShowInfoText(playerid,"~w~AUTOMOBILIS ~g~ATRAKINTAS", 1000);
        PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
        return 1;
    }
    return 1;
}

/*
CMD:accept( playerid, params[ ] )
{
    new idx, accept[128], string[ 128 ], giveplayerid, Float:Kords[ 3 ], IP[ 16 ], IP2[ 16 ];
    accept = strtok(params, idx);
    if(!strlen(accept))
    {
        SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /accept [þodis]");
        SendClientMessage(playerid,GRAD,"PAGALBA: car, house, biz, live, license, bk, garage, sup, frisk, blindfold");
        return 1;
    }
    if(!strcmp("car",accept,true))
    {
        if(Offer[playerid][0] == 255) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Niekas tau nesiulo pirkti naujo automobilio.");
        if(OfferPrice[playerid][0] > GetPlayerMoney(playerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Neturi pakankamai pinigø, kad galetum nupirkti jo automobilá.");
        if(!IsPlayerConnected(Offer[playerid][0]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");
            Offer[playerid][0] = 255;
            return 1;
        }
        if(!PlayerToPlayer(5, playerid,Offer[playerid][0]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: þaidëjas nëra prie jûsø.");
            Offer[playerid][0] = 255;
            return 1;
        }

        if(GetPlayerState(Offer[playerid][0]) != PLAYER_STATE_DRIVER) return SendClientMessage(playerid,GRAD,"Pardavëjas turi sëdëti savo automobilyje.");
        new idof = OfferID[ playerid ][ 0 ];

        GetPlayerIp( playerid, IP, 16 );
        GetPlayerIp( Offer[playerid][0], IP2, 16 );
        
        if( !strcmp( IP, IP2, true ) || pInfo[ playerid ][ pUcpID ] == pInfo[ Offer[playerid][0] ][ pUcpID ] )
            return true;

        if ( cInfo[ idof ][ cOwner ] != pInfo[ Offer[ playerid ][ 0 ] ][ pMySQLID ] )
        {
            SendClientMessage( Offer[ playerid ][ 0 ], COLOR_LIGHTRED, "Perspëjimas: Tai ne jûsø automobilis.");
            Offer[ playerid ][ 0 ] = 255;
            return 1;
        }

        format(string,sizeof(string),"%s nupirko jûsø automobilá uþ $%d.",GetPlayerNameEx(playerid),OfferPrice[playerid][0]);
        SendClientMessage(Offer[playerid][0],COLOR_NEWS,string);
        format(string,sizeof(string),"Jûs nupirkote ið jo %s automobilá uþ $%d.",GetPlayerNameEx(Offer[playerid][0]),OfferPrice[playerid][0]);
        SendClientMessage(playerid,COLOR_NEWS,string);

        GivePlayerMoney(Offer[playerid][0],OfferPrice[playerid][0]);
        GivePlayerMoney(playerid,-OfferPrice[playerid][0]);

        PayLog( pInfo[ Offer[ playerid ][ 0 ] ][ pMySQLID ],4, pInfo[ playerid ][ pMySQLID ], OfferPrice[playerid][0] );
        PayLog( pInfo[ playerid ][ pMySQLID ],3, pInfo[ Offer[ playerid ][ 0 ] ][ pMySQLID ], OfferPrice[playerid][0] );
        cInfo[ idof ][ cOwner ] = pInfo[ playerid ][ pMySQLID ];
        cInfo[ idof ][ cVehID ] = 0;

        SaveCar( idof );

        DestroyVehicle(OfferID[ playerid ][ 0 ]);
        nullVehicle   (OfferID[ playerid ][ 0 ]);
            
        pInfo[Offer[playerid][0]][pCarGet] --;

        LoadPlayerVehicles( playerid );
        LoadPlayerVehicles( Offer[playerid][0] );

        SaveAccount( playerid );
        SaveAccount( Offer[ playerid ][ 0 ] );
        PlayerPlaySound(playerid, 1057, 0.0, 0.0, 0.0);
        Offer[playerid][0] = 255;
        OfferPrice[playerid][0] = 0;
        OfferID[playerid][0] = 0;
        return 1;
    }
    else if(!strcmp("house",accept,true))
    {
        if(Offer[playerid][1] == 255) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Niekas tau nesiulo pirkti namo");
        if(OfferPrice[playerid][1] > GetPlayerMoney(playerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Neturi pakankamai pinigø, kad galetum nupirkti ðá namà .");
        if(!IsPlayerConnected(Offer[playerid][1]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");
            Offer[playerid][1] = 255;
            return 1;
        }
        if(!PlayerToPlayer(5, playerid,Offer[playerid][1]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: þaidëjas nëra prie jûsø.");
            Offer[playerid][1] = 255;
            return 1;
        }
        
        GetPlayerIp( playerid, IP, 16 );
        GetPlayerIp( Offer[playerid][1], IP2, 16 );

        if( !strcmp( IP, IP2, true ) || pInfo[ playerid ][ pUcpID ] == pInfo[ Offer[playerid][1] ][ pUcpID ] )
            return true;
            
        format(string,sizeof(string),"%s nupirko Jûsø namà  uþ $%d.",GetPlayerNameEx(playerid),OfferPrice[playerid][1]);
        SendClientMessage(Offer[playerid][1],COLOR_NEWS,string);
        format(string,sizeof(string),"Jûs nupirkote ið jo %s namà  uþ $%d.",GetPlayerNameEx(Offer[playerid][1]),OfferPrice[playerid][1]);
        SendClientMessage(playerid,COLOR_NEWS,string);
        pInfo[Offer[playerid][2]][pSpawn] = DefaultSpawn;
        GivePlayerMoney(Offer[playerid][1],OfferPrice[playerid][1]);
        GivePlayerMoney(playerid,-OfferPrice[playerid][1]);
        PayLog( pInfo[ Offer[ playerid ][ 1 ] ][ pMySQLID ],6, pInfo[ playerid ][ pMySQLID ], OfferPrice[playerid][1] );
        PayLog( pInfo[ playerid ][ pMySQLID ],1, pInfo[ Offer[ playerid ][ 1 ] ][ pMySQLID ], OfferPrice[playerid][1] );
        hInfo[OfferID[playerid][1]][hOwner] = pInfo[ playerid ][ pMySQLID ];
        UpdateHouseInfoText(OfferID[playerid][1]);
        SaveHouse(OfferID[playerid][1]);
        SaveAccount( playerid );
        SaveAccount( Offer[ playerid ][ 1 ] );
        PlayerPlaySound(playerid, 1057, 0.0, 0.0, 0.0);
        Offer[playerid][1] = 255;
        OfferPrice[playerid][1] = 0;
        OfferID[playerid][1] = 0;
        return 1;
    }
    else if(!strcmp("biz",accept,true))
    {
        if(Offer[playerid][2] == 255) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Niekas tau nesiulo pirkti verslo.");
        if(OfferPrice[playerid][2] > GetPlayerMoney(playerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Neturi pakankamai pinigø, kad galetum nupirkti ðá verslà.");
        if(!IsPlayerConnected(Offer[playerid][2]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");
            Offer[playerid][2] = 255;
            return 1;
        }
        if(!PlayerToPlayer(5, playerid,Offer[playerid][2]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: þaidëjas nëra prie jûsø.");
            Offer[playerid][2] = 255;
            return 1;
        }
        
        GetPlayerIp( playerid, IP, 16 );
        GetPlayerIp( Offer[playerid][2], IP2, 16 );

        if( !strcmp( IP, IP2, true ) || pInfo[ playerid ][ pUcpID ] == pInfo[ Offer[playerid][2] ][ pUcpID ] )
            return true;
        
        format(string,sizeof(string),"%s nupirko jûsø verslà uþ $%d.",GetPlayerNameEx(playerid),OfferPrice[playerid][2]);
        SendClientMessage(Offer[playerid][2],COLOR_NEWS,string);
        format(string,sizeof(string),"Jûs nupirkote ið jo %s verslà uþ $%d.",GetPlayerNameEx(Offer[playerid][2]),OfferPrice[playerid][2]);
        SendClientMessage(playerid,COLOR_NEWS,string);
        pInfo[Offer[playerid][2]][pSpawn] = DefaultSpawn;
        GivePlayerMoney(Offer[playerid][2],OfferPrice[playerid][2]);
        GivePlayerMoney(playerid,-OfferPrice[playerid][2]);
        PayLog( pInfo[ Offer[ playerid ][ 2 ] ][ pMySQLID ],5, pInfo[ playerid ][ pMySQLID ], OfferPrice[playerid][2] );
        PayLog( pInfo[ playerid ][ pMySQLID ],2, pInfo[ Offer[ playerid ][ 2 ] ][ pMySQLID ], OfferPrice[playerid][2] );        
        bInfo[ OfferID[playerid][2] ][ bOwner ] = pInfo[ playerid ][ pMySQLID ];
        UpdateBusinessEntrance(OfferID[playerid][2]);
        SaveBusiness(OfferID[playerid][2]);
        PlayerPlaySound(playerid, 1057, 0.0, 0.0, 0.0);
        SaveAccount( playerid );
        SaveAccount( Offer[ playerid ][ 2 ] );
        Offer[playerid][2] = 255;
        OfferPrice[playerid][2] = 0;
        OfferID[playerid][2] = 0;
        return 1;
    }
    else if(!strcmp("live",accept,true))
    {
        if(Offer[playerid][4] == 255) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Niekas tau nesiulo tiesioginio pokalbio.");
        if(!PlayerToPlayer(5, playerid,Offer[playerid][4])) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Tu nesi ðialia prië tau siulanøio þmogaus");
        if(!IsPlayerConnected(Offer[playerid][4]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");
            Offer[playerid][4] = 255;
            return 1;
        }
        TalkingLive[playerid] = Offer[playerid][4];
        TalkingLive[Offer[playerid][4]] = playerid;
        format(string,sizeof(string),"%s priëmë ið jûsø pasiûlimá .",GetPlayerNameEx(playerid));
        SendClientMessage(Offer[playerid][4],COLOR_NEWS,string);
        format(string,sizeof(string),"Jûs priëmëte pasiûlimá  ið %s",GetPlayerNameEx(Offer[playerid][2]));
        SendClientMessage(playerid,COLOR_NEWS,string);
        Offer[playerid][4] = 255;
        return 1;
    }
    
    else if(!strcmp("fight",accept,true))
    {
        if(Mires[playerid] > 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûsø veikëjas ðiuo metu yra kritinëje arba komos bûsenoje.");
        if(Offer[playerid][6] == 255) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Niekas tau nesiulo tiesioginio pokalbio.");
        if(CheckBox() == 1) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Bokso salë uþimta, palaukite.");
        if(!PlayerToPlayer(5, playerid,Offer[playerid][6])) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Tu nesi ðialia prië tau siulanøio þmogaus");
        if(!IsPlayerConnected(Offer[playerid][6]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");
            Offer[playerid][6] = 255;
            return 1;
        }
        format(string,sizeof(string),"%s priëmë ið jûsø pasiølimá .",GetPlayerNameEx(playerid));
        SendClientMessage(Offer[playerid][6],COLOR_NEWS,string);
        format(string,sizeof(string),"Jûs priëmëte pasiûlimá  ið %s",GetPlayerNameEx(Offer[playerid][6]));
        SendClientMessage(playerid,COLOR_NEWS,string);

        SetPlayerPos(playerid,768.7744,-66.8329,1001.5692);
        SetPlayerFacingAngle(playerid,137.2355);
        SetPlayerPos(Offer[playerid][6],764.6347,-70.4305,1001.5692);
        SetPlayerFacingAngle(Offer[playerid][6],313.6439);

        SetPlayerHealth(playerid,150);
        SetPlayerHealth(Offer[playerid][6],150);
        format(string, sizeof(string), "[MG NEWS] Bokso varþybos prasideda, ringe %s kovos priëð %s, kova prasidës uz 15 sekundþiu.",  GetPlayerNameEx(playerid), GetPlayerNameEx(Offer[playerid][6]));
        SendNEWS(COLOR_NEWS,string);
        Boxing[playerid] = true;
        Boxing[Offer[playerid][6]] = true;
        Offer[playerid][6] = 255;
        BoxStart = 15;
        TogglePlayerControllable(playerid, false);
        TogglePlayerControllable(Offer[playerid][6], false);
        return 1;
    }
    else if ( !strcmp( "bk", accept, true ) )
    {
        if ( !UsePDCMD( playerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti ðio veiksmo nedirbdami policijos departamente." );
        string = strtok(params, idx);
        giveplayerid = strval( string );
        if ( pInfo[ giveplayerid ][ pBackup ] == 0 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, Jûs ðiuo metu nekvietëte jokio pastiprinimo. " );

        GetPlayerPos( giveplayerid, Kords[ 0 ], Kords[ 1 ], Kords[ 2 ] );
        SetPlayerCheckPointEx( playerid, CHECKPOINT_BACKUP, Kords[ 0 ], Kords[ 1 ], Kords[ 2 ], 5.0 );
        SetPVarInt( playerid, "BACKUP", giveplayerid );
        return 1;
    }
    else if ( !strcmp( "sup", accept, true ) )
    {
        string = strtok(params, idx);
        giveplayerid = strval( string );
        if ( !IsPlayerConnected( giveplayerid ) )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje. " );

        new type = GetPVarInt( giveplayerid, "OFER_SUP");
        SetPVarInt( giveplayerid, "OFER_SUP", 0 );

        if ( !PlayerToPlayer( 1.0, playerid, giveplayerid ) )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs nesate ðalia to þaidëjo. " );

        if ( type == 0 )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: þaidëjas jums nieko nesiølo. " );

        new Float:x,
            Float:y,
            Float:z,
            Float:a;

        GetPlayerPos        ( giveplayerid, x, y, z );
        GetPlayerFacingAngle( giveplayerid, a );
            
        x += (0.8 * floatsin(-a, degrees) );
        y += (0.8 * floatcos(-a, degrees) );
            
        SetPlayerPos( playerid, x, y, z );
        SetPlayerFacingAngle( playerid, a+180 );

        switch ( type )
        {
            case 1:
            {
                OnePlayAnim( giveplayerid,"GANGS","hndshkba",4.0,0,0,0,0,0);
                OnePlayAnim( playerid,"GANGS","hndshkba",4.0,0,0,0,0,0);
            }
            case 2:
            {
                OnePlayAnim( giveplayerid,"GANGS","hndshkda",4.0,0,0,0,0,0);
                OnePlayAnim( playerid,"GANGS","hndshkda",4.0,0,0,0,0,0);
            }
            case 3:
            {
                OnePlayAnim( giveplayerid,"GANGS","hndshkfa_swt",4.0,0,0,0,0,0);
                OnePlayAnim( playerid,"GANGS","hndshkfa_swt",4.0,0,0,0,0,0);
            }
            case 4:
            {
                if ( !strcmp( "Vyras", pInfo[ playerid ][ pSex ], true ) )
                    OnePlayAnim(playerid, "KISSING", "Playa_Kiss_02", 3.0, 0, 0, 0, 0, 0);
                else if ( !strcmp( "Moteris", pInfo[ playerid ][ pSex ], true ) )
                    OnePlayAnim(playerid, "BD_Fire", "grlfrd_kiss_03", 2.0, 0, 0, 0, 0, 0);
                if ( !strcmp( "Vyras", pInfo[ giveplayerid ][ pSex ], true ) )
                    OnePlayAnim(giveplayerid, "KISSING", "Playa_Kiss_02", 3.0, 0, 0, 0, 0, 0);
                else if ( !strcmp( "Moteris", pInfo[ giveplayerid ][ pSex ], true ) )
                    OnePlayAnim(giveplayerid, "BD_Fire", "grlfrd_kiss_03", 2.0, 0, 0, 0, 0, 0);
            }
            default:
            {
                OnePlayAnim( giveplayerid,"GANGS","hndshkfa_swt",4.0,0,0,0,0,0);
                OnePlayAnim( playerid,"GANGS","hndshkfa_swt",4.0,0,0,0,0,0);
            }
        }
        return 1;
    }
    else if(!strcmp("garage",accept,true))
    {
        if(Offer[playerid][7] == 255) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Niekas tau nesiulo pirkti garaþo");
        if(OfferPrice[playerid][7] > GetPlayerMoney(playerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Neturi pakankamai pinigø, kad galetum nupirkti ðá garaþá .");
        if(!IsPlayerConnected(Offer[playerid][7]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");
            Offer[playerid][7] = 255;
            return 1;
        }
        if(!PlayerToPlayer(5, playerid,Offer[playerid][7]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: þaidëjas nëra prie jûsø.");
            Offer[playerid][7] = 255;
            return 1;
        }
        
        GetPlayerIp( playerid, IP, 16 );
        GetPlayerIp( Offer[playerid][7], IP2, 16 );

        if( !strcmp( IP, IP2, true ) || pInfo[ playerid ][ pUcpID ] == pInfo[ Offer[playerid][7] ][ pUcpID ] )
            return true;
        
        format(string,sizeof(string),"%s nupirko Jûsø garaþà uþ $%d.",GetPlayerNameEx(playerid),OfferPrice[playerid][7]);
        SendClientMessage(Offer[playerid][7],COLOR_NEWS,string);
        format(string,sizeof(string),"Jûs nupirkote ið jo %s garaþà uþ $%d.",GetPlayerNameEx(Offer[playerid][7]),OfferPrice[playerid][7]);
        SendClientMessage(playerid,COLOR_NEWS,string);
        GivePlayerMoney(Offer[playerid][7],OfferPrice[playerid][7]);
        GivePlayerMoney(playerid,-OfferPrice[playerid][7]);
        PayLog( pInfo[ Offer[ playerid ][ 7 ] ][ pMySQLID ],13, pInfo[ playerid ][ pMySQLID ], OfferPrice[playerid][7] );
        PayLog( pInfo[ playerid ][ pMySQLID ],12, pInfo[ Offer[ playerid ][ 7 ] ][ pMySQLID ], OfferPrice[playerid][7] );        gInfo[OfferID[playerid][7]][gOwner] = pInfo[ playerid ][ pMySQLID ];
        UpdateGarageEntrance(OfferID[playerid][7]);
        SaveGarage(OfferID[playerid][7]);
        SaveAccount( playerid );
        SaveAccount( Offer[ playerid ][ 7 ] );
        PlayerPlaySound(playerid, 1057, 0.0, 0.0, 0.0);
        Offer[playerid][7] = 255;
        OfferPrice[playerid][7] = 0;
        OfferID[playerid][7] = 0;
        return 1;
    }
    else if ( !strcmp( "frisk", accept, true ) )
    {
        string = strtok(params, idx);
        giveplayerid = strval( string );
        if ( GetPVarInt( playerid, "APIESKA" ) != giveplayerid )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Jis nepraðo leidimo apieðkoti jûsø. " );
        if ( !PlayerToPlayer   ( 5.0, playerid, giveplayerid ) )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti ðio veiksmo veikëjui, kuris nëra ðalia Jûsø..");

        SendClientMessage( giveplayerid, COLOR_GREEN2, "_____________________ Turimi daiktai __________________");
        format( string, 56, "Pinigø: %d ", GetPlayerMoney(playerid) );
        SendClientMessage( giveplayerid, COLOR_WHITE, string );

        ShowPlayerInvInfoForPlayer(playerid, giveplayerid);
        for ( new i = 0; i < 11; i++ )
        {
            new wep,
                ammo,
                wepname[ 24 ];
            GetPlayerWeaponData( playerid, i, wep, ammo );
            if ( wep > 0 )
            {
                GetWeaponName    ( wep, wepname, 24 );
                format           ( string, 50," Ginklas %s ðoviniø %d ", wepname, ammo );
                SendClientMessage( giveplayerid, COLOR_FADE1, string );
            }
        }
        format      ( string, 70, "* %s apiëðko %s ." ,GetPlayerNameEx( giveplayerid ), GetPlayerNameEx( playerid ) );
        ProxDetector( 20.0, giveplayerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
        return 1;
    }
    else if(!strcmp(accept, "fine", true))
    {
        new Alloc:mem = Alloc:GetPVarInt(playerid, "FineOfferMemory"),
            sellerid,
            price,
            query[256];
        if(!mem)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Niekas tau nesiûlo baudos.");

        sellerid = mget(mem, 0);
        price = mget(mem, 1);
        mgets(string, sizeof string, mem, 2);
        free(mem);
        DeletePVar(playerid, "FineOfferMemory");

        if(!IsPlayerConnected(sellerid))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");
        if(!IsPlayerInRangeOfPlayer(playerid, sellerid, 4.0))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Tu nesi ðialia prië tau siølanèio þmogaus");
        ///if(GetPlayerMoney(playerid) < price)
        // pasirod moketi turi visada :|
         //   return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jums neuþtenka pinigø.");

        mysql_real_escape_string(string, string);
        format(query, sizeof(query), "INSERT INTO `tickets` (name,crime,reporter,price) VALUES ('%s','%s','%s','%d')",GetPlayerNameEx(playerid),string,GetName(sellerid),price);
        mysql_query(DbHandle, query, false);

        format( query, sizeof(query), " ** Jûs iðraðëte baudos lapelá %s'ui. ",GetPlayerNameEx(playerid) );
        SendClientMessage( sellerid, COLOR_WHITE, query );

        SendClientMessage(playerid, COLOR_WHITE, " ** Sumokëjote baudà.");

        GivePlayerMoney(playerid, -price);
        return 1;
    }
    else if(!strcmp(accept, "blindfold", true))
    {
        string = strtok(params, idx);
        new targetid = strval(string);

        if(Offer[ playerid ][ 8 ] == INVALID_PLAYER_ID)
            SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: jums nieko nesiûlo uþriðti raiðèio.");

        else if(Offer[ playerid ][ 8 ] != targetid)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Jis nepraðo leidimo uþriðti jums raiðtá ant akiø.");

        else if(!IsPlayerConnected(Offer[ playerid ][ 8 ]))
            SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");

        else if(!IsPlayerInRangeOfPlayer(playerid, Offer[ playerid ][ 8 ], 5.0))
            SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: þaidëjas nëra prie jûsø.");

        else 
        {
            format(string, sizeof(string), " uþriða %s raiðtá ant galvos uþdengdamas akis.", GetPlayerNameEx(playerid));
            cmd_me(targetid, string);

            TextDrawShowForPlayer(playerid, BlindfoldTextdraw);
            SetPlayerCameraPos(playerid, 0.0, 0.0, 1000.0);
            SetPlayerCameraLookAt(playerid, 0.0, 0.0, 1005.0);
            IsBlindfolded[ playerid ] = true;
        }
        Offer[ playerid ][ 8 ] = INVALID_PLAYER_ID;
    }
    return 1;
}
*/

CMD:stop( playerid, params[ ] )
{
    if(VehicleLoadTimer[ playerid ] != -1)
    {
        new vehicleid = GetPVarInt(playerid,"vehicleid");
        if(IsValidVehicle(vehicleid) && IsVehicleLoaded[ vehicleid ])
            IsVehicleLoaded[ vehicleid ] = false;
        // Jei tai buvo priekaba, leidziam kurti vilkika.
        if(IsValidVehicle(GetTrailerPullingVehicle(vehicleid)))
            IsVehicleLoaded[ GetTrailerPullingVehicle(vehicleid) ] = false;
        KillTimer(VehicleLoadTimer[ playerid ]);
		PlayerTextDrawHide(playerid, InfoText[playerid]);
        VehicleLoadTimer[ playerid ] = -1;
		VehicleLoadTime[ playerid ] = 0;
        return 1;
    }
    if ( LaikoTipas[ playerid ] == 0 ) return 1;
    Laikas    [ playerid ] = 0;
    LaikoTipas[ playerid ] = 0;

    if ( Checkpoint[ playerid ] != CHECKPOINT_NONE )
    {
        DisablePlayerCheckpoint( playerid );
        Checkpoint[ playerid ] = CHECKPOINT_NONE;
    }
    if(CheckUnfreeze(playerid))
        TogglePlayerControllable(playerid,true);
    

    return 1;
}
CMD:canceloffer( playerid, params[ ] )
{
    new
        giveplayerid,
        string[ 64 ];
        
    if ( sscanf( params, "u", giveplayerid ) ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /canceloffer [þaidëjo id]");
    if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");
    for(new i = 0; i > 5; i ++)
    {
        if(Offer[giveplayerid][i] == playerid)
        {
            SendClientMessage(playerid,GRAD,"Jûs atðaukëte pasiûlimá .");
            format(string,sizeof(string),"%s atðaukë savo pasiûlimá .",GetPlayerNameEx(playerid));
            SendClientMessage(giveplayerid,GRAD,string);
            Offer[giveplayerid][i] = 255;
            return 1;
        }
    }
    return 1;
}

CMD:anims( playerid, params[ ] )
{
    SendClientMessage(playerid,COLOR_GREEN,"________________________ Animacijos ________________________");
    SendClientMessage(playerid,COLOR_WHITE,"/fall /injured /push /handsup /kiss /cell /slapass /bomb /drunk /laugh /facepalm");
    SendClientMessage(playerid,COLOR_FADE1,"/basketball /medic /spraycan /robman /taichi /lookout /sit /lay /sup /crossarms");
    SendClientMessage(playerid,COLOR_WHITE,"/deal /crack /smoke /bar /hike /dance /fuck /lean /walk /rap /caract /sex");
    SendClientMessage(playerid,COLOR_FADE1,"/tired /box /scratch /hide /vomit /eats /cop /stance /wave /rap /skick /aload");
    SendClientMessage(playerid,COLOR_WHITE,"/flag /giver /look /show /shout /endchat /face /gsign /dj /loudtalk");
    SendClientMessage(playerid,COLOR_FADE1,"/rem /lift /place /yes /no /bag /wank /pee /riot /walk /knife /bat");
    SendClientMessage(playerid,COLOR_WHITE,"/lebelly /leface /ahouse /talk");
    SendClientMessage(playerid,COLOR_GREEN,"____________________________________________________________");
    return true;
}
CMD:gsign( playerid, params[ ] )
{
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /gsign [1-9]");
    switch ( animid )
    {
        case 1: LoopingAnim(playerid,"GHANDS","gsign2",4.1,1,1,1,1,1);
        case 2: LoopingAnim(playerid,"GHANDS","gsign3",4.1,1,1,1,1,1);
        case 3: LoopingAnim(playerid,"GHANDS","gsign4",4.1,1,1,1,1,1);
        case 4: LoopingAnim(playerid,"GHANDS","gsign5",4.1,1,1,1,1,1);
        case 5: LoopingAnim(playerid,"GHANDS","gsign1LH",4.1,1,1,1,1,1);
        case 6: LoopingAnim(playerid,"GHANDS","gsign2LH",4.1,1,1,1,1,1);
        case 7: LoopingAnim(playerid,"GHANDS","gsign3LH",4.1,1,1,1,1,1);
        case 8: LoopingAnim(playerid,"GHANDS","gsign4LH",4.1,1,1,1,1,1);
        case 9: LoopingAnim(playerid,"GHANDS","gsign5LH",4.1,1,1,1,1,1);
    }
    return 1;
}
//-----------------------------------[InCarAnims]----------------------------------------------
CMD:caract( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER)
    {
        SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Ðis veiksmas galimas tik automobilyje.");
        return 1;
    }
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /caract [1-7]");
    switch ( animid )
    {
        case 1: LoopingAnim(playerid,"PED","TAP_HAND",4.0,1,0,0,0,0);
        case 2: LoopingAnim(playerid,"CAR", "sit_relaxed", 4.0, 1, 0, 0, 0, 0);
        case 3: LoopingAnim(playerid,"CAR", "tap_hand", 4.0, 1, 0, 0, 0, 0);
        case 4: BackAnim(playerid,"CAR_CHAT", "carfone_in", 4.0,0,1,1,1,0,3);
        case 5: LoopingAnim(playerid,"CAR_CHAT", "carfone_loopa", 4.0, 1, 0, 0, 0, 0);
        case 6: LoopingAnim(playerid,"CAR_CHAT", "carfone_loopb", 4.0, 1, 0, 0, 0, 0);
        case 7: OnePlayAnim(playerid,"DRIVEBYS","Gang_DrivebyLHS",3.0,0,0,0,0,0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /caract [1-7]");
    }
    return 1;
}
//------------------------------------[OnFootAnims]--------------------------------------------
CMD:scratch( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    LoopingAnim(playerid,"MISC","Scratchballs_01",3.0,1,0,0,0,0);
    return 1;
}
CMD:giver( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    OnePlayAnim(playerid,"KISSING","gift_give",3.0,0,0,0,0,0);
    return 1;
}
CMD:facepalm( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim(playerid, "MISC", "plyr_shkhead",4.1,0,1,1,0,0);
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}
CMD:dj(playerid, params[])
{
    if(GetPlayerState(playerid) != 1) return SendClientMessage(playerid, COLOR_GREY, "Animacijá  galima naudoti stovint ant kojø.");
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /dj [1-4]");
    switch(animid)
    {
        case 1: LoopingAnim(playerid, "SCRATCHING", "scdldlp", 4.0, 1, 0, 0, 0, 0);
        case 2: LoopingAnim(playerid, "SCRATCHING", "scdlulp", 4.0, 1, 0, 0, 0, 0);
        case 3: LoopingAnim(playerid, "SCRATCHING", "scdrdlp", 4.0, 1, 0, 0, 0, 0);
        case 4: LoopingAnim(playerid, "SCRATCHING", "scdrulp", 4.0, 1, 0, 0, 0, 0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /dj [1-4]");
    }
    return 1;
}
CMD:face( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /face [1-6]");
    switch ( animid )
    {
        case 1: LoopingAnim(playerid,"PED","facanger",3.0,1,1,1,1,1);
        case 2: LoopingAnim(playerid,"PED","facgum",3.0,1,1,1,1,1);
        case 3: LoopingAnim(playerid,"PED","facsurp",3.0,1,1,1,1,1);
        case 4: LoopingAnim(playerid,"PED","facsurpm",3.0,1,1,1,1,1);
        case 5: LoopingAnim(playerid,"PED","factalk",3.0,1,1,1,1,1);
        case 6: LoopingAnim(playerid,"PED","facurios",3.0,1,1,1,1,1);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /face [1-6]");
    }
    return 1;
}
CMD:endchat( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /endchat [1-3]");
    switch ( animid )
    {
        case 1: OnePlayAnim(playerid,"PED","endchat_01",8.0,0,0,0,0,0);
        case 2: OnePlayAnim(playerid,"PED","endchat_02",8.0,0,0,0,0,0);
        case 3: OnePlayAnim(playerid,"PED","endchat_03",8.0,0,0,0,0,0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /endchat [1-3]");
    }
    return 1;
}
CMD:show( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    LoopingAnim(playerid,"ON_LOOKERS","point_loop",3.0,1,0,0,0,0);
    return 1;
}
CMD:shout( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /shout [1-3]");
    switch ( animid )
    {
        case 1: BackAnim(playerid,"ON_LOOKERS","shout_loop",3.0,1,0,0,0,0,6);
        case 2: LoopingAnim(playerid,"ON_LOOKERS","shout_01",3.0,1,0,0,0,0);
        case 3: LoopingAnim(playerid,"ON_LOOKERS","shout_02",3.0,1,0,0,0,0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /shout [1-3]");
    }
    return 1;
}
CMD:look( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /look [1-3]");
    switch ( animid )
    {
        case 1: LoopingAnim(playerid,"ON_LOOKERS","lkup_loop",3.0,1,0,0,0,0);
        case 2: LoopingAnim(playerid,"ON_LOOKERS","lkaround_loop",3.0,1,0,0,0,0);
        case 3: LoopingAnim(playerid,"PED","flee_lkaround_01",3.0,1,1,1,1,1);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /look [1-3]");
    }
    return 1;
}
CMD:flag( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    OnePlayAnim(playerid,"CAR","flag_drop",3.0,0,0,0,0,0);
    return 1;
}
CMD:cell( playerid, params[ ] )
{
	if(Freezed[ playerid ])
		return 1;
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    ClearAnimations(playerid);
    SetPlayerSpecialAction(playerid,SPECIAL_ACTION_USECELLPHONE);
    BackOut[playerid] = 2;
    return 1;
}
CMD:handsup(playerid)
{
	if(Freezed[ playerid ])
		return 1;
		
    if(GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        SetPlayerSpecialAction(playerid, SPECIAL_ACTION_HANDSUP);
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");
    return true;
}
CMD:drunk( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /drunk [1-3]");
    switch ( animid )
    {
        case 1: LoopingAnim(playerid,"PED","WALK_DRUNK",4.1,1,1,1,1,1);
        case 2: LoopingAnim(playerid,"PAULNMAC", "pnm_loop_a", 3.0, 1, 0, 0, 0, 0);
        case 3: LoopingAnim(playerid,"PAULNMAC", "pnm_loop_b", 3.0, 1, 0, 0, 0, 0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /drunk [1-3]");
    }
    return 1;
}
CMD:bomb( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /bomb [1-2]");
    switch ( animid )
    {
        case 1: LoopingAnim(playerid, "BOMBER","BOM_Plant_Loop",4.0,1,0,0,1,0);
        case 2: OnePlayAnim(playerid,"MISC", "plunger_01", 2.0, 0, 0, 0, 0, 0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /bomb [1-2]");
    }
    return 1;
}
CMD:laugh( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    OnePlayAnim(playerid, "RAPPING", "Laugh_01", 4.0, 0, 0, 0, 0, 0);
    return 1;
}
CMD:lookout( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /lookout [1-2]");
    switch ( animid )
    {
        case 1: OnePlayAnim(playerid, "FOOD", "eat_vomit_sk", 4.0,0,0,0,0,0);
        case 2: OnePlayAnim(playerid, "PED", "handscower", 4.0,0,1,1,1,1);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /lookout [1-2]");
    }
    return 1;
}
CMD:sex(playerid, params[])
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT)
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø.");
    

    new id;

    if (sscanf(params, "d", id)) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /sex [1-7]");

    switch(id)
    {
        case 1: LoopingAnim(playerid, "BLOWJOBZ", "BJ_COUCH_START_W", 4.0, 0, 1, 1, 1, 0);
        case 2: LoopingAnim(playerid, "BLOWJOBZ", "BJ_COUCH_LOOP_W", 4.0, 0, 1, 1, 1, 0);
        case 3: LoopingAnim(playerid, "BLOWJOBZ", "BJ_COUCH_END_W", 4.0, 0, 1, 1, 1, 0);
        case 4: LoopingAnim(playerid, "BLOWJOBZ", "BJ_COUCH_START_P", 4.0, 0, 1, 1, 1, 0);
        case 5: LoopingAnim(playerid, "BLOWJOBZ", "BJ_COUCH_LOOP_P", 4.0, 0, 1, 1, 1, 0);
        case 6: LoopingAnim(playerid, "BLOWJOBZ", "BJ_COUCH_END_P", 4.0, 0, 1, 1, 1, 0);
        case 7: LoopingAnim(playerid, "BLOWJOBZ", "BJ_STAND_START_W", 4.0, 0, 1, 1, 1, 0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /sex [1-7]");
    }
    return 1;
}
CMD:robman( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /robman [1-2]");
    switch ( animid )
    {
        case 1: LoopingAnim(playerid, "SHOP", "ROB_Loop_Threat", 4.0, 1, 0, 0, 0, 0); // Rob
        case 2: LoopingAnim(playerid,"PED", "gang_gunstand", 4.0,1,0,0,0,0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /robman [1-2]");
    }
    return 1;
}
CMD:crossarms( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /crossarms [1-2]");
    switch ( animid )
    {
        case 1: LoopingAnim(playerid, "COP_AMBIENT", "Coplook_loop", 4.0, 0, 1, 1, 1, -1);
        case 2: LoopingAnim(playerid,"OTB", "wtchrace_loop", 4.0, 1, 0, 0, 0, 0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /crossarms [1-2]");
    }
    return 1;
}
CMD:lay( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /lay [1-10]");
    switch ( animid )
    {
        case 1: LoopingAnim(playerid,"BEACH", "bather", 4.0, 1, 0, 0, 0, 0);
        case 2: LoopingAnim(playerid,"BEACH", "parksit_w_loop", 4.0, 1, 0, 0, 0, 0);
        case 3: LoopingAnim(playerid,"BEACH","parksit_m_loop", 4.0, 1, 0, 0, 0, 0);
        case 4: LoopingAnim(playerid,"BEACH","lay_bac_loop", 4.0, 1, 0, 0, 0, 0);
        case 5: LoopingAnim(playerid,"BEACH","sitnwait_loop_w", 4.0, 1, 0, 0, 0, 0);
        case 6: BackAnim(playerid,"SUNBATHE","Lay_Bac_in",3.0,0,1,1,1,0,5);
        case 7: LoopingAnim(playerid,"SUNBATHE","batherdown",3.0,0,1,1,1,0);
        case 8: BackAnim(playerid,"SUNBATHE","parksit_m_in",3.0,0,1,1,1,0,1);
        case 9: LoopingAnim(playerid,"CAR", "Fixn_Car_Loop", 4.0, 1, 0, 0, 0, 0);
        case 10: LoopingAnim(playerid, "CRACK", "CRCKIDLE4", 4.0, 1, 0, 0, 0, 0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /lay [1-9]");
    }
    return 1;
}
CMD:hide( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /hide [1-2]");
    switch ( animid )
    {
        case 1: LoopingAnim(playerid, "ped", "cower", 3.0, 1, 0, 0, 0, 0);
        case 2: LoopingAnim(playerid,"ON_LOOKERS","panic_hide",3.0,1,0,0,0,0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /hide [1-2]");
    }
    return 1;
}
CMD:vomit( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    OnePlayAnim(playerid, "FOOD", "EAT_Vomit_P", 3.0, 0, 0, 0, 0, 0); // Vomit BAH!
    return 1;
}
CMD:eats( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    OnePlayAnim(playerid, "FOOD", "EAT_Burger", 3.0, 0, 0, 0, 0, 0); // Eat Burger
    return 1;
}
CMD:wave( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /wave [1-5]");
    switch ( animid )
    {
        case 1: LoopingAnim(playerid, "ON_LOOKERS", "wave_loop", 4.0, 1, 0, 0, 0, 0);
        case 2: OnePlayAnim(playerid,"BD_Fire", "BD_GF_Wave", 4.0, 0, 0, 0, 0, 0);
        case 3: LoopingAnim(playerid,"RIOT","RIOT_CHANT",4.0,1,1,1,1,0);
        case 4: OnePlayAnim(playerid,"WUZI", "Wuzi_Follow", 5.0, 0, 0, 0, 0, 0);
        case 5: OnePlayAnim(playerid,"KISSING", "gfwave2", 4.0, 0, 0, 0, 0, 0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /wave [1-5]");
    }
    return 1;
}
CMD:slapass( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /slapass [1-2]");
    switch ( animid )
    {
        case 1: OnePlayAnim(playerid, "SWEET", "sweet_ass_slap", 4.0, 0, 0, 0, 0, 0);
        case 2: LoopingAnim(playerid,"MISC","Bitchslap",4.0,1,0,0,0,0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /slap [1-2]");
    }
    return 1;
}
CMD:deal( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    OnePlayAnim(playerid, "DEALER", "DEALER_DEAL", 4.0, 0, 0, 0, 0, 0); // Deal Drugs
    return 1;
}
CMD:smoke( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /smoke [1-6]");
    switch ( animid )
    {
        case 1: LoopingAnim(playerid,"SMOKING", "M_smklean_loop", 4.0, 1, 0, 0, 0, 0); // male
        case 2: LoopingAnim(playerid,"SMOKING", "F_smklean_loop", 4.0, 1, 0, 0, 0, 0); //female
        case 3: LoopingAnim(playerid,"SMOKING","M_smkstnd_loop", 4.0, 1, 0, 0, 0, 0); // standing-fucked
        case 4: OnePlayAnim(playerid,"SMOKING","M_smk_out", 4.0, 0, 0, 0, 0, 0); // standing
        case 5: OnePlayAnim(playerid,"SMOKING","M_smk_in",3.0,0,0,0,0,0);
        case 6: OnePlayAnim(playerid,"SMOKING","M_smk_tap",3.0,0,0,0,0,0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /smoke [1-6]");
    }
    return 1;
}
CMD:bar( playerid, params[ ] )
{
    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
    {
        new
            id;

        if ( sscanf( params, "d", id ) )
            SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /bar [1-12]");
        else
        {
            switch ( id )
            {
                case 1: LoopingAnim( playerid, "BAR", "Barcustom_get", 3.1, 0, 1, 1, 1, 1);
                case 2: LoopingAnim( playerid, "BAR", "Barcustom_loop", 3.1, 1, 1, 1, 1, 1);
                case 3: LoopingAnim( playerid, "BAR", "Barcustom_order", 3.1, 0, 1, 1, 1, 1);
                case 4: LoopingAnim( playerid, "BAR", "BARman_idle", 3.1, 0, 1, 1, 1, 1);
                case 5: LoopingAnim( playerid, "BAR", "Barserve_bottle", 3.1, 0, 1, 1, 1, 1);
                case 6: LoopingAnim( playerid, "BAR", "Barserve_give", 3.1, 0, 1, 1, 1, 1);
                case 7: LoopingAnim( playerid, "BAR", "Barserve_glass", 3.1, 0, 1, 1, 1, 1);
                case 8: LoopingAnim( playerid, "BAR", "Barserve_in", 3.1, 0, 1, 1, 1, 1);
                case 9: LoopingAnim( playerid, "BAR", "Barserve_loop", 3.1, 1, 1, 1, 1, 1);
                case 10: LoopingAnim( playerid, "BAR", "Barserve_order", 3.1, 0, 1, 1, 1, 1);
                case 11: LoopingAnim( playerid, "BAR", "dnk_stndF_loop", 3.1, 1, 1, 1, 1, 1);
                case 12: LoopingAnim( playerid, "BAR", "dnk_stndM_loop", 3.1, 1, 1, 1, 1, 1);
                default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /bar [1-12]");
            }
        }
    }
    return true;
}
CMD:hike( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /hike [1-3]");
    switch ( animid )
    {
        case 1: LoopingAnim(playerid,"MISC","hiker_pose",4.0,1,0,0,0,0);
        case 2: LoopingAnim(playerid,"MISC","hiker_pose_l",4.0,1,0,0,0,0);
        case 3: OnePlayAnim(playerid,"PED","idle_taxi",3.0,0,0,0,0,0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /hike [1-3]");
    }
    return 1;
}
CMD:fuck( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /fuck [1-2]");
    switch ( animid )
    {
        case 1: OnePlayAnim(playerid,"PED","fucku",4.0,0,0,0,0,0);
        case 2: OnePlayAnim(playerid,"RIOT","RIOT_FUKU",2.0,0,0,0,0,0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /fuck [1-2]");
    }
    return 1;
}
CMD:taichi( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    LoopingAnim(playerid,"PARK","Tai_Chi_Loop",4.0,1,0,0,0,0);
    return 1;
}
CMD:sit( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /sit [1-6]");
    switch ( animid )
    {
        case 1: BackAnim(playerid,"PED","SEAT_down",4.1,0,0,0,1,-1,8);
        case 2: LoopingAnim(playerid,"MISC","seat_lr",2.0,1,0,0,0,0);
        case 3: LoopingAnim(playerid,"MISC","seat_talk_01",2.0,1,0,0,0,0);
        case 4: LoopingAnim(playerid,"MISC","seat_talk_02",2.0,1,0,0,0,0);
        case 5 : LoopingAnim( playerid, "PED", "SEAT_idle", 4.1, 0, 0, 0, 1, 1 );
        case 6 : LoopingAnim( playerid, "CRACK", "crckidle1", 3.1, 0, 1, 1, 1, 1);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /sit [1-6]");
    }
    return 1;
}
CMD:talk( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "PED", "IDLE_CHAT", 4.0, 1, 0, 0, 1, 1 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}
CMD:fall( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /fall [1-3]");
    switch ( animid )
    {
        case 1: LoopingAnim(playerid,"PED","KO_skid_front",4.1,0,1,1,1,0);
        case 2: LoopingAnim(playerid, "PED","FLOOR_hit_f", 4.0, 1, 0, 0, 0, 0);
        case 3: LoopingAnim(playerid,"PED","KO_skid_back",4.1,0,1,1,1,0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /fall [1-2]");
    }
    return 1;
}
CMD:kiss( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /kiss [1-2]");
    switch ( animid )
    {
        case 1: OnePlayAnim(playerid, "KISSING", "Playa_Kiss_02", 3.0, 0, 0, 0, 0, 0);
        case 2: OnePlayAnim(playerid, "BD_Fire", "grlfrd_kiss_03", 2.0, 0, 0, 0, 0, 0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /kiss [1-2]");
    }
    return 1;
}
CMD:injured( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /injured [1-4]");
    switch ( animid )
    {
        case 1: LoopingAnim(playerid, "SWEET", "Sweet_injuredloop", 4.0, 1, 0, 0, 0, 0);
        case 2: LoopingAnim(playerid, "WUZI", "CS_Dead_Guy", 4.0, 1, 1, 1, 1, 1);
        case 3: LoopingAnim(playerid, "PED", "gas_cwr", 4.0, 1, 1, 1, 1, 1);
        case 4: LoopingAnim(playerid, "FINALE", "FIN_Cop1_Loop", 4.0, 1, 0, 0, 0, 0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /injured [1-4]");
    }
    return 1;
}
CMD:rap( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /rap [1-11]");
    switch ( animid )
    {
        case 1 : LoopingAnim( playerid, "RAPPING", "RAP_A_Loop", 4.0, 1, 1, 1, 1, 1 );
        case 2 : LoopingAnim( playerid, "RAPPING", "RAP_C_Loop", 4.0, 1, 1, 1, 1, 1 );
        case 3 : LoopingAnim( playerid, "GANGS", "prtial_gngtlkA", 4.0, 1, 1, 1, 1, 1 );
        case 4 : LoopingAnim( playerid, "GANGS", "prtial_gngtlkB", 4.0, 1, 1, 1, 1, 1 );
        case 5 : LoopingAnim( playerid, "GANGS", "prtial_gngtlkC", 4.0, 1, 1, 1, 1, 1 );
        case 6 : LoopingAnim( playerid, "GANGS", "prtial_gngtlkD", 4.0, 1, 1, 1, 1, 1 );
        case 7 : LoopingAnim( playerid, "GANGS", "prtial_gngtlkE", 4.0, 1, 1, 1, 1, 1 );
        case 8 : LoopingAnim( playerid, "GANGS", "prtial_gngtlkF", 4.0, 1, 1, 1, 1, 1 );
        case 9 : LoopingAnim( playerid, "GANGS", "prtial_gngtlkG", 4.0, 1, 1, 1, 1, 1 );
        case 10 : LoopingAnim( playerid, "GANGS", "prtial_gngtlkH", 4.0, 1, 1, 1, 1, 1 );
        case 11 : LoopingAnim(playerid, "RAPPING", "RAP_B_Loop", 4.0, 1, 1, 1, 1, 1 );
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /rap [1-11]");
    }
    return 1;
}
CMD:push( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /push [1-2]");
    switch ( animid )
    {
        case 1: OnePlayAnim(playerid,"GANGS","shake_cara",4.0,0,0,0,0,0);
        case 2: OnePlayAnim(playerid,"GANGS","shake_carSH",4.0,0,0,0,0,0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /push [1-2]");
    }
    return 1;
}
CMD:spraycan(playerid)
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    OnePlayAnim(playerid,"SPRAYCAN","spraycan_full",4.0,0,0,0,0,0);
    return 1;
}
CMD:skick( playerid, params[ ] )
{
    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
    {
        new
            id;

        if ( sscanf( params, "d", id ) ) SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /skick [1-4]");
        else
        {
            switch ( id )
            {
                case 1 : LoopingAnim( playerid, "POLICE", "Door_Kick", 4.0, 0, 0, 0, 0, 0 );
                case 2 : LoopingAnim( playerid, "FIGHT_D", "FightD_2", 4.0, 0, 1, 1, 0, 0 );
                case 3 : LoopingAnim( playerid, "FIGHT_C", "FightC_M", 4.0, 0, 1, 1, 0, 0 );
                case 4 : LoopingAnim( playerid, "FIGHT_D", "FightD_G", 4.0, 0, 0, 0, 0, 0 );
                default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /skick [1-4]");
            }
        }
    }
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}
CMD:medic( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    OnePlayAnim(playerid,"MEDIC","CPR",4.0,0,0,0,0,0);
    return 1;
}
CMD:tired( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /tired [1-2]");
    switch ( animid )
    {
        case 1: LoopingAnim(playerid,"PED","IDLE_tired",3.0,1,0,0,0,0);
        case 2: OnePlayAnim(playerid,"FAT","Idle_Tired",3.0,1,0,0,0,0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /tired [1-2]");
    }
    return 1;
}
CMD:box( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    LoopingAnim(playerid,"GYMNASIUM","GYMshadowbox",4.0,1,1,1,1,0);
    return 1;
}
CMD:leface( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "PED", "KO_shot_face", 4.0, 0, 1, 1, 1, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}
CMD:lebelly( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "PED", "KO_shot_stom", 4.0, 0, 1, 1, 1, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}
CMD:crack( playerid, params[ ] )
{
    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
    {
        new
            id;

        if ( sscanf( params, "d", id ) ) SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /crack [1-6]");
        else
        {
            switch ( id )
            {
                case 1 : LoopingAnim( playerid, "CRACK", "crckdeth1", 4.0, 1, 0, 0, 0, 0 );
                case 2 : LoopingAnim( playerid, "CRACK", "crckdeth2", 4.0, 1, 0, 0, 0, 0 );
                case 3 : LoopingAnim( playerid, "CRACK", "crckdeth3", 4.0, 1, 0, 0, 0, 0 );
                case 4 : LoopingAnim( playerid, "CRACK", "crckidle2", 4.0, 0, 0, 0, 0, 0 );
                case 5 : LoopingAnim( playerid, "CRACK", "crckidle3", 4.0, 1, 0, 0, 0, 0 );
                case 6 : LoopingAnim( playerid, "CRACK", "crckidle4", 4.0, 1, 0, 0, 0, 0 );
                default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /crack [1-6]");
            }
        }
    }
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}
CMD:bat( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /bat [1-2]");
    switch(animid) {
        case 1: LoopingAnim(playerid, "CRACK", "Bbalbat_Idle_01", 4.0, 1, 0, 0, 0, 0);
        case 2: LoopingAnim(playerid, "CRACK", "Bbalbat_Idle_02", 4.0, 1, 0, 0, 0, 0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /bat [1-2]");
    }
    return true;
}
CMD:ahouse( playerid, params[ ] )
{
    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
    {
        new
            id;

        if ( sscanf( params, "d", id ) ) SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /ahouse [1-6]");
        else
        {
            switch ( id )
            {
                case 1: LoopingAnim(playerid, "INT_HOUSE", "BED_In_L", 4.1, 0, 1, 1, 1, 1);
                case 2: LoopingAnim(playerid, "INT_HOUSE", "BED_In_R", 4.1, 0, 1, 1, 1, 1);
                case 3: LoopingAnim(playerid, "INT_HOUSE", "BED_Out_L", 4.1, 0, 1, 1, 1, 1);
                case 4: LoopingAnim(playerid, "INT_HOUSE", "BED_Out_R", 4.1, 0, 1, 1, 1, 1);
                case 5: LoopingAnim(playerid, "INT_HOUSE", "LOU_Loop", 4.1, 0, 1, 1, 1, 1);
                case 6: LoopingAnim(playerid, "INT_HOUSE", "LOU_Out", 4.1, 0, 1, 1, 1, 1);
                default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /ahouse [1-6]");
            }
        }
    }
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}
CMD:cop( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /cop [1-19]");
    switch ( animid )
    {
        case 1: LoopingAnim(playerid,"SWORD", "sword_IDLE", 4.0, 0, 1, 1, 1, -1);
        case 2: LoopingAnim(playerid,"POLICE","CopTraf_away",4.0,1,0,0,0,0);
        case 3: LoopingAnim(playerid,"POLICE","CopTraf_come",4.0,1,0,0,0,0);
        case 4: LoopingAnim(playerid,"POLICE","CopTraf_left",4.0,1,0,0,0,0);
        case 5: LoopingAnim(playerid,"POLICE","CopTraf_stop",4.0,1,0,0,0,0);
        case 6: LoopingAnim(playerid,"POLICE","Cop_move_fwd",4.0,1,1,1,1,1);
        case 7: LoopingAnim(playerid,"ped", "ARRESTgun", 4.0, 0, 1, 1, 1, -1);
        case 8: OnePlayAnim(playerid, "COP_AMBIENT", "Copbrowse_in", 4.1, 0, 0, 0, 0, 0);
        case 9: OnePlayAnim(playerid, "COP_AMBIENT", "Copbrowse_loop", 4.1, 0, 0, 0, 0, 0);
        case 10: OnePlayAnim(playerid, "COP_AMBIENT", "Copbrowse_nod", 4.1, 0, 0, 0, 0, 0);
        case 11: OnePlayAnim(playerid, "COP_AMBIENT", "Copbrowse_out", 4.1, 0, 0, 0, 0, 0);
        case 12: OnePlayAnim(playerid, "COP_AMBIENT", "Copbrowse_shake", 4.1, 0, 0, 0, 0, 0);
        case 13: OnePlayAnim(playerid, "COP_AMBIENT", "Coplook_in", 4.1, 0, 0, 0, 0, 0);
        case 14: OnePlayAnim(playerid, "COP_AMBIENT", "Coplook_loop", 4.1, 0, 0, 0, 0, 0);
        case 15: OnePlayAnim(playerid, "COP_AMBIENT", "Coplook_nod", 4.1, 0, 0, 0, 0, 0);
        case 16: OnePlayAnim(playerid, "COP_AMBIENT", "Coplook_out", 4.1, 0, 0, 0, 0, 0);
        case 17: OnePlayAnim(playerid, "COP_AMBIENT", "Coplook_shake", 4.1, 0, 0, 0, 0, 0);
        case 18: OnePlayAnim(playerid, "COP_AMBIENT", "Coplook_think", 4.1, 0, 0, 0, 0, 0);
        case 19: OnePlayAnim(playerid, "COP_AMBIENT", "Coplook_watch", 4.1, 0, 0, 0, 0, 0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /cop [1-19]");
    }
    return 1;
}

CMD:stance( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /stance [1-16]");
    if(animid == 1) LoopingAnim(playerid,"DEALER","DEALER_IDLE",4.0,1,0,0,0,0);
    else if(animid == 2) LoopingAnim(playerid,"PED","WOMAN_IDLESTANCE",4.0,1,0,0,0,0);
    else if(animid == 3) LoopingAnim(playerid,"PED","CAR_HOOKERTALK",4.0,1,0,0,0,0);
    else if(animid == 4) LoopingAnim(playerid,"FAT","FatIdle",4.0,1,0,0,0,0);
    else if(animid == 5) LoopingAnim(playerid,"WUZI","Wuzi_Stand_Loop",4.0,1,0,0,0,0);
    else if(animid == 6) LoopingAnim(playerid,"GRAVEYARD","mrnf_loop",4.0,1,0,0,0,0);
    else if(animid == 7) LoopingAnim(playerid,"GRAVEYARD","mrnm_loop",4.0,1,0,0,0,0);
    else if(animid == 8) LoopingAnim(playerid,"GRAVEYARD","prst_loopa",4.0,1,0,0,0,0);
    else if(animid == 9) LoopingAnim(playerid,"PED","idlestance_fat",4.0,1,0,0,0,0);
    else if(animid == 10) LoopingAnim(playerid,"PED","idlestance_old",4.0,1,0,0,0,0);
    else if(animid == 11) LoopingAnim(playerid,"PED","turn_l",4.0,1,0,0,0,0);
    else if(animid == 12) LoopingAnim( playerid, "DEALER", "DEALER_IDLE_01", 4.0, 0, 1, 1, 1, 0 );
    else if(animid == 13) LoopingAnim( playerid, "DEALER", "DEALER_IDLE_02", 4.0, 0, 1, 1, 1, 0 );
    else if(animid == 14) LoopingAnim( playerid, "DEALER", "DEALER_IDLE_03", 4.0, 0, 1, 1, 1, 0 );
    else if(animid == 15) LoopingAnim( playerid, "DEALER", "DRUGS_BUY", 4.0, 0, 1, 1, 1, 0 );
    else if(animid == 16) LoopingAnim( playerid, "DEALER", "shop_pay", 4.0, 0, 1, 1, 1, 0 );
    else SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /stance [1-16]");
    return 1;
}
CMD:basketball( playerid, params[ ] )
{
    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
    {
        new
            id;

        if ( sscanf( params, "d", id ) ) SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /basketball [1-5]");
        else
        {
            switch ( id )
            {
                case 1 : LoopingAnim( playerid, "BSKTBALL", "BBALL_idleloop", 4.0, 1, 0, 0, 0, 0 );
                case 2 : LoopingAnim( playerid, "BSKTBALL", "BBALL_Jump_Shot", 4.0, 0, 0, 0, 0, 0 );
                case 3 : LoopingAnim( playerid, "BSKTBALL", "BBALL_pickup", 4.0, 0, 0, 0, 0, 0 );
                case 4 : LoopingAnim( playerid, "BSKTBALL", "BBALL_run", 4.1, 1, 1, 1, 1, 1 );
                case 5 : LoopingAnim( playerid, "BSKTBALL", "BBALL_def_loop", 4.0, 1, 0, 0, 0, 0 );
                default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /basketball [1-5]");
            }
        }
    }
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}
CMD:lean( playerid, params[ ] )
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    new
        animid;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /lean [1-2]");
    switch ( animid )
    {
        case 1: LoopingAnim(playerid,"GANGS","leanIDLE",4.0,0,1,1,1,0);
        case 2: LoopingAnim(playerid,"MISC","Plyrlean_loop",4.0,0,1,1,1,0);
        default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /lean [1-2]");
    }
    return 1;
}
CMD:dance( playerid, params[ ] )
{
    if (GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
    {
        new
            id;

        if ( sscanf( params, "d", id ) ) SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /dance [1-13]");
        else
        {
            switch ( id )
            {
                case 1 : SetPlayerSpecialAction( playerid, SPECIAL_ACTION_DANCE1 );
                case 2 : SetPlayerSpecialAction( playerid, SPECIAL_ACTION_DANCE2 );
                case 3 : SetPlayerSpecialAction( playerid, SPECIAL_ACTION_DANCE3 );
                case 4 : SetPlayerSpecialAction( playerid, SPECIAL_ACTION_DANCE4 );
                case 5: LoopingAnim(playerid, "DANCING", "dance_loop", 4.1, 1, 1, 1, 1, 1);
                case 6: LoopingAnim(playerid, "DANCING", "DAN_Down_A", 4.1, 1, 1, 1, 1, 1);
                case 7: LoopingAnim(playerid, "DANCING", "DAN_Left_A", 4.1, 1, 1, 1, 1, 1);
                case 8: LoopingAnim(playerid, "DANCING", "DAN_Loop_A", 4.1, 1, 1, 1, 1, 1);
                case 9: LoopingAnim(playerid, "DANCING", "DAN_Right_A", 4.1, 1, 1, 1, 1, 1);
                case 10: LoopingAnim(playerid, "DANCING", "DAN_Up_A", 4.1, 1, 1, 1, 1, 1);
                case 11: LoopingAnim(playerid, "DANCING", "dnce_M_a", 4.1, 1, 1, 1, 1, 1);
                case 12: LoopingAnim(playerid, "DANCING", "dnce_M_b", 4.1, 1, 1, 1, 1, 1);
                case 13: LoopingAnim(playerid, "DANCING", "dnce_M_c", 4.1, 1, 1, 1, 1, 1);
                default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /dance [1-13]");
            }
        }
    }
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}
CMD:benchpress(playerid, params[])
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø.");

    new number;
    if(sscanf(params,"i", number))
    {
        benchpress_usage:
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Naudojimas /benchpress [1 - 7]");
    }


    switch(number)
    {
        case 1: OnePlayAnim(playerid, "benchpress", "gym_bp_celebrate", 4.1, 0, 0, 0, 0, 0);
        case 2: LoopingAnim(playerid, "benchpress", "gym_bp_down", 4.1, 1, 0, 0, 0, 0);
        case 3: OnePlayAnim(playerid, "benchpress", "gym_bp_getoff", 4.1, 0, 0, 0, 0, 0);
        case 4: OnePlayAnim(playerid, "benchpress", "gym_bp_geton", 4.1, 0, 0, 0, 0, 0);
        case 5: LoopingAnim(playerid, "benchpress", "gym_bp_up_A", 4.1, 1, 0, 0, 0, 0);       
        case 6: LoopingAnim(playerid, "benchpress", "gym_bp_up_B", 4.1, 1, 0, 0, 0, 0);       
        case 7: LoopingAnim(playerid, "benchpress", "gym_bp_up_smooth", 4.1, 1, 0, 0, 0, 0);  
        default: goto benchpress_usage;
    }
    return 1;
}

CMD:camera(playerid, params[])
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø.");

    new number;
    if(sscanf(params,"i", number))
    {
        camera_usage:
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Naudojimas /camera [1 - 14]");
    }

    switch(number)
    {
        case 1: OnePlayAnim(playerid, "CAMERA", "camcrch_cmon", 4.1, 0, 0, 0, 0, 0);
        case 2: LoopingAnim(playerid, "CAMERA", "camcrch_idleloop", 4.1, 1, 0, 0, 1, 1); 
        case 4: OnePlayAnim(playerid, "CAMERA", "camcrch_to_camstnd", 4.1, 0, 0, 0, 0, 0);
        case 5: OnePlayAnim(playerid, "CAMERA", "camstnd_cmon", 4.1, 0, 0, 0, 0, 0);
        case 6: LoopingAnim(playerid, "CAMERA", "camstnd_idleloop", 4.1, 1, 0, 0, 0, 0); 
        case 7: LoopingAnim(playerid, "CAMERA", "camstnd_lkabt", 4.1, 1, 0, 0, 0, 0);       
        case 8: OnePlayAnim(playerid, "CAMERA", "camstnd_to_camcrch", 4.1, 0, 0, 0, 0, 0);
        case 9: OnePlayAnim(playerid, "CAMERA", "piccrch_in", 4.1, 0, 0, 0, 0, 0);
        case 10: OnePlayAnim(playerid, "CAMERA", "piccrch_out", 4.1, 0, 0, 0, 0, 0);
        case 11: OnePlayAnim(playerid, "CAMERA", "piccrch_take", 4.1, 0, 0, 0, 0, 0);
        case 12: OnePlayAnim(playerid, "CAMERA", "picstnd_in", 4.1, 0, 0, 0, 0, 0);
        case 13: OnePlayAnim(playerid, "CAMERA", "picstnd_out", 4.1, 0, 0, 0, 0, 0);
        case 14: LoopingAnim(playerid, "CAMERA", "picstnd_take", 4.1, 1, 1, 1, 1, 1);
        default: goto camera_usage;
    }
    return 1;
}

CMD:carry(playerid, params[])
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø.");

    new number;
    if(sscanf(params,"i", number))
    {
        carry_usage:
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Naudojimas /carry [1 - 6]");
    }

    switch(number)
    {
        case 1: OnePlayAnim(playerid, "CARRY", "liftup", 4.1, 0, 0, 0, 0, 0);
        case 2: OnePlayAnim(playerid, "CARRY", "liftup05", 4.1, 0, 0, 0, 0, 0);
        case 3: OnePlayAnim(playerid, "CARRY", "liftup105", 4.1, 0, 0, 0, 0, 0);
        case 4: OnePlayAnim(playerid, "CARRY", "putdwn", 4.1, 0, 0, 0, 0, 0);
        case 5: OnePlayAnim(playerid, "CARRY", "putdwn05", 4.1, 0, 0, 0, 0, 0);
        case 6: OnePlayAnim(playerid, "CARRY", "putdwn105", 4.1, 0, 0, 0, 0, 0);
        default: goto carry_usage;
    }
    return 1;
}



CMD:pee(playerid, params[])
{
    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
    {
        SetPlayerSpecialAction( playerid, 68 );
    }
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}
CMD:knife( playerid, params[ ] )
{
    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
    {
        new
            id;

        if ( sscanf( params, "d", id ) ) SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /knife [1-4]");
        else
        {
            switch ( id )
            {
                case 1 : LoopingAnim( playerid, "KNIFE", "KILL_Knife_Ped_Damage", 4.0, 0, 1, 1, 1, 0 );
                case 2 : LoopingAnim( playerid, "KNIFE", "KILL_Knife_Ped_Die", 4.0, 0, 1, 1, 1, 0 );
                case 3 : LoopingAnim( playerid, "KNIFE", "KILL_Knife_Player", 4.0, 0, 0, 0, 0, 0 );
                case 4 : LoopingAnim( playerid, "KNIFE", "KILL_Partial", 4.0, 0, 1, 1, 1, 1 );
                default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /knife [1-4]");
            }
        }
    }
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}
CMD:hit( playerid, params[ ] )
{
    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
    {
        new
            id;

        if ( sscanf( params, "d", id ) ) SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /hit [1-3]");
        else
        {
            switch ( id )
            {
                case 1 : LoopingAnim( playerid, "FIGHT_D", "FightD_3", 4.0, 0, 1, 1, 0, 0 );
                case 2 : LoopingAnim( playerid, "FIGHT_B", "FightB_G", 4.0, 0, 0, 0, 0, 0 );
                case 3 : LoopingAnim( playerid, "PED", "BIKE_elbowL", 4.0, 0, 0, 0, 0, 0 );
                default: SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /hit [1-3]");
            }
        }
    }
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}
CMD:loudtalk( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "RIOT", "RIOT_shout", 4.0, 1, 0, 0, 0, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}
CMD:aload( playerid, params[ ] )
{
    new
        id;
    if( sscanf( params, "d", id) )
        SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /aload [1-2]");
    else
    {
        switch(id)
        {
            case 1:
                LoopingAnim( playerid, "COLT45", "colt45_reload", 4.0, 0, 1, 1, 1, 1);
            case 2:
                LoopingAnim( playerid, "UZI", "UZI_reload", 4.0, 0, 1, 1, 1, 1);
        }
    }
    return true;
}
CMD:wank( playerid, params[ ] )
{
    #pragma unused params
    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "PAULNMAC", "wank_loop", 4.1, 1, 1, 1, 1, 1);
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis ðiomis animacijomis galite tik tada, kada Jûsø veikëjas yra ant kojø. ");

    return true;
}
CMD:walk( playerid, params[ ] )
{
    new
        animid;
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    if(sscanf(params,"d",animid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /walk [0 - 14]");
    if(animid == 1) LoopingAnim(playerid,"PED","WALK_gang1",4.1,1,1,1,1,1);
    else if(animid == 2) LoopingAnim(playerid,"PED","WALK_gang2",4.1,1,1,1,1,1);
    else if(animid == 3) LoopingAnim(playerid,"FAT","FatWalk",4.1,1,1,1,1,1);
    else if(animid == 4) LoopingAnim(playerid,"WUZI","CS_Wuzi_pt1",4.1,1,1,1,1,1);
    else if(animid == 5) LoopingAnim(playerid,"WUZI","Wuzi_walk",3.0,1,1,1,1,1);
    else if(animid == 6) LoopingAnim(playerid,"POOL","Pool_walk",3.0,1,1,1,1,1);
    else if(animid == 7) LoopingAnim(playerid,"PED","Walk_player",3.0,1,1,1,1,1);
    else if(animid == 8) LoopingAnim(playerid,"PED","Walk_old",3.0,1,1,1,1,1);
    else if(animid == 9) LoopingAnim(playerid,"PED","Walk_fatold",3.0,1,1,1,1,1);
    else if(animid == 10) LoopingAnim(playerid,"PED","woman_walkfatold",3.0,1,1,1,1,1);
    else if(animid == 11) LoopingAnim(playerid,"PED","woman_walknorm",3.0,1,1,1,1,1);
    else if(animid == 12) LoopingAnim(playerid,"PED","woman_walkold",3.0,1,1,1,1,1);
    else if(animid == 13) LoopingAnim(playerid,"PED","woman_walkpro",3.0,1,1,1,1,1);
    else if(animid == 14) LoopingAnim(playerid,"PED","woman_walkshop",3.0,1,1,1,1,1);
    else SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /walk [1-14]");
    return 1;
}
CMD:walkstyle( playerid, params[ ] )
{
    new
        walkstyle;
        
    if(pInfo[ playerid ][ pDonator ] < 1 ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs neesate rëmëjas. ");
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    if(sscanf(params,"d",walkstyle)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /walkstyle [0 - 8]");
    if(walkstyle < 0 || walkstyle > 8) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /walkstyle [0 - 8]");
    SendClientMessage(playerid,GRAD,"Stilius sëkmingai pakeistas");
    if(walkstyle == 0) pInfo[ playerid ][ pWalkStyle ] = 0;
    else if(walkstyle == 1) pInfo[ playerid ][ pWalkStyle ] = 1;
    else if(walkstyle == 2) pInfo[ playerid ][ pWalkStyle ] = 2;
    else if(walkstyle == 3) pInfo[ playerid ][ pWalkStyle ] = 3;
    else if(walkstyle == 4) pInfo[ playerid ][ pWalkStyle ] = 4;
    else if(walkstyle == 5) pInfo[ playerid ][ pWalkStyle ] = 5;
    else if(walkstyle == 6) pInfo[ playerid ][ pWalkStyle ] = 6;
    else if(walkstyle == 7) pInfo[ playerid ][ pWalkStyle ] = 7;
    else if(walkstyle == 8) pInfo[ playerid ][ pWalkStyle ] = 8;
    return 1;
}
CMD:talkstyle(playerid, params[])
{
    new talkstyle;
        
    if(pInfo[ playerid ][ pDonator ] < 1 ) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs neesate rëmëjas. ");

    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite keisti kalbëjimo stiliaus bûdamas transporto priemonëje.");

    if(sscanf(params,"d",talkstyle) || talkstyle < 0 || talkstyle > 4) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /talkstyle [0 - 4]");

    SendClientMessage(playerid,GRAD,"Stilius sëkmingai pakeistas");
    pInfo[ playerid ][ pTalkStyle ] = talkstyle;
    return 1;
}


CMD:freeze( playerid, params [ ] )
{
    new
        giveplayerid,
        string[ 126 ];
    if ( GetPlayerAdminLevel(playerid) >= 1 )
    {
        if ( sscanf( params, "u", giveplayerid ) )
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /freeze [þaidëjoID]");
        if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid,GRAD," {FF6347}Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");
        if( !Freezed[giveplayerid] )
        {
            ShowInfoText(giveplayerid,"~w~ UZSALDYTAS",1000);
            TogglePlayerControllable(giveplayerid, false);
            Freezed[giveplayerid] = true;
            format(string,sizeof(string),"AdmWarn: Administratorius (%s) uþðaldë (/freeze) veikëjà (%s)",GetName(playerid),GetName(giveplayerid));
            SendAdminMessage(COLOR_ADM,string);
        }
        else
        {
            ShowInfoText(giveplayerid,"~w~ ATSALDYTAS",1000);
            TogglePlayerControllable(giveplayerid, true);
            Freezed[giveplayerid] = false;
            format(string,sizeof(string),"AdmWarn: Administratorius (%s) atðaldë (/unfreeze) veikëjà (%s)",GetName(playerid),GetName(giveplayerid));
            SendAdminMessage(COLOR_ADM,string);
        }
    }
    return 1;
}
CMD:noooc( playerid, params [ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 1 )
    {
        if(OOCDisabled == true)
        {
            OOCDisabled = false;
            SendClientMessageToAll(COLOR_LIGHTRED,"AdmCmd serverio Administratorius áðjungë globalø OOC kanalà - /o");
        }
        else if(OOCDisabled == false)
        {
            OOCDisabled = true;
            SendClientMessageToAll(COLOR_LIGHTRED,"AdmCmd serverio Administratorius ájungë globalø OOC kanalà - /o");
        }
    }
    return 1;
}

/*
CMD:reconnectnpc(playerid, params[])
{
    if(!pInfo[ playerid ][ pAdmin ] && !IsPlayerAdmin(playerid))
        return 0;

    new npcid;
    if(isnull(params))
        SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas /reconnectnpc [ NPC vardas ]");

    else if((npcid = FindNPCByName(params)) == INVALID_PLAYER_ID)
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, tokio NPC nëra.");

    else 
    {
        Kick(npcid);
        SendClientMessage(playerid, COLOR_LIGHTRED, "NPC iðmestas, jis vël prisijungs po sekundës.");
        defer NpcReconnectDelay(playerid, params, strlen(params));
    }
    return 1;
}

timer NpcReconnectDelay[1000](adminid, npcname[], len)
{
    new string[ 60 ];
    ConnectNPC(npcname);

    format(string, sizeof(string), "NPC %s jungiamas á serverá.", npcname);
    SendClientMessage(adminid, COLOR_LIGHTRED, string);
}*/

/*
CMD:whipe( playerid, params [ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 5 )
    {
        new
            reason[ 256 ],
            reason2[ 256 ],
            string[ 256 ],
            string2[ 256 ],
            inv[ 256 ],
            inv2[ MAX_HOUSETRUNK_SLOTS ][ 2 ],
            idx,
            id,
            id2;

        reason2 = strtok(params, idx);
        if (!reason2[0])
            SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /whipe [tipas]");
        else
        {
            if(!strcmp(reason2,"guns",true))
            {
                new Cache:result = mysql_query(DbHandle,  "SELECT `id`, `cTrunk` FROM `vehicles`" );
                
                for(new i = 0; i < cache_get_row_count(); i++)
                {
                    format(string2, 256, "");
                    id = cache_get_field_content_int(i, "id");
                    cache_get_field_content(i, "cTrunk", inv);

                    sscanf( inv, "p</>dddddddddddddddddddddddd",
                        inv2[ 0 ][ 0 ],
                        inv2[ 0 ][ 1 ],
                        inv2[ 1 ][ 0 ],
                        inv2[ 1 ][ 1 ],
                        inv2[ 2 ][ 0 ],
                        inv2[ 2 ][ 1 ],
                        inv2[ 3 ][ 0 ],
                        inv2[ 3 ][ 1 ],
                        inv2[ 4 ][ 0 ],
                        inv2[ 4 ][ 1 ],
                        inv2[ 5 ][ 0 ],
                        inv2[ 5 ][ 1 ],
                        inv2[ 6 ][ 0 ],
                        inv2[ 6 ][ 1 ],
                        inv2[ 7 ][ 0 ],
                        inv2[ 7 ][ 1 ],
                        inv2[ 8 ][ 0 ],
                        inv2[ 8 ][ 1 ],
                        inv2[ 9 ][ 0 ],
                        inv2[ 9 ][ 1 ],
                        inv2[ 10 ][ 0 ],
                        inv2[ 10 ][ 1 ],
                        inv2[ 11 ][ 0 ],
                        inv2[ 11 ][ 1 ]);
                    for(new k = 0; k < MAX_TRUNK_SLOTS; k++)
                    {
                        if( inv2[ k ][ 0 ] < 51 )
                            format(string2, 256, "%s0/0/", string2);
                        else
                            format(string2, 256, "%s%d/%d/", string2, inv2[ k ][ 0 ], inv2[ k ][ 1 ]);
                    }
                    format( reason, 256, "UPDATE `vehicles` SET `cTrunk` = '%s' WHERE `id` = %d", string2, id );
                    mysql_query(DbHandle,  reason, false);
                }
                format(string, 256, "");
                format(string2, 256, "");
                format(reason, 256, "");
                format(inv, 256, "");
                cache_delete(result);

                result = mysql_query(DbHandle,  "SELECT `hID`, `Inventory` FROM `houses`" );
                for(new j = 0; j < cache_get_row_count(); j++)
                {
                    format(string2, 256, "");
                    id = cache_get_field_content_int(j, "hID");
                    cache_get_field_content(j, "Inventory", inv);
                    sscanf( inv, "p</>dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd",
                        inv2[ 0 ][ 0 ],
                        inv2[ 0 ][ 1 ],
                        inv2[ 1 ][ 0 ],
                        inv2[ 1 ][ 1 ],
                        inv2[ 2 ][ 0 ],
                        inv2[ 2 ][ 1 ],
                        inv2[ 3 ][ 0 ],
                        inv2[ 3 ][ 1 ],
                        inv2[ 4 ][ 0 ],
                        inv2[ 4 ][ 1 ],
                        inv2[ 5 ][ 0 ],
                        inv2[ 5 ][ 1 ],
                        inv2[ 6 ][ 0 ],
                        inv2[ 6 ][ 1 ],
                        inv2[ 7 ][ 0 ],
                        inv2[ 7 ][ 1 ],
                        inv2[ 8 ][ 0 ],
                        inv2[ 8 ][ 1 ],
                        inv2[ 9 ][ 0 ],
                        inv2[ 9 ][ 1 ],
                        inv2[ 10 ][ 0 ],
                        inv2[ 10 ][ 1 ],
                        inv2[ 11 ][ 0 ],
                        inv2[ 11 ][ 1 ],
                        inv2[ 12 ][ 0 ],
                        inv2[ 12 ][ 1 ],
                        inv2[ 13 ][ 0 ],
                        inv2[ 13 ][ 1 ],
                        inv2[ 14 ][ 0 ],
                        inv2[ 14 ][ 1 ],
                        inv2[ 15 ][ 0 ],
                        inv2[ 15 ][ 1 ],
                        inv2[ 16 ][ 0 ],
                        inv2[ 16 ][ 1 ],
                        inv2[ 17 ][ 0 ],
                        inv2[ 17 ][ 1 ],
                        inv2[ 18 ][ 0 ],
                        inv2[ 18 ][ 1 ],
                        inv2[ 19 ][ 0 ],
                        inv2[ 19 ][ 1 ],
                        inv2[ 20 ][ 0 ],
                        inv2[ 20 ][ 1 ],
                        inv2[ 21 ][ 0 ],
                        inv2[ 21 ][ 1 ],
                        inv2[ 22 ][ 0 ],
                        inv2[ 22 ][ 1 ],
                        inv2[ 23 ][ 0 ],
                        inv2[ 23 ][ 1 ],
                        inv2[ 24 ][ 0 ],
                        inv2[ 24 ][ 1 ],
                        inv2[ 25 ][ 0 ],
                        inv2[ 25 ][ 1 ],
                        inv2[ 26 ][ 0 ],
                        inv2[ 26 ][ 1 ],
                        inv2[ 27 ][ 0 ],
                        inv2[ 27 ][ 1 ],
                        inv2[ 28 ][ 0 ],
                        inv2[ 28 ][ 1 ],
                        inv2[ 29 ][ 0 ],
                        inv2[ 29 ][ 1 ]);
                    for(new i = 0; i < MAX_HOUSETRUNK_SLOTS; i++)
                    {
                        if( inv2[ i ][ 0 ] < 51 )
                            format(string2, 256, "%s0/0/", string2);
                        else
                            format(string2, 256, "%s%d/%d/", string2, inv2[ i ][ 0 ], inv2[ i ][ 1 ]);
                    }
                    format( reason, 256, "UPDATE `houses` SET `Inventory` = '%s' WHERE `hID` = %d", string2, id );
                    mysql_query(DbHandle,  reason, false);
                }
                cache_delete(result);

                format(string, 256, "");
                format(string2, 256, "");
                format(reason, 256, "");
                format(inv, 256, "");

                result = mysql_query(DbHandle,  "SELECT `id`, `Inventory` FROM `garazai`" );
                
                for(new j = 0; j < cache_get_row_count(); j++)
                {
                    format(string2, 256, "");
                    id = cache_get_field_content_int(j, "id");
                    cache_get_field_content(j, "Inventory", inv);

                    sscanf( inv, "p</>dddddddddddddddddddd",
                        inv2[ 0 ][ 0 ],
                        inv2[ 0 ][ 1 ],
                        inv2[ 1 ][ 0 ],
                        inv2[ 1 ][ 1 ],
                        inv2[ 2 ][ 0 ],
                        inv2[ 2 ][ 1 ],
                        inv2[ 3 ][ 0 ],
                        inv2[ 3 ][ 1 ],
                        inv2[ 4 ][ 0 ],
                        inv2[ 4 ][ 1 ],
                        inv2[ 5 ][ 0 ],
                        inv2[ 5 ][ 1 ],
                        inv2[ 6 ][ 0 ],
                        inv2[ 6 ][ 1 ],
                        inv2[ 7 ][ 0 ],
                        inv2[ 7 ][ 1 ],
                        inv2[ 8 ][ 0 ],
                        inv2[ 8 ][ 1 ],
                        inv2[ 9 ][ 0 ],
                        inv2[ 9 ][ 1 ]);
                    for(new i = 0; i < MAX_GARAGETRUNK_SLOTS; i++)
                    {
                        if( inv2[ i ][ 0 ] < 51 )
                            format(string2, 256, "%s0/0/", string2);
                        else
                            format(string2, 256, "%s%d/%d/", string2, inv2[ i ][ 0 ], inv2[ i ][ 1 ]);
                    }
                    format( reason, 256, "UPDATE `garazai` SET `Inventory` = '%s' WHERE `id` = %d", string2, id );
                    mysql_query(DbHandle,  reason, false);
                }
                format(string, 256, "");
                format(string2, 256, "");
                format(reason, 256, "");
                format(inv, 256, "");
                cache_delete(result);

                mysql_pquery(DbHandle,  "DELETE FROM `AC`" );
                mysql_pquery(DbHandle,  "UPDATE `players` SET `Weapons` = '0/0/0/0/0/0/0/0/'" );

                result = mysql_query(DbHandle,  "SELECT `id`, `Inventory` FROM `players`" );
                for(new j = 0; j < cache_get_row_count(); j++)
                {
                    format(string2, 256, "");
                    id = cache_get_field_content_int(j, "id");
                    cache_get_field_content(j, "Inventory", inv);
                    sscanf( inv, "p</>dddddddddddd",
                        inv2[ 0 ][ 0 ],
                        inv2[ 0 ][ 1 ],
                        inv2[ 1 ][ 0 ],
                        inv2[ 1 ][ 1 ],
                        inv2[ 2 ][ 0 ],
                        inv2[ 2 ][ 1 ],
                        inv2[ 3 ][ 0 ],
                        inv2[ 3 ][ 1 ],
                        inv2[ 4 ][ 0 ],
                        inv2[ 4 ][ 1 ],
                        inv2[ 5 ][ 0 ],
                        inv2[ 5 ][ 1 ]);
                    for(new i = 0; i < INVENTORY_SLOTS; i++)
                    {
                        if( inv2[ i ][ 0 ] < 51 )
                            format(string2, 256, "%s0/0/", string2);
                        else
                            format(string2, 256, "%s%d/%d/", string2, inv2[ i ][ 0 ], inv2[ i ][ 1 ]);
                    }
                    format( reason, 256, "UPDATE `players` SET `Inventory` = '%s' WHERE `id` = %d", string2, id );
                    mysql_query(DbHandle,  reason, false);
                }
                cache_delete(result);
                SendClientMessage(playerid,GRAD,"Ginklø whipe atliktas");
            }
            else if(!strcmp(reason2,"items",true))
            {
                reason2 = strtok( params, idx );
                id2 = strval( reason2 );
                if ( id2 > 211 || id2 < 1 )      return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: blogas daikto ID..");
                new Cache:result = mysql_query(DbHandle,  "SELECT `id`, `cTrunk` FROM `vehicles`" );
                
                for(new j = 0; j < cache_get_row_count(); j++)
                {
                    format(string2, 256, "");
                    id = cache_get_field_content_int(j, "id");
                    cache_get_field_content(j, "cTrunk", inv);
                    sscanf( inv, "p</>dddddddddddddddddddddddd",
                        inv2[ 0 ][ 0 ],
                        inv2[ 0 ][ 1 ],
                        inv2[ 1 ][ 0 ],
                        inv2[ 1 ][ 1 ],
                        inv2[ 2 ][ 0 ],
                        inv2[ 2 ][ 1 ],
                        inv2[ 3 ][ 0 ],
                        inv2[ 3 ][ 1 ],
                        inv2[ 4 ][ 0 ],
                        inv2[ 4 ][ 1 ],
                        inv2[ 5 ][ 0 ],
                        inv2[ 5 ][ 1 ],
                        inv2[ 6 ][ 0 ],
                        inv2[ 6 ][ 1 ],
                        inv2[ 7 ][ 0 ],
                        inv2[ 7 ][ 1 ],
                        inv2[ 8 ][ 0 ],
                        inv2[ 8 ][ 1 ],
                        inv2[ 9 ][ 0 ],
                        inv2[ 9 ][ 1 ],
                        inv2[ 10 ][ 0 ],
                        inv2[ 10 ][ 1 ],
                        inv2[ 11 ][ 0 ],
                        inv2[ 11 ][ 1 ]);
                    for(new i = 0; i < MAX_TRUNK_SLOTS; i++)
                    {
                        if( inv2[ i ][ 0 ] == id2 )
                            format(string2, 256, "%s0/0/", string2);
                        else
                            format(string2, 256, "%s%d/%d/", string2, inv2[ i ][ 0 ], inv2[ i ][ 1 ]);
                    }
                    format( reason, 256, "UPDATE `vehicles` SET `cTrunk` = '%s' WHERE `id` = %d", string2, id );
                    mysql_query(DbHandle,  reason, false);
                }
                format(string, 256, "");
                format(string2, 256, "");
                format(reason, 256, "");
                format(inv, 256, "");
                cache_delete(result);

                result = mysql_query(DbHandle,  "SELECT `id`, `Inventory` FROM `garazai`" );
                for(new j = 0; j < cache_get_row_count(); j++)
                {
                    format(string2, 256, "");
                    id = cache_get_field_content_int(j, "id");
                    cache_get_field_content(j, "Inventory", inv);
                    sscanf( inv, "p</>dddddddddddddddddddd",
                        inv2[ 0 ][ 0 ],
                        inv2[ 0 ][ 1 ],
                        inv2[ 1 ][ 0 ],
                        inv2[ 1 ][ 1 ],
                        inv2[ 2 ][ 0 ],
                        inv2[ 2 ][ 1 ],
                        inv2[ 3 ][ 0 ],
                        inv2[ 3 ][ 1 ],
                        inv2[ 4 ][ 0 ],
                        inv2[ 4 ][ 1 ],
                        inv2[ 5 ][ 0 ],
                        inv2[ 5 ][ 1 ],
                        inv2[ 6 ][ 0 ],
                        inv2[ 6 ][ 1 ],
                        inv2[ 7 ][ 0 ],
                        inv2[ 7 ][ 1 ],
                        inv2[ 8 ][ 0 ],
                        inv2[ 8 ][ 1 ],
                        inv2[ 9 ][ 0 ],
                        inv2[ 9 ][ 1 ]);
                    for(new i = 0; i < MAX_GARAGETRUNK_SLOTS; i++)
                    {
                        if( inv2[ i ][ 0 ] == id2 )
                            format(string2, 256, "%s0/0/", string2);
                        else
                            format(string2, 256, "%s%d/%d/", string2, inv2[ i ][ 0 ], inv2[ i ][ 1 ]);
                    }
                    format( reason, 256, "UPDATE `garazai` SET `Inventory` = '%s' WHERE `id` = %d", string2, id );
                    mysql_query(DbHandle,  reason, false);
                }
                format(string, 256, "");
                format(string2, 256, "");
                format(reason, 256, "");
                format(inv, 256, "");
                cache_delete(result);
                format(string, 256, "DELETE FROM `AC` WHERE `WeaponID` = %d", id2);
                mysql_query(DbHandle,  string, false);
                format(string, 256, "");
                result = mysql_query(DbHandle,  "SELECT `hID`, `Inventory` FROM `houses`" );
                for(new j = 0; j < cache_get_row_count(); j++)
                {
                    format(string2, 256, "");
                    id = cache_get_field_content_int(j, "hID");
                    cache_get_field_content(j, "Inventory", inv);
                    sscanf( inv, "p</>dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd",
                        inv2[ 0 ][ 0 ],
                        inv2[ 0 ][ 1 ],
                        inv2[ 1 ][ 0 ],
                        inv2[ 1 ][ 1 ],
                        inv2[ 2 ][ 0 ],
                        inv2[ 2 ][ 1 ],
                        inv2[ 3 ][ 0 ],
                        inv2[ 3 ][ 1 ],
                        inv2[ 4 ][ 0 ],
                        inv2[ 4 ][ 1 ],
                        inv2[ 5 ][ 0 ],
                        inv2[ 5 ][ 1 ],
                        inv2[ 6 ][ 0 ],
                        inv2[ 6 ][ 1 ],
                        inv2[ 7 ][ 0 ],
                        inv2[ 7 ][ 1 ],
                        inv2[ 8 ][ 0 ],
                        inv2[ 8 ][ 1 ],
                        inv2[ 9 ][ 0 ],
                        inv2[ 9 ][ 1 ],
                        inv2[ 10 ][ 0 ],
                        inv2[ 10 ][ 1 ],
                        inv2[ 11 ][ 0 ],
                        inv2[ 11 ][ 1 ],
                        inv2[ 12 ][ 0 ],
                        inv2[ 12 ][ 1 ],
                        inv2[ 13 ][ 0 ],
                        inv2[ 13 ][ 1 ],
                        inv2[ 14 ][ 0 ],
                        inv2[ 14 ][ 1 ],
                        inv2[ 15 ][ 0 ],
                        inv2[ 15 ][ 1 ],
                        inv2[ 16 ][ 0 ],
                        inv2[ 16 ][ 1 ],
                        inv2[ 17 ][ 0 ],
                        inv2[ 17 ][ 1 ],
                        inv2[ 18 ][ 0 ],
                        inv2[ 18 ][ 1 ],
                        inv2[ 19 ][ 0 ],
                        inv2[ 19 ][ 1 ],
                        inv2[ 20 ][ 0 ],
                        inv2[ 20 ][ 1 ],
                        inv2[ 21 ][ 0 ],
                        inv2[ 21 ][ 1 ],
                        inv2[ 22 ][ 0 ],
                        inv2[ 22 ][ 1 ],
                        inv2[ 23 ][ 0 ],
                        inv2[ 23 ][ 1 ],
                        inv2[ 24 ][ 0 ],
                        inv2[ 24 ][ 1 ],
                        inv2[ 25 ][ 0 ],
                        inv2[ 25 ][ 1 ],
                        inv2[ 26 ][ 0 ],
                        inv2[ 26 ][ 1 ],
                        inv2[ 27 ][ 0 ],
                        inv2[ 27 ][ 1 ],
                        inv2[ 28 ][ 0 ],
                        inv2[ 28 ][ 1 ],
                        inv2[ 29 ][ 0 ],
                        inv2[ 29 ][ 1 ]);
                    for(new i = 0; i < MAX_HOUSETRUNK_SLOTS; i++)
                    {
                        if( inv2[ i ][ 0 ] == id2 )
                            format(string2, 256, "%s0/0/", string2);
                        else
                            format(string2, 256, "%s%d/%d/", string2, inv2[ i ][ 0 ], inv2[ i ][ 1 ]);
                    }
                    format( reason, 256, "UPDATE `houses` SET `Inventory` = '%s' WHERE `hID` = %d", string2, id );
                    mysql_query(DbHandle,  reason, false);
                }
                format(string, 256, "");
                format(string2, 256, "");
                format(reason, 256, "");
                format(inv, 256, "");
                cache_delete(result);

                result = mysql_query(DbHandle,  "SELECT `id`, `Inventory` FROM `players`" );
                for(new j = 0; j < cache_get_row_count() ;j++)
                {
                    format(string2, 256, "");
                    id = cache_get_field_content_int(j, "id");
                    cache_get_field_content(j, "Inventory", inv);
                    sscanf( inv, "p</>dddddddddddd",
                        inv2[ 0 ][ 0 ],
                        inv2[ 0 ][ 1 ],
                        inv2[ 1 ][ 0 ],
                        inv2[ 1 ][ 1 ],
                        inv2[ 2 ][ 0 ],
                        inv2[ 2 ][ 1 ],
                        inv2[ 3 ][ 0 ],
                        inv2[ 3 ][ 1 ],
                        inv2[ 4 ][ 0 ],
                        inv2[ 4 ][ 1 ],
                        inv2[ 5 ][ 0 ],
                        inv2[ 5 ][ 1 ]);
                    for(new i = 0; i < INVENTORY_SLOTS; i++)
                    {
                        if( inv2[ i ][ 0 ] == id2 )
                            format(string2, 256, "%s0/0/", string2);
                        else
                            format(string2, 256, "%s%d/%d/", string2, inv2[ i ][ 0 ], inv2[ i ][ 1 ]);
                    }
                    format( reason, 256, "UPDATE `players` SET `Inventory` = '%s' WHERE `id` = %d", string2, id );
                    mysql_query(DbHandle,  reason, false);
                }
                format(string, 256, "");
                format(string2, 256, "");
                format(reason, 256, "");
                format(inv, 256, "");
                cache_delete(result);

                result = mysql_query(DbHandle,  "SELECT `id`, `Weapons` FROM `players`" );
                
                for(new j = 0; j < cache_get_row_count(); j++)
                {
                    format(string2, 256, "");
                    id = cache_get_field_content_int(j, "id");
                    cache_get_field_content(j, "Weapons", inv);
                    sscanf( inv, "p</>dddd",
                        inv2[ 0 ][ 0 ],
                        inv2[ 0 ][ 1 ],
                        inv2[ 1 ][ 0 ],
                        inv2[ 1 ][ 1 ]);
                    for(new i = 0; i < MAX_SAVED_WEAPONS; i++)
                    {
                        if( inv2[ i ][ 0 ] == id2 )
                            format(string2, 256, "%s0/0/", string2);
                        else
                            format(string2, 256, "%s%d/%d/", string2, inv2[ i ][ 0 ], inv2[ i ][ 1 ]);
                    }
                    format( reason, 256, "UPDATE `players` SET `Weapons` = '%s' WHERE `id` = %d", string2, id );
                    mysql_query(DbHandle,  reason, false);
                }
                cache_delete(result);
                SendClientMessage(playerid,GRAD,"Daikto whipe atliktas");
            }
            else
                SendClientMessage(playerid,COLOR_RED,"Blogas tipas");
        }
    }
    return 1;
}*/


/*
CMD:setstatcar(playerid, params[])
{
    if( GetPlayerAdminLevel(playerid) >= 4 )
    {
        new id,
            id2,
            string[ 126 ];
            
        if ( sscanf( params, "dd", id, id2 ) )
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /setstatcar [Kodas][Kodas2]");
            SendClientMessage(playerid,GRAD,"KODAI: 1 Sudauþymai | 2 Draudimas");
            return 1;
        }

        new idcar = GetPlayerVehicleID( playerid );
        if(!IsPlayerInAnyVehicle(playerid) || cInfo[ idcar ][ cOwner ] == 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: jûs nesëdite transporto priemonëje arba tr. priemonë priklauso serveriui.");
        switch(id)
        {
            case 1:
            {
                cInfo[idcar][cDuzimai] = id2;
                format(string,126,"Tr. priemonës %d sunaikinimø kiekis buvo pakeistas á %d",idcar,id2);
            }
            case 2:
            {
                cInfo[idcar][cInsurance] = id2;
                format(string,126,"Tr. priemonës %d draudimo kiekis buvo pakeistas á %d",idcar,id2);
            }
        }
        AdminLog( pInfo[ playerid ][ pMySQLID ], cInfo[ idcar ][ cOwner ], string );
        SendClientMessage(playerid,COLOR_FADE1,string);
    }
    return 1;
}
*/
/*
CMD:setstat(playerid, params[])
{
    if( pInfo[ playerid ][ pAdmin ] >= 4 )
    {
        new id,
            id2,
            giveplayerid,
            string[ 126 ];

        if ( sscanf( params, "udd", giveplayerid, id, id2 ) )
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /setstat [þaidëjo id][Kodas][Kodas2]");
            SendClientMessage(playerid,GRAD,"KODAI: 1 Lygis | 2 Bankas | 3 Nuomos Raktas | 4 Darbas | 5 Mirtys");
            SendClientMessage(playerid,GRAD,"KODAI: 6 Tel.Nr. | 7 darbo Lygis");
            return 1;
        }
        if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");
        switch(id)
        {
            case 1:
            {
                pInfo[giveplayerid][pLevel] = id2;
                SetPlayerScore( giveplayerid, pInfo[ giveplayerid ][ pLevel ] );
                format(string,126,"þaidëjo %s lygis buvo pakeistas á %d",GetName(giveplayerid),id2);
            }
            case 2:
            {
                pInfo[giveplayerid][pBank] = id2;
                format(string,126,"þaidëjo %s banko sá skaita buvo pakeistas á %d",GetName(giveplayerid),id2);
            }
            case 3:
            {
                pInfo[giveplayerid][pSpawn] = DefaultSpawn;
                pInfo[giveplayerid][pHouseKey] = id2;
                format(string,126,"þaidëjo %s nuomos raktas buvo pakeistas á %d",GetName(giveplayerid),id2);
            }
            case 4:
            {
                if( id2 > 0 && id2 < MAX_JOBS && pInfo[ giveplayerid ][ pMember ] == 0 )
                {
                    pInfo[ giveplayerid ][ pJob ] = id2;
                    pInfo[ giveplayerid ][ pJobLevel ] = 0;
                    pInfo[ giveplayerid ][ pJobSkill ] = 0;
                    format(string,126,"þaidëjo %s darbas buvo pakeistas á %d",GetName(giveplayerid),id2);
                }
                else 
                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, netinkamas darbo ID arba ðis þaidëjas jau turi darbà.");
            }
            case 5:
            {
                pInfo[giveplayerid][pDeaths] = id2;
                format(string,126,"þaidëjo %s mirtys buvo pakeisto á %d",GetName(giveplayerid),id2);
            }
            case 6:
            {
                pInfo[giveplayerid][pPhone] = id2;
                format(string,126,"þaidëjo %s telefono numeris buvo pakeistas á %d",GetName(giveplayerid),id2);
            }
            case 7:
            {
                pInfo[giveplayerid][pJobLevel] = id2;
                format(string,126,"þaidëjo %s darbo Lygis buvo pakeistas á %d",GetName(giveplayerid),id2);
            }
        }
        SendClientMessage(playerid,COLOR_FADE1,string);
        AdminLog( pInfo[ playerid ][ pMySQLID ],pInfo[ giveplayerid ][ pMySQLID ], string );
    }
    return 1;
}
*/

/*
CMD:forcelogout(playerid, params[])
{
    if( GetPlayerAdminLevel(playerid) >= 3 )
    {
        new
            playerLID;

        if(sscanf(params, "u", playerLID))
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /forcelogout [ þaidëjo vardas/ id ] ");

        if(playerLID == INVALID_PLAYER_ID)
            return SendClientMessage(playerid, COLOR_GREY, "Toks ID nëra rastas, ðis þaidëjas nëra prisijungæs.");

        if(!AdminDuty[playerid])
            return SendClientMessage(playerid, COLOR_GREY, "Jûs turite bûti AOD, kad galëtumëte atjungti þaidëjá .");

        format(szMessage, sizeof(szMessage), "Jûs buvote atjungtas nuo administratoriaus %s.", GetName( playerid ));
        SendClientMessage(playerid, COLOR_WHITE, szMessage);

        SaveAccount(playerLID);
        PlayerOn[playerLID] = false;

        SendClientMessage(playerLID, COLOR_GREY, "Jûs buvote atjungtas.");
        ShowPlayerLoginDialog(playerid);
    }
    return 1;
}
*/
CMD:togglefading(playerid)
{
    if(!GetPlayerAdminLevel(playerid))
        return 0;

    if(PlayerFading)
        SendClientMessage(playerid, -1, "Uþtemimo efetktas iðjungtas.");
    else 
        SendClientMessage(playerid, -1, "Uþtemimo efektas ájungtas");
    PlayerFading = !PlayerFading;
    return 1;
}

CMD:npcrecord(playerid, params[])
{
    if(GetPlayerAdminLevel(playerid) < 4 && !IsPlayerAdmin(playerid))
        return 0;

    if(IsPlayerDataRecorded[ playerid ])
    {
        IsPlayerDataRecorded[ playerid ] = false;
        StopRecordingPlayerData(playerid);
        SendClientMessage(playerid, COLOR_NEWS, "Áraðymas sëkmingai baigtas. Failas yra scriptfiles direktorijoje.");
    }
    else 
    {
        if(isnull(params))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, teisingas naudojimas /npcrecord [Failo pavadinimas kuriame bus áraðas]");

        new type, string[133];
        format(string, sizeof(string), "%s.rec", params);

        if(fexist(string))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, áraðas su tokiu pavadinimu jau egzistuoja.");

        if(IsPlayerInAnyVehicle(playerid))
            type = PLAYER_RECORDING_TYPE_DRIVER;
        else 
            type = PLAYER_RECORDING_TYPE_ONFOOT;

        StartRecordingPlayerData(playerid, type, params);
        SendClientMessage(playerid, COLOR_NEWS, "Áraðinëjimas pradëtas. Já pabaigti galite vël paraðæ /npcrecord");
        IsPlayerDataRecorded[ playerid ] = true;
    }   
    return 1;
}

CMD:amenu(playerid)
{
    if ( GetPlayerAdminLevel(playerid) >= 4 )
    {
        ShowPlayerDialog(playerid,19,DIALOG_STYLE_LIST,"Administratoriaus meniu",
                                                                "- Namai\n\
                                                                 - Bizniai\n\
                                                                 - Frakcijos\n\
                                                                 - Tr. priemonës\n\
                                                                 - Serverio áëjimai\n\
                                                                 - Skelbti balsavimà \n\
                                                                 - Automobiliø turgus\n\
                                                                 - Garaþai\n\
                                                                 - Industrijos\n\
                                                                 - Interjerai\n\
                                                                 - Ávairios koordinatës\n\
                                                                 - Grafiti\n\
                                                                 - Serverio klaidos","Rinktis","Atðaukti");
    }
    return 1;
}
CMD:hideadmins( playerid, params[ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 3 )
    {
        #pragma unused params
        switch ( GetPVarInt( playerid, "hideadmin" ) )
        {
            case true:
            {
                SetPVarInt( playerid, "hideadmin", false );
                SendClientMessage(playerid,GRAD,"Dabar tave matys adminø sá raðe.");
            }
            case false:
            {
                SetPVarInt( playerid, "hideadmin", true );
                SendClientMessage(playerid,GRAD,"Dabar tave nematys adminø sá raðe.");
            }
        }
    }
    return 1;
}
/*
CMD:checkflist(playerid, params[])
{
    if( GetPlayerAdminLevel(playerid) >= 1 )
    {
        new
            fid,
            string[ 100 ];
        if( sscanf( params,"d", fid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /checkflist [FRAKCIJOS ID ]" );
        if ( fid > sizeof ( fInfo ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: netinkamas frakcijos ID " );

        foreach(Player, pid )
        {
            if( PlayerFaction( pid ) == fid )
            {
                format(string, sizeof(string), "%d [%s] %s", pInfo[ pid ][ pRank ], GetPlayerRangName( pid ), GetName( pid ));
                SendClientMessage(playerid, COLOR_WHITE, string);
            }
        }
    }
    return 1;
}
CMD:fon( playerid, params[ ] )
{
    new
        string[ 126 ];
    if( GetPlayerAdminLevel(playerid) >= 0)
    {
        new id;
        if ( sscanf( params, "d", id ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /fon [FRAKCIJOS ID] "),
		SendClientMessage( playerid, COLOR_LIGHTRED, "FRAKCIJOS ID: 1 - policijos departamentas, 2 - medicinos departamentas");
        if ( id < 1 || id > sizeof fInfo ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: netinkamas frakcijos ID " );

        format( string, 126, "Frakcijoje: %s, ðiuo metu yra prisijungusiu þmoniø: %d ",fInfo[ id ][ fName ], IsOnlineFactionMembers( id ) );
        SendClientMessage( playerid, COLOR_LIGHTRED2, string );
    }
    else
    {
        if(PlayerFaction( playerid ) == 1)
        {
            format( string, 126, "Frakcijoje: %s, ðiuo metu yra prisijungusiu þmoniø: %d ", fInfo[ 2 ][ fName ], IsOnlineFactionMembers( 2 ) );
            SendClientMessage( playerid, COLOR_LIGHTRED2, string );
        }
        else if( PlayerFaction( playerid ) == 2 )
        {
            format( string, 126, "Frakcijoje: %s, ðiuo metu yra prisijungusiu þmoniø: %d ", fInfo[ 1 ][ fName ], IsOnlineFactionMembers( 1 ) );
            SendClientMessage( playerid, COLOR_LIGHTRED2, string );
        }
    }
    return 1;
}
*/
/*
 IsOnlineFactionMembers( id )
{
    new count;
    foreach(Player,i)
    {
        if ( id == PlayerFaction( i ) )
            count ++;
    }
    return count;
}
*/
CMD:gotonowhere(playerid)
{
    if(!GetPlayerAdminLevel(playerid) && !IsPlayerAdmin(playerid))
        return 0;

    static LastUsed[ MAX_PLAYERS ];
    new timestamp = gettime();
    if(timestamp - LastUsed[ playerid ] < 5)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Ðià komandà galima naudoti tik kas penkias sekundes");

    LastUsed[ playerid ] = timestamp;

    new Float:x = random(4000) + -2000,
        Float:y = random(4000) + -2000,
        Float:z;

    MapAndreas_FindZ_For2DCoord(x, y, z);

    SetPlayerPos(playerid, x, y, z);
    SetPlayerVirtualWorld(playerid, 0);
    SetPlayerInterior(playerid, 0);
    SendClientMessage(playerid, COLOR_NEWS, "Sëkmingai persikëlëte kaþkur.");
    return 1;
}
/*
CMD:gotohouse(playerid, params[])
{
    if(GetPlayerAdminLevel(playerid) < 4)
        return 0;
    
    new houseindex, Float:x, Float:y, Float:z;
    if(sscanf(params, "d", houseindex)) 
        SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /gotohouse [namo id]");
    if(!IsValidHouse(houseindex))
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, tokio namo nëra.");
    else 
    {
        GetHouseEntrancePos(houseindex, x, y, z);
        SetPlayerPos(playerid, x, y, z);
        SetPlayerInterior(playerid, GetHouseEntranceInteriorID(houseindex));
        SetPlayerVirtualWorld(playerid, GetHouseEntranceVirtualWorld(houseindex));
        SendClientMessage(playerid, COLOR_WHITE, "[AdmCmd] Persikeletë á nurodytà vietà: gyvenamasis namas");
    }
    return 1;
}
*/

CMD:rtc( playerid, params[ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 1 )
    {
        #pragma unused params
        new car = GetNearestVehicle( playerid, 5.0 );
        if ( car != INVALID_VEHICLE_ID )
        {
            SetVehicleToRespawn( car );
            if ( cInfo[ car ][ cVirtWorld ] > 0 && cInfo[ car ][ cOwner ] > 0 )
                SetVehicleVirtualWorld( car, cInfo[ car ][ cVirtWorld ] );
            else SetVehicleVirtualWorld( car, 0 );
            cInfo[ car ][ cFuel ] = GetVehicleFuelTank( GetVehicleModel( car ) );
            SendClientMessage( playerid, COLOR_WHITE, "Tr. priemonë grá þinta á savo pradinà atsiradimo vietà . Degalai atstatyti.");
            return 1;
        }
        else
            SendClientMessage( playerid, COLOR_WHITE, "Jûs turite stovëti/bûti ðalia tr. priemonës norëdami já  atstatyti á pradinà vietà .");
    }
    return 1;
}

CMD:mute( playerid, params[ ] )
{
    new
        giveplayerid,
        string[ 126 ];
    if(GetPlayerAdminLevel(playerid) >= 2)
    {
        if ( sscanf( params, "u", giveplayerid ) )
        {
            SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /unmute [þaidëjoID]" );
            return 1;
        }
        if(!IsPlayerConnected( giveplayerid )) return SendClientMessage( playerid, GRAD,"þaidëjas norimu ID neprisijungæs!");

        if( Mute[ giveplayerid ] )
        {
            format          ( string, 126, "AdmWarn: Administratorius (%s) leido kalbëti (/unmute) veikëjui (%s) ",GetName(playerid), GetName( giveplayerid ) );
            SendAdminMessage( COLOR_ADM, string );

            Mute[ giveplayerid ] = false;
        }
        else
        {
            format          ( string, 126, "AdmWarn: Administratorius (%s) uþdraudë kalbëti (/mute) veikëjui (%s) ",GetName(playerid), GetName( giveplayerid ) );
            SendAdminMessage( COLOR_ADM, string );

            Mute[ giveplayerid ] = true;
        }
    }
    return 1;
}
CMD:setarmour( playerid, params[ ] )
{
    new
        giveplayerid,
        Armor,
        string[ 126 ];
    if(GetPlayerAdminLevel(playerid) >= 3)
    {
        if ( sscanf( params, "ud", giveplayerid, Armor ) )
        {
            SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /setarmour [þaidëjoID][armor]" );
            return 1;
        }
        if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");
        SetPlayerArmour ( giveplayerid, Armor );
        format          ( string, 126 ,"AdmWarn: Administratorius (%s) nustatë veikëjui (%s) ðarvø lygi: %d",GetName(playerid),GetName(giveplayerid),Armor);
        SendAdminMessage( COLOR_ADM, string );
        return 1;
    }
    return 1;
}


 IsVehicleUsed(vehicleid)
{
    if(IsVehicleTrailer(GetVehicleModel(vehicleid)))
    {
        new veh = GetTrailerPullingVehicle(vehicleid);
        if(veh != INVALID_VEHICLE_ID)
            return IsVehicleUsed(veh);
    }
    foreach(Player,i)
    {
        if(GetPlayerVehicleID(i) == vehicleid)
            return true;
    }
    return false;
}

CMD:apkills( playerid, params[ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 1 )
    {
        #pragma unused params
        if ( GetPVarInt( playerid, "AP_KILLS" ) == 0 )
        {
            SendClientMessage( playerid, COLOR_WHITE, "Dabar nebematysite serverio þaidëjø atliekamø nuþudymu." );
            SetPVarInt( playerid, "AP_KILLS", 1 );
            return 1;
        }
        else if ( GetPVarInt( playerid, "AP_KILLS" ) == 1 )
        {
            SendClientMessage( playerid, COLOR_WHITE, "Dabar matysite serverio þaidëjø atliekamus nuþudimus" );
            SetPVarInt( playerid, "AP_KILLS", 0 );
            return 1;
        }
    }
    return 1;
}

CMD:specoff( playerid, params[ ] )
{
    if(!GetPlayerAdminLevel(playerid) && !pInfo[ playerid ][ pFactionManager ])
        return 0;
    
    if(GetPlayerState(playerid) == PLAYER_STATE_SPECTATING)
    {
        TogglePlayerSpectating(playerid, false);
        SetCameraBehindPlayer(playerid);
        PlayerSpectatedPlayer[ playerid ] = INVALID_PLAYER_ID;
        DestroyDynamic3DTextLabel(SpecCommandLabel[ playerid ]);
    }
    return 1;
}
CMD:spec(playerid, params[])
{
    if(!GetPlayerAdminLevel(playerid) && !pInfo[ playerid ][ pFactionManager ])
        return 0;
    
    new giveplayerid;
    if(sscanf(params, "u", giveplayerid)) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /spec [þaidëjo id] ");

    if(!IsPlayerConnected(giveplayerid)) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje. ");

    if(pInfo[ playerid ][ pFactionManager ] && !GetPlayerAdminLevel(playerid) &&  GetPlayerAdminLevel(giveplayerid) && AdminDuty[ giveplayerid ])
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite stebëti administratoriaus darbe.");

    TogglePlayerSpectating(playerid, true);
    SetPlayerInterior(playerid, GetPlayerInterior(giveplayerid ));
    SetPlayerVirtualWorld(playerid, GetPlayerVirtualWorld(giveplayerid));
    
    if (IsPlayerInAnyVehicle(giveplayerid))
        PlayerSpectateVehicle(playerid, GetPlayerVehicleID(giveplayerid));
    else
        PlayerSpectatePlayer(playerid, giveplayerid);

    PlayerSpectatedPlayer[ playerid ] = giveplayerid;

    //SpecCommandLabel[ playerid ] = CreateDynamic3DTextLabel(" ", 0x00000044, 0.0, 0.0, 0.0, 10.0, .attachedplayer = giveplayerid);
        
    if(!IsPlayerInAnyVehicle(playerid) && !GetPVarInt(playerid, "SpectateWeaponCount"))
    {
        new string[128], count, weaponid, ammo;
        for(new i = 0; i < 13; i++)
        {   
            GetPlayerWeaponData(playerid, i, weaponid, ammo);
            if(weaponid && ammo)
            {
                count++;
                format(string, sizeof(string),"%s|%d|%d", string, weaponid, ammo);
            }
        }
        SetPVarString(playerid, "SpectateWeaponString", string);
        SetPVarInt(playerid, "SpectateWeaponCount", count);
    }
    ResetPlayerWeapons(playerid);
    return 1;
}

/*
CMD:afrisk(playerid, params[])
{
    if(!GetPlayerAdminLevel(playerid))
        return 0;

    new targetid,
        string[20 + MAX_ITEM_NAME ];

    if(sscanf(params, "u", targetid))
        return SendClientMessage(playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /frisk [þaidëjo id/dalis vardo]");

    if(!IsPlayerConnected(targetid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");

    if(IsPlayerInventoryEmpty(targetid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, þaidëjas neturi nei vieno daikto.");


//    ShowPlayerInvInfoForPlayer(targetid, playerid);

    SendClientMessage( playerid, COLOR_GREEN2, "_____________________ Laikomi ginklai __________________");

    for(new i = 0; i < 11; i++)
    {
        new wep,
            ammo,
            wepname[ 24 ];
        GetPlayerWeaponData(targetid, i, wep, ammo);
        if ( wep > 0 )
        {
            GetWeaponName(wep, wepname, sizeof(wepname));
            format(string, sizeof(string)," Ginklas %s ðoviniø %d ", wepname, ammo);
            SendClientMessage(playerid, COLOR_FADE1, string);
        }
    }
    return 1;
}
*/
/*
CMD:aproperty( playerid, params[ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 1 )
    {
        new giveplayerid,
            string[ 126 ];
        if ( sscanf( params, "u", giveplayerid ) ) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /aproperty [þaidëjo id]" );
        if ( !IsPlayerConnected( giveplayerid ) )  return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");
        new
            zone[ MAX_ZONE_NAME ];

        SendClientMessage( playerid, COLOR_LIGHTRED, "________Turimi namai________");
        foreach(Houses,h)
        {
            if ( hInfo[h][hOwner] == pInfo[ giveplayerid ][ pMySQLID ] )
            {
                Get2DZone( hInfo[ h ][ hEnter ][ 0 ], hInfo[ h ][ hEnter ][ 1 ], zone, MAX_ZONE_NAME );
                format( string, 126, "Namas(ID:%d) Vertë: %d Vieta: %s", h, hInfo[ h ][ hPrice ], zone );
                SendClientMessage( playerid, COLOR_WHITE, string );
            }
        }
        SendClientMessage( playerid, COLOR_LIGHTRED, "________Turimi verslai________");
        for(new i = 0; i < GetBusinessCount(); i++)
        {
            if ( bInfo[ i ][ bOwner ] == pInfo[ giveplayerid ][ pMySQLID ] )
            {
                Get2DZone( bInfo[ i ][ bEnter ][ 0 ], bInfo[ i ][ bEnter ][ 1 ], zone, MAX_ZONE_NAME );
                format( string, 126, "Verlsas(ID:%d) Vertë: %d Vieta: %s", i, bInfo[ i ][ bPrice ], zone );
                SendClientMessage( playerid, COLOR_WHITE, string );
            }
        }
        SendClientMessage( playerid, COLOR_LIGHTRED, "________Turimi garaþài________");
        foreach(Garages,h)
        {
            if ( pInfo[ giveplayerid ][ pMySQLID ] == gInfo[ h ][ gOwner ] )
            {
                Get2DZone( gInfo[ h ][ gEntrance ][ 0 ], gInfo[ h ][ gEntrance ][ 1 ], zone, MAX_ZONE_NAME );
                format( string, 126, "Garaþas(ID:%d) Vertë: %d Vieta: %s", h, gInfo[ h ][ gPrice ], zone );
                SendClientMessage( playerid, COLOR_WHITE, string );
            }
        }
        SendClientMessage( playerid, COLOR_LIGHTRED, "________Turimi automobiliai________");
        format           ( string, 140, "SELECT cName,cNumbers,cVehID FROM `vehicles` WHERE `cOwner` = %d", pInfo[ giveplayerid ][ pMySQLID ] );
        new Cache:result = mysql_query(DbHandle,  string );
        new slot = 1;
        for(new i = 0; i < cache_get_row_count(); i++)
        {
            new vName[ 24 ],
                Numbers[ 24 ],
                spawned;

            cache_get_field_content(i, "cName", vName);
            cache_get_field_content(i, "cNumbers", Numbers);
            spawned = cache_get_field_content_int(i, "cVehID");
            format( string, 126, "Maðina(ID:%d): %s Numeriai: %s Serverio ID: %d", slot, vName, Numbers, spawned);
            SendClientMessage( playerid, COLOR_WHITE, string );
            slot++;
        }
        cache_delete(result);
    }
    return 1;
}
*/
CMD:ahelp( playerid, params[ ] )
{
    if( GetPlayerAdminLevel(playerid) >= 1 )
    {
        #pragma unused params

        SendClientMessage( playerid, COLOR_LIGHTRED, "|____________________________ADMINISTRATORIAUS SKYRIUS____________________________|");
        if ( GetPlayerAdminLevel(playerid) >= 1 )
        {
            SendClientMessage( playerid, COLOR_WHITE, "[AdmLvl 1] /kick /ban /warn /jail /noooc /adminduty /gethere /check /afrisk /fon "),
            SendClientMessage( playerid, COLOR_FADE1, "[AdmLvl 1] /freeze /slap /spec /specoff /setint /setvw /intvw /masked /aheal /spawn ");
            SendClientMessage( playerid, COLOR_WHITE, "[AdmLvl 1] /mark /lockacc /rc  /setskin  /aproperty /apkills /fon ");
            SendClientMessage( playerid, COLOR_FADE1, "[AdmLvl 1] PERSIKËLIMAS: /gotols /gotofc /gotobb /gotopc /goto /gotomark /gotobiz /gotohouse /gotogarage /gotopos");
            SendClientMessage( playerid, COLOR_WHITE, "[AdmLvl 1] TR. PRIEMONËS: /getoldcar /rtc /rfc /rjc /rc");				
        }
        if ( GetPlayerAdminLevel(playerid) >= 2 )
            SendClientMessage( playerid, COLOR_FADE1, "[AdmLvl 2] /dtc /gotocar /mute/rac ");
        if ( GetPlayerAdminLevel(playerid) >= 3 )
            SendClientMessage( playerid, COLOR_WHITE, "[AdmLvl 3] /sethp /setarmour /forcelogout /hideadmins /serverguns /checkgun /kickall ");
        if ( GetPlayerAdminLevel(playerid) >= 4 )
        {
            SendClientMessage( playerid, COLOR_FADE1, "[AdmLvl 4] /auninvite /givemoney /giveweapon /amenu /intmenu"),
            SendClientMessage( playerid, COLOR_WHITE, "[AdmLvl 4] /makeleader /setstat /setstatcar /gotohouse /gotobiz");			
            SendClientMessage( playerid, COLOR_FADE1, "[AdmLvl 4] /makeadmin /makemoderator /cartax /housetax /biztax");
            SendClientMessage(playerid, COLOR_WHITE, "[AdmLvl 4] /makefactinomanager  /giveitem ");
        }
    }
    return 1;
}

/*
CMD:giveitem(playerid,params[])
{
    if(pInfo[ playerid ][ pAdmin ] < 4)
        return 0;

    new giveplayerid,
        itemid,
        amount,
        string[160];

    if(sscanf(params,"uii", giveplayerid, itemid, amount))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /giveitem [þaidëjo id/dalis vardo] [DaiktoID] [Kiekis(0 atëmimui)]");

    if(!IsPlayerConnected(giveplayerid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");

    if(!IsValidItem(itemid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: blogas daikto ID..");

    if(amount < 0)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: minimalus kiekis 1. Ávedus 0, daiktas bus atimtas.");


    if(!amount)
    {
        if(!IsItemInPlayerInventory(giveplayerid, itemid))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, þaidëjas tokio daikto neturi.");

        format(string, sizeof(string), "AdmWarn: Administratorius (%s) atemë (%s) ið veikëjo (%s)",GetName(playerid), GetItemName(itemid), GetName(giveplayerid));
        SendAdminMessage(COLOR_LIGHTRED, string);
        GivePlayerItem(giveplayerid, itemid, -GetPlayerItemAmount(giveplayerid, itemid));
    }
    else 
    {
        if(IsItemDrug(itemid))
        {
            NarkLog(pInfo[ playerid ][ pMySQLID ], 6, pInfo[ giveplayerid ][ pMySQLID ], GetItemName(itemid), amount);
            NarkLog(pInfo[ giveplayerid ][ pMySQLID ], 5, pInfo[ playerid ][ pMySQLID ], GetItemName(itemid), amount);
        }
        if((IsPlayerInventoryFull(giveplayerid) && !IsItemStackable(itemid)) || (IsPlayerInventoryFull(giveplayerid) && IsItemStackable(itemid) && !IsItemInPlayerInventory(giveplayerid, itemid)))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, þaidëjo inventorius pilnas.");

        if(itemid == ITEM_PHONE)
        {
            new more = random(32) * 1000;
            pInfo[giveplayerid][pPhone] = 110000 + pInfo[ giveplayerid ][ pMySQLID ] + more;
        }
        format(string, sizeof(string), "AdmWarn: Administratorius (%s) suteikë (%s), kiekis (%d) veikëjui (%s)",GetName(playerid),GetItemName(itemid),amount,GetName(giveplayerid));
        SendAdminMessage(COLOR_ADM, string );
        format(string, sizeof(string), "Davë daiktà: %s", GetItemName(itemid));
        AdminLog(pInfo[ playerid ][ pMySQLID ], pInfo[ giveplayerid ][ pMySQLID ], string );
        GivePlayerItem(giveplayerid, itemid, amount);
    }
    return 1;
}
*/


 GetVehiclePlayerCount(vehicleid)
{
    new count;
    foreach(new i : Player)
        if(IsPlayerInVehicle(i, vehicleid))
            count++;
    return count;
}
 HasVehicleDriver(vehicleid)
{
    foreach(new i : Player)
        if(IsPlayerInVehicle(i, vehicleid) && !GetPlayerVehicleSeat(i))
            return true;
    return false;
}
 GetVehicleDriver(vehicleid)
{
    foreach(new i : Player)
        if(IsPlayerInVehicle(i, vehicleid) && !GetPlayerVehicleSeat(i))
            return i;
    return INVALID_PLAYER_ID;
}

 AcesToSVehicle( vehicleid, playerid )
{
    printf("AcesToSVehicle(%d, %d). Player faction:%d Vehicle faction:%d Vehicle Rank:%d",
        vehicleid, playerid, 
        pInfo[ playerid ][ pMember ],
        sVehicles[ vehicleid ][ Faction ],
        sVehicles[ vehicleid ][ Rang ]);
    if ( cInfo[ vehicleid ][ cOwner ] != 0 ) return true;
    if ( sVehicles[ vehicleid ][ Faction ] > 0 && sVehicles[ vehicleid ][ Faction ] == pInfo[ playerid ][ pMember ] && sVehicles[ vehicleid ][ Rang ] <= pInfo[ playerid ][ pRank ] ) return true;
    else if ( sVehicles[ vehicleid ][ Job ] > 0 && pInfo[ playerid ][ pJob ] == sVehicles[ vehicleid ][ Job ] ) return true;
    else if ( sVehicles[ vehicleid ][ Faction ] == 0 && sVehicles[ vehicleid ][ Job ] == 0 ) return true;
    else return false;
}
public OnPlayerEnterVehicle(playerid, vehicleid, ispassenger)
{
    #if defined DEBUG 
        printf("OnPlayerEnterVehicle(%s, %d, %d)",GetName(playerid), vehicleid, ispassenger);
    #endif
    new Float:Kords[3];
    SetPVarInt( playerid, "FALSE_ENTER", 1 );
    if(cInfo[vehicleid][cLock] == 1)
    {
        GetPlayerPos( playerid, Kords[ 0 ], Kords[ 1 ], Kords[ 2 ] );
        SetPlayerPos( playerid, Kords[ 0 ], Kords[ 1 ], Kords[ 2 ] +0.1 );
        SetVehicleParamsForPlayer( vehicleid, playerid, 0, cInfo[ vehicleid ][ cLock ] );
        SetPVarInt( playerid, "FALSE_ENTER", 0 );
    }


    if ( !ispassenger )
    {
        if ( isLicCar( vehicleid ) ) return 1;

        if ( !AcesToSVehicle( vehicleid, playerid ) )
        {
            GetPlayerPos( playerid, Kords[ 0 ], Kords[ 1 ], Kords[ 2 ] );
            SetPlayerPos( playerid, Kords[ 0 ], Kords[ 1 ], Kords[ 2 ]+1 );
        }

        new ocupy = IsVehicleOcupied( vehicleid );
        if ( ocupy != INVALID_PLAYER_ID )
        {
            new string[ 126 ];
            format( string, sizeof(string), "Dëmesio, Jûsø tr. priemonæ ið Jûsø atëmë veikëjas: %s (ID:%d)", GetName( playerid ), playerid );
            SendClientMessage( ocupy, COLOR_WHITE, string );
        }
    }
    return 1;
}
 IsVehicleOcupied( vehicleid )
{
    foreach(Player,i)
    {
        if ( IsPlayerInVehicle( i, vehicleid ) && GetPlayerVehicleSeat( i ) == 0 )
            return i;
    }
    return INVALID_PLAYER_ID;
}

public OnPlayerExitVehicle(playerid, vehicleid)
{
    #if defined DEBUG
        printf("[debug] OnPlayerExitVehicle(%s, %d)", GetName(playerid), vehicleid);
    #endif
    return 1;
}
/*
forward PlayerReturnToTrashVehicle(playerid,vehicleid);
public PlayerReturnToTrashVehicle(playerid,vehicleid)
{
    if(IsPlayerInVehicle(playerid, vehicleid))
        return 1;

    EndTrashMission(playerid);
    SendClientMessage(playerid, GRAD,"Nespëjote gráþti á transporto priemonà, todël misija buvo baigta.");
    return 1;
}*/

 isLicCar( vehicleid )
{
    if ( cInfo[ vehicleid ][ cOwner ] != 0 ) return false;
    if ( sVehicles[ vehicleid ][ Faction ] == -1 ) return true;
    else return false;
}

public OnPlayerStateChange(playerid, newstate, oldstate)
{
    #if defined DEBUG
        printf("OnPlayerStateChange(%s, %d, %d)", GetName(playerid), newstate, oldstate);
    #endif
    SetPVarInt( playerid, "PLAYER_STATE", newstate );

    if(newstate == PLAYER_STATE_DRIVER || newstate == PLAYER_STATE_PASSENGER)
    {
        if(!IsDriveByWeapon(GetPlayerWeapon(playerid)))
        {
            SetPlayerArmedWeapon(playerid, 0);
        }
        LastVehicleDriverSqlId[ GetPlayerVehicleID(playerid) ] = GetPlayerSqlId(playerid);
    }


    if(newstate == PLAYER_STATE_DRIVER)
    {
        new veh = GetPlayerVehicleID( playerid );

        ShowPlayerInfoText( playerid );
        /*SetPVarInt( playerid, "PLAYER_VEH_MODEL", GetVehicleModel( veh ) );
        if ( cInfo[ veh ][ cLock ] == 1 && GetPVarInt( playerid, "FALSE_ENTER" ) == 0 )
            return KickPlayer( "AC", playerid, "álipo á uþrakinta tr. priemone." );


        DeletePVar( playerid, "FALSE_ENTER" );
        if ( cInfo[ veh ][ cOwner ] == 0 && sVehicles[ veh ][ Job ] > 0 )
        {

        }
    */
/*

        if ( Audio_IsClientConnected( playerid ) )
        {
            Audio_StopRadio( playerid );
            if ( VehicleRadio[ veh ] != 99 )
            {
                SetPlayerRadio( playerid, VehRadio[ veh ] );
                SetPlayerRadioVolume( playerid, GetPVarInt( playerid, "VOLUME" ) );
            }
        }
        else if ( Audio_IsClientConnected( playerid ) == 0 )
        {
            if ( VehicleRadio[ veh ] != 99 )
                SetPlayerRadio( playerid, VehRadio[ veh ] );
        }
*/
        OldCar[ playerid ] = veh;
/*
        if ( isLicCar( veh ) )
        {
            SendClientMessage( playerid, COLOR_LIGHTRED2,"** Los Santos Driver License Center "),
            SendClientMessage( playerid, COLOR_LIGHTRED2,"** KAINORAÐTIS: Automobilio - 1200$ | Motociklo - 900$ | Laivybos - 300$ |Skraidymo - 5600$"),				
            SendClientMessage( playerid, COLOR_WHITE," ** Norëdami pradëti egzaminà licencijai ágyti raðykite komandà: /takelesson ");
            return 1;
        }
        
        */
        /*if(sVehicles[ veh ][ Job ] == JOB_TRASH)
        {
            KillTimer(TrashTimer[ playerid ]);
            if(TrashMission[ playerid]  == TRASH_MISSION_NONE)
            {
                new string[128];
                format(string,sizeof(string),"~n~~n~~n~Rinkite siuksles ið pazymetø tasku~n~Naudokite /takegarbage ju paemimui~n~Siame sunkvezimyje yra %d maisai",TrashBagsInTrashVehicle[ veh ]);
                GameTextForPlayer(playerid, string, 3000, 7);
                SendClientMessage(playerid, COLOR_NEWS, string);
            }
            // Returninam nes nenorim kad sakytu "SPAUSKITE ALT"
            return 1;

        }
    */
/*
        if(Engine[veh] == false && VehicleHasEngine( GetVehicleModel( veh ) ) )
            ShowInfoText(playerid, "~w~SPAUSKITE ~r~ALT~n~~w~Kad uzvestumete automobili.", 2000);

        if( !VehicleHasEngine( GetVehicleModel( veh ) ) )
            VehicleEngine( veh, 1 );

        if ( pInfo[ playerid ][ pMySQLID ] == cInfo[ veh ][ cOwner ] && cInfo[ veh ][ cTicket ] > 0)
        {
            new string[ 126 ];
            format           ( string, 126, "Jûs nesate sumokëjàs automobilio baudos, kurios suma lygi $%d", cInfo[ veh ][ cTicket ] );
            SendClientMessage( playerid, COLOR_WHITE, string );
            return 1;
        }
    }
    */
    }
    else if(newstate == PLAYER_STATE_ONFOOT)
    {
        //UpdatePlayerInfoText(playerid);

//        new veh = OldCar[ playerid ];
        /*if(sVehicles[ veh ][ Job ] == JOB_TRASH && pInfo[ playerid ][ pJob ] == JOB_TRASH)
        {
            if(TrashMission[ playerid ] != TRASH_MISSION_NONE)
            {
                // PAdidinta iki 90 sekundziu Mantyvdo nurodymais.
                TrashTimer[ playerid ] = SetTimerEx("PlayerReturnToTrashVehicle", 90000, false, "ii",playerid,veh);
                GameTextForPlayer(playerid, "~n~~n~~n~Turite grizti i automobili per 90 sekundziu~n~, kitaip misija baigsis.",5000, 7);
            }
            else 
                SetVehicleToRespawn(veh);
            
            return 1;
        }
        */
    }
    if(oldstate == PLAYER_STATE_DRIVER)
    {
/*        if ( VehicleRadio[ OldCar[ playerid ] ] != 99 )
            StopPlayerRadio( playerid );

*/
        //Check_VHP( OldCar[ playerid ], 1 );
        if(GetPVarInt(playerid,"used") == 1)
        {
            SetCameraBehindPlayer(playerid);
            DestroyPlayerObject(playerid,vehview[playerid]);
            SetPVarInt(playerid,"used",0);
        }
       /* if ( Belt[ playerid ] == true )
        {
            new string[ 126 ];
            format      ( string, 126, "* %s iðlipdamas atsisegë saugos dirþà .", GetPlayerNameEx( playerid ) );
            ProxDetector( 15.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
            Belt[ playerid ] = false;
        }
        */
        if ( Checkpoint[ playerid ] == CHECKPOINT_LIC )
        {
            DisablePlayerCheckpoint( playerid );
            Checkpoint[ playerid ] = CHECKPOINT_NONE;
            SetVehicleToRespawn( OldCar[ playerid ] );
            SetPVarInt( playerid, "LIC_TYPE", 0 );
            SetPVarInt( playerid, "LIC_TIME", 0 );
        }
    }
    if(oldstate == PLAYER_STATE_PASSENGER)
    {/*
        if ( VehicleRadio[ OldCar[ playerid ] ] != 99 )
            StopPlayerRadio( playerid );
*/
        if(GetPVarInt(playerid,"used") == 1)
        {
            SetCameraBehindPlayer(playerid);
            DestroyPlayerObject(playerid,vehview[playerid]);
            SetPVarInt(playerid,"used",0);
        }

        /*if ( Belt[ playerid ] == true )
        {
            new string[ 126 ];
            format( string, 126, "* %s atsisega saugos dirþà ir iðlipa ið tr. priemonës.", GetPlayerNameEx( playerid ) );
            ProxDetector( 15.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
            Belt[ playerid ] = false;
        }*/
    }
    if(newstate == PLAYER_STATE_PASSENGER)
    {
        new veh = GetPlayerVehicleID( playerid );

        if(IsVehicleSeatUsedForCargo(veh, GetPlayerVehicleSeat(playerid)))
        {
            RemovePlayerFromVehicle(playerid);
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite sësti èia, kadangi èia pridëtà kroviniø.");
        }
/*
        if ( Audio_IsClientConnected( playerid ) )
        {
            Audio_StopRadio( playerid );
            if ( VehicleRadio[ veh ] != 99 )
            {
                SetPlayerRadio( playerid, VehRadio[ veh ] );
                SetPlayerRadioVolume( playerid, GetPVarInt( playerid, "VOLUME" ) );
            }
        }
        else if ( Audio_IsClientConnected( playerid ) == 0 )
        {
            if ( VehicleRadio[ veh ] != 99 )
                SetPlayerRadio( playerid, VehRadio[ veh ] );
        }
*/
        OldCar[ playerid ] = veh;
    }
    #if defined DEBUG
        printf("OnPlayerStateChange(%s, %d, %d) end", GetName(playerid), newstate, oldstate);
    #endif
    return 1;
}
/*public OnPlayerClickMap(playerid, Float:fX, Float:fY, Float:fZ)
{
    if (GetPlayerAdminLevel(playerid) >= 1)
    {
        MapAndreas_FindZ_For2DCoord(fX, fY, fZ);
        if (IsPlayerInAnyVehicle(playerid))
            return SetVehiclePos(GetPlayerVehicleID(playerid), fX, fY, fZ+1);
        else
            return SetPlayerPos(playerid, fX, fY, fZ+1);
    }
    return 1;
}
*/
 IsVehicleSeatUsedForCargo(vehicleid, seat)
{
    // priekines vietose nebuna kroviniu
    if(seat < 2)
        return false;
    new model = GetVehicleModel(vehicleid);

    switch(model)
    {
        case 413, 459, 482, 440, 498:
        {
            if(GetVehicleCargoCount(vehicleid))
                return true;
        }
    }
    return false;
}

GetVehicleCargoCount(vehicleid,cargoid = -1)
{
    new count = 0;
    for(new i = 0; i < sizeof VehicleCargo[]; i++)
    {
        if(VehicleCargo[ vehicleid ][ i ][ Amount ])
			if(cargoid == -1)
				count += VehicleCargo[ vehicleid ][ i ][ Amount ];
			else 
				if(VehicleCargo[ vehicleid ][ i ][ CargoId ] ==cargoid)
					return VehicleCargo[ vehicleid ][ i ][ Amount ];
    }
    return count;
}

public OnPlayerEnterCheckpoint(playerid)
{
    #if defined DEBUG
        printf("[debug] OnPlayerEnterCheckpoint(%s)", GetName(playerid));
    #endif
    
    switch(Checkpoint[playerid])
    {
        case CHECKPOINT_CAR:
        {
            DisablePlayerCheckpoint(playerid);
            Checkpoint[playerid] = CHECKPOINT_NONE;
            PlayerPlaySound(playerid, 1057, 0.0, 0.0, 0.0);
            return 1;
        }
		
		case CHECKPOINT_SHIP:
		{
			DisablePlayerCheckpoint(playerid);
            Checkpoint[playerid] = CHECKPOINT_NONE;
            PlayerPlaySound(playerid, 1057, 2774.0313,-2417.8794,13.6462);
            return 1;
		}
  /*/
        case CHECKPOINT_TRASH_DROPOFF:
        {
            new vehicleid = GetPVarInt(playerid, "TrashMission_Vehicle");
            pInfo[ playerid ][ pPayCheck ] += TRASH_MISSION_COMPLETED_BONUS;
            CurrentTrashCp[ playerid ]  = 0;
            ShowMissionTrashObjects(playerid, TrashMission[ playerid ] );
            TrashMission[ playerid ] = TRASH_MISSION_NONE;
            cmd_ame(playerid, "pasuka automobilio raktelá ir iðjungia variklá.");
            SendClientMessage(playerid, GRAD, "Baigëte misijà. Jums prie algos buvo pridëti " #TRASH_MISSION_COMPLETED_BONUS "$ Norëdami pradëti dar vienà misijà: /startmission");
            
            VehicleEngine(vehicleid, 0);
            Engine[vehicleid] = false;
            TrashBagsInTrashVehicle[ vehicleid ] = 0;
            DisablePlayerCheckpoint( playerid );
            Checkpoint[ playerid ] = CHECKPOINT_NONE;
        }
        */
        case CHECKPOINT_BACKUP:
        {
            DisablePlayerCheckpoint( playerid );
            Checkpoint[ playerid ] = CHECKPOINT_NONE;
            SetPVarInt( playerid, "BACKUP", INVALID_PLAYER_ID );
        }/*
        case CHECKPOINT_LIC:
        {
            new veh = GetPlayerVehicleID( playerid ),
                Float:VehHealth;
            GetVehicleHealth( veh, VehHealth );
            switch( GetPVarInt( playerid, "LIC_TYPE" ) )
            {
                case 1:
                {
                    if ( GetPVarInt( playerid, "LIC_CP" ) == 38)
                    {
                        if ( GetPlayerMoney(playerid) > 1119 )
                        {
                            new mistakes = GetPVarInt( playerid, "LIC_MISTAKE" ),
                                string[ 256 ];
                            if ( mistakes > 0 )
                                format( string, 256, "\tVirðijote greitá %d kartà (-us)\n", mistakes );
                            if ( VehHealth < 900 )
                                format( string, 256, "%s\tAutomobilis buvo apgadinta\n",string );
                            if ( strlen( string ) > 0 )
                            {
                                format( string, 256, "{FFFFFF}Jûs padarëte ðiais klaidas:\n%s\nTodël egzaminas skubiai nutraukiamas. ", string );
                                ShowPlayerDialog( playerid, 9999, DIALOG_STYLE_MSGBOX , "Egzaminas neiðlaikytas", string, "Iðjungti", "");
                                SetVehicleToRespawn( veh );
                                Checkpoint[ playerid ] = CHECKPOINT_NONE;
                                SetPVarInt             ( playerid, "LIC_TIME", 0 );
                                DisablePlayerCheckpoint( playerid );
                                return 1;
                            }
                            pInfo[ playerid ][ pLicCar ] = 1;
                            GivePlayerItem(playerid, ITEM_TEORIJA, -1);
							SendClientMessage(playerid, COLOR_LIGHTRED2,"** Los Santos Driver License Center "),		
							SendClientMessage(playerid, COLOR_WHITE," ** Jûs sëkmingai iðsilaikëte vairavimo testà ir ágijote licencija vairuoti automobilá. ");
                            GivePlayerMoney( playerid, -1200 ); //Teisiø kainà.
                            PlayerPlaySound( playerid, 1057, 0.0, 0.0, 0.0);
                            SetPVarInt     ( playerid, "LIC_TIME", 0 );
                            DisablePlayerCheckpoint( playerid );
                            Checkpoint[ playerid ] = CHECKPOINT_NONE;
                            SetVehicleToRespawn( veh );
                            return 1;
                        }
                        else
                        {
                            SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai pinigø, kad galëtumëte laikyti egzaminà. Egzaminas kainuojà 1200$ " );
                            return 1;
                        }
                    }
                    SetPVarInt     ( playerid, "LIC_CP", GetPVarInt( playerid, "LIC_CP" ) + 1 );
                    ShowInfoText   ( playerid,"~w~Vaziuokite i tolimesni zymekli",3000 );
                    PlayerPlaySound( playerid, 1057, 0.0, 0.0, 0.0);
                    DisablePlayerCheckpoint(playerid);
                    Checkpoint[playerid] = CHECKPOINT_NONE;
                    setLicenseCp   ( playerid );
                    return 1;
                }
                case 2:
                {
                    if ( GetPVarInt( playerid, "LIC_CP" ) == 38 )
                    {
                        if ( GetPlayerMoney(playerid) > 899 )
                        {
                            new mistakes = GetPVarInt( playerid, "LIC_MISTAKE" ),
                                string[ 256 ];
                            if ( mistakes > 0 )
                                format( string, 256, "\tVirðijote greitá %d kartà (-us)\n", mistakes );
                            if ( VehHealth < 900 )
                                format( string, 256, "%s\tMotociklas buvo apgadintas\n",string );
                            if ( strlen( string ) > 0 )
                            {
                                format( string, 256, "{FFFFFF}Jûs padarëte ðiais klaidas:\n%s\nTodël egzaminas skubiai nutraukiamas.", string );
                                ShowPlayerDialog( playerid, 9999, DIALOG_STYLE_MSGBOX , "Vairavimo testas neiðlaikytas", string, "Iðjungti", "");
                                SetVehicleToRespawn( veh );
                                GivePlayerItem(playerid, ITEM_TEORIJA, -1);
                                Checkpoint[ playerid ] = CHECKPOINT_NONE;
                                SetPVarInt             ( playerid, "LIC_TIME", 0 );
                                DisablePlayerCheckpoint( playerid );
                                return 1;
                            }
                            SetVehicleToRespawn( veh );
                            pInfo[ playerid ][ pLicMoto ] = 1;
							SendClientMessage( playerid, COLOR_LIGHTRED2,"** Los Santos Driver License Center "),		
							SendClientMessage( playerid, COLOR_WHITE," ** Jûs sëkmingai iðsilaikëte motociklo vairavimo testà ir ágijote licencija vairuoti motocikla. ");
                            GivePlayerMoney( playerid, -900 );//Motociklo teisiø kainà
                            PlayerPlaySound( playerid, 1057, 0.0, 0.0, 0.0);
                            SetPVarInt     ( playerid, "LIC_TIME", 0 );
                            DisablePlayerCheckpoint( playerid );
                            Checkpoint[ playerid ] = CHECKPOINT_NONE;
                            return 1;
                        }
                        else
                        {
                            SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai pinigø, kad galëtumëte laikyti egzaminà. Egzaminas kainuojà 900$ " );
                            return 1;
                        }
                    }
                    SetPVarInt     ( playerid, "LIC_CP", GetPVarInt( playerid, "LIC_CP" ) + 1 );
                    ShowInfoText   ( playerid,"~w~Vaziuokite i tolimesni zymekli",3000 );
                    PlayerPlaySound( playerid, 1057, 0.0, 0.0, 0.0);
                    DisablePlayerCheckpoint(playerid);
                    Checkpoint[playerid] = CHECKPOINT_NONE;
                    setLicenseCp   ( playerid );
                    return 1;
                }
                case 4:
                {
                    if ( GetPVarInt( playerid, "LIC_CP" ) == 13 )
                    {
                        if ( GetPlayerMoney(playerid) >= 299 )
                        {
                            new
                                string[ 256 ];
                            if ( VehHealth < 900 )
                                format( string, 256, "%s\tValtis buvo apgadinta\n",string );
                            if ( strlen( string ) > 0 )
                            {
                                format( string, 256, "{FFFFFF}Jûs padarëte ðiais klaidas:\n%s\nTodël egzaminas skubiai nutraukiamas.", string );
                                ShowPlayerDialog( playerid, 9999, DIALOG_STYLE_MSGBOX , "Laivybos egzaminas nutrauktas", string, "Iðjungti", "");
                                SetVehicleToRespawn( veh );
                                GivePlayerItem(playerid, ITEM_TEORIJA, -1);

                                Checkpoint[ playerid ] = CHECKPOINT_NONE;
                                SetPVarInt             ( playerid, "LIC_TIME", 0 );
                                DisablePlayerCheckpoint( playerid );
                                return 1;
                            }
                            SetVehicleToRespawn( veh );
                            pInfo[ playerid ][ pLicBoat ] = 1;
							SendClientMessage( playerid, COLOR_LIGHTRED2,"** Los Santos License Center "),		
							SendClientMessage( playerid, COLOR_WHITE," ** Sëkmingai iðsilaikëte laivybos egzaminà ir ágijote licencija plaukti/valdyti bet koká laivà. ");
                            GivePlayerMoney( playerid, -300 );
                            PlayerPlaySound( playerid, 1057, 0.0, 0.0, 0.0);
                            SetPVarInt     ( playerid, "LIC_TIME", 0 );
                            DisablePlayerCheckpoint( playerid );
                            Checkpoint[ playerid ] = CHECKPOINT_NONE;
                            return 1;
                        }
                        else
                        {
                            SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai pinigø, kad galëtumëte laikyti egzaminà. Egzaminas kainuojà 300$ " );
                            return 1;
                        }
                    }
                    SetPVarInt( playerid, "LIC_CP", GetPVarInt( playerid, "LIC_CP" ) + 1 );
                    ShowInfoText( playerid,"~w~Plaukite i kita zymekli",3000 );
                    PlayerPlaySound( playerid, 1057, 0.0, 0.0, 0.0);
                    DisablePlayerCheckpoint(playerid);
                    Checkpoint[playerid] = CHECKPOINT_NONE;
                    setLicenseCp( playerid );
                    return 1;
                }
            }
        }
        */
        case CHECKPOINT_TLC:
        {
            DisablePlayerCheckpoint( playerid );
            Checkpoint[ playerid ] = CHECKPOINT_NONE;
            return 1;
        }
    }
    return 1;
}

public OnPlayerLeaveCheckpoint(playerid)
{
    return 1;
}


public OnPlayerEnterRaceCheckpoint(playerid)
{
    switch( Checkpoint[ playerid ] )
    {
        /*
        case CHECKPOINT_LIC:
        {
            switch( GetPVarInt( playerid, "LIC_TYPE" ) )
            {
                case 3:
                {
                    if ( GetPVarInt( playerid, "LIC_CP" ) == 14 )
                    {
                        new veh = GetPlayerVehicleID( playerid ),
                            Float:VehHealth;
                        GetVehicleHealth( veh, VehHealth );
                        if ( GetPlayerMoney(playerid) >= 5599)
                        {
                            new string[ 256 ];
                            if ( VehHealth < 900 )
                                format( string, 256, "%s\tLëktuvas buvo apdauþytas\n ",string );
                            if ( strlen( string ) > 0 )
                            {
                                format( string, 256, "{FFFFFF}Jûs padarëte ðiais klaidas:\n%s\nTodël neiðlaikëte skraidymo testo. ", string );
                                ShowPlayerDialog( playerid, 9999, DIALOG_STYLE_MSGBOX , "Skraidymo testas neiðlaikytas", string, "Iðjungti", "");
                                SetVehicleToRespawn( veh );
                                GivePlayerItem(playerid, ITEM_TEORIJA, -1);

                                Checkpoint[ playerid ] = CHECKPOINT_NONE;
                                SetPVarInt             ( playerid, "LIC_TIME", 0 );
                                DisablePlayerCheckpoint( playerid );
                                return 1;
                            }
                            SetVehicleToRespawn( veh );
                            pInfo[ playerid ][ pLicHeli ] = 1;
							SendClientMessage( playerid, COLOR_LIGHTRED2,"** Los Santos Piloting License Center "),		
							SendClientMessage( playerid, COLOR_WHITE," ** Jûs sëkmingai iðsilaikëte vairavimo testà ir ágijote licencija vairuoti. ");
                            GivePlayerMoney            ( playerid, -5600 );
                            PlayerPlaySound            ( playerid, 1057, 0.0, 0.0, 0.0);
                            SetPVarInt                 ( playerid, "LIC_TIME", 0 );
                            Data_SetPlayerLocation(playerid, "license_pilot_end");
                            DisablePlayerRaceCheckpoint( playerid );
                            Checkpoint[ playerid ] = CHECKPOINT_NONE;
                            return 1;
                        }
                        else
                        {
                            SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai pinigø, kad galëtumëte laikyti egzaminà. Egzaminas kainuojà 5600$ " );
                            return 1;
                        }
                    }
                    SetPVarInt      ( playerid, "LIC_CP", GetPVarInt( playerid, "LIC_CP" ) + 1 );
                    ShowInfoText    ( playerid,"~w~Skriskite i kita zymekli",3000 );
                    PlayerPlaySound ( playerid, 1057, 0.0, 0.0, 0.0);
                    DisablePlayerCheckpoint(playerid);
                    Checkpoint[playerid] = CHECKPOINT_NONE;
                    setLicenseCp    ( playerid );
                    return 1;
                }
            }
        }
        */
    }
    return 1;
}

public OnPlayerLeaveRaceCheckpoint(playerid)
{
    if ( GetPVarInt( playerid, "LIC_CP" ) == 12 && GetPVarInt( playerid, "LIC_TYPE" ) == 3 )
        DisablePlayerRaceCheckpoint(playerid);
    return 1;
}

public OnRconCommand(cmd[])
{
    return 1;
}

public OnPlayerRequestSpawn(playerid)
{
    return 1;
}

public OnObjectMoved(objectid)
{
    return 1;
}

public OnPlayerObjectMoved(playerid, objectid)
{
    return 1;
}

public OnPlayerPickUpPickup(playerid, pickupid)
{
    return 1;
}

public OnVehicleMod(playerid, vehicleid, componentid)
{
    #if defined DEBUG
        printf("[debug] OnVehicleMod(%s, %d, %d)", GetName(playerid), vehicleid, componentid);
    #endif
//    KickPlayer( "AC", playerid, "Tuninguoja automobilá, tuningavimo salone." );
    SetVehicleToRespawn( vehicleid );
    return 1;
}

public OnVehiclePaintjob(playerid, vehicleid, paintjobid)
{
    #if defined DEBUG
        printf("[debug] OnVehiclePaintjob(%s, %d, %d)", GetName(playerid), vehicleid, paintjobid);
    #endif
    return 1;
}

public OnVehicleRespray(playerid, vehicleid, color1, color2)
{
    #if defined DEBUG
        printf("[debug] OnVehicleRespray(%s, %d, %d, %d)", GetName(playerid), vehicleid, color1, color2);
    #endif
    return 1;
}

public OnPlayerSelectedMenuRow(playerid, row)
{
    return 1;
}

public OnPlayerExitedMenu(playerid)
{
    return 1;
}

public OnPlayerInteriorChange(playerid, newinteriorid, oldinteriorid)
{
    #if defined DEBUG
        printf("[debug] OnPlayerInteriorChange(%s, %d, %d)", GetName(playerid), newinteriorid, oldinteriorid);
    #endif
    CancelEdit(playerid);
    return 1;
}

public OnPlayerKeyStateChange(playerid, newkeys, oldkeys)
{
    //Anti CuffedJump
    if(newkeys & KEY_JUMP && !(oldkeys & KEY_JUMP) && GetPlayerSpecialAction(playerid) == SPECIAL_ACTION_CUFFED) 
        ApplyAnimation(playerid, "GYMNASIUM", "gym_jog_falloff",4.1,0,1,1,0,0);

    if( PRESSED(KEY_CROUCH) && GetPlayerCameraMode(playerid) == 55 )
    {
        SetPlayerArmedWeapon(playerid, 0);
        PutPlayerInVehicle(playerid, GetPlayerVehicleID ( playerid ), GetPlayerVehicleSeat(playerid));
    }

    if ( PRESSED( KEY_SPRINT ) )
    {
        if(gPlayerUsingLoopingAnim[playerid] == true)
        {
            gPlayerUsingLoopingAnim[playerid] = false;
            ApplyAnimation(playerid, "CARRY", "crry_prtial", 4.0, 0, 0, 0, 0, 0);
        }
        if(IsOnePlayAnim[playerid] == true)
        {
            ClearAnimations(playerid);
            IsOnePlayAnim[playerid] = false;
        }
        if(BackOut[playerid] == 1)
        {
            ApplyAnimation(playerid,"SUNBATHE","parksit_m_out",3.0,0,0,0,0,0);
        }
        if(BackOut[playerid] == 2)
        {
            SetPlayerSpecialAction(playerid,SPECIAL_ACTION_STOPUSECELLPHONE);
            RemovePlayerAttachedObject( playerid, 3 );
        }
        if(BackOut[playerid] == 4)
        {
            ApplyAnimation(playerid,"CAR_CHAT","carfone_out",3.0,0,0,0,0,0);
        }
        if(BackOut[playerid] == 5)
        {
            ApplyAnimation(playerid,"SUNBATHE","Lay_Bac_out",3.0,0,0,0,0,0);
        }
        if(BackOut[playerid] == 6)
        {
            ApplyAnimation(playerid,"ON_LOOKERS","shout_out",3.0,0,0,0,0,0);
        }
        if(BackOut[playerid] == 7)
        {
            ApplyAnimation(playerid,"ON_LOOKERS","pointup_out",3.0,0,0,0,0,0);
        }
        if(BackOut[playerid] == 8)
        {
            ApplyAnimation(playerid,"PED","seat_up",3.0,0,0,0,0,0);
        }
        BackOut[playerid] = 0;
    }
    if(!IsPlayerInAnyVehicle(playerid) && newkeys & KEY_SECONDARY_ATTACK)
    {
        if ( Ruko[ playerid ] > 0 )
        {
            SetPlayerSpecialAction( playerid, SPECIAL_ACTION_NONE);
            Ruko[ playerid ] = 0;
        }
        new Float:pos[3];
        GetPlayerPos(playerid, pos[0], pos[1], pos[2]);
        if(pos[1] < -1301.4 && pos[1] > -1303.2417 && pos[0] < 1786.2131 && pos[0] > 1784.1555)    // He is using the elevator button
            ShowElevatorDialog(playerid);
        else    // Is he in a floor button?
        {
            if(pos[1] > -1301.4 && pos[1] < -1299.1447 && pos[0] < 1785.6147 && pos[0] > 1781.9902)
            {
                // He is most likely using it, check floor:
                new i=20;
                while(pos[2] < GetDoorsZCoordForFloor(i) + 3.5 && i > 0)
                    i --;

                if(i == 0 && pos[2] < GetDoorsZCoordForFloor(0) + 2.0)
                    i = -1;

                if(i <= 19)
                {
                    CallElevator(playerid, i + 1);
                    GameTextForPlayer(playerid, "~r~Liftas iskviestas.", 3500, 4);
                }
            }
        }
    }
    if ( PRESSED( KEY_WALK ) && pInfo [playerid][pWalkStyle] == 1)
    {
        if ( !IsPlayerInAnyVehicle( playerid ) )
            LoopingAnim(playerid,"PED","WALK_Gang1",4.1,1,1,1,1,1);
    }
    if ( PRESSED( KEY_WALK ) && pInfo [playerid][pWalkStyle] == 2)
    {
        if ( !IsPlayerInAnyVehicle( playerid ) )
            LoopingAnim(playerid,"PED","WALK_Gang2",4.1,1,1,1,1,1);
    }
    if ( PRESSED( KEY_WALK ) && pInfo [playerid][pWalkStyle] == 3)
    {
        if ( !IsPlayerInAnyVehicle( playerid ) )
            LoopingAnim(playerid,"PED","WALK_FatWalk",4.1,1,1,1,1,1);
    }
    if ( PRESSED( KEY_WALK ) && pInfo [playerid][pWalkStyle] == 4)
    {
        if ( !IsPlayerInAnyVehicle( playerid ) )
            LoopingAnim(playerid,"PED","Wuzi_walk",3.0,1,1,1,1,1);
    }
    if ( PRESSED( KEY_WALK ) && pInfo [playerid][pWalkStyle] == 5)
    {
        if ( !IsPlayerInAnyVehicle( playerid ) )
            LoopingAnim(playerid,"PED","Walk_Wuzi",4.1,1,1,1,1,1);
    }
    if ( PRESSED( KEY_WALK ) && pInfo [playerid][pWalkStyle] == 6)
    {
        if ( !IsPlayerInAnyVehicle( playerid ) )
            LoopingAnim(playerid,"PED","Walk_player",4.1,1,1,1,1,1);
    }
    if ( PRESSED( KEY_WALK ) && pInfo [playerid][pWalkStyle] == 7)
    {
        if ( !IsPlayerInAnyVehicle( playerid ) )
            LoopingAnim(playerid,"PED","woman_walknorm",4.1,1,1,1,1,1);
    }
    if ( PRESSED( KEY_WALK ) && pInfo [playerid][pWalkStyle] == 8)
    {
        if ( !IsPlayerInAnyVehicle( playerid ) )
            LoopingAnim(playerid,"PED","woman_walkpro",4.1,1,1,1,1,1);
    }
//    if ( PRESSED( KEY_YES ) )
//        cmd_inv( playerid, "" );
    if ( PRESSED( KEY_ACTION ) )
    {
        if ( IsPlayerInAnyVehicle( playerid ) )
        {

            if ( GetPlayerState( playerid ) != PLAYER_STATE_DRIVER )
                return 1;
            new engine, lights, alarm, doors, bonnet, boot, objective,
                vehicleid = GetPlayerVehicleID( playerid );

            GetVehicleParamsEx(vehicleid, engine, lights, alarm, doors, bonnet, boot, objective);

            if ( lights == 1 )
                return SetVehicleParamsEx(vehicleid, engine, 0, alarm, doors, bonnet, boot, objective);
            else
                return SetVehicleParamsEx(vehicleid, engine, 1, alarm, doors, bonnet, boot, objective);
        }
        return 1;
    }
    
    if ( HOLDING( KEY_FIRE ) )
    {
        if ( GetPVarInt( playerid, "MECHANIC" ) == 1 && GetPlayerWeapon( playerid ) == 41 )
        {
            new car = GetNearestVehicle( playerid, 5.0 );
            if ( car != INVALID_VEHICLE_ID )
            {
                DeletePVar( playerid, "MECHANIC" );
                SetTimerEx("Mechaniku", 500, false, "dd", playerid, 30 );
            }
        }
    }
    if( newkeys == KEY_SECONDARY_ATTACK )
    {
        new veh = GetPlayerVehicleID(playerid);
        if( IsPlayerInAnyVehicle(playerid) && StartingEngine[playerid] == false && Engine[veh] == false && Laikas[playerid] == 0)
        {
            RemovePlayerFromVehicle(playerid);
            StartingEngine[playerid] = false;
            return 1;
        }
    }
    return 1;
    
}
public OnRconLoginAttempt(ip[], password[], success)
{
    return 1;
}

public OnPlayerTakeDamage(playerid, issuerid, Float:amount, weaponid, bodypart)
{
    #if defined DEBUG 
        printf("OnPlayerTakeDamage(%s, %s, %f, %d, %d)", GetName(playerid), GetName(issuerid), amount, weaponid, bodypart);
    #endif
    if ( issuerid == INVALID_PLAYER_ID ) 
        return 1;

    new Float:stat[2];
    GetPlayerHealth(playerid, stat[0]);
    GetPlayerArmour(playerid, stat[1]);

    // Jei turi str bonusu reikia padidint zala su melee ginklais.
    if(pInfo[ issuerid ][ pStrengthLevel ] && IsMeleeWeapon(weaponid))
    {
        new Float:extraDMG = amount / pInfo[ issuerid ][ pStrengthLevel ] / 10.0;
        if(extraDMG <= stat[1]) 
            SetPlayerArmour(playerid, stat[1] - extraDMG);
        else 
        {
            extraDMG -= stat[1];
            SetPlayerArmour(playerid, 0.0);
            SetPlayerHealth(playerid, stat[0] - extraDMG);
        }
    }
    return 1;
}

 IsMeleeWeapon(weaponid)
{
    switch(weaponid)
    {
        case 0 ..15: return true;
    }
    return false;
}
 IsDriveByWeapon(weaponid)
{
    // Ginklai su kuriais leidþiamas drive-by
	switch(weaponid)
	{
		case 25, 26, 27, 28, 29, 30, 31, 32: return true;
		default: return false;
	}
	return false;
}


public OnPlayerGiveDamage(playerid, damagedid, Float:amount, weaponid, bodypart)
{
    #if defined DEBUG 
        printf("[debug] OnPlayerGiveDamage(%d, %d, %f, %d, %d)", playerid, damagedid, amount, weaponid, bodypart);
    #endif
    if ( damagedid == INVALID_PLAYER_ID ) return 1;
    new
        ShooterWep = weaponid;
    if( Boxing[ damagedid ] == true )
    {
        new Float:HP;

        GetPlayerHealth( damagedid, HP );
        if ( !PlayerToPoint( 50.0, damagedid, 772.7922, -71.4917, 1000.5853 ) )
            BoxEnd( damagedid );

        if ( HP < 15 )
            BoxEnd( damagedid );
    }
    new string[ 32 ];
    for(new i = 0; i < 46; i++)
    {
        format(string, 32, "%d", i);
        if( GetPVarInt( damagedid, string ) != 0 ) break;

        SetTimerEx( "NullWeapons", 1000*120, false, "d", playerid );

    }
    format(string, 32, "%d", weaponid);
    SetPVarInt(damagedid, string, GetPVarInt(damagedid, string) + 1);
    return 1;
}

public OnPlayerUpdate(playerid)
{   
//    OnLookupComplete(playerid);
    SetPVarInt( playerid, "Is_AFK", 1 );

    new gunid = GetPlayerWeapon(playerid),
        str[80],
        wepname[ 24 ],
        Float:X2,
        Float:Y2,
        Float:Z2;
/*
    GetWeaponName( gunid, wepname, sizeof(wepname) );

    if(GetWeaponSlotByID(gunid) == 7)
    {
        format( str, sizeof(str), "Neleistinai gautas ginklas (%s)", wepname);
        //TogglePlayerControllable(playerid, 0);
        //ResetPlayerWeapons( playerid );
        //ClearWeaponsFromPlayerInventory(playerid);
        //BanPlayer( "AC", playerid, str );
        SendClientMessage(playerid, COLOR_RED, str);
        return 1;
    }
    */
    new iCurWeap = GetPlayerWeapon(playerid); // Return the player's current weapon
    if(iCurWeap != GetPVarInt(playerid, "iCurrentWeapon")) // If he changed weapons since the last update
    {
        // Lets call a callback named OnPlayerChangeWeapon
//        OnPlayerChangeWeapon(playerid, GetPVarInt(playerid, "iCurrentWeapon"), iCurWeap);
        SetPVarInt(playerid, "iCurrentWeapon", iCurWeap); // Update the weapon variable
    }
    for(new i = 0; i < MAX_ROADBLOCKS; i++)
    {
        if( !IsValidDynamicObject(RoadBlocks[ i ]) ) continue;
        if( RID[ i ] != 1 ) continue;
        GetDynamicObjectPos(RoadBlocks[ i ], X2, Y2, Z2);
        if( IsPlayerInRangeOfPoint( playerid, 2.0, X2, Y2, Z2 ) )
        {
            new panels, doors, lights, tires;
            new carid = GetPlayerVehicleID( playerid );
            GetVehicleDamageStatus( carid, panels, doors, lights, tires );
            tires = encode_tires( 1, 1, 1, 1 );
            UpdateVehicleDamageStatus( carid, panels, doors, lights, tires );
            break;
        }
    }
    return 1;
}


CMD:togacmsg(playerid, params[])
{
    if(!GetPlayerAdminLevel(playerid))
        return 0;

    if(ShowACTestMsg[ playerid ])
        SendClientMessage(playerid, COLOR_NEWS, "Nuo  ðiol nebematysite Test AC þinuèiø.");
    else 
        SendClientMessage(playerid, COLOR_NEWS, "Vël matysite Test AC þinutes.");
    ShowACTestMsg[ playerid ] = !ShowACTestMsg[ playerid ];
    return 1;
}
/*
 OnPlayerChangeWeapon(playerid, oldweapon, newweapon)
{
    new
        weapons[ 2 ];
        //eile[ 128 ],
        //string[ 140 ];

    //RemovePlayerAttachedObject( playerid, 8 );
    //RemovePlayerAttachedObject( playerid, 9 );

    GetPlayerWeaponData( playerid, GetSlotByID( oldweapon ), weapons[ 0 ], weapons[ 1 ] );

    if( !IsPlayerInAnyVehicle( playerid ) )
    {
        
        if( weapons[ 1 ] < 1)
        {
            format( eile, sizeof( eile ), "DELETE FROM `AC` WHERE `ID` = %d AND `WeaponID` = %d", pInfo[ playerid ][ pMySQLID ], oldweapon );
            mysql_query(DbHandle,  eile, false);
            format(string, sizeof( string ), "%dbone", oldweapon );
            SetPVarFloat ( playerid, string, 0 );
        }
        
        if(IsWeaponHasAmmo(newweapon))
        {
            CheckWeaponCheat(playerid, newweapon, 0);
            
            if(!IsPlayerWeaponInDB(playerid, newweapon))
            {
                if(GetPVarInt(playerid, "PossibleWeaponCheat"))
                {
                    format(string ,sizeof(string),"OnPlayerChangeWeapon. Þaidëjas %s bande panaudoti ginkla neregistruota DB(%d). ANTRAS KARTAS.", GetName(playerid), newweapon);
                    ACTestLog(string);

                    GetWeaponName(newweapon, string, sizeof(string));
                    format(string, sizeof(string), "[AntiCheat testas]Þaidëjas %s GALIMAI(ne 100% tikslu) cheatino ginklà %s.(Ðias þinutes galima iðjungti su /togacmsg)", GetName(playerid), string);
                    foreach(new i : Player) 
                    {
                        if(IsPlayerAdmin(i) || pInfo[ i ][ pAdmin ] && ShowACTestMsg[ i ])
                            SendClientMessage(i, 0xff76a1d3, string);
                    }
                }
                else 
                {
                    format(string ,sizeof(string),"OnPlayerChangeWeapon. Þaidëjas %s bande panaudoti ginkla neregistruota DB(%d)", GetName(playerid), newweapon);
                    ACTestLog(string);
                    SetPVarInt(playerid, "PossibleWeaponCheat", true);
                }
            }
            else 
            {
                if(GetPVarInt(playerid, "PossibleWeaponCheat"))
                    DeletePVar(playerid, "PossibleWeaponCheat");
            }

            // Weapons.p test 
            static DidNewACBan[MAX_PLAYERS] = {false, ...};

            if(!IsPlayerWeaponInMemory(playerid ,newweapon))
            {
                DidNewACBan[ playerid ] = true;
                SendClientMessage(playerid, COLOR_LIGHTRED, "Ech, naujas AC dabar tave bûtø uþblokavæs... ");
                format(string, sizeof(string), "Weapons.p Weapon cheated? Val:%d weaponid:%d", IsPlayerWeaponInMemory(playerid, newweapon), newweapon);
                ACTestLog(string);
            }
            else
                DidNewACBan[ playerid ] = false;
            // End of weapons.p test

            new val = CheckWeaponCheat( playerid, newweapon, 0 );
            if(val) // it banned someone :(
            {
                if(!DidNewACBan[ playerid ])
                {
                    SendClientMessage(playerid, COLOR_PURPLE, "Ha, uþbanino tave. Naujas AC to nebûtø padaræs :P");
                    DidNewACBan[ playerid ] =false;
                }
                GetPlayerName(playerid, string ,sizeof(string));
                format(string, sizeof(string), "Þaidëjas %s buvo uþblokuotas uþ ginklo %d cheat. Params: oldweapon:%d newweapon:%d. Old wep data: ID:%d Ammo:%d", 
                    string, newweapon, oldweapon, newweapon, weapons[ 0 ], weapons[ 1 ]);
                ACTestLog(string);
            }
            
        }


        // Jei senasis ginklas buvo uþdëtas kaip objektas, reikia já uþdëti.
        if(oldweapon)
        {
            for(new i = 0; i < MAX_PLAYER_ATTACHED_WEAPONS; i++)
            {
                if(PlayerAttachedWeapons[ playerid ][ i ][ WeaponId ] == oldweapon)
                    SetPlayerAttachedObject(playerid, PlayerAttachedWeapons[ playerid ][ i ][ ObjectSlot ], GunObjects[ oldweapon ], 1,  0.199999, -0.139999, 0.030000, 0.500007, -115.000000, 0.000000, 1.000000, 1.000000, 1.000000);
            }
        }
        // Jei naujasis ginklas uþdëtas kaip objektas, paslepiam objektà nes iðsitraukë realø ginklà.
        if(newweapon)
        {
            for(new i = 0; i < MAX_PLAYER_ATTACHED_WEAPONS; i++)
            {
                if(PlayerAttachedWeapons[ playerid ][ i ][ WeaponId ] == newweapon)
                    RemovePlayerAttachedObject(playerid, PlayerAttachedWeapons[ playerid ][ i ] [ ObjectSlot ]);
            }
        }

    }
    return true;
}*/


 IsPlayerSpectatingPlayer(playerid, spectatee)
{
    if(PlayerSpectatedPlayer[ playerid ] == spectatee)
        return true;
    else 
        return false;
}
 IsPlayerHaveManyGuns( playerid, wepid )
{
    new
        count = 0;

    for ( new i = 0; i < 13; i++ )
    {
        new data[ 2 ];
        GetPlayerWeaponData( playerid, i, data[ 0 ], data[ 1 ] );
        if( wepid == data[ 0 ] ) return false;
        if( GetSlotByID( data[ 0 ] ) == 2 || GetSlotByID( data[ 0 ] ) == 4 || GetSlotByID( data[ 0 ] ) == 5 || GetSlotByID( data[ 0 ] ) == 6 )
            count++;
    }

    if(count >= MAX_SAVED_WEAPONS)
        return true;

    return false;
}

public OnVehicleStreamIn(vehicleid, forplayerid)
{
    SetVehicleParamsForPlayer(vehicleid,forplayerid,0,cInfo[vehicleid][cLock]);
    return 1;
}

public OnVehicleStreamOut(vehicleid, forplayerid)
{
    return 1;
}

public OnPlayerModelSelection(playerid, response, listid, modelid)
{
    if ( listid == skinlist )
    {
        if ( response )
        {
            SetPlayerSkin   ( playerid, modelid );
        }
    }
    return 1;
}



public OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
    printf("[debug] OnDialogResponse(%s, %d, %d, %d, %s)", GetName(playerid), dialogid, response, listitem, inputtext);
    new string[4096];
    if(dialogid == 6)
    {
        if(response == 1)
        {
            switch(listitem)
            {
                case 0:
                {
                    if(GetPlayerMoney(playerid) < 150)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai grynøjø pinigø su savimi");
                    GivePlayerWeapon(playerid,1,1);
                    GivePlayerMoney(playerid,-150);
                    SendClientMessage(playerid,COLOR_WHITE," ** Sëkmingai nusipirkote kastetà, kuris Jums kainavo 150$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 1:
                {
                    if(GetPlayerMoney(playerid) < 498)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai grynøjø pinigø su savimi");
                    GivePlayerWeapon(playerid,2,1);
                    GivePlayerMoney(playerid,-498);
                    SendClientMessage(playerid,COLOR_WHITE," ** Sëkmingai nusipirkote golfo lazdà, kuri Jums kainavo 498$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 2:
                {
                    if(GetPlayerMoney(playerid) < 89)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai grynøjø pinigø su savimi");
                    GivePlayerWeapon(playerid,4,1);
                    GivePlayerMoney(playerid,-89);
                    SendClientMessage(playerid,COLOR_WHITE," ** Sëkmingai nusipirkote kastetà, kuris Jums kainavo 89$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 3:
                {
                    if(GetPlayerMoney(playerid) < 91)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai grynøjø pinigø su savimi");
                    GivePlayerWeapon(playerid,5,1);
                    GivePlayerMoney(playerid,-91);
                    SendClientMessage(playerid,COLOR_WHITE," ** Sëkmingai nusipirkote beisbolo lazdà, kuris Jums kainavo 91$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 4:
                {
                    if(GetPlayerMoney(playerid) < 75)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai grynøjø pinigø su savimi");
                    GivePlayerWeapon(playerid,6,1);
                    GivePlayerMoney(playerid,-75);
                    SendClientMessage(playerid,COLOR_WHITE," ** Sëkmingai nusipirkote kastuvà, kuris Jums kainavo 75$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 5:
                {
                    if(GetPlayerMoney(playerid) < 344)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai grynøjø pinigø su savimi");
                    GivePlayerWeapon(playerid,7,1);
                    GivePlayerMoney(playerid,-344);
                    SendClientMessage(playerid,COLOR_WHITE," ** Sëkmingai nusipirkote kastuvà, kuris Jums kainavo 75$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 6:
                {
                    if(GetPlayerMoney(playerid) < 43)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai grynøjø pinigø su savimi");
                    GivePlayerWeapon(playerid,15,1);
                    GivePlayerMoney(playerid,-43);
                    SendClientMessage(playerid,COLOR_WHITE," ** Sëkmingai nusipirkote lazdà, kuris Jums kainavo 75$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 7:
                {
                    if(GetPlayerMoney(playerid) < 110)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai grynøjø pinigø su savimi");
                    GivePlayerWeapon(playerid,41,80);
                    GivePlayerMoney(playerid,-110);
                    SendClientMessage(playerid,COLOR_WHITE," ** Sëkmingai nusipirkote daþø balionëlá, kuris Jums kainavo 110$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 8:
                {
                    if(GetPlayerMoney(playerid) < 720)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai grynøjø pinigø su savimi");
                    GivePlayerWeapon(playerid,8,1);
                    GivePlayerMoney(playerid,-720);
                    SendClientMessage(playerid,COLOR_WHITE," ** Sëkmingai nusipirkote katanà, kuri Jums kainavo 720$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }				
            }
        }
    }
    else if(dialogid == 7)
    {
        if(response == 1)
        {
            switch(listitem)
            {
                case 0:
                {
                    if(GetPlayerMoney(playerid) < 300)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai grynøjø pinigø su savimi");
                    GivePlayerWeapon(playerid,10,1);
                    GivePlayerMoney(playerid,-300);
                    SendClientMessage(playerid,COLOR_NEWS," Uþ roþiná vibratoriø sumokëjote: 300$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 1:
                {
                    if(GetPlayerMoney(playerid) < 300)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai grynøjø pinigø su savimi");
                    GivePlayerWeapon(playerid,11,1);
                    GivePlayerMoney(playerid,-250);
                    SendClientMessage(playerid,COLOR_NEWS," Uþ maþa baltá  vibratoriø sumokëjote: 250$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 2:
                {
                    if(GetPlayerMoney(playerid) < 300)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai grynøjø pinigø su savimi");
                    GivePlayerWeapon(playerid,12,1);
                    GivePlayerMoney(playerid,-330);
                    SendClientMessage(playerid,COLOR_NEWS," Uþ didelá baltá  vibratoriø sumokëjote: 330$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 3:
                {
                    if(GetPlayerMoney(playerid) < 260)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai grynøjø pinigø su savimi");
                    GivePlayerWeapon(playerid,13,1);
                    GivePlayerMoney(playerid,-260);
                    SendClientMessage(playerid,COLOR_NEWS," Uþ blizgantá vibratoriø sumokëjote: 260$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
            }
        }
    }
    else if(dialogid == 8)
    {
        if(response == 1)
        {
            new pay[ 128 ];
            format      ( pay, sizeof(pay),"============= BAUDOS LAPELIS =============\n\
                                    \nPareigûnas: %s\n\
                                    Bauda grynais: $%d\n\
                                    \nGrynieji pinigai: $%d\n\
                                    Jûsø banko sá skaita: $%d\n\
                                    \n========================================\n\
                                    Kokiu bûdu apmokësite baudá ?",
            GetName( Offer[ playerid ][ 3 ]),GetPVarInt( playerid, "MOKESTIS" ), GetPlayerMoney(playerid), GetPlayerBankMoney(playerid));
            ShowPlayerDialog(playerid,98,DIALOG_STYLE_MSGBOX,"BAUDOS APMOKËJIMAS",pay,"Grynais","Banku");
            return 1;
        }
        else if(response == 0)
        {

            SendClientMessage(Offer[playerid][3], COLOR_FADE2, "{FF6347}Perspëjimas: þmogus atsisakë susimokëti baudos lapelá.");
            DeletePVar( playerid, "MOKESTIS" );
            Offer[playerid][3] = 255;
            return 1;
        }
    }
    else if(dialogid == 9)
    {
        if(response == 1)
        {
            if ( GetPlayerMoney(playerid) < 3000 && listitem < 17)
                return SendClientMessage(playerid, COLOR_FADE2, "{FF6347}Perspëjimas: Neturite pakankamai pinigø ($3000)");
            switch(listitem)
            {
                case 0: SetPVarInt( playerid, "MOD", 1025 );
                case 1: SetPVarInt( playerid, "MOD", 1074 );
                case 2: SetPVarInt( playerid, "MOD", 1076 );
                case 3: SetPVarInt( playerid, "MOD", 1078 );
                case 4: SetPVarInt( playerid, "MOD", 1081 );
                case 5: SetPVarInt( playerid, "MOD", 1082 );
                case 6: SetPVarInt( playerid, "MOD", 1085 );
                case 7: SetPVarInt( playerid, "MOD", 1096 );
                case 8: SetPVarInt( playerid, "MOD", 1097 );
                case 9: SetPVarInt( playerid, "MOD", 1098 );
                case 10: SetPVarInt( playerid, "MOD", 1084 );
                case 11: SetPVarInt( playerid, "MOD", 1073 );
                case 12: SetPVarInt( playerid, "MOD", 1075 );
                case 13: SetPVarInt( playerid, "MOD", 1077 );
                case 14: SetPVarInt( playerid, "MOD", 1079 );
                case 15: SetPVarInt( playerid, "MOD", 1080 );
                case 16: SetPVarInt( playerid, "MOD", 1083 );
                case 17:
                {
                    if ( GetPlayerMoney(playerid) < 5000 ) return SendClientMessage(playerid, COLOR_FADE2, "{FF6347}Perspëjimas: Neturite pakankamai pinigø ($5000)");
                    SetPVarInt( playerid, "MOD2", 1087 );
                }
                case 18: SetPVarInt( playerid, "MOD2", -1 );
                case 19: SetPVarInt( playerid, "MOD", -1 );
                case 20: SetPVarInt( playerid, "MOD", -2 );
            }
            StartTimer(playerid,180,3);
        }
    }
    else if ( dialogid == 16 )
    {
        if ( response == 1 )
        {
            new skin = strval( inputtext );
            if ( skin < 1 || skin > 299 || skin == 6 || skin == 7 || skin == 8 || skin == 149 || skin == 86) return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: neteisingai nurodytas iðvaizdos ID.");
            else
            {
                SetPlayerSkin( playerid, skin );
                return 1;
            }
        }
    }
    else if ( dialogid == 19 ) // amenu
    {
        if ( response == 1 )
        {
            switch( listitem )
            {
                case 0:
                {
             //       HouseManagementDialog.ShowMain(playerid);
                    return 1;
                }
                case 1:
                {
            //        BusinessManagementDialog.ShowMain(playerid);
                    return 1;
                }
                case 2:
                {
                    ShowPlayerDialog( playerid, 29, DIALOG_STYLE_LIST,"Frakcijø meniu","- Kurti naujà \n\
                                                                                        - Iðtrinti\n\
                                                                                        - Tvarkyti frakcijas", "Rinktis", "Atðaukti" );
                    return 1;
                }
                case 3:
                {
                   /* ShowPlayerDialog( playerid, 45, DIALOG_STYLE_LIST,"Serverio automobiliai","- Kurti naujà \n\
                                                                                               - Priskirti automobilá frakcijai \n\
                                                                                               - Priskirti automobilá darbui \n\
																							   - Pakeisti atsiradimo vietà \n\
                                                                                               - (Faction) Keisti reikalaujama rangà \n\
                                                                                               - (Faction) Keisti  automobilio spalvà \n\
                                                                                               - Paðalinti tr. priemonæ \n\
                                                                                               - Patikrinti bagaþinæ", "Rinktis", "Atðaukti" );
*/
                    ShowJobVehManagementDialog(playerid);
                    return 1;
                }
                case 4:
                {
//                    EntranceManagementDialog.ShowMain(playerid);
                    return 1;
                }
                case 5:
                {
                    ShowPlayerDialog( playerid, 58, DIALOG_STYLE_INPUT,"Serverio balsavimas", "Iraðykite klausima, á kuri butu galima atsakyti taip arba ne",  "Rinktis", "Atðaukti" );
                    return 1;
                }
                
                case 6:
                {
                    new bigstring[ 1024 ] = "Kurti naujà\n{FFFFFF}";
                    foreach(VehicleShopIterator, i)
                        format(bigstring, sizeof(bigstring),"%s%d. %s\n",bigstring, i, VehicleShops[ i ][ Name ]);
                    ShowPlayerDialog( playerid, DIALOG_VEHICLE_SHOPS_LIST, DIALOG_STYLE_LIST,"Turgûs", bigstring, "Pirkti", "Atðaukti" );
                }
                
                case 7:
                {
//                    GarageManagementDialog.ShowMain(playerid);
                }
                case 8:
                    return TruckerJob:ShowPlayerDialog(playerid, ActionList);
                //case 9: 
                    //return InteriorManagementDialog.ShowMain(playerid);
                //case 10: 
                    //return CoordinateManagementDialog.ShowMain(playerid);
                //case 11:
                    //return GraffitiManagementDialog.ShowMain(playerid);
                case 12:
                    return BugReportManagementDialog.ShowMain(playerid);
            }
        }
    }
    else if ( dialogid == 159 )
    {
        if( !response )
            return true;
        switch( listitem )
        {
            case 0:
            {
                if( !GetPVarInt( playerid, "oKepure" ) )
                {
                    SetPlayerAttachedObject(playerid, 0, 19330, 2, 0.132999,-0.011999,0.004000,-177.199996,-2.200002,30.799991);
                    EditAttachedObject(playerid, 0);
                    SendClientMessage(playerid, 0xFFFFFFFF, "Norëdami pasukti/pakeisti kamerà laikykite klaviðus: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
                    SetPVarInt( playerid, "oKepure", true );
                }
                else
                {
                    SetPVarInt( playerid, "oKepure", false );
                    SendClientMessage( playerid, COLOR_WHITE,"Daiktas buvo sëkmingai panaikintas/nuimtas.");
                    RemovePlayerAttachedObject( playerid, 0 );
                }
            }
            case 1:
            {
                if( !GetPVarInt( playerid, "oKepure" ) )
                {
                    SetPlayerAttachedObject(playerid, 0, 19331, 2, 0.132999,-0.011999,0.004000,-177.199996,-2.200002,30.799991);
                    EditAttachedObject(playerid, 0);
                    SendClientMessage(playerid, 0xFFFFFFFF, "Norëdami pasukti/pakeisti kamerà laikykite klaviðus: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
                    SetPVarInt( playerid, "oKepure", true );
                }
                else
                {
                    SetPVarInt( playerid, "oKepure", false );
                    SendClientMessage( playerid, COLOR_WHITE,"Daiktas buvo sëkmingai panaikintas/nuimtas.");
                    RemovePlayerAttachedObject( playerid, 0 );
                }
            }
            case 2:
            {
                if( !GetPVarInt( playerid, "oKepure" ) )
                {
                    SetPlayerAttachedObject(playerid, 0, 19472, 2, 0.132999,-0.011999,0.004000,-177.199996,-2.200002,30.799991);
                    EditAttachedObject(playerid, 0);
                    SendClientMessage(playerid, 0xFFFFFFFF, "Norëdami pasukti/pakeisti kamerà laikykite klaviðus: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
                    SetPVarInt( playerid, "oKepure", true );
                }
                else
                {
                    SetPVarInt( playerid, "oKepure", false );
                    SendClientMessage( playerid, COLOR_WHITE,"Daiktas buvo sëkmingai panaikintas/nuimtas.");
                    RemovePlayerAttachedObject( playerid, 0 );
                }
            }
        }
    }

    else if ( dialogid == 58 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;
            format( string, 256, "AdmCmd Administratorius %s paskelbë serveryje balsavimà þaidëjams. ", GetName( playerid ) );
            SendClientMessageToAll( COLOR_LIGHTRED, string );
            format( string, 256, "AdmCmd Administratoriaus klausimas: %s", inputtext );
            SendClientMessageToAll( COLOR_LIGHTRED2, string );
            SendClientMessageToAll( COLOR_WHITE, "Á uþduotà Administratoriaus klausimà galite atsakyti komandomis /taip arba /ne." );
            SetTimerEx( "Vote", 60000, 0, "s", inputtext );
            foreach(Player,id)
                Voted[ id ] = false;
            return 1;
        }
    }    
    else if ( dialogid == 91 )
    {
        if ( response == 1 )
        {
            switch( listitem )
            {
                case 0:
                {
                    new nextexp = ( GetPlayerLevel(playerid) + 1 ) * 4;
                    format( string, 349, "{FFFFFF}-Veikëjo OOC informacija\n\
                                            \t- Lygis: \t\t\t%d\n\
                                            \t- Patirtis: \t\t%d/%d\n\
                                            \t- Praþaista valandø: \t%d\n\
                                            \t- áspëjimai: \t\t%d\n\
                                            \t- Mirtys: \t\t%d\n\
                                            \t- Darbo lygis: \t\t%d\n\
                                            \t- Darbo patirtis: \t%d/%d\n\
                                          -Veikëjo IC informacija\n\
                                          -Frakcijos ir darbo informacija",
                                          GetPlayerLevel(playerid),
                                          pInfo[ playerid ][ pExp    ], nextexp,
                                          GetPlayerConnectedTime(playerid),
                                          pInfo[ playerid ][ pWarn   ],
                                          GetPlayerDeaths(playerid),
                                          pInfo[ playerid ][ pJobLevel ],
                                          pInfo[ playerid ][ pJobSkill ],
                                      ( ( pInfo[ playerid ][ pJobLevel ] +1 ) * 100) );
                    ShowPlayerDialog( playerid, 92, DIALOG_STYLE_LIST, GetName( playerid ), string, "Rinktis", "Iðjungti");
                    return 1;
                }
                case 1:
                {
                    format( string, 349, "{FFFFFF}-Veikëjo OOC informacija\n\
                                                  -Veikëjo IC informacija\n\
                                            \t- Pinigai: \t\t%d\n\
                                                    \t- Pinigai banke: \t%d\n\
                                                    \t- Pinigai indëlyje: \t%d\n\
                                                    \t- Metai: \t\t%d\n\
                                                    \t- Lytis: \t\t\t%s\n\
                                                    \t- Tautybë: \t\t%s\n\
                                                    \t- Liga: \t\t\t%s\n\
                                                    \t- Telefono nr.: \t\t%d\n\
                                                  - Frakcijos ir darbo informacija",
                                                    GetPlayerMoney(playerid),
                                                    GetPlayerBankMoney(playerid),
                                                    pInfo[ playerid ][ pSavings ],
                                                    pInfo[ playerid ][ pAge     ],
                                                    pInfo[ playerid ][ pSex     ],
                                                    pInfo[ playerid ][ pOrigin  ],
                                                    Ligos[ pInfo[ playerid ][ pLiga ] ],
                                                    pInfo[ playerid ][ pPhone   ]);
                    ShowPlayerDialog( playerid, 93, DIALOG_STYLE_LIST, GetName( playerid ), string, "Rinktis", "Iðjungti");
                    return 1;
                }
                case 2:
                {
                    format( string, 349, "{FFFFFF}-Veikëjo OOC informacija\n\
                                                  -Veikëjo IC informacija\n\
                                                  -Frakcijos ir darbo informacija\n\
                                                    \t\t- Pavadinimas: \t%s\n\
                                                    \t- Darbo informacija\n\
                                                    \t\t- Pavadinimas: \t%s\n\
                                                    \t\t- Kontraktas: \t%d",
                                                    pInfo[ playerid ][ pMember ],
                                                 //   fInfo[ PlayerFaction( playerid ) ][ fName ],
                                                  //  GetPlayerRangName( playerid ),
                                                    GetJobName( pInfo[ playerid ][ pJob ] ),
                                                    pInfo[ playerid ][ pJobContr ] );
                    ShowPlayerDialog( playerid, 91, DIALOG_STYLE_LIST, GetName( playerid ), string, "Rinktis", "Iðjungti");
                    return 1;
                }
            }
        }
    }
    else if ( dialogid == 92 )
    {
        if ( response == 1 )
        {
            switch( listitem )
            {
                case 0:
                {
                    new nextexp = ( GetPlayerLevel(playerid) + 1 ) * 4;
                    format( string, 349, "{FFFFFF}-Veikëjo OOC informacija\n\
                                            \t- Lygis: \t\t\t%d\n\
                                            \t- Patirtis: \t\t%d/%d\n\
                                            \t- Praþaista valandø: \t%d\n\
                                            \t- áspëjimai: \t\t%d\n\
                                            \t- Mirtys: \t\t%d\n\
                                            \t- Darbo lygis: \t\t%d\n\
                                            \t- Darbo patirtis: \t%d/%d\n\
                                          -Veikëjo IC informacija\n\
                                          -Frakcijos ir darbo informacija",
                                          GetPlayerLevel(playerid),
                                          pInfo[ playerid ][ pExp    ], nextexp,
                                          GetPlayerConnectedTime(playerid),
                                          pInfo[ playerid ][ pWarn   ],
                                          GetPlayerDeaths(playerid),
                                          pInfo[ playerid ][ pJobLevel ],
                                          pInfo[ playerid ][ pJobSkill ],
                                      ( ( pInfo[ playerid ][ pJobLevel ] +1 ) * 100) );
                    ShowPlayerDialog( playerid, 92, DIALOG_STYLE_LIST, GetName( playerid ), string, "Rinktis", "Iðjungti");
                    return 1;
                }
                case 8:
                {
                    format( string, 349, "{FFFFFF}-Veikëjo OOC informacija\n\
                                                  -Veikëjo IC informacija\n\
                                                    \t- Pinigai: \t\t%d\n\
                                                    \t- Pinigai banke: \t%d\n\
                                                    \t- Pinigai indëlyje: \t%d\n\
                                                    \t- Metai: \t\t%d\n\
                                                    \t- Lytis: \t\t\t%s\n\
                                                    \t- Tautybë: \t\t%s\n\
                                                    \t- Liga: \t\t\t%s\n\
                                                    \t- Telefono nr.: \t\t%d\n\
                                                   -Frakcijos ir darbo informacija",
                                                    GetPlayerMoney(playerid),
                                                    GetPlayerBankMoney(playerid),
                                                    pInfo[ playerid ][ pSavings ],
                                                    pInfo[ playerid ][ pAge     ],
                                                    pInfo[ playerid ][ pSex     ],
                                                    pInfo[ playerid ][ pOrigin  ],
                                                    Ligos[ pInfo[ playerid ][ pLiga ] ],
                                                    pInfo[ playerid ][ pPhone   ]);
                    ShowPlayerDialog( playerid, 93, DIALOG_STYLE_LIST, GetName( playerid ), string, "Rinktis", "Iðjungti");
                    return 1;
                }
                case 9:
                {
                    format( string, 349, "{FFFFFF}-Veikëjo OOC informacija\n\
                                                  -Veikëjo IC informacija\n\
                                                  -Frakcijos ir darbo informacija\n\
                                                    \t\t- Pavadinimas: \t%s\n\
                                                    \t- Darbo informacija\n\
                                                    \t\t- Pavadinimas: \t%s\n\
                                                    \t\t- Kontraktas: \t%d",
                                                    pInfo[ playerid ][ pMember ],
                                                   // fInfo[ PlayerFaction( playerid ) ][ fName ],
                                                    GetJobName( pInfo[ playerid ][ pJob ] ),
                                                    pInfo[ playerid ][ pJobContr ] );
                    ShowPlayerDialog( playerid, 91, DIALOG_STYLE_LIST, GetName( playerid ), string, "Rinktis", "Iðjungti");
                    return 1;
                }
            }
        }
    }
    else if ( dialogid == 93 )
    {
        if ( response == 1 )
        {
            switch( listitem )
            {
                case 0:
                {
                    new nextexp = ( GetPlayerLevel(playerid) + 1 ) * 4;
                    format( string, 349, "{FFFFFF}-Veikëjo OOC informacija\n\
                                            \t- Lygis: \t\t\t%d\n\
                                            \t- Patirtis: \t\t%d/%d\n\
                                            \t- Praþaista valandø: \t%d\n\
                                            \t- áspëjimai: \t\t%d\n\
                                            \t- Mirtys: \t\t%d\n\
                                            \t- Darbo lygis: \t\t%d\n\
                                            \t- Darbo patirtis: \t%d/%d\n\
                                          -Veikëjo IC informacija\n\
                                          -Frakcijos ir darbo informacija",
                                          GetPlayerLevel(playerid),
                                          pInfo[ playerid ][ pExp    ], nextexp,
                                          GetPlayerConnectedTime(playerid),
                                          pInfo[ playerid ][ pWarn   ],
                                          GetPlayerDeaths(playerid),
                                          pInfo[ playerid ][ pJobLevel ],
                                          pInfo[ playerid ][ pJobSkill ],
                                      ( ( pInfo[ playerid ][ pJobLevel ] +1 ) * 100) );
                    ShowPlayerDialog( playerid, 92, DIALOG_STYLE_LIST, GetName( playerid ), string, "Rinktis", "Iðjungti");
                    return 1;
                }
                case 1:
                {
                    format( string, 349, "{FFFFFF}-Veikëjo OOC informacija\n\
                                                  -Veikëjo IC informacija\n\
                                                    \t- Pinigai: \t\t%d\n\
                                                    \t- Pinigai banke: \t%d\n\
                                                    \t- Pinigai indëlyje: \t%d\n\
                                                    \t- Metai: \t\t%d\n\
                                                    \t- Lytis: \t\t\t%s\n\
                                                    \t- Tautybë: \t\t%s\n\
                                                    \t- Liga: \t\t\t%s\n\
                                                    \t- Telefono nr.: \t\t%d\n\
                                                   -Frakcijos ir darbo informacija",
                                                    GetPlayerMoney(playerid),
                                                    GetPlayerBankMoney(playerid),
                                                    pInfo[ playerid ][ pSavings ],
                                                    pInfo[ playerid ][ pAge     ],
                                                    pInfo[ playerid ][ pSex     ],
                                                    pInfo[ playerid ][ pOrigin  ],
                                                    Ligos[ pInfo[ playerid ][ pLiga ] ],
                                                    pInfo[ playerid ][ pPhone   ]);
                    ShowPlayerDialog( playerid, 93, DIALOG_STYLE_LIST, GetName( playerid ), string, "Rinktis", "Iðjungti");
                    return 1;
                }
                case 10:
                {
                    format( string, 349, "{FFFFFF}-Veikëjo OOC informacija\n\
                                                  -Veikëjo IC informacija\n\
                                                  -Frakcijos ir darbo informacija\n\
                                                    \t\t- Pavadinimas: \t%s\n\
                                                    \t- Darbo informacija\n\
                                                    \t\t- Pavadinimas: \t%s\n\
                                                    \t\t- Kontraktas: \t%d",
                                                    pInfo[ playerid ][ pMember ],
                                                  //  fInfo[ PlayerFaction( playerid ) ][ fName ],
                                             //       GetPlayerRangName( playerid ),
                                                    GetJobName( pInfo[ playerid ][ pJob ] ),
                                                    pInfo[ playerid ][ pJobContr ] );
                    ShowPlayerDialog( playerid, 91, DIALOG_STYLE_LIST, GetName( playerid ), string, "Rinktis", "Iðjungti");
                    return 1;
                }
            }
        }
    }
    else if(dialogid == 98)
    {
        if(response == 1)
        {
            if(GetPlayerMoney(playerid) < GetPVarInt( playerid, "MOKESTIS" ) )
            {
                SendClientMessage(playerid, COLOR_FADE2, "{FF6347}Perspëjimas: Neturite pakankamai pinigø, kad sumoketumëte baudá .");
                DeletePVar( playerid, "MOKESTIS" );
                SendClientMessage(Offer[playerid][3], COLOR_FADE2, "{FF6347}Perspëjimas: þaidëjas neturi pakankamai pinigø, kad sumokëtu baudá .");
                return 1;
            }
            GivePlayerMoney(playerid,-GetPVarInt( playerid, "MOKESTIS" ));
            SendClientMessage(playerid,COLOR_WHITE,"Bauda sumokëta");
            SendClientMessage(Offer[playerid][3],COLOR_WHITE,"Jis sumokëjo baudá .");
            pInfo[ playerid ][ pPaydFines ] += pInfo[ playerid ][ pFines ];
            pInfo[ playerid ][ pFines ] = 0;
            DeletePVar( playerid, "MOKESTIS" );
            Offer[playerid][3] = 255;
            return 1;
        }
        else if(response == 0)
        {
            if(GetPlayerBankMoney(playerid) < GetPVarInt( playerid, "MOKESTIS" ))
            {
                SendClientMessage(playerid, COLOR_FADE2, "{FF6347}Perspëjimas: Neturite pakankamai pinigø, kad sumoketumëte baudá .");
                DeletePVar( playerid, "MOKESTIS" );
                SendClientMessage(Offer[playerid][3], COLOR_FADE2, "{FF6347}Perspëjimas: þaidëjas neturi pakankamai pinigu, kad sumokëtu baudá .");
                return 1;
            }
            AddPlayerBankMoney(playerid, - GetPVarInt( playerid, "MOKESTIS" ));
            SendClientMessage(playerid,COLOR_WHITE,"Bauda sumokëta");
            SendClientMessage(Offer[playerid][3],COLOR_WHITE,"Jis sumokëjo baudá .");
            pInfo[ playerid ][ pPaydFines ] += pInfo[ playerid ][ pFines ];
            pInfo[ playerid ][ pFines ] = 0;
            DeletePVar( playerid, "MOKESTIS" );
            Offer[playerid][3] = 255;
            return 1;
        }
    }
    else if(dialogid == 101) // Lifto
    {
        if(!response)
            return 0;

        if(FloorRequestedBy[listitem] != INVALID_PLAYER_ID || IsFloorInQueue(listitem))
            GameTextForPlayer(playerid, "~r~Aukstas jau yra uzsakytas.", 3500, 4);
        else if(DidPlayerRequestElevator(playerid))
            GameTextForPlayer(playerid, "~r~Jus jau kvietete lifta.", 3500, 4);
        else
            CallElevator(playerid, listitem);

        return 1;
    }
    else if ( dialogid == 114 )
    {
                if(!response)
                    return 1;

                tmpinteger[ playerid ] = listitem;

                switch( listitem )
                {
                    case 0:
                    {
                        ShowPlayerDialog( playerid, 150, DIALOG_STYLE_MSGBOX, "Gaminimo informacija",
                                "PAGAMINIMO KAINA: $1000 uþ 50vnt\n\
                                PAGAMINIMO KIEKIS: 50vnt\n\
                                PAGAMINIMO TRUKMË: 2 payday", "GAMINTI", "ATáAUKTI" );
                    }
                    case 1:
                    {
                        ShowPlayerDialog( playerid, 150, DIALOG_STYLE_MSGBOX, "Gaminimo informacija",
                                "PAGAMINIMO KAINA: $1500 uþ 100vnt\n\
                                PAGAMINIMO KIEKIS: 100vnt\n\
                                PAGAMINIMO TRUKMË: 2 payday", "GAMINTI", "ATáAUKTI" );
                    }
                    case 2:
                    {
                        ShowPlayerDialog( playerid, 150, DIALOG_STYLE_MSGBOX, "Gaminimo informacija",
                                "PAGAMINIMO KAINA: $1000 uþ 25vnt\n\
                                PAGAMINIMO KIEKIS: 25vnt\n\
                                PAGAMINIMO TRUKMË: 4 payday", "GAMINTI", "ATáAUKTI" );
                    }
                    case 3:
                    {
                        ShowPlayerDialog( playerid, 150, DIALOG_STYLE_MSGBOX, "Gaminimo informacija",
                                "PAGAMINIMO KAINA: $1500 uþ 50vnt\n\
                                PAGAMINIMO KIEKIS: 50vnt\n\
                                PAGAMINIMO TRUKMË: 3 payday", "GAMINTI", "ATáAUKTI" );
                    }
                }
    }
    else if ( dialogid == 160 )
    {
                if(!response)
                    return 1;

                switch( listitem )
                {
                    case 0:
                    {
                        tmpinteger[ playerid ] = 4;
                        ShowPlayerDialog( playerid, 150, DIALOG_STYLE_MSGBOX, "Gaminimo informacija",
                                "PAGAMINIMO KAINA: $1200 uþ 30vnt\n\
                                PAGAMINIMO KIEKIS: 30vnt\n\
                                PAGAMINIMO TRUKMË: 3 payday", "GAMINTI", "ATáAUKTI" );
                    }
                    case 1:
                    {
                        tmpinteger[ playerid ] = 5;
                        ShowPlayerDialog( playerid, 150, DIALOG_STYLE_MSGBOX, "Gaminimo informacija",
                                "PAGAMINIMO KAINA: $1300 uþ 50vnt\n\
                                PAGAMINIMO KIEKIS: 50vnt\n\
                                PAGAMINIMO TRUKMË: 3 payday", "GAMINTI", "ATáAUKTI" );
                    }
                    case 2:
                    {
                        tmpinteger[ playerid ] = 6;
                        ShowPlayerDialog( playerid, 150, DIALOG_STYLE_MSGBOX, "Gaminimo informacija",
                                "PAGAMINIMO KAINA: $1000 uþ 100vnt\n\
                                PAGAMINIMO KIEKIS: 100vnt\n\
                                PAGAMINIMO TRUKMË: 3 payday", "GAMINTI", "ATáAUKTI" );
                    }
                    case 3:
                    {
                        tmpinteger[ playerid ] = 7;
                        ShowPlayerDialog( playerid, 150, DIALOG_STYLE_MSGBOX, "Gaminimo informacija",
                                "PAGAMINIMO KAINA: $1200 uþ 30vnt\n\
                                PAGAMINIMO KIEKIS: 30vnt\n\
                                PAGAMINIMO TRUKMË: 3 payday", "GAMINTI", "ATáAUKTI" );
                    }
                }
    }
	if(dialogid == DIALOG_TPDA_MAIN)
	{
		if(!response) 
			return 1;
			
		new str[ 2048 ];	
		switch(listitem)
		{
			case 0:
			{
				foreach(IndustryIterator, i)
					format(str, sizeof(str), "%s{FFFFFF}%s{C0C0C0}(%s, %s)\n", 
                        str, 
                        Industries[ i ][ Name ], 
                        GetIndustrySectorName(i),
                        (Industries[ i ][ IsBuyingCargo ]) ? ("Atidaryta") : ("Uþdaryta"));
				ShowPlayerDialog(playerid,DIALOG_TPDA_INDUSTRY, DIALOG_STYLE_LIST,"TPDA",str,"Tæsti","Atgal");
			}
			case 1:
			{
				new count = 0, index;
                foreach(CommodityIterator, i)
                {
                    if(!Commodities[ i ][ IsBusinessCommodity ])
                        continue;

                    /*index = GetBusinessIndex(Commodities[ i ][ IndustryId ]);
                    if(index == -1)
                        ErrorLog("Invalid commodity business ID. Id of that business:%d", Commodities[ i ][ IndustryId ]);
                    else 
                    {
                        format(str, sizeof(str), "%s%d. %s\n", str, GetBusinessID(index), GetBusinessName(index));
                        count++;
                    }*/
                }

				if(!count)
					ShowPlayerDialog(playerid, 9999, DIALOG_STYLE_MSGBOX, "TPDA", "Ðiuo metu nëra verlslø perkanèiø prekes!", "Gerai", "");
				else 
					ShowPlayerDialog(playerid, DIALOG_TPDA_BUSINESS, DIALOG_STYLE_LIST, "TPDA", str, "Tæsti", "Atgal");
			}
			case 2:
			{
				if(ShipInfo[ Status ] == Docked)
				{
                    new secs;
                    // Jei dar nebuvo iðplaukæs..
                    if(!ShipInfo[ LastArrivalTimestamp ])
                        secs = CARGOSHIP_DOCKED_INTERVAL / 1000 - (gettime() - ServerStartTimestamp);
                    else 
                        secs = gettime() - ShipInfo[ LastArrivalTimestamp ] + CARGOSHIP_DOCKED_INTERVAL;
                    format(str,sizeof(str),"Statusas: laivas uoste\nLaivas iðplauks uþ %2d minuèiø %2d sekundþiø", secs / 60, secs % 60); 
                }
                else if(ShipInfo[ Status ] == Arriving)
                {
                    strcat(str, "Laivas plaukia atgal á uostà....");
                }
				else
                {
                    new secs =  (CARGOSHIP_MOVING_INTERVAL / 1000) - (gettime() - ShipInfo[ LastDepartureTimestamp ]);
                    // Ðitas kodas taps neámanomas kai timeriai bus tikslûs.
                    if(secs <= 0)
                        str = "Statusas: ðvartuojasi\nNaujai atvykæs laivas pradës priimti krovinius jau netrukus..";
                    else 
                        format(str,sizeof(str),"Statusas: iðplaukæs\nNaujas laivas atplauks uþ %2d minuèiø %2d sekundþiø",secs / 60, secs % 60);
                }


                strcat(str, "\n\n\t\t{FFFFFF}Superkamos prekës\n\n");
                for(new i = 0; i < sizeof TruckerCargo; i ++)
                {
                    if(!TruckerCargo[ i ][ Id ])
                        continue;
                    if(!IsShipAcceptingCargo(TruckerCargo[ i ][ Id ]))
                        continue;

                    new price = GetShipCargoPrice(TruckerCargo[ i ][ Id ]);
                    if(!price)
                        continue;


                    if((i && i % 2 == 0) || (i == sizeof TruckerCargo -1))
                        strcat(str, "\n");
                    format(str, sizeof(str), "%s%s $%d\t\t\t\t",
                        str, 
                        TruckerCargo[ i ][ Name ], price);
                    
                }
				ShowPlayerDialog(playerid, DIALOG_SHIP_INFO, DIALOG_STYLE_MSGBOX, "Laivo informacija", str, "Naviguoti", "Iðeiti");
			}
		}
		return 1;
	}
	if( dialogid == DIALOG_SHIP_INFO)
	{
		if(!response)
			return 1;
		
		SetPlayerCheckPointEx(playerid, CHECKPOINT_SHIP, SHIP_POS_X, SHIP_POS_Y, SHIP_POS_Z, 5.0);
		return 1;
	}
    if (dialogid == DIALOG_TPDA_INDUSTRY)
    {
        if(!response)
            return 1;
			
        new str[2048];

        format(str, sizeof(str), "{FFFFFF}Sveiki atvyke á {00FF66}%s!\n", Industries[ listitem ][ Name ]);
        strcat(str, "{00FF66}Parduodama: \n");
        format(str, sizeof(str), "%s{C8C8C8}Prekë%sYra sandëlyje(limitas)\t\tKaina\tPagaminama per valandà\n",
            str,
            GetSpaceString(8));
        // Parduodamos prekes
        foreach(CommodityIterator, i)
            if(Commodities[ i ][ IndustryId ] == Industries[ listitem ][ Id ] && Commodities[ i ][ SellBuyStatus ] == Selling && !Commodities[ i ][ IsBusinessCommodity ])
                format(str, sizeof(str), "%s{FFFFFF}%s%s%d vienetai {C8C8C8}(%d){FFFFFF}\t\t$%d\t+%d\n",
                    str, 
                    GetCargoName(Commodities[ i ][ CargoId ]), GetSpaceString(MAX_TRUCKER_CARGO_NAME - strlen(GetCargoName(Commodities[ i ][ CargoId ])) + 8),
                    Commodities[ i ][ CurrentStock ],
                    GetCargoLimit(Commodities[ i ][ CargoId ]), 
                    Commodities[ i ][ Price ],
                    GetCargoProduction(Commodities[ i ][ CargoId ])
                );

        strcat(str, "{00FF66}\nPerkama: \n");
        if(!GetIndustryBoughtCommodityCount(listitem)) // Jei nëra prekiø perkamø.
            strcat(str, "{C8C8C8}Ði firma nieko neperka");
        else 
        {
            strcat(str,"{C8C8C8}Prekë                  Yra sandëlyje (limitas)\t\tKaina\tSunaudojama per valandà\n");
            // Ilgas kodo gabalas kad sudët sàraðà perkamø dalykø
            foreach(CommodityIterator, i)
            if(Commodities[ i ][ IndustryId ] == Industries[ listitem ][ Id ] && Commodities[ i ][ SellBuyStatus ] == Buying && !Commodities[ i ][ IsBusinessCommodity ])
                format(str, sizeof(str), "%s{FFFFFF}%s\t\t\t\t%d vienetai{C8C8C8}(%d){FFFFFF}\t\t\t$%d\t-%d\n",
                    str, 
                    GetCargoName(Commodities[ i ][ CargoId ]),
                    Commodities[ i ][ CurrentStock ],
                    GetCargoLimit(Commodities[ i ][ CargoId ]),
                    Commodities[ i ][ Price ],
                    GetCargoConsumption(Commodities [ i ][ CargoId ])
                );
                   
        }
        SetPVarInt(playerid, "Company", listitem);
        SetPVarInt(playerid, "Business", -1);
        ShowPlayerDialog( playerid, DIALOG_INDUSTRY_INFO, DIALOG_STYLE_MSGBOX, Industries[ listitem ][ Name ], str, "Paþymëti", "Atgal" );
        return 1;
    }

    // Sarasas verslu. Reikia rodyt ju info.
    /*if(dialogid == DIALOG_TPDA_BUSINESS)
    {
        if(!response) 
            return 1;

        // Vienintelis budas gauti verslo indeksa is teksto..
        new tmp[16],id, index,str[1024];
        strmid(tmp, inputtext, 0, strfind(inputtext, "."));
        id = strval(tmp);

        // Pagal ID susirandam indeksa.
        index = GetBusinessIndex(id);
        
            

        // Dabar jau galima formatuot informacija.
        format(str, sizeof(str), "{FFFFFF}Sveiki atvyke á {00FF66}%s!\n\n", bInfo[ index ][ bName ]);
       
        strcat(str, "{00FF66}\nPerkama: \n");
        if(!GetBusinessBoughtCommodityCount(index)) // Jei nëra prekiø perkamø.
            strcat(str, "{C8C8C8}Ði firma nieko neperka");
        else 
        {
            strcat(str,"{C8C8C8}Prekë\t\tYra sandëlyje (limitas)\t\tKaina\n");
            // Ilgas kodo gabalas kad sudët sàraðà perkamø dalykø
            foreach(CommodityIterator, i)
            if(Commodities[ i ][ IndustryId ] == bInfo[ index ][ bID ] 
                && Commodities[ i ][ SellBuyStatus ] == Buying 
                && Commodities[ i ][ IsBusinessCommodity ])
                format(str, sizeof(str), "%s{FFFFFF}%s\t\t %d vienetai{C8C8C8}(%d){FFFFFF}\t\t\t$%d\n",
                    str, 
                    GetCargoName(Commodities[ i ][ CargoId ]),
                    Commodities[ i ][ CurrentStock ],
                    MAX_BUSINESS_PRODUCTS,
                    Commodities[ i ][ Price ]
                );
                   
        }
        SetPVarInt(playerid, "Company", -1);
        SetPVarInt(playerid, "Business", index);
        ShowPlayerDialog( playerid, DIALOG_INDUSTRY_INFO, DIALOG_STYLE_MSGBOX, bInfo[ index ][ bName ], str, "Paþymëti", "Atgal" );
        return 1;
    }   */

    // Jei pasirinko OK, tiesiog nustatyt CP.
    if(dialogid == DIALOG_INDUSTRY_INFO)
    {
        if(!response)
            return OnDialogResponse(playerid, DIALOG_TPDA_MAIN, 1, 0, "Perþiûrëti visas industrijas");
			
		if ( Checkpoint[ playerid ] != CHECKPOINT_NONE ) 
			return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: KLAIDA!" );

        if(GetPVarInt(playerid, "Business") == -1)
        {
            listitem = GetPVarInt(playerid, "Company");
            SetPlayerCheckPointEx( playerid, CHECKPOINT_CAR, Industries[ listitem ][ PosX ], Industries[ listitem ][ PosY ], Industries[ listitem ][ PosZ ], 6.0 );
        }
        else
        {
            listitem = GetPVarInt(playerid, "Business");
            //SetPlayerCheckPointEx( playerid, CHECKPOINT_CAR, bInfo[ listitem ][ bEnter ][ 0 ], bInfo[ listitem ][ bEnter ][ 1 ], bInfo[ listitem ][ bEnter ][ 2 ], 6.0 );
        }
        return 1;
    }

    // Masinos krovinio sarasas. Jei pasirenka, turi paimti.
    if(dialogid == DIALOG_VEHICLE_CARGO_LIST)
    {
        if(!response) 
            return 1;

        new cargoid,
            vehicleid = GetPVarInt(playerid, "vehicleid"),
            cargoname[32];

        strmid(cargoname, inputtext, 0, strfind(inputtext, "\t"));
        cargoid = strval(cargoname);
        

        // Now we have a selected cargo id 


        // Galejo but paimtas tas krovinys per ta laika kol apsisprende zmogus...
        if(!IsCargoInVehicle(vehicleid, cargoid))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, pasirinkto krovinio/prekës jau nebëra sandalyje.");


        // Jei þaidëjas forklifte, parkaunam ant jo.
        if(GetVehicleModel(GetPlayerVehicleID(playerid)) == 530)
        {
			if(!IsCargoCompatibleWithVehicle(cargoid, GetVehicleModel(GetPlayerVehicleID(playerid))))
				return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðio krovinio negalite pasiimti.");
			if(!HasVehicleSpaceForCargo(GetPlayerVehicleID(playerid), cargoid))
				return 1;
				
            RemoveCargoFromVehicle(vehicleid, cargoid);
            AddCargoToVehicle(GetPlayerVehicleID(playerid), cargoid);
            return 1;
        }
        // Taip gali atsitikti, kai ziuri /trailer cargo
        if(IsPlayerInAnyVehicle(playerid))
            return 1;

        if(!IsCargoCarryable(cargoid))
            return 1;
        if(IsPlayerAttachedObjectSlotUsed(playerid, 7))
            return 1;

        SetPVarInt(playerid, "CargoId", cargoid);
        ApplyAnimation( playerid, "CARRY", "liftup", 4.0, 0, 1, 0, 0, 0 );
        SetPVarInt( playerid, "Tipas2", false );
        SetPlayerSpecialAction(playerid,SPECIAL_ACTION_CARRY);
        RemoveCargoFromVehicle(vehicleid, cargoid);
        return 1;
    }
    // Sàraðas biznio/industrijos prekiø. Pasirinkimas = Pirkimas
    if(dialogid == DIALOG_SOLD_COMMODITY_LIST)
    {
        if(!response) return 1;

        // If the first bit is turned on, it's an industry NOT a business.
        new index = GetPVarInt(playerid, "Industry_Index"), 
            count = 0,
            commodityIndex = -1;
        // Sudëtingas ir kvailas bûdas gaut pasirinktai prekei.
        foreach(CommodityIterator, i)
        {
            if(Industries[ index ][ Id ] != Commodities[ i ][ IndustryId ])
                continue;
            if(Commodities[ i ][ SellBuyStatus ] != Selling) 
                continue;
            if(listitem == count)
            {
                commodityIndex = i;
                break;
            }
            count++;
        }
        if(Commodities[ commodityIndex ][ CurrentStock ] <= 0)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, pasirinkto krovinio/prekës jau nebëra sandalyje.");

        if(GetPlayerMoney(playerid) < Commodities[ commodityIndex ][ Price ])
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai grynø pinigø, kad nusipirktumëte ðià prekæ.");

        if(IsCargoCarryable(Commodities[ commodityIndex ][ CargoId ]))
        {
            // Jei jis forklifte
            new vehicleid = GetPlayerVehicleID(playerid);
            if(GetVehicleModel(vehicleid) == 530)
            {
                if(!HasVehicleSpaceForCargo(vehicleid, Commodities[ commodityIndex ][ CargoId ]))
                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, autokaras jau yra pilnas ir negali daugiau pakelti kroviniø");

                AddCargoToVehicle(vehicleid, Commodities[ commodityIndex ][ CargoId ]);
                GivePlayerMoney(playerid, -Commodities[ commodityIndex ][ Price ]);
                return 1;
            }   

            if(IsPlayerAttachedObjectSlotUsed(playerid, 7))
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti ðio veiksmo, kadangi rankose jau turite kroviná/prekæ.");
            if(IsPlayerInAnyVehicle(playerid))
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, pirmiausià iðlipkite ið tr. priemonës, kad paiimtumëte kroviná.");

            SetPVarInt(playerid, "CargoId", Commodities[ commodityIndex ][ CargoId ]);
            SetPVarInt(playerid, "CommodityIndex", commodityIndex);
            ApplyAnimation( playerid, "CARRY", "liftup", 4.0, 0, 1, 0, 0, 0 );
            SetPVarInt( playerid, "Tipas2", false );
            SetPlayerSpecialAction(playerid,SPECIAL_ACTION_CARRY);
        }
        else 
        {
            new vehicleid = GetNearestVehicle(playerid, 4.0);
            if(vehicleid == INVALID_VEHICLE_ID)
                return SendClientMessage(playerid, GRAD, "Klaida, Jûsø tr. priemonë nëra tinkama krovinio/prekës gabenimui arba jos nëra ðalia Jûsø.");

            if(IsTrailerAttachedToVehicle(vehicleid))
                vehicleid = GetVehicleTrailer(vehicleid);

			if(!CanPlayerUseTruckerVehicle(playerid,GetVehicleModel(vehicleid)))
				return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jûs neturite pakankamai kroviniø perveþimo darbuotojo patirties, kad galëtumëte dirbti su ðia tr. priemone.");
            if(cInfo[ vehicleid ][ cLock ])
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti ðio veiksmo, kadangi sunkveþimis uþrakintas");
            if(IsVehicleLoaded[ vehicleid ])
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðiuo metu vyksta tr. priemonës pakrovimas.");
            if(!IsCargoCompatibleWithVehicle(Commodities[ commodityIndex ][ CargoId ], GetVehicleModel(vehicleid)))
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ði tr. priemonë yra netinkama Jûsø pasirinktam kroviniui.");
            if(!HasVehicleSpaceForCargo(vehicleid, Commodities[ commodityIndex ][ CargoId ]))
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, sunkveþimyje nëra laisvos vietos Jûsø kroviniui.");

            IsVehicleLoaded[ vehicleid ] = true;   
            VehicleLoadTimer[ playerid ] = SetTimerEx("OnPlayerLoadCommodity",1000,true, "iii",playerid, commodityIndex, vehicleid);
			VehicleLoadTime[ playerid ] = 60;
            SetPVarInt(playerid, "vehicleid", vehicleid);
			//PlayerTextDrawShow     ( playerid, InfoText[ playerid ] );
			PlayerTextDrawSetString( playerid, InfoText[ playerid ], "Krovinys bus pakrautas uz 60 sekundziu");

            new pullingVeh = GetTrailerPullingVehicle(vehicleid);
            if(IsValidVehicle(pullingVeh))
            {
                VehicleEngine(pullingVeh, VEHICLE_PARAMS_OFF);
                Engine[pullingVeh] = false;
            }
            else 
            {
                VehicleEngine(vehicleid, VEHICLE_PARAMS_OFF);
                Engine[vehicleid] = false;
            }
            SendClientMessage(playerid, COLOR_NEWS, "Dëmesio, Jûsø pasirinktas krovinys/prekë bus pakrauta á Jûsø tr. priemonæ per 60 sekundþiø. Praðome palaukti.");
        }
		RemoveCargoFromIndustry(index, Commodities[ commodityIndex ][ CargoId ]);
        UpdateIndustryInfo(index);
        format(string,sizeof(string),"Sëkmingai nusipirkotæ prekæ/kroviná pavadinimu: %s, uþ kurià sumokëjote %d$", 
            GetCargoName(Commodities[ commodityIndex ][ CargoId ]), Commodities[ commodityIndex ][ Price ]);
        SendClientMessage(playerid, COLOR_LIGHTRED2, string);
        GivePlayerMoney(playerid, -Commodities[ commodityIndex ][ Price ]);
        return 1;
    }
	else if(dialogid == DIALOG_COMMODITY_SELL)
	{
		if(!response) return 1;
		
		new bool:sellToBusines = false,
			bool:sellToIndustry = false,
			bool:sellToShip = false,
			tmp[32],
			cargoid,
			boughtamount,
			index = GetPVarInt(playerid, "IndustryIndex"),
			price,
			vehicleid = GetPVarInt(playerid, "vehicleid");
			
		// Iðsiaiðkinam kam parduodam.
		if(GetPVarInt(playerid,"CommoditySellTo") == 1)
			sellToBusines = true;
		else if(GetPVarInt(playerid, "CommoditySellTo") == 2)
			sellToIndustry = true;
		else 
			sellToShip = true;
			
		// Susirandam cargo ID prekës pasirinktos.
		strmid(tmp, inputtext,0, strfind(inputtext,"\t"));
		cargoid = strval(tmp);

        // Ar niekas kitas neiðëmë krovinio.
        if(!IsCargoInVehicle(vehicleid, cargoid))
            return SendClientMessage(playerid, GRAD, "Klaida, ðio krovinio nebëra.");
		
		// O dabar jau pradedam pardavima...
		
		// Susiþinom KIEK gali nupirkti industrija/verslas
		if(sellToIndustry)
		{	
            if(!Industries[ index ][ IsBuyingCargo ])
                return SendClientMessage(playerid, GRAD, "Klaida, dël produktø pertekliaus ði industrija nedirba.");
            if(!HasIndustryRoomForCargo(index, cargoid))
                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, industrija ðiai prekei nebeturi vietos.");
			boughtamount = GetCargoLimit(cargoid) - GetIndustryCargoStock(index,cargoid); // Kiek GALI pirk industrija

			if(boughtamount > GetVehicleCargoCount(vehicleid, cargoid))
				boughtamount = GetVehicleCargoCount(vehicleid,cargoid);
		
			
			if(!boughtamount)
				return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti ðio veiksmo, kadangi industrijos sandëlys pilnas.");
			
            price = Commodities[ GetIndustryCargoIndex(index, cargoid) ][ Price ] * boughtamount;
            if(!price)
                return SendClientMessage(playerid, GRAD, "Klaida, Jûsø tr. priemonëje nëra prekiø, kurias superka pasirinktas fabrikas.");
			
			// duodam viska industrijai
			AddCargoToIndustry(index, cargoid, boughtamount);
			
			// Iðimam viskà ið transporto priemoëns.
			RemoveCargoFromVehicle(vehicleid, cargoid, boughtamount);
		}
        /*
		else if(sellToBusines)
		{
            new commodityIndex = GetBusinessCargoIndex(index, cargoid);
            // Kiek truksta iki limito.
			boughtamount = MAX_BUSINESS_PRODUCTS / 50 - Commodities[ commodityIndex ][ CurrentStock ];

            if(boughtamount <= 0)
            {
                StopBusinessBuyingCargo(index, cargoid);
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðis verslas nebeperka prekiø.");
            }

            // Jei furistas nepapildys iki galo verslo sumazinam kiek gali parduot.
			if(boughtamount > GetVehicleCargoCount(vehicleid, cargoid))
				boughtamount = GetVehicleCargoCount(vehicleid,cargoid);

            // Debug code
            if(boughtamount < 0)
            {
                new s[128];
                format(s, sizeof(s), "First. Boughtamount: %d. Current stock:%d VehicleCargoCount:%d commodity index:%d biz index:%d", 
                    boughtamount, Commodities[ commodityIndex ][ CurrentStock ],  GetVehicleCargoCount(vehicleid, cargoid), commodityIndex, index);
                ImpossibleLog(s);
                return SendClientMessage(playerid, 0xFF0000FF, "KLAIDA. Praneðkite apie tai administracijai.");
            }
            // end of debug code
			
			// Jei verlslas perka daugiau negu gali ápirkti
			if(boughtamount * Commodities[ commodityIndex ][ Price ] > bInfo[ index ][ bBank ])
				boughtamount = bInfo[ index ][ bBank ] / Commodities[ commodityIndex ][ Price ];

            // Debug code
            if(boughtamount < 0)
            {
                new s[128];
                format(s, sizeof(s), "Second. Boughtamount:%d. Current stock:%d VehicleCargoCount:%d commodity index:%d biz index:%d", 
                    boughtamount, Commodities[ commodityIndex ][ CurrentStock ],  GetVehicleCargoCount(vehicleid, cargoid), commodityIndex, index);
                ImpossibleLog(s);
                return SendClientMessage(playerid, 0xFF0000FF, "KLAIDA. Praneðkite apie tai administracijai.");
            }
            // end of debug code

			price = Commodities[ commodityIndex ][ Price ] * boughtamount;
            if(!price)
                return SendClientMessage(playerid, GRAD, "Klaida, Jûsø tr. priemonëje nëra prekiø, kurias superka pasirinktas verslas.");
            printf("Boughtamount:%d price:%d stock:%d",boughtamount, price, GetBusinessCargoStock(index));
			AddCargoToBusiness(index, cargoid, boughtamount);
			bInfo[ index ][ bBank ] -= price;
			
			// Iðimam viskà ið transporto priemoëns.
			RemoveCargoFromVehicle(vehicleid, cargoid, boughtamount);

            if(bInfo[ index ][ bBank ] < Commodities[ commodityIndex ][ Price ])
            {
                // reik surast savininkà ir pasakyk kad kapeikos baigës :(
                foreach(Player,j)
                    if(pInfo[ j ][ pMySQLID ] == bInfo[ index ][ bOwner ])
                    {
                        SendClientMessage(j, GRAD, "Jûsø versle baigësi pinigai, daugiau produktø nebepirksite.");
                        StopBusinessBuyingCargo(index, cargoid);
                        break;
                    }
            }
		}*/
		
		if(sellToShip)
		{
			if(ShipInfo[ Status ] != Docked)
				return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ðiuo metu krovininis laivas yra iðplaukæs. Naudodami komanda /tpda galite pamatyti kada jis atplauks.");
			// Laivas didelis, supirks VISAS kurias turi.
			boughtamount = GetVehicleCargoCount(vehicleid,cargoid);
			
			price = GetShipCargoPrice(cargoid) * boughtamount;
            if(!price)
                return SendClientMessage(playerid, GRAD, "Klaida, Jûsø tr. priemonëje nëra prekiø, kurias superka laivas.");
			ShipInfo[ CurrentStock ] = boughtamount * GetCargoSlot(cargoid);
			
			// Iðimam viskà ið transporto priemoëns.
			RemoveCargoFromVehicle(vehicleid, cargoid, boughtamount);
		}
		
		format(string,sizeof(string), "[FABRIKAS] Sëkmingai pardavëte visas savo pakrautas prekes/krovinius, ið kuriø uþdirbote %d$.",price);
		GivePlayerMoney(playerid, price);
		SendClientMessage(playerid, COLOR_LIGHTRED2, string);
		return 1;
	}
    else if( dialogid == 129 )
    {
        if(!response)
            return 1;
            
        if( isnull( inputtext ) )
            return SendClientMessage( playerid, COLOR_LIGHTRED,"Dëmesio, neáraðëte jokiø þodþiø á pateiktà laukelá");
            
        new
            result2[ 3 ][ 128 ],
            dialog[ 2048 ],
            sAge,
            sGender[ 11 ],
            sPhoneNumber,
            IDNumber[ 16 ],
            test[ 3 ],
            dates[ 3 ],
            rows,
            nationality[ 24 ];


        mysql_format(DbHandle, string, 128, "SELECT `PhoneNr`,`ucpuser`,`Age`,`Sex`,`id`,`Origin`,`WantedLevel` FROM `players` WHERE `Name` = '%e'", inputtext );
        new Cache:result = mysql_query(DbHandle,  string );
        rows = cache_get_row_count();

        if( rows )
        {

        sPhoneNumber = cache_get_field_content_int(0, "PhoneNr");
        test[0] = cache_get_field_content_int(0, "ucpuser");
        sAge = cache_get_field_content_int(0, "Age");
        cache_get_field_content(0, "Sex", sGender);
        test[1] = cache_get_field_content_int(0, "id");
        cache_get_field_content(0, "Origin", nationality);
        test[2] = cache_get_field_content_int(0, "WantedLevel");


        format(IDNumber, sizeof(IDNumber), "%d000000", test[0] );
        format(IDNumber, sizeof(IDNumber), "%s%d", IDNumber, sAge );
        format(IDNumber, sizeof(IDNumber), "%s%d", IDNumber, test[1] );

        getdate(dates[0], dates[1], dates[2]);
        format(dialog, sizeof(dialog), "Paieðkomas asmuo: %s", inputtext);
        format(dialog, sizeof(dialog), "%s\nAmþius: %d (gimë %d)", dialog, sAge, dates[0] - sAge);
        format(dialog, sizeof(dialog), "%s\nAsmens kodas: %s", dialog, IDNumber );
        format(dialog, sizeof(dialog), "%s\nLytis: %s", dialog, sGender);
        format(dialog, sizeof(dialog), "%s\nTautybë: %s", dialog, nationality);
        if( sPhoneNumber == 0 )
            format(dialog, sizeof(dialog), "%s\nTelefono numeris: Nëra", dialog);
        else
            format(dialog, sizeof(dialog), "%s\nTelefono numeris: %d", dialog, sPhoneNumber);

        if( test[2] > 0 )
            format(dialog, sizeof(dialog), "%s\nPilietis ieðkomas!", dialog);
            
        }
        cache_delete(result);
        
        mysql_format(DbHandle, string, 128, "SELECT `crime`,`reporter`,`When` FROM `crimes` WHERE `name` = '%e' ORDER BY `When` DESC LIMIT 7", inputtext);
        result = mysql_query(DbHandle,  string );
        rows = cache_get_row_count();

        for(new i = 0; i < cache_get_row_count(); i++)
        {
            cache_get_field_content(i, "crime", result2[0]);
            cache_get_field_content(i, "reporter", result2[1]);
            cache_get_field_content(i, "When", result2[2]);

            format( dialog, sizeof( dialog ),"%s\n-->áskaita: %s\n", dialog, result2[0] );
            format( dialog, sizeof( dialog ), "%sPolicininkas: %s\n", dialog, result2[1] );
            format( dialog, sizeof( dialog ), "%sLaikas: %s", dialog, result2[2] );
        }
        
        cache_delete(result);
        ShowPlayerDialog( playerid, 136, DIALOG_STYLE_LIST,"Informacija rasta", dialog,
            "Uþdaryti", "" );
    }
    else if( dialogid == 130 )
    {
        if(!response)
            return 1;

        if(isnull(inputtext))
            return SendClientMessage(playerid, COLOR_LIGHTRED,"Klaida, neáraðëte tr. priemonës numeriø.");

        new dialog[ 1024 ], Cache:result, name[MAX_PLAYER_NAME], tmp[ 128 ];

        mysql_format(DbHandle, dialog, sizeof(dialog), 
            "SELECT vehicles.cName, vehicles.cInsurance, vehicles.cCrimes, players.name, arrestedcars.who, arrestedcars.Time, carcrimes.id AS crime_id, carcrimes.crime, carcrimes.reporter, carcrimes.when \
            FROM vehicles \
            LEFT JOIN players ON players.id = vehicles.cOwner \
            LEFT JOIN arrestedcars ON arrestedcars.cMySQL = vehicles.id \
            LEFT JOIN carcrimes ON carcrimes.numbers = vehicles.cNumbers \
            WHERE cNumbers = '%e'", inputtext);
        result = mysql_query(DbHandle, dialog);

        if(!cache_get_row_count())
            SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, transporto priemonë nerasta.");
        else 
        {
            cache_get_field_content(0, "cName", dialog);
            cache_get_field_content(0, "name", name);

            format(dialog, sizeof(dialog), "DUOM. BAZË: Tr. Priemonës pavadinimas: %s\n\
                DUOM. BAZË: Tr. Priemonës numeriai: %s\n\ 
                DUOM. BAZË: Tr. Priemonës draudimas: %d\n\
                DUOM. BAZË: Tr. Priemonës savininkas: %s\n", 
                dialog, 
                inputtext,
                cache_get_field_content_int(0, "cInsurance"),
                name);

            if(cache_get_field_content_int(0, "cCrimes"))
                strcat(dialog, "Tr. priemonë ieðkoma.");

            // Processinam arrestedcars duomenis
            cache_get_field_content(0, "who", name);
            if(!ismysqlnull(name))
            {
                cache_get_field_content(0, "Time", tmp);
                format(dialog, sizeof(dialog), "%sTr. priemonë buvo areðtuota!\nPareigûnas: %s\nLaikas ir data: %s", dialog, name, tmp);
            }

            // Processinam carcrimes
            for(new i = 0; i < cache_get_row_count(); i++)
            {
                cache_get_field_content(i, "reporter", name);
                cache_get_field_content(i, "crime", string);
                cache_get_field_content(i, "when", tmp);
                if(ismysqlnull(name))
                    break;
                format(dialog, sizeof(dialog), "%s-->áskaita: %s\nPolicininkas: %s\nLaikas: %s", dialog, string, name, tmp);
            }
            ShowPlayerDialog(playerid, 136, DIALOG_STYLE_LIST,"Informacija rasta", dialog, "Uþdaryti", "" );
        }
        cache_delete(result);
      
    }
    else if( dialogid == 131 )
    {
        if(!response)
            return 1;

        new
            rows,
            i = 0;
                    
        new Cache:result = mysql_query(DbHandle,  "SELECT `Name` FROM `players` WHERE `WantedLevel` != 0" );
        rows = cache_get_row_count();

        if( rows )
        {
            for(i = 0; i < cache_get_row_count(); i++)
            {
                if( i == listitem )
                {
                    cache_get_field_content(i, "Name", string);
                    OnDialogResponse( playerid, 129, 1, 0, string );
                }
            }
        }
        cache_delete(result);
    }
    else if( dialogid == 141 )
    {
        if(!response)
            return 1;
        new
            i = 0;

        new Cache:result = mysql_query(DbHandle,  "SELECT `cNumbers` FROM `vehicles` WHERE `cCrimes` != 0" );

        for(i = 0; i < cache_get_row_count(); i++)
        {
            if( i == listitem )
            {
                cache_get_field_content(i, "cNumbers", string);
                OnDialogResponse( playerid, 130, 1, 0, string );
            }
        }
        cache_delete(result);
    }
    else if( dialogid == 134 )
    {
        if(!response)
            return 1;
        if( isnull( inputtext ) )
            return SendClientMessage( playerid, COLOR_WHITE,"Neáraðëte tr. priemonës numerio ! ");
        mysql_real_escape_string( inputtext, inputtext, DbHandle, 128);
        SetPVarString ( playerid, "CarNumber", inputtext );
        ShowPlayerDialog( playerid, 135, DIALOG_STYLE_INPUT,"Pridëti ieðkoma maðiná  (2)",
        "{1797cd}LOS SANTOS POLICE DEPARTAMENT\n\
		{FFFFFF}Áraðykite prieþastá kodël tr. priemonë paieðkoma", "Pridëti", "Atðaukti " );
    }
    if(dialogid == INTERIORMENU)
    {
        if(response)
        {
            if(listitem == 18) // {FF0000} Custom Interriorai
            {
            ShowPlayerDialog(playerid, INTERIORMENU+19, DIALOG_STYLE_LIST, "Custom mapai","Poþeminis Casino\n\
                                                                                      Bilijardo Baras\n\
                                                                                      MD \n\
                                                                                      Bankas\n\
                                                                                      Bandidos Baras \n\
                                                                                      Mantom Baras \n\
                                                                                      Savivaldybë \n\
                                                                                      \nAtgal", "Pasirinkti", "Atðaukti");
            }
            if(listitem == 19) // Back
            {
            return ShowPlayerDialog(playerid, INTERIORMENU, DIALOG_STYLE_LIST, "Interior Categories","24/7's\nAirports\nAmmunations\nHouses\nHouses 2\nMissions\nStadiums\nCasinos\nShops\nGarages\nGirlfriends\nClothing/Barber Store\nResturants/Clubs\nNo Category\nBurglary\nBurglary 2\nGym\nDepartment\n{FF0000} Custom Interriorai\nBack", "Select", "Cancel");
            }
        }
        return 1;
    }
    if(dialogid == INTERIORMENU+19) // {FF0000} Custom Interriorai
    {
        if(response)
        {
            if(listitem == 0) // Casino
            {
         //     SetPlayerPos(playerid,1947.6279,1924.9791,1693.6992);
            SetPlayerPos(playerid, -133.3848,56.0560,1000.7200);
            SetPlayerInterior(playerid,3);
            }
            if(listitem == 1) // Pool
            {
        //      SetPlayerPos(playerid,1912.3922,1924.3256,1599.0569);
            SetPlayerPos(playerid, 343.2530,11.6263,999.8078);
            SetPlayerInterior(playerid,3);
            }
            if(listitem == 2) // MD (Naujas??)
            {
        //      SetPlayerPos(playerid,2488.1943,955.7484,364.7727);
            SetPlayerPos(playerid, 2825.9783,-181.6222,7934.1270);
            SetPlayerInterior(playerid,3);
            }
            if(listitem == 3) // banko int
            {
            SetPlayerPos(playerid,97.1219,-26.0835,1000.5128);
            SetPlayerInterior(playerid,3);
            }
            if(listitem == 4) // Bandidos baras
            {
            SetPlayerPos(playerid,1070.9766,1787.5327,1101.9008);
            SetPlayerInterior(playerid,18);
            }
            if(listitem == 5) // Mantom
            {
            SetPlayerPos(playerid,2127.4014,-1767.0282,1895.4453);
            SetPlayerInterior(playerid,10);
            }
            if(listitem == 6) // Savivaldybë
            {
            SetPlayerPos(playerid,1477.4452,-1755.5870,2405.0327);
            SetPlayerInterior(playerid,17);
            }
            if(listitem == 7) // Back
            {
            ShowPlayerDialog(playerid, INTERIORMENU, DIALOG_STYLE_LIST, "Interior Categories","24/7's\nAirports\nAmmunations\nHouses\nHouses 2\nMissions\nStadiums\nCasinos\nShops\nGarages\nGirlfriends\nClothing/Barber Store\nResturants/Clubs\nNo Category\nBurglary\nBurglary 2\nGym\nDepartment\n{FF0000} Custom Interriorai\nBack", "Select", "Cancel");
            }
        }
        return 1;
    }
  
    return 1;
}


public OnPlayerClickPlayer(playerid, clickedplayerid, source)
{
    #if defined DEBUG
        printf("[debug] OnPlayerClickPlayer(%s, %s, %d)", GetName(playerid), GetName(clickedplayerid), source);
    #endif
    return 1;
}

 GetWeaponObjectModel(weaponid)
{
    switch(weaponid)
    {
        case WEAPON_DILDO:              return 321;
        case WEAPON_DILDO2:             return 322;
        case WEAPON_VIBRATOR:           return 323;
        case WEAPON_VIBRATOR2:          return 324;
        case WEAPON_FLOWER:             return 325;
        case WEAPON_CANE:               return 326;
        case WEAPON_BRASSKNUCKLE:       return 331;
        case WEAPON_GOLFCLUB:           return 333;
        case WEAPON_NITESTICK:          return 334;
        case WEAPON_KNIFE:              return 335;
        case WEAPON_BAT:                return 336;
        case WEAPON_SHOVEL:             return 337;
        case WEAPON_POOLSTICK:          return 338;
        case WEAPON_CHAINSAW:           return 341;
        case WEAPON_GRENADE:            return 342;
        case WEAPON_TEARGAS:            return 343;
        case WEAPON_MOLTOV:             return 344;
        case WEAPON_COLT45:             return 346;
        case WEAPON_SILENCED:           return 347;
        case WEAPON_DEAGLE:             return 348;
        case WEAPON_SAWEDOFF:           return 350;
        case WEAPON_SHOTGUN:            return 349;
        case WEAPON_SHOTGSPA:           return 351;
        case WEAPON_UZI:                return 352;
        case WEAPON_MP5:                return 353;
        case WEAPON_AK47:               return 355;
        case WEAPON_M4:                 return 356;
        case WEAPON_RIFLE:              return 357;
        case WEAPON_SNIPER:             return 358;
        case WEAPON_ROCKETLAUNCHER:     return 359;
        case WEAPON_HEATSEEKER:         return 360;
        case WEAPON_FLAMETHROWER:       return 361;
        case WEAPON_MINIGUN:            return 362;
        case WEAPON_SATCHEL:            return 363;
        case WEAPON_BOMB:               return 364;
        case WEAPON_SPRAYCAN:           return 365;
        case WEAPON_FIREEXTINGUISHER:   return 366;
        case WEAPON_CAMERA:             return 367;
        case WEAPON_TEC9:               return 372;
        case WEAPON_PARACHUTE:          return 371;
    }
    return 0;
}
 GetSpaceString(amount)
{
    new string[64];
    for(new i = 0; i < amount; i++)
        strcat(string, " ");
    return string;
}
FUNKCIJA:Vote( )
{
    new string[ 64 ];
    SendClientMessageToAll( COLOR_WHITE, "Balsavimas baiktas!" );
    format( string, sizeof(string), "Rezultatai: TAIP - %d, NE - %d, ", Votes[ 0 ], Votes[ 1 ] );
    SendClientMessageToAll( COLOR_WHITE, string );

    foreach(Player,id)
        Voted[ id ] = true;

    Votes[ 0 ] = 0;
    Votes[ 1 ] = 0;
    return 1;
}


 ShowPlayerSecretQuestionDialog(playerid, question[], errostr[] = "")
{
    new content[ 512 ];
    format(content, sizeof(content), "{AA1100}%s\n{FFFFFF}Kad prisijungtumëte, atsakykite á savo pasirinktà klausimà. \n\n%s?", errostr, question);
    ShowPlayerDialog(playerid, DIALOG_SECRET_QUESTION, DIALOG_STYLE_PASSWORD, "Prisijungimas: atsakymas", content, "Jungtis", "Iðeiti");
    return 1;
}
 ShowPlayerQuestionSetDialog(playerid, errostr[] = "")
{
    new string[256];
    format(string, sizeof(string), "{AA1100}%s\n{FFFFFF}Sugalvokite klausimà á kurá galëtumëte atsakyti tik jûs patys.\nÁ já atskayti turësite kas kartà jungdamiesi, tai uþtrikins saugumà.", errostr);
    ShowPlayerDialog(playerid, DIALOG_SECRET_QUESTION_SET, DIALOG_STYLE_INPUT, "Saugos klausimas", string, "Tæsti", "Iðeiti");
    return 1;
}
 ShowPlayerAnswerSetDialog(playerid, question[], errostr[] = "")
{
    new string[512];
    format(string, sizeof(string), "{AA1100}%s\n{FFFFFF}Jûsø klausimas:%s?\nÁveskite atsakymà ir nepamirðkite jo.", errostr, question);
    ShowPlayerDialog(playerid, DIALOG_SECRET_ANSWER_SET, DIALOG_STYLE_PASSWORD, "Saugos atsakymas", string, "Tæsti", "Iðeiti");
    return 1;
}

 GetVehicleSpeed2( vehid )
{
    new Float:x,
        Float:y,
        Float:z;
    GetVehicleVelocity( vehid, x, y, z );
    return floatround( floatsqroot( x*x + y*y + z*z ) * 170 );
}
 SetVehicleSpeed( vehicleid, mph )
{
    new Float:Vx,
        Float:Vy,
        Float:Vz,
        Float:DV,
        Float:multiple;

    GetVehicleVelocity( vehicleid, Vx, Vy, Vz);
    DV = floatsqroot( Vx*Vx + Vy*Vy + Vz*Vz );
    if ( DV > 0 )
    {
        multiple = ( mph / ( DV * 170 ) );
        return SetVehicleVelocity( vehicleid, Vx*multiple, Vy*multiple, Vz*multiple );
    }
    return 0;
}

public OnVehicleDamageStatusUpdate( vehicleid, playerid )
{
    new Float:Damage,
        text[ 126 ];

    GetVehicleHealth( vehicleid, Damage );

    if ( ( V_HP[ vehicleid ] - 250.0 ) > Damage )
    {
        if ( Engine[ vehicleid ] == true )
        {
            Engine[ vehicleid ] = false;
            VehicleEngine( vehicleid, 0 );
            format       ( text, 126, "* Automobilio variklis iðsijungia.(( %s ))", GetPlayerNameEx( playerid ) );
            ProxDetector(20.0, playerid, text, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
            return 1;
        }
    }
    if ( Damage < 300 )
    {
        if ( Engine[ vehicleid ] == true )
        {
            Engine[ vehicleid ] = false;
            VehicleEngine( vehicleid, 0 );
            format       ( text, 126, "* Automobilio variklis iðsijungia.(( %s ))", GetPlayerNameEx( playerid ) );
            ProxDetector(20.0, playerid, text, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
            return 1;
        }
    }
    Check_VHP( vehicleid, 0, Damage );
    return 1;
}

FUNKCIJA:Drugs( )
{
    foreach(Player,i)
    {
        if ( IsPlayerAddicted( i ) && !GetPVarInt( i, "Addicted" ) )
        {
            SetPVarInt( i, "Addicted", true );
            SetPVarInt( i, "AddictionTime", 10+random(16) );
            DrugTimer[ i ] = SetTimerEx( "DrugsEffect", 7000, false, "i", i );
        }
    }
    return 1;
}

FUNKCIJA:DrugsEffect( i )
{
    KillTimer( DrugTimer[ i ] );
    if( GetPVarInt( i, "Addicted" ) )
    {
        new
            Float:Health,
            sum = 0;
            
        sum += pInfo[ i ][ pHeroineAddict ];
        sum += pInfo[ i ][ pAmfaAddict ];
        sum += pInfo[ i ][ pCocaineAddict ];
        sum += pInfo[ i ][ pMetaAmfaineAddict ];
        sum += pInfo[ i ][ pExtazyAddict ];
        sum += pInfo[ i ][ pPCPAddict ];
        sum += pInfo[ i ][ pCrackAddict ];
        sum += pInfo[ i ][ pOpiumAddict ];
        
        GetPlayerHealth( i, Health );
        if ( Health-sum > 15.0 )
            SetPlayerHealth( i, Health-sum );
        else
            SetPlayerHealth( i, 15 );
        DrugTimer[ i ] = SetTimerEx( "DrugsEffect", 7000, false, "i", i );
    }
    return 1;
}
CMD:shipstatus(playerid)
{
    if(!GetPlayerAdminLevel(playerid))
        return 0;

    new string[64];
    switch(ShipInfo[ Status ])
    {
        case Moving: string = "Iðplaukia";
        case Docked: string = "Uoste";
        case Arriving: string = "Plaukia á uostà";
    }
    format(string, sizeof(string),"Laivo statusas: %s", string);
    SendClientMessage(playerid, COLOR_NEWS, string);
    return 1;
}

FUNKCIJA:CargoShipDeparture()
{
    #if defined DEBUG
        print("[debug] CargoShipDeparture()");
    #endif
	// Timeris paleistas viena karta. Paleidzia kita timeri: laivo isplaukimas.
	// Po to kai laivas isplaukia, vel paleidziamas sitas timeris. 
	SetTimer("CargoShipReturn", CARGOSHIP_MOVING_INTERVAL, false);
	ShipInfo[ Status ] = Moving;
	ShipInfo[ LastDepartureTimestamp ] = gettime();

    MoveCargoShipToPoint(2822.5435, -3951.1589, 29.2656);
    return 1;
}

FUNKCIJA:CargoShipReturn()
{
    #if defined DEBUG
        print("[debug] CargoShipReturn()");
    #endif
	// Timeris pradeda laivo objektø graþinimà á uostà
    // Kai objektai baigs judëti, laivas vël priims krovinius.
	SetTimer("CargoShipDeparture", CARGOSHIP_DOCKED_INTERVAL, false);
	
    ShipInfo[ Status ] = Arriving;

    MoveCargoShipToPoint(.ToSpawn = true);
    return 1;
}

 MoveCargoShipToPoint(Float:x = 0.0, Float:y = 0.0, Float:z = 0.0, ToSpawn = false)
{
    if(ToSpawn)
    {
        for(new i = 0; i < sizeof(CargoShipObjectPositions); i++)
            MoveDynamicObject(ShipInfo[ ObjectIDs ][ i ], 
                CargoShipObjectPositions[ i ][ PosX ], 
                CargoShipObjectPositions[ i ][ PosY ], 
                CargoShipObjectPositions[ i ][ PosZ ],
                10.0,
                CargoShipObjectPositions[ i ][ RotX ],
                CargoShipObjectPositions[ i ][ RotY ],
                CargoShipObjectPositions[ i ][ RotZ ]);
    }
    else 
    {
        new middle = sizeof(CargoShipObjectPositions) / 2,
            Float:diffX,
            Float:diffY,
            Float:diffZ;
        for(new i = 0; i < sizeof(CargoShipObjectPositions); i++)
        {
            diffX = CargoShipObjectPositions[ middle ][ PosX ] - CargoShipObjectPositions[ i ][ PosX ];
            diffY = CargoShipObjectPositions[ middle ][ PosY ] - CargoShipObjectPositions[ i ][ PosY ];
            diffZ = CargoShipObjectPositions[ middle ][ PosZ ] - CargoShipObjectPositions[ i ][ PosZ ];


            MoveDynamicObject(ShipInfo[ ObjectIDs ][ i ], x - diffX, y - diffY, z - diffZ, 10.0, 
                CargoShipObjectPositions[ i ][ RotX ],
                CargoShipObjectPositions[ i ][ RotY ],
                CargoShipObjectPositions[ i ][ RotZ ]);

        }
    }
}

public OnDynamicObjectMoved(objectid)
{
    if(ShipInfo[ Status ] == Arriving)
    {
        for(new i = 0; i < sizeof(CargoShipObjectPositions); i++)
        {
            if(objectid != ShipInfo[ ObjectIDs ][ i ])
                continue;

            ShipInfo[ Status ] = Docked;
            ShipInfo[ LastArrivalTimestamp ] = gettime();
            break;
        }
    }
    else if(objectid == ShipInfo[ ObjectIDs ][ 0 ])
        ErrorLog("OnDynamicObjectMoved(%d) invalid ship status:%d", objectid, _:ShipInfo[ Status ]);
    return 0;
}


 IsPlayerInRangeOfPlayer(playerid, playerid2, Float:distance)
{
    new Float:x,Float:y,Float:z;
    GetPlayerPos(playerid2, x, y, z);
    return IsPlayerInRangeOfPoint(playerid, distance, x, y, z);
}
 IsPlayerInRangeOfCargoShip(playerid, Float:distance)
	return IsPlayerInRangeOfPoint(playerid, distance, SHIP_POS_X, SHIP_POS_Y, SHIP_POS_Z);

 GetShipCargoPrice(cargoid)
{
    new sum, count, lowestSellPrice = -1;
    foreach(CommodityIterator, i)
        if(Commodities[ i ][ CargoId ] == cargoid)
        {
            if(Commodities[ i ][ SellBuyStatus ] == Buying)
            {
                sum += Commodities[ i ][ Price ];
                count++;
            }
            // Jei parduoda ta preke, norim susirast min pardavimo kaina
            else 
                if(lowestSellPrice == -1 || Commodities[ i ][ Price ] < lowestSellPrice)
                    lowestSellPrice = Commodities[ i ][ Price ];
        }

    // Jei niekas neperka tos prekes(for some reason)... naudosim parduodamas prekes :(
    if(!count)
    {
        foreach(CommodityIterator, i)
        if(Commodities[ i ][ CargoId ] == cargoid && Commodities[ i ][ SellBuyStatus ] == Selling)
        {
            sum += Commodities[ i ][ Price ];
            count++;
        }
        if(!count)
            return 0;
        new average = floatround(sum / count);
        return average + (average / 100 * SHIP_BUY_PRICE_MARGIN);
    }

    new average = floatround(sum / count);
    new price  = average - (average / 100 * SHIP_BUY_PRICE_MARGIN);
    // Jei gavome kaina kuri maþesnë uþ maþiausià pardavimo kainà, keièiam jà á maþiausià pardavimo kainà
    // Tai darom kad þaidëjai neprarastø pinigø. idiots.
    if(price < lowestSellPrice)
        price = lowestSellPrice;
    return price;
}


FUNKCIJA:IndustryUpdate()
{
	new ticks = GetTickCount();
	new soldCommodityIndex = -1, // Indeksas prekes kuria industrija parduoda. I JA taps visi jos turimi resursai.
        soldCommodityProduction, // Kiek pagaminama prekiø ið ðio resurso.
		//madeamount, // Kiek pagamino ta prake nauju prekiu.
		limit;
        //consumption; // Kiekis kuri sunaudos pagaminti antrini daikta
	foreach(IndustryIterator, i)
	{
		// Jei ji nieko neperka. Kitaip tariant jeigu pirmas sektorius
		if(!GetIndustryBoughtCommodityCount(i))
		{
            printf("Industry name:%s| doesnt buy anything", Industries[i][Name]);
			foreach(CommodityIterator, j)
				if(Commodities[ j ][ IndustryId ] == Industries[ i ][ Id ]
					&& !Commodities[ j ][ IsBusinessCommodity ]
					&& Commodities[ j ][ SellBuyStatus ] == Selling)
				{
					limit = GetCargoLimit(Commodities[ j ][ CargoId ]);
					Commodities[ j ][CurrentStock ] += GetCargoProduction(Commodities[ j ][ CargoId ]);

					if(Commodities[ j ][ CurrentStock ] > limit)
						Commodities[ j ][ CurrentStock ] = limit;
				}
		}
        // Jei nieko neparduoda, tiesiog suvalgo resursus.
        if(!GetIndustrySoldCommodityCount(i))
        {
            printf("Industry name:%s| doesnt sell anything", Industries[i][Name]);
            foreach(CommodityIterator, j)
                if(Commodities[ j ][ IndustryId ] == Industries[ i ][ Id ]
                    && !Commodities[ j ][ IsBusinessCommodity ]
                    && Commodities[ j ][ SellBuyStatus ] == Buying)
                {
                    Commodities[ j ][ CurrentStock ] -= GetCargoConsumption(Commodities[ j ][ CargoId ]);
                    if(Commodities[ j ][ CurrentStock ] < 0)
                        Commodities[ j ][ CurrentStock ] = 0;
                }
        }
        else 
        {
            // Nuo èia kodas baisu, todël niekam neleidþiu skaityt '_'

            // Pereinam per visas prekes.
            // Susirandom TOS industrijos PARDUODAMAS prekes. Jas gaminsim.
            foreach(CommodityIterator, j)
            if(Commodities[ j ][ IndustryId ] == Industries[ i ][ Id ]
            && !Commodities[ j ][ IsBusinessCommodity ]
            && Commodities[ j ][ SellBuyStatus ] == Selling)
            {
                soldCommodityIndex = j;
                soldCommodityProduction = GetCargoProduction(Commodities[ j ][ CargoId ]);
                limit = GetCargoLimit(Commodities[ j ][ CargoId ]);

                // Iðvis. Jeigu pasiektas limitas, nieko èia nereikia daryt.
                if(Commodities[ soldCommodityIndex ] [ CurrentStock ] >= limit)
                    break;

                enum bought_commodity_info {
                    index, 
                    Consumption,
                    canBeMade
                };
                new BoughtCommodities[ MAX_TRUCKER_CARGO ][ bought_commodity_info ], boughtCommodityCount = 0, willBeMadeAmount = -1;
                foreach(CommodityIterator, k)
                    if(Commodities[ k ][ IndustryId ] == Industries[ i ][ Id ]
                        && !Commodities[ k ][ IsBusinessCommodity ]
                        && Commodities[ k ][ SellBuyStatus ] == Buying)
                    {
                        BoughtCommodities[ boughtCommodityCount ][ index ] = k;
                        BoughtCommodities[ boughtCommodityCount ][ Consumption] = GetCargoConsumption(Commodities[ k ][ CargoId ]);
                        BoughtCommodities[ boughtCommodityCount ][ canBeMade] = Commodities[ k ][ CurrentStock ] / BoughtCommodities[ boughtCommodityCount ][ Consumption];
                        boughtCommodityCount++;
                    }

                for(new k = 0; k < boughtCommodityCount; k++)
                {
                    if(BoughtCommodities[ k ][ canBeMade ] >= 0 && (BoughtCommodities[ k ][ canBeMade ] < willBeMadeAmount || willBeMadeAmount == -1))
                        willBeMadeAmount = BoughtCommodities[ k ][ canBeMade ];
                }
                // Tai reiðkia kad neuþtenka produktø pagaminti bent vienam vienetui :(
                if(willBeMadeAmount <= 0)
                    break;

                // Jei nori gamint daugiau nei telpa.
                if(willBeMadeAmount * soldCommodityProduction > limit)
                {
                    willBeMadeAmount = (limit - Commodities[ soldCommodityIndex ][ CurrentStock ]) / soldCommodityProduction;
                    willBeMadeAmount++; // Nes kitaip gali neuþsipildyt.
                    Industries[ i ][ IsBuyingCargo ] = false;
                }
                // Jei jau nebepagamina iki full limito, vël gali pirkti.
                else if(willBeMadeAmount * soldCommodityProduction < limit && !Industries[ i ][ IsBuyingCargo ])
                    Industries[ i ][ IsBuyingCargo ] = true;

                Commodities[ soldCommodityIndex ][ CurrentStock ] += willBeMadeAmount * soldCommodityProduction;
                if(Commodities[ soldCommodityIndex ][ CurrentStock ] > limit)
                    Commodities[ soldCommodityIndex ][ CurrentStock ] = limit;

                for(new k = 0; k < boughtCommodityCount; k++)
                    Commodities[ BoughtCommodities[ k ][ index ] ][ CurrentStock ] -= willBeMadeAmount * BoughtCommodities[ k ][ Consumption ];

                



/*
                if(CanIndustryProduceCargo(i, Commodities[ soldCommodityIndex ][ CargoId ]))            
                {    
                    foreach(CommodityIterator, k)
                    {
                        if(Commodities[ k ][ IndustryId] == Industries[  i ][ Id ] 
                            && Commodities[ k ][ SellBuyStatus ] == Buying
                            && !Commodities[ k ][ IsBusinessCommodity ])
                        {
                            Commodities[ i ][ CurrentStock ] -= GetCargoConsumption(Commodities[ i ][ CargoId ]);
                        }
                    }
                    Commodities[ soldCommodityIndex ][ ]
                }
                */
            }
        }
        /*
		else 
		{
            // Pereinam per visas prekes.
            // Susirandom TOS industrijos PARDUODAMAS prekes. Jas gaminsim.
			foreach(CommodityIterator, j)
			if(Commodities[ j ][ IndustryId ] == Industries[ i ][ Id ]
			&& !Commodities[ j ][ IsBusinessCommodity ]
			&& Commodities[ j ][ SellBuyStatus ] == Selling)
			{
				soldCommodityIndex = j;
                soldCommodityProduction = GetCargoProduction(Commodities[ j ][ CargoId ]);
                limit = GetCargoLimit(Commodities[ j ][ CargoId ]);

                // Iðvis. Jeigu pasiektas limitas, nieko èia nereikia daryt.
                if(Commodities[ soldCommodityIndex ] [ CurrentStock ] >= limit)
                    break;

                // Jop, antras loopas :/
                foreach(CommodityIterator, k)
                if(Commodities[ k ][ IndustryId ] == Industries[ i ][ Id ]
                    && !Commodities[ k ][ IsBusinessCommodity ]
                    && Commodities[ k ][ SellBuyStatus ] == Buying
                    && Commodities[ k ][ CurrentStock ] >= (consumption = GetCargoConsumption(Commodities[ k ][ CargoId ]))) // Jei yra pakankmai tos prekes 
                {

                    // Pridedam parduodamos prekes, atimam sunaudotus resursus.
                    madeamount = Commodities[ k ][ CurrentStock ] / consumption;
                    

                    Commodities[ soldCommodityIndex ][ CurrentStock ] += madeamount * soldCommodityProduction;

                    // Jei virsijo limita reikia sumazint iki limito ir sumazint gaminama kieki.
                    if(Commodities[ soldCommodityIndex ][ CurrentStock ] > limit)
                    {
                        madeamount = Commodities[ soldCommodityIndex ][ CurrentStock ] - limit;
                        Commodities[ soldCommodityIndex ][ CurrentStock ] = limit;
                    }
                    Commodities[ k ][ CurrentStock ] -= madeamount / consumption;
                    if(Commodities[ soldCommodityIndex ] [ CurrentStock ] >= limit)
                        break;
                }
			}
		}
        */
		
		UpdateIndustryInfo(i);// O sita funkcija dar ir trecia loop'a turi :/
		SaveIndustryCommodities(i); // Jei ilgai uztrunka, galima sita eilute uzkomentuot. i think
	}
	printf("IndustryUpdate() uztruko %d MS", GetTickCount() - ticks);
}


FUNKCIJA:MinTime()
{
    new Hour,
        Min,
        Sec;
       // string[ 256 ];
    gettime( Hour, Min, Sec );

    foreach(Player,i)
    {
        if (!IsPlayerLoggedIn(i)) continue;

        //CheckIfAFKing( i );

        pInfo[ i ][ pPayDayHad ] ++;

        
        //if( GetPlayerState(i) == 2 && VehicleHasEngine( GetVehicleModel( GetPlayerVehicleID ( i ) ) ) && Engine[GetPlayerVehicleID ( i )] == true )
        //    SyncFuel( GetPlayerVehicleID ( i ) );
        
        
        new Float:HP;
        GetPlayerHealth( i, HP );

        // Jei nori valgyti jau labai, pradeda silpti jegos
        if(pInfo[ i ][ pHunger ] >= 10 && HP - pInfo[ i ][ pHunger ] / 10.0 > 2.0)
            SetPlayerHealth(i, HP - pInfo[ i ][ pHunger ] / 10.0);

        // Persigalgius svaigsta galva
        if(pInfo[ i ][ pHunger ] <= -5)
            SetPlayerDrunkLevel(i, GetPlayerDrunkLevel(i)+900);
    }

    if ( Hour > OldHour || Hour == 0 && OldHour == 23 )
    {
        printf("Serverio PayDay Valanda: %d Sena Valanda: %d ", Hour, OldHour);

     /*   foreach(Player,playerid)
        {
            PayDay(playerid);
        }
        */
        /*if ( Hour == 20 )
            Loterry( );*/

        OldHour = Hour;
        //SetWorldTime( Hour );
        //SaveMisc();
        //SaveFactions(0);
        Mats += 50; // Mats per valanda
        /*UpdateJacker( 0, random( 2 ) );
        UpdateJacker( 1, random( 2 ) );
        UpdateJacker( 2, random( 2 ) );*/

//        Produkcija( );

        for(new w = 0; w < sizeof DrugMake; w++)
        {
            if( DrugMake[ w ][ dLaikas ] > 0 && !DrugMake[ w ][ dMade ])
            {
                DrugMake[ w ][ dLaikas ] -= 1;
                if( DrugMake[ w ][ dLaikas ] < 1 )
                {
                    DrugMake[ w ][ dLaikas ] = 0;
                    DrugMake[ w ][ dMade ] = true;
                }
            }
        }
        return 1;
    }
    return 1;
}

 PlayerPlayMusic( playerid )
{
    SetTimerEx( "StopMusic", 5000, false, "d", playerid );
    PlayerPlaySound(playerid, 1068, 0.0, 0.0, 0.0);
    return 1;
}

FUNKCIJA:StopMusic( playerid )
    return PlayerPlaySound( playerid, 1069, 0.0, 0.0, 0.0);


FUNKCIJA:Sekunde()
{
    // Jei dingo ryðys su MySQL.
    // mysql_ping sugebëjo crash'inti serverá.
    // 2015.02.20.
    /*#if defined BEBRAS_HOME_MODE
    if(!mysql_ping())
    {
        OnSQLConnectionLost();
    }
    #endif
    */

    foreach(Player,i)
    {
//        UpdateTaxiInformation(i);

        new string[ 256 ];



        if(!IsPlayerLoggedIn(i))
        {
            //SetPVarInt( i, "LOGIN_TIME", GetPVarInt( i, "LOGIN_TIME" ) +1 );
            //if ( GetPVarInt( i, "LOGIN_TIME" ) > 30 )
            //    Kick( i );
            //continue;
        }

      //  new plstate = GetPVarInt( i, "PLAYER_STATE" );
//            IsAfk = GetPVarInt( i, "Is_AFK" );

        /*if ( ObjUpdate[ i ] )
        {
            Streamer_Update( i );
            ObjUpdate[ i ] = false;
        }*/
        /*
        if ( plstate == PLAYER_STATE_DRIVER )
        {
            new
                speed = ac_GetPlayerSpeed( i, true );

            if( speed > AC_MAX_SPEED && !IsAirVehicle( GetVehicleModel( GetPlayerVehicleID( i ) ) ) )
            {
                ac_SpeedWarns[ i ]++;

                if( ac_SpeedWarns[ i ] >= AC_MAX_CHECKS )
                    KickPlayer( "AC", i, "Buvo panaudotas Speed Hack" );
            }
            else
                ac_SpeedWarns[ i ] = 0;
        }
        */
        if ( pInfo[ i ][ pLeftTime ] > 0 )
            pInfo[ i ][ pLeftTime ] --;

        if ( GetPVarInt( i, "Anti_Spam" ) > 0 )
            SetPVarInt( i, "Anti_Spam", GetPVarInt( i, "Anti_Spam" )-1 );

        if ( GetPVarInt( i, "BUNNY" ) == 1 )
            SetPVarInt( i, "BUNNY", 0 );

        new other = GetPVarInt( i, "BACKUP" );
        if ( IsPlayerConnected(other) )
        {
            new Float:POS[ 3 ];
            GetPlayerPos( other, POS[ 0 ], POS[ 1 ], POS[ 2 ] );
            DisablePlayerCheckpoint(i);
            Checkpoint[i] = CHECKPOINT_NONE;
            SetPlayerCheckPointEx( i, CHECKPOINT_BACKUP, POS[ 0 ], POS[ 1 ], POS[ 2 ], 5.0 );
        }

        if(Unfreeze[i] > 0)
        {
            Unfreeze[i] --;
            if(Unfreeze[i] == 0 && CheckUnfreeze(i))
                TogglePlayerControllable(i, true);
        }
        if(BoxStart > 0)
        {
            BoxStart --;
            if(Boxing[i] == true)
            {
                format(string,56,"~w~Kova prasides uz %d sec.", BoxStart);
                GameTextForPlayer(i,string,1000,3);
                if(BoxStart == 0)
                {
                    foreach(Player,p2)
                    {
                        if ( Boxing[ p2 ] )
                        {
                            GameTextForPlayer( p2, "~w~KAUKITES!!!", 5000, 3);
                            TogglePlayerControllable( p2, true);
                        }
                    }
                }
            }
        }
        
        timeris[i]++;
        
        if( GetPlayerState(i) == 2 && Engine[GetPlayerVehicleID ( i )] == true && VehicleHasEngine( GetVehicleModel( GetPlayerVehicleID ( i ) ) ) && timeris[i] > 2 )
        {
            timeris[i] = 0;
            Meter[GetPlayerVehicleID ( i )] += floatround(floatdiv(GetVehicleSpeed2(GetPlayerVehicleID ( i )), 60.0));
            if(Meter[GetPlayerVehicleID ( i )] > 8)
            {
                Meter[GetPlayerVehicleID ( i )] = 0;
                Kils[GetPlayerVehicleID ( i )]++;

                if(Kils[GetPlayerVehicleID ( i )] > 2)
                {
                    Kils[ GetPlayerVehicleID ( i ) ] = 0;
                    cInfo[ GetPlayerVehicleID ( i ) ][ cKM ] += 1.0;
                }
            }
        }
        if(Ruko[i] > 0)
        {
            Ruko[i] --;
            new Float:HP;
            switch (Ruko[i])
            {
                case 0:
                {
                    cmd_ame(i, "numeta cigaretës nuorukà ant þemës.");
                    SetPlayerSpecialAction(i,SPECIAL_ACTION_NONE);
                    GetPlayerHealth(i,HP);
                    if( HP + 5 < 100 )
                        SetPlayerHealth(i,HP+5);
                }
                case 20,40,60,80,100,120,140,160:
                {
                    GetPlayerHealth(i,HP);
                    if(HP + 2 < 100)
                        SetPlayerHealth(i,HP+2);
                }
            }
        }
        if ( GetPVarInt( i, "EATING" ) >= 1 )
        {
            SetPVarInt( i, "EATING", GetPVarInt( i, "EATING" )-1 );
            new Float:HP;
            GetPlayerHealth( i, HP );

            if( HP + 2 < 100 )
                SetPlayerHealth( i, HP +2 );
        }
        SetPVarInt( i, "Is_AFK", 0 );

        static fadeCount[ MAX_PLAYERS ];
        fadeCount[ i ]++;
      /*  if(PlayerFading)
        {
            if((-3 >= pInfo[ i ][ pHunger ] >= -6 && fadeCount[ i ] % 60 == 0)
                || (-7 >= pInfo[ i ][ pHunger ] >= -10 && fadeCount[ i ] % 50 == 0)
                || (-11 >=pInfo[ i ][ pHunger ] >= -14 && fadeCount[ i ] % 40 == 0)
                || (-15 >=pInfo[ i ][ pHunger ] >= -18 && fadeCount[ i ] % 30 == 0)
                || (-21 >=pInfo[ i ][ pHunger ] >= -24 && fadeCount[ i ] % 20 == 0)
                || (-25 >=pInfo[ i ][ pHunger ] >= -28 && fadeCount[ i ] % 10 == 0)
                || (-29 >=pInfo[ i ][ pHunger ])
                )
                FadeColorForPlayer(i, 0, 0, 0, 0, 0, 0, 0, 255, 10, true);
        }
        */
    }
    return 1;
}


FUNKCIJA:CuffsTime(giveplayerid, playerid)
{
    new Float:Kor[4],IntVi[2];
    if(IsPlayerInAnyVehicle(playerid) && GetPlayerState(giveplayerid) == PLAYER_STATE_ONFOOT)
        PutPlayerInVehicle(giveplayerid,GetPlayerVehicleID(playerid),3);
    else if(GetPlayerState(playerid) == PLAYER_STATE_ONFOOT)
    {
        GetPlayerPos(playerid,Kor[0],Kor[1],Kor[2]);
        SetPlayerPos(giveplayerid,Kor[0],Kor[1]+1,Kor[2]);
        IntVi[0] = GetPlayerInterior(playerid);
        SetPlayerInterior(giveplayerid,IntVi[0]);
        IntVi[1] = GetPlayerVirtualWorld(playerid);
        SetPlayerVirtualWorld(giveplayerid,IntVi[1]);
    }
    if(GetPVarInt(giveplayerid, "Drag"))
        SetTimerEx("CuffsTime", 1000, false, "ii",giveplayerid,playerid);
    return 1;
}

 GetPlayerNameEx( playerid )
{
    new str[26];
    GetPlayerName(playerid,str,24);
    for(new i = 0; i < MAX_PLAYER_NAME; i++)
    {
        if (str[i] == '_') str[i] = ' ';
        if(pInfo[playerid][pMask] == 0) format( str, 26, "Kaukëtasis((%d))", pInfo[ playerid ][ pMySQLID ] );
    }
    return str;
}
 strtok(const string[], &index)
{
    new length = strlen(string);
    while ((index < length) && (string[index] <= ' '))
    {
        index++;
    }
    new offset = index;
    new result[20];
    while ((index < length) && (string[index] > ' ') && ((index - offset) < (sizeof(result) - 1)))
    {
        result[index - offset] = string[index];
        index++;
    }
    result[index - offset] = EOS;
    return result;
}




 GetName(playerid)
{
    new name[MAX_PLAYER_NAME];
    GetPlayerName(playerid,name,MAX_PLAYER_NAME);
    return name;
}
 strrest(const string[], index)
{
    new length = strlen(string),
        offset = index,
        result[256];
    while ((index < length) && ((index - offset) < (sizeof(result) - 1)) && (string[index] > '\r'))
    {
        result[index - offset] = string[index];
        index++;
    }
    result[index - offset] = EOS;
    return result;
}

 IsKeyJustDown(key, newkeys, oldkeys) { if((newkeys & key) && !(oldkeys & key)) return 1; return 0; }

 SendChatMessage(playerid,color,text[])
{
    if(strlen(text) > 100)
    {
        new string[ 140 ],
//            message2[ 140 ],
            start = -1, 
            end = -1, 
            colorCount;

        // Suskaièiuojam kiek yra spalvos kodø, nes uþ jø simbolius reikës kompensuot ilgá.
        while((start = strfind(text, "{",.pos = start+1)) != -1 && (end = strfind(text, "}", .pos = end + 1)) != -1)
        {
            if(end - start == 7)
                colorCount++; 
        }

          
		
		strmid(string, text, 0, 81 + colorCount * 8);
		SendClientMessage(playerid, color, string);
		
		strdel(string, 0, strlen(string));
		strmid(string, text, 81 + colorCount * 8, strlen(text));
		SendClientMessage(playerid, color, string);
        return 1;
    }
    else return
        SendClientMessage(playerid,color,text);
}
 SendChatMessageToAll(color,text[])
{
    if(strlen(text) > 100)
    {
        foreach(new i : Player)
            SendChatMessage(i, color, text);
        /*new string[ 140 ],
            message2[ 140 ];

        strmid( message2, text, 0, 81, 82 );
        format( string, 132, "%s ...", message2 );
        SendClientMessageToAll( color, string );

        strmid( string, text, 81, 160, 200 );
        format( string, 132, "... %s", string);
        SendClientMessageToAll( color, string );
        */
        return 1;
    }
    else return
        SendClientMessageToAll(color,text);
}
 split(const strsrc[], strdest[][], delimiter)
{
    new i, li,
        aNum,
        len;
    while(i <= strlen(strsrc))
    {
        if(strsrc[i]==delimiter || i==strlen(strsrc))
        {
            len = strmid(strdest[aNum], strsrc, li, i, 128);
            strdest[aNum][len] = 0;
            li = i+1;
            aNum++;
        }
        i++;
    }
    return 1;
}

 GetClosestVehicleToVehicle(vehicleid, Float:distance)
{
    new  winner = INVALID_VEHICLE_ID, Float:winnerDistance = 99999, Float:x,Float:y, Float:z;
    GetVehiclePos(vehicleid, x, y, z);
    for(new i = 1; i < MAX_VEHICLES; i++)
    {
        if(!IsValidVehicle(i)) continue;
        if(i == vehicleid) continue;
        new Float:tmpDis = GetVehicleDistanceFromPoint(i, x, y, z);
        if(tmpDis < distance && tmpDis < winnerDistance)
        {
            winner = i;
            winnerDistance = tmpDis;
        }
    }
    return winner;
}
 GetNearestVehicle(playerid, Float:distance)
{
    if(IsPlayerInAnyVehicle(playerid))
        return GetPlayerVehicleID(playerid);

    distance = floatabs(distance);
    new Float:X, Float:Y, Float:Z,
        Float:NearestPos = distance,
        NearestVehicle = INVALID_VEHICLE_ID;
    GetPlayerPos(playerid, X, Y, Z);
    foreach(Vehicles,i)
    {
        if(!IsVehicleStreamedIn(i, playerid)) continue;
        if(NearestPos > GetVehicleDistanceFromPoint( i, X, Y, Z ) ) NearestPos = GetVehicleDistanceFromPoint( i, X, Y, Z ), NearestVehicle = i;
    }
    return NearestVehicle;
}
 GetNearestPlayer(playerid, Float:distance)
{
    distance = floatabs(distance);
    new Float:X, Float:Y, Float:Z,
        Float:NearestPos = distance,
        NearestVehicle = INVALID_PLAYER_ID;
    GetPlayerPos(playerid, X, Y, Z);
    foreach(Player,i)
    {
        if ( i == playerid ) continue;
        if ( NearestPos > GetPlayerDistanceFromPoint( i, X, Y, Z ) ) NearestPos = GetPlayerDistanceFromPoint( i, X, Y, Z ), NearestVehicle = i;
    }
    if( NearestVehicle == INVALID_PLAYER_ID ) return INVALID_PLAYER_ID;
    else return NearestVehicle;
}
 LockVehicle(carid,type)
{
    foreach(Player,i)
    {
        SetVehicleParamsForPlayer(carid,i,0,type);
        cInfo[carid][cLock] = type;
    }
    return 1;
}

 SendAdminWarningMessage(const format[], va_args<>)
{
    new str[ 180 ];
    va_format(str, sizeof (str), format, va_start<2>);
    strins(str, "[AdmWarn]", 0);

    foreach(new i : Player)
        if((IsPlayerAdmin(i) || GetPlayerAdminLevel(i)) && TogChat[ i ][ 3 ])
            SendChatMessage(i, COLOR_ADM, str);
    return 1;
}

 SendAdminMessagePlayer( playerid, color, text[ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 1) //&& TogChat[ playerid ][ 3 ] == true )
    {
        if(strlen(text) > 120)
        {
            new string[ 140 ],
                message2[ 140 ];

            strmid(message2, text, 0, 120, 121);
            strcat(message2, " ...");
            SendClientMessage( playerid, color, message2 );

            strmid(string, text, 120, strlen(text));
            strins(string, "... ", 0);
            SendClientMessage( playerid, color, string );
            return 1;
        }
        else 
            return SendClientMessage(playerid,color,text);
        //return SendChatMessage( playerid, color, string );
    }
    return 1;
}


 SendAdminMessage(color, text[])
{
    foreach(Player,i)
    {
        if(GetPlayerAdminLevel(i) >= 1)// && TogChat[i][3] == true)
        {
            SendAdminMessagePlayer(i, color, text);
            //SendChatMessage(i, color, string);
        }
    }
    return 1;
}

 SendRadioMessage(chanel, slot, color, string[])
{
    foreach(Player,i)
    {
        if(chanel == 911 && pInfo[ i ][ pMember ] != 2) continue;
        if(chanel == 912 && pInfo[ i ][ pMember ] != 3) continue;
        if(pInfo[i][pRChannel] == chanel && pInfo[ i ][ pRSlot ] == slot)
            SendChatMessage(i, color, string);
    }
    return 1;
}
 SendJobMessage(job, color, string[])
{
    foreach(Player,i)
    {
        if(IsPlayerConnected(i))
        if(pInfo[i][pJob] == job)
            SendChatMessage(i, color, string);
    }
    return 1;
}
 ProxDetectorCords(Float:radi, string[], Float:pX, Float:pY, Float:pZ, col1, col2, col3, col4, col5, worldid, interiorid )
{
    foreach(Player,player2)
    {
        if ( worldid == GetPlayerVirtualWorld(player2) && interiorid == GetPlayerInterior(player2))
        {
            if ( IsPlayerInRangeOfPoint( player2, radi / 16, pX, pY, pZ ))
                SendChatMessage(player2, col1, string);
            else if ( IsPlayerInRangeOfPoint( player2, radi / 8, pX, pY, pZ ))
                SendChatMessage(player2, col2, string);
            else if ( IsPlayerInRangeOfPoint( player2, radi / 4, pX, pY, pZ ))
                SendChatMessage(player2, col3, string);
            else if ( IsPlayerInRangeOfPoint( player2, radi / 2, pX, pY, pZ ))
                SendChatMessage(player2, col4, string);
            else if ( IsPlayerInRangeOfPoint( player2, radi, pX, pY, pZ ))
                SendChatMessage(player2, col5, string);
        }
    }
    return 1;
}
 ProxDetector(Float:radi, playerid, string[], col1, col2, col3, col4, col5 )
{
    new Float:pX, Float:pY, Float:pZ;
    GetPlayerPos( playerid, pX, pY, pZ );
    foreach(Player,player2)
    {
        if ( GetPlayerVirtualWorld(playerid) == GetPlayerVirtualWorld(player2))
        {
            if ( IsPlayerInRangeOfPoint( player2, radi / 16, pX, pY, pZ ))
                SendChatMessage(player2, col1, string);
            else if ( IsPlayerInRangeOfPoint( player2, radi / 8, pX, pY, pZ ))
                SendChatMessage(player2, col2, string);
            else if ( IsPlayerInRangeOfPoint( player2, radi / 4, pX, pY, pZ ))
                SendChatMessage(player2, col3, string);
            else if ( IsPlayerInRangeOfPoint( player2, radi / 2, pX, pY, pZ ))
                SendChatMessage(player2, col4, string);
            else if ( IsPlayerInRangeOfPoint( player2, radi, pX, pY, pZ ))
                SendChatMessage(player2, col5, string);
        }
    }
    return 1;
}
 ProxDetector2( Float:radi, playerid, string[], col1, col2, col3, col4, col5)
{
    new Float:pX, Float:pY, Float:pZ;
    GetPlayerPos( playerid, pX, pY, pZ );
    foreach(Player,player2)
    {
        if ( GetPlayerVirtualWorld(playerid) == GetPlayerVirtualWorld(player2) && player2 != playerid)
        {
            if ( IsPlayerInRangeOfPoint( player2, radi / 16, pX, pY, pZ ))
                SendChatMessage(player2, col1, string);
            else if ( IsPlayerInRangeOfPoint( player2, radi / 8, pX, pY, pZ ))
                SendChatMessage(player2, col2, string);
            else if ( IsPlayerInRangeOfPoint( player2, radi / 4, pX, pY, pZ ))
                SendChatMessage(player2, col3, string);
            else if ( IsPlayerInRangeOfPoint( player2, radi / 2, pX, pY, pZ ))
                SendChatMessage(player2, col4, string);
            else if ( IsPlayerInRangeOfPoint( player2, radi, pX, pY, pZ ))
                SendChatMessage(player2, col5, string);
        }
    }
    return 1;
}
 isAtFishPlace( playerid )
{
    if ( PlayerToPoint( 10.0, playerid, 838.3501,-2066.7195,12.8672 )   ||
		PlayerToPoint( 10.0, playerid, 852.8965,-2004.0170,13.6268 )   ||
		PlayerToPoint( 10.0, playerid, 820.8741,-1978.4301,12.8672 )   ||
		PlayerToPoint( 20.0, playerid, 360.9109,-2087.1472,7.8359 )   ||
		PlayerToPoint( 20.0, playerid, 366.8915,-2087.8433,7.8359 )   ||
		PlayerToPoint( 20.0, playerid, 371.6957,-2087.9500,7.8359 )   ||
		PlayerToPoint( 20.0, playerid, 377.0125,-2088.0688,7.8359 )   ||
		PlayerToPoint( 20.0, playerid, 382.4442,-2088.1890,7.8359 )   ||
		PlayerToPoint( 20.0, playerid, 387.8258,-2088.3081,7.8359)   ||
		PlayerToPoint( 20.0, playerid, 393.0408,-2088.4241,7.8359)   ||
		PlayerToPoint( 20.0, playerid, 396.7432,-2088.3235,7.8359 )   ||
		PlayerToPoint( 20.0, playerid, 400.9409,-2088.4832,7.8359 )   ||
		PlayerToPoint( 20.0, playerid, 409.2190,-2047.9943,7.8359 )   ||
		PlayerToPoint( 20.0, playerid, 398.3943,-2033.1278,7.8359 )   ||
		PlayerToPoint( 20.0, playerid, 398.7134,-2022.7917,7.8359 )   ||
		PlayerToPoint( 20.0, playerid, 398.6473,-2013.0237,7.8359 )   ||
		PlayerToPoint( 20.0, playerid, 379.5823,-1991.6434,7.8359 )   ||
		PlayerToPoint( 20.0, playerid, 379.5994,-1983.2726,7.8359 )   ||
		PlayerToPoint( 20.0, playerid, 379.2360,-1975.4845,7.8359 )   ||
		PlayerToPoint( 20.0, playerid, 379.6255,-1964.2043,7.8359 )   ||
		PlayerToPoint( 20.0, playerid, 379.5504,-1957.1350,7.8359 )   ||
		PlayerToPoint( 20.0, playerid, 379.6788,-1949.0586,7.8359 )   ||
		PlayerToPoint( 20.0, playerid, 379.7071,-1940.9346,7.8359 )   ||
		PlayerToPoint( 20.0, playerid, 379.7292,-1934.0704,7.8359 )   ||
		PlayerToPoint( 10.0, playerid, 2941.6365,-2052.0046,3.5480 )   ||
		PlayerToPoint( 10.0, playerid, 2937.7944,-2051.4407,3.5480 )   ||
		PlayerToPoint( 10.0, playerid, 2925.5537,-2051.4146,3.5480 )   ||
		PlayerToPoint( 10.0, playerid, 2909.3167,-2070.1389,1.4211 )   ||
		PlayerToPoint( 10.0, playerid, 2908.5703,-2075.4700,1.3767 )   ||
		PlayerToPoint( 10.0, playerid, 2910.2266,-2083.6211,1.3899 )   ||
		PlayerToPoint( 10.0, playerid, 2910.3711,-2094.7788,1.7545 )   ||
		PlayerToPoint( 10.0, playerid, 2908.8552,-2106.5283,2.2764 )   ||
		PlayerToPoint( 10.0, playerid, 2903.4199,-2125.1414,2.8222 )   ||
		PlayerToPoint( 10.0, playerid, 2910.5989,-2040.3712,1.4755 )   ||
		PlayerToPoint( 10.0, playerid, 2910.7673,-2029.8617,1.4876 )   ||
		PlayerToPoint( 10.0, playerid, 2911.0625,-2011.3090,1.4985 )   ||
		PlayerToPoint( 10.0, playerid, 2912.0918,-2000.0720,1.3222 )   
        ) 
        return 1;
    return 0;
}
 IsAtGasStation(playerid)
{
    if(PlayerToPoint(6.0,playerid,1938.5521,-1772.3696,13.3828) ||
    PlayerToPoint(6.0,playerid,1944.7205,-1772.0673,13.3906)   ||
    PlayerToPoint(6.0,playerid,1002.4495,-940.1524,42.1797)     ||
    PlayerToPoint(6.0,playerid,1003.3901,-933.6976,42.1797)  ||
    PlayerToPoint(6.0,playerid,652.6022,-560.3837,16.3359)    ||
    PlayerToPoint(8.0,playerid,653.2297,-568.9595,16.3359)    ||
    PlayerToPoint(5.0,playerid,657.7242,-569.0873,16.3359)  ||
    PlayerToPoint(8.0,playerid,657.7718,-560.6857,16.3359)) return 1;
    return 0;
}
 GetPlayerFirstName(playerid)
{
    new namestring[2][MAX_PLAYER_NAME];
    new name[MAX_PLAYER_NAME];
    GetPlayerName(playerid,name,MAX_PLAYER_NAME);
    split(name, namestring, '_');
    return namestring[0];
}
 GetPlayerLastName(playerid)
{
    new namestring[2][MAX_PLAYER_NAME];
    new name[MAX_PLAYER_NAME];
    GetPlayerName(playerid,name,MAX_PLAYER_NAME);
    split(name, namestring, '_');
    return namestring[1];
}

 FindNPCByName(npcname[])
{
    new name[ MAX_PLAYER_NAME ];
    
    if(isnull(npcname))
        return INVALID_PLAYER_ID;

    foreach(new i : Bot)
    {
        GetPlayerName(i, name, sizeof(name));
        if(!strcmp(name, npcname))
            return i;
    }
    return INVALID_PLAYER_ID;
}
 ClearChatbox(playerid, lines)
{
    for(new i = 0; i < lines; i++)
    SendClientMessage(playerid, COLOR_WHITE, " ");
}
 Susirgti(playerid)
{
    new ligosID = random(sizeof Ligos),
        susirgti = random(100),
        string[128];
    if(ligosID == 0) return 1;
    switch(susirgti)
    {
        case 5,30,45,60,77,99:
        {
            pInfo[playerid][pLiga] = ligosID;
            format(string,sizeof(string),"INFO: Jûs susirgote %s liga, kuo greièiau pavalgykite ir nepraraskite jëgø.",Ligos[ligosID]);
            SendClientMessage(playerid,COLOR_LIGHTRED,string);
            SendClientMessage(playerid,COLOR_LIGHTRED,"INFO: Patariame apsilankyti pas gydytojá , arba pasitarti su vaistininku.");
            return 1;
        }
    }
    return 1;
}

 StartTimer(playerid,ilgis,tipas)
{
    if(Mires[playerid] > 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: ðiuo metu Jûs esate komos bûsenoje.");
    if(Laikas[playerid] == 0)
    {
        Laikas[playerid] = ilgis;
        LaikoTipas[playerid] = tipas;
        TogglePlayerControllable(playerid, false );
        SendClientMessage(playerid,COLOR_WHITE, " Galite sustabdyti pradëta veiskma paraðà /stop.");
 //       UpdatePlayerInfoText( playerid );
        return 1;
    }
    return 1;
}
 TimeEnd(playerid,tipas)
{
    LaikoTipas[playerid] = 0;
    //UpdatePlayerInfoText(playerid,GetPlayerState( playerid ));
    if ( tipas == 99 ) return 1;
    new veh = GetPlayerVehicleID( playerid );
    switch( tipas )
    {
        case 1:
        {
            new Float:HP,
                VD,
                string[ 126 ];
            GetVehicleHealth( veh, HP );

            VD = floatround(HP/100);

            if ( GetPlayerMoney(playerid) < VD )
            {
                format( string, 126, " {FF6347}Perspëjimas: transporto priemonës sutaisymo kaina yra $%d", VD );
                SendClientMessage( playerid, GRAD, string );
                return 1;
            }
            GivePlayerMoney( playerid, -VD );
            AddJobExp( playerid, 5+random(6) );
            SetVehicleHealth(veh,1000);
            RepairVehicle(veh);
            format( string, 126, " Automobilio sutvarkymas jums kainavo: $%d", VD );
            SendClientMessage(playerid, COLOR_WHITE, " Sveikiname, transporto priemonës tvarkymas sëkmingai pavyko.");
            if(CheckUnfreeze(playerid))
                TogglePlayerControllable(playerid,true);
            return true;
        }
        case 5:
        {
            veh = GetPVarInt( playerid, "CAR_JACK" );
            LockVehicle(veh,0);
            new string[54];
            format(string,54,"* %s sulauþo automobilio spinele ir atrakina dureles.",GetPlayerNameEx(playerid));
            ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
            if(CheckUnfreeze(playerid))
            TogglePlayerControllable(playerid,true);
            CJLog( pInfo[ playerid ][ pMySQLID ], cInfo[veh][cID], "Sëkmingai atrakino tr. priemonës dureles" );
            return true;
        }
        case 6:
        {
            new string[126];
            format(string, 126, "* Automobilio variklis uþsivedë.(( %s )).", GetPlayerNameEx( playerid ));
            ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
            StartingEngine[playerid] = false;
            Engine[GetPlayerVehicleID(playerid)] = true;
            VehicleEngine(GetPlayerVehicleID(playerid), 1);
            TogglePlayerControllable(playerid,true);
            CJLog( pInfo[ playerid ][ pMySQLID ], cInfo[GetPlayerVehicleID(playerid)][cID], "Sëkmingai uþvedë tr. priemonës variklá" );
            return true;
        }
        case 10:
        {
            new Float: Car_X,
                Float: Car_Y,
                Float: Car_Z,
                veh2 = GetPVarInt( playerid, "TOWING" );

            GetVehiclePos( veh, Car_X, Car_Y, Car_Z );
            if ( !PlayerToPoint( 10, playerid, Car_X, Car_Y, Car_Z ) )
                return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs nesate ðalia to automobilio ");

            if ( IsTrailerAttachedToVehicle( veh ) )
                DetachTrailerFromVehicle( veh );
            else
                AttachTrailerToVehicle( veh2, veh );
            TogglePlayerControllable( playerid, true );
            return 1;
        }
    }
    return 1;
}

ImpossibleLog(const string[])
{
    new File:lol = fopen("This_Should_Never_Exist.txt", io_append);
    if(lol)
    {
        new year,month,day, hour,minute, second,str[64];
        getdate(year,month,day);
        gettime(hour,minute,second);
        format(str,sizeof(str),"%d.%d.%d %d:%d:%d",
            year,month,day, hour,minute,second);
        fwrite(lol, str);
        fwrite(lol, string);
        fwrite(lol, "\r\n");
    }
    fclose(lol);
}

forward OnPlayerLoadCommodity(playerid, commodity_index, vehicleid);
public OnPlayerLoadCommodity(playerid, commodity_index, vehicleid)
{
	new string[64];
	VehicleLoadTime[ playerid ]--;
	format(string,sizeof(string),"Krovinys bus pakrautas uz %d sekundziu.", VehicleLoadTime[ playerid ] );
	PlayerTextDrawSetString(playerid, InfoText[ playerid ] , string);
	if(VehicleLoadTime[ playerid ] <= 0)
	{
		AddCargoToVehicle(vehicleid, Commodities[ commodity_index ][ CargoId ]);

		// Jei tai buvo priekaba, leidziam kurti vilkika.
		if(IsValidVehicle(GetTrailerPullingVehicle(vehicleid)))
			IsVehicleLoaded[ GetTrailerPullingVehicle(vehicleid) ] = false;

        if(VehicleLoadTime[ playerid ] != 0)
        {
            format(string,sizeof(string),"VehicleLoadTime[%d != 0. Its %d",playerid, VehicleLoadTime[ playerid ]);
            ImpossibleLog(string);
        }

		IsVehicleLoaded[ vehicleid ] = false;
		SendClientMessage(playerid, COLOR_NEWS, "Jûsø krovinys pakrautas!");
		KillTimer(VehicleLoadTimer[ playerid ]);
		VehicleLoadTimer[ playerid ] = -1;
		PlayerTextDrawHide(playerid, InfoText[ playerid ]);
		DeletePVar(playerid, "vehicleid");
	}
    return 1;
}
 divmod( const number, const divider, &div, &mod )
{
    div = floatround( number / divider, floatround_floor );
    mod = number - div * divider;
}
 CheckUnfreeze(playerid)
{
    if(Freezed[playerid] == true || Mires[playerid] > 0)
        return 0;
    else
        return 1;
}

 encode_tires(tires1, tires2, tires3, tires4)
    return tires1 | (tires2 << 1) | (tires3 << 2) | (tires4 << 3);

 CheckBox()
{
    foreach(Player,i)
        if(Boxing[i] == true) return 1;
    return 0;
}
 BoxEnd(loser)
{
    new winer = INVALID_PLAYER_ID;
    foreach(Player,i)
    {
        if(Boxing[i] && i != loser)
        {
            winer = i;
            break;
        }
    }
    if(winer != INVALID_PLAYER_ID)
    {
        new string[126];
        format(string, 126, "[SAN NEWS] Bokso varþybos baigësi, laimëjo %s priëð %s.",GetName(winer),GetName(loser));
//        SendNEWS(COLOR_NEWS,string);
        Boxing[loser] = false;
        Boxing[winer] = false;

        GameTextForPlayer(winer,"~g~ Laimejai kova",3000,3);
        GameTextForPlayer(loser,"~g~ Pralaimejai kova",3000,3);
        SetPlayerHealth(loser, 30);
        SetPlayerHealth(winer, 60);

        //if(pInfo[winer][pJob] == JOB_BOXER)
         //   pInfo[winer][pPayCheck] += 50;
        AddJobExp( winer, 3 );
        SetPlayerPos(winer,768.7744,-66.8329,1001.5692);
        SetPlayerFacingAngle(winer,137.2355);
        SetPlayerPos(loser,764.6347,-70.4305,1001.5692);
        SetPlayerFacingAngle(loser,313.6439);
        ApplyAnimation(winer, "ROB_BANK", "SHP_HandsUp_Scr", 4.0, 0, 1, 1, 1, 0 );
        ApplyAnimation(loser, "CRACK", "crckdeth2", 4.0, 1, 0, 0, 0, 0 );
    }
    Boxing[loser] = false;
    return 1;
}
 PreloadAnimsForPlayer( playerid )
{
    PreloadAnimLib(playerid,"BOMBER");
    PreloadAnimLib(playerid,"RAPPING");
    PreloadAnimLib(playerid,"SHOP");
    PreloadAnimLib(playerid,"BEACH");
    PreloadAnimLib(playerid,"SMOKING");
    PreloadAnimLib(playerid,"FOOD");
    PreloadAnimLib(playerid,"ON_LOOKERS");
    PreloadAnimLib(playerid,"DEALER");
    PreloadAnimLib(playerid,"CRACK");
    PreloadAnimLib(playerid,"CARRY");
    PreloadAnimLib(playerid,"RIOT");
    PreloadAnimLib(playerid,"COP_AMBIENT");
    PreloadAnimLib(playerid,"PARK");
    PreloadAnimLib(playerid,"INT_HOUSE");
    PreloadAnimLib(playerid,"PED");
    PreloadAnimLib(playerid,"MISC");
    PreloadAnimLib(playerid,"OTB");
    PreloadAnimLib(playerid,"BD_Fire");
    PreloadAnimLib(playerid,"BENCHPRESS");
    PreloadAnimLib(playerid,"KISSING");
    PreloadAnimLib(playerid,"BSKTBALL");
    PreloadAnimLib(playerid,"MEDIC");
    PreloadAnimLib(playerid,"SWORD");
    PreloadAnimLib(playerid,"POLICE");
    PreloadAnimLib(playerid,"SUNBATHE");
    PreloadAnimLib(playerid,"FAT");
    PreloadAnimLib(playerid,"WUZI");
    PreloadAnimLib(playerid,"SWEET");
    PreloadAnimLib(playerid,"ROB_BANK");
    PreloadAnimLib(playerid,"GANGS");
    PreloadAnimLib(playerid,"GYMNASIUM");
    PreloadAnimLib(playerid,"CAR");
    PreloadAnimLib(playerid,"CAR_CHAT");
    PreloadAnimLib(playerid,"GRAVEYARD");
    PreloadAnimLib(playerid,"POOL");
    PreloadAnimLib(playerid,"GHANDS");
    PreloadAnimLib( playerid, "BEACH" );
    PreloadAnimLib( playerid, "BOMBER" );
    PreloadAnimLib( playerid, "BASEBALL" );
    PreloadAnimLib( playerid, "BSKTBALL" );
    PreloadAnimLib( playerid, "CARRY" );
    PreloadAnimLib( playerid, "RIOT" );
    PreloadAnimLib( playerid, "COP_AMBIENT" );
    PreloadAnimLib( playerid, "CRACK" );
    PreloadAnimLib( playerid, "DANCING" );
    PreloadAnimLib( playerid, "DEALER" );
    PreloadAnimLib( playerid, "INT_SHOP" );
    PreloadAnimLib( playerid, "GHANDS" );
    PreloadAnimLib( playerid, "GANGS" );
    PreloadAnimLib( playerid, "PED" );
    PreloadAnimLib( playerid, "RAPPING" );
    PreloadAnimLib( playerid, "ROB_BANK" );
    PreloadAnimLib( playerid, "SUNBATHE" );
    PreloadAnimLib( playerid, "SMOKING" );
    PreloadAnimLib( playerid, "SWEET" );
    PreloadAnimLib( playerid, "SPRAYCAN" );
    PreloadAnimLib( playerid, "FOOD" );
    PreloadAnimLib( playerid, "ON_LOOKERS" );
    PreloadAnimLib( playerid, "PARK" );
    PreloadAnimLib( playerid, "KISSING" );
    PreloadAnimLib( playerid, "POLICE" );
    PreloadAnimLib( playerid, "FIGHT_B" );
    PreloadAnimLib( playerid, "FIGHT_C" );
    PreloadAnimLib( playerid, "FIGHT_D" );
    PreloadAnimLib( playerid, "MEDIC" );
    PreloadAnimLib( playerid, "GRAVEYARD" );
    PreloadAnimLib( playerid, "GYMNASIUM" );
    PreloadAnimLib( playerid, "KNIFE" );
    PreloadAnimLib( playerid, "MISC" );
    PreloadAnimLib( playerid, "HEIST9" );
    PreloadAnimLib( playerid, "SHOP" );
    PreloadAnimLib( playerid, "FOOD" );
    PreloadAnimLib( playerid, "SWORD" );
    PreloadAnimLib( playerid, "CAMERA" );
    return 1;
}
 PreloadAnimLib( playerid, animlib[ ] )
    return ApplyAnimation( playerid, animlib, "null", 0.0, 0, 0, 0, 0, 0);

 OnePlayAnim(playerid,animlib[],animname[], Float:Speed, looping, lockx, locky, lockz, lp)
{
    ApplyAnimation(playerid, animlib, animname, Speed, looping, lockx, locky, lockz, lp);
    IsOnePlayAnim[playerid] = true;
    ShowInfoText( playerid, "~w~~h~Spauskite ~r~SPACE~w~~h~ kad sutabdytumet animacija.", 3000 );
}
 BackAnim(playerid,animlib[],animname[], Float:Speed, looping, lockx, locky, lockz, lp,animback)
{
    BackOut[playerid] = animback;
    gPlayerUsingLoopingAnim[playerid] = true;
    ApplyAnimation(playerid, animlib, animname, Speed, looping, lockx, locky, lockz, lp);
    ShowInfoText( playerid, "~w~~h~Spauskite ~r~SPACE~w~~h~ kad sutabdytumet animacija.", 3000 );
}
 LoopingAnim(playerid,animlib[],animname[], Float:Speed, looping, lockx, locky, lockz, lp)
{
    gPlayerUsingLoopingAnim[playerid] = true;
    UsingLoopAnim[ playerid ] = true;
    ApplyAnimation(playerid, animlib, animname, Speed, looping, lockx, locky, lockz, lp, 1);
    ShowInfoText( playerid, "~w~~h~Spauskite ~r~SPACE~w~~h~ kad sutabdytumet animacija.", 3000 );
}

 TuneCarMods(vehicleid)
{
    switch(cInfo[vehicleid][cTuning])
    {
        case MOD_URANUS1:
        {
            AddVehicleComponent(vehicleid,1088);
            AddVehicleComponent(vehicleid,1090);
            AddVehicleComponent(vehicleid,1092);
            AddVehicleComponent(vehicleid,1094);
            AddVehicleComponent(vehicleid,1164);
            AddVehicleComponent(vehicleid,1166);
            AddVehicleComponent(vehicleid,1168);
            return 1;
        }
        case MOD_URANUS2:
        {
            AddVehicleComponent(vehicleid,1089);
            AddVehicleComponent(vehicleid,1091);
            AddVehicleComponent(vehicleid,1093);
            AddVehicleComponent(vehicleid,1095);
            AddVehicleComponent(vehicleid,1163);
            AddVehicleComponent(vehicleid,1165);
            AddVehicleComponent(vehicleid,1167);
            return 1;
        }
        case MOD_JESTER:
        {
            AddVehicleComponent(vehicleid,1065);
            AddVehicleComponent(vehicleid,1067);
            AddVehicleComponent(vehicleid,1069);
            AddVehicleComponent(vehicleid,1071);
            AddVehicleComponent(vehicleid,1160);// spoiler
            AddVehicleComponent(vehicleid,1159);
            AddVehicleComponent(vehicleid,1162);
            return 1;
        }
        case MOD_JESTER2:
        {
            AddVehicleComponent(vehicleid,1066);
            AddVehicleComponent(vehicleid,1067);
            AddVehicleComponent(vehicleid,1070);
            AddVehicleComponent(vehicleid,1072);
            AddVehicleComponent(vehicleid,1158);// spoiler
            AddVehicleComponent(vehicleid,1161);
            AddVehicleComponent(vehicleid,1173);
            return 1;
        }
        case MOD_SULTAN:
        {
            AddVehicleComponent(vehicleid,1026);
            AddVehicleComponent(vehicleid,1028);
            AddVehicleComponent(vehicleid,1032);
            AddVehicleComponent(vehicleid,1027);
            AddVehicleComponent(vehicleid,1138);// spoiler
            AddVehicleComponent(vehicleid,1141);
            AddVehicleComponent(vehicleid,1169);
            return 1;
        }
        case MOD_SULTAN2:
        {
            AddVehicleComponent(vehicleid,1029);
            AddVehicleComponent(vehicleid,1030);
            AddVehicleComponent(vehicleid,1031);
            AddVehicleComponent(vehicleid,1033);
            AddVehicleComponent(vehicleid,1139);// spoiler
            AddVehicleComponent(vehicleid,1140);
            AddVehicleComponent(vehicleid,1170);
            return 1;
        }
        case MOD_STRATUM:
        {
            AddVehicleComponent(vehicleid,1055);
            AddVehicleComponent(vehicleid,1056);
            AddVehicleComponent(vehicleid,1058);
            AddVehicleComponent(vehicleid,1062);
            AddVehicleComponent(vehicleid,1164);// spoiler
            AddVehicleComponent(vehicleid,1154);
            AddVehicleComponent(vehicleid,1155);
            return 1;
        }
        case MOD_STRATUM2:
        {
            AddVehicleComponent(vehicleid,1057);
            AddVehicleComponent(vehicleid,1059);
            AddVehicleComponent(vehicleid,1060);
            AddVehicleComponent(vehicleid,1061);
            AddVehicleComponent(vehicleid,1163);// spoiler
            AddVehicleComponent(vehicleid,1156);
            AddVehicleComponent(vehicleid,1157);
            return 1;
        }
        case MOD_ELEGY:
        {
            AddVehicleComponent(vehicleid,1034);
            AddVehicleComponent(vehicleid,1036);
            AddVehicleComponent(vehicleid,1038);
            AddVehicleComponent(vehicleid,1040);
            AddVehicleComponent(vehicleid,1147);// spoiler
            AddVehicleComponent(vehicleid,1149);
            AddVehicleComponent(vehicleid,1171);
            return 1;
        }
        case MOD_ELEGY2:
        {
            AddVehicleComponent(vehicleid,1035);
            AddVehicleComponent(vehicleid,1037);
            AddVehicleComponent(vehicleid,1039);
            AddVehicleComponent(vehicleid,1042);
            AddVehicleComponent(vehicleid,1146);// spoiler
            AddVehicleComponent(vehicleid,1148);
            AddVehicleComponent(vehicleid,1172);
            return 1;
        }
        case MOD_FLASH:
        {
            AddVehicleComponent(vehicleid,1046);
            AddVehicleComponent(vehicleid,1047);
            AddVehicleComponent(vehicleid,1049);
            AddVehicleComponent(vehicleid,1051);
            AddVehicleComponent(vehicleid,1054);// spoiler
            AddVehicleComponent(vehicleid,1150);
            AddVehicleComponent(vehicleid,1153);
            return 1;
        }
        case MOD_FLASH2:
        {
            AddVehicleComponent(vehicleid,1045);
            AddVehicleComponent(vehicleid,1048);
            AddVehicleComponent(vehicleid,1050);
            AddVehicleComponent(vehicleid,1052);
            AddVehicleComponent(vehicleid,1053);// spoiler
            AddVehicleComponent(vehicleid,1151);
            AddVehicleComponent(vehicleid,1152);
            return 1;
        }
        case MOD_BLADE1:
        {
            AddVehicleComponent(vehicleid,1104);
            AddVehicleComponent(vehicleid,1107);
            AddVehicleComponent(vehicleid,1108);// spoiler
            AddVehicleComponent(vehicleid,1128);
            AddVehicleComponent(vehicleid,1184);
            return 1;
        }
        case MOD_BLADE2:
        {
            AddVehicleComponent(vehicleid,1105);
            AddVehicleComponent(vehicleid,1181);
            AddVehicleComponent(vehicleid,1183);// spoiler
            return 1;
        }
        case MOD_BROADWAY1:
        {
            AddVehicleComponent(vehicleid,1044);
            AddVehicleComponent(vehicleid,1099);
            AddVehicleComponent(vehicleid,1174);//
            AddVehicleComponent(vehicleid,1176);
            AddVehicleComponent(vehicleid,1042);
            return 1;
        }
        case MOD_BROADWAY2:
        {
            AddVehicleComponent(vehicleid,1043);
            AddVehicleComponent(vehicleid,1099);
            AddVehicleComponent(vehicleid,1175);//
            AddVehicleComponent(vehicleid,1177);
            AddVehicleComponent(vehicleid,1043);
            return 1;
        }
        case MOD_REMINGTON1:
        {
            AddVehicleComponent(vehicleid,1100);
            AddVehicleComponent(vehicleid,1101);
            AddVehicleComponent(vehicleid,1122);
            AddVehicleComponent(vehicleid,1126);
            AddVehicleComponent(vehicleid,1179);
            AddVehicleComponent(vehicleid,1180);
            AddVehicleComponent(vehicleid,1123);
            return 1;
        }
        case MOD_REMINGTON2:
        {
            AddVehicleComponent(vehicleid,1125);
            AddVehicleComponent(vehicleid,1124);
            AddVehicleComponent(vehicleid,1106);
            AddVehicleComponent(vehicleid,1127);
            AddVehicleComponent(vehicleid,1178);
            AddVehicleComponent(vehicleid,1185);
            return 1;
        }
        case MOD_SAVANA1:
        {
            AddVehicleComponent(vehicleid,1102);
            AddVehicleComponent(vehicleid,1133);
            AddVehicleComponent(vehicleid,1129);
            AddVehicleComponent(vehicleid,1187);
            AddVehicleComponent(vehicleid,1189);
            return 1;
        }
        case MOD_SAVANA2:
        {
            AddVehicleComponent(vehicleid,1102);
            AddVehicleComponent(vehicleid,1133);
            AddVehicleComponent(vehicleid,1132);
            AddVehicleComponent(vehicleid,1186);
            AddVehicleComponent(vehicleid,1188);
            return 1;
        }
        case MOD_SLAMVAN1:
        {
            AddVehicleComponent(vehicleid,1109);
            AddVehicleComponent(vehicleid,1115);
            AddVehicleComponent(vehicleid,1113);
            AddVehicleComponent(vehicleid,1118);
            AddVehicleComponent(vehicleid,1120);
            AddVehicleComponent(vehicleid,1117);
            return 1;
        }
        case MOD_SLAMVAN2:
        {
            AddVehicleComponent(vehicleid,1110);
            AddVehicleComponent(vehicleid,1116);
            AddVehicleComponent(vehicleid,1114);
            AddVehicleComponent(vehicleid,1119);
            AddVehicleComponent(vehicleid,1121);
            AddVehicleComponent(vehicleid,1117);
            return 1;
        }
        case MOD_TORNADO1:
        {
            AddVehicleComponent(vehicleid,1134);
            AddVehicleComponent(vehicleid,1137);
            AddVehicleComponent(vehicleid,1135);
            AddVehicleComponent(vehicleid,1190);
            AddVehicleComponent(vehicleid,1193);
            return 1;
        }
        case MOD_TORNADO2:
        {
            AddVehicleComponent(vehicleid,1134);
            AddVehicleComponent(vehicleid,1137);
            AddVehicleComponent(vehicleid,1136);
            AddVehicleComponent(vehicleid,1191);
            AddVehicleComponent(vehicleid,1192);
            return 1;
        }
    }
    return 1;
}

 AddTuneToVehicle(vehicleid,type,playerid)
{
    new model = GetVehicleModel(vehicleid);
    switch (model)
    {
        case 558:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Perspëjimas: Jûs neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_URANUS1;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_URANUS2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 559:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Perspëjimas: Jûs neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_JESTER;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_JESTER2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 560:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Perspëjimas: Jûs neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_SULTAN;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_SULTAN2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 561:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Perspëjimas: Jûs neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_STRATUM;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_STRATUM2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 562:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Perspëjimas: Jûs neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_ELEGY;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_ELEGY2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 565:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Perspëjimas: Jûs neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_FLASH;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_FLASH2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 536:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Perspëjimas: Jûs neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_BLADE1;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_BLADE2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 575:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Perspëjimas: Jûs neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_BROADWAY1;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_BROADWAY2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 534:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Perspëjimas: Jûs neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_REMINGTON1;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_REMINGTON2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 567:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Perspëjimas: Jûs neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_SAVANA1;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_SAVANA2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 535:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Perspëjimas: Jûs neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_SLAMVAN1;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_SLAMVAN2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 576:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Perspëjimas: Jûs neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_TORNADO1;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_TORNADO2;
            StartTimer(playerid,180,4);
            return 1;
        }
        default: return SendClientMessage(playerid,GRAD, "{FF6347}Perspëjimas: Jûsø automobilius negali buti tuninguojamas.");
    }
    return 1;
}
 GetCarOwner(vehicleid)
{
    foreach(Player,playerid)
    {
        if ( IsPlayerLoggedIn(playerid) == 0 ) continue;
        if ( pInfo[ playerid ][ pMySQLID ] == cInfo[ vehicleid ][ cOwner ] )
        return playerid;
    }
    return INVALID_PLAYER_ID;
}
 CheckCarKeys(playerid,vehicle)
{
    if ( pInfo[ playerid ][ pMySQLID ] == cInfo[ vehicle ][ cOwner ] ) return 1;
    else if ( pInfo[ playerid ][ pDubKey ] == cInfo[ vehicle ][ cID ] ) return 1;
    else if ( pInfo[ playerid ][ pMember ] == cInfo[ vehicle ][ cFaction ] && cInfo[ vehicle ][ cFaction ] > 0) return 1;
    else return 0;
}
 PlayerToPlayer( Float:radi, playerid, targetid )
{
    new Float:pX,
        Float:pY,
        Float:pZ;
    GetPlayerPos( targetid, pX, pY, pZ );
    return IsPlayerInRangeOfPoint( playerid, radi, pX, pY, pZ );
}
 PlayerToCar( Float:radi, playerid, veh )
{
    new Float:pX,
        Float:pY,
        Float:pZ;
    GetVehiclePos( veh, pX, pY, pZ );
    return IsPlayerInRangeOfPoint( playerid, radi, pX, pY, pZ );
}
 GetOriginType(playerid)
{
    if(!strcmp("Airis",pInfo[playerid][pOrigin],true) ||
       !strcmp("Anglas",pInfo[playerid][pOrigin],true) ||
       !strcmp("Skotas",pInfo[playerid][pOrigin],true) ||
       !strcmp("Kanadietis",pInfo[playerid][pOrigin],true) ||
       !strcmp("Australas",pInfo[playerid][pOrigin],true) ||
       !strcmp("Amerikietis",pInfo[playerid][pOrigin],true))
        return -1;
    else if(!strcmp("Kinas",pInfo[playerid][pOrigin],true))
        return 2;
    else if(!strcmp("Rusas",pInfo[playerid][pOrigin],true))
        return 3;
    else if(!strcmp("Italas",pInfo[playerid][pOrigin],true))
        return 4;
    else if(!strcmp("Ispanas",pInfo[playerid][pOrigin],true))
        return 7;
    else if(!strcmp("Japonas",pInfo[playerid][pOrigin],true))
        return 8;
    else if(!strcmp("Olandas",pInfo[playerid][pOrigin],true))
        return 9;
    else if(!strcmp("Brazilas",pInfo[playerid][pOrigin],true))
        return 10;
    else if(!strcmp("Portugalas",pInfo[playerid][pOrigin],true))
        return 10;
    else if(!strcmp("Kubietis",pInfo[playerid][pOrigin],true))
        return 11;
    else if(!strcmp("Norvegas",pInfo[playerid][pOrigin],true))
        return 12;
    else if(!strcmp("Vokietis",pInfo[playerid][pOrigin],true))
        return 13;
    else if(!strcmp("Prancuzas",pInfo[playerid][pOrigin],true))
        return 15;
    else if(!strcmp("Kolumbietis",pInfo[playerid][pOrigin],true))
        return 16;
    else if(!strcmp("Slovakas",pInfo[playerid][pOrigin],true))
        return 17;
    else if(!strcmp("Graikas",pInfo[playerid][pOrigin],true))
        return 18;
    else if(!strcmp("Baltarusis",pInfo[playerid][pOrigin],true))
        return 19;
    else if(!strcmp("Ukrainietis",pInfo[playerid][pOrigin],true))
        return 20;
    else if(!strcmp("Lietuvis",pInfo[playerid][pOrigin],true))
        return 21;
    else if(!strcmp("Latvis",pInfo[playerid][pOrigin],true))
        return 22;
    else if(!strcmp("Estas",pInfo[playerid][pOrigin],true))
        return 23;
    else if(!strcmp("Lenkas",pInfo[playerid][pOrigin],true))
        return 24;
    else if(!strcmp("Èekas",pInfo[playerid][pOrigin],true))
        return 25;
    else if(!strcmp("Bulgaras",pInfo[playerid][pOrigin],true))
        return 26;
    else if(!strcmp("Arabas",pInfo[playerid][pOrigin],true))
        return 27;
    else if(!strcmp("Suomis",pInfo[playerid][pOrigin],true))
        return 28;
    else if(!strcmp("Portugalas",pInfo[playerid][pOrigin],true))
        return 29;
    else if(!strcmp("Kroatas",pInfo[playerid][pOrigin],true))
        return 30;
    else if(!strcmp("Belgas",pInfo[playerid][pOrigin],true))
        return 31;
    else if(!strcmp("Meksikietis",pInfo[playerid][pOrigin],true))
        return 32;
    else if(!strcmp("Dominikietis",pInfo[playerid][pOrigin],true))
        return 33;
    else if(!strcmp("Indas",pInfo[playerid][pOrigin],true))
        return 34;
    return -1;
}
 SendOrginMessage(playerid,text[])
{
    new string[256],
        tempstr[30];
    switch(GetOriginType(playerid))
    {
        case 2: tempstr = "Kiniëtiðkai";
        case 3: tempstr = "Rusiðkai";
        case 4: tempstr = "Italiðkai";
        case 7: tempstr = "Ispaniðkai";
        case 8: tempstr = "Japoniðkai";
        case 9: tempstr = "Olandiðkai";
        case 10: tempstr = "Portugaliðkai";
        case 11: tempstr = "Kubietiðkai";
        case 12: tempstr = "Norvegiðkai";
        case 13: tempstr = "Vokiðkai";
        case 15: tempstr = "Prancûziðkai";
        case 16: tempstr = "Turkiðkai";
		case 17: tempstr = "Slovakiðkai"; 
		case 18: tempstr = "Graikiðkai";
		case 19: tempstr = "Baltarusiðkai";
		case 20: tempstr = "Ukrainietiðkai";
		case 21: tempstr = "Lietuviðkai";
		case 22: tempstr = "Latviðkai";
		case 23: tempstr = "Estiðkai";
		case 24: tempstr = "Lenkiðkai";
		case 25: tempstr = "Èekiðkai";
		case 26: tempstr = "Bulgariðkai";
		case 27: tempstr = "Arabiðkai";
		case 28: tempstr = "Suomiðkaii";
		case 29: tempstr = "Portugaliðkai";
		case 30: tempstr = "Kroatiðkai";
		case 31: tempstr = "Belgiðkai";
		case 32: tempstr = "Meksikietiðkai";
		case 33: tempstr = "Domininkietiðkai";
		case 34: tempstr = "Indiiðkai";		
    }
    new virt = GetPlayerVirtualWorld( playerid );
    new intt = GetPlayerInterior( playerid );
    foreach(Player,i)
    {
        if(GetOriginType(i) == GetOriginType(playerid))
            format(string,256,"%s sako %s: %s",GetPlayerNameEx(playerid),tempstr,text);
        else if(GetOriginType(playerid) == -1)
            format(string,256,"%s sako: %s",GetPlayerNameEx(playerid),text);
        else
            format(string,256,"%s sako %s: Neþinoma kalba",GetPlayerNameEx(playerid),tempstr);
        if(PlayerToPlayer( 11, playerid, i ) && virt == GetPlayerVirtualWorld( playerid ) && intt == GetPlayerInterior( playerid ))
            SendChatMessage(i,COLOR_FADE1,string);
    }
}

 VehicleHasWindows( model )
{
    if ( IsVehicleBike( model ) || !VehicleHasEngine( model ) )
        return false;
    switch ( model )
    {
        case 424, 429, 430, 432, 439, 446, 452, 453, 454, 457, 471, 472, 473,
             480, 484, 485, 486, 493, 530, 531, 533, 536, 539, 555, 567, 568,
             571, 572, 575, 595:
        return false;
    }
    return true;
}

stock
    IsAirVehicle( model )
{
    switch ( model )
    {
        case 592, 577, 511, 512, 593, 520, 553, 476, 519, 460, 513, 548, 425, 417, 487, 488, 497, 563, 447, 467: return true;
    }

    return false;
}

 IsVehicleBike( model )
{
    switch ( model )
    {
        case 581, 462, 521, 463, 522, 461, 448, 471, 468, 481, 523, 586, 509, 510:
        return true;
    }
    return false;
}

 IsVehicleTaxed(model)
{
    if(IsVehicleBike(model))
        return false;
    else 
        return true;
}

 GetTrailerPullingVehicle(vehicleid)
{
    if(!IsValidVehicle(vehicleid))  
        return INVALID_VEHICLE_ID;
  
    for(new i = 1; i < MAX_VEHICLES; i++)
    {
        if(!IsValidVehicle(i))
            continue;
        if(GetVehicleTrailer(i) == vehicleid)
            return i;
    }
    return INVALID_VEHICLE_ID;
}
 IsVehicleTrailer( model )
{
    switch ( model )
    {
        case 435, 450, 584, 591:
        return true;
    }
    return false;
}

 IsPlayerAtFishBoat ( playerid )
{
    new idcar = INVALID_VEHICLE_ID;
    if(GetPlayerState(playerid) == PLAYER_STATE_DRIVER)
        idcar = GetPlayerVehicleID( playerid );
    else
        idcar = GetNearestVehicle( playerid, 10.0 );
    if ( idcar == INVALID_VEHICLE_ID )
        return INVALID_VEHICLE_ID;
    switch ( GetVehicleModel( idcar ) )
    {
        case 473, 453, 454, 493, 484:
        return idcar;
    }
    return INVALID_VEHICLE_ID;
}

 IsVehicleTrucker( model )
{
    switch ( model )
    {
        case 530, 600, 543, 605, 422, 478, 554, 413, 459, 482, 440, 498, 609, 499, 414, 456, 455, 578, 443, 428, 403, 514, 515:
        return true;
    }
    return false;
}
 IsWeaponHasAmmo( model )
{
    switch ( model )
    {
        case 0..15,19..21,44..47:
        return false;
    }
    return true;
}
 VehicleHasEngine( model )
{
    switch ( model )
    {
        case 509, 481, 510:
        return false;
    }
    return true;
}
 IsAPlane( model )
{
    switch ( model )
    {
        case 592, 577, 511, 512, 593, 520, 553, 476, 519, 460, 513, 548, 425,
             417, 487, 488, 497, 563, 447, 469:
        return true;
    }
    return false;
}
 IsABoat( model )
{
    switch ( model )
    {
        case 472, 473, 493, 595, 484, 430, 453, 452, 446, 454:
            return true;
    }
    return false;
}
 GetJobName(jobid)
{
    new txt[ 56 ];
    format( txt, 56, "%s", pJobs[ jobid ][ Name ] );
    return txt;
}

 ShowPlayerInfoText( playerid )
{
    printf("ltrp.pwn ShowPlayerInfoText(%d) called", playerid);
    //PlayerTextDrawShow( playerid, Greitis[ playerid ] );
    return 1;
}
 HidePlayerInfoText( playerid )
{
    PlayerTextDrawHide( playerid, Greitis[ playerid ] );
    return 1;
}
 ShowInfoText( playerid, text[ ], time)
{
    printf("ltrp.pwn ShowInfoText(%d, %s, %d) called", playerid, text, time);
   // PlayerTextDrawShow     ( playerid, InfoText[ playerid ] );
    PlayerTextDrawSetString( playerid, InfoText[ playerid ], text );
    if(InfoTextTimer[ playerid ] != -1)
        KillTimer(InfoTextTimer[ playerid ]);
    InfoTextTimer[ playerid ] = SetTimerEx             ( "HideInfoText", time, false, "d", playerid );
    return 1;
}

FUNKCIJA:HideInfoText( playerid )
{
    PlayerTextDrawHide( playerid, InfoText[ playerid ] );
    InfoTextTimer[ playerid ] = -1;
    return 1;
}

 NearPhone( playerid )
{
    if( GetPVarInt ( playerid, "NearPhone" ) )
        return 0;
    if ( PlayerToPoint( 5.0, playerid, 281.7844,1000.8417,2119.1150 ) ||
         PlayerToPoint( 5.0, playerid, 281.7772,1003.5189,2119.1150 ) ||
         PlayerToPoint( 5.0, playerid, 281.7768,1006.3833,2119.1150 ) ||
         PlayerToPoint( 5.0, playerid, 44.4012,1219.5900,19.0292 ) ||
		 PlayerToPoint( 5.0, playerid, 2069.4824,-1767.1677,13.5625) ||
		 PlayerToPoint( 5.0, playerid, 2069.4824,-1767.1677,13.5625) ||
	  	 PlayerToPoint( 5.0, playerid, 1809.4171,-1598.1709,13.5469) ||
		 PlayerToPoint( 5.0, playerid, 1807.5339,-1599.3356,13.5469) ||
	 	 PlayerToPoint( 5.0, playerid, 1805.8998,-1600.7174,13.5469) ||
	   	 PlayerToPoint( 5.0, playerid, 1711.2419,-1605.1455,13.5469) ||
		 PlayerToPoint( 5.0, playerid, 1542.4297,-1684.7871,13.5545) ||
		 PlayerToPoint( 5.0, playerid, 1522.2264,-1830.7876,13.5469) ||
		 PlayerToPoint( 5.0, playerid, 2166.6946,-1155.4084,24.8679) ||
		 PlayerToPoint( 5.0, playerid, 1771.3378,-1543.3586,9.4434 ) ||		
         IsPlayerInRangeOfPoint(playerid, 5.0, 378.5848,-1717.8740,23.2230) ||
         PlayerToPoint( 5.0, playerid, -22.9650,1075.2723,19.7422 ) )
         return 1;
    else return 0;
}
 NearBankomat( playerid )
{
    if ( PlayerToPoint( 5.0, playerid, -796.7485400,1501.4931600,21.5664000 ) ||
         PlayerToPoint( 5.0, playerid, -638.9351800,1444.8444800,12.6069600 ) ||
         PlayerToPoint( 5.0, playerid, -181.5487,1027.2559,19.7344 ) ||
		 PlayerToPoint( 5.0, playerid, 2068.71, -1769.29, 13.20 ) ||
         PlayerToPoint( 5.0, playerid, 2139.42, -1163.94, 23.62 ) ||
         PlayerToPoint( 5.0, playerid, 2405.95, -1548.69, 23.81 ) ||		 
         PlayerToPoint( 5.0, playerid, -96.5025,1189.8512,19.7422 ) )
         return 1;
    else return 0;
}

 SetPlayerCheckPointEx( playerid, type, Float:x, Float:y, Float:z, Float:a )
{
    if( Checkpoint[ playerid ] == CHECKPOINT_NONE )
    {
        SetPVarFloat(playerid, "CPx", x);
        SetPVarFloat(playerid, "CPy", y);
        SetPVarFloat(playerid, "CPz", z);
        SetPlayerCheckpoint( playerid, x, y, z, a);
        Checkpoint[ playerid ] = type;
    }
    return 1;
}
 SetPlayerRaceCheckPointEx( playerid, cptype, type, Float:x, Float:y, Float:z, Float:nextx, Float:nexty, Float:nextz, Float:size )
{
    SetPlayerRaceCheckpoint( playerid, type, x, y, z, nextx, nexty, nextz, size );
    Checkpoint[ playerid ] = cptype;
    return 1;
}


 LoadIndustries()
{
    new industryCount;
    new Cache:result = mysql_query(DbHandle, "SELECT * FROM industries");
    for(new i = 0; i < cache_get_row_count(); i++)
    {
        Industries[ industryCount ][ Id ] = cache_get_field_content_int(i, "id");
        cache_get_field_content(i, "name",  Industries[ industryCount ][ Name ], DbHandle, MAX_INDUSTRY_NAME);
        Industries[ industryCount ][ PosX ] = cache_get_field_content_float(i, "x");
        Industries[ industryCount ][ PosY ] = cache_get_field_content_float(i, "y");
        Industries[ industryCount ][ PosZ ] = cache_get_field_content_float(i, "z");

        Itter_Add(IndustryIterator, industryCount);
        Industries[ industryCount ][ IsBuyingCargo ] = true;
        industryCount++;
    }
    cache_delete(result);
    printf("Serveryje yra sukurtos %d kompanijos.",industryCount);

    // Tuo paèiu ir Laivo objektus sukuriam.
    ShipInfo[ ObjectIDs ][ 0 ] =  CreateDynamicObject(5160, 2829.95313, -2479.57031, 5.26560,   0.00000, 0.00000, 270.00000);
    ShipInfo[ ObjectIDs ][ 1 ] =  CreateDynamicObject(5156, 2838.03906, -2423.88281, 10.96090,   0.00000, 0.00000, 270.00000);
    ShipInfo[ ObjectIDs ][ 2 ] =  CreateDynamicObject(3724, 2838.19067, -2407.12109, 29.31250,   0.00000, 0.00000, 270.00000);
    ShipInfo[ ObjectIDs ][ 3 ] =  CreateDynamicObject(5167, 2838.03125, -2371.95313, 7.29690,   0.00000, 0.00000, 270.00000);
    ShipInfo[ ObjectIDs ][ 4 ] =  CreateDynamicObject(5166, 2829.95313, -2479.57031, 5.26560,   0.00000, 0.00000, 270.00000);
    ShipInfo[ ObjectIDs ][ 5 ] =  CreateDynamicObject(3724, 2838.21411, -2489.00000, 29.31250,   0.00000, 0.00000, 90.00000);
    ShipInfo[ ObjectIDs ][ 6 ] =  CreateDynamicObject(5155, 2838.02344, -2358.47656, 21.31250,   0.00000, 0.00000, 270.00000);
    ShipInfo[ ObjectIDs ][ 7 ] =  CreateDynamicObject(5158, 2837.77344, -2334.47656, 11.99220,   0.00000, 0.00000, 0.00000);
    ShipInfo[ ObjectIDs ][ 8 ] =  CreateDynamicObject(5154, 2838.14063, -2447.84375, 15.75000,   0.00000, 0.00000, 270.00000);
    ShipInfo[ ObjectIDs ][ 9 ] =  CreateDynamicObject(5157, 2838.03906, -2532.77344, 17.02340,   0.00000, 0.00000, 270.00000);
    ShipInfo[ ObjectIDs ][ 10 ] =  CreateDynamicObject(5165, 2838.03125, -2520.18750, 18.41406,   0.00000, 0.00000, 0.00000);
}


 UpdateIndustryInfo(index)
{
    if(IsValidDynamic3DTextLabel(Industries[ index ][ Label ]))
        DestroyDynamic3DTextLabel(Industries [ index ][ Label ]);
	
	if(!IsValidDynamicPickup(Industries [ index ][ Pickup ]))
		Industries[ index ][ Pickup ] = CreateDynamicPickup(1318, 1, Industries[ index ][ PosX ], Industries[ index ][ PosY ], Industries [ index ][ PosZ ]);
	
    new string[512];
    format(string,sizeof(string),"{CC0000}%s\n\n", Industries[ index ][ Name ]);
    foreach(CommodityIterator, i)
    {
        if(Commodities[ i ][ IndustryId ] == Industries [ index ][ Id ]
            && !Commodities[ i ][ IsBusinessCommodity ])
		{
			new E_COMMODITY_SELL_BUY_STATUS:isBuying  = Commodities[ i ][ SellBuyStatus ];
            format(string, sizeof(string),"%s\n\n{00CC00}[%s]{FFFFFF}%s\nKaina %d / vieneta\nSandelyje yra: %d / %d\n",
                string, 
				(isBuying == Buying) ? ("Perka") : ("Parduoda"),
                GetCargoName(Commodities [ i ][ CargoId ]), 
				Commodities[ i ][ Price ],
				Commodities[ i ][ CurrentStock ],
				GetCargoLimit(Commodities [ i ][ CargoId ] ));
		}
	}
    Industries[ index ][ Label ] = CreateDynamic3DTextLabel(string, COLOR_WHITE, Industries[ index ][ PosX ], Industries[ index ][ PosY ], Industries[ index ][ PosZ ], 40.0);
    return 1;
}

 LoadTruckerCargo()
{
    new cargoCount = 0;
    new Cache:result = mysql_query(DbHandle, "SELECT * FROM trucker_cargo");
    for(new i = 0; i < cache_get_row_count(); i++)
    {
        if(cargoCount >= sizeof TruckerCargo)
        {
            printf("KLAIDA. Lenteleje 'trucker_cargo' yra daugiau prekiu(%d) nei leidziama(%d)", cache_get_row_count(), sizeof TruckerCargo);
            break;
        }
        TruckerCargo[ cargoCount ][ Id ] = cache_get_field_content_int(i, "id");
        cache_get_field_content(i, "name", TruckerCargo[ cargoCount ][ Name ], DbHandle, MAX_TRUCKER_CARGO_NAME);
        TruckerCargo[ cargoCount ][ Limit ] = cache_get_field_content_int(i, "limit");
        TruckerCargo[ cargoCount ][ Production ] = cache_get_field_content_int(i, "production");
        TruckerCargo[ cargoCount ][ Consumption ] = cache_get_field_content_int(i, "consumption");
        TruckerCargo[ cargoCount ][ Slot ] = cache_get_field_content_int(i, "slot");
        TruckerCargo[ cargoCount ][ Type ] = cache_get_field_content_int(i, "type");
        Itter_Add(TruckerCargoIterator, cargoCount);
        cargoCount++;
    }
    cache_delete(result);
    printf("Serveryje yra %d furistu kroviniai.",cargoCount);
}
 LoadCommodities()
{
    new commodityCount,
        industryId, cargoId, buysellstatus[8],curentStock, type[9], price,
        Cache:result;
    result = mysql_query(DbHandle, "SELECT * FROM commodities");
    for(new i = 0; i < cache_get_row_count(); i++)
    {
        if(commodityCount >= sizeof Commodities)
        {
            printf("KLAIDA. Lenteleje 'commodities' yra daugiau prekiu(%d) nei leidziama(%d)", cache_get_row_count(), sizeof Commodities);
            break;
        }
        industryId = cache_get_field_content_int(i, "industry_id");
        cargoId = cache_get_field_content_int(i, "cargo_id");
        cache_get_field_content(i, "sell_buy_status", buysellstatus);
        curentStock = cache_get_field_content_int(i, "current_stock");
        cache_get_field_content(i, "type", type);
        price = cache_get_field_content_int(i, "price");

        Commodities[ commodityCount ][ IndustryId ] = industryId;
        Commodities[ commodityCount ][ CargoId ] = cargoId;
        Commodities[ commodityCount ][ CurrentStock ] = curentStock;
        Commodities[ commodityCount ][ SellBuyStatus ] = (!strcmp(buysellstatus,"Buying")) ? (Buying) : (Selling);
        Commodities[ commodityCount ][ Price ] = price;
        if(!strcmp(type, "Business",true))
            Commodities[ commodityCount ][ IsBusinessCommodity ] = true;
        Itter_Add(CommodityIterator, commodityCount);
        commodityCount++;
    }
	cache_delete(result);
    printf("Serveryje yra %d parduodamu/perkamu prekiu.",commodityCount);
}



 nullVehicle( vehicleid )
{
    Engine[ vehicleid ] = false;
    VGaraze[ vehicleid ] = false;
//    VehicleFish[ vehicleid ] = 0;

    if( IsValidDynamic3DTextLabel( Units [ vehicleid ] ) )
        DestroyDynamic3DTextLabel( Units [ vehicleid ] );

    strmid( cInfo[ vehicleid ][ cName    ], "None" , 0, 24, 24 );
    strmid( cInfo[ vehicleid ][ cNumbers ], "None" , 0, 24, 24 );

    cInfo[ vehicleid ][ cOwner     ] = 0;
    cInfo[ vehicleid ][ cID        ] = 0;
    cInfo[ vehicleid ][ cModel     ] = 0;
    cInfo[ vehicleid ][ cSpawn     ][ 0 ] = 0;
    cInfo[ vehicleid ][ cSpawn     ][ 1 ] = 0;
    cInfo[ vehicleid ][ cSpawn     ][ 2 ] = 0;
    cInfo[ vehicleid ][ cSpawn     ][ 3 ] = 0;
    cInfo[ vehicleid ][ cColor     ][ 0 ] = 0;
    cInfo[ vehicleid ][ cColor     ][ 1 ] = 0;
    cInfo[ vehicleid ][ cLock      ] = 0;
    cInfo[ vehicleid ][ cFuel      ] = 0;
    cInfo[ vehicleid ][ cFaction   ] = 0;
    cInfo[ vehicleid ][ cWheels    ] = 0;
    cInfo[ vehicleid ][ cTuning    ] = 0;
    cInfo[ vehicleid ][ cInsurance ] = 0;
    cInfo[ vehicleid ][ cDuzimai   ] = 0;
    cInfo[ vehicleid ][ cLockType  ] = 0;
    cInfo[ vehicleid ][ cAlarm     ] = 0;
    cInfo[ vehicleid ][ cTicket    ] = 0;
    cInfo[ vehicleid ][ cHidraulik ] = 0;
    cInfo[ vehicleid ][ cDub       ] = 0;
    cInfo[ vehicleid ][ cKM        ] = 0.0;
    cInfo[ vehicleid ][ cVirtWorld ] = 0;
    for( new slot = 0; slot < MAX_TRUNK_SLOTS; slot ++)
    {
        cInfo[ vehicleid ][ cTrunkWeapon ][ slot ] = 0;
        cInfo[ vehicleid ][ cTrunkAmmo   ][ slot ] = 0;
        cInfo[ vehicleid ][ cTrunkItemDurability ][ slot ] = 0;
        cInfo[ vehicleid ][ cTrunkItemContent ][ slot ] = 0;
    } 

    for(new i = 0; i < MAX_TRUCKER_CARGO_OBJECTS; i++)
    {
        if(cInfo[ vehicleid ][ objectai ][ i ] != -1)
            DestroyObject(cInfo[ vehicleid ][ objectai ][ i ]);
        cInfo[ vehicleid ][ objectai ][ i ] = -1;
    }

    /* Krovinii */
    for(new i = 0; i <  sizeof VehicleCargo[]; i++)
    {
        VehicleCargo[ vehicleid ][ i ][ CargoId ] = 0;
        VehicleCargo[ vehicleid ][ i ][ Amount ] = 0;
    }
    return 1;
}
 VehicleEngine(vehicleid, param)
{
    new engine, lights, alarm, doors, bonnet, boot, objective;

    GetVehicleParamsEx(vehicleid, engine, lights, alarm, doors, bonnet, boot, objective);
    SetVehicleParamsEx(vehicleid, param, lights, alarm, doors, bonnet, boot, objective);
}
 VehicleAlarm(vehicleid, param)
{
    new engine, lights, alarm, doors, bonnet, boot, objective;
    GetVehicleParamsEx(vehicleid, engine, lights, alarm, doors, bonnet, boot, objective);
    SetVehicleParamsEx(vehicleid, engine, lights, param, doors, bonnet, boot, objective);
}
 GetVehicleName( model )
{
    new str [ 24 ];
    format( str, 24, "%s", aVehicleNames[ model - 400 ] ) ;
    return str;
}
 GetVehicleFuelTank( model )
    return vBakas[ model - 400 ];

 GetVehicleTrunkSlots( model )
    return vSlotai[ model - 400 ];


FUNKCIJA:KicknPlayer( playerid )
{
    Kick( playerid );
    return true;
}

#define INVALID_3D_TEXT Text3D:-1
CMD:ame( playerid, params[ ] )
{
    new
        result[ 128 ];

    if ( sscanf( params, "s[128]", result ) ) return SendClientMessage( playerid, COLOR_RED, "/ame [veiksmas]" );
    else if ( strlen ( result ) > 120 ) SendClientMessage( playerid, COLOR_GREY, "Nedaugiau 120 simboliø." );
    else
    {
        format(szMessage, sizeof(szMessage), "%s %s", GetPlayerNameEx( playerid), params);
        SetPlayerChatBubble( playerid, szMessage, COLOR_PURPLE, 20, 10000 );
        format( szMessage, sizeof(szMessage),"> %s", szMessage );
        SendClientMessage( playerid, COLOR_PURPLE, szMessage );
//        if ( pInfo[ playerid ][ pJob ] == JOB_JACKER && ( LaikoTipas[playerid] == 5 || LaikoTipas[playerid] == 6 ) )
 //           ActionLog( pInfo[ playerid ][ pMySQLID ], szMessage );
    }

    return true;
}

 TeleportPlayerToCoord( playerid, Float:x, Float:y, Float:z )
{
    // Funkcija: TeleportPlayerToCoord( playerid, Float:x, Float:y, Float:z )
    // Nuteleportuos þaidëjá  á tam tikrá  koordinatà

    if ( GetPlayerState( playerid ) == PLAYER_STATE_DRIVER )
        SetVehiclePos( GetPlayerVehicleID( playerid ), x, y, z );
    else
        SetPlayerPos( playerid, x, y, z );
}

CMD:badge( playerid, params[ ] )
{
    #pragma unused params
    if (pInfo[playerid][pMember] == 2)
    {
            SetPlayerAttachedObject(playerid, 6,19347,16,0.071999,-0.112999,0.036999,115.699981,-2.099976,-36.599925);
            EditAttachedObject(playerid, 6);
            SendClientMessage( playerid, COLOR_POLICE,"[LSPD] Dabar nusistatykite norimà pozicijà þenkleliui," );
            SendClientMessage( playerid, COLOR_RED,"[LSPD] Norëdami paðalinti þenklelá naudokita komandà: /rbadge." );
    }
    else
        SendClientMessage( playerid, COLOR_RED,"Jûs neesate policininkas.");
    return true;

}

CMD:rbadge( playerid, params[] )
{
    #pragma unused params
    if (pInfo[playerid][pMember] == 2)
    {
        SendClientMessage( playerid, COLOR_POLICE,"[LSPD] Þenklelis buvo paðalintas");
        RemovePlayerAttachedObject( playerid, 6 );
    }
    else
        SendClientMessage( playerid, COLOR_LIGHTRED,"Klaida, negalite naudoti komandos nebûdami pareigûnø.");
    return true;

}

/*
CMD:vest( playerid, params[ ] )
{
    #pragma unused params
    if ( UsePDCMD( playerid ) == 0 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti ðio veiksmo nedirbdami policijos departamente." );
    if(!PDJOBPlace(playerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite persirengti nebødamas persirengimo kabinoje/kambaryje.");
    new
        Float:armour;
    GetPlayerArmour( playerid, armour );
    if( armour > 0 )
    {
        SendClientMessage( playerid, COLOR_POLICE,"[LSPD] Neperðaunama liemenë buvo nuimta." );
        SetPlayerArmour( playerid, 0 );
        RemovePlayerAttachedObject( playerid, 5 );
    }
    else
    {
        SendClientMessage( playerid, COLOR_POLICE,"[LSPD] Neperðaunama liemenë buvo uþdëta." );
        SetPlayerArmour( playerid, 100 );
        SetPlayerAttachedObject( playerid, 5, 19142, 1, 0.1,  0.05, 0.0,  0.0, 0.0, 0.0 );
        EditAttachedObject( playerid, 5 );
    }
    return 1;
}
*/
/*
CMD:blindfold(playerid, params[])
{
    new targetid,
        string[ 128 ],
        invindex = -1;

    for(new i = 0; i < GetPlayerItemCount(playerid); i++)
        if(IsItemBlindfold(GetPlayerItemAtIndex(playerid, i)) && !IsPlayerWearingItem(playerid, GetPlayerItemAtIndex(playerid, i)))
        {
            invindex = i;
            break;
        }

    if(invindex == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neturite skarelës arba jà naudojate pats.");

    if(GetPlayerSpecialAction(playerid) == SPECIAL_ACTION_CUFFED)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Niekam negalite atriðti/uþriðti raiðèio kai jûsø rankos surakintos.");

    if(sscanf(params, "u", targetid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas /blindfold [Þaidëjo ID/Dalis vardo]");

    if(!IsPlayerConnected(targetid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Tokio þaidëjo nëra!");

    if(!IsPlayerInRangeOfPlayer(playerid, targetid, 5.0))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Þaidëjas yra per toli.");


    if(IsBlindfolded[ targetid ])
    {
        format(string, sizeof(string), "atriða akiø raiðtá nuo %s veido", GetPlayerNameEx(targetid));
        cmd_me(playerid, string);
        TextDrawHideForPlayer(targetid, BlindfoldTextdraw);
        SetCameraBehindPlayer(targetid);
        IsBlindfolded[ targetid ] = false;
    }
    else 
    {
        format(string, sizeof(string), "%s nori jums ant akiø uþriðti raiðá. Per já nieko nematysite. Raðykite /accept blindfold %d", GetPlayerNameEx(playerid), playerid);
        SendClientMessage(targetid, COLOR_NEWS, string);
        format(string, sizeof(string), "Veikëjas %s gavo praðymà leisti uþriðti jam raiðtá ant akiø, palaukite kol veikëjas atsakys.", GetPlayerNameEx(targetid));
        SendClientMessage(playerid, COLOR_NEWS, string);

        Offer[ targetid ][ 8 ] = playerid;
    }
    return 1;
}*/

public OnQueryError(errorid, error[], callback[], query[], connectionHandle)
{
    new hour, minute, second, 
        File:file = fopen("mysql_errlog.txt", io_append),
        string[64];
    gettime(hour, minute, second);
    if(!file)
        return 0;

    format(string, sizeof(string),"[%d.%d.%d]", hour, minute ,second);
    fwrite(file, string);
    format(string, sizeof(string),"Error id: %d. ",errorid);
    fwrite(file, string);
    fwrite(file, "Error: ");
    fwrite(file, error);
    fwrite(file, ". Query: ");
    fwrite(file, query);
    fwrite(file, "\r\n");
    fclose(file);
    return 1;
}

forward OnSQLConnectionLost();
public OnSQLConnectionLost()
{
    printf("SQL connection lost");
    new string[40];
    SendAdminMessage(0xFF0000FF, "Dëmesio. Dingo ryðys su duomenø baze. Jei ðios þinutës tæsis, imkitës veiksmø.");
    format(string, sizeof(string),"Bandoma prisijungti vël: %d", mysql_reconnect());
    SendAdminMessage(0xFF0000FF, string);
}

 IsNumeric(const string[]) 
{
    new length = strlen(string);
    if(length == 0)
    {
        return false;
    }
    for(new i = 0; i < length; i++)
    {
        if(string[ i ] > '9' || string[ i ] < '0')
        {
            return false;
        }
    }
    return true;
}