package lt.ltrp.item.event;

import lt.ltrp.item.Item;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.02.21.
 */
public class PlayerDropItemEvent extends PlayerEvent {

    private Item item;

    public PlayerDropItemEvent(Player player, Item item) {
        super(player);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
