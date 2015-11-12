/*
	* Death.p failas yra dalis LTRP modifikacijos. 
	* Failas atsakingas uþ þaidëjo mirtá(áskaitant komos bûsenà)
	*
	* Þaidëjui mirus pirmà kartà(OnPlayerDeath) nustatomas laikas(ðiuo menu 10 min) jei per já niekas jo neiðgelbëja 
	* arba þaidëjas panaudoja komandà /die, tai uþskaitoma IC mirtimi.
	* Atsijungus likæs laikas iðsaugomas, jei lieka maþiau nei 30 sekundþiø, IC mirtis uþskaitoma automatiðkai. 
	*
	* Kodo autorius: Bebras.
	*
	*
*/

/*

	CREATE TABLE IF NOT EXISTS player_coma
	(
		player_id INT NOT NULL,
		seconds_remaining INT NOT NULL,
		death_date DATETIME NOT NULL,
		pos_x FLOAT NOT NULL,
		pos_y FLOAT NOT NULL,
		pos_z FLOAT NOT NULL,
		PRIMARY KEY(player_id)
	);

	ALTER TABLE player_coma ADD FOREIGN KEY(player_id) REFFERENCES players(id) ON DELETE CASCADE:

*/



#include <YSI\y_hooks>



#define COMA_DURATION_MIN 				10


static bool:IsInComa[MAX_PLAYER char],
	ComaTime[ MAX_PLAYERS ],
	Timer:ComaTimer[MAX_PLAYERS];

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

hook OnPlayerDeath(playerid, killerid, reason)
{
	IsInComa[ playerid ] = true;
	SetComaTimer(playerid, COMA_DURATION_MIN * 60);
	return 1;
}

hook OnPlayerDisconnect(playerid, reason)
{
	if(IsPlayerInComa(playerid))
	{
		stop ComaTimer[ playerid ];

		new Float:x, Float:y, Float:z, query[180];
		GetPlayerPos(playerid, x, y, z);
		mysql_format(DbHandle, query, sizeof(query), "INSERT INTO player_coma (player_id, seconds_remaining, death_date, pos_x, pos_y, pos_z) VALUES(%d, %d, FROM_UNIXTIME(%d), %f, %f, %f)",
			GetPlayerSqlId(playerid),
			ComaTime[ playerid ],
			gettime(),
			x,
			y,
			z);
		mysql_pquery(DbHandle, query);
	}
	return 1;
}

public OnPlayerSpawnSetUp(playerid)
{
	#if defined death_OnPlayerSpawnSetUp
		death_OnPlayerSpawnSetUp(playerid);
	#endif
	new query[60], Cache:result;
	mysql_format(DbHandle, query, sizeof(query), "SELECT * FROM player_coma WHERE player_id = %d", GetPlayerSqlId(playerid));
	result = mysql_query(DbHandle, query);
	if(cache_get_row_count())
	{
		new Float:x, Float:y, Float:z, duration;

		x = cache_get_field_content_float(0, "pos_x");
		y = cache_get_field_content_float(0, "pos_y");
		z = cache_get_field_content_float(0, "pos_z");
		duration = cache_get_field_content_int(0, "seconds_remaining");

		SetComaTimer(playerid, duration);

		mysql_format(DbHandle, query, sizeof(query), "DLETE FROM player_coma WHERE player_id = %d", GetPlayerSqlId(playerid));
		mysql_pquery(DbHandle, query);
	}
	cache_delete(result);
	return 1;
}
#if defined _ALS_OnPlayerSpawnSetUp
	#undef OnPlayerSpawnSetUp
#else 
	#define _ALS_OnPlayerSpawnSetUp
#endif
#defined OnPlayerSpawnSetUp 			death_OnPlayerSpawnSetUp
#if defined death_OnPlayerSpawnSetUp
	forward death_OnPlayerSpawnSetUp(playerid);
#endif



public OnPlayerDie(playerid)
{
	 IsInComa[ playerid ] = false;

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

static SetComaTimer(playerid, seconds)
{
	ComaTimer[playerid] = repeat ComaTimer(playerid);
	return 1;
}



/*
				
				                                                                                                            
				             ,,                                                AW                                           
				MMP""MM""YMM db                                               ,M'MMP""MM""YMM             `7MM              
				P'   MM   `7                                                  MV P'   MM   `7               MM              
				     MM    `7MM  `7MMpMMMb.pMMMb.  .gP"Ya `7Mb,od8 ,pP"Ybd   AW       MM   ,6"Yb.  ,pP"Ybd  MM  ,MP',pP"Ybd 
				     MM      MM    MM    MM    MM ,M'   Yb  MM' "' 8I   `"  ,M'       MM  8)   MM  8I   `"  MM ;Y   8I   `" 
				     MM      MM    MM    MM    MM 8M""""""  MM     `YMMMa.  MV        MM   ,pm9MM  `YMMMa.  MM;Mm   `YMMMa. 
				     MM      MM    MM    MM    MM YM.    ,  MM     L.   I8 AW         MM  8M   MM  L.   I8  MM `Mb. L.   I8 
				   .JMML.  .JMML..JMML  JMML  JMML.`Mbmmd'.JMML.   M9mmmP',M'       .JMML.`Moo9^Yo.M9mmmP'.JMML. YA.M9mmmP' 
				                                                          MV                                                
				                                                         AW                                                 

*/




timer ComaTimer[1000](playerid)
{
	ComaTime[playerid]--;
	if(ComaTime[playerid] == 0)
	{
		OnPlayerDie(playerid);
	}
	stop ComaTimer[ playerid ];
	return 1;
}

forward OnPlayerDie(playerid);



/*
				                                                                                                                          
				                                                 ,...                                      ,,                             
				          mm                   `7MM            .d' ""                               mm     db                             
				          MM                     MM            dM`                                  MM                                    
				,pP"Ybd mmMMmm ,pW"Wq.   ,p6"bo  MM  ,MP'     mMMmm`7MM  `7MM  `7MMpMMMb.  ,p6"bo mmMMmm `7MM  ,pW"Wq.`7MMpMMMb.  ,pP"Ybd 
				8I   `"   MM  6W'   `Wb 6M'  OO  MM ;Y         MM    MM    MM    MM    MM 6M'  OO   MM     MM 6W'   `Wb MM    MM  8I   `" 
				`YMMMa.   MM  8M     M8 8M       MM;Mm         MM    MM    MM    MM    MM 8M        MM     MM 8M     M8 MM    MM  `YMMMa. 
				L.   I8   MM  YA.   ,A9 YM.    , MM `Mb.       MM    MM    MM    MM    MM YM.    ,  MM     MM YA.   ,A9 MM    MM  L.   I8 
				M9mmmP'   `Mbmo`Ybmd9'   YMbmd'.JMML. YA.    .JMML.  `Mbod"YML..JMML  JMML.YMbmd'   `Mbmo.JMML.`Ybmd9'.JMML  JMML.M9mmmP' 
				                                                                                                                          
				                                                                                                                          
*/

stock IsPlayerInComa(playerid)
{
	if(0 < playerid < MAX_PLAYERS)
		return IsInComa[ playerid ];
	return false;
}