package lt.ltrp.house.rent.dao

import lt.ltrp.house.`object`.House
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.house.rent.`object`.HouseTenant

/**
 * Created by Bebras on 2016-10-06.
 */
interface HouseTenantDao {

    fun get(house: House): List<HouseTenant>
    fun get(player: LtrpPlayer): HouseTenant?
    fun insert(houseTenant: HouseTenant): Int
    fun remove(houseTenant: HouseTenant)

}