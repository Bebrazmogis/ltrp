package lt.ltrp.item.object;

import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface ConsumableItem extends Item {

    public int getDosesLeft();
    public void setDosesLeft(int dosesLeft);
    boolean use(LtrpPlayer player , Inventory inventory);

}
