package lt.ltrp.house.weed

import lt.ltrp.`object`.PlayerData
import lt.ltrp.house.`object`.House
import lt.ltrp.house.weed.`object`.HouseWeedSapling
import net.gtaun.shoebill.data.Location

/**
 * Created by Bebras on 2016-10-14.
 * This class defines the available methods for managing house weed objects
 */
abstract class HouseWeedController protected constructor() {


    init {
        HouseWeedController.instance = this
    }

    abstract fun createWeed(house: House, location: Location, playerData: PlayerData): HouseWeedSapling
    abstract fun destroyWeed(weedSapling: HouseWeedSapling)
    abstract fun updateWeed(weedSapling: HouseWeedSapling)


    companion object {
        lateinit var instance: HouseWeedController

    }

}