package lt.ltrp.vehicle.event;

import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.PlayerVehicle;

/**
 * @author Bebras
 *         2016.03.09.
 */
public class PlayerVehicleScrapEvent extends PlayerVehicleEvent {

    public PlayerVehicleScrapEvent(LtrpPlayer player, PlayerVehicle vehicle) {
        super(player, vehicle);
    }
}
