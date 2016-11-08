package lt.ltrp.event.property.garage;

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
