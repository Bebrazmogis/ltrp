package lt.ltrp.vehicle.event;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.PlayerVehicleArrest;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

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

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer) super.getPlayer();
    }

    public PlayerVehicleArrest getArrest() {
        return arrest;
    }
}
