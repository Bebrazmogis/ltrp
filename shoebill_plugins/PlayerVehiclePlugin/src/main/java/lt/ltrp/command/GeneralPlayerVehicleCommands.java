package lt.ltrp.command;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PlayerVehicle;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.06.06.
 *
 *         Basically commands NOT IN /v group
 */
public class GeneralPlayerVehicleCommands extends Commands {

    public boolean lock(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player.isInAnyVehicle()) {
            PlayerVehicle vehicle = PlayerVehicle.getByVehicle(player.getVehicle());
            if(vehicle == null)
                vehicle = PlayerVehicle.getClosest(player, 10f);
            if(vehicle != null && vehicle.getOwnerId() == player.getUUID()) {
                vehicle.setLocked(!vehicle.isLocked());
                if(vehicle.isLocked())
                    player.sendInfoText("~w~AUTOMOBILIS ~r~UZRAKINTAS", 1000);
                else
                    player.sendInfoText("~w~AUTOMOBILIS ~g~ATRAKINTAS", 1000);
                player.playSound(1052);
                return true;
            }
        }
        return false;
    }

}
