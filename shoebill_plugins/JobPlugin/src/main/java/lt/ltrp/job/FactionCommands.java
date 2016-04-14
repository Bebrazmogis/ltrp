package lt.ltrp.job;

import lt.ltrp.command.Commands;
import lt.ltrp.dao.JobDao;
import lt.ltrp.data.Color;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.util.Map;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.03.04.
 */
public class FactionCommands extends Commands {

    private JobManager jobManager;
    private EventManager eventManager;
    private JobDao jobDao;

    public FactionCommands(EventManager eventManager, JobManager jobManager, JobDao dao) {
        this.eventManager = eventManager;
        this.jobManager = jobManager;
        this.jobDao = dao;
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Job job = player.getJob();
        if(job != null && job instanceof Faction) {
            return true;
        } else {
            return false;
        }
    }

    @Command
    public boolean fList(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Faction faction = (Faction)player.getJob();
        player.sendMessage(Color.GREEN, "|___________ Frakcijai priklausantys nariai ___________|");
        Map<String, Rank> employees =  jobDao.getEmployeeList(faction);
        for(String username : employees.keySet()) {
            player.sendMessage(Color.WHITE, String.format("** %s [%d]", username, employees.get(username).getNumber()));
        }
        return true;
    }



}
