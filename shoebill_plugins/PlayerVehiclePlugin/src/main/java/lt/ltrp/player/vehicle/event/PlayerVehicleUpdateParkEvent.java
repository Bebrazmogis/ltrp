package lt.ltrp.player.vehicle.event;


import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.vehicle.object.PlayerVehicle;
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
