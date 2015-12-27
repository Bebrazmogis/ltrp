package lt.ltrp.job.drugdealer;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.command.CommandParam;
import lt.ltrp.command.Commands;
import lt.ltrp.data.Color;
import lt.ltrp.item.Item;
import lt.ltrp.item.WeedSeedItem;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;

/**
 * @author Bebras
 *         2015.12.27.
 */
public class DrugDealerCommands extends Commands {

    private DrugDealerJob job;

    public DrugDealerCommands(DrugDealerJob job) {
        this.job = job;
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
        } else if(player.getMoney() <= amount * SEED_PRICE) {
            player.sendErrorMessage("Jums neuþtenka pinigø " + amount + " sëkloms. Jø kaina: " + (amount * SEED_PRICE));
        } else if(player.getInventory().isFull()) {
            player.sendErrorMessage("Jûsø inventorius pilnas!");
        } else {
            int price = amount*SEED_PRICE;
            player.giveMoney(-price);
            WeedSeedItem item = new WeedSeedItem("Marihuanos sëklos");
            item.setAmount(amount);
            player.getInventory().add(item);
            LtrpGamemode.getDao().getItemDao().insert(item, LtrpPlayer.class, player.getUserId());
            player.sendMessage(Color.WHITE, "Nusipirkote " + amount + " sëklas uþ " + price + ". Gero derliaus!");
            return true;
        }
        return false;
    }
}
