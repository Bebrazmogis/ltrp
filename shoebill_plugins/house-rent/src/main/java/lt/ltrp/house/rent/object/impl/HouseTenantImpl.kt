package lt.ltrp.house.rent.`object`.impl

import lt.ltrp.`object`.Entity
import lt.ltrp.`object`.PlayerData
import lt.ltrp.`object`.impl.EntityImpl
import lt.ltrp.house.`object`.House
import lt.ltrp.house.rent.`object`.HouseTenant
import lt.ltrp.house.rent.event.HouseRentEvent
import lt.ltrp.house.rent.event.HouseUnrentEvent
import net.gtaun.util.event.EventManager
import java.time.Duration
import java.time.LocalDateTime

/**
 * Created by Bebras on 2016-10-06.
 *
 */
class HouseTenantImpl(uuid: Int,
                      override val player: PlayerData,
                      override var house: House,
                      override var rentTime: LocalDateTime,
                      var eventManager: EventManager):
        EntityImpl(uuid), HouseTenant {

    constructor(player: PlayerData, house: House, eventManager: EventManager): this(Entity.INVALID_ID, player, house, LocalDateTime.now(), eventManager) {

    }


    override fun rent() {
        eventManager.dispatchEvent(HouseRentEvent(house, player))
    }

    override fun unRent() {
        eventManager.dispatchEvent(HouseUnrentEvent(house, player))
    }

    override fun getRentPeriod(): Duration {
        return Duration.between(rentTime, LocalDateTime.now())
    }
}