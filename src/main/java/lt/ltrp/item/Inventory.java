package lt.ltrp.item;

import lt.ltrp.InventoryEntity;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.constant.WeaponModel;

/**
 * @author Bebras
 *         2015.11.14.
 */
public interface Inventory {

    public void add(Item item);

    /**
     * Tries to add an item, if the item is stackable and inventory contains an item of that {@link lt.ltrp.item.ItemType} it will be added
     * @param item item to be added
     * @return true if the item was added, false otherwise
     */
    public boolean tryAdd(Item item);
    public void add(Item[] items);
    public void remove(Item item);
    public void remove(int index);
    public String getName();
    public void clear();
    public boolean contains(Item item);
    public boolean containsType(ItemType type);
    public boolean containsWeapon(WeaponModel model);
    public boolean isFull();
    public int getItemCount();
    public Item[] getItems();
    public Item[] getItems(ItemType type);
    public <T> T[] getItems(Class<T> t);
    public Item getItem(ItemType type);
    public void show(LtrpPlayer player);

    /**
     *
     * @return returns the {@link lt.ltrp.InventoryEntity} this inventory is associated to
     */
    public InventoryEntity getEntity();
}
