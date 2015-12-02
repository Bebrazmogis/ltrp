package lt.ltrp.item;

import lt.ltrp.player.LtrpPlayer;

/**
 * @author Bebras
 *         2015.11.14.
 */
public abstract class ConsumableItem extends BasicItem {

    private int dosesLeft;

    public ConsumableItem(String name, int id, ItemType type, int dosesLeft, boolean stackable) {
        super(name, id, type, stackable);
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
