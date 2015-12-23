package lt.ltrp.dialogmenu.admin;

import lt.ltrp.dialogmenu.PlayerDialogMenu;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.13.
 */
public class JobVehicleMenu extends PlayerDialogMenu {

    private boolean isShown;

    public JobVehicleMenu(LtrpPlayer player, EventManager manager) {
        super(player, manager);
    }

    @Override
    public void show() {
        LtrpPlayer player = getPlayer();
        EventManager eventManager = getEventManager();
        isShown = true;
        ListDialog.create(player, eventManager)
                .caption("Serverio automobiliai")
                .buttonOk("Pasirinkti")
                .buttonCancel("Iðeiti")
                .item("- Kurti naujà", item -> {
                    
                })
                .build()
                .show();
    }

    @Override
    public boolean isShown() {
        return isShown;
    }
}

/*
 else if ( dialogid == 45 )
    {
        if ( response == 1 )
        {
            switch ( listitem )
            {
                case 0:
                {
                    ShowPlayerDialog( playerid, 46, DIALOG_STYLE_INPUT,"Automobilio sukûrimas","áraðykite automobilio ID, spalvà , 2 spalvà \nPavyzdys: 562 2 3","Patvirtinti","Atðaukti" );
                    return 1;
                }
                case 1:
                {
                    new car = GetPlayerVehicleID( playerid );
                    if( IsPlayerInAnyVehicle( playerid ) && cInfo[ car ][ cOwner ] == 0 )
                        ShowPlayerDialog( playerid, 47, DIALOG_STYLE_INPUT,"Automobilio frakcija","áraðykite frakcijos ID kad automobilis priklausytu jai\nFrakcijø ID galite rasti per frakcijø meniu.","Patvirtinti","Atðaukti" );
                    return 1;
                }
                case 2:
                {
                    new car = GetPlayerVehicleID( playerid );
                    if( IsPlayerInAnyVehicle( playerid ) && cInfo[ car ][ cOwner ] == 0 )
                        ShowPlayerDialog( playerid, 48, DIALOG_STYLE_INPUT,"Automobilio darbas","áraðykite barbo ID kad automobilis priklausytu jam.","Patvirtinti","Atðaukti" );
                    return 1;
                }
                case 3:
                {
                    new car = GetPlayerVehicleID( playerid );
                    if( IsPlayerInAnyVehicle( playerid ) && cInfo[ car ][ cOwner ] == 0 )
                        ShowPlayerDialog( playerid, 80, DIALOG_STYLE_MSGBOX,"Automobilio spawn","Jeigu jau pastatëte automobilá ten kur reikia, tai patvirtinkite tai.","Patvirtinti","Atðaukti" );
                    return 1;
                }
                case 4:
                {
                    new car = GetPlayerVehicleID( playerid );
                    if( IsPlayerInAnyVehicle( playerid ) && cInfo[ car ][ cOwner ] == 0 )
                        ShowPlayerDialog( playerid, 50, DIALOG_STYLE_INPUT,"Automobilio rangas","áraðykite rango minimalø numerá.","Patvirtinti","Atðaukti" );
                    return 1;
                }
                case 5:
                {
                    new car = GetPlayerVehicleID( playerid );
                    if( IsPlayerInAnyVehicle( playerid ) && cInfo[ car ][ cOwner ] == 0 )
                        ShowPlayerDialog( playerid, 52, DIALOG_STYLE_INPUT,"Automobilio spalva","áraðykite norimas automobilio spalvas\nPavzdþiui: 3 45","Patvirtinti","Atðaukti" );
                    return 1;
                }
                case 6:
                {
                    new car = GetPlayerVehicleID( playerid );
                    if( IsPlayerInAnyVehicle( playerid ) && cInfo[ car ][ cOwner ] == 0 )
                    {
                        format( string, 126, "DELETE FROM scars WHERE sID = %d LIMIT 1;", sVehicles[ car ][ Id ] );
                        mysql_query(DbHandle,  string, false);
                        sVehicles[ car ][ Id     ] = 0;
                        sVehicles[ car ][ Model  ] = 0;
                        sVehicles[ car ][ SpawnX  ] = 0.0;
                        sVehicles[ car ][ SpawnY  ] = 0.0;
                        sVehicles[ car ][ SpawnZ  ] = 0.0;
                        sVehicles[ car ][ SpawnA  ] = 0.0;
                        sVehicles[ car ][ Color1 ] = 0;
                        sVehicles[ car ][ Color2 ] = 0;
                        sVehicles[ car ][ Faction] = 0;
                        sVehicles[ car ][ Job    ] = 0;
                        sVehicles[ car ][ Rang   ] = 0;

                        DestroyVehicle( car );
                        SendClientMessage( playerid, COLOR_WHITE, "Transporto priemonë buvo sëkmingai iðtrinta." );
                        return 1;
                    }
                }
                case 7: // Maðinos inventoriaus perþiûra
                {
                    new car = GetPlayerVehicleID( playerid );
                    if( IsPlayerInAnyVehicle( playerid ) && cInfo[ car ][ cOwner ] != 0 )
                        ShowTrunk( playerid, car );
                }
            }
        }
    }
    else if ( dialogid == 46 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;
            new id,
                model,
                color1,
                color2,
                Float:Spawn_x,
                Float:Spawn_y,
                Float:Spawn_z,
                Float:Spawn_a;
            //  p< >ddd
            //sscanf(string, "k<vehicle>", id )
            sscanf( inputtext, "p< >ddd", model, color1, color2 );
            if ( model > 611 || model < 400 ) return SendClientMessage( playerid, COLOR_LIGHTRED, "Perspëjimas: blogas nurodytas automobilio modelio ID.");
            GetPlayerPos( playerid, Spawn_x, Spawn_y, Spawn_z );
            GetPlayerFacingAngle( playerid, Spawn_a );

            format( string, 256, "INSERT INTO `scars` (sModel,sCar_x,sCar_y,sCar_z,sCar_a,sColor1,sColor2) VALUES (%d,'%f','%f','%f','%f',%d,%d)",
            model, Spawn_x, Spawn_y, Spawn_z, Spawn_a, color1, color2);

            new Cache:result;
            result = mysql_query(DbHandle,  string);

            cache_delete(result);
            format( string, 256, "SELECT sID FROM scars WHERE sModel = %d AND sCar_x = '%f' AND sCar_y = '%f'", model, Spawn_x, Spawn_y );
            result = mysql_query(DbHandle, string);

            if ( cache_get_row_count( ) )
            {


                id = CreateVehicle( model, Spawn_x, Spawn_y, Spawn_z, Spawn_a, color1, color2, -1 );

                cInfo[ id ][ cFuel ] = GetVehicleFuelTank( model );
                sVehicles[ id ][ Id     ] =  cache_get_field_content_int(0, "sID");
                sVehicles[ id ][ Model  ] = model;
                sVehicles[ id ][ SpawnX  ] = Spawn_x;
                sVehicles[ id ][ SpawnY  ] = Spawn_y;
                sVehicles[ id ][ SpawnZ  ] = Spawn_z;
                sVehicles[ id ][ SpawnZ  ] = Spawn_a;
                sVehicles[ id ][ Color1 ] = color1;
                sVehicles[ id ][ Color2 ] = color2;
                format( string, 24, "{000000}TLP - %d", id + 1000 );
                SetVehicleNumberPlate( id, string );
            }
            cache_delete(result);
            return 1;

        }
    }
    else if ( dialogid == 47 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;
            if( IsPlayerInAnyVehicle( playerid ) )
            {
                new car = GetPlayerVehicleID( playerid );
                sVehicles[ car ][ Faction ] = strval( inputtext );
                if ( sVehicles[ car ][ Faction ] == 2 )
                {
                    if( sVehicles[ car ][ Model ] == 598 || sVehicles[ car ][ Model ] == 596 || sVehicles[ car ][ Model ] == 599 || sVehicles[ car ][ Model ] == 490 || sVehicles[ car ][ Model ] == 426 || sVehicles[ car ][ Model ] == 427 )
                    {
                        cInfo[ car ][ cTrunkWeapon ][ 0 ] = 29;
                        cInfo[ car ][ cTrunkAmmo   ][ 0 ] = 300;
                        cInfo[ car ][ cTrunkWeapon ][ 1 ] = 31;
                        cInfo[ car ][ cTrunkAmmo   ][ 1 ] = 300;
                        cInfo[ car ][ cTrunkWeapon ][ 2 ] = 25;
                        cInfo[ car ][ cTrunkAmmo   ][ 2 ] = 50;
                    }
                }
                else if ( sVehicles[ car ][ Faction ] == 3 )
                {
                    if( sVehicles[ car ][ Model ] == 407 || sVehicles[ car ][ Model ] == 544 )
                    {
                        cInfo[ car ][ cTrunkWeapon ][ 0 ] = 9;
                        cInfo[ car ][ cTrunkAmmo   ][ 0 ] = 1;
                        cInfo[ car ][ cTrunkWeapon ][ 1 ] = 42;
                        cInfo[ car ][ cTrunkAmmo   ][ 1 ] = 1000;
                    }
                }
                else
                {
                    cInfo[ car ][ cTrunkWeapon ][ 0 ] = 0;
                    cInfo[ car ][ cTrunkAmmo   ][ 0 ] = 0;
                    cInfo[ car ][ cTrunkWeapon ][ 1 ] = 0;
                    cInfo[ car ][ cTrunkAmmo   ][ 1 ] = 0;
                    cInfo[ car ][ cTrunkWeapon ][ 2 ] = 0;
                    cInfo[ car ][ cTrunkAmmo   ][ 2 ] = 0;
                }
                format( string, 126, "Transporto priemonë buvo priskirta frakcijai, kurios ID: %d ",sVehicles[ car ][ Faction ] );
                SendClientMessage( playerid, GRAD, string );
                SaveSVehicle( car );
            }
        }
    }
    else if ( dialogid == 48 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;
            new car = GetPlayerVehicleID( playerid );
            if( IsPlayerInAnyVehicle( playerid ) && sVehicles[ car ][ Faction ] == 0 )
            {
                sVehicles[ car ][ Job ] = strval( inputtext );
                cInfo[ car ][ cTrunkWeapon ][ 0 ] = 0;
                cInfo[ car ][ cTrunkAmmo   ][ 0 ] = 0;
                cInfo[ car ][ cTrunkWeapon ][ 1 ] = 0;
                cInfo[ car ][ cTrunkAmmo   ][ 1 ] = 0;
                cInfo[ car ][ cTrunkWeapon ][ 2 ] = 0;
                cInfo[ car ][ cTrunkAmmo   ][ 2 ] = 0;
                format( string, 126, "Transporto priemonë buvo priskirta nefrakciniam darbui, kurio ID: %d ",sVehicles[ car ][ Job ] );
                SendClientMessage( playerid, GRAD, string );
                SaveSVehicle( car );
            }
        }
    }
    else if ( dialogid == 50 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;
            new car = GetPlayerVehicleID( playerid );
            if( IsPlayerInAnyVehicle( playerid ) )
            {
                sVehicles[ car ][ Rang ] = strval( inputtext );
                format( string, 126, "Frakcijos transporto priemonës minimalus galimas rangas pakeistas á: %d ",sVehicles[ car ][ Rang ] );
                SendClientMessage( playerid, GRAD, string );
                SaveSVehicle( car );
            }
        }
    }
    else if ( dialogid == 52 )
    {
        if ( response == 1 )
        {
            if ( !inputtext[ 0 ] ) return 1;
            if( IsPlayerInAnyVehicle( playerid ) )
            {
                new car = GetPlayerVehicleID( playerid ),
                    color1,
                    color2;
                sscanf( inputtext, "p< >dd", color1, color2 );
                sVehicles[ car ][ Color1 ] = color1;
                sVehicles[ car ][ Color2 ] = color2;
                ChangeVehicleColor( car, color1, color2 );
                SaveSVehicle( car );
                return 1;
            }
        }
    }


 */
