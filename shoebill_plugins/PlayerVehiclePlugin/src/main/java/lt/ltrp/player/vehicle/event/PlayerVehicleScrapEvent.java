package lt.ltrp.player.vehicle.event;


import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.vehicle.object.PlayerVehicle;

/**
 * @author Bebras
 *         2016.03.09.
 */
public class PlayerVehicleScrapEvent extends PlayerVehicleEvent {

    public PlayerVehicleScrapEvent(LtrpPlayer player, PlayerVehicle vehicle) {
        super(player, vehicle);
    }
}
