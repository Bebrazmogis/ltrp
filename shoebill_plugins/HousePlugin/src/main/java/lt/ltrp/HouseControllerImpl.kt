package lt.ltrp

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.impl.HouseImpl
import lt.ltrp.dao.impl.MySqlHouseDaoImpl
import lt.ltrp.data.Color
import lt.ltrp.house.HouseController
import lt.ltrp.house.event.HouseCreateEvent
import lt.ltrp.house.`object`.House
import net.gtaun.shoebill.data.Location
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-14.
 */
class HouseControllerImpl(private val houseContainer: HouseContainer,
                          private val houseDao: MySqlHouseDaoImpl,
                          private val eventManager: EventManager)
                          : HouseController() {

    init {
        houseContainer.houses.addAll(houseDao.get())
    }


    override fun create(id: Int, name: String?, ownerUserId: Int, pickupModelId: Int, price: Int, entrance: Location?, exit: Location?, labelColor: Color?, money: Int, rentPrice: Int): House {
        val house = HouseImpl(id, name, ownerUserId, pickupModelId, price, entrance, exit, labelColor, money, rentPrice, eventManager)
        houseContainer.houses.add(house)
        houseDao.insert(house)
        eventManager.dispatchEvent(HouseCreateEvent(house))
        return house
    }

    override fun getAll(): Collection<House> {
        return houseContainer.houses
    }

    override fun get(location: Location): House? {
        return all.firstOrNull{ it.exit != null && it.exit.distance(location) < 200 }
    }

    override fun showManagementDialog(player: LtrpPlayer?) {

    }

    override fun getClosest(location: Location, maxDistance: Float): House? {
        return all.filter { it.entrance != null && it.entrance.distance(location) < maxDistance }
                .minBy { it.entrance.distance(location) }
    }

    override fun remove(house: House) {
        houseDao.remove(house)
        houseContainer.houses.remove(house)
        house.destroy()
    }

    override fun update(house: House) {
        houseDao.update(house)
    }

    fun clear() {
        houseContainer.houses.clear()
    }

}