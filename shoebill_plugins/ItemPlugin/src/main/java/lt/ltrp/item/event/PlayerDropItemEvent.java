package lt.ltrp.event.item;


import lt.ltrp.event.player.PlayerEvent;
import lt.ltrp.object.Item;
import lt.ltrp.object.LtrpPlayer;

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
