package lt.ltrp.vehicle.event;

import lt.ltrp.object.DmvVehicle;
import net.gtaun.util.event.Event;

/**
 * @author Bebras
 *         2016.04.20.
 */
public class DmvVehicleDestroyEvent  extends Event{

    private DmvVehicle vehicle;

    public DmvVehicleDestroyEvent(DmvVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public DmvVehicle getVehicle() {
        return vehicle;
    }
}
