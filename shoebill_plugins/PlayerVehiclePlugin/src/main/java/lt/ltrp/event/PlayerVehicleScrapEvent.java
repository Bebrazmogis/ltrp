package lt.ltrp.event;


import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PlayerVehicle;

/**
 * @author Bebras
 *         2016.03.09.
 */
public class PlayerVehicleScrapEvent extends PlayerVehicleEvent {

    public PlayerVehicleScrapEvent(LtrpPlayer player, PlayerVehicle vehicle) {
        super(player, vehicle);
    }
}
