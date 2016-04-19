package lt.ltrp;

import lt.ltrp.data.Color;
import lt.ltrp.data.JobData;
import lt.ltrp.object.Rank;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.03.04.
 */
public class FactionAcceptCommands {


    @Command
    public boolean faction(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        FactionInviteOffer offer = player.getOffer(FactionInviteOffer.class);
        if(offer == null) {
            player.sendErrorMessage("Jums niekas nesi�lo prisijungti prie frakcijos.");
        } else if(offer.isExpired()) {
            player.sendErrorMessage("Pasi�lymo laikas pasibaig�.");
        } else {
            player.getOffers().remove(offer);
            LtrpPlayer leader = offer.getOfferedBy();
            JobData leaderData = JobController.get().getJobData(leader);
            Rank minRank = leaderData.getJob().getRanks().stream().min((r1, r2) -> Integer.compare(r1.getNumber(), r2.getNumber())).get();
            JobController.get().setJob(player, leaderData.getJob(), minRank);
            player.sendMessage(Color.NEWS, "Prisijung�te prie frakcijos \"" + leaderData.getJob().getName() + "\". J�s� rangas: " + minRank.getName());
            leader.sendMessage(Color.NEWS, player.getCharName() + " prisijung� prie j�s� frakcijos, jam paskirtas rangas " + minRank.getName());
            LtrpPlayer.getPlayerDao().update(player);
        }
        return true;
    }

}
