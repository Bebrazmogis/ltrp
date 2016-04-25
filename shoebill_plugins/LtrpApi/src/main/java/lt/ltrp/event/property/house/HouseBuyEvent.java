package lt.ltrp.event.property.house;

import lt.ltrp.object.House;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.20.
 */
public class HouseBuyEvent extends HouseEvent {

    private LtrpPlayer oldOwner;
    private LtrpPlayer newOwner;

    public HouseBuyEvent(House house, LtrpPlayer oldOwner, LtrpPlayer newOwner) {
        super(house);
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
    }

    public LtrpPlayer getOldOwner() {
        return oldOwner;
    }

    public LtrpPlayer getNewOwner() {
        return newOwner;
    }
}
