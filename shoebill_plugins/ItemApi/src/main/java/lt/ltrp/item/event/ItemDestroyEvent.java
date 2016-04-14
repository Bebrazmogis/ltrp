package lt.ltrp.item.event;


import lt.ltrp.item.object.Item;

/**
 * @author Bebras
 *         2016.04.07.
 */
public class ItemDestroyEvent extends ItemEvent {

    public ItemDestroyEvent(Item item) {
        super(item);
    }
}
