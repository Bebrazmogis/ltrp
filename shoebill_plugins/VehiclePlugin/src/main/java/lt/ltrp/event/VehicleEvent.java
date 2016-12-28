package lt.ltrp.event;


import lt.ltrp.object.LtrpVehicle;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class VehicleEvent extends net.gtaun.shoebill.event.vehicle.VehicleEvent {


    protected VehicleEvent(LtrpVehicle vehicle) {
        super(vehicle);
    }

    @Override
    public LtrpVehicle getVehicle() {
        return (LtrpVehicle)super.getVehicle();
    }
}
