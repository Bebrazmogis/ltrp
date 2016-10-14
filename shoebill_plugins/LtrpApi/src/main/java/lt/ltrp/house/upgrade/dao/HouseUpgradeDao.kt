package lt.ltrp.house.upgrade.dao

import lt.ltrp.house.`object`.House
import lt.ltrp.house.upgrade.data.HouseUpgrade

/**
 * Created by Bebras on 2016-10-14.
 * Interface defining methods for interacting with house upgrades
 */
interface HouseUpgradeDao {

    fun get(house: House): Set<HouseUpgrade>
    fun insert(upgrade: HouseUpgrade): Int
    fun remove(upgrade: HouseUpgrade)

}