package lt.ltrp.player.vehicle.event;


import lt.ltrp.event.player.PlayerEvent;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.vehicle.object.PlayerVehicle;

/**
 * @author Bebras
 *         2016.03.09.
 */
public class PlayerVehicleEvent extends PlayerEvent {

    private PlayerVehicle vehicle;

    public PlayerVehicleEvent(LtrpPlayer player, PlayerVehicle vehicle) {
        super(player);
        this.vehicle = vehicle;
    }

    public PlayerVehicle getVehicle() {
        return vehicle;
    }
}
