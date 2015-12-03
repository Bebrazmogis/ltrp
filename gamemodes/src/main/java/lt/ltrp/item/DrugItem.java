package lt.ltrp.item;

import lt.ltrp.player.LtrpPlayer;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class DrugItem extends ConsumableItem {

    public DrugItem(String name, int id, ItemType type, int dosesLeft) {
        super(name, id, type, dosesLeft, true);
    }

    @Override
    public boolean use(LtrpPlayer player, Inventory inventory) {
        boolean success = super.use(player, inventory);
        if(getDosesLeft() == 0) {
            this.destroy();
        }
        return success;
    }
}
