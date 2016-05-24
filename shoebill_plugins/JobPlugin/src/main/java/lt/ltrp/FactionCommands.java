package lt.ltrp;

import lt.ltrp.command.Commands;
import lt.ltrp.data.Color;
import lt.ltrp.data.PlayerJobData;
import lt.ltrp.object.Faction;
import lt.ltrp.object.Job;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.Rank;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.util.Map;

/**
 * @author Bebras
 *         2016.03.04.
 */
public class FactionCommands extends Commands {

    private EventManager eventManager;

    public FactionCommands(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerJobData jobData = JobController.get().getJobData(player);
        Job job = jobData.getJob();
        if(job != null && job instanceof Faction) {
            return true;
        } else {
            return false;
        }
    }

    @Command
    public boolean fList(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Faction faction = JobController.get().getFaction(player);
        player.sendMessage(Color.GREEN, "|___________ Frakcijai priklausantys nariai ___________|");
        Map<String, Rank> employees =  JobController.get().getDao().getEmployeeList(faction);
        for(String username : employees.keySet()) {
            player.sendMessage(Color.WHITE, String.format("** %s [%d]", username, employees.get(username).getNumber()));
        }
        return true;
    }



}
