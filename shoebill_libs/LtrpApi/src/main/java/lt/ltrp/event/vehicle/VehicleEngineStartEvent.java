package lt.ltrp.event.vehicle;


import lt.ltrp.Deniable;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.event.vehicle.VehicleEvent;

/**
 * @author Bebras
 *         2015.12.18.
 */
public class VehicleEngineStartEvent extends VehicleEvent implements Deniable {

    private LtrpPlayer player;
    private boolean success;
    private boolean denied;

    public VehicleEngineStartEvent(LtrpVehicle vehicle, LtrpPlayer player, boolean success) {
        super(vehicle);
        this.player = player;
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public LtrpVehicle getVehicle() {
        return (LtrpVehicle)super.getVehicle();
    }

    public LtrpPlayer getPlayer() {
        return player;
    }


    @Override
    public void deny() {
        denied = true;
    }

    @Override
    public boolean isDenied() {
        return denied;
    }
}
