package lt.ltrp.vehicle.event;

import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.PlayerVehicle;
import net.gtaun.shoebill.event.player.PlayerEvent;

/**
 * @author Bebras
 *         2016.03.09.
 */
public class PlayerVehicleEvent extends PlayerEvent{

    private PlayerVehicle vehicle;

    public PlayerVehicleEvent(LtrpPlayer player, PlayerVehicle vehicle) {
        super(player);
        this.vehicle = vehicle;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

    public PlayerVehicle getVehicle() {
        return vehicle;
    }
}
