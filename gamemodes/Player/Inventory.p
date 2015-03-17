
#include <YSI\y_items>

/*

	CREATE TABLE IF NOT EXISTS player_items (
		id INT AUTO_INCREMENT NOT NULL,
		player_id INT NOT NULL,
		item_id SMALLINT NOT NULL,
		amount SMALLINT UNSIGNED NOT NULL,
		extra INT NOT NULL DEFAULT '0',
		PRIMARY KEY(player_id, item_id)
	) ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;

*/


#define MAX_PLAYER_ITEMS 				8

enum E_PLAYER_INVENTORY_DATA 
{
	Id, 
	ItemId,
	Amount,
	Extra, // Labai ávairûs dalykai gali bûti èia laikomi...  PVz þuvies krepðy þuvys, cigareèiø pakelyje cigareèiø skaièius.
};

static PlayerItems[ MAX_PLAYERS ][ MAX_PLAYER_ITEMS ][ E_PLAYER_INVENTORY_DATA ],
	bool:IsPlayerInventoryLoaded[ MAX_PLAYERS ];



forward OnPlayerItemLoad(playerid);


/*
			                                                   
			                       ,,        ,,    ,,          
			`7MM"""Mq.            *MM      `7MM    db          
			  MM   `MM.            MM        MM                
			  MM   ,M9 `7MM  `7MM  MM,dMMb.  MM  `7MM  ,p6"bo  
			  MMmmdM9    MM    MM  MM    `Mb MM    MM 6M'  OO  
			  MM         MM    MM  MM     M8 MM    MM 8M       
			  MM         MM    MM  MM.   ,M9 MM    MM YM.    , 
			.JMML.       `Mbod"YML.P^YbmdP'.JMML..JMML.YMbmd'  
			                                                   
			                                                    


			                                                 ,,                             
			`7MM"""YMM                                mm     db                             
			  MM    `7                                MM                                    
			  MM   d `7MM  `7MM  `7MMpMMMb.  ,p6"bo mmMMmm `7MM  ,pW"Wq.`7MMpMMMb.  ,pP"Ybd 
			  MM""MM   MM    MM    MM    MM 6M'  OO   MM     MM 6W'   `Wb MM    MM  8I   `" 
			  MM   Y   MM    MM    MM    MM 8M        MM     MM 8M     M8 MM    MM  `YMMMa. 
			  MM       MM    MM    MM    MM YM.    ,  MM     MM YA.   ,A9 MM    MM  L.   I8 
			.JMML.     `Mbod"YML..JMML  JMML.YMbmd'   `Mbmo.JMML.`Ybmd9'.JMML  JMML.M9mmmP' 
			                                                                                
			                                                                                
*/

hook OnPlayerConnect(playerid)
{
	new query[120];
	GetPlayerName(playerid, query, sizeof(query));
	mysql_format(DbHandle, query, sizeof(query), "SELECT * FROM player_items WHERE player_id = (SELECT id FROM players WHERE name = '%e')", query);
	mysql_pquery(DbHandle, query, "OnPlayerItemLoad", "i", playerid);
}


hook OnPlayerDisconnect(playerid, reason)
{
	if(IsPlayerInventoryLoaded[ playerid ])
	{
		IsPlayerInventoryLoaded[ playerid ] = false;

		static EmptyPlayerItems[ E_PLAYER_INVENTORY_DATA ];
		for(new i = 0; i < MAX_PLAYER_ITEMS; i++)
			PlayerItems[ playerid ][ i ] = EmptyPlayerItems;
	}
}

public OnPlayerItemLoad(playerid)
{
	for(new i = 0; i < cache_get_row_count(); i++)
	{
		PlayerItems[ playerid ][ i ][ Id ] = cache_get_field_content_int(i, "id");
		PlayerItems[ playerid ][ i ][ ItemId ] = cache_get_field_content_int(i, "item_id");
		PlayerItems[ playerid ][ i ][ Amount ] = cache_get_field_content_int(i, "amount");
		PlayerItems[ playerid ][ i ][ Extra ] = cache_get_field_content_int(i, "extra");
	}
	IsPlayerInventoryLoaded[ playerid ] = true;
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

stock GivePlayerItem(playerid, itemid, amount = 1)
{
	new query[130], freeindex = -1;

	for(new i = 0; i < MAX_PLAYER_ITEMS; i++)
	{
		if(!PlayerItems[ playerid ][ i ][ Id ])
		{
			if(freeindex == -1)
				freeindex = i;
			continue;
		}

		if(PlayerItems[ playerid ][ i ][ ItemId ] == itemid)
		{
			PlayerItems[ playerid ][ i ][ Amount ] += amount;

			if(amount <= 0)
			{
				PlayerItems[ playerid ][ i ][ Id ] = 0;
				mysql_format(DbHandle, query, sizeof(query), "DELETE FROM player_items WHERE id = %d LIMIT 1", PlayerItems[ playerid ][ i ][ Id ]);
			}
			else
			{
				mysql_format(DbHandle, query, sizeof(query), "UPDATE player_items SET amount = %d WHERE id = %d", 
					PlayerItems[ playerid ][ i ][ Amount ],
					PlayerItems[ playerid ][ i ][ Amount ]);
			}
			mysql_pquery(DbHandle, query);
			return 1;
		}
	}
	// Nebetelpa daiktai
	if(freeindex == -1)
		return 0;

	PlayerItems[ playerid ][ freeindex ][ ItemId ] = itemid;
	PlayerItems[ playerid ][ freeindex ][ Amount ] = amount;
	PlayerItems[ playerid ][ freeindex ][ Extra ] = 0;

	mysql_format(DbHandle, query, sizeof(query), "INSERT INTO player_items (player_id, item_id, amount) VALUES (%d, %d, %d)",
		GetPlayerSqlId(playerid),
		itemid,
		amount);
	new Cache:result = mysql_query(DbHandle, query);
	PlayerItems[ playerid ][ freeindex ][ Id ] = cache_insert_id();
	cache_delete(result);
	return 1;
}




stock IsItemInPlayerInventory(playerid, itemid)
{
	for(new i = 0; i < MAX_PLAYER_ITEMS; i++)
		if(PlayerItems[ playerid ][ i ][ Id ])
			if(PlayerItems[ playerid ][ i ][ ItemId ] == itemid)
				return true;
	return false;
}

stock GetPlayerItemAmount(playerid, itemid)
{
	for(new i = 0; i < MAX_PLAYER_ITEMS; i++)
		if(PlayerItems[ playerid ][ i ][ Id ])
			if(PlayerItems[ playerid ][ i ][ ItemId ] == itemid)
				return PlayerItems[ playerid ][ i ][ Amount ];
	return 0;
}





