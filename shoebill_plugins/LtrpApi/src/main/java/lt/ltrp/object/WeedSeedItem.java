package lt.ltrp.object;

import lt.ltrp.ItemController;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.15.
 */
public interface WeedSeedItem extends Item {

    static WeedSeedItem create(EventManager eventManager) {
        return ItemController.get().createWeedSeed(eventManager);
    }

    boolean plant(LtrpPlayer player, Inventory inventory);
}
