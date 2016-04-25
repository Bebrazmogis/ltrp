package lt.ltrp.object;

import lt.ltrp.ItemController;
import lt.ltrp.constant.ItemType;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Bebras
 *         2015.11.14.
 */
public interface Inventory {

    static Inventory create(EventManager eventManager, InventoryEntity owner, String name, int size) {
        return ItemController.get().createInventory(eventManager, owner, name, size);
    }

    static Inventory create(EventManager eventManager, InventoryEntity owner, String name) {
        throw new NotImplementedException();
    }

    public void add(Item item);

    /**
     * Tries to add an item, if the item is stackable and inventory contains an item of that {@link lt.ltrp.constant.ItemType} it will be added
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
    public void show(Player player);

    /**
     *
     * @return returns the {@link InventoryEntity} this inventory is associated to
     */
    public InventoryEntity getEntity();
}
