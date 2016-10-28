package lt.ltrp.event

import lt.ltrp.`object`.Item
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.event.player.PlayerEvent

/**
 * Created by Bebras on 2016-10-06.
 */
class PlayerSelectItemOptionEvent(player: LtrpPlayer, var item: Item, var option: String, var optionName: String):
        PlayerEvent(player) {
}