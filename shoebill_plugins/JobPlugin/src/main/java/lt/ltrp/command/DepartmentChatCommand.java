package lt.ltrp.command;

import lt.ltrp.JobPlugin;
import lt.ltrp.constant.ItemType;
import lt.ltrp.data.Color;
import lt.ltrp.data.PlayerJobData;
import lt.ltrp.object.Faction;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.object.Player;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.06.03.
 */
public class DepartmentChatCommand extends Commands {


    private static final Collection<Faction> FACTIONS = new ArrayList<>();

    private Faction faction;

    public DepartmentChatCommand(Faction faction) {
        this.faction = faction;
        FACTIONS.add(faction);
    }

    @BeforeCheck
    public boolean bc(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerJobData jobData = JobPlugin.get(JobPlugin.class).getJobData(player);
        return jobData != null && jobData.getJob().equals(faction);
    }

    @Command
    @CommandHelp("Iðsiunèia þinutæ per tarp-dapartamentinæ racijà")
    public boolean d(Player p, @CommandParameter(name = "Tekstas")String text) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerJobData jobData = JobPlugin.get(JobPlugin.class).getJobData(player);
        if(text == null)
            return false;
        if(!player.getInventory().containsType(ItemType.Radio))
            player.sendErrorMessage("Jûs neturite racijos!");
        else {
            FACTIONS.forEach(f -> f.sendMessage(Color.NAVY, String.format("|TARPDEPARTAMENTINË RACIJA| %s[%s] praneða: %s",
                    player.getCharName(), jobData.getJobRank().getName(), text)));
        }
        return true;
    }

    @Override
    protected void finalize() throws Throwable {
        FACTIONS.remove(faction);
        super.finalize();
    }
}
