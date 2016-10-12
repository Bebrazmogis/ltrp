package lt.ltrp;

import lt.ltrp.data.Color;
import lt.ltrp.data.PlayerJobData;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.Rank;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.03.04.
 */
public class FactionAcceptCommands {

    private JobPlugin jobs;

    public FactionAcceptCommands(JobPlugin jobPlugin) {
        this.jobs = jobPlugin;
    }

    @Command
    public boolean faction(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        FactionInviteOffer offer = player.getOffer(FactionInviteOffer.class);
        if(offer == null) {
            player.sendErrorMessage("Jums niekas nesiûlo prisijungti prie frakcijos.");
        } else if(offer.isExpired()) {
            player.sendErrorMessage("Pasiûlymo laikas pasibaigë.");
        } else {
            player.getOffers().remove(offer);
            LtrpPlayer leader = offer.getOfferedBy();
            PlayerJobData leaderData = JobController.get().getJobData(leader);
            Rank minRank = leaderData.getJob().getRanks().stream().min((r1, r2) -> Integer.compare(r1.getNumber(), r2.getNumber())).get();
            jobs.setJob(player, leaderData.getJob(), minRank);
            player.sendMessage(Color.NEWS, "Prisijungëte prie frakcijos \"" + leaderData.getJob().getName() + "\". Jûsø rangas: " + minRank.getName());
            leader.sendMessage(Color.NEWS, player.getCharName() + " prisijungë prie jûsø frakcijos, jam paskirtas rangas " + minRank.getName());
        }
        return true;
    }

}
