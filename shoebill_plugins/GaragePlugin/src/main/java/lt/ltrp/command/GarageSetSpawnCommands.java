package lt.ltrp.command;

import lt.ltrp.SpawnPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.data.SpawnData;
import lt.ltrp.object.Garage;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class GarageSetSpawnCommands {


    @Command(name = "Gara�as")
    public boolean house(Player p) {
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

}
