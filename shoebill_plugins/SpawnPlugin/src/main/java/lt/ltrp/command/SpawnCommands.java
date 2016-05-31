package lt.ltrp.command;

import lt.ltrp.SpawnPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.data.SpawnData;
import lt.ltrp.object.LtrpPlayer;
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

}
