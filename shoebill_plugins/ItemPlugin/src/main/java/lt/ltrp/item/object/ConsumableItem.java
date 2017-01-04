package lt.ltrp.object;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface ConsumableItem extends Item {

    public int getDosesLeft();
    public void setDosesLeft(int dosesLeft);
    boolean use(LtrpPlayer player , Inventory inventory);

}
