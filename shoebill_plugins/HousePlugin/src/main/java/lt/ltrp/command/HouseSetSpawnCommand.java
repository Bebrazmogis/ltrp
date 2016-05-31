package lt.ltrp.command;

import lt.ltrp.SpawnPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.data.SpawnData;
import lt.ltrp.object.House;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class HouseSetSpawnCommand {

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
