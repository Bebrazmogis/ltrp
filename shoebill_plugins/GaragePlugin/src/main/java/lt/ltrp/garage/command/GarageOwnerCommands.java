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
                player.sendErrorMessage("Komanda  /" + cmd + " gali naudoti tik garaþo savininkas.");
        }
        return false;
    }

    @Command
    @CommandHelp("Atidaro garaþo inventoriø")
    public boolean gInv(Player p) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        Garage garage = Garage.get(player);
        if(garage == null)
            player.sendErrorMessage("Ðià komandà galite naudoti tik bûdami garaþe.");
        else if(!garage.isOwner(player))
            player.sendErrorMessage("Ðià komandà gali naudoti tik garaþo savininkas");
        else {
            garage.getInventory().show(player);
        }
        return true;
    }

    @Command
    @CommandHelp("Pasiûlo parduoti garaþà kitam þaidëjui")
    public boolean sellGarage(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target,
                              @CommandParameter(name = "Kaina")int price) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        Garage garage = Garage.getClosest(p.getLocation(), 5f);
        if(garage == null)
            player.sendErrorMessage("Ðià komandà galite naudoti tik bûdami prie garaþo arba jo viduje!");
        else if(garage.getOwner() != garage.getOwner())
            player.sendErrorMessage("Ðis garaþas jums nepriklauso todël jo parduoti negalite.");
        else if(price < 0)
            player.sendErrorMessage("Kaina turi bûti teigiama!");
        else if(target == null)
            player.sendErrorMessage("Tokio þaidëjo nëra");
        else if(player.getDistanceToPlayer(target) > 10f)
            player.sendErrorMessage("Þaidëjas yra per toli kad galëtume jam kà nors parduoti");
        else if(target.containsOffer(BuyGarageOffer.class))
            player.sendErrorMessage("Ðiam þaidëjui jau kaþkas siûlo pirkti garaþà.");
        else {
            BuyGarageOffer offer = new BuyGarageOffer(target, player, garage, price, eventManager);
            target.getOffers().add(offer);
            player.sendMessage(Color.GARAGE, "Pasiûlymas pirkti jûsø garaþà uþ " + price + Currency.SYMBOL + " iðsiøstas " + target.getName() + " laukite atsakymo");
            target.sendMessage(Color.GARAGE, "Þaidëjas  " + player.getName() + " jums siûlo pirkti jo garaþà uþ " + price + Currency.SYMBOL + ", naudokite /accept garage pirkimui");
        }
        return true;
    }

    @Command
    @CommandHelp("Uþrakina/atrakina garaþo duris")
    public boolean lock(Player p) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        Location loc = player.getLocation();
        Garage garage = Garage.getClosest(p.getLocation(), 8f);
        if(garage == null)
            return false;
        if(garage.getOwner() != player.getUUID())
            player.sendErrorMessage("Garaþas jums nepriklauso!");
        else if(garage.getEntrance().distance(loc) > 3f && garage.getExit() != null && garage.getExit().distance(loc) > 3f)
            player.sendErrorMessage("Jûs per toli nuo durø!");
        else {
            garage.setLocked(!garage.isLocked());
            player.sendActionMessage("ákiða raktà á durø spynà, nestipriai já pasuka");
            if(garage.isLocked()) {
                garage.sendActionMessage("durys uþsirakina");
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
