package lt.ltrp.command;

import lt.ltrp.VehicleFuelPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.VehicleParam;

/**
 * @author Bebras
 *         2016.06.06.
 */
public class FuelStationCommands extends Commands {

    private VehicleFuelPlugin fuelPlugin;

    public FuelStationCommands(VehicleFuelPlugin fuelPlugin) {
        this.fuelPlugin = fuelPlugin;
    }

    @Command
    @CommandHelp("Pradeda kuro pylimà degalinëje")
    public boolean fill(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
        if(vehicle == null)
            player.sendErrorMessage("Ðià komandà naudoti galite tik bûdamas transporto priemonëje.");
        else if(!fuelPlugin.isInGasStation(player))
            player.sendErrorMessage("Ðià komandà  galite naudoti tik bûdamas degalinëje.");
        else if(vehicle.getState().getEngine() == VehicleParam.PARAM_ON)
            player.sendErrorMessage("Pilti kuro su uþkurtu varikliu negalite!");
        else if(fuelPlugin.isInFillUp(player))
            player.sendErrorMessage("Jûs jau pilate kurà, sustoti galite su /stopfill");
        else {
            fuelPlugin.startFillUp(player, vehicle);
            //player.sendActionMessage("");
        }
        return true;
    }


    @Command
    @CommandHelp("Sustabdo kuro pylimà")
    public boolean stopFill(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
        if(vehicle == null)
            player.sendErrorMessage("Ðià komandà naudoti galite tik bûdamas transporto priemonëje.");
        else if(!fuelPlugin.isInGasStation(player))
            player.sendErrorMessage("Ðià komandà  galite naudoti tik bûdamas degalinëje.");
        else if(!fuelPlugin.isInFillUp(player))
            player.sendErrorMessage("Jûs neesate pradëjæs pilti kuro.");
        else {
            player.sendMessage(Color.NEWS, "Pylimas baigtas.");
            fuelPlugin.endFillUp(player);
        }
        return true;
    }

}
