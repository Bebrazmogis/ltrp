package lt.ltrp.item.object.drug;

import lt.ltrp.item.object.Inventory;
import lt.ltrp.item.object.Item;
import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface DrugItem extends Item {

    boolean use(LtrpPlayer player, Inventory inventory);

}
