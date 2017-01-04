package lt.ltrp.command;

import lt.ltrp.JobPlugin;
import lt.ltrp.PoliceJobPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.player.job.data.PlayerJobData;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.PoliceFaction;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.06.03.
 */
public class PoliceLeaderCommands extends Commands {

    private PoliceJobPlugin policePlugin;

    public PoliceLeaderCommands(PoliceJobPlugin policePlugin) {
        this.policePlugin = policePlugin;
    }

    @BeforeCheck
    public boolean bc(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerJobData jobData = player.getJobData();
        if(jobData != null) {
            if(jobData.getJob().equals(policePlugin) && policePlugin.getPoliceFaction().getLeaders().contains(player))
                return true;
        }
        return false;
    }

    @Command
    public boolean autTazer(Player p) {
        return autTaser(p);
    }


    @Command
    @CommandHelp("Iðjungia/ájungia tazerio naudojimà visiems darbuotojams")
    public boolean autTaser(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PoliceFaction faction = policePlugin.getPoliceFaction();
        PlayerJobData playerJobData = player.getJobData();
        faction.setTaserEnabled(!faction.isTaserEnabled());
        if(faction.isTaserEnabled())
            faction.sendMessage(Color.LIGHTRED, playerJobData.getRank().getName() + " ájungë tazerio naudojimà.");
        else
            faction.sendMessage(Color.LIGHTRED, playerJobData.getRank().getName() + " iðjungë tazerio naudojimà visiems darbuotjams.");
        return true;
    }

}
