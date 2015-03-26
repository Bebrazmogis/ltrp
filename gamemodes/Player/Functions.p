





stock FindPlayerSqlIdServerID(sqlid)
{
	foreach(new i : Player)
		if(GetPlayerSqlId(i) == sqlid)
			return i;
	return INVALID_PLAYER_ID;
}

stock GetSqlIdName(sqlid)
{
    new name[MAX_PLAYER_NAME];
    foreach(Player, i)
    {
        if(pInfo[ i ][ pMySQLID ] == sqlid)
        {
            GetPlayerName(i, name,sizeof(name));
            return name;
        } 
    }
    new query[60];
    format(query,sizeof(query),"SELECT Name FROM `players` WHERE id = %d", sqlid);
    new Cache:result = mysql_query(DbHandle, query);
    if(cache_get_row_count())
        cache_get_field_content(0, "Name", name);
    cache_delete(result);
    return name;
}