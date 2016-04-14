package lt.ltrp.item;

import lt.ltrp.item.constant.ItemType;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class ContainerItem extends BasicItem {

    private int itemCount, size;

    public ContainerItem(int id, String name, EventManager eventManager, ItemType type, boolean stackable, int items, int maxsize) {
        super(id, name, eventManager, type, stackable);
        this.itemCount = items;
        this.size = maxsize;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
