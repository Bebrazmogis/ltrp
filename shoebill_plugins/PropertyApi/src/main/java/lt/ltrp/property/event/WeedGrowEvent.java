package lt.ltrp.property.event;


import lt.ltrp.property.data.HouseWeedSapling;
import lt.ltrp.property.object.House;

/**
 * @author Bebras
 *         2015.12.05.
 */
public class WeedGrowEvent extends HouseEvent {

    private HouseWeedSapling sapling;
    private boolean fullyGrown;


    public WeedGrowEvent(House h, HouseWeedSapling sapling, boolean fullyGrown) {
        super(h);
        this.sapling = sapling;
        this.fullyGrown = fullyGrown;
    }

    public HouseWeedSapling getSapling() {
        return sapling;
    }

    public boolean isFullyGrown() {
        return fullyGrown;
    }
}
