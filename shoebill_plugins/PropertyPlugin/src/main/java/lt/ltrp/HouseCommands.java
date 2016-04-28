package lt.ltrp;


import lt.ltrp.constant.HouseUpgradeType;
import lt.ltrp.data.Color;
import lt.ltrp.data.SpawnData;
import lt.ltrp.event.player.PlayerSpawnLocationChangeEvent;
import lt.ltrp.event.property.house.HouseLockToggleEvent;
import lt.ltrp.event.property.house.HouseRentEvent;
import lt.ltrp.event.property.house.HouseRentStopEvent;
import lt.ltrp.object.House;
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
        p.sendMessage(lt.ltrp.data.Color.LIGHTGREY,"  /hinv /cuteed /hradio ");
        p.sendMessage(lt.ltrp.data.Color.WHITE,"NAMO BANKAS: /housewithdraw /housedeposit /houseinfo");
        p.sendMessage(lt.ltrp.data.Color.LIGHTGREY,"PATOBULINIMAI: /eat /hradio /hu");
        p.sendMessage(lt.ltrp.data.Color.WHITE,"NUOSAVYBE: /buyhouse /sellhouse");
        p.sendMessage(lt.ltrp.data.Color.LIGHTGREY,"NUOMA: /setrent /rentroom /unrent /tenantry /evict /evictall");
        p.sendMessage(lt.ltrp.data.Color.GREEN, "__________________________________________________________________");
        return true;
    }



    @Command
    @CommandHelp("Leid�ia pavalgyti b�nant namuose")
    public boolean eat(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = null;
        if(player.getProperty() instanceof House)
            house = (House)player.getProperty();

        if(house == null)
            player.sendErrorMessage("�i� komand� galite naudoti tik b�damas name.");
        else if(!house.isUpgradeInstalled(HouseUpgradeType.Refrigerator))
            player.sendErrorMessage("�iame name n�ra k� valgyti...");
        else {
            player.sendActionMessage("paima valgio i� �aldytuvo ir pradeda valgyti.");
            player.setHealth(100f);
        }
        return true;
    }

    @Command
    public boolean rentRoom(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.getClosest(player.getLocation(), 8f);
        if(house == null)
            player.sendErrorMessage("�i� komand� galite naudoti tik b�dami prie nuomuojamo namo ��jimo.");
        else if(house.getOwner() == LtrpPlayer.INVALID_USER_ID || house.getRentPrice() == 0)
            player.sendErrorMessage("Namas n�ra nuomuojamas!");
        else if(house.getRentPrice() > player.getMoney())
            player.sendErrorMessage("Neturite pakankamai pinig� pradiniam nuomos mokes�iui.");
        else {
            player.playSound(1052);
            house.getTenants().add(player.getUUID());
            house.addMoney(house.getRentPrice());
            player.giveMoney(-house.getRentPrice());
            eventManager.dispatchEvent(new HouseRentEvent(house, player));
            SpawnData spawnData = new SpawnData(SpawnData.SpawnType.House, house.getUUID(), SpawnData.DEFAULT.getSkin(), SpawnData.DEFAULT.getWeaponData());
            eventManager.dispatchEvent(new PlayerSpawnLocationChangeEvent(player, spawnData));
            player.sendMessage(Color.HOUSE, "Sveikiname, s�kmingai i�sinuomavote kambar� �iame name. Nusitatykite atsiradimo viet� su komanda /setspawn.");
        }
        return true;
    }

    @Command
    public boolean unrent(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        SpawnData spawnData = player.getSpawnData();
        if(spawnData == null || spawnData.getType() != SpawnData.SpawnType.House)
            player.sendErrorMessage("J�s nesinomuojate namo!");
        else {
            player.playSound(1052);
            House house = House.get(spawnData.getId());
            house.getTenants().remove(player.getUUID());
            eventManager.dispatchEvent(new HouseRentStopEvent(house, player));
            eventManager.dispatchEvent(new PlayerSpawnLocationChangeEvent(player, SpawnData.DEFAULT));
            player.sendMessage(Color.HOUSE, "Sveikiname, s�kmingai atsisak�te dabartinio gyvenamojo namo nuomos. Nuo �iol atsirasite nebe �iame name.");
        }
        return true;
    }


    @Command
    public boolean lock(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.getClosest(p.getLocation(), 8f);
        if(house == null && player.getProperty() instanceof House)
            house = (House) player.getProperty();
        if(house == null)
            return false;
        if(house.getOwner() != player.getUUID())
            player.sendErrorMessage("Namas jums nepriklauso!");
        else {
            house.setLocked(!house.isLocked());
            if(house.isLocked())
                player.sendGameText(8000, 1, "Namas ~r~uzrakintas");
            else
                player.sendGameText(8000, 1, "Namas ~g~atrakintas");
            eventManager.dispatchEvent(new HouseLockToggleEvent(house, player, house.isLocked()));
        }
        return true;
    }

    @Command
    public boolean enter(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.getClosest(p.getLocation(), 8f);
        if(house == null && player.getProperty() instanceof House)
            house = (House) player.getProperty();
        if(house == null)
            return false;
        if(house.isLocked())
            player.sendErrorMessage("Namas u�rakintas");
        else if(!player.isInAnyVehicle()){
            player.setLocation(house.getExit());
        }
        return true;
    }

    @Command
    public boolean exit(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.getClosest(p.getLocation(), 8f);
        if(house == null && player.getProperty() instanceof House)
            house = (House) player.getProperty();
        if(house == null)
            return false;
        if(house.isLocked())
            player.sendErrorMessage("Namas u�rakintas");
        else if(!player.isInAnyVehicle()){
            player.setLocation(house.getEntrance());
        }
        return true;
    }

    // TODO cmd:hu


}

