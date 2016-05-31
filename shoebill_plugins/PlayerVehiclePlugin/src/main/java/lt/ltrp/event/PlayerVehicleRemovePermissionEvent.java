package lt.ltrp.event;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.constant.PlayerVehiclePermission;
import lt.ltrp.object.PlayerVehicle;

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
