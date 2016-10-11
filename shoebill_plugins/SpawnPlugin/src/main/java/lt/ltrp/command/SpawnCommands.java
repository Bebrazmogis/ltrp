package lt.ltrp.command;

import lt.ltrp.JobPlugin;
import lt.ltrp.SpawnPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.data.PlayerJobData;
import lt.ltrp.data.SpawnData;
import lt.ltrp.object.*;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class SpawnCommands {


    @Command(name = "LosSantos")
    @CommandHelp()
    public boolean losSantos(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        SpawnData spawnData = SpawnPlugin.get(SpawnPlugin.class).getSpawnData(player);
        spawnData.setType(SpawnData.SpawnType.Default);
        spawnData.setId(0);
        SpawnPlugin.get(SpawnPlugin.class).setSpawnData(player, spawnData);
        player.sendMessage(Color.NEWS, "Vieta s�kmingai nustatyta, dabar prisijung� � server� kit� kart� atsirasite  Los Santos mieste.");
        return true;
    }

    @Command(name = "Frakcija")
    public boolean faction(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerJobData jobData = JobPlugin.get(JobPlugin.class).getJobData(player);
        if(jobData == null || !(jobData instanceof Faction))
            player.sendErrorMessage("J�s nepriklausote jokiai frakcijai, tod�l negalite atsirasti jos b�stin�je.");
        else {
            SpawnPlugin spawnPlugin = SpawnPlugin.get(SpawnPlugin.class);
            SpawnData spawnData = spawnPlugin.getSpawnData(player);
            spawnData.setType(SpawnData.SpawnType.Faction);
            spawnData.setId(((Faction) jobData).getUUID());
            spawnPlugin.setSpawnData(player, spawnData);
            player.sendMessage(Color.NEWS, "Atsiradimo vieta pakeista, dabar atsirasite " + ((Faction) jobData).getName() + " b�stin�je.");
        }
        return true;
    }

    @Command(name = "Verslas")
    public boolean business(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Business business = Business.getClosest(player.getLocation(), 5f);
        if(business == null)
            player.sendErrorMessage("�i� komand� galite naudoti tik b�dami prie savo verslo.");
        if(!business.isOwner(player))
            player.sendErrorMessage("�is verslas jums nepriklauso.");
        else {
            SpawnPlugin plugin = SpawnPlugin.get(SpawnPlugin.class);
            SpawnData spawnData = plugin.getSpawnData(player);
            spawnData.setType(SpawnData.SpawnType.Business);
            spawnData.setId(business.getUUID());
            plugin.setSpawnData(player ,spawnData);
            player.sendMessage(Color.BUSINESS, "Atsiradimo vieta pakeista, dabar atsirasite prie verslo " + business.getName());
        }
        return true;
    }


    @Command(name = "Gara�as")
    public boolean garage(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Garage garage = Garage.getClosest(player, 5f);
        if(garage == null)
            player.sendErrorMessage("�i� komand� galite naudoti tik b�damas prie gara�o arba jo viduje.");
        else if(!garage.isOwner(player))
            player.sendErrorMessage("�is gara�as jums nepriklauso.");
        else {
            SpawnPlugin plugin = SpawnPlugin.get(SpawnPlugin.class);
            SpawnData spawnData = plugin.getSpawnData(player);
            spawnData.setType(SpawnData.SpawnType.Garage);
            spawnData.setId(garage.getUUID());
            plugin.setSpawnData(player, spawnData);
            player.sendMessage(Color.HOUSE, "Atsiradimo vieta pakeista. Dabar atsirasite �iame gara�e.");
        }
        return true;
    }

    @Command(name = "Namas")
    public boolean house(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.getClosestHouse(player, 5f);
        if(house == null)
            player.sendErrorMessage("�i� komand� galite naudoti tik b�damas prie namo arba jo viduje.");
        else if(!house.isOwner(player) && !house.getTenants().contains(player.getUUID()))
            player.sendErrorMessage("�is namas jums nepriklauso ir j�s jo nesinuomojate.");
        else {
            SpawnPlugin plugin = SpawnPlugin.get(SpawnPlugin.class);
            SpawnData spawnData = plugin.getSpawnData(player);
            spawnData.setType(SpawnData.SpawnType.House);
            spawnData.setId(house.getUUID());
            plugin.setSpawnData(player, spawnData);
            player.sendMessage(Color.HOUSE, "Atsiradimo vieta pakeista. Dabar atsirasite �iame name.");
        }
        return true;
    }
}
