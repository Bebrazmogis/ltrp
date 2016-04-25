package lt.ltrp.object;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface DurableItem extends Item {

    void use();
    void setDurability(int durability);
    int getMaxDurability();
    int getDurability();



}
