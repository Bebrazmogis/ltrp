package lt.ltrp.player.vehicle.event;


import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.data.VehicleLock;
import lt.ltrp.player.vehicle.object.PlayerVehicle;


/**
 * @author Bebras
 *         2016.03.18.
 */
public class PlayerVehicleBuyLockEvent extends PlayerVehicleEvent {

    private VehicleLock lock;

    public PlayerVehicleBuyLockEvent(LtrpPlayer player, PlayerVehicle vehicle, VehicleLock lock) {
        super(player, vehicle);
        this.lock = lock;
    }

    public VehicleLock getLock() {
        return lock;
    }
}
