



#define MAX_ITEM_NAME 					32
#define ITEM_NO_DURABILITY 				(-1)

enum E_ITEM_FUNCTION_PARAMETERS 
{
	NoParameters = 0,
	PlayerId = 1,
	ItemId = 2,

};

enum E_ITEM_DATA 
{
	Id,
	Name[ MAX_ITEM_NAME ],
	bool:IsStackable,
	bool:IsContainer,
	MaxDurability,
	UsageFunction[ 32 ],
	E_ITEM_FUNCTION_PARAMETERS:UsageFunctionArgs,
};



stock static const ItemData[ ][ E_ITEM_DATA ] 
{
	{ITEM_PHONE, 		"Mobilusis tel.", 			false, false, 	ITEM_NO_DURABILITY, "OnPlayerUsePhone", PlayerId | ItemId},
	{ITEM_MASK, 		"Veido kaukë", 				false, false, 	ITEM_NO_DURABILITY, "OnPlayerUseMask", PlayerId | ItemId},
	{ITEM_RADIO, 		"Racija", 					false, false, 	ITEM_NO_DURABILITY, "cmd_radiohelp", PlayerId},
	{ITEM_ZIB, 			"Þiebtuvëlis", 				false, false, 	20, "", NoParameters},
	{ITEM_CIG,			"Cigareèiø pakelis", 		false, true,	20, "OnPlayerStartSmoking", PlayerId | ItemId},
	{ITEM_FUEL,			"Degalø bakelis", 			false, true, 	30, "OnPlayerUseFuelTank", PlayerId | ItemId},
	{ITEM_TOLKIT,		"Árankiø komplektas",		false, false, 	ITEM_NO_DURABILITY, "OnPlayerUseToolkit", PlayerId | ItemId},
	{ITEM_CLOCK,		"Rankinis laikrodis",		false, false,	ITEM_NO_DURABILITY, "OnPlayerUseWatch", PlayerId | ItemId},
	{ITEM_DICE,			"Loðimo kauliuka", 			false, false,	ITEM_NO_DURABILITY, "OnPlayerUseDice", PlayerId | ItemId},
	{ITEM_VAISTAI,		"Vaistai",					true, false,	1, "OnPlayerUseMedicine", PlayerId | ItemId},
	{ITEM_WEED,			"Marihuana",				true, false,	1,	"OnPlayerStartSmoking", PlayerId | ItemId },
	{ITEM_SEED,			"Sëklos", 					true, false, 	1, "OnPlayerUseWeedSeeds", PlayerId | ItemId},
	{ITEM_DRUGS,		"Medikamentai",				false, false	ITEM_NO_DURABILITY, "", NoParameters},
	{ITEM_HERAS,		"Heroinas",					true, false, 	1,	"OnPlayerUseHeroin", PlayerId | ItemId},
	{ITEM_MATS,			"Paketai",					true, false,	1, 	"OnPlayerUseWeaponPacket", PlayerId | ItemId},
	
};






