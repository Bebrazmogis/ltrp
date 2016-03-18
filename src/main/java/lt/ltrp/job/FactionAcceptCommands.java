package lt.ltrp.job;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
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
            player.setJob(leader.getJob());
            Rank minRank = leader.getJob().getRanks().stream().min((r1, r2) -> Integer.compare(r1.getNumber(), r2.getNumber())).get();
            player.setJobRank(minRank);
            player.sendMessage(Color.NEWS, "Prisijung�te prie frakcijos \"" + leader.getJob().getName() + "\". J�s� rangas: " + minRank.getName());
            leader.sendMessage(Color.NEWS, player.getCharName() + " prisijung� prie j�s� frakcijos, jam paskirtas rangas " + minRank.getName());
            LtrpGamemode.getDao().getPlayerDao().update(player);
        }
        return true;
    }

}
