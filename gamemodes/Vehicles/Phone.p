
#include <YSI\y_hooks>

#define MAX_VEHICLE_PHONES 				2




static VehiclePhones[ MAX_VEHICLES ][ MAX_VEHICLE_PHONES ][ E_PRIVATE_PHONE_DATA ];



hook OnVehicleDeath(vehicleid)
{
	for(new i = 0; i < MAX_VEHICLE_PHONES; i++)
	{
		VehiclePhones[ vehicleid ][ i ][ Number ] = 0;
		VehiclePhones[ vehicleid ][ i ][ Online ] = false;
	}
	return 1;
}

/*
LoadVehiclePhones(sqlid, vehicleid)
{
	new query[90], Cache:result;
	mysql_format(DbHandle, query, sizeof(query), "SELECT * FROM phones WHERE location_type = %d AND location_id = %d", _:VehicleTrunk, sqlid);
	result = mysql_query(DbHandle, query);

	for(new i = 0; i < cache_get_row_count(); i++)
	{
		VehiclePhones[ vehicleid ][ i ][ Number ] = cache_get_field_content_int(i, "number");
		VehiclePhones[ vehicleid ][ i ][ Online ] = (cache_get_field_content_int(i, "online")) ? (true) : (false);
	}
	cache_delete(result);
	return 1;
}

*/


GetVehiclePhonenumberVehicleId(phonenumber)
{
	if(!phonenumber)
		return INVALID_VEHICLE_ID;

	for(new i = 1; i < MAX_VEHICLES; i++)
		if(IsValidVehicle(i))
		{
			for(new j = 0; j < MAX_VEHICLE_PHONES; j++)
				if(VehiclePhones[ i ][ j ][ Number ] == phonenumber)
					return i;
		}
	return INVALID_VEHICLE_ID;
}

IsPhoneInAnyVehicle(phonenumber)
{
	for(new i = 1; i < MAX_VEHICLES; i++)
		if(IsValidVehicle(i))
			for(new j = 0; j < MAX_VEHICLE_PHONES; j++)
				if(VehiclePhones[ i ][ j ][ Number ] == phonenumber)
					return true;
	return false;
}

IsVehiclePhonenumberOnline(phonenumber)
{
	for(new i = 1; i < MAX_VEHICLES; i++)
		if(IsValidVehicle(i))
			for(new j = 0; j < MAX_VEHICLE_PHONES; j++)
				if(VehiclePhones[ i ][ j ][ Number ] == phonenumber)
					return VehiclePhones[ i ][ j ][ Online ];
	return false;
}
