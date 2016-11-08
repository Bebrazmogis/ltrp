package lt.ltrp.house.event

import lt.ltrp.house.`object`.House
import lt.ltrp.house.event.HouseEvent

/**
 * Created by Bebras on 2016-10-06.
 */
class HouseLoadedEvent(house: House): HouseEvent(house) {
}