package lt.ltrp.command;

import lt.ltrp.JobPlugin;
import lt.ltrp.PoliceJobPlugin;
import lt.ltrp.constant.SwatType;
import lt.ltrp.data.Color;
import lt.ltrp.player.job.data.PlayerJobData;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.04.
 */
public class SwatCommands extends Commands{

    private PoliceJobPlugin policePlugin;
    private EventManager eventManager;

    public SwatCommands(PoliceJobPlugin policePlugin, EventManager eventManager) {
        this.policePlugin = policePlugin;
        this.eventManager = eventManager;
    }

    @BeforeCheck
    public boolean bc(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerJobData job = player.getJobData();
        return job != null && job.getJob().equals(policePlugin.getPoliceFaction()) && policePlugin.isSwat(player);
    }

    @Command
    @CommandHelp("Iðsiunèia OOC þinutæ á frakcijos chatà")
    public boolean f(Player p, @CommandParameter(name = "Tekstas")String text) {
        LtrpPlayer player = LtrpPlayer.get(p);
        SwatType swatType = policePlugin.getSwatType(player);
        if(text == null)
            return false;
        else if(!policePlugin.getPoliceFaction().isChatEnabled())
            player.sendErrorMessage("Frakcijos pokalbiai yra iðjungti.");
        else {
            PlayerJobData jobData = player.getJobData();
            jobData.getJob().sendMessage(Color.CYAN, String.format("((%s[%s] (%s): %s ))", jobData.getRank().getName(),
                    swatType.name(),
                    player.getName(), text));
        }
        return true;
    }
}
