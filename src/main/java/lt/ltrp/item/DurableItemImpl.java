package lt.ltrp.item;

import lt.ltrp.item.constant.ItemType;
import lt.ltrp.item.object.DurableItem;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class DurableItemImpl extends BasicItem implements DurableItem {


    private int maxDurability, durability;

    public DurableItemImpl(int id, String name, EventManager eventManager, ItemType type, int durability, int maxdurability, boolean stackable) {
        super(id, name, eventManager, type, stackable);
        this.durability = durability;
        this.maxDurability = maxdurability;
    }

    public void use() {
        durability--;
        if(durability == 0) {
            this.destroy();
        }
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }


    public int getMaxDurability() {
        return maxDurability;
    }

    public int getDurability() {
        return durability;
    }

}

