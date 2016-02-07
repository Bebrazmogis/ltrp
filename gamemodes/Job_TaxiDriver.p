#include <YSI\y_hooks>


#define MIN_TAXI_FARE					0
#define MAX_TAXI_FARE					100
#define TAXI_ENTRY_FEE					10


new TaxiVehiclePrice[ MAX_VEHICLES ] = {-1, ... },
	PlayerTaxiDriver[ MAX_PLAYERS ] = { INVALID_PLAYER_ID, ... },
	PlayerTaxi[ MAX_PLAYERS ] = { INVALID_VEHICLE_ID, ...},
	Float:PlayerDistanceDrivenAsPassenger[ MAX_PLAYERS ],
	Float:PlayerLastTaxiPos[ MAX_PLAYERS ][ 3 ];







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


hook OnPlayerStateChange(playerid, newstate, oldstate)
{

	if(newstate == PLAYER_STATE_PASSENGER)
	{
		new vehicleid = GetPlayerVehicleID(playerid), string[130];
		if(IsTaxiVehicle(GetVehicleModel(vehicleid)))
		{
			if(HasVehicleDriver(vehicleid))
			{
				if(GetPlayerMoney(playerid) < TaxiVehiclePrice[ vehicleid ] && GetPlayerBankMoney(playerid) < TaxiVehiclePrice[ playerid ])
				{
					RemovePlayerFromVehicle(playerid);
					return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs net");
				}
				PlayerDistanceDrivenAsPassenger[ playerid ] = 0.0;
				PlayerTaxiDriver[ playerid ] = GetVehicleDriver(vehicleid);
				PlayerTaxi[ playerid ] = vehicleid;
				GetVehiclePos(vehicleid, PlayerLastTaxiPos[ playerid ][ 0 ], PlayerLastTaxiPos[ playerid ][ 1 ], PlayerLastTaxiPos[ playerid ][ 2 ]);
				format(string, sizeof(string),"[TAXI] Nurodyta kilometro kaina %d$. Álipdami sumokëjote ásëdimo mokestá - $" #TAXI_ENTRY_FEE " ", TaxiVehiclePrice[ vehicleid ]);
				SendClientMessage(playerid, COLOR_YELLOW, string);
				return 1;
			}
			else
			{
				format(string, sizeof(string),"[TAXI] Nurodyta kilometro kaina %d$. Ðiuo metu vairuotojo nëra, kelionës mokestis bus ájungtas jam álipus.", TaxiVehiclePrice[ vehicleid ]);
				SendClientMessage(playerid, COLOR_YELLOW, string);
			}
		}

	}
	if(oldstate == PLAYER_STATE_PASSENGER)
	{
		if(PlayerTaxiDriver[ playerid ] != INVALID_PLAYER_ID)
		{
			OnPlayerLeaveTaxi(playerid, GetPlayerVehicleID(PlayerTaxiDriver[ playerid ]));
			return 1;
		}
	}
	if(oldstate == PLAYER_STATE_DRIVER)
	{
		if(IsPlayerTaxiDriver(playerid))
		{
			// Jei taksistas iðlipo, klientams kelionë free.
			foreach(new i : Player)
			{
				if(PlayerTaxiDriver[ i ] == playerid)
				{
					PlayerTaxiDriver[ i ] = INVALID_PLAYER_ID;
				    PlayerDistanceDrivenAsPassenger[ i ] = 0.0;
				    PlayerTaxi[ i ] = INVALID_VEHICLE_ID;
				    SendClientMessage(i, COLOR_YELLOW, "[TAXI] Vairuotojas iðlipo, todël ði kelionë jums nieko nekainuos.");
				}
			}
			return 1;
		}
	}
	if(newstate == PLAYER_STATE_DRIVER)
	{
		new vehicleid = GetPlayerVehicleID(playerid);
		if(IsTaxiVehicle(GetVehicleModel(vehicleid)))
		{
			// VISI kas sëdi tam taxi, iðskyrus vairuotoja tampa jo klientais.
			foreach(new i : Player)
			{
				if(IsPlayerInVehicle(i, vehicleid) && i != playerid)
				{
					PlayerTaxiDriver[ i ] = playerid;
					PlayerDistanceDrivenAsPassenger[ i ] = 0.0;
					PlayerTaxi[ i ] = vehicleid;
					SendClientMessage(i, COLOR_YELLOW, "[TAXI] Vairuotojas álipo. Sumokëjote $" #TAXI_ENTRY_FEE " pradiná kelionës mokestá.");
				}
			}
			return 1;
		}
	}
	return 0;
}



hook OnPlayerDisconnect(playerid, reason)
{
	if(PlayerTaxiDriver[ playerid ] != INVALID_PLAYER_ID)
		OnPlayerLeaveTaxi(playerid, GetPlayerVehicleID(PlayerTaxiDriver[ playerid ]));
	return 1;
}


forward OnPlayerLeaveTaxi(playerid, vehicleid);
public OnPlayerLeaveTaxi(playerid, vehicleid)
{
	// Invalid vehicle.
	if(!IsValidVehicle(vehicleid))
		return 0;
	if(GetPlayerTaxiTripPrice(playerid))
	{
		new price = GetPlayerTaxiTripPrice(playerid),
			string[ 100 ];
		if(price <= GetPlayerMoney(playerid))
			GivePlayerMoney(playerid, -price);
		else if(price <= GetPlayerBankMoney(playerid))
			SetPlayerBankMoney(playerid, GetPlayerBankMoney(playerid) - price);
		// Uþtenka pinigø sudëjus bankà ir grynuosius
		else if(price <= GetPlayerMoney(playerid)+ GetPlayerBankMoney(playerid))
		{
			price -= GetPlayerMoney(playerid);
			GivePlayerMoney(playerid, -GetPlayerMoney(playerid));
			SetPlayerBankMoney(playerid, GetPlayerBankMoney(playerid) - price);
		}
		// O jei neuþtenka, paimsim viskà kà turi.
		else
		{
			price = GetPlayerMoney(playerid) + GetPlayerBankMoney(playerid);
			GivePlayerMoney(playerid, -GetPlayerMoney(playerid));
			SetPlayerBankMoney(playerid, 0);
		}

		GivePlayerMoney(PlayerTaxiDriver[ playerid ], price);
		format(string, sizeof(string), "[TAXI] Keleivis iðlipo ið Jûsø transporto priemonës kartu palikdamas: %d$ uþ kelionæ.",price);
	    SendClientMessage(PlayerTaxiDriver[playerid], COLOR_NEWS, string);
	    format(string, sizeof(string), "[TAXI] Jûs sumokëjote taksistui %d$ uþ %.2f KM", price, PlayerDistanceDrivenAsPassenger[ playerid ] / 100);
	    SendClientMessage(playerid, COLOR_YELLOW, string);
	}

    PlayerTaxiDriver[ playerid ] = INVALID_PLAYER_ID;
    PlayerDistanceDrivenAsPassenger[ playerid ] = 0.0;
    PlayerTaxi[ playerid ] = INVALID_VEHICLE_ID;
    DeletePVar(playerid, "Driver_Warned");
    return 1;
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

stock UpdateTaxiInformation(playerid)
{
	if(IsPlayerTaxiDriver(playerid))
		return 1;
	if(!IsValidVehicle(GetPlayerTaxi(playerid)))
		return 1;

	if(TaxiVehiclePrice[ GetPlayerTaxi(playerid) ] == -1)
		return 1;

	if(GetPlayerTaxiTripPrice(playerid) > GetPlayerBankMoney(playerid) + GetPlayerMoney(playerid) && !GetPVarInt(playerid, "Driver_Warned"))
	{
		SendClientMessage(PlayerTaxiDriver[ playerid ], COLOR_NEWS, "Jûsø keleiviui baigësi pinigai!");
		SetPVarInt(playerid, "Driver_Warned", true);
	}

	new Float:progress = GetVehicleDistanceFromPoint(GetPlayerTaxi(playerid), PlayerLastTaxiPos[ playerid ][ 0 ], PlayerLastTaxiPos[ playerid ][ 1 ], PlayerLastTaxiPos[ playerid ][ 2 ]);
	PlayerDistanceDrivenAsPassenger[ playerid ] += progress;
	GetVehiclePos(GetPlayerTaxi(playerid), PlayerLastTaxiPos[ playerid ][ 0 ], PlayerLastTaxiPos[ playerid ][ 1 ], PlayerLastTaxiPos[ playerid ][ 2 ]);

	UpdatePlayerInfoText(playerid);
	return 1;
}

stock IsTaxiVehicle(model)
{
	switch(model)
	{
		case 420, 438: return true;
		default: return false;
	}
	return false;
}

stock GetPlayerTaxiTripPrice(playerid)
{
	if(!IsValidVehicle(PlayerTaxi[ playerid ]))
		return 0;
	if(TaxiVehiclePrice[ PlayerTaxi[ playerid ] ] == -1)
		return 0;
	return floatround(PlayerDistanceDrivenAsPassenger[ playerid ] * TaxiVehiclePrice[ PlayerTaxi[ playerid ] ] / 380) + TAXI_ENTRY_FEE ;
}

stock GetPlayerTaxi(playerid)
	return PlayerTaxi[ playerid ];

stock GetPlayerTaxiDriver(playerid)
	return PlayerTaxiDriver[ playerid ];

stock IsPlayerTaxiDriver(playerid)
{
	foreach(Player, i)
		if(PlayerTaxiDriver[ i ] == playerid)
			return true;
	return false;
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


CMD:taxi(playerid, params[])
{
    new
        new_fare,
        vehicleid = GetPlayerVehicleID( playerid ),
        string[120];

    if(!IsTaxiVehicle(GetVehicleModel(vehicleid)))
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite atlikti veiksmo nebûdami TAXI transporto priemonëje.");
    if(GetPlayerVehicleSeat(playerid) != 0)
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, jûs neesate tr. priemonës vairuotojas.");
    if(GetVehiclePlayerCount(vehicleid) != 1)
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, negalite nustatyti kainos, kada jau veþate klientus..");
    if(sscanf(params, "d", new_fare))
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Teisingas komandos naudojimas: /taxi [KILOMENTRO KAINA], nustaèius -1 kelionë bus nemokama." );
    if(new_fare == -1)
    {
    	TaxiVehiclePrice[ vehicleid ] = -1;
    	SendClientMessage(playerid, COLOR_YELLOW, "[TAXI] Keleiviø mokestis sëkmingai paðalintas, norëdami sugràþinti raðykite /taxi");
    	return 1;
    }
    if(MIN_TAXI_FARE > new_fare || new_fare > MAX_TAXI_FARE)
    	return SendClientMessage(playerid, COLOR_LIGHTRED, "Klaida, nustatyta kaina neleidþiama. Leidþiamos kainos " #MIN_TAXI_FARE " - " #MAX_TAXI_FARE);


    format(string, sizeof(string), "[TAXI] Jûsø taksometro mokestis uþ vienà kilometrà sëkmingai buvo pakeistas á %d$.", new_fare);
    SendClientMessage( playerid, COLOR_YELLOW, string );
    TaxiVehiclePrice[ vehicleid ] = new_fare;
    return 1;
}