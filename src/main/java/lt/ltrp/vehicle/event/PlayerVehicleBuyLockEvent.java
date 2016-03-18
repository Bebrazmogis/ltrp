package lt.ltrp.vehicle.event;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.PlayerVehicle;
import lt.ltrp.vehicle.VehicleLock;

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
