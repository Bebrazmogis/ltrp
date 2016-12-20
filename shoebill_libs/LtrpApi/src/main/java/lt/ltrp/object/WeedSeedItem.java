package lt.ltrp.object;

import lt.ltrp.item.ItemController;
import lt.ltrp.item.ItemFactory;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.15.
 */
public interface WeedSeedItem extends Item {

    static WeedSeedItem create(EventManager eventManager) {
        return ItemFactory.Companion.getInstance().createWeedSeed(eventManager);
    }

    boolean plant(LtrpPlayer player, Inventory inventory);
}
