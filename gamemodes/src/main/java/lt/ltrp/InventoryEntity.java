package lt.ltrp;

import lt.ltrp.item.Inventory;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class InventoryEntity extends NamedEntity {

    private Inventory inventory;

    public InventoryEntity(int id, String name, Inventory inventory) {
        super(id, name);
        this.inventory = inventory;
    }

    public InventoryEntity() {

    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
