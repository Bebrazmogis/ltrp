package lt.ltrp.property;

import lt.ltrp.item.object.InventoryEntity;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.property.object.Property;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2015.12.05.
 */
public class PropertyCommands {


    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player != null) {
            if(player.getProperty() == null) {
                System.out.println("PropertyCommands :: beforeChcek. Cmd " + cmd + " returning true");
                return true;
            }
        }
        return false;
    }


    @Command()
    @CommandHelp("Atveria nekilnojamojo turto inventori�")
    public boolean ninv(LtrpPlayer player) {
        Property property = player.getProperty();
        if(property != null ) {
            if(property.isOwner(player) && property instanceof InventoryEntity) {
                ((InventoryEntity)property).getInventory().show(player);
            } else {
                player.sendErrorMessage("J�s neesate savininkas.");
            }
        }
        return false;
    }

}
