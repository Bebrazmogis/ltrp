package lt.ltrp.item.event;


import lt.ltrp.item.object.InventoryEntity;
import lt.ltrp.item.object.Item;

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
