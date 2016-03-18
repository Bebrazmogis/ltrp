package lt.ltrp.vehicle.event;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.PlayerVehicle;

/**
 * @author Bebras
 *         2016.03.18.
 */
public class PlayerVehicleBuyInsuranceEvent extends PlayerVehicleEvent {

    public PlayerVehicleBuyInsuranceEvent(LtrpPlayer player, PlayerVehicle vehicle) {
        super(player, vehicle);
    }
}
