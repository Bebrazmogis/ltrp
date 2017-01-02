package lt.ltrp.event

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.event.player.PlayerEvent

/**
 * Created by Bebras on 2016-10-15.
 * Fired when a player spawns for the first time
 */
class PlayerFirstSpawnEvent(player: LtrpPlayer): PlayerEvent(player) {
}