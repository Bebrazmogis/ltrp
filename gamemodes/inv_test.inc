
#include <a_samp>


/*
	CREATE TABLE IF NOT EXISTS player_items (
		id INT NOT NULL AUTO_INCREMENT,
		player_id INT NOT NULL,
		item_id INT NOT NULL,
		amount INT NOT NULL,
		PRIMARY KEY(id),
		INDEX(player_id)
	) ENGINE=INNODB;

	ALTER TABLE player_items ADD FOREIGN KEY(player_id) REFERENCES players(id) ON DELETE CASCADE;
*/


#define MAX_PLAYER_ITEMS 			10
#define DIALOG_PLAYER_INVENTORY 		510544

#define INVALID_ITEM_ID				0


forward OnPlayerUseItem(playerid, itemid);
forward OnPlayerDropItem(playerid, itemid);

enum E_ITEM_DATA {
	Id,
	Name[ MAX_ITEM_NAME ],
	EffectFunction[ 32 ]
};

new static const ItemData[][ E_ITEM_DATA ] = {
	{41, "Telefonas", "OnPlayerCheckPhone"},
	{1, "Cigaretes", "OnPlayerAttemptSmoke"}
};

enum E_PLAYER_ITEM_DATA {
	Id,
	Amount
};
new PlayerItems[ MAX_PLAYERS ][ MAX_PLAYER_ITEMS ][ E_PLAYER_ITEM_DATA ];




stock ShowPlayerInventory(playerid)
{
	new string[];
	for(new i = 0; i < MAX_PLAYER_ITEMS; i++)
	{
		if(PlayerItems[ playerid ][ i ][ Id ] != INVALID_ITEM_ID)
			format(string, sizeof(string),"%s%s\t%d\n",
				string, GetItemName(PlayerItems[ playerid ][ i ][ Id ]),
				PlayerItems[ playerid ][ i ][ Amount ]);
	}
	if(isnull(string))
		return ShowPlayerDialog(playerid, 8812, DIALOG_STYLE_MSGBOX, "kuprinë", "Jûs nieko neturite.", "Gerai", "");
	ShowPlayerDialog(playerid, DIALOG_PLAYER_INVENTORY, DIALOG_STYLE_LIST, "Kuprinë", string, "Pasirinkti", "Iðeiti");
	return 1;
}

public OnDialogResponse(playerid, dialogid, response, listitem, inputtext[])
{
	switch(dialogid)
	{
		case DIALOG_PLAYER_INVENTORY:
		{
			if(!response)
				return 1;

			new index;
			for(new i = 0; i < MAX_PLAYER_ITEMS; i++)
				if(PlayerItems[ playerid ][ i ][ Id ] != INVALID_ITEM_ID)
				{
					if(count == listitem)
					{
						index = i;
						break;
					}
					count++;
				}
			//////////////////
			PlayerUsedItemIndex[ playerid ] = index;
			ShowPlayerDialog(playerid, DIALOG_PLAYER_INVENTORY_ACTIONS, DIALOG_STYLE_LIST, "Veiksmas", "Panaudoti\n...\n...\n...\nIðmesti", "Pasirinkti", "Iðeiti");
			return 1;
		}
		case DIALOG_PLAYER_INVENTORY_ACTIONS:
		{
			if(!response)
				return 1;

			switch(listitem)
			{
				// Use it.
				case 0:
				{
					for(new i = 0; i < sizeof(ItemData); i++)
					{
						if(ItemData[ i ][ Id ] == PlayerItems[ playerid ][ PlayerUsedItemIndex[ playerid ] ][ Id ])
						{
							if(isnull(ItemData[ i ][ EffectFunction]))
							{
								#if defined OnPlayerUseItem
									OnPlayerUseItem(playerid, ItemData[ i ][ Id ]);
								#endif
							}
							else
							{
								CallLocalFunction(ItemData[ i ][ EffectFunction ], "ii", playerid, ItemData[ i ][ Id ]);
							}
							return 1;
						}
					}
					return 1;
				}

				// Ismetimas
				case 4:
				{
					if(!response)
						return 1;
					#if defined OnPlayerDropItem
						OnPlayerDropItem(playerid, ItemData[ playerid ][ PlayerUsedItemIndex[ playerid ] ][ Id ]);
					#endif
					return 1;
				}
			}
		}
	}
}




stock GivePlayerItem(playerid, itemid, amount = 1, sqlupdate = true)
{
	if(itemid == INVALID_ITEM_ID)
		return 0;

	new freeSlot = -1;
	for(new i = 0; i < MAX_PLAYER_ITEMS; i++)
	{
		if(PlayerItems[ playerid ][ i ][ Id ] == itemid)
		{
			PlayerItems[ playerid ][ i ][ Amount ] += amount;
			if(sqlupdate)
				UpdatePlayerItemInfo(playerid, itemid);
			return 1;
		}
		else if(PlayerItems[ playerid ][ i ][ Id ] == INVALID_ITEM_ID)
			freeSlot = i;

	}
	if(freeSlot == -1)
		return 0;

	PlayerItems[ playerid ][ i ][ Id ] = itemid;
	PlayerItems[ playerid ][ i ][ Amount ] = amount;
	InsertPlayerItemInfo(playerid, itemid);
	return 1;
}



static stock UpdatePlayerItemInfo(playerid, itemid)
{
	new query[120];
	format(query, sizeof(query), "UPDATE player_items SET amount = %d WHERE player_id = %d AND item_id = %d",
		GetPlayerItemAmount(playerid, itemid), GetPlayerSqlId(playerid), itemid);
	mysql_query(query);
}


static stock InsertPlayerItemInfo(playerid, itemid)
{
	new query[200];
	mysql_query(query, sizeof(query), "INSERT INTO player_items (player_id, item_id, amount) VALUES(%d, %d,%d) ON DUPLICATE KEY UPDATE amount = VALUES(amount)",
		GetPlayerSqlId(playerid), itemid, GetPlayerItemAmount(playerid, itemid));
	mysql_query(query);
}


public OnPlayerUseItem(playerid, itemid)
{

}
public OnPlayerDropItem(playerid, itemid)
{

}