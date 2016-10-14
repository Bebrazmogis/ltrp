package lt.ltrp.house.upgrade

import lt.ltrp.house.`object`.House
import lt.ltrp.house.upgrade.constant.HouseUpgradeType

/**
 * Created by Bebras on 2016-10-14.
 * A set of methods providing ways to interact with house upgrades
 */
abstract class HouseUpgradeController protected constructor() {

    init {
        instance = this
    }

    abstract fun insert(house: House, type: HouseUpgradeType)
    abstract fun remove(house: House, type: HouseUpgradeType)

    companion object {
        lateinit var instance: HouseUpgradeController

        fun get(): HouseUpgradeController {
            return instance
        }
    }
}