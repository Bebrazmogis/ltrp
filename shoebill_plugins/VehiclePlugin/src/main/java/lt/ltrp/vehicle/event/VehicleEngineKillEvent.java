package lt.ltrp.vehicle.event;


import lt.ltrp.Deniable;
import lt.ltrp.vehicle.object.LtrpVehicle;
import net.gtaun.shoebill.entities.Player;

/**
 * @author Bebras
 *         2016.02.14.
 */
public class VehicleEngineKillEvent extends VehicleEvent implements Deniable {

    private Player player;
    private boolean denied;

    public VehicleEngineKillEvent(LtrpVehicle vehicle, Player player) {
        super(vehicle);
        this.player =player;
        this.denied = false;
    }

    public Player getPlayer() {
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
