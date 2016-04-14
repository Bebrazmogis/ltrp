package lt.ltrp.item.event;

import lt.ltrp.item.object.Item;
import net.gtaun.util.event.Event;

/**
 * @author Bebras
 *         2016.04.07.
 */
public abstract class ItemEvent extends Event{

    private Item item;

    public ItemEvent(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
