package lt.ltrp.player.event


import lt.ltrp.player.`object`.LtrpPlayer
import net.gtaun.util.event.Event

/**
 * @author Bebras
 *         2016.04.07.
 *         Base event related to [LtrpPlayer]
 */
abstract class PlayerEvent(open val player: LtrpPlayer) : Event() {

}
