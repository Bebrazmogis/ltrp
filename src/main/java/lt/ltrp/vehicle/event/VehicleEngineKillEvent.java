package lt.ltrp.vehicle.event;

import lt.ltrp.Deniable;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.event.vehicle.VehicleEvent;
import net.gtaun.shoebill.object.Vehicle;

/**
 * @author Bebras
 *         2016.02.14.
 */
public class VehicleEngineKillEvent extends VehicleEvent implements Deniable {

    private LtrpPlayer player;
    private boolean denied;

    public VehicleEngineKillEvent(LtrpVehicle vehicle, LtrpPlayer player) {
        super(vehicle);
        this.player =player;
        this.denied = false;
    }

    @Override
    public LtrpVehicle getVehicle() {
        return (LtrpVehicle)super.getVehicle();
    }

    @Override
    public void deny() {
        this.denied = true;
    }

    @Override
    public boolean isDenied() {
        return denied;
    }
}
