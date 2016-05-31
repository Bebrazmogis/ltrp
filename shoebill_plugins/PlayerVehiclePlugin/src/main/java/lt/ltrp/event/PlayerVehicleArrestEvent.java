package lt.ltrp.event;


import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PlayerVehicle;

/**
 * @author Bebras
 *         2016.03.18.
 */
public class PlayerVehicleArrestEvent extends PlayerVehicleEvent {

    private String reason;

    public PlayerVehicleArrestEvent(LtrpPlayer player, PlayerVehicle vehicle, String reason) {
        super(player, vehicle);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
