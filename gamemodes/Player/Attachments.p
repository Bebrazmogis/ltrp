



/*

CREATE TABLE IF NOT EXISTS player_attachments (
	player_id INT NOT NULL,
	`index` TINYINT NOT NULL,
	model_id INT NOT NULL,
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
	PRIMARY KEY(player_id, `index`)
) ENGINE=INNODB DEFAULT CHARSET=cp1257 COLLATE=cp1257_bin;

ALTER TABLE player_attachments ADD FOREIGN KEY(player_id) REFERENCES players(id) ON DELETE CASCADE;
*/



stock AddPlayerAttachedObject(playerid, index, modelid, bone, Float:offx = 0.0, Float:offy = 0.0, Float:offz = 0.0, Float:rotx = 0.0,
	Float:roty = 0.0, Float:rotz = 0.0, Float:scalex = 1.0, Float:scaley = 1.0, Float:scalez = 1.0, color1 = 0, color2 = 0)
{
	new query[1024];
	mysql_format(DbHandle, query, sizeof(query), "INSERT INTO player_attachments (player_id, `index`, model_id, bone, off_x, off_y, off_z, rot_x, rot_y, rot_z, scale_x, scale_y, scale_z, color1, color2) VALUES \
		(%d, %d, %d, %d, %f, %f, %f, %f, %f, %f, %f, %f, %f, %d, %d)",
		GetPlayerSqlId(playerid), index, modelid, bone, offx, offy, offz, rotx, roty, rotz, scalex, scaley, scalez, color1, color2);

	mysql_format(DbHandle, query, sizeof(query),"%s ON DUPLICATE KEY UPDATE model_id = VALUES(model_id), off_x = VALUES(off_x), off_y = VALUES(off_y), off_z = VALUES(off_z), \
		rot_x = VALUES(rot_x), rot_y = VALUES(rot_y), rot_z = VALUES(rot_z), scale_x = VALUES(scale_x), scale_y = VALUES(scale_y), scale_z = VALUES(scale_z), color1 = VALUES(color1), color2 = VALUES(color2)",query);
	
	return mysql_pquery(DbHandle, query);
}

stock DeletePlayerAttachedObject(playerid, index)
{
	new query[100];
	mysql_format(DbHandle, query, sizeof(query), "DELETE FROM player_attachments WHERE player_id = %d AND `index` = %d",
		GetPlayerSqlId(playerid), index);
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
	for(new i = 0; i < cache_get_row_count(); i++)
		SetPlayerAttachedObject(playerid, 
				cache_get_field_content_int(i, "index"),
				cache_get_field_content_int(i, "model_id"),
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
	return 1;
}