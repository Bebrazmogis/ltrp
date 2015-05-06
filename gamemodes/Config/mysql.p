
new DbHandle;


#define VPS_MODE
#define BEBRAS_HOME_MODE

#if defined VPS_MODE
    #define MYSQL_HOST "localhost"
    #define MYSQL_USER "root"
    #define MYSQL_PASS "tQsWA_Sz^c*wAx8Ung@3"
    #define MYSQL_DB   "c1t1_l0ss4nt0s"
#else
    #define MYSQL_HOST "localhost"
    #define MYSQL_USER "root"
    #define MYSQL_PASS "tQsWA_Sz^c*wAx8Ung@3"
    #define MYSQL_DB   "c1t1_l0ss4nt0s"
#endif

#if defined BEBRAS_HOME_MODE
    #undef MYSQL_PASS
    #undef MYSQL_DB
    #define MYSQL_PASS ""
    #define MYSQL_DB   "ltrp-property-modules"
#endif


public OnGameModeInit()
{
	//=============================[ Prisijungimas prie MySQL duomenø bazës ]================================
    mysql_log(LOG_ALL, LOG_TYPE_HTML);
    DbHandle = mysql_connect(MYSQL_HOST, MYSQL_USER, MYSQL_DB, MYSQL_PASS, .pool_size = 4);
    mysql_set_charset("cp1257");
    mysql_option(LOG_TRUNCATE_DATA, false);

    if(DbHandle) 
    	print("Severis sëkmingai prisijungë prie MySQL duomenø bazës.");
    else
    {
    	printf("MySQL prisijungti nepavyko. Klaidos kodas: %d", mysql_errno());
    	ErrorLog("MySQL prisijungti nepavyko. Klaidos kodas: %d", mysql_errno());
    	SendRconCommand("exit");
    }

    #if defined mysql_OnGameModeInit
    	mysql_OnGameModeInit();
    #endif
}
#if defined _ALS_OnGameModeInit
	#undef OnGameModeInit
#else 
	#define _ALS_OnGameModeInit
#endif
#define OnGameModeInit 				mysql_OnGameModeInit
#if defined mysql_OnGameModeInit
	forward mysql_OnGameModeInit();
#endif