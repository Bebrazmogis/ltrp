package lt.ltrp.house.weed

import lt.ltrp.`object`.PlayerData
import lt.ltrp.house.`object`.House
import lt.ltrp.house.weed.`object`.HouseWeedSapling
import lt.ltrp.house.weed.`object`.impl.HouseWeedSaplingImpl
import lt.ltrp.house.weed.dao.HouseWeedDao
import net.gtaun.shoebill.data.Location
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-14.
 * A concrete implementation of [HouseWeedController]
 */
class HouseWeedControllerImpl(private val houseWeedDao: HouseWeedDao,
                              private val eventManager: EventManager): HouseWeedController() {


    override fun destroyWeed(weedSapling: HouseWeedSapling) {
        weedSapling.house.weedSaplings.remove(weedSapling)
        weedSapling.destroy()
        houseWeedDao.remove(weedSapling)
    }

    override fun updateWeed(weedSapling: HouseWeedSapling) {
        houseWeedDao.update(weedSapling)
    }


    override fun createWeed(house: House, location: Location, playerData: PlayerData): HouseWeedSapling {
        val weed = HouseWeedSaplingImpl(house, location, playerData, eventManager)
        house.weedSaplings.add(weed)
        houseWeedDao.insert(weed)
        return weed
    }


}