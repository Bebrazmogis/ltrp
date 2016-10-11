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
        player.sendMessage(Color.NEWS, "Vieta sëkmingai nustatyta, dabar prisijungæ á serverá kità kartà atsirasite  Los Santos mieste.");
        return true;
    }

    @Command(name = "Frakcija")
    public boolean faction(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerJobData jobData = JobPlugin.get(JobPlugin.class).getJobData(player);
        if(jobData == null || !(jobData instanceof Faction))
            player.sendErrorMessage("Jûs nepriklausote jokiai frakcijai, todël negalite atsirasti jos bûstinëje.");
        else {
            SpawnPlugin spawnPlugin = SpawnPlugin.get(SpawnPlugin.class);
            SpawnData spawnData = spawnPlugin.getSpawnData(player);
            spawnData.setType(SpawnData.SpawnType.Faction);
            spawnData.setId(((Faction) jobData).getUUID());
            spawnPlugin.setSpawnData(player, spawnData);
            player.sendMessage(Color.NEWS, "Atsiradimo vieta pakeista, dabar atsirasite " + ((Faction) jobData).getName() + " bûstinëje.");
        }
        return true;
    }

    @Command(name = "Verslas")
    public boolean business(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Business business = Business.getClosest(player.getLocation(), 5f);
        if(business == null)
            player.sendErrorMessage("Ðià komandà galite naudoti tik bûdami prie savo verslo.");
        if(!business.isOwner(player))
            player.sendErrorMessage("Ðis verslas jums nepriklauso.");
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


    @Command(name = "Garaþas")
    public boolean garage(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Garage garage = Garage.getClosest(player, 5f);
        if(garage == null)
            player.sendErrorMessage("Ðià komandà galite naudoti tik bûdamas prie garaþo arba jo viduje.");
        else if(!garage.isOwner(player))
            player.sendErrorMessage("Ðis garaþas jums nepriklauso.");
        else {
            SpawnPlugin plugin = SpawnPlugin.get(SpawnPlugin.class);
            SpawnData spawnData = plugin.getSpawnData(player);
            spawnData.setType(SpawnData.SpawnType.Garage);
            spawnData.setId(garage.getUUID());
            plugin.setSpawnData(player, spawnData);
            player.sendMessage(Color.HOUSE, "Atsiradimo vieta pakeista. Dabar atsirasite ðiame garaþe.");
        }
        return true;
    }

    @Command(name = "Namas")
    public boolean house(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.getClosestHouse(player, 5f);
        if(house == null)
            player.sendErrorMessage("Ðià komandà galite naudoti tik bûdamas prie namo arba jo viduje.");
        else if(!house.isOwner(player) && !house.getTenants().contains(player.getUUID()))
            player.sendErrorMessage("Ðis namas jums nepriklauso ir jûs jo nesinuomojate.");
        else {
            SpawnPlugin plugin = SpawnPlugin.get(SpawnPlugin.class);
            SpawnData spawnData = plugin.getSpawnData(player);
            spawnData.setType(SpawnData.SpawnType.House);
            spawnData.setId(house.getUUID());
            plugin.setSpawnData(player, spawnData);
            player.sendMessage(Color.HOUSE, "Atsiradimo vieta pakeista. Dabar atsirasite ðiame name.");
        }
        return true;
    }
}
