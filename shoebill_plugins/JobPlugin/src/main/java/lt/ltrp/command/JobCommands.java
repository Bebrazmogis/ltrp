package lt.ltrp.command;

import lt.ltrp.JobPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.data.PlayerJobData;
import lt.ltrp.object.Faction;
import lt.ltrp.object.JobGate;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.object.Player;

import java.util.Optional;

/**
 * @author Bebras
 *         2016.03.04.
 */
public class JobCommands extends Commands {


    private JobPlugin jobPlugin;

    public JobCommands(JobPlugin plugin) {
        this.jobPlugin = plugin;
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        return true;
    }

    @Command
    public boolean open(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerJobData jobData = jobPlugin.getJobData(player);
        if(jobData != null) {
            Optional<JobGate> closestJobGate = jobData.getJob().getGates()
                    .stream()
                    .filter(jg -> jg.getJob().equals(jobData.getJob()))
                    .min((jg1, jg2) -> Float.compare(jg1.getClosedPosition().distance(player.getLocation()), jg2.getClosedPosition().distance(player.getLocation())));
            if(closestJobGate.isPresent() && closestJobGate.get().getOpenPosition().distance(player.getLocation()) < 10f) {
                JobGate gate = closestJobGate.get();
                if(jobData.getJobRank().getNumber() < gate.getRank().getNumber())
                    player.sendErrorMessage("Jûsø rangas per þemas kad galëtumëte naudotis ðiais vartais.");
                else if(gate.isOpen())
                    player.sendErrorMessage("Vartai jau atidaryti!");
                else {
                    gate.open();
                    player.sendInfoText("Vartai atidaryti");
                }
            }
        }
        return true;
    }

    @Command
    public boolean close(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerJobData jobData = jobPlugin.getJobData(player);
        if(jobData != null) {
            Optional<JobGate> closestJobGate = jobData.getJob().getGates()
                    .stream()
                    .filter(jg -> jg.getJob().equals(jobData.getJob()))
                    .min((jg1, jg2) -> Float.compare(jg1.getClosedPosition().distance(player.getLocation()), jg2.getClosedPosition().distance(player.getLocation())));
            if(closestJobGate.isPresent() && closestJobGate.get().getOpenPosition().distance(player.getLocation()) < 10f) {
                JobGate gate = closestJobGate.get();
                if(jobData.getJobRank().getNumber() < gate.getRank().getNumber())
                    player.sendErrorMessage("Jûsø rangas per þemas kad galëtumëte naudotis ðiais vartais.");
                else if(!gate.isOpen())
                    player.sendErrorMessage("Vartai jau uþdaryti!");
                else {
                    gate.close();
                    player.sendInfoText("Vartai uþdaryti");
                }
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Leidþia iðeiti ið darbo")
    public boolean leaveJob(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerJobData jobData = jobPlugin.getJobData(player);
        if(jobData == null)
            player.sendErrorMessage("Jûs neturite darbo.");
        else if(jobData instanceof Faction)
            player.sendErrorMessage("Kad iðeitumëte ið frakcijos, naudokite /leavefaction");
        else if(jobData.getRemainingContract() > 0)
            player.sendErrorMessage("Jûsø kontraktas dar nesibaigë. Jums dar liko " + jobData.getRemainingContract() + " valandos.");
        else {
            JobPlugin.get(JobPlugin.class).setJob(player, null);
            player.removeJobWeapons();
            player.sendMessage(Color.NEWS, "Palikote savo darbovietæ, sëkmës!");
        }
        return true;
    }
}
