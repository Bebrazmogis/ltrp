package lt.ltrp.event.vehicle;


import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PlayerVehicle;
import net.gtaun.shoebill.data.AngledLocation;

/**
 * @author Bebras
 *         2016.03.11.
 */
public class PlayerVehicleUpdateParkEvent extends PlayerVehicleEvent {

    private AngledLocation location;

    public PlayerVehicleUpdateParkEvent(LtrpPlayer player, PlayerVehicle vehicle, AngledLocation location) {
        super(player, vehicle);
        this.location = location;
    }

    public AngledLocation getLocation() {
        return location;
    }
}
