package lt.ltrp.command;

import lt.ltrp.constant.JobProperty;
import lt.ltrp.data.Color;
import lt.ltrp.job.JobController;
import lt.ltrp.job.object.ContractJob;
import lt.ltrp.job.object.Job;
import lt.ltrp.job.object.JobRank;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.player.job.PlayerJobController;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.object.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static sun.audio.AudioPlayer.player;

/**
 * Created by Bebras on 2016-10-26.
 *
 */
public class TestCommands {


    @Command
    public boolean takeJob(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player.getJobData() != null) {
            player.sendErrorMessage("Jûs jau turite darbà.");
        } else {
            Collection<ContractJob> cJobs = new ArrayList<>();
            JobController.instance.get().forEach(j -> {
                if(j instanceof ContractJob) {
                    cJobs.add((ContractJob) j);
                }
            });
            Optional<ContractJob> contractJobOptional = cJobs.stream()
                    .filter(j -> j.getLocation().distance(player.getLocation()) < 10f).findFirst();
            if(!contractJobOptional.isPresent()){
                player.sendErrorMessage("Ðià komandà galite naudoti tik prie darbovieèiø.");
            } else {
                Job job = contractJobOptional.get();
                player.getJobData().setJob(job);
                player.sendMessage(Color.NEWS, "* Jûs ásidarbinote, jeigu reikia daugiau pagalbos raðykite /help.");
            }
        }
        return true;
    }

    @Command public boolean testJob(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.sendMessage(Color.SILVER, "Darbø skaièius: " + JobController.instance.get().size());
        for(Job job : JobController.instance.get()) {
            player.sendMessage(Color.CADETBLUE, String.format("[%d]%s. Class:%s JobRank count: %d Vehicle count: %d",
                    job.getUUID(),
                    job.getName(),
                    job.getClass().getSimpleName(),
                    job.getRanks() == null ? -1 : job.getRanks().size(),
                    job.getVehicles() == null ? -1 : job.getVehicles().size()));
            int propertyCount = 0;
            String propString = "";
            for(Field f : job.getClass().getFields()) {
                if(f.isAnnotationPresent(JobProperty.class)) {
                    propertyCount++;
                    try {
                        propString += f.getName() + ":" + f.get(job);
                    } catch(IllegalAccessException e) {
                        propString += f.getName() + " unaccessible";
                    }
                }
            }
            if(propertyCount > 0)
                player.sendMessage(Color.ROYALBLUE, String.format("Properties: %d. %s", propertyCount, propString));
        }
        return true;
    }



    @Command public boolean giveMeJob(Player p, int jobId) {
        LtrpPlayer player = LtrpPlayer.get(p);

        Job job = JobController.instance.get(jobId);
        if(job == null) {
            player.sendErrorMessage("YO, nëra toki odarbo");
        } else {
            PlayerJobController.Companion.getInstance().setJob(player, job);
            p.sendMessage(Color.CRIMSON, "Tavo naujas darbas :" + job.getName() + " id: " + job.getUUID());
        }
        return true;
    }

    @Command
    public boolean giveMeRank(Player player, int rankNumber) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(p.getJobData() == null) {
            p.sendErrorMessage("you got no job son");
        } else {
            JobRank rank = p.getJobData().getJob().getRankByNumber(rankNumber);
            if(rank == null) {
                p.sendErrorMessage("No such rank.");
            } else {
                PlayerJobController.Companion.getInstance().setRank(p, rank);
                p.sendMessage(Color.LIGHTGREEN, "gz kid, new rank: "+ rank.getName() + " Numb: "+ rank.getNumber());
                return true;
            }
        }
        return false;
    }
}
