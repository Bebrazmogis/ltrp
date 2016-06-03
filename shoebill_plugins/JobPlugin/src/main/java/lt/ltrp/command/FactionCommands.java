package lt.ltrp.command;

import lt.ltrp.JobController;
import lt.ltrp.JobPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.data.PlayerJobData;
import lt.ltrp.object.Faction;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.Rank;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.util.Map;

/**
 * @author Bebras
 *         2016.03.04.
 */
public class FactionCommands extends Commands {

    private EventManager eventManager;
    private JobPlugin jobPlugin;


    public FactionCommands(EventManager eventManager, JobPlugin jobPlugin) {
        this.eventManager = eventManager;
        this.jobPlugin = jobPlugin;
    }

    /*/ @BeforeCheck
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
    */

    @Command
    @CommandHelp("Iðsiunèia OOC þinutæ á frakcijos chatà")
    public boolean f(Player p, @CommandParameter(name = "Tekstas")String text) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerJobData jobData = JobPlugin.get(JobPlugin.class).getJobData(player);
        // TODO muted?
        if(text == null || jobData == null)
            return false;
        else if(!(jobData.getJob() instanceof Faction))
            player.sendErrorMessage("Ðis chatas galimas tik dirbant frakcijoje.");
        else {
            jobData.getJob().sendMessage(Color.CYAN, String.format("((%s (%s): %s ))", jobData.getJobRank().getName(),
                    player.getName(), text));
        }
        return true;
    }

    @Command
    public boolean fList(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Faction faction = JobController.get().getFaction(player);
        if(faction == null)
            player.sendErrorMessage("Jûs nepriklausote jokiai frakcijai");
        else {
            player.sendMessage(Color.GREEN, "|___________ Frakcijai priklausantys nariai ___________|");
            Map<String, Rank> employees =  JobController.get().getDao().getEmployeeList(faction);
            for(String username : employees.keySet()) {
                player.sendMessage(Color.WHITE, String.format("** %s [%d]", username, employees.get(username).getNumber()));
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Leidþia iðeiði ið frakcijos")
    public boolean leaveFaction(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerJobData jobData = jobPlugin.getJobData(player);
        if(jobData == null || !(jobData.getJob() instanceof Faction))
            player.sendErrorMessage("Jûs neturite darbo.");
        else {
            jobPlugin.setJob(player, null);
            player.removeJobWeapons();
            ((Faction) jobData.getJob()).getLeaders().remove(player.getUUID());
            player.sendMessage(Color.NEWS, "Sëkmingai iðëjote ið frakcijos.");
        }
        return true;
    }



}
