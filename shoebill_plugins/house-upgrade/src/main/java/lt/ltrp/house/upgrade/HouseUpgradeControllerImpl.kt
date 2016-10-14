package lt.ltrp.house.upgrade

import lt.ltrp.house.`object`.House
import lt.ltrp.house.upgrade.constant.HouseUpgradeType
import lt.ltrp.house.upgrade.dao.HouseUpgradeDao
import lt.ltrp.house.upgrade.data.HouseUpgrade

/**
 * Created by Bebras on 2016-10-14.
 * A concrete implementation of the [HouseUpgradeController] interface
 */
class HouseUpgradeControllerImpl(private val houseUpgradeDao: HouseUpgradeDao):
        HouseUpgradeController() {


    override fun remove(house: House, type: HouseUpgradeType) {
        val upgrade = house.upgrades.firstOrNull { it.type == type }
        if(upgrade != null) {
            house.upgrades.remove(upgrade)
            houseUpgradeDao.remove(upgrade)
        }
    }


    override fun insert(house: House, type: HouseUpgradeType) {
        if(!house.isUpgradeInstalled(type)) {
            val upgrade = HouseUpgrade(house, type)
            house.upgrades.add(upgrade)
            houseUpgradeDao.insert(upgrade)
        }
    }
}