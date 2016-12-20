package lt.ltrp.item

import lt.ltrp.`object`.*
import lt.ltrp.constant.ItemType
import net.gtaun.shoebill.constant.SpecialAction
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-11-09.
 * This class exists only for legacy reasons
 */
@Deprecated("Don't use, use ItemFactory and InventoryFactory instead")
interface ItemController {

    fun createInventory(eventManager: EventManager, owner: InventoryEntity, name: String, size: Int): Inventory

    fun createItem(type: ItemType, name: String, entity: InventoryEntity, eventManager: EventManager): Item
    fun createItem(type: ItemType, owner: InventoryEntity, eventManager: EventManager): Item
    fun createItem(type: ItemType, name: String, specialAction: SpecialAction, owner: InventoryEntity, eventManager: EventManager): Item

    companion object {
        private var instance = ItemControllerImpl()
        fun get(): ItemController {
            return instance
        }
    }
}
private class ItemControllerImpl() : ItemController {

    override fun createInventory(eventManager: EventManager, owner: InventoryEntity, name: String, size: Int): Inventory {
        return InventoryFactory.INSTANCE.create(owner, name, size)
    }

    override fun createItem(type: ItemType, name: String, entity: InventoryEntity, eventManager: EventManager): Item {
        return ItemFactory.instance.create(type, name, entity, eventManager)
    }

    override fun createItem(type: ItemType, owner: InventoryEntity, eventManager: EventManager): Item {
        return ItemFactory.instance.create(type, owner, eventManager)
    }

    override fun createItem(type: ItemType, name: String, specialAction: SpecialAction, owner: InventoryEntity, eventManager: EventManager): Item {
        return ItemFactory.instance.create(type, name, specialAction, owner, eventManager)
    }

}