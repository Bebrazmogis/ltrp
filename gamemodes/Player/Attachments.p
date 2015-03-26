



/*

CREATE TABLE IF NOT EXISTS player_attachments (
	player_id INT NOT NULL,
	itemid INT NOT NULL,
	bone TINYINT NOT NULL,
	off_x FLOAT NOT NULL,
	off_y FLOAT NOT NULL,
	off_z FLOAT NOT NULL,
	rot_x FLOAT NOT NULL,
	rot_y FLOAT NOT NULL,
	rot_z FLOAT NOT NULL,
	scale_x FLOAT NOT NULL,
	scale_y FLOAT NOT NULL,
	scale_z FLOAT NOT NULL,
	color1 TINYINT UNSIGNED NOT NULL,
	color2 TINYINT UNSIGNED NOT NULL,
	PRIMARY KEY(player_id, itemid)
) ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;

ALTER TABLE player_attachments ADD FOREIGN KEY(player_id) REFERENCES players(id) ON DELETE CASCADE;
*/


new PlayerWornItems[ MAX_PLAYERS ][ 10 ];         // Pastaba. Tik akiniai, kepurës, ðalmai ir pnð èia laikomi. 





stock AddPlayerAttachedItem(playerid, itemid, bone, Float:offx = 0.0, Float:offy = 0.0, Float:offz = 0.0, Float:rotx = 0.0,
	Float:roty = 0.0, Float:rotz = 0.0, Float:scalex = 1.0, Float:scaley = 1.0, Float:scalez = 1.0, color1 = 0, color2 = 0)
{
	SetPlayerAttachedObject(playerid, GetAttachedItemSlot(itemid), GetItemObjectModel(itemid), bone, offx, offy, offz, rotx, roty, rotz, scalex, scaley, scalez, color1, color2);
	PlayerWornItems[ playerid ][ GetAttachedItemSlot(itemid) ] = itemid;

	new query[1024];
	mysql_format(DbHandle, query, sizeof(query), "INSERT INTO player_attachments (player_id, itemid, bone, off_x, off_y, off_z, rot_x, rot_y, rot_z, scale_x, scale_y, scale_z, color1, color2) VALUES \
		(%d, %d, %d, %f, %f, %f, %f, %f, %f, %f, %f, %f, %d, %d)",
		GetPlayerSqlId(playerid), itemid, bone, offx, offy, offz, rotx, roty, rotz, scalex, scaley, scalez, color1, color2);

	mysql_format(DbHandle, query, sizeof(query),"%s ON DUPLICATE KEY UPDATE off_x = VALUES(off_x), off_y = VALUES(off_y), off_z = VALUES(off_z), \
		rot_x = VALUES(rot_x), rot_y = VALUES(rot_y), rot_z = VALUES(rot_z), scale_x = VALUES(scale_x), scale_y = VALUES(scale_y), scale_z = VALUES(scale_z), color1 = VALUES(color1), color2 = VALUES(color2)",query);
	
	return mysql_pquery(DbHandle, query);
}

stock DeletePlayerAttachedItem(playerid, itemid)
{
	new query[100];
	RemovePlayerAttachedObject(playerid, GetAttachedItemSlot(itemid));
	PlayerWornItems[ playerid ][ GetAttachedItemSlot(itemid) ] = -1;

	mysql_format(DbHandle, query, sizeof(query), "DELETE FROM player_attachments WHERE player_id = %d AND itemid = %d",
		GetPlayerSqlId(playerid), itemid);
	return mysql_pquery(DbHandle, query);
}


public OnPlayerFirstSpawn(playerid)
{
	#if defined attachments_OnPlayerFirstSpawn
		attachments_OnPlayerFirstSpawn(playerid);
	#endif

	new query[70];
	mysql_format(DbHandle, query, sizeof(query), "SELECT * FROM player_attachments WHERE player_id = %d", GetPlayerSqlId(playerid));
	mysql_pquery(DbHandle, query, "OnPlayerAttachmentLoad", "i", playerid);
	return 1;
}
#if defined _ALS_OnPlayerFirstSpawn
	#undef OnPlayerFirstSpawn
#else 
	#define _ALS_OnPlayerFirstSpawn
#endif
#define OnPlayerFirstSpawn 				attachments_OnPlayerFirstSpawn
#if defined attachments_OnPlayerFirstSpawn
	forward attachments_OnPlayerFirstSpawn(playerid);
#endif



