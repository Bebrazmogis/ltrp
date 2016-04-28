package lt.ltrp.object.impl;

import lt.ltrp.object.Inventory;
import lt.ltrp.object.InventoryEntity;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.19.
 */
public abstract class InventoryPropertyImpl extends PropertyImpl implements InventoryEntity {

    private Inventory inventory;

    public InventoryPropertyImpl(int id, String name, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit, Color labelColor, EventManager eventManager) {
        super(id, name, ownerUserId, pickupModelId, price, entrance, exit, labelColor, eventManager);
    }


    @Override
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
