package lt.ltrp.item.event;


import lt.ltrp.item.object.Item;
import lt.ltrp.player.event.PlayerEvent;
import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.02.21.
 */
public class PlayerDropItemEvent extends PlayerEvent {

    private Item item;

    public PlayerDropItemEvent(LtrpPlayer player, Item item) {
        super(player);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
