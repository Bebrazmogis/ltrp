package lt.ltrp.business.event;

import lt.ltrp.object.Business;
import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessDoorLockToggleEvent extends BusinessEvent {

    private boolean locked;

    public BusinessDoorLockToggleEvent(Business property,LtrpPlayer player, boolean locked) {
        super(property, player);
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }
}
