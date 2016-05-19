package lt.ltrp.command;


import lt.ltrp.LtrpWorld;
import lt.ltrp.constant.Currency;
import lt.ltrp.data.Color;
import lt.ltrp.event.property.garage.GarageBuyEvent;
import lt.ltrp.event.property.garage.PlayerEnterGarageEvent;
import lt.ltrp.event.property.garage.PlayerExitGarageEvent;
import lt.ltrp.object.Garage;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
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

    /*@BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player != null) {
            Property property = player.getProperty();
            if(property != null && property instanceof Garage || Garage.getClosest(player.getLocation(), 5f) != null) {
                return true;
            }
        }
        return false;
    }*/

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
    public boolean enter(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Garage garage = Garage.getClosest(p.getLocation(), 8f);
        if(garage == null)
            return false;
        if(garage.isLocked())
            player.sendErrorMessage("Garaþas uþrakintas");
        else if(garage.getExit() == null)
            player.sendErrorMessage("Garaþas dar neárengtas!");
        else if(garage.getVehicle() != null)
            player.sendErrorMessage("Garaþe jau stovi maðina, antra netilps!");
        else {
            LtrpVehicle vehicle = null;
            if(!player.isInAnyVehicle()) {
                player.setLocation(garage.getExit());
            } else {
                vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
                garage.setVehicle(vehicle);
                vehicle.setLocation(garage.getVehicleExit());
            }
            eventManager.dispatchEvent(new PlayerEnterGarageEvent(garage, player, vehicle));
        }
        return true;
    }


    @Command
    public boolean exit(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Garage garage = Garage.getClosest(p.getLocation(), 8f);
        if(garage == null)
            return false;
        if(garage.isLocked())
            player.sendErrorMessage("Garaþas uþrakintas");
        else {
            LtrpVehicle vehicle = null;
            if(player.isInAnyVehicle()) {
                vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
                garage.setVehicle(null);
                vehicle.setLocation(garage.getVehicleEntrance());
                //vehicle.setInterior(garage.getVehicleEntrance().getInteriorId());
            } else {
                player.setLocation(garage.getEntrance());
            }
            eventManager.dispatchEvent(new PlayerExitGarageEvent(garage, player, vehicle));
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


}
