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
        p.sendMessage(Color.GREEN, "|__________________GARAÞO VALDYMO INFORMACIJA__________________|");
        p.sendMessage(Color.LIGHTGREY,"  /ginv - garaþe laikomi daiktai.");
        p.sendMessage(Color.WHITE,"  /buygarage - naudojamas garaþo pirkimui.");
        p.sendMessage(Color.LIGHTGREY,"  /lock - garaþo uþrakinimas/atrakinimas.");
        p.sendMessage(Color.WHITE,"  /sellgarage [Þaidëjo ID/ Dalis vardo] [SUMA] - parduoti savo garaþà");
        p.sendMessage(Color.GREEN, "__________________________________________________________________");
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

    @Command
    public boolean lock(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Garage garage = Garage.getClosest(p.getLocation(), 8f);
        if(garage == null && player.getProperty() instanceof Garage)
            garage = (Garage) player.getProperty();
        if(garage == null)
            return false;
        if(garage.getOwner() != player.getUUID())
            player.sendErrorMessage("Garaþas jums nepriklauso!");
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
            player.sendErrorMessage("Garaþas uþrakintas");
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
            player.sendErrorMessage("Garaþas uþrakintas");
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
    @CommandHelp("Nuperka garaþà")
    public boolean buyGarage(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Garage garage = Garage.getClosest(p.getLocation(), 5f);
        if(garage == null)
            player.sendErrorMessage("Ðià komandà galite naudoti tik bûdami prie garaþo!");
        else if(garage.getOwner() != LtrpPlayer.INVALID_USER_ID)
            player.sendErrorMessage("Ðis garaþas jau turi savininkà");
        else if(garage.getPrice() > player.getMoney())
            player.sendErrorMessage("Jûs neturite tiek pinigø.");
        else {
            int price = garage.getPrice();
            player.giveMoney(-price);
            LtrpWorld.get().addMoney(price);
            garage.setOwner(player.getUUID());
            player.sendMessage(Color.GARAGE, "Sëkmingai ásigijote garaþà uþ " + price + Currency.SYMBOL);
            eventManager.dispatchEvent(new GarageBuyEvent(garage, null, player));
        }
        return true;
    }

    @Command
    @CommandHelp("Pasiûlo parduoti garaþà kitam þaidëjui")
    public boolean sellGarage(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target,
                              @CommandParameter(name = "Kaina")int price) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Garage garage = Garage.getClosest(p.getLocation(), 5f);
        if(garage == null && player.getProperty() instanceof Garage)
            garage = (Garage) player.getProperty();

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

}
