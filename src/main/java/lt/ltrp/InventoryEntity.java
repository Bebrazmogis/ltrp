package lt.ltrp;

import lt.ltrp.item.Inventory;

/**
 * @author Bebras
 *         2016.04.08.
 */
public interface InventoryEntity extends NamedEntity {

    void setInventory(Inventory inventory);
    Inventory getInventory();

}
