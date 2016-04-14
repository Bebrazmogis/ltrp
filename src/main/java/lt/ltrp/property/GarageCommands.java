package lt.ltrp.property;


import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.property.object.Garage;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.05.
 */
public class GarageCommands {

    private EventManager eventManager;

    public GarageCommands(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player != null) {
            if(player.getProperty() == null || !(player.getProperty() instanceof Garage)) {
                System.out.println("GarageCommands :: beforeChcek. Cmd " + cmd + " returning false");
                return false;
            }
        }

        return true;
    }

    @Command
    @CommandHelp("Atidaro namo inventoriø")
    public boolean gInv(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Garage garage = (Garage)player.getProperty();
        if(garage.getInventory() == null) {

        } else if(!garage.isOwner(player)) {
            player.sendErrorMessage("Ðis garaþas jums nepriklauso");
        } else {
            garage.getInventory().show(player);
        }
        return true;
    }

}
