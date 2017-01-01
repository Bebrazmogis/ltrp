package lt.ltrp.player.event

import lt.ltrp.`object`.LtrpPlayer
import net.gtaun.shoebill.constant.DisconnectReason

/**
 * Created by Bebras on 2016-11-09.
 * A wrapper to the shoebill [net.gtaun.shoebill.event.player.PlayerDisconnectEvent] event
 */
class PlayerDisconnectEvent(player: LtrpPlayer, val reason: DisconnectReason) : PlayerEvent(player) {

}