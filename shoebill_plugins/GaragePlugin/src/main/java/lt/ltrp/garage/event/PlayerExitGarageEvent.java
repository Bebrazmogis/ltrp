package lt.ltrp.garage.event;


import lt.ltrp.event.property.PlayerEnterPropertyEvent;
import lt.ltrp.object.Garage;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PlayerExitGarageEvent extends PlayerEnterPropertyEvent {

    private LtrpVehicle vehicle;

    public PlayerExitGarageEvent(Garage garage, LtrpPlayer player, LtrpVehicle vehicle) {
        super(garage, player);
        this.vehicle = vehicle;
    }

    @Override
    public Garage getProperty() {
        return (Garage)super.getProperty();
    }

    public LtrpVehicle getVehicle() {
        return vehicle;
    }

}
