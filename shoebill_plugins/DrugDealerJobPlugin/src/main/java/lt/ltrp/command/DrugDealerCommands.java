package lt.ltrp.command;

import lt.ltrp.job.JobController;
import lt.ltrp.data.Color;
import lt.ltrp.player.job.data.PlayerJobData;
import lt.ltrp.event.item.ItemCreateEvent;
import lt.ltrp.object.DrugDealerJob;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.WeedSeedItem;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.object.Player;
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
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player.getJobData() != null && player.getJobData().getJob().equals(job)) {
            return true;
        } else {
            return false;
        }
    }

    @Command
    @CommandHelp("Leid�ia �sigyti narkotik� s�kl�")
    public boolean buySeeds(Player p, @CommandParameter(name = "kiekis")int amount) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player.getLocation().distance(job.getLocation()) > 8f) {
            player.sendErrorMessage("�ia niekas nieko neparduoda.");
        } else if(amount <= 0) {
            player.sendErrorMessage("Kiekis negali b�ti ma�esnis nei 1.");
        } else if(player.getMoney() <= amount * job.getSeedPrice()) {
            player.sendErrorMessage("Jums neu�tenka pinig� " + amount + " s�kloms. J� kaina: " + (amount * job.getSeedPrice()));
        } else if(player.getInventory().isFull()) {
            player.sendErrorMessage("J�s� inventorius pilnas!");
        } else {
            int price = amount*job.getSeedPrice();
            player.giveMoney(-price);
            WeedSeedItem item = WeedSeedItem.create(eventManager);
            item.setAmount(amount);
            player.getInventory().add(item);
            eventManager.dispatchEvent(new ItemCreateEvent(item, player));
            player.sendMessage(Color.WHITE, "Nusipirkote " + amount + " s�klas u� " + price + ". Gero derliaus!");
            return true;
        }
        return false;
    }
}
