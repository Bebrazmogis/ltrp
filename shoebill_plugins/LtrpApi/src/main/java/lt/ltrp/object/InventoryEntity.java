package lt.ltrp.object;

import lt.ltrp.NamedEntity;

/**
 * @author Bebras
 *         2016.04.08.
 */
public interface InventoryEntity extends NamedEntity {

    void setInventory(Inventory inventory);
    Inventory getInventory();

}
