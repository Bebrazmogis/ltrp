#include <YSI\y_hooks>


#define MAX_PLAYER_VEHICLES 			10


enum E_PLAYER_VEHICLE_ITEM_DATA
{
	Id,
	ItemId,
	Amount,
	ContentAmount,
	Durability,
};


enum E_PLAYER_VEHICLE_DATA
{
	SqlId,
    Model,
    Float:Spawn[4],
    Color[2],
    bool:Locked,
    Fuel,
    LicensePlate[24],
    Faction,
    Duzimai,
    LockLevel,
    AlarmLevel,
    Insurance,
    VehicleId,
    Float:Mileage,
    VirtWorld,
    Doors,
    Lights,
    Tires,
    Panels,
   	Float:Health,
    objectai[ MAX_TRUCKER_CARGO_OBJECTS ]
};
static PlayerVehicles[ MAX_PLAYERS ][ MAX_PLAYER_VEHICLES ][ E_PLAYER_VEHICLE_DATA ],
	PlayerVehicleItems[ MAX_PLAYERS ][ MAX_PLAYER_VEHICLES ][ E_PLAYER_VEHICLE_ITEM_DATA ],
	bool:IsPlayerVehicleDataLoaded[ MAX_PLAYERS ];







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

LoadPlayerVehicles(playerid)
{
	new query[ ], Cache:result, lastvehsqlid, lastitemsqlid, vehsqlid, playerVehicleCount = -1;
	mysql_format(DbHandle, query, sizeof(query), "SELECT *, vehicle_items.id AS vehicle_item_id, vehicle_items.item_id, vehicle_items.amount, vehicle_items.content_amount, vehicle_items.durability \
		FROM player_vehicles WHERE owner = %d LEFT JOIN vehicle_items ON player_vehicles.id = vehicle_items.vehicle_id", GetPlayerSqlId(playerid));

	result = mysql_query(DbHandle, query);
	for(new i = 0; i < cache_get_row_count(); i++)
	{
		vehsqlid = cache_get_field_content_int(i, "id");

		// Perëjom prie kitos transporto priemonës krovimo.
		if(!vehsqlid || vehsqlid != lastvehsqlid)
		{
			playerVehicleCount++;
			lastvehsqlid = vehsqlid;

			PlayerVehicles[ playerid ][playerVehicleCount ][ SqlId ] = vehsqlid;
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ Model ] = cache_get_field_content_int(i, "model");
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ Spawn ][ 0 ] = cache_get_field_content_float(i, "x");
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ Spawn ][ 1 ] = cache_get_field_content_float(i, "y");
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ Spawn ][ 2 ] = cache_get_field_content_float(i, "z");
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ Spawn ][ 3 ] = cache_get_field_content_float(i, "angle");
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ Color ][ 0 ] = cache_get_field_content_int(i, "color1");
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ Color ][ 0 ] = cache_get_field_content_int(i, "color2");
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ Locked ] = cache_get_field_content_int(i, "locked");
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ LockLevel ] = cache_get_field_content_int(i, "lock_level");
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ Fuel ] = cache_get_field_content_int(i, "fuel");
			cache_get_field_content(i, "license_plate", PlayerVehicles[ playerid ][ playerVehicleCount ][ LicensePlate ], DbHandle, MAX_VEHICLE_LICENSE_PLATE);
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ Insurance ] = cache_get_field_content_int(i, "insurance");
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ Duzimai ] = cache_get_field_content_int(i, "times_crashed");
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ AlarmLevel ] = cache_get_field_content_int(i, "alarm_level");
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ Panels ] = cache_get_field_content_int(i, "panels");
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ Doors ] = cache_get_field_content_int(i, "doors");
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ Lights ] = cache_get_field_content_int(i, "lights");
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ Tires ] = cache_get_field_content_int(i, "tires");
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ Health ] = cache_get_field_content_float(i, "health");
			PlayerVehicles[ palyerid ][ playerVehicleCount ][ VehicleId ] = INVALID_VEHICLE_ID;
		}
	}
}



/*


		`7MMF'   `7MF'`7MM"""Mq.`7MM"""Yb.      db   MMP""MM""YMM `7MM"""YMM       .M"""bgd   .g8""8q. `7MMF'
		  MM       M    MM   `MM. MM    `Yb.   ;MM:  P'   MM   `7   MM    `7      ,MI    "Y .dP'    `YM. MM
		  MM       M    MM   ,M9  MM     `Mb  ,V^MM.      MM        MM   d        `MMb.     dM'      `MM MM
		  MM       M    MMmmdM9   MM      MM ,M  `MM      MM        MMmmMM          `YMMNq. MM        MM MM
		  MM       M    MM        MM     ,MP AbmmmqMA     MM        MM   Y  ,     .     `MM MM.      ,MP MM      ,
		  YM.     ,M    MM        MM    ,dP'A'     VML    MM        MM     ,M     Mb     dM `Mb.    ,dP' MM     ,M
		   `bmmmmd"'  .JMML.    .JMMmmmdP'.AMA.   .AMMA..JMML.    .JMMmmmmMMM     P"Ybmmd"    `"bmmd"' .JMMmmmmMMM
		                                                                                          MMb
		                                                                                           `bood'


			      ,...                                      ,,
			    .d' ""                               mm     db
			    dM`                                  MM
			   mMMmm`7MM  `7MM  `7MMpMMMb.  ,p6"bo mmMMmm `7MM  ,pW"Wq.`7MMpMMMb.  ,pP"Ybd
			    MM    MM    MM    MM    MM 6M'  OO   MM     MM 6W'   `Wb MM    MM  8I   `"
			    MM    MM    MM    MM    MM 8M        MM     MM 8M     M8 MM    MM  `YMMMa.
			    MM    MM    MM    MM    MM YM.    ,  MM     MM YA.   ,A9 MM    MM  L.   I8
			  .JMML.  `Mbod"YML..JMML  JMML.YMbmd'   `Mbmo.JMML.`Ybmd9'.JMML  JMML.M9mmmP'


*/





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

