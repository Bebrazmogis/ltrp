package lt.ltrp.vehicle.event;

import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.constant.PlayerVehiclePermission;
import lt.ltrp.vehicle.object.PlayerVehicle;

/**
 * @author Bebras
 *         2016.03.11.
 */
public class PlayerVehicleRemovePermissionEvent extends PlayerVehicleEvent {

    private int target;
    private PlayerVehiclePermission permission;

    public PlayerVehicleRemovePermissionEvent(LtrpPlayer player, PlayerVehicle vehicle, int target, PlayerVehiclePermission permission) {
        super(player, vehicle);
        this.target = target;
        this.permission = permission;
    }

    public int getTarget() {
        return target;
    }

    public PlayerVehiclePermission getPermission() {
        return permission;
    }
}
