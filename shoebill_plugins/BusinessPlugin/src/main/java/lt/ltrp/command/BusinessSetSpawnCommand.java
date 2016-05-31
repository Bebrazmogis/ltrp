package lt.ltrp.command;

import lt.ltrp.SpawnPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.data.SpawnData;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class BusinessSetSpawnCommand {


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

}
