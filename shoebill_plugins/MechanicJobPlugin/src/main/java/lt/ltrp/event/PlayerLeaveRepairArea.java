package lt.ltrp.event;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.event.player.PlayerEvent;

/**
 * @author Bebras
 *         2016.02.09.
 */
public class PlayerLeaveRepairArea extends PlayerEvent {


    private LtrpVehicle vehicle;
    private float distance;

    public PlayerLeaveRepairArea(LtrpPlayer player, LtrpVehicle vehicle, float distance) {
        super(player);
        this.vehicle = vehicle;
        this.distance = distance;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer) super.getPlayer();
    }

    public LtrpVehicle getVehicle() {
        return vehicle;
    }

    public float getDistance() {
        return distance;
    }
}
