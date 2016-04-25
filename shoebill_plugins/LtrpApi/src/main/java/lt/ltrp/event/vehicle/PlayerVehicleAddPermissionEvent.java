package lt.ltrp.event.vehicle;


import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PlayerVehicle;
import lt.ltrp.constant.PlayerVehiclePermission;

/**
 * @author Bebras
 *         2016.03.11.
 */
public class PlayerVehicleAddPermissionEvent extends PlayerVehicleEvent {

    private int target;
    private PlayerVehiclePermission permission;

    public PlayerVehicleAddPermissionEvent(LtrpPlayer player, PlayerVehicle vehicle, int target, PlayerVehiclePermission permission) {
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
