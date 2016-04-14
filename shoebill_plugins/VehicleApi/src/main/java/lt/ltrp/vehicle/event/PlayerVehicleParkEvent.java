package lt.ltrp.vehicle.event;


import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.object.PlayerVehicle;

/**
 * @author Bebras
 *         2016.03.11.
 */
public class PlayerVehicleParkEvent extends PlayerVehicleEvent {

    public PlayerVehicleParkEvent(LtrpPlayer player, PlayerVehicle vehicle) {
        super(player, vehicle);
    }
}
