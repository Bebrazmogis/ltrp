package lt.ltrp.player.job.data

import lt.ltrp.data.PlayerOffer
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.job.`object`.Faction
import net.gtaun.util.event.EventManager

/**
 * @author Bebras
 *         2016.03.04.
 */
class FactionInviteOffer(player: LtrpPlayer, offeredBy: LtrpPlayer, val faction: Faction, eventManager: EventManager):
        PlayerOffer(player, offeredBy, eventManager, 120, FactionInviteOffer::class.java) {

}
