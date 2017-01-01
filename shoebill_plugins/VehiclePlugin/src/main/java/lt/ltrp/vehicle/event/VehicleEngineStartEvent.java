package lt.ltrp.vehicle.event;


import lt.ltrp.Deniable;
import lt.ltrp.vehicle.object.LtrpVehicle;
import net.gtaun.shoebill.entities.Player;

/**
 * @author Bebras
 *         2015.12.18.
 */
public class VehicleEngineStartEvent extends VehicleEvent implements Deniable {

    private Player player;
    private boolean success;
    private boolean denied;

    public VehicleEngineStartEvent(LtrpVehicle vehicle, Player player, boolean success) {
        super(vehicle);
        this.player = player;
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public Player getPlayer() {
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
