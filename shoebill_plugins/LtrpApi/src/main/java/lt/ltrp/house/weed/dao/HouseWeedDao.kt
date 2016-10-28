package lt.ltrp.house.weed.dao

import lt.ltrp.house.`object`.House
import lt.ltrp.house.weed.`object`.HouseWeedSapling

/**
 * Created by Bebras on 2016-10-14.
 * A set of methods defining the possible [HouseWeedSapling] interactions
 */
interface HouseWeedDao {

    /**
     * Retrieves the unharvested saplings for a specific house
     * @param house
     * *
     * @return returns a list of saplings
     */
    fun getWeed(house: House): Set<HouseWeedSapling>

    /**
     * Updates sapling data
     * @param sapling
     */
    fun update(sapling: HouseWeedSapling)

    /**
     * Insert a new sapling
     * @param sapling
     */
    fun insert(sapling: HouseWeedSapling)

    fun remove(weedSapling: HouseWeedSapling)
}