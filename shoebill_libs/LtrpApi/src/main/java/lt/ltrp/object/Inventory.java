package lt.ltrp.object;

import lt.ltrp.item.ItemController;
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
        return ItemController.Companion.get().createInventory(eventManager, owner, name, size);
    }

    static Inventory create(EventManager eventManager, InventoryEntity owner, String name) {
        return ItemController.Companion.get().createInventory(eventManager, owner, name, 15);
    }

    public void add(Item item);

    /**
     * Tries to add an item, if the item is stackable and inventory contains an item of that {@link lt.ltrp.constant.ItemType} it will be added
     * @param item item to be added
     * @return true if the item was added, false otherwise
     */
    boolean tryAdd(Item item);
    void add(Item[] items);
    void remove(Item item);
    void remove(int index);
    String getName();
    void clear();
    boolean contains(Item item);
    boolean containsType(ItemType type);
    boolean containsWeapon(WeaponModel model);
    boolean isFull();
    boolean isEmpty();
    int getItemCount();
    Item[] getItems();
    Item[] getItems(ItemType type);
    <T> T[] getItems(Class<T> t);
    Item getItem(ItemType type);
    void show(Player player);

    /**
     *
     * @return returns the {@link InventoryEntity} this inventory is associated to
     */
    public InventoryEntity getEntity();
}
