package lt.ltrp;

import lt.ltrp.item.Inventory;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class InventoryEntityImpl extends NamedEntityImpl implements InventoryEntity {

    private Inventory inventory;

    public InventoryEntityImpl(int id, String name, Inventory inventory) {
        super(id, name);
        this.inventory = inventory;
    }

    public InventoryEntityImpl() {

    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
