package lt.ltrp.vehicle.event;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.PlayerVehicle;
import net.gtaun.shoebill.event.player.PlayerEvent;

/**
 * @author Bebras
 *         2016.03.11.
 */
public class PlayerVehicleParkEvent extends PlayerVehicleEvent {

    public PlayerVehicleParkEvent(LtrpPlayer player, PlayerVehicle vehicle) {
        super(player, vehicle);
    }
}
