package lt.ltrp.garage.event;

import lt.ltrp.object.Garage;

/**
 * @author Bebras
 *         2016.05.19.
 */
public class GarageDestroyEvent extends GarageEvent {

    public GarageDestroyEvent(Garage garage) {
        super(garage);
    }


}