forward OnPlayerAttachmentLoad(playerid);
public OnPlayerAttachmentLoad(playerid)
{
	new itemid, modelid, index;
	for(new i = 0; i < cache_get_row_count(); i++)
	{
		itemid = cache_get_field_content_int(i, "item_id");
		modelid = GetItemObjectModel(itemid);
		index = GetAttachedItemSlot(itemid);
		if(index == -1)
		{
			printf("ERROR. Attachments.p : OnPlayerAttachmentLoad(%s). Index is -1. Itemid:%d", GetName(playerid), itemid);
		}
		SetPlayerAttachedObject(playerid, 
			index,
			modelid,
			cache_get_field_content_int(i, "bone"),
			cache_get_field_content_float(i, "off_x"),
			cache_get_field_content_float(i, "off_y"),
			cache_get_field_content_float(i, "off_z"),
			cache_get_field_content_float(i, "rot_x"),
			cache_get_field_content_float(i, "rot_y"),
			cache_get_field_content_float(i, "rot_z"),
			cache_get_field_content_float(i, "scale_x"),
			cache_get_field_content_float(i, "scale_y"),
			cache_get_field_content_float(i, "scale_z"),
			cache_get_field_content_int(i, "color1"),
			cache_get_field_content_int(i, "color2"));
		PlayerWornItems[ playerid ][ index ] = itemid;
	}
	return 1;
}





stock GetAttachedItemSlot(itemid)
{
	switch(itemid)
	{
		case ITEM_WatchType1,
			ITEM_WatchType2,
			ITEM_WatchType6,
			ITEM_WatchType4:
				return 6;
		case ITEM_Bandana10,
			ITEM_Bandana11,  
			ITEM_Bandana12, 
			ITEM_Bandana13, 
			ITEM_Bandana14,  
			ITEM_Bandana15, 
			ITEM_Bandana16,  
			ITEM_Bandana17,
			ITEM_Bandana18, 
			ITEM_Bandana19,
			ITEM_EyePatch1:
				return 1;
		case ITEM_GlassesType1,
			ITEM_GlassesType2,
			ITEM_GlassesType3,
			ITEM_GlassesType4,
			ITEM_GlassesType7,
			ITEM_GlassesType10,
			ITEM_GlassesType13,
			ITEM_GlassesType14,
			ITEM_GlassesType15 ,
			ITEM_GlassesType16,
			ITEM_GlassesType17,
			ITEM_GlassesType18 ,
			ITEM_GlassesType19,
			ITEM_GlassesType20,
			ITEM_GlassesType21,
			ITEM_GlassesType22,
			ITEM_GlassesType23,
			ITEM_GlassesType24 ,
			ITEM_GlassesType25,
			ITEM_GlassesType26,
			ITEM_GlassesType27 ,
			ITEM_GlassesType28,
			ITEM_PoliceGlasses2,
			ITEM_PoliceGlasses3:
				return 2;
		case ITEM_HAIR1,
			ITEM_HAIR2,
			ITEM_Bandana2,  
			ITEM_Bandana4,
			ITEM_Bandana5   ,
			ITEM_Bandana6,   
			ITEM_Bandana7,
			ITEM_Bandana8, 
			ITEM_Bandana9,
			ITEM_Beret2,
			ITEM_Beret3,
			ITEM_Beret4,
			ITEM_Beret1,
			ITEM_Beret5,
			ITEM_CapBack3,    
			ITEM_CapBack4,   
			ITEM_CapBack5,    
			ITEM_CapBack7,    
			ITEM_CapBack8,    
			ITEM_CapBack9,    
			ITEM_CapBack10,
			ITEM_CapBack11,
			ITEM_CapBack12,   
			ITEM_CapBack13,
			ITEM_CapBack14,  
			ITEM_CapBack15,  
			ITEM_CapBack16,   
			ITEM_CapBack17,  
			ITEM_CapBack18,
			ITEM_CapBack19, 
			ITEM_CapBack20,
			ITEM_CapBack21,
			ITEM_SkullyCap1,
			ITEM_SkullyCap2,
			ITEM_HatMan1,
			ITEM_HatMan2,
			ITEM_SantaHat1,
			ITEM_SantaHat2,
			ITEM_HoodyHat3,
			ITEM_CowboyHat2, 
			ITEM_CowboyHat3,
			ITEM_CowboyHat4,
			ITEM_CowboyHat5,
			ITEM_tophat01,
			ITEM_HatBowler6,
			ITEM_pilotHat01,
			ITEM_HatBowler1, 
			ITEM_HatBowler2,
			ITEM_HatBowler3,
			ITEM_SillyHelmet2,
			ITEM_SillyHelmet3,
			ITEM_PlainHelmet1,
			ITEM_MotorcycleHelmet4,
			ITEM_MotorcycleHelmet5,
			ITEM_MotorcycleHelmet6,
			ITEM_MotorcycleHelmet7,
			ITEM_MotorcycleHelmet8,
			ITEM_MotorcycleHelmet9,
			ITEM_HockeyMask1,
			ITEM_MASK,
			ITEM_MaskZorro1:
					return 0;
		case ITEM_KREPSYS, ITEM_LAGAMINAS, ITEM_ROD:
			return 4;

		case ITEM_KUPRINE:
			return 6;
	}
	return -1;
}


stock IsPlayerWearingItem(playerid, itemid)
{
	if(GetAttachedItemSlot(itemid) == -1)
		return false;

	if(PlayerWornItems[ playerid ][ GetAttachedItemSlot(itemid) ] == itemid)
		return true;
	else 
		return false;
}