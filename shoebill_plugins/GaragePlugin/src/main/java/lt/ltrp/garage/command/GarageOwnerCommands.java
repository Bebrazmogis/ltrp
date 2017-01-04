package lt.ltrp.garage.command;

import lt.ltrp.command.Commands;
import lt.ltrp.constant.Currency;
import lt.ltrp.data.BuyGarageOffer;
import lt.ltrp.data.Color;
import lt.ltrp.object.Garage;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.19.
 */
public class GarageOwnerCommands  extends Commands {

    private EventManager eventManager;

    public GarageOwnerCommands(EventManager eventManager) {
        super();
        this.eventManager = eventManager;
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        cmd = cmd.toLowerCase();
        Garage garage = Garage.getClosest(p.getLocation(), 8f);
        if(garage != null) {
            if(garage.isOwner(player)) {
                return true;
            } else
                player.sendErrorMessage("Komanda  /" + cmd + " gali naudoti tik gara�o savininkas.");
        }
        return false;
    }

    @Command
    @CommandHelp("Atidaro gara�o inventori�")
    public boolean gInv(Player p) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        Garage garage = Garage.get(player);
        if(garage == null)
            player.sendErrorMessage("�i� komand� galite naudoti tik b�dami gara�e.");
        else if(!garage.isOwner(player))
            player.sendErrorMessage("�i� komand� gali naudoti tik gara�o savininkas");
        else {
            garage.getInventory().show(player);
        }
        return true;
    }

    @Command
    @CommandHelp("Pasi�lo parduoti gara�� kitam �aid�jui")
    public boolean sellGarage(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                              @CommandParameter(name = "Kaina")int price) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        Garage garage = Garage.getClosest(p.getLocation(), 5f);
        if(garage == null)
            player.sendErrorMessage("�i� komand� galite naudoti tik b�dami prie gara�o arba jo viduje!");
        else if(garage.getOwner() != garage.getOwner())
            player.sendErrorMessage("�is gara�as jums nepriklauso tod�l jo parduoti negalite.");
        else if(price < 0)
            player.sendErrorMessage("Kaina turi b�ti teigiama!");
        else if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra");
        else if(player.getDistanceToPlayer(target) > 10f)
            player.sendErrorMessage("�aid�jas yra per toli kad gal�tume jam k� nors parduoti");
        else if(target.containsOffer(BuyGarageOffer.class))
            player.sendErrorMessage("�iam �aid�jui jau ka�kas si�lo pirkti gara��.");
        else {
            BuyGarageOffer offer = new BuyGarageOffer(target, player, garage, price, eventManager);
            target.getOffers().add(offer);
            player.sendMessage(Color.GARAGE, "Pasi�lymas pirkti j�s� gara�� u� " + price + Currency.SYMBOL + " i�si�stas " + target.getName() + " laukite atsakymo");
            target.sendMessage(Color.GARAGE, "�aid�jas  " + player.getName() + " jums si�lo pirkti jo gara�� u� " + price + Currency.SYMBOL + ", naudokite /accept garage pirkimui");
        }
        return true;
    }

    @Command
    @CommandHelp("U�rakina/atrakina gara�o duris")
    public boolean lock(Player p) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        Location loc = player.getLocation();
        Garage garage = Garage.getClosest(p.getLocation(), 8f);
        if(garage == null)
            return false;
        if(garage.getOwner() != player.getUUID())
            player.sendErrorMessage("Gara�as jums nepriklauso!");
        else if(garage.getEntrance().distance(loc) > 3f && garage.getExit() != null && garage.getExit().distance(loc) > 3f)
            player.sendErrorMessage("J�s per toli nuo dur�!");
        else {
            garage.setLocked(!garage.isLocked());
            player.sendActionMessage("�ki�a rakt� � dur� spyn�, nestipriai j� pasuka");
            if(garage.isLocked()) {
                garage.sendActionMessage("durys u�sirakina");
                player.sendGameText(8000, 1, "Garazas ~r~uzrakintas");
            }
            else {
                garage.sendActionMessage("durys atsirakina");
                player.sendGameText(8000, 1, "Garazas ~g~atrakintas");
            }
        }
        return true;
    }
}
