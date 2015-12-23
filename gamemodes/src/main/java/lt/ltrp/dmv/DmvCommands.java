package lt.ltrp.dmv;

import lt.ltrp.command.Commands;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;

/**
 * @author Bebras
 *         2015.12.17.
 */
public class DmvCommands extends Commands {

    /*@BeforeCheck
    public boolean beforeCheck(LtrpPlayer player, String cmd, String params) {

    }*/

    @Command()
    @CommandHelp("Pradeda vairavimo pamokà")
    public boolean takeLesson(LtrpPlayer player) {
        Dmv dmv = null;
        for(Dmv d : DmvManager.getInstance().getDmvs()) {
            if(d.getLocation().distance(player.getLocation()) < 7.0) {
                dmv = d;
                break;
            }
        }

        LtrpVehicle vehicle = player.getVehicle();
        if(dmv == null && vehicle != null) {
            for(Dmv d : DmvManager.getInstance().getDmvs()) {
                if(d.getVehicles().contains(vehicle)) {
                    dmv = d;
                    break;
                }
            }
        }


        if(dmv != null) {
            dmv.startTest(player);
            return true;
        }
        return false;
    }

}
