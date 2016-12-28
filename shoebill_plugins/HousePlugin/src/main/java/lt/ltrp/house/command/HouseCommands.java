package lt.ltrp.house.command;


import lt.ltrp.LtrpWorld;
import lt.ltrp.constant.Currency;
import lt.ltrp.house.upgrade.constant.HouseUpgradeType;
import lt.ltrp.house.event.HouseBuyEvent;
import lt.ltrp.house.event.PlayerEnterHouseEvent;
import lt.ltrp.house.event.PlayerExitHouseEvent;
import lt.ltrp.modelpreview.SkinModelPreview;
import lt.ltrp.house.object.House;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.05.
 */
public class HouseCommands {



    private EventManager eventManager;

    public HouseCommands(EventManager eventManager) {
        this.eventManager = eventManager;
    }

   /*@BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player != null) {
            if(player.getProperty() == null || !(player.getProperty() instanceof House)) {
                System.out.println("HouseCommands :: beforeChcek. Cmd " + cmd + " returning false");
                return false;
            }
        }

        return true;
    }*/

    @Command
    @CommandHelp("houseHelp")
    public boolean houseHelp(Player p) {
        p.sendMessage(lt.ltrp.data.Color.GREEN, "|__________________NAMO VALDYMO INFORMACIJA__________________|");
        p.sendMessage(lt.ltrp.data.Color.LIGHTGREY,"  /hinv /cutweed /hradio ");
        p.sendMessage(lt.ltrp.data.Color.WHITE,"NAMO BANKAS: /housewithdraw /housedeposit /houseinfo");
        p.sendMessage(lt.ltrp.data.Color.LIGHTGREY,"PATOBULINIMAI: /eat /hradio /hu");
        p.sendMessage(lt.ltrp.data.Color.WHITE,"NUOSAVYBE: /buyhouse /sellhouse");
        p.sendMessage(lt.ltrp.data.Color.LIGHTGREY,"NUOMA: /setrent /rentroom /unrent /tenantry /evict /evictall");
        p.sendMessage(lt.ltrp.data.Color.GREEN, "__________________________________________________________________");
        return true;
    }

    @Command
    public boolean enter(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.getClosest(p.getLocation(), 8f);
        if(house == null)
            return false;
        if(house.isLocked() || house.getExit() == null)
            player.sendErrorMessage("Namas uşrakintas");
        else if(!player.isInAnyVehicle()){
            player.setLocation(house.getExit());
            eventManager.dispatchEvent(new PlayerEnterHouseEvent(house, player));
        }
        return true;
    }

    @Command
    public boolean exit(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.getClosest(p.getLocation(), 8f);
        if(house == null)
            return false;
        if(house.isLocked())
            player.sendErrorMessage("Namas uşrakintas");
        else if(!player.isInAnyVehicle()){
            player.setLocation(house.getEntrance());
            eventManager.dispatchEvent(new PlayerExitHouseEvent(house, player));
        }
        return true;
    }

    @Command
    @CommandHelp("Nuperka namà")
    public boolean buyHouse(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.getClosest(player.getLocation(), 5f);
        if(house == null || house.getOwner() != LtrpPlayer.INVALID_USER_ID) {
            player.sendErrorMessage("Prie jûsø nëra jokio namo arba jis neparduodamas");
        } else if(house.getPrice() > player.getMoney())
            player.sendErrorMessage("Jums neuştenka pinigø ásigyti ğá namà");
        else {
            int price = house.getPrice();
            house.setOwner(player.getUUID());
            player.giveMoney(-price);
            LtrpWorld.get().addMoney(price);
            player.sendMessage("Sëkmingai ásigijote namà uş " + Currency.SYMBOL + price + ".");
            eventManager.dispatchEvent(new HouseBuyEvent(house, null, player));
        }
        return true;
    }

    @Command
    @CommandHelp("Leidşia persirengti drabuşius bûnant namuose")
    public boolean clothes(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house == null)
            return false;
        else {
            SkinModelPreview.create(player, eventManager, (pv, i) -> {
                player.setSkin(i);
                player.sendActionMessage("persirengiia drabuşius");
            }).show();
        }
        return true;
    }


}

