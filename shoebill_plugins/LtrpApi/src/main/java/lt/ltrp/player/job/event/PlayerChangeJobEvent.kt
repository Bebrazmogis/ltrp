package lt.ltrp.player.job.event

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.event.player.PlayerEvent
import lt.ltrp.job.`object`.Job


/**
 * @author Bebras
 *         2016.04.07.
 */
class PlayerChangeJobEvent(player: LtrpPlayer, val oldJob: Job?, val newJob: Job?): PlayerEvent(player) {

}
