package lt.ltrp.garage.event;

import lt.ltrp.event.property.PropertyEvent;
import lt.ltrp.object.Garage;

/**
 * @author Bebras
 *         2016.04.20.
 */
public class GarageEvent extends PropertyEvent {


    public GarageEvent(Garage garage) {
        super(garage);
    }

    @Override
    public Garage getProperty() {
        return (Garage)super.getProperty();
    }
}
