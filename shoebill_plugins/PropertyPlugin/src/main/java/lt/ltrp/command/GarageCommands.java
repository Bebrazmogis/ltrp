package lt.ltrp.command;


import lt.ltrp.LtrpWorld;
import lt.ltrp.constant.Currency;
import lt.ltrp.data.BuyGarageOffer;
import lt.ltrp.data.Color;
import lt.ltrp.event.property.garage.GarageBuyEvent;
import lt.ltrp.event.property.garage.GarageLockToggleEvent;
import lt.ltrp.object.Garage;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import lt.ltrp.object.Property;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
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
            Property property = player.getProperty();
            if(property != null && property instanceof Garage || Garage.getClosest(player.getLocation(), 5f) != null) {
                return true;
            }
        }
        return false;
    }

    @Command
    @CommandHelp("garagehelp")
    public boolean garageHelp(Player p) {
        p.sendMessage(Color.GREEN, "|__________________GARA�O VALDYMO INFORMACIJA__________________|");
        p.sendMessage(Color.LIGHTGREY,"  /ginv - gara�e laikomi daiktai.");
        p.sendMessage(Color.WHITE,"  /buygarage - naudojamas gara�o pirkimui.");
        p.sendMessage(Color.LIGHTGREY,"  /lock - gara�o u�rakinimas/atrakinimas.");
        p.sendMessage(Color.WHITE,"  /sellgarage [�aid�jo ID/ Dalis vardo] [SUMA] - parduoti savo gara��");
        p.sendMessage(Color.GREEN, "__________________________________________________________________");
        return true;
    }

    @Command
    @CommandHelp("Atidaro namo inventori�")
    public boolean gInv(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Garage garage = (Garage)player.getProperty();
        if(garage.getInventory() == null) {

        } else if(!garage.isOwner(player)) {
            player.sendErrorMessage("�is gara�as jums nepriklauso");
        } else {
            garage.getInventory().show(player);
        }
        return true;
    }

    @Command
    public boolean lock(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Garage garage = Garage.getClosest(p.getLocation(), 8f);
        if(garage == null && player.getProperty() instanceof Garage)
            garage = (Garage) player.getProperty();
        if(garage == null)
            return false;
        if(garage.getOwner() != player.getUUID())
            player.sendErrorMessage("Gara�as jums nepriklauso!");
        else {
            garage.setLocked(!garage.isLocked());
            if(garage.isLocked())
                player.sendGameText(8000, 1, "~r~uzrakinta");
            else
                player.sendGameText(8000, 1, "~g~atrakinta");
            eventManager.dispatchEvent(new GarageLockToggleEvent(garage, player, garage.isLocked()));
        }
        return true;
    }

    @Command
    public boolean enter(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Garage garage = Garage.getClosest(p.getLocation(), 8f);
        if(garage == null && player.getProperty() instanceof Garage)
            garage = (Garage) player.getProperty();
        if(garage == null)
            return false;
        if(garage.isLocked())
            player.sendErrorMessage("Gara�as u�rakintas");
        else {
            if(player.isInAnyVehicle()) {
                LtrpVehicle vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
                garage.setVehicle(vehicle);
                vehicle.setLocation(garage.getVehicleExit());
                vehicle.setInterior(garage.getVehicleExit().getInteriorId());
            } else {
                player.setLocation(garage.getExit());
            }
        }
        return true;
    }

    @Command
    public boolean exit(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Garage garage = Garage.getClosest(p.getLocation(), 8f);
        if(garage == null && player.getProperty() instanceof Garage)
            garage = (Garage) player.getProperty();
        if(garage == null)
            return false;
        if(garage.isLocked())
            player.sendErrorMessage("Gara�as u�rakintas");
        else {
            if(player.isInAnyVehicle()) {
                LtrpVehicle vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
                garage.setVehicle(null);
                vehicle.setLocation(garage.getVehicleEntrance());
                vehicle.setInterior(garage.getVehicleEntrance().getInteriorId());
            } else {
                player.setLocation(garage.getEntrance());
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Nuperka gara��")
    public boolean buyGarage(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Garage garage = Garage.getClosest(p.getLocation(), 5f);
        if(garage == null)
            player.sendErrorMessage("�i� komand� galite naudoti tik b�dami prie gara�o!");
        else if(garage.getOwner() != LtrpPlayer.INVALID_USER_ID)
            player.sendErrorMessage("�is gara�as jau turi savinink�");
        else if(garage.getPrice() > player.getMoney())
            player.sendErrorMessage("J�s neturite tiek pinig�.");
        else {
            int price = garage.getPrice();
            player.giveMoney(-price);
            LtrpWorld.get().addMoney(price);
            garage.setOwner(player.getUUID());
            player.sendMessage(Color.GARAGE, "S�kmingai �sigijote gara�� u� " + price + Currency.SYMBOL);
            eventManager.dispatchEvent(new GarageBuyEvent(garage, null, player));
        }
        return true;
    }

    @Command
    @CommandHelp("Pasi�lo parduoti gara�� kitam �aid�jui")
    public boolean sellGarage(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                              @CommandParameter(name = "Kaina")int price) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Garage garage = Garage.getClosest(p.getLocation(), 5f);
        if(garage == null && player.getProperty() instanceof Garage)
            garage = (Garage) player.getProperty();

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

}
