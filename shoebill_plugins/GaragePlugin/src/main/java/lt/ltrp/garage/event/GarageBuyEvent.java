package lt.ltrp.garage.event;

import lt.ltrp.object.Garage;
import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.20.
 */
public class GarageBuyEvent extends GarageEvent {

    private LtrpPlayer oldOwner;
    private LtrpPlayer newOwner;

    public GarageBuyEvent(Garage garage, LtrpPlayer oldOwner, LtrpPlayer newOwner) {
        super(garage);
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
