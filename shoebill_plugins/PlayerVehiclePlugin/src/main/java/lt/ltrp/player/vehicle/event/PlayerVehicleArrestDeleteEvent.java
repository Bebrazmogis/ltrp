package lt.ltrp.player.vehicle.event;


import lt.ltrp.event.player.PlayerEvent;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.vehicle.data.PlayerVehicleArrest;

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
