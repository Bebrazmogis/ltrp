#if defined ANTI_CHEAT_PLUGIN_INCLUDED

#else

#define ANTI_CHEAT_PLUGIN_INCLUDED
#pragma library "AntiCheat"

#define AC_ACTION_ONMONEY_REPORT   	(0)
#define AC_ACTION_ONMONEY_RESET    	(1)

#define AC_DETECTED_DISABLED_WEAPON (0)
#define AC_DETECTED_JETPACK 		(1)
#define AC_DETECTED_MONEY   		(2)
#define AC_DETECTED_SPAWNED_WEAPON  (3)
#define AC_DETECTED_SPEEDHACK		(4)

forward AC_OnCheatDetected(playerid,cheat_type,ac_extra);

native ACget_UpdateDelay();
native ACset_UpdateDelay(ac_ticks);

native ACget_JetpackCheck();
native ACset_JetpackCheck(ac_enable = 1);
native ACget_JetpackAllowed(playerid = 0);
native ACset_JetpackAllowed(ac_allowed = 1,playerid = (-1));

native ACget_WeaponCheck();
native ACset_WeaponCheck(ac_enable = 1);
native ACset_AllowedWeapon(weaponid,ac_allowed = 1,playerid = (-1));
native ACget_AllowedWeapon(weaponid,playerid = 0);
native ACgive_PlayerWeapon(playerid, weaponid, ammo);
native ACreset_PlayerWeapons(playerid);
native ACset_SpawnWeaponCheck(enable = 1);
native ACget_SpawnWeaponCheck();

native ACset_MoneyCheck(ac_enable = 1);
native ACget_MoneyCheck();
native ACget_ActionOnMoneyCheat();
native ACset_ActionOnMoneyCheat(ac_action = 0);
native ACget_PlayerMoney(playerid);
native ACgive_PlayerMoney(playerid,amount);
native ACset_PlayerMoney(playerid,amount);
native ACreset_PlayerMoney(playerid);
native ACfix_PlayerMoney(playerid);
native ACGet_PlayerWeapon(playerid);
native ACGet_WeaponByServer(playerid,weapon);
native RemovePlayerWeapon(playerid,weapon);

native AC_AddPlayerClass(modelid, Float:spawn_x, Float:spawn_y, Float:spawn_z, Float:z_angle, weapon1, weapon1_ammo, weapon2, weapon2_ammo, weapon3, weapon3_ammo);
native AC_AddPlayerClassEx(teamid, modelid, Float:spawn_x, Float:spawn_y, Float:spawn_z, Float:z_angle, weapon1, weapon1_ammo, weapon2, weapon2_ammo, weapon3, weapon3_ammo);

#define GivePlayerWeapon ACgive_PlayerWeapon
#define GetPlayerWeapon ACGet_PlayerWeapon
#define ResetPlayerWeapons ACreset_PlayerWeapons
#define GetPlayerMoney ACget_PlayerMoney
#define GivePlayerMoney ACgive_PlayerMoney
#define ResetPlayerMoney ACreset_PlayerMoney
#define AddPlayerClass AC_AddPlayerClass
#define AddPlayerClassEx AC_AddPlayerClassEx

#endif
