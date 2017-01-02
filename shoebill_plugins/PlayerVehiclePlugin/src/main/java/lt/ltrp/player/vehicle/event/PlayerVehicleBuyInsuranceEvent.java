package lt.ltrp.player.vehicle.event;


import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.vehicle.object.PlayerVehicle;

/**
 * @author Bebras
 *         2016.03.18.
 */
public class PlayerVehicleBuyInsuranceEvent extends PlayerVehicleEvent {

    public PlayerVehicleBuyInsuranceEvent(LtrpPlayer player, PlayerVehicle vehicle) {
        super(player, vehicle);
    }
}
