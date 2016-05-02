package lt.ltrp.object;

import net.gtaun.shoebill.constant.SpecialAction;

/**
 * @author Bebras
 *         2016.04.30.
 */
public interface DrinkItem extends Item {

    boolean use(LtrpPlayer player, Inventory inventory);
    SpecialAction getSpecialAction();
}
