package lt.ltrp.`object`.impl;

import lt.ltrp.`object`.Inventory;
import lt.ltrp.`object`.InventoryEntity;

/**
 * @author Bebras
 *         2015.11.29.
 */
abstract  class InventoryEntityImpl(uuid: Int, name: String, override var inventory: Inventory) : NamedEntityImpl(uuid, name), InventoryEntity {


}
