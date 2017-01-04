package lt.ltrp.garage.event;

import lt.ltrp.object.Garage;
import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.20.
 */
public class GarageLockToggleEvent extends GarageEvent {

    private boolean locked;
    private LtrpPlayer player;

    public GarageLockToggleEvent(Garage garage, LtrpPlayer player, boolean locked) {
        super(garage);
        this.locked = locked;
        this.player = player;
    }

    public boolean isLocked() {
        return locked;
    }

    public LtrpPlayer getPlayer() {
        return player;
    }
}