CMD:v(playerid, params[])
{
    new idx, select[128], string[ 2048 ], giveplayerid, Float:Kords[ 3 ];
    select = strtok(params, idx);

    if(!IsPlayerVehicleDataLoaded[ playerid ])
    	LoadPlayerVehicles(playerid);

    if(!strlen(select))
    {
        SendClientMessage(playerid, COLOR_GREEN, "|______________________Tr. Priemoniu komandos ir naudojimas__________________________|");
        SendClientMessage(playerid,COLOR_LIGHTRED2, "  KOMANDOS NAUDOJIMAS: /v [KOMANDA], pavyzdþiui: /v list");
        SendClientMessage(playerid,COLOR_WHITE,"  PAGRINDINËS KOMANDOS: list, get, park, buypark, lock, find, documents ");
        SendClientMessage(playerid,COLOR_FADE1,"  TR. PRIEMONËS SKOLINIMAS: dubkey takedubkey removedubs getdub ");
        SendClientMessage(playerid,COLOR_WHITE,"  TOBULINIMAS/TVARKYMAS: register buy alarm buylock buyinsurance buymod");
        SendClientMessage(playerid,COLOR_FADE1,"  VALDYMAS: /trunk /trunko /bonnet /windows /setbelt /maxspeed /vradio ");
        SendClientMessage(playerid,COLOR_WHITE,"  KITA: destroy scrap payticket faction buy ");
        SendClientMessage(playerid, COLOR_GREEN, "|__________________________________________________________________________________|");
        return 1;
    }
    if(!strcmp("destroy",select,true))
    {
        if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER) return SendClientMessage(playerid,COLOR_LIGHTRED,"Dëmesio, norëdami sunaikinti tr. priemonæ privalote sedëti joje.");
        new idcar = GetPlayerVehicleID( playerid );
        if(cInfo[idcar][cOwner] != pInfo[playerid][pMySQLID]) return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, ðis automobilis nepriklauso Jums, tad negalite atlikti ðio veiksmo");
        new Query[140];

        foreach(Player,i)
        {
            if ( pInfo[ i ][ pDubKey ] == cInfo[ idcar ][ cID ] )
                pInfo[ i ][ pDubKey ] = 0;
        }
        format( Query, 126, "UPDATE players SET pDubKey = 0 WHERE pDubKey = %d", cInfo[ idcar ][ cID ] );
        mysql_query(DbHandle,  Query, false);

        cInfo[idcar][cVehID] = 0;
        DestroyVehicle(idcar);
        if ( pInfo[ playerid ][ pCarGet ] > 0 )
            pInfo[ playerid ][ pCarGet ] --;

        format(Query,sizeof(Query),"DELETE FROM `vehicles` WHERE `ID` = '%i'", cInfo[ idcar ][ cID ]);
        mysql_query(DbHandle,  Query, false);
        nullVehicle( idcar );
        LoadPlayerVehicles( playerid );

        PayLog( pInfo[ playerid ][ pMySQLID ],16, -2, GetVehicleModel( idcar ) );
        SendClientMessage( playerid, COLOR_LIGHTRED, "Dëmesio, Jûsø pasirinkta tr. priemonë buvo sunaikinta negràþinamai." );
        return 1;
    }
    if(!strcmp("scrap",select,true))
    {
        new
            idcar = INVALID_VEHICLE_ID;

        if(GetPlayerState(playerid) == PLAYER_STATE_DRIVER)
            idcar = GetPlayerVehicleID( playerid );
        else
            idcar = GetNearestVehicle( playerid, 5.0 );
        if ( idcar == INVALID_VEHICLE_ID ) return SendClientMessage( playerid, COLOR_LIGHTRED,"Dëmesio, norëdami sunaikinti tr. priemonæ privalote sedëti joje.");
        if(cInfo[idcar][cOwner] != pInfo[playerid][pMySQLID]) return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, ðis automobilis nepriklauso Jums, tad negalite atlikti ðio veiksmo");
        new Query[256],
            Kaina1 = GetVehiclePrice( GetVehicleModel( idcar ) ),
            Float:Kaina2 = Kaina1 / 2,
            Float:Kaina3 = 0;

        if(cInfo[idcar][cDuzimai] < 1)
            Kaina3 = Kaina2;
        else
            Kaina3 = Kaina2 / cInfo[idcar][cDuzimai];

        if( Kaina3 < 1 ) return 1;

        GivePlayerMoney(playerid, floatround( Kaina3 ));
        foreach(Player,i)
        {
            if ( pInfo[ i ][ pDubKey ] == cInfo[ idcar ][ cID ] )
                pInfo[ i ][ pDubKey ] = 0;
        }
        format( Query, 126, "UPDATE players SET pDubKey = 0 WHERE pDubKey = %d", cInfo[ idcar ][ cID ] );
        mysql_query(DbHandle,  Query, false);

        cInfo[idcar][cVehID] = 0;
        DestroyVehicle(idcar);
        if ( pInfo[ playerid ][ pCarGet ] > 0 )
            pInfo[ playerid ][ pCarGet ] --;

        format(Query,sizeof(Query),"DELETE FROM `vehicles` WHERE `ID` = '%i'", cInfo[ idcar ][ cID ]);
        mysql_query(DbHandle,  Query, false);
        nullVehicle( idcar );
        LoadPlayerVehicles( playerid );

        PayLog( pInfo[ playerid ][ pMySQLID ],16, -2, GetVehicleModel( idcar ) );
        SendClientMessage( playerid, COLOR_LIGHTRED, "Dëmesio, Jûsø pasirinkta tr. priemonë buvo sunaikinta negràþinamai." );
        return 1;
    }
    if(!strcmp("removedubs",select,true))
    {
        if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER) return SendClientMessage(playerid,COLOR_LIGHTRED,"Dëmesio, norëdami atlikti ðá veiksmà privalote sedëti tr. priemonëje.");
        new idcar = GetPlayerVehicleID( playerid );
        if(cInfo[idcar][cOwner] != pInfo[playerid][pMySQLID]) return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, ðis automobilis nepriklauso Jums, tad negalite atlikti ðio veiksmo");
        new affected;
        foreach(Player,i)
        {
            if ( pInfo[ i ][ pDubKey ] == cInfo[ idcar ][ cID ] )
            {
                affected ++;
                pInfo[ i ][ pDubKey ] = 0;
            }
        }
        format( string, 126, "UPDATE players SET pDubKey = 0 WHERE pDubKey = %d", cInfo[ idcar ][ cID ] );
        mysql_query(DbHandle,  string, false);
        format( string, 126, "Dublikuotu raktu paðalinimas pavyko. Ið viso buvo panaikinti %d dublikuoti raktai.", cache_affected_rows( ) + affected );
        SendClientMessage( playerid, COLOR_LIGHTRED2, string );
        return 1;
    }
    if(!strcmp("takedubkey",select,true))
    {
        if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER) return SendClientMessage(playerid,COLOR_LIGHTRED,"Dëmesio, norëdami atlikti ðá veiksmà privalote sedëti tr. priemonëje.");
        new idcar = GetPlayerVehicleID( playerid );
        if(cInfo[idcar][cOwner] != pInfo[playerid][pMySQLID]) return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, ðis automobilis nepriklauso Jums, tad negalite atlikti ðio veiksmo");
        select = strtok( params, idx );
        if( !strlen( select ) ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /v takedubkey [VEIKËJO ID]");
        giveplayerid  = strval( select );
        if( !IsPlayerConnected( giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");
        if( !PlayerToPlayer( 10, playerid, giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti ðio veiksmo su veikëju, kuris nëra ðalia Jûsø.");
        if ( pInfo[ giveplayerid ][ pDubKey ] == cInfo[ idcar ][ cID ] )
        {
            pInfo[ giveplayerid ][ pDubKey ] = 0;
            format( string, 80, "Dëmesio, tr. priemonës savininkas %s pasiimë ið Jûsø savo tr priemonës raktelius.", GetName(playerid) );
            SendClientMessage( giveplayerid, COLOR_NEWS, string );
            format( string, 80, "Sveikiname, Jûs sëkmingai pasiimëte savo tr. priemonës raktelius ið veikëjo %s ", GetName( giveplayerid ) );
            SendClientMessage( playerid, COLOR_NEWS, string );
            return 1;
        }
        return 1;
    }
    else if(!strcmp("dubkey",select,true))
    {
        if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER) return SendClientMessage(playerid,COLOR_LIGHTRED,"Dëmesio, norëdami atlikti ðá veiksmà privalote sedëti tr. priemonëje.");
        new idcar = GetPlayerVehicleID( playerid );
        if(cInfo[idcar][cOwner] != pInfo[playerid][pMySQLID]) return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, ðis automobilis nepriklauso Jums, tad negalite atlikti ðio veiksmo");
        select = strtok( params, idx );
        if( !strlen( select ) ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /v dubkey [VEIKËJO ID]");
        giveplayerid  = strval( select );
        if( !IsPlayerConnected( giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");
        if( !PlayerToPlayer( 10, playerid, giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti ðio veiksmo su veikëju, kuris nëra ðalia Jûsø.");
        pInfo[ giveplayerid ][ pDubKey ] = cInfo[ idcar ][ cID ];
        format( string, 80, "Dëmesio, tr. priemonës savininkas %s suteikë savo tr. priemonës raktelius, nuo ðiol galësite naudotis ðia tr. priemone", GetName(playerid) );
        SendClientMessage( giveplayerid, COLOR_NEWS, string );
        format( string, 80, "Sveikiname, Jûs sëkmingai suteikëte galimybæ naudotis savo tr. priemonæ veikëjui %s ", GetName( giveplayerid ) );
        SendClientMessage( playerid, COLOR_NEWS, string );
        return 1;
    }
    else if(!strcmp("list",select,true))
    {

        format     ( string, 180, "SELECT cName,cDuzimai,cFuel,cNumbers,cAlarm,cLockType,cInsurance,cVehID FROM `vehicles` WHERE `cOwner` = %d", pInfo[playerid][pMySQLID] );
        new Cache:result = mysql_query(DbHandle,  string );
        new slot = 1;
        SendClientMessage( playerid, COLOR_GREEN, "|______________________JUMS PRIKLAUSANTIS TRANSPORTAS_____________________|" );
        if ( cache_get_row_count( ) )
        {
            for(new i = 0; i < cache_get_row_count(); i++)
            {
                new vName[ 24 ],
                    Duzimai,
                    Fuel,
                    Numbers[ 24 ],
                    Alarm,
                    LockType,
                    Insurance,
                    spawned;

                cache_get_field_content(i, "cName", vName);
                Duzimai = cache_get_field_content_int(i, "cDuzimai");
                Fuel = cache_get_field_content_int(i, "cFuel");
                cache_get_field_content(i, "cNumbers", Numbers);
                Alarm = cache_get_field_content_int(i, "cAlarm");
                LockType = cache_get_field_content_int(i, "cLockType");
                Insurance = cache_get_field_content_int(i, "cInsurance");
                spawned = cache_get_field_content_int(i, "cVehID");



                if ( spawned == 0 ) string = "Ne"; else string = "Taip";
                format( string, 256, "%d. Modelis[%s] Paþeidimai[%d] Degalø bake[%dl.] Numeriai[%s] Signalizacija[lvl:%d] Uþraktas[lvl:%d] Draudimas[%d] Iðkviesta[%s]",
                            slot,
                            vName,
                            Duzimai,
                            Fuel,
                            Numbers,
                            Alarm,
                            LockType,
                            Insurance,
                            string);

                SendClientMessage( playerid, COLOR_WHITE, string );
                slot ++;
            }
        }
        cache_delete(result);

        format     ( string, 140, "SELECT cName,cDuzimai,cFuel,cNumbers,cAlarm,cLockType,cInsurance,cVehID FROM `vehicles` WHERE `id` = %d LIMIT 1;", pInfo[playerid][pDubKey] );
        result = mysql_query(DbHandle,  string );
        SendClientMessage( playerid, COLOR_GREEN, "|______________________GALIMAS KT. TRANSPORTAS_____________________|" );
        if ( cache_get_row_count( ) )
        {
            new vName[ 24 ],
                Duzimai,
                Fuel,
                Numbers[ 24 ],
                Alarm,
                LockType,
                Insurance,
                spawned;

            cache_get_field_content(0, "cName", vName);
            Duzimai = cache_get_field_content_int(0, "cDuzimai");
            Fuel = cache_get_field_content_int(0, "cFuel");
            cache_get_field_content(0, "cNumbers", Numbers);
            Alarm = cache_get_field_content_int(0, "cAlarm");
            LockType = cache_get_field_content_int(0, "cLockType");
            Insurance = cache_get_field_content_int(0, "cInsurance");
            spawned = cache_get_field_content_int(0, "cVehID");

            if ( spawned == 0 ) string = "Ne"; else string = "Taip";

            format( string, 256, "Modelis[%s] Paþeidimai[%d] Degalai[%dl.] Numeriai[%s] Signalizacija[lvl:%d] Uþraktas[lvl:%d] Draudimas[%d] Iðkviesta[%s]",
                vName,
                Duzimai,
                Fuel,
                Numbers,
                Alarm,
                LockType,
                Insurance,
                string);
            SendClientMessage( playerid, COLOR_WHITE, string );
        }
        cache_delete(result);
        return 1;
    }
    else if(!strcmp("documents",select,true))
    {
        if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER) return SendClientMessage(playerid,COLOR_LIGHTRED,"Dëmesio, norëdami atlikti ðá veiksmà privalote sedëti tr. priemonëje.");
        new idcar = GetPlayerVehicleID( playerid );
        if( cInfo[idcar][cOwner] != pInfo[playerid][pMySQLID] && pInfo[ playerid ][ pDubKey ] != cInfo[ idcar ][ cID ] ) return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, ði tr. priemonë nepriklauso Jums.");
        select = strtok( params, idx );
        if( !strlen( select ) ) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /v documents [þaidëjo id]");
        giveplayerid  = strval( select );
        if( !IsPlayerConnected( giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");
        if( !PlayerToPlayer( 10, playerid, giveplayerid ) ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite naudoti ðio veiksmo su veikëju, kuris nëra ðalia Jûsø.");
        format           ( string, 126, "Parodei jam(-ai) %s savo automobilio dokumentus. ", GetName(giveplayerid) );
        SendClientMessage( playerid, COLOR_WHITE, string );

        SendClientMessage( giveplayerid, COLOR_GREEN,"|___________________Tr. priemonës dokumentai______________________|");
        format           ( string, 126, "| Tr. priemonës savininkas: %s | Tr. priemonës modelis: %s",GetVehicleOwnerName( cInfo[ idcar][ cOwner ] ), cInfo[ idcar][ cName ]);
        SendClientMessage( giveplayerid, COLOR_WHITE, string );
        format           ( string, 126, "| Uþrakto lygis: %d | Signalicazijos lygis: %d",cInfo[ idcar ][ cLockType ],cInfo[ idcar ][ cAlarm ]);
        SendClientMessage( giveplayerid, COLOR_WHITE, string );
        format           ( string, 126, "| Draudimas: %d ", cInfo[ idcar ][ cInsurance ]);
        SendClientMessage( giveplayerid, COLOR_WHITE, string );
        format           ( string, 126, "| Numeriai: %s ",  cInfo[ idcar ][ cNumbers ]);
        SendClientMessage( giveplayerid, COLOR_WHITE, string );
        format           ( string, 126, "| Paþeidimai: %d ",    cInfo[ idcar ][ cDuzimai ]);
        SendClientMessage( giveplayerid, COLOR_WHITE, string );
        format           ( string, 126, "| Visa rida: %.0f ",    cInfo[ idcar ][ cKM ]);
        SendClientMessage( giveplayerid, COLOR_WHITE, string );
        return 1;
    }
    else if(!strcmp("get",select,true))
    {
        select = strtok( params, idx );
        new slot = strval( select );
        if(!slot ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /v get [/v list SÀRAÐO NUMERIS]" );
        if(slot < 0 || slot > 20 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Sàraðo numeris negali bût didesnis uþ 20 ar maþesnis uþ vienetà." );

        if((pInfo[ playerid ][ pDonator ] < 1 && pInfo[ playerid ][ pCarGet ] > 1 ) ||
            (pInfo[ playerid ][ pDonator ] == 2 && pInfo[ playerid ][ pCarGet ] > 2) ||
            (pInfo[ playerid ][ pDonator ] == 3 && pInfo[ playerid ][ pCarGet ] > 3))
            return SendClientMessage( playerid, GRAD, "Tu jau esi iðsispawninàs per daug automobiliø" );

        if(pInfo[ playerid ][ pCar ][ slot ] == 0) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, nurodytas tr. priemonës sàraðo numeris neegzistuoja." );
        if(checkArrestedCar( playerid, pInfo[ playerid ][ pCar ][ slot ] ) ) return 1;

        format (string, 80, "SELECT * FROM `vehicles` WHERE `id` = %d LIMIT 1", pInfo[ playerid ][ pCar ][ slot ] );
        new Cache:result = mysql_query(DbHandle,  string );
        // Neturëtø NIEKADA To bût... bet logas sako kad yra buvæ :/ we must know WHY.
        if(!cache_get_row_count())
        {
            SendClientMessage(playerid, COLOR_LIGHTRED,"Ávyko sistemos klaida, susisiekite su Administracija.");
            new str[128];
            format(str,sizeof(str),"ERROR.Klaida. Buvo imama transporto priemonë su MySQL ID %d, taèiau jos nëra. ", pInfo[ playerid ][ pCar ][ slot ]);
            ImpossibleLog(str);
            cache_delete(result);
            return 1;
        }

        new ID,
            vName[ 24 ],
            Owner,
            model,
            Float:Spawn_x,
            Float:Spawn_y,
            Float:Spawn_z,
            Float:Spawn_a,
            color1,
            color2,
            Lock,
            Fuel,
            Numbers[ 24 ],
            factio,
            Wheels,
            Tuning,
            Insurance,
            Duzimai,
            LockType,
            Alarm,
            Trunk[ 512 ],
            Ticket,
            Hidraulik,
            crimes,
            VehIDD,
            Damage[ 50 ],
            Float:KM,
            VW;

        ID = cache_get_field_content_int(0, "id");
        cache_get_field_content(0, "cName", vName);
        Owner = cache_get_field_content_int(0, "cOwner");
        model = cache_get_field_content_int(0, "cModel");
        Spawn_x = cache_get_field_content_float(0, "cSpawn1");
        Spawn_y = cache_get_field_content_float(0, "cSpawn2");
        Spawn_z = cache_get_field_content_float(0, "cSpawn3");
        Spawn_a = cache_get_field_content_float(0, "cAngle");
        color1 = cache_get_field_content_int(0, "cColor1");
        color2 = cache_get_field_content_int(0, "cColor2");
        Lock = cache_get_field_content_int(0, "cLock");
        Fuel = cache_get_field_content_int(0, "cFuel");
        cache_get_field_content(0, "cNumbers", Numbers);
        factio = cache_get_field_content_int(0, "cFaction");
        Wheels = cache_get_field_content_int(0, "cWheels");
        Tuning = cache_get_field_content_int(0, "cTuning");
        Insurance = cache_get_field_content_int(0, "cInsurance");
        Duzimai = cache_get_field_content_int(0, "cDuzimai");
        LockType = cache_get_field_content_int(0, "cLockType");
        Alarm = cache_get_field_content_int(0, "cAlarm");
        cache_get_field_content(0, "cTrunk", Trunk);
        Ticket = cache_get_field_content_int(0, "cTicket");
        Hidraulik = cache_get_field_content_int(0, "cHidraulik");
        crimes = cache_get_field_content_int(0, "cCrimes");
        VehIDD = cache_get_field_content_int(0, "cVehID");
        cache_get_field_content(0, "cDamage", Damage);
        KM = cache_get_field_content_float(0, "cKM");
        VW = cache_get_field_content_int(0, "cVW");


        cache_delete(result);

        if ( VehIDD > 0 )
        {
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, pasirinkta tr. priemonë jau yra iðparkuota..");
        }
        new masina = CreateVehicle( model, Spawn_x, Spawn_y, Spawn_z, Spawn_a, color1, color2, -1 );

        new panels,doors,lights,tires,Health;
        sscanf( Damage,"p</>ddddd",panels, doors, lights, tires, Health);
        UpdateVehicleDamageStatus(masina, panels, doors, lights, tires);
        SetVehicleHealth(masina, Health);

        strmid( cInfo[ masina ][ cName    ], vName  , 0, 24, 24 );
        strmid( cInfo[ masina ][ cNumbers ], Numbers, 0, 24, 24 );
        strmid( cInfo[ masina ][ cDamage  ], Damage,  0, 40, 40 );

        cInfo[ masina ][ cOwner     ] = Owner;
        cInfo[ masina ][ cID        ] = ID;
        cInfo[ masina ][ cModel     ] = model;
        cInfo[ masina ][ cSpawn     ][ 0 ] = Spawn_x;
        cInfo[ masina ][ cSpawn     ][ 1 ] = Spawn_y;
        cInfo[ masina ][ cSpawn     ][ 2 ] = Spawn_z;
        cInfo[ masina ][ cSpawn     ][ 3 ] = Spawn_a;
        cInfo[ masina ][ cColor     ][ 0 ] = color1;
        cInfo[ masina ][ cColor     ][ 1 ] = color2;
        cInfo[ masina ][ cLock      ] = Lock;
        cInfo[ masina ][ cFuel      ] = Fuel;
        cInfo[ masina ][ cFaction   ] = factio;
        cInfo[ masina ][ cWheels    ] = Wheels;
        cInfo[ masina ][ cTuning    ] = Tuning;
        cInfo[ masina ][ cInsurance ] = Insurance;
        cInfo[ masina ][ cDuzimai   ] = Duzimai;
        cInfo[ masina ][ cLockType  ] = LockType;
        cInfo[ masina ][ cAlarm     ] = Alarm;
        cInfo[ masina ][ cTicket    ] = Ticket;
        cInfo[ masina ][ cHidraulik ] = Hidraulik;
        cInfo[ masina ][ cCrimes    ] = crimes;
        cInfo[ masina ][ cVehID     ] = masina;
        cInfo[ masina ][ cKM        ] = KM;
        cInfo[ masina ][ cVirtWorld ] = VW;
        UnPackTrunk( masina, Trunk );


        TuneCarMods( masina);
        AddVehicleComponent( masina, cInfo[ masina ][ cWheels ] );
        AddVehicleComponent( masina, cInfo[ masina ][ cHidraulik ] );
        SetVehicleVirtualWorld( masina, VW );
        LoadVehicleCargo(cInfo[ masina ][ cID ], masina);
        LoadVehicleFish(cInfo[ masina ][ cID ], masina);

        SetPlayerCheckPointEx( playerid, CHECKPOINT_CAR, cInfo[ masina ][ cSpawn ][ 0 ],
                                           cInfo[ masina ][ cSpawn ][ 1 ],
                                           cInfo[ masina ][ cSpawn ][ 2 ], 3.0 );
        Engine[ masina ] = false;
        sVehicles[ masina ][ Faction ] = 0;

        format( string, 80, "{000000}%s", cInfo[ masina ][ cNumbers ] );
        SetVehicleNumberPlate( masina, string );
        SendClientMessage( playerid, COLOR_LIGHTRED2, "Jûsø tr. priemonë sëkmingai iðparkuota ir vieta paþymëta raudonu taðku." );
        pInfo[ playerid ][ pCarGet ] ++;
        SaveVehicleEx( masina, "cVehID", masina );
        return 1;
    }
    else if(!strcmp("getdub",select,true))
    {
        if ( pInfo[ playerid ][ pDubKey ] == 0) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, negalite gauti tr. priemonës jei neturite dublikuotu raktu." );
        if ( checkArrestedCar( playerid, pInfo[ playerid ][ pDubKey ] ) ) return 1;

        format (string, 80, "SELECT * FROM `vehicles` WHERE `id` = %d LIMIT 1", pInfo[ playerid ][ pDubKey ] );
        new Cache:result = mysql_query(DbHandle,  string );

        new ID,
            vName[ 24 ],
            Owner,
            model,
            Float:Spawn_x,
            Float:Spawn_y,
            Float:Spawn_z,
            Float:Spawn_a,
            color1,
            color2,
            Lock,
            Fuel,
            Numbers[ 24 ],
            factio,
            Wheels,
            Tuning,
            Insurance,
            Duzimai,
            LockType,
            Alarm,
            Trunk[ 512 ],
            Ticket,
            Hidraulik,
            crimes,
            VehIDD,
            Damage[ 50 ],
            Float:KM,
            VW;

        ID = cache_get_field_content_int(0, "id");
        cache_get_field_content(0, "cName", vName);
        Owner = cache_get_field_content_int(0, "cOwner");
        model = cache_get_field_content_int(0, "cModel");
        Spawn_x = cache_get_field_content_float(0, "cSpawn1");
        Spawn_y = cache_get_field_content_float(0, "cSpawn2");
        Spawn_z = cache_get_field_content_float(0, "cSpawn3");
        Spawn_a = cache_get_field_content_float(0, "cAngle");
        color1 = cache_get_field_content_int(0, "cColor1");
        color2 = cache_get_field_content_int(0, "cColor2");
        Lock = cache_get_field_content_int(0, "cLock");
        Fuel = cache_get_field_content_int(0, "cFuel");
        cache_get_field_content(0, "cNumbers", Numbers);
        factio = cache_get_field_content_int(0, "cFaction");
        Wheels = cache_get_field_content_int(0, "cWheels");
        Tuning = cache_get_field_content_int(0, "cTuning");
        Insurance = cache_get_field_content_int(0, "cInsurance");
        Duzimai = cache_get_field_content_int(0, "cDuzimai");
        LockType = cache_get_field_content_int(0, "cLockType");
        Alarm = cache_get_field_content_int(0, "cAlarm");
        cache_get_field_content(0, "cTrunk", Trunk);
        Ticket = cache_get_field_content_int(0, "cTicket");
        Hidraulik = cache_get_field_content_int(0, "cHidraulik");
        crimes = cache_get_field_content_int(0, "cCrimes");
        VehIDD = cache_get_field_content_int(0, "cVehID");
        cache_get_field_content(0, "cDamage", Damage);
        KM = cache_get_field_content_float(0, "cKM");
        VW = cache_get_field_content_int(0, "cVW");
        cache_delete(result);

        if ( VehIDD > 0 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, tr. priemonë jau yra iðparkuota.");
        new masina = CreateVehicle( model, Spawn_x, Spawn_y, Spawn_z, Spawn_a, color1, color2, -1 );

        new panels,doors,lights,tires,Health;
        sscanf( Damage,"p</>ddddd",panels, doors, lights, tires, Health);
        UpdateVehicleDamageStatus(masina, panels, doors, lights, tires);
        SetVehicleHealth(masina, Health);

        strmid( cInfo[ masina ][ cName    ], vName  , 0, 24, 24 );
        strmid( cInfo[ masina ][ cNumbers ], Numbers, 0, 24, 24 );
        strmid( cInfo[ masina ][ cDamage  ], Damage,  0, 40, 40 );

        cInfo[ masina ][ cOwner     ] = Owner;
        cInfo[ masina ][ cID        ] = ID;
        cInfo[ masina ][ cModel     ] = model;
        cInfo[ masina ][ cSpawn     ][ 0 ] = Spawn_x;
        cInfo[ masina ][ cSpawn     ][ 1 ] = Spawn_y;
        cInfo[ masina ][ cSpawn     ][ 2 ] = Spawn_z;
        cInfo[ masina ][ cSpawn     ][ 3 ] = Spawn_a;
        cInfo[ masina ][ cColor     ][ 0 ] = color1;
        cInfo[ masina ][ cColor     ][ 1 ] = color2;
        cInfo[ masina ][ cLock      ] = Lock;
        cInfo[ masina ][ cFuel      ] = Fuel;
        cInfo[ masina ][ cFaction   ] = factio;
        cInfo[ masina ][ cWheels    ] = Wheels;
        cInfo[ masina ][ cTuning    ] = Tuning;
        cInfo[ masina ][ cInsurance ] = Insurance;
        cInfo[ masina ][ cDuzimai   ] = Duzimai;
        cInfo[ masina ][ cLockType  ] = LockType;
        cInfo[ masina ][ cAlarm     ] = Alarm;
        cInfo[ masina ][ cTicket    ] = Ticket;
        cInfo[ masina ][ cHidraulik ] = Hidraulik;
        cInfo[ masina ][ cCrimes    ] = crimes;
        cInfo[ masina ][ cVehID     ] = masina;
        cInfo[ masina ][ cKM        ] = KM;
        cInfo[ masina ][ cDub       ] = 1;
        cInfo[ masina ][ cVirtWorld ] = VW;
        UnPackTrunk( masina, Trunk );

        TuneCarMods( masina);
        AddVehicleComponent   ( masina, cInfo[ masina ][ cWheels ] );
        AddVehicleComponent   ( masina, cInfo[ masina ][ cHidraulik ] );
        SetVehicleVirtualWorld( masina, VW );
        LoadVehicleCargo(cInfo[ masina ][ cID ], masina);

        SetPlayerCheckPointEx( playerid, CHECKPOINT_CAR, cInfo[ masina ][ cSpawn ][ 0 ],
                                           cInfo[ masina ][ cSpawn ][ 1 ],
                                           cInfo[ masina ][ cSpawn ][ 2 ], 3.0 );
        Engine[ masina ] = false;
        sVehicles[ masina ][ Faction ] = 0;

        if ( IsPlayerConnected( GetCarOwner( masina ) ) )
            pInfo[ GetCarOwner( masina ) ][ pCarGet ] ++;

        format( string, 80, "{000000}%s", cInfo[ masina ][ cNumbers ] );
        SetVehicleNumberPlate( masina, string );
        SendClientMessage( playerid, COLOR_LIGHTRED2, "Jûsø tr. priemonë sëkmingai iðparkuota ir vieta paþymëta raudonu taðku." );
        SaveVehicleEx( masina, "cVehID", masina );
        return 1;
    }
    else if(!strcmp("park",select,true))
    {
        new idcar = INVALID_VEHICLE_ID;
        if(GetPlayerState(playerid) == PLAYER_STATE_DRIVER)
            idcar = GetPlayerVehicleID( playerid );
        else
            idcar = GetNearestVehicle( playerid, 5.0 );
        if ( idcar == INVALID_VEHICLE_ID ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, aplink Tave nëra jokios tr. priemonës");

        if(cInfo[idcar][cOwner] != pInfo[playerid][pMySQLID] && pInfo[ playerid ][ pDubKey ] != cInfo[ idcar ][ cID ]) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Tai ne jûsø automobilis!");
        new Float:CarHP;
        GetVehicleHealth( idcar, CarHP );
        if ( CarHP < 500 )
            return SendClientMessage( playerid, COLOR_LIGHTRED, "Dëmesio, Jûsø tr. priemonë yra pernelyg daug sudauþytà, kad galëtumëte jà uþparkuoti." );

        if(!PlayerToPoint(10.0,playerid,cInfo[idcar][cSpawn][0],cInfo[idcar][cSpawn][1],cInfo[idcar][cSpawn][2]))
            return SendChatMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Jûs negalite èia parkuoti, nes tai nëra transporto priemonës parkavimo vieta (norëdami nusipirkti parkavimo vietà , raðykite /v buypark)");

        new panels,doors,lights,tires;
        GetVehicleDamageStatus(idcar,panels,doors,lights,tires);
        format( cInfo[ idcar ][ cDamage ], 50, "%d/%d/%d/%d/%d/", panels, doors, lights, tires, floatround( CarHP) );
        cInfo[ idcar ][ cVirtWorld ] = GetVehicleVirtualWorld( idcar );

        cInfo[idcar][cVehID] = 0;
        DestroyVehicle(idcar);
        if ( pInfo[ playerid ][ pCarGet ] > 0 )
            pInfo[ playerid ][ pCarGet ] --;

        SaveCar(idcar);
        nullVehicle( idcar );
        SendClientMessage(playerid,COLOR_LIGHTRED2," Jûsø tr. priemonë buvo sëkmingai priparkuota. Norëdami gauti raðykite /v get.");
        PlayerPlaySound(playerid, 1057, 0.0, 0.0, 0.0);

        return 1;
    }
    else if(!strcmp("buypark",select,true))
    {
        new idcar = INVALID_VEHICLE_ID;
        if(GetPlayerState(playerid) == PLAYER_STATE_DRIVER)
            idcar = GetPlayerVehicleID( playerid );
        else
            idcar = GetNearestVehicle( playerid, 5.0 );
        if ( idcar == INVALID_VEHICLE_ID ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, aplink Tave nëra jokios tr. priemonës");

        if(cInfo[idcar][cOwner] != pInfo[playerid][pMySQLID]) return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, ði tr. priemonë nepriklauso Jums.");
        if(PlayerMoney[ playerid ] < 400) return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, Jums nepakanka pakankamai pinigø, kad galëtumëte nusipirkti parkavimo vietà. Kaina: 400$");

        GetVehiclePos(idcar,cInfo[idcar][cSpawn][0],cInfo[idcar][cSpawn][1],cInfo[idcar][cSpawn][2]);
        GetVehicleZAngle(idcar,cInfo[idcar][cSpawn][3]);
        pInfo[playerid][pCarGet] --;
        cInfo[idcar][cVehID] = 0;

        new panels,doors,lights,tires, Float:Health;
        GetVehicleDamageStatus(idcar, panels, doors, lights, tires);
        GetVehicleHealth( idcar, Health );
        format( cInfo[ idcar ][ cDamage ], 50, "%d/%d/%d/%d/%d/", panels, doors, lights, tires, floatround( Health) );
        cInfo[ idcar ][ cVirtWorld ] = GetVehicleVirtualWorld( idcar );

        DestroyVehicle(idcar);
        SaveCar(idcar);
        nullVehicle( idcar );
        GivePlayerMoney(playerid, -400);
        SendClientMessage(playerid,COLOR_LIGHTRED2," Nauja tr. priemonës parkavimo vieta sëkmingai nustatyta. Dabar naudodami /v get, tr. priemonæ gausite èia.");
        PlayerPlaySound(playerid, 1057, 0.0, 0.0, 0.0);
        Engine[idcar] = false;
        return 1;
    }
    else if(!strcmp("register",select,true))
    {
        if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER)
            return SendClientMessage(playerid,COLOR_LIGHTRED,"Dëmesio, norëdami atlikti ðá veiksmà privalote sedëti tr. priemonëje.");
        new veh = GetPlayerVehicleID( playerid );
        if(cInfo[veh][cOwner] != pInfo[playerid][pMySQLID])
            return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, ði tr. priemonë nepriklauso Jums.");

        if(PlayerMoney[ playerid ] < 350)
            return SendClientMessage(playerid,COLOR_LIGHTRED,"Dëmesio, tr. priemonës registracija kainuoja 350$.");

        new numers = 10 + cInfo[veh][cID],
            leters[][2] = {
                {"A"},
                {"B"},
                {"C"},
                {"D"},
                {"E"},
                {"F"},
                {"G"},
                {"H"},
                {"I"},
                {"J"},
                {"K"},
                {"L"},
                {"M"},
                {"N"},
                {"O"},
                {"P"},
                {"Q"},
                {"R"},
                {"S"},
                {"T"},
                {"U"},
                {"V"},
                {"Y"},
                {"Z"}
            };

        format( cInfo[ veh ][ cNumbers ], 24, "%s%s%s%d",leters[ random( sizeof( leters ) ) ],
                                                          leters[ random( sizeof( leters ) ) ],
                                                          leters[ random( sizeof( leters ) ) ],
                                                          numers);

        format(string,256,"Sëkmingai uþregistravote tr. priemonæ Los Santos miesto automobiliø registre, Jûsø tr. priemonës numeriai: %s",cInfo[veh][cNumbers]);

        SendClientMessage(playerid,COLOR_LIGHTRED2,string);
        GivePlayerMoney(playerid,-350);
        cInfo[ veh ][ cCrimes     ] = 0;

        SaveCar( veh );
        format( string, 24, "{000000}%s", cInfo[ veh ][ cNumbers ] );
        SetVehicleNumberPlate(veh, string );
        return 1;
    }
    else if(!strcmp("payticket",select,true))
    {
        if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER) return SendClientMessage(playerid,COLOR_LIGHTRED,"Dëmesio, norëdami atlikti ðá veiksmà privalote sedëti tr. priemonëje.");
        new idcar = GetPlayerVehicleID( playerid );
        if(cInfo[idcar][cOwner] != pInfo[playerid][pMySQLID]) return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, ði tr. priemonë nepriklauso Jums.");
        if(PlayerMoney[ playerid ] < cInfo[ idcar ][ cTicket ]) return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, Jums nepakanka pinigø, kad susimokëtumëte baudà.");
        format           ( string, 64, "Sëkmingai susimokëjote baudà, kuri Jums kainavo: %d$. Kità kartà bûkite atsargesni.",cInfo[ idcar ][ cTicket ] );
        SendClientMessage( playerid, COLOR_GREEN, string );
        GivePlayerMoney(playerid, -cInfo[ idcar ][ cTicket ]);
        cInfo[ idcar ][ cTicket ] = 0;
        SaveCar( idcar );
        return 1;
    }
    else if(!strcmp("find",select,true))
    {
        select = strtok( params, idx );
        new slot = strval( select );
        if ( slot == 0 )
            return SendClientMessage( playerid, COLOR_LIGHTRED,"Teisingas komandos naudojimas:/v find [/v list SÀRAÐO NUMERIS] " );
        if ( slot < 0 || slot > 20 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Dëmesio, nurodytas sàraðo numeris neturi bûti didesnis uþ 20 ar maþesnis uþ 1" );

        foreach(Vehicles,tacke)
        {
            if ( cInfo[ tacke ][ cOwner ] == pInfo[ playerid ][ pMySQLID ] && pInfo[ playerid ][ pCar ][ slot ] == cInfo[ tacke ][ cID ])
            {
                if ( cInfo[ tacke ][ cVehID ] != 0 )
                {
                    if(cInfo[ tacke ][cAlarm] < 1) return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite naudotis ðia galimybæ, kadangi Jûsø tr.priemonëje nëra ámontuoto GPS siûstuvo.");
                    if ( VGaraze[ tacke ] == false )
                    {
                        GetVehiclePos( tacke, Kords[ 0 ], Kords[ 1 ],Kords[ 2 ] );
                        SetPlayerCheckPointEx( playerid, CHECKPOINT_CAR, Kords[ 0 ], Kords[ 1 ], Kords[ 2 ], 3.0 );
                        SendClientMessage( playerid, COLOR_LIGHTRED2, "[GPS] Pasirinkta tr. priemonë paþymëta þemëlapyje raudonu taðku." );
                        return 1;
                    }
                    else
                    {
                        foreach(Garages,g)
                        {
                            if ( gInfo[ g ][ gID ] == cInfo[ tacke ][ cVirtWorld ] )
                            {
                                SetPlayerCheckPointEx( playerid, CHECKPOINT_CAR, gInfo[ g ][ gVehicleEnter ][ 0 ], gInfo[ g ][ gVehicleEnter ][ 1 ],gInfo[ g ][ gVehicleEnter ][ 2 ], 3.0 );
                                SendClientMessage( playerid, COLOR_LIGHTRED2, "[GPS] Jûsø tr. priemonë yra pastatytà garaþe, kurio kordinates paþymëjome raudonu taðku" );
                                return 1;
                            }
                        }
                    }
                    return 1;
                }
            }
        }
        return 1;
    }
    // buycar
    else if(!strcmp("buy",select,true))
    {
        if(pInfo[ playerid ][ pCar ][ 20 ] > 0)
            return SendClientMessage(playerid,COLOR_LIGHTRED,"Dëmesio, Jûs jau pasiekëte maksimalø 20 automobiliø limità.");

        foreach(VehicleShopIterator, i)
        {
            if(!IsPlayerInRangeOfPoint(playerid, 5.0, VehicleShops[ i ][ PosX ], VehicleShops[ i ][ PosY ], VehicleShops[ i ][ PosZ ]))
                continue;

            for(new j = 0; j < MAX_VEHICLE_SHOP_VEHICLES; j++)
                if(VehicleShops[ i ][ VehicleModels ][ j ])
                    format(string, sizeof(string),"%s%s - $%d\n", string, aVehicleNames[ VehicleShops[ i ][ VehicleModels ][ j ] - 400 ], VehicleShops[ i ][ VehiclePrices ][ j ]);
            CurrentPlayerVehicleShop[ playerid ] = i;
            ShowPlayerDialog(playerid, DIALOG_VEHICLE_SHOP, DIALOG_STYLE_LIST, VehicleShops[ i ][ Name ], string, "Pirkti", "Atðaukti");
            return 1;
        }
        SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neesate prie transporto parduotuvës.");
    }

    else if(!strcmp("lock",select,true))
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
    else if(!strcmp("faction",select,true))
    {
        if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER) return SendClientMessage(playerid,COLOR_LIGHTRED,"Dëmesio, norëdami atlikti ðá veiksmà privalote sedëti tr. priemonëje.");
        new idcar = GetPlayerVehicleID( playerid );
        if(cInfo[idcar][cOwner] != pInfo[playerid][pMySQLID]) return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, ði tr. priemonë nepriklauso Jums.");
        select = strtok(params, idx);
        if(!strlen(select)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /v faction [frakcijos id]");
        giveplayerid  = strval(select);
        if(pInfo[playerid][pMember] != giveplayerid ) return SendClientMessage(playerid,COLOR_LIGHTRED, "Klaida, Tu nperiklausai ðiai frakcijai.");
        cInfo[idcar][cFaction] = giveplayerid;
        format(string,256,"Jûs priskyrëte savo automobilá ðiai frakcijai: %d",giveplayerid);
        SendClientMessage(playerid,COLOR_WHITE,string);
        return 1;
    }
    else if(!strcmp("buyinsurance",select,true))
    {
        if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER) return SendClientMessage(playerid,COLOR_LIGHTRED,"Dëmesio, norëdami atlikti ðá veiksmà privalote sedëti tr. priemonëje.");
        new idcar = GetPlayerVehicleID( playerid );
        if(cInfo[idcar][cOwner] != pInfo[playerid][pMySQLID]) return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, ði tr. priemonë nepriklauso Jums.");
        new insprice = 800;

        insprice += cInfo[ idcar ][ cDuzimai   ] * 300;
        insprice += cInfo[ idcar ][ cInsurance ] * 100;

        if ( PlayerMoney[ playerid ] < insprice)
        {
            format           ( string, 126, "Klaida, neturite pakankamai grynøjø pinigø, kadangi draudimo kaina yra %d$. Uþ kiekviena paþeidimà papildomai %d$.",insprice,insprice += cInfo[ idcar ][ cDuzimai   ] * 200),
            SendClientMessage( playerid, COLOR_LIGHTRED, string );
            return 1;
        }
        GivePlayerMoney  ( playerid, -insprice );
        format           ( string, 126, "Draudimo pratæsimas vienieriems metams Jums kainavo %d$.", insprice);
        SendClientMessage( playerid, COLOR_GREEN, string);
        cInfo[ idcar ][ cInsurance ] ++;
        SaveCar(idcar);
        return 1;
    }
    else if(!strcmp("buyalarm",select,true))
    {
        if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER) return SendClientMessage(playerid,COLOR_LIGHTRED,"Dëmesio, norëdami atlikti ðá veiksmà privalote sedëti tr. priemonëje.");
        new idcar = GetPlayerVehicleID( playerid );
        if(cInfo[idcar][cOwner] != pInfo[playerid][pMySQLID]) return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, ði tr. priemonë nepriklauso Jums.");
        new nearestplaya = GetNearestPlayer(playerid,10.0);
        if(!IsPlayerConnected(nearestplaya)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio, norëdami ádiegti atnaujinimus á tr. priemonæ turite praðyti mechaniko paslaugø.");
        if(pInfo[nearestplaya][pJob] != JOB_MECHANIC) return SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio, norëdami ádiegti atnaujinimus á tr. priemonæ turite praðyti mechaniko paslaugø.");
        select = strtok(params, idx);
        if(!strlen(select))
        {
            SendClientMessage(playerid,COLOR_LIGHTRED, "Teisingas komandos naudojimas: /v buyalarm [id]");
            SendClientMessage(playerid,COLOR_FADE1,"Pasirinkite norima signalizacijos lygi.");
            SendClientMessage(playerid,COLOR_FADE1,"1. Paprasta signalizacija - $400");
            SendClientMessage(playerid,COLOR_FADE1,"2. Paprasta signalizacija su GPS ryðiu - $850");
            SendClientMessage(playerid,COLOR_FADE1,"3. Profesonali signalizacija su GPS ir PD ryðiu - $2100");
            SendClientMessage(playerid,COLOR_FADE1,"4. Pro. Signalizacija su GPS, policijos ir asmeniniu praneðikliu - $3000");
            return 1;
        }
        giveplayerid = strval(select);
        if(giveplayerid == 1)
        {
            if(PlayerMoney[ playerid ] < 400) return SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio Jums nepakanka grynøjø pinigø ðiam atnaujinimui. ($1500).");
            GivePlayerMoney(playerid,-400);
            cInfo[idcar][cAlarm] = 1;
            SendClientMessage(playerid,COLOR_LIGHTRED2,"Sveikiname, 1 lygio signalizacija buvo sëkmingai ádiegta á Jûsø tr. priemonæ.");
            SaveCar(idcar);
            return 1;
        }
        else if(giveplayerid == 2)
        {
            if(PlayerMoney[ playerid ] < 850) return SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio Jums nepakanka grynøjø pinigø ðiam atnaujinimui. ($3000).");
            GivePlayerMoney(playerid,-850);
            cInfo[idcar][cAlarm] = 2;
            SendClientMessage(playerid,COLOR_LIGHTRED2,"Sveikiname, 2 lygio signalizacija buvo sëkmingai ádiegta á Jûsø tr. priemonæ.");
            SaveCar(idcar);
            return 1;
        }
        else if(giveplayerid == 3)
        {
            if(PlayerMoney[ playerid ] < 2100) return SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio Jums nepakanka grynøjø pinigø ðiam atnaujinimui. ($6000).");
            GivePlayerMoney(playerid,-2100);
            cInfo[idcar][cAlarm] = 3;
            SendClientMessage(playerid,COLOR_LIGHTRED2,"Sveikiname, 3 lygio signalizacija buvo sëkmingai ádiegta á Jûsø tr. priemonæ.");
		    SaveCar(idcar);
            return 1;
        }
        else if(giveplayerid == 4)
        {
            if(PlayerMoney[ playerid ] < 3000) return SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio Jums nepakanka grynøjø pinigø ðiam atnaujinimui. ($9000).");
            GivePlayerMoney(playerid,-3000);
            cInfo[idcar][cAlarm] = 4;
            SendClientMessage(playerid,COLOR_LIGHTRED2,"Sveikiname, 4 lygio signalizacija buvo sëkmingai ádiegta á Jûsø tr. priemonæ.");
            SaveCar(idcar);
            return 1;
        }
        return 1;
    }
    else if(!strcmp("buylock",select,true))
    {
        if(GetPlayerState(playerid) != PLAYER_STATE_DRIVER) return SendClientMessage(playerid,COLOR_LIGHTRED,"Dëmesio, norëdami atlikti ðá veiksmà privalote sedëti tr. priemonëje.");
        new idcar = GetPlayerVehicleID( playerid );
        if(cInfo[idcar][cOwner] != pInfo[playerid][pMySQLID]) return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, ði tr. priemonë nepriklauso Jums.");
        new nearestplaya = GetNearestPlayer(playerid,10.0);
        if(!IsPlayerConnected(nearestplaya)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: ðalia jûsø nëra automechaniko.");
        if(pInfo[nearestplaya][pJob] != JOB_MECHANIC) return SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio, norëdami ádiegti atnaujinimus á tr. priemonæ turite praðyti mechaniko paslaugø.");
        select = strtok(params, idx);
        if(!strlen(select))
        {
            SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /v buylock [id]");
            SendClientMessage(playerid,COLOR_FADE1,"Pasirinkite norima uþrakto lygi.");
            SendClientMessage(playerid,COLOR_FADE1,"1. Neþinomos firm. spynos uþraktas - $200");
            SendClientMessage(playerid,COLOR_FADE1,"2. Originalus spynos uþraktas - $500");
            SendClientMessage(playerid,COLOR_FADE1,"3. Sustiprintas spynos uþraktas - $1100");
            SendClientMessage(playerid,COLOR_FADE1,"4. Titaninis spynos uþraktas - $1600");
            SendClientMessage(playerid,COLOR_FADE1,"5. Titaninis spynos uþraktas su el. rakteliu - $2100");
            return 1;
        }
        giveplayerid = strval(select);
        if(giveplayerid == 1)
        {
            if(PlayerMoney[ playerid ] < 200) return SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio Jums nepakanka grynøjø pinigø ðiam atnaujinimui. ($500).");
            GivePlayerMoney(playerid,-200);
            cInfo[idcar][cLockType] = 1;
            SendClientMessage(playerid,COLOR_LIGHTRED2,"Sveikiname, 1 lygio uþraktas buvo sëkmingai ádiegtas á Jûsø tr. priemonæ.");
            return 1;
        }
        else if(giveplayerid == 2)
        {
            if(PlayerMoney[ playerid ] < 500) return SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio Jums nepakanka grynøjø pinigø ðiam atnaujinimui. ($1000).");
            GivePlayerMoney(playerid,-500);
            cInfo[idcar][cLockType] = 2;
            SendClientMessage(playerid,COLOR_LIGHTRED2,"Sveikiname, 2 lygio uþraktas buvo sëkmingai ádiegtas á Jûsø tr. priemonæ.");
            return 1;
        }
        else if(giveplayerid == 3)
        {
            if(PlayerMoney[ playerid ] < 1100) return SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio Jums nepakanka grynøjø pinigø ðiam atnaujinimui. ($1500).");
            GivePlayerMoney(playerid,-1100);
            cInfo[idcar][cLockType] = 3;
            SendClientMessage(playerid,COLOR_LIGHTRED2,"Sveikiname, 3 lygio uþraktas buvo sëkmingai ádiegtas á Jûsø tr. priemonæ.");
            return 1;
        }
        else if(giveplayerid == 4)
        {
            if(PlayerMoney[ playerid ] < 1600) return SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio Jums nepakanka grynøjø pinigø ðiam atnaujinimui. ($2000).");
            GivePlayerMoney(playerid,-1600);
            cInfo[idcar][cLockType] = 4;
            SendClientMessage(playerid,COLOR_LIGHTRED2,"Sveikiname, 4 lygio sëkmingai imontuota á jûsø automobilá.");
            return 1;
        }
        else if(giveplayerid == 5)
        {
            if(PlayerMoney[ playerid ] < 2100) return SendClientMessage(playerid, COLOR_LIGHTRED, "Dëmesio Jums nepakanka grynøjø pinigø ðiam atnaujinimui. ($2500).");
            GivePlayerMoney(playerid,-2100);
            cInfo[idcar][cLockType] = 5;
            SendClientMessage(playerid,COLOR_LIGHTRED2,"Sveikiname, 5 lygio uþraktas buvo sëkmingai ádiegtas á Jûsø tr. priemonæ.");
            return 1;
        }
    }
    else if(!strcmp("sellto",select,true))
    {
        new idcar = INVALID_VEHICLE_ID;
        if(GetPlayerState(playerid) == PLAYER_STATE_DRIVER)
            idcar = GetPlayerVehicleID( playerid );
        else
            idcar = GetNearestVehicle( playerid, 6.0 );
        if ( idcar == INVALID_VEHICLE_ID ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Klaida, aplink Tave nëra jokios tr. priemonës");

        if(cInfo[idcar][cOwner] != pInfo[playerid][pMySQLID]) return SendClientMessage(playerid,COLOR_LIGHTRED,"Klaida, ði tr. priemonë nepriklauso Jums.");
        select = strtok(params, idx);
        if(!strlen(select)) return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /v sellto [þaidëjo id][kaina]");
        giveplayerid = strval(select);
        if(!IsPlayerConnected(giveplayerid))  return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nurodytas veikëjo ID negalimas, kadangi toks ID nëra prisijungæs serveryje.");
        select = strtok(params, idx);
        new price = strval(select);
        if(price < 0 || price > 99999999) return SendClientMessage(playerid, COLOR_LIGHTRED, "Perspëjimas: Kaina negali buti maþesnë negu 0 ir didesnë negu 99999999.");
        format(string,256,"Jûs siulote jam %s,kad jis nupirktu jûsø automobilá uþ: $%d.",GetPlayerNameEx(giveplayerid),price);
        SendClientMessage(playerid,COLOR_WHITE,string);
        format(string,256,"Automobilio savininkas %s siûlo jums nupirkti jo automobilá uþ: $%d, jeigu sutinkate,raðykite /accept car.",GetPlayerNameEx(playerid),price);
        SendClientMessage(giveplayerid,COLOR_WHITE,string);
        Offer[giveplayerid][0] = playerid;
        OfferPrice[giveplayerid][0] = price;
        OfferID[ giveplayerid ][ 0 ] = idcar;
        return 1;
    }
    else
    {
        SendClientMessage(playerid, COLOR_LIGHTRED, "__________________________Tr. Priemoniu komandos ir naudojimas__________________________");
        SendClientMessage(playerid,COLOR_LIGHTRED2, "  KOMANDOS NAUDOJIMAS: /v [komanda], pavyzdþiui: /v list");
        SendClientMessage(playerid,GRAD,"  PAGRINDINES: list, get, park, find, buy, dubkey, takedubkey, removedubs");
        SendClientMessage(playerid,GRAD,"  PAGRINDINES: faction, documents, getdub, payticket");
        SendClientMessage(playerid,GRAD,"  TOBULINIMAS: register, buyalarm, buylock, buyinsurance");
        SendClientMessage(playerid,GRAD,"  KITOS: /vradio, /trunk, /trunko, /bonnet, /windows, /setbelt, /maxspeed");
        return 1;
    }
    return 1;
}

/*
	             ,,                                                AW
	MMP""MM""YMM db                                               ,M'mm                   `7MM
	P'   MM   `7                                                  MV MM                     MM
	     MM    `7MM  `7MMpMMMb.pMMMb.  .gP"Ya `7Mb,od8 ,pP"Ybd   AWmmMMmm  ,6"Yb.  ,pP"Ybd  MM  ,MP',pP"Ybd
	     MM      MM    MM    MM    MM ,M'   Yb  MM' "' 8I   `"  ,M'  MM   8)   MM  8I   `"  MM ;Y   8I   `"
	     MM      MM    MM    MM    MM 8M""""""  MM     `YMMMa.  MV   MM    ,pm9MM  `YMMMa.  MM;Mm   `YMMMa.
	     MM      MM    MM    MM    MM YM.    ,  MM     L.   I8 AW    MM   8M   MM  L.   I8  MM `Mb. L.   I8
	   .JMML.  .JMML..JMML  JMML  JMML.`Mbmmd'.JMML.   M9mmmP',M'    `Mbmo`Moo9^Yo.M9mmmP'.JMML. YA.M9mmmP'
	                                                          MV
	                                                         AW                                              */





















