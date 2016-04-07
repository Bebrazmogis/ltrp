package lt.ltrp.item.event;

import lt.ltrp.InventoryEntity;
import lt.ltrp.item.Item;

/**
 * @author Bebras
 *         2016.04.07.
 */
public class ItemCreateEvent extends ItemEvent {

    private InventoryEntity owner;

    public ItemCreateEvent(Item item, InventoryEntity owner) {
        super(item);
        this.owner = owner;
    }

    public InventoryEntity getOwner() {
        return owner;
    }
}
