package lt.ltrp.item.event;


import lt.ltrp.item.object.Inventory;
import lt.ltrp.item.object.Item;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.util.event.Event;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class ItemLocationChangeEvent extends Event {

    private Item item;
    private Inventory oldInventory;
    private Inventory newInventory;
    private LtrpPlayer player;

    public ItemLocationChangeEvent(Item item, Inventory oldInventory, Inventory newInventory, LtrpPlayer player) {
        this.item = item;
        this.oldInventory = oldInventory;
        this.newInventory = newInventory;
        this.player = player;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Inventory getOldInventory() {
        return oldInventory;
    }

    public void setOldInventory(Inventory oldInventory) {
        this.oldInventory = oldInventory;
    }

    public Inventory getNewInventory() {
        return newInventory;
    }

    public void setNewInventory(Inventory newInventory) {
        this.newInventory = newInventory;
    }

    public LtrpPlayer getPlayer() {
        return player;
    }

    public void setPlayer(LtrpPlayer player) {
        this.player = player;
    }
}
