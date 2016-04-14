package lt.ltrp.item.object;

import lt.ltrp.api.NamedEntity;
import lt.ltrp.item.object.Inventory;

/**
 * @author Bebras
 *         2016.04.08.
 */
public interface InventoryEntity extends NamedEntity {

    void setInventory(Inventory inventory);
    Inventory getInventory();

}
