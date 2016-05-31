package lt.ltrp.event;

import lt.ltrp.event.vehicle.VehicleEvent;
import lt.ltrp.object.PlayerVehicle;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class PlayerVehicleDestroyEvent extends VehicleEvent {


    public PlayerVehicleDestroyEvent(PlayerVehicle vehicle) {
        super(vehicle);
    }

    @Override
    public PlayerVehicle getVehicle() {
        return (PlayerVehicle)super.getVehicle();
    }
}
