package lt.ltrp.event.vehicle;

import lt.ltrp.object.LtrpVehicle;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class VehicleDestroyEvent extends VehicleEvent {


    public VehicleDestroyEvent(LtrpVehicle vehicle) {
        super(vehicle);
    }
}
