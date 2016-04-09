package lt.ltrp.job.drugdealer;

import lt.ltrp.command.CommandParam;
import lt.ltrp.command.Commands;
import lt.ltrp.data.Color;
import lt.ltrp.item.WeedSeedItem;
import lt.ltrp.item.event.ItemCreateEvent;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.27.
 */
public class DrugDealerCommands extends Commands {


    private DrugDealerJob job;
    private EventManager eventManager;


    public DrugDealerCommands(DrugDealerJob job, EventManager eventManager) {
        this.job = job;
        this.eventManager = eventManager;
    }


    @BeforeCheck
    public boolean beforeCheck(LtrpPlayer player, String cmd, String params) {
        if(player.getJob().equals(job)) {
            return true;
        } else {
            return false;
        }
    }

    @Command
    @CommandHelp("Leidþia ásigyti narkotikø sëklø")
    public boolean buySeeds(LtrpPlayer player, @CommandParam("kiekis")int amount) {
        if(player.getLocation().distance(job.getLocation()) > 8f) {
            player.sendErrorMessage("Èia niekas nieko neparduoda.");
        } else if(amount <= 0) {
            player.sendErrorMessage("Kiekis negali bûti maþesnis nei 1.");
        } else if(player.getMoney() <= amount * job.getSeedPrice()) {
            player.sendErrorMessage("Jums neuþtenka pinigø " + amount + " sëkloms. Jø kaina: " + (amount * job.getSeedPrice()));
        } else if(player.getInventory().isFull()) {
            player.sendErrorMessage("Jûsø inventorius pilnas!");
        } else {
            int price = amount*job.getSeedPrice();
            player.giveMoney(-price);
            WeedSeedItem item = new WeedSeedItem(eventManager);
            item.setAmount(amount);
            player.getInventory().add(item);
            eventManager.dispatchEvent(new ItemCreateEvent(item, player));
            player.sendMessage(Color.WHITE, "Nusipirkote " + amount + " sëklas uþ " + price + ". Gero derliaus!");
            return true;
        }
        return false;
    }
}
