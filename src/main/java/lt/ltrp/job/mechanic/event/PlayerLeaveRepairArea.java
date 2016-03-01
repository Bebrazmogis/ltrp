package lt.ltrp.job.mechanic.event;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
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
