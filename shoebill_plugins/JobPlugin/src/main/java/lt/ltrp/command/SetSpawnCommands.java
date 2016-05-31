package lt.ltrp.command;

import lt.ltrp.JobPlugin;
import lt.ltrp.SpawnPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.data.PlayerJobData;
import lt.ltrp.data.SpawnData;
import lt.ltrp.object.Faction;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class SetSpawnCommands {


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

}
