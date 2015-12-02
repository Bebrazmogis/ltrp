package lt.ltrp.item;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class DurableItem extends BasicItem {


    private int maxDurability, durability;

    public DurableItem(String name, int id, ItemType type, int durability, int maxdurability, boolean stackable) {
        super(name, id, type, stackable);
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
