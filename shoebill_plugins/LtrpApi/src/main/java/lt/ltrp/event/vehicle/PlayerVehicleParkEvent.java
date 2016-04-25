package lt.ltrp.event.vehicle;


import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PlayerVehicle;

/**
 * @author Bebras
 *         2016.03.11.
 */
public class PlayerVehicleParkEvent extends PlayerVehicleEvent {

    public PlayerVehicleParkEvent(LtrpPlayer player, PlayerVehicle vehicle) {
        super(player, vehicle);
    }
}
