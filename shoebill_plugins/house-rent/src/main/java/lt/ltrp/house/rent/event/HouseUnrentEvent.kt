package lt.ltrp.house.rent.event

import lt.ltrp.house.`object`.House
import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.player.`object`.PlayerData
import lt.ltrp.house.event.HouseEvent

/**
 * Created by Bebras on 2016-10-06.
 */
class HouseUnrentEvent(house: House, val player: PlayerData): HouseEvent(house) {
}