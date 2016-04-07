package lt.ltrp.item;

import lt.ltrp.player.LtrpPlayer;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.11.14.
 */
public abstract class ConsumableItem extends BasicItem {

    private int dosesLeft;

    public ConsumableItem(int id, String name, EventManager eventManager, ItemType type, int dosesLeft, boolean stackable) {
        super(id, name, eventManager, type, stackable);
        this.dosesLeft = dosesLeft;
    }

    public int getDosesLeft() {
        return dosesLeft;
    }

    public void setDosesLeft(int dosesLeft) {
        this.dosesLeft = dosesLeft;
    }

    @ItemUsageOption(name = "Vartoti")
    public boolean use(LtrpPlayer player , Inventory inventory) {
        dosesLeft--;
        return true;
    }


}
