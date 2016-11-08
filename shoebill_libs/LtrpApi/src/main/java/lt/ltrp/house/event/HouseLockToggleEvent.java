package lt.ltrp.house.event;

import lt.ltrp.house.object.House;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.20.
 */
public class HouseLockToggleEvent extends HouseEvent {

    private boolean locked;

    public HouseLockToggleEvent(House house, LtrpPlayer player, boolean locked) {
        super(house, player);
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }
}
