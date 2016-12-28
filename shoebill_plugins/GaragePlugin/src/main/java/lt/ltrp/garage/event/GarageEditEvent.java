package lt.ltrp.garage.event;

import lt.ltrp.object.Garage;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.05.19.
 */
public class GarageEditEvent extends GarageEvent {

    private LtrpPlayer player;

    public GarageEditEvent(Garage garage, LtrpPlayer player) {
        super(garage);
        this.player = player;
    }

    public LtrpPlayer getPlayer() {
        return player;
    }
}
