package lt.ltrp.player.job.command

import lt.ltrp.data.Color
import lt.ltrp.player.job.data.FactionInviteOffer
import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.player.job.PlayerJobController
import net.gtaun.shoebill.common.command.Command
import net.gtaun.shoebill.`object`.Player

/**
 * @author Bebras
 *         2016.03.04.
 */
class FactionAcceptCommands {

    @Command
    fun faction(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val offer = player.getOffer(FactionInviteOffer::class.java)
        if(offer == null) {
            player.sendErrorMessage("Jums niekas nesiûlo prisijungti prie frakcijos.");
        } else if(offer.isExpired) {
            player.sendErrorMessage("Pasiûlymo laikas pasibaigë.");
        } else {
            player.offers.remove(offer)
            val leader = offer.offeredBy
            val job = leader.jobData?.job
            if(job != null) {
                PlayerJobController.instance.setJob(player, job)
                player.sendMessage(Color.NEWS, "Prisijungëte prie frakcijos \"" + leader.jobData?.job?.name + "\". Jûsø rangas: " + player.jobData?.rank?.name)
                leader.sendMessage(Color.NEWS, player.charName + " prisijungë prie jûsø frakcijos, jam paskirtas rangas " + player.jobData?.rank?.name)
            }
        }
        return true
    }

}
