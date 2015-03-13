#define MAP_ANDREAS_MODE_NONE			0
#define MAP_ANDREAS_MODE_MINIMAL		1 // for future use
#define MAP_ANDREAS_MODE_FULL			2

native MapAndreas_Init(mode);
native MapAndreas_FindZ_For2DCoord(Float:X, Float:Y, &Float:Z);


/* 	Ðis kodas pridëtas specialiai LTRP naudojimui by Bebras. 
	Taèiau gali bûti naudojamas ir kitur.
	Jis prideda auto- MapAndreas_Init ir patikrinima ar egzistuoja duomenu failas.

	
 */
public OnGameModeInit()
{
	MapAndreas_Init(MAP_ANDREAS_MODE_FULL);
	if(!fexist("SAfull.hmap"))
		print("\r\n\r\nKlaida. Nerastas 'SAfull.hmap' failas 'scriptfiles' folderyje.");
	#if defined MapAndreas_OnGameModeInit
		MapAndreas_OnGameModeInit();
	#endif
}
#if defined _ALS_OnGameModeInit
	#undef OnGameModeInit
#else 
	#define _ALS_OnGameModeInit
#endif
#define OnGameModeInit MapAndreas_OnGameModeInit
#if defined MapAndreas_OnGameModeInit
	forward MapAndreas_OnGameModeInit();
#endif