package lt.ltrp.event.item;


import lt.ltrp.object.Item;

/**
 * @author Bebras
 *         2016.04.07.
 */
public class ItemDestroyEvent extends ItemEvent {

    public ItemDestroyEvent(Item item) {
        super(item);
    }
}
