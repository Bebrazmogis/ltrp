package lt.ltrp.event.property;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.property.Garage;
import lt.ltrp.vehicle.LtrpVehicle;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PlayerEnterGarageEvent extends PlayerGarageEvent {

    private LtrpVehicle vehicle;

    public PlayerEnterGarageEvent(LtrpPlayer player, Garage property, LtrpVehicle vehicle) {
        super(player, property);
        this.vehicle = vehicle;
    }


    public LtrpVehicle getVehicle() {
        return vehicle;
    }

}
