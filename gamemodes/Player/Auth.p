/*
	* Auth.p failas yra dalis LTRP modifikacijos.
	* Failas atsakingas uþ þaidëjo autentikavimà(áskaitant ir slaptà klausimà/atsakymà), spawn paruoðimà
	*
	*
	* Kodo autorius: Bebras.
	*
	*
*/


/*

		CREATE TABLE IF NOT EXISTS player_crashes
		(
			player_id INT(11) NOT NULL,
			timestamp DATETIME NOT NULL,
			pos_x FLOAT NOT NULL,
			pos_y FLOAT NOT NULL,
			pos_z FLOAT NOT NULL,
			PRIMARY KEY(player_id)
		);
		ALTER TABLE player_crashes ADD FOREIGN KEY(player_id) REFFERENCES players(id) ON DELETE CASCADE;

*/


#include <YSI\y_hooks>

new PlayerLoggedIn[MAX_PLAYER char];


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

hook OnPlayerConnect(playerid)
{
	ShowAuthDialog(playerid);
	return 1;
}

hook OnPlayerDisconnect(playerid, reason)
{
	PlayerLoggedIn{ playerid } = 0;
	return 1;
}




/*


						                               ,,
						 .M"""bgd mm            mm     db
						,MI    "Y MM            MM
						`MMb.   mmMMmm  ,6"Yb.mmMMmm `7MM  ,p6"bo
						  `YMMNq. MM   8)   MM  MM     MM 6M'  OO
						.     `MM MM    ,pm9MM  MM     MM 8M
						Mb     dM MM   8M   MM  MM     MM YM.    ,
						P"Ybmmd"  `Mbmo`Moo9^Yo.`Mbmo.JMML.YMbmd'


                                                      ,,
     `7MM"""YMM                                mm     db
       MM    `7                                MM
       MM   d `7MM  `7MM  `7MMpMMMb.  ,p6"bo mmMMmm `7MM  ,pW"Wq.`7MMpMMMb.  ,pP"Ybd
       MM""MM   MM    MM    MM    MM 6M'  OO   MM     MM 6W'   `Wb MM    MM  8I   `"
       MM   Y   MM    MM    MM    MM 8M        MM     MM 8M     M8 MM    MM  `YMMMa.
       MM       MM    MM    MM    MM YM.    ,  MM     MM YA.   ,A9 MM    MM  L.   I8
     .JMML.     `Mbod"YML..JMML  JMML.YMbmd'   `Mbmo.JMML.`Ybmd9'.JMML  JMML.M9mmmP'




*/

static ShowAuthDialog(playerid)
{

}













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

                format( string, sizeof( string ), "SELECT `Priezastis` FROM `nuobaudos` WHERE `Ka` = 'uþdarë á kalëjimá ' AND `Kam` = %d ORDER BY `Data` DESC LIMIT 1", pInfo[ playerid ][ pMySQLID ]);
                new Cache:result = mysql_query(DbHandle,  string );

                if(cache_get_row_count())
                {
                    cache_get_field_content(0, "Priezastis", string);
                    format( string2, sizeof( string2 ), "Prieþastis: %s", string);
                    SendClientMessage       ( playerid, COLOR_LIGHTRED, "Jûs buvote pasodintas á OOC Jail!" );
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

                        format( string, sizeof( string ), "SELECT `Priezastis` FROM `nuobaudos` WHERE `Ka` = 'uþdarë á kalëjimá ' AND `Kam` = %d ORDER BY `Data` DESC LIMIT 1", pInfo[ playerid ][ pMySQLID ]);
                        new Cache:result = mysql_query(DbHandle,  string );

                        if(cache_get_row_count())
                        {
                            cache_get_field_content(0, "Priezastis", string);
                            format( string2, sizeof( string2 ), "Prieþastis: %s", string);
                            SendClientMessage       ( playerid, COLOR_LIGHTRED, "Jûs buvote pasodintas á OOC Jail!" );
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
                SendClientMessage(playerid, COLOR_LIGHTRED, "Jûs buvote paleistas ið ligoninës.");
                SendClientMessage(playerid, COLOR_LIGHTRED, "Gydymas kainavo 150$. Ginklai bei kiti daiktai buvo pamesti, iðskyrus telefonà.");
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
            SendClientMessage       ( playerid, COLOR_LIGHTRED, "Dëmesio, Jûs buvote mirtinai suþeistas ir dabar Jums reikia skubios pagalbos." );
            SendClientMessage       ( playerid, COLOR_LIGHTRED, "Apaèioje eina laikas iki mirties, jei norite mirti nelaukæ raðykite /die." );
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

                format( string, sizeof( string ), "SELECT `Priezastis` FROM `nuobaudos` WHERE `Ka` = 'uþdarë á kalëjimá ' AND `Kam` = %d ORDER BY `Data` DESC LIMIT 1", pInfo[ playerid ][ pMySQLID ]);
                new Cache:result = mysql_query(DbHandle,  string );
                if(cache_get_row_count())
                {
                    cache_get_field_content(0, "Priezastis", string);
                    format( string2, sizeof( string2 ), "Prieþastis: %s", string);
                    SendClientMessage       ( playerid, COLOR_LIGHTRED, "Jûs buvote pasodintas á OOC Jail!" );
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

        }
    }
    return SpawnPlayer( playerid );
}













forward OnPlayerSpawnSetUp(playerid);