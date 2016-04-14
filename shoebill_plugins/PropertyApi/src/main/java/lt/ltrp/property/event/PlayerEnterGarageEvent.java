package lt.ltrp.property.event;

import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.property.object.Garage;
import lt.ltrp.vehicle.object.LtrpVehicle;

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
