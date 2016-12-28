package lt.ltrp.house.weed.event;


import lt.ltrp.event.property.HouseEvent;
import lt.ltrp.house.object.House;
import lt.ltrp.house.weed.object.HouseWeedSapling;

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
