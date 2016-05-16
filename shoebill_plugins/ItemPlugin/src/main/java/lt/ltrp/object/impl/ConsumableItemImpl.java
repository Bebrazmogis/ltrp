package lt.ltrp.object.impl;

import lt.ltrp.constant.ItemType;
import lt.ltrp.util.ItemUsageOption;
import lt.ltrp.object.ConsumableItem;
import lt.ltrp.object.Inventory;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.11.14.
 */
public abstract class ConsumableItemImpl extends BasicItem implements ConsumableItem {

    private int dosesLeft;

    public ConsumableItemImpl(int id, String name, EventManager eventManager, ItemType type, int dosesLeft, boolean stackable) {
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
