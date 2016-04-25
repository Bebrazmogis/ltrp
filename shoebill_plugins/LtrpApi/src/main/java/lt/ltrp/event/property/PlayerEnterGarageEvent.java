package lt.ltrp.event.property;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.Garage;
import lt.ltrp.object.LtrpVehicle;

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
