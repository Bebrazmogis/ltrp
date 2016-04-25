package lt.ltrp.event.item;


import lt.ltrp.object.InventoryEntity;
import lt.ltrp.object.Item;

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
