package lt.ltrp.command;


import lt.ltrp.LtrpWorld;
import lt.ltrp.constant.Currency;
import lt.ltrp.constant.HouseUpgradeType;
import lt.ltrp.data.Color;
import lt.ltrp.data.SpawnData;
import lt.ltrp.event.player.PlayerSpawnLocationChangeEvent;
import lt.ltrp.event.property.house.*;
import lt.ltrp.object.House;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.data.Location;
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
    @CommandHelp("Leidþia pavalgyti bûnant namuose")
    public boolean eat(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house == null)
            player.sendErrorMessage("Ðià komandà galite naudoti tik bûdamas name.");
        else if(!house.isUpgradeInstalled(HouseUpgradeType.Refrigerator))
            player.sendErrorMessage("Ðiame name nëra kà valgyti...");
        else {
            player.sendActionMessage("paima valgio ið ðaldytuvo ir pradeda valgyti.");
            player.setHealth(100f);
        }
        return true;
    }

    @Command
    public boolean rentRoom(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.getClosest(player.getLocation(), 8f);
        if(house == null)
            player.sendErrorMessage("Ðià komandà galite naudoti tik bûdami prie nuomuojamo namo áëjimo.");
        else if(house.getOwner() == LtrpPlayer.INVALID_USER_ID || house.getRentPrice() == 0)
            player.sendErrorMessage("Namas nëra nuomuojamas!");
        else if(house.getRentPrice() > player.getMoney())
            player.sendErrorMessage("Neturite pakankamai pinigø pradiniam nuomos mokesèiui.");
        else {
            player.playSound(1052);
            house.getTenants().add(player.getUUID());
            house.addMoney(house.getRentPrice());
            player.giveMoney(-house.getRentPrice());
            eventManager.dispatchEvent(new HouseRentEvent(house, player));
            SpawnData spawnData = new SpawnData(SpawnData.SpawnType.House, house.getUUID(), SpawnData.DEFAULT.getSkin(), SpawnData.DEFAULT.getWeaponData());
            eventManager.dispatchEvent(new PlayerSpawnLocationChangeEvent(player, spawnData));
            player.sendMessage(Color.HOUSE, "Sveikiname, sëkmingai iðsinuomavote kambará ðiame name. Nusitatykite atsiradimo vietà su komanda /setspawn.");
        }
        return true;
    }

    @Command
    public boolean unrent(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        SpawnData spawnData = player.getSpawnData();
        if(spawnData == null || spawnData.getType() != SpawnData.SpawnType.House)
            player.sendErrorMessage("Jûs nesinomuojate namo!");
        else {
            player.playSound(1052);
            House house = House.get(spawnData.getId());
            house.getTenants().remove(player.getUUID());
            eventManager.dispatchEvent(new HouseRentStopEvent(house, player));
            eventManager.dispatchEvent(new PlayerSpawnLocationChangeEvent(player, SpawnData.DEFAULT));
            player.sendMessage(Color.HOUSE, "Sveikiname, sëkmingai atsisakëte dabartinio gyvenamojo namo nuomos. Nuo ðiol atsirasite nebe ðiame name.");
        }
        return true;
    }


    @Command
    public boolean lock(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Location loc = player.getLocation();
        House house = House.getClosest(p.getLocation(), 8f);
        if(house == null)
            return false;
        if(house.getOwner() != player.getUUID())
            player.sendErrorMessage("Namas jums nepriklauso!");
        else if(house.getEntrance().distance(loc) > 3f && house.getExit() != null && house.getExit().distance(loc) > 3f)
            player.sendErrorMessage("Jûs per toli nuo durø!");
        else {
            house.setLocked(!house.isLocked());
            player.sendActionMessage("ákiða raktà á durø spynà, nestipriai já pasuka");
            if(house.isLocked()) {
                house.sendActionMessage("durys uþsirakina");
                player.sendGameText(8000, 1, "Namas ~r~uzrakintas");
            }
            else {
                house.sendActionMessage("durys atsirakina");
                player.sendGameText(8000, 1, "Namas ~g~atrakintas");
            }
            eventManager.dispatchEvent(new HouseLockToggleEvent(house, player, house.isLocked()));
        }
        return true;
    }

    @Command
    public boolean enter(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.getClosest(p.getLocation(), 8f);
        if(house == null)
            return false;
        if(house.isLocked() || house.getExit() == null)
            player.sendErrorMessage("Namas uþrakintas");
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
            player.sendErrorMessage("Namas uþrakintas");
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
            player.sendErrorMessage("Jums neuþtenka pinigø ásigyti ðá namà");
        else {
            int price = house.getPrice();
            house.setOwner(player.getUUID());
            player.giveMoney(-price);
            LtrpWorld.get().addMoney(price);
            player.sendMessage("Sëkmingai ásigijote namà uþ " + Currency.SYMBOL + price + ".");
            eventManager.dispatchEvent(new HouseBuyEvent(house, null, player));
        }
        return true;
    }


}

