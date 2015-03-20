



#define MAX_ITEM_NAME 					64

#define Item:%0(%1)						forward %0(%1); public %0(%1)

#define INVALID_ITEM_ID					(-1)

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
	bool:IsDrug,
	bool:IsWeapon,
	bool:IsStackable,
	bool:IsContainer,
	bool:IsDegradable,
	ObjectModel,
	MaxDurability,
	MaxCapacity,
	UsageFunction[ 32 ],
	E_ITEM_FUNCTION_PARAMETERS:UsageFunctionArgs,
};



stock static const ItemData[ ][ E_ITEM_DATA ] =
{
	{INVALID_ITEM_ID,		"Tu��ia",									false, false, false, false, false,	0,		0,		0,	"",	NoParameters},
	{ITEM_PHONE, 			"Mobilusis tel.", 							false, false, false, false, false,	0,		0,	 	0, "OnPlayerUsePhone", PlayerId | ItemId},
	{ITEM_MASK, 			"Veido kauk�", 								false, false, false, false, false,	0,		0,	 	0, "OnPlayerUseMask", PlayerId | ItemId},
	{ITEM_RADIO, 			"Racija", 									false, false, false, false, false,	0,		0,	 	0, "cmd_radiohelp", PlayerId},
	{ITEM_ZIB, 				"�iebtuv�lis", 								false, false, false, false, true, 	0,		20,	 	20, "", NoParameters},
	{ITEM_CIG,				"Cigare�i� pakelis", 						false, false, false, true, false, 	0,		0,		20, "OnPlayerStartSmoking", PlayerId | ItemId},
	{ITEM_FUEL,				"Degal� bakelis", 							false, false, false, true,  false,	0,		0,		30, "OnPlayerUseFuelTank", PlayerId | ItemId},
	{ITEM_TOLKIT,			"�ranki� komplektas",						false, false, false, false, false,	0,		0,	 	0, "OnPlayerUseToolkit", PlayerId | ItemId},
	{ITEM_CLOCK,			"Rankinis laikrodis",						false, false, false, false, false,	0,		0,		0, "OnPlayerUseWatch", PlayerId | ItemId},
	{ITEM_DICE,				"Lo�imo kauliuka", 							false, false, false, false, false,	0,		0,		0, "OnPlayerUseDice", PlayerId | ItemId},
	{ITEM_VAISTAI,			"Vaistai",									false, false, true, false, false, 	0,		0,		1, "OnPlayerUseMedicine", PlayerId | ItemId},
	{ITEM_WEED,				"Marihuana",								true, false, true, false, false, 	0,		0,		1,	"OnPlayerStartSmoking", PlayerId | ItemId },
	{ITEM_SEED,				"S�klos", 									false, false, true, false,  false,	0,		0,		1, "OnPlayerUseWeedSeeds", PlayerId | ItemId},
	{ITEM_DRUGS,			"Medikamentai",								false, false, false, false, false,	0,		0,		0, "", NoParameters},
	{ITEM_HERAS,			"Heroinas",									true, false, true, false,  false,	0,		0,		1,	"OnPlayerUseHeroin", PlayerId | ItemId},
	{ITEM_MATS,				"Paketai",									false, false, true, false, false, 	0,		0,		1, 	"cmd_make", PlayerId},
	{ITEM_SVIRKSTAS, 		"�virk�tas",								false, false, false, false, false,	0,		0,	 	1, 	"", NoParameters},
	{ITEM_NOTE,				"U�ra�� knygut�",							false, false, false, false, false,	0,		0,		0, "cmd_note", NoParameters},
	{ITEM_HELMET,			"�almas",									false, false, false, false, false,	0,		0,		0,	"OnPlayerUseHelmet", PlayerId | ItemId},
	{ITEM_ROD,				"Me�ker�",									false, false, false, false, false,	18632,	0,		0,	"OnPlayerUseFishingRod", PlayerId | ItemId},
	{ITEM_RODTOOL,			"Pakelis masalo",							false, false, true, true,	 false, 	0,		0,		20,	"",	PlayerId | ItemId},
	{ITEM_FISH,				"Krep�ys �uvims",							false, false, false, true, false, 	0,		0,		0,	"", NoParameters},
	{ITEM_MEDLIC,			"Receptas",									false, false, false, false, false,	0,		0,		0, "", NoParameters},
	{ITEM_MEDIC,			"Receptiniai vaistai", 						false, false, false, false, false,	0,		0,	 	0, "", NoParameters},
	{ITEM_BEER,				"Butelis alaus",							false, false, false, false, false,	0,		0,		1, "OnPlayerUseBeer", PlayerId | ItemId },
	{ITEM_SPRUNK,			"Sprunk skardin�", 							false, false, false, false, false,	0,		0,		1, "OnPlayerUseSprunk", PlayerId | ItemId},
	{ITEM_VINE,				"Vynas",									false, false, false, false, false,	0,		0,	 	1, "OnPlayerUseVine", PlayerId | ItemId},
	{ITEM_PAPER,			"Laikra�tis",								false, false, false, false, false,	0,		0,		0, "", NoParameters},
	{ITEM_MOLOTOV,			"Degusis skystis",							false, false, false, false, false,	0,		0,		1,	"OnPlayerUseMolotov", PlayerId | ItemId},
	{ITEM_MP3,				"MP3 grotuvas",								false, false, false, false, false,	0,		0,	 	0,	"OnPlayerUseMP3Player", PlayerId | ItemId},
	{ITEM_MAGNETOLA,		"Automagnetola",							false, false, false, false, false,	0,		0,		0,	"", NoParameters},
	{ITEM_AUDIO,			"Nam� audio sistema",						false, false, false, false, false,	0,		0,	 	1, "OnPlayerUseHouseAudio", PlayerId | ItemId},
	{ITEM_BIGAUDIO,			"Grotuvas",									false, false, false, false, false,	0,		0,		0,	"OnPlayerUseAudioPlayer", PlayerId | ItemId},
	{ITEM_TEORIJA,			"Teorijos lapas",							false, false, false, false, false,	0,		0,		0,	"",	NoParameters},
	{ITEM_ROADBLOCK,		"Kelio u�tvara", 							false, false, false, false, false,	0,		0,	 	0, "", NoParameters},
	{ITEM_AMFA,				"Amfetaminas",								true, false, true, false, false, 	0,		0,		1, "OnPlayerUseAmphetamine", PlayerId | ItemId},
	{ITEM_METAMFA,			"Metamfetaminas",							true, false, true, false, false, 	0,		0,		1, "OnPlayerUseMetaAmphetamine", PlayerId | ItemId},
	{ITEM_COCAINE,			"Kokainas",									true, false, true, false, false, 	0,		0,		1, "OnPlayerUseCocaine", PlayerId | ItemId},
	{ITEM_Bandana2,			"Skarel� 2",								false, false, false, false, false,	18892,	0,		0,	"OnPlayerUseBandana", PlayerId | ItemId},
	{ITEM_Bandana4,			"Skarel� 4",								false, false, false, false, false,	18894,	0,		0,	"OnPlayerUseBandana", PlayerId | ItemId},
	{ITEM_Bandana5,			"Skarel� 5",								false, false, false, false, false,	18895,	0,		0,	"OnPlayerUseBandana", PlayerId | ItemId},
	{ITEM_Bandana6,			"Skarel� 6",								false, false, false, false, false,	18896,	0,		0,	"OnPlayerUseBandana", PlayerId | ItemId},
	{ITEM_Bandana7,			"Skarel� 7",								false, false, false, false, false,	18897,	0,		0,	"OnPlayerUseBandana", PlayerId | ItemId},
	{ITEM_Bandana8,			"Skarel� 8",								false, false, false, false, false,	18898,	0,		0,	"OnPlayerUseBandana", PlayerId | ItemId},
	{ITEM_Bandana9,			"Skarel� 9",								false, false, false, false, false,	18899,	0,		0,	"OnPlayerUseBandana", PlayerId | ItemId},
	{ITEM_Bandana10,		"Skarel� 10",								false, false, false, false, false,	18911,	0,		0,	"OnPlayerUseBandana", PlayerId | ItemId},
	{ITEM_Bandana11,		"Skarel� 11",								false, false, false, false, false,	18912,	0,		0,	"OnPlayerUseBandana", PlayerId | ItemId},
	{ITEM_Bandana12,		"Skarel� 12",								false, false, false, false, false,	18913,	0,		0,	"OnPlayerUseBandana", PlayerId | ItemId},
	{ITEM_Bandana13,		"Skarel� 13",								false, false, false, false, false,	18914,	0,		0,	"OnPlayerUseBandana", PlayerId | ItemId},
	{ITEM_Bandana14,		"Skarel� 14",								false, false, false, false, false,	18915,	0,		0,	"OnPlayerUseBandana", PlayerId | ItemId},
	{ITEM_Bandana15,		"Skarel� 15",								false, false, false, false, false,	18916,	0,		0,	"OnPlayerUseBandana", PlayerId | ItemId},
	{ITEM_Bandana16,		"Skarel� 16",								false, false, false, false, false,	18917,	0,		0,	"OnPlayerUseBandana", PlayerId | ItemId},
	{ITEM_Bandana17,		"Skarel� 17",								false, false, false, false, false,	18918,	0,		0,	"OnPlayerUseBandana", PlayerId | ItemId},
	{ITEM_Bandana18,		"Skarel� 18",								false, false, false, false, false,	18919,	0,		0,	"OnPlayerUseBandana", PlayerId | ItemId},
	{ITEM_Bandana19,		"Skarel� 19",								false, false, false, false, false,	18920,	0,		0,	"OnPlayerUseBandana", PlayerId | ItemId},
	{ITEM_EyePatch1,		"Akies rai�tis",							false, false, false, false, false,	19085,	0,		0,	"OnPlayerUseBandana",	PlayerId | ItemId},
	{ITEM_Beret2,			"Beret� 2",									false, false, false, false, false,	18921,	0,		0,	"OnPlayerUseBeret", PlayerId | ItemId},
	{ITEM_Beret3,			"Beret� 3",									false, false, false, false, false,	18922,	0,		0,	"OnPlayerUseBeret", PlayerId | ItemId},
	{ITEM_Beret4,			"Beret� 4",									false, false, false, false, false,	18923,	0,		0,	"OnPlayerUseBeret", PlayerId | ItemId},
	{ITEM_Beret1,			"Beret� 1",									false, false, false, false, false,	18924,	0,		0,	"OnPlayerUseBeret", PlayerId | ItemId},
	{ITEM_Beret5,			"Beret� 5",									false, false, false, false, false,	18925,	0,		0,	"OnPlayerUseBeret", PlayerId | ItemId},
	{ITEM_CapBack3,			"Kepur� 3",									false, false, false, false, false,	19200,	0,		0, "OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_CapBack4,			"Kepur� 4",									false, false, false, false, false,	18942,	0,		0, "OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_CapBack5,			"Kepur� 5",									false, false, false, false, false,	18943,	0,		0, "OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_CapBack7,			"Kepur� 7",									false, false, false, false, false,	18926,	0,		0, "OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_CapBack8,			"Kepur� 8",									false, false, false, false, false,	18927,	0,		0, "OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_CapBack9,			"Kepur� 9",									false, false, false, false, false,	18928,	0,		0, "OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_CapBack10,		"Kepur� 10",								false, false, false, false, false,	18929,	0,		0, "OnPlayerUseHat", PlayerId | ItemId},	
	{ITEM_CapBack11,		"Kepur� 11",								false, false, false, false, false,	18930,	0,		0, "OnPlayerUseHat", PlayerId | ItemId},	
	{ITEM_CapBack12,		"Kepur� 12",								false, false, false, false, false,	18931,	0,		0, "OnPlayerUseHat", PlayerId | ItemId},	
	{ITEM_CapBack13,		"Kepur� 13",								false, false, false, false, false,	18932,	0,		0, "OnPlayerUseHat", PlayerId | ItemId},	
	{ITEM_CapBack14,		"Kepur� 14",								false, false, false, false, false,	18933,	0,		0, "OnPlayerUseHat", PlayerId | ItemId},	
	{ITEM_CapBack15,		"Kepur� 15",								false, false, false, false, false,	18934,	0,		0, "OnPlayerUseHat", PlayerId | ItemId},	
	{ITEM_CapBack16,		"Kepur� 16",								false, false, false, false, false,	18935,	0,		0, "OnPlayerUseHat", PlayerId | ItemId},	
	{ITEM_CapBack17,		"Kepur� 17",								false, false, false, false, false,	19093,	0,		0, "OnPlayerUseHat", PlayerId | ItemId},	
	{ITEM_CapBack18,		"Kepur� 18",								false, false, false, false, false,	19160,	0,		0, "OnPlayerUseHat", PlayerId | ItemId},	
	{ITEM_CapBack19,		"Kepur� 19",								false, false, false, false, false,	18953,	0,		0, "OnPlayerUseHat", PlayerId | ItemId},	
	{ITEM_CapBack20,		"Kepur� 20",								false, false, false, false, false,	18954,	0,		0, "OnPlayerUseHat", PlayerId | ItemId},	
	{ITEM_CapBack21,		"Kepur� 21",								false, false, false, false, false,	18961,	0,		0, "OnPlayerUseHat", PlayerId | ItemId},	
	{ITEM_SkullyCap1,		"Skullycap 1",								false, false, false, false, false,	18964,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_SkullyCap2,		"Skullycap 2",								false, false, false, false, false,	18965,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_HatMan1,			"Hatman 1",									false, false, false, false, false,	18967,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_HatMan2,			"Hatman 2",									false, false, false, false, false,	18968,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_SantaHat1,		"Kal�d� kepur� 1",							false, false, false, false, false,	19064,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_SantaHat2,		"Kal�d� kepur� 2",							false, false, false, false, false,	19065,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_HoodyHat3,		"Hoodyhat 3",								false, false, false, false, false,	19069,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_SillyHelmet2,		"SillyHelmet 2",							false, false, false, false, false,	19114,	0,	 	0, "OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_SillyHelmet3,		"SillyHelmet 3",							false, false, false, false, false,	19115,	0,	 	0, "OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_PlainHelmet1,		"PlainHellmet 1",							false, false, false, false, false,	19116,	0,	 	0, "OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_tophat01,			"Cilindras",								false, false, false, false, false,	19352,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_pilotHat01,		"Piloto kepur�",							false, false, false, false, false,	19520,	0,	 	0,	"OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_CowboyHat1,		"Skrybel�  1",								false, false, false, false, false,	19095,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},		
	{ITEM_CowboyHat2,		"Skrybel�  2",								false, false, false, false, false,	18962,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},		
	{ITEM_CowboyHat3,		"Skrybel�  3",								false, false, false, false, false,	19096,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},		
	{ITEM_CowboyHat4,		"Skrybel�  4",								false, false, false, false, false,	19097,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},		
	{ITEM_CowboyHat5,		"Skrybel�  5",								false, false, false, false, false,	19098,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},		
	{ITEM_HatBowler1,		"Skrybel� B1",								false, false, false, false, false,	18944,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},		
	{ITEM_HatBowler2,		"Skrybel� B2",								false, false, false, false, false,	18945,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},		
	{ITEM_HatBowler3,		"Skrybel� B3",								false, false, false, false, false,	18947,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},		
	{ITEM_HatBowler6,		"Skrybel� B6",								false, false, false, false, false,	19488,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},		
	{ITEM_HAIR1,			"Perukas",									false, false, false, false, false,	19516,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_HAIR2,			"Perukas",									false, false, false, false, false,	19518,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_HAIR5,			"Perukas",									false, false, false, false, false,	19274,	0,		0,	"OnPlayerUseHat", PlayerId | ItemId},
	{ITEM_WatchType1,		"Lakrodis 1",								false, false, false, false, false,	19039,	0,		0, "OnPlayerUseWatch", PlayerId | ItemId},
	{ITEM_WatchType2,		"Lakrodis 2",								false, false, false, false, false,	19040,	0,		0, "OnPlayerUseWatch", PlayerId | ItemId},
	{ITEM_WatchType6,		"Lakrodis 6",								false, false, false, false, false,	19044,	0,		0, "OnPlayerUseWatch", PlayerId | ItemId},
	{ITEM_WatchType4,		"Lakrodis 4",								false, false, false, false, false,	19042,	0,		0, "OnPlayerUseWatch", PlayerId | ItemId},
	{ITEM_GlassesType1,		"Akiniai 1",								false, false, false, false, false,	19006,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType2,		"Akiniai 2",								false, false, false, false, false,	19007,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType3,		"Akiniai 3",								false, false, false, false, false,	19008,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType4,		"Akiniai 4",								false, false, false, false, false,	19009,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType7,		"Akiniai 7",								false, false, false, false, false,	19012,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType10 ,	"Akiniai 10",								false, false, false, false, false,	19015,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType13 ,	"Akiniai 13",								false, false, false, false, false,	19018,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType14 ,	"Akiniai 14",								false, false, false, false, false,	19019,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType15 ,	"Akiniai 15",								false, false, false, false, false,	19020,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType16 ,	"Akiniai 16",								false, false, false, false, false,	19021,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType17 ,	"Akiniai 17",								false, false, false, false, false,	19022,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType18 ,	"Akiniai 18",								false, false, false, false, false,	19023,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType19 ,	"Akiniai 19",								false, false, false, false, false,	19024,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType20 ,	"Akiniai 20",								false, false, false, false, false,	19025,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType21 ,	"Akiniai 21",								false, false, false, false, false,	19026,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType22 ,	"Akiniai 22",								false, false, false, false, false,	19027,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType23 ,	"Akiniai 23",								false, false, false, false, false,	19028,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType24 ,	"Akiniai 24",								false, false, false, false, false,	19029,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType25 ,	"Akiniai 25",								false, false, false, false, false,	19030,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType26 ,	"Akiniai 26",								false, false, false, false, false,	19031,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType27 ,	"Akiniai 27",								false, false, false, false, false,	19032,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_GlassesType28 ,	"Akiniai 28",								false, false, false, false, false,	19033,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_PoliceGlasses2,	"Policijos Akiniai 1",						false, false, false, false, false,	19139,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_PoliceGlasses3,	"Policijos Akiniai 2",						false, false, false, false, false,	19140,	0,		0,	"OnPlayerUseGlassees", PlayerId | ItemId},
	{ITEM_MotorcycleHelmet4,"�almas 4",									false, false, false, false, false,	18978,	0,		0, "OnPlayerUseHelmet",	PlayerId | ItemId},
	{ITEM_MotorcycleHelmet5,"�almas 5",									false, false, false, false, false,	18979,	0,		0, "OnPlayerUseHelmet",	PlayerId | ItemId},
	{ITEM_MotorcycleHelmet6,"�almas 6",									false, false, false, false, false,	18977,	0,		0, "OnPlayerUseHelmet",	PlayerId | ItemId},
	{ITEM_MotorcycleHelmet7,"�almas 7",									false, false, false, false, false,	0,		0,		0, "OnPlayerUseHelmet",	PlayerId | ItemId},
	{ITEM_MotorcycleHelmet8,"�almas 8",									false, false, false, false, false,	0,		0,		0, "OnPlayerUseHelmet",	PlayerId | ItemId},
	{ITEM_MotorcycleHelmet9,"�almas 9",									false, false, false, false, false,	18952,	0,		0, "OnPlayerUseHelmet",	PlayerId | ItemId},
	{ITEM_HockeyMask1,		"Ledo ritulio kauk�",						false, false, false, false, false,	19036,	0,	 	0, "OnPlayerUseMask",		PlayerId | ItemId},
	{ITEM_MaskZorro1,		"Zoro kauk�",								false, false, false, false, false,	18974,	0,		0,	"OnPlayerUseMask",		PlayerId | ItemId},
	{ITEM_KREPSYS,			"Krep�ys",									false, false, false, false, false,	2919,	0,	 	0,	"OnPlayerUseSuitcase",	PlayerId | ItemId},
	{ITEM_LAGAMINAS,		"Lagaminas",								false, false, false, false, false,	1210,	0,		0,	"OnPlayerUseSuitcase",	PlayerId | ItemId},
	{ITEM_AMFAMISC,			"Amfetamino sudedamosios dalys",			false, false, true, false, false, 	0,		0,		0,	"OnPlayerUseDrugIngredient", PlayerId},
	{ITEM_COCAINEMISC,		"Kokaino sudedamosios dalys",				false, false, true, false, false, 	0,		0,		0,	"OnPlayerUseDrugIngredient", PlayerId},
	{ITEM_METAAMFAMISC,		"Metamfetamino sudedamosios dalys",			false, false, true, false, false, 	0,		0,		0,	"OnPlayerUseDrugIngredient", PlayerId},
	{ITEM_MATCHES, 			"Degtuk� d��ut�",							false, false, true, true,	 true, 		0,		20,		20,	"", NoParameters},
	{ITEM_KUPRINE,			"Kuprin�",									false, false, false, false, false,	0,		0,		0, "OnPlayerUseBackpack",	PlayerId},
	{ITEM_EXTAZY,			"Ekstazi",									true, false, true, false, false, 	0,		0,		1, "OnPlayerUseEctazy", PlayerId},
	{ITEM_PCP,				"PCP",										true, false, true, false,  false,	0,		0,		1, "OnPlayerUsePCP", PlayerId},
	{ITEM_CRACK,			"Krekas",									true, false, true, false, false, 	0,		0,		1, "OnPlayerStartSmoking", PlayerId | ItemId},
	{ITEM_OPIUM,			"Opijus",									true, false, true, false, false, 	0,		0,		1, "OnPlayerStartSmoking",	PlayerId | ItemId},
		
	// "Guns guns guns" - Call of duty Modern Warfare 
	{1, 					"Kastetas",									false, true, false, false, false, 	331,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{2, 					"Golfo lazda",								false, true, false, false, false,	333,	0, 		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{3,						"Bananas",									false, true, false, false, false,	334,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{4,						"Peilis", 									false, true, false, false, false,	335,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId}, 
	{5, 					"Beisbolo lazda",							false, true, false, false, false,	336,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{6, 					"Kastuvas",									false, true, false, false, false,	337,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{7, 					"P�lo lazda",								false, true, false, false, false,	338,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{8,						"Katana",									false, true, false, false, false,	339,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{9, 					"Benzininis pj�klas",						false, true, false, false, false,	341,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{10, 					"Vibratorius",								false, true, false, false, false,	321,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{11,					"Vibratorius",								false, true, false, false, false,	322,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{12,					"Vibratorius",								false, true, false, false, false,	323,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{13,					"Vibratorius",								false, true, false, false, false,	324,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{14,					"G�li� puok�t�",							false, true, false, false, false,	325,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{15, 					"Lazda",									false, true, false, false, false,	326,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{16,					"Granata",									false, true, false, false, false,	342,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{17, 					"A�arin�s dujos",							false, true, false, false, false,	343,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{18,					"Moltov kokteilis",							false, true, false, false, false,	344,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{22,					"9mm pistoletas",							false, true, false, false, false,	346,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{23,					"9mm pistoletas(duslintuvas)",				false, true, false, false, false,	347,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{24,					"Desert Eagle",								false, true, false, false, false,	348,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{25, 					"Shotgun",									false, true, false, false, false,	349,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{26,					"Nupjautas Shotgun",						false, true, false, false, false,	350,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{27,					"Combat Shotgun",							false, true, false, false, false,	351,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{28,					"Micro SMG",								false, true, false, false, false,	352,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{29,					"MP5",										false, true, false, false, false,	353,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{30,					"AK-47",									false, true, false, false, false,	355,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{31,					"M4",										false, true, false, false, false,	356,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{32,					"Tec-9",									false, true, false, false, false,	372,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{33, 					"Rifle",									false, true, false, false, false,	357,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{34, 					"Sniper",									false, true, false, false, false,	358,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{35,					"Raketsvaidis",								false, true, false, false, false,	359,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{36,					"Raketsvaidis",								false, true, false, false, false,	360,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{37,					"Ugniasvaidis",								false, true, false, false, false,	361,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{38,					"Minigun",									false, true, false, false, false,	362,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{39,					"Daiktas",									false, true, false, false, false,	363,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{40,					"Detonatorius",								false, true, false, false, false,	364,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{41,					"Pur�kiami da�ai",							false, true, false, false, false,	365,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{42,					"Gesintuvas",								false, true, false, false, false,	366,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{43,					"Kamera",									false, true, false, false, false,	367,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{44,					"Naktiniio matymo akiniai",					false, true, false, false, false,	368,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{45,					"�iluminio matymo akiniai",					false, true, false, false, false,	369,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId},
	{46,					"Para�iutas",								false, true, false, false, false,	371,	0,		0, "OnPlayerUseWeapon", PlayerId | ItemId}
};






static stock GetItemIndex(itemid)
{
	for(new i = 0; i < sizeof ItemData; i++)	
		if(ItemData[ i ][ Id ] == itemid)
			return i;
	return -1;
}






stock SelectPlayerItem(playerid, itemid)
{
	new index = GetItemIndex(itemid);
	if(index == -1)
		return 0;

	if(isnull(ItemData[ index ][ UsageFunction ]))
		return 0;

	new funcReturned = -1;

	switch(ItemData[ index ][ UsageFunctionArgs ])
	{
		case NoParameters:
			funcReturned = CallLocalFunction(ItemData[ index ][ UsageFunction ], "");
		case PlayerId:
			funcReturned = CallLocalFunction(ItemData[ index ][ UsageFunction ], "i", playerid);
		case ItemId:
			funcReturned = CallLocalFunction(ItemData[ index ][ UsageFunction ], "i", itemid);
		case PlayerId | ItemId:
			funcReturned = CallLocalFunction(ItemData[ index ][ UsageFunction ], "ii", playerid, itemid);
	}


	return CallLocalFunction("OnPlayerUseItem", "iii", playerid, itemid, funcReturned);
}

forward OnPlayerUseItem(playerid, itemid, success);


stock IsItemDegradable(itemid)
{
	new index = GetItemIndex(itemid);
	if(index == -1)
		return 0;
	else 
		return ItemData[ index ][ IsDegradable ];
}

stock IsItemWeapon(itemid)
{
	new index = GetItemIndex(itemid);
	if(index == -1)
		return 0;
	else 
		return ItemData[ index ][ IsWeapon ];
}

stock IsItemDrug(itemid)
{
	new index = GetItemIndex(itemid);
	if(index == -1)
		return 0;
	else 
		return ItemData[ index ][ IsDrug ];
}

stock GetItemObjectModel(itemid)
{
	new index = GetItemIndex(itemid);
	if(index == -1)
		return 0;
	else 
		return ItemData[ index ][ ObjectModel ];
}

stock GetItemName(itemid)
{

	new index = GetItemIndex(itemid),
		s[ MAX_ITEM_NAME ];
	if(index == -1)
		return s;
	else 
	{
		strcat(s, ItemData[ index ][ Name ]);
		return s;
	}
}

stock GetItemMaxCapacity(itemid)
{
	new index = GetItemIndex(itemid);
	if(index == -1)
		return 0;
	else 
		return ItemData[ index ][ MaxCapacity ];
}

stock IsItemStackable(itemid)
{
	new index = GetItemIndex(itemid);
	if(index == -1)
		return 0;
	else 
		return ItemData[ index ][ IsStackable ];
}


stock IsValidItem(itemid)
{
	new index = GetItemIndex(itemid);
	if(index == -1)
		return false;
	else 
		return true;
}

stock GetItemId(itemname[])
{
	for(new i = 0; i < sizeof(ItemData); i++)
		if(!strcmp(ItemData[ i ][ Name ], itemname))
			return ItemData[ i ][ Id ];
	return INVALID_ITEM_ID;
}
