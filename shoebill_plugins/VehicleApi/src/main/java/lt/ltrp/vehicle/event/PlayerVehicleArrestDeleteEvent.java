package lt.ltrp.vehicle.event;


import lt.ltrp.player.event.PlayerEvent;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.data.PlayerVehicleArrest;

/**
 * @author Bebras
 *         2016.03.18.
 */
public class PlayerVehicleArrestDeleteEvent extends PlayerEvent {

    private PlayerVehicleArrest arrest;

    public PlayerVehicleArrestDeleteEvent(LtrpPlayer player, PlayerVehicleArrest arrest) {
        super(player);
        this.arrest = arrest;
    }


    public PlayerVehicleArrest getArrest() {
        return arrest;
    }
}
