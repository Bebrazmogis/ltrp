/*
    ---------------------------------------------------------------------------
     Lithuanian Role Play - 2015 m.
    ---------------------------------------------------------------------------
    
    Gamemode sukurtas 2009m.
    Gamemode kur�jas: Gedaas (Warrior).
    Legalus gamemode savininkas: Nova.
    
    ---------------------------------------------------------------------------
     
    This script is copyrighted to Gedas, Nova.
    It may be edited, hosted, renamed, redistributed, etc. as long as credit is given to him.
     
    ---------------------------------------------------------------------------
    
    Skripto autorin�s teis�s priklauso kur�jui Gedas, Nova.
    Skriptas gali b�ti redaguojamas, naudojamas ar kitaip kei�iamas jei tik nebus pa�eid�iamos �ios teis�s.


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
//#include <timerfix>
//#include <a_http>
#include <foreach>
#include <a_mysql>
//#include <audio>
#include <sscanf2> 
#include <streamer>
//#include <lookup>
#include <zcmd>
//#include <airbreak>
//#include <GameTextS7.inc>
//#include <fader>
//#include <OnPlayerFirstSpawn>
//#include <mysql_pause_player>
#include <mapandreas>
#include <Sheobill>
#include <crashdetect>
#include <filemanager>
//#include <Cards>

#include <YSI\y_dialog>
#include <YSI\y_malloc>
#include <YSI\y_hooks>
#include <YSI\y_timers>
#include <YSI\y_va>
//#include <YSI\y_inline>



/*#if !defined abs 
    #define abs(%0) ((%0 > 0)?(%0):(-%0))
#endif
*/

stock abs(value)
    return (value > 0) ? (value) : (-value);

forward Float:GetPlayerMaxHealth(playerid);
forward OnPlayerLoginEx(playerid, sqlid);


#pragma dynamic 108920
#define DEBUG

#define BENZO_KAINA 5
#define AC_MAX_CHECKS           (2)         // Kiek kart� patikrinama, prie� imantis veiksm�
#define AC_MAX_SPEED            (230)       // Did�iausias veik�jo / ma�inos jud�jimo greitis
#define AC_SPEED(%0,%1,%2,%3,%4) floatround(floatsqroot(%4?(%0*%0+%1*%1+%2*%2):(%0*%0+%1*%1))*%3*1.6)


#undef  MAX_PLAYERS
#define MAX_PLAYERS 256


#include "ErrorLog"
#include "Config/mysql"


#include "Tabula/Zonos.pwn"
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

// Yra tik 13 slot� GTA SA. Tod�l u�sid�ti daugiau ginkl� �aid�jas negali.
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

// Darb� ID
#define JOB_NONE      0
//#define JOB_MECHANIC  1
//#define JOB_SWEEPER   2
//#define JOB_BOXER     3
//#define JOB_TRASH     3
//#define JOB_DRUGS     4
#define JOB_GUN       5
#define JOB_TRUCKER   6
//#define JOB_JACKER    7

// Daugiausiai ginkl� turim� rankose
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
#define MAX_WEED_SEEDS  200 // Kiek daugiausiai �ol�s seeds gali laikyti �mogus
#define MAX_TRUNK_SLOTS 12 // Ma�inos baga�in�s vietos

// Namo inventoriaus viet� kiekis

// D�mesio. Pakeitus �� skai�i� reikia keisti ir LoadHouseInv
#define MAX_HOUSETRUNK_SLOTS 30 
#define MAX_GARAGETRUNK_SLOTS 10 // Gara�o inventoriaus viet� kiekis

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


// Mygtuk� nustatymai
#define HOLDING(%0) \
    ((newkeys & (%0)) == (%0))
#define PRESSED(%0) \
    (((newkeys & (%0)) == (%0)) && ((oldkeys & (%0)) != (%0)))
#define RELEASED(%0) \
    (((newkeys & (%0)) != (%0)) && ((oldkeys & (%0)) == (%0)))


// UnixTimestamp kada buvo �jungtas serveris.
new ServerStartTimestamp;

enum radioInfo
{
    rName [ 28 ],
    rType [ 20 ],
    rUrl  [ 62 ]
};

#define STATIONS 18

/*
#define MAX_RADIO_STATION_URL           64
new
    RadioStations [ STATIONS ] [ radioInfo ] =
{
    { "SKY FM", "Classic Rock", "http://www.sky.fm/mp3/classicrock.pls" },
    { "SKY.FM", "Classic Rap", "http://www.sky.fm/aacplus/classicrap.pls" },
    { "SKY.FM", "60's Rock", "http://www.sky.fm/aacplus/60srock.pls" },
    { "SKY.FM","80's Dance","http://www.sky.fm/aacplus/80sdance.pls" },
    { "SKY FM","80s Rock Hits","http://www.sky.fm/aacplus/80srock.pls" },
    { "SKY FM", "90's Hits", "http://www.sky.fm/aacplus/hit90s.pls" },
    { "SKY.FM", "90's R&B", "http://www.sky.fm/aacplus/90srnb.pls" },
    { "SKY.FM", "New Age", "http://www.sky.fm/aacplus/newage.pls" },
    { "SKY.FM","Russian Pop","http://www.sky.fm/aacplus/russianpop.pls" },
    { "SKY FM","Top Hits","http://www.sky.fm/aacplus/tophits.pls" },
    { "Chanson Radio","Russian","http://icecast.chanson.cdnvideo.ru:8000/chanson_64_bu.ogg" },
    { "Pulse Radio", "Variety", "http://cast9.directhostingcenter.com:2199/tunein/ulzgmqis.pls" },
    { "BassProject", "Electro", "http://bassproject.net/radio/listen256mp3.pls" },
    { "Dubbase  FM", "Dubstep", "http://player.listenlive.co/26151" },
    { "BestNetRadio","90s Alternative","http://107.155.126.42:7070/listen.pls" },
    { "BestNetRadio","90s Pop Rock","http://107.155.126.42:7080/listen.pls" },
    { "West Coast Classic", "Rap", "http://streaming.radionomy.com/WestCoastClassics"},
    { "Radio Salsa", "Rap", "http://streaming.radionomy.com/Radio-Salsa---Clasicos"}
};
*/
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
	Moving, // I�plaukia
    Arriving // Atplaukia. Rodomi judantys objektai.
};

#define CARGOSHIP_DOCKED_INTERVAL       40*60*1000
#define CARGOSHIP_MOVING_INTERVAL       5*60*1000


enum E_SHIP_DATA {
	E_SHIP_STATUS:Status,
	LastDepartureTimestamp,
    LastArrivalTimestamp,
	CurrentStock, // Skai�iuojamas cargo slotais, ne vienetais. 
    ObjectIDs[ 11 ] // Hard-coded nes tai yra CreateDynamicObject skai�ius.
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

// Sekund�mis
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
    bool:Windows[MAX_VEHICLES] = { false, ... },
    bool:gPlayerUsingLoopingAnim[MAX_PLAYERS],
    bool:IsOnePlayAnim[MAX_PLAYERS],
    BackOut[MAX_PLAYERS],
    Unfreeze[MAX_PLAYERS],
    AfkCheck[MAX_PLAYERS],
    OldCar[MAX_PLAYERS],
    tmpinteger[MAX_PLAYERS],
    //bool:Belt[MAX_PLAYERS],
    //Float:Tlc[3],
    bool:Voted[MAX_PLAYERS] = { true, ... },
    Votes[ 2 ],
    //RadioStation[ MAX_PLAYERS ],
    //RadioName[ MAX_PLAYERS ],
    //VehicleRadio[ MAX_VEHICLES ] = { 99, ... },
    //VehRadio[ MAX_VEHICLES ][ 128 ],
    //Police[ MAX_VEHICLES ],
    bool:VGaraze[ MAX_VEHICLES ] = { false, ... },
    Camera      [ MAX_PLAYERS  ] = { -1, ... },
    PlayerSpeed [ MAX_PLAYERS  ],
    bool:TazerAut = true,
    Pickups[ 5 ],
    szMessage[256],
    timeris                 [ MAX_PLAYERS  ],
    Meter                   [ MAX_VEHICLES ],
    Kils                    [ MAX_VEHICLES ],
    bool:UsingLoopAnim[ MAX_PLAYERS ],
    bool:AnimsPrelo   [ MAX_PLAYERS ],
    //ObjUpdate[ MAX_PLAYERS ],
    Float:V_HP[ MAX_VEHICLES ],
    ac_SpeedWarns           [ MAX_PLAYERS ],
    //PlayerMoney             [ MAX_PLAYERS ],
    //bool:PUzrakinta = false,
    vehview[ MAX_PLAYERS ],
    skinlist,
    Text3D:DeathLabel[MAX_PLAYERS],
    LastVehicleDriverSqlId[ MAX_VEHICLES ],
    bool:IsPlayerDataRecorded[ MAX_PLAYERS ],
    LastAds[ 10 ][ MAX_AD_TEXT ],
    LastPlayerCommandTimestamp[ MAX_PLAYERS ],
    Text3D:SpecCommandLabel[ MAX_PLAYERS ] = {INVALID_3DTEXT_ID, ... },
    SpecCommandTimer[ MAX_PLAYERS ] = {-1, ... },
    PlayerSpectatedPlayer[ MAX_PLAYERS ] = {INVALID_PLAYER_ID, ... }, 
    DrugTimer[MAX_PLAYERS],
    Text3D:Units[ MAX_VEHICLES ],
    bool:IsFillingFuel[ MAX_PLAYERS ],
    PlayerFillUpTimer[ MAX_PLAYERS ],
    InfoTextTimer[ MAX_PLAYERS ] = {-1, ...},
    LastPlayerAd[ MAX_PLAYERS ];                                // Timestamp kada ra�� paskutin� skelbim�.
    //PVarning                [  MAX_PLAYERS ];//Login
    
new Iterator:Audio3D<MAX_PLAYERS>;




// Prikabinami �aid�jo ginklai, komanda /wepaon
enum E_PLAYER_ATTACHED_WEAPON_DATA
{
    WeaponId,
    ObjectSlot
};
new PlayerAttachedWeapons[ MAX_PLAYERS ][ MAX_PLAYER_ATTACHED_WEAPONS ][ E_PLAYER_ATTACHED_WEAPON_DATA ];


// Vagi� darbo reikalaujamos ma�inos.
/*
enum E_JACKER_BOUGHT_VEHICLE_DATA {
    VehicleModel, 
    AmountNeeded
};
new JackerBoughtVehicles[ 3 ][ E_JACKER_BOUGHT_VEHICLE_DATA ];
*/



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

stock SetPlayerHealthBonus(playerid, Float:health)
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
/*enum E_FACTION_DATA
{
    fID,
    fName[126],
    fLeader[MAX_PLAYER_NAME],
    Float:fSpawn[4],
    fRank1[54],
    fRank2[54],
    fRank3[54],
    fRank4[54],
    fRank5[54],
    fRank6[54],
    fRank7[54],
    fRank8[54],
    fRank9[54],
    fRank10[54],
    fRank11[54],
    fRank12[54],
    fRank13[54],
    fInt,
    fBank,
    fMatsPriv,
    fChat,
    fPayDay[MAX_FACTION_RANKS],
};
new fInfo[20][E_FACTION_DATA];
new Iterator:Faction<16>;
*/
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

new Iterator:Vehicles<MAX_VEHICLES>;

/*
enum fires
{
    bool:active,
    ugnis[ 80 ],
    smoke
};

new Fire[MAX_FIRE][fires];*/


//#include "Player\Weapons" // Yra AC dalyk�
//#include "Tabula\TAC.pwn" // AntiCheatas


//#include "Coordinates"

#include "BugReporting"

//#include "Items"
//#include "Phones"

//#include "FishingSystem"
//#include "Job_TaxiDriver"
//#include "Property\Interiors"
//#include "Property\Furniture"
//#include "Property\_General"
//#include "Property\Businesses"
//#include "Property\Houses"
//#include "Property\Garages"
//#include "Player\Functions"
//#include "Player\Inventory"
//#include "Player\Attachments"
//#include "Player\Phone"
//#include "Vehicles\vPhone"
//#include "Bank"
//#include "Graffiti"
//#include "Entrances"
//#include "Gambling/Blackjack"


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
/*
#define MAX_LIC_Q 7
enum LIC_QUIZ_
{
    Question[ 126 ],
    Answer1[ 126 ],
    Answer2[ 126 ],
    Answer3[ 126 ],
    TruAnsw
}
new LIC_QUIZ[ MAX_LIC_Q ][ LIC_QUIZ_ ] = { // DMV Klausimai
    {"Laikantis teises galima nereguoti � kelio eismo taisykles (�viesaforus, kelio �enklus ir kt.)?",
        "Galima",
        "Negalima",
        "Galima, nes laikantis teises tu turi pirmum� visais atvejais.", 2},
    {"Va�iuojant vairavimo mokyklos transportu, galima va�iuoti ne tam paskirtu mar�rutu?",
        "Galima",
        "Negalima",
        "Galima, nes tu sumok�jei pinigus ir gali daryti k� nori", 2},
    {"Ar reikia rodyti pos�k�, norint sustoti pakel�je?",
        "Ne",
        "Taip",
        "nemanau, gi sustoti tik tenoriu, kam rodyti?", 2},
    {"Laikantis teises tu patenki � eismo �vyk�, k� darai?",
        "I�kvie�iu policij� ir laukiu kol bus i�spr�sta problema",
        "I�va�iuoju i� eismo �vykio, nes tuo metu laikausi teises",
        "Palieku transporto priemon� ir pasi�alinu i� eismo �vykio", 1},
    {"Va�iuodamas automobiliu pastebite susitrenkusias dvi transporto priemones, k� darote?",
        "Prava�iuoju nekreipdamas d�mesio.",
        "Prane�u apie pasteb�ta situacij� ir va�iuoju toliau",
        "Va�iuoju toliau nekreipdamas d�mesio, nes esu susikoncentrav�s � teisi� laikym�", 2},
    {"Va�iuojant motociklu b�tina dev�ti �alm�?",
        "Taip",
        "Ne",
        "Neb�tina, nes tai trukdys va�iuoti motociklu.", 1},
    {"Norint pasi�alinti i� transporto priemon�s, galima palikti j� viduryje kelio?",
        "Taip, galima palikti transporto priemon� viduryje kelio",
        "Ne, transporto priemon� privalau statyti ten, kur ji netrukdyt� eismo stabilumui",
        "Taip, galiu statyti transporto priemon� viduryje kelio, nes neturiu laiko jos patraukti.", 2}
};*/

new const  // CCTV koordinat�s
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

/*new const
Float:LogWathc[8][6] = { //Vaizdas prisijungimui vykstant, pirma kordinat� - vieta, antra kordinat� - kamera
    {135.1453, 54.5497, 78.8274,   135.7209, 53.7337, 78.4673},
    {-68.4013, 147.1631, 40.6497,  -68.5086, 146.1703, 40.4897},
    {641.6294, -621.9632, 46.3704,  641.9489, -621.0170, 46.1054},
    {1438.6992, 335.3381, 56.2001,  1437.8203, 334.8645, 55.9900},
    {337.2339, -663.2688, 32.7126, 336.9972, -662.2993, 32.6026},
    {962.9985, -283.5939, 75.7649,  962.2916, -284.2987, 75.6147},
    {1199.7195, -70.2596, 44.4392, 1198.7742, -70.5790, 44.4041},
    {534.7148, -226.6253, 18.7703, 533.8982, -227.1986, 18.6952}
};*/

enum businfo
{
    objectas,
    Float:X4,
    Float:Y4,
    Float:Z4,
};

new RandBus[12][businfo] = {
{0, -178.7006,1044.9867,19.7422}, // �i�k�l�1
{0, -167.4145,1086.3560,19.7422}, // �i�k�l�2
{0, -135.5973,1086.2920,19.7422}, // �i�k�l�3
{0, -105.6469,1113.8851,19.7422}, // �i�k�l�4
{0, -88.2529,1128.5619,19.7422}, // �i�k�l�5
{0, -87.8232,1163.3447,19.7422}, // �i�k�l�6
{0, -170.2490,1214.6812,19.7422}, // �i�k�l�7
{0, -854.4310,1539.2550,22.5638}, // �i�k�l�8
{0, -890.0706,1544.4739,25.9505}, // �i�k�l�9
{0, -784.9171,1621.5551,27.1172}, // �i�k�l�10
{0, -336.4170,1162.9825,19.7301}, // �i�k�l�11
{0, 76.6545,1211.3668,18.8376} // �i�k�l�end
};



new const
Float:CloseGate[][8] = {
    {1500.0, 245.3984375, 72.459747314453, 1002.641418457, 0.0, 0.0, 0.0,1.0},
    {1500.0, 246.9089050293, 72.448867797852, 1002.640625, 0.0, 0.0, 0.0,1.0},
    {989.0,   2575.48, -1300.29, 1038.17, 0.00, 0.00, 286.44, 1.0},
    {19302.0, 2565.68, -1302.77, 1031.63, 0.00, 0.00, 90.48,  1.0},
    {19302.0, 2565.68, -1301.04, 1031.63, 0.00, 0.00, 271.26, 1.0},
    {1495.0, 67.37020, 1969.24316, 430.76300, 0.0, 0.00, 0.00, 1.0},
    {1495.0, -645.35809, -1792.86157, -76.05270, 0.0, 0.00, 0.00, 1.0},
    {5779.0, -258.30231, 1031.96106, 20.47970, 0.0, 0.00, 0.00, 2.0},
    {5779.0, -258.30121, 1081.63147, 20.47970, 0.0, 0.00, 0.00, 2.0},
    {5779.0, -258.30121, 1074.85144, 20.47970, 0.0, 0.00, 0.00, 2.0},
    {5779.0, -258.30231, 1047.10107, 20.47970, 0.0, 0.00, 0.00, 2.0},
    {5779.0, -258.30231, 1040.26111, 20.47970, 0.0, 0.00, 0.00, 2.0},
    {1569.0, -979.44855, -2429.74902, 2232.50000, 0.0, 0.00, 180.00, 1.0},
    {968.0, 1544.68, -1630.99, 13.33,   0.00, 90.00, 90.00, 1.0},
	{968.0, 1557.84, -1608.66, 13.20,   0.00, 90.00, 90.00, 1.0},
	{10184.0, 1590.14, -1638.37, 14.30,   0.00, 0.00, 90.00, 1.0},
	{1569.0, -10.01, 2053.78, 2129.00,   0.00, 0.00, 90.00, 1.0},
	{1495.0,1794.00976562,-1525.18676758,5699.42480469,   0.00, 0.00, 360.00, 1.0},
	{1495.0,1797.00830078,-1525.15258789,5699.42480469,   0.00, 0.00, 180.00, 1.0},
	{1495.0,1808.73071289,-1545.87463379,5699.42480469,   0.00, 0.00, 0.00, 1.0},
	{1495.0,1811.73828125,-1545.82873535,5699.42480469,0.00000000,0.00000000,180.00000000, 1.0},
	{1495.0,1808.76147461,-1547.63208008,5699.42480469,0.00000000,0.00000000,0.00000000, 1.0},
	{1495.0,1811.76184082,-1547.60510254,5699.42480469,0.00000000,0.00000000,180.00000000, 1.0},
	{1495.0, 1760.05, -1561.55, 8.59,   0.00, 0.00, 0.00, 1.0},
	{1966.0, 1813.71191, -1536.81494, 13.50000,   0.00000, 0.00000, 88.30000, 1.0},
	{1569.0, 1467.07, -2758.31, 5284.21,   0.00, 0.00, 0.00, 2.0}
};

new const
Float:MoveGate[][6] = {
    {244.03276062012, 72.469627380371, 1002.6823730469, 0.0, 0.0, 0.0},
    {247.29063415527, 72.450302124023, 1002.640625, 0.0, 0.0, 0.0},
    {2578.72, -1300.29, 1038.17, 0.00, 0.00, 286.44},
    {2565.68, -1304.26, 1031.63, 0.00, 0.00, 90.48},
    {2565.68, -1299.53, 1031.63, 0.00, 0.00, 271.26},
    {65.87020, 1969.24316, 430.76300, 0.0, 0.0, 0.0},
    { -646.85809, -1792.86157, -76.05270, 0.0, 0.0, 0.0},
    {-258.30231, 1031.96106, 16.9797, 0.0, 0.00, 0.00},
    {-258.30121, 1081.63147, 16.9797, 0.0, 0.00, 0.00},
    {-258.30121, 1074.85144, 16.9797, 0.0, 0.00, 0.00},
    {-258.30231, 1047.10107, 16.9797, 0.0, 0.00, 0.00},
    {-258.30231, 1040.26111, 16.9797, 0.0, 0.00, 0.00},
    {-979.44849, -2429.74902, 2232.50000, 0.00000, 0.00000, 100.00000},
    {1544.78, -1631.00, 13.43,   0.00, 0.00, 90.00},
	{1557.94, -1608.76, 13.30,   0.00, 0.00, 90.00},
	{1590.14, -1640.47, 16.22,   0.00, -90.00, 90.00},
	{-10.11, 2053.88, 2129.10,   0.00, 0.00, 0.00},
	{1792.7498,-1525.28676758,5699.52480469,   0.00, 0.00, 360.00},
	{1798.2883,-1525.25258789,5699.52480469,   0.00, 0.00, 180.00},
	{1807.4307,-1545.97463379,5699.52480469,   0.00, 0.00, 0.00},
	{1813.0383,-1545.92873535,5699.52480469,0.00000000,0.00000000,180.00000000},
	{1807.4815,-1547.73208008,5699.52480469,0.00000000,0.00000000,0.00000000},
	{1812.9818,-1547.70510254,5699.52480469,0.00000000,0.00000000,180.00000000},
	{1758.7714, -1562.55, 8.69,   0.00, 0.00, 0.00},
	{1813.3119, -1536.8149, 15.9800,   90.00000, 0.00000, 88.30000},
	{1467.17, -2758.41, 5284.31,   0.00, 0.00, 90.00}
};
new 
    bool:Gates[sizeof(CloseGate)] = { false, ... },
    Gates2[sizeof(CloseGate)];

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

new const pJobs[ MAX_JOBS ][ jobs ] = { // Darb� (�sidarbinimo) koordinat�s, alga, kontraktas, maksimali alga
    { 99999.0, 99999.0, 8888.0, 50, 0, 2400,         "Bedarbis" },
    { 1657.9742,-1817.6954,13.6508, 600, 6, 900,         "Mechanikas" },
    { 99999.0, 99999.0, 8888.0, 0, 5, 750,        "Gatvi� valytojas" },
//    { 758.1678, -77.3299, 1000.6499, 150, 5, 500,     "Kovos men� treneris" },
    { 2195.7881,-1973.2227,13.5589, 400, 5, 2200,         "�i�k�lininkas" },
    { 99999.0, 99999.0, 8888.0, 350, 5, 150,  "Gatvi� �lav�jas." },
    { 99999.0, 99999.0, 8888.0, 0, 5, 150,  "Mechanikas." },
    { 2281.1189,-2365.0647,13.5469, 400, 5, 900,  "Krovini� perve�im� vairuotojas" },
    { 99999.0, 99999.0, 8888.0, 400, 10, 2200,      "Automobiliu vagis (nelegalus)" }
};

new Ligos[7][25] = { // Ligos
{"Neturi"},
{"Bronhitu"},
{"Plau�iu u�degimu"},
{"Gripu"},
{"Raupais"},
{"Apendicitu"},
{"Sloga"}
};




/*U�kraunamas tr. priemoni� turgus sellcars, sellbikes, sellsport*/
/*
new SellCars [ 90 ][ 2 ];
new SellBikes[ 11 ][ 2 ];
new SportCars[ 20 ][ 2 ];
*/

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
new CurrentPlayerVehicleShop[ MAX_PLAYERS ];


/*U�kraunamas tr. priemoni� turgus sellcars, sellbikes, sellsport pabaiga*/


/*  Nefrakcinio darbo �i�k�lininkai nustatymai */

#define TRASH_MISSION_NONE 0
#define TRASH_MISSION_MONTGOMERY 1
#define TRASH_MISSION_DILIMORE 2
#define TRASH_MISSION_POLOMINO_CREEK 3
#define TRASH_MISSION_JEFFERSON 4
#define TRASH_MISSION_IDLEWOOD 5

#define MAX_GARBAGE_CANS                200
#define TRASH_MISSION_COMPLETED_BONUS   380
#define TRASH_OBJECT_INDEX              4

/*  Nefrakcinio darbo �i�k�lininkai nustatymai pabaiga*/


enum E_GARBAGE_CANS {
    gModel,
    gMission,
    gObjectId
};

//new GarbageInfo[ MAX_GARBAGE_CANS ][ E_GARBAGE_CANS ];
/*
new TrashMission[ MAX_PLAYERS ] = { TRASH_MISSION_NONE, ...}, 
    CurrentTrashCp[ MAX_PLAYERS ], 
    TrashTimer[ MAX_PLAYERS ],
    IsCarryingTrash[ MAX_PLAYERS ],
    TrashBagsInTrashVehicle[ MAX_VEHICLES ];
*/



stock IsPlayerAddicted( playerid )
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

stock GetVehiclePrice( model )
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

stock Check_VHP( vehicleid, mode = 0, Float:HP = 1000.0 )
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

stock PDJOBPlace( playerid )
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

stock StopLoopingAnim( playerid, bool:  setvar = true )
{
    // Funkcija: StopLoopingAnim( playerid )

    if ( setvar )
        UsingLoopAnim[ playerid ] = false;

    ApplyAnimation( playerid, "CARRY", "crry_prtial", 4.0, 0, 0, 0, 0, 0 );
}

stock NullAnimVariables( playerid )
{
    // Funkcija: NullAnimVariables( playerid );

    UsingLoopAnim[ playerid ] = false;
    AnimsPrelo   [ playerid ] = false;
}
/*
stock GetNumber( playerid, number )
{
    new
        string[ 128 ],
        ministr[ MAX_PLAYER_NAME ];

    // Jei telefon� knygoj turi t� nr.
    string = GetPlayerPhonebookName(playerid, number);
    if(!isnull(string))
        return string;

    // Tikrinimas tarp u�ra�� ar yra tas nr... Po poros versij7 galima sunaikint.
    for ( new i = 1; i <= 7; i++ )
    {
        format           ( ministr, 8, "NOTE2_%d", i );
        if( GetPVarInt( playerid, ministr ) == number )
        {
            format           ( ministr, 8, "NOTE_%d", i );
            GetPVarString    ( playerid, ministr, string, 126 );
            return string;
        }
    }
    valstr(string,number);
    return string;
}

*/
/*
stock PlacePlayerRoadBlockInPos( playerid, type )
{
    // Funkcija: PlacePlayerRoadBlockInPos( playerid, type )

    new
        objectid;

    switch ( type )
    {
        case 1 : objectid = 978;
        case 2 : objectid = 981;
        case 3 : objectid = 1228;
        case 4 : objectid = 1423;
        case 5 : objectid = 1251;
        case 6 : objectid = 8548;
        case 7 : objectid = 2599;
        case 8 : objectid = 1238;
        case 9:
        {
            if( PlayerFaction( playerid ) == 1 )
                objectid = 2899;
        }
        case 10:
        {
            if( PlayerFaction( playerid ) == 5 )
                objectid = 1426;
        }
        case 11:
        {
            if( PlayerFaction( playerid ) == 5 )
                objectid = 1466;
        }
        case 12:
        {
            if( PlayerFaction( playerid ) == 5 )
                objectid = 1428;
        }
        case 13:
        {
            if( PlayerFaction( playerid ) == 5 )
                objectid = 1436;
        }
        case 14:
        {
            if( PlayerFaction( playerid ) == 5 )
                objectid = 3864;
        }
        case 15:
        {
            if( PlayerFaction( playerid ) == 5 )
                objectid = 1262;
        }
        case 16:
        {
            if( PlayerFaction( playerid ) == 5 )
                objectid = 1358;
        }
        case 17:
        {
            if( PlayerFaction( playerid ) == 5 )
                objectid = 1299;
        }
        case 18:
        {
            if( PlayerFaction( playerid ) == 5 )
                objectid = 19125;
        }

        default:
        {
            SendClientMessage( playerid, COLOR_RED, "Klaida, nurodytas blogas/netinkamas kelio blokados tipas." );
            return false;
        }
    }

    new
        slotid = GetFreeRoadBlockSlot( );

    if ( slotid < MAX_ROADBLOCKS )
    {
        new
            Float:pX,
            Float:pY,
            Float:pZ,
            Float:pA;

        GetPlayerPos        ( playerid, pX, pY, pZ );
        GetPlayerFacingAngle( playerid, pA );
        GetXYJudgedByAngle  ( 1.5, pX, pY, pA, pX, pY );

        if( objectid == 2899 )
            RID[ slotid ] = 1;
        else
            RID[ slotid ] = 0;

        RoadBlocks[ slotid ] = CreateDynamicObject( objectid, pX, pY, pZ,
             0, 0, pA );
        if( objectid == 2599 )
        SetDynamicObjectMaterial(RoadBlocks[ slotid ], 0, 967, "cj_barr_set_1", "Stop2_64");
        if( objectid == 8548 )
        SetDynamicObjectMaterial(RoadBlocks[ slotid ], 0, 7184, "vgndwntwn1", "stop2_64");
        if( objectid == 1251 )
        SetDynamicObjectMaterial(RoadBlocks[ slotid ], 0, 12938, "sw_apartments", "sw_policeline");
        Streamer_Update( playerid );
        EditDynamicObject(playerid, RoadBlocks[ slotid ]);
    }
    else
    {
        SendClientMessage( playerid, COLOR_GREY, "   Jau vir�ytas maksimal� kel\
            io blokad� skai�ius." );
        return false;
    }

    return true;
}
*/
stock RemoveRoadBlock( id )
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

stock NullRoadBlocks( )
{
    // Funkcija: NullRoadBlocks( )

    for ( new i = 0; i < MAX_ROADBLOCKS; i++ )
    {
        RoadBlocks[ i ] = 0;
        RID[ i ] = 0;
    }
}

stock GetFreeBoxSlot( )
{
    // Funkcija: GetFreeBoxSlot( )
    for ( new i = 0; i < MAX_BOXES; i++ )
    {
        if(!IsValidDynamicObject(CargoBox[ i ][ ObjectId ]))
            return i;
    }

    return -1;
}

stock GetFreeRoadBlockSlot( )
{
    // Funkcija: GetFreeRoadBlockSlot( )

    for ( new i = 0; i < MAX_ROADBLOCKS; i++ )
    {
        if ( RoadBlocks[ i ] == 0 )
            return i;
    }

    return MAX_ROADBLOCKS;
}


stock GetAdminRank( playerid )
{
    // Funkcija: GetAdminRank( playerid )
    // Returnins veik�jo admin rank�

    new
        admrank[ 32 ];

    switch ( GetPlayerAdminLevel(playerid) )
    {
        case 1,2: admrank = "Administratorius";
        case 3: admrank = "Vyr. Administratorius";
        case 4: admrank = "Pagr. Administratorius";
        case 5: admrank = "Projekto savininkas";
        case 6: admrank = "Skripto pri�i�r�tojas";
        default: admrank = "";
    }

    return admrank;
}

stock GetXYInFrontOfPlayer(playerid, &Float:x, &Float:y, Float:distance)
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

stock GetXYBehindPoint(Float:x,Float:y,&Float:x2,&Float:y2,Float:A,Float:distance)
{
    x2 = x - (distance * floatsin(-A,degrees));
    y2 = y - (distance * floatcos(-A,degrees));
}

stock GetXYBehindVehicle(vehicleid, &Float:x2, &Float:y2, Float:dist)
{
    new Float:x,Float:y,Float:a;
    GetVehiclePos(vehicleid, x,y,a);
    GetVehicleZAngle(vehicleid, a);
    GetXYBehindPoint(x, y, x2, y2, a, dist);
}

stock GetXYJudgedByAngle( Float:distance, Float:x, Float:y, Float:angle, &Float:rx, &Float:ry )
{
    // Funkcija: GetXYJudgedByAngle( distance, Float:x, Float:y Float:angle, &Float:rx, &Float:ry )

    rx = x + distance * floatsin( -angle, degrees );
    ry = y + distance * floatcos( -angle, degrees );
}

stock doesVehicleExist(const vehicleid) {
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
    {"�aldoma priekaba"},
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
    {"�aldoma priekaba 2"},
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

stock SyncFuel( vehicleid )
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
    // Gra�ina industrijai priklausant� nurodytos prek�s kiek�.
    foreach(CommodityIterator, i)
        if(Commodities[ i ][ IndustryId ] == Industries[ index ][ Id ]
            && Commodities[ i ][ CargoId ] == cargoid
            && !Commodities[ i ][ IsBusinessCommodity ])
            return Commodities[ i ][ CurrentStock ];
    return 0;
}

/*
stock UnLoadFactions()
{
    foreach(Faction,i)
    {
        fInfo[ i ][ fID     ] = 0;
        fInfo[ i ][ fName   ] = 0;
        fInfo[ i ][ fLeader ] = 0;
        fInfo[ i ][ fSpawn  ][0] = 0;
        fInfo[ i ][ fSpawn  ][1] = 0;
        fInfo[ i ][ fSpawn  ][2] = 0;
        fInfo[ i ][ fSpawn  ][3] = 0;
        fInfo[ i ][ fRank1  ] = 0;
        fInfo[ i ][ fRank2  ] = 0;
        fInfo[ i ][ fRank3  ] = 0;
        fInfo[ i ][ fRank4  ] = 0;
        fInfo[ i ][ fRank5  ] = 0;
        fInfo[ i ][ fRank6  ] = 0;
        fInfo[ i ][ fRank7  ] = 0;
        fInfo[ i ][ fRank8  ] = 0;
        fInfo[ i ][ fRank9  ] = 0;
        fInfo[ i ][ fRank10 ] = 0;
        fInfo[ i ][ fRank11 ] = 0;
        fInfo[ i ][ fRank12 ] = 0;
        fInfo[ i ][ fRank13 ] = 0;
        fInfo[ i ][ fInt    ] = 0;
        fInfo[ i ][ fBank   ] = 0;
        fInfo[ i ][ fMatsPriv ] = 0;
    }
    Itter_Clear(Faction);
    return 1;
}*/


stock ShowTrunk( playerid, veh )
{
    new string[ 1028 ];
    for( new slot = 0; slot < GetVehicleTrunkSlots( GetVehicleModel( veh ) ); slot ++)
    {
        if ( cInfo[ veh ][ cTrunkWeapon ][ slot ] == 0)
            format( string, sizeof(string), "%s%d. N�ra\n", string,slot+1);
        else if ( cInfo[ veh ][ cTrunkWeapon ][ slot ] > 0 )
        {
            format( string, sizeof(string), "%s%d. %s %d\n", string, slot+1, GetItemName(cInfo[ veh ][ cTrunkWeapon ][ slot ]) , cInfo[ veh ][ cTrunkAmmo ][ slot ] );
        }
    }
    format( string, sizeof(string), "%s\nI�junkti", string);
    ShowPlayerDialog(playerid,13,DIALOG_STYLE_LIST,"Baga�in�",string,"Paimti","Atgal");
    return 1;
}


stock IsItemInTrunk( vehicle, item )
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



stock RemoveAllCargoFromVehicle(vehicleid)
{
    for(new i = 0; i < sizeof VehicleCargo[]; i++)
        if(VehicleCargo[ vehicleid ][ i ][ Amount])
            RemoveCargoFromVehicle(vehicleid, VehicleCargo[ vehicleid ][ i ][ CargoId ], VehicleCargo[ vehicleid ][ i ][ Amount]);
    return 1;
}

stock RemoveCargoFromVehicle(vehicleid, cargoid, amount = 1)
{
    new query[140];
    for(new i = 0; i < sizeof(VehicleCargo[]); i++)
    {
        if(!VehicleCargo[ vehicleid ][ i ][ Amount ]) 
            continue;
        if(VehicleCargo[ vehicleid ][ i ][ CargoId ] != cargoid)
            continue;

        VehicleCargo[ vehicleid ][ i ][ Amount ] -= amount;
        // Jei neb�ra krovini�, pa�alinam ir jo ID i� ma�inos ir DB
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
stock DestroyVehicleObject(vehicleid, index)
{
    if(index < 0 || index >= MAX_TRUCKER_CARGO_OBJECTS)
        return printf("Error. Klaida. DestroyVehicleObject(%d,%d) index invalid", vehicleid, index);
    DestroyObject(cInfo[ vehicleid ][ objectai ][ index ]);
    cInfo[ vehicleid ][ objectai ][ index ] = -1;
    return 1;
}
stock AddCargoToVehicle(vehicleid, cargoid, bool:ignore_sql = false)
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

stock GetFreeVehicleObjectSlot(vehicleid)
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
stock GetLastUsedVehicleObjectSlot(vehicleid)
{
    for(new i = MAX_TRUCKER_CARGO_OBJECTS-1; i >= 0; i--)
        if(cInfo[ vehicleid ][ objectai ][ i ] != -1)
            return i;
    return -1;
}

stock GetTruckerCargoOffsets(model, cargo_type, number, &Float:x, &Float:y, &Float:z, &Float:rotx, &Float:roty, &Float:rotz)
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
stock GetTruckerCargoObject(cargo_type)
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

stock CanPlayerUseTruckerVehicle(playerid, model)
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
stock HasVehicleSpaceForCargo(vehicleid, cargoid)
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
stock GetVehicleCargoLimit(model)
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
stock ShowVehicleCargo(playerid, vehicleid)
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
        ShowPlayerDialog( playerid, 9999, DIALOG_STYLE_MSGBOX, "Vehicle Cargo", "J�s� transporto priemon�je n�ra jokio krovinio", "Gerai", "" );
        
    else 
    {
        ShowPlayerDialog( playerid, DIALOG_VEHICLE_CARGO_LIST, DIALOG_STYLE_LIST, "Vehicle Cargo", string, "Pasiimti", "Atgal" );
        SetPVarInt(playerid, "vehicleid", vehicleid);
    }
    return 1;
}

stock ShowTPDA( playerid )
{
	ShowPlayerDialog(playerid, DIALOG_TPDA_MAIN, DIALOG_STYLE_LIST, "TPDA", "{C0C0C0}Per�i�r�ti{FFFFFF} Visas industrijas\n{C0C0C0}Per�i�r�ti{FFFFFF} Verslus perkan�ius prekes\n{C0C0C0}Per�i�r�ti{FFFFFF} Laivo informacij�", "Pasirinkti","I�eiti");
    return 1;
}

stock GetIndustrySectorName(industry_index)
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
		name = "Pirmin�";
	else if(sold && bought)
		name = "Antrin�";
	else 
		name = "Paslaugin�";
	return name;
}


stock GetIndustryBoughtCommodityCount(industry_index)
{
    new count = 0;
    foreach(CommodityIterator, i)
        if(Commodities[ i ][ IndustryId ] == Industries[ industry_index ][ Id ] 
            && Commodities[ i ][ SellBuyStatus ] == Buying
            && !Commodities[ i ][ IsBusinessCommodity ])
            count++;
    return count;
}
stock GetIndustrySoldCommodityCount(industry_index)
{
    new count = 0;
    foreach(CommodityIterator, i)
        if(Commodities[ i ][ IndustryId ] == Industries[ industry_index ][ Id ] 
            && Commodities[ i ][ SellBuyStatus ] == Selling
            && !Commodities[ i ][ IsBusinessCommodity ])
            count++;
    return count;
}

stock IsShipAcceptingCargo(cargoid)
{
    #pragma unused cargoid
	// Laivas superka visas prekes kaip ir verslai...
	// Kitaip tariant, jeigu jokia industrija to neperka - laivas perka.
	//if(IsAnyIndustryBuyingCargo(cargoid))
	//	return false;
    // Nuo 2015.01.04 laivas superka VISKA.
	return true;
}

stock IsAnyIndustryBuyingCargo(cargoid)
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
stock HasIndustryRoomForCargo(industry_index, cargoid)
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
stock IsIndustryAcceptingCargo(industry_index, cargoid)
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
stock IsIndustrySellingCargo(industry_index, cargoid)
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

stock SaveIndustryCommodities(industry_index)
{
	new query[140];
	foreach(CommodityIterator, i)
    {
		if(Commodities[ i ][ IndustryId ] == Industries[ industry_index ][ Id ]
            && !Commodities[ i ][ IsBusinessCommodity ])
        {
			format(query,sizeof(query), "UPDATE commodities SET current_stock = %d WHERE cargo_id = %d AND industry_id = %d AND Type = 'Industry'",
				Commodities[ i ][ CurrentStock ], Commodities[ i ][ CargoId ], Industries[ industry_index ][ Id ]); 
			mysql_query(DbHandle, query, false);
		}
	}
}

stock AddCargoToIndustry(industry_index, cargoid, amount = 1)
{
    new query[160];
    foreach(CommodityIterator, i)
    {
		if(Commodities[ i ][ IndustryId ] == Industries[ industry_index ][ Id ]
            && Commodities[ i ][ CargoId ] == cargoid
            && !Commodities[ i ][ IsBusinessCommodity ])
        {
            Commodities[ i ][ CurrentStock ] += amount;
            format(query,sizeof(query),"UPDATE commodities SET current_stock = %d WHERE industry_id = %d AND cargo_id = %d AND type = 'Industry'",
                Commodities[ i ][ CurrentStock ], Industries[ industry_index ][ Id ] ,Commodities[ i ][ CargoId ]);
            mysql_query(DbHandle, query, false);
			UpdateIndustryInfo(industry_index);
            return 1;
        }
	}
    return false;
}

stock RemoveCargoFromIndustry(industry_index, cargoid, amount = 1)
{
	new query[160];
	foreach(CommodityIterator, i)
        if(Commodities[ i ][ IndustryId ] == Industries[ industry_index ][ Id ]
            && Commodities[ i ][ CargoId ] == cargoid
            && !Commodities[ i ][ IsBusinessCommodity ])
        {
			Commodities[ i ][ CurrentStock ] -= amount;
            format(query,sizeof(query),"UPDATE commodities SET current_stock = %d WHERE industry_id = %d AND cargo_id = %d AND type = 'Industry'",
                Commodities[ i ][ CurrentStock ], Industries[ industry_index ][ Id ] ,Commodities[ i ][ CargoId ]);
            mysql_query(DbHandle, query, false);
			UpdateIndustryInfo(industry_index);
            return 1;
		}
	return false;
}


stock GetCargoName(cargoid) 
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
stock IsPlayerInRangeOfAnyIndustry(playerid, Float:distance)
{
    if(GetPlayerIndustryInRange(playerid,distance) == -1)
        return false;
    return true;
}

stock GetPlayerIndustryInRange(playerid,Float:distance)
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
stock GetCommoditySellPrice(commodity_index)
    return Commodities[ commodity_index ][ Price ];

stock GetCargoProduction(cargoid)
{
    foreach(TruckerCargoIterator, i)
        if(TruckerCargo[ i ][ Id ] == cargoid)
            return TruckerCargo[ i ][ Production ];
    return 0;
}

stock GetCargoConsumption(cargoid)
{
    foreach(TruckerCargoIterator, i)
        if(TruckerCargo[ i ][ Id ] == cargoid)
            return TruckerCargo[ i ][ Consumption ];
    return 0;
}

stock GetCargoLimit(cargoid)
{
    foreach(TruckerCargoIterator, i)
        if(TruckerCargo[ i ][ Id ] == cargoid)
            return TruckerCargo[ i ][ Limit ];
    return 0;
}

stock GetCargoType(cargoid)
{
    foreach(TruckerCargoIterator, i)
        if(TruckerCargo[ i ][ Id ] == cargoid)
            return TruckerCargo[ i ][ Type ];
    return 0;
}

stock GetCargoSlot(cargoid)
{
    foreach(TruckerCargoIterator, i)
        if(TruckerCargo[ i ][ Id ] == cargoid)
            return TruckerCargo[ i ][ Slot ];
    return 0;
}
stock IsCargoInVehicle(vehicleid, cargoid)
{
    for(new i = 0; i < MAX_TRUCKER_CARGO; i++)
        if(VehicleCargo[ vehicleid ][ i ][ Amount ] && VehicleCargo[ vehicleid ][ i ][ CargoId ] == cargoid)
            return true;
    return false;
}
stock IsCargoCarryable(cargoid)
{
    switch(GetCargoType(cargoid))
    {
        case 2: return true;
        default: return false;
    }
    return false;
}

stock IsCargoCompatibleWithVehicle(cargoid, model)
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

stock WepNames[][24] = { // Ginkl� pavadinimai
        {"Niekas"},
        {"Kastetas"},
        {"Golfo lazda"},
        {"Bananas"},
        {"Peilis"},
        {"Beisbolo lazda"},
        {"Kastuvas"},
        {"Bilijardo lazda"},
        {"Katana"},
        {"Benzininis pj�klas"},
        {"Purpurinis vibratorius"},
        {"Baltas vibratorius"},
        {"Baltas vibratorius"},
        {"Sidabrinis vibratorius"},
        {"G�li� puok�t�"},
        {"Lazda"},
        {"Granatos"},
        {"Dujin� granata"},
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
        {"Nuotolin� bomba"},
        {"Detonatorius"},
        {"Balion�lis"},
        {"Gesintuvas"},
        {"Fotoaparatas"},
        {"Naktiniai akiniai"},
        {"Infrar. akiniai"},
        {"Para�iutas"},
        {"Netikras pistoletas"}
    };





/*
FUNKCIJA:Explosion( Float:x, Float:y, Float:z, object, id, bool:destroy, virw, inter )
{
    CreateExplosion( x+1, y-1, z, 13, 30 );
    CreateExplosion( x-1, y+1, z, 13, 30 );
    CreateExplosion( x-1, y+1, z, 13, 30 );
    CreateExplosion( x+1, y-1, z, 13, 30 );
    if ( destroy )
    {
        for ( new i = 0; i < 80; i++ )
        {
            DestroyDynamicObject( Fire[ object ][ ugnis ][ i ] );
        }
        DestroyDynamicObject( Fire[ object ][ smoke ] );
        Fire[ object ][ active ] = false;
    }
    else
    {
        if( id == 0 )
        {
            for ( new i = 0; i < 80; i++ )
            {
                if( i < 20 )
                    Fire[ object ][ ugnis ] [ i ] = CreateDynamicObject(3461, x-random(5), y+random(5), (z+0.7)-3, 0, 0, 0, virw, inter, -1, 500.0 );
                else if( i >= 20 && i < 40 )
                    Fire[ object ][ ugnis ] [ i ] = CreateDynamicObject(3461, x+random(5), y-random(5), (z+0.7)-3, 0, 0, 0, virw, inter, -1, 500.0 );
                else if( i >= 40 && i < 60 )
                    Fire[ object ][ ugnis ] [ i ] = CreateDynamicObject(3461, x-random(5), y-random(5), (z+0.7)-3, 0, 0, 0, virw, inter, -1, 500.0 );
                else
                    Fire[ object ][ ugnis ] [ i ] = CreateDynamicObject(3461, x+random(5), y+random(5), (z+0.7)-3, 0, 0, 0, virw, inter, -1, 500.0 );
            }
        }
    }
    return 1;
}*/

/*
stock SaveAccount(playerid)
{
    new string[2048],
        string2[256],
        string3[256];
    MySQLCheckConnection();
    pInfo[ playerid ][ pMoney ] = GetPlayerMoney(playerid);
    mysql_real_escape_string(pInfo[playerid][pSex], pInfo[playerid][pSex], DbHandle, 15);
    mysql_real_escape_string(pInfo[playerid][pOrigin], pInfo[playerid][pOrigin], DbHandle, MAX_PLAYER_NAME);
    mysql_real_escape_string(pInfo[playerid][pCard], string2);
    mysql_real_escape_string(pInfo[playerid][pForumName], string3);

    format(string, sizeof(string), "UPDATE players SET Money = '%d', Level = '%d', AdminLevel = '%d', Respect = '%d', Bank = '%d'", pInfo[ playerid ][ pMoney ], pInfo[ playerid ][ pLevel ], pInfo[ playerid ][ pAdmin ], pInfo[ playerid ][ pExp ], GetPlayerBankMoney(playerid) );
    format(string, sizeof(string), "%s, Skinas = '%d', Warnings = '%d', JailTime = '%d', Jailed = '%d', pDubKey = '%d'", string, pInfo[ playerid ][ pSkin ], pInfo[ playerid ][ pWarn ], pInfo[ playerid ][ pJailTime ], pInfo[ playerid ][ pJail ], pInfo[ playerid ][ pDubKey ] );
    format(string, sizeof(string), "%s, DriverWarn = '%d', Tester = '%d', pFines = '%d', pPFines = '%d'", string, pInfo[ playerid ][ pDriverWarn ], pInfo[ playerid ][ pTester ], pInfo[ playerid ][ pFines ], pInfo[ playerid ][ pPaydFines ] );
    format(string, sizeof(string), "%s, House = '%d', PhoneNr = '%d', Leader = '%d', Member = '%d', Rank = '%d'", string, pInfo[ playerid ][ pHouseKey ], pInfo[ playerid ][ pPhone ], pInfo[ playerid ][ pLead ], pInfo[ playerid ][ pMember ], pInfo[ playerid ][ pRank ] );
    format(string, sizeof(string), "%s, RChanel = '%d', VirWorld = '%d', Intas = '%d', Crashed = '%d', Pos_X = '%f'", string, pInfo[ playerid ][ pRChannel ], pInfo[ playerid ][ pVirWorld ], pInfo[ playerid ][ pInt ], pInfo[ playerid ][ pCrash ], pInfo[ playerid ][ pCrashPos ][ 0 ] );
    format(string, sizeof(string), "%s, Pos_Y = '%f', Pos_Z = '%f', Age = '%d', Liga = '%d', WantedLevel = '%d'", string, pInfo[ playerid ][ pCrashPos ][ 1 ], pInfo[ playerid ][ pCrashPos ][ 2 ], pInfo[ playerid ][ pAge ], pInfo[ playerid ][ pLiga ], pInfo[ playerid ][ pWantedLevel ] );
    format(string, sizeof(string), "%s, Job = '%d', JobContr = '%d', MotoLic = '%d', CarLic = '%d', BoatLic = '%d'", string, pInfo[ playerid ][ pJob ], pInfo[ playerid ][ pJobContr ], pInfo[ playerid ][ pLicMoto ], pInfo[ playerid ][ pLicCar ], pInfo[ playerid ][ pLicBoat ] );
    format(string, sizeof(string), "%s, FlyLic = '%d', GunLic = '%d', BoxStyle = '%d', Deaths = '%d', ConnectedTime = '%d'", string, pInfo[ playerid ][ pLicHeli ], pInfo[ playerid ][ pLicWeapon ], pInfo[ playerid ][ pBoxStyle ], pInfo[ playerid ][ pDeaths ], pInfo[ playerid ][ pOnTime ] );
    format(string, sizeof(string), "%s, Origin = '%s', PayDayHad = '%d', PayDay = '%d', Sex = '%s'", string, pInfo[ playerid ][ pOrigin ], pInfo[ playerid ][ pPayDayHad ], pInfo[ playerid ][ pPayCheck ], pInfo[ playerid ][ pSex ] );
    format(string, sizeof(string), "%s, pJobCar = '%d', Inventory = ' ', Weapons = '  ', JobSkill = '%d', JobLevel = '%d'", string, pInfo[ playerid ][ pSavings ], pInfo[ playerid ][ pJobSkill ], pInfo[ playerid ][ pJobLevel ] );
    format(string, sizeof(string), "%s, LeftTime = '%d', Donator = '%d', WalkStyle = '%d', TalkStyle = '%d', HeroineAddict = '%d'", string, pInfo[ playerid ][ pLeftTime ], pInfo[ playerid ][ pDonator ], pInfo[ playerid ][ pWalkStyle ], pInfo[ playerid ][ pTalkStyle ], pInfo[ playerid ][ pHeroineAddict ] );
    format(string, sizeof(string), "%s, AmfaAddict = '%d', MetamfaAddict = '%d', CocaineAddict = '%d'", string, pInfo[ playerid ][ pAmfaAddict ], pInfo[ playerid ][ pMetaAmfaineAddict ], pInfo[ playerid ][ pCocaineAddict ] );
    format(string, sizeof(string), "%s, playerLastLogOn = CURRENT_TIMESTAMP, playerSpawn = '%d', bSpawn = '%d', Card = '%s', ForumName = '%s'", string, _:pInfo[ playerid ][ pSpawn ], pInfo[ playerid ][ pBSpawn ], string2, string3 );
    format(string, sizeof(string), "%s, ExtazyAddict = '%d', PCPAddict = '%d', CrackAddict = '%d', OpiumAddict = '%d', Points = '%d'", string, pInfo[ playerid ][ pExtazyAddict ], pInfo[ playerid ][ pPCPAddict ], pInfo[ playerid ][ pCrackAddict ], pInfo[ playerid ][ pOpiumAddict ], pInfo[ playerid ][ pPoints ]);
    format(string, sizeof(string), "%s, HealthLevel = %d, StrengthLevel = %d, JobHours = %d, Hunger = %d, TotalPaycheck = %d ",string, pInfo[ playerid ][ pHealthLevel ],pInfo[ playerid ][ pStrengthLevel ], pInfo[ playerid ][ pJobHours ], pInfo[ playerid ][ pHunger ], pInfo[ playerid ][ pTotalPaycheck ]);
    format(string, sizeof(string), "%s, radio_slot = %d ", string, pInfo[ playerid ][ pRSlot ]);
    format(string, sizeof(string), "%s WHERE Name = '%s'", string, GetName(playerid));
    
    mysql_query(DbHandle, string, false);
    printf("Vartotojas buvo s�kmingai i�saugotas duomen� baz�je (u�klausa truko: %d)",strlen(string));
    return 1;
}
*/
main()
{
    ServerStartTimestamp = gettime();
    MySQLCheckConnection();
    print( "\n----------------------- Lithuanian ROLE-PLAY ---------------------" );
    print( "| GameMode creator: WARRIOR, Gedas" );
    print( "| GameMode translated by: Nova" );
    print( "-----------------------------------------------------------------\n" );
}


/*
stock PackPoints( playerid )
{
    new string[ 56 ];
    format( string, 56, "%s%d/%d/", string,
        pInfo[ playerid ][ pPoints  ][ 0 ],
        pInfo[ playerid ][ pPoints  ][ 1 ]);
    return string;
}
*/
/*
stock UpdateJacker( spot, vehs1 )
{
    // Taigi vehs1 reik�m�s:
    // 0 - Lengvoji
    // 1 - Dviratis

    // Bet mums nebeidomu kokia ta transporto  priemone
    #pragma unused vehs1

    JackerBoughtVehicles[ spot ][ AmountNeeded ] = 3;
    SetSellSpot:
    new randomShop = Iter_Random(VehicleShopIterator), vehicles[ MAX_VEHICLE_SHOP_VEHICLES ], count;

    // N�ra nei vieno turgaus :/
    if(randomShop == -1)
        return 1;
    for(new i = 0; i < MAX_VEHICLE_SHOP_VEHICLES; i++)
    {
        if(VehicleShops[ randomShop ][ VehicleModels ][ i ])
        {
            vehicles[ count++ ] = i;
        }

    }
    JackerBoughtVehicles[ spot ][ VehicleModel ] = VehicleShops[ randomShop ][ VehicleModels ][ vehicles[ random(count) ] ];
    // Jei tokia jau buvo...
    for(new i = 0; i < sizeof JackerBoughtVehicles; i++)
        if(i != spot && JackerBoughtVehicles[ i ][ VehicleModel ] == JackerBoughtVehicles[ spot ][ VehicleModel ])
            goto SetSellSpot;
    // Na valciu ir priekabu nevogsim...
    if(IsVehicleTrailer(JackerBoughtVehicles[ spot ][ VehicleModel ]) || IsABoat(JackerBoughtVehicles[ spot ][ VehicleModel ]))
        goto SetSellSpot;

    printf("Sellspot: %s", GetVehicleName( JackerBoughtVehicles[ spot ][ VehicleModel ] ));

    return 1;
}
*/


stock LoadServer( )
{
    //LoadFactions();
    //LoadMisc();
//  LoadTax();
    //LoadFuelInfo();
   // LoadSEnter();
    //LoadSellCars();
    //LoadGarbage();
    LoadIndustries();
    LoadTruckerCargo();
    LoadCommodities();
    // Turi b�ti PO to kai u�krautos IR prek�s IR industrijos.
    foreach(IndustryIterator, i)
        UpdateIndustryInfo(i);
    //LoadVehicleShops();
    return 1;
}
public OnPlayerEditAttachedObject( playerid, response, index, modelid, boneid,
                                   Float:fOffsetX, Float:fOffsetY, Float:fOffsetZ,
                                   Float:fRotX, Float:fRotY, Float:fRotZ,
                                   Float:fScaleX, Float:fScaleY, Float:fScaleZ )
{
    SendClientMessage(playerid, 0xFFFFFFFF, "Objekto redagavimas s�kmingai baigtas.");

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
/*
public OnPlayerEnterDynamicCP(playerid, checkpointid)
{
    if( pInfo[ playerid ][ pJob ] == JOB_DRUGS && IsPlayerInRangeOfPoint( playerid, 2, 748.0026,257.0667,27.0859 ) )
    {
        ShowPlayerDialog( playerid, 114, DIALOG_STYLE_LIST,"Narkotik� parduotuv�",
                "METAMFETAMINAS\n\
                AMFETAMINAS\n\
                KOKAINAS\n\
                HEROINAS", "TOLIAU", "AT�AUKTI" );
    }
    if( pInfo[ playerid ][ pJob ] == JOB_DRUGS && IsPlayerInRangeOfPoint( playerid, 2, -279.4338,2722.4390,62.4920 ) )
    {
        ShowPlayerDialog( playerid, 160, DIALOG_STYLE_LIST,"Narkotik� parduotuv�",
                "EXTAZY\n\
                PCP\n\
                Krekas\n\
                Opijus", "TOLIAU", "AT�AUKTI" );
    }
    return 0;
}*/


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
    // Timeri� nustatymai
    SetTimer("Sekunde", 1000, 1);
    SetTimer("MinTime", 60000, 1);
    //SetTimer("Spidometras", 250, 1 );
    //SetTimer("Drugs", 15*60000, 1);
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
//=============================[ Prijungiame server� naudojamus NPC bot'us ]================================
/*
    NPCPlane = AddStaticVehicle(592, 1960.2163, -2434.0457, 12.5013, 180.0000, -1, -1);
    sVehicles[ NPCPlane ][ Id          ] = 0;
    sVehicles[ NPCPlane ][ Model       ] = 592;
    sVehicles[ NPCPlane ][ SpawnX       ] = 1960.2163;
    sVehicles[ NPCPlane ][ SpawnY       ] = -2434.0457;
    sVehicles[ NPCPlane ][ SpawnZ      ] = 12.5013;
    sVehicles[ NPCPlane ][ SpawnA      ] = 180.0000;
    new engine, lights, alarm, doors, bonnet, boot, objective;
    GetVehicleParamsEx(NPCPlane, engine, lights, alarm, doors, bonnet, boot, objective);
    SetVehicleParamsEx(NPCPlane, VEHICLE_PARAMS_ON, VEHICLE_PARAMS_ON, alarm, VEHICLE_PARAMS_ON, bonnet, boot, objective);
*/
    //LoadStaticVehicles();
    /*
    for(new i = 1; i < MAX_VEHICLES; i++)
        if(sVehicles[ i ][ Model ] == 538)
        {
            NPCTrain = i;
            printf("Train found at index:%d", i);
            break;
        }
        */


    //=============================[ U�raunam serverio nekilnojam�ji turt� ir kt. kas susij� su tuo]================================
    LoadServer( );
    //=============================[ U�kraunam transporto priemoni� vagim� ]================================
  /*  UpdateJacker( 0, 0 );
    UpdateJacker( 1, 0 );
    UpdateJacker( 2, 2 );*/
    //=============================[ Serverio darb� 3D label ]================================
    //for ( new i = 0; i < MAX_JOBS; i++ )
    //{
    //    new mini[ 130 ];
    //    format( mini, 126, "{33AA11}%s\n{FFFFFF}Darbo kontraktas: {FFBB00}%d {FFFFFF}atlyginimai\n{FFBB00}/takejob", pJobs[ i ][ Name ], pJobs[ i ][ Contr ] );
    //    CreateDynamic3DTextLabel( mini, COLOR_WHITE, pJobs[ i ][ Job_x ], pJobs[ i ][ Job_y ], pJobs[ i ][ Job_z ], 15, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, 1, 0, 0, -1, 15.0);
    //}
    //
    //CreateDynamicCP(748.0026, 257.0667, 27.0859, 2.0, -1, -1, -1, 6.0 );
    //CreateDynamicCP(-279.4338, 2722.4390, 62.4920, 2.0, -1, -1, -1, 6.0 );
	//CreateDynamicCP(1803.4606,-1520.4922,5700.4302, 2.0, -1, -1, -1, 3.0 );
    
    //=============================[ Pickup'ai ]================================
   // Pickups[ 1 ] = CreateDynamicPickup(1240, 2, 1810.2020,-1583.3362,5703.9175); // Gyvybi� atsistatym� pickup kal�jime

    //------------------------[ 3DTextLabeliai. U�ra�ai, ��jimai. ]------------------------------------------
	//CreateDynamic3DTextLabel("Los Santos License Center\nTeorijos ir praktikos egzaminai\n{FFFFFF}/license",COLOR_NEWS, 1491.0953,1306.8651,1093.2891 ,7.0, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, 1, 0, 0, -1, 15.0);
    //CreateDynamic3DTextLabel("Nauj� automobili� s�lonas\nParduodam� automobili� s�ra�as\nKomanda: {FFFFFF}/v buy",COLOR_NEWS, 2131.8079,-1151.2266,24.0707 ,7.0, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, 1, 0, 0, -1, 15.0);
    //CreateDynamic3DTextLabel("Sunki�j� tr. priemoni� salonas\nParduodam� automobili� s�ra�as\nKomanda: {FFFFFF}/v buy",COLOR_NEWS, 2748.5361,-2451.3025,13.6599 ,7.0, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, 1, 0, 0, -1, 15.0);
    //CreateDynamic3DTextLabel("Motocikl� ir dvira�i� parduotuv�\nParduodam� preki� s�ra�as\nKomanda: {FFFFFF}/v buy",COLOR_NEWS, 1738.9440,-1269.4951,13.5430 ,7.0, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, 1, 0, 0, -1, 15.0);
    //CreateDynamic3DTextLabel("Los Santos uosto salonas\nParduodam� laiv�\nKomanda: {FFFFFF}/v buy",COLOR_LIGHTRED2,-444.3486,1154.1063,1.7273,7.0, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, 1, 0, 0, -1, 15.0);
    //CreateDynamic3DTextLabel("Los Santos savivaldyb�\nKomanda:{FFFFFF}/duty",COLOR_NEWS, 1500.8645,-1814.7734,2410.8157 ,7.0, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, 1, 0, 0, -1, 15.0);
    //CreateDynamic3DTextLabel("Los Santos Prison Yard\nTIK DARBUOTOJAMS\n�va�iavimui /enter",COLOR_POLICE,1753.5140,-1595.8026,13.5380, 20.0, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, false, -1, -1, -1, 15.0);
	//CreateDynamic3DTextLabel("Los Santos Prison Yard\nTIK DARBUOTOJAMS\nI�va�iavimui naudokite /exit",COLOR_POLICE,I5I^, 20.0, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, false, -1, -1, -1, 15.0);
    //CreateDynamic3DTextLabel("Los Santos Fire Departament\nTr. priemoni� gara�as\n�va�iavimui /enter",COLOR_LIGHTRED,1284.9084,-1346.3730,13.6000, 20.0, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, false, -1, -1, -1, 15.0);
	//CreateDynamic3DTextLabel("Los Santos Fire Departament\nTr. priemoni� gara�as\nI�va�iavimui naudokite /exit",COLOR_LIGHTRED,-1763.6812,984.6740,22.0003, 20.0, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, false, -1, -1, -1, 15.0);	
    //CreateDynamic3DTextLabel("�ia galite kovos stiliu\n ra�ykite {FFBB00}/learnfight",COLOR_WHITE,754.9053,-40.0628,1000.5859,7.0, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, false, -1, -1, -1, 15.0);
    //CreateDynamic3DTextLabel("Privatus, u�daras sand�lys\n Tik privatiems klientams\nKomanda:{FFFFFF}/buyseeds",COLOR_NEWS,-2172.5056,679.8398,55.1615,7.0, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, 1, 0, 0, -1, 15.0);
    //CreateDynamic3DTextLabel("Privatus, u�daras sand�lys\n Joki� pa�alini�\nKomanda: {FFFFFF}/buymats",COLOR_NEWS,-2074.3081,-2246.5073,31.6890,7.0, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, 1, 0, 0, -1, 15.0);
    //CreateDynamic3DTextLabel("Los Santos reklamos skyrius\nSkelbkite,ra�ykite savo skelbimus � eter�\nKomanda: {FFFFFF}/ad",COLOR_NEWS,1128.8257,-1489.5168,22.7690,7.0, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, 1, 0, 0, -1, 15.0);
    //CreateDynamic3DTextLabel("Tr. priemon�s sutvarkymas\n\nKomanda: {FFBB00}/fix [SPALVA1] [SPALVA2] / KAINA: 500 $",COLOR_WHITE,2075.5986,-1831.0374,13.5545,8.0, INVALID_PLAYER_ID, INVALID_VEHICLE_ID, false, 0, 0, -1, 15.0);

    CreateAllTrash( );
    //Produkcija( );
    SetTimer( "CreateAllTrash", 5*60000, 1 );

    
    //{Objeckto ID,Stovi:X,Stovi:Y,Stovi:Z,Rotacija:X,Rotacija:Y,Rotacija:Z,Judes:X,Judes:Y,Judes:Z,Rotacija:X,Rotacija:Y,Rotacija:Z,Frakcijos ID}
    for(new obj = 0; obj < sizeof CloseGate; obj ++)
        Gates2[obj] = CreateObject(floatround(CloseGate[obj][0],floatround_round),CloseGate[obj][1],CloseGate[obj][2],CloseGate[obj][3],CloseGate[obj][4],CloseGate[obj][5],CloseGate[obj][6]);

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
    //=============================[ Sutvarkome narkotikus transporto priemon�je ]================================
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


    //=============================[ Sutvarkome transporto priemoni� degalus ir savininkus ]================================
    for( new car = 0; car < MAX_VEHICLES; car ++ )
    {
        cInfo[ car ][ cOwner] = 0;
        strmid( cInfo[ car ][ cNumbers ], "J", 0, 1, 2 );
    }
    printf( "Serveryje sukurtas objekt� skai�ius: %d", CountDynamicObjects( ) );
    printf( "Serveryje sukurt� 3D Text skai�ius: %d", CountDynamic3DTextLabels( ) );
    printf( "Serveryje sukurt� dinamini� aren� skai�ius: %d", CountDynamicAreas( ) );
    printf( "Visi �laubaumai nuleisti ir atrakinti.");


    BlindfoldTextdraw = TextDrawCreate(0.0, 0.0, "box");
    TextDrawTextSize(BlindfoldTextdraw, 640.0, 0.0);
    TextDrawLetterSize(BlindfoldTextdraw, 0.0, 100.0);
    TextDrawBoxColor(BlindfoldTextdraw, 0x000000FF);
    TextDrawUseBox(BlindfoldTextdraw, 1);

    //=============================[ Serverio laiko ir atlyginim� nustatym� sutvarkymas ]================================
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

/*public OnPlayerAirbreak(playerid)
{
    if( GetPlayerAdminLevel(playerid) >= 5 )
        return true;

    if ( IsPlayerInAnyVehicle( playerid ) )
        KickPlayer( "AC", playerid, "AirBreak tr. priemon�je." );
    else
        KickPlayer( "AC", playerid, "AirBreak vaik�tant/b�giojant" );
        
    return 1;
}
*/
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
    /*switch( random( 3 ) )
    {
        case 0:
        {
            InterpolateCameraPos(playerid, 1616.193847, -1611.084350, 103.193656, 1034.297485, -1283.787353, 116.283706, 18000);
            InterpolateCameraLookAt(playerid, 1612.181518, -1613.264404, 101.156806, 1036.176879, -1288.001098, 114.356887, 5000);
        }
        case 1:
        {
            InterpolateCameraPos(playerid, 2646.913574, -1597.005493, 43.657371, 1803.948364, -1426.308959, 48.698162, 20000);
            InterpolateCameraLookAt(playerid, 2649.125732, -1601.473632, 43.280689, 1808.733764, -1425.373535, 47.591224, 4000);
        }
        case 2:
        {
            InterpolateCameraPos(playerid, 126.606079, -1787.460937, 95.428718, 626.815612, -973.070617, 124.867210, 20000);
            InterpolateCameraLookAt(playerid, 128.530899, -1791.819824, 93.913764, 624.234130, -977.284240, 124.104843, 4000);
        }
    }
    */
    /*new pName[MAX_PLAYER_NAME+1];
    GetPlayerName(playerid,pName,MAX_PLAYER_NAME+1);
    if(!strcmp(pName,"npc1",true))
    {
        SetPlayerSkin( playerid, 69 );
        return 1;
    }
    if(!strcmp(pName,"npc2",true))
    {
        SetPlayerSkin( playerid, 12 );
        return 1;
    }
    */
    //if( PlayerOn[ playerid ] )
    //    SpawnPlayerEx( playerid );
    #if defined DEBUG
        printf("OnPlayerRequestClass(%s, %d) end", GetName(playerid), classid);
    #endif
    return 1;
}

/*stock GetPlayerSqlId(playerid)
    return pInfo[ playerid ][ pMySQLID ];
*/

stock GetPlayerHouseKey(playerid)
    return pInfo[ playerid ][ pHouseKey ];

stock Float:GetPlayerMaxHealth(playerid)
    return 100.0 + pInfo[ playerid ][ pHealthLevel ] * 3; 

stock GetPlayerSavings(playerid)
    return pInfo[ playerid ][ pSavings ];

stock GetPlayerTotalPaycheck(playerid)
    return pInfo[ playerid ][ pTotalPaycheck ];

stock SetPlayerTotalPaycheck(playerid, value)
    return pInfo[ playerid ][ pTotalPaycheck ] = value;


//stock GetPlayerPhoneNumber(playerid)
//    return pInfo[ playerid ][ pPhone ];


stock GetPlayerIP(playerid)
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
/*public Audio_OnClientConnect( playerid )
{
    RadioName[ playerid ] = 99;
    return 1;
}
public Audio_OnClientDisconnect( playerid )
{
    RadioName[ playerid ] = 99;
    return 1;
}
public Audio_OnRadioStationChange( playerid, station )
{
    return 1;
}
*/
public OnPlayerConnect(playerid)
{
    #if defined DEBUG
        printf("[debug] OnPlayerConnect(%s)", GetName(playerid));
    #endif


    //OnLookupComplete(playerid);
    //MySQL_Check_Account( playerid );
    //CheckBan(playerid);	
//=============================[ Iconos �em�lapyje rodomos visiems ]================================
  /*  SetPlayerMapIcon( playerid, 70, fInfo[ 2 ][ fSpawn ][ 0 ], fInfo[ 2 ][ fSpawn ][ 1 ] ,fInfo[ 2 ][ fSpawn ][ 2 ], 22, 0, MAPICON_LOCAL ); //Ligonin�
    SetPlayerMapIcon( playerid, 71, 2861.1670,-1405.5068,11.7382, 52, 0, MAPICON_LOCAL ); //Bankas
    SetPlayerMapIcon( playerid, 72, 1671.8431,-1858.0848,13.5313, 27, 0, MAPICON_LOCAL ); //Mechanikai
    SetPlayerMapIcon( playerid, 73, fInfo[ 5 ][ fSpawn ][ 0 ], fInfo[ 5 ][ fSpawn ][ 1 ] ,fInfo[ 5 ][ fSpawn ][ 2 ], 20, 0, MAPICON_LOCAL ); //Goverment
    SetPlayerMapIcon( playerid, 74, 2131.8408,-1151.3246,24.0603, 55, 0, MAPICON_LOCAL ); //CarShop
    SetPlayerMapIcon( playerid, 75, fInfo[ 1 ][ fSpawn ][ 0 ], fInfo[ 1 ][ fSpawn ][ 1 ] ,fInfo[ 1 ][ fSpawn ][ 2 ], 30, 0, MAPICON_LOCAL ); //PD
    SetPlayerMapIcon( playerid, 76, 2281.1189,-2365.0647,13.5469, 51, 0, MAPICON_LOCAL ); //KROVINIAI
    SetPlayerMapIcon( playerid, 78, fInfo[ 4 ][ fSpawn ][ 0 ], fInfo[ 4 ][ fSpawn ][ 1 ] ,fInfo[ 4 ][ fSpawn ][ 2 ], 56, 0, MAPICON_LOCAL ); //San News
   // SetPlayerMapIcon( playerid, 80, -368.4724,1510.7081,76.3117, 55, 0, MAPICON_LOCAL ); //CarShop BIKE
    SetPlayerMapIcon( playerid, 81, 1491.0953,1306.8651,1093.2891, 36, 0, MAPICON_LOCAL ); //DMV
    SetPlayerMapIcon( playerid, 82, 1368.7064,-1279.9117,13.5469, 18, 0, MAPICON_LOCAL ); //GunShop
    */
	
    for(new car = 0; car < 21; car++)
        pInfo[ playerid ][ pCar ][ car ] = 0;

    //new
    //    string[ 128 ];

    /*
    for(new i = 0; i < 47; i++)
    {
        if( GetSlotByID( i ) == 2 || GetSlotByID( i ) == 3 || GetSlotByID( i ) == 4 || GetSlotByID( i ) == 5 || GetSlotByID( i ) == 6 )
        {
            format(string, sizeof(string), "%dbone", i );
            SetPVarInt ( playerid, string, 0 );
        }
    }
    */

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
/*
    // Tr. priemon�s greitis, degalai ir kita.
    Greitis[ playerid ] = CreatePlayerTextDraw( playerid, 535.0, 350.0, "_" );
    PlayerTextDrawFont            ( playerid, Greitis[ playerid ], 2 );
    PlayerTextDrawLetterSize      ( playerid, Greitis[ playerid ], 0.2, 1.3 );
    PlayerTextDrawSetShadow       ( playerid, Greitis[ playerid ], 0 );
    PlayerTextDrawUseBox          ( playerid, Greitis[ playerid ], 1 );
    PlayerTextDrawBoxColor        ( playerid, Greitis[ playerid ], 0x00000044 );
    PlayerTextDrawSetOutline      ( playerid, Greitis[ playerid ], 0 );
*/
    
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

stock NullPlayerInfo( playerid )
{
//    pInfo[ playerid ][ pLevel    ] = 1;
    //pInfo[ playerid ][ pMoney    ] = 0;
    //pInfo[ playerid ][ pBank     ] = 0;
    pInfo[ playerid ][ pExp      ] = 0;
    //PlayerOn[ playerid ] = false;
  //  pInfo[ playerid ][ pAdmin    ] = 0;
//    PlayerMoney     [ playerid ] = 0;

//    pInfo[ playerid ][ pSkin     ] = 216;
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

//    TrashMission    [ playerid ] = TRASH_MISSION_NONE;
//    CurrentTrashCp  [ playerid ] = 0;
    //IsCarryingTrash[ playerid ] = false;
   // DeletePVar(playerid, "TrashMission_Vehicle");
    InfoTextTimer[ playerid ] = -1;

    VehicleLoadTimer[ playerid ] = -1;
	VehicleLoadTime[ playerid ] = 0;

    IsFillingFuel[ playerid ] = false;
/*
    for(new i = 0; i < sizeof(PlayerWornItems[]); i++)
        PlayerWornItems[ playerid ][ i ] = -1;

    for(new i = 0; i < sizeof(PlayerAttachedWeapons[]); i++)
    {
        RemovePlayerAttachedObject(playerid, PlayerAttachedWeapons[ playerid ][ i ][ ObjectSlot ]);
        PlayerAttachedWeapons[ playerid ][ i ][ WeaponId ] = 0;
    }

    for(new i = 0; i < 10; i++) 
        if(IsPlayerAttachedObjectSlotUsed(playerid, i))
            RemovePlayerAttachedObject(playerid, i);
*/
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
            format(string,sizeof(string),"%s paliko server� (�vyko kliento klaida/nutr�ko ry�ys).",name);
            GetPlayerPos(playerid,pInfo[playerid][pCrashPos][0],pInfo[playerid][pCrashPos][1],pInfo[playerid][pCrashPos][2]);

            pInfo[playerid][pVirWorld] = GetPlayerVirtualWorld(playerid);

            pInfo[playerid][pInt] = GetPlayerInterior(playerid);
            pInfo[playerid][pCrash] = 1;
        }
        case 1: format(string,64,"%s paliko server� (Klientas atsijung�).",name);
        case 2: format(string,64,"%s paliko server� (Klientas i�mestas).",name);
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
       /* foreach(Player,id)
        {
            if ( IsPlayerInDynamicArea( id, aInfo[ playerid ][ aArea ] ) )
                Set3DAudioForPlayer( id, "", playerid );
        }
        */
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
    // Jei pyl�si kur�, nemokamo jam neduosim
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
    /*
    if(FirstSpawn[ playerid ])
    {
        
    }
    else 
    {
        // Jei spectatino ir turi ginkl� reikia jam juos atiduoti.
        if(GetPVarInt(playerid, "SpectateWeaponCount"))
        {
            new buffer[128], weapons[ 26 ], specifier[16], count = GetPVarInt(playerid, "SpectateWeaponCount")*2;
            GetPVarString(playerid, "SpectateWeaponString", buffer, sizeof(buffer));
            format(specifier, sizeof(specifier), "p<|>a<i>[%d]", count);
            sscanf(buffer, specifier, weapons);

            for(new i = 0; i < count; i+=2)
                GivePlayerWeapon(playerid, weapons[ i ], weapons[ i + 1]);
        }
    }
    */
    
   // ObjUpdate[ playerid ] = true;

   /* if( pInfo[ playerid ][ pMask ] == 0 )
    {
        foreach(Player,i)
        {
            ShowPlayerNameTagForPlayer(i, playerid, pInfo[playerid][pMask]);
        }
    }*/
  /*  if( GetPlayerLevel(playerid) == 0 )
    {
        SetPlayerLevel(playerid, 1);
        SetPlayerScore( playerid, 1 );
        SetPlayerTeam( playerid, playerid );
        return 1;
    }*/
   // SetPlayerTeam( playerid, playerid );
    //SetPlayerScore( playerid, GetPlayerLevel(playerid));
    //SetTimerEx("GiveWeapons", 2000, 0, "d", playerid );
    #if defined DEBUG
        printf("[debug] OnPlayerSpawn(%s) end", GetName(playerid));
    #endif
    return 1;
}
/*
FUNKCIJA:GiveWeapons(playerid)
{
    if (!IsPlayerConnected(playerid)) 
        return 1;
    for ( new gun = 0; gun < MAX_SAVED_WEAPONS; gun++ )
    {
        if(pInfo[ playerid ][ pGun ][ gun ] > 0 && pInfo[ playerid ][ pAmmo ][ gun ] > 0)
        {
            GivePlayerWeapon(playerid, pInfo[ playerid ][ pGun ][ gun ], pInfo[ playerid ][ pAmmo ][ gun ]);

            pInfo[ playerid ][ pGun  ][ gun ] = 0;
            pInfo[ playerid ][ pAmmo ][ gun ] = 0;
        }
    }
    return 1;
}
*/

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
        printf( "Tr. priemon�s MySQL ID: %d (%s), sunaikinusiojo ID: %d (%s)", cInfo[ vehicleid ][ cID ], GetVehicleName( GetVehicleModel( vehicleid ) ), killerid, GetName( killerid ));
        format( string, 126, "AdmWarn: ([%d]%s[%d]) sunaikino tr. priemon�.", killerid, GetName( killerid ) );
        SendAdminMessage( COLOR_ADM, string );
    }
    // Jei ka�k� ve��, krovinys prarastas :(
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

            format           ( string, 126,"* D�mesio, Tr. priemon� buvo sunaikinta, duomenys: %s, laikas ir data: %d-%d-%d %d:%d:%d, sunaikinusiojo ID: %d (%s)",cInfo[ vehicleid ][ cName ], time[ 3 ], time[ 4 ], time[ 5 ],time[ 0 ], time[ 1 ], time[ 2 ], killerid, GetName( killerid ));
            SendClientMessage( carowner, COLOR_FADE1, string);
            pInfo[ carowner ][ pCarGet ] --;
        }
        nullVehicle   ( vehicleid );
    }
    return 1;
}
public OnPlayerText(playerid, text[])
{
    new string[ 256 ],
        zone[30];

    GetPlayer2DZone(playerid, zone, 30);

    if(Mires[playerid] > 0)
    {
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �iuo metu J�s esate komos/kritin�je b�senoje.");
        return false;
    }
    if(Mute[playerid] == true)
    {
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jui yra u�drausti kalb�ti (/mute) susisiekite su Administratoriumi d�l draudimo pa�alinimo (/unmute).");
        return false;
    }

    if ( AfkCheck[ playerid ] != 0 )
        AfkCheck[ playerid ] = 0;

    if (TalkingLive[playerid] != 255) // Interviu sistema
    {
        if (PlayerToPlayer(5.0, playerid, TalkingLive[playerid]))
        {
            format(string,179, "[SAN NEWS] Tiesioginis intervi� su %s: %s", GetName( playerid ), text );
//            SendNEWS(COLOR_GREEN,string);
            return false;
        }
        else
        SendClientMessage(playerid,GRAD, "Klaida, J�s� kalbinamas asmuo yra per toli nuo J�s�, tad negalite t�sti interviu kol jis nebus �alia. Nutraukimui: /live." );
        return false;
    }
    new Float:dist = 9.0;

    format( string, 144, "%s", text );
    SetPlayerChatBubble(playerid, string, COLOR_FADE1, dist, 10000);
    format(string,179," %s sako: %s", GetPlayerNameEx( playerid ), text);
    if ( IsPlayerInAnyVehicle(playerid))
    {
        new vehid = GetPlayerVehicleID(playerid);
        if(VehicleHasWindows(GetVehicleModel(vehid)))
        {
            if ( Windows[ vehid ] )
                format( string, 179, "(Atidarytas langas (-ai)) %s", string );
            else
            {
                format( string, 179, "(U�darytas langas (-ai)) %s", string );
                dist = 1.0;
            }
        }
    }
    ProxDetector(dist, playerid, string,COLOR_FADE1,COLOR_FADE2,COLOR_FADE3,COLOR_FADE4,COLOR_FADE5);
    new times = strlen(text);
    if(times > 100)
        times = 100;
    if ( gPlayerUsingLoopingAnim[ playerid ] == false && pInfo[ playerid ][ pTalkStyle ] == 0 )
    {
        ApplyAnimation( playerid, "PED", "IDLE_CHAT", 4.0, 1, 1, 1, 1, 1 );
        SetTimerEx("UnChat", times * 100, false, "d",playerid);
    }
    if ( gPlayerUsingLoopingAnim[ playerid ] == false && pInfo[ playerid ][ pTalkStyle ] == 1 )
    {
        ApplyAnimation( playerid, "MISC", "IDLE_CHAT_02", 4.0, 1, 1, 1, 1, 1 );
        SetTimerEx("UnChat", times * 100, false, "d",playerid);
    }
    if (gPlayerUsingLoopingAnim[ playerid ] == false && pInfo[ playerid ][ pTalkStyle ] == 2 )
    {
        ApplyAnimation( playerid, "GHANDS", "gsign1", 4.0, 1, 1, 1, 1, 1 );
        SetTimerEx("UnChat", times * 100, false, "d",playerid);
    }
    if ( gPlayerUsingLoopingAnim[ playerid ] == false && pInfo[ playerid ][ pTalkStyle ] == 3 )
    {
        ApplyAnimation( playerid, "GANGS", "prtial_gngtlkD", 4.0, 1, 1, 1, 1, 1 );
        SetTimerEx("UnChat", times * 100, false, "d",playerid);
    }
    if ( gPlayerUsingLoopingAnim[ playerid ] == false && pInfo[ playerid ][ pTalkStyle ] == 4 )
    {
        ApplyAnimation( playerid, "GANGS", "prtial_gngtlkH", 4.0, 1, 1, 1, 1, 1 );
        SetTimerEx("UnChat", times * 100, false, "d",playerid);
    }
    return false;
}

FUNKCIJA:UnChat(playerid)
{
    StopLoopingAnim(playerid);
    return true;
}


CMD:fpv(playerid,params[])
{
    #pragma unused params
    if(!IsPlayerInAnyVehicle(playerid)) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nor�dami naudoti �� veiksm�, privalote sed�ti/b�tii tr. priemon�je. ");
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
    SendClientMessage( playerid, COLOR_GREEN, "|________________________________Serverio komand� s�ra�as________________________________|");
    SendClientMessage( playerid, COLOR_FADE2, "  ROLE-PLAY VEIKSMAI: /me /do /try");
    SendClientMessage( playerid, COLOR_FADE1, "  IC IR OOC KALB�JIMO KANALAI: IC - /f /r /s /low /g /ame /w /cw /ds. OOC - /b (/o)oc /pm.");
    SendClientMessage( playerid, COLOR_FADE2, "  VEIK�JO VALDYMAS: /levelup /fpv /stats /inv /invweapon /transfer /pay /anims /learnfight /stop /die /(lic)ences");
    SendClientMessage( playerid, COLOR_FADE1, "  DARBAS IR FRAKCIJA: /leavefaction /takejob /leavejob");	
    SendClientMessage( playerid, COLOR_FADE2, "  ADMINISTRACIJA IR PAGALBA: /admins /moderators /(re)port /askq");
    SendClientMessage( playerid, COLOR_FADE1, "  TURTO PIRKIMAS: /buy /buygun /buyhouse /buybiz");
    SendClientMessage( playerid, COLOR_FADE2, "  KITOS KOMANDOS: /bail /id /make /bank /note /knock /maxspeed /charity /lock /accept /fines /vehiclefines /seatbelt");
    SendClientMessage( playerid, COLOR_FADE1, "  KITOS KOMANDOS: /lastad /bell /setcard /ccard /windows /trunk /bonnet /sid /savings");
//    if ( pInfo[ playerid ][ pJob ] == JOB_MECHANIC )
//    SendClientMessage( playerid, COLOR_LIGHTRED2, "  MECHANIKO KOMANDOS: /repair /repaint /addwheels");
    //if ( pInfo[ playerid ][ pJob ] == JOB_TRASH )
    //  SendClientMessage( playerid, COLOR_LIGHTRED2, "  �I�K�LININKO: /startmission /endmission /takegarbage /throwgarbage");
//    if ( pInfo[ playerid ][ pJob ] == JOB_DRUGS )
 //     SendClientMessage( playerid, COLOR_LIGHTRED2, "  NARKOTIK� PREKEIVIO KOMANDOS: /buyseeds /cutweed");
    if ( pInfo[ playerid ][ pJob ] == JOB_GUN )
      SendClientMessage( playerid, COLOR_LIGHTRED2, "  GINKL� GAMINTOJO KOMANDOS: /weaponlist /make");
    if ( pInfo[ playerid ][ pJob ] == JOB_TRUCKER )
	{ 
      SendClientMessage( playerid, COLOR_LIGHTRED2, "  KROVINI� PERVE�IMO VAIRUOTOJO KOMANDOS: /tpda /cargo /killcheckpoint"),
      SendClientMessage( playerid, COLOR_LIGHTRED2, "  /tpda - krovini� tvarkara�tis | /cargo - krovini� valdymas | /killcheckpoint - esame CP panaikinimas.");	
	}
   // if ( pInfo[ playerid ][ pJob ] == JOB_JACKER )
//		SendClientMessage( playerid, COLOR_LIGHTRED2, "  TR. PRIEMON�S VOGIMO KOMANDOS: /sellcar /info /spots");
//	if ( PlayerFaction( playerid ) == 1 )
		//SendClientMessage( playerid, COLOR_POLICE, "  LOS SANTOS POLICIJOS DEPARTAMENTAS: /policehelp");
   /* if ( PlayerFaction( playerid ) == 2 ) 
	{
        SendClientMessage( playerid, COLOR_LIGHTRED2, "|________________________________Mediko komandos________________________________|"),
		SendClientMessage( playerid, COLOR_WHITE, "  /rb /rrb /drag /fdgear /duty /heal /takefmoney /checfkbudget /flist /tlc");     
	}
    if ( PlayerFaction( playerid ) == 5 ) 
	{
        SendClientMessage( playerid, COLOR_GREEN2, "|________________________Savivaldyb�s darbuotojo komandos________________________|"),
		SendClientMessage( playerid, COLOR_WHITE, "  /duty /takemoney /takefmoney /checfkbudget /checkbudget /flist");     
	}*/
    if ( pInfo[ playerid ][ pLead ] > 1 )
        SendClientMessage( playerid, COLOR_WHITE, "  FRAKCIJOS VALDYMAS: /invite /uninvite /setrank /flist /nof /togf" );
    if ( GetPlayerAdminLevel(playerid) > 0 )
    SendClientMessage( playerid, COLOR_WHITE, "  ADMINISTRATORIUS: Pokalbiai - /a, darbas - /aduty, komandos - /ahelp /togadmin");
    if ( pInfo[ playerid ][ pDonator ] > 0 )
        SendClientMessage( playerid, COLOR_FADE1, "  REM�JAS: /blockpm /togpm /walkstyle /talkstyle /mask");
    SendClientMessage( playerid, COLOR_FADE2, "  SISTEM� PAGALBA/KOMANDOS: /v /radiohelp /phonehelp /phonebookhelp /bizhelp /househelp ");
    SendClientMessage( playerid, COLOR_FADE1, "  SISTEM� PAGALBA/KOMANDOS: /vradio /garagehelp /fishinghelp /toghelp /gunhelp");	
    SendClientMessage( playerid, COLOR_GREEN2, "________________________Daugiau informacijos________________________");
    SendClientMessage( playerid, COLOR_FADE1, "  Vis informacija pateikta m�s� diskusij� forume forum.ltrp.lt");
    SendClientMessage( playerid, COLOR_FADE2, "  Jei prireik� pagalbos, visados galite klausti naudodami komanda /askq");
    //Produkcija( );
    return 1;
}
CMD:gunhelp(playerid)
{
    SendClientMessage( playerid, COLOR_GREEN, "|__________________GINKL� KOMANDOS__________________|");
    SendClientMessage( playerid, COLOR_WHITE, "  /leavegun - numeta ant �em�s rankoje laikom� ginkl�, kuri galite paiimti su /grabgun.");
    SendClientMessage( playerid, COLOR_FADE1, "  /grabgun - paiiima ant �em�s rodom� ginkl�");
    SendClientMessage( playerid, COLOR_WHITE, "  /buygun - naudojama ginkl� parduotuv�je");
    SendClientMessage( playerid, COLOR_GREEN, "__________________________________________________________________");	
    return 1;
}
CMD:phonebookhelp(playerid)
{
    SendClientMessage( playerid, COLOR_GREEN, "|__________________TELEFON� KNYGOS KOMANDOS__________________|");
    SendClientMessage( playerid, COLOR_WHITE, "  /phonebook - J�s� telefon� adres� s�ra�as.");
    SendClientMessage( playerid, COLOR_FADE1, "  /addcontact - prid�site kontakt� � telefon� knyg�");
    SendClientMessage( playerid, COLOR_WHITE, "  /deletecontact - i�trinsite kontakt� i� telefon� knygos");
    SendClientMessage( playerid, COLOR_GREEN, "__________________________________________________________________");	
    return 1;
}
CMD:toghelp(playerid)
{
    SendClientMessage( playerid, COLOR_GREEN, "|__________________KANAL� I�JUNGIMO KOMANDOS__________________|");
    SendClientMessage( playerid, COLOR_WHITE, "  /togooc - nebematysite OOC kanal� ir informacijos");
    SendClientMessage( playerid, COLOR_FADE1, "  /tognames - i�jungsite rodomus veik�j� vardus");
    SendClientMessage( playerid, COLOR_WHITE, "  /tognews - nebematysite SAN News kanalo skelbiam� naujien�");
    SendClientMessage( playerid, COLOR_FADE1, "  /togpm - nebegal�site gauti priva�i� �inu�i�");
    SendClientMessage( playerid, COLOR_FADE1, "  KITOS KOMANDOS: /togf /togq /togadmin");
    SendClientMessage( playerid, COLOR_GREEN, "__________________________________________________________________");	
    return 1;
}
/*CMD:policehelp(playerid)
{
    if ( PlayerFaction( playerid ) == 1 )
    {
        SendClientMessage( playerid, COLOR_POLICE, "|__________________LOS SANTOS POLICIJOS DEPARTAMENTO__________________|"),
        SendClientMessage( playerid, COLOR_WHITE, "  PATIKRINIMO KOMANDOS: /frisk /checkalco /fines /vehiclefines /checkspeed /mdc /take"),
        SendClientMessage( playerid, COLOR_FADE1, "  BUD�JIMO PRAD�IOS KOMANDOS: /duty /wepstore"),
        SendClientMessage( playerid, COLOR_WHITE, "  SU�MIMO KOMANDOS: /tazer /cuff /drag"),
        SendClientMessage( playerid, COLOR_FADE1, "  GAUDYNI�/SITUACIJ� KOMANDOS: /bk /rb  /rrb /m /tlc /ram"),
        SendClientMessage( playerid, COLOR_WHITE, "  KOMANDOS NUBAUSTI: /fine /vehiclefine /arrest /prison /arrestcar /licwarn "),
        SendClientMessage( playerid, COLOR_FADE1, "  KITOS KOMANDOS: /flist /setunit /delunit /police /delarrestcar /jobid /cutdownweed"),
        SendClientMessage( playerid, COLOR_WHITE, "  DRABU�IAI/APRANGA: /vest /badge /rbadge /pdclothes"),
        SendClientMessage( playerid, COLOR_POLICE, "____________________________________________________________________________");	
    }	
    return 1;
}
*/
CMD:radiohelp(playerid)
{
    SendClientMessage( playerid, COLOR_GREEN, "|__________________RACIJOS NAUDOJIMO KOMANDOS__________________|");
    SendClientMessage( playerid, COLOR_FADE1, "  /r [TEKSTAS] - IC pokalbi� per racij� kanalas." );
    SendClientMessage( playerid, COLOR_WHITE, "  /rlow [TEKSTAS] - IC pokalbi� per racij� kanalas kalbant tyliai" );
    SendClientMessage( playerid, COLOR_FADE1, "  /setchannel [1-3] [RACIJOS KANALAS] - racijos kanalo nustatymas/keitimas./setslot" );
    SendClientMessage( playerid, COLOR_WHITE, "  /setslot [1-3] - nustatyti viet� kanalui." );
    SendClientMessage( playerid, COLOR_GREEN, "__________________________________________________________________" );	
    return 1;
}
CMD:phonehelp(playerid)
{
    SendClientMessage( playerid, COLOR_GREEN, "|__________________MOBILAUS TELEFONO NAUDOJIMAS__________________|");
    SendClientMessage( playerid, COLOR_LIGHTRED2, "Nor�dami su�inoti specialiuosius numerius para�ykite komand� /call");	
    SendClientMessage( playerid, COLOR_FADE1, "  /call [NUMERIS] - skambinti � pasirinkt� numer�." );
    SendClientMessage( playerid, COLOR_WHITE, "  (/h)angup - pad�ti telefon� pokalbio metu ir nutraukti pokalb�." );	
    SendClientMessage( playerid, COLOR_FADE1, "  (/p)ickup - atsiliepti � ateinant� skambut�." );	
    SendClientMessage( playerid, COLOR_WHITE, "  /sms [NUMERIS] [TEKSTAS] - para�yti trumpaja �inut� � pasirinkt� numer�." );
    SendClientMessage( playerid, COLOR_FADE1, "  /turnphone - i�jungti/�jungti telefon�." );	
    SendClientMessage( playerid, COLOR_WHITE, "  /speaker - �jungti garsiakalb� telefone." );	
    SendClientMessage( playerid, COLOR_FADE1, "  /ucall - komanda skirta taksafonams.." );	
    SendClientMessage( playerid, COLOR_GREEN, "___________________________________________________________________" );		
    return 1;
}
CMD:bizhelp(playerid)
{
    SendClientMessage(playerid,COLOR_GREEN,"|__________________BIZNIO VALDYMO INFORMACIJA__________________|");
	SendClientMessage(playerid,COLOR_WHITE,"  /furniture - komandoje naudojama viduje biznio, su kuria galite pirktis baldus � savo bizn�");
	SendClientMessage(playerid,COLOR_FADE1,"  /buybiz - jei esate �alia parduodamo biznio, su �ia komand� galite j� nusipirkti.");	
	SendClientMessage(playerid,COLOR_WHITE,"  /sellbiz [VEIK�JO ID] [KAINA] - galite parduoti savo turim� versl�.");	
	SendClientMessage(playerid,COLOR_FADE1,"  /biz - pagrindinis biznio valdymas, nustatymai ir kt. Komanda veikia prie biznio ��jimo.");
	SendClientMessage(playerid,COLOR_WHITE,"  /cargoprice - naudojama norint pateikti u�sakym� perkant prekes..");		
    SendClientMessage( playerid, COLOR_GREEN, "__________________________________________________________________" );	
    return 1;
}
/*
CMD:househelp(playerid)
{
    SendClientMessage(playerid,COLOR_GREEN,"|__________________NAMO VALDYMO INFORMACIJA__________________|");
	SendClientMessage(playerid,COLOR_WHITE,"  /furniture - komandoje naudojama viduje namo, su kuria galite pirktis baldus � savo bizn�");
	SendClientMessage(playerid,COLOR_FADE1,"  /buyhouse - jei esate �alia parduodamo namo su �ia komand� galite j� nusipirkti.");	
	SendClientMessage(playerid,COLOR_WHITE,"  /sellhouse [VEIK�JO ID] [KAINA] - galite parduoti savo turim� nam�.");	
	SendClientMessage(playerid,COLOR_FADE1,"  /setrent [NUOMOS KAIN�] - naudojama nustatant nuomos kain� savo name.");
	SendClientMessage(playerid,COLOR_WHITE,"  /lock - nuosavo namo dur� u�rakinimas, atrakinimas.");		
	SendClientMessage(playerid,COLOR_WHITE,"  /housewithdraw [SUMA] - pinig� i�sieimas i� namo. /housedeposit [SUMA] - pinig� �sid�jimas � namo seif�.");
	SendClientMessage(playerid,COLOR_FADE1,"  /houseinfo - namo informacija..");	
	SendClientMessage(playerid,COLOR_WHITE,"  /hinv - namo seifo informacija.");	
	SendClientMessage(playerid,COLOR_FADE1,"  /hradio - name esan�ios audio sistemos valdymas.");
	SendClientMessage(playerid,COLOR_WHITE,"  /tenantry - nuominink� informacija.");
	SendClientMessage(playerid,COLOR_WHITE,"  /evict - pa�alinti nuominink�..");
	SendClientMessage(playerid,COLOR_FADE1,"  /evictall - pa�alinti visus nuomininkus i� namo.");	
	SendClientMessage(playerid,COLOR_WHITE,"  /eat - atsistatyti gyvyb�s pavalgant.");	
	SendClientMessage(playerid,COLOR_FADE1,"  /hu - namo patobulinimas.");
	SendClientMessage(playerid,COLOR_WHITE,"  /clothes - persirengimas namuose, i�vaizdos pakeitimas.");		
    SendClientMessage( playerid, COLOR_GREEN, "__________________________________________________________________" );	
    return 1;
}
*/
/*
CMD:setunit( playerid, params[ ] )
{
    new
        string[ 256 ];

    if(UsePDCMD(playerid) != 1) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s nesate pareig�nas, kad atliktum�t �� veiksm�.");
    if(sscanf( params, "s[128]", string ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /setunit [PAVADINIMAS]" );
    new idcar = GetPlayerVehicleID( playerid );
    if( sVehicles[ idcar ][ Faction ] == 2 )
    {
        new Float:X, Float:Y, Float:Z;
        GetVehicleModelInfo(GetVehicleModel( idcar ), VEHICLE_MODEL_INFO_SIZE, X, Y, Z);
        if ( !IsValidDynamic3DTextLabel( Units [idcar] ) )
            Units [idcar] = CreateDynamic3DTextLabel (string, COLOR_WHITE, 0, (-0.5*Y), 0.0, 15.0, INVALID_PLAYER_ID, idcar, 1);
        else
            UpdateDynamic3DTextLabelText (Units [idcar], COLOR_WHITE, string);
    }
    return 1;
}
CMD:delunit( playerid, params[ ] )
{
    if(UsePDCMD(playerid) != 1) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s nesate pareig�nas, kad atliktum�t �� veiksm�.");
    new idcar = GetPlayerVehicleID( playerid );
    if( sVehicles[ idcar ][ Faction ] == 2 && IsValidDynamic3DTextLabel( Units [idcar] ) )
        DestroyDynamic3DTextLabel( Units [ idcar ] );
    return 1;
}
*/
CMD:forumname( playerid, params[ ] )
{
    if (GetPlayerAdminLevel(playerid) >= 1 || pInfo[ playerid ][ pTester ] >= 1 )
    {
        new string[ 256 ];

        if ( sscanf( params, "s[256]", string ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Komanda naudojama: /forumname [VARDAS FORUME forum.ltrp.lt]" );
        format( pInfo[ playerid ][ pForumName ], 256, string );
        SendClientMessage( playerid, GRAD, "J�s� forumo vardas s�kmingai pakeistas." );
    }
    return 1;
}
CMD:setcard( playerid, params[ ] )
{
    new string[ 256 ];

    if ( sscanf( params, "s[256]", string ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Komanda naudojama: /setcard [VEIK�JO APRA�YMAS]" );
    format( pInfo[ playerid ][ pCard ], 256, string );
    SendClientMessage( playerid, GRAD, "Pateiktas veik�jo apra�ymas s�kmingai atnaujintas." );
    return 1;
}
CMD:ccard( playerid, params[ ] )
{
    new giveplayerid,
        string[ 256 ];

    if ( sscanf( params, "u", giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Komanda naudojama: /ccard [VEIK�JO ID]" );
    if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid,GRAD,"Veik�jas su nurodytu ID skai�iumi �iuo metu n�ra prisijung�s.");
    format(string,256,"* %s (( %s ))",pInfo[ giveplayerid ][ pCard ],GetPlayerNameEx(giveplayerid));
    SendClientMessage( playerid, COLOR_PURPLE, string );
    return 1;
}
CMD:me( playerid, params[ ] )
{
    new string[ 256 ];
    if ( sscanf( params, "s[256]", string ) ) return SendClientMessage( playerid ,GRAD, "{FF6347}Komanda naudojama: /me [VEIK�JO VEIKSMAS], pvz: /me pasilenkia ir u�siri�a batus." );
    if ( Mute[ playerid ] == true) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, �iuo metu Jums yra u�drausta kalb�tis (/mute), nor�dami pa�alinti draudim� susisiekite su Administratoriumi." );
    SetPlayerChatBubble( playerid, string, COLOR_PURPLE, 20.0, 10000);
    format             ( string, 256, "* %s %s" ,GetPlayerNameEx( playerid ), string );
    ProxDetector       ( 15.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
//    if ( pInfo[ playerid ][ pJob ] == JOB_JACKER && ( LaikoTipas[playerid] == 5 || LaikoTipas[playerid] == 6 ) )
//        ActionLog( pInfo[ playerid ][ pMySQLID ], string );
    return 1;
}
CMD:stats( playerid, params[ ] )
{
    #pragma unused params
    ShowStats(playerid, playerid);
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
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite i�mesti ginklo b�damas komos b�senoje.");

    if(!GetPlayerWeapon(playerid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s nelaikote ginklo.");
    if(IsPlayerInAnyVehicle(playerid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite i�mesti ginklo b�dami transporto priemon�je.");

    // MD ir PD negali i�mest.
    if(PlayerFaction(playerid) == 1 || PlayerFaction(playerid) == 2 || IsPlayerWeaponJobWeapon(playerid, GetPlayerWeapon(playerid)))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s negalite i�mesti ginklo.");

    new index = -1;
    for(new i = 0; i < MAX_DROPPED_WEAPONS; i++)
        if(!IsValidDynamicObject(DroppedWeapons[ i ][ ObjectId ]) && !DroppedWeapons[ i ][ DissapearTimer ])
        {
            index = i;
            break;
        }
    if(index == -1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, pasiektas ant �em�s gulin�i� ginkl� limitas. Pabandykite v�liau.");


    new Float:x, Float:y, Float:pz, Float:z,
        weaponid = GetPlayerWeapon(playerid);
    CheckWeaponCheat(playerid, weaponid, 0);
    if(!IsPlayerWeaponInDB(playerid, weaponid))
    {
        new s[128];
        format(s, sizeof(s),"�Aid�jo %d(%s) ginklas %d neregistruotas DB.", playerid, GetName(playerid), weaponid);
        ACTestLog(s);
    }
    GetPlayerPos(playerid, x, y, pz);
    MapAndreas_FindZ_For2DCoord(x, y, z); // Magija

    // Taigi. Jei �mogus po tilto ar pn�, MapAndreas gra�ins VIR� tilto koordinates. 
    // Tod�l jeigu jis duoda mums koordinates auk��iau nei �aid�jo Z, atimam i� �aid�jo Z �iektiek.
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
    format(string, sizeof(string), "i�meta ginkl� kuris atrodo kaip %s", string);
    cmd_ame(playerid, string);
    RemovePlayerWeapon(playerid, weaponid); // Yay jis ir i� DB i�trins (sun)
    return 1;
}
*/
/*
CMD:grabgun(playerid)
{
	if(Mires[ playerid ] > 0)
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite i�mesti ginklo b�damas komos b�senoje.");

    for(new i = 0; i < MAX_DROPPED_WEAPONS; i++)
    {
        if(!DroppedWeapons[ i ][ CanBePickedUp ])
            continue;
        if(!IsPlayerInRangeOfDynamicObject(playerid, 2.0, DroppedWeapons[ i ][ ObjectId ]))
            continue;

        if(IsPlayerInventoryFull(playerid))
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: j�s� inventoriuje nepakanka vietos, atsilaisvinkite ir bandykite dar kart." );
    

        GivePlayerItem(playerid, DroppedWeapons[ i ][ WeaponId ], DroppedWeapons[ i ][ Ammo ]);
        SendClientMessage ( playerid, COLOR_WHITE, " Ginklas s�kmingai �d�tas � inventori�. ");
        PlayerPlaySound   ( playerid, 1057, 0.0, 0.0, 0.0);
        OnDroppedWeaponDestroyed(i);
        return 1;

    }
    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, prie j�s� n�ra jokio ginklo.");
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

stock IsPlayerInRangeOfDynamicObject(playerid, Float:distance, objectid)
{
    if(!IsValidDynamicObject(objectid))
        return false;

    new Float:x, Float:y, Float:z;
    GetDynamicObjectPos(objectid, x, y, z);
    return IsPlayerInRangeOfPoint(playerid, distance, x, y, z);
}




/*
CMD:prescribe( playerid, params [ ] )
{
    new
        drug [ 15 ],
        drug_id,
        drug_amount,
        drug_curramount,
        drug_hasdrug;

    if( sscanf( params, "s[15]d", drug, drug_amount ) )
    {
        SendClientMessage( playerid, COLOR_WHITE,"/prescribe [NARKOTIKAS] [KIEKIS]" );
        SendClientMessage ( playerid, COLOR_WHITE, " Galimi narkotik� variantai: Metamfetaminas, Kokainas, Zole, Heroinas, Amfa, Opijus, PCP, Extazy, Krekas" );
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
                drug_hasdrug = PlayerHasItemInInvEx( playerid, drug_id );

                if( drug_hasdrug < INVENTORY_SLOTS )
                {
                    drug_curramount = InvInfo [ playerid ][ drug_hasdrug ][ iAmmount ];

                    if( drug_curramount > drug_amount )
                    {
                        if( AddItemToInventory( playerid, drug_id, drug_amount ) )
                        {
                            InvInfo [ playerid ][ drug_hasdrug ][ iAmmount ] = drug_curramount - drug_amount;

                            SendClientMessage( playerid, COLOR_WHITE,"Nurodytas narkotikas buvo s�kmingai perskirtas � pasirinkt� kiek�." );
                        }
                            else
                        {
                            SendClientMessage( playerid, COLOR_GRAD, "{FF6347}J�s� inventoriuj� n�ra pakankamai vietos, kad atliktum�t �� veiksm�." );
                        }

                    }
                        else
                    {
                        SendClientMessage ( playerid, COLOR_GRAD, "{FF6347}Negalite perskirti tiek narkotik�, kuri� neturite." );
                    }
                }
                    else
                {
                    SendClientMessage( playerid, COLOR_GRAD, "{FF6347}J�s neturite �io narkotiko su savimi." );
                }
            }
        }

    }

    return true;
}
*/
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
                        SendClientMessage( playerid, COLOR_WHITE, "Pasirinktas narkotikas buvo s�kmingai sujungtas." );
                    }
                        else
                    {
                        SendClientMessage ( playerid, COLOR_GRAD, "{FF6347}Klaida, J�s neturite tiek narkotik� kiek nurod�te" );
                    }
                    }
                        else
                    {
                        SendClientMessage( playerid, COLOR_GRAD, "{FF6347}Klaida, J�s nurod�te narkotik�, kurio neturite." );
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
    if ( item2 < 1 ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Gaminamas kiekis negali b�ti ma�esnis negu 1.");
    switch( item )
    {
        case 1:
        {
            if(pInfo[ playerid ][ pJob ] != JOB_GUN) 
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s neturite galimyb�s gamintis �aunamojo ginklo..");

            if(!IsItemInPlayerInventory(playerid, ITEM_MATS)) 
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto netur�dami atitinkam� dali�.");

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
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto netur�dami atitinkam� dali�.");

                    if ( pasigamino == false )
                    {
                        SendClientMessage(playerid, COLOR_RED, "Klaida, ginklo pagaminimas buvo at�auktas, kadangi dalys ir netinkamos." );
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
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto netur�dami atitinkam� dali�.");
                    if ( pasigamino == false )
                    {
                        SendClientMessage( playerid, COLOR_RED, "Klaida, ginklo pagaminimas buvo at�auktas, kadangi dalys ir netinkamos." );
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
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto netur�dami atitinkam� dali�.");
                    if ( pasigamino == false )
                    {
                        SendClientMessage( playerid, COLOR_RED, "Klaida, ginklo pagaminimas buvo at�auktas, kadangi dalys ir netinkamos." );
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
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto netur�dami atitinkam� dali�.");
                    if( pasigamino == false )
                    {
                        SendClientMessage( playerid, COLOR_RED, "Klaida, ginklo pagaminimas buvo at�auktas, kadangi dalys ir netinkamos." );
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
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto netur�dami atitinkam� dali�.");
                    if ( pasigamino == false )
                    {
                        SendClientMessage( playerid, COLOR_RED, "Klaida, ginklo pagaminimas buvo at�auktas, kadangi dalys ir netinkamos." );
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
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto netur�dami atitinkam� dali�.");
                    if ( pasigamino == false )
                    {
                        SendClientMessage( playerid, COLOR_RED, "Klaida, ginklo pagaminimas buvo at�auktas, kadangi dalys ir netinkamos." );
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
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto netur�dami atitinkam� dali�.");
                    if ( pasigamino == false )
                    {
                        SendClientMessage( playerid, COLOR_RED, "Klaida, ginklo pagaminimas buvo at�auktas, kadangi dalys ir netinkamos." );
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
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto netur�dami atitinkam� dali�.");
                    if ( pasigamino == false )
                    {
                        SendClientMessage( playerid, COLOR_RED, "Klaida, ginklo pagaminimas buvo at�auktas, kadangi dalys ir netinkamos." );
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
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gamintis pasirinkto produkto netur�dami atitinkam� dali�.");
                    if ( pasigamino == false )
                    {
                        SendClientMessage( playerid, COLOR_RED, "Klaida, ginklo pagaminimas buvo at�auktas, kadangi dalys ir netinkamos." );
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
            format           ( string, 43, "Sveikinome, Jums s�kmingai pavyko pasigaminti %s", wepname );
            SendClientMessage( playerid, COLOR_WHITE, string       );
        }
        case 2:
            return SendClientMessage(playerid, -1, "Komanda nenaudojama. Naudokite /makemolotov");
        

        
    }
    return 1;
}
*/
/*
CMD:buyseeds( playerid, params[ ] )
{
    #pragma unused params
    if(!Data_IsPlayerInRangeOfCoords(playerid, 5.0, "job_dealer_seeds_buy"))
        return SendClientMessage( playerid, GRAD, "Gaila, bet �iuo metu aplinkui Jus n�ra vietos susijusios su juodaj� rinka. Ie�kokite toliau.");
    if ( GetPlayerMoney(playerid) < 200 ) return SendClientMessage( playerid ,GRAD, "{FF6347}Klaida, J�s neturite pakankamai gryn�j� pinig�. ");
    if ( pInfo[ playerid ][ pJob ] != JOB_DRUGS) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudotis �ia galimybe neb�damas narkotiku prekeiviu." );

    if(IsPlayerInventoryFull(playerid))
        return SendClientMessage(playerid, COLOR_FADE2, "{FF6347}Klaida, J�s� inventoriuje n�ra laisvos vietos, kad atliktum�t �� veiksm�..");

    GivePlayerItem(playerid, ITEM_SEED, 10); 

    GivePlayerMoney( playerid, -200);
    SendClientMessage( playerid, COLOR_WHITE, " S�kmingai nusipirkote 10 �ol�s augalo s�kl�, kurios kainavo 200$.");
    return 1;
}*/
/*
CMD:pay( playerid, params[ ] )
{
    new giveplayerid,
        items,
        string[ 128 ],
        IP[ 16 ],
        IP2[ 16 ];
        
    if ( sscanf( params, "ud", giveplayerid, items ) )
    return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /pay [VEIK�JO ID] [PINIG� SUMA]");
    if ( playerid == giveplayerid ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti komandos sau." );
	if ( GetPlayerLevel(playerid) < 2 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudotis �ia komanda netur�dami antro lygio." );	
    if ( !IsPlayerConnected( giveplayerid ) ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodyto ID n�ra prisijung�s serveryje.");
    if ( !PlayerToPlayer   ( 5.0, playerid, giveplayerid ) ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, komanda galite naudoti jei �aid�jas yra �alia J�s�.");
    if ( items < 1 || items > 999999 ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, perduodama suma negali b�ti ma�esn� nei 1$ ar didesn� nei 999999$" );
    if ( GetPlayerMoney(playerid) < items ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, savo rankose neturi tokios nurodytos sumos. ");

    GetPlayerIp( playerid, IP, 16 );
    GetPlayerIp( giveplayerid, IP2, 16 );

    if( !strcmp( IP, IP2, true ) || pInfo[ playerid ][ pUcpID ] == pInfo[ giveplayerid ][ pUcpID ] )
        return true;

    GivePlayerMoney( playerid, -items );
    GivePlayerMoney( giveplayerid, items );

    LoopingAnim( playerid, "DEALER", "shop_pay", 4.0, 0, 1, 1, 1, 0 );
    format           ( string, sizeof(string), " Sveikiname, pasirinktam veik�jui %s buvo perduota. %d$.", GetPlayerNameEx(giveplayerid), items );
    SendClientMessage( playerid, COLOR_WHITE, string );
    format           ( string, sizeof(string), " K�tik s�kmingai gavote %d$ i� veik�jo %s.", items, GetPlayerNameEx(playerid) );
    SendClientMessage( giveplayerid, COLOR_WHITE, string );

    if ( items > 49999 )
    {
        format( string, sizeof(string), "AdmWarn: veik�jas (%s) naudodamas (/pay) sumok�jo veik�jui (%s), %d$.", GetName( playerid ), GetName( giveplayerid ), items );
        SendAdminMessage( COLOR_ADM, string );
    }

    PayLog           ( pInfo[ playerid ][ pMySQLID ], 7, pInfo[ giveplayerid ][ pMySQLID ], items );
    PayLog           ( pInfo[ giveplayerid ][ pMySQLID ], 8, pInfo[ playerid ][ pMySQLID ], items );
    SaveAccount      ( playerid );
    SaveAccount      ( giveplayerid );
    return 1;
}
*/
/*
CMD:buymats( playerid, params[ ] )
{
    if(!Data_IsPlayerInRangeOfCoords(playerid, 5.0, "job_dealer_material_buy")) 
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s nesate paket� pirkimo vietoje. " );
    if(pInfo[ playerid ][ pJob ] != JOB_GUN ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s nesate ginkl� prekeivis. " );
    new mat,
        string[ 70 ];

    if(sscanf( params, "d", mat) ) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /buymats [kiekis]");
    if(Mats < mat ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: �iuo metu tiek materij� neturime, bandykite v�liau.");
    if(GetPlayerMoney(playerid) < mat * 5 ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: neturite pakankamai pinig�.");
    if(mat < 0 ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Negalima pirkti ma�iau negu 0 ");

    if(IsPlayerInventoryFull(playerid))
        return SendClientMessage(playerid, COLOR_FADE2, "{FF6347}Persp�jimas: j�s� inventoriuje nepakanka vietos, atsilaisvinkite ir bandykite dar kart.");

    GivePlayerItem(playerid, ITEM_MATS, mat);
    GivePlayerMoney( playerid, - mat * 2 );
    Mats -= mat;
    format          ( string, 70, " Nusipirkai %d paket�, b�k atsargus kad policija nepagautu. ", mat );
	SendClientMessage( playerid, COLOR_WHITE, string );
    SaveAccount( playerid );
    return 1;
}
CMD:weaponlist( playerid, params[ ] )
{
    #pragma unused params
	SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /make [2][GINKLO ID]");
	SendClientMessage( playerid ,GRAD, "GINKLAI: ID:23 (9mm su duslintuvu, 150 kulk�) | ID:24 (Desert Eagle,200 kulk�)");
	SendClientMessage( playerid ,GRAD, "GINKLAI: ID:25 (Shotgun, 400 kulk�) | ID:28 (UZI, 350 kulk�) | ID:29 (MP5, 500kulk�)");
	SendClientMessage( playerid ,GRAD, "GINKLAI: ID:30 (AK-47, 700 kulk�)");
	SendClientMessage( playerid ,GRAD, "GINKLAI: ID:32 (Tec9, 400 kulk�) | ID:33 (Rifle 1300 kulk�) | ID:34 (Sniper,1500 kulk�)");
    return 1;
}
*/
/*
CMD:fdgear( playerid, params[ ] )
{
    #pragma unused params
    if ( PlayerFaction( playerid ) != 2 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti �io veiksmo neb�dami ugniagesi� departamento darbuotoju." );
    ShowPlayerDialog( playerid, 159, DIALOG_STYLE_LIST,"{FFFFFF}Gaisrinink� apranga",
        "Gaisrinink� �almas 1\n\
        Gaisrinink� �almas 2\n\
        Kauk�", "Pasiimti", "At�aukti" );
    return 1;
}
CMD:pgear( playerid, params[ ] )
{
    #pragma unused params
    if ( UsePDCMD( playerid ) == 0 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente." );
    if(!PDJOBPlace(playerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nor�dami persirengti privalote b�ti persirengimo kabinoje.");
    ShowPlayerDialog( playerid, 104, DIALOG_STYLE_LIST,"{FFFFFF}Pareig�no apranga",
        "Policijos kepur� 1\n\
        Policijos kepur� 2\n\
        Policijos kepur� 3\n\
        Policijos akiniai\n\
        Policijos kepur� 4\n\
        Policijos kepur� 5\n\
        Skydas", "Pasiimti", "At�aukti" );
    return 1;
}
*/
/*
CMD:drag( playerid, params[ ] )
{
    new
        giveplayerid,
        string[ 128 ];

    if(PlayerFaction( playerid ) != 1 && PlayerFaction( playerid ) != 2 ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente.");
    if(sscanf( params, "u", giveplayerid)) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /drag [VEIK�JO ID]");
    if(Mires[playerid] > 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje.");
    if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
    if(!PlayerToPlayer(10,playerid,giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti �io veiksmo su veik�ju, kuris n�ra �alia J�s�. ");
    if(!GetPVarInt(giveplayerid, "Drag"))
    {
        if(IsPlayerInAnyVehicle(giveplayerid) && GetPlayerState(playerid) == PLAYER_STATE_ONFOOT) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jas yra tr. priemon�je, o J�s ne.");
        if(IsPlayerInAnyVehicle(playerid) && GetPlayerState(giveplayerid) == PLAYER_STATE_ONFOOT) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s esate tr. priemon�je, o nurodytas veik�jas ne");
        SetPVarInt(giveplayerid, "Drag", true);
        format(string, sizeof(string), "* %s prad�jo tempti/traukti %s.", GetPlayerNameEx(playerid), GetPlayerNameEx(giveplayerid));
        ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
        ShowInfoText(giveplayerid, "~w~Tempiamas", 2500);
        SetTimerEx("CuffsTime", 1000, false, "ii",giveplayerid,playerid);
        TogglePlayerControllable(giveplayerid, 0);
        return 1;
    }
    else
    {
        if(IsPlayerInAnyVehicle(giveplayerid) && GetPlayerState(playerid) == PLAYER_STATE_ONFOOT) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jas yra tr. priemon�je, o J�s ne.");
        if(IsPlayerInAnyVehicle(playerid) && GetPlayerState(giveplayerid) == PLAYER_STATE_ONFOOT) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s esate tr. priemon�je, o nurodytas veik�jas ne");
        SetPVarInt(giveplayerid, "Drag", false);
        format(string, sizeof(string), "* %s nustotojo tempti/traukti %s.", GetPlayerNameEx(playerid), GetPlayerNameEx(giveplayerid));
        ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
        ShowInfoText(giveplayerid, "~w~Nebe tempiamas", 2500);
        TogglePlayerControllable(giveplayerid, 1);
        return 1;
    }
}*/
/*
CMD:m(playerid, params[])
{
    return cmd_megaphone(playerid, params);
}
CMD:megaphone( playerid, params[ ] )
{
    new
        gMessage[ 64 ],
        string[ 256 ];
        
    if(UsePDCMD(playerid) != 1) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente.");
    if(sscanf( params, "s[64]", gMessage)) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /m(egaphone) [SKELBIAMAS TEKSTAS]");
    if(Mires[playerid] > 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje.");
    if(Mute[playerid] == true) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �iuo metu Jums yra u�drausta kalb�tis (/mute), nor�dami pa�alinti draudim� susisiekite su Administratoriumi.");
    new idcar = GetNearestVehicle(playerid, 2.0);
    if(!IsValidVehicle(idcar) || sVehicles[ idcar ][ Faction ] != 2) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, turite b�ti policijos transporto priemon�je arba �alia jos.");

    format(string, sizeof(string), "[LSPD] %s!", gMessage);
    ProxDetector(40.0, playerid, string,COLOR_POLICEM,COLOR_POLICEM,COLOR_POLICEM,COLOR_POLICEM,COLOR_POLICEM);
    return 1;
}
*/
/*
CMD:killcheckpoint(playerid, params[])
{
    if ( pInfo[ playerid ][ pJob ] != JOB_TRUCKER && !UsePDCMD( playerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Atliekant veiksm� �vyko klaida. " );
    if ( UsePDCMD( playerid ) )
    {
        if ( !GetPVarInt( playerid, "BACKUP") )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, J�s �iuo metu nekviet�te jokio pastiprinimo." );
    }
    DisablePlayerCheckpoint( playerid );
    Checkpoint[ playerid ] = CHECKPOINT_NONE;
    SetPVarInt( playerid, "BACKUP", INVALID_PLAYER_ID );
    return 1;
}
**/
/*
CMD:abk( playerid, params[ ] )
{
    new
        giveplayerid,
        Float:Kords[ 3 ];
    if ( !UsePDCMD( playerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente." );
    if ( sscanf( params, "u", giveplayerid ) )  return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /abk [veik�jo ID]");
    if(IsPlayerNPC(giveplayerid)) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti komandos ant NPC boto. ");
    if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
    if ( pInfo[ giveplayerid ][ pBackup ] == 0 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, J�s �iuo metu nekviet�te jokio pastiprinimo. " );

    GetPlayerPos( giveplayerid, Kords[ 0 ], Kords[ 1 ], Kords[ 2 ] );
    SetPlayerCheckPointEx( playerid, CHECKPOINT_BACKUP, Kords[ 0 ], Kords[ 1 ], Kords[ 2 ], 5.0 );
    SetPVarInt( playerid, "BACKUP", giveplayerid );
    return 1;
}
CMD:bk( playerid, params[ ] )
{
    cmd_backup( playerid, "" );
    return 1;
}
CMD:backup( playerid, params[ ] )
{
    #pragma unused params
    new
        string[ 128 ];
        
    if ( UsePDCMD( playerid ) != 1 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente." );
    if ( Mires[ playerid ] > 0 )     return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje." );
    format(string, sizeof(string), "|DI�PE�ERIN� PRANE�A| D�MESIO VISIEMS PADALINIAMS, pareig�nas %s pra�o skubaus pastiprinimo, vietos kordinat�s nustatytos J�s� GPS.", GetPlayerNameEx(playerid));
    if(pInfo[playerid][pBackup] == 0)
    {
        foreach(Player,i)
        {
            if ( pInfo[ i ][ pMember ] == pInfo[ playerid ][ pMember ] )
            {
                format( string, sizeof(string), "|DI�PE�ERIN� PRANE�A| D�MESIO VISIEMS PADALINIAMS, pareig�nas %s pra�o skubaus pastiprinimo, vietos kordinat�s nustatytos J�s� GPS..", GetPlayerNameEx( playerid ) );
                SendClientMessage( i, COLOR_LIGHTRED2, string );
                format( string, sizeof(string), "|DI�PE�ERIN� PRANE�A| Jeigu galite atvykti � pastiprinim� ra�ykite prane�kite dipe�erinei. (/abk %d)", playerid );
                SendClientMessage( i, COLOR_LIGHTRED2, string );
                pInfo[ playerid ][ pBackup ] = 1;
            }
        }
        return 1;
    }
    else if (pInfo[ playerid ][ pBackup ] == 1 )
    {
        foreach(Player,i)
        {
            if ( GetPVarInt( i, "BACKUP" ) == playerid )
            {
                DisablePlayerCheckpoint( i );
                Checkpoint[ i ] = CHECKPOINT_NONE;
                SetPVarInt( i, "BACKUP", INVALID_PLAYER_ID );
            }
            if ( pInfo[ i ][ pMember ] == pInfo[ playerid ][ pMember ])
            {
                format ( string, 126, "|DI�PE�ERIN� PRANE�A| D�MESIO, pareig�nas %s at�auk� pastiprinimo pra�ym�.", GetPlayerNameEx( playerid ) );
                SendClientMessage( i, COLOR_LIGHTRED2, string );
                pInfo[ playerid ][ pBackup ] = 0;
            }
        }
        return 1;
    }
    return 1;
}
*/
/*
CMD:ramcar( playerid, params[ ] )
{
    #pragma unused params
    if(UsePDCMD(playerid) != 1) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami polici\jos departamente.");
    if(pInfo[playerid][pRank] < 2)       return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, komanda leid�iama naudotis 2 rango pareig�nams.");
    new car = INVALID_VEHICLE_ID;
    if(GetPlayerState(playerid) == PLAYER_STATE_DRIVER)
        car = GetPlayerVehicleID( playerid );
    else
        car = GetNearestVehicle( playerid, 5.0 );
    if ( car == INVALID_VEHICLE_ID ) return SendClientMessage( playerid, COLOR_LIGHTRED, " Klaida, aplink Jus n�ra jokios tr. priemon�s.");

    if(cInfo[ car ][ cOwner ] != 0 && cInfo[ car ][ cLock ] == 1)
    {
        ShowInfoText(playerid, "~w~ Tr. priemones dureles islauztos", 5000);
        LockVehicle(car, 0);
        VehicleAlarm(car, 0);
    }
    return 1;
}
*/
/*
CMD:heal( playerid, params[ ] )
{
    new
        giveplayerid,
        string[ 128 ];
        
    if(PlayerFaction( playerid ) != 2) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti �io veiksmo neb�dami mediku.");
    if(sscanf( params, "u", giveplayerid)) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /heal [VEIK�JO ID]");
    if(giveplayerid == playerid) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s negalite naudoti komandos ant sav�s.");
    if(!IsPlayerConnected(giveplayerid))  return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
    if(!PlayerToPlayer(10.0, playerid, giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti �io veiksmo su veik�ju, kuris n�ra �alia J�s�.");
    SetPlayerHealth( giveplayerid, 100);
    if(pInfo[giveplayerid][pLiga] > 0)
    {
        format(string, sizeof(string), "Daktaras Jums dav� vaistu d�l ligos: %s. Dabar prad�site sveikti ir J�s� sveikata ger�s.",Ligos[pInfo[giveplayerid][pLiga]]);
        SendClientMessage(giveplayerid, COLOR_GREEN,string);
        pInfo[giveplayerid][pLiga] = 0;
        if(GetPlayerMoney(giveplayerid) > 50)
        {
            pInfo[playerid][pPayCheck] += 50;
            GivePlayerMoney(giveplayerid,-50);
        }
        return 1;
    }
    if(Mires[giveplayerid] > 0)
    {
        Mires[giveplayerid] = 0;
        TogglePlayerControllable(giveplayerid, true);
        SendClientMessage(giveplayerid, COLOR_WHITE,"Daktaras s�kmingai pad�jo Jums i�gyti, bei pasveikti. Gydymo i�laidos 50$.");
        if(GetPlayerMoney(giveplayerid) > 50)
        {
            pInfo[playerid][pPayCheck] += 50;
            GivePlayerMoney(giveplayerid,-50);
        }
        ApplyAnimation(giveplayerid, "CRACK", "null", 0.0, 0, 0, 0, 0, 0);
        ApplyAnimation(playerid, "MEDIC", "CPR", 4.0, 0, 0, 0, 0, 0 );
        DestroyDynamic3DTextLabel( DeathLabel[giveplayerid] );
        return 1;
    }
    return 1;
}
*/
/*
CMD:rrb( playerid, params[ ] )
{
    cmd_removeroadblock( playerid, "" );
    return 1;
}
CMD:removeroadblock( playerid, params[ ] )
{
    #pragma unused params
    if(PlayerFaction( playerid ) != 1 && PlayerFaction( playerid ) != 2 && PlayerFaction( playerid ) != 5)    
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos ar medicinos departamente.");
    if(pInfo[playerid][pRank] < 2) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, komanda leid�iama naudotis tik nuo 2 rango.");
    for ( new i = 0; i < MAX_ROADBLOCKS; i++ )
    {
        if ( RoadBlocks[ i ] > 0 )
        {
            new
                Float:rX,
                Float:rY,
                Float:rZ;

            GetDynamicObjectPos( RoadBlocks[ i ], rX, rY, rZ );

            if ( IsPlayerInRangeOfPoint( playerid, 5.0, rX, rY, rZ ) )
            {
                RemoveRoadBlock( i );
                SendClientMessage(playerid, COLOR_GREEN, "Kelio u�tvara/blokada buvo s�kmingai pa�alinta/paiimta.");
                return 1;
            }
        }
    }
    return 1;
}
CMD:rb( playerid, params[ ] )
{
    cmd_roadblock( playerid, params );
    return 1;
}
CMD:roadblock( playerid, params[ ] )
{
    new
        giveplayerid;
    if(PlayerFaction( playerid ) != 1 && PlayerFaction( playerid ) != 2 && PlayerFaction( playerid ) != 5)          
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos ar medicinos departamente.");
    if(pInfo[playerid][pRank] < 2)       
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, komanda leid�iama naudotis tik nuo 2 rango.");
    if(sscanf( params, "d", giveplayerid)) 
        return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /roadblock [U�TVAROS/BLOKADOS MODELIO ID]");
    if(IsPlayerInAnyVehicle(playerid))  
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, kelio u�tvaros/blokados negalite pad�ti sed�dami tr. priemon�je.");
    if(Mires[playerid] > 0)              
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje.");

    if ( PlacePlayerRoadBlockInPos( playerid, giveplayerid ) )
    {
        SendClientMessage( playerid, COLOR_WHITE, "Nurodyta kelio u�tvara/blokada s�kmingai pastatyta nurodytoje vietoje." );
        SendClientMessage       ( playerid, COLOR_WHITE, "Nor�dami pa�alinti kelio u�tvara/blokada naudokite komanda - /rrb" );
    }
    return 1;
}
*/
/*
CMD:jobid( playerid, params[ ] )
{
    new
        giveplayerid,
        string[ 64 ];
    if(UsePDCMD(playerid) != 1) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente.");
    if(sscanf( params, "u", giveplayerid)) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /jobid [VEIK�JO ID]");
    if(Mires[playerid] > 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje.");
    if(!IsPlayerConnected(giveplayerid))  return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
    if(!PlayerToPlayer(5.0,playerid,giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti �io veiksmo su veik�ju, kuris n�ra �alia J�s�. ");
    SendClientMessage(giveplayerid,COLOR_GREEN2, "|______________LOS SANTOS DEPARTAMENTAS______________|");
    SendClientMessage(giveplayerid,COLOR_GREEN2, "|______________  PAREIG�NO PA�YM�JIMAS ______________|");
    format(string,sizeof(string),"Pareig�no vardas: %s     Pavard�: %s",GetPlayerFirstName(playerid),GetPlayerLastName(playerid));
    SendClientMessage(giveplayerid,COLOR_WHITE, string);
    format(string,sizeof(string),"Pareig�no pareigos/rangas: %s     Am�ius: %d",GetPlayerRangName( playerid ),pInfo[playerid][pAge]);
    SendClientMessage(giveplayerid,COLOR_WHITE, string);
    return 1;
}
*/

/*
CMD:takefmoney( playerid, params[ ] )
{
    new
        giveplayerid,
        mony,
        string[ 126 ];
        
    if(pInfo[playerid][pLead] == 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: J�s nesate frakcijos vadovas.");
    if(sscanf( params, "ud", giveplayerid, mony)) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /takefmoney [administratoriaus id (3lvl)][suma]. Daugiausiai nuiimti galite: 50.000$.");
    if(!IsPlayerConnected(giveplayerid))  return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
    if(mony < 0 || mony > 50000) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nuimdami pinigus turite nurodyti sum�, kuri b�ti didesn� nei 1$, bei ma�esn� u� 50,000$.");
    if(!PlayerToPlayer(2.0,playerid,giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �alia J�s� n�ra reikiamo Administratoriaus..");
    if(GetPlayerAdminLevel(giveplayerid) < 3) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �alia stovin�io Administratoriaus lygis yra per ma�as �iai komandai.");
    if(mony > fInfo[PlayerFaction( playerid )][fBank]) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, frakcijos biud�ete n�ra nurodytos sumos");
    GivePlayerMoney(playerid,mony);
    fInfo[PlayerFaction( playerid )][fBank] -= mony;
    format(string,126,"AdmWarn: Frakcijos vadovas (%s) i� frakcijos biud�eto i��m� %d$.",GetName(playerid),mony);
    SendAdminMessage(COLOR_ADM,string);
    SaveFactions(PlayerFaction( playerid ));
    return 1;
}
*/
/*
CMD:checkfbudget( playerid, params[ ] )
{
    new
        string[ 126 ];
    #pragma unused params
    if(pInfo[playerid][pLead] == 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti �ios komandos jei nesate �ios frakcijos vadovu..");
    SendClientMessage(playerid,COLOR_GREEN,"|____________FRAKCIJOS BIUD�ETAS____________|");
    format(string,126,"�uo metu frakcijos biud�ete yra %d$",fInfo[PlayerFaction( playerid )][fBank]);
    SendClientMessage(playerid,COLOR_WHITE,string);
    return 1;
}
CMD:checkbudget( playerid, params[ ] )
{
    new
        string[ 126 ];
    #pragma unused params
    if(pInfo[playerid][pLead] != 6) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: J�s nesate meras.");
    SendClientMessage(playerid,COLOR_GREEN,"|_____BENDRAS LOS SANTOS MIESTO BIUD�ETAS_____|");
    format(string,126,"Bendras Los Santos miesto biud�etas sieka %d$",Biudzetas);
    SendClientMessage(playerid,COLOR_WHITE,string);
    return 1;
}
CMD:charity( playerid, params[ ] )
{
    new
        giveplayerid,
        string[ 126 ];
    if(sscanf( params, "d", giveplayerid)) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /charity [SUMA], naudodami �i� komand� J�s paremsite miesto biud�et�.");
    if(giveplayerid > GetPlayerMoney(playerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite nurodyti sumos, kurios neturite.");
    if(giveplayerid < 0 ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodyta suma negali b�ti ma�esn� u� 1$");
    format( string, 126, "D�kojame u� J�s� param�, kadangi J�s miesto biud�et� parem�t %d$", giveplayerid );
    SendClientMessage( playerid, COLOR_NEWS, string );
    GivePlayerMoney(playerid,-giveplayerid);
    Biudzetas += giveplayerid;
    SaveMisc();
    return 1;
}
*/
/*
CMD:cuff( playerid, params[ ] )
{
    if ( UsePDCMD( playerid ) == 1 || ( PlayerFaction( playerid ) == 5 && pInfo[ playerid ][ pRank ] > 4 ) )
    {
        new
            giveplayerid,
            string[ 64 ];

        if(sscanf( params, "u", giveplayerid)) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /cuff [VEIK�JO ID]");
        if(Mires[playerid] > 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje.");
        if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
        if(!PlayerToPlayer(10,playerid,giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti �io veiksmo su veik�ju, kuris n�ra �alia J�s�. ");
        if(pInfo[giveplayerid][pCuffs] == 0)
        {
            if(IsPlayerInAnyVehicle(giveplayerid) && GetPlayerState(playerid) == PLAYER_STATE_ONFOOT) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jas yra tr. priemon�je, o J�s ne.");
            if(IsPlayerInAnyVehicle(playerid) && GetPlayerState(giveplayerid) == PLAYER_STATE_ONFOOT) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s esate tr. priemon�je, o nurodytas veik�jas ne");
            pInfo[giveplayerid][pCuffs] = 1;
            format(string, sizeof(string), "suima %s abi rankas u� nugaros ir u�deda antrankius ant rank�.", GetPlayerNameEx(giveplayerid));
            cmd_ame(playerid, string);
            ShowInfoText(giveplayerid, "~w~Rankos surakintos", 2500);
            SetPlayerSpecialAction(giveplayerid, SPECIAL_ACTION_CUFFED );
            SetPlayerAttachedObject(giveplayerid, 0, 19418, 6, -0.011000, 0.028000, -0.022000, -15.600012, -33.699977, -81.700035, 0.891999, 1.000000, 1.168000);
            return 1;
        }
        else if(pInfo[giveplayerid][pCuffs] == 1)
        {
            if(IsPlayerInAnyVehicle(giveplayerid) && GetPlayerState(playerid) == PLAYER_STATE_ONFOOT) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jas yra tr. priemon�je, o J�s ne.");
            if(IsPlayerInAnyVehicle(playerid) && GetPlayerState(giveplayerid) == PLAYER_STATE_ONFOOT) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s esate tr. priemon�je, o nurodytas veik�jas ne");
            pInfo[giveplayerid][pCuffs] = 0;
            SetPlayerSpecialAction(giveplayerid, SPECIAL_ACTION_NONE);
            RemovePlayerAttachedObject(giveplayerid, 0);
            format(string, sizeof(string), "nuima u�d�tus antrankius %s ir susideda juos � savo d�kl�.", GetPlayerNameEx(giveplayerid));
            cmd_ame(playerid, string);
            ShowInfoText(giveplayerid, "~w~Rankos atrakintos", 2500);
            return 1;
        }
    }
    return 1;
}
*/
/*
CMD:duty(playerid)
{
    new
        string[ 170 ];
        
    if(PlayerFaction( playerid ) == 1 )
    {
        if(!PDJOBPlace(playerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite persirengti neb�damas persirengimo kabinoje/kambaryje.");
        if(pInfo[playerid][pJobDuty] == 0)
        {
            format(string, sizeof(string), "* pareig�nas %s atsidaro savo ginkl� saugykl�.", GetPlayerNameEx(playerid));
            ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
            SetPlayerArmour ( playerid, 100 );
            pInfo[playerid][pJobDuty] = 1;
            //cmd_wepstore( playerid, "" );
            SetPlayerColor( playerid, 0x8d8dffAA );
            return 1;
        }
        else if(pInfo[playerid][pJobDuty] == 1)
        {
            SetPlayerArmour ( playerid, 0 );
            format(string, sizeof(string), "* pareig�nas %s pasid�jo savo turimus ginklus � savo ginkl� saugykl�.", GetPlayerNameEx(playerid));
            ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
            printf("Removing player %s job weapons", GetName(playerid));
            RemovePlayerJobWeapons(playerid);
            pInfo[playerid][pJobDuty] = 0;
            SetPlayerColor( playerid, TEAM_HIT_COLOR );
            return 1;
        }
    }
    
    else if(PlayerFaction( playerid ) == 2 )
    {
        if(pInfo[playerid][pJobDuty] == 0)
        {
            pInfo[playerid][pJobDuty] = 1;
            SendClientMessage(playerid, COLOR_LIGHTRED, "[LSFD] J�s prad�jote darb� kaip departamento darbuotojas, nuo �iol galite naudotis departamento komandomis");
            format(string, sizeof(string), "[Los Santos pagalbos skyrius] %s prad�jo darb� departamente. Skubios pagalbos departamento numeris i�kvietimams: /call 911.", GetPlayerNameEx(playerid));
            SendChatMessageToAll(COLOR_LIGHTRED2, string);
            SetPlayerColor( playerid, 0xc66871FF );
            return 1;
        }
        else if(pInfo[playerid][pJobDuty] == 1)
        {
            RemovePlayerJobWeapons(playerid);
            pInfo[playerid][pJobDuty] = 0;
            SendClientMessage(playerid, COLOR_LIGHTRED, "[LSFD] J�s baigiate darb� kaip departamento darbuotojas..");
            SetPlayerColor( playerid, TEAM_HIT_COLOR );
            return 1;
        }
    }
    else if(PlayerFaction( playerid ) == 4 )
    {
        if(pInfo[playerid][pJobDuty] == 0)
        {
            pInfo[playerid][pJobDuty] = 1;
            SendClientMessage(playerid, COLOR_WHITE, "Prad�jote darb�.");
            GivePlayerWeapon(playerid, 43, 50);
            return 1;
        }
        else if(pInfo[playerid][pJobDuty] == 1)
        {
            RemovePlayerWeapon( playerid, 43 );
            pInfo[playerid][pJobDuty] = 0;
            SendClientMessage(playerid, COLOR_WHITE, "Baig�te darb�.");
            return 1;
        }
    }

    
    else if(PlayerFaction( playerid ) == 5 )
    {
        if( pInfo[ playerid ][ pRank ] >= 4 )
        {
            if(pInfo[playerid][pJobDuty] == 0)
            {
                format(string, sizeof(string), "*%s i� savo saugyklos pasiem� a�arines dujas, banan� ir �sideda � d�klus.", GetPlayerNameEx(playerid));
                ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
                GivePlayerJobWeapon(playerid, 41, 500);
                GivePlayerJobWeapon(playerid, 3, 1);
                pInfo[playerid][pJobDuty] = 1;
                return 1;
            }
            else if(pInfo[playerid][pJobDuty] == 1)
            {
                format(string, sizeof(string), "*%s i�siema turimas a�arines dujas, banan� ir �sideda � savo saugykl�.", GetPlayerNameEx(playerid));
                ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
                RemovePlayerJobWeapons(playerid);
                pInfo[playerid][pJobDuty] = 0;
                return 1;
            }
        }
    }
    return 1;
}
*/
/*
CMD:windows( playerid, params[ ] )
{
    #pragma unused params
    if ( !IsPlayerInAnyVehicle( playerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nor�dami atlikti �� veiksm� privalote b�ti tr. priemon�je. ");
    new vehid = GetPlayerVehicleID( playerid ),
        string[ 60 ];
    if ( !VehicleHasWindows( GetVehicleModel( vehid ) )) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, transporto priemon�, kurioje esate neturi lang�.");
    if ( Windows[ vehid ] == false )
    {
        format      ( string, 60, "* %s atidaro tr. priemon�s lang� (-us)." ,GetPlayerNameEx( playerid ));
        ProxDetector( 20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
        Windows[ vehid ] = true;
        return 1;
    }
    else if ( Windows[ vehid ] == true )
    {
        format      ( string, 60, "* %s u�daro tr. priemon�s lang� (-us)." ,GetPlayerNameEx( playerid ));
        ProxDetector( 20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
        Windows[ vehid ] = false;
        return 1;
    }
    return 1;
}
*/
/*
CMD:trunk( playerid, params[ ] )
{
    #pragma unused params
    if ( Mires[ playerid ] > 0 )   return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje." );
    if ( IsPlayerInAnyVehicle( playerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atidaryti baga�in�s sed�damas tr. priemon�je.");
    new car = GetNearestVehicle( playerid, 10.0 );
    if ( car == INVALID_VEHICLE_ID ) return 1;
    if ( GetVehicleTrunkSlots( GetVehicleModel( car ) ) < 1 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, �i transporto priemon� neturi baga�in�s." );
    if ( cInfo[ car ][ cLock ] == 1 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, tr. priemon�s baga�in� yra u�rakinta." );
    if ( sVehicles[ car ][ Faction ] > 0 )
    {
        if ( sVehicles[ car ][ Faction ] != pInfo[ playerid ][ pMember ] )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudotis �ia transporto priemone." );
    }
    new engine, lights, alarm, doors, bonnet, boot, objective;

    GetVehicleParamsEx( car, engine, lights, alarm, doors, bonnet, boot, objective );

    if ( boot != 1 )
    return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, tr. priemon�s baga�in� �iuo metu u�daryta." );

    new string[ 110 ];
    format      ( string, sizeof(string), "* %s pakelia baga�in�s dangt� ir atidaro tr. priemon�s baga�in�." ,GetPlayerNameEx( playerid ));
    ProxDetector( 20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
    ShowTrunk( playerid, car );
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
            format         ( string, 126, " %s %s i�jung� privat� frakcijos kanal� (/f). " ,GetPlayerRangName( playerid ), GetName( playerid ) );
            fInfo[ PlayerFaction( playerid ) ][ fChat ] = 1;
            SendTeamMessage( PlayerFaction( playerid ), COLOR_NEWS, string );
            return 1;
        }
        else
        {
            format         ( string, 126, " %s %s �jung� privat� frakcijos kanal� (/f). " ,GetPlayerRangName( playerid ), GetName( playerid ) );
            fInfo[ PlayerFaction( playerid ) ][ fChat ] = 0;
            SendTeamMessage( PlayerFaction( playerid ), COLOR_NEWS, string );
            return 1;
        }
    }
    return 1;
}

*/
/*
CMD:f( playerid, params[ ] )
{
    new string[ 256 ];
    if ( sscanf( params, "s[256]", string ) ) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /f [TEKSTAS]" );
    if ( Mires[ playerid ] > 0 )              return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje." );
    if ( Mute[ playerid ] == true )           return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, �iuo metu Jums yra u�drausta kalb�tis (/mute), nor�dami pa�alinti draudim� susisiekite su Administratoriumi." );
    if ( PlayerFaction( playerid ) == 0)      return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudotis �ia komanda netur�dami frakcijos." );
    if ( fInfo[ PlayerFaction( playerid ) ][ fChat ] == 1 && pInfo[ playerid ][ pLead ] == 0)
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, frakcijos kanalas (/f) �iuo metu yra i�jungtas. Susisiekite su frakcijos vadovu." );

    new
        pdtype[ 20 ] = "";
    if( PlayerFaction( playerid ) == 1 )
    {
        if ( GetPVarInt( playerid, "PDTYPE" ) == 1 ) pdtype = "(SWAT: Marksman)";
		if ( GetPVarInt( playerid, "PDTYPE" ) == 2 ) pdtype = "(SWAT: Elite)";
        else if ( GetPVarInt( playerid, "PDTYPE" ) == 3 ) pdtype = "(SWAT: Enforcer)";
    }
    format         ( string, 256, "((%s (%s%s): %s ))" ,GetPlayerRangName( playerid ), GetName( playerid ), pdtype, string );
    SendTeamMessage( PlayerFaction( playerid ), COLOR_FCHAT, string );
    return 1;
}
*/
CMD:id( playerid, params[ ] )
{
    new id[ 24 ];
    if ( sscanf( params, "s[24]", id ) )    return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /id [VEIK�JO ID] arba /id [VEIK�JO VARDAS ARBA DALIS VARDO]" );

    new string[ 64 ],
        name[ MAX_PLAYER_NAME ];

    foreach(Player,i)
    {
        GetPlayerName( i, name, sizeof(name) );
        if ( strfind( name, id, true ) != -1 )
        {
            format           ( string, 50,"Surastas veik�jas (ID: %d) %s", i, name );
            SendClientMessage( playerid, COLOR_WHITE, string );
        }
    }
    return 1;
}
CMD:frisk( playerid, params[ ] )
{
    new string[ 140 ],
        giveplayerid;
    if ( sscanf( params, "u", giveplayerid ) ) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /frisk [VEIK�JO ID]" );
    if ( !IsPlayerConnected( giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
    if ( !PlayerToPlayer   ( 5.0, playerid, giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti �io veiksmo veik�jui, kuris n�ra �alia J�s�..");

    format           ( string, 140, "D�mesio, %s nori Jus apie�koti, jei leid�iat�s apie�komas ra�ykite /accept frisk %d", GetPlayerNameEx( playerid ), playerid );
    SendClientMessage( giveplayerid, COLOR_WHITE, string );
    format           ( string, 140, "Veik�jas %s gavo pra�ym� leisti b�ti apie�komas J�s�, palaukite kol veik�jas atsakys. ", GetPlayerNameEx( giveplayerid ) );
    SendClientMessage( playerid, COLOR_WHITE, string );
    SetPVarInt       ( giveplayerid, "APIESKA", playerid );
    return 1;
}

CMD:do( playerid, params[ ] )
{
    new string[ 256 ];
    if ( sscanf( params, "s[256]", string ) ) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /do [VEIKSMAS]. Pavyzdys komandos naudojimui: /do Ant stalo pad�ta pinigin�" ); 
    if ( Mute[ playerid ] == true) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, �iuo metu Jums yra u�drausta kalb�tis (/mute), nor�dami pa�alinti draudim� susisiekite su Administratoriumi." );
    SetPlayerChatBubble(playerid, string, COLOR_PURPLE, 20.0, 10000);
    format(string,256,"* %s (( %s ))",string,GetPlayerNameEx(playerid));
    ProxDetector(15.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
//    if ( pInfo[ playerid ][ pJob ] == JOB_JACKER && ( LaikoTipas[playerid] == 5 || LaikoTipas[playerid] == 6 ) )
 //       ActionLog( pInfo[ playerid ][ pMySQLID ], string );
    return 1;
}
CMD:b( playerid, params[ ] )
{
    new string[ 256 ];
    if ( sscanf( params, "s[256]", string ) ) 
		return SendClientMessage( playerid , COLOR_LIGHTRED, "OOC kanalo naudojimas: /b [TEKSTAS]" ); 
	//SendClientMessage( playerid , COLOR_LIGHTRED, "Nenaudokite �io kanalo be reikalo, kadangi tai gali pri�aukti nuobaud�." );
    if ( Mute[playerid] == true ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, �iuo metu Jums yra u�drausta kalb�tis (/mute), nor�dami pa�alinti draudim� susisiekite su Administratoriumi.");

    if(AdminDuty[playerid] == true)
    //if ( pInfo[ playerid ][ pAdmin ] >= 1 )
    {
        format      ( string, 256, "{d6d6d6}(([ID: %d] {ca965a}%s{d6d6d6}: %s ))", playerid, GetName( playerid ), string );
        ProxDetector( 10.0, playerid, string, COLOR_FADE1, COLOR_FADE2, COLOR_FADE3, COLOR_FADE4, COLOR_FADE5 );
    }
    //else if ( pInfo[ playerid ][ pAdmin] >= 0 )
    else if(AdminDuty[playerid] == false)
    {
		format      ( string, 256, "(([ID: %d] %s: %s ))", playerid, GetName( playerid ), string );
		ProxDetector( 10.0, playerid, string, COLOR_FADE1, COLOR_FADE2, COLOR_FADE3, COLOR_FADE4, COLOR_FADE5 );
    }
    return 1;
}
CMD:g( playerid, params[ ] )
{
    new string[ 256 ];
    if ( sscanf( params, "s[256]", string ) ) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /g [TEKSTAS]" );
    if ( Mires[ playerid ] > 0 )   return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje." );
    if ( Mute[ playerid ] == true) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, �iuo metu Jums yra u�drausta kalb�tis (/mute), nor�dami pa�alinti draudim� susisiekite su Administratoriumi." );
    format          ( string, 256, "%s",string );
    SendOrginMessage( playerid, string );
    return 1;
}
/*
CMD:setswat( playerid, params[ ] )
{
	new giveplayerid,
		type;
	if ( sscanf( params, "ud", giveplayerid, type ) )
		return SendClientMessage( playerid ,COLOR_LIGHTRED, "KOMANDOS NAUDOJIMAS: /setswat [VEIK�JO ID][1-3]"), SendClientMessage( playerid ,COLOR_LIGHTRED, "B�RIAI: 1 - Marksman | 2 - Elite | 3 - Enforcer");
	if( UsePDCMD(playerid) != 1)
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente.");
    if ( pInfo[playerid][pRank] < 10 )
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �i� komand� gali naudoti tik auk�to rango pareig�nai.");
	if ( !PlayerToPlayer( 10.0, playerid, giveplayerid ) )
		return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jas n�ra �alia J�s�.");
 	if( !PDJOBPlace(playerid)) return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, nor�dami atlikti �� veiksm�, privalote b�ti policijos departamente");
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
/*
CMD:setfd( playerid, params[ ] )
{
    new
        giveplayerid;
    if ( sscanf( params, "u", giveplayerid ) ) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /setfd [VEIK�JO ID]");
    if ( PlayerFaction( playerid ) != 2 ) return SendClientMessage( playerid, GRAD, "neturite galimyb�s atlikti �� veiksm�." );
    if ( pInfo[playerid][pRank] < 6 ) return SendClientMessage( playerid, GRAD, "neturite galimyb�s atlikti �� veiksm�." );
    if ( !PlayerToPlayer( 10.0, playerid, giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti �io veiksmo jei nurodytas veik�jas n�ra �alia J�s�. ");
    if ( PlayerFaction( giveplayerid ) != 2 ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s ne");

    switch ( GetPVarInt ( giveplayerid, "PDTYPE" ) )
    {
        case 0:
        {
            SetPlayerSkin   ( giveplayerid, 277 );
            GivePlayerJobWeapon( giveplayerid, 9, 1 );
            GivePlayerJobWeapon( giveplayerid, 42, 2000 );
        }
        case 1:
        {
            SetPlayerSkin   ( giveplayerid, 276);
        }
    }
    return 1;
}
*/
/*
CMD:checkalco( playerid, params[ ] )
{
    new giveplayerid,
        DrunkLevel,
        string[ 126 ];
    if ( sscanf( params, "u", giveplayerid) ) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /checkalco [VEIK�JO ID]");
    if ( UsePDCMD( playerid ) == 0 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudotis �ia komanda jei nesate pareig�nas." );
    if ( !PlayerToPlayer( 10.0, playerid, giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti �io veiksmo jei nurodytas veik�jas n�ra �alia J�s�. ");
    DrunkLevel = floatround( GetPlayerDrunkLevel( giveplayerid ) /1000 );
    format      ( string, 126, "* %s prideda alkotester� prie %s lup�, kuris pripu�ia d promil� (-i�)." ,GetPlayerNameEx( playerid ), GetPlayerNameEx( giveplayerid ), DrunkLevel );
    ProxDetector( 20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
    return 1;
}
*/
/*
CMD:ad( playerid, params[ ] )
{
    if(!Data_IsPlayerInRangeOfCoords(playerid, 10.0, "advertisement_center")) 
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, reklamas � eter� galite skelbti (/ad) tik reklamos skyriuje, kuris yra Verona Mall." );
    if ( gettime() - LastPlayerAd[ playerid ] <= 30 && pInfo[ playerid ][ pDonator ] == 0 )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, skelbim� galite ra�yti tik prab�gus 30 sekund�i� po buvusio skelbimo para�ymo. " );

    new string[ 256 ],
        coast;

    if (isnull(params)) 
        return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /ad [J�S� REKLAMA]");
    coast = strlen( params );
    if ( coast < 40 )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Negalite paskelbti reklamos, kurios nesudaro net 40 simboli�. " );
    if ( GetPlayerMoney(playerid) < 250 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai pinig� (250$), kad atliktum�t �� veiksm�. ");

    GivePlayerMoney( playerid, -250);
    format           ( string, sizeof(string), "[Skelbimas] %s, kontaktai: %d", params, pInfo[ playerid ][ pPhone ] );
    SendNEWS         ( COLOR_AD, string );


    for(new i = sizeof LastAds - 1; i != 0; i--)
        if(!isnull(LastAds[ i-1 ]))
            strcpy(LastAds[ i ], LastAds[ i-1 ], MAX_AD_TEXT);
    strcpy(LastAds[ 0 ], string, MAX_AD_TEXT);

    //format           ( string, 256, "U� paskelbt� skelbim� eteryje sumok�jai: %d ", coast * 2 );
    //SendClientMessage( playerid, COLOR_WHITE, string );
    format           ( string, 70, "AdmWarn: veik�jas (%s) [ID: %d] para�� skelbim� (/ad)", GetName( playerid ), playerid );
    SendAdminMessage ( COLOR_ADM, string );

    LastPlayerAd[ playerid ] = gettime();
    return 1;
}*/

CMD:lastad( playerid, params[ ] )
{
    new
        string2[ 2048 ];
    for(new i = 0; i < sizeof(LastAds); i++)
    {
        if(isnull(LastAds[ i ]))
            continue;

        if(strlen(LastAds[ i ]) > 65)
        {
            new string[ 140 ];

            strmid( string, LastAds[ i ], 0, 64 );
            format( string2, 1024, "%s%s\n", string2, string );

            format( string, 140, "" );

            strmid( string, LastAds[ i ], 64, 128 );
            format( string2, 1024, "%s%s\n", string2, string );
            
            format( string, 140, "" );
            
            strmid( string, LastAds[ i ], 128, 192 );
            format( string2, 1024, "%s%s\n", string2, string );
        }
        else
            format( string2, 1024, "%s%s\n", string2, LastAds[ i ] );
    }
    ShowPlayerDialog(playerid,9999,DIALOG_STYLE_LIST,"Paskutiniai skelbimai",string2,"Atgal","");
    return 1;
}

CMD:crouch( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "CAMERA", "camcrch_idleloop", 4.0, 0, 1, 1, 1, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

    return true;
}

CMD:yes( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "GANGS", "Invite_Yes", 4.0, 0, 1, 1, 1, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

    return true;
}

CMD:no( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "GANGS", "Invite_No", 4.0, 0, 1, 1, 1, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

    return true;
}

CMD:chand( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_DRIVER )
        LoopingAnim( playerid, "CAR", "Tap_hand", 4.0, 0, 1, 1, 1, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra tr. priemon�je. ");

    return true;
}

CMD:bag( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "BASEBALL", "Bat_IDLE", 4.0, 1, 1, 1, 1, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

    return true;
}

CMD:riot( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "RIOT", "RIOT_ANGRY", 4.0, 1, 0, 0, 0, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

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
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

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
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");
    return true;
}

CMD:rem( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "MUSCULAR", "MuscleIdle", 4.0, 0, 1, 1, 1, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

    return true;
}


CMD:re( playerid, params[] )
    return cmd_report( playerid, params );

CMD:report( playerid, params[] )
{
    new giveplayerid,
        string[ 256 ];
    if ( sscanf( params, "us[256]",giveplayerid, string ) ) 
        return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /report [PRANE�AMO VEIK�JO ID][K� VEIK�JAS NUSI�ENG�] " );
    if ( giveplayerid == INVALID_PLAYER_ID )
        return SendClientMessage( playerid , COLOR_LIGHTRED, "Klaida, blogai nurodytas prane�amo veik�jo ID. Pasitikrinkite ar j� nurod�te teisingai.");
    if ( GetPVarInt( playerid, "REPORTED" ) == 1 ) 
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite taip greitai naudotis komanda /report. Palaukite minut� ir bandykite v�l. ");

    format(string,256,"** [AdmWarn] Veik�jas %s (ID:%d) prane�� apie (ID %d) %s, problema: %s ",GetName( playerid ), playerid, giveplayerid, GetName( giveplayerid ), string );
    SendAdminMessage(COLOR_YELLOW, string);
    SendAdminMessage(COLOR_YELLOW, "** KOMANDOS: /are [VEIK�JO ID] patvirtint/priimti prane�im� |  /dre [VEIK�JO ID] [KOD�L ATMET�T PRANE�IM�] - atmesti"); 

	SendChatMessage(playerid, COLOR_GREEN, "Sveikiname, J�s� prane�imas buvo s�kmingai i�si�stas visiems budintiems Administratoriams. Administratorius susisieks su Jumis d�l tolimesni� veiksm�..");

    SetPVarInt( playerid, "REPORTED", 1 );
    SetTimerEx( "REPORT_T", 60000, false, "d", playerid );
    return 1;
}
FUNKCIJA:REPORT_T( playerid )
    return DeletePVar( playerid, "REPORTED" );

/*
CMD:flist( playerid, params[ ] )
{
    #pragma unused params
    if ( PlayerFaction( playerid ) == 0 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudotis �ia komanda neb�dami frakcijoje.");
    SendClientMessage( playerid, COLOR_GREEN, "|___________ Frakcijai priklausantys nariai ___________|");
    new string[ 30 ];
    foreach(Player,i)
    {
        if ( pInfo[ playerid ][ pMember ] == pInfo[ i ][ pMember ] )
        {
            format           ( string, 30, "** %s [%d]", GetName( i ), pInfo[ i ][ pRank ] );
            SendClientMessage( playerid, COLOR_WHITE, string );
        }
    }
    return 1;
}
*/
CMD:w( playerid, params[ ] )
{
    new string[ 256 ],
        giveplayerid;
    if ( Mires[ playerid ] > 0 )   return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje." );
    if ( Mute[ playerid ] == true ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, �iuo metu Jums yra u�drausta kalb�tis (/mute), nor�dami pa�alinti draudim� susisiekite su Administratoriumi." );
    if ( sscanf( params, "us[256]", giveplayerid, string ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "IC kanalo naudojimas: /w [VEIK�JO ID][TEKSTAS], su �ia komanda galite sakyti �inutes �nab�d�dami..");
    if ( !PlayerToPlayer( 2.0, playerid, giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo veik�jui, kuris n�ra �alia J�s�. ");
    format         ( string, 256, "%s �nab�d�damas sako: %s", GetPlayerNameEx( playerid ), string);
    SendChatMessage( giveplayerid, COLOR_LIGHTRED, string );
    SendChatMessage( playerid, COLOR_LIGHTRED, string );
    format         ( string, 126, "* %s pasilenk�s prie %s, negirdimai su�nabd�a �od�ius ir atsitraukia.", GetPlayerNameEx( playerid ), GetPlayerNameEx( giveplayerid ) );
    ProxDetector   ( 15.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE );
    return 1;
}
CMD:cw( playerid, params[ ] )
{
    new string[ 256 ],
        message[ 256 ];
    if ( Mires[ playerid ] > 0 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje." );
    if ( Mute[ playerid ] == true ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, �iuo metu Jums yra u�drausta kalb�tis (/mute), nor�dami pa�alinti draudim� susisiekite su Administratoriumi." );
    if ( sscanf( params, "s[256]", message ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "IC kanalo naudojimas: /cw [TEKSTAS] ");
    if ( !IsPlayerInAnyVehicle( playerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s nes�dite transporto priemon�je. ");
    new veh = GetPlayerVehicleID(playerid);
    foreach(Player,i)
    {
        if ( IsPlayerInVehicle( i, veh ) )
        {
            new plstate = GetPlayerState( playerid );

            if ( plstate == PLAYER_STATE_DRIVER )
            {
                format( string, 256, "Vairuotojas %s sako: %s", GetPlayerNameEx( playerid ), message );
                SendChatMessage( i, 0xD7DFF3AA, string );
            }
            else if ( plstate == PLAYER_STATE_PASSENGER )
            {
                format( string, 256, "Pakeleivis %s sako: %s", GetPlayerNameEx( playerid ), message );
                SendChatMessage( i, 0xD7DFF3AA, string );
            }
        }
    }
    return 1;
}
CMD:oldcar(playerid, params[])
{
    new string[ 56 ];
    format(string, sizeof(string), "Paskutin�s tr. priemon�s, kuri� naudojote ID: %d", OldCar[ playerid ] );
    SendClientMessage( playerid, COLOR_WHITE, string );
    return 1;
}
/*
CMD:setbelt( playerid, params[ ] )
{
    #pragma unused params
    if ( !IsPlayerInAnyVehicle( playerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, J�s nesate tr. priemon�je. ");
    if ( !VehicleHasWindows( GetVehicleModel( GetPlayerVehicleID( playerid ) ) ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, tr. priemon� kurioje s�dite neturi saugos dir��, tad veiksmas negalimas.");
    new string[ 126 ];
    if ( Belt[ playerid ] == false )
    {
        format( string, 126, "* %s patempia saugos dir�� ir u�sisega saugos dir�us.", GetPlayerNameEx( playerid ) );
        Belt[ playerid ] = true;
    }
    else if ( Belt[ playerid ] == true )
    {
        format( string, 126, "* %s atsisega saugos dir�us.", GetPlayerNameEx( playerid ) );
        Belt[ playerid ] = false;
    }
    ProxDetector( 15.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
    return 1;
}
*/
CMD:togf( playerid, params[ ] )
{
    #pragma unused params
    if ( GetPVarInt( playerid, "TOG_FAMILY" ) == 1)
    {
        SetPVarInt       ( playerid, "TOG_FAMILY", 0 );
        SendClientMessage( playerid, GRAD, "J�s nebematysite frakcijos kanalo �inu�i�." );
        return 1;
    }
    else if ( GetPVarInt( playerid, "TOG_FAMILY" ) == 0)
    {
        SetPVarInt       ( playerid, "TOG_FAMILY", 1 );
        SendClientMessage( playerid, GRAD, "J�s matysite frakcijos kanalo pokalb�." );
        return 1;
    }
    return 1;
}
CMD:towup( playerid, params[ ] )
{
    if ( GetPlayerState( playerid ) != PLAYER_STATE_DRIVER ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: turite b�ti automobilyje Town Truck. " );
    new veh = GetPlayerVehicleID( playerid ),
        veh2;
    if ( GetVehicleModel( veh ) != 525 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: turite b�ti automobilyje Town Truck. " );
    if ( sscanf ( params, "d", veh2 ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /towup [Automobilio ID matomas /dl] ");
    new Float: Car_X,
        Float: Car_Y,
        Float: Car_Z;
    GetVehiclePos( veh2, Car_X, Car_Y, Car_Z );
    if ( !PlayerToPoint( 10, playerid, Car_X, Car_Y, Car_Z ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s nesate �alia to automobilio ");

    if ( IsTrailerAttachedToVehicle( veh ) )
        DetachTrailerFromVehicle( veh );
    else
    {
        StartTimer( playerid, 15, 10);
        SetPVarInt( playerid, "TOWING", veh2 );
    }
    return 1;
}
/*
CMD:fdclothes( playerid, params[ ] )
{
	#pragma unused params
	if ( PlayerFaction( playerid ) != 2 )
	    return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami ugniagesi� departamente.");

	if(!Data_IsPlayerInRangeOfCoords(playerid, 70.0, "job_firefighter_clothes"))
		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite persirengti neb�damas persirengimo kabinoje/kambaryje.");

	ShowModelSelectionMenu ( playerid, skinlist, "Select Skin" ) ;
    return 1;
}
CMD:pdclothes( playerid, params[ ] )
{
    #pragma unused params
    if ( !UsePDCMD( playerid ) )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente.");

    if(!PDJOBPlace(playerid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite persirengti neb�damas persirengimo kabinoje/kambaryje.");

    ShowModelSelectionMenu ( playerid, skinlist, "Select Skin" ) ;
    //ShowPlayerDialog(playerid,16,DIALOG_STYLE_INPUT,"Apranga","�ra�ykite norim� aprangos\nSkino ID, tada jis jum bus pakeistas.","Pakeisti","I�jungti");
    return 1;
}*/

CMD:sid( playerid, params[ ] )
{
    new string[ 80 ],
        giveplayerid;
    if ( sscanf( params, "u", giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /sid [ veik�jo vardas/ id ] ");
    if ( !PlayerToPlayer( 10, playerid, giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s turite b�ti �alia kito veik�jo. ");
    format           ( string, 80, "|______________%s______________|", GetName( playerid ) );
    SendClientMessage( giveplayerid, COLOR_GREEN, string );

    format           ( string, 80, "*| Vardas: %s Pavard�: %s", GetPlayerFirstName( playerid ), GetPlayerLastName( playerid ) );
    SendClientMessage( giveplayerid, COLOR_WHITE, string );

    format           ( string, 80, "*| Gimimo metai: %d Metai: %d", 2011 - pInfo[ playerid ][ pAge ], pInfo[ playerid ][ pAge ] );
    SendClientMessage( giveplayerid, COLOR_WHITE, string );

    format           ( string, 80, "*| Tautyb�: %s", pInfo[ playerid ][ pOrigin ] );
    SendClientMessage( giveplayerid, COLOR_WHITE, string );

    format           ( string, 80, "*| Asmens kodas: %d000000%d%d", pInfo[ playerid ][ pUcpID ], pInfo[ playerid ][ pAge ], pInfo[ playerid ][ pMySQLID ] );
    SendClientMessage( giveplayerid, COLOR_WHITE, string );

    format           ( string, 80, "* %s parodo savo asmens dokument� %s" ,GetName( playerid ), GetName( giveplayerid ) );
    ProxDetector     ( 20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
    return 1;
}

CMD:levelup(playerid,params[])
{
    if(!pInfo[ playerid ][ pPoints ])
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite laisv� ta�k� prisid�ti prie veik�jo savybi�..");

    new type;
    if(sscanf(params,"i",type) || (type != 1 && type != 2)) 
    {
        SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /levelup [1/2]");
        SendClientMessage(playerid, COLOR_LIGHTRED, "Pasirinkimas: Papildomos gyvyb�s - 1, papildoma j�ga - 2");
        return 1;
    }

    new string[70];
    switch(type)
    {
        case 1: 
        {
            pInfo[ playerid ][ pHealthLevel ]++;
            format(string,sizeof(string), "[LevelUp] S�kmingai pasik�l�t� veik�jo gyvybi� skai�i�. Dabar J�s� veik�jas tur�s %.2f gyvybes.",pInfo[ playerid ][ pHealthLevel ] * 3 + 100.0);
            SendClientMessage(playerid, COLOR_NEWS, string);
        }
        case 2:
        {
            pInfo[ playerid ][ pStrengthLevel ] ++;
            SendClientMessage(playerid, COLOR_NEWS, "[LevelUp] S�kmingai pasik�l�t� veik�jo fizin� j�g�. Daugiau informacijos komandoje /stats.");
        }
    }
    pInfo[ playerid ][ pPoints ]--;
    SaveAccount(playerid);

    format(string,sizeof(string),"Jums liko %d ta�kai.", pInfo[ playerid ][ pPoints ]);
    SendClientMessage(playerid, COLOR_NEWS, string);
    return 1;
}
/*
CMD:o( playerid, params[ ] )
{
    new string[ 256 ];
    if ( sscanf( params, "s[256]", string ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /o [tekstas]" );
    if ( Mute[ playerid ] == true ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, �iuo metu Jums yra u�drausta kalb�tis (/mute), nor�dami pa�alinti draudim� susisiekite su Administratoriumi." );
    if ( OOCDisabled == false ) return SendClientMessage( playerid, GRAD, "OOC kanalas yra u�draustas administratoriaus." );
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
CMD:a( playerid, params[ ] )
{
    if(GetPlayerAdminLevel(playerid) > 0 )
    {
        new string[ 256 ];

        if ( sscanf( params, "s[256]", string ) ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /a [tekstas]");
        format          ( string, 256, "[Adm. level: %d] %s[ID:%d]: %s", GetPlayerAdminLevel(playerid), GetName( playerid ), playerid, string );
        SendAdminMessage( COLOR_ADM, string );
        return 1;
    }
    return 1;
}
CMD:admins( playerid, params[ ] )
{
    #pragma unused params
    SendClientMessage(playerid, COLOR_TEAL, "---------------------------PRISIJUNG� ADMINISTRATORIAI----------------------------------");

    foreach(Player, x) {
        if(GetPlayerAdminLevel(x) >= 1 && !GetPVarInt( x, "hideadmin" ) && AdminDuty[ x ]) {
            format(szMessage, sizeof(szMessage), "%s [AdmLVL: %d] %s [%s] budintis statusas (/re).",  GetAdminRank( x ), GetPlayerAdminLevel(x), GetName(x), pInfo[ x ][ pForumName ]);
            SendClientMessage(playerid, COLOR_GREEN, szMessage);
        }
        if(GetPlayerAdminLevel(x) >= 1 && !GetPVarInt( x, "hideadmin" ) && !AdminDuty[ x ]) {
            format(szMessage, sizeof(szMessage), "%s [AdmLVL: %d] %s [%s] nebudintis statusas.",  GetAdminRank( x ),GetPlayerAdminLevel(x), GetName(x), pInfo[ x ][ pForumName ]);
            SendClientMessage(playerid, COLOR_GREY, szMessage);
        }
    }

    SendClientMessage(playerid, COLOR_TEAL, "----------------------------------------------------------------------------------------------");
    return 1;
}
/*
CMD:transfer( playerid, params[ ] )
{
    new giveplayerid,
        items,
        IP[ 16 ],
        IP2[ 16 ],
        string[ 126 ];
    if ( sscanf( params, "ud", giveplayerid, items ) )
    {
        SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /transfer [veik�jo id] [suma]");
        return 1;
    }
    if ( pInfo[ playerid ][ pSavings ] > 0 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: Banku naudotis negalite, kol esate pasid�je terminuot� ind�l�. " );
    if ( !PlayerToPoint( 20.0, playerid, 295.6938,1012.7919,2119.1150 ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s nesate banke" );
    if ( GetPlayerLevel(playerid) < 2 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s� Lygis per ma�as, minimalus 2 Lygis. " );
    if ( playerid == giveplayerid ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: ginkl�/pinig� sau duoti negalite." );
    if ( !IsPlayerConnected( giveplayerid ) ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
    if ( items < 0 || items > 999999 ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: perduodama suma negali b�ti ma�esn� nei 0 ir didesn� negu 999999 " );
    if ( GetPlayerBankMoney(playerid) < items ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: neturite tiek pinig�. ");

    GetPlayerIp( playerid, IP, 16 );
    GetPlayerIp( giveplayerid, IP2, 16 );

    if( !strcmp( IP, IP2, true ) || pInfo[ playerid ][ pUcpID ] == pInfo[ giveplayerid ][ pUcpID ] )
        return true;

    SendClientMessage(giveplayerid, COLOR_GREEN, "|____ PERVEDIMAS ____|");
    format(string, 126, " Perved�: $%d",items); SendClientMessage(giveplayerid, COLOR_WHITE, string);
    format(string, 126, " Perved�jas: %s",GetName( playerid )); SendClientMessage(giveplayerid, COLOR_FADE1, string);

    if ( items > 49999 )
    {
        format( string, 70, "AdmWarn: veik�jas (%s) naudodamas (/transfer) perved� veik�jui (%s), %d$", GetName( playerid ), GetName( giveplayerid ), items );
        SendAdminMessage( COLOR_ADM, string );
    }

    SendClientMessage( playerid, COLOR_WHITE, "Pervedimas s�kmingas!" );
    PayLog           ( pInfo[ playerid ][ pMySQLID ], 15, pInfo[ giveplayerid ][ pMySQLID ], items );
    PayLog           ( pInfo[ giveplayerid ][ pMySQLID ], 14, pInfo[ playerid ][ pMySQLID ], items );

    SetPlayerBankMoney(playerid, GetPlayerBankMoney(playerid) - items);
    SetPlayerBankMoney(giveplayerid, GetPlayerBankMoney(giveplayerid) + items);

    SaveAccount      ( playerid );
    SaveAccount      ( giveplayerid );
    return 1;
}

CMD:bank( playerid, params[ ] )
{
    #pragma unused params
    if ( NearBankomat( playerid ) && !IsPlayerInAnyVehicle( playerid ) )
    {
        new string[ 64 ];
        format( string, sizeof(string), "J�s� banko s�skaitoje %d$\n Kiek nor�site i�siimti?",GetPlayerBankMoney(playerid));
        ShowPlayerDialog( playerid, 2, DIALOG_STYLE_INPUT, "Bankomatas", string, "Nuimti", "At�aukti" );
        return 1;
    }
    return 1;
}*/
/*
CMD:tlc( playerid, params[ ] )
{
    #pragma unused params
    if ( PlayerFaction( playerid ) == 1 || PlayerFaction( playerid ) == 2 )
    {
        if ( Tlc[ 0 ] == 0 ) return 1;
        SetPlayerCheckPointEx( playerid, CHECKPOINT_TLC, Tlc[ 0 ], Tlc[ 1 ], Tlc[ 2 ], 5.0 );
        Tlc[ 0 ] = 0;
        return 1;
    }
    return 1;
}*/
/*
CMD:note( playerid, params[ ] )
{
    if(!IsItemInPlayerInventory(playerid, ITEM_NOTE)) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: su savimi neturite u�ra�� knygut�s.");
    new string[ 256 ],
        param [ 256  ],
        skai,
        ministr[ 8 ],
        ed[ 16 ];

    param = strtok( params, skai );
    if ( !strlen( param ) )
    {
        SendClientMessage( playerid, GRAD, "PAGALBA: /note [ tekstas ] " );
        SendClientMessage( playerid, COLOR_WHITE, "INFORMACIJA: delete - i�valykite tekst�." );
        SendClientMessage( playerid, COLOR_WHITE, "INFORMACIJA: show - per�iur�ti savo u�ra�us." );
        SendClientMessage( playerid, COLOR_WHITE, "INFORMACIJA: add - prid�site papildomos u�ra�us.");
        return 1;
    }
    if ( !strcmp( param, "delete", true ) )
    {
        param = strtok( params, skai );
        if( !strlen( param ) ) return SendClientMessage( playerid, COLOR_WHITE, "PAGALBA: /note delete [ slotas 1 - 7 ]" );
        new slot = strval( param );
        if ( slot > 0 && slot <= 7 )
        {
            format           ( ministr, 8, "NOTE_%d", slot );
            SetPVarString    ( playerid, ministr, "" );
            format           ( ministr, 8, "NOTE2_%d", slot );
            SetPVarInt       ( playerid, ministr, 0 );
            SendClientMessage( playerid, COLOR_WHITE, "U�ra�ai i�trinti" );
            format     ( string, sizeof(string), "DELETE FROM notes WHERE owner = %d AND slot = %d", pInfo[ playerid ][ pMySQLID ], slot );
            mysql_query(DbHandle,  string, false);
            return 1;
        }
        else
            return SendClientMessage( playerid, COLOR_WHITE, "PAGALBA: /note delete [ slotas 1 - 7 ]" );
    }
    else if ( !strcmp( param,"show" , true ) )
    {
        SendClientMessage( playerid, COLOR_GREEN2, "_____________ J�s� u�ra�ai _____________" );
        for ( new i = 1; i <= 7; i++ )
        {
            format           ( ministr, 8, "NOTE_%d", i );
            GetPVarString    ( playerid, ministr, string, 129 );
            format           ( ministr, 8, "NOTE2_%d", i );
            format           ( string, sizeof(string),"| %d: %s/%d", i, string, GetPVarInt ( playerid, ministr ) );
            SendClientMessage( playerid, COLOR_WHITE, string );
        }
    }
    else if ( !strcmp( param, "add", true ) )
    {
        param = strtok( params, skai );
        if( !strlen( param ) ) return SendClientMessage( playerid, COLOR_WHITE, "PAGALBA: /note add [ slotas 1 - 7 ][ tel. nr. ][ tekstas ]" );
        new slot = strval( param );
        param = strtok( params, skai );
        if( !strlen( param ) ) return SendClientMessage( playerid, COLOR_WHITE, "PAGALBA: /note add [ slotas 1 - 7 ][ tel. nr. ][ tekstas ]" );
        new number = strval( param );
        param = strtok( params, skai );
        if( !strlen( param ) ) return SendClientMessage( playerid, COLOR_WHITE, "PAGALBA: /note add [ slotas 1 - 7 ][ tel. nr. ][ tekstas ]" );
        
        if ( slot > 0 && slot < 8 )
        {
            if ( strlen( param ) < 1 || strlen( param ) > 128)
                return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: Per ma�ai/daug teksto!");
            if ( number < 0 )
                return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: Klaida!");

            valstr(ed,number);

            mysql_real_escape_string(param,param);

            format           ( ministr, 8, "NOTE_%d", slot );
            SetPVarString    ( playerid, ministr, param );
            format           ( ministr, 8, "NOTE2_%d", slot );
            SetPVarInt       ( playerid, ministr, number );
            format           ( string, sizeof(string),"| %d: %s/%d", slot, param, number );
            SendClientMessage( playerid, COLOR_WHITE, string );
            savePlayerNotes  ( playerid, slot );
        }
        else
            return SendClientMessage( playerid, COLOR_WHITE, "PAGALBA: /note add [ slotas 1 - 7 ][ tel. nr. ][ tekstas ]" );
    }
    return 1;
}
*/
stock checkVehicleByNumbers( numbers[ ] )
{
    foreach(Vehicles,i)
    {
        if ( !strcmp( numbers, cInfo[ i ][ cNumbers ], true ) ) return i;
    }
    return INVALID_VEHICLE_ID;
}

/*
CMD:fopen( playerid, params[ ] )
{
    #pragma unused params
    if( PlayerFaction( playerid ) == 1 && pInfo[playerid][pRank] >= 4 )
    {
        if(IsPlayerInRangeOfPoint(playerid, 3.0 , 225.07645, 115.93130, 1002.21564 ) )
        {
            if ( vartai[ 2 ][ 1 ] == 0)
            {
                MoveObject (vartai[ 2 ][ 0 ], 223.57640, 115.93130, 1002.21558, 0.97, 0, 0, 0);
                vartai[ 2 ][ 1 ] = 1;
            }
            else 
            {
                MoveObject (vartai[ 2 ][ 0 ], 225.07645, 115.93130, 1002.21564, 0.97, 0, 0, 0);
                vartai[ 2 ][ 1 ] = 0;
            }
        }
        else if(IsPlayerInRangeOfPoint(playerid, 3.0 , 239.64650, 118.66280, 1002.21570 ) )
        {
            if ( vartai[ 3 ][ 1 ] == 0)
            {
                MoveObject (vartai[ 3 ][ 0 ], 239.64650, 120.16280, 1002.21570, 0.97, 0, 0, -90);
                vartai[ 3 ][ 1 ] = 1;
            }
            else
            {
                MoveObject (vartai[ 3 ][ 0 ], 239.64650, 118.66280, 1002.21570, 0.97, 0, 0, -90);
                vartai[ 3 ][ 1 ] = 0;
            }
        }
        else if(IsPlayerInRangeOfPoint(playerid, 3.0 , 217.56250, 120.77590, 1002.20752 ) )
        {
            if ( vartai[ 4 ][ 1 ] == 0)
            {
                MoveObject (vartai[ 4 ][ 0 ], 217.56250, 122.27590, 1002.20752, 0.97, 0, 0, -90);
                vartai[ 4 ][ 1 ] = 1;
            }
            else
            {
                MoveObject (vartai[ 4 ][ 0 ], 217.56250, 120.77590, 1002.20752, 0.97, 0, 0, -90);
                vartai[ 4 ][ 1 ] = 0;
            }
        }
        else if(IsPlayerInRangeOfPoint(playerid, 3.0 , 266.45758, 115.84705, 1003.61621 ) )
        {
            if ( vartai[ 5 ][ 1 ] == 0)
            {
                MoveObject (vartai[ 5 ][ 0 ], 267.95761, 115.84710, 1003.6162, 0.97, 0, 0, 180);
                vartai[ 5 ][ 1 ] = 1;
            }
            else
            {
                MoveObject (vartai[ 5 ][ 0 ], 266.45758, 115.84705, 1003.61621, 0.97, 0, 0, 180);
                vartai[ 5 ][ 1 ] = 0;
            }
        }
        else if(IsPlayerInRangeOfPoint(playerid, 3.0 , -10.00610, 2053.78491, 2129.00000 ) )
        {
            if ( vartai[ 6 ][ 1 ] == 0)
            {
                MoveObject (vartai[ 6 ][ 0 ], -10.00607, 2053.78491, 2129.00000, 0.97, 0, 0, 180);
                vartai[ 6 ][ 1 ] = 1;
            }
            else
            {
                MoveObject (vartai[ 6 ][ 0 ], -10.00610, 2053.78491, 2129.00000, 0.97, 0, 0, 180);
                vartai[ 6 ][ 1 ] = 0;
            }
        }		
    }
    return 1;
}
*/
/*
CMD:open( playerid, params[ ] )
{
    #pragma unused params
    new Float:dist = 5.0;
    if ( IsPlayerInAnyVehicle( playerid ) )
        dist = 9.0;
    else
        dist = 5.0;

    for ( new obj = 0; obj < sizeof CloseGate; obj ++ )
    {
        if ( PlayerToPoint( dist, playerid, CloseGate[ obj ][ 1 ], CloseGate[ obj ][ 2 ], CloseGate[ obj ][ 3 ] )
           && PlayerFaction( playerid ) == floatround( CloseGate[ obj ][ 7 ], floatround_round ) )
        {
            if ( Gates[ obj ] == false )
            {
                MoveObject  ( Gates2[ obj ], MoveGate[ obj ][ 0 ], MoveGate[ obj ][ 1 ], MoveGate[ obj ][ 2 ] , 0.97, MoveGate[ obj ][ 3 ], MoveGate[ obj ][ 4 ], MoveGate[ obj ][ 5 ] );
                Gates[ obj ] = true;
            }
            else if ( Gates[ obj ] == true )
            {
                MoveObject  ( Gates2[ obj ], CloseGate[ obj ][ 1 ], CloseGate[ obj ][ 2 ], CloseGate[ obj ][ 3 ], 0.97, CloseGate[ obj ][ 4 ], CloseGate[ obj ][ 5 ], CloseGate[ obj ][ 6 ] );
                Gates[ obj ] = false;
            }
        }
    }
    return 1;
}
*/
/*
CMD:bonnet(playerid)
{
    if ( !IsPlayerInAnyVehicle( playerid ) )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s nes�dite transporto priemon�je." );
    if ( GetPlayerState( playerid ) != PLAYER_STATE_DRIVER )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s ne automobilio vairuotojas." );

    new engine, lights, alarm, doors, bonnet, boot, objective,
        vehicleid = GetPlayerVehicleID( playerid );

    GetVehicleParamsEx(vehicleid, engine, lights, alarm, doors, bonnet, boot, objective);

    if ( bonnet == 1 )
    {
        SendClientMessage(playerid, COLOR_WHITE, " Automobilio kapotas u�darytas." );
        SetVehicleParamsEx(vehicleid, engine, lights, alarm, doors, 0, boot, objective);
        PlayerPlaySound(playerid, 1057, 0.0, 0.0, 0.0 );
        return 1;
    }
    else
    {
        SendClientMessage(playerid, COLOR_WHITE, " Automobilio kapotas atidarytas." );
        SetVehicleParamsEx(vehicleid, engine, lights, alarm, doors, 1, boot, objective);
        PlayerPlaySound(playerid, 1057, 0.0, 0.0, 0.0);
        return 1;
    }
}
CMD:trunko(playerid)
{
    new engine, lights, alarm, doors, bonnet, boot, objective,
        vehicleid = GetNearestVehicle( playerid, 10.0 );

    if ( vehicleid == INVALID_VEHICLE_ID )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, �alia J�s� n�ra jokios tr. priemon�s. ");
    if ( cInfo[ vehicleid ][ cLock ] == 1 )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, tr. priemon�s baga�in� yra u�rakinta." );

    GetVehicleParamsEx( vehicleid, engine, lights, alarm, doors, bonnet, boot, objective );

    if ( boot == 1 )
    {
        SendClientMessage(playerid, COLOR_WHITE, " U�dar�t� tr. priemon�s baga�in�." );
        SetVehicleParamsEx(vehicleid, engine, lights, alarm, doors, bonnet, 0, objective);
        PlayerPlaySound(playerid, 1057, 0.0, 0.0, 0.0 );
        return 1;
    }
    else
    {
        SendClientMessage(playerid, COLOR_WHITE, " Atidar�te tr. priemon�s baga�in�." );
        SetVehicleParamsEx(vehicleid, engine, lights, alarm, doors, bonnet, 1, objective);
        PlayerPlaySound(playerid, 1057, 0.0, 0.0, 0.0);
        return 1;
    }
}
*/
CMD:buygun( playerid, params[ ] )
{
    #pragma unused params
    if(!PlayerToPoint(10.0,playerid,296.7012,-37.4115,1001.5156)) return 1;
    ShowPlayerDialog(playerid,6,DIALOG_STYLE_LIST ,"Ammu-nation parduotuv�","\
      1. Kastetas \t150$\
    \n2. Profesonali golfo lazda \t498$\
    \n3. Ki�eninis peilis \t89$\
    \n4. Medin� beisbolo lazda \t91$\
    \n5. Kastuvas \t75$\
    \n6. Bilijardo lazda \t344$\
    \n7. Paprasta lazda rankai \t43$\
	\n8. Da�� balion�lis (80) \t110$\
	\n9. Japoni�kas kalavijas - katana \t720$","Pirkti","At�aukti");
    return 1;
}
CMD:try( playerid, params[ ] )
{
    new string[ 256 ];
    if( sscanf( params, "s[256]", string ) ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /try [veiksmas]");
    if( Mires[ playerid ] > 0 ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje.");
    if( Mute[ playerid ] == true ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �iuo metu Jums yra u�drausta kalb�tis (/mute), nor�dami pa�alinti draudim� susisiekite su Administratoriumi.");
    new tryd = random(2)+1;
    if(tryd == 1)
        format(string,256," *** %s bando: %s ir pavyksta.",GetPlayerNameEx( playerid ), string);
    else
        format(string,256," *** %s bando: %s bet nepavyksta.",GetPlayerNameEx( playerid ), string );
    ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE);
    return 1;
}
CMD:s( playerid, params[ ] )
{
    new string[ 256 ];
    if(Mires[playerid] > 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje.");
    if(Mute[playerid] == true) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �iuo metu Jums yra u�drausta kalb�tis (/mute), nor�dami pa�alinti draudim� susisiekite su Administratoriumi.");
    if(sscanf(params,"s[256]", string )) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /s [textas]");
    format(string, 256, "%s �aukia: %s", GetPlayerNameEx(playerid), string);
    ProxDetector(15.0, playerid, string,COLOR_WHITE,COLOR_WHITE,COLOR_WHITE,COLOR_FADE1,COLOR_FADE2);
    return 1;
}

CMD:low( playerid, params[ ] )
{
    new string[ 256 ];
    if(Mires[playerid] > 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje.");
    if(Mute[playerid] == true) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �iuo metu Jums yra u�drausta kalb�tis (/mute), nor�dami pa�alinti draudim� susisiekite su Administratoriumi.");
    if(sscanf( params, "s[256]", string )) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /low [textas]");
    format(string, 256, "%s sako:[Tyliai] %s", GetPlayerNameEx(playerid), string);
    ProxDetector(2.0, playerid, string,COLOR_WHITE,COLOR_WHITE,COLOR_WHITE,COLOR_FADE1,COLOR_FADE2);
    return 1;
}
CMD:t( playerid, params[ ] )
{
    new string[ 256 ];
    if(Mires[playerid] > 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje.");
    if(Mute[playerid] == true) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �iuo metu Jums yra u�drausta kalb�tis (/mute), nor�dami pa�alinti draudim� susisiekite su Administratoriumi.");
    if(sscanf(params,"s[256]", string )) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /t [textas]");
    format(string, 256, "%s sako: %s", GetPlayerNameEx(playerid), string);
    ProxDetector(7.0, playerid, string,COLOR_WHITE,COLOR_WHITE,COLOR_WHITE,COLOR_FADE1,COLOR_FADE2);
    return 1;
}
/*
CMD:d( playerid, params[ ] )
{
    new string[ 256 ];
    if(Mires[playerid] > 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje.");
    if(Mute[playerid] == true) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �iuo metu Jums yra u�drausta kalb�tis (/mute), nor�dami pa�alinti draudim� susisiekite su Administratoriumi.");
    if(PlayerFaction( playerid ) == 1 || PlayerFaction( playerid ) == 2 || PlayerFaction( playerid ) == 3 || PlayerFaction( playerid ) == 5)
    {
        if(sscanf( params, "s[256]", string )) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /d [textas]");
        format(string, 256, "|TARPDEPARTAMENTIN� RACIJA| %s[%s] prane�a: %s", GetPlayerNameEx( playerid ),GetPlayerRangName( playerid ), string);
        SendTeamMessage(1, COLOR_POLICE, string);
        SendTeamMessage(2, COLOR_LIGHTRED, string);
        SendTeamMessage(5, COLOR_LIGHTRED2, string);
        return 1;
    }
    return 1;
}
*/
/*
CMD:startmission(playerid,params[])
{
    if(pInfo[ playerid ][ pJob ] != JOB_TRASH)
        return 0;
    new vehicleid = GetPlayerVehicleID(playerid);

    if(TrashMission[ playerid ] != TRASH_MISSION_NONE)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s jau esate prad�jas vygdyti misij�, nor�dami j� nutraukti ra�ykite /endmission");

    if(!vehicleid || sVehicles[ vehicleid ][ Job ] != JOB_TRASH)   
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nor�dami atlikti �� veiksm�, turite b�ti �i��kleve��je.");

    if(isnull(params))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /startmission [RAJONO PAVADINIMAS]"),
		SendClientMessage(playerid, COLOR_LIGHTRED, "RAJONAI: Rodeo Market Mulholand Jefferson Idlewood Colinas Beach East");

    new missionId = TRASH_MISSION_NONE;
    if(!strcmp(params,"Rodeo",true))
        missionId = TRASH_MISSION_MONTGOMERY;
    else if(!strcmp(params,"Market", true))
        missionId = TRASH_MISSION_DILIMORE;
    else if(!strcmp(params,"Mulholand", true))
        missionId = TRASH_MISSION_POLOMINO_CREEK;
    else if(!strcmp(params,"Jefferson", true))
        missionId = TRASH_MISSION_JEFFERSON;
    else if(!strcmp(params,"Idlewood", true))
        missionId = TRASH_MISSION_IDLEWOOD;		

    if(missionId == TRASH_MISSION_NONE)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /startmission [RAJONO PAVADINIMAS]"),
		SendClientMessage(playerid, COLOR_LIGHTRED, "RAJONAI: Rodeo Market Mulholand Jefferson Idlewood");

    TrashMission[ playerid ] = missionId;
    CurrentTrashCp[ playerid ] = 1;
    SetPVarInt(playerid, "TrashMission_Vehicle", vehicleid);
    SetTimerEx( "StartEngine", 1500, false, "dd", playerid, vehicleid );
    StartingEngine[playerid] = true;
    //SetPlayerCheckPointEx(playerid, CHECKPOINT_TRASH, TrashCp[ missionId ][ 0 ][ PosX ], TrashCp[ missionId ][ 0 ][ PosY ], TrashCp[ missionId ][ 0 ][ PosZ ], 5.0);
    ShowPlayerTrashMissionCP(playerid, TrashMission[ playerid ], 1);
	SendClientMessage(playerid, COLOR_NEWS, "�i��kleve�io misija s�kmingai prad�ta."),	
    SendClientMessage(playerid, COLOR_LIGHTRED2, "MISIJA: Va�iuokite surinkti �i�k�li� � pasirinkt� rajon�, kuris nustatytas J�s� �em�lapyje."),
 	SendClientMessage(playerid, COLOR_NEWS, "KOMANDOS: /takegarbage - paiimti �i��k�l�ms. /throwgarbage - i�mesti �i�k�l�ms � sunke�im�.");	   
	cmd_ame(playerid, "pasuka automobilio raktel� ir bando u�vesti varikl�.");
    return 1;
}

CMD:endmission(playerid)
{
    if(pInfo[ playerid ][ pJob ] != JOB_TRASH)
        return 0;
    if(TrashMission[ playerid ] == TRASH_MISSION_NONE)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s nesate prad�jas misijos. Ra�ykite  /startmission");

    EndTrashMission(playerid);
    SendClientMessage(playerid, COLOR_LIGHTRED, "S�kmingai nutrauk�t� misij�, bet J�s� surinktos �i�k�l�s sunkve�ime niekur nedings.");
    return 1;
}
*/
/*
// Valid numbers are 1 - limit 
stock ShowPlayerTrashMissionCP(playerid, mission, number)
{
    new count = 0;
    for(new i = 0 ; i < sizeof(GarbageInfo); i++)
    {
        if(GarbageInfo[ i ][ gMission ] == TRASH_MISSION_NONE) continue;
        if(GarbageInfo[ i ][ gMission ] == mission)
        {
            count++;
            if(number == count)
            {
                new Float:x,Float:y,Float:z;
                GetDynamicObjectPos(GarbageInfo[ i ][ gObjectId ], x, y, z);
                SetPlayerCheckPointEx(playerid, CHECKPOINT_TRASH, x, y, z, 4.0);
                return 1;
            }
        }
    }
    return 0;
}
*/
/*
stock HideTrashMissionObjectForPlayer(playerid, mission, number)
{
    new Players[ MAX_PLAYERS ], playerCount;
    foreach(Player,i)
    {
        if(i == playerid) continue;
        Players[ playerCount ++ ] = i;
    }

    new count = 0;
    for(new i = 0 ; i < sizeof(GarbageInfo); i++)
    {
        if(GarbageInfo[ i ][ gMission ] == TRASH_MISSION_NONE) continue;
        if(GarbageInfo[ i ][ gMission ] == mission)
        {
            count++;
            if(number == count)
            {
                new Float:pos[6];
                GetDynamicObjectPos(GarbageInfo[ i ][ gObjectId ], pos[0], pos[1], pos[2]);
                GetDynamicObjectRot(GarbageInfo[ i ][ gObjectId ], pos[3], pos[4], pos[5]);
                DestroyDynamicObject(GarbageInfo[ i ][ gObjectId ]);
                GarbageInfo[ i ][ gObjectId ] = CreateDynamicObjectEx(GarbageInfo[ i ][ gModel ],
                    pos[0],pos[1],pos[2],pos[3],pos[4],pos[5],
                    .players = Players, .maxplayers = playerCount);
                return 1;
            }
        }
    }
    return 0;
}*/
/*
EndTrashMission(playerid)
{
    SetVehicleToRespawn(GetPVarInt(playerid, "TrashMission_Vehicle"));
    TrashBagsInTrashVehicle[ GetPVarInt(playerid, "TrashMission_Vehicle") ] = 0;
    ShowMissionTrashObjects(playerid, TrashMission[ playerid ]);
    SetPlayerSpecialAction(playerid, SPECIAL_ACTION_NONE);
    TrashMission[ playerid ] = TRASH_MISSION_NONE;
    DisablePlayerCheckpoint(playerid);
    CurrentTrashCp[ playerid ] = 0;
    Checkpoint[ playerid ] = CHECKPOINT_NONE;
    DeletePVar(playerid, "TrashMission_Vehicle");
    KillTimer(TrashTimer[ playerid ]);
    if(IsPlayerAttachedObjectSlotUsed(playerid, TRASH_OBJECT_INDEX))
        RemovePlayerAttachedObject(playerid, TRASH_OBJECT_INDEX);
}*/
/*
CMD:takegarbage(playerid)
{
    if(pInfo[ playerid ][ pJob ] != JOB_TRASH)
        return 0;
    if(!IsPlayerInCheckpoint(playerid) && Checkpoint[ playerid ] == CHECKPOINT_TRASH)
        return SendClientMessage(playerid, GRAD, "Klaida, nor�dami paiimti �i�k�les privalote b�ti �i�k�li� pa�ym�toje vietoje.");
    if(IsPlayerInAnyVehicle(playerid))
        return SendClientMessage(playerid, GRAD, "Klaida, negalite paimti �iuk�li� b�dami transporto priemon�je.");
    if(IsCarryingTrash[ playerid ])
        return SendClientMessage(playerid, GRAD,"Klaida, J�s jau turite pa�m�s �i�k�les");

    ApplyAnimation(playerid,"CARRY","LIFTUP",4.1,0,1,1,0,1000,1);
    SetPVarInt(playerid, "Tipas2", 3);
    SetPlayerSpecialAction(playerid,SPECIAL_ACTION_CARRY);
    IsCarryingTrash[ playerid ] = true;
    //SetPlayerAttachedObject(playerid,TRASH_OBJECT_INDEX ,1265,1, 0.100000, 0.553958, -0.024002, 356.860290, 269.945068, 0.000000, 0.834606, 1.000000, 0.889027 );
    HideTrashMissionObjectForPlayer(playerid, TrashMission[ playerid ], CurrentTrashCp[ playerid ]);

    SendClientMessage(playerid, COLOR_NEWS, "S�kmingai paiim�te mai�� su �i�k�l�mis, dabar prieikite prie sunkve�imio galo ir �meskite su komanda: /throwgarbage");
    Checkpoint[playerid] = CHECKPOINT_NONE;
    DisablePlayerCheckpoint(playerid);
    return 1;
}

CMD:throwgarbage(playerid)
{
    if(pInfo[ playerid ][ pJob ] != JOB_TRASH)
        return 0;

    if(!IsCarryingTrash[ playerid ])
        return SendClientMessage(playerid, GRAD, "Klaida, negalite i�mesti �i�k�li�, kuri� neturite rankoje. �sitikinkite ar tikrai paiim�te �i�k�li� mai��.");

    new vehicleid = GetPVarInt(playerid, "TrashMission_Vehicle"),Float:x,Float:y,Float:z;
    GetVehiclePos(vehicleid, x,y, z);
    GetXYBehindVehicle(vehicleid,x,y,4.0);


    if(!IsPlayerInRangeOfPoint(playerid, 3.0, x,y,z-1.0))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, esate per toli nuo sunkve�imio galo, tad negalite �mesti �i�k�li�.");

    TrashBagsInTrashVehicle[ vehicleid ]++;
    ApplyAnimation(playerid, "GRENADE", "WEAPON_THROWU", 4.1, 0, 0, 0, 0, 0);
    SetPlayerSpecialAction(playerid, SPECIAL_ACTION_NONE);
    IsCarryingTrash[ playerid ] = false;
    SetTimerEx("RemoveGarbageBagDelay", 300, false,"i",playerid);

    CurrentTrashCp[ playerid ] ++;
    // Baigta misija. ShowPlayerTrashMissionCP gra�ins 0 jei neb�ra k� rodyt...
    if(!ShowPlayerTrashMissionCP(playerid, TrashMission[ playerid ], CurrentTrashCp[ playerid ])
        || TrashBagsInTrashVehicle[ vehicleid ] >= GetTrashMissionCPCount(TrashMission[ playerid ]))
    {
        Data_SetPlayerCheckPointEx(playerid, CHECKPOINT_TRASH_DROPOFF, "job_trash_dropoff", 5.0);
        SendClientMessage(playerid, COLOR_NEWS, "J�s� sunkve�imis pilnas. Ve�kite �iuk�les � �iuk�lyn�.");
    }
    else 
    {
        SendClientMessage(playerid, COLOR_NEWS, "Va�iuokite prie sekan�io konteinerio.");
    }

    return 1;
}*/
/*
stock ShowMissionTrashObjects(playerid,missionid)
{
    for(new i = 0; i < sizeof GarbageInfo; i++)
    {
        if(missionid != GarbageInfo[ i ][ gMission ] || GarbageInfo[ i ][ gMission ] == TRASH_MISSION_NONE) continue;

        new Players[ MAX_PLAYERS ], count = 0,Float:x,Float:y,Float:z,Float:rx,Float:ry,Float:rz;
        foreach(Player,j)
        {
            if(j == playerid) continue;
            if(Streamer_IsItemVisible(j, STREAMER_TYPE_OBJECT, GarbageInfo[ i ][ gObjectId ]))
                Players[ count++ ] = j;
        }
        Players[ count++ ] = playerid;

        GetDynamicObjectPos(GarbageInfo[ i ][ gObjectId ],x, y, z);
        GetDynamicObjectRot(GarbageInfo[ i ][ gObjectId ], rx, ry, rz);
        DestroyDynamicObject(GarbageInfo[ i ][ gObjectId ]);
        GarbageInfo[ i ][ gObjectId ] = CreateDynamicObjectEx(GarbageInfo[ i ][ gModel ], x, y, z, rx, ry, rz, .players = Players, .maxplayers = count);
    }
}

stock GetTrashMissionCPCount(missionid)
{
    new count = 0;
    for(new i = 0; i < sizeof(GarbageInfo); i++)
    {
        if(GarbageInfo[ i ][ gMission ] == TRASH_MISSION_NONE ) continue;
        if(GarbageInfo[ i ][ gMission ] == missionid)
            count++;
    }
    return count;
}

forward RemoveGarbageBagDelay(playerid);
public RemoveGarbageBagDelay(playerid)
    return RemovePlayerAttachedObject(playerid, TRASH_OBJECT_INDEX);

*/
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
    if ( idcar == INVALID_VEHICLE_ID ) return SendClientMessage( playerid, GRAD, "Tai kad aplink tave n�ra jokio automobilio...");
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

    if ( sscanf( params, "u", giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /giverec [veik�jo id]");
    if ( !PlayerToPlayer( 5.0, playerid, giveplayerid ) )  return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s nesate �alia veik�jo. ");
    if ( PlayerFaction( playerid ) == 2 )
    {
        if(IsPlayerInventoryFull(playerid))
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: Nepakanka vietos jo inventoriuje" );
       // GivePlayerItem(giveplayerid, ITEM_MEDLIC, 1); 
        GivePlayerBasicItem(giveplayerid, ITEM_MEDLIC, 1, 35, 0);
        format      (string, 126, "* %s i�ra�o vaist� recept� ir paduoda %s ", GetPlayerNameEx( playerid ), GetPlayerNameEx( giveplayerid ) );
        ProxDetector(20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
        return 1;
    }
    return 1;
}*/


CMD:tpda(playerid)
{
    if ( pInfo[ playerid ][ pJob ] != JOB_TRUCKER )
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti �ios komandos nedirbdami krovini� perve�im� vairuotoju.");
    if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, komanda galite naudoti tik sed�dami tr. priemon�s vairuotojo vietoje");
    if(!IsVehicleTrucker(GetVehicleModel(GetPlayerVehicleID(playerid))))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �i tr. priemon� negalima krovini� perve�imams.");

    ShowTPDA(playerid);
    return 1;
}


CMD:cargo(playerid, params[])
{
    if ( pInfo[ playerid ][ pJob ] != JOB_TRUCKER )
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti �ios komandos nedirbdami krovini� perve�im� vairuotoju.");
    
    if(isnull(params) || strfind(params," ") != -1)
    {
        cargo_help:
        SendClientMessage(playerid, COLOR_LIGHTRED, "__________________________Krovini� valdymas ir komandos__________________________");
        SendClientMessage(playerid,COLOR_LIGHTRED2, "  TEISINGAS KOMANDOS NAUDOJIMAS: /cargo [KOMANDA], pavyzd�iui: /cargo list");
        SendClientMessage(playerid,GRAD,"  PAGRINDIN�S KOMANDOS: list, place, fork, unfork, putdown, pickup, buy, sell");
        SendClientMessage(playerid,GRAD,"  KITOS KOMANDOS: /trailer - priekab� valdymas");		
        return 1;
    }
    // S�ra�as turimo krovinio
    if(!strcmp(params, "list",true))
    {
        if(IsPlayerInAnyVehicle(playerid))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, turite b�ti prie savo sunkve�imio nor�dami atlikti �� veiksm�.");

        new vehicleid = GetNearestVehicle( playerid, 5.0 );
        if(vehicleid == INVALID_VEHICLE_ID)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �alia J�s� n�ra jokios tr. priemon�s.");
        new model = GetVehicleModel(vehicleid);
        if(!IsVehicleTrucker(model) && !IsVehicleTrailer(model))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �i tr. priemon� yra tu��ia.");
        if(cInfo[ vehicleid ][ cLock ])
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �alia J�s� esanti tr. priemon� yra u�rakinta. Atrakinkite ir bandykite dar kart�.");
        
        ShowVehicleCargo(playerid, vehicleid);
        return 1;
    }
    if(!strcmp("buy",params,true))
    {
        new string[512];
        // Jei ne prie industrijos, n�ra k� pikrt.
        if(!IsPlayerInRangeOfAnyIndustry(playerid, 4.0))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nor�dami pirkti prekes turite b�ti/stov�ti �alia kompanijos, kuri parduoda juos.");

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

        /// Jei �aid�jas laiko d��� su prek�m
        if(cargoid)
        {
            if(!sellToBusines && !sellToIndustry && !sellToShip)
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, turite b�ti �alia kompanijos arba verslo arba �i kompanija/verslas neperka joki� preki�.");

            new price;
            if(sellToIndustry)
            {
                if(!Industries[ index ][ IsBuyingCargo ])
                    return SendClientMessage(playerid, GRAD, "Klaida, d�l produkt� pertekliaus �i industrija nedirba.");
                if(!HasIndustryRoomForCargo(index, cargoid))
                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, industrija �iai prekei nebeturi vietos.");
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
                    SendClientMessage(playerid, COLOR_LIGHTRED, "Verslas nebeturi pakankamai l���, kad nupirktu �i� prek�. Susisiekite su savininku.");
                    // reik surast savinink� ir pasakyk kad kapeikos baig�s :(
                    foreach(Player,i)
                        if(pInfo[ i ][ pMySQLID ] == bInfo[ index ][ bOwner ])
                        {
                            SendClientMessage(playerid, COLOR_LIGHTRED, "D�mesio, J�s� verslo banke pasibaig� pinigai, visi preki� pirkimai buvo at�aukti/nutraukti.");
                            StopBusinessBuyingCargo(index, cargoid);
                            break;
                        }
                }
                bInfo[ index ][ bBank ] -= price;
                AddCargoToBusiness(index, cargoid);
                
                // Jei verslas pasiek� limit�, jis daugiau pirkti nebegali.
                if(bInfo[ index ][ bProducts ] >= MAX_BUSINESS_PRODUCTS)
                    StopBusinessBuyingCargo(index, cargoid);
            }
            */
            else if(sellToShip)
            {
                if(ShipInfo[ Status ] != Docked)
                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, laivas �iuo metu yra i�plauk�s. Naudokite komanda /tpda daugiau informacijos.");
                
                price = GetShipCargoPrice(cargoid);
                ShipInfo[ CurrentStock ] += GetCargoSlot(cargoid);
            }
              
            GivePlayerMoney(playerid, price);
            DeletePVar(playerid, "CargoId");
            ApplyAnimation(playerid,"CARRY","putdwn", 4.0, 0, 1, 0, 0, 0 );
            RemovePlayerAttachedObject(playerid, 7);
            SetPlayerSpecialAction(playerid,SPECIAL_ACTION_NONE);
            format(string,sizeof(string)," J�s� ve�am� krovin�/prekes pavadinimu:  {97cd17}%s{FFFFFF}, s�kmingai nupirko u�{97cd17}%d${FFFFFF}",GetCargoName(cargoid), price);
            SendClientMessage(playerid, COLOR_WHITE, string);
        }
        // Jei �aid�jas nelaiko prek�s, bandom ie�kot transporto piremoni�.
        else 
        {
            if(!sellToBusines && !sellToIndustry && !sellToShip)
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s neesate prie kompanijos arba verslo arba jie neperka �ios prek�s.");
            
            new vehicleid = GetNearestVehicle(playerid, 5.0);
        
            
            if(IsTrailerAttachedToVehicle(vehicleid))
                vehicleid = GetVehicleTrailer(vehicleid);

            if(!CanPlayerUseTruckerVehicle(playerid,GetVehicleModel(vehicleid)))
                return SendClientMessage(playerid, COLOR_LIGHTRED, "D�mesio, neturite pakankamai patirties ta�k�, kad gal�tum�te ve�ti krovinius su �ia tr. priemone.");

            // Jei ma�ina yra �alia, ir tai f�rist� transporto priemon� ARBA priekaba.
            if(vehicleid != INVALID_VEHICLE_ID
                && (IsVehicleTrucker(GetVehicleModel(vehicleid)) || IsVehicleTrailer(GetVehicleModel(vehicleid))))
            {
                if(cInfo[ vehicleid ][ cLock ])
                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �i tr. priemon� yra u�rakinta.");
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
                    ShowPlayerDialog(playerid, 9999, DIALOG_STYLE_MSGBOX, "Preki� pardavimas","J�s� automobilyje n�ra preki� kurias perka �i kompanija.","Gerai","");
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
                    ShowPlayerDialog(playerid, DIALOG_COMMODITY_SELL, DIALOG_STYLE_LIST, "Preki� pardavimas",string, "Parduoti", "I�eiti");
                }
            }
            else 
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s neturite rankoje preki� arba �alia J�s� n�ra tr. priemon�s su prek�mis.");
        }   
        DeletePVar(playerid, "CargoId");
        return 1;
    }
    if(!strcmp(params, "fork", true))
    {
        if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER) 
            return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, privalot� sed�ti savo tr. priemon�je.");
        
        new vehicleid = GetPlayerVehicleID( playerid );
        if( GetVehicleModel( vehicleid ) != 530 )
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, komanda /fork galima tik s�dint specialioje tam skirtoje tr. priemon�je.");

        vehicleid = GetClosestVehicleToVehicle(vehicleid, 8.0);
        if(vehicleid == INVALID_VEHICLE_ID)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �alia J�s� n�ra jokios tr. priemon�s.");
        if(!CanPlayerUseTruckerVehicle(playerid,GetVehicleModel(GetPlayerVehicleID(playerid))))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "D�mesio, neturite pakankamai patirties ta�k�, kad gal�tum�te ve�ti krovinius su �ia tr. priemone.");
        if(cInfo[ vehicleid ][ cLock ])
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �alia esanti tr. priemon� yra u�rakinta.");
        ShowVehicleCargo( playerid, vehicleid);
        return 1;
    }
    if(!strcmp(params, "unfork", true))
    {
        new vehicleid = GetPlayerVehicleID( playerid );
        if( GetVehicleModel( vehicleid ) != 530 )
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, komanda /fork galima tik s�dint specialioje tam skirtoje tr. priemon�je.");
        vehicleid = GetClosestVehicleToVehicle(vehicleid, 8.0);
        if(vehicleid == INVALID_VEHICLE_ID)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �alia J�s� n�ra jokios tr. priemon�s.");
        if(cInfo[ vehicleid ][ cLock ])
            return SendClientMessage(playerid,  COLOR_LIGHTRED, "Klaida, �alia esanti tr. priemon� yra u�rakinta.");
        if(!CanPlayerUseTruckerVehicle(playerid,GetVehicleModel(vehicleid)))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "D�mesio, neturite pakankamai patirties ta�k�, kad gal�tum�te ve�ti krovinius su �ia tr. priemone.");
        new cargoid;
        for(new i = 0; i < sizeof VehicleCargo[]; i++)
        {
            if(!VehicleCargo[ vehicleid ][ i ][ Amount ])
                continue;
            cargoid = VehicleCargo[ vehicleid ][ i ][ CargoId ];
            break;
        }
        if(!cargoid)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s� transporto priemon�je n�ra jokio krovinio!");
        if(!HasVehicleSpaceForCargo(vehicleid, cargoid))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �ioje transporto primon�je neb�ra vietos!");
        
        RemoveCargoFromVehicle(GetPlayerVehicleID(playerid), cargoid);
        AddCargoToVehicle(vehicleid, cargoid);
        SendClientMessage(playerid, COLOR_WHITE, " Sveikiname,  {97cd17}s�kmingai {FFFFFF} pakrov�t� tr. priemon� prek�mis.");
        return 1;
    }
    if(!strcmp(params, "putdown", true))
    {
        new slotid = GetFreeBoxSlot( ),
            cargoid = GetPVarInt(playerid, "CargoId");
        if(slotid == -1)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite pad�ti �ios de��s, kadangi serveryje negali b�t� daugiau nei " #MAX_BOXES " d��i�");
 
        if(!cargoid)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite pad�ti d���s, kurios nelaikote rankoje.");

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
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite paiimti dar vienos d���s, kada rankose jau ka�k� laikote.");

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
                format(string,sizeof(string),"S�kmingai pak�l�t� d��� ant kurios etiket�s yra para�ytas prek�s pavadinimas: {97cd17} %s", GetCargoName( CargoBox[ i ][ CargoId ]));
                SendClientMessage(playerid, COLOR_WHITE, string);
                CargoBox[ i ][ CargoId ] = 0;
                return 1;
            }
        }
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, prie J�s� n�ra jokios d���s, kad gal�tum�te paiimti.");
        return 1;
    }
    if(!strcmp(params,"place", true))
    {
        new vehicleid = GetNearestVehicle(playerid, 5.0),
            cargoid = GetPVarInt(playerid, "CargoId");
        if(vehicleid == INVALID_VEHICLE_ID)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �alia J�s� turi b�ti tr. priemon�, kurioje pad�sit� krovin�");

        if(IsTrailerAttachedToVehicle(vehicleid))
            vehicleid = GetVehicleTrailer(vehicleid);
            
        if(!CanPlayerUseTruckerVehicle(playerid,GetVehicleModel(vehicleid)))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "D�mesio, neturite pakankamai patirties ta�k�, kad gal�tum�te ve�ti krovinius su �ia tr. priemone.");
        if(!cargoid)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s nelaikote d���s rankose, kad gal�tum�te �d�ti.");
        if(!IsCargoCompatibleWithVehicle(cargoid, GetVehicleModel(vehicleid)))
            return SendClientMessage(playerid, COLOR_LIGHTRED, " Klaida, �i tr. priemon� n�ra pritaikyta tokio tipo kroviniui.");
        if(!HasVehicleSpaceForCargo(vehicleid, cargoid))
            return SendClientMessage(playerid, COLOR_LIGHTRED, " D�mesio, �� tr. priemon� jau yra pakrauta ir pilna.");
        if(cInfo[ vehicleid ][ cLock ])
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �alia esanti tr. priemon� yra u�rakinta.");

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
            return SendClientMessage(playerid,GRAD, "�iuo metu n�ra ant �em�s pad�t� d��i�.");
        ShowPlayerDialog(playerid, 9999, DIALOG_STYLE_MSGBOX, "Deziu, padetu ant zemes sarasas.", string, "Gerai", "");
        return 1;
    }
    else 
        goto cargo_help;
    return 1;
}


stock GetIndustryCount()
{
    new count = 0;
    for(new i = 0; i < sizeof Industries; i++)
        if(Industries[ i ][ Id ])
            count++;
    return count;
}
stock GetIndustryCargoIndex(index,cargoid)
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
        SendClientMessage(playerid,COLOR_LIGHTRED2, "  KOMANDOS NAUDOJIMAS: /trailer [komanda], pavyzd�iui: /trailer cargo");
        SendClientMessage(playerid,GRAD,"  PAGRINDINES: lock, detach, lights, cargo sellto");
        return 1;
    }
    new vehicleid = GetPlayerVehicleID(playerid);
    new trailerid = GetVehicleTrailer(vehicleid),
        action[ 32 ];

    strmid(action, params, 0, strfind(params, " "));
    strdel(params, 0, strfind(params, " "));

    if(!IsValidVehicle(trailerid) || !IsVehicleTrailer(GetVehicleModel(trailerid)))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Prie j�s� transporto priemon�s n�ra prikabinta priekaba");

    if(!strcmp(action, "sellto", true))
    {
        if(pInfo[ playerid ][ pMySQLID ] != cInfo[ trailerid ][ cOwner ])
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s neesate �ios priekabos savininkas.");

        new target, price;
        if(sscanf(params, "ui", target, price))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Komandos naudojimas: /trailer sellto [�aid�jo ID/Dalis vardo] [ Kaina ]");
        if(!IsPlayerConnected(target))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, tokio �aid�jo n�ra.");
        if(!IsPlayerInRangeOfPlayer(playerid, target, 4.0)) 
            return SendClientMessage(playerid, COLOR_WHITE,"[ KLAIDA! ] �aid�jas per toli nuo j�s�. ");

        SellVehicleToPlayer(playerid, trailerid, target, price);
        return 1;
    }
    if(!strcmp(action,"lock",true))
    {
        if(CheckCarKeys(playerid, trailerid))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �i priekaba jums nepriklauso.");
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
    if ( Voted[ playerid ] == true ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: Balsavimas neprad�tas, arba jau balsavoje. " );
    SendClientMessage( playerid, COLOR_WHITE, "J�s� balsas s�kmingai �skai�iuotas. " );
    Votes[ 0 ] ++;
    Voted[ playerid ] = true;
    return 1;
}
CMD:ne( playerid, params[ ] )
{
    #pragma unused params
    if ( Voted[ playerid ] == true ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: Balsavimas neprad�tas, arba jau balsavoje. " );
    SendClientMessage( playerid, COLOR_WHITE, "J�s� balsas s�kmingai �skai�iuotas. " );
    Votes[ 1 ] ++;
    Voted[ playerid ] = true;
    return 1;
}

CMD:sellto( playerid, params[ ] )
{
            new
                pid,
                price,
                vehicleid = INVALID_VEHICLE_ID,
                string[ 128 ],
                IP[ 16 ],
                IP2[ 16 ];

            if(GetPlayerState(playerid) == PLAYER_STATE_DRIVER)
                vehicleid = GetPlayerVehicleID( playerid );
            else
                vehicleid = GetNearestVehicle( playerid, 5.0 );
            if ( vehicleid == INVALID_VEHICLE_ID ) return SendClientMessage( playerid, GRAD, "Tai kad aplink tave n�ra jokio automobilio...");

            if( sscanf(params, "ud", pid, price ) ) SendClientMessage(playerid, COLOR_WHITE, "Naudojimas: /sellto [veik�jo ID] [Kaina]");
            else if (pid == INVALID_PLAYER_ID) SendClientMessage(playerid, COLOR_WHITE, "[ KLAIDA ! ] veik�jo n�ra serveryje !");
            else if ( cInfo[vehicleid][cOwner] != pInfo[playerid][pMySQLID] ) SendClientMessage(playerid, COLOR_WHITE, "[ KLAIDA ! ] J�s nes�dite savo transporto priemon�je!");
            else if ( price < 0 || price > 9999999) SendClientMessage(playerid, COLOR_WHITE, "[ KLAIDA! ] Kaina turi b�ti tarp 0 ir 9999999");
            else if ( !PlayerToPlayer( 4, playerid, pid ) ) SendClientMessage( playerid, COLOR_WHITE,"[ KLAIDA! ] �aid�jas per toli nuo j�s�. ");
            else
            {
                GetPlayerIp( playerid, IP, 16 );
                GetPlayerIp( pid, IP2, 16 );

                if( !strcmp( IP, IP2, true ) || pInfo[ playerid ][ pUcpID ] == pInfo[ pid ][ pUcpID ] )
                    return true;
                    
                format(string,sizeof(string),"J�s siulote jam %s,kad jis nupirktu j�s� automobil� u�: $%d.",GetPlayerNameEx(pid),price);
                SendClientMessage(playerid,COLOR_WHITE,string);
                format(string,sizeof(string),"Automobilio savininkas %s si�lo jums nupirkti jo automobil� u�: $%d, jeigu sutinkate,ra�ykite /accept car.",GetPlayerNameEx(playerid),price);
                SendClientMessage(pid,COLOR_WHITE,string);
                Offer[pid][0] = playerid;
                OfferPrice[pid][0] = price;
                OfferID[ pid ][ 0 ] = vehicleid;
            }
            return true;
}

stock SellVehicleToPlayer(owner_playerid, vehicleid, buyer_playerid, price)
{
    new
        string[ 128 ],
        IP[ 16 ],
        IP2[ 16 ];

    GetPlayerIp( owner_playerid, IP, 16 );
    GetPlayerIp( buyer_playerid, IP2, 16 );

    if( !strcmp( IP, IP2, true ) || pInfo[ owner_playerid ][ pUcpID ] == pInfo[ buyer_playerid ][ pUcpID ] )
        return true;
        
    format(string,sizeof(string),"J�s siulote jam %s,kad jis nupirktu j�s� automobil� u�: $%d.",GetPlayerNameEx(buyer_playerid),price);
    SendClientMessage(owner_playerid,COLOR_WHITE,string);
    format(string,sizeof(string),"Automobilio savininkas %s si�lo jums nupirkti jo automobil� u�: $%d, jeigu sutinkate,ra�ykite /accept car.",GetPlayerNameEx(owner_playerid),price);
    SendClientMessage(buyer_playerid,COLOR_WHITE,string);
    Offer[buyer_playerid][0] = owner_playerid;
    OfferPrice[buyer_playerid][0] = price;
    OfferID[ buyer_playerid ][ 0 ] = vehicleid;
    return 1;
}

/*
CMD:vradio( playerid)
{
    if(!IsPlayerInAnyVehicle(playerid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s neesate transporto priemon�je.");
    if ( GetPlayerVehicleSeat( playerid ) == 0 || GetPlayerVehicleSeat( playerid ) == 1 )
    {
        new vehicle = GetPlayerVehicleID( playerid );

        if ( IsItemInTrunk( vehicle, ITEM_MAGNETOLA ) == MAX_TRUNK_SLOTS ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: Automobilio baga�in�je n�ra automagnetolos" );
        new string[ 126 ];
        format( string, 126, "- Radijo stotys\
                                \n- Garsumas \t[ %d ]\
                                \n- I�jungti", GetRadioVolume( playerid ) );
        ShowPlayerDialog( playerid, 70, DIALOG_STYLE_LIST,"Automagnetola", string, "Rinktis", "Atsaukti" );
        return 1;
    }
    else SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s s�dite ne automobilio priekyje." );
    return 1;
}
*/

/*
CMD:tazer( playerid, params[ ] )
{
    #pragma unused params
    if ( UsePDCMD( playerid ) == 1 || ( PlayerFaction( playerid ) == 5 && pInfo[ playerid ][ pRank ] > 4 ) )
    {
        if ( Mires[ playerid ] > 0 )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje." );

        if ( IsPlayerInAnyVehicle( playerid ) )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s automobilyje. ");
        if ( TazerAut == false )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: Tazeris �iuo metu u�draustas. " );

        new string[ 126 ],
            wepdata[2];
        if ( GetPVarInt( playerid, "TAZER_MODE" ) == 0 )
        {
            GetPlayerWeaponData( playerid, 2, wepdata[ 0 ] , wepdata[ 1 ]);
            SetPVarInt( playerid, "TAZER_GUN_SLOT_2", wepdata[ 0 ] );
            SetPVarInt( playerid, "TAZER_AMMO_SLOT_2", wepdata[ 1 ] );
            format      ( string, 126, "* %s i�traukia i� d�klo tazer�. ", GetPlayerNameEx( playerid ) );
            ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
            GivePlayerJobWeapon( playerid, 23, 2 );
            SetPVarInt( playerid, "TAZER_MODE", 1 );
            return 1;
        }
        else if ( GetPVarInt( playerid, "TAZER_MODE" ) == 1 )
        {
            format      ( string, 126, "* %s ideda � d�kla tazer�. ", GetPlayerNameEx( playerid ) );
            ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);

            GivePlayerJobWeapon( playerid, GetPVarInt( playerid, "TAZER_GUN_SLOT_2" ), GetPVarInt( playerid, "TAZER_AMMO_SLOT_2" ) );
            SetPVarInt( playerid, "TAZER_MODE", 0 );
            return 1;
        }
    }
    return 1;
}

CMD:auttazer( playerid, params[ ] )
{
    #pragma unused params
    if ( UsePDCMD( playerid )!= 1 )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente." );

    if ( Mires[ playerid ] > 0 )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje." );

    if ( pInfo[playerid][pLead] != 2 )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s negalite naudoti �ios komandos. " );

    new string[ 126 ];
    if ( TazerAut == true )
    {
        format( string, 126, " %s %s i�jung� savo tazer�. ", GetPlayerRangName( playerid ), GetName( playerid ) );
        SendTeamMessage( PlayerFaction( playerid ), COLOR_POLICE, string );
        TazerAut = false;
        return 1;
    }
    else
    {
        format( string, 126, " %s %s �jung� tazer�. ", GetPlayerRangName( playerid ), GetName( playerid ) );
        SendTeamMessage( PlayerFaction( playerid ), COLOR_NEWS, string );
        TazerAut = true;
        return 1;
    }
}*/
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
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s neesate i�sitrauk�s ginklo.");
    
    if(strfind(params, " ") != -1)
        strmid(action, params, 0, strfind(params, " "));
    else 
        strmid(action, params, 0, strlen(params));
    strdel(params, 0, strlen(action));

    if(!strcmp(action, "adjust", true))
    {
        if(GetWeaponSlotByID(weaponid) < 2 || GetWeaponSlotByID(weaponid) > 6)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �io ginklo pozicijos keisti negalite.");

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

        // Jei �ia pasiek�m, rei�kia n�ra PlayerAttachedWeapons to ginklo+
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s neesate u�sid�j� �io ginklo.");
    }
    else if(!strcmp(action, "show", true))
    {
        if(GetWeaponSlotByID(weaponid) < 2 || GetWeaponSlotByID(weaponid) > 6)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �io ginklo u�sid�ti negalite.");

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
            SendClientMessage(playerid, COLOR_NEWS, "S�kmingai u�sid�jote ginkl�. Jo pozicij� galite keisti su /weapon adjust");
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
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �is ginklas nebuvo rodomas. Ne�vykdyti jokie poky�iai.");
    }
    else 
        goto weapon_help;
    return 1;
}*/


CMD:savings( playerid, params[ ] )
{
    if ( !PlayerToPoint( 20.0, playerid, 295.7723,1021.7993,2123.6130 ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nor�dami naudotis komanda privalote b�ti banke." );
    if ( pInfo[ playerid ][ pSavings ] > 0 )
    {
        GivePlayerMoney( playerid, pInfo[ playerid ][ pSavings ] );
        pInfo[ playerid ][ pSavings ] = 0;
        SendClientMessage( playerid, COLOR_LIGHTRED, "D�mesio, J�s� ind�lis � bank� jau buvo pad�tas. Gr��iname pinigus atgal." );
        return 1;
    }
    new indelis;
    if ( sscanf( params, "d", indelis ) )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /savings [IND�LIO SUMA]" );
    if ( indelis < 5000 || indelis > 25000)
        return SendClientMessage( playerid, COLOR_LIGHTRED, "D�mesio, ind�lis turi b�ti ne ma�esnis u� 5000$, bei nevir�yti nurodyto ribos - 25000$.");
    if ( indelis > GetPlayerMoney(playerid) )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, neturite tiek pinig� kiek nurod�te." );
    GivePlayerMoney( playerid, -indelis );
    pInfo[ playerid ][ pSavings ] = indelis;

    new string[ 123 ];
    format( string, 123, "[Los Santos Bank] Sveikiname, J�s� ind�lis buvo s�kmingai pad�tas � bank�. Dabartinis ind�lis: %d$", indelis );
    SendClientMessage( playerid, COLOR_NEWS, string );
    return 1;
}
/*
CMD:police( playerid, params[ ] )
{
    #pragma unused params
    if ( GetPlayerVehicleSeat( playerid ) == 0 || GetPlayerVehicleSeat( playerid ) == 1 )
    {
        new vehicleid = GetPlayerVehicleID( playerid ),
            string[ 123 ];
        if ( VehicleHasWindows( GetVehicleModel( vehicleid ) ) && Windows[ vehicleid ] == false )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: Automobilio langas u�darytas. " );
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
                format      ( string, 126, "* %s atsidar�s lang� u�deda policijos persp�j�m�j� �vytur�li ant stogo ir �jungi� j�. ", GetPlayerNameEx( playerid ) );
                ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
                return 1;
            }
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: Tai ne policijos automobilis. " );
        }
        else if ( Police[ vehicleid ] > 0 )
        {
            DestroyDynamicObject( Police[ vehicleid ] );
            Police[ vehicleid ] = 0;
            format      ( string, 126, "* %s i�ki�a rank� ir nuiima policijos persp�j�m�j� �vytur�l� nuo stogo. ", GetPlayerNameEx( playerid ) );
            ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
            return 1;
        }
    }
    return 1;

}
*/
/*
CMD:sellcar( playerid, params[ ] )
{
    #pragma unused params
    new string[ 128 ];
    if ( pInfo[ playerid ][ pJob ] != JOB_JACKER )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo neb�damas automobiliu vagimi. " );
    if ( pInfo[ playerid ][ pLeftTime ] > 0 )
    {
        format( string, sizeof(string), "Klaida, kit� automobil� gal�site priduoti tik u� %d minu�i�. Pra�ome palaukti." , pInfo[ playerid ][ pLeftTime ] / 60 );
        SendClientMessage( playerid, COLOR_LIGHTRED, string );
        return 1;
    }
    if(Data_IsPlayerInRangeOfCoords(playerid, 10.0, "jacker_buy_spot_1"))
    {
        if ( JackerBoughtVehicles[ 0 ][ VehicleModel ] > 0 && JackerBoughtVehicles[ 0 ][ AmountNeeded ] > 0 )
        {
            if ( IsPlayerInAnyVehicle( playerid ) )
            {
                new vehicle = GetPlayerVehicleID( playerid ),
                model = GetVehicleModel( vehicle );
                if ( sVehicles[ vehicle ][ Faction ] > 0 )
                    return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo, kadangi tr. priemon� priklauso frakcijai. " );
                if ( JackerBoughtVehicles[ 0 ][ VehicleModel ] == model )
                {
                    new money = 760;
                    format           ( string, sizeof(string), "SMS: Nustebinai mane, atgabenai �� %s, u� �� darbel� atsilyginsiu Tau %d$. Siunt�jas: Nenustatytas numeris", GetVehicleName( model ), money);
                    SendClientMessage( playerid, COLOR_LIGHTRED2, string);
                    GivePlayerMoney  ( playerid, money );
                    CJLog( pInfo[ playerid ][ pMySQLID ], cInfo[vehicle][cID], "Pardav� vogt� tr. priemon�." );
                    AddJobExp        ( playerid, 1 );
                    DestroyVehicle   ( vehicle );
                    pInfo[ playerid ][ pLeftTime ] = 1500;
                    cInfo[ vehicle ][ cVehID ] = 0;
                    new owner = GetCarOwner( vehicle );
                    if ( IsPlayerConnected( owner ) )
                    {
                        if ( pInfo[ owner ][ pCarGet ] > 0 )
                            pInfo[ owner ][ pCarGet ] --;
                    }
                    SaveCar    ( vehicle );
                    nullVehicle( vehicle );
                    JackerBoughtVehicles[ 0 ][ AmountNeeded ] --;
                    if ( JackerBoughtVehicles[ 0 ][ AmountNeeded ] == 0 )
                        UpdateJacker( 0, random( 2 ) );
                    return 1;
                }
                format           ( string, sizeof(string), "SMS: Ey, a� neu�sakiau i� Taves %s, manau tarp m�s� reikalai baigti, viso. Siunt�jas: Nenustatytas numeris", GetVehicleName( model ));
                SendClientMessage( playerid, COLOR_LIGHTRED2, string);
                return 1;
            }
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nor�damas atlikti �� veiksm� privalai sed�ti tr. priemon�je. " );
        }
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, jeigu i�kilo problem� prane�kite b�tinai apie tai diskusij� forume." );
    }
    else if(Data_IsPlayerInRangeOfCoords(playerid, 10.0, "jacker_buy_spot_2"))
    {
        if ( JackerBoughtVehicles[ 1 ][ VehicleModel ] > 0 && JackerBoughtVehicles[ 1 ][ AmountNeeded ] > 0 )
        {
            if ( IsPlayerInAnyVehicle( playerid ) )
            {
                new vehicle = GetPlayerVehicleID( playerid ),
                    model = GetVehicleModel( vehicle );
                if ( sVehicles[ vehicle ][ Faction ] > 0 )
                    return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo, kadangi tr. priemon� priklauso frakcijai. " );
                if ( JackerBoughtVehicles[ 1 ][ VehicleModel ] == model )
                {   
                    new money = 980;
                    format           ( string, sizeof(string), "SMS: Tau s�kmingai pavyko atgabenti %s, u� �� darbel� atsilyginsiu Tau %d$. Siunt�jas: Nenustatytas numeris", GetVehicleName( model ), money);
                    SendClientMessage( playerid, COLOR_LIGHTRED2, string);
                    GivePlayerMoney  ( playerid, money );
                    CJLog( pInfo[ playerid ][ pMySQLID ], cInfo[vehicle][cID], "Pardav� vogt� tr. priemon�." );
                    AddJobExp        ( playerid, 1 );
                    DestroyVehicle   ( vehicle );
                    pInfo[ playerid ][ pLeftTime ] = 1900;
                    cInfo[ vehicle ][ cVehID ] = 0;
                    new owner = GetCarOwner( vehicle );
                    if ( IsPlayerConnected( owner ) )
                    {
                        if ( pInfo[ owner ][ pCarGet ] > 0 )
                            pInfo[ owner ][ pCarGet ] --;
                    }
                    SaveCar    ( vehicle );
                    nullVehicle( vehicle );
                    JackerBoughtVehicles[ 1 ][ AmountNeeded ] --;
                    if ( JackerBoughtVehicles[ 1 ][ AmountNeeded ] == 0 )
                        UpdateJacker( 1, random( 2 ) );
                    return 1;
                }
                format           ( string, sizeof(string), "SMS: Ey, a� neu�sakiau i� Taves %s, manau tarp m�s� reikalai baigti, viso. Siunt�jas: Nenustatytas numeris", GetVehicleName( model ));
                SendClientMessage( playerid, COLOR_LIGHTRED2, string);
                return 1;
            }
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nor�damas atlikti �� veiksm� privalai sed�ti tr. priemon�je. " );
        }
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, jeigu i�kilo problem� prane�kite b�tinai apie tai diskusij� forume." );
    }
    else if(Data_IsPlayerInRangeOfCoords(playerid, 10.0, "jacker_buy_spot_3"))
    {
        if ( JackerBoughtVehicles[ 2 ][ VehicleModel ] > 0 && JackerBoughtVehicles[ 2 ][ AmountNeeded ] > 0 )
        {
            if ( IsPlayerInAnyVehicle( playerid ) )
            {
                new vehicle = GetPlayerVehicleID( playerid ),
                    model = GetVehicleModel( vehicle );
                if ( sVehicles[ vehicle ][ Faction ] > 0 )
                    return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo, kadangi tr. priemon� priklauso frakcijai.. " );
                if ( JackerBoughtVehicles[ 2 ][ VehicleModel ] == model )
                {
                    new money = 640;
                    format           ( string, sizeof(string), "SMS: Laukiau Taves, maniau nepasirodysi. Gra�i %s, u� �� darbel� atsilyginsiu Tau %d$. Siunt�jas: Nenustatytas numeris", GetVehicleName( model ), money);
                    SendClientMessage( playerid, COLOR_LIGHTRED2, string);
                    GivePlayerMoney  ( playerid, money );
                    CJLog( pInfo[ playerid ][ pMySQLID ], cInfo[vehicle][cID], "Pardav� vogt� tr. priemon�." );
                    AddJobExp        ( playerid, 1 );
                    DestroyVehicle   ( vehicle );
                    pInfo[ playerid ][ pLeftTime ] = 1200;
                    cInfo[ vehicle ][ cVehID ] = 0;
                    new owner = GetCarOwner( vehicle );
                    if ( IsPlayerConnected( owner ) )
                    {
                        if ( pInfo[ owner ][ pCarGet ] > 0 )
                            pInfo[ owner ][ pCarGet ] --;
                    }
                    SaveCar    ( vehicle );
                    nullVehicle( vehicle );
                    JackerBoughtVehicles[ 2 ][ AmountNeeded ] --;
                    if ( JackerBoughtVehicles[ 2 ][ AmountNeeded ] == 0 )
                        UpdateJacker( 2, random( 2 ) );
                    return 1;
                }
                format           ( string, sizeof(string), "SMS: Ey, a� neu�sakiau i� Taves %s, manau tarp m�s� reikalai baigti, viso. Siunt�jas: Nenustatytas numeris", GetVehicleName( model ) );
                SendClientMessage( playerid, COLOR_LIGHTRED2, string);
                return 1;
            }
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nor�damas atlikti �� veiksm� privalai sed�ti tr. priemon�je. " );
        }
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, jeigu i�kilo problem� prane�kite b�tinai apie tai diskusij� forume." );
    }
    return 1;
}
*/
/*
CMD:info(playerid)
{
    if(pInfo[ playerid ][ pJob ] != JOB_JACKER)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, tik dirbdamas automobili� vagies darb� galite naudotis �iuo veiksmu. ");
    new string[ 160 ];
    if(Data_IsPlayerInRangeOfCoords(playerid, 10.0, "jacker_info_1"))
        format(string, sizeof(string), "SMS: Gird�jau ie�kai darbelio, o a� ie�kausi %s, pasistengsiu gerai atsilyginti jei tik tinka ir nebijai rizikuoti -Ne�inomas siunt�jas.", GetVehicleName(JackerBoughtVehicles[ 0 ][ VehicleModel ]));
    else if(Data_IsPlayerInRangeOfCoords(playerid, 10.0, "jacker_info_2"))
        format(string, sizeof(string), "SMS: Skubiai ie�kausi %s, visados moku daugiausia u� kitus, tad manau nenuvilsi man�s. Siunt�jas: Nenustatytas numeris", GetVehicleName( JackerBoughtVehicles[ 1 ][ VehicleModel ] ));
    else if(Data_IsPlayerInRangeOfCoords(playerid, 10.0, "jacker_info_3"))
        format(string, sizeof(string), "SMS: Turiu klausim�, apsiimsi %s nuvarym�? Pasir�pinsiu, kad rizika b�t� apmok�ta Siunt�jas: Nenustatytas numeris", GetVehicleName( JackerBoughtVehicles[ 2 ][ VehicleModel ] ));
    else
        format(string, sizeof(string), "SMS: Kod�l vis dar negaunu �ini�? Atsisakai darbo? Nelabai patinka man tokie �mon�s -Ne�inomas siunt�jas");
    SendClientMessage(playerid, COLOR_LIGHTRED2, string);
    return 1;
}
CMD:spots( playerid, params[ ] )
{
    #pragma unused params
    if ( pInfo[ playerid ][ pJob ] != JOB_JACKER )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, tik dirbdamas automobili� vagies darb� galite naudotis �iuo veiksmu. " );
    new zone1[ MAX_ZONE_NAME ],
        zone2[ MAX_ZONE_NAME ],
        zone3[ MAX_ZONE_NAME ],
        string[ 128 ];

    Get2DZone( 868.8514,-30.3725, zone1, 28 );
    Get2DZone( 2827.3010,896.9294, zone2, 28 );
    Get2DZone( 2207.4143,-2296.2839, zone3, 28 );

    format( string, sizeof(string), "SMS: Tiesiog atve�k automobilius � �iuos gara�us %s, %s, %s, ir baigiam reikalus. -Ne�inomas siunt�jas", zone1, zone2, zone3 );
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
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /sup [ veik�jo id ] [ 1-3 ]");

    if( GetPlayerState( playerid ) != PLAYER_STATE_ONFOOT ) return 1;
    if ( !PlayerToPlayer( 1.0, playerid, giveplayerid ) )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: nor�dami naudoti �i� komand� turite b�ti �alia veik�jo. " );
    if ( type < 0 || type > 4 )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /sup [ veik�jo id ] [ 1 - 3 ]");

    SendClientMessage( playerid, COLOR_WHITE, "J�s pasi�l�te pasisveikinim� �aid�jui, laukite jo patvirtinimo. " );
    format( string, sizeof(string), "PASI�LIMAS: �aid�jas %s si�lo jums pasisvekinima, jeigu sutinkate ra�ykite: /accept sup %d)",GetPlayerNameEx( playerid ), playerid );
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
stock MoveCamera( playerid, Float:degres, Float:radius, camera )
{
    static Float:WachX[ MAX_PLAYERS ],
           Float:WachY[ MAX_PLAYERS ];
    WachX[ playerid ] = CCTV[ camera ][ 0 ] + ( floatmul(radius, floatsin(-degres, degrees)));
    WachY[ playerid ] = CCTV[ camera ][ 1 ] + ( floatmul(radius, floatcos(-degres, degrees)));
    SetPlayerCameraLookAt(playerid, WachX[ playerid ], WachY[ playerid ], CCTV[ camera ][ 3 ]);
    return 1;
}
/*
CMD:license(playerid)
{
    if(!Data_IsPlayerInRangeOfCoords(playerid, 5.0, "license_center"))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s ne licenzijavimo center.");

    ShowPlayerDialog( playerid, 95, DIALOG_STYLE_MSGBOX, "Licenzijos teorijos egzaminas.",
                                                           "{FFFFFF}Mes leid�iame laikyti teorijos egzamin� �iems dalykams\n\
                                                            \t- Automobilio\n\
                                                            \t- Motociklo\n\
                                                            \t- Laivybos\n\
                                                            \t- Pilotavimo\n\
                                                            Spauskite prad�ti, kad prad�tum�te test�.", "Prad�ti", "At�aukti");
    return 1;
}
*/
/*
CMD:maxspeed( playerid, params[ ] )
{
    if ( !IsPlayerInAnyVehicle( playerid ) )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nor�dami atlikti �� veiksm� privalote b�ti tr. priemon�je. " );
    new speed,
        string[ 126 ];

    if ( sscanf( params, "d", speed ) )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /maxspeed [ 30 - 130 ] " );
    if ( speed < 30 )
    {
        PlayerSpeed[ playerid ] = 0;
        format( string, 126, " * Grei�io ribotuvas i�jungtas. " );
    }
    else if ( speed > 29 && speed < 130 )
    {
        PlayerSpeed[ playerid ] = speed;
        format( string, 126, " * Grei�io ribotuvas buvo nustatytas: %d Km/h ", speed );
    }
    else if ( speed >= 130 )
    {
        PlayerSpeed[ playerid ] = 0;
        format( string, 126, " * Grei�io ribotuvas i�jungtas. " );
    }
    SendClientMessage( playerid, COLOR_WHITE, string );
    return 1;
}
*/
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
/*
CMD:licwarn( playerid, params[ ] )
{
    new string[ 126 ],
        giveplayerid;
    if ( UsePDCMD( playerid ) == 0 )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente." );
    if ( sscanf( params, "u", giveplayerid ) )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /licwarn [ veik�jo ID/Vardas ] " );
    if ( giveplayerid == INVALID_PLAYER_ID )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje." );
    if ( !PlayerToPlayer( 5.0, playerid, giveplayerid ) )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s nesate �alia veik�jo. " );
    if ( pInfo[ giveplayerid ][ pDriverWarn ] >= 3 )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: Vairuotojas jau turi 3 �sp�jimus, naudokite /take . " );

    format           ( string, 126, "[LSPD] Pareig�nas i�ra�� Jums �sp�jim� licencijai d�l %s, d�l vairavimo. ", GetPlayerNameEx(playerid) );
    SendClientMessage( giveplayerid , COLOR_POLICE, string );

    format           ( string, 126, "[LSPD] S�kmingai persp�jote asmen� %s, d�l nustatytos prie�asties. ", GetPlayerNameEx(giveplayerid) );
    SendClientMessage( playerid, COLOR_POLICE, string );

    pInfo[ giveplayerid ][ pDriverWarn ] ++;
    return 1;
}
*/
/*
CMD:bell( playerid, params[ ] )
{
    #pragma unused params
    if ( !PDJOBPlace(playerid) )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s nesate policijos departamente. " );
    if ( GetPVarInt( playerid, "PD_BELL" ) == 1 )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: /bell komanda galima naudoti tik kas 30 sekund�i�. " );
    new string[ 126 ];
    format         ( string, 126, "[LSPD D�R� SKAMBUTIS] Asmuo %s paskambino � d�r� skambut� ir laukia J�s�.", GetPlayerNameEx(playerid) );
    SendTeamMessage( 1, COLOR_POLICE, string );
    format      ( string, 126, "* %s paskambina varpeliu." ,GetPlayerNameEx( playerid ) );
    ProxDetector( 20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
    SetPVarInt( playerid, "PD_BELL", 1 );
    SetTimerEx( "PD_BELL", 30000, false, "d", playerid );
    return 1;
}
*/
FUNKCIJA:PD_BELL( playerid )
    return DeletePVar( playerid, "PD_BELL" );

CMD:blockpm( playerid, params[ ] )
{
    if(pInfo[ playerid ][ pDonator ] < 2  && !GetPlayerAdminLevel(playerid))
        return 0;

    new string[ 126 ],
        giveplayerid;

    if(sscanf( params, "u", giveplayerid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /blockpm [ veik�jo id/vardo dalis ]");

    if(!IsPlayerConnected(giveplayerid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /blockpm [ veik�jo id/vardo dalis ]");


    if(PlayersBlocked[ playerid ][ giveplayerid ])
    {
        format(string, sizeof(string), "�aid�jas %s buvo atblokuotas, dabar gausite i� jo �inutes. ", GetName(giveplayerid));
        SendClientMessage(playerid, COLOR_WHITE, string);
    }
    else
    {
        format(string, sizeof(string), "�aid�jas %s buvo u�blokuotas, dabar nebegausite i� jo priva�i� �inu�i�.", GetName(giveplayerid));
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
    if ( !IsPlayerInAnyVehicle( playerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nor�dami atlikti �� veiksm� privalote b�ti tr. priemon�je. " );
    new vehicle = GetPlayerVehicleID( playerid ),
        model = GetVehicleModel( vehicle );
    if ( GetPlayerState( playerid ) != PLAYER_STATE_DRIVER ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s nesate �io automobilio vairuotojas. " );

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
    return SendClientMessage( playerid, GRAD, "Apgailestaujame, �i ma�ina neturi pakeliamo stogo. " );
}

CMD:nofuel(playerid)
{
    cInfo [ GetPlayerVehicleID(playerid) ] [ cFuel ] = 0;
    return 1;
}
CMD:fill(playerid)
{
    if(!IsAtGasStation(playerid))
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s nesate degalin�je. " );
    if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, kur� gali pilti tik vairuotojas");
    if(IsFillingFuel[ playerid ])
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s jau pilat�s kur�.");
    new veh = GetPlayerVehicleID(playerid);
    if(Engine[ veh ] == true ) 
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: I�junkite varikl�" );

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
stock StopFillUp(playerid)
{
    new string[ 126 ];
    format          ( string, sizeof(string), "DEGALIN�\nMokestis u� degalus: %d\nKuo atsikaitysite? spustelkite migtuka", GetPVarInt( playerid, "MOKESTIS" ) );
    ShowPlayerDialog( playerid ,5, DIALOG_STYLE_MSGBOX, "DEGALIN�", string, "Grynais", "Banku" );
    KillTimer(PlayerFillUpTimer[ playerid ]);
    IsFillingFuel[ playerid ] = false;
    return 1;
}
/*
CMD:checkspeed( playerid, params[ ] )
{
    new
        string[ 64 ];
    if(PlayerFaction( playerid ) != 1) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente.");

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
            format(string, sizeof(string), "[LSPD] Prava�iuojan�ios tr. priemon�s greitis yra: %dkm/h (( %s ))", GetVehicleSpeed2( car ), GetVehicleName( GetVehicleModel( car ) ) );
            SendClientMessage(playerid, COLOR_POLICE, string );
            count++;
        }
    }
    if(!count)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalimas veiksmas, kadangi aplink Jus n�ra prava�iuojan�i� tr. priemoni�");
    return true;
}
*/
/*
CMD:mdc( playerid, params[] )
{
    new
        string[ 64 ];
    if(PlayerFaction( playerid ) != 1) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente.");
    new idcar = GetPlayerVehicleID( playerid );
    if( PDJOBPlace(playerid) || ( IsPlayerInAnyVehicle( playerid ) && sVehicles[ idcar ][ Faction ] == 2 ) )
    {
        format( string, 64, "Policijos duomen� baz� - Prisijungta: %s", GetName( playerid ) );
        ShowPlayerDialog(playerid, 128, DIALOG_STYLE_LIST,string,
        "Surasti asmen�\n\
        Ie�koti tr. priemon�s ((Numeris))\n\
        Paie�kom� s�ra�as\n\
        Kal�jimo duomen� baz�\n\
        Prid�ti prie paie�kom� asmen��\n\
        Paskelbti tr. priemon� paie�kom�\n\
        Paie�kom� tr. priemoni� s�ra�as\n\
        Are�tuotu tr. priemoni� s�ra�as\n\
        I�kvietim� registras", "Pasirinkti", "At�aukti" );
    }
    else
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nor�dami atlikti �� veiksm� privalote sed�ti policijos tr. priemon�je arba b�dami nuovadoje.");
    return 1;
}
*/
/*
CMD:bail( playerid, params[ ] )
{
    if(!Data_IsPlayerInRangeOfCoords(playerid, 30.0, "prison_bail_spot"))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, privalote b�ti �alia kal�jimo priemamojo langelio.");
    if( GetPVarInt(playerid, "BailTime") < 1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jums n�ra u� k� mok�ti.");
        
    new string[ 256 ],
        rows,
        Cache:result;

    format( string, 256, "SELECT * FROM `tickets` WHERE `name` = '%s' AND `paid` = 0", GetPlayerNameEx(playerid) );
    //result = mysql_query(DbHandle,  string );
    rows = cache_get_row_count();

    cache_delete(result);

    if( rows )
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti i�pirkos tur�dami nesumok�t� baud�.");

    if ( GetPlayerBankMoney(playerid) < GetPVarInt(playerid, "Bail") )
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s� banko s�skaitoje n�ra pakankamai pinig� i�pirkai..");

    AddPlayerBankMoney(playerid,  - GetPVarInt(playerid, "Bail"));
    pInfo[playerid][pJailTime] = GetPVarInt(playerid, "BailTime");
    SaveAccount( playerid );
    ShowPlayerInfoText( playerid );
    DeletePVar(playerid, "Bail");
    DeletePVar(playerid, "BailTime");
    return 1;
}
*/
/*
CMD:prison( playerid, params[ ] )
{
    if(!Data_IsPlayerInRangeOfCoords(playerid, 30.0, "ic_prison"))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nor�dami atlikti �� veiksm� privalote b�ti �alia kal�jimo.");
    if(UsePDCMD(playerid) != 1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente.");
    new giveplayerid,
        string[ 126 ],
        time,
        bill,
        bail,
        bailtime;

    if ( sscanf( params, "rdddd", giveplayerid, time, bill, bail, bailtime ) )
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /prison [VEIK�JO ID][LAIKAS MINUT�MIS][BAUDA][I�PIRKA][LAIKAS MINUT�MIS PO I�PIRKOS]");
    if(IsPlayerNPC(giveplayerid)) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite u�daryti �io veik�jo, kadangi tai serverio dirbtinis �aid�jas (BOT)");
    if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
    if(pInfo[giveplayerid][pJail] > 0)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jas jau yra kal�jime");
    if(!Data_IsPlayerInRangeOfCoords(giveplayerid, 30.0, "ic_prison"))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s nesate prie kal�jimo, tad negalite atlikti �io veiksmo.");
    if( time <= 60)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nustatant kal�jimo laika, minimalus laikas yra 60.");
    if ( bill < 1000 || bill > 1000000)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, bauda privalo b�ti didesn� nei 1000$, bet nevir�yti 1000000$");
    if ( bail < 1000 || bail > 1000000)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, i�pirkos suma privalo b�ti didesn� nei 1000$, bet nevir�yti 1000000$");
    if( bailtime <= 1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nustatytas laikas po i�pirkos privalo b�ti didesnis nei 1.");
        
    format(string, 126, "[LSPD] Pareig�nas %s pasodino � kal�jim� asmen� %s, %d minut�ms.", GetPlayerNameEx(playerid), GetPlayerNameEx(giveplayerid), time);
    SendChatMessageToAll(COLOR_LIGHTRED, string);
    format(string, 126, "[LSPD] J�s buvote u�darytas � kal�jim� %d minut�ms, bei tur�site susimok�ti pareig�no nustaty� baud�: %d$",time,bill);
    SendClientMessage(giveplayerid,COLOR_LIGHTRED, string);
    SetPVarInt(giveplayerid, "Bail", bail);
    SetPVarInt(giveplayerid, "BailTime", bailtime*60);
    if ( GetPlayerMoney(giveplayerid) > bill )
        GivePlayerMoney( giveplayerid, -bill );
    else
    {
        if ( GetPlayerBankMoney(giveplayerid) > bill )
             AddPlayerBankMoney(playerid,  - bill);
    }
    pInfo[giveplayerid][pJailTime] = time*60;
    pInfo[giveplayerid][pJail] = 2;
    pInfo[giveplayerid][pWantedLevel] = 0;
    Data_SetPlayerLocation(giveplayerid, "ic_prison");
    ResetPlayerWeapons(giveplayerid);
    SetPlayerSkin   ( giveplayerid, 8 );
    SaveAccount( giveplayerid );
    ShowPlayerInfoText( giveplayerid );
    return 1;
}
*/
/*
CMD:arrest( playerid, params[ ] )
{
    if(!Data_IsPlayerInRangeOfCoords(playerid, 20.0, "ic_custody"))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: J�s nesate prie kal�jimo.");
    if(UsePDCMD(playerid) != 1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente.");
    new giveplayerid,
        string[ 126 ],
        time,
        bill;

    if ( sscanf( params, "rdd", giveplayerid, time, bill ) )
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /arrest [VEIK�JO ID][MINUT�S][BAUDA]");
    if(IsPlayerNPC(giveplayerid)) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite u�daryti �io veik�jo, kadangi tai serverio dirbtinis �aid�jas (BOT)");
    if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
    if(pInfo[giveplayerid][pJail] > 0)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jas jau pasodintas � are�tin�");
    if(!Data_IsPlayerInRangeOfCoords(giveplayerid, 40.0, "ic_custody"))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, veik�jas n�ra �alia kal�jimo.");
    if( time < 1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodant are�tavimo laik�, privaloma nurodyti daugiau nei 1 minut�.");
    if ( bill < 1 || bill > 20000)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, bauda negali b�ti didesn� nei 20000$");

    format(string, 126, "[LSPD] Pareig�nas %s pasodino � are�tin� asmen�  %s, %d minut�ms.", GetPlayerNameEx(playerid), GetPlayerNameEx(giveplayerid), time);
    SendChatMessageToAll(COLOR_POLICE, string);
    format(string, 126, "[LSPD] J�s buvote u�darytas � kal�jim� %d minut�ms, bei tur�site susimok�ti pareig�no nustaty� baud�: %d$",time,bill);
    SendClientMessage(giveplayerid,COLOR_POLICE, string);
    if ( GetPlayerMoney(giveplayerid) > bill )
        GivePlayerMoney( giveplayerid, -bill );
    else
    {
        if ( GetPlayerBankMoney(giveplayerid) > bill )
             AddPlayerBankMoney(playerid,  - bill);
    }
    pInfo[giveplayerid][pJailTime] = time*60;
    pInfo[giveplayerid][pJail] = 3;
    pInfo[giveplayerid][pWantedLevel] = 0;
    Data_SetPlayerLocation(giveplayerid, "ic_custody");
    ResetPlayerWeapons(giveplayerid);
    SaveAccount( giveplayerid );
    ShowPlayerInfoText( giveplayerid );
    SetPVarInt(giveplayerid, "Drag", false);
    TogglePlayerControllable(giveplayerid, 1);
    return 1;
}
*/
/*
CMD:wepstore( playerid, params[ ] )
{
    #pragma unused params
    if(UsePDCMD(playerid) != 1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente.");

    if(!PDJOBPlace(playerid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite persirengti neb�damas persirengimo kabinoje/kambaryje.");

    ShowPlayerDialog(playerid,102,DIALOG_STYLE_LIST, "Policijos Departamento Ginklin�", "Desert Eagle - 100 kulk�\nFotoparatas - 20 nuotrauk�\nPolicininko lazda\nA�arin�s dujos - 150", "Rinktis", "At�aukti" );
    return 1;
}
*/
CMD:gov( playerid, params[ ] )
{
    new string[ 256 ];
    //if(UsePDCMD(playerid) != 1)
        //return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente.");
    if(pInfo[playerid][pLead] != 2 && pInfo[playerid][pLead] != 6 && pInfo[playerid][pLead] != 3 ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, komand� gali naudoti tik Los Santos miesto meras arba policijos darbuotojas.");
    if ( sscanf( params, "s[256]", string ) )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /gov [TEKSTAS] " );
    SendChatMessageToAll( COLOR_LIGHTRED2, "|___________ LOS SANTOS___________ |"),
    SendChatMessageToAll( COLOR_LIGHTRED2, "|_________ MIESTO VALD�IA__________|");	
    format( string, 256, "|PRANE�IMAS| %s ", string );
    SendChatMessageToAll( COLOR_WHITE, string );
    format( string, 256, "|PRANE��JAS| %s ", GetName(playerid) );
    SendChatMessageToAll( COLOR_FADE1, string );
    return 1;
}

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

CMD:makemoderator( playerid, params[ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 4 )
    {
        new string[ 100 ],
            giveplayerid,
            level;

        if ( sscanf( params, "rd", giveplayerid, level ) )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /makemoderator [VEIK�JO ID][MODERATORIAUS LYGIS] " );

        if(IsPlayerNPC(giveplayerid)) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite u�daryti �io veik�jo, kadangi tai serverio dirbtinis �aid�jas (BOT)");
        if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");

        pInfo[ giveplayerid ][ pTester ] = level;
        SaveAccount( giveplayerid );
        format( string, 100, "AdmWarn: Administratorius (%s) suteik� veik�jui (%s) moderatoriaus status�. ", GetName( playerid ), GetName( giveplayerid ) );
        SendAdminMessage( COLOR_ADM, string );
        SendClientMessage( giveplayerid, COLOR_MODERATOR, "[ModCmd] Sveikiname, jus buvote priimtas � moderatori� grup�. Informacija /modhelp ");
    }
    return 1;
}
/*
CMD:togooc( playerid, params[] )
{
    #pragma unused params
    if(TogChat[playerid][0] == true)
    {
        TogChat[playerid][0] = false;
        SendClientMessage(playerid,COLOR_LIGHTRED,"[TOGooc] Dabar nebematysite joki� OOC prane�im� savo pokalbi� kanale");
        return 1;
    }
    else if(TogChat[playerid][0] == false)
    {
        TogChat[playerid][0] = true;
        SendClientMessage(playerid,COLOR_LIGHTRED,"[TOGooc] OOC prane�im� rodymas buvo �jungtas ");
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
            SendClientMessage(playerid,COLOR_LIGHTRED,"[TOGpm] Dabar nebegausite priva�i� �inu�i�, gal�site tik si�sti kitiems.");
            return 1;
        }
        else if(TogChat[playerid][2] == false)
        {
            TogChat[playerid][2] = true;
            SendClientMessage(playerid,COLOR_LIGHTRED,"[TOGpm] Priva�i� �inu�i� gaviklis buvo �jungtas.");
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
        SendClientMessage(playerid,COLOR_LIGHTRED,"[TOGadmin] I�jung�te prane�imus apie Administratoriaus veiksmus serveryje.");
        return 1;
    }
    else if(TogChat[playerid][3] == false)
    {
        TogChat[playerid][3] = true;
        SendClientMessage(playerid,COLOR_LIGHTRED,"[TOGadmin] Administratori� veiksm� prane�imai �jungti.");
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
        SendClientMessage(playerid,COLOR_LIGHTRED,"[TOGnews] Prane�am� naujien� prane�imai buvo i�jungti.");
        return 1;
    }
    else if(TogChat[playerid][1] == false)
    {
        TogChat[playerid][1] = true;
        SendClientMessage(playerid,COLOR_LIGHTRED,"[TOGnews] Naujien� prane�imai �jungti.");
        return 1;
    }
    return 1;
}
*/
CMD:buysex( playerid, params[ ] )
{
    #pragma unused params
    if(PlayerToPoint(10.0,playerid,-103.9604,-22.6792,1000.7188))
        ShowPlayerDialog(playerid,7,DIALOG_STYLE_LIST,"SEX PREKI� MENU","\
            1.Ro�inis vibratorius \t$300\
            \n2.Ma�as baltas vibratorius \t$250\
            \n3.Didelis baltas vibratorius \t$330\
            \n4.Blizgantis vibratorius \t$260"
            ,"Pirkti","I�jungti");
    return 1;
}


CMD:payfines( playerid, params[ ] )
{
    #pragma unused params
    if ( !PlayerToPoint( 20.0, playerid, 295.7723,1021.7993,2123.6130 ) )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nor�dami susimok�ti baud� turite b�ti banke. " );
    if ( (pInfo[ playerid ][ pFines ]/2) > GetPlayerMoney(playerid) )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, Jums nepakanka pinig� sumos, kad susimok�tum�te baud�" );
    if ( pInfo[ playerid ][ pFines ] == 0 )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "J�s neturite jokios skolingos nuobaudos ar nusi�engimo.�" );
    GivePlayerMoney( playerid, -(pInfo[ playerid ][ pFines ]/2) );

    new string[ 128 ];
    format           ( string, sizeof(string), "=================================\n\
                                    \nNustatyta bauda: %d$\n\
                                    Sumok�ta: %d$\n\
                                    Mok�jimo procentas: 50proc.\n\
                                    \nSumok�ta baud�: %d$\n\
                                    ==================================\n",
                                    pInfo[ playerid ][ pFines ],
                                    (pInfo[ playerid ][ pFines ]/2),
                                    pInfo[ playerid ][ pPaydFines ] + (pInfo[ playerid ][ pFines ]/2) );
    ShowPlayerDialog( playerid, 9999, DIALOG_STYLE_MSGBOX, "APMOK�JIMO SUMA", string, "OK", "");

    pInfo[ playerid ][ pPaydFines ] += (pInfo[ playerid ][ pFines ]/2);

    pInfo[ playerid ][ pFines ] = 0;
    SaveAccount( playerid );
    return 1;
}
/*
CMD:fine( playerid, params[ ] )
{
    if ( PlayerFaction( playerid ) != 1)
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente.");
    new price,
        giveplayerid,
        string[ 256 ],
        reason[ 128 ];
    if ( sscanf( params, "uds[128]", giveplayerid, price, reason ) )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /fine [veik�jo id][bauda][prie�astis]" );
    if ( !IsPlayerConnected( giveplayerid ) )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: �aid�jas norimu ID neprisijung�s!");
    if ( !PlayerToPlayer( 10.0, playerid, giveplayerid ) )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti �io veiksmo su veik�ju, kuris n�ra �alia J�s�.");
    if ( price < 1 || price > 5000 )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: Baudos suma negali b�ti didesn� negu $5000 arba ma�esn� negu $1.");
    if(GetPVarInt(giveplayerid, "FineOfferMemory"))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �is �aid�jas dar nepatvirtino/neatmet� baudos.");
    

    format(string, sizeof(string), "[LSPD] I�ra��t� baud� asmeniui: %s, kurios dydys yra %d$, dabar �is asmuo privalo sutikti su J�s� bauda.",GetPlayerNameEx(giveplayerid),price);
    SendClientMessage(playerid,COLOR_POLICE,string);
    format(string, sizeof(string), "[LSPD] Pareig�nas %s i�ra�� Jums baud�, kurios suma yra %d$. Jei sutinkate su bauda turite ra�yti: /accept fine.",GetPlayerNameEx(playerid),price);
    SendClientMessage(giveplayerid,COLOR_POLICE,string);
    
    new Alloc:mem = malloc(2 + strlen(reason));
    if(!mem)
        return print("ERROR. Klaida. CMD:fine failed in allocating memory.");

    mset(mem, 0, playerid);
    mset(mem, 1, price);
    msets(mem, 2, reason);
    SetPVarInt(giveplayerid, "FineOfferMemory", _:mem);
    SetTimerEx("FineOfferExpires", 15000, false, "i", giveplayerid);
    return 1;
}
*/
forward FineOfferExpires(playerid);
public FineOfferExpires(playerid)
{
    if(GetPVarInt(playerid, "FineOfferMemory"))
        free(Alloc:GetPVarInt(playerid, "FineOfferMemory"));
    DeletePVar(playerid, "FineOfferMemory");
}
/*
CMD:fines( playerid, params[ ] )
{
    if ( PlayerFaction( playerid ) != 1)
        ShowFines(playerid, playerid);
    else
    {
        new giveplayerid;
        if ( sscanf( params, "u", giveplayerid ) )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /fines [veik�jo id]" );
        if ( !IsPlayerConnected( giveplayerid ) )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: �aid�jas norimu ID neprisijung�s!");
        if ( !PlayerToPlayer( 10.0, playerid, giveplayerid ) )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti �io veiksmo su veik�ju, kuris n�ra �alia J�s�.");

        ShowFines(playerid, giveplayerid);
    }
    return 1;
}
*/
/*
CMD:vehiclefines( playerid, params[ ] )
{
    if ( PlayerFaction( playerid ) != 1)
    {
        if ( IsPlayerInAnyVehicle( playerid ) )
            ShowVehicleFines(playerid, GetPlayerVehicleID(playerid));
    }
    else
    {
        new giveplayerid;
        if ( sscanf( params, "d", giveplayerid ) )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /vehiclefines [TR. PRIEMON�S ID]" );
        if ( !doesVehicleExist(giveplayerid) )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nurodytas tr. priemon�s ID n�ra galimas." );
        if ( cInfo[ giveplayerid ][ cOwner ] < 1 )
            return 1;

        ShowVehicleFines(playerid, giveplayerid);
    }
    return 1;
}
*/


CMD:tognames( playerid, params[ ] )
{
    #pragma unused params
    if ( GetPVarInt( playerid, "NAMES_SHOW" ) == 0 )
    {
        foreach(Player,i)
        {
            ShowPlayerNameTagForPlayer(playerid, i, 0);
        }
        SendClientMessage( playerid, COLOR_LIGHTRED, "[TOGnames] Kit� veik�j� vardai buvo pasl�pti. Nor�dami �jungti pakartokite komand�: /tognames. " );
        SetPVarInt( playerid, "NAMES_SHOW", 1 );
        return 1;
    }
    else if ( GetPVarInt( playerid, "NAMES_SHOW" ) == 1 )
    {
        foreach(Player,i)
        {
            ShowPlayerNameTagForPlayer(playerid, i, pInfo[i][pMask]);
        }
        SendClientMessage( playerid, COLOR_WHITE, "[TOGnames] Kit� veik�j� vard� rodymas buvo �jungtas.." );
        SetPVarInt( playerid, "NAMES_SHOW", 0 );
        return 1;
    }
    return 1;
}
/*
CMD:dice(playerid)
{
    if(!IsItemInPlayerInventory(playerid, ITEM_DICE))
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nor�dami naudotis �ia komanda privalote savo invetoriuje tur�ti lo�im� kauliukus. " );

    OnPlayerUseDice(playerid, ITEM_DICE);
    return 1;
}*/


public OnPlayerCommandReceived(playerid, cmdtext[]) {
    #if defined DEBUG
        printf("[debug] OnPlayerCommandReceived(%s, %s)", GetName(playerid), cmdtext);
    #endif

    if(!IsPlayerLoggedIn(playerid))
    {
        SendClientMessage( playerid, GRAD, " J�s nesate prisijung�s, pra�ome prisijungti.");
        return 0;
    }
    SetPVarInt( playerid, "Is_AFK", 0 );
    if( GetPVarInt( playerid, "Anti_Spam" ) > 0 )
    {
        //SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: Labai j�s� pra�ome, nenaudokite komand� taip greit. " );
        //return 0;
    }
    SetPVarInt( playerid, "Anti_Spam", 3 );
    return 1;
}

public OnPlayerCommandPerformed(playerid, cmdtext[ ], success)
{
    #if defined DEBUG 
        printf("[debug] OnPlayerCommandPerformed(%s, %ds %d)", GetName(playerid), cmdtext, success);
    #endif
    LastPlayerCommandTimestamp[ playerid ] = gettime();
    //foreach(new i : Player)
       // if(IsPlayerSpectatingPlayer(i, playerid))
            //UpdateDynamic3DTextLabelText(SpecCommandLabel[ i ], 0x00AA00FF, cmdtext);
    //SetTimerEx("SpecLabelDissapear", 30000, false, "i", playerid);



    //if ( !success )
    //    return SendClientMessage( playerid, COLOR_LIGHTRED, "Ne�inoma komanda: J�s� para�yta komanda neegzistuoja. Pabandykite dar kart� arba naudokit�s /askq komanda. " );

    if ( AfkCheck[ playerid ] != 0 )
        AfkCheck[ playerid ] = 0;

    return success;
}

forward SpecLabelDissapear(playerid);
public SpecLabelDissapear(playerid)
{
    foreach(new i : Player)
        if(IsPlayerSpectatingPlayer(i, playerid))
            UpdateDynamic3DTextLabelText(SpecCommandLabel[ i ], 0x00AA00FF, " ");
}

/*
CMD:invite( playerid, params[ ] )
{
    new
        giveplayerid,
        string[ 128 ];
        
    if(pInfo[playerid][pLead] > 0 )
    {
        if ( sscanf( params, "u", giveplayerid ) ) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /invite [veik�jo ID]" );
        if(!IsPlayerConnected(giveplayerid))  return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
        if(pInfo[ giveplayerid ][ pMember ] > 0 ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jas jau atstovauja kitai frakcijai. ");
        pInfo[ giveplayerid ][ pMember ] = pInfo[ playerid ][ pLead ];
        pInfo[ giveplayerid ][ pRank ] = 1;
        pInfo[ giveplayerid ][ pJob ] = JOB_NONE;
        pInfo[ giveplayerid ][ pJobLevel ] = 0;
        pInfo[ giveplayerid ][ pJobSkill ] = 0;
        format(string,sizeof(string),"D�mesio, J�s buvote pakviestas prisijungti � %s. Jus pakviet� prisijungti frakcijos narys: %s.",fInfo[PlayerFaction( playerid )][fName],GetName(playerid));
        SendClientMessage(giveplayerid,COLOR_NEWS, string);
        format(string,sizeof(string),"J�s� pasirinktas veik�jas %s buvo s�kmingai prid�tas � frakcijos narius.",GetName(giveplayerid));
        SendClientMessage(playerid,COLOR_NEWS, string);
        return 1;
    }
    else
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s neturite galimyb�s naudotis �ia komanda.");
}
*/
/*
CMD:setrank( playerid, params[ ] )
{
    new
        giveplayerid,
        rank,
        string[ 128 ];
        
    if(pInfo[playerid][pLead] < 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s neturite galimyb�s naudotis �ia komanda.");
    if ( sscanf( params, "ud", giveplayerid, rank ) ) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /setrank [veik�jo ID] [rangas]" );
    if(IsPlayerNPC(giveplayerid)) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite u�daryti �io veik�jo, kadangi tai serverio dirbtinis �aid�jas (BOT)");
    if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
    if(pInfo[giveplayerid][pMember] != pInfo[playerid][pLead]) return SendClientMessage(playerid, COLOR_LIGHTRED,"Klaida, nurodytas veik�jas nepriklauso J�s� turimai frakcijai.");
    if(rank < 0 || rank > (MAX_FACTION_RANKS-1)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas blogas frakcijos rango numeris.");
    pInfo[giveplayerid][pRank] = rank;
    format(string,sizeof(string),"Sveikiname, Tavo frakcijos vadovas %s pakeit� J�s� esam� rang� frakcijoje � %d",GetName(playerid),pInfo[giveplayerid][pRank]);
    SendClientMessage(giveplayerid,COLOR_NEWS, string);
    format(string,sizeof(string),"S�kmingai pakeit�t� savo frakcijos nario %s rang� � %d, jis buvo informtuotas.",GetName(giveplayerid),pInfo[giveplayerid][pRank]);
    SendClientMessage(playerid,COLOR_NEWS, string);
    return 1;
}
*/
/*
CMD:uninvite( playerid, params[ ] )
{
    new
        giveplayerid,
        string[ 128 ];
        
    if(pInfo[playerid][pLead] > 0 )
    {
        if ( sscanf( params, "u", giveplayerid ) ) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /uninvite [veik�jo ID]" );
        if(!IsPlayerConnected(giveplayerid))  return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
        if(pInfo[giveplayerid][pMember] != pInfo[playerid][pMember] )
        {
            SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, nurodytas veik�jas nepriklauso J�s� turimai frakcijai.");
            return 1;
        }
        if ( pInfo[ giveplayerid ][ pMember ] == 2 || pInfo[ giveplayerid ][ pMember ] == 5 )
        {
            RemovePlayerJobWeapons(giveplayerid);
            ResetPlayerWeapons( giveplayerid );
            SetPlayerArmour( giveplayerid, 0 );
        }

        pInfo[giveplayerid][pMember] = 0;
        pInfo[giveplayerid][pRank] = 0;
        pInfo[giveplayerid][pLead] = 0;
        pInfo[giveplayerid][pSpawn] = DefaultSpawn;
        format(string,sizeof(string),"S�kmingai pa�alinote veik�j� %s i� savo frakcijos, jam bus prane�ta apie pa�alinim�",GetName(giveplayerid));
        SendClientMessage(playerid,COLOR_NEWS, string);
        format(string,sizeof(string),"D�mesio, J�s buvote pa�alintas i� savo frakcijos. Jus pa�alino veik�jas: %s",GetName(playerid));
        SendClientMessage(giveplayerid,COLOR_NEWS, string);
        return 1;
    }
    else
       return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s neturite galimyb�s naudotis �ia komanda.");
}*/

/*
CMD:setspawn (playerid, params[])
{
    if(isnull(params))
    {
        SetSpawnInfo:
        SendClientMessage(playerid, COLOR_LIGHTRED, "|________VEIK�JO ATSIRADIMO VIETA PRISIJUNGUS________|");
        SendClientMessage(playerid, COLOR_LIGHTRED2, "Atsiradimo viet� taip pat galite redaguoti vartotojo valdymo pulte: ltrp.lt");
        SendClientMessage(playerid, COLOR_WHITE, "KOMANDOS NAUDOJIMAS: /setspawn [VIETA]");
        SendClientMessage(playerid, COLOR_WHITE, "VIETOS: Idlewood, Los Santos, Namas, Frakcija, Verslas, Gara�as");
        SendClientMessage(playerid, COLOR_LIGHTRED2, "_______________________________");
    }
    else
    {
        if(!strcmp(params, "Idlewood", true))
        {
            pInfo[ playerid ][ pSpawn ] = DefaultSpawn;
            SendClientMessage( playerid, COLOR_NEWS,"Vieta s�kmingai nustatyta, dabar prisijung� � server� kit� kart� atsirasite Idlewood rajone..");
        }
		else if(!strcmp(params, "Los Santos", true))
        {
            pInfo[ playerid ][ pSpawn ] = SpawnLosSantos;
            SendClientMessage( playerid, COLOR_NEWS,"Vieta s�kmingai nustatyta, dabar prisijung� � server� kit� kart� atsirasite Los Santos Unity Station.");
        }
        else if(!strcmp(params, "Namas", true))
        {
            new index = GetPlayerHouseIndex(playerid, true);
            if(index == -1)
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, turite stov�ti prie namo kur� norite pasirinkti kaip atsiradimo viet�.");

            if(!IsPlayerHouseOwner(playerid, index) && !IsPlayerHouseTenant(playerid, index))
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, turite b�ti namo savininkas arba j� nuomotis kad gal�tum�te j� pasirinkti kaip atsiradimo viet�.");

            pInfo[ playerid ][ pBSpawn ] = GetHouseID(index);
            pInfo[ playerid ][ pSpawn ] = SpawnHouse;
            SendClientMessage(playerid, COLOR_NEWS,"Vieta s�kmingai nustatyta, dabar prisijung� � server� kit� kart� atsirasite �alia savo namo.");
        }
        else if(!strcmp(params, "Frakcija", true))
        {
            if( PlayerFaction( playerid ) > 0 )
            {
                pInfo[ playerid ][ pSpawn ] = SpawnFaction;
                SendClientMessage( playerid, COLOR_NEWS,"Vieta s�kmingai nustatyta, dabar prisijung� � server� kit� kart� atsirasite frakcijos nustatytoje atsiradimo vietoje.");
            }
            else
                SendClientMessage( playerid, COLOR_LIGHTRED,"Klaida, J�s nepriklausote jokiai frakcijai. Pasitikrinkite veik�jo informacija komanda /stats.");

        }
        else if(!strcmp(params, "Verslas", true))
        {
            new index = GetPlayerBusinessIndex(playerid);
            if(index == -1)
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Turite stov�ti prie verslo kur� norite pasirinkti kaip atsiradimo viet�.");
           	if(!IsPlayerBusinessOwner(playerid, index))
        		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �is verslas jums nepriklauso.");

            pInfo[ playerid ][ pBSpawn ] = GetBusinessID(index);
            pInfo[ playerid ][ pSpawn ] = SpawnBusiness;
            SendClientMessage( playerid, COLOR_NEWS,"Vieta s�kmingai nustatyta, dabar prisijung� � server� kit� kart� atsirasite �alia savo verslo.");
        }
        else if(!strcmp(params, "Garazas", true) || !strcmp(params, "Gara�as", true))
        {
        	new index = GetPlayerGarageIndex(playerid);
        	if(index == -1)
        		return SendClientMessage(playerid, COLOR_LIGHTRED, "Turite stov�ti prie gara�o prie kurio norite atsirasti.");
        	if(!IsPlayerGarageOwner(playerid, index))
        		return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �is gara�as jums nepriklauso.");

        	pInfo[ playerid ][ pBSpawn ] = GetGarageID(index);
        	pInfo[ playerid ][ pSpawn ] = SpawnGarage;
        	SendClientMessage(playerid, COLOR_NEWS, "Vieta s�kmingai pakeista. Kit� kart� prisijung� � server� atsirasite prie gara�o.");
        }
        else 
            goto SetSpawnInfo;
        SaveAccount(playerid);
    }
    return true;
}
*/

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
        SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /accept [�odis]");
        SendClientMessage(playerid,GRAD,"PAGALBA: car, house, biz, live, license, bk, garage, sup, frisk, blindfold");
        return 1;
    }
    if(!strcmp("car",accept,true))
    {
        if(Offer[playerid][0] == 255) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Niekas tau nesiulo pirkti naujo automobilio.");
        if(OfferPrice[playerid][0] > GetPlayerMoney(playerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Neturi pakankamai pinig�, kad galetum nupirkti jo automobil�.");
        if(!IsPlayerConnected(Offer[playerid][0]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
            Offer[playerid][0] = 255;
            return 1;
        }
        if(!PlayerToPlayer(5, playerid,Offer[playerid][0]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: �aid�jas n�ra prie j�s�.");
            Offer[playerid][0] = 255;
            return 1;
        }

        if(GetPlayerState(Offer[playerid][0]) != PLAYER_STATE_DRIVER) return SendClientMessage(playerid,GRAD,"Pardav�jas turi s�d�ti savo automobilyje.");
        new idof = OfferID[ playerid ][ 0 ];

        GetPlayerIp( playerid, IP, 16 );
        GetPlayerIp( Offer[playerid][0], IP2, 16 );
        
        if( !strcmp( IP, IP2, true ) || pInfo[ playerid ][ pUcpID ] == pInfo[ Offer[playerid][0] ][ pUcpID ] )
            return true;

        if ( cInfo[ idof ][ cOwner ] != pInfo[ Offer[ playerid ][ 0 ] ][ pMySQLID ] )
        {
            SendClientMessage( Offer[ playerid ][ 0 ], COLOR_LIGHTRED, "Persp�jimas: Tai ne j�s� automobilis.");
            Offer[ playerid ][ 0 ] = 255;
            return 1;
        }

        format(string,sizeof(string),"%s nupirko j�s� automobil� u� $%d.",GetPlayerNameEx(playerid),OfferPrice[playerid][0]);
        SendClientMessage(Offer[playerid][0],COLOR_NEWS,string);
        format(string,sizeof(string),"J�s nupirkote i� jo %s automobil� u� $%d.",GetPlayerNameEx(Offer[playerid][0]),OfferPrice[playerid][0]);
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
        if(Offer[playerid][1] == 255) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Niekas tau nesiulo pirkti namo");
        if(OfferPrice[playerid][1] > GetPlayerMoney(playerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Neturi pakankamai pinig�, kad galetum nupirkti �� nam�.");
        if(!IsPlayerConnected(Offer[playerid][1]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
            Offer[playerid][1] = 255;
            return 1;
        }
        if(!PlayerToPlayer(5, playerid,Offer[playerid][1]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: �aid�jas n�ra prie j�s�.");
            Offer[playerid][1] = 255;
            return 1;
        }
        
        GetPlayerIp( playerid, IP, 16 );
        GetPlayerIp( Offer[playerid][1], IP2, 16 );

        if( !strcmp( IP, IP2, true ) || pInfo[ playerid ][ pUcpID ] == pInfo[ Offer[playerid][1] ][ pUcpID ] )
            return true;
            
        format(string,sizeof(string),"%s nupirko J�s� nam� u� $%d.",GetPlayerNameEx(playerid),OfferPrice[playerid][1]);
        SendClientMessage(Offer[playerid][1],COLOR_NEWS,string);
        format(string,sizeof(string),"J�s nupirkote i� jo %s nam� u� $%d.",GetPlayerNameEx(Offer[playerid][1]),OfferPrice[playerid][1]);
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
        if(Offer[playerid][2] == 255) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Niekas tau nesiulo pirkti verslo.");
        if(OfferPrice[playerid][2] > GetPlayerMoney(playerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Neturi pakankamai pinig�, kad galetum nupirkti �� versl�.");
        if(!IsPlayerConnected(Offer[playerid][2]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
            Offer[playerid][2] = 255;
            return 1;
        }
        if(!PlayerToPlayer(5, playerid,Offer[playerid][2]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: �aid�jas n�ra prie j�s�.");
            Offer[playerid][2] = 255;
            return 1;
        }
        
        GetPlayerIp( playerid, IP, 16 );
        GetPlayerIp( Offer[playerid][2], IP2, 16 );

        if( !strcmp( IP, IP2, true ) || pInfo[ playerid ][ pUcpID ] == pInfo[ Offer[playerid][2] ][ pUcpID ] )
            return true;
        
        format(string,sizeof(string),"%s nupirko j�s� versl� u� $%d.",GetPlayerNameEx(playerid),OfferPrice[playerid][2]);
        SendClientMessage(Offer[playerid][2],COLOR_NEWS,string);
        format(string,sizeof(string),"J�s nupirkote i� jo %s versl� u� $%d.",GetPlayerNameEx(Offer[playerid][2]),OfferPrice[playerid][2]);
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
        if(Offer[playerid][4] == 255) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Niekas tau nesiulo tiesioginio pokalbio.");
        if(!PlayerToPlayer(5, playerid,Offer[playerid][4])) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Tu nesi �ialia pri� tau siulan�io �mogaus");
        if(!IsPlayerConnected(Offer[playerid][4]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
            Offer[playerid][4] = 255;
            return 1;
        }
        TalkingLive[playerid] = Offer[playerid][4];
        TalkingLive[Offer[playerid][4]] = playerid;
        format(string,sizeof(string),"%s pri�m� i� j�s� pasi�lim�.",GetPlayerNameEx(playerid));
        SendClientMessage(Offer[playerid][4],COLOR_NEWS,string);
        format(string,sizeof(string),"J�s pri�m�te pasi�lim� i� %s",GetPlayerNameEx(Offer[playerid][2]));
        SendClientMessage(playerid,COLOR_NEWS,string);
        Offer[playerid][4] = 255;
        return 1;
    }
    
    else if(!strcmp("fight",accept,true))
    {
        if(Mires[playerid] > 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s� veik�jas �iuo metu yra kritin�je arba komos b�senoje.");
        if(Offer[playerid][6] == 255) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Niekas tau nesiulo tiesioginio pokalbio.");
        if(CheckBox() == 1) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Bokso sal� u�imta, palaukite.");
        if(!PlayerToPlayer(5, playerid,Offer[playerid][6])) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Tu nesi �ialia pri� tau siulan�io �mogaus");
        if(!IsPlayerConnected(Offer[playerid][6]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
            Offer[playerid][6] = 255;
            return 1;
        }
        format(string,sizeof(string),"%s pri�m� i� j�s� pasi�lim�.",GetPlayerNameEx(playerid));
        SendClientMessage(Offer[playerid][6],COLOR_NEWS,string);
        format(string,sizeof(string),"J�s pri�m�te pasi�lim� i� %s",GetPlayerNameEx(Offer[playerid][6]));
        SendClientMessage(playerid,COLOR_NEWS,string);

        SetPlayerPos(playerid,768.7744,-66.8329,1001.5692);
        SetPlayerFacingAngle(playerid,137.2355);
        SetPlayerPos(Offer[playerid][6],764.6347,-70.4305,1001.5692);
        SetPlayerFacingAngle(Offer[playerid][6],313.6439);

        SetPlayerHealth(playerid,150);
        SetPlayerHealth(Offer[playerid][6],150);
        format(string, sizeof(string), "[MG NEWS] Bokso var�ybos prasideda, ringe %s kovos pri�� %s, kova prasid�s uz 15 sekund�iu.",  GetPlayerNameEx(playerid), GetPlayerNameEx(Offer[playerid][6]));
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
        if ( !UsePDCMD( playerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente." );
        string = strtok(params, idx);
        giveplayerid = strval( string );
        if ( pInfo[ giveplayerid ][ pBackup ] == 0 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, J�s �iuo metu nekviet�te jokio pastiprinimo. " );

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
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje. " );

        new type = GetPVarInt( giveplayerid, "OFER_SUP");
        SetPVarInt( giveplayerid, "OFER_SUP", 0 );

        if ( !PlayerToPlayer( 1.0, playerid, giveplayerid ) )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s nesate �alia to �aid�jo. " );

        if ( type == 0 )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: �aid�jas jums nieko nesi�lo. " );

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
        if(Offer[playerid][7] == 255) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Niekas tau nesiulo pirkti gara�o");
        if(OfferPrice[playerid][7] > GetPlayerMoney(playerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Neturi pakankamai pinig�, kad galetum nupirkti �� gara��.");
        if(!IsPlayerConnected(Offer[playerid][7]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
            Offer[playerid][7] = 255;
            return 1;
        }
        if(!PlayerToPlayer(5, playerid,Offer[playerid][7]))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: �aid�jas n�ra prie j�s�.");
            Offer[playerid][7] = 255;
            return 1;
        }
        
        GetPlayerIp( playerid, IP, 16 );
        GetPlayerIp( Offer[playerid][7], IP2, 16 );

        if( !strcmp( IP, IP2, true ) || pInfo[ playerid ][ pUcpID ] == pInfo[ Offer[playerid][7] ][ pUcpID ] )
            return true;
        
        format(string,sizeof(string),"%s nupirko J�s� gara�� u� $%d.",GetPlayerNameEx(playerid),OfferPrice[playerid][7]);
        SendClientMessage(Offer[playerid][7],COLOR_NEWS,string);
        format(string,sizeof(string),"J�s nupirkote i� jo %s gara�� u� $%d.",GetPlayerNameEx(Offer[playerid][7]),OfferPrice[playerid][7]);
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
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: Jis nepra�o leidimo apie�koti j�s�. " );
        if ( !PlayerToPlayer   ( 5.0, playerid, giveplayerid ) )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti �io veiksmo veik�jui, kuris n�ra �alia J�s�..");

        SendClientMessage( giveplayerid, COLOR_GREEN2, "_____________________ Turimi daiktai __________________");
        format( string, 56, "Pinig�: %d ", GetPlayerMoney(playerid) );
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
                format           ( string, 50," Ginklas %s �ovini� %d ", wepname, ammo );
                SendClientMessage( giveplayerid, COLOR_FADE1, string );
            }
        }
        format      ( string, 70, "* %s api��ko %s ." ,GetPlayerNameEx( giveplayerid ), GetPlayerNameEx( playerid ) );
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
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Niekas tau nesi�lo baudos.");

        sellerid = mget(mem, 0);
        price = mget(mem, 1);
        mgets(string, sizeof string, mem, 2);
        free(mem);
        DeletePVar(playerid, "FineOfferMemory");

        if(!IsPlayerConnected(sellerid))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
        if(!IsPlayerInRangeOfPlayer(playerid, sellerid, 4.0))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Tu nesi �ialia pri� tau si�lan�io �mogaus");
        ///if(GetPlayerMoney(playerid) < price)
        // pasirod moketi turi visada :|
         //   return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, Jums neu�tenka pinig�.");

        mysql_real_escape_string(string, string);
        format(query, sizeof(query), "INSERT INTO `tickets` (name,crime,reporter,price) VALUES ('%s','%s','%s','%d')",GetPlayerNameEx(playerid),string,GetName(sellerid),price);
        mysql_query(DbHandle, query, false);

        format( query, sizeof(query), " ** J�s i�ra��te baudos lapel� %s'ui. ",GetPlayerNameEx(playerid) );
        SendClientMessage( sellerid, COLOR_WHITE, query );

        SendClientMessage(playerid, COLOR_WHITE, " ** Sumok�jote baud�.");

        GivePlayerMoney(playerid, -price);
        return 1;
    }
    else if(!strcmp(accept, "blindfold", true))
    {
        string = strtok(params, idx);
        new targetid = strval(string);

        if(Offer[ playerid ][ 8 ] == INVALID_PLAYER_ID)
            SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: jums nieko nesi�lo u�ri�ti rai��io.");

        else if(Offer[ playerid ][ 8 ] != targetid)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Jis nepra�o leidimo u�ri�ti jums rai�t� ant aki�.");

        else if(!IsPlayerConnected(Offer[ playerid ][ 8 ]))
            SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");

        else if(!IsPlayerInRangeOfPlayer(playerid, Offer[ playerid ][ 8 ], 5.0))
            SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: �aid�jas n�ra prie j�s�.");

        else 
        {
            format(string, sizeof(string), " u�ri�a %s rai�t� ant galvos u�dengdamas akis.", GetPlayerNameEx(playerid));
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
/*
CMD:takejob( playerid, params[ ] )
{
    for ( new i = 0; i < MAX_JOBS; i++ )
    {
        if ( PlayerToPoint( 5.0, playerid, pJobs[ i ][ Job_x ], pJobs[ i ][ Job_y ], pJobs[ i ][ Job_z ] ) )
        {
            if ( PlayerFaction( playerid ) < 8 && PlayerFaction( playerid ) != 0 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s jau esate frakcijoje/darbuov�t�je." );
            if ( pInfo[ playerid ][ pJob    ] != 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: J�s jau turite darb�." );
            SendClientMessage( playerid, COLOR_WHITE, "* J�s �sidarbinote, jeigu reikia daugiau pagalbos ra�ykite /help." );
            pInfo[ playerid ][ pJob ] = i;
            pInfo[ playerid ][ pJobContr ] = pJobs[ i ][ Contr ];
            return 1;
        }
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
        
    if ( sscanf( params, "u", giveplayerid ) ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /canceloffer [�aid�jo id]");
    if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
    for(new i = 0; i > 5; i ++)
    {
        if(Offer[giveplayerid][i] == playerid)
        {
            SendClientMessage(playerid,GRAD,"J�s at�auk�te pasi�lim�.");
            format(string,sizeof(string),"%s at�auk� savo pasi�lim�.",GetPlayerNameEx(playerid));
            SendClientMessage(giveplayerid,GRAD,string);
            Offer[giveplayerid][i] = 255;
            return 1;
        }
    }
    return 1;
}
/*
CMD:die( playerid, params[ ] )
{
    if ( Mires[ playerid ] == 0 ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: J�s ne komos busenoje.");
    if ( Mires[ playerid ] > 420 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: Dar nepra�jo 3 minut�s po j�s� mirties. " );
    StopLoopingAnim( playerid, false );
    SetPlayerHealth( playerid, 0 );
    TogglePlayerControllable(playerid, 0);
    DestroyDynamic3DTextLabel( DeathLabel[playerid] );
    return 1;
}*/
/*
CMD:repair( playerid, params[ ] )
{
    new
        veh,
        string[ 126 ];
    if(pInfo[playerid][pJob] != JOB_MECHANIC) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nor�dami atlikti �� veiksm�, privalote b�ti auto mechaniku.");
    if(!IsPlayerInAnyVehicle( playerid )) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nor�dami atlikti �� veiksm� privalote sed�ti tr. priemon�je.");
    veh = GetPlayerVehicleID( playerid );
    if ( Engine[ veh ] == true )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Pradedant taisyti tr. priemon� turi b�ti u�gesintas variklis." );

    if(PlayerToPoint(60.0, playerid, 1655.4087,-1798.4670,13.5455 ))
    {
        new Float:VD;
        GetVehicleHealth( veh, VD );

        VD = VD/5;

        if ( GetPlayerMoney(playerid) < VD )
        {
            format( string, 126, "[Mechanikas] D�mesio, �ios tr. priemon� sutvarkymo kain� nuo �alos yra %d$", floatround(VD) );
            SendClientMessage( playerid, COLOR_LIGHTRED2, string );
            return 1;
        }
        StartTimer(playerid,120,1);
        return 1;
    }
    else
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, komanda galite naudoti tik mechaniko dirbtuv�se.");
    return 1;
}
CMD:repaint( playerid, params[ ] )
{
    new
        veh = GetPlayerVehicleID( playerid ),
        color,
        color2;
    if(pInfo[playerid][pJob] != JOB_MECHANIC) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nor�dami atlikti �� veiksm�, privalote b�ti auto mechaniku.");
    if(GetPlayerState( playerid ) != PLAYER_STATE_DRIVER) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nor�dami atlikti �� veiksm� privalote sed�ti tr. priemon�je.");
    if ( Engine[ veh ] == true )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Pradedant taisyti tr. priemon� turi b�ti u�gesintas variklis." );
    if(PlayerToPoint(60.0, playerid, 1655.4087,-1798.4670,13.5455 ))
    {
        if ( GetPlayerMoney(playerid) < 450 )
            return SendClientMessage( playerid, COLOR_LIGHTRED2, "[Mechanikas] D�mesio tr. priemon�s perda�ymas kainuos 450$" );

        if ( sscanf( params, "dd", color, color2 ) ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /repaint [SPALVA1][SPALVA2]");

        SetPVarInt( playerid, "PAINT1", color );
        SetPVarInt( playerid, "PAINT2", color2 );
        StartTimer(playerid,180,2);
        return 1;
    }
    else
        SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: J�s ne auto mechaniko dirbtuv�se.");
    return 1;
}
*/
/*
CMD:addwheels( playerid, params[ ] )
{
    if(pInfo[playerid][pJob] != JOB_MECHANIC) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nor�dami atlikti �� veiksm�, privalote b�ti auto mechaniku.");
    if(GetPlayerState( playerid ) != PLAYER_STATE_DRIVER) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nor�dami atlikti �� veiksm� privalote sed�ti tr. priemon�je.");
    new idcar = GetPlayerVehicleID( playerid );
    if ( Engine[ idcar ] == true )
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Pradedant taisyti tr. priemon� turi b�ti u�gesintas variklis." );
    if(IsVehicleBike( GetVehicleModel( idcar ) ) ) return 1;
    if(cInfo[ idcar ][ cOwner ] == 0 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: �iems automobiliams d�ti modifikacijas draud�iama. ");
    if(PlayerToPoint(60.0, playerid, 1655.4087,-1798.4670,13.5455 ))
    ShowPlayerDialog(playerid,9,DIALOG_STYLE_LIST,"Ratlankiai","\
        1.Offroad\n\
        2.Mega\n\
        3.Wires\n\
        4.Twist\n\
        5.Grove\n\
        6.Import\n\
        7.Atomic\n\
        8.Ahab\n\
        9.Virtual\n\
        10.Access\n\
        11.Trance\n\
        12.Shadow\n\
        13.Rimshine\n\
        14.Classic\n\
        15.Cutter\n\
        16.Switch\n\
        17.Dollar\n\
        18.Hidraulika\n\
        Nuimti Hidraulik�\n\
        Nuimti ratus\n\
        Nuimti tunig�","U�d�ti","At�aukti");
    else
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s ne auto mechaniko dirbtuv�se." );
    return 1;
}
*/
/*
CMD:fix( playerid, params[ ] )
{
    new
        veh = GetPlayerVehicleID( playerid ),
        colorr,
        colorr2;
    if(GetPlayerState( playerid ) != PLAYER_STATE_DRIVER) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nor�dami atlikti �� veiksm� privalote sed�ti tr. priemon�je.");
    new Float:health;
    GetVehicleHealth(veh, health);
    if(health > 700) return SendClientMessage(playerid, COLOR_LIGHTRED, "Apgailestaujame, bet J�s� tr. priemon�s b�kle yra pakankamai gera, tod�l jos netavarkysime.");
    if ( Engine[ veh ] == true ) return SendClientMessage( playerid, -1, "{FF6347}Persp�jimas: Automobilio variklis turi b�ti u�gesintas." );
    if(PlayerToPoint(8.0, playerid, 2075.5986,-1831.0374,13.5545 ))
    {
        if ( GetPlayerMoney(playerid) < 500 )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: Automobilio perda�ymas kainuoja $ 500." );

        if ( sscanf( params, "dd", colorr, colorr2 ) ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /fix [SPALVA1] [SPALVA2]");

        cInfo[ veh ][ cColor ][ 0 ] = colorr;
        cInfo[ veh ][ cColor ][ 1 ] = colorr2;

        ChangeVehicleColor( veh, colorr, colorr2 );
        SendClientMessage( playerid, COLOR_LIGHTRED2, "Sveikiname, J�s� tr. priemon� buvo perda�yt� � J�s� pasirinkta spalv�. Tikim�s Jums patiks!" );
        RemovePlayerWeapon( playerid, 41 );
        SetVehicleHealth(veh, 1000);
        RepairVehicle(veh);
        GivePlayerMoney( playerid, -500 );

        SaveVehicleEx(veh, "cColor1", colorr);
        SaveVehicleEx(veh, "cColor2", colorr2);
        return 1;
    }
    return 1;
}
*/
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
        SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: �is veiksmas galimas tik automobilyje.");
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
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

    return true;
}
CMD:dj(playerid, params[])
{
    if(GetPlayerState(playerid) != 1) return SendClientMessage(playerid, COLOR_GREY, "Animacij� galima naudoti stovint ant koj�.");
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
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");
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
        return SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�.");
    

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
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

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
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

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
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

    return true;
}
CMD:lebelly( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "PED", "KO_shot_stom", 4.0, 0, 1, 1, 1, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

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
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

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
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

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
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

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
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

    return true;
}
CMD:benchpress(playerid, params[])
{
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�.");

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
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�.");

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
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�.");

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
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

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
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

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
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

    return true;
}
CMD:loudtalk( playerid, params[ ] )
{
    #pragma unused params

    if ( GetPlayerState( playerid ) == PLAYER_STATE_ONFOOT )
        LoopingAnim( playerid, "RIOT", "RIOT_shout", 4.0, 1, 0, 0, 0, 0 );
    else
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

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
        SendClientMessage( playerid, COLOR_LIGHTRED, "Naudotis �iomis animacijomis galite tik tada, kada J�s� veik�jas yra ant koj�. ");

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
        
    if(pInfo[ playerid ][ pDonator ] < 1 ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: J�s neesate r�m�jas. ");
    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) return 1;
    if(sscanf(params,"d",walkstyle)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /walkstyle [0 - 8]");
    if(walkstyle < 0 || walkstyle > 8) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /walkstyle [0 - 8]");
    SendClientMessage(playerid,GRAD,"Stilius s�kmingai pakeistas");
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
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: J�s neesate r�m�jas. ");

    if(GetPlayerState(playerid) != PLAYER_STATE_ONFOOT) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite keisti kalb�jimo stiliaus b�damas transporto priemon�je.");

    if(sscanf(params,"d",talkstyle) || talkstyle < 0 || talkstyle > 4) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /talkstyle [0 - 4]");

    SendClientMessage(playerid,GRAD,"Stilius s�kmingai pakeistas");
    pInfo[ playerid ][ pTalkStyle ] = talkstyle;
    return 1;
}
/*
CMD:cartax( playerid, params[ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 4 )
    {
        #pragma unused params
        new giveplayerid,
            string[ 126 ];
        if ( sscanf( params, "i", giveplayerid ) )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /cartax [ 1 - 99 ] " );

        if ( giveplayerid < 0 || giveplayerid > 100 )
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Mokestis u� automobilius negali b�t didesnis negu 100.");

        cartax = giveplayerid;
        format         ( string, 126, " Kilometro kaina buvo nustatyta � %d", giveplayerid);
        SaveMisc();
    }
    return 1;
}
CMD:biztax( playerid, params[ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 4 )
    {
        #pragma unused params
        new giveplayerid,
            string[ 126 ];
        if ( sscanf( params, "i", giveplayerid ) )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /biztax [ 1 - 99 ] " );

        if ( giveplayerid < 0 || giveplayerid > 100 )
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Mokestis u� verslus negali b�t didesnis negu 100.");

        biztax = giveplayerid;
        format         ( string, 126, " Verslo mokestis buvo nustatytas � %d", giveplayerid);
        SaveMisc();
    }
    return 1;
}
CMD:housetax( playerid, params[ ] )
{
    if (GetPlayerAdminLevel(playerid) >= 4 )
    {
        #pragma unused params
        new giveplayerid,
            string[ 126 ];
        if ( sscanf( params, "i", giveplayerid ) )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /cartax [ 1 - 99 ] " );

        if ( giveplayerid < 0 || giveplayerid > 100 )
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Mokestis u� automobilius negali b�t didesnis negu 100.");

        housetax = giveplayerid;
        format         ( string, 126, " Kilometro kaina buvo nustatyta � %d", giveplayerid);
        SaveMisc();
    }
    return 1;
}
*/
CMD:pos(playerid)
{
    if(!IsPlayerAdmin(playerid) && !GetPlayerAdminLevel(playerid))
        return 0;

    new Float:x, Float:y, Float:z, string[64];
    GetPlayerPos(playerid, x, y, z);
    format(string, sizeof(string), "J�s� koordinat�s: x - %f y - %f z - %f", x, y, z);
    SendClientMessage(playerid, COLOR_NEWS, string);
    return 1;
}

CMD:goto( playerid, params [ ] )
{
    if (GetPlayerAdminLevel(playerid) >= 1 )
    {
        new
            giveplayerid,
            Float:Kords[3];
        if ( sscanf( params, "u", giveplayerid ) )
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /goto [�aid�jo id]");
        if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
        new idcar = GetPlayerVehicleID( playerid );
        GetPlayerPos(giveplayerid,Kords[0],Kords[1],Kords[2]);
        if(!IsPlayerInAnyVehicle(playerid))
        {
            SetPlayerPos(playerid,Kords[0],Kords[1]+2,Kords[2]);
            SetPlayerInterior(playerid,GetPlayerInterior(giveplayerid));
            SetPlayerVirtualWorld(playerid,GetPlayerVirtualWorld(giveplayerid));
        }
        else
            SetVehiclePos(idcar,Kords[0],Kords[1],Kords[2]+2);
        SendClientMessage(playerid,GRAD,"S�kmingai nusiteleportavai.");
    }
    return 1;
}
CMD:gethere( playerid, params [ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 1 )
    {
        new
            giveplayerid,
            Float:Kords[3];
        if ( sscanf( params, "u", giveplayerid ) )
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /gethere [�aid�jo id]");
        if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
        if(IsPlayerNPC(giveplayerid)) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite u�daryti �io veik�jo, kadangi tai serverio dirbtinis �aid�jas (BOT)");
        GetPlayerPos(playerid,Kords[0],Kords[1],Kords[2]);
        if(!IsPlayerInAnyVehicle(giveplayerid))
        {
            SetPlayerPos(giveplayerid,Kords[0],Kords[1]+2,Kords[2]);
            SetPlayerInterior(giveplayerid,GetPlayerInterior(playerid));
            SetPlayerVirtualWorld(giveplayerid,GetPlayerVirtualWorld(playerid));
        }
        else
        {
            SetVehiclePos(GetPlayerVehicleID(giveplayerid),Kords[0],Kords[1]+2,Kords[2]);
            SetVehicleVirtualWorld( GetPlayerVehicleID(giveplayerid), GetPlayerVirtualWorld(playerid) );
        }
        SendClientMessage(giveplayerid,GRAD,"J�s buvote nuteleportuotas �alia administratoriaus.");
    }
    return 1;
}
CMD:setskin( playerid, params [ ] )
{
    new
        giveplayerid,
        skinas,
        string[ 126 ];
    if ( GetPlayerAdminLevel(playerid) >= 1 )
    {
        if ( sscanf( params, "ud", giveplayerid, skinas ) )
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /setskin [�aid�joID][skino id]");
        if( !IsPlayerConnected( giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
        if ( IsPlayerNPC( giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /setskin [�aid�joID][skino id]");
        if ( skinas < 1 || skinas > 299 || skinas == 149 || skinas == 86) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: neteisingai nurodytas i�vaizdos ID.");
        SetPlayerSkin   ( giveplayerid, skinas );
        format          ( string, 126, "AdmWarn: Administratorius (%s) pakeit� veik�jui (%s) i�vaizd� (ID %d)",GetName(playerid), GetName(giveplayerid), skinas);
        SendAdminMessage( COLOR_ADM, string);
        return 1;
    }
    return 1;
}
CMD:slap( playerid, params [ ] )
{
    new
        giveplayerid,
        string[ 126 ],
        Float:Kords[ 3 ];
    if (GetPlayerAdminLevel(playerid) >= 1 )
    {
        if ( sscanf( params, "u", giveplayerid ) )
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /slap [�aid�joID]");
        if(!IsPlayerConnected( giveplayerid )) return SendClientMessage( playerid, GRAD," {FF6347}Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
        if(IsPlayerNPC(giveplayerid)) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite u�daryti �io veik�jo, kadangi tai serverio dirbtinis �aid�jas (BOT)");
        GetPlayerPos    ( giveplayerid, Kords[ 0 ], Kords[ 1 ], Kords[ 2 ] );
        new Float: HP;
        GetPlayerHealth ( giveplayerid, HP );
        format          ( string, sizeof(string), "AdmWarn: Administratorius (%s) panaudojo komand� (/slap) veik�jui (%s)",GetName(playerid), GetName( giveplayerid ) );
        SendAdminMessage( COLOR_ADM, string );
        SetPlayerPos    ( giveplayerid, Kords[ 0 ], Kords[ 1 ], Kords[ 2 ] + 5 );
        SetPlayerHealth ( giveplayerid, HP - 5 );
        PlayerPlaySound ( giveplayerid, 1130, Kords[ 0 ], Kords[ 1 ], Kords[ 2 ] + 5);
        return 1;
    }
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
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /freeze [�aid�joID]");
        if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid,GRAD," {FF6347}Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
        if( !Freezed[giveplayerid] )
        {
            ShowInfoText(giveplayerid,"~w~ UZSALDYTAS",1000);
            TogglePlayerControllable(giveplayerid, false);
            Freezed[giveplayerid] = true;
            format(string,sizeof(string),"AdmWarn: Administratorius (%s) u��ald� (/freeze) veik�j� (%s)",GetName(playerid),GetName(giveplayerid));
            SendAdminMessage(COLOR_ADM,string);
        }
        else
        {
            ShowInfoText(giveplayerid,"~w~ ATSALDYTAS",1000);
            TogglePlayerControllable(giveplayerid, true);
            Freezed[giveplayerid] = false;
            format(string,sizeof(string),"AdmWarn: Administratorius (%s) at�ald� (/unfreeze) veik�j� (%s)",GetName(playerid),GetName(giveplayerid));
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
            SendClientMessageToAll(COLOR_LIGHTRED,"AdmCmd serverio Administratorius ��jung� global� OOC kanal� - /o");
        }
        else if(OOCDisabled == false)
        {
            OOCDisabled = true;
            SendClientMessageToAll(COLOR_LIGHTRED,"AdmCmd serverio Administratorius �jung� global� OOC kanal� - /o");
        }
    }
    return 1;
}
CMD:kickall( playerid, params [ ] )
{
    new
        gMessage[ 64 ],
        string[ 216 ];

    if ( GetPlayerAdminLevel(playerid) >= 3 )
    {
        if ( sscanf( params, "s[64]", gMessage ) )
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /kickall [prei�astis]");
        foreach(Player,i)
        {
            if( i != playerid && !IsPlayerNPC(i) )
                SetTimerEx("KicknPlayer", 100, false, "d", i );
        }
        format( string, 216, "AdmCmd %s i�met� visus �aid�jus i� serverio, pri��astis: %s", GetName( playerid ), gMessage);
        SendClientMessageToAll( COLOR_LIGHTRED, string);
        return 1;
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
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, tokio NPC n�ra.");

    else 
    {
        Kick(npcid);
        SendClientMessage(playerid, COLOR_LIGHTRED, "NPC i�mestas, jis v�l prisijungs po sekund�s.");
        defer NpcReconnectDelay(playerid, params, strlen(params));
    }
    return 1;
}

timer NpcReconnectDelay[1000](adminid, npcname[], len)
{
    new string[ 60 ];
    ConnectNPC(npcname);

    format(string, sizeof(string), "NPC %s jungiamas � server�.", npcname);
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
                SendClientMessage(playerid,GRAD,"Ginkl� whipe atliktas");
            }
            else if(!strcmp(reason2,"items",true))
            {
                reason2 = strtok( params, idx );
                id2 = strval( reason2 );
                if ( id2 > 211 || id2 < 1 )      return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: blogas daikto ID..");
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
CMD:makeleader(playerid, params[])
{
    new
        fact,
        giveplayerid,
        string[ 128 ];
    if( GetPlayerAdminLevel(playerid) >= 4 )
    {
        if ( sscanf( params, "ud", giveplayerid, fact ) ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /makeleader [�aid�jo id][frakcija]");
        if ( !IsPlayerConnected(giveplayerid) ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
        if ( IsPlayerNPC( giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /makeleader [�aid�joID][frackija]");
        if ( fact < 1 || fact > sizeof fInfo ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: Frakcija gali b�ti nuo 1 iki 15 " );
        new fact2 = FactionMySQLID( fact );

        pInfo[giveplayerid][pLead] = fact2;
        pInfo[giveplayerid][pMember] = fact2;
        pInfo[giveplayerid][pRank] = 13;
        strmid(fInfo[fact][fLeader], GetName(giveplayerid), 0, 54, 54);

        RemovePlayerJobWeapons(giveplayerid);
        SetPlayerArmour( giveplayerid, 0 );

        format(string, sizeof(string), "NAUJIENA: Administratorius %s, suteik� Jums frakcijos vadovo pareigas frakcijai: %s ",GetName(playerid),fInfo[fact][fName]);
        SendClientMessage(giveplayerid,COLOR_NEWS,string);
        format(string, sizeof(string), "INFORMACIJA: J�s paskyr�te �aid�j� %s frakcijos %s vadovu.",GetName(giveplayerid),fInfo[fact][fName]);
        SendClientMessage(playerid,GRAD,string);
        //SaveFactions(fact);
    }
    return 1;
}

*/
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
            SendClientMessage(playerid,GRAD,"KODAI: 1 Sudau�ymai | 2 Draudimas");
            return 1;
        }

        new idcar = GetPlayerVehicleID( playerid );
        if(!IsPlayerInAnyVehicle(playerid) || cInfo[ idcar ][ cOwner ] == 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: j�s nes�dite transporto priemon�je arba tr. priemon� priklauso serveriui.");
        switch(id)
        {
            case 1:
            {
                cInfo[idcar][cDuzimai] = id2;
                format(string,126,"Tr. priemon�s %d sunaikinim� kiekis buvo pakeistas � %d",idcar,id2);
            }
            case 2:
            {
                cInfo[idcar][cInsurance] = id2;
                format(string,126,"Tr. priemon�s %d draudimo kiekis buvo pakeistas � %d",idcar,id2);
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
            SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /setstat [�aid�jo id][Kodas][Kodas2]");
            SendClientMessage(playerid,GRAD,"KODAI: 1 Lygis | 2 Bankas | 3 Nuomos Raktas | 4 Darbas | 5 Mirtys");
            SendClientMessage(playerid,GRAD,"KODAI: 6 Tel.Nr. | 7 darbo Lygis");
            return 1;
        }
        if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
        switch(id)
        {
            case 1:
            {
                pInfo[giveplayerid][pLevel] = id2;
                SetPlayerScore( giveplayerid, pInfo[ giveplayerid ][ pLevel ] );
                format(string,126,"�aid�jo %s lygis buvo pakeistas � %d",GetName(giveplayerid),id2);
            }
            case 2:
            {
                pInfo[giveplayerid][pBank] = id2;
                format(string,126,"�aid�jo %s banko s�skaita buvo pakeistas � %d",GetName(giveplayerid),id2);
            }
            case 3:
            {
                pInfo[giveplayerid][pSpawn] = DefaultSpawn;
                pInfo[giveplayerid][pHouseKey] = id2;
                format(string,126,"�aid�jo %s nuomos raktas buvo pakeistas � %d",GetName(giveplayerid),id2);
            }
            case 4:
            {
                if( id2 > 0 && id2 < MAX_JOBS && pInfo[ giveplayerid ][ pMember ] == 0 )
                {
                    pInfo[ giveplayerid ][ pJob ] = id2;
                    pInfo[ giveplayerid ][ pJobLevel ] = 0;
                    pInfo[ giveplayerid ][ pJobSkill ] = 0;
                    format(string,126,"�aid�jo %s darbas buvo pakeistas � %d",GetName(giveplayerid),id2);
                }
                else 
                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, netinkamas darbo ID arba �is �aid�jas jau turi darb�.");
            }
            case 5:
            {
                pInfo[giveplayerid][pDeaths] = id2;
                format(string,126,"�aid�jo %s mirtys buvo pakeisto � %d",GetName(giveplayerid),id2);
            }
            case 6:
            {
                pInfo[giveplayerid][pPhone] = id2;
                format(string,126,"�aid�jo %s telefono numeris buvo pakeistas � %d",GetName(giveplayerid),id2);
            }
            case 7:
            {
                pInfo[giveplayerid][pJobLevel] = id2;
                format(string,126,"�aid�jo %s darbo Lygis buvo pakeistas � %d",GetName(giveplayerid),id2);
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
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /forcelogout [ �aid�jo vardas/ id ] ");

        if(playerLID == INVALID_PLAYER_ID)
            return SendClientMessage(playerid, COLOR_GREY, "Toks ID n�ra rastas, �is �aid�jas n�ra prisijung�s.");

        if(!AdminDuty[playerid])
            return SendClientMessage(playerid, COLOR_GREY, "J�s turite b�ti AOD, kad gal�tum�te atjungti �aid�j�.");

        format(szMessage, sizeof(szMessage), "J�s buvote atjungtas nuo administratoriaus %s.", GetName( playerid ));
        SendClientMessage(playerid, COLOR_WHITE, szMessage);

        SaveAccount(playerLID);
        PlayerOn[playerLID] = false;

        SendClientMessage(playerLID, COLOR_GREY, "J�s buvote atjungtas.");
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
        SendClientMessage(playerid, -1, "U�temimo efetktas i�jungtas.");
    else 
        SendClientMessage(playerid, -1, "U�temimo efektas �jungtas");
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
        SendClientMessage(playerid, COLOR_NEWS, "�ra�ymas s�kmingai baigtas. Failas yra scriptfiles direktorijoje.");
    }
    else 
    {
        if(isnull(params))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, teisingas naudojimas /npcrecord [Failo pavadinimas kuriame bus �ra�as]");

        new type, string[133];
        format(string, sizeof(string), "%s.rec", params);

        if(fexist(string))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �ra�as su tokiu pavadinimu jau egzistuoja.");

        if(IsPlayerInAnyVehicle(playerid))
            type = PLAYER_RECORDING_TYPE_DRIVER;
        else 
            type = PLAYER_RECORDING_TYPE_ONFOOT;

        StartRecordingPlayerData(playerid, type, params);
        SendClientMessage(playerid, COLOR_NEWS, "�ra�in�jimas prad�tas. J� pabaigti galite v�l para�� /npcrecord");
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
                                                                 - Tr. priemon�s\n\
                                                                 - Serverio ��jimai\n\
                                                                 - Skelbti balsavim�\n\
                                                                 - Automobili� turgus\n\
                                                                 - Gara�ai\n\
                                                                 - Industrijos\n\
                                                                 - Interjerai\n\
                                                                 - �vairios koordinat�s\n\
                                                                 - Grafiti\n\
                                                                 - Serverio klaidos","Rinktis","At�aukti");
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
                SendClientMessage(playerid,GRAD,"Dabar tave matys admin� s�ra�e.");
            }
            case false:
            {
                SetPVarInt( playerid, "hideadmin", true );
                SendClientMessage(playerid,GRAD,"Dabar tave nematys admin� s�ra�e.");
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
        if ( fid > sizeof ( fInfo ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: netinkamas frakcijos ID " );

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
        if ( id < 1 || id > sizeof fInfo ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: netinkamas frakcijos ID " );

        format( string, 126, "Frakcijoje: %s, �iuo metu yra prisijungusiu �moni�: %d ",fInfo[ id ][ fName ], IsOnlineFactionMembers( id ) );
        SendClientMessage( playerid, COLOR_LIGHTRED2, string );
    }
    else
    {
        if(PlayerFaction( playerid ) == 1)
        {
            format( string, 126, "Frakcijoje: %s, �iuo metu yra prisijungusiu �moni�: %d ", fInfo[ 2 ][ fName ], IsOnlineFactionMembers( 2 ) );
            SendClientMessage( playerid, COLOR_LIGHTRED2, string );
        }
        else if( PlayerFaction( playerid ) == 2 )
        {
            format( string, 126, "Frakcijoje: %s, �iuo metu yra prisijungusiu �moni�: %d ", fInfo[ 1 ][ fName ], IsOnlineFactionMembers( 1 ) );
            SendClientMessage( playerid, COLOR_LIGHTRED2, string );
        }
    }
    return 1;
}
*/
/*
stock IsOnlineFactionMembers( id )
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
CMD:checkjail( playerid, params[ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 1 )
    {
        new giveplayerid,
            string[ 126 ];
        if ( sscanf( params, "u", giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /checkjail [ �aid�jo vardas/ id ] ");
        if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
        else if ( pInfo[ giveplayerid ][ pJailTime ] > 0 )
        {
            format( string, 126, "�aid�jui %s liko dar pras�d�ti %d minu�i�.", GetName( giveplayerid ), pInfo[ giveplayerid ][ pJailTime ] / 60 );
            SendClientMessage( playerid, COLOR_WHITE, string );
        }
        else
            SendClientMessage( playerid, COLOR_WHITE, "�aid�jas nes�di kal�jime." );
    }
    return 1;
}
CMD:masked( playerid, params[ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 1 )
    {
        #pragma unused params
        new string[ 64 ];
        SendClientMessage( playerid, COLOR_GREEN2, "_____�aid�jai �iuo metu u�sid�j� kaukes_____");
        foreach(Player,playa)
        {
            if ( pInfo[ playa ][ pMask ] == 0 )
            {
                format( string, sizeof(string), "ID: %d, MySQL ID: %d %s", playa, pInfo[ playa ][ pMySQLID ], GetName( playa ) );
                SendClientMessage( playerid, COLOR_WHITE, string );
            }
        }
    }
    return 1;
}

CMD:gotonowhere(playerid)
{
    if(!GetPlayerAdminLevel(playerid) && !IsPlayerAdmin(playerid))
        return 0;

    static LastUsed[ MAX_PLAYERS ];
    new timestamp = gettime();
    if(timestamp - LastUsed[ playerid ] < 5)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "�i� komand� galima naudoti tik kas penkias sekundes");

    LastUsed[ playerid ] = timestamp;

    new Float:x = random(4000) + -2000,
        Float:y = random(4000) + -2000,
        Float:z;

    MapAndreas_FindZ_For2DCoord(x, y, z);

    SetPlayerPos(playerid, x, y, z);
    SetPlayerVirtualWorld(playerid, 0);
    SetPlayerInterior(playerid, 0);
    SendClientMessage(playerid, COLOR_NEWS, "S�kmingai persik�l�te ka�kur.");
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
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, tokio namo n�ra.");
    else 
    {
        GetHouseEntrancePos(houseindex, x, y, z);
        SetPlayerPos(playerid, x, y, z);
        SetPlayerInterior(playerid, GetHouseEntranceInteriorID(houseindex));
        SetPlayerVirtualWorld(playerid, GetHouseEntranceVirtualWorld(houseindex));
        SendClientMessage(playerid, COLOR_WHITE, "[AdmCmd] Persikelet� � nurodyt� viet�: gyvenamasis namas");
    }
    return 1;
}
*/
/*
CMD:gotobiz(playerid, params[])
{
    if(GetPlayerAdminLevel(playerid) < 4)
        return 0;

    new bizindex, tmp[16];
    // Jeigu ne�ved� indekso arba �ved� daugiau ka�k� bet tai ne �odis "sqlid"
    if((sscanf(params, "is[16]", bizindex, tmp) || isnull(tmp) || strcmp(tmp, "sqlid", true)) && sscanf(params, "d", bizindex)) 
        SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /gotobiz [biznio id]");

    else if(!IsValidBusiness(bizindex) && (strcmp(tmp, "sqlid", true) || isnull(tmp)))
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, tokio verslo n�ra.");

    else if(!isnull(tmp) && !strcmp(tmp, "sqlid", true) && !IsValidBusinessSqlId(bizindex))
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, tokio verslo n�ra.");
    else 
    {
        new Float:x, Float:y, Float:z;
        if(!isnull(tmp) && !strcmp(tmp, "sqlid", true))
        {
            bizindex = GetBusinessIndex(bizindex);
        }

        GetBusinessEntrancePos(bizindex, x, y, z);
        SetPlayerPos(playerid, x, y, z);
        SetPlayerInterior(playerid, GetBusinessEntranceInteriorID(bizindex));
        SetPlayerVirtualWorld(playerid, GetBusinessEntranceVirtualWorld(bizindex));

        SendClientMessage(playerid, COLOR_WHITE, "[AdmCmd] Persikelet� � nurodyt� viet�: biznis/verslas");
    }
    return 1;
}
CMD:gotogarage(playerid, params[])
{
    if(!IsPlayerAdmin(playerid) && !GetPlayerAdminLevel(playerid))
        return 0;

    new index, string[60];

    if(sscanf(params, "i", index))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas /gotogarage [Gara�o ID]");

    if(!IsValidGarage(index))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, gara�o su tokiu ID n�ra.");

    new Float:x, Float:y, Float:z, Float:angle;

    new vehicleid = GetPlayerVehicleID(playerid);
    if(vehicleid)
    {
        GetGarageVehicleEntrancePos(index, x, y ,z, angle);
        SetVehicleZAngle(vehicleid, angle);
        SetVehiclePos(vehicleid, x, y ,z);
        LinkVehicleToInterior(vehicleid, GetGarageEntranceInteriorID(index));
        SetVehicleVirtualWorld(vehicleid, GetGarageEntranceVirtualWorld(index));
        foreach(new i : Player)
            if(IsPlayerInVehicle(i, vehicleid))
            {
                SetPlayerVirtualWorld(i, GetGarageEntranceVirtualWorld(index));
                SetPlayerInterior(i, GetGarageEntranceInteriorID(index));
            }
    }
    else 
    {
        GetGarageEntrancePos(index, x,y ,z);
        SetPlayerPos(playerid, x, y, z);
        Unfreeze[ playerid ] = 2;
        TogglePlayerControllable(playerid, false);
        SetPlayerVirtualWorld(playerid, GetGarageEntranceVirtualWorld(index));
        SetPlayerInterior(playerid, GetGarageEntranceInteriorID(index));
        SetPlayerFacingAngle(playerid, angle);
    }
    format(string, sizeof(string), "S�kmingai nusik�l�te prie gara�o kurio ID %d", index);
    SendClientMessage(playerid, COLOR_NEWS, string);
    return 1;
}*/

CMD:setweather( playerid, params[ ] )
{
    if(GetPlayerAdminLevel(playerid) >= 2)
    {
        new tmp,
            string[ 128 ];
        if ( sscanf( params, "d", tmp ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /setweather [oro id]");
        format          ( string, sizeof(string), "AdmWarn: Administratorius (%s) pakeit� serverio or�: ID %d", GetName( playerid ), tmp );
        SendAdminMessage( COLOR_ADM, string );
        SetWeather( tmp );
    }
    return 1;
}
/*
CMD:dtc( playerid, params[ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 2 )
    {
        #pragma unused params
        new car = GetNearestVehicle( playerid, 5.0 );
        if ( car != INVALID_VEHICLE_ID )
        {
            if ( cInfo[ car ][ cOwner ] > 0 )
            {
                new panels,doors,lights,tires, Float:CarHP;
                GetVehicleHealth( car, CarHP );
                
                if ( CarHP < 300 )
                    return SendClientMessage( playerid, GRAD, "Automobilis yra pernelyg daug sudau�ytas, tod�l negalite jo parkuoti. " );
                
                GetVehicleDamageStatus(car,panels,doors,lights,tires);
                format( cInfo[ car ][ cDamage ], 50, "%d/%d/%d/%d/%d/", panels, doors, lights, tires, floatround( CarHP) );
                cInfo[ car ][ cVirtWorld ] = GetVehicleVirtualWorld( car );

                cInfo[car][cVehID] = 0;
                DestroyVehicle(car);
                
                new carowner = GetCarOwner( car );
                if ( IsPlayerConnected( carowner ) )
                    pInfo[ carowner ][ pCarGet ] --;

                SaveCar(car);
                nullVehicle( car );
                SendClientMessage( playerid, COLOR_WHITE, "Tr. priemon� priverstinai priparkuota.");
                return 1;
            }
        }
        else
            SendClientMessage( playerid, COLOR_WHITE, "J�s turite stov�ti/b�ti �alia tr. priemon�s nor�dami j� atstatyti � pradin� viet�.");
    }
    return 1;
}*/
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
            SendClientMessage( playerid, COLOR_WHITE, "Tr. priemon� gr��inta � savo pradin� atsiradimo viet�. Degalai atstatyti.");
            return 1;
        }
        else
            SendClientMessage( playerid, COLOR_WHITE, "J�s turite stov�ti/b�ti �alia tr. priemon�s nor�dami j� atstatyti � pradin� viet�.");
    }
    return 1;
}
/*
CMD:rc(playerid, params[])
{
    if(GetPlayerAdminLevel(playerid) < 1)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s neesate administratorius.");
    
    new vehicleid;
    if(sscanf(params, "i", vehicleid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /rc [ ma�inos ID ] ");

    if(!IsValidVehicle(vehicleid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Su tokiu ID ma�ina neegzistuoja. ");

    if(IsVehicleUsed(vehicleid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Transporto priemon� n�ra tu��ia!");

    SetVehicleToRespawn(vehicleid);
    SetVehicleVirtualWorld(vehicleid, 0);
    return 1;
}
CMD:rjc( playerid, params[ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 1 )
    {
        new string[ 126 ],
            frakcija;
        if ( sscanf( params, "d", frakcija ) )
        {
            SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /rjc [DARBO ID] " );
            return 1;
        }
        if ( frakcija < 0 || frakcija >= MAX_JOBS ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: darbo numeris yra per didelis. " );
        new bool:Used[ MAX_VEHICLES ] = { false, ... };
        foreach(Player,i)
        {
            if ( GetPlayerState( i ) == PLAYER_STATE_DRIVER )
            Used[ GetPlayerVehicleID( i ) ] = true;
        }
        foreach(Vehicles, veh )
        {
            if ( sVehicles[ veh ][ Job ] == frakcija )
            {
                if ( Used[ veh ] == false )
                {
                    SetVehicleToRespawn( veh );
                    SetVehicleVirtualWorld( veh, 0 );
                }
            }
        }
        format                ( string, 126, "AdmCmd buvo atstatytos visos darbo %s tr. priemon�s. ", pJobs[ frakcija ][ Name ] );
        SendClientMessageToAll( COLOR_LIGHTRED, string);
    }
    return 1;
}
CMD:rfc( playerid, params[ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 2 )
    {
        new string[ 126 ],
            frakcija;
        if ( sscanf( params, "d", frakcija ) )
        {
            SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /rfc [ frakcija ] " );
            return 1;
        }
        if ( frakcija < 0 || frakcija >= sizeof( fInfo ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: frakcijos numeris yra per didelis. " );
        if ( fInfo[ frakcija ][ fID ] == 0 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: tokia frakcija neegzistuoja. " );
        new bool:Used[ MAX_VEHICLES ] = { false, ... };
        foreach(Player,i)
        {
            if ( GetPlayerState( i ) == PLAYER_STATE_DRIVER )
            Used[ GetPlayerVehicleID( i ) ] = true;
        }
        foreach(Vehicles, veh )
        {
            if ( sVehicles[ veh ][ Faction ] == FactionMySQLID( frakcija ) )
            {
                if ( Used[ veh ] == false )
                {
                    SetVehicleToRespawn( veh );
                    SetVehicleVirtualWorld( veh, 0 );
                }
            }
        }
        format                ( string, 126, "AdmCmd buvo atstatytos visos frakcijos %s tr. priemon�s. ", fInfo[ frakcija ][ fName ] );
        SendClientMessageToAll( COLOR_LIGHTRED, string);
    }
    return 1;
}*/


/*
CMD:gotocar( playerid, params[ ] )
{
    new
        id,
        Float:Kords[ 3 ];
        
    if(GetPlayerAdminLevel(playerid) >= 2)
    {
        if ( sscanf( params, "d", id ) )
        {
            SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /gotocar [Automobilis]" );
            return 1;
        }
        GetVehiclePos(id,Kords[0],Kords[1],Kords[2]);
        SetPlayerPos(playerid,Kords[0],Kords[1],Kords[2]+2);
        SetPlayerVirtualWorld( playerid, GetVehicleVirtualWorld( id ) );
        SendClientMessage(playerid,GRAD,"[AdmCmd] Persikel�te � nurodyt� viet�: tr. priemon�");
        return 1;
    }
    return 1;
}*/
CMD:mute( playerid, params[ ] )
{
    new
        giveplayerid,
        string[ 126 ];
    if(GetPlayerAdminLevel(playerid) >= 2)
    {
        if ( sscanf( params, "u", giveplayerid ) )
        {
            SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /unmute [�aid�joID]" );
            return 1;
        }
        if(!IsPlayerConnected( giveplayerid )) return SendClientMessage( playerid, GRAD,"�aid�jas norimu ID neprisijung�s!");

        if( Mute[ giveplayerid ] )
        {
            format          ( string, 126, "AdmWarn: Administratorius (%s) leido kalb�ti (/unmute) veik�jui (%s) ",GetName(playerid), GetName( giveplayerid ) );
            SendAdminMessage( COLOR_ADM, string );

            Mute[ giveplayerid ] = false;
        }
        else
        {
            format          ( string, 126, "AdmWarn: Administratorius (%s) u�draud� kalb�ti (/mute) veik�jui (%s) ",GetName(playerid), GetName( giveplayerid ) );
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
            SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /setarmour [�aid�joID][armor]" );
            return 1;
        }
        if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
        SetPlayerArmour ( giveplayerid, Armor );
        format          ( string, 126 ,"AdmWarn: Administratorius (%s) nustat� veik�jui (%s) �arv� lygi: %d",GetName(playerid),GetName(giveplayerid),Armor);
        SendAdminMessage( COLOR_ADM, string );
        return 1;
    }
    return 1;
}
CMD:sethp( playerid, params[ ] )
{
    new
        giveplayerid,
        HP,
        string[ 126 ];
    if(GetPlayerAdminLevel(playerid) >= 3)
    {
        if ( sscanf( params, "ud", giveplayerid, HP ) )
        {
            SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /sethp [�aid�joID][hp]" );
            return 1;
        }
        if(!IsPlayerConnected(giveplayerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
        if ( IsPlayerNPC( giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /sethp [�aid�joID][hp]");
        SetPlayerHealth ( giveplayerid, HP );
        format          ( string, 126 ,"AdmWarn: Administratorius (%s) pakeit� veik�jo (%s) gyvybi� skai�i�: %d",GetName(playerid),GetName(giveplayerid),HP);
        SendAdminMessage( COLOR_ADM, string );
        return 1;
    }
    return 1;
}
CMD:rac(playerid)
{
    if(GetPlayerAdminLevel(playerid) < 2)
        return 0;

    foreach(Vehicles,veh)
    {
        if (!IsVehicleUsed(veh) && VGaraze[ veh ] == false)
        {
            SetVehicleToRespawn( veh );
            if ( cInfo[ veh ][ cVirtWorld ] > 0 && cInfo[ veh ][ cOwner ] > 0 )
                SetVehicleVirtualWorld( veh, cInfo[ veh ][ cVirtWorld ] );
            else SetVehicleVirtualWorld( veh, 0 );
        }
    }
    SendClientMessageToAll(COLOR_LIGHTRED, "AdmCmd buvo atstatytos visos nenaudojamos serverio tr. priemon�s");
    return 1;
}

stock IsVehicleUsed(vehicleid)
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
            SendClientMessage( playerid, COLOR_WHITE, "Dabar nebematysite serverio �aid�j� atliekam� nu�udymu." );
            SetPVarInt( playerid, "AP_KILLS", 1 );
            return 1;
        }
        else if ( GetPVarInt( playerid, "AP_KILLS" ) == 1 )
        {
            SendClientMessage( playerid, COLOR_WHITE, "Dabar matysite serverio �aid�j� atliekamus nu�udimus" );
            SetPVarInt( playerid, "AP_KILLS", 0 );
            return 1;
        }
    }
    return 1;
}
CMD:check( playerid, params[ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 1 )
    {
        new giveplayerid;
        if ( sscanf( params, "u", giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /check [�aid�jo id]");
        if ( !IsPlayerConnected(giveplayerid) )  return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje. ");
        ShowStats(playerid,giveplayerid);
    }
    return 1;
}

CMD:setint( playerid, params[ ] )
{
    if(GetPlayerAdminLevel(playerid) >= 1)
    {
        new giveplayerid,
            inter,
            string[ 50 ];
        if ( sscanf( params, "ud", giveplayerid, inter ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /setint [�aid�jo id][Interjeras] ");
        if ( !IsPlayerConnected(giveplayerid) )  return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje. ");
        SetPlayerInterior( giveplayerid, inter );
        format          ( string, 50, "Pakeitei %s jo interjera � %d ", GetName(giveplayerid), inter );
        SendClientMessage( playerid, COLOR_FADE1, string );
    }
    return 1;
}
CMD:setvw( playerid, params[ ] )
{
    if( GetPlayerAdminLevel(playerid) >= 1 )
    {
        new giveplayerid,
            inter,
            string[ 50 ];
        if ( sscanf( params, "ud", giveplayerid, inter ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /setvw [�aid�jo id][Virtualus Pasaulis] ");
        if ( !IsPlayerConnected(giveplayerid) )  return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje. ");
        SetPlayerVirtualWorld( giveplayerid, inter );
        format          ( string, 50, "Pakeitei %s jo virtualu pasauli � %d ", GetName(giveplayerid), inter );
        SendClientMessage( playerid, COLOR_FADE1, string );
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
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /spec [�aid�jo id] ");

    if(!IsPlayerConnected(giveplayerid)) 
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje. ");

    if(pInfo[ playerid ][ pFactionManager ] && !GetPlayerAdminLevel(playerid) &&  GetPlayerAdminLevel(giveplayerid) && AdminDuty[ giveplayerid ])
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite steb�ti administratoriaus darbe.");

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
CMD:intvw( playerid, params[ ] )
{
    if(GetPlayerAdminLevel(playerid) >= 1)
    {
        #pragma unused params
        new string[ 60 ];
        format           ( string, 60, " Jus� interioras: %d, Virtualus pasaulis: %d", GetPlayerInterior( playerid ), GetPlayerVirtualWorld( playerid ) );
        SendClientMessage( playerid, COLOR_WHITE, string );
    }
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
        return SendClientMessage(playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /frisk [�aid�jo id/dalis vardo]");

    if(!IsPlayerConnected(targetid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");

    if(IsPlayerInventoryEmpty(targetid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �aid�jas neturi nei vieno daikto.");


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
            format(string, sizeof(string)," Ginklas %s �ovini� %d ", wepname, ammo);
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
        if ( sscanf( params, "u", giveplayerid ) ) return SendClientMessage( playerid , COLOR_LIGHTRED, "Teisingas komandos naudojimas: /aproperty [�aid�jo id]" );
        if ( !IsPlayerConnected( giveplayerid ) )  return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");
        new
            zone[ MAX_ZONE_NAME ];

        SendClientMessage( playerid, COLOR_LIGHTRED, "________Turimi namai________");
        foreach(Houses,h)
        {
            if ( hInfo[h][hOwner] == pInfo[ giveplayerid ][ pMySQLID ] )
            {
                Get2DZone( hInfo[ h ][ hEnter ][ 0 ], hInfo[ h ][ hEnter ][ 1 ], zone, MAX_ZONE_NAME );
                format( string, 126, "Namas(ID:%d) Vert�: %d Vieta: %s", h, hInfo[ h ][ hPrice ], zone );
                SendClientMessage( playerid, COLOR_WHITE, string );
            }
        }
        SendClientMessage( playerid, COLOR_LIGHTRED, "________Turimi verslai________");
        for(new i = 0; i < GetBusinessCount(); i++)
        {
            if ( bInfo[ i ][ bOwner ] == pInfo[ giveplayerid ][ pMySQLID ] )
            {
                Get2DZone( bInfo[ i ][ bEnter ][ 0 ], bInfo[ i ][ bEnter ][ 1 ], zone, MAX_ZONE_NAME );
                format( string, 126, "Verlsas(ID:%d) Vert�: %d Vieta: %s", i, bInfo[ i ][ bPrice ], zone );
                SendClientMessage( playerid, COLOR_WHITE, string );
            }
        }
        SendClientMessage( playerid, COLOR_LIGHTRED, "________Turimi gara��i________");
        foreach(Garages,h)
        {
            if ( pInfo[ giveplayerid ][ pMySQLID ] == gInfo[ h ][ gOwner ] )
            {
                Get2DZone( gInfo[ h ][ gEntrance ][ 0 ], gInfo[ h ][ gEntrance ][ 1 ], zone, MAX_ZONE_NAME );
                format( string, 126, "Gara�as(ID:%d) Vert�: %d Vieta: %s", h, gInfo[ h ][ gPrice ], zone );
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
            format( string, 126, "Ma�ina(ID:%d): %s Numeriai: %s Serverio ID: %d", slot, vName, Numbers, spawned);
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
            SendClientMessage( playerid, COLOR_FADE1, "[AdmLvl 1] PERSIK�LIMAS: /gotols /gotofc /gotobb /gotopc /goto /gotomark /gotobiz /gotohouse /gotogarage /gotopos");
            SendClientMessage( playerid, COLOR_WHITE, "[AdmLvl 1] TR. PRIEMON�S: /getoldcar /rtc /rfc /rjc /rc");				
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
CMD:getoldcar( playerid, params[ ] )
{
    if ( GetPlayerAdminLevel(playerid) >= 1 )
    {
        new car;
        if ( sscanf( params, "d", car ) )
        {
            if ( OldCar[ playerid ] == 0 )
                return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /getoldcar [ automobilio id ] " );
            new Float: T[ 3 ];

            GetPlayerPos( playerid, T[ 0 ], T[ 1 ], T[ 2 ] );
            SetVehiclePos( OldCar[ playerid ], T[ 0 ], T[ 1 ], T[ 2 ] );
            SetVehicleVirtualWorld( OldCar[ playerid ], GetPlayerVirtualWorld(playerid) );
            return 1;
        }
        if(!IsValidVehicle(car))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, tokios transporto priemon�s n�ra.");
        new Float: T[ 3 ];

        GetPlayerPos( playerid, T[ 0 ], T[ 1 ], T[ 2 ] );
        SetVehiclePos( car, T[ 0 ], T[ 1 ], T[ 2 ] );
        SetVehicleVirtualWorld( car, GetPlayerVirtualWorld(playerid) );
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
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /giveitem [�aid�jo id/dalis vardo] [DaiktoID] [Kiekis(0 at�mimui)]");

    if(!IsPlayerConnected(giveplayerid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veik�jo ID negalimas, kadangi toks ID n�ra prisijung�s serveryje.");

    if(!IsValidItem(itemid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: blogas daikto ID..");

    if(amount < 0)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: minimalus kiekis 1. �vedus 0, daiktas bus atimtas.");


    if(!amount)
    {
        if(!IsItemInPlayerInventory(giveplayerid, itemid))
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �aid�jas tokio daikto neturi.");

        format(string, sizeof(string), "AdmWarn: Administratorius (%s) atem� (%s) i� veik�jo (%s)",GetName(playerid), GetItemName(itemid), GetName(giveplayerid));
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
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �aid�jo inventorius pilnas.");

        if(itemid == ITEM_PHONE)
        {
            new more = random(32) * 1000;
            pInfo[giveplayerid][pPhone] = 110000 + pInfo[ giveplayerid ][ pMySQLID ] + more;
        }
        format(string, sizeof(string), "AdmWarn: Administratorius (%s) suteik� (%s), kiekis (%d) veik�jui (%s)",GetName(playerid),GetItemName(itemid),amount,GetName(giveplayerid));
        SendAdminMessage(COLOR_ADM, string );
        format(string, sizeof(string), "Dav� daikt�: %s", GetItemName(itemid));
        AdminLog(pInfo[ playerid ][ pMySQLID ], pInfo[ giveplayerid ][ pMySQLID ], string );
        GivePlayerItem(giveplayerid, itemid, amount);
    }
    return 1;
}
*/


stock GetVehiclePlayerCount(vehicleid)
{
    new count;
    foreach(new i : Player)
        if(IsPlayerInVehicle(i, vehicleid))
            count++;
    return count;
}
stock HasVehicleDriver(vehicleid)
{
    foreach(new i : Player)
        if(IsPlayerInVehicle(i, vehicleid) && !GetPlayerVehicleSeat(i))
            return true;
    return false;
}
stock GetVehicleDriver(vehicleid)
{
    foreach(new i : Player)
        if(IsPlayerInVehicle(i, vehicleid) && !GetPlayerVehicleSeat(i))
            return i;
    return INVALID_PLAYER_ID;
}

stock AcesToSVehicle( vehicleid, playerid )
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
            format( string, sizeof(string), "D�mesio, J�s� tr. priemon� i� J�s� at�m� veik�jas: %s (ID:%d)", GetName( playerid ), playerid );
            SendClientMessage( ocupy, COLOR_WHITE, string );
        }
    }
    return 1;
}
stock IsVehicleOcupied( vehicleid )
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
    SendClientMessage(playerid, GRAD,"Nesp�jote gr��ti � transporto priemon�, tod�l misija buvo baigta.");
    return 1;
}*/

stock isLicCar( vehicleid )
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
            return KickPlayer( "AC", playerid, "�lipo � u�rakinta tr. priemone." );


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
            SendClientMessage( playerid, COLOR_LIGHTRED2,"** KAINORA�TIS: Automobilio - 1200$ | Motociklo - 900$ | Laivybos - 300$ |Skraidymo - 5600$"),				
            SendClientMessage( playerid, COLOR_WHITE," ** Nor�dami prad�ti egzamin� licencijai �gyti ra�ykite komand�: /takelesson ");
            return 1;
        }
        
        */
        /*if(sVehicles[ veh ][ Job ] == JOB_TRASH)
        {
            KillTimer(TrashTimer[ playerid ]);
            if(TrashMission[ playerid]  == TRASH_MISSION_NONE)
            {
                new string[128];
                format(string,sizeof(string),"~n~~n~~n~Rinkite siuksles i� pazymet� tasku~n~Naudokite /takegarbage ju paemimui~n~Siame sunkvezimyje yra %d maisai",TrashBagsInTrashVehicle[ veh ]);
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
            format           ( string, 126, "J�s nesate sumok�j�s automobilio baudos, kurios suma lygi $%d", cInfo[ veh ][ cTicket ] );
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
            format      ( string, 126, "* %s i�lipdamas atsiseg� saugos dir��.", GetPlayerNameEx( playerid ) );
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
            format( string, 126, "* %s atsisega saugos dir�� ir i�lipa i� tr. priemon�s.", GetPlayerNameEx( playerid ) );
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
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite s�sti �ia, kadangi �ia prid�t� krovini�.");
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
stock IsVehicleSeatUsedForCargo(vehicleid, seat)
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
            cmd_ame(playerid, "pasuka automobilio raktel� ir i�jungia varikl�.");
            SendClientMessage(playerid, GRAD, "Baig�te misij�. Jums prie algos buvo prid�ti " #TRASH_MISSION_COMPLETED_BONUS "$ Nor�dami prad�ti dar vien� misij�: /startmission");
            
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
                                format( string, 256, "\tVir�ijote greit� %d kart� (-us)\n", mistakes );
                            if ( VehHealth < 900 )
                                format( string, 256, "%s\tAutomobilis buvo apgadinta\n",string );
                            if ( strlen( string ) > 0 )
                            {
                                format( string, 256, "{FFFFFF}J�s padar�te �iais klaidas:\n%s\nTod�l egzaminas skubiai nutraukiamas. ", string );
                                ShowPlayerDialog( playerid, 9999, DIALOG_STYLE_MSGBOX , "Egzaminas nei�laikytas", string, "I�jungti", "");
                                SetVehicleToRespawn( veh );
                                Checkpoint[ playerid ] = CHECKPOINT_NONE;
                                SetPVarInt             ( playerid, "LIC_TIME", 0 );
                                DisablePlayerCheckpoint( playerid );
                                return 1;
                            }
                            pInfo[ playerid ][ pLicCar ] = 1;
                            GivePlayerItem(playerid, ITEM_TEORIJA, -1);
							SendClientMessage(playerid, COLOR_LIGHTRED2,"** Los Santos Driver License Center "),		
							SendClientMessage(playerid, COLOR_WHITE," ** J�s s�kmingai i�silaik�te vairavimo test� ir �gijote licencija vairuoti automobil�. ");
                            GivePlayerMoney( playerid, -1200 ); //Teisi� kain�.
                            PlayerPlaySound( playerid, 1057, 0.0, 0.0, 0.0);
                            SetPVarInt     ( playerid, "LIC_TIME", 0 );
                            DisablePlayerCheckpoint( playerid );
                            Checkpoint[ playerid ] = CHECKPOINT_NONE;
                            SetVehicleToRespawn( veh );
                            return 1;
                        }
                        else
                        {
                            SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai pinig�, kad gal�tum�te laikyti egzamin�. Egzaminas kainuoj� 1200$ " );
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
                                format( string, 256, "\tVir�ijote greit� %d kart� (-us)\n", mistakes );
                            if ( VehHealth < 900 )
                                format( string, 256, "%s\tMotociklas buvo apgadintas\n",string );
                            if ( strlen( string ) > 0 )
                            {
                                format( string, 256, "{FFFFFF}J�s padar�te �iais klaidas:\n%s\nTod�l egzaminas skubiai nutraukiamas.", string );
                                ShowPlayerDialog( playerid, 9999, DIALOG_STYLE_MSGBOX , "Vairavimo testas nei�laikytas", string, "I�jungti", "");
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
							SendClientMessage( playerid, COLOR_WHITE," ** J�s s�kmingai i�silaik�te motociklo vairavimo test� ir �gijote licencija vairuoti motocikla. ");
                            GivePlayerMoney( playerid, -900 );//Motociklo teisi� kain�
                            PlayerPlaySound( playerid, 1057, 0.0, 0.0, 0.0);
                            SetPVarInt     ( playerid, "LIC_TIME", 0 );
                            DisablePlayerCheckpoint( playerid );
                            Checkpoint[ playerid ] = CHECKPOINT_NONE;
                            return 1;
                        }
                        else
                        {
                            SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai pinig�, kad gal�tum�te laikyti egzamin�. Egzaminas kainuoj� 900$ " );
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
                                format( string, 256, "{FFFFFF}J�s padar�te �iais klaidas:\n%s\nTod�l egzaminas skubiai nutraukiamas.", string );
                                ShowPlayerDialog( playerid, 9999, DIALOG_STYLE_MSGBOX , "Laivybos egzaminas nutrauktas", string, "I�jungti", "");
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
							SendClientMessage( playerid, COLOR_WHITE," ** S�kmingai i�silaik�te laivybos egzamin� ir �gijote licencija plaukti/valdyti bet kok� laiv�. ");
                            GivePlayerMoney( playerid, -300 );
                            PlayerPlaySound( playerid, 1057, 0.0, 0.0, 0.0);
                            SetPVarInt     ( playerid, "LIC_TIME", 0 );
                            DisablePlayerCheckpoint( playerid );
                            Checkpoint[ playerid ] = CHECKPOINT_NONE;
                            return 1;
                        }
                        else
                        {
                            SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai pinig�, kad gal�tum�te laikyti egzamin�. Egzaminas kainuoj� 300$ " );
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
                                format( string, 256, "%s\tL�ktuvas buvo apdau�ytas\n ",string );
                            if ( strlen( string ) > 0 )
                            {
                                format( string, 256, "{FFFFFF}J�s padar�te �iais klaidas:\n%s\nTod�l nei�laik�te skraidymo testo. ", string );
                                ShowPlayerDialog( playerid, 9999, DIALOG_STYLE_MSGBOX , "Skraidymo testas nei�laikytas", string, "I�jungti", "");
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
							SendClientMessage( playerid, COLOR_WHITE," ** J�s s�kmingai i�silaik�te vairavimo test� ir �gijote licencija vairuoti. ");
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
                            SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai pinig�, kad gal�tum�te laikyti egzamin�. Egzaminas kainuoj� 5600$ " );
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
//    KickPlayer( "AC", playerid, "Tuninguoja automobil�, tuningavimo salone." );
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
/*
public OnPlayerEnterDynamicArea(playerid, areaid)
{
    new plstate = GetPVarInt( playerid, "PLAYER_STATE" );
    foreach(Audio3D,ip)
    {
        if ( aInfo[ ip ][ aArea ] == areaid &&
             aInfo[ ip ][ aObjekt ] > 0 &&
             plstate == PLAYER_STATE_ONFOOT )
            return Set3DAudioForPlayer( playerid, aInfo[ ip ][ aStation ], ip);
    }
    return 1;
}
*/
public OnPlayerLeaveDynamicArea(playerid, areaid)
{
    return 1;
}
/*
FUNKCIJA:Mechaniku( playerid, left )
{
    new Keys,
        ud,
        lr,
        Car = GetNearestVehicle( playerid, 5.0 );
    GetPlayerKeys( playerid, Keys, ud, lr );
    if ( Car != INVALID_VEHICLE_ID && GetPlayerWeapon( playerid ) == 41)
    {
        new string[ 56 ],
            give = GetPVarInt( playerid, "OFFER2_ID" ),
            money = GetPVarInt( playerid, "OFFER2_COAST" );
        if ( !IsPlayerConnected( give ) )
            return 1;

        if ( Keys == KEY_FIRE )
        {
            format( string, 56, "PERDAZOMA %d", left );
            GameTextForPlayer( playerid, string, 500, 3 );
            left --;
        }
        else
        {
            format( string, 56, "DAR LIKO %d", left );
            GameTextForPlayer( playerid, string, 500, 3 );
        }
        if ( left > 0 )
                SetTimerEx("Mechaniku", 500, false, "dd", playerid, left );
        else if ( left == 0 )
        {
            ChangeVehicleColor( Car, GetPVarInt( playerid, "COLOR_1" ),GetPVarInt( playerid, "COLOR_2" ) );
            SendClientMessage( playerid, COLOR_WHITE, " ** Automobilis s�kmingai perda�ytas norima spalva " );
            RemovePlayerWeapon( playerid, 41 );

            GivePlayerMoney( playerid, money );
            GivePlayerMoney( give, -money );

            SendClientMessage( give, COLOR_WHITE, " ** J�s� automobilis buvo s�kmingai perda�ytas. " );

            DeletePVar( playerid, "OFFER2_ID" );
            DeletePVar( playerid, "OFFER2_COAST" );
            return 1;
        }
    }
    else
    {
        RemovePlayerWeapon( playerid, 41 );
        return true;
    }
    return 1;
}
*/
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
   /* if(IsKeyJustDown(KEY_FIRE,newkeys,oldkeys))
    {
        new veh = GetPlayerVehicleID(playerid),
            string[ 126 ];

        if ( GetPVarInt( playerid, "MOKESTIS" ) >= BENZO_KAINA )
        {
            StopFillUp(playerid);
        }
        
        new
            bool:found = false;
                
        foreach(Player, x)
        {
            if(GetPlayerAdminLevel(x) >= 1 && AdminDuty[ x ])
            {
                found = true;
                break;
            }
        }
        if( !isLicCar( veh ) && Engine[veh] == false && VehicleHasEngine(GetVehicleModel(veh)) && GetPlayerState(playerid) == PLAYER_STATE_DRIVER)
        {
            if(cInfo[ veh ][ cOwner ] > 0 && CheckCarKeys(playerid,veh) == 0)
            {
                if ( pInfo[ playerid ][ pJob ] != JOB_JACKER )
                    return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti �io veiksmo nedirbdami automobiliu vagimi." );
                if( !found )
                    return true;
                    
                if(IsItemInPlayerInventory(playerid, ITEM_TOLKIT))
                {
                    if(cInfo[veh][cLockType] == 0) StartTimer(playerid,60,6);
                    else StartTimer(playerid,120*cInfo[veh][cLockType],6);
                    format(string,126,"** %s i� �ranki� d��ut�s i�sitraukia reples, atsuktuv� ir bando ardyti spinel�, kad u�vestu automobil�.",GetPlayerNameEx(playerid));
                    ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
                    CJLog( pInfo[ playerid ][ pMySQLID ], cInfo[veh][cID], "Bando u�vesti tr. priemon� vogdamas" );
                    if(cInfo[veh][cAlarm] == 1 || cInfo[veh][cAlarm] == 2)
                    {
                        format(string,126,"** Pypsi tr. priemon�s signalizacija (( %s ))",cInfo[veh][cName]);
                        ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
                        VehicleAlarm(veh, 1);
                        return 1;
                    }
                    else if(cInfo[veh][cAlarm] == 3)
                    {
                        new zone[30];
                        GetPlayer2DZone(playerid, zone, 30);
                        format(string,126,"** Pypsi tr. priemon�s signalizacija (( %s ))",cInfo[veh][cName]);
                        ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
                        SendTeamMessage(1, COLOR_LIGHTRED, "|________________�vykio prane�imas________________|");
                        SendTeamMessage(1, COLOR_WHITE, "|Dispe�erin�: Automobilio signalizacija prane�a apie �silau�im�.");
                        format(string, 126, "|Vieta: Automobilio GPS imtuvas prane�a, kad automobilis yra rajone %s",zone);
                        SendTeamMessage(1, COLOR_WHITE, string);
                        VehicleAlarm(veh, 1);
                        return 1;
                    }
                    else if(cInfo[veh][cAlarm] == 4)
                    {
                        new zone[30],
                            CarOwner = GetCarOwner(veh);
                        GetPlayer2DZone(playerid, zone, 30);
                        format(string,126,"** Pypsi tr. priemon�s signalizacija (( %s ))",cInfo[veh][cName]);
                        ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
                        SendTeamMessage(1, COLOR_LIGHTRED, "|________________�vykio prane�imas________________|");
                        SendTeamMessage(1, COLOR_WHITE, "|Dispe�erin�: Automobilio signalizacija prane�a apie �silau�im�.");
                        format(string, 126, "|Vieta: Automobilio GPS imtuvas prane�a, kad automobilis yra rajone %s",zone);
                        SendTeamMessage(1, COLOR_WHITE, string);
                        if(!IsPlayerConnected(CarOwner)) return 1;
                        SendClientMessage(CarOwner, COLOR_WHITE, "SMS: � J�s� automobil� bando ka�kas �silau�ti, siunt�jas: J�su Automobilis");
                        SetVehicleParamsForPlayer(veh,CarOwner,1,cInfo[veh][cLock]);
                        PlayerPlaySound(CarOwner, 1052, 0.0, 0.0, 0.0);
                        VehicleAlarm(veh, 1);
                        return 1;
                    }
                }
                else return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite �ranki� d��ut�s, kad gal�tum�te prad�ti vogti. ");
            }
        }
        
           if(StartingEngine[playerid] == true || Laikas[playerid] > 0) return 1;

            // Vienintelis b�das u�kurti �iuk�liave�� yra /startmission, NEBENT misija jau pradeta.
            if(sVehicles[ veh ][ Job ] == JOB_TRASH && TrashMission[ playerid ] == TRASH_MISSION_NONE)
                return 1;
            // Kol transporto priemon�s ar ju priekabos kraunamos, j� u�kurti negalima.
            if(IsVehicleLoaded [ veh ] || (IsValidVehicle(GetVehicleTrailer(veh)) && IsVehicleLoaded[ GetVehicleTrailer(veh) ]))
                return SendClientMessage(playerid, GRAD, "J�s� automobilis kraunamas. Niekur va�iuot negalite.");

            StartingEngine[playerid] = true;
            cmd_ame(playerid, "pasuka automobilio raktel� ir bando u�vesti varikl�.");
            if( cInfo[ veh ][ cDuzimai   ] == 0 )
                SetTimerEx( "StartEngine", 1500, false, "dd", playerid, veh );
            if( cInfo[ veh ][ cDuzimai   ] >= 1 )
                SetTimerEx( "StartEngine", 1600, false, "dd", playerid, veh );
            if( cInfo[ veh ][ cDuzimai   ] > 4 )
                SetTimerEx( "StartEngine", 2000, false, "dd", playerid, veh );
            if( cInfo[ veh ][ cDuzimai   ] > 6 )
                SetTimerEx( "StartEngine", 2400, false, "dd", playerid, veh );
            if( cInfo[ veh ][ cDuzimai   ] > 8 )
                SetTimerEx( "StartEngine", 3000, false, "dd", playerid, veh );
            return 1;
        }
        else if( !isLicCar( veh ) && Engine[veh] == true && VehicleHasEngine(GetVehicleModel(veh)) && GetPlayerState( playerid ) == PLAYER_STATE_DRIVER)
        {
            if( StartingEngine[playerid] == true || Laikas[playerid] > 0 ) return 1;
            Engine[veh] = false;
            cmd_ame(playerid, "pasuka automobilio raktel� ir i�jungia varikl�.");
            VehicleEngine(veh, 0 );
            return 1;
        }
        
    }
    */
    return 1;
    
}

FUNKCIJA:StartEngine(playerid,veh)
{
    if(!IsPlayerInAnyVehicle(playerid)) return StartingEngine[playerid] = false;
    if(GetPlayerVehicleID(playerid) != veh) return StartingEngine[playerid] = false;
    if(StartingEngine[playerid] == false) return 1;
    new CarDuzimai = cInfo[veh][cDuzimai],
        RandomStart = random(CarDuzimai + 1),
        Float:Damage;
    GetVehicleHealth( veh, Damage );

    if( cInfo[ veh ][ cFuel ] <= 0 || cInfo[ veh ][ cInsurance ] < 0 || Damage < 400)
    {
        SendClientMessage( playerid, COLOR_RED, "U�vedimas nepavyko!" );
        StartingEngine[playerid] = false;
        VehicleEngine(veh, 0 );
        return 1;
    }
    switch(RandomStart)
    {
        case 0 .. 5:
        {
            SendClientMessage( playerid, COLOR_WHITE, "Tr. priemon�s variklis s�kmingai u�vestas." );
            VehicleEngine(veh, 1 );
            StartingEngine[playerid] = false;
            Engine[veh] = true;
            return 1;
        }
        case 6 .. 100:
        {
            SendClientMessage( playerid, COLOR_RED, "U�vedimas nepavyko!" );
            StartingEngine[playerid] = false;
            VehicleEngine(veh, 0 );
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

    // Tazeris zalos daryti neturi.
    if( GetPVarInt( issuerid, "TAZER_MODE" )) 
    {
        SetPlayerHealth(playerid, stat[ 0 ] + amount);
        return 1;
    }

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

stock IsMeleeWeapon(weaponid)
{
    switch(weaponid)
    {
        case 0 ..15: return true;
    }
    return false;
}
stock IsDriveByWeapon(weaponid)
{
    // Ginklai su kuriais leid�iamas drive-by
	switch(weaponid)
	{
		case 25, 26, 27, 28, 29, 30, 31, 32: return true;
		default: return false;
	}
	return false;
}

public OnPlayerWeaponShot(playerid, weaponid, hittype, hitid, Float:fX, Float:fY, Float:fZ)
{
    printf("OnPlayerWeaponShot");
//    if(!IsPlayerWeaponInMemory(playerid, weaponid))
 //   {
    //    SendClientMessage(playerid, COLOR_LIGHTRED, "-.- Naujas AC nor�jo k� tik tave u�blokuoti, �iaip ne taip i�gelb�jau...");
       //new string[128];
        //format(string, sizeof(string),"Weapons.p : OnPlayerWeaponShot(%d, %d, %d, %d, %f, %f, %f)", playerid, weaponid, hittype, hitid, fX, fY, fZ);
        //ACTestLog(string);
   // }
    return 1;
}

public OnPlayerGiveDamage(playerid, damagedid, Float:amount, weaponid, bodypart)
{
    #if defined DEBUG 
        printf("[debug] OnPlayerGiveDamage(%d, %d, %f, %d, %d)", playerid, damagedid, amount, weaponid, bodypart);
    #endif
    if ( damagedid == INVALID_PLAYER_ID ) return 1;
    new
        ShooterWep = weaponid;
    /*if( PlayerFaction( playerid ) == 1 )
    {
        switch( ShooterWep )
        {
            case 25:
            {
                ApplyAnimation( damagedid, "PED", "KO_skid_front", 4.1, 0, 1, 1, 1, 0);
                ApplyAnimation( damagedid, "PED", "KO_skid_front", 4.1, 0, 1, 1, 1, 0);
            }
        }
    }
    */
    /*
    if ( GetPVarInt( playerid, "TAZER_MODE" ) == 1 )
    {
        if ( ShooterWep != 23 ) return SetPVarInt( playerid, "TAZER_MODE", 0 );

        if( !PlayerToPlayer   ( 7.0, playerid, damagedid ) )
            return 1;

        new string[ 126 ],
            name[ 24 ];
        GetPlayerName( damagedid, name, 24 );

        SendClientMessage( damagedid, COLOR_WHITE, "* Tave pa�ov� elektros �oku." );
        format           ( string, 126 , "* Tu pa�ovei elektros �oku %s, jis gul�s apie 30 sekund�iu.", name );
        SendClientMessage( playerid, COLOR_WHITE, string);
        format           ( string, 126 , "* %s i��auna elektros �ok� nutaik�s � %s ir nukre�ia su didele �tampa.", GetPlayerNameEx( playerid ) , name);
        ProxDetector     ( 20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
        ShowInfoText     ( damagedid, "~r~Nukrestas soko!", 2500);
        TogglePlayerControllable( damagedid, 0 );
        ApplyAnimation( damagedid, "CRACK", "crckdeth2", 4.0, 1, 1, 1, 1, 1);
        SetTimerEx( "TazerTime", 30000, false, "i", damagedid );

        RemovePlayerWeapon( playerid, 23 );
        return 1;
    }*/
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
        SendClientMessage(playerid, COLOR_NEWS, "Nuo  �iol nebematysite Test AC �inu�i�.");
    else 
        SendClientMessage(playerid, COLOR_NEWS, "V�l matysite Test AC �inutes.");
    ShowACTestMsg[ playerid ] = !ShowACTestMsg[ playerid ];
    return 1;
}
/*
stock OnPlayerChangeWeapon(playerid, oldweapon, newweapon)
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
                    format(string ,sizeof(string),"OnPlayerChangeWeapon. �aid�jas %s bande panaudoti ginkla neregistruota DB(%d). ANTRAS KARTAS.", GetName(playerid), newweapon);
                    ACTestLog(string);

                    GetWeaponName(newweapon, string, sizeof(string));
                    format(string, sizeof(string), "[AntiCheat testas]�aid�jas %s GALIMAI(ne 100% tikslu) cheatino ginkl� %s.(�ias �inutes galima i�jungti su /togacmsg)", GetName(playerid), string);
                    foreach(new i : Player) 
                    {
                        if(IsPlayerAdmin(i) || pInfo[ i ][ pAdmin ] && ShowACTestMsg[ i ])
                            SendClientMessage(i, 0xff76a1d3, string);
                    }
                }
                else 
                {
                    format(string ,sizeof(string),"OnPlayerChangeWeapon. �aid�jas %s bande panaudoti ginkla neregistruota DB(%d)", GetName(playerid), newweapon);
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
                SendClientMessage(playerid, COLOR_LIGHTRED, "Ech, naujas AC dabar tave b�t� u�blokav�s... ");
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
                    SendClientMessage(playerid, COLOR_PURPLE, "Ha, u�banino tave. Naujas AC to neb�t� padar�s :P");
                    DidNewACBan[ playerid ] =false;
                }
                GetPlayerName(playerid, string ,sizeof(string));
                format(string, sizeof(string), "�aid�jas %s buvo u�blokuotas u� ginklo %d cheat. Params: oldweapon:%d newweapon:%d. Old wep data: ID:%d Ammo:%d", 
                    string, newweapon, oldweapon, newweapon, weapons[ 0 ], weapons[ 1 ]);
                ACTestLog(string);
            }
            
        }


        // Jei senasis ginklas buvo u�d�tas kaip objektas, reikia j� u�d�ti.
        if(oldweapon)
        {
            for(new i = 0; i < MAX_PLAYER_ATTACHED_WEAPONS; i++)
            {
                if(PlayerAttachedWeapons[ playerid ][ i ][ WeaponId ] == oldweapon)
                    SetPlayerAttachedObject(playerid, PlayerAttachedWeapons[ playerid ][ i ][ ObjectSlot ], GunObjects[ oldweapon ], 1,  0.199999, -0.139999, 0.030000, 0.500007, -115.000000, 0.000000, 1.000000, 1.000000, 1.000000);
            }
        }
        // Jei naujasis ginklas u�d�tas kaip objektas, paslepiam objekt� nes i�sitrauk� real� ginkl�.
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


stock IsPlayerSpectatingPlayer(playerid, spectatee)
{
    if(PlayerSpectatedPlayer[ playerid ] == spectatee)
        return true;
    else 
        return false;
}
stock IsPlayerHaveManyGuns( playerid, wepid )
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


public OnPlayerStreamIn(playerid, forplayerid)
{
    ShowPlayerNameTagForPlayer(forplayerid, playerid, pInfo[playerid][pMask]);
    return 1;
}

public OnPlayerStreamOut(playerid, forplayerid)
{
    return 1;
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

public OnPlayerModelSelectionEx(playerid, response, extraid, modelid)
{
    return 1;
}



public OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
    printf("[debug] OnDialogResponse(%s, %d, %d, %d, %s)", GetName(playerid), dialogid, response, listitem, inputtext);
    new string[4096];
    /*
    if(dialogid == 5)
    {
        new veh = GetPlayerVehicleID( playerid );
        if( response == 1 )
        {
            if ( GetPlayerMoney(playerid) < GetPVarInt( playerid, "MOKESTIS" ) )
            {
                SendClientMessage( playerid, COLOR_FADE2, "{FF6347}Persp�jimas: Neturite pakankamai pinig�, kad sumok�tum�te u� degalus." );
                cInfo[ veh ][ cFuel ] = GetPVarInt( playerid, "FILLED" );
                DeletePVar( playerid, "MOKESTIS" );
                return 1;
            }
            GivePlayerMoney( playerid, - GetPVarInt( playerid, "MOKESTIS" ) );
            ShowInfoText( playerid, "~w~Benzino bakas uzpildytas", 1000 );
            SaveCar( veh );
            DeletePVar( playerid, "MOKESTIS" );
            return 1;
        }
        else if( response == 0 )
        {
            if( GetPlayerBankMoney(playerid) < GetPVarInt( playerid, "MOKESTIS" ) )
            {
                SendClientMessage( playerid, COLOR_FADE2, "{FF6347}Persp�jimas: Neturite pakankamai pinig�, kad sumoketum�te u� degalus." );
                cInfo[ veh ][ cFuel ] = GetPVarInt( playerid, "FILLED" );
                DeletePVar( playerid, "MOKESTIS" );
                return 1;
            }
            AddPlayerBankMoney(playerid, - GetPVarInt( playerid, "MOKESTIS" ));
            ShowInfoText( playerid, "~w~Benzino bakas uzpildytas", 1000 );
            SaveCar( veh );
            DeletePVar( playerid, "MOKESTIS" );
            return 1;
        }
        else return 1;
    }
    */
    if(dialogid == 6)
    {
        if(response == 1)
        {
            switch(listitem)
            {
                case 0:
                {
                    if(GetPlayerMoney(playerid) < 150)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai gryn�j� pinig� su savimi");
                    GivePlayerWeapon(playerid,1,1);
                    GivePlayerMoney(playerid,-150);
                    SendClientMessage(playerid,COLOR_WHITE," ** S�kmingai nusipirkote kastet�, kuris Jums kainavo 150$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 1:
                {
                    if(GetPlayerMoney(playerid) < 498)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai gryn�j� pinig� su savimi");
                    GivePlayerWeapon(playerid,2,1);
                    GivePlayerMoney(playerid,-498);
                    SendClientMessage(playerid,COLOR_WHITE," ** S�kmingai nusipirkote golfo lazd�, kuri Jums kainavo 498$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 2:
                {
                    if(GetPlayerMoney(playerid) < 89)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai gryn�j� pinig� su savimi");
                    GivePlayerWeapon(playerid,4,1);
                    GivePlayerMoney(playerid,-89);
                    SendClientMessage(playerid,COLOR_WHITE," ** S�kmingai nusipirkote kastet�, kuris Jums kainavo 89$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 3:
                {
                    if(GetPlayerMoney(playerid) < 91)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai gryn�j� pinig� su savimi");
                    GivePlayerWeapon(playerid,5,1);
                    GivePlayerMoney(playerid,-91);
                    SendClientMessage(playerid,COLOR_WHITE," ** S�kmingai nusipirkote beisbolo lazd�, kuris Jums kainavo 91$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 4:
                {
                    if(GetPlayerMoney(playerid) < 75)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai gryn�j� pinig� su savimi");
                    GivePlayerWeapon(playerid,6,1);
                    GivePlayerMoney(playerid,-75);
                    SendClientMessage(playerid,COLOR_WHITE," ** S�kmingai nusipirkote kastuv�, kuris Jums kainavo 75$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 5:
                {
                    if(GetPlayerMoney(playerid) < 344)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai gryn�j� pinig� su savimi");
                    GivePlayerWeapon(playerid,7,1);
                    GivePlayerMoney(playerid,-344);
                    SendClientMessage(playerid,COLOR_WHITE," ** S�kmingai nusipirkote kastuv�, kuris Jums kainavo 75$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 6:
                {
                    if(GetPlayerMoney(playerid) < 43)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai gryn�j� pinig� su savimi");
                    GivePlayerWeapon(playerid,15,1);
                    GivePlayerMoney(playerid,-43);
                    SendClientMessage(playerid,COLOR_WHITE," ** S�kmingai nusipirkote lazd�, kuris Jums kainavo 75$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 7:
                {
                    if(GetPlayerMoney(playerid) < 110)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai gryn�j� pinig� su savimi");
                    GivePlayerWeapon(playerid,41,80);
                    GivePlayerMoney(playerid,-110);
                    SendClientMessage(playerid,COLOR_WHITE," ** S�kmingai nusipirkote da�� balion�l�, kuris Jums kainavo 110$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 8:
                {
                    if(GetPlayerMoney(playerid) < 720)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai gryn�j� pinig� su savimi");
                    GivePlayerWeapon(playerid,8,1);
                    GivePlayerMoney(playerid,-720);
                    SendClientMessage(playerid,COLOR_WHITE," ** S�kmingai nusipirkote katan�, kuri Jums kainavo 720$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }				
            }
        }
    }
    /*
    else if(dialogid == DIALOG_VEHICLE_SHOP)
    {
        if(!response)   
            return 1;
        
        new model, price, name[32], query[220], Float:pos[4];
        strmid(name, inputtext, 0, strfind(inputtext, " - "));

        for(new i = 0; i < MAX_VEHICLE_SHOP_VEHICLES; i++)
        {
            if(!VehicleShops[ CurrentPlayerVehicleShop[ playerid ] ][ VehicleModels ][ i ]) 
                continue;
            if(!strcmp(aVehicleNames[ VehicleShops[ CurrentPlayerVehicleShop[ playerid ] ][ VehicleModels ][ i ] - 400 ], name))
            {
                model = VehicleShops[ CurrentPlayerVehicleShop[ playerid ] ][ VehicleModels ][ i ];
                price = VehicleShops[ CurrentPlayerVehicleShop[ playerid ] ][ VehiclePrices ][ i ];
                break;
            }
        }
        if(!model)
        {
            format(query, sizeof(query), "DIALOG_VEHICLE_SHOP. Inputtext:%s VehicleShopSQLID:%d sugebejo nerasti modelio.", inputtext, VehicleShops[ CurrentPlayerVehicleShop[ playerid ] ][ Id ]);
            ImpossibleLog(query);
            return 1;
        }

        if(price > GetPlayerMoney(playerid)) 
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: Neturi pakankamai pinigu kad gal�tum nusipirkti �� automobil�.");

        // Okay, galima duot jam masina. 
        // Dar reikia spalvos ir spawn pozicijos.

        // Gaunam spawn pozicija.
        format(query, sizeof(query), "SELECT x,y,z,angle FROM vehicle_shop_spawns WHERE shop_id = %d ORDER BY RAND() LIMIT 1", VehicleShops[ CurrentPlayerVehicleShop[ playerid ] ][ Id ]);
        new Cache:result = mysql_query(DbHandle, query);
        if(cache_get_row_count())
        {
            pos[ 0 ] = cache_get_field_content_float(0, "x");
            pos[ 1 ] = cache_get_field_content_float(0, "y");
            pos[ 2 ] = cache_get_field_content_float(0, "z");
            pos[ 3 ] = cache_get_field_content_float(0, "angle");
        }
        cache_delete(result);

        // Gaunam spalvas.
        new color1 = random(256);
        new color2 = random(256);

        // Okay, duodam masiniuka ir gero kelio.
        mysql_real_escape_string(name, name);
        format(query,sizeof(query),"INSERT INTO `vehicles` (cOwner,cModel,cName,cSpawn1,cSpawn2,cSpawn3,cAngle,cColor1,cColor2,cFuel) VALUES (%d,%d,'%s',%f, %f, %f, %f, %d, %d, %d)",
            pInfo[ playerid ][ pMySQLID ], model, name, pos[0], pos[1] ,pos[2], pos[3], color1, color2, GetVehicleFuelTank(model));
    
        if(!(result = mysql_query(DbHandle, query)))
        {
            // Tikimes kad �ito nebus, bet JEIGU...
            printf("Error. Klaida. Nepavyko �ra�yti �aid�jo %d transporto priemon�s.", pInfo[ playerid ][ pMySQLID ]);
            SendClientMessage(playerid, COLOR_LIGHTRED, "Atsipra�ome, �vyko klaida. Pra�ome pabandyti v�liau.");
            return 0;
        }

        for(new i = 1; i < 22; i++)
            if(!pInfo[ playerid ][ pCar ][ i ])
            {
                pInfo[ playerid ][ pCar ][ i ] = cache_insert_id();
                break;
            }
        cache_delete(result);

        PayLog(pInfo[ playerid ][ pMySQLID ], 3, -1, -price);
        PlayerPlaySound(playerid, 1057, 0.0, 0.0, 0.0);
        TogglePlayerControllable(playerid, 1);
        if(IsPlayerInAnyVehicle(playerid)) 
            RemovePlayerFromVehicle(playerid);
        SendClientMessage(playerid,COLOR_NEWS,"S�kmingai nusipirkote nauj�  automobil�, galite ji pamatyti para�� /v list.");
        GivePlayerMoney(playerid, -price);
        SaveAccount( playerid );
        return 1;
    }
    */
    else if(dialogid == 7)
    {
        if(response == 1)
        {
            switch(listitem)
            {
                case 0:
                {
                    if(GetPlayerMoney(playerid) < 300)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai gryn�j� pinig� su savimi");
                    GivePlayerWeapon(playerid,10,1);
                    GivePlayerMoney(playerid,-300);
                    SendClientMessage(playerid,COLOR_NEWS," U� ro�in� vibratori� sumok�jote: 300$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 1:
                {
                    if(GetPlayerMoney(playerid) < 300)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai gryn�j� pinig� su savimi");
                    GivePlayerWeapon(playerid,11,1);
                    GivePlayerMoney(playerid,-250);
                    SendClientMessage(playerid,COLOR_NEWS," U� ma�a balt� vibratori� sumok�jote: 250$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 2:
                {
                    if(GetPlayerMoney(playerid) < 300)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai gryn�j� pinig� su savimi");
                    GivePlayerWeapon(playerid,12,1);
                    GivePlayerMoney(playerid,-330);
                    SendClientMessage(playerid,COLOR_NEWS," U� didel� balt� vibratori� sumok�jote: 330$");
                    PlayerPlaySound(playerid, 1052, 0.0, 0.0, 0.0);
                    return 1;
                }
                case 3:
                {
                    if(GetPlayerMoney(playerid) < 260)
                        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai gryn�j� pinig� su savimi");
                    GivePlayerWeapon(playerid,13,1);
                    GivePlayerMoney(playerid,-260);
                    SendClientMessage(playerid,COLOR_NEWS," U� blizgant� vibratori� sumok�jote: 260$");
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
                                    \nPareig�nas: %s\n\
                                    Bauda grynais: $%d\n\
                                    \nGrynieji pinigai: $%d\n\
                                    J�s� banko s�skaita: $%d\n\
                                    \n========================================\n\
                                    Kokiu b�du apmok�site baud�?",
            GetName( Offer[ playerid ][ 3 ]),GetPVarInt( playerid, "MOKESTIS" ), GetPlayerMoney(playerid), GetPlayerBankMoney(playerid));
            ShowPlayerDialog(playerid,98,DIALOG_STYLE_MSGBOX,"BAUDOS APMOK�JIMAS",pay,"Grynais","Banku");
            return 1;
        }
        else if(response == 0)
        {

            SendClientMessage(Offer[playerid][3], COLOR_FADE2, "{FF6347}Persp�jimas: �mogus atsisak� susimok�ti baudos lapel�.");
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
                return SendClientMessage(playerid, COLOR_FADE2, "{FF6347}Persp�jimas: Neturite pakankamai pinig� ($3000)");
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
                    if ( GetPlayerMoney(playerid) < 5000 ) return SendClientMessage(playerid, COLOR_FADE2, "{FF6347}Persp�jimas: Neturite pakankamai pinig� ($5000)");
                    SetPVarInt( playerid, "MOD2", 1087 );
                }
                case 18: SetPVarInt( playerid, "MOD2", -1 );
                case 19: SetPVarInt( playerid, "MOD", -1 );
                case 20: SetPVarInt( playerid, "MOD", -2 );
            }
            StartTimer(playerid,180,3);
        }
    }
    /*
    else if(dialogid == 13)
    {
        new vehicle = GetNearestVehicle( playerid, 5.0 );
        if(vehicle == INVALID_VEHICLE_ID)
            return 1;
        if ( sVehicles[ vehicle ][ Faction ] > 0 )
        {
            if ( sVehicles[ vehicle ][ Faction ] != pInfo[ playerid ][ pMember ] )
                return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: KLAIDA." );
        }
        if( response )
        {
            if( listitem == MAX_TRUNK_SLOTS ) return 1;
            TakeFromTrunk( playerid, vehicle, listitem );
        }
    }
    */
    else if ( dialogid == 16 )
    {
        if ( response == 1 )
        {
            new skin = strval( inputtext );
            if ( skin < 1 || skin > 299 || skin == 6 || skin == 7 || skin == 8 || skin == 149 || skin == 86) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: neteisingai nurodytas i�vaizdos ID.");
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
                    ShowPlayerDialog( playerid, 29, DIALOG_STYLE_LIST,"Frakcij� meniu","- Kurti nauj� \n\
                                                                                        - I�trinti\n\
                                                                                        - Tvarkyti frakcijas", "Rinktis", "At�aukti" );
                    return 1;
                }
                case 3:
                {
                   /* ShowPlayerDialog( playerid, 45, DIALOG_STYLE_LIST,"Serverio automobiliai","- Kurti nauj� \n\
                                                                                               - Priskirti automobil� frakcijai�\n\
                                                                                               - Priskirti automobil� darbui�\n\
																							   - Pakeisti atsiradimo viet� \n\
                                                                                               - (Faction) Keisti reikalaujama rang�\n\
                                                                                               - (Faction) Keisti  automobilio spalv�\n\
                                                                                               - Pa�alinti tr. priemon� \n\
                                                                                               - Patikrinti baga�in�", "Rinktis", "At�aukti" );
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
                    ShowPlayerDialog( playerid, 58, DIALOG_STYLE_INPUT,"Serverio balsavimas", "Ira�ykite klausima, � kuri butu galima atsakyti taip arba ne",  "Rinktis", "At�aukti" );
                    return 1;
                }
                
                case 6:
                {
                    new bigstring[ 1024 ] = "Kurti nauj�\n{FFFFFF}";
                    foreach(VehicleShopIterator, i)
                        format(bigstring, sizeof(bigstring),"%s%d. %s\n",bigstring, i, VehicleShops[ i ][ Name ]);
                    ShowPlayerDialog( playerid, DIALOG_VEHICLE_SHOPS_LIST, DIALOG_STYLE_LIST,"Turg�s", bigstring, "Pirkti", "At�aukti" );
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
    /*
    else if ( dialogid == 29 )
    {
        if ( response == 1 )
        {
            switch( listitem )
            {
                case 0:
                {
                    ShowPlayerDialog( playerid, 30, DIALOG_STYLE_INPUT,"Frakcijos sukurimas","�ra�ykite norima frakcijos pavadinim�:","Patvirtinti","At�aukti" );
                    return 1;
                }
                case 1:
                {
                    foreach(Faction,id)
                    {
                        format( string, 256, "%s%d. %s\n", string, id, fInfo[ id ][ fName ] );
                    }
                    ShowPlayerDialog( playerid, 61, DIALOG_STYLE_LIST, "Frakcij� trinimas", string, "Rinktis", "At�aukti" );
                }
                case 2:
                {
                    foreach(Faction,id)
                    {
                        format( string, 256, "%s%d. %s\n", string, id, fInfo[ id ][ fName ] );
                    }
                    ShowPlayerDialog( playerid, 31, DIALOG_STYLE_LIST, "Frakcij� tvarkymas", string, "Rinktis", "At�aukti" );
                    return 1;
                }
            }
        }
    }
    else if ( dialogid == 30 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;

            mysql_real_escape_string(inputtext,inputtext, DbHandle, 128);

            new id = Itter_Free(Faction);
            format( string, 256, "INSERT INTO `factions` (fName) VALUES ('%s')", inputtext );
            mysql_query(DbHandle, string, false);
            
            format( string, 126, "SELECT id,fRank1,fRank2,fRank3,fRank4,fRank5,fRank6,fRank7,fRank8,fRank9,fRank10,fRank11,fRank12,fRank13 FROM `factions` WHERE `fName`='%s'", inputtext );
            new Cache:result = mysql_query(DbHandle,  string );
            cache_get_field_content(0, "fRank1", fInfo[ id ][ fRank1 ], DbHandle, 54);
            cache_get_field_content(0, "fRank2", fInfo[ id ][ fRank2 ], DbHandle, 54);
            cache_get_field_content(0, "fRank3", fInfo[ id ][ fRank3 ], DbHandle, 54);
            cache_get_field_content(0, "fRank4", fInfo[ id ][ fRank4 ], DbHandle, 54);
            cache_get_field_content(0, "fRank5", fInfo[ id ][ fRank5 ], DbHandle, 54);
            cache_get_field_content(0, "fRank6", fInfo[ id ][ fRank6 ], DbHandle, 54);
            cache_get_field_content(0, "fRank7", fInfo[ id ][ fRank7 ], DbHandle, 54);
            cache_get_field_content(0, "fRank8", fInfo[ id ][ fRank8 ], DbHandle, 54);
            cache_get_field_content(0, "fRank9", fInfo[ id ][ fRank9 ], DbHandle, 54);
            cache_get_field_content(0, "fRank10", fInfo[ id ][ fRank10 ], DbHandle, 54);
            cache_get_field_content(0, "fRank11", fInfo[ id ][ fRank11 ], DbHandle, 54);
            cache_get_field_content(0, "fRank12", fInfo[ id ][ fRank12 ], DbHandle, 54);
            cache_get_field_content(0, "fRank13", fInfo[ id ][ fRank13 ], DbHandle, 54);

            cache_delete(result);
            format( fInfo[ id ][ fName ], 126, "%s", inputtext );
            format( string, 256,"S�kmingai suk�r�te frakcij� pavadinimu: %s", inputtext );
            SendClientMessage( playerid, GRAD, string );
            Itter_Add(Faction,id);
            return 1;
        }
    }
    else if ( dialogid == 31 )
    {
        if ( response == 1 )
        {
            format( string, 256, "- Pavadinimas\t[ %s ]\n\
                                  - Bankas\t[ %d ]\n\
                                  - Algos \n\
                                  - Spawn vieta\n\
                                  - Rangai\n\
                                  - Lideris \t[ %s ]", fInfo[ listitem ][ fName   ],
                                                       fInfo[ listitem ][ fBank   ],
                                                       fInfo[ listitem ][ fLeader ] );
            tmpinteger[ playerid ] = listitem;
            ShowPlayerDialog( playerid, 32, DIALOG_STYLE_LIST, fInfo[ listitem ][ fName ] ,string, "Rinktis", "At�aukti" );
            return 1;
        }
    }
    */
    /*
    else if ( dialogid == 32 )
    {
        if ( response == 1 )
        {
            switch ( listitem )
            {
                case 0:
                {
                    ShowPlayerDialog( playerid, 33, DIALOG_STYLE_INPUT,"Frakcijos pavadinimo keitimas","�ra�ykite norima frakcijos pavadinim�:","Patvirtinti","At�aukti" );
                    return 1;
                }
                case 1:
                {
                    ShowPlayerDialog( playerid, 34, DIALOG_STYLE_INPUT,"Frakcijos banko keitimas","�ra�ykite norima sum� nustatyti bankui:","Patvirtinti","At�aukti" );
                    return 1;
                }
                case 2:
                {
                    for( new id; id < MAX_FACTION_RANKS; id++)
                        format( string, 256, "%s %d. %d\n", string, id, fInfo[ tmpinteger[ playerid ] ][ fPayDay ][ id ] );

                    ShowPlayerDialog( playerid, 35, DIALOG_STYLE_LIST, "Frakcijos algos keitimas",string,"Rinktis","At�aukti" );
                    return 1;
                }
                case 3:
                {
                    GetPlayerPos( playerid, fInfo[ tmpinteger[ playerid ] ][ fSpawn ][ 0 ],
                                            fInfo[ tmpinteger[ playerid ] ][ fSpawn ][ 1 ],
                                            fInfo[ tmpinteger[ playerid ] ][ fSpawn ][ 2 ]);

                    fInfo[ tmpinteger[ playerid ] ][ fInt ] = GetPlayerInterior( playerid );
                    //SaveFactions( tmpinteger[ playerid ] );
                    SendClientMessage( playerid, GRAD, "Frakcijos darbuotoj�/nari� atsiradimo vieta buvo s�kmingai pakeista." );

                    tmpinteger[ playerid ] = -9900;
                    return 1;
                }
                case 4:
                {
                    ShowAdminMenu( playerid, 0, tmpinteger[ playerid ] );
                    return 1;
                }
            }
        }
    }
    else if ( dialogid == 33 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;

            format( fInfo[ tmpinteger[ playerid ] ][ fName ], 126, "%s", inputtext );
            format( string, 256,"Frakcijos pavadinimas buvo pakeistas �: %s", inputtext );
            SendClientMessage( playerid, GRAD, string );
            SaveFactions( tmpinteger[ playerid ] );

            ShowAdminMenu( playerid, 0, tmpinteger[ playerid ] );
            return 1;
        }
    }
    else if ( dialogid == 34 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;

            fInfo[ tmpinteger[ playerid ] ][ fBank ] = strval( inputtext );
            format( string, 256,"Frakcijos bankas buvo pakeistas �: %d", fInfo[ tmpinteger[ playerid ] ][ fBank ] );
            SendClientMessage( playerid, GRAD, string );
            SaveFactions( tmpinteger[ playerid ] );

            tmpinteger[ playerid ] = -9900;
            return 1;
        }
    }

    */
    /*
    else if ( dialogid == 35 )
    {
        if ( response == 1 )
        {
            ShowPlayerDialog( playerid, 100, DIALOG_STYLE_INPUT,"Frakcijos algos keitimas","�ra�ykite norima sum�:","Patvirtinti","At�aukti" );
            SetPVarInt( playerid, "FRAKCIJA", listitem );
            return 1;
        }
    }
    else if ( dialogid == 36 )
    {
        if ( response == 1 )
        {
            switch ( listitem )
            {
                case 0:
                {
                    ShowPlayerDialog( playerid, 37, DIALOG_STYLE_INPUT,"Frakcijos rango keitimas","�ra�ykite norima rang�:","Patvirtinti","At�aukti" );
                    return 1;
                }
                case 1:
                {
                    ShowPlayerDialog( playerid, 38, DIALOG_STYLE_INPUT,"Frakcijos rango keitimas","�ra�ykite norima rang�:","Patvirtinti","At�aukti" );
                    return 1;
                }
                case 2:
                {
                    ShowPlayerDialog( playerid, 39, DIALOG_STYLE_INPUT,"Frakcijos rango keitimas","�ra�ykite norima rang�:","Patvirtinti","At�aukti" );
                    return 1;
                }
                case 3:
                {
                    ShowPlayerDialog( playerid, 40, DIALOG_STYLE_INPUT,"Frakcijos rango keitimas","�ra�ykite norima rang�:","Patvirtinti","At�aukti" );
                    return 1;
                }
                case 4:
                {
                    ShowPlayerDialog( playerid, 41, DIALOG_STYLE_INPUT,"Frakcijos rango keitimas","�ra�ykite norima rang�:","Patvirtinti","At�aukti" );
                    return 1;
                }
                case 5:
                {
                    ShowPlayerDialog( playerid, 42, DIALOG_STYLE_INPUT,"Frakcijos rango keitimas","�ra�ykite norima rang�:","Patvirtinti","At�aukti" );
                    return 1;
                }
                case 6:
                {
                    ShowPlayerDialog( playerid, 43, DIALOG_STYLE_INPUT,"Frakcijos rango keitimas","�ra�ykite norima rang�:","Patvirtinti","At�aukti" );
                    return 1;
                }
                case 7:
                {
                    ShowPlayerDialog( playerid, 44, DIALOG_STYLE_INPUT,"Frakcijos rango keitimas","�ra�ykite norima rang�:","Patvirtinti","At�aukti" );
                    return 1;
                }
                case 8:
                {
                    ShowPlayerDialog( playerid, 145, DIALOG_STYLE_INPUT,"Frakcijos rango keitimas","�ra�ykite norima rang�:","Patvirtinti","At�aukti" );
                    return 1;
                }
                case 9:
                {
                    ShowPlayerDialog( playerid, 146, DIALOG_STYLE_INPUT,"Frakcijos rango keitimas","�ra�ykite norima rang�:","Patvirtinti","At�aukti" );
                    return 1;
                }
                case 10:
                {
                    ShowPlayerDialog( playerid, 147, DIALOG_STYLE_INPUT,"Frakcijos rango keitimas","�ra�ykite norima rang�:","Patvirtinti","At�aukti" );
                    return 1;
                }
                case 11:
                {
                    ShowPlayerDialog( playerid, 148, DIALOG_STYLE_INPUT,"Frakcijos rango keitimas","�ra�ykite norima rang�:","Patvirtinti","At�aukti" );
                    return 1;
                }
                case 12:
                {
                    ShowPlayerDialog( playerid, 152, DIALOG_STYLE_INPUT,"Frakcijos rango keitimas","�ra�ykite norima rang�:","Patvirtinti","At�aukti" );
                    return 1;
                }
            }
        }
    }
    */
    /*
    else if ( dialogid == 37 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;

            format( fInfo[ tmpinteger[ playerid ] ][ fRank1 ], 56, "%s", inputtext );
            format( string, 256,"Frakcijos rangas pakeistas �: %s", inputtext );
            SendClientMessage( playerid, GRAD, string );
            SaveFactions( tmpinteger[ playerid ] );

            ShowAdminMenu( playerid, 0, tmpinteger[ playerid ] );
            return 1;
        }
    }
    else if ( dialogid == 38 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;

            format( fInfo[ tmpinteger[ playerid ] ][ fRank2 ], 56, "%s", inputtext );
            format( string, 256,"Frakcijos rangas pakeistas �: %s", inputtext );
            SendClientMessage( playerid, GRAD, string );
            SaveFactions( tmpinteger[ playerid ] );

            ShowAdminMenu( playerid, 0, tmpinteger[ playerid ] );
            return 1;
        }
    }
    else if ( dialogid == 39 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;

            format( fInfo[ tmpinteger[ playerid ] ][ fRank3 ], 56, "%s", inputtext );
            format( string, 256,"Frakcijos rangas pakeistas �: %s", inputtext );
            SendClientMessage( playerid, GRAD, string );
            SaveFactions( tmpinteger[ playerid ] );

            ShowAdminMenu( playerid, 0, tmpinteger[ playerid ] );
            return 1;
        }
    }
    else if ( dialogid == 40 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;

            format( fInfo[ tmpinteger[ playerid ] ][ fRank4 ], 56, "%s", inputtext );
            format( string, 256,"Frakcijos rangas pakeistas �: %s", inputtext );
            SendClientMessage( playerid, GRAD, string );
            SaveFactions( tmpinteger[ playerid ] );

            ShowAdminMenu( playerid, 0, tmpinteger[ playerid ] );
            return 1;
        }
    }
    else if ( dialogid == 41 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;

            format( fInfo[ tmpinteger[ playerid ] ][ fRank5 ], 56, "%s", inputtext );
            format( string, 256,"Frakcijos rangas pakeistas �: %s", inputtext );
            SendClientMessage( playerid, GRAD, string );
            SaveFactions( tmpinteger[ playerid ] );

            ShowAdminMenu( playerid, 0, tmpinteger[ playerid ] );
            return 1;
        }
    }
    else if ( dialogid == 42 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;

            format( fInfo[ tmpinteger[ playerid ] ][ fRank6 ], 56, "%s", inputtext );
            format( string, 256,"Frakcijos rangas pakeistas �: %s", inputtext );
            SendClientMessage( playerid, GRAD, string );
            SaveFactions( tmpinteger[ playerid ] );

            ShowAdminMenu( playerid, 0, tmpinteger[ playerid ] );
            return 1;
        }
    }
    else if ( dialogid == 43 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;

            format( fInfo[ tmpinteger[ playerid ] ][ fRank7 ], 56, "%s", inputtext );
            format( string, 256,"Frakcijos rangas pakeistas �: %s", inputtext );
            SendClientMessage( playerid, GRAD, string );
            SaveFactions( tmpinteger[ playerid ] );

            ShowAdminMenu( playerid, 0, tmpinteger[ playerid ] );
            return 1;
        }
    }
    else if ( dialogid == 44 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;

            format( fInfo[ tmpinteger[ playerid ] ][ fRank8 ], 56, "%s", inputtext );
            format( string, 256,"Frakcijos rangas pakeistas �: %s", inputtext );
            SendClientMessage( playerid, GRAD, string );
            SaveFactions( tmpinteger[ playerid ] );

            ShowAdminMenu( playerid, 0, tmpinteger[ playerid ] );
            return 1;
        }
    }
    else if ( dialogid == 145 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;

            format( fInfo[ tmpinteger[ playerid ] ][ fRank9 ], 56, "%s", inputtext );
            format( string, 256,"Frakcijos rangas pakeistas �: %s", inputtext );
            SendClientMessage( playerid, GRAD, string );
            SaveFactions( tmpinteger[ playerid ] );

            ShowAdminMenu( playerid, 0, tmpinteger[ playerid ] );
            return 1;
        }
    }
    else if ( dialogid == 146 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;

            format( fInfo[ tmpinteger[ playerid ] ][ fRank10 ], 56, "%s", inputtext );
            format( string, 256,"Frakcijos rangas pakeistas �: %s", inputtext );
            SendClientMessage( playerid, GRAD, string );
            SaveFactions( tmpinteger[ playerid ] );

            ShowAdminMenu( playerid, 0, tmpinteger[ playerid ] );
            return 1;
        }
    }
    else if ( dialogid == 147 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;

            format( fInfo[ tmpinteger[ playerid ] ][ fRank11 ], 56, "%s", inputtext );
            format( string, 256,"Frakcijos rangas pakeistas �: %s", inputtext );
            SendClientMessage( playerid, GRAD, string );
            SaveFactions( tmpinteger[ playerid ] );

            ShowAdminMenu( playerid, 0, tmpinteger[ playerid ] );
            return 1;
        }
    }
    else if ( dialogid == 148 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;

            format( fInfo[ tmpinteger[ playerid ] ][ fRank12 ], 56, "%s", inputtext );
            format( string, 256,"Frakcijos rangas pakeistas �: %s", inputtext );
            SendClientMessage( playerid, GRAD, string );
            SaveFactions( tmpinteger[ playerid ] );

            ShowAdminMenu( playerid, 0, tmpinteger[ playerid ] );
            return 1;
        }
    }
    */
    /*
    else if( dialogid == 149 )
    {
        if(!response)
            return 1;
        new
            rows,
            i = 0;

        new Cache:result = mysql_query(DbHandle,  "SELECT `numbers` FROM `arrestedcars`" );
        
        rows = cache_get_row_count();

        for(i = 0; i < rows; i++)
        {
            if( i == listitem )
            {
                cache_get_field_content(i, "numbers", string);
                OnDialogResponse( playerid, 130, 1, 0, string );
            }
        }
        cache_delete(result);
    }
    */
    /*
    else if ( dialogid == 150 )
    {
        if ( response == 1 )
        {
            switch( tmpinteger[ playerid ] )
            {
                case 0:
                {
                    for(new w = 0; w < sizeof DrugMake; w++)
                    {
                        if( DrugMake[ w ][ dOwner ] == pInfo[ playerid ][ pMySQLID ] && DrugMake[ w ][ dItemID ] == ITEM_METAMFA && DrugMake[ w ][ dLaikas ] > 0)
                            return SendClientMessage( playerid, COLOR_WHITE, " J�s jau gaminate �� narkotik�! ");
                        if( DrugMake[ w ][ dOwner ] == pInfo[ playerid ][ pMySQLID ] && DrugMake[ w ][ dItemID ] == ITEM_METAMFA && DrugMake[ w ][ dLaikas ] == 0 && DrugMake[ w ][ dMade ])
                        {
                            if( PlayerMoney     [ playerid ] < 1000 )
                                return SendClientMessage( playerid, COLOR_WHITE, "Neturite pakankamai pinig�!");

                            if(IsPlayerInventoryFull(playerid))
                                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s� inventorius pilnas.");

                            GivePlayerItem(playerid, ITEM_METAMFA, 50);
                            GivePlayerMoney(playerid, -1000);
                            DrugMake[ w ][ dMade ] = false;
                            DrugMake[ w ][ dLaikas ] = 0;
                            DrugMake[ w ][ dOwner ] = 0;
                            DrugMake[ w ][ dItemID ] = 0;
                            SendClientMessage( playerid, COLOR_WHITE, "NARKOTIKAI PAGAMINTI!");
                            
                            return true;
                        }
                        if( DrugMake[ w ][ dOwner ] == 0 && DrugMake[ w ][ dItemID ] == 0 && DrugMake[ w ][ dLaikas ] == 0 && !DrugMake[ w ][ dMade ])
                        {
                            if( PlayerMoney     [ playerid ] < 1000 )
                                return SendClientMessage( playerid, COLOR_WHITE, "Neturite pakankamai pinig�!");
                            DrugMake[ w ][ dMade ] = false;
                            DrugMake[ w ][ dLaikas ] = 2;
                            DrugMake[ w ][ dOwner ] = pInfo[ playerid ][ pMySQLID ];
                            DrugMake[ w ][ dItemID ] = ITEM_METAMFA;
                            SendClientMessage( playerid, COLOR_WHITE, "NARKOTIKAI PRAD�TI PAGAMINTI!");
                            return true;
                        }
                    }
                }
                case 1:
                {
                    for(new w = 0; w < sizeof DrugMake; w++)
                    {
                        if( DrugMake[ w ][ dOwner ] == pInfo[ playerid ][ pMySQLID ] && DrugMake[ w ][ dItemID ] == ITEM_AMFA && DrugMake[ w ][ dLaikas ] > 0)
                            return SendClientMessage( playerid, COLOR_WHITE, " J�s jau gaminate �� narkotik�! ");
                        if( DrugMake[ w ][ dOwner ] == pInfo[ playerid ][ pMySQLID ] && DrugMake[ w ][ dItemID ] == ITEM_AMFA && DrugMake[ w ][ dLaikas ] == 0 && DrugMake[ w ][ dMade ])
                        {
                            if( PlayerMoney     [ playerid ] < 1500 )
                                return SendClientMessage( playerid, COLOR_WHITE, "Neturite pakankamai pinig�!");

                            if(IsPlayerInventoryFull(playerid))
                                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s� inventorius pilnas.");

                            GivePlayerItem(playerid, ITEM_AMFA, 100);
                            GivePlayerMoney(playerid, -1500);
                            DrugMake[ w ][ dMade ] = false;
                            DrugMake[ w ][ dLaikas ] = 0;
                            DrugMake[ w ][ dOwner ] = 0;
                            DrugMake[ w ][ dItemID ] = 0;
                            SendClientMessage( playerid, COLOR_WHITE, "NARKOTIKAI PAGAMINTI!");
                            
                            return true;
                        }
                        if( DrugMake[ w ][ dOwner ] == 0 && DrugMake[ w ][ dItemID ] == 0 && DrugMake[ w ][ dLaikas ] == 0 && !DrugMake[ w ][ dMade ])
                        {
                            if( PlayerMoney     [ playerid ] < 1500 )
                                return SendClientMessage( playerid, COLOR_WHITE, "Neturite pakankamai pinig�!");
                            DrugMake[ w ][ dMade ] = false;
                            DrugMake[ w ][ dLaikas ] = 2;
                            DrugMake[ w ][ dOwner ] = pInfo[ playerid ][ pMySQLID ];
                            DrugMake[ w ][ dItemID ] = ITEM_AMFA;
                            SendClientMessage( playerid, COLOR_WHITE, "NARKOTIKAI PRAD�TI PAGAMINTI!");
                            return true;
                        }
                    }
                }
                case 2:
                {
                    for(new w = 0; w < sizeof DrugMake; w++)
                    {
                        if( DrugMake[ w ][ dOwner ] == pInfo[ playerid ][ pMySQLID ] && DrugMake[ w ][ dItemID ] == ITEM_COCAINE && DrugMake[ w ][ dLaikas ] > 0)
                            return SendClientMessage( playerid, COLOR_WHITE, " J�s jau gaminate �� narkotik�! ");
                        if( DrugMake[ w ][ dOwner ] == pInfo[ playerid ][ pMySQLID ] && DrugMake[ w ][ dItemID ] == ITEM_COCAINE && DrugMake[ w ][ dLaikas ] == 0 && DrugMake[ w ][ dMade ])
                        {
                            if( PlayerMoney     [ playerid ] < 1000 )
                                return SendClientMessage( playerid, COLOR_WHITE, "Neturite pakankamai pinig�!");

                            if(IsPlayerInventoryFull(playerid))
                                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s� inventorius pilnas.");

                            GivePlayerItem(playerid, ITEM_COCAINE, 25);
                            GivePlayerMoney(playerid, -1000);
                            DrugMake[ w ][ dMade ] = false;
                            DrugMake[ w ][ dLaikas ] = 0;
                            DrugMake[ w ][ dOwner ] = 0;
                            DrugMake[ w ][ dItemID ] = 0;
                            SendClientMessage( playerid, COLOR_WHITE, "NARKOTIKAI PAGAMINTI!");
                            
                            return true;
                        }
                        if( DrugMake[ w ][ dOwner ] == 0 && DrugMake[ w ][ dItemID ] == 0 && DrugMake[ w ][ dLaikas ] == 0 && !DrugMake[ w ][ dMade ])
                        {
                            if( PlayerMoney     [ playerid ] < 1000 )
                                return SendClientMessage( playerid, COLOR_WHITE, "Neturite pakankamai pinig�!");
                            DrugMake[ w ][ dMade ] = false;
                            DrugMake[ w ][ dLaikas ] = 2;
                            DrugMake[ w ][ dOwner ] = pInfo[ playerid ][ pMySQLID ];
                            DrugMake[ w ][ dItemID ] = ITEM_COCAINE;
                            SendClientMessage( playerid, COLOR_WHITE, "NARKOTIKAI PRAD�TI PAGAMINTI!");
                            return true;
                        }
                    }
                }
                case 3:
                {
                    for(new w = 0; w < sizeof DrugMake; w++)
                    {
                        if( DrugMake[ w ][ dOwner ] == pInfo[ playerid ][ pMySQLID ] && DrugMake[ w ][ dItemID ] == ITEM_HERAS && DrugMake[ w ][ dLaikas ] > 0)
                            return SendClientMessage( playerid, COLOR_WHITE, " J�s jau gaminate �� narkotik�! ");
                        if( DrugMake[ w ][ dOwner ] == pInfo[ playerid ][ pMySQLID ] && DrugMake[ w ][ dItemID ] == ITEM_HERAS && DrugMake[ w ][ dLaikas ] == 0 && DrugMake[ w ][ dMade ])
                        {
                            if( PlayerMoney     [ playerid ] < 1500 )
                                return SendClientMessage( playerid, COLOR_WHITE, "Neturite pakankamai pinig�!");

                            if(IsPlayerInventoryFull(playerid))
                                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s� inventorius pilnas.");

                            GivePlayerItem(playerid, ITEM_HERAS, 50);
                            GivePlayerMoney(playerid, -1500);
                            DrugMake[ w ][ dMade ] = false;
                            DrugMake[ w ][ dLaikas ] = 0;
                            DrugMake[ w ][ dOwner ] = 0;
                            DrugMake[ w ][ dItemID ] = 0;
                            SendClientMessage( playerid, COLOR_WHITE, "NARKOTIKAI PAGAMINTI!");
                            return true;
                        }
                        if( DrugMake[ w ][ dOwner ] == 0 && DrugMake[ w ][ dItemID ] == 0 && DrugMake[ w ][ dLaikas ] == 0 && !DrugMake[ w ][ dMade ])
                        {
                            if( PlayerMoney     [ playerid ] < 1500 )
                                return SendClientMessage( playerid, COLOR_WHITE, "Neturite pakankamai pinig�!");
                            DrugMake[ w ][ dMade ] = false;
                            DrugMake[ w ][ dLaikas ] = 2;
                            DrugMake[ w ][ dOwner ] = pInfo[ playerid ][ pMySQLID ];
                            DrugMake[ w ][ dItemID ] = ITEM_HERAS;
                            SendClientMessage( playerid, COLOR_WHITE, "NARKOTIKAI PRAD�TI PAGAMINTI!");
                            return true;
                        }
                    }
                }
                case 4:
                {
                    for(new w = 0; w < sizeof DrugMake; w++)
                    {
                        if( DrugMake[ w ][ dOwner ] == pInfo[ playerid ][ pMySQLID ] && DrugMake[ w ][ dItemID ] == ITEM_EXTAZY && DrugMake[ w ][ dLaikas ] > 0)
                            return SendClientMessage( playerid, COLOR_WHITE, " J�s jau gaminate �� narkotik�! ");
                        if( DrugMake[ w ][ dOwner ] == pInfo[ playerid ][ pMySQLID ] && DrugMake[ w ][ dItemID ] == ITEM_EXTAZY && DrugMake[ w ][ dLaikas ] == 0 && DrugMake[ w ][ dMade ])
                        {
                            if( PlayerMoney     [ playerid ] < 1200 )
                                return SendClientMessage( playerid, COLOR_WHITE, "Neturite pakankamai pinig�!");

                            if(IsPlayerInventoryFull(playerid))
                                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s� inventorius pilnas.");

                            GivePlayerItem(playerid, ITEM_EXTAZY, 30 );
                            GivePlayerMoney(playerid, -1200);
                            DrugMake[ w ][ dMade ] = false;
                            DrugMake[ w ][ dLaikas ] = 0;
                            DrugMake[ w ][ dOwner ] = 0;
                            DrugMake[ w ][ dItemID ] = 0;
                            SendClientMessage( playerid, COLOR_WHITE, "NARKOTIKAI PAGAMINTI!");
                            return true;
                        }
                        if( DrugMake[ w ][ dOwner ] == 0 && DrugMake[ w ][ dItemID ] == 0 && DrugMake[ w ][ dLaikas ] == 0 && !DrugMake[ w ][ dMade ])
                        {
                            if( PlayerMoney     [ playerid ] < 1200 )
                                return SendClientMessage( playerid, COLOR_WHITE, "Neturite pakankamai pinig�!");
                            DrugMake[ w ][ dMade ] = false;
                            DrugMake[ w ][ dLaikas ] = 2;
                            DrugMake[ w ][ dOwner ] = pInfo[ playerid ][ pMySQLID ];
                            DrugMake[ w ][ dItemID ] = ITEM_EXTAZY;
                            SendClientMessage( playerid, COLOR_WHITE, "NARKOTIKAI PRAD�TI PAGAMINTI!");
                            return true;
                        }
                    }
                }
                case 5:
                {
                    for(new w = 0; w < sizeof DrugMake; w++)
                    {
                        if( DrugMake[ w ][ dOwner ] == pInfo[ playerid ][ pMySQLID ] && DrugMake[ w ][ dItemID ] == ITEM_PCP && DrugMake[ w ][ dLaikas ] > 0)
                            return SendClientMessage( playerid, COLOR_WHITE, " J�s jau gaminate �� narkotik�! ");
                        if( DrugMake[ w ][ dOwner ] == pInfo[ playerid ][ pMySQLID ] && DrugMake[ w ][ dItemID ] == ITEM_PCP && DrugMake[ w ][ dLaikas ] == 0 && DrugMake[ w ][ dMade ])
                        {
                            if( PlayerMoney     [ playerid ] < 1300 )
                                return SendClientMessage( playerid, COLOR_WHITE, "Neturite pakankamai pinig�!");

                            if(IsPlayerInventoryFull(playerid))
                                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s� inventorius pilnas.");

                            GivePlayerItem(playerid, ITEM_PCP, 50);
                            GivePlayerMoney(playerid, -1300);
                            DrugMake[ w ][ dMade ] = false;
                            DrugMake[ w ][ dLaikas ] = 0;
                            DrugMake[ w ][ dOwner ] = 0;
                            DrugMake[ w ][ dItemID ] = 0;
                            SendClientMessage( playerid, COLOR_WHITE, "NARKOTIKAI PAGAMINTI!");
                            return true;
                        }
                        if( DrugMake[ w ][ dOwner ] == 0 && DrugMake[ w ][ dItemID ] == 0 && DrugMake[ w ][ dLaikas ] == 0 && !DrugMake[ w ][ dMade ])
                        {
                            if( PlayerMoney     [ playerid ] < 1300 )
                                return SendClientMessage( playerid, COLOR_WHITE, "Neturite pakankamai pinig�!");
                            DrugMake[ w ][ dMade ] = false;
                            DrugMake[ w ][ dLaikas ] = 2;
                            DrugMake[ w ][ dOwner ] = pInfo[ playerid ][ pMySQLID ];
                            DrugMake[ w ][ dItemID ] = ITEM_PCP;
                            SendClientMessage( playerid, COLOR_WHITE, "NARKOTIKAI PRAD�TI PAGAMINTI!");
                            return true;
                        }
                    }
                }
                case 6:
                {
                    for(new w = 0; w < sizeof DrugMake; w++)
                    {
                        if( DrugMake[ w ][ dOwner ] == pInfo[ playerid ][ pMySQLID ] && DrugMake[ w ][ dItemID ] == ITEM_CRACK && DrugMake[ w ][ dLaikas ] > 0)
                            return SendClientMessage( playerid, COLOR_WHITE, " J�s jau gaminate �� narkotik�! ");
                        if( DrugMake[ w ][ dOwner ] == pInfo[ playerid ][ pMySQLID ] && DrugMake[ w ][ dItemID ] == ITEM_CRACK && DrugMake[ w ][ dLaikas ] == 0 && DrugMake[ w ][ dMade ])
                        {
                            if( PlayerMoney     [ playerid ] < 1000 )
                                return SendClientMessage( playerid, COLOR_WHITE, "Neturite pakankamai pinig�!");

                            if(IsPlayerInventoryFull(playerid))
                                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s� inventorius pilnas.");


                            GivePlayerItem(playerid, ITEM_CRACK, 100);
                            GivePlayerMoney(playerid, -1000);
                            DrugMake[ w ][ dMade ] = false;
                            DrugMake[ w ][ dLaikas ] = 0;
                            DrugMake[ w ][ dOwner ] = 0;
                            DrugMake[ w ][ dItemID ] = 0;
                            SendClientMessage( playerid, COLOR_WHITE, "NARKOTIKAI PAGAMINTI!");
                            return true;
                        }
                        if( DrugMake[ w ][ dOwner ] == 0 && DrugMake[ w ][ dItemID ] == 0 && DrugMake[ w ][ dLaikas ] == 0 && !DrugMake[ w ][ dMade ])
                        {
                            if( PlayerMoney     [ playerid ] < 1000 )
                                return SendClientMessage( playerid, COLOR_WHITE, "Neturite pakankamai pinig�!");
                            DrugMake[ w ][ dMade ] = false;
                            DrugMake[ w ][ dLaikas ] = 2;
                            DrugMake[ w ][ dOwner ] = pInfo[ playerid ][ pMySQLID ];
                            DrugMake[ w ][ dItemID ] = ITEM_CRACK;
                            SendClientMessage( playerid, COLOR_WHITE, "NARKOTIKAI PRAD�TI PAGAMINTI!");
                            return true;
                        }
                    }
                }
                case 7:
                {
                    for(new w = 0; w < sizeof DrugMake; w++)
                    {
                        if( DrugMake[ w ][ dOwner ] == pInfo[ playerid ][ pMySQLID ] && DrugMake[ w ][ dItemID ] == ITEM_OPIUM && DrugMake[ w ][ dLaikas ] > 0)
                            return SendClientMessage( playerid, COLOR_WHITE, " J�s jau gaminate �� narkotik�! ");
                        if( DrugMake[ w ][ dOwner ] == pInfo[ playerid ][ pMySQLID ] && DrugMake[ w ][ dItemID ] == ITEM_OPIUM && DrugMake[ w ][ dLaikas ] == 0 && DrugMake[ w ][ dMade ])
                        {
                            if( PlayerMoney     [ playerid ] < 1200 )
                                return SendClientMessage( playerid, COLOR_WHITE, "Neturite pakankamai pinig�!");

                            if(IsPlayerInventoryFull(playerid))
                                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s� inventorius pilnas.");

                            GivePlayerItem(playerid, ITEM_OPIUM, 30);
                            GivePlayerMoney(playerid, -1200);
                            DrugMake[ w ][ dMade ] = false;
                            DrugMake[ w ][ dLaikas ] = 0;
                            DrugMake[ w ][ dOwner ] = 0;
                            DrugMake[ w ][ dItemID ] = 0;
                            SendClientMessage( playerid, COLOR_WHITE, "NARKOTIKAI PAGAMINTI!");
                            return true;
                        }
                        if( DrugMake[ w ][ dOwner ] == 0 && DrugMake[ w ][ dItemID ] == 0 && DrugMake[ w ][ dLaikas ] == 0 && !DrugMake[ w ][ dMade ])
                        {
                            if( PlayerMoney     [ playerid ] < 1200 )
                                return SendClientMessage( playerid, COLOR_WHITE, "Neturite pakankamai pinig�!");
                            DrugMake[ w ][ dMade ] = false;
                            DrugMake[ w ][ dLaikas ] = 2;
                            DrugMake[ w ][ dOwner ] = pInfo[ playerid ][ pMySQLID ];
                            DrugMake[ w ][ dItemID ] = ITEM_EXTAZY;
                            SendClientMessage( playerid, COLOR_WHITE, "NARKOTIKAI PRAD�TI PAGAMINTI!");
                            return true;
                        }
                    }
                }
            }
        }
        tmpinteger[ playerid ] = -1;
    }
    */
    /*
    else if ( dialogid == 151 )
    {
        if ( response == 1 )
        {
            foreach(Busines,i)
            {
                if ( PlayerToPoint( 5.0, playerid, bInfo[ i ][ bEnter ][ 0 ], bInfo[ i ][ bEnter ][ 1 ], bInfo[ i ][ bEnter ][ 2 ] ) )
                {
                    switch( listitem )
                    {
                        case 0: bInfo[ i ][ bNasumas ] = 50;
                        case 1: bInfo[ i ][ bNasumas ] = 100;
                        case 2: bInfo[ i ][ bNasumas ] = 150;
                        case 3: bInfo[ i ][ bNasumas ] = 200;
                        case 4: bInfo[ i ][ bNasumas ] = 250;
                    }
                    SaveBiz( i );
                    SendClientMessage( playerid, COLOR_WHITE, "Biznio/verslo na�umas buvo s�kmingai pakeistas." );
                    return 1;
                }
            }
        }
        return 1;
    }
    */
    /*
    else if ( dialogid == 152 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;

            format( fInfo[ tmpinteger[ playerid ] ][ fRank13 ], 56, "%s", inputtext );
            format( string, 256,"Frakcijos rangas pakeistas �: %s", inputtext );
            SendClientMessage( playerid, GRAD, string );
            SaveFactions( tmpinteger[ playerid ] );

            ShowAdminMenu( playerid, 0, tmpinteger[ playerid ] );
            return 1;
        }
    }
    */
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
                    SendClientMessage(playerid, 0xFFFFFFFF, "Nor�dami pasukti/pakeisti kamer� laikykite klavi�us: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
                    SetPVarInt( playerid, "oKepure", true );
                }
                else
                {
                    SetPVarInt( playerid, "oKepure", false );
                    SendClientMessage( playerid, COLOR_WHITE,"Daiktas buvo s�kmingai panaikintas/nuimtas.");
                    RemovePlayerAttachedObject( playerid, 0 );
                }
            }
            case 1:
            {
                if( !GetPVarInt( playerid, "oKepure" ) )
                {
                    SetPlayerAttachedObject(playerid, 0, 19331, 2, 0.132999,-0.011999,0.004000,-177.199996,-2.200002,30.799991);
                    EditAttachedObject(playerid, 0);
                    SendClientMessage(playerid, 0xFFFFFFFF, "Nor�dami pasukti/pakeisti kamer� laikykite klavi�us: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
                    SetPVarInt( playerid, "oKepure", true );
                }
                else
                {
                    SetPVarInt( playerid, "oKepure", false );
                    SendClientMessage( playerid, COLOR_WHITE,"Daiktas buvo s�kmingai panaikintas/nuimtas.");
                    RemovePlayerAttachedObject( playerid, 0 );
                }
            }
            case 2:
            {
                if( !GetPVarInt( playerid, "oKepure" ) )
                {
                    SetPlayerAttachedObject(playerid, 0, 19472, 2, 0.132999,-0.011999,0.004000,-177.199996,-2.200002,30.799991);
                    EditAttachedObject(playerid, 0);
                    SendClientMessage(playerid, 0xFFFFFFFF, "Nor�dami pasukti/pakeisti kamer� laikykite klavi�us: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
                    SetPVarInt( playerid, "oKepure", true );
                }
                else
                {
                    SetPVarInt( playerid, "oKepure", false );
                    SendClientMessage( playerid, COLOR_WHITE,"Daiktas buvo s�kmingai panaikintas/nuimtas.");
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
            format( string, 256, "AdmCmd Administratorius %s paskelb� serveryje balsavim� �aid�jams. ", GetName( playerid ) );
            SendClientMessageToAll( COLOR_LIGHTRED, string );
            format( string, 256, "AdmCmd Administratoriaus klausimas: %s", inputtext );
            SendClientMessageToAll( COLOR_LIGHTRED2, string );
            SendClientMessageToAll( COLOR_WHITE, "� u�duot� Administratoriaus klausim� galite atsakyti komandomis /taip arba /ne." );
            SetTimerEx( "Vote", 60000, 0, "s", inputtext );
            foreach(Player,id)
                Voted[ id ] = false;
            return 1;
        }
    }
    /*
    else if ( dialogid == 61 )
    {
        if ( response == 1 )
        {
            tmpinteger[ playerid ] = listitem;
            ShowPlayerDialog( playerid, 62, DIALOG_STYLE_LIST, fInfo[ listitem ][ fName ] ,"- Pa�alinti frakcij�\n","Rinktis", "At�aukti" );
            return 1;
        }
    }
    */
    /*
    else if(dialogid == DIALOG_VEHICLE_SHOPS_LIST)
    {
        if(!response)
            return 1;

        if(!listitem)
        {
            // naujo kurimas.
            ShowPlayerDialog(playerid, DIALOG_VEHICLE_SHOP_NEW, DIALOG_STYLE_INPUT, "Naujas turgus", "�veskite turgaus pavadinim�", "Kurti" ,"I�eiti");
            return 1;
        }
        new tmp[32], index;
        strmid(tmp, inputtext, 0, strfind(inputtext, "."));
        index = strval(tmp);
        SetPVarInt(playerid, "VehicleShop_Index", index);
        ShowPlayerDialog(playerid, DIALOG_VEHICLE_SHOPS_MENU, DIALOG_STYLE_LIST, VehicleShops[ index][ Name ], " - Per�i�r�ti parduodama transport�\n - Keisti pavadinim�\n - {AA1100}I�trinti", "Pasirinkti", "I�eiti");
        return 1;
    }
    else if(dialogid == DIALOG_VEHICLE_SHOPS_MENU)
    {
        if(!response)
            return 1;

        switch(listitem)
        {
            // vehicle list 
            case 0:
            {
                new str[2048] = "Prid�ti transporto priemon� � turg�\n{FFFFFF}", index = GetPVarInt(playerid, "VehicleShop_Index");
                for(new i = 0; i < MAX_VEHICLE_SHOP_VEHICLES; i++)
                    if(VehicleShops[ index ][ VehicleModels ][ i ])
                        format(str, sizeof(str), "%s%s\t$%d\n", str, aVehicleNames[ VehicleShops[ index ][ VehicleModels ][ i ] - 400], VehicleShops[ index ][ VehiclePrices ][ i ]);
                ShowPlayerDialog(playerid, DIALOG_VEHICLE_SHOPS_VEH_LIST, DIALOG_STYLE_LIST, "Transporto s�ra�as", str, "Pasirinkti", "I�eiti");
            }
            case 1: // New name 
                ShowPlayerDialog(playerid, DIALOG_VEHICLE_SHOPS_NEW_NAME, DIALOG_STYLE_INPUT, "Pavadinimo keitimas", "�veskite nauj� pavadinim�", "Keisti", "I�eiti");
            case 2: // DELETE 
                ShowPlayerDialog(playerid, DIALOG_VEHICLE_SHOPS_DELETE_CON, DIALOG_STYLE_MSGBOX, "{FF0000}D�mesio.", "Ar tikrai norite pa�alinti �� turg�?\nVisos jo parduodamos transporto priemon�s bus prarastos.", "Taip", "Ne");
        }
        return 1;
    }
    else if(dialogid == DIALOG_VEHICLE_SHOPS_VEH_LIST)
    {
        if(!response)
            return 1;

        if(!listitem)
        {
            // Prid�ti nauj� ma�in�.
            ShowPlayerDialog(playerid, DIALOG_VEHICLE_SHOPS_VEH_NEW, DIALOG_STYLE_INPUT, "Nauja transporto priemon�.", "�ra�ykite tokiu formatu: modelis/pavadinimas kaina", "Kurti", "I�eiti");
            return 1;
        }

        new index = GetPVarInt(playerid, "VehicleShop_Index"), vehIndex = -1, name[32];
        strmid(name, inputtext, 0, strfind(inputtext, "\t"));
        for(new i = 0; i < MAX_VEHICLE_SHOP_VEHICLES; i++)
        {
            if(VehicleShops[ index ][ VehicleModels ][ i ] >= 400 && !strcmp(aVehicleNames[ VehicleShops[ index ][ VehicleModels ][ i ] - 400], name))
            {
                vehIndex = i;
                break;
            }
        }

        if(vehIndex == -1)
        {
            ErrorLog("ltrp.pwn : OnDialogResponse : DIALOG_VEHICLE_SHOPS_VEH_LIST. Vehicle was not found. inputtext:%s name:%s", inputtext, name);
            return 0;
        }

        SetPVarInt(playerid, "VehicleShop_VehIndex", vehIndex);
        ShowPlayerDialog(playerid, DIALOG_VEHICLE_SHOPS_VEH_MAIN, DIALOG_STYLE_LIST, "Transporto valdymas", "Keisti kain�\nI�trinti", "Pasirinkti", "I�eiti");
        return 1;
    }
    else if(dialogid == DIALOG_VEHICLE_SHOPS_VEH_NEW)
    {
        if(!response)
            return 1;
        
        // �emiau kvie�iamas OnDialogResponse.. Tai n�ra gerai ir a� tai �inau.... bet 
        // it makes shit A LOT easier.

        new model, name[32], price, query[140];
        // Jei naudo�iau sscanf 3 visas �itas daiktas b�t� viena eilut� :/
        if(sscanf(inputtext, "ii", model, price))
        {
            if(sscanf(inputtext, "s[32]i", name, price))
                return OnDialogResponse(playerid, DIALOG_VEHICLE_SHOPS_VEH_LIST, true, 0, "");
            else 
                for(new i = 0; i < sizeof(aVehicleNames); i++)
                    if(!strcmp(aVehicleNames[ i ], name))
                    {
                        model = i + 400;
                        break;
                    }
        }
        if(price < 0)
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Kaina negali b�ti ma�esn� u� 0.");
            return OnDialogResponse(playerid, DIALOG_VEHICLE_SHOPS_VEH_LIST, true, 0, "");
        }
        if(model < 400 || model > 611)
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Modelis netinkamas. Galimi modeliai 400-611");
            return OnDialogResponse(playerid, DIALOG_VEHICLE_SHOPS_VEH_LIST, true, 0, "");
        }


        new index = GetPVarInt(playerid, "VehicleShop_Index"),vehIndex = -1;
        for(new i = 0; i < MAX_VEHICLE_SHOP_VEHICLES; i++)
        {
            if(!VehicleShops[ index ][ VehicleModels ][ i ])
            {
                VehicleShops[ index ][ VehicleModels ][ i ] = model;
                VehicleShops[ index ][ VehiclePrices ][ i ] = price;
                vehIndex = i;
                break;
            }
            else if(VehicleShops[ index ][ VehicleModels ][ i ] == model)
            {
                SendClientMessage(playerid, COLOR_LIGHTRED, "�iame turguje �i transporto priemon� jau yra!");
                return 1;
            }
        }
        if(vehIndex == -1)
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "�iame turguje daugiau transporto priemoni� netelpa. Limitas " #MAX_VEHICLE_SHOP_VEHICLES ".");
            return OnDialogResponse(playerid, DIALOG_VEHICLE_SHOPS_VEH_LIST, true, 0, "");
        }

         // Na k�, �ia jau tur�tume tur�ti visk� ko reik�s.
        format(query, sizeof(query), "INSERT INTO vehicle_shop_vehicles (shop_id, model, price) VALUES(%d, %d, %d)",
            VehicleShops[ index ][ Id ],
            VehicleShops[ index ][ VehicleModels ][ vehIndex ],
            VehicleShops[ index ][ VehiclePrices ][ vehIndex ]);
        mysql_pquery(DbHandle, query);
        SendClientMessage(playerid, COLOR_NEWS, "Nauja transporto priemon� s�kmingai prid�ta � turg�.");
        return 1;
    }
    else if(dialogid == DIALOG_VEHICLE_SHOPS_VEH_MAIN)
    {
        if(!response)
            return 1;

        new str[100];
        switch(listitem)
        {
            case 0: // keisti kaina.
            {
                format(str, sizeof(str), "�veskite nauj� %s kain�.\nDabartin� kaina: %d$", 
                    aVehicleNames[ VehicleShops[ GetPVarInt(playerid, "VehicleShop_Index") ][ VehicleModels ][ GetPVarInt(playerid, "VehicleShop_VehIndex")] - 400 ],
                    VehicleShops[ GetPVarInt(playerid, "VehicleShop_Index") ][ VehiclePrices ][ GetPVarInt(playerid, "VehicleShop_VehIndex")]);
                ShowPlayerDialog(playerid, DIALOG_VEHICLE_SHOPS_VEH_PRICE, DIALOG_STYLE_INPUT, "Kainos keitimas", str, "T�sti", "I�eiti");
            }
            case 1: // DELETE.
            {
                new index = GetPVarInt(playerid, "VehicleShop_Index"), 
                    vehIndex = GetPVarInt(playerid, "VehicleShop_VehIndex");
                format(str, sizeof(str),"DELETE FROM vehicle_shop_vehicles WHERE shop_id = %d AND model = %d",
                    VehicleShops[ index ][ Id ],
                    VehicleShops[ index ][ VehicleModels ][ vehIndex ]);
                mysql_pquery(DbHandle, str);
                VehicleShops[ index ][ VehicleModels ][ vehIndex ] = 0;
                VehicleShops[ index ][ VehiclePrices ][ vehIndex ] = 0;
                SendClientMessage(playerid, COLOR_NEWS, "Transporto priemon� pa�alinta i� turguas.");
            }
        }
        return 1;
    }
    else if(dialogid == DIALOG_VEHICLE_SHOPS_VEH_PRICE)
    {
        if(!response)
            return 1;
        
        new price, 
            query[120], 
            index = GetPVarInt(playerid, "VehicleShop_Index"), 
            vehIndex = GetPVarInt(playerid, "VehicleShop_VehIndex");
        if(sscanf(inputtext, "i", price) || price < 0)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Kaina turi b�ti skai�ius didesnis u� 0.");

        VehicleShops[ index ][ VehiclePrices ][ vehIndex ] = price;
        format(query, sizeof(query), "UPDATE vehicle_shop_vehicles SET price = %d WHERE shop_id = %d AND model = %d",
            price, VehicleShops[ index ][ Id ], VehicleShops[ index ][ VehicleModels ][ vehIndex ]);
        mysql_pquery(DbHandle, query);
        SendClientMessage(playerid, COLOR_NEWS, "Kaina s�kmingai atnaujinta.");
        return 1;
    }
    else if(dialogid == DIALOG_VEHICLE_SHOPS_DELETE_CON)
    {
        if(!response)
            return 1;

        new index = GetPVarInt(playerid, "VehicleShop_Index"), query[ 60 ];

        // �ita u�klausa i�trins tik i� vieno table.
        // Pasikliaujam pa�iu mysql pa�alint i� kit� table, jam taip nurodyta padaryti.
        format(query, sizeof(query), "DELETE FROM vehicle_shops WHERE id = %d", VehicleShops[ index ][ Id ]);
        mysql_pquery(DbHandle, query);

        Iter_Remove(VehicleShopIterator, index);
        VehicleShops[ index ][ Id ] = 0;
        strdel(VehicleShops[ index ][ Name ], 0, strlen(VehicleShops[ index ][ Name ]));
        DestroyDynamic3DTextLabel(VehicleShops[ index ][ Label ]);
        SendClientMessage(playerid, COLOR_NEWS, "Turgus s�kmingai sunaikintas");
        return 1;
    }
    else if(dialogid == DIALOG_VEHICLE_SHOPS_NEW_NAME)
    {
        if(!response)
            return 1;

        if(isnull(inputtext) || strlen(inputtext) >= MAX_VEHICLE_SHOP_NAME)
            return ShowPlayerDialog(playerid, DIALOG_VEHICLE_SHOPS_NEW_NAME, DIALOG_STYLE_INPUT, "Pavaidnimo keitimas", "�veskite nauj� pavadinim�", "Keisti", "I�eiti");

        new index = GetPVarInt(playerid, "VehicleShop_Index"), query[ 156 ];
        strcat(query, inputtext);
        mysql_real_escape_string(query, query);
        format(query, sizeof(query), "UPDATE vehicle_shops SET name = '%s' WHERE id = %d",
            query, VehicleShops[ index ][ Id ]);
        mysql_pquery(DbHandle, query);

        format(VehicleShops[ index ][ Name ], MAX_VEHICLE_SHOP_NAME, inputtext);

        DestroyDynamic3DTextLabel(VehicleShops[ index ][ Label ]);
        format(query,sizeof(query), "%s\nParduodam� trnasporto priemoni� s�ra�as\nKomanda: {FFFFFF}/v buy",
            VehicleShops[ index ][ Name ]);
        
        VehicleShops[ index ][ Label ] = CreateDynamic3DTextLabel(query, COLOR_NEWS, 
            VehicleShops[ index ][ PosX ],
            VehicleShops[ index ][ PosY ],
            VehicleShops[ index ][ PosZ ],
            7.0,  INVALID_PLAYER_ID, INVALID_VEHICLE_ID, 1, 0, 0, -1, 15.0
        );
        SendClientMessage(playerid, COLOR_NEWS, "Pavadinimas s�kmingai pakeistas.");
        return 1;
    }
    else if(dialogid == DIALOG_VEHICLE_SHOP_NEW)
    {
        if(!response)
            return 1;
        if(isnull(inputtext) || strlen(inputtext) >= MAX_VEHICLE_SHOP_NAME)
            return ShowPlayerDialog(playerid, DIALOG_VEHICLE_SHOP_NEW, DIALOG_STYLE_INPUT, "Naujas turgus", "�veskite turgaus pavadinim�", "Kurti" ,"I�eiti");

        new index = Iter_Free(VehicleShopIterator);
        if(index == -1)
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, pasiektas turgaus limitas(" #MAX_VEHICLE_SHOPS ").");

        new query[160], Float:x, Float:y, Float:z;
        strcat(query, inputtext);
        GetPlayerPos(playerid, x, y, z);
        mysql_real_escape_string(query, query);
        format(query, sizeof(query), "INSERT INTO vehicle_shops (name, x, y, z) VALUES ('%s', %f, %f, %f)",
            query, x, y, z);
        new Cache:result = mysql_query(DbHandle, query);

        VehicleShops[ index ][ Id ] = cache_insert_id();
        cache_delete(result);
        strcat(VehicleShops[ index ][ Name ], inputtext, MAX_VEHICLE_SHOP_NAME);
        VehicleShops[ index ][ PosX ] = x;
        VehicleShops[ index ][ PosY ] = y;
        VehicleShops[ index ][ PosZ ] = z;

        format(query,sizeof(query), "%s\nParduodam� trnasporto priemoni� s�ra�as\nKomanda: {FFFFFF}/v buy",
            VehicleShops[ index ][ Name ]);
        
        VehicleShops[ index ][ Label ] = CreateDynamic3DTextLabel(query, COLOR_NEWS, 
            VehicleShops[ index ][ PosX ],
            VehicleShops[ index ][ PosY ],
            VehicleShops[ index ][ PosZ ],
            7.0,  INVALID_PLAYER_ID, INVALID_VEHICLE_ID, 1, 0, 0, -1, 15.0
        );
        Itter_Add(VehicleShopIterator, index);
        SendClientMessage(playerid, COLOR_NEWS, "Turgus s�kmingai sukurtas.");
        return 1;
    }
    */
    /*
    else if ( dialogid == 67 )
    {
        if ( response == 1 )
        {
            switch ( listitem )
            {
                case 0:
                {
                    for ( new i = 0; i < STATIONS; i++ )
                    {
                        format ( string, sizeof(string), "%s%s\n",
                                string,
                                RadioStations [ i ][ rName ] );
                    }
                    format ( string, sizeof(string), "%s�vesti savo stot�",
                            string );
                    ShowPlayerDialog( playerid, 68, DIALOG_STYLE_LIST, "Radijo stotys", string, "Rinktis", "At�aukti" );
                    return 1;
                }
                case 1:
                    return ShowPlayerDialog( playerid, 69, DIALOG_STYLE_INPUT, "MP3 Valdymas", "�emiau �ra�ykite norim� garsum� nuo 0 iki 100 ","Rinktis", "At�aukti");
                case 2:
                {
                    format         ( string, 256, "* %s i�jungia MP3 grotuv�." ,GetPlayerNameEx( playerid ) );
                    ProxDetector   ( 20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
                    StopPlayerRadio( playerid );
                    return 1;
                }
            }
        }
    }
    */
    /*
    else if ( dialogid == 137 )
    {
        if ( !response ) return true;
        SetPlayerRadio( playerid, inputtext );
        format        ( string, 256, "* %s nustato MP3 grotuvo radijos da�n�." ,GetPlayerNameEx( playerid ) );
        ProxDetector  ( 20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
    }
    else if ( dialogid == 138 )
    {
        if ( !response ) return true;
        new veh = GetPlayerVehicleID( playerid );
        format(VehRadio[ veh ], 128, "%s", inputtext);
        foreach(Player,i)
        {
            if ( IsPlayerInVehicle( i, veh ) )
            {
                SetPlayerRadio( i, inputtext );
            }
        }
        VehicleRadio[ veh ] = 1;
        format        ( string, 256, "* %s nustato automagnetolos radijos da�n�." ,GetPlayerNameEx( playerid ) );
        ProxDetector  ( 20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
    }
    else if ( dialogid == 140 )
    {
        if ( !response ) return true;
        format(aInfo[ playerid ][ aStation ], 128, "%s", inputtext);
        foreach(Player,id)
        {
            if ( IsPlayerInDynamicArea( id, aInfo[ playerid ][ aArea ] ) )
                Set3DAudioForPlayer( id, inputtext, playerid );
        }
        format        ( string, 256, "* %s pakei�ia magnetolos nustatyt� da�n� � kit�." ,GetPlayerNameEx( playerid ) );
        ProxDetector  ( 20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
    }
    else if ( dialogid == 68 )
    {
        if ( response == 1 )
        {
            if( listitem < STATIONS )
            {
                SetPlayerRadio( playerid, RadioStations [ listitem ][ rUrl ] );
                format        ( string, 256, "* %s nustato MP3 grotuvo radijos da�n�." ,GetPlayerNameEx( playerid ) );
                ProxDetector  ( 20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
            }
            else
                ShowPlayerDialog( playerid, 137, DIALOG_STYLE_INPUT, "Radijas", "�veskite radijos stot�", "OK", "Cancel");
            return 1;
        }
    }
    else if ( dialogid == 69 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;
            new volume = strval( inputtext );
            if ( volume < 0 || volume > 101 ) return 1;
            format( string, 126, "MP3 grotuvo garsas buvo nustatytas �: %d", volume );
            SendClientMessage( playerid, COLOR_WHITE, string );
            SetPlayerRadioVolume( playerid, volume );
            format      ( string, 256, "* %s pakei�ia MP3 grojimo gars�." ,GetPlayerNameEx( playerid ) );
            ProxDetector( 20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
            return 1;
        }
    }
    else if ( dialogid == 70 )
    {
        if ( response == 1 )
        {
            switch ( listitem )
            {
                case 0:
                {
                    for ( new i = 0; i < STATIONS; i++ )
                    {
                        format ( string, sizeof(string), "%s%s\n",
                                string,
                                RadioStations [ i ][ rName ] );
                    }
                    strcat(string, "�vesti savo stoties URL");
                    ShowPlayerDialog( playerid, 71, DIALOG_STYLE_LIST, "Radijo stotys", string, "Rinktis", "At�aukti" );
                    return 1;
                }
                case 1:
                    return ShowPlayerDialog( playerid, 72, DIALOG_STYLE_INPUT, "Automognetolos valdymas", "�emiau �ra�ykite norim� garsum� nuo 0 iki 100 ","Rinktis", "At�aukti");
                case 2:
                {
                    format         ( string, 256, "* %s i�jungia automagnetola." ,GetPlayerNameEx( playerid ) );
                    ProxDetector   ( 20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
                    new veh = GetPlayerVehicleID( playerid );
                    VehicleRadio[ veh ] = 99;
                    foreach(Player,i)
                    {
                        if ( IsPlayerInVehicle( i, veh ) )
                        {
                            StopPlayerRadio( i );
                        }
                    }
                    return 1;
                }
            }
        }
    }
    else if ( dialogid == 71 )
    {
        if ( response == 1 )
        {
            new veh = GetPlayerVehicleID( playerid );
            if( listitem < STATIONS )
            {
                format(VehRadio[ veh ], 128, "%s", RadioStations [ listitem ][ rUrl ]);
                foreach(Player,i)
                {
                    if ( IsPlayerInVehicle( i, veh ) )
                    {
                        SetPlayerRadio( i, RadioStations [ listitem ][ rUrl ] );
                    }
                }
                VehicleRadio[ veh ] = 1;
                format        ( string, 256, "* %s nustato automagnetolos radijos da�n�." ,GetPlayerNameEx( playerid ) );
                ProxDetector  ( 20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
            }
            else
                ShowPlayerDialog( playerid, 138, DIALOG_STYLE_INPUT, "Radijas", "�veskite radijos stoties URL", "OK", "Cancel");
            return 1;
        }
    }
    else if ( dialogid == 72 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;
            new volume = strval( inputtext );
            if ( volume < 0 || volume > 101 ) return 1;
            format( string, 126, "Automagnetolos garsas buvo nustatytas �: %d", volume );
            SendClientMessage( playerid, COLOR_WHITE, string );
            new veh = GetPlayerVehicleID( playerid );
            foreach(Player,i)
            {
                if ( IsPlayerInVehicle( i, veh ) )
                {
                    SetPlayerRadioVolume( i, volume );
                }
            }
            format      ( string, 256, "* %s pakei�ia automagnetolos grojimo gars�." ,GetPlayerNameEx( playerid ) );
            ProxDetector( 20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
            return 1;
        }
    }
    */
    /*
    else if ( dialogid == 76 )
    {
        if ( response == 1 )
        {
            switch ( listitem )
            {
                case 0:
                {
                    for(new i = 0; i < STATIONS; i++)
                    {
                        format(string, sizeof(string), "%s%s\n",
                                string,
                                RadioStations [ i ][ rName ] );
                    }
                    strcat(string, "�vesti savo stot�");
                    ShowPlayerDialog( playerid, 77, DIALOG_STYLE_LIST, "Radijo stotys", string, "Rinktis", "At�aukti" );
                    return 1;
                }
                case 1:
                {
                    foreach(Audio3D,i)
                    {
                        if ( IsPlayerInDynamicArea( playerid, aInfo[ i ][ aArea ] ) )
                            return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: netoli J�s� jau yra pastatyta magnetola�." );
                    }
                    if ( aInfo[ playerid ][ aObjekt ] > 0 )
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: jau esate pad�j�s magnetol�." );

                    new Float:kordes[ 3 ];
                    GetPlayerPos( playerid, kordes[ 0 ], kordes[ 1 ], kordes[ 2 ]);
                    Itter_Add(Audio3D,playerid);

                    format(aInfo[ playerid ][ aStation ], 128, "");
                    aInfo[ playerid ][ aKords ][ 0 ] = kordes[ 0 ];
                    aInfo[ playerid ][ aKords ][ 1 ] = kordes[ 1 ];
                    aInfo[ playerid ][ aKords ][ 2 ] = kordes[ 2 ];

                    aInfo[ playerid ][ aObjekt ] = CreateDynamicObject( 2103, kordes[ 0 ], kordes[ 1 ], kordes[ 2 ]-1.0, 0, 0, 0, GetPlayerVirtualWorld(playerid));
                    aInfo[ playerid ][ aArea   ] = CreateDynamicCircle( kordes[ 0 ], kordes[ 1 ], 25, GetPlayerVirtualWorld(playerid) );

                    foreach(Player,id)
                    {
                        if ( IsPlayerInDynamicArea( id, aInfo[ playerid ][ aArea ] ) )
                            Set3DAudioForPlayer( id, aInfo[ playerid ][ aStation ], playerid );
                    }

                    format      ( string, 256, "* %s padeda magnetol� ant �em�s ir �jungia radij�." ,GetPlayerNameEx( playerid ) );
                    ProxDetector( 20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
                    return 1;
                }
                case 2:
                {
                    if ( aInfo[ playerid ][ aObjekt ] > 0 )
                    {
                        if(!IsPlayerInRangeOfPoint(playerid, 5.0, aInfo[ playerid ][ aKords ][ 0 ], aInfo[ playerid ][ aKords ][ 1 ], aInfo[ playerid ][ aKords ][ 2 ]))
                            return SendClientMessage(playerid, COLOR_LIGHTRED, "J�s esate per  toli nuo magnetolos.");

                        foreach(Player,id)
                        {
                            if ( IsPlayerInDynamicArea( id, aInfo[ playerid ][ aArea ] ) )
                                    Set3DAudioForPlayer( id, "", playerid );
                        }
                        format(aInfo[ playerid ][ aStation ], 128, "");
                        aInfo[ playerid ][ aKords ][ 0 ] = 0.0;
                        aInfo[ playerid ][ aKords ][ 1 ] = 0.0;
                        aInfo[ playerid ][ aKords ][ 2 ] = 0.0;

                        DestroyDynamicObject( aInfo[ playerid ][ aObjekt ] );

                        if ( IsValidDynamicArea( aInfo[ playerid ][ aArea ] ) )
                            DestroyDynamicArea( aInfo[ playerid ][ aArea ] );

                        aInfo[ playerid ][ aObjekt ] = 0;
                        Itter_Remove(Audio3D,playerid);
                        return 1;
                    }
                    else
                        return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: nesate pastat�s magnetolos." );
                }
            }
        }
        return 1;
    }
    */
    /*
    else if ( dialogid == 77 )
    {
        if ( response == 1 )
        {
            if ( aInfo[ playerid ][ aObjekt ] == 0 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: pirmiausia pad�kite ant �em�s magnetol�." );
            if( listitem < STATIONS )
            {
                format(aInfo[ playerid ][ aStation ], 128, "%s", RadioStations [ listitem ][ rUrl ]);
                foreach(Player,id)
                {
                    if ( IsPlayerInDynamicArea( id, aInfo[ playerid ][ aArea ] ) )
                        Set3DAudioForPlayer( id, RadioStations [ listitem ][ rUrl ], playerid );
                }
                format        ( string, 256, "* %s pakei�ia dabartin� magnetolos da�n�." ,GetPlayerNameEx( playerid ) );
                ProxDetector  ( 20.0, playerid, string, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE, COLOR_PURPLE );
            }
            else
                ShowPlayerDialog( playerid, 140, DIALOG_STYLE_INPUT, "Radijas", "�veskite radijos stot�", "OK", "Cancel");
            return 1;
        }
    }
  */
    
    else if ( dialogid == 91 )
    {
        if ( response == 1 )
        {
            switch( listitem )
            {
                case 0:
                {
                    new nextexp = ( GetPlayerLevel(playerid) + 1 ) * 4;
                    format( string, 349, "{FFFFFF}-Veik�jo OOC informacija\n\
                                            \t- Lygis: \t\t\t%d\n\
                                            \t- Patirtis: \t\t%d/%d\n\
                                            \t- Pra�aista valand�: \t%d\n\
                                            \t- �sp�jimai: \t\t%d\n\
                                            \t- Mirtys: \t\t%d\n\
                                            \t- Darbo lygis: \t\t%d\n\
                                            \t- Darbo patirtis: \t%d/%d\n\
                                          -Veik�jo IC informacija\n\
                                          -Frakcijos ir darbo informacija",
                                          GetPlayerLevel(playerid),
                                          pInfo[ playerid ][ pExp    ], nextexp,
                                          GetPlayerConnectedTime(playerid),
                                          pInfo[ playerid ][ pWarn   ],
                                          GetPlayerDeaths(playerid),
                                          pInfo[ playerid ][ pJobLevel ],
                                          pInfo[ playerid ][ pJobSkill ],
                                      ( ( pInfo[ playerid ][ pJobLevel ] +1 ) * 100) );
                    ShowPlayerDialog( playerid, 92, DIALOG_STYLE_LIST, GetName( playerid ), string, "Rinktis", "I�jungti");
                    return 1;
                }
                case 1:
                {
                    format( string, 349, "{FFFFFF}-Veik�jo OOC informacija\n\
                                                  -Veik�jo IC informacija\n\
                                            \t- Pinigai: \t\t%d\n\
                                                    \t- Pinigai banke: \t%d\n\
                                                    \t- Pinigai ind�lyje: \t%d\n\
                                                    \t- Metai: \t\t%d\n\
                                                    \t- Lytis: \t\t\t%s\n\
                                                    \t- Tautyb�: \t\t%s\n\
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
                    ShowPlayerDialog( playerid, 93, DIALOG_STYLE_LIST, GetName( playerid ), string, "Rinktis", "I�jungti");
                    return 1;
                }
                case 2:
                {
                    format( string, 349, "{FFFFFF}-Veik�jo OOC informacija\n\
                                                  -Veik�jo IC informacija\n\
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
                    ShowPlayerDialog( playerid, 91, DIALOG_STYLE_LIST, GetName( playerid ), string, "Rinktis", "I�jungti");
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
                    format( string, 349, "{FFFFFF}-Veik�jo OOC informacija\n\
                                            \t- Lygis: \t\t\t%d\n\
                                            \t- Patirtis: \t\t%d/%d\n\
                                            \t- Pra�aista valand�: \t%d\n\
                                            \t- �sp�jimai: \t\t%d\n\
                                            \t- Mirtys: \t\t%d\n\
                                            \t- Darbo lygis: \t\t%d\n\
                                            \t- Darbo patirtis: \t%d/%d\n\
                                          -Veik�jo IC informacija\n\
                                          -Frakcijos ir darbo informacija",
                                          GetPlayerLevel(playerid),
                                          pInfo[ playerid ][ pExp    ], nextexp,
                                          GetPlayerConnectedTime(playerid),
                                          pInfo[ playerid ][ pWarn   ],
                                          GetPlayerDeaths(playerid),
                                          pInfo[ playerid ][ pJobLevel ],
                                          pInfo[ playerid ][ pJobSkill ],
                                      ( ( pInfo[ playerid ][ pJobLevel ] +1 ) * 100) );
                    ShowPlayerDialog( playerid, 92, DIALOG_STYLE_LIST, GetName( playerid ), string, "Rinktis", "I�jungti");
                    return 1;
                }
                case 8:
                {
                    format( string, 349, "{FFFFFF}-Veik�jo OOC informacija\n\
                                                  -Veik�jo IC informacija\n\
                                                    \t- Pinigai: \t\t%d\n\
                                                    \t- Pinigai banke: \t%d\n\
                                                    \t- Pinigai ind�lyje: \t%d\n\
                                                    \t- Metai: \t\t%d\n\
                                                    \t- Lytis: \t\t\t%s\n\
                                                    \t- Tautyb�: \t\t%s\n\
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
                    ShowPlayerDialog( playerid, 93, DIALOG_STYLE_LIST, GetName( playerid ), string, "Rinktis", "I�jungti");
                    return 1;
                }
                case 9:
                {
                    format( string, 349, "{FFFFFF}-Veik�jo OOC informacija\n\
                                                  -Veik�jo IC informacija\n\
                                                  -Frakcijos ir darbo informacija\n\
                                                    \t\t- Pavadinimas: \t%s\n\
                                                    \t- Darbo informacija\n\
                                                    \t\t- Pavadinimas: \t%s\n\
                                                    \t\t- Kontraktas: \t%d",
                                                    pInfo[ playerid ][ pMember ],
                                                   // fInfo[ PlayerFaction( playerid ) ][ fName ],
                                                    GetJobName( pInfo[ playerid ][ pJob ] ),
                                                    pInfo[ playerid ][ pJobContr ] );
                    ShowPlayerDialog( playerid, 91, DIALOG_STYLE_LIST, GetName( playerid ), string, "Rinktis", "I�jungti");
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
                    format( string, 349, "{FFFFFF}-Veik�jo OOC informacija\n\
                                            \t- Lygis: \t\t\t%d\n\
                                            \t- Patirtis: \t\t%d/%d\n\
                                            \t- Pra�aista valand�: \t%d\n\
                                            \t- �sp�jimai: \t\t%d\n\
                                            \t- Mirtys: \t\t%d\n\
                                            \t- Darbo lygis: \t\t%d\n\
                                            \t- Darbo patirtis: \t%d/%d\n\
                                          -Veik�jo IC informacija\n\
                                          -Frakcijos ir darbo informacija",
                                          GetPlayerLevel(playerid),
                                          pInfo[ playerid ][ pExp    ], nextexp,
                                          GetPlayerConnectedTime(playerid),
                                          pInfo[ playerid ][ pWarn   ],
                                          GetPlayerDeaths(playerid),
                                          pInfo[ playerid ][ pJobLevel ],
                                          pInfo[ playerid ][ pJobSkill ],
                                      ( ( pInfo[ playerid ][ pJobLevel ] +1 ) * 100) );
                    ShowPlayerDialog( playerid, 92, DIALOG_STYLE_LIST, GetName( playerid ), string, "Rinktis", "I�jungti");
                    return 1;
                }
                case 1:
                {
                    format( string, 349, "{FFFFFF}-Veik�jo OOC informacija\n\
                                                  -Veik�jo IC informacija\n\
                                                    \t- Pinigai: \t\t%d\n\
                                                    \t- Pinigai banke: \t%d\n\
                                                    \t- Pinigai ind�lyje: \t%d\n\
                                                    \t- Metai: \t\t%d\n\
                                                    \t- Lytis: \t\t\t%s\n\
                                                    \t- Tautyb�: \t\t%s\n\
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
                    ShowPlayerDialog( playerid, 93, DIALOG_STYLE_LIST, GetName( playerid ), string, "Rinktis", "I�jungti");
                    return 1;
                }
                case 10:
                {
                    format( string, 349, "{FFFFFF}-Veik�jo OOC informacija\n\
                                                  -Veik�jo IC informacija\n\
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
                    ShowPlayerDialog( playerid, 91, DIALOG_STYLE_LIST, GetName( playerid ), string, "Rinktis", "I�jungti");
                    return 1;
                }
            }
        }
    }
    /*
    else if ( dialogid == 94 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;
            new id = strval( inputtext );
            foreach(Houses,house)
            {
                if (PlayerToPoint( 2.0, playerid, hInfo[ house ][ hEnter ][ 0 ], hInfo[ house ][ hEnter ][ 1 ], hInfo[ house ][ hEnter ][ 2 ] ) )
                {
                    hInfo[ house ][ hGar ] = gInfo[ id ][ gID ];
                    SaveHouse( house );
                    SendClientMessage( playerid, COLOR_WHITE, "Gara�as buvo s�kmingai priskirtas. " );
                    return 1;
                }
            }
        }
        return 1;
    }
    */
    else if(dialogid == 98)
    {
        if(response == 1)
        {
            if(GetPlayerMoney(playerid) < GetPVarInt( playerid, "MOKESTIS" ) )
            {
                SendClientMessage(playerid, COLOR_FADE2, "{FF6347}Persp�jimas: Neturite pakankamai pinig�, kad sumoketum�te baud�.");
                DeletePVar( playerid, "MOKESTIS" );
                SendClientMessage(Offer[playerid][3], COLOR_FADE2, "{FF6347}Persp�jimas: �aid�jas neturi pakankamai pinig�, kad sumok�tu baud�.");
                return 1;
            }
            GivePlayerMoney(playerid,-GetPVarInt( playerid, "MOKESTIS" ));
            SendClientMessage(playerid,COLOR_WHITE,"Bauda sumok�ta");
            SendClientMessage(Offer[playerid][3],COLOR_WHITE,"Jis sumok�jo baud�.");
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
                SendClientMessage(playerid, COLOR_FADE2, "{FF6347}Persp�jimas: Neturite pakankamai pinig�, kad sumoketum�te baud�.");
                DeletePVar( playerid, "MOKESTIS" );
                SendClientMessage(Offer[playerid][3], COLOR_FADE2, "{FF6347}Persp�jimas: �aid�jas neturi pakankamai pinigu, kad sumok�tu baud�.");
                return 1;
            }
            AddPlayerBankMoney(playerid, - GetPVarInt( playerid, "MOKESTIS" ));
            SendClientMessage(playerid,COLOR_WHITE,"Bauda sumok�ta");
            SendClientMessage(Offer[playerid][3],COLOR_WHITE,"Jis sumok�jo baud�.");
            pInfo[ playerid ][ pPaydFines ] += pInfo[ playerid ][ pFines ];
            pInfo[ playerid ][ pFines ] = 0;
            DeletePVar( playerid, "MOKESTIS" );
            Offer[playerid][3] = 255;
            return 1;
        }
    }
    /*
    else if ( dialogid == 100 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;

            new rangas = GetPVarInt( playerid, "FRAKCIJA" ),
                frakcija = tmpinteger[ playerid ],
                alga = strval( inputtext );
            if ( alga < 0 || alga > 8000 )
                return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: Algos suma negali but ma�esn� negu 0 ir didesne negu 8000" );

            format( string, 126, "Frakcijai: %s buvo pakeista alga: %d rangui: %d", fInfo[ frakcija ][ fName ], alga, rangas );
            SendClientMessage( playerid, COLOR_WHITE, string );

            AdminLog( pInfo[ playerid ][ pMySQLID ], -1, string );

            fInfo[ frakcija ][ fPayDay ][ rangas ] = alga;
            SavePayDay( frakcija );

            format( string, 12, "" );
            for( new id; id < MAX_FACTION_RANKS; id++)
                format( string, 256, "%s %d. $%d\n", string, id, fInfo[ frakcija ][ fPayDay ][ id ] );

            ShowPlayerDialog( playerid, 35, DIALOG_STYLE_LIST, "Frakcijos algos keitimas",string,"Rinktis","At�aukti" );
            return 1;
        }
    }
    */
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
                                "PAGAMINIMO KAINA: $1000 u� 50vnt\n\
                                PAGAMINIMO KIEKIS: 50vnt\n\
                                PAGAMINIMO TRUKM�: 2 payday", "GAMINTI", "AT�AUKTI" );
                    }
                    case 1:
                    {
                        ShowPlayerDialog( playerid, 150, DIALOG_STYLE_MSGBOX, "Gaminimo informacija",
                                "PAGAMINIMO KAINA: $1500 u� 100vnt\n\
                                PAGAMINIMO KIEKIS: 100vnt\n\
                                PAGAMINIMO TRUKM�: 2 payday", "GAMINTI", "AT�AUKTI" );
                    }
                    case 2:
                    {
                        ShowPlayerDialog( playerid, 150, DIALOG_STYLE_MSGBOX, "Gaminimo informacija",
                                "PAGAMINIMO KAINA: $1000 u� 25vnt\n\
                                PAGAMINIMO KIEKIS: 25vnt\n\
                                PAGAMINIMO TRUKM�: 4 payday", "GAMINTI", "AT�AUKTI" );
                    }
                    case 3:
                    {
                        ShowPlayerDialog( playerid, 150, DIALOG_STYLE_MSGBOX, "Gaminimo informacija",
                                "PAGAMINIMO KAINA: $1500 u� 50vnt\n\
                                PAGAMINIMO KIEKIS: 50vnt\n\
                                PAGAMINIMO TRUKM�: 3 payday", "GAMINTI", "AT�AUKTI" );
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
                                "PAGAMINIMO KAINA: $1200 u� 30vnt\n\
                                PAGAMINIMO KIEKIS: 30vnt\n\
                                PAGAMINIMO TRUKM�: 3 payday", "GAMINTI", "AT�AUKTI" );
                    }
                    case 1:
                    {
                        tmpinteger[ playerid ] = 5;
                        ShowPlayerDialog( playerid, 150, DIALOG_STYLE_MSGBOX, "Gaminimo informacija",
                                "PAGAMINIMO KAINA: $1300 u� 50vnt\n\
                                PAGAMINIMO KIEKIS: 50vnt\n\
                                PAGAMINIMO TRUKM�: 3 payday", "GAMINTI", "AT�AUKTI" );
                    }
                    case 2:
                    {
                        tmpinteger[ playerid ] = 6;
                        ShowPlayerDialog( playerid, 150, DIALOG_STYLE_MSGBOX, "Gaminimo informacija",
                                "PAGAMINIMO KAINA: $1000 u� 100vnt\n\
                                PAGAMINIMO KIEKIS: 100vnt\n\
                                PAGAMINIMO TRUKM�: 3 payday", "GAMINTI", "AT�AUKTI" );
                    }
                    case 3:
                    {
                        tmpinteger[ playerid ] = 7;
                        ShowPlayerDialog( playerid, 150, DIALOG_STYLE_MSGBOX, "Gaminimo informacija",
                                "PAGAMINIMO KAINA: $1200 u� 30vnt\n\
                                PAGAMINIMO KIEKIS: 30vnt\n\
                                PAGAMINIMO TRUKM�: 3 payday", "GAMINTI", "AT�AUKTI" );
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
                        (Industries[ i ][ IsBuyingCargo ]) ? ("Atidaryta") : ("U�daryta"));
				ShowPlayerDialog(playerid,DIALOG_TPDA_INDUSTRY, DIALOG_STYLE_LIST,"TPDA",str,"T�sti","Atgal");
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
					ShowPlayerDialog(playerid, 9999, DIALOG_STYLE_MSGBOX, "TPDA", "�iuo metu n�ra verlsl� perkan�i� prekes!", "Gerai", "");
				else 
					ShowPlayerDialog(playerid, DIALOG_TPDA_BUSINESS, DIALOG_STYLE_LIST, "TPDA", str, "T�sti", "Atgal");
			}
			case 2:
			{
				if(ShipInfo[ Status ] == Docked)
				{
                    new secs;
                    // Jei dar nebuvo i�plauk�s..
                    if(!ShipInfo[ LastArrivalTimestamp ])
                        secs = CARGOSHIP_DOCKED_INTERVAL / 1000 - (gettime() - ServerStartTimestamp);
                    else 
                        secs = gettime() - ShipInfo[ LastArrivalTimestamp ] + CARGOSHIP_DOCKED_INTERVAL;
                    format(str,sizeof(str),"Statusas: laivas uoste\nLaivas i�plauks u� %2d minu�i� %2d sekund�i�", secs / 60, secs % 60); 
                }
                else if(ShipInfo[ Status ] == Arriving)
                {
                    strcat(str, "Laivas plaukia atgal � uost�....");
                }
				else
                {
                    new secs =  (CARGOSHIP_MOVING_INTERVAL / 1000) - (gettime() - ShipInfo[ LastDepartureTimestamp ]);
                    // �itas kodas taps ne�manomas kai timeriai bus tiksl�s.
                    if(secs <= 0)
                        str = "Statusas: �vartuojasi\nNaujai atvyk�s laivas prad�s priimti krovinius jau netrukus..";
                    else 
                        format(str,sizeof(str),"Statusas: i�plauk�s\nNaujas laivas atplauks u� %2d minu�i� %2d sekund�i�",secs / 60, secs % 60);
                }


                strcat(str, "\n\n\t\t{FFFFFF}Superkamos prek�s\n\n");
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
				ShowPlayerDialog(playerid, DIALOG_SHIP_INFO, DIALOG_STYLE_MSGBOX, "Laivo informacija", str, "Naviguoti", "I�eiti");
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

        format(str, sizeof(str), "{FFFFFF}Sveiki atvyke � {00FF66}%s!\n", Industries[ listitem ][ Name ]);
        strcat(str, "{00FF66}Parduodama: \n");
        format(str, sizeof(str), "%s{C8C8C8}Prek�%sYra sand�lyje(limitas)\t\tKaina\tPagaminama per valand�\n",
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
        if(!GetIndustryBoughtCommodityCount(listitem)) // Jei n�ra preki� perkam�.
            strcat(str, "{C8C8C8}�i firma nieko neperka");
        else 
        {
            strcat(str,"{C8C8C8}Prek�                  Yra sand�lyje (limitas)\t\tKaina\tSunaudojama per valand�\n");
            // Ilgas kodo gabalas kad sud�t s�ra�� perkam� dalyk�
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
        ShowPlayerDialog( playerid, DIALOG_INDUSTRY_INFO, DIALOG_STYLE_MSGBOX, Industries[ listitem ][ Name ], str, "Pa�ym�ti", "Atgal" );
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
        format(str, sizeof(str), "{FFFFFF}Sveiki atvyke � {00FF66}%s!\n\n", bInfo[ index ][ bName ]);
       
        strcat(str, "{00FF66}\nPerkama: \n");
        if(!GetBusinessBoughtCommodityCount(index)) // Jei n�ra preki� perkam�.
            strcat(str, "{C8C8C8}�i firma nieko neperka");
        else 
        {
            strcat(str,"{C8C8C8}Prek�\t\tYra sand�lyje (limitas)\t\tKaina\n");
            // Ilgas kodo gabalas kad sud�t s�ra�� perkam� dalyk�
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
        ShowPlayerDialog( playerid, DIALOG_INDUSTRY_INFO, DIALOG_STYLE_MSGBOX, bInfo[ index ][ bName ], str, "Pa�ym�ti", "Atgal" );
        return 1;
    }   */

    // Jei pasirinko OK, tiesiog nustatyt CP.
    if(dialogid == DIALOG_INDUSTRY_INFO)
    {
        if(!response)
            return OnDialogResponse(playerid, DIALOG_TPDA_MAIN, 1, 0, "Per�i�r�ti visas industrijas");
			
		if ( Checkpoint[ playerid ] != CHECKPOINT_NONE ) 
			return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: KLAIDA!" );

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
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, pasirinkto krovinio/prek�s jau neb�ra sandalyje.");


        // Jei �aid�jas forklifte, parkaunam ant jo.
        if(GetVehicleModel(GetPlayerVehicleID(playerid)) == 530)
        {
			if(!IsCargoCompatibleWithVehicle(cargoid, GetVehicleModel(GetPlayerVehicleID(playerid))))
				return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �io krovinio negalite pasiimti.");
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
    // S�ra�as biznio/industrijos preki�. Pasirinkimas = Pirkimas
    if(dialogid == DIALOG_SOLD_COMMODITY_LIST)
    {
        if(!response) return 1;

        // If the first bit is turned on, it's an industry NOT a business.
        new index = GetPVarInt(playerid, "Industry_Index"), 
            count = 0,
            commodityIndex = -1;
        // Sud�tingas ir kvailas b�das gaut pasirinktai prekei.
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
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, pasirinkto krovinio/prek�s jau neb�ra sandalyje.");

        if(GetPlayerMoney(playerid) < Commodities[ commodityIndex ][ Price ])
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, neturite pakankamai gryn� pinig�, kad nusipirktum�te �i� prek�.");

        if(IsCargoCarryable(Commodities[ commodityIndex ][ CargoId ]))
        {
            // Jei jis forklifte
            new vehicleid = GetPlayerVehicleID(playerid);
            if(GetVehicleModel(vehicleid) == 530)
            {
                if(!HasVehicleSpaceForCargo(vehicleid, Commodities[ commodityIndex ][ CargoId ]))
                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, autokaras jau yra pilnas ir negali daugiau pakelti krovini�");

                AddCargoToVehicle(vehicleid, Commodities[ commodityIndex ][ CargoId ]);
                GivePlayerMoney(playerid, -Commodities[ commodityIndex ][ Price ]);
                return 1;
            }   

            if(IsPlayerAttachedObjectSlotUsed(playerid, 7))
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo, kadangi rankose jau turite krovin�/prek�.");
            if(IsPlayerInAnyVehicle(playerid))
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, pirmiausi� i�lipkite i� tr. priemon�s, kad paiimtum�te krovin�.");

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
                return SendClientMessage(playerid, GRAD, "Klaida, J�s� tr. priemon� n�ra tinkama krovinio/prek�s gabenimui arba jos n�ra �alia J�s�.");

            if(IsTrailerAttachedToVehicle(vehicleid))
                vehicleid = GetVehicleTrailer(vehicleid);

			if(!CanPlayerUseTruckerVehicle(playerid,GetVehicleModel(vehicleid)))
				return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, J�s neturite pakankamai krovini� perve�imo darbuotojo patirties, kad gal�tum�te dirbti su �ia tr. priemone.");
            if(cInfo[ vehicleid ][ cLock ])
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo, kadangi sunkve�imis u�rakintas");
            if(IsVehicleLoaded[ vehicleid ])
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �iuo metu vyksta tr. priemon�s pakrovimas.");
            if(!IsCargoCompatibleWithVehicle(Commodities[ commodityIndex ][ CargoId ], GetVehicleModel(vehicleid)))
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �i tr. priemon� yra netinkama J�s� pasirinktam kroviniui.");
            if(!HasVehicleSpaceForCargo(vehicleid, Commodities[ commodityIndex ][ CargoId ]))
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, sunkve�imyje n�ra laisvos vietos J�s� kroviniui.");

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
            SendClientMessage(playerid, COLOR_NEWS, "D�mesio, J�s� pasirinktas krovinys/prek� bus pakrauta � J�s� tr. priemon� per 60 sekund�i�. Pra�ome palaukti.");
        }
		RemoveCargoFromIndustry(index, Commodities[ commodityIndex ][ CargoId ]);
        UpdateIndustryInfo(index);
        format(string,sizeof(string),"S�kmingai nusipirkot� prek�/krovin� pavadinimu: %s, u� kuri� sumok�jote %d$", 
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
			
		// I�siai�kinam kam parduodam.
		if(GetPVarInt(playerid,"CommoditySellTo") == 1)
			sellToBusines = true;
		else if(GetPVarInt(playerid, "CommoditySellTo") == 2)
			sellToIndustry = true;
		else 
			sellToShip = true;
			
		// Susirandam cargo ID prek�s pasirinktos.
		strmid(tmp, inputtext,0, strfind(inputtext,"\t"));
		cargoid = strval(tmp);

        // Ar niekas kitas nei��m� krovinio.
        if(!IsCargoInVehicle(vehicleid, cargoid))
            return SendClientMessage(playerid, GRAD, "Klaida, �io krovinio neb�ra.");
		
		// O dabar jau pradedam pardavima...
		
		// Susi�inom KIEK gali nupirkti industrija/verslas
		if(sellToIndustry)
		{	
            if(!Industries[ index ][ IsBuyingCargo ])
                return SendClientMessage(playerid, GRAD, "Klaida, d�l produkt� pertekliaus �i industrija nedirba.");
            if(!HasIndustryRoomForCargo(index, cargoid))
                    return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, industrija �iai prekei nebeturi vietos.");
			boughtamount = GetCargoLimit(cargoid) - GetIndustryCargoStock(index,cargoid); // Kiek GALI pirk industrija

			if(boughtamount > GetVehicleCargoCount(vehicleid, cargoid))
				boughtamount = GetVehicleCargoCount(vehicleid,cargoid);
		
			
			if(!boughtamount)
				return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo, kadangi industrijos sand�lys pilnas.");
			
            price = Commodities[ GetIndustryCargoIndex(index, cargoid) ][ Price ] * boughtamount;
            if(!price)
                return SendClientMessage(playerid, GRAD, "Klaida, J�s� tr. priemon�je n�ra preki�, kurias superka pasirinktas fabrikas.");
			
			// duodam viska industrijai
			AddCargoToIndustry(index, cargoid, boughtamount);
			
			// I�imam visk� i� transporto priemo�ns.
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
                return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �is verslas nebeperka preki�.");
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
                return SendClientMessage(playerid, 0xFF0000FF, "KLAIDA. Prane�kite apie tai administracijai.");
            }
            // end of debug code
			
			// Jei verlslas perka daugiau negu gali �pirkti
			if(boughtamount * Commodities[ commodityIndex ][ Price ] > bInfo[ index ][ bBank ])
				boughtamount = bInfo[ index ][ bBank ] / Commodities[ commodityIndex ][ Price ];

            // Debug code
            if(boughtamount < 0)
            {
                new s[128];
                format(s, sizeof(s), "Second. Boughtamount:%d. Current stock:%d VehicleCargoCount:%d commodity index:%d biz index:%d", 
                    boughtamount, Commodities[ commodityIndex ][ CurrentStock ],  GetVehicleCargoCount(vehicleid, cargoid), commodityIndex, index);
                ImpossibleLog(s);
                return SendClientMessage(playerid, 0xFF0000FF, "KLAIDA. Prane�kite apie tai administracijai.");
            }
            // end of debug code

			price = Commodities[ commodityIndex ][ Price ] * boughtamount;
            if(!price)
                return SendClientMessage(playerid, GRAD, "Klaida, J�s� tr. priemon�je n�ra preki�, kurias superka pasirinktas verslas.");
            printf("Boughtamount:%d price:%d stock:%d",boughtamount, price, GetBusinessCargoStock(index));
			AddCargoToBusiness(index, cargoid, boughtamount);
			bInfo[ index ][ bBank ] -= price;
			
			// I�imam visk� i� transporto priemo�ns.
			RemoveCargoFromVehicle(vehicleid, cargoid, boughtamount);

            if(bInfo[ index ][ bBank ] < Commodities[ commodityIndex ][ Price ])
            {
                // reik surast savinink� ir pasakyk kad kapeikos baig�s :(
                foreach(Player,j)
                    if(pInfo[ j ][ pMySQLID ] == bInfo[ index ][ bOwner ])
                    {
                        SendClientMessage(j, GRAD, "J�s� versle baig�si pinigai, daugiau produkt� nebepirksite.");
                        StopBusinessBuyingCargo(index, cargoid);
                        break;
                    }
            }
		}*/
		
		if(sellToShip)
		{
			if(ShipInfo[ Status ] != Docked)
				return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, �iuo metu krovininis laivas yra i�plauk�s. Naudodami komanda /tpda galite pamatyti kada jis atplauks.");
			// Laivas didelis, supirks VISAS kurias turi.
			boughtamount = GetVehicleCargoCount(vehicleid,cargoid);
			
			price = GetShipCargoPrice(cargoid) * boughtamount;
            if(!price)
                return SendClientMessage(playerid, GRAD, "Klaida, J�s� tr. priemon�je n�ra preki�, kurias superka laivas.");
			ShipInfo[ CurrentStock ] = boughtamount * GetCargoSlot(cargoid);
			
			// I�imam visk� i� transporto priemo�ns.
			RemoveCargoFromVehicle(vehicleid, cargoid, boughtamount);
		}
		
		format(string,sizeof(string), "[FABRIKAS] S�kmingai pardav�te visas savo pakrautas prekes/krovinius, i� kuri� u�dirbote %d$.",price);
		GivePlayerMoney(playerid, price);
		SendClientMessage(playerid, COLOR_LIGHTRED2, string);
		return 1;
	}
   
    /*
    else if ( dialogid == 104 )
    {
        if( !response )
            return true;
        switch( listitem )
        {
            case 0:
            {
                if( !GetPVarInt( playerid, "oKepure" ) )
                {
                    SetPlayerAttachedObject(playerid, 0, 18636, 2, 0.132999,-0.011999,0.004000,-177.199996,-2.200002,30.799991);
                    EditAttachedObject(playerid, 0);
                    SendClientMessage(playerid, 0xFFFFFFFF, "Nor�dami pasukti/pakeisti kamer� laikykite klavi�us: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
                    SetPVarInt( playerid, "oKepure", true );
                }
                else
                {
                    SetPVarInt( playerid, "oKepure", false );
                    SendClientMessage( playerid, COLOR_WHITE,"Daiktas buvo s�kmingai panaikintas/nuimtas.");
                    RemovePlayerAttachedObject( playerid, 0 );
                }
            }
            case 1:
            {
                if( !GetPVarInt( playerid, "oKepure" ) )
                {
                    SetPlayerAttachedObject(playerid, 0, 19099, 2, 0.132999,-0.011999,0.004000,-177.199996,-2.200002,30.799991);
                    EditAttachedObject(playerid, 0);
                    SendClientMessage(playerid, 0xFFFFFFFF, "Nor�dami pasukti/pakeisti kamer� laikykite klavi�us: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
                    SetPVarInt( playerid, "oKepure", true );
                }
                else
                {
                    SetPVarInt( playerid, "oKepure", false );
                    SendClientMessage( playerid, COLOR_WHITE,"Daiktas buvo s�kmingai panaikintas/nuimtas.");
                    RemovePlayerAttachedObject( playerid, 0 );
                }
            }
            case 2:
            {
                if( !GetPVarInt( playerid, "oKepure" ) )
                {
                    SetPlayerAttachedObject(playerid, 0, 19100, 2, 0.132999,-0.011999,0.004000,-177.199996,-2.200002,30.799991);
                    EditAttachedObject(playerid, 0);
                    SendClientMessage(playerid, 0xFFFFFFFF, "Nor�dami pasukti/pakeisti kamer� laikykite klavi�us: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
                    SetPVarInt( playerid, "oKepure", true );
                }
                else
                {
                    SetPVarInt( playerid, "oKepure", false );
                    SendClientMessage( playerid, COLOR_WHITE,"Daiktas buvo s�kmingai panaikintas/nuimtas.");
                    RemovePlayerAttachedObject( playerid, 0 );
                }
            }
            case 3:
            {
                if( !GetPVarInt( playerid, "oAkiniai" ) )
                {
                    SetPlayerAttachedObject(playerid, 2, 19138, 2, 0.100000,0.050000,-0.004999,90.000000,90.000000,0.000000);
                    EditAttachedObject(playerid, 2);
                    SendClientMessage(playerid, 0xFFFFFFFF, "Nor�dami pasukti/pakeisti kamer� laikykite klavi�us: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
                    SetPVarInt( playerid, "oAkiniai", true );
                }
                else
                {
                    SetPVarInt( playerid, "oAkiniai", false );
                    SendClientMessage( playerid, COLOR_WHITE,"Daiktas buvo s�kmingai panaikintas/nuimtas.");
                    RemovePlayerAttachedObject( playerid, 2 );
                }
            }
            case 4:
            {
                if( !GetPVarInt( playerid, "oKepure" ) )
                {
                    SetPlayerAttachedObject(playerid, 0, 19141, 2, 0.11, 0.0, 0.0, 0.0, 0.0, 0.0);
                    EditAttachedObject(playerid, 0);
                    SendClientMessage(playerid, 0xFFFFFFFF, "Nor�dami pasukti/pakeisti kamer� laikykite klavi�us: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
                    SetPVarInt( playerid, "oKepure", true );
                }
                else
                {
                    SetPVarInt( playerid, "oKepure", false );
                    SendClientMessage( playerid, COLOR_WHITE,"Daiktas buvo s�kmingai panaikintas/nuimtas.");
                    RemovePlayerAttachedObject( playerid, 0 );
                }
            }
            case 5:
            {
                if( !GetPVarInt( playerid, "oKepure" ) )
                {
                    SetPlayerAttachedObject(playerid, 0, 19200, 2, 0.11, 0.0, 0.0, 0.0, 0.0, 0.0);
                    EditAttachedObject(playerid, 0);
                    SendClientMessage(playerid, 0xFFFFFFFF, "Nor�dami pasukti/pakeisti kamer� laikykite klavi�us: {FFFF00}~k~~PED_SPRINT~{FFFFFF}.");
                    SetPVarInt( playerid, "oKepure", true );
                }
                else
                {
                    SetPVarInt( playerid, "oKepure", false );
                    SendClientMessage( playerid, COLOR_WHITE,"Daiktas buvo s�kmingai panaikintas/nuimtas.");
                    RemovePlayerAttachedObject( playerid, 0 );
                }
            }
            case 6:
            {
                if( IsPlayerAttachedObjectSlotUsed ( playerid, 4 ) )
                    return RemovePlayerAttachedObject ( playerid, 4 );

                SetPlayerAttachedObject(playerid, 4, 18637, 5, -0.064955, -0.135697, -0.200892, 54.463840, 10.984453, 87.706436, 1.078429, 1.000000, 1.000000 );
                EditAttachedObject(playerid, 4);
            }
        }
    }
    */

    else if( dialogid == 129 )
    {
        if(!response)
            return 1;
            
        if( isnull( inputtext ) )
            return SendClientMessage( playerid, COLOR_LIGHTRED,"D�mesio, ne�ra��te joki� �od�i� � pateikt� laukel�");
            
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
        format(dialog, sizeof(dialog), "Paie�komas asmuo: %s", inputtext);
        format(dialog, sizeof(dialog), "%s\nAm�ius: %d (gim� %d)", dialog, sAge, dates[0] - sAge);
        format(dialog, sizeof(dialog), "%s\nAsmens kodas: %s", dialog, IDNumber );
        format(dialog, sizeof(dialog), "%s\nLytis: %s", dialog, sGender);
        format(dialog, sizeof(dialog), "%s\nTautyb�: %s", dialog, nationality);
        if( sPhoneNumber == 0 )
            format(dialog, sizeof(dialog), "%s\nTelefono numeris: N�ra", dialog);
        else
            format(dialog, sizeof(dialog), "%s\nTelefono numeris: %d", dialog, sPhoneNumber);

        if( test[2] > 0 )
            format(dialog, sizeof(dialog), "%s\nPilietis ie�komas!", dialog);
            
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

            format( dialog, sizeof( dialog ),"%s\n-->�skaita: %s\n", dialog, result2[0] );
            format( dialog, sizeof( dialog ), "%sPolicininkas: %s\n", dialog, result2[1] );
            format( dialog, sizeof( dialog ), "%sLaikas: %s", dialog, result2[2] );
        }
        
        cache_delete(result);
        ShowPlayerDialog( playerid, 136, DIALOG_STYLE_LIST,"Informacija rasta", dialog,
            "U�daryti", "" );
    }
    else if( dialogid == 130 )
    {
        if(!response)
            return 1;

        if(isnull(inputtext))
            return SendClientMessage(playerid, COLOR_LIGHTRED,"Klaida, ne�ra��te tr. priemon�s numeri�.");

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
            SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, transporto priemon� nerasta.");
        else 
        {
            cache_get_field_content(0, "cName", dialog);
            cache_get_field_content(0, "name", name);

            format(dialog, sizeof(dialog), "DUOM. BAZ�: Tr. Priemon�s pavadinimas: %s\n\
                DUOM. BAZ�: Tr. Priemon�s numeriai: %s\n\ 
                DUOM. BAZ�: Tr. Priemon�s draudimas: %d\n\
                DUOM. BAZ�: Tr. Priemon�s savininkas: %s\n", 
                dialog, 
                inputtext,
                cache_get_field_content_int(0, "cInsurance"),
                name);

            if(cache_get_field_content_int(0, "cCrimes"))
                strcat(dialog, "Tr. priemon� ie�koma.");

            // Processinam arrestedcars duomenis
            cache_get_field_content(0, "who", name);
            if(!ismysqlnull(name))
            {
                cache_get_field_content(0, "Time", tmp);
                format(dialog, sizeof(dialog), "%sTr. priemon� buvo are�tuota!\nPareig�nas: %s\nLaikas ir data: %s", dialog, name, tmp);
            }

            // Processinam carcrimes
            for(new i = 0; i < cache_get_row_count(); i++)
            {
                cache_get_field_content(i, "reporter", name);
                cache_get_field_content(i, "crime", string);
                cache_get_field_content(i, "when", tmp);
                if(ismysqlnull(name))
                    break;
                format(dialog, sizeof(dialog), "%s-->�skaita: %s\nPolicininkas: %s\nLaikas: %s", dialog, string, name, tmp);
            }
            ShowPlayerDialog(playerid, 136, DIALOG_STYLE_LIST,"Informacija rasta", dialog, "U�daryti", "" );
        }
        cache_delete(result);
      
    }
    /*
    else if( dialogid == 132 )
    {
        if(!response)
            return 1;

        if( isnull( inputtext ) )
            return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, ne�ra��te vardo ir pavard�s � pateikta laukel�" );

        else if(PlayerFaction( playerid ) == 1)
        {
            SetPVarString ( playerid, "nick", inputtext );
            ShowPlayerDialog( playerid, 133, DIALOG_STYLE_INPUT,"�tariam�j� prid�jimas",
            "{1797cd}LOS SANTOS POLICE DEPARTAMENT\n\
				{FFFFFF}�ra�ykite prie�asti d�l ko �traukti �tariamaj�", "Prid�ti", "U�daryti" );
        }
    }
    */
    /*
    else if( dialogid == 133 )
    {
        if(!response)
            return 1;

        new
            name[ 32 ],
            bool:found = false;

        if( isnull( inputtext ) )
            return SendClientMessage(playerid, COLOR_GREY, "Ne�ra��te prie�asties !" );

        else if(PlayerFaction( 1 ) == playerid)
        {
            GetPVarString ( playerid, "nick", name, sizeof(name) );
            foreach(Player,i)
            {
                if(!strcmp(GetName( i ),name,true))
                {
                    SuspectPlayer(i,inputtext,GetName( playerid ));
                    found = true;
                    break;
                }
            }
            if(!found)
            {
                format(string, 256, "%s gavo �skait� d�l %s nuo pareig�no %s",name,inputtext,GetName( playerid ));
                SendTeamMessage( 1, COLOR_BLUE, string );
                format(string, 256, "INSERT INTO `crimes` (name,crime,reporter) VALUES ('%s','%s','%s')",name,inputtext,GetName(playerid));
                mysql_query(DbHandle, string, false);
                format(string, 256, "UPDATE `players` SET `WantedLevel`=`WantedLevel`+1 WHERE `Name`='%s' AND `Jailed` = 0 AND `JailTime` = 0", name );
                mysql_query(DbHandle, string, false);
            }
            DeletePVar ( playerid, "nick" );
        }
    }
    */
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
            return SendClientMessage( playerid, COLOR_WHITE,"Ne�ra��te tr. priemon�s numerio ! ");
        mysql_real_escape_string( inputtext, inputtext, DbHandle, 128);
        SetPVarString ( playerid, "CarNumber", inputtext );
        ShowPlayerDialog( playerid, 135, DIALOG_STYLE_INPUT,"Prid�ti ie�koma ma�in� (2)",
        "{1797cd}LOS SANTOS POLICE DEPARTAMENT\n\
		{FFFFFF}�ra�ykite prie�ast� kod�l tr. priemon� paie�koma", "Prid�ti", "At�aukti " );
    }
    /*
    else if( dialogid == 135 )
    {
        if(!response)
            return 1;
        if( isnull( inputtext ) )
            return SendClientMessage( playerid, COLOR_WHITE,"Ne�ra��te prie�asties ! ");
            
        new
            string2[ 128 ],
            rows,
            tmpnumbers;

        mysql_real_escape_string( inputtext, inputtext, DbHandle, 128);
        GetPVarString ( playerid, "CarNumber", string2, sizeof(string2) );

        format     ( string, 128, "SELECT id,cCrimes FROM vehicles WHERE cNumbers = '%s'", string2);
        new Cache:result = mysql_query(DbHandle,  string );
        rows = cache_get_row_count();

        if( rows )
        {
            rows = cache_get_field_content_int(0, "id");
            tmpnumbers = cache_get_field_content_int(0, "cCrimes");
        }

        format     ( string, 256, "INSERT INTO carcrimes (numbers,crime,reporter) VALUES ('%s','%s','%s')", string2, inputtext, GetName( playerid ) );
        mysql_query(DbHandle,  string, false);

        format         ( string, 128 ,"[LSPD] Tr. priemon�, kurios valstybiniai numeriai %s buvo �trauka pareig�no %s � �skaita.", string2, GetName( playerid ) );
        SendTeamMessage( 1, COLOR_POLICE, string );

        format         ( string, 128, "[LSPD] Nurodyta �skaitos prie�astis: %s", inputtext );
        SendTeamMessage( 1, COLOR_POLICE, string );

        new vehiclebynumbers = checkVehicleByNumbers( string2 );
        if( !checkArrestedCar( playerid, rows, 0 ) )
        {
            if ( vehiclebynumbers != INVALID_VEHICLE_ID )
            {
                cInfo[ vehiclebynumbers ][ cCrimes ] ++;
                if ( cInfo[ vehiclebynumbers ][ cCrimes ] > 6 )
                cInfo[ vehiclebynumbers ][ cCrimes ] = 6;
            }
            else
            {
                format     ( string, 128, "UPDATE vehicles SET cCrimes = %d WHERE cNumbers = '%s'", tmpnumbers + 1, string2);
                mysql_query(DbHandle,  string,false);
            }
        }
        cache_delete(result);
    }
    */
  
    if(dialogid == INTERIORMENU)
    {
        if(response)
        {
            if(listitem == 18) // {FF0000} Custom Interriorai
            {
            ShowPlayerDialog(playerid, INTERIORMENU+19, DIALOG_STYLE_LIST, "Custom mapai","Po�eminis Casino\n\
                                                                                      Bilijardo Baras\n\
                                                                                      MD \n\
                                                                                      Bankas\n\
                                                                                      Bandidos Baras \n\
                                                                                      Mantom Baras \n\
                                                                                      Savivaldyb� \n\
                                                                                      \nAtgal", "Pasirinkti", "At�aukti");
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
            if(listitem == 6) // Savivaldyb�
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

/*
stock ShowAdminMenu( playerid, type, tmp )
{
    switch( type )
    {
        case 0:
        {
            new bigstring[256];
            format( bigstring, sizeof(bigstring), "%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n",fInfo[ tmp ][ fRank1 ],
                                                                                fInfo[ tmp ][ fRank2 ],
                                                                                fInfo[ tmp ][ fRank3 ],
                                                                                fInfo[ tmp ][ fRank4 ],
                                                                                fInfo[ tmp ][ fRank5 ],
                                                                                fInfo[ tmp ][ fRank6 ],
                                                                                fInfo[ tmp ][ fRank7 ],
                                                                                fInfo[ tmp ][ fRank8 ],
                                                                                fInfo[ tmp ][ fRank9 ],
                                                                                fInfo[ tmp ][ fRank10 ],
                                                                                fInfo[ tmp ][ fRank11 ],
                                                                                fInfo[ tmp ][ fRank12 ],
                                                                                fInfo[ tmp ][ fRank13 ]);

            ShowPlayerDialog( playerid, 36, DIALOG_STYLE_LIST, "Rangai" ,bigstring, "Keisti", "At�aukti" );
            return 1;
        }
    }
    return 1;
}
*/
/*
stock Set3DAudioForPlayer( playerid, station[ ], radio )
{
    if ( Audio_IsClientConnected( playerid ) == 0 )
    {
        if ( station[ 0 ] == 0 )
        {
            StopAudioStreamForPlayer( playerid );
            RadioName   [ playerid ] = 99;
            return 1;
        }
        PlayAudioStreamForPlayer( playerid, station, aInfo[ radio ][ aKords ][ 0 ], aInfo[ radio ][ aKords ][ 1 ], aInfo[ radio ][ aKords ][ 2 ], 30.0, 1);
        RadioName[ playerid ] = 2;
        return 1;
    }

    if ( RadioStation[ playerid ] != 99 )
        Audio_Stop( playerid, RadioStation[ playerid ] );
    if ( station[ 0 ] == 0 )
    {
        Audio_Stop( playerid, RadioStation[ playerid ] );
        RadioStation[ playerid ] = 99;
        RadioName   [ playerid ] = 99;
        return 1;
    }
    RadioStation[ playerid ] = Audio_PlayStreamed( playerid, station, false, false, false);
    Audio_Set3DPosition( playerid, RadioStation[ playerid ], aInfo[ radio ][ aKords ][ 0 ], aInfo[ radio ][ aKords ][ 1 ], aInfo[ radio ][ aKords ][ 2 ], 30.0);
    RadioName[ playerid ] = 2;
    return 1;
}*/

stock GetWeaponObjectModel(weaponid)
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
stock GetSpaceString(amount)
{
    new string[64];
    for(new i = 0; i < amount; i++)
        strcat(string, " ");
    return string;
}
/*
stock StopPlayerRadio( playerid )
{
    if ( Audio_IsClientConnected( playerid ) )
        Audio_Stop( playerid, RadioStation[ playerid ] );
    else
        StopAudioStreamForPlayer( playerid );

    RadioStation[ playerid ] = 99;
    RadioName   [ playerid ] = 99;
    return 1;
}

stock SetPlayerRadio( playerid, radio[ ] )
{
    if ( Audio_IsClientConnected( playerid ) == 0 )
    {
        PlayAudioStreamForPlayer( playerid, radio );
        RadioName[ playerid ] = 1;
        return 1;
    }
    if ( RadioStation[ playerid ] != 99 )
        Audio_Stop( playerid, RadioStation[ playerid ] );

    RadioStation[ playerid ] = Audio_PlayStreamed( playerid, radio, false, false, false);
    RadioName   [ playerid ] = 1;

    if ( GetRadioVolume( playerid ) == 0 )
        SetPVarInt( playerid, "VOLUME", 100 );
    else
        SetPlayerRadioVolume( playerid, GetRadioVolume( playerid ) );

    return 1;
}

stock SetPlayerRadioVolume( playerid, volume )
{
    SetPVarInt( playerid, "VOLUME", volume );
    Audio_SetVolume( playerid, RadioStation[ playerid ], volume );
    return 1;
}
stock GetRadioVolume( playerid )
    return GetPVarInt( playerid, "VOLUME" );

*/
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
/*
stock MySQL_Check_Account( playerid )
{
    new string[ 90 ],
        plname[ MAX_PLAYER_NAME ],
        bool:ret = true;

    GetPlayerName( playerid, plname, 24 );


    mysql_format(DbHandle, string, sizeof(string), "SELECT `Name` FROM `players` WHERE `Name` = '%e' LIMIT 1", plname);
    new Cache:result = mysql_query(DbHandle,  string );
    if(cache_get_row_count( ) > 0 )
		{
			SendClientMessage( playerid, COLOR_LIGHTRED2,"Sveiki, J�s s�kmingai prisijung�te prie Lithuanian role-play serverio."),
			SendClientMessage( playerid, COLOR_LIGHTRED2,"Serveris �iuo metu tikrina J�s� vartotojo duomenis, pra�ome palaukti...");
		}
    else
    {
        SendClientMessage( playerid, COLOR_LIGHTRED,"Klaida, vartotojas su kuriuo jungiat�s � m�s� server� neegzistuoja m�s� duomen� baz�je, patikrinkite ar jis tikrai sukurtas ltrp.lt." );			
        SetTimerEx("KicknPlayer", 100, false, "d", playerid );
        ret = false;
    }
    cache_delete(result);
    return ret;
}



FUNKCIJA:MySQL_Load_Player(extraid, password[])
{
    new string[ 300 ],
        sqlid,
        question[ 128 ],
        answer[ 129 ];
    mysql_real_escape_string(password, password, DbHandle, 129);

    format(string, sizeof(string), "SELECT id, secret_question, secret_answer FROM `players` WHERE Name = '%s' AND Password = '%s' LIMIT 1", GetName( extraid ), password );
    new Cache:result = mysql_query(DbHandle, string);

    if(cache_get_row_count())
    {
        sqlid = cache_get_field_content_int(0, "id");
        cache_get_field_content(0, "secret_question", question);
        cache_get_field_content(0, "secret_answer", answer);
        
        SetPVarInt(extraid, "TmpSqlId", sqlid);


        // Jei n�ra nustat�s klausimo/atskaymo, tur�s tai padaryti dabar.
        if(isnull(question) || isnull(answer))
        {
            ShowPlayerQuestionSetDialog(extraid);
        }
        else 
        {
            SetPVarString(extraid, "SecretQuestion", question); // Reik�s jei tekt� nety�ia dar kart� rodyt �it� GUI.
            SetPVarString(extraid, "SecretAnswer", answer); // Mums jo reik�s tikrinant k� �ved�.
            ShowPlayerSecretQuestionDialog(extraid, question);
        }
        
    }
    else
    {
        ShowPlayerLoginDialog(extraid);
    }
    cache_delete(result);
    return true;
}*/

ShowPlayerLoginDialog(playerid, errorstr[] = "")
{
    GetPlayerIp(playerid, pInfo[ playerid ][ pConnectionIP ], 18);
    new kayitmsg[512];
    format(kayitmsg,sizeof(kayitmsg),"{AA1000}%s\n\
        {FFFFFF} Sveiki prisijung� � {cca267}Lithuanian role-play (ltrp.lt){FFFFFF} server�, dabar galite prisijungti\n\n\
         Vartotojas: {cca267}%s\n{FFFFFF}  Skripto versija: {cca267}" #VERSION "{FFFFFF}, atnaujintas: {cca267}" #BUILD_DATE "\n\n\
         {FFFFFF}�veskite slapta�od�:", errorstr, GetName( playerid ));
    ShowPlayerDialog(playerid, 0, DIALOG_STYLE_PASSWORD,"Prisijungimas", kayitmsg, "Jungtis", "I�jungti");
    return 1;
}

stock ShowPlayerSecretQuestionDialog(playerid, question[], errostr[] = "")
{
    new content[ 512 ];
    format(content, sizeof(content), "{AA1100}%s\n{FFFFFF}Kad prisijungtum�te, atsakykite � savo pasirinkt� klausim�. \n\n%s?", errostr, question);
    ShowPlayerDialog(playerid, DIALOG_SECRET_QUESTION, DIALOG_STYLE_PASSWORD, "Prisijungimas: atsakymas", content, "Jungtis", "I�eiti");
    return 1;
}
stock ShowPlayerQuestionSetDialog(playerid, errostr[] = "")
{
    new string[256];
    format(string, sizeof(string), "{AA1100}%s\n{FFFFFF}Sugalvokite klausim� � kur� gal�tum�te atsakyti tik j�s patys.\n� j� atskayti tur�site kas kart� jungdamiesi, tai u�trikins saugum�.", errostr);
    ShowPlayerDialog(playerid, DIALOG_SECRET_QUESTION_SET, DIALOG_STYLE_INPUT, "Saugos klausimas", string, "T�sti", "I�eiti");
    return 1;
}
stock ShowPlayerAnswerSetDialog(playerid, question[], errostr[] = "")
{
    new string[512];
    format(string, sizeof(string), "{AA1100}%s\n{FFFFFF}J�s� klausimas:%s?\n�veskite atsakym� ir nepamir�kite jo.", errostr, question);
    ShowPlayerDialog(playerid, DIALOG_SECRET_ANSWER_SET, DIALOG_STYLE_PASSWORD, "Saugos atsakymas", string, "T�sti", "I�eiti");
    return 1;
}
/*
public OnPlayerLoginEx(playerid, sqlid)
{
    #if defined DEBUG
        printf("OnPlayerLoginEx(%s, %d)", GetName(playerid), sqlid);
    #endif
    new string[1024],
        Cache:result;
    format(string, sizeof(string), "SELECT * FROM players WHERE id = %d", sqlid);
    result = mysql_query(DbHandle, string);

    if(cache_get_row_count())
    {
        PlayerPlaySound( playerid, 1057, 0.0, 0.0, 0.0);
        pInfo[ playerid ][ pMySQLID ] = cache_get_field_content_int(0, "id");
        pInfo[ playerid ][ pLevel ] = cache_get_field_content_int(0, "Level");
        pInfo[ playerid ][ pAdmin ] = cache_get_field_content_int(0, "AdminLevel");
        pInfo[ playerid ][ pOnTime ] = cache_get_field_content_int(0, "ConnectedTime");
        pInfo[ playerid ][ pBoxStyle ] = cache_get_field_content_int(0, "BoxStyle");
        cache_get_field_content(0, "Sex", pInfo[ playerid ][ pSex ], DbHandle, 12);
        pInfo[ playerid ][ pAge ] = cache_get_field_content_int(0, "Age");
        cache_get_field_content(0, "Origin", pInfo[ playerid ][ pOrigin ], DbHandle, 24);
        pInfo[ playerid ][ pLiga ] = cache_get_field_content_int(0, "Liga");
        pInfo[ playerid ][ pExp ] = cache_get_field_content_int(0, "Respect");
        pInfo[ playerid ][ pMoney ] = cache_get_field_content_int(0, "Money");
        GetPlayerBankMoney(playerid) = cache_get_field_content_int(0, "Bank");
        pInfo[ playerid ][ pDeaths ] = cache_get_field_content_int(0, "Deaths");
        pInfo[ playerid ][ pWantedLevel ] = cache_get_field_content_int(0, "WantedLevel");
        pInfo[ playerid ][ pJob ] = cache_get_field_content_int(0, "Job");
        pInfo[ playerid ][ pJail ] = cache_get_field_content_int(0, "Jailed");
        pInfo[ playerid ][ pJailTime ] = cache_get_field_content_int(0, "JailTime");
        pInfo[ playerid ][ pLead ] = cache_get_field_content_int(0, "Leader");
        pInfo[ playerid ][ pMember ] = cache_get_field_content_int(0, "Member");
        pInfo[ playerid ][ pSavings ] = cache_get_field_content_int(0, "pJobCar");
        pInfo[ playerid ][ pRank ] = cache_get_field_content_int(0, "Rank");
        pInfo[ playerid ][ pSkin ] = cache_get_field_content_int(0, "Skinas");
        pInfo[ playerid ][ pJobContr ] = cache_get_field_content_int(0, "JobContr");
        pInfo[ playerid ][ pInt ] = cache_get_field_content_int(0, "Intas");
        pInfo[ playerid ][ pPhone ] = cache_get_field_content_int(0, "PhoneNr");
        pInfo[ playerid ][ pHouseKey ] = cache_get_field_content_int(0, "House");
        pInfo[ playerid ][ pCrashPos ][ 0 ] = cache_get_field_content_float(0, "Pos_x");
        pInfo[ playerid ][ pCrashPos ][ 1 ] = cache_get_field_content_float(0, "Pos_y");
        pInfo[ playerid ][ pCrashPos ][ 2 ] = cache_get_field_content_float(0, "Pos_z");
        pInfo[ playerid ][ pLicCar ] = cache_get_field_content_int(0, "CarLic");
        pInfo[ playerid ][ pLicHeli ] = cache_get_field_content_int(0, "FlyLic");
        pInfo[ playerid ][ pLicBoat ] = cache_get_field_content_int(0, "BoatLic");
        pInfo[ playerid ][ pLicMoto ] = cache_get_field_content_int(0, "MotoLic");
        pInfo[ playerid ][ pLicWeapon ] = cache_get_field_content_int(0, "GunLic");
        pInfo[ playerid ][ pPayCheck ] = cache_get_field_content_int(0, "PayDay");
        pInfo[ playerid ][ pPayDayHad ] = cache_get_field_content_int(0, "PayDayHad");
        pInfo[ playerid ][ pCrash ] = cache_get_field_content_int(0, "Crashed");
        pInfo[ playerid ][ pWarn ] = cache_get_field_content_int(0, "Warnings");
        pInfo[ playerid ][ pVirWorld ] = cache_get_field_content_int(0, "VirWorld");
        pInfo[ playerid ][ pRChannel ] = cache_get_field_content_int(0, "RChanel");
        pInfo[ playerid ][ pRSlot ] = cache_get_field_content_int(0, "radio_slot");
        pInfo[ playerid ][ pUcpID    ] = cache_get_field_content_int(0, "ucpuser");
        pInfo[ playerid ][ pDubKey ] = cache_get_field_content_int(0, "pDubKey");
        pInfo[ playerid ][ pJobSkill ] = cache_get_field_content_int(0, "JobSkill");
        pInfo[ playerid ][ pJobLevel ] = cache_get_field_content_int(0, "JobLevel");
        pInfo[ playerid ][ pLeftTime ] = cache_get_field_content_int(0, "LeftTime");
        pInfo[ playerid ][ pDriverWarn ] = cache_get_field_content_int(0, "DriverWarn");
        pInfo[ playerid ][ pTester ] = cache_get_field_content_int(0, "Tester");
        pInfo[ playerid ][ pFines ] = cache_get_field_content_int(0, "pFines");
        pInfo[ playerid ][ pPaydFines ] = cache_get_field_content_int(0, "pPFines");
        pInfo[ playerid ][ pDonator ] = cache_get_field_content_int(0, "Donator");
        pInfo[ playerid ][ pWalkStyle ] = cache_get_field_content_int(0, "WalkStyle");
        pInfo[ playerid ][ pTalkStyle ] = cache_get_field_content_int(0, "TalkStyle");
        pInfo[ playerid ][ pHeroineAddict ] = cache_get_field_content_int(0, "HeroineAddict");
        pInfo[ playerid ][ pAmfaAddict ] = cache_get_field_content_int(0, "AmfaAddict");
        pInfo[ playerid ][ pMetaAmfaineAddict ] = cache_get_field_content_int(0, "MetamfaAddict");
        pInfo[ playerid ][ pCocaineAddict ] = cache_get_field_content_int(0, "CocaineAddict");
       	pInfo[ playerid ][ pSpawn ] = E_PLAYER_SPAWN_LOCATIONS: cache_get_field_content_int(0, "playerSpawn");
        pInfo[ playerid ][ pBSpawn ] = cache_get_field_content_int(0, "bSpawn");
        cache_get_field_content(0, "Card", pInfo[ playerid ][ pCard ], DbHandle, 256); // s[256]
        cache_get_field_content(0, "ForumName", pInfo[ playerid ][ pForumName ], DbHandle, 256); // s[256]
        pInfo[ playerid ][ pExtazyAddict ] = cache_get_field_content_int(0, "ExtazyAddict");
        pInfo[ playerid ][ pPCPAddict    ] = cache_get_field_content_int(0, "PCPAddict");
        pInfo[ playerid ][ pCrackAddict  ] = cache_get_field_content_int(0, "CrackAddict");
        pInfo[ playerid ][ pOpiumAddict  ] = cache_get_field_content_int(0, "OpiumAddict");
        pInfo[ playerid ][ pPoints       ] = cache_get_field_content_int(0, "Points");
        pInfo[ playerid ][ pHealthLevel  ] = cache_get_field_content_int(0, "HealthLevel");
        pInfo[ playerid ][ pStrengthLevel] = cache_get_field_content_int(0, "StrengthLevel");
        pInfo[ playerid ][ pJobHours ] = cache_get_field_content_int(0, "JobHours");
        pInfo[ playerid ][ pHunger ] = cache_get_field_content_int(0, "Hunger");
        pInfo[ playerid ][ pTotalPaycheck ]  = cache_get_field_content_int(0, "TotalPaycheck");
        pInfo[ playerid ][ pFactionManager ] = (cache_get_field_content_int(0, "faction_manager")) ? (true) : (false);
    
        cache_delete(result);


        //UnPackPoints( extraid, points );

        if(CheckLock(playerid))
            return true;
        
        ResetPlayerMoney( playerid );
        GivePlayerMoney( playerid, pInfo[ playerid ][ pMoney       ] );
        SetPlayerScore( playerid, GetPlayerLevel(playerid));
        SetPlayerFightingStyle( playerid, pInfo[ playerid ][ pBoxStyle    ] );
        GetPlayerIp(playerid, pInfo[ playerid ][ pConnectionIP ], 18);

        format( string, sizeof(string), "UPDATE players SET playerIP = '%s' WHERE id = %d", pInfo[ playerid ][ pConnectionIP ], pInfo[ playerid ][ pMySQLID ] );
        mysql_query(DbHandle,  string, false);
        format( string, sizeof(string), "INSERT INTO `IPLog` (Kas, IP) VALUES (%d,'%s')", pInfo[ playerid ][ pMySQLID ], pInfo[ playerid ][ pConnectionIP ] );
        mysql_query(DbHandle,  string, false);
        
        PlayerOn[ playerid ] = true;
        //--------------------[Tackes uzkraunam]--------------------
        LoadPlayerVehicles( playerid );

        foreach(Vehicles,veh)
        {
            if ( cInfo[ veh ][ cOwner ] == pInfo[ playerid ][ pMySQLID ] && cInfo[ veh ][ cVehID ] > 0 && cInfo[ veh ][ cDub ] == 0)
                pInfo[ playerid ][ pCarGet ] ++;
        }
        
        CheckWeaponCheat( playerid, 0, 1 );
        //----------------------------------------------------------
        ClearChatbox( playerid, 10 );
        format           ( string, 256, "{FFFFFF}Sveikiname sugr��us, J�s prisijung�te su veik�ju %s. S�km�s serveryje!.", GetName( playerid ) );
        SendClientMessage( playerid, COLOR_FADE1,string);
        format           ( string, 56, "~w~Sveikas ~n~~h~~g~%s", GetName( playerid ) );
        //------------------[Nustatum tikslu spawn vieta]-----------
        SpawnPlayerEx ( playerid );
        //---------------------[Sukuriam Info texta]----------------
        ShowPlayerInfoText(playerid);
        GameTextForPlayer (playerid, string, 5000, 1 );
        //------------------[Patvarkom Info texta ]-----------------
        UpdatePlayerInfoText( playerid, PLAYER_STATE_ONFOOT );
        loadPlayerNotes( playerid );
        SetPlayerHealth( playerid, 100);
    }
    else 
        cache_delete(result);


    return 1;
}
*/

/*
stock LoadPlayerKomp(playerid)
{
    new string[ 256 ];
    format(string, 126, "SELECT * FROM `komp` WHERE `kam` = %d AND `priimta` = 0;", pInfo[ playerid ][ pMySQLID ]);
    new Cache:result = mysql_query(DbHandle,  string );
    new id,
        kam2,
        ka,
        kiek,
        kiek2,
        kas[ MAX_PLAYER_NAME ];


    for(new j = 0; j < cache_get_row_count(); j++)
    {
        id = cache_get_field_content_int(j, "id");
        kam2 = cache_get_field_content_int(j, "kam2");
        ka = cache_get_field_content_int(j, "ka");
        kiek = cache_get_field_content_int(j, "kiek");
        kiek2 = cache_get_field_content_int(j, "kiek2");
        cache_get_field_content(j, "Kas", kas);
        switch( ka )
        {
            case 0:
            {
                GivePlayerMoney( playerid, kiek );
                format( string, 126, " J�s� kompensacija priimta, gra�inta pinig�: %d, kompensacijos numeris: %d", kiek, id );
                SendClientMessage( playerid, COLOR_WHITE, string );

                format( string, 256, "UPDATE `komp` SET `priimta` = 1 WHERE `id` = %d LIMIT 1;", id );
                mysql_pquery(DbHandle,  string );
            }
            case 1:
            {
                if ( IsPlayerHaveManyGuns( playerid, kiek ) ) 
                {
                    cache_delete(result);
                    return true;
                }
                GivePlayerWeapon( playerid, kiek, kiek2 );

                new wepname[ 24 ];
                GetWeaponName( kiek, wepname, 24 );

                format( string, 126, " J�s� kompensacija priimta, gra�intas ginklas: %s kulk�: %d, kompensacijos numeris: %d", wepname, kiek2, id );
                SendClientMessage( playerid, COLOR_WHITE, string );

                format( string, 256, "UPDATE `komp` SET `priimta` = 1 WHERE `id` = %d LIMIT 1;", id );
                mysql_pquery(DbHandle,  string );
            }
            case 2:
            {
                new bool:spawned = false;
                foreach(Vehicles,i)
                {
                    if(kam2 == cInfo[ i ][ cID ])
                    {
                        cInfo[ i ][ cInsurance ] = kiek;
                        cInfo[ i ][ cDuzimai   ] = kiek2;
                        SaveCar( i );
                        spawned = true;
                        break;
                    }
                }
                cache_delete(result);
                if(spawned == false)
                {
                    format( string, 256, "UPDATE `vehicles` SET `cInsurance` = %d, `cDuzimai` = %d WHERE id = %d LIMIT 1;", kiek, kiek2, kam2 );
                    mysql_pquery(DbHandle,  string );
                }
                format( string, 256, " J�s� kompensacija priimta, automobilio unikalus ID: %d nustatyta du�im�: %d nustatyta draudimo: %d, kompensacijos numeris: %d", kam2, kiek2, kiek, id );
                SendClientMessage( playerid, COLOR_WHITE, string );

                format( string, 256, "UPDATE `komp` SET `priimta` = 1 WHERE `id` = %d LIMIT 1;", id );
                mysql_pquery(DbHandle,  string );
                return 1;
            }
        }
    }
    cache_delete(result);
    return 1;
}
*/
/*
// SUTVARKOM SPAWN
stock SpawnPlayerEx( playerid )
{
    new
        string[ 256 ],
        string2[ 256 ],
        Float:x,
        Float:y,
        Float:z;
        
    if(pInfo[ playerid ][ pJailTime ] >= 1 && Mires[playerid] == 0 )
    {
        switch(pInfo[playerid][pJail])
        {
            case 1:
            {
                Data_GetCoordinates("ooc_jail", x, y, z);
                SetSpawnInfo(playerid, 0, pInfo[ playerid ][ pSkin ], x, y, z, 0, 0, 0, 0, 0, 0, 0 );
                SetPlayerVirtualWorld(playerid, playerid);

                format( string, sizeof( string ), "SELECT `Priezastis` FROM `nuobaudos` WHERE `Ka` = 'u�dar� � kal�jim�' AND `Kam` = %d ORDER BY `Data` DESC LIMIT 1", pInfo[ playerid ][ pMySQLID ]);
                new Cache:result = mysql_query(DbHandle,  string );
                
                if(cache_get_row_count())
                {
                    cache_get_field_content(0, "Priezastis", string);
                    format( string2, sizeof( string2 ), "Prie�astis: %s", string);
                    SendClientMessage       ( playerid, COLOR_LIGHTRED, "J�s buvote pasodintas � OOC Jail!" );
                    SendClientMessage       ( playerid, COLOR_LIGHTRED, string2 );
                }
                cache_delete(result);
            }
            case 2:
            {
                SetPlayerInterior(playerid, Data_GetInterior("ic_prison"));
                Data_GetCoordinates("ic_prison", x, y, z);
                SetSpawnInfo(playerid, 0, pInfo[ playerid ][ pSkin ], x, y, z,0, 0, 0, 0, 0, 0, 0);
            }
            case 3:
            {
                SetPlayerInterior(playerid, Data_GetInterior("ic_custody"));
                Data_GetCoordinates("ic_custody", x, y, z);
                SetSpawnInfo(playerid, 0, pInfo[ playerid ][ pSkin ], x, y, z, 0, 0, 0, 0, 0, 0, 0 );
            }
        }
    }
    else if( Mires[ playerid ] > 0 )
    {
        if( Mires[ playerid ] == 1 )
        {
            if(pInfo[ playerid ][ pJailTime ] >= 1)
            {
                switch(pInfo[playerid][pJail])
                {
                    case 1:
                    {
                        Data_GetCoordinates("ooc_jail", x, y, z);
                        SetSpawnInfo(playerid, 0, pInfo[ playerid ][ pSkin ], x, y, z, 0, 0, 0, 0, 0, 0, 0 );
                        SetPlayerVirtualWorld( playerid, playerid );

                        format( string, sizeof( string ), "SELECT `Priezastis` FROM `nuobaudos` WHERE `Ka` = 'u�dar� � kal�jim�' AND `Kam` = %d ORDER BY `Data` DESC LIMIT 1", pInfo[ playerid ][ pMySQLID ]);
                        new Cache:result = mysql_query(DbHandle,  string );

                        if(cache_get_row_count())
                        {
                            cache_get_field_content(0, "Priezastis", string);
                            format( string2, sizeof( string2 ), "Prie�astis: %s", string);
                            SendClientMessage       ( playerid, COLOR_LIGHTRED, "J�s buvote pasodintas � OOC Jail!" );
                            SendClientMessage       ( playerid, COLOR_LIGHTRED, string2 );
                        }
                        cache_delete(result);
                    }
                    case 2:
                    {
                        SetPlayerInterior(playerid, Data_GetInterior("ic_prison"));
                        Data_GetCoordinates("ic_prison", x, y, z);
                        SetSpawnInfo(playerid, 0, pInfo[ playerid ][ pSkin ], x, y, z,0, 0, 0, 0, 0, 0, 0);
                    }
                    case 3:
                    {
                        SetPlayerInterior(playerid, Data_GetInterior("ic_custody"));
                        Data_GetCoordinates("ic_custody", x, y, z);
                        SetSpawnInfo(playerid, 0, pInfo[ playerid ][ pSkin ], x, y, z, 0, 0, 0, 0, 0, 0, 0 );
                    }
                }
            }
            else
            {
                Data_GetCoordinates("hospital_discharge", x, y, z);
                SetSpawnInfo(playerid, NO_TEAM, pInfo[ playerid ][ pSkin ], x, y, z, 0, 0, 0, 0, 0, 0, 0);
                SetPlayerInterior(playerid, Data_GetInterior("hospital_discharge"));
                SetPlayerVirtualWorld(playerid, Data_GetVirtualWorld("hospital_discharge"));
                pInfo[playerid][pDeaths] ++;

                for(new i = 0; i < MAX_PLAYER_ITEMS; i++)
                {
                    new itemid = GetPlayerItemAtIndex(playerid, i);
                    if(itemid != ITEM_PHONE)
                    {
                        RemovePlayerItemAtIndex(playerid, i);
                    }
                }

                DestroyDynamic3DTextLabel( DeathLabel[playerid] );
                SendClientMessage(playerid, COLOR_LIGHTRED, "J�s buvote paleistas i� ligonin�s.");
                SendClientMessage(playerid, COLOR_LIGHTRED, "Gydymas kainavo 150$. Ginklai bei kiti daiktai buvo pamesti, i�skyrus telefon�.");
            }
            Mires[ playerid ] = 0;
        }
        else
        {
            format( string, 256, "((\n" );

            for(new i = 0; i < 47; i++)
            {
                format(string2, 256, "%d", i);
                if( GetPVarInt( playerid, string2 ) == 0 || GetSlotByID( i ) > 6 ) continue;

                format(string, 256, "%s%s hits: %d\n", string, WepNames[i], GetPVarInt( playerid, string2 ));
            }

            format( string, 256, "%s ))", string );

            SendClientMessage( playerid, COLOR_GREY, string );
            DeathLabel[playerid] = CreateDynamic3DTextLabel(string, COLOR_RED, pInfo[playerid][pCrashPos][0],pInfo[playerid][pCrashPos][1],pInfo[playerid][pCrashPos][2]+2, 30.0, playerid, INVALID_VEHICLE_ID, 1);
            NullWeapons( playerid );
            SetSpawnInfo            ( playerid, NO_TEAM, pInfo[ playerid ][ pSkin ], pInfo[playerid][pCrashPos][0],pInfo[playerid][pCrashPos][1],pInfo[playerid][pCrashPos][2], 0, 0, 0, 0, 0, 0, 0 );
            SetPlayerInterior     ( playerid, pInfo[playerid][pInt] );
            SetPlayerVirtualWorld ( playerid, pInfo[playerid][pVirWorld] );
            SendClientMessage       ( playerid, COLOR_LIGHTRED, "D�mesio, J�s buvote mirtinai su�eistas ir dabar Jums reikia skubios pagalbos." );
            SendClientMessage       ( playerid, COLOR_LIGHTRED, "Apa�ioje eina laikas iki mirties, jei norite mirti nelauk� ra�ykite /die." );
        }
    }
    else if( pInfo[ playerid ][ pJailTime ] >= 1 )
    {
        switch(pInfo[playerid][pJail])
        {
            case 1:
            {
                Data_GetCoordinates("ooc_jail", x, y, z);
                SetSpawnInfo( playerid, 0, pInfo[ playerid ][ pSkin ],x, y ,z, 0, 0, 0, 0, 0, 0, 0 );
                SetPlayerVirtualWorld( playerid, playerid );

                format( string, sizeof( string ), "SELECT `Priezastis` FROM `nuobaudos` WHERE `Ka` = 'u�dar� � kal�jim�' AND `Kam` = %d ORDER BY `Data` DESC LIMIT 1", pInfo[ playerid ][ pMySQLID ]);
                new Cache:result = mysql_query(DbHandle,  string );
                if(cache_get_row_count())
                {
                    cache_get_field_content(0, "Priezastis", string);
                    format( string2, sizeof( string2 ), "Prie�astis: %s", string);
                    SendClientMessage       ( playerid, COLOR_LIGHTRED, "J�s buvote pasodintas � OOC Jail!" );
                    SendClientMessage       ( playerid, COLOR_LIGHTRED, string2 );
                }
                cache_delete(result);
            }
            case 2:
            {
                SetPlayerInterior(playerid, Data_GetInterior("ic_prison"));
                Data_GetCoordinates("ic_prison", x, y, z);
                SetSpawnInfo(playerid, 0, pInfo[ playerid ][ pSkin ], x, y, z,0, 0, 0, 0, 0, 0, 0);
            }
            case 3:
            {
                SetPlayerInterior(playerid, Data_GetInterior("ic_custody"));
                Data_GetCoordinates("ic_custody", x, y, z);
                SetSpawnInfo(playerid, 0, pInfo[ playerid ][ pSkin ], x, y ,z, 0, 0, 0, 0, 0, 0, 0 );
            }
        }
    }
    else
    {
        if( pInfo[ playerid ][ pCrash ] == 1 )
        {
            SetSpawnInfo( playerid, NO_TEAM, pInfo[ playerid ][ pSkin ], pInfo[ playerid ][ pCrashPos ][ 0 ],pInfo[ playerid ][ pCrashPos ][ 1 ], pInfo[ playerid ][ pCrashPos ][ 2 ]+1.0, 0, 0, 0, 0, 0, 0, 0 );
            format      ( string, sizeof(string), "~r~KLAIDA! ~n~~h~~g~Gryztate atgal" );
            GameTextForPlayer    ( playerid, string, 5000, 1 );
            SetPlayerInterior( playerid, pInfo[playerid][pInt] );
            SetPlayerVirtualWorld( playerid, pInfo[playerid][pVirWorld] );
            pInfo[ playerid ][ pCrash ] = 0;
        }
        else
        {
            switch(pInfo[ playerid ][ pSpawn ])
            {
                case SpawnHouse:
                {
                    new housekey;
                    foreach(Houses,h)
                    {
                        if (pInfo[ playerid ][ pBSpawn ] == hInfo[ h ][ hID ])
                        {
                            housekey = h;
                        }
                    }
                    SetSpawnInfo(playerid, 0, pInfo[ playerid ][ pSkin ], hInfo[ housekey ][ hEnter ][ 0 ],hInfo[ housekey ][ hEnter ][ 1 ],hInfo[ housekey ][ hEnter ][ 2 ], 0, 0, 0, 0, 0, 0, 0 );
                    SetPlayerVirtualWorld( playerid, hInfo[ housekey ][ hEntranceVirw ] );
                    SetPlayerInterior    ( playerid, hInfo[ housekey ][ hEntranceInt ] );
                }
                case SpawnFaction: SetSpawnInfo( playerid, 0, pInfo[ playerid ][ pSkin ], fInfo[ PlayerFaction( playerid ) ][ fSpawn ][ 0 ],fInfo[ PlayerFaction( playerid ) ][ fSpawn ][ 1 ],fInfo[ PlayerFaction( playerid ) ][ fSpawn ][ 2 ], 0, 0, 0, 0, 0, 0, 0 );
                case SpawnBusiness:
                {
                    new index = GetBusinessIndex(pInfo[ playerid ][ pBSpawn ]);
                    if(index != -1)
                    {
                        SetSpawnInfo(playerid, 0, pInfo[ playerid ][ pSkin ], bInfo[ index ][ bEnter ][ 0 ],bInfo[ index ][ bEnter ][ 1 ],bInfo[ index ][ bEnter ][ 2 ], 0, 0, 0, 0, 0, 0, 0 );
                        SetPlayerVirtualWorld(playerid, bInfo[ index ][ bEntranceVirw ]);
                        SetPlayerInterior(playerid, bInfo[ index ][ bEntranceInt ]);
                    }
                }
                case SpawnLosSantos: 
                {
                    Data_GetCoordinates("spawn_los_santos", x, y, z);
                    SetPlayerInterior(playerid, Data_GetInterior("spawn_los_santos"));
                    SetPlayerVirtualWorld(playerid, Data_GetVirtualWorld("spawn_los_santos"));
                    SetSpawnInfo( playerid, 0, pInfo[ playerid ][ pSkin ], x, y, z,0, 0, 0, 0, 0, 0, 0 );
                }
                case SpawnGarage:
                {
                    new index = GetGarageIndex(pInfo[ playerid ][ pBSpawn ]);
                    if(index != -1)
                    {
                        GetGarageEntrancePos(index, x, y, z);
                        SetSpawnInfo(playerid, 0, pInfo[ playerid ][ pSkin ], x, y, z, 0, 0, 0, 0, 0, 0, 0 );
                        SetPlayerVirtualWorld(playerid, GetGarageEntranceVirtualWorld(index));
                        SetPlayerInterior(playerid, GetGarageEntranceInteriorID(index));    
                    }
                }
                default:
                {
                    Data_GetCoordinates("default_spawn", x, y, z);
                    SetPlayerInterior(playerid, Data_GetInterior("default_spawn"));
                    SetPlayerVirtualWorld(playerid, Data_GetVirtualWorld("default_spawn"));
                    SetSpawnInfo(playerid, 0, pInfo[ playerid ][ pSkin ], x, y, z,0, 0, 0, 0, 0, 0, 0);
                }
            }
        }
    }
    return SpawnPlayer( playerid );
}
*/
/*
stock returnFuelText( vehicle, model )
{
    new string[ 26 ],

        fuel = (cInfo[ vehicle ][ cFuel ] * 20) / GetVehicleFuelTank( model );
    switch( fuel )
    {
        case 0: format( string, 26, "~r~...................." );
        case 1: format( string, 26, "~r~I..................." );
        case 2: format( string, 26, "~r~II.................." );
        case 3: format( string, 26, "~r~III................." );
        case 4: format( string, 26, "~y~IIII................" );
        case 5: format( string, 26, "~y~IIIII..............." );
        case 6: format( string, 26, "~y~IIIIII.............." );
        case 7: format( string, 26, "~y~IIIIIII............." );
        case 8: format( string, 26, "~w~IIIIIIII............" );
        case 9: format( string, 26, "~w~IIIIIIIII..........." );
        case 10:format( string, 26, "~w~IIIIIIIIII.........." );
        case 11:format( string, 26, "~w~IIIIIIIIIII........." );
        case 12:format( string, 26, "~w~IIIIIIIIIIII........" );
        case 13:format( string, 26, "~w~IIIIIIIIIIIII......." );
        case 14:format( string, 26, "~w~IIIIIIIIIIIIII......" );
        case 15:format( string, 26, "~w~IIIIIIIIIIIIIII....." );
        case 16:format( string, 26, "~w~IIIIIIIIIIIIIIII...." );
        case 17:format( string, 26, "~w~IIIIIIIIIIIIIIIII..." );
        case 18:format( string, 26, "~w~IIIIIIIIIIIIIIIIII.." );
        case 19:format( string, 26, "~w~IIIIIIIIIIIIIIIIIII." );
        case 20:format( string, 26, "~w~IIIIIIIIIIIIIIIIIIII" );
    }
    return string;
}
stock AddJobExp( playerid, exp )
{
    pInfo[ playerid ][ pJobSkill ] += exp;
    if ( (( pInfo[ playerid ][ pJobLevel ] + 1 ) * 100) <= pInfo[ playerid ][ pJobSkill ] )
    {
        if ( pInfo[ playerid ][ pJobLevel ] == 10 )
        {
            pInfo[ playerid ][ pJobSkill ] -= exp;
            return 1;
        }
        pInfo[ playerid ][ pJobLevel ] ++;
        pInfo[ playerid ][ pJobSkill ] = 0;
        ShowInfoText( playerid, "~g~Sveikiname jusu darbo lygis padidejo", 4000 );
        return 1;
    }
    return 1;
}
*/

stock GetVehicleSpeed2( vehid )
{
    new Float:x,
        Float:y,
        Float:z;
    GetVehicleVelocity( vehid, x, y, z );
    return floatround( floatsqroot( x*x + y*y + z*z ) * 170 );
}
stock SetVehicleSpeed( vehicleid, mph )
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
            format       ( text, 126, "* Automobilio variklis i�sijungia.(( %s ))", GetPlayerNameEx( playerid ) );
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
            format       ( text, 126, "* Automobilio variklis i�sijungia.(( %s ))", GetPlayerNameEx( playerid ) );
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
/*
FUNKCIJA:DrugsEffects( i )
{
    KillTimer( DrugTimer[ i ] );
    if( !GetPVarInt( i, "Addicted" ) )
    {
        new
            Float:Health;

        GetPlayerHealth( i, Health );
        
        if( GetPVarInt( i, "DrugHPLimit" ) > 0 && GetPVarInt( i, "DrugHP" )+Health <= 100)
        {
            SetPVarInt( i, "DrugHPLimit", GetPVarInt( i, "DrugHPLimit" )-GetPVarInt( i, "DrugHP" ) );
            SetPlayerHealth( i, Health+GetPVarInt( i, "DrugHP" ) );
        }
        DrugTimer[ i ] = SetTimerEx( "DrugsEffects", 12000, false, "i", i );
    }
    return 1;
}*/

/*
stock UpdatePlayerInfoText(playerid ,plstate = PLAYER_STATE_ONFOOT )
{
    new string[ 512 ], speed;

    if ( pInfo[ playerid ][ pRChannel ] > 0 )
    {
        format( string, 40, "~w~R.kanalas: %d~n~", pInfo[ playerid ][ pRChannel ] );
        format( string, 40, "%s~w~R.slot: %d~n~", string, pInfo[ playerid ][ pRSlot ]);
    }

    if ( plstate == PLAYER_STATE_DRIVER )
    {
        new veh = OldCar[ playerid ],
            model = GetPVarInt( playerid, "PLAYER_VEH_MODEL" );
        Check_VHP( veh, 1 );
        if ( GetPVarInt( playerid, "MOKESTIS" ) == 0 )
        {
            speed = GetVehicleSpeed2( veh );
            if ( GetPVarInt( playerid, "LIC_TIME") > 0 )
            {
                SetPVarInt( playerid, "LIC_TIME", GetPVarInt( playerid, "LIC_TIME") - 1);
                if ( speed > 60 )
                    SetPVarInt( playerid, "LIC_MISTAKE", GetPVarInt( playerid, "LIC_MISTAKE" ) + 1 );

                if ( GetPVarInt( playerid, "LIC_TIME") == 1 )
                {
                    SetVehicleToRespawn( OldCar[ playerid ] );
                    DisablePlayerRaceCheckpoint( playerid );
                    if( GetPVarInt( playerid, "LIC_TYPE" ) == 3 )
                        Data_SetPlayerLocation(playerid, "license_pilot_end");
                }
            }
            if ( PlayerSpeed[ playerid ] > 0 && speed >= PlayerSpeed[ playerid ] )
                SetVehicleSpeed( veh, PlayerSpeed[ playerid ]-5 );

            format( string, 256, "%sGreitis: %d km/h~n~~w~Rida: %.0f km", string, speed, cInfo[ veh ][ cKM ]);

            if ( VehicleHasEngine( model ) )
                format( string, 256, "%s~n~~w~Degalai: %s", string, returnFuelText( veh, model ) );
        }
        else
            format( string, 256, "%s~w~Degaline~n~Moketi: $%d~n~Degalai: %s~n~Ipilta: %d L", string, GetPVarInt( playerid, "MOKESTIS" ), returnFuelText( veh, model ), cInfo[ veh ][ cFuel ] - GetPVarInt( playerid, "FILLED" ) );
    }
    if ( Laikas[ playerid ] > 0 )
    {
        new mins,
            secs;

        ShowPlayerInfoText( playerid );
        divmod( Laikas[ playerid ], 60, mins, secs );
        if (mins > 0)
            format(string, 256,"%s~n~~w~Atliekama: %d:%02d",string,mins,secs );
        else
            format(string, 256,"%s~n~~w~Atliekama: %d",string,secs );
    }
    if ( Mires[ playerid ] > 0 )
    {
        new mins,
            secs;

        ShowPlayerInfoText( playerid );
        divmod( Mires[ playerid ], 60, mins, secs );
        if ( mins > 0 )
            format(string, 256,"%s~n~~w~Iki mirties: %d:%02d", string, mins, secs );
        else
            format(string, 256,"%s~n~~w~Iki mirties: %d", string, secs );
    }
    if ( pInfo[ playerid ][ pJailTime ] > 0 )
    {
        new hours,
            mins,
            secs;

        ShowPlayerInfoText( playerid );
        divmod(pInfo[playerid][pJailTime], 3600, hours, mins );
        divmod(mins, 60, mins, secs );
        format(string, 256, "%s~n~~r~Sedeti liko: ~w~", string );
        if ( hours > 0 )
            format( string, 256, "%s%d:%02d:%02d", string, hours, mins, secs );
        else if ( mins > 0 )
            format( string, 256, "%s%d:%02d", string, mins, secs );
        else
            format( string, 256, "%s%d", string, secs );
    }
    if(GetPlayerTaxiTripPrice(playerid))
    {
        // Jei keleivis
        if(GetPlayerTaxiDriver(playerid) != INVALID_PLAYER_ID)
        {
            format(string, sizeof(string),"%s~n~~g~Taksometras: $%d", string, GetPlayerTaxiTripPrice(playerid));
            ShowPlayerInfoText( playerid );
        }
    }
    // Jei vairuotojas
    else if(IsPlayerTaxiDriver(playerid))
    {
        new count = 1;
        strcat(string, "~n~~b~___Taksometras___");
        foreach(Player, j)
        {
            if(GetPlayerTaxiDriver(j) == playerid)
                format(string, sizeof(string), "%s~n~~g~Keleivis #%d: $%d", string, count++, GetPlayerTaxiTripPrice(j));
        }
    }
    if( !strlen( string ) ) HidePlayerInfoText( playerid );
    else 
        PlayerTextDrawSetString( playerid, Greitis[ playerid ], string );
    return 1;
}
*/
/*
CMD:goaway(playerid)
{
    CargoShipDeparture();
    return 1;
}

CMD:comeback(playerid)
{
    CargoShipReturn();
    return 1;
}


CMD:status(playerid)
{
    new string[64];
    switch(ShipInfo[ Status ])
    {
        case Moving: string = "I�plaukia";
        case Docked: string = "Vietoj, uoste.";
        case Arriving: string = "Atplauki/Parkuojasi";
    }
    format(string, sizeof(string),"Statusas laivo:%s",string);
    SendClientMessage(playerid, -1, string);
    return 1;
}

CMD:ataik(playerid)
{
    new Float:x, Float:y, Float:z;
    GetPlayerPos(playerid, x,y,z);
    MoveCargoShipToPoint(x,y,z);
    return 1;
}
*/

CMD:shipstatus(playerid)
{
    if(!GetPlayerAdminLevel(playerid))
        return 0;

    new string[64];
    switch(ShipInfo[ Status ])
    {
        case Moving: string = "I�plaukia";
        case Docked: string = "Uoste";
        case Arriving: string = "Plaukia � uost�";
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
	// Timeris pradeda laivo objekt� gra�inim� � uost�
    // Kai objektai baigs jud�ti, laivas v�l priims krovinius.
	SetTimer("CargoShipDeparture", CARGOSHIP_DOCKED_INTERVAL, false);
	
    ShipInfo[ Status ] = Arriving;

    MoveCargoShipToPoint(.ToSpawn = true);
    return 1;
}

stock MoveCargoShipToPoint(Float:x = 0.0, Float:y = 0.0, Float:z = 0.0, ToSpawn = false)
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


stock IsPlayerInRangeOfPlayer(playerid, playerid2, Float:distance)
{
    new Float:x,Float:y,Float:z;
    GetPlayerPos(playerid2, x, y, z);
    return IsPlayerInRangeOfPoint(playerid, distance, x, y, z);
}
stock IsPlayerInRangeOfCargoShip(playerid, Float:distance)
	return IsPlayerInRangeOfPoint(playerid, distance, SHIP_POS_X, SHIP_POS_Y, SHIP_POS_Z);

stock GetShipCargoPrice(cargoid)
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
    // Jei gavome kaina kuri ma�esn� u� ma�iausi� pardavimo kain�, kei�iam j� � ma�iausi� pardavimo kain�
    // Tai darom kad �aid�jai neprarast� pinig�. idiots.
    if(price < lowestSellPrice)
        price = lowestSellPrice;
    return price;
}


FUNKCIJA:IndustryUpdate()
{
	new ticks = GetTickCount();
	new soldCommodityIndex = -1, // Indeksas prekes kuria industrija parduoda. I JA taps visi jos turimi resursai.
        soldCommodityProduction, // Kiek pagaminama preki� i� �io resurso.
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
            // Nuo �ia kodas baisu, tod�l niekam neleid�iu skaityt '_'

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

                // I�vis. Jeigu pasiektas limitas, nieko �ia nereikia daryt.
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
                // Tai rei�kia kad neu�tenka produkt� pagaminti bent vienam vienetui :(
                if(willBeMadeAmount <= 0)
                    break;

                // Jei nori gamint daugiau nei telpa.
                if(willBeMadeAmount * soldCommodityProduction > limit)
                {
                    willBeMadeAmount = (limit - Commodities[ soldCommodityIndex ][ CurrentStock ]) / soldCommodityProduction;
                    willBeMadeAmount++; // Nes kitaip gali neu�sipildyt.
                    Industries[ i ][ IsBuyingCargo ] = false;
                }
                // Jei jau nebepagamina iki full limito, v�l gali pirkti.
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

                // I�vis. Jeigu pasiektas limitas, nieko �ia nereikia daryt.
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
        Sec,
        string[ 256 ];
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
/*stock Loterry( )
{
    new number = random( 19 ) + 1,
        slot,
        Winners,
        string[ 126 ],
        Taken;
    foreach(Player,playerid)
    {
        slot = PlayerHasItemInInvEx( playerid, ITEM_TICKET );
        if ( slot != INVENTORY_SLOTS )
        {
            if ( InvInfo[ playerid ][ slot ][ iAmmount] == number )
            {
                new winprice = ( random( 100 ) + random( 100 ) ) + 4500;
                format( string, 126, "LOTERIJA: Sveikiname! J�s laim�jote loterijoje, ir gavote $%d ", winprice );
                SendClientMessage( playerid, COLOR_GREEN, string );
                GivePlayerMoney( playerid, winprice );
                Winners ++;
                Taken += winprice;
                ClearInvSlot( playerid, slot );
            }
            else if ( ( InvInfo[ playerid ][ slot ][ iAmmount] + 1) == number )
            {
                new winprice = random( 100 ) + 1800;
                format( string, 126, "LOTERIJA: Sveikiname! J�s laim�jote loterijoje, ir gavote $%d ", winprice );
                SendClientMessage( playerid, COLOR_GREEN, string );
                GivePlayerMoney( playerid, winprice );
                Winners ++;
                Taken += winprice;
                ClearInvSlot( playerid, slot );
            }
            else if ( ( InvInfo[ playerid ][ slot ][ iAmmount] - 1) == number )
            {
                new winprice = random( 100 ) + 1800;
                format( string, 126, "LOTERIJA: Sveikiname! J�s laim�jote loterijoje, ir gavote $%d ", winprice );
                SendClientMessage( playerid, COLOR_GREEN, string );
                GivePlayerMoney( playerid, winprice );
                Winners ++;
                Taken += winprice;
                ClearInvSlot( playerid, slot );
            }
        }
    }
    SendClientMessageToAll( COLOR_GREEN2, "_________ Loterijos naujienos _________ ");
    format                ( string, 126, "�iandienos laimingas skai�ius: %d", number );
    SendClientMessageToAll( COLOR_WHITE, string );
    format                ( string, 126, "�iandien turime laim�toj�: %d ", Winners );
    SendClientMessageToAll( COLOR_WHITE, string );
    format                ( string, 126, "�iandien i�dalinta loterijoje: $%d", Taken );
    SendClientMessageToAll( COLOR_WHITE, string );
    return 1;
}*/

/*
stock PayDay( playerid )
{
        if ( PlayerOn[ playerid ] == false ) return 1;

        new Bank = GetPlayerBankMoney(playerid),
            //fullPaycheck = pInfo[ playerid ][ pTotalPaycheck ],
            //leftPaycheck = pInfo[ playerid ][ pTotalPaycheck ],
            housekey,
            payforhouses,
            payforbiz,
            payforcar,
            string[ 126 ],
            rows;

        // �aid�jas vis labiau ir labiau nori valgyti..
        pInfo[ playerid ][ pHunger ] += random(3);

        foreach(Houses,h)
        {
            if ( pInfo[ playerid ][ pHouseKey ] == hInfo[ h ][ hID ] )
                housekey = h;
            if ( hInfo[h][hOwner] == pInfo[ playerid ][ pMySQLID ] )
                payforhouses += housetax;
        }
        for(new i = 0; i < GetBusinessCount(); i++)
        {
            if(IsPlayerBusinessOwner(playerid, i))
                payforbiz += biztax;
        }

        format(string, sizeof(string), "SELECT cModel FROM `vehicles` WHERE `cOwner` = '%d'", pInfo[playerid][pMySQLID] );
        new Cache:result = mysql_query(DbHandle,  string );
        rows = cache_get_row_count();
        for(new i = 0; i < rows; i++)
        {
            if(IsVehicleTaxed(cache_get_field_content_int(i, "cModel")))
                payforcar += cartax;
        }
        cache_delete(result);

        if( pInfo[ playerid ][ pHouseKey ] > 0 )
        {
            if ( hInfo[housekey][hOwner] != pInfo[ playerid ][ pMySQLID ] )// Prie namo savininko pridedam nuoma
            {
                if( Bank < hInfo[ housekey ][ hRentPrice ] )
                {
                    SendClientMessage( playerid, COLOR_WHITE, "J�s buvote i�keldintas i� nuomojamo namo, nes nesumok�jote nuomos." );
                    pInfo[ playerid ][ pHouseKey ] = 0;
                    pInfo[ playerid ][ pSpawn ] = DefaultSpawn;
                }
                else if ( Bank >= hInfo[ housekey ][ hRentPrice ] )
                {
                    hInfo[ housekey ][ hBank ] += hInfo[ housekey ][ hRentPrice ];
                    Bank -= hInfo[ housekey ][ hRentPrice ];
                    Biudzetas += hInfo[ housekey ][ hRentPrice ];
                }
            }
        }
        
        new
            rand = random(3);
        if( ( pInfo[ playerid ][ pHeroineAddict ]-rand ) >= 3 )
            pInfo[ playerid ][ pHeroineAddict ] -= rand;
        if( ( pInfo[ playerid ][ pAmfaAddict ]-rand ) >= 3 )
            pInfo[ playerid ][ pAmfaAddict ] -= rand;
        if( ( pInfo[ playerid ][ pCocaineAddict ]-rand ) >= 3 )
            pInfo[ playerid ][ pCocaineAddict ] -= rand;
        if( ( pInfo[ playerid ][ pMetaAmfaineAddict ]-rand ) >= 3 )
            pInfo[ playerid ][ pMetaAmfaineAddict ] -= rand;
        if( ( pInfo[ playerid ][ pExtazyAddict ]-rand ) >= 5 )
            pInfo[ playerid ][ pExtazyAddict ] -= rand;
        if( ( pInfo[ playerid ][ pPCPAddict ]-rand ) >= 3 )
            pInfo[ playerid ][ pPCPAddict ] -= rand;
        if( ( pInfo[ playerid ][ pCrackAddict ]-rand ) >= 4 )
            pInfo[ playerid ][ pCrackAddict ] -= rand;
        if( ( pInfo[ playerid ][ pOpiumAddict ]-rand ) >= 2 )
            pInfo[ playerid ][ pOpiumAddict ] -= rand;
            
        if ( pInfo[ playerid ][ pPayDayHad ] >= 20) // 20 Min Online Butinas
        {
            new pfaction = PlayerFaction( playerid ),
                rank = pInfo[ playerid ][ pRank ],
                ForFact = 0,
                ForGov = 0,
                alga = 0,
                palukana = floatround( pInfo[ playerid ][ pSavings ]/200 );

            if( pfaction > 0 )
            {
                pInfo[ playerid ][ pPayCheck ] += fInfo[ pfaction ][ fPayDay ][ rank ];
                alga = pInfo[ playerid ][ pPayCheck ];
                pInfo[ playerid ][ pJob ] = JOB_NONE;
                ForFact = floatround(alga/100*5);
                ForGov = ForFact;
                fInfo[pfaction][fBank] += ForFact;
            }

            if( pInfo[ playerid ][ pJob ] > JOB_NONE )
            {
				pInfo[ playerid ][ pJobHours ]++;
                pInfo[ playerid ][ pPayCheck ] += pJobs[ pInfo[ playerid ][ pJob ] ][ PayCheck ];
                pInfo[ playerid ][ pPayCheck ] += pInfo[ playerid ][ pJobLevel ] * 20;
                if( pInfo[ playerid ][ pPayCheck ] >= pJobs[ pInfo[ playerid ][ pJob ] ][ MaxPayday ] )
                    pInfo[ playerid ][ pPayCheck ] = pJobs[ pInfo[ playerid ][ pJob ] ][ MaxPayday ];
                alga = pInfo[ playerid ][ pPayCheck ];
                ForGov = floatround(alga/100*5);
            }

            pInfo[ playerid ][ pSavings ] += palukana;
            Biudzetas += ForGov;
            Bank -= payforhouses;
            Bank -= payforbiz;
            Bank -= payforcar;
            PlayerPlayMusic( playerid );
            SendClientMessage(playerid, COLOR_GREEN, "|______________ Los Santos banko ataskaita______________ |");
            format(string, sizeof(string), "| Gautas atlyginimas: %d$ | Papildomi mokes�iai: %d$ |",pInfo[playerid][pPayCheck],ForGov); SendClientMessage(playerid, COLOR_WHITE, string);
            if(pfaction > 0)
            {
                format           ( string, sizeof(string), "| Frakcijos nustatyti mokes�iai: %d$ |",ForFact);
                SendClientMessage( playerid, COLOR_FADE1, string );
            }
            format(string, sizeof(string), "| Buv�s banko balansas: %d$ |",GetPlayerBankMoney(playerid)); SendClientMessage(playerid, COLOR_WHITE, string);
            pInfo[playerid][pPayCheck] -= ( ForGov + ForFact );
            format(string, sizeof(string), "| Pal�kanos: %d$ |",palukana); SendClientMessage(playerid, COLOR_FADE1, string);
            SendClientMessage(playerid, COLOR_FADE1, "| Pal�kan� procentas: 0.5% |");
            format(string, sizeof(string), "| Galutin� gauta suma: %d$ |",pInfo[playerid][pPayCheck]); SendClientMessage(playerid, COLOR_WHITE, string);
            SendClientMessage(playerid,COLOR_GREEN, "|--------------------------------------------------------------------------|");
            format(string, sizeof(string), "| Dabartinis banko balansas: %d$ |",Bank);
            SendClientMessage(playerid, COLOR_FADE1, string);
            pInfo[ playerid ][ pTotalPaycheck ] += pInfo[playerid][pPayCheck];
            format(string, sizeof(string), "| Sukauptas atlyginimas: %d$", pInfo[ playerid ][ pTotalPaycheck ]);
            SendClientMessage(playerid, COLOR_FADE1, string);

            if ( hInfo[ housekey ][ hRentPrice ] > 0 && hInfo[ housekey ][ hOwner ] != pInfo[ playerid ][ pMySQLID ])
            {
                format(string, sizeof(string), "| Mokestis u� nuom�: %d$ |",hInfo[ housekey ][ hRentPrice ]);
                SendClientMessage(playerid, COLOR_WHITE, string);
            }
            if ( payforhouses > 0 )
            {
                format(string, 126, "| Mokestis u� nekilnojama turt�: %d$ |", payforhouses);
                SendClientMessage(playerid, COLOR_WHITE, string);
                Biudzetas += payforhouses;
            }
            if ( payforbiz > 0 )
            {
                format(string, 126, "| Verslo mokestis: %d$ |", payforbiz);
                SendClientMessage(playerid, COLOR_WHITE, string);
                Biudzetas += payforbiz;
            }
            if ( payforcar > 0 )
            {
                format(string, 126, "| Tr. Priemoni� mokestis: %d$ |", payforcar);
                SendClientMessage(playerid, COLOR_WHITE, string);
                Biudzetas += payforcar;
            }
            GameTextForPlayer(playerid, "~y~Mokesciai~n~~g~Alga", 5000, 1);

            SetPlayerBankMoney(playerid, Bank);
            pInfo[ playerid ][ pOnTime    ] ++;
            pInfo[ playerid ][ pExp ] ++;

            if ( ( GetPlayerLevel(playerid) + 1 ) * 4 <= pInfo[ playerid ][ pExp ] )
            {
                SetPlayerLevel(playerid, GetPlayerLevel(playerid) + 1);
                pInfo[ playerid ][ pExp   ] = 0;
                SetPlayerScore ( playerid, GetPlayerLevel(playerid));
                ShowInfoText   ( playerid, "~w~ Sveikiname, k�tik veik�jo lygis pakilo (/levelup).", 4000);
                PlayerPlaySound( playerid, 1057, 0.0, 0.0, 0.0 );
                pInfo[ playerid ][ pPoints ]++;
            }
            PayLog( pInfo[ playerid ][ pMySQLID ], 9, -1, pInfo[playerid][pPayCheck] );
            pInfo[ playerid ][ pPayDayHad ] = 0;
            pInfo[ playerid ][ pPayCheck  ] = 0;

            if ( pInfo[ playerid ][ pJobContr  ] > 0 )
                pInfo[ playerid ][ pJobContr  ] --;

            if ( pInfo[ playerid ][ pLiga ] == 0 )
                Susirgti( playerid );
        }
        else
            return SendClientMessage(playerid, COLOR_LIGHTRED,"Apgailestaujame, bet atlyginimo u� �i� valand� negausite, kadangi J�s nebuvote prisijung�s pakankamai.");
        return 1;
}
*/
/*stock ForcePayDay( playerid )
{
        new Bank = GetPlayerBankMoney(playerid),
            housekey,
            payforhouses,
            payforbiz,
            payforcars,
            name[ 24 ];
        GetPlayerName( playerid, name, 24 );
        foreach(Houses,h)
        {
            if ( pInfo[ playerid ][ pHouseKey ] == hInfo[ h ][ hID ] )
                housekey = h;
            if ( !strcmp( name, hInfo[ h ][ hOwner ], true ) )
                payforhouses += housetax;
        }
        foreach(Busines,b)
        {
            if ( !strcmp( name, bInfo[ b ][ bOwner ], true ) )
                payforbiz += biztax;
        }
        foreach(Vehicles,veh)
        {
        if ( pInfo[ playerid ][ pMySQLID ] == cInfo[ veh ][ cOwner ] )
        veh = payforcars;
        payforcars += cartax;
        }
        if( pInfo[ playerid ][ pHouseKey ] > 0 )
        {
            if ( strcmp( name, hInfo[ housekey ][ hOwner ], true ) )// Prei namo savininko pridedam nuoma
            {
                if( Bank < hInfo[ housekey ][ hRent ] )
                {
                    SendClientMessage( playerid, COLOR_WHITE, "J�s buvote i�keldintas i� nuomojamo namo, nes nesumok�jote nuomos." );
                    pInfo[ playerid ][ pHouseKey ] = 0;
                }
                else if ( Bank > hInfo[ housekey ][ hRent ] )
                {
                    hInfo[ housekey ][ hBank ] += hInfo[ housekey ][ hRent ];
                    Bank -= hInfo[ housekey ][ hRent ];
                    Bank -= 30;
                    Biudzetas += 30;
                }
            }
        }
        {
            new pfaction = PlayerFaction( playerid );

            if( pfaction < 8 && pInfo[ playerid ][ pJob ] > JOB_NONE && pfaction > 0)
                pInfo[ playerid ][ pJob ] = JOB_NONE;

            pInfo[ playerid ][ pPayCheck ] += pJobs[ pInfo[ playerid ][ pJob ] ][ PayCheck ];
            if ( pInfo[ playerid ][ pJob ] > 0 )
                pInfo[ playerid ][ pPayCheck ] += pInfo[ playerid ][ pJobLevel ] * 20;

            new rank = pInfo[ playerid ][ pRank ]; // To Not to bug server crash.
            if ( rank < 0 || rank > 9 )
                rank = 1;

            switch ( pfaction )
            {
                case 1,2,3:
                {
                    pInfo[ playerid ][ pPayCheck ] += fInfo[ pfaction ][ fPayDay ][ rank ];
                }
                case 4..15:
                {
                    pInfo[ playerid ][ pPayCheck ] += fInfo[ pfaction ][ fPayDay ][ rank ];
                }
            }

            if( pInfo[ playerid ][ pPayCheck ] >= pJobs[ pInfo[ playerid ][ pJob ] ][ MaxPayday ] )
                pInfo[ playerid ][ pPayCheck ] = pJobs[ pInfo[ playerid ][ pJob ] ][ MaxPayday ];

            new ForFact = 0,
                ForGov = floatround(pInfo[playerid][pPayCheck]/20),
                palukana = floatround( pInfo[ playerid ][ pSavings ]/200 );

            pInfo[ playerid ][ pSavings ] += palukana;
            if(pfaction > 0)
            {
                ForFact = floatround(pInfo[playerid][pPayCheck]/20);
                fInfo[pfaction][fBank] += ForFact;
            }
            Biudzetas += ForGov;
            Bank -= payforhouses;
            new string[126];
            PlayerPlayMusic( playerid );
            SendClientMessage(playerid, COLOR_GREEN, "|____ BANKO ATASKAITA ____|");
            format(string, 126, " Gauta alga: $%d Moke��iai valstybei: $%d",pInfo[playerid][pPayCheck],ForGov); SendClientMessage(playerid, COLOR_WHITE, string);
            if(pfaction > 0)
            {
                format           ( string, 126, " Moke��iai frakcijai: $%d",ForFact);
                SendClientMessage( playerid, COLOR_FADE1, string );
            }
            format(string, 126, " Balansas: $%d",GetPlayerBankMoney(playerid)); SendClientMessage(playerid, COLOR_FADE1, string);
            pInfo[playerid][pPayCheck] = pInfo[playerid][pPayCheck] - ForGov - ForFact;
            format(string, 126, " Gautos pal�kanos: $%d",palukana); SendClientMessage(playerid, COLOR_FADE1, string);
            SendClientMessage(playerid, COLOR_FADE1, " Pal�kan� procentas: 0.5 % ");
            format(string, 126, " Galutin� gauta suma: $%d",pInfo[playerid][pPayCheck]); SendClientMessage(playerid, COLOR_FADE1, string);
            SendClientMessage(playerid,COLOR_GREEN, "|-----------------------------------|");
            Bank += pInfo[playerid][pPayCheck];
            format(string, 126, " Dabartinis banko balansas: $%d",Bank); SendClientMessage(playerid, COLOR_FADE1, string);
            if ( hInfo[ housekey ][ hRent ] > 0)
            {
                format(string, 126, " Nuomos mokestis: $%d",hInfo[ housekey ][ hRent ]+30);
                SendClientMessage(playerid, COLOR_FADE1, string);
            }
            if ( payforhouses > 0 )
            {
                format(string, 126, " Nekilnojamo turto mokestis: $%d", payforhouses);
                SendClientMessage(playerid, COLOR_FADE1, string);
                Biudzetas += payforhouses;
            }
            if ( payforbiz > 0 )
            {
                format(string, 126, " Verslo mokestis: $%d", payforbiz);
                SendClientMessage(playerid, COLOR_FADE1, string);
                Biudzetas += payforbiz;
            }
            if ( payforcars > 0 )
            {
                format(string, 126, " Tr. Priemoni� mokestis: $%d", payforcars);
                SendClientMessage(playerid, COLOR_FADE1, string);
                Biudzetas += payforcars;
            }
            GameTextForPlayer(playerid, "~y~Mokesciai~n~~g~Alga", 5000, 1);

            pInfo[ playerid ][ pBank      ] = Bank;
            pInfo[ playerid ][ pOnTime    ] ++;
            pInfo[ playerid ][ pExp ] ++;

            if ( ( pInfo[ playerid ][ pLevel ] + 1 ) * 4 <= pInfo[ playerid ][ pExp ] )
            {
                pInfo[ playerid ][ pLevel ] ++;
                pInfo[ playerid ][ pExp   ] = 0;
                SetPlayerScore ( playerid, pInfo[ playerid ][ pLevel ] );
                ShowInfoText   ( playerid, "~w~ Sveikiname Jums pakilo veik�jo lygis.", 4000);
                PlayerPlaySound( playerid, 1057, 0.0, 0.0, 0.0 );
            }
            PayLog( pInfo[ playerid ][ pMySQLID ], 9, -1, pInfo[playerid][pPayCheck] );
            pInfo[ playerid ][ pPayDayHad ] = 0;
            pInfo[ playerid ][ pPayCheck  ] = 0;

            if ( pInfo[ playerid ][ pJobContr  ] > 0 )
                pInfo[ playerid ][ pJobContr  ] --;

            if ( PlayerHasItemInInv( playerid, ITEM_VAISTAI ) )
            {
                InvInfo[ playerid ][ PlayerHasItemInInvEx( playerid, ITEM_VAISTAI ) ][ iAmmount ] -= 1;
                if ( InvInfo[ playerid ][ PlayerHasItemInInvEx( playerid, ITEM_VAISTAI ) ][ iAmmount ] == 0 )
                    ClearInvSlot( playerid, PlayerHasItemInInvEx( playerid, ITEM_VAISTAI ) );
            }
            if ( pInfo[ playerid ][ pLiga ] == 0 )
                Susirgti( playerid );
        }
        return 1;
}*/


stock PlayerPlayMusic( playerid )
{
    SetTimerEx( "StopMusic", 5000, false, "d", playerid );
    PlayerPlaySound(playerid, 1068, 0.0, 0.0, 0.0);
    return 1;
}

FUNKCIJA:StopMusic( playerid )
    return PlayerPlaySound( playerid, 1069, 0.0, 0.0, 0.0);


FUNKCIJA:Sekunde()
{
    // Jei dingo ry�ys su MySQL.
    // mysql_ping sugeb�jo crash'inti server�.
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

        new plstate = GetPVarInt( i, "PLAYER_STATE" );
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
        /*
        if ( Laikas[ i ] > 0 )
        {
            if( plstate != PLAYER_STATE_DRIVER )
                UpdatePlayerInfoText( i );

            Laikas[ i ] --;
            if ( Laikas[ i ] == 0)
                TimeEnd( i, LaikoTipas[ i ] );
        }*/
        /*
        if( pInfo[i][pJailTime] > 0 )
        {
            if ( pInfo[ i ][ pJail ] == 1 && IsAfk == 1 )
            {
                new airbrk = GetPVarInt( i, "AIRBRK" );
                if (!Data_IsPlayerInRangeOfCoords(i, 10.0, "ooc_jail"))
                {
                    SetPVarInt( i, "AIRBRK", airbrk + 1 );
                    if ( airbrk > 3 )
                        KickPlayer( "AC", i, "OOC jail AirBreak" );
                }
                else if ( airbrk > 0 )
                    SetPVarInt( i, "AIRBRK", airbrk - 1 );
                pInfo[ i ][ pJailTime ] --;
                UpdatePlayerInfoText( i );
            }

            
            else if ( pInfo[ i ][ pJail ] == 2 )
            {
                new airbrk = GetPVarInt( i, "AIRBRK" );
                if (!Data_IsPlayerInRangeOfCoords(i, 100.0, "ic_prison") && !PlayerToPoint( 100.0, i, 1772.46643,-1548.6113,9.913315) )
                {
                    SetPVarInt( i, "AIRBRK", airbrk + 1 );
                    if ( airbrk > 3 )
                        KickPlayer( "AC", i, "IC prison AirBreak" );
                }
                else if ( airbrk > 0 )
                    SetPVarInt( i, "AIRBRK", airbrk - 1 );
                pInfo[ i ][ pJailTime ] --;
                UpdatePlayerInfoText( i );
            }
            else if ( pInfo[ i ][ pJail ] == 3 )
            {
                new airbrk = GetPVarInt( i, "AIRBRK" );
                if (!Data_IsPlayerInRangeOfCoords(i, 10.0, "ic_custody"))
                {
                    SetPVarInt( i, "AIRBRK", airbrk + 1 );
                    if ( airbrk > 3 )
                        KickPlayer( "AC", i, "IC arrest AirBreak" );
                }
                else if ( airbrk > 0 )
                    SetPVarInt( i, "AIRBRK", airbrk - 1 );
                pInfo[ i ][ pJailTime ] --;
                UpdatePlayerInfoText( i );
            }
            
        }
        */
        /*
        if(pInfo[i][pJailTime] == 0 && pInfo[i][pJail] > 0)
        {
            Data_SetPlayerLocation(i, "jail_discharge");
            pInfo[i][pWantedLevel] = 0;
            pInfo[i][pJailTime] = 0;
            pInfo[i][pJail] = 0;
            ShowInfoText(i, "~w~Jus esate paleidziamas is kalejimo ~g~Sekmes...", 5000);
            UpdatePlayerInfoText( i );
            SaveAccount( i );
        }*/
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
        /*
        if(Mires[i] > 1)
        {
            //UpdatePlayerInfoText(i);
            Mires[i] --;
            //TogglePlayerControllable( i, false );
            
            if (GetPlayerState( i ) == PLAYER_STATE_ONFOOT)
                ApplyAnimation(i, "CRACK", "crckdeth2", 4.0, 1, 0, 0, 0, 0);
            
            switch (Mires[i])
            {
                case 1:
                {
                    StopLoopingAnim( i, false );
                    SetPlayerHealth( i, 0 );
                }
            }
        }
        */
        if(Ruko[i] > 0)
        {
            Ruko[i] --;
            new Float:HP;
            switch (Ruko[i])
            {
                case 0:
                {
                    cmd_ame(i, "numeta cigaret�s nuoruk� ant �em�s.");
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

FUNKCIJA:TazerTime( playerid )
return TogglePlayerControllable( playerid, 1 );

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

stock GetPlayerNameEx( playerid )
{
    new str[26];
    GetPlayerName(playerid,str,24);
    for(new i = 0; i < MAX_PLAYER_NAME; i++)
    {
        if (str[i] == '_') str[i] = ' ';
        if(pInfo[playerid][pMask] == 0) format( str, 26, "Kauk�tasis((%d))", pInfo[ playerid ][ pMySQLID ] );
    }
    return str;
}
stock strtok(const string[], &index)
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




stock GetName(playerid)
{
    new name[MAX_PLAYER_NAME];
    GetPlayerName(playerid,name,MAX_PLAYER_NAME);
    return name;
}
stock strrest(const string[], index)
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
/*
stock SendOOC(color,string[])
{
    foreach(Player,playerid)
    {
        if(TogChat[playerid][0] == true){
            SendChatMessage(playerid, color, string);
        }
    }
    return 1;
}
stock SendNEWS(color,string[])
{
    foreach(Player,playerid)
    {
        if(TogChat[playerid][1] == true){
            SendChatMessage(playerid, color, string);
        }
    }
    return 1;
}
*/
stock IsKeyJustDown(key, newkeys, oldkeys) { if((newkeys & key) && !(oldkeys & key)) return 1; return 0; }

stock SendChatMessage(playerid,color,text[])
{
    if(strlen(text) > 100)
    {
        new string[ 140 ],
//            message2[ 140 ],
            start = -1, 
            end = -1, 
            colorCount;

        // Suskai�iuojam kiek yra spalvos kod�, nes u� j� simbolius reik�s kompensuot ilg�.
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
stock SendChatMessageToAll(color,text[])
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
stock split(const strsrc[], strdest[][], delimiter)
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

stock GetClosestVehicleToVehicle(vehicleid, Float:distance)
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
stock GetNearestVehicle(playerid, Float:distance)
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
stock GetNearestPlayer(playerid, Float:distance)
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
stock LockVehicle(carid,type)
{
    foreach(Player,i)
    {
        SetVehicleParamsForPlayer(carid,i,0,type);
        cInfo[carid][cLock] = type;
    }
    return 1;
}
/*
stock SendTeamMessage(team, color, string[])
{
    foreach(Player,i)
    {
        if ( PlayerFaction( i ) == team && GetPVarInt( i, "TOG_FAMILY" ) == 1)
            SendChatMessage( i, color, string );
    }
    return 1;
}

*/
stock SendAdminWarningMessage(const format[], va_args<>)
{
    new str[ 180 ];
    va_format(str, sizeof (str), format, va_start<2>);
    strins(str, "[AdmWarn]", 0);

    foreach(new i : Player)
        if((IsPlayerAdmin(i) || GetPlayerAdminLevel(i)) && TogChat[ i ][ 3 ])
            SendChatMessage(i, COLOR_ADM, str);
    return 1;
}

stock SendAdminMessagePlayer( playerid, color, text[ ] )
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


stock SendAdminMessage(color, text[])
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

stock SendRadioMessage(chanel, slot, color, string[])
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
stock SendJobMessage(job, color, string[])
{
    foreach(Player,i)
    {
        if(IsPlayerConnected(i))
        if(pInfo[i][pJob] == job)
            SendChatMessage(i, color, string);
    }
    return 1;
}
stock ProxDetectorCords(Float:radi, string[], Float:pX, Float:pY, Float:pZ, col1, col2, col3, col4, col5, worldid, interiorid )
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
stock ProxDetector(Float:radi, playerid, string[], col1, col2, col3, col4, col5 )
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
stock ProxDetector2( Float:radi, playerid, string[], col1, col2, col3, col4, col5)
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
stock isAtFishPlace( playerid )
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
stock IsAtGasStation(playerid)
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
stock GetPlayerFirstName(playerid)
{
    new namestring[2][MAX_PLAYER_NAME];
    new name[MAX_PLAYER_NAME];
    GetPlayerName(playerid,name,MAX_PLAYER_NAME);
    split(name, namestring, '_');
    return namestring[0];
}
stock GetPlayerLastName(playerid)
{
    new namestring[2][MAX_PLAYER_NAME];
    new name[MAX_PLAYER_NAME];
    GetPlayerName(playerid,name,MAX_PLAYER_NAME);
    split(name, namestring, '_');
    return namestring[1];
}

stock FindNPCByName(npcname[])
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
stock ClearChatbox(playerid, lines)
{
    for(new i = 0; i < lines; i++)
    SendClientMessage(playerid, COLOR_WHITE, " ");
}
stock Susirgti(playerid)
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
            format(string,sizeof(string),"INFO: J�s susirgote %s liga, kuo grei�iau pavalgykite ir nepraraskite j�g�.",Ligos[ligosID]);
            SendClientMessage(playerid,COLOR_LIGHTRED,string);
            SendClientMessage(playerid,COLOR_LIGHTRED,"INFO: Patariame apsilankyti pas gydytoj�, arba pasitarti su vaistininku.");
            return 1;
        }
    }
    return 1;
}
stock MySQLCheckConnection()
{
    return 1;
        /*
    if(mysql_ping() == 1)
        return 1;
    else
    {
        ImpossibleLog("MySQL connection lsot.");
        print("MySQL: Prisijungimas yra nutr�k�s, serveris isjungiamas.");
        return SendRconCommand("exit");
    }
    */
}
/*
stock SuspectPlayer(playerid,crime[],sendername[])
{
    new string[256];
    format(string, sizeof(string), "[LSPD] Asmuo %s gavo �skaita nuo pareig�no: %s, �skaita: %s",GetName(playerid),sendername,crime);
    SendTeamMessage( 1, COLOR_POLICE, string );
    format(string, sizeof(string), "[LSPD] Policininkas %s �ra�� jums �skait�, esate kaltinamas %s, tai yra %d j�s� �skaita.", sendername,crime,pInfo[playerid][pWantedLevel]);
    SendChatMessage(playerid, COLOR_POLICE, string);
    format(string, sizeof(string), "INSERT INTO `crimes` (name,crime,reporter) VALUES ('%s','%s','%s')",GetName(playerid),crime,sendername);
    mysql_pquery(DbHandle, string);
    if( ( pInfo[playerid][pJail] > 0 && pInfo[playerid][pJail] != 1 ) && pInfo[playerid][pJailTime] > 0 )
        return true;

    pInfo[playerid][pWantedLevel] ++;

    if(pInfo[playerid][pWantedLevel] > 5)
        pInfo[playerid][pWantedLevel] = 6;
    return 1;
}
stock UsePDCMD(playerid)
{
    if(pInfo[ playerid ][ pMember ] == 2)
        return 1;
    else
        return 0;
}
*/
stock StartTimer(playerid,ilgis,tipas)
{
    if(Mires[playerid] > 0) return SendClientMessage(playerid, COLOR_LIGHTRED, "Persp�jimas: �iuo metu J�s esate komos b�senoje.");
    if(Laikas[playerid] == 0)
    {
        Laikas[playerid] = ilgis;
        LaikoTipas[playerid] = tipas;
        TogglePlayerControllable(playerid, false );
        SendClientMessage(playerid,COLOR_WHITE, " Galite sustabdyti prad�ta veiskma para�� /stop.");
 //       UpdatePlayerInfoText( playerid );
        return 1;
    }
    return 1;
}
stock TimeEnd(playerid,tipas)
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
                format( string, 126, " {FF6347}Persp�jimas: transporto priemon�s sutaisymo kaina yra $%d", VD );
                SendClientMessage( playerid, GRAD, string );
                return 1;
            }
            GivePlayerMoney( playerid, -VD );
            AddJobExp( playerid, 5+random(6) );
            SetVehicleHealth(veh,1000);
            RepairVehicle(veh);
            format( string, 126, " Automobilio sutvarkymas jums kainavo: $%d", VD );
            SendClientMessage(playerid, COLOR_WHITE, " Sveikiname, transporto priemon�s tvarkymas s�kmingai pavyko.");
            if(CheckUnfreeze(playerid))
                TogglePlayerControllable(playerid,true);
            return true;
        }
        /*
        case 2:
        {
            if ( GetPlayerMoney(playerid) < 450 )
                return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: transporto priemon�s perda�ymo kaina yra $450." );
            GivePlayerMoney( playerid, -450 );
            AddJobExp( playerid, 5+random(6) );
            cInfo[ veh ][ cColor ][ 0 ] = GetPVarInt( playerid, "PAINT1" );
            cInfo[ veh ][ cColor ][ 1 ] = GetPVarInt( playerid, "PAINT2" );

            ChangeVehicleColor(veh, cInfo[veh][cColor][0], cInfo[veh][cColor][1]);
            SendClientMessage(playerid, COLOR_WHITE, " Sveikiname, transporto priemon�s spalva buvo s�kmingai pakeista.");
            if(cInfo[ veh ][ cOwner ] > 0) SaveCar(veh);
            if(CheckUnfreeze(playerid))
            TogglePlayerControllable(playerid,true);
            return true;
        }
        case 3:
        {
            new MOD1 = GetPVarInt( playerid, "MOD" ),
                MOD2 = GetPVarInt( playerid, "MOD2" );

            if ( MOD1 != 1087 && MOD1 > 0)
            {
                cInfo[ veh ][ cWheels ] = MOD1;
                AddVehicleComponent( veh, cInfo[ veh ][ cWheels ] );
                GivePlayerMoney( playerid, -2000 );
                AddJobExp( playerid, 5+random(6) );
                SendClientMessage( playerid, COLOR_WHITE, " Sveikiname, automobilio ratlankiai buvo s�kmingai pakeisti � kitus.");
                SaveCar( veh );
                DeletePVar( playerid, "MOD" );
                if ( CheckUnfreeze( playerid ) )
                    TogglePlayerControllable( playerid, true );
                return 1;
            }
            if ( MOD2 == 1087 )
            {
                cInfo[ veh ][ cHidraulik ] = MOD2;
                AddVehicleComponent( veh, cInfo[ veh ][ cHidraulik ] );
                GivePlayerMoney( playerid, -5000 );
                AddJobExp( playerid, 5+random(6) );
                SendClientMessage( playerid, COLOR_WHITE, "  Sveikiname, automobilio hidraulikos modifikacija buvo s�kmingai �d�ta.");
                SaveCar( veh );
                DeletePVar( playerid, "MOD2" );
                if ( CheckUnfreeze( playerid ) )
                    TogglePlayerControllable( playerid, true );
                return 1;
            }
            if ( MOD1 == -1 )
            {
                RemoveVehicleComponent( veh, cInfo[ veh ][ cWheels ] );
                cInfo[ veh ][ cWheels ] = 0;
                SendClientMessage( playerid, COLOR_WHITE, " Modifikacija, buvo s�kmingai pa�alinta.");
                SaveCar( veh );
                DeletePVar( playerid, "MOD" );
                if ( CheckUnfreeze( playerid ) )
                    TogglePlayerControllable( playerid, true );
                return 1;
            }
            if ( MOD2 == -1 )
            {
                RemoveVehicleComponent( veh, cInfo[ veh ][ cHidraulik ] );
                cInfo[ veh ][ cHidraulik ] = 0;
                SendClientMessage( playerid, COLOR_WHITE, " Modifikacija, buvo s�kmingai pa�alinta.");
                SaveCar( veh );
                DeletePVar( playerid, "MOD2" );
                if ( CheckUnfreeze( playerid ) )
                    TogglePlayerControllable( playerid, true );
                return 1;
            }
            if ( MOD1 == -2 )
            {
                cInfo[ veh ][ cTuning ] = 0;
                SendClientMessage( playerid, COLOR_WHITE, " Modifikacija, buvo s�kmingai pa�alinta.");
                SendClientMessage( playerid, COLOR_WHITE, "* PASTABA: Modifikacija bus visi�kai nuimta, tik po automobilio priparkavimo.");
                SaveCar( veh );
                DeletePVar( playerid, "MOD1" );
                if ( CheckUnfreeze( playerid ) )
                    TogglePlayerControllable( playerid, true );
                return 1;
            }
            return true;
        }

        case 4:
        {
            TuneCarMods(pInfo[playerid][pCarGet]);
            SendClientMessage(playerid, COLOR_WHITE, " Tuningas buvo s�kmingai prid�tas.");
            if(cInfo[ veh ][ cOwner ] > 0) SaveCar(veh);
            if(CheckUnfreeze(playerid))
            TogglePlayerControllable(playerid,true);
            return true;
        }
        */
        case 5:
        {
            veh = GetPVarInt( playerid, "CAR_JACK" );
            LockVehicle(veh,0);
            new string[54];
            format(string,54,"* %s sulau�o automobilio spinele ir atrakina dureles.",GetPlayerNameEx(playerid));
            ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
            if(CheckUnfreeze(playerid))
            TogglePlayerControllable(playerid,true);
            CJLog( pInfo[ playerid ][ pMySQLID ], cInfo[veh][cID], "S�kmingai atrakino tr. priemon�s dureles" );
            return true;
        }
        case 6:
        {
            new string[126];
            format(string, 126, "* Automobilio variklis u�sived�.(( %s )).", GetPlayerNameEx( playerid ));
            ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
            StartingEngine[playerid] = false;
            Engine[GetPlayerVehicleID(playerid)] = true;
            VehicleEngine(GetPlayerVehicleID(playerid), 1);
            TogglePlayerControllable(playerid,true);
            CJLog( pInfo[ playerid ][ pMySQLID ], cInfo[GetPlayerVehicleID(playerid)][cID], "S�kmingai u�ved� tr. priemon�s varikl�" );
            return true;
        }
        /*case 7:
        {
            foreach(Busines,i)
            {
                if ( bInfo[ i ][ bProds ] < 50 )
                {
                    new string[ 126 ],
                        from = GetPVarInt( playerid, "KROVINYS" ),
                        ZoneName[ 28 ],
                        deliver;


                    Get2DZone( Unload[ from ][ 0 ],Unload[ from ][ 1 ], ZoneName, 28 );

                    DisablePlayerCheckpoint(playerid);
                    Checkpoint[playerid] = CHECKPOINT_NONE;
                    SetPlayerCheckPointEx( playerid, CHECKPOINT_TRUCK2, bInfo[ i ][ bEnter ][ 0 ],bInfo[ i ][ bEnter ][ 1 ],bInfo[ i ][ bEnter ][ 2 ], 14.0 );

                    switch( bInfo[ i ][ bType ] )
                    {
                        case 1: deliver = 6;
                        case 2: deliver = 0;
                        case 3: deliver = 7;
                    }
                    SetPVarInt( playerid, "KROVINYS2", deliver );

                    SendClientMessage( playerid, COLOR_LIGHTRED, "|________U�sakymo tvarkara�tis________|");
                    format           ( string, 126, "Krovinys: %s", Load[ deliver ][ lName ] );
                    SendClientMessage( playerid, COLOR_WHITE, string );
                    format           ( string, 126, "Gabenama i�: %s", ZoneName);
                    SendClientMessage( playerid, COLOR_WHITE, string );
                    Get2DZone        ( bInfo[ i ][ bEnter ][ 0 ],bInfo[ i ][ bEnter ][ 1 ], ZoneName, 28 );
                    format           ( string, 126, "Gabenama �: %s", ZoneName);
                    SendClientMessage( playerid, COLOR_WHITE, string );
                    TogglePlayerControllable( playerid, true );
                    format(string, 126, "* Darbininkai pakrauna krovin� � sunkve�im�. (( %s )).", GetPlayerNameEx( playerid ));
                    ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
                    return 1;
                }
            }
            new string[ 126 ],
                deliver = random( 13 ),
                from = GetPVarInt( playerid, "KROVINYS" ),
                ZoneName[ 28 ];
            SetPVarInt( playerid, "KROVINYS2", deliver );

            Get2DZone( Unload[ from ][ 0 ],Unload[ from ][ 1 ], ZoneName, 28 );

            DisablePlayerCheckpoint(playerid);
            Checkpoint[playerid] = CHECKPOINT_NONE;
            SetPlayerCheckPointEx( playerid, CHECKPOINT_TRUCK2, Load[ deliver ][ Load_x ], Load[ deliver ][ Load_y ], Load[ deliver ][ Load_z ], 6.0 );

            SendClientMessage( playerid, COLOR_LIGHTRED, "|________U�sakymo tvarkara�tis________|");
            format           ( string, 126, "Krovinys: %s", Load[ deliver ][ lName ] );
            SendClientMessage( playerid, COLOR_WHITE, string );
            format           ( string, 126, "Gabenama i�: %s", ZoneName);
            SendClientMessage( playerid, COLOR_WHITE, string );
            format           ( string, 126, "Gabenama �: %s", Load[ deliver ][ Place ]);
            SendClientMessage( playerid, COLOR_WHITE, string );
            TogglePlayerControllable( playerid, true );
            format(string, 126, "* Darbininkai pakrauna krovin� � sunkve�im�. (( %s )).",GetPlayerNameEx( playerid ));
            ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
            return 1;
        }
        case 8:
        {
            new string[ 126 ],
                mony = random( 50 ) + 50;

            format(string, 126, "* Darbininkai i�krauna krovin� i� sunkve�imio. (( %s )).", GetPlayerNameEx( playerid ));
            ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);

            DisablePlayerCheckpoint( playerid );
            SendClientMessage( playerid, COLOR_GREEN, "J�s s�kmingai pristat�te krovin�" );

            foreach(Busines,i)
            {
                if ( PlayerToPoint( 14.0, playerid, bInfo[ i ][ bEnter ][ 0 ],bInfo[ i ][ bEnter ][ 1 ],bInfo[ i ][ bEnter ][ 2 ] ) && bInfo[ i ][ bBank ] > mony )
                {
                    bInfo[ i ][ bProds ] = 100;
                    bInfo[ i ][ bBank  ] -= mony;
                    TogglePlayerControllable( playerid, true );
                    pInfo[ playerid ][ pPayCheck ] += mony;
                    Checkpoint[ playerid ] = CHECKPOINT_NONE;
                    AddJobExp( playerid, 2 );
                    return 1;
                }
            }
            new exp;
            if ( GetPVarInt( playerid, "KROVINYS2" ) >= 10 )
            {
                Mats += 60;
                mony += 30;
                exp += 2;
            }
            exp += 2;
            TogglePlayerControllable( playerid, true );
            pInfo[ playerid ][ pPayCheck ] += mony;
            AddJobExp( playerid, exp );
            Checkpoint[ playerid ] = CHECKPOINT_NONE;
            return 1;
        }
        case 9:
        {
            new string[ 126 ],
                deliver = random( 3 ) + 13,
                from = GetPVarInt( playerid, "KROVINYS" ),
                ZoneName[ 28 ];
            SetPVarInt( playerid, "KROVINYS2", deliver );

            Get2DZone( Unload[ from ][ 0 ],Unload[ from ][ 1 ], ZoneName, 28 );

            DisablePlayerCheckpoint(playerid);
            Checkpoint[playerid] = CHECKPOINT_NONE;
            SetPlayerCheckPointEx( playerid, CHECKPOINT_TRUCK2, Load[ deliver ][ Load_x ], Load[ deliver ][ Load_y ], Load[ deliver ][ Load_z ], 6.0 );

            SendClientMessage( playerid, COLOR_LIGHTRED, "________ Ve�tara�tis ________");
            format           ( string, 126, "Krovinys: %s", Load[ deliver ][ lName ] );
            SendClientMessage( playerid, COLOR_WHITE, string );
            format           ( string, 126, "Gabenama i�: %s", ZoneName);
            SendClientMessage( playerid, COLOR_WHITE, string );
            format           ( string, 126, "Gabenama �: %s", Load[ deliver ][ Place ]);
            SendClientMessage( playerid, COLOR_WHITE, string );
            TogglePlayerControllable( playerid, true );

            format(string, 126, "* Darbininkai pakrauna krovin� � sunkve�im�. (( %s )).", GetPlayerNameEx( playerid ));
            ProxDetector(20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
            return 1;
        }*/
        case 10:
        {
            new Float: Car_X,
                Float: Car_Y,
                Float: Car_Z,
                veh2 = GetPVarInt( playerid, "TOWING" );

            GetVehiclePos( veh, Car_X, Car_Y, Car_Z );
            if ( !PlayerToPoint( 10, playerid, Car_X, Car_Y, Car_Z ) )
                return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: J�s nesate �alia to automobilio ");

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
		SendClientMessage(playerid, COLOR_NEWS, "J�s� krovinys pakrautas!");
		KillTimer(VehicleLoadTimer[ playerid ]);
		VehicleLoadTimer[ playerid ] = -1;
		PlayerTextDrawHide(playerid, InfoText[ playerid ]);
		DeletePVar(playerid, "vehicleid");
	}
    return 1;
}
stock divmod( const number, const divider, &div, &mod )
{
    div = floatround( number / divider, floatround_floor );
    mod = number - div * divider;
}
stock CheckUnfreeze(playerid)
{
    if(Freezed[playerid] == true || Mires[playerid] > 0)
        return 0;
    else
        return 1;
}

stock encode_tires(tires1, tires2, tires3, tires4)
    return tires1 | (tires2 << 1) | (tires3 << 2) | (tires4 << 3);

stock CheckBox()
{
    foreach(Player,i)
        if(Boxing[i] == true) return 1;
    return 0;
}
stock BoxEnd(loser)
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
        format(string, 126, "[SAN NEWS] Bokso var�ybos baig�si, laim�jo %s pri�� %s.",GetName(winer),GetName(loser));
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
stock PreloadAnimsForPlayer( playerid )
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
stock PreloadAnimLib( playerid, animlib[ ] )
    return ApplyAnimation( playerid, animlib, "null", 0.0, 0, 0, 0, 0, 0);

stock OnePlayAnim(playerid,animlib[],animname[], Float:Speed, looping, lockx, locky, lockz, lp)
{
    ApplyAnimation(playerid, animlib, animname, Speed, looping, lockx, locky, lockz, lp);
    IsOnePlayAnim[playerid] = true;
    ShowInfoText( playerid, "~w~~h~Spauskite ~r~SPACE~w~~h~ kad sutabdytumet animacija.", 3000 );
}
stock BackAnim(playerid,animlib[],animname[], Float:Speed, looping, lockx, locky, lockz, lp,animback)
{
    BackOut[playerid] = animback;
    gPlayerUsingLoopingAnim[playerid] = true;
    ApplyAnimation(playerid, animlib, animname, Speed, looping, lockx, locky, lockz, lp);
    ShowInfoText( playerid, "~w~~h~Spauskite ~r~SPACE~w~~h~ kad sutabdytumet animacija.", 3000 );
}
stock LoopingAnim(playerid,animlib[],animname[], Float:Speed, looping, lockx, locky, lockz, lp)
{
    gPlayerUsingLoopingAnim[playerid] = true;
    UsingLoopAnim[ playerid ] = true;
    ApplyAnimation(playerid, animlib, animname, Speed, looping, lockx, locky, lockz, lp, 1);
    ShowInfoText( playerid, "~w~~h~Spauskite ~r~SPACE~w~~h~ kad sutabdytumet animacija.", 3000 );
}

stock TuneCarMods(vehicleid)
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

stock AddTuneToVehicle(vehicleid,type,playerid)
{
    new model = GetVehicleModel(vehicleid);
    switch (model)
    {
        case 558:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Persp�jimas: J�s neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_URANUS1;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_URANUS2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 559:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Persp�jimas: J�s neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_JESTER;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_JESTER2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 560:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Persp�jimas: J�s neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_SULTAN;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_SULTAN2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 561:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Persp�jimas: J�s neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_STRATUM;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_STRATUM2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 562:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Persp�jimas: J�s neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_ELEGY;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_ELEGY2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 565:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Persp�jimas: J�s neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_FLASH;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_FLASH2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 536:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Persp�jimas: J�s neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_BLADE1;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_BLADE2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 575:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Persp�jimas: J�s neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_BROADWAY1;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_BROADWAY2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 534:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Persp�jimas: J�s neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_REMINGTON1;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_REMINGTON2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 567:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Persp�jimas: J�s neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_SAVANA1;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_SAVANA2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 535:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Persp�jimas: J�s neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_SLAMVAN1;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_SLAMVAN2;
            StartTimer(playerid,180,4);
            return 1;
        }
        case 576:
        {
            if(GetPlayerMoney(playerid) > 7000)
                GivePlayerMoney(playerid,-7000);
            else return SendClientMessage(playerid,GRAD, "{FF6347}Persp�jimas: J�s neturite pakankamai pingu ($7000).");
            if(type == 1) cInfo[vehicleid][cTuning] = MOD_TORNADO1;
            else if(type == 2) cInfo[vehicleid][cTuning] = MOD_TORNADO2;
            StartTimer(playerid,180,4);
            return 1;
        }
        default: return SendClientMessage(playerid,GRAD, "{FF6347}Persp�jimas: J�s� automobilius negali buti tuninguojamas.");
    }
    return 1;
}
stock GetCarOwner(vehicleid)
{
    foreach(Player,playerid)
    {
        if ( IsPlayerLoggedIn(playerid) == 0 ) continue;
        if ( pInfo[ playerid ][ pMySQLID ] == cInfo[ vehicleid ][ cOwner ] )
        return playerid;
    }
    return INVALID_PLAYER_ID;
}
stock CheckCarKeys(playerid,vehicle)
{
    if ( pInfo[ playerid ][ pMySQLID ] == cInfo[ vehicle ][ cOwner ] ) return 1;
    else if ( pInfo[ playerid ][ pDubKey ] == cInfo[ vehicle ][ cID ] ) return 1;
    else if ( pInfo[ playerid ][ pMember ] == cInfo[ vehicle ][ cFaction ] && cInfo[ vehicle ][ cFaction ] > 0) return 1;
    else return 0;
}
stock PlayerToPlayer( Float:radi, playerid, targetid )
{
    new Float:pX,
        Float:pY,
        Float:pZ;
    GetPlayerPos( targetid, pX, pY, pZ );
    return IsPlayerInRangeOfPoint( playerid, radi, pX, pY, pZ );
}
stock PlayerToCar( Float:radi, playerid, veh )
{
    new Float:pX,
        Float:pY,
        Float:pZ;
    GetVehiclePos( veh, pX, pY, pZ );
    return IsPlayerInRangeOfPoint( playerid, radi, pX, pY, pZ );
}
stock GetOriginType(playerid)
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
    else if(!strcmp("�ekas",pInfo[playerid][pOrigin],true))
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
stock SendOrginMessage(playerid,text[])
{
    new string[256],
        tempstr[30];
    switch(GetOriginType(playerid))
    {
        case 2: tempstr = "Kini�ti�kai";
        case 3: tempstr = "Rusi�kai";
        case 4: tempstr = "Itali�kai";
        case 7: tempstr = "Ispani�kai";
        case 8: tempstr = "Japoni�kai";
        case 9: tempstr = "Olandi�kai";
        case 10: tempstr = "Portugali�kai";
        case 11: tempstr = "Kubieti�kai";
        case 12: tempstr = "Norvegi�kai";
        case 13: tempstr = "Voki�kai";
        case 15: tempstr = "Pranc�zi�kai";
        case 16: tempstr = "Turki�kai";
		case 17: tempstr = "Slovaki�kai"; 
		case 18: tempstr = "Graiki�kai";
		case 19: tempstr = "Baltarusi�kai";
		case 20: tempstr = "Ukrainieti�kai";
		case 21: tempstr = "Lietuvi�kai";
		case 22: tempstr = "Latvi�kai";
		case 23: tempstr = "Esti�kai";
		case 24: tempstr = "Lenki�kai";
		case 25: tempstr = "�eki�kai";
		case 26: tempstr = "Bulgari�kai";
		case 27: tempstr = "Arabi�kai";
		case 28: tempstr = "Suomi�kaii";
		case 29: tempstr = "Portugali�kai";
		case 30: tempstr = "Kroati�kai";
		case 31: tempstr = "Belgi�kai";
		case 32: tempstr = "Meksikieti�kai";
		case 33: tempstr = "Domininkieti�kai";
		case 34: tempstr = "Indii�kai";		
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
            format(string,256,"%s sako %s: Ne�inoma kalba",GetPlayerNameEx(playerid),tempstr);
        if(PlayerToPlayer( 11, playerid, i ) && virt == GetPlayerVirtualWorld( playerid ) && intt == GetPlayerInterior( playerid ))
            SendChatMessage(i,COLOR_FADE1,string);
    }
}

stock VehicleHasWindows( model )
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

stock IsVehicleBike( model )
{
    switch ( model )
    {
        case 581, 462, 521, 463, 522, 461, 448, 471, 468, 481, 523, 586, 509, 510:
        return true;
    }
    return false;
}

stock IsVehicleTaxed(model)
{
    if(IsVehicleBike(model))
        return false;
    else 
        return true;
}

stock GetTrailerPullingVehicle(vehicleid)
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
stock IsVehicleTrailer( model )
{
    switch ( model )
    {
        case 435, 450, 584, 591:
        return true;
    }
    return false;
}

stock IsPlayerAtFishBoat ( playerid )
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

stock IsVehicleTrucker( model )
{
    switch ( model )
    {
        case 530, 600, 543, 605, 422, 478, 554, 413, 459, 482, 440, 498, 609, 499, 414, 456, 455, 578, 443, 428, 403, 514, 515:
        return true;
    }
    return false;
}
stock IsWeaponHasAmmo( model )
{
    switch ( model )
    {
        case 0..15,19..21,44..47:
        return false;
    }
    return true;
}
stock VehicleHasEngine( model )
{
    switch ( model )
    {
        case 509, 481, 510:
        return false;
    }
    return true;
}
stock IsAPlane( model )
{
    switch ( model )
    {
        case 592, 577, 511, 512, 593, 520, 553, 476, 519, 460, 513, 548, 425,
             417, 487, 488, 497, 563, 447, 469:
        return true;
    }
    return false;
}
stock IsABoat( model )
{
    switch ( model )
    {
        case 472, 473, 493, 595, 484, 430, 453, 452, 446, 454:
            return true;
    }
    return false;
}
stock GetJobName(jobid)
{
    new txt[ 56 ];
    format( txt, 56, "%s", pJobs[ jobid ][ Name ] );
    return txt;
}
/*
stock GetPlayerRangName( playerid )
{
    new rtext[ 30 ],
        pfaction = PlayerFaction( playerid );
    switch( pInfo[ playerid ][ pRank ] )
    {
        case 1: format(rtext, 126, "%s", fInfo[pfaction][fRank1]);
        case 2: format(rtext, 126, "%s", fInfo[pfaction][fRank2]);
        case 3: format(rtext, 126, "%s", fInfo[pfaction][fRank3]);
        case 4: format(rtext, 126, "%s", fInfo[pfaction][fRank4]);
        case 5: format(rtext, 126, "%s", fInfo[pfaction][fRank5]);
        case 6: format(rtext, 126, "%s", fInfo[pfaction][fRank6]);
        case 7: format(rtext, 126, "%s", fInfo[pfaction][fRank7]);
        case 8: format(rtext, 126, "%s", fInfo[pfaction][fRank8]);
        case 9: format(rtext, 126, "%s", fInfo[pfaction][fRank9]);
        case 10: format(rtext, 126, "%s", fInfo[pfaction][fRank10]);
        case 11: format(rtext, 126, "%s", fInfo[pfaction][fRank11]);
        case 12: format(rtext, 126, "%s", fInfo[pfaction][fRank12]);
        case 13: format(rtext, 126, "%s", fInfo[pfaction][fRank13]);
        default: rtext = "Joks";
    }
    return rtext;
}
*/
stock ShowStats( giveplayerid, playerid )
{
        new string[ 180 ],
            spawnplace[ 256 ],
            nextexp = ( GetPlayerLevel(playerid) + 1 ) * 4,
            rankstr[32], 
            hunger[16];
        
        switch ( pInfo[ playerid ][ pSpawn ] )
        {
            case DefaultSpawn: spawnplace = "Idlewood Pizza Stack";
            case SpawnHouse: spawnplace = "Nomuojamas/nuosavas namas";
            case SpawnFaction: spawnplace = "Frakcijos vieta.";
            case SpawnBusiness: spawnplace = "Verslas";
            case SpawnLosSantos: spawnplace = "Los Santos Unity Station";
            default: spawnplace = "Idlewood Pizza Stack";
        }        
        if(pInfo[ playerid ][ pJob ] == JOB_TRUCKER) // Nes furistai ant tiek geresni u� kitus.
        {
            new hours = pInfo[ playerid ][ pJobHours ];
            if(hours >= 48)
                rankstr = "Profesionalus vairuotojas";
            else if(hours >= 32)
                rankstr = "Vairuotojas";
            else if(hours >= 24)
                rankstr = "Vairuotojas-Mokinys";
            else if(hours >= 12) 
                rankstr = "Profesionalus kurjeris";
            else
                rankstr = "Kurjeris-Mokinys";
        }  
        else 
            format(rankstr, sizeof(rankstr),"%d/%d",
                    pInfo[playerid][pJobLevel], pInfo[ playerid ][ pJobSkill ]);

        if(pInfo[ playerid ][ pHunger ] < 0)
            hunger = "Persivalg�s";
        else if(pInfo[ playerid ][ pHunger ] < 10)
            hunger = "Sotus";
        else if(pInfo[ playerid ][ pHunger ] < 15)
            hunger = "Alkanas";
        else
            hunger = "Labai alkanas";


		format           ( string, sizeof(string), "|__________________________________%s__________________________________|", 
		GetName(playerid));  
		SendClientMessage( giveplayerid, COLOR_GREEN, string );
		format           ( string, sizeof(string), "|VEIK�JAS| Lygis:[%d] Pra�aista valand�:[%d] Patirties ta�kai:[%d/%d] Am�ius:[%d] Lytis:[%s] Tautyb�:[%s]" ,
		GetPlayerLevel(playerid),GetPlayerConnectedTime(playerid),pInfo[playerid][pExp],nextexp,pInfo[playerid][pAge],pInfo[playerid][pSex],pInfo[playerid][pOrigin]);
		SendClientMessage( giveplayerid, COLOR_FADE1, string);
		format           ( string, sizeof(string), "|VEIK�JAS| Telefonas:[%d] Mir�i� skai�ius:[%d] Liga:[%s] Alkis:[%s]" ,
		pInfo[playerid][pPhone], GetPlayerDeaths(playerid),Ligos[pInfo[playerid][pLiga]], hunger);
		SendClientMessage( giveplayerid, COLOR_FADE2, string);		
		format           ( string, sizeof(string), "|VEIK�JAS| Rem�jo lygis:[%d] �sp�jimai:[%d] Atsiradimas:[%s] Gyvyb�s:[%d] J�ga:[%d]" ,
		pInfo[playerid][pDonator],pInfo[playerid][pWarn],spawnplace,100+ pInfo[ playerid ][ pHealthLevel ] * 3, pInfo[ playerid ][ pStrengthLevel ]);	
		SendClientMessage( giveplayerid, COLOR_FADE1, string);		
		format           ( string, sizeof(string), "|FINANSAI| Grynieji pinigai:[%d$] Banko s�skaitoje:[%d$] Pad�tas ind�lis:[%d$] Pal�kan� procentas: 0.5% " ,
		GetPlayerMoney(playerid),GetPlayerBankMoney(playerid),pInfo[ playerid ][ pSavings ]);
		SendClientMessage( giveplayerid, COLOR_FADE2, string);		
		format           ( string, sizeof(string), "|DARBAS| Dirba:[%s] Kontraktas:[%d] Rangas darbe:[%s] Patirties ta�kai darbe:[%d]" ,
		GetJobName(pInfo[playerid][pJob]),pInfo[playerid][pJobContr], rankstr,(( pInfo[ playerid ][ pJobLevel ] +1 ) * 100));	
		SendClientMessage( giveplayerid, COLOR_FADE1, string);		
		//format           ( string, sizeof(string), "|FRAKCIJA| Frakcijos pavadinimas:[%s (ID%d)] Rangas frakcijoje: [Nr.%d, %s]" ,
		//fInfo[ PlayerFaction( playerid ) ][ fName ],pInfo[ playerid ][ pMember ], pInfo[ playerid ][ pRank ], GetPlayerRangName( playerid ));
		SendClientMessage( giveplayerid, COLOR_FADE2, string);
        format           ( string, sizeof(string), "|PRIKLAUSOMYB�| Heroinas:[%d] Amfetaminas:[%d] Kokainas:[%d] Metamfetaminas[%d] Ekstazi:[%d]",
		pInfo[ playerid ][ pHeroineAddict ], pInfo[ playerid ][ pAmfaAddict ], pInfo[ playerid ][ pCocaineAddict ], pInfo[ playerid ][ pMetaAmfaineAddict ], pInfo[ playerid ][ pExtazyAddict ] );
        SendClientMessage( giveplayerid, COLOR_FADE1, string );
        format           ( string, sizeof(string), "|PRIKLAUSOMYB�| PCP:[%d] Krekas:[%d] Opiumas:[%d]",
		pInfo[ playerid ][ pPCPAddict ], pInfo[ playerid ][ pCrackAddict ], pInfo[ playerid ][ pOpiumAddict ] );
        SendClientMessage( giveplayerid, COLOR_FADE2, string );		
		format           ( string, sizeof(string), "|ADMINISTRACIJA| Int:[%d], VirtW[%d], Administratoriaus lygis:[%d]", 
		GetPlayerInterior(playerid),GetPlayerVirtualWorld(playerid),GetPlayerAdminLevel(playerid));
		if( GetPlayerAdminLevel(giveplayerid) > 0 )		
		SendClientMessage( giveplayerid, COLOR_WHITE, string);
		format           ( string, sizeof(string), "--------------------------------------------%s--------------------------------------------|", 
		GetName(playerid));  
		SendClientMessage( giveplayerid, COLOR_GREEN, string );
        return 1;
}

stock ShowPlayerInfoText( playerid )
{
    printf("ltrp.pwn ShowPlayerInfoText(%d) called", playerid);
    //PlayerTextDrawShow( playerid, Greitis[ playerid ] );
    return 1;
}
stock HidePlayerInfoText( playerid )
{
    PlayerTextDrawHide( playerid, Greitis[ playerid ] );
    return 1;
}
stock ShowInfoText( playerid, text[ ], time)
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
/*
stock Jail(kas[],playerid,time, kodel[])
{
    new string[ 216 ],
        name[ MAX_PLAYER_NAME ],
        ip[ 16 ];

    GetPlayerName(playerid,name,MAX_PLAYER_NAME);

    foreach(Player,i)
    {
        if( !strcmp(kas,GetName(i),true) )
        {
            format(string,216, "INSERT INTO `nuobaudos` (Kas, Ka, Kam, Priezastis) VALUES('%d', 'u�dar� � kal�jim�', '%d', '%s')", pInfo[ i ][ pMySQLID ], pInfo[ playerid ][ pMySQLID ], kodel);
            mysql_pquery(DbHandle, string);
            break;
        }
    }

    format                ( string, 126, "AdmCmd Administratorius %s pasodino � kal�jim�%s, %d minut�ms.", kas, name, time);
    SendClientMessageToAll( COLOR_LIGHTRED, string );
    format                ( string, 126, "AdmCmd Nurodyt� prie�astis: %s ",kodel);
    SendClientMessageToAll( COLOR_LIGHTRED, string );
    pInfo[ playerid ][ pJailTime ] = time*60 ;
    pInfo[ playerid ][ pJail     ] = 1;
    Data_SetPlayerLocation(playerid, "ooc_jail");
    if( time > 0 )
        ResetPlayerWeapons   ( playerid );

    GetPlayerIp(playerid,ip,16);

    MySQLCheckConnection();
    SaveAccount( playerid );
    return 1;
}
*/
stock NearPhone( playerid )
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
stock NearBankomat( playerid )
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

stock SetPlayerCheckPointEx( playerid, type, Float:x, Float:y, Float:z, Float:a )
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
stock SetPlayerRaceCheckPointEx( playerid, cptype, type, Float:x, Float:y, Float:z, Float:nextx, Float:nexty, Float:nextz, Float:size )
{
    SetPlayerRaceCheckpoint( playerid, type, x, y, z, nextx, nexty, nextz, size );
    Checkpoint[ playerid ] = cptype;
    return 1;
}
/*
stock setLicenseCp( playerid )
{
    switch( GetPVarInt( playerid, "LIC_TYPE" ) )
    {

//Automobilio ir motociklo licenzijos laikymo kordinat�s
        case 1,2:
        {
            switch( GetPVarInt( playerid, "LIC_CP" ) )
            {
				case 1: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2433.3464,-1475.4110,23.5458,5.0);
				case 2: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2474.5378,-1447.1062,24.6292,5.0);
				case 3: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2551.2375,-1446.5909,32.9943,5.0);
				case 4: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2623.5039,-1446.4851,30.9974,5.0);
				case 5: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2644.4197,-1420.3839,29.9973,5.0);
				case 6: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2675.4060,-1443.1221,30.0867,5.0);
				case 7: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2706.2893,-1509.9299,30.0624,5.0);
				case 8: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2765.7788,-1491.7843,28.2176,5.0);
				case 9: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2857.5083,-1490.9799,10.4573,5.0);
				case 10: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2878.6841,-1421.1472,10.5704,5.0);
				case 11: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2821.7412,-1385.2881,17.8054,5.0);
				case 12: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2799.2200,-1298.7750,39.4824,5.0);
				case 13: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2747.9302,-1255.6283,59.1255,5.0);
				case 14: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2611.9409,-1253.9144,47.9993,5.0);
				case 15: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2464.1138,-1254.3789,24.4127,5.0);
				case 16: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2397.5059,-1254.5380,23.5399,5.0);
				case 17: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2369.0769,-1284.1478,23.5522,5.0);
				case 18: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2316.9075,-1298.2368,23.7711,5.0);
				case 19: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2301.8547,-1364.8926,23.5720,5.0);
				case 20: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2244.6155,-1380.9020,23.5498,5.0);
				case 21: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2211.2725,-1426.3003,23.5350,5.0);
				case 22: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2210.4849,-1475.2710,23.5340,5.0);
				case 23: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2210.2141,-1523.9452,23.5441,5.0);
				case 24: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2199.3867,-1623.4713,15.6714,5.0);
				case 25: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2174.0693,-1633.8223,14.3178,5.0);
				case 26: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2122.5835,-1616.0321,13.1031,5.0);
				case 27: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2080.0620,-1654.7314,13.1068,5.0);
				case 28: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2079.6543,-1706.7753,13.1066,5.0);
				case 29: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2079.6130,-1735.2004,13.1029,5.0);
				case 30: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2096.4258,-1754.0223,13.1176,5.0);
				case 31: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2126.2566,-1754.7616,13.1226,5.0);
				case 32: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2168.0786,-1754.4491,13.0959,5.0);
				case 33: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2254.0505,-1733.6403,13.0988,5.0);
				case 34: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2333.9773,-1734.3068,13.0989,5.0);
				case 35: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2405.4519,-1734.4344,13.1003,5.0);
				case 36: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2433.1086,-1642.9390,26.9986,5.0);
				case 37: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2433.3088,-1603.4298,25.7429,5.0);
				case 38: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 2433.5308,-1559.5063,23.5441,5.0);
            }
        }

//Skraidymo licenzijos laikymo kordinat�s
        case 3:
        {
            switch( GetPVarInt( playerid, "LIC_CP" ) )
            {
				 case 1: SetPlayerRaceCheckPointEx( playerid, CHECKPOINT_LIC, 3,1918.3333,-2257.5476,19.3346,1916.1844,-2306.5242,78.6446,10.0 );
				 case 2: SetPlayerRaceCheckPointEx( playerid, CHECKPOINT_LIC, 3,1916.1844,-2306.5242,78.6446,1701.5432,-2387.9390,113.0942, 10.0 );
				 case 3: SetPlayerRaceCheckPointEx( playerid, CHECKPOINT_LIC, 3,1701.5432,-2387.9390,113.0942,1447.6058,-2242.4688,144.6308, 10.0 );
				 case 4: SetPlayerRaceCheckPointEx( playerid, CHECKPOINT_LIC, 3,1447.6058,-2242.4688,144.6308,1210.0662,-2004.8344,152.9503, 10.0 );
				 case 5: SetPlayerRaceCheckPointEx( playerid, CHECKPOINT_LIC, 3,1210.0662,-2004.8344,152.9503,925.8694,-1798.6184,136.2259, 10.0 );
				 case 6: SetPlayerRaceCheckPointEx( playerid, CHECKPOINT_LIC, 3,925.8694,-1798.6184,136.2259,535.4799,-1650.8319,122.6211, 10.0 );
				 case 7: SetPlayerRaceCheckPointEx( playerid, CHECKPOINT_LIC, 3,535.4799,-1650.8319,122.6211,20.3207,-1320.7156,146.6390, 10.0 );
				 case 8: SetPlayerRaceCheckPointEx( playerid, CHECKPOINT_LIC, 3,20.3207,-1320.7156,146.6390,-209.3477,-1445.8248,147.5437, 10.0 );
				 case 9: SetPlayerRaceCheckPointEx( playerid, CHECKPOINT_LIC, 3,-209.3477,-1445.8248,147.5437,75.2898,-2016.5239,123.6778, 10.0 );
				 case 10: SetPlayerRaceCheckPointEx( playerid, CHECKPOINT_LIC, 3,75.2898,-2016.5239,123.6778,370.7771,-2128.4263,143.4955, 10.0 );
				 case 11: SetPlayerRaceCheckPointEx( playerid, CHECKPOINT_LIC, 3,370.7771,-2128.4263,143.4955,943.4516,-2188.8318,165.2468, 10.0 );
				 case 12: SetPlayerRaceCheckPointEx( playerid, CHECKPOINT_LIC, 3,943.4516,-2188.8318,165.2468,1463.1583,-2273.5215,140.7639, 10.0 );
				 case 13: SetPlayerRaceCheckPointEx( playerid, CHECKPOINT_LIC, 3,1463.1583,-2273.5215,140.7639,1930.7386,-2248.5745,14.9643, 10.0 );
				 case 14: SetPlayerRaceCheckPointEx( playerid, CHECKPOINT_LIC, 3,1930.7386,-2248.5745,14.9643,1930.7386,-2248.5745,14.9643, 10.0 );
            }
        }

//Laivo licenzijos laikymo kordinat�s
        case 4:
        {
            switch( GetPVarInt( playerid, "LIC_CP" ) )
            {
				case 1: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 722.9761,-1601.5739,-0.0674,5.0); //
				case 2: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 723.0297,-1878.1135,-0.2829,5.0); //
				case 3: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 605.8445,-2010.0400,-0.4840,5.0); //
				case 4: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 327.2888,-2103.7786,-0.1991,5.0); //
				case 5: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 73.2563,-1763.0634,0.0182,5.0); //
				case 6: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 75.2143,-1400.7008,-0.1708,5.0); //
				case 7: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 32.8657,-1430.4677,-0.2938,5.0); //
				case 8: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 7.8134,-1695.4635,-0.5832,5.0); //
				case 9: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 103.3241,-1971.6035,-0.5250,5.0); //
				case 10: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 302.8342,-1927.3613,-0.2275,5.0); //
				case 11: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 375.8572,-2117.1875,-0.0151,5.0); //
				case 12: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 678.6392,-2024.4448,-0.3480,5.0); //
				case 13: SetPlayerCheckPointEx( playerid, CHECKPOINT_LIC, 718.8523,-1628.1936,-0.1787,5.0); //
            }
        }
    }
    return 1;
}
*/
/*
stock FactionID( mysqlid )
{
    foreach(Faction,id)
    {
        if ( fInfo[ id ][ fID ] == mysqlid )
        return id;
    }
    return 0;
}
stock GetPlayerFactionId(playerid)
    return pInfo[ playerid ][ pMember ];

stock PlayerFaction( playerid )
{
    foreach(Faction,id)
    {
        if ( pInfo[ playerid ][ pMember ] == fInfo[ id ][ fID ] )
        return id;
    }
    return 0;
}
*/
/*
stock savePlayerNotes( playerid, slot )
{
    new string[ 126 ],
        string2[ 126 ],
        rows,
        ministr[ 8 ],
        Cache:result;

    format     ( string, 126, "SELECT slot FROM notes WHERE owner = %d AND slot = %d", pInfo[ playerid ][ pMySQLID ], slot );
    result = mysql_query(DbHandle,  string );
    rows = cache_get_row_count();

    if ( !rows )
    {
        format           ( ministr, 8, "NOTE_%d", slot );
        GetPVarString    ( playerid, ministr, string2, 126 );
        format           ( ministr, 8, "NOTE2_%d", slot );
        format           ( string2, sizeof(string2), "%s/%d", string2, GetPVarInt ( playerid, ministr ) );
        format     ( string, 200,"INSERT INTO notes (owner,slot,note) VALUES (%d,%d,'%s')", pInfo[ playerid ][ pMySQLID ], slot, string2 );
        mysql_query(DbHandle,  string, false);
    }
    else
    {
        format           ( ministr, 8, "NOTE_%d", slot );
        GetPVarString    ( playerid, ministr, string2, 126 );
        format           ( ministr, 8, "NOTE2_%d", slot );
        format           ( string2, sizeof(string2), "%s/%d", string2, GetPVarInt ( playerid, ministr ) );
        format     ( string, 200,"UPDATE notes SET note = '%s' WHERE owner = %d AND slot = %d", string2, pInfo[ playerid ][ pMySQLID ], slot );
        mysql_query(DbHandle,  string, false);
    }
    cache_delete(result);
    return 1;
}
*/
/*
stock LoadGarbage()
{
    new index = 0, Float:pos[6], misssionLocation[32];

    new Cache:result = mysql_query(DbHandle, "SELECT * FROM `garbage_positions`");

    for(new i = 0; i < cache_get_row_count(); i++)
    {
        if(index >= MAX_GARBAGE_CANS)
        {
            printf("KLAIDA. Duomen� baz�je yra daugiau �iuk�li�(%d) nei leid�ia limitas(" #MAX_GARBAGE_CANS ")", cache_get_row_count());
            break;
        }
        GarbageInfo[ index ][ gModel ] = cache_get_field_content_int(i, "model_id");
        pos[ 0 ] = cache_get_field_content_float(i, "pos_x");
        pos[ 1 ] = cache_get_field_content_float(i, "pos_y");
        pos[ 2 ] = cache_get_field_content_float(i, "pos_z");
        pos[ 3 ] = cache_get_field_content_float(i, "rot_x");
        pos[ 4 ] = cache_get_field_content_float(i, "rot_y");
        pos[ 5 ] = cache_get_field_content_float(i, "rot_z");
        cache_get_field_content(i, "mission_location", misssionLocation);
        GarbageInfo[ index ][ gObjectId ] = CreateDynamicObject(GarbageInfo[ index ][ gModel ], pos[0], pos[1], pos[2], pos[3], pos[4], pos[5]);

        if(!strcmp(misssionLocation, "Rodeo", true))
            GarbageInfo[ index ][ gMission ] = TRASH_MISSION_MONTGOMERY;
        else if(!strcmp(misssionLocation, "Market", true))
            GarbageInfo[ index ][ gMission ] = TRASH_MISSION_DILIMORE;
        else if(!strcmp(misssionLocation, "Mulholand", true))
            GarbageInfo[ index ][ gMission ] = TRASH_MISSION_POLOMINO_CREEK;
		else if(!strcmp(misssionLocation, "Jefferson", true))
            GarbageInfo[ index ][ gMission ] = TRASH_MISSION_JEFFERSON;
        else if(!strcmp(misssionLocation, "Idlewood", true))
            GarbageInfo[ index ][ gMission ] = TRASH_MISSION_IDLEWOOD;

        index++;
    }
    cache_delete(result);
    printf("Serveryje yra sukurta %d siuksliu konteineriai.", index);
}*/

stock LoadIndustries()
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

    // Tuo pa�iu ir Laivo objektus sukuriam.
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


stock UpdateIndustryInfo(index)
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

stock LoadTruckerCargo()
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
stock LoadCommodities()
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



stock nullVehicle( vehicleid )
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
stock VehicleEngine(vehicleid, param)
{
    new engine, lights, alarm, doors, bonnet, boot, objective;

    GetVehicleParamsEx(vehicleid, engine, lights, alarm, doors, bonnet, boot, objective);
    SetVehicleParamsEx(vehicleid, param, lights, alarm, doors, bonnet, boot, objective);
}
stock VehicleAlarm(vehicleid, param)
{
    new engine, lights, alarm, doors, bonnet, boot, objective;
    GetVehicleParamsEx(vehicleid, engine, lights, alarm, doors, bonnet, boot, objective);
    SetVehicleParamsEx(vehicleid, engine, lights, param, doors, bonnet, boot, objective);
}
stock GetVehicleName( model )
{
    new str [ 24 ];
    format( str, 24, "%s", aVehicleNames[ model - 400 ] ) ;
    return str;
}
stock GetVehicleFuelTank( model )
    return vBakas[ model - 400 ];

stock GetVehicleTrunkSlots( model )
    return vSlotai[ model - 400 ];

/*
stock GetVehicleCargoLimit(model)
    return vSlotai3[ model - 400 ];
*/
//Sutvarkom kick,ban ir t.t cmds
/*forward _Kick ( playerid );
public _Kick ( playerid ) Kick ( playerid );
#define Kick(%0) SetTimerEx ( "_Kick", 1000, 0, "d", %0 )*/
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
    else if ( strlen ( result ) > 120 ) SendClientMessage( playerid, COLOR_GREY, "Nedaugiau 120 simboli�." );
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

stock TeleportPlayerToCoord( playerid, Float:x, Float:y, Float:z )
{
    // Funkcija: TeleportPlayerToCoord( playerid, Float:x, Float:y, Float:z )
    // Nuteleportuos �aid�j� � tam tikr� koordinat�

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
            SendClientMessage( playerid, COLOR_POLICE,"[LSPD] Dabar nusistatykite norim� pozicij� �enkleliui," );
            SendClientMessage( playerid, COLOR_RED,"[LSPD] Nor�dami pa�alinti �enklel� naudokita komand�: /rbadge." );
    }
    else
        SendClientMessage( playerid, COLOR_RED,"J�s neesate policininkas.");
    return true;

}

CMD:rbadge( playerid, params[] )
{
    #pragma unused params
    if (pInfo[playerid][pMember] == 2)
    {
        SendClientMessage( playerid, COLOR_POLICE,"[LSPD] �enklelis buvo pa�alintas");
        RemovePlayerAttachedObject( playerid, 6 );
    }
    else
        SendClientMessage( playerid, COLOR_LIGHTRED,"Klaida, negalite naudoti komandos neb�dami pareig�n�.");
    return true;

}

/*
CMD:vest( playerid, params[ ] )
{
    #pragma unused params
    if ( UsePDCMD( playerid ) == 0 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti �io veiksmo nedirbdami policijos departamente." );
    if(!PDJOBPlace(playerid)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite persirengti neb�damas persirengimo kabinoje/kambaryje.");
    new
        Float:armour;
    GetPlayerArmour( playerid, armour );
    if( armour > 0 )
    {
        SendClientMessage( playerid, COLOR_POLICE,"[LSPD] Neper�aunama liemen� buvo nuimta." );
        SetPlayerArmour( playerid, 0 );
        RemovePlayerAttachedObject( playerid, 5 );
    }
    else
    {
        SendClientMessage( playerid, COLOR_POLICE,"[LSPD] Neper�aunama liemen� buvo u�d�ta." );
        SetPlayerArmour( playerid, 100 );
        SetPlayerAttachedObject( playerid, 5, 19142, 1, 0.1,  0.05, 0.0,  0.0, 0.0, 0.0 );
        EditAttachedObject( playerid, 5 );
    }
    return 1;
}
*/
/*
public OnLookupComplete(playerid)
{
    if(IsProxyUser(playerid))
        KickPlayer( "AC", playerid, "Proxy" );
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
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, j�s neturite skarel�s arba j� naudojate pats.");

    if(GetPlayerSpecialAction(playerid) == SPECIAL_ACTION_CUFFED)
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Niekam negalite atri�ti/u�ri�ti rai��io kai j�s� rankos surakintos.");

    if(sscanf(params, "u", targetid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas /blindfold [�aid�jo ID/Dalis vardo]");

    if(!IsPlayerConnected(targetid))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "Tokio �aid�jo n�ra!");

    if(!IsPlayerInRangeOfPlayer(playerid, targetid, 5.0))
        return SendClientMessage(playerid, COLOR_LIGHTRED, "�aid�jas yra per toli.");


    if(IsBlindfolded[ targetid ])
    {
        format(string, sizeof(string), "atri�a aki� rai�t� nuo %s veido", GetPlayerNameEx(targetid));
        cmd_me(playerid, string);
        TextDrawHideForPlayer(targetid, BlindfoldTextdraw);
        SetCameraBehindPlayer(targetid);
        IsBlindfolded[ targetid ] = false;
    }
    else 
    {
        format(string, sizeof(string), "%s nori jums ant aki� u�ri�ti rai��. Per j� nieko nematysite. Ra�ykite /accept blindfold %d", GetPlayerNameEx(playerid), playerid);
        SendClientMessage(targetid, COLOR_NEWS, string);
        format(string, sizeof(string), "Veik�jas %s gavo pra�ym� leisti u�ri�ti jam rai�t� ant aki�, palaukite kol veik�jas atsakys.", GetPlayerNameEx(targetid));
        SendClientMessage(playerid, COLOR_NEWS, string);

        Offer[ targetid ][ 8 ] = playerid;
    }
    return 1;
}*/
/*
CMD:mask( playerid, params[ ] )
{   
    #pragma unused params
    new string[ 64 ],
        bool:found;
    if ( PlayerFaction( playerid ) != 1 && pInfo[ playerid ][ pDonator ] <= 1 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: kauke galite naudoti tik nuo 2 r�m�jo lygio arba turi b�ti PD" );

    foreach(Player, x)
    {
        if(GetPlayerAdminLevel(x) >= 1 && AdminDuty[ x ])
        {
            found = true;
            break;
        }
    }

    if(pInfo[playerid][pMask] == 1)
    {
        if ( !found ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Persp�jimas: kauke galite naudoti tik tada kai yra administratori�" );
        format      ( string, sizeof(string), "* %s u�simauna� kauk�.", GetPlayerNameEx( playerid ));
        ProxDetector( 20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
        pInfo[ playerid ][ pMask ] = 0;
        foreach(Player,i)
        {
            ShowPlayerNameTagForPlayer(i, playerid, pInfo[playerid][pMask]);
        }
        return 1;
    }
    else
    {
        format      ( string, sizeof(string), "* %s nusimauna� kauk�.", GetPlayerNameEx( playerid ) );
        ProxDetector( 20.0, playerid, string, COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE,COLOR_PURPLE);
        pInfo[ playerid ][ pMask ] = 1;
        foreach(Player,i)
        {
            ShowPlayerNameTagForPlayer(i, playerid, pInfo[playerid][pMask]);
        }
        return 1;
    }
}
*/

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
    SendAdminMessage(0xFF0000FF, "D�mesio. Dingo ry�ys su duomen� baze. Jei �ios �inut�s t�sis, imkit�s veiksm�.");
    format(string, sizeof(string),"Bandoma prisijungti v�l: %d", mysql_reconnect());
    SendAdminMessage(0xFF0000FF, string);
}

stock IsNumeric(const string[]) 
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