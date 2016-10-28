package lt.ltrp

import lt.ltrp.`object`.InventoryEntity
import lt.ltrp.`object`.WeaponItem
import lt.ltrp.`object`.impl.WeaponItemImpl
import lt.ltrp.dao.ItemDao
import lt.ltrp.data.LtrpWeaponData
import lt.ltrp.event.item.ItemCreateEvent
import lt.ltrp.item.ItemFactory
import net.gtaun.shoebill.constant.WeaponModel
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-28.
 */
class ItemFactoryImpl(private val eventManager: EventManager,
                      private val itemDao: ItemDao): ItemFactory {

    override fun createWeapon(model: WeaponModel, ammo: Int, owner: InventoryEntity): WeaponItem {
        val item = WeaponItemImpl(eventManager, LtrpWeaponData (model, ammo, false))
        eventManager.dispatchEvent(ItemCreateEvent (item, owner))
        itemDao.insert(item)
        return item
    }

}