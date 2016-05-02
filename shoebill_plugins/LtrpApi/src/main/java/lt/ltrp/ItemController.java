package lt.ltrp;

import lt.ltrp.constant.ItemType;
import lt.ltrp.dao.ItemDao;
import lt.ltrp.dao.PhoneDao;
import lt.ltrp.object.*;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface ItemController {

    WeedSeedItem createWeedSeed(EventManager eventManager);

    WeedItem createWeed(EventManager eventManager, int doses);



    class Instance {
        static ItemController instance;
    }

    static ItemController get() {
        return Instance.instance;
    }

    Inventory createInventory(EventManager eventManager, InventoryEntity owner, String name, int size);
    Item createItem(ItemType type, InventoryEntity owner, EventManager eventManager);
    Item createItem(ItemType type, String name, InventoryEntity entity, EventManager eventManager);
    Item createItem(ItemType type, String name, SpecialAction specialAction, InventoryEntity entity, EventManager eventManager);

    ItemDao getItemDao();
    PhoneDao getPhoneDao();

}
