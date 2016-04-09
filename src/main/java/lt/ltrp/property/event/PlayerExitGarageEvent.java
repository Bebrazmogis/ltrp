package lt.ltrp.property.event;

import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.property.Garage;
import lt.ltrp.vehicle.LtrpVehicle;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PlayerExitGarageEvent extends PlayerGarageEvent {

    private LtrpVehicle vehicle;

    public PlayerExitGarageEvent(LtrpPlayer player, Garage property, LtrpVehicle vehicle) {
        super(player, property);
        this.vehicle = vehicle;
    }

    public LtrpVehicle getVehicle() {
        return vehicle;
    }
}
