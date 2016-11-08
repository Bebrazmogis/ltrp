package lt.ltrp.house.rent.event

import lt.ltrp.house.`object`.House
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.PlayerData
import lt.ltrp.house.event.HouseEvent

/**
 * Created by Bebras on 2016-10-06.
 */
class HouseRentEvent(house: House, val player: PlayerData): HouseEvent(house) {
}