package lt.ltrp.item;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class ContainerItem extends BasicItem {

    private int itemCount, size;

    public ContainerItem(String name, int id, ItemType type, boolean stackable, int items, int maxsize) {
        super(name, id, type, stackable);
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
