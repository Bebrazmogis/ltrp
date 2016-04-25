package lt.ltrp.event.vehicle;


import lt.ltrp.Deniable;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.event.vehicle.VehicleEvent;

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

    public LtrpPlayer getPlayer() {
        return player;
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
