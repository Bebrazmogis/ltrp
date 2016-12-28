package lt.ltrp.object;

import lt.ltrp.item.ItemController;
import lt.ltrp.constant.ItemType;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.entities.Destroyable;
import net.gtaun.shoebill.entities.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.11.14.
 */
public interface Item extends NamedEntity, Destroyable {

    public ItemType getType();
    public boolean isStackable();
    public int getAmount();
    public void setAmount(int amount);
    void showOptions(Player player, Inventory inventory, AbstractDialog parentDialog);

    public static Item create(ItemType type, InventoryEntity entity, EventManager eventManager) {
        return ItemController.Companion.get().createItem(type, entity, eventManager);
    }

    public static Item create(ItemType type, String name, InventoryEntity entity, EventManager eventManager) {
        return ItemController.Companion.get().createItem(type, name, entity, eventManager);
    }

    public static Item create(ItemType type, String name, SpecialAction specialAction, InventoryEntity entity, EventManager eventManager) {
        return ItemController.Companion.get().createItem(type, name, specialAction, entity, eventManager);
    }

    public static Item create(ItemType type, EventManager eventManager) {
        return create(type, null, eventManager);
    }

}
