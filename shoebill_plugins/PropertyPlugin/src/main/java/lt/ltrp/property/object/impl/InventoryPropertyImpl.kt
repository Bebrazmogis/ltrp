package lt.ltrp.property.`object`.impl

import lt.ltrp.`object`.Inventory
import lt.ltrp.`object`.InventoryEntity
import net.gtaun.shoebill.data.Location
import net.gtaun.util.event.EventManager

/**
 * @author Bebras
 *         2016.04.19.
 */
abstract class InventoryPropertyImpl(uuid: Int, name: String, entrance: Location, eventManager: EventManager) :
        AbstractProperty(uuid, name, entrance, eventManager), InventoryEntity {

    protected val inventory = Inventory.create(eventManager, this, name)

}
