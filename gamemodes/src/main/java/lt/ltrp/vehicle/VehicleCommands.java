package lt.ltrp.vehicle;

import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.VehicleParam;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class VehicleCommands {


    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        if(p.getVehicle() == null) {
            return false;
        }
        return true;
    }


    @Command
    @CommandHelp("Parodo transporto priemonës inventoriø")
    public boolean trunk(LtrpPlayer player) {
        LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 4.0f);
        if(vehicle != null) {
            if(vehicle.getState().getBoot() == VehicleParam.PARAM_ON) {
                vehicle.getInventory().show(player);
                return true;
            } else
                player.sendErrorMessage(vehicle.getModelName() + " uþrakinta. Naudokite /trunko");
        } else
            player.sendErrorMessage("Prie jûsø nëra jokios transporto priemonës");
        return false;
    }


}
