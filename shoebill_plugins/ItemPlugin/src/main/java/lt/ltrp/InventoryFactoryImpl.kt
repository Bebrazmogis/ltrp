package lt.ltrp

import lt.ltrp.`object`.Inventory
import lt.ltrp.`object`.InventoryEntity
import lt.ltrp.`object`.impl.FixedSizeInventory
import lt.ltrp.`object`.impl.InfiniteInventoryImpl
import lt.ltrp.item.InventoryFactory
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-11-09.
 */
class InventoryFactoryImpl(private val eventManager: EventManager) : InventoryFactory {

    override fun create(owner: InventoryEntity, name: String): Inventory {
        return InfiniteInventoryImpl(name, owner, eventManager)
    }

    override fun create(owner: InventoryEntity, name: String, size: Int): Inventory {
        val inv = FixedSizeInventory(eventManager, name, size, owner)

        return inv
    }
}