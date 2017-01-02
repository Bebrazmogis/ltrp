package lt.ltrp.object.drug;

import lt.ltrp.object.Inventory;
import lt.ltrp.object.Item;
import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface DrugItem extends Item {

    boolean use(LtrpPlayer player, Inventory inventory);

}
