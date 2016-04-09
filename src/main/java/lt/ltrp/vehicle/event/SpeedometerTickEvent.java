package lt.ltrp.vehicle.event;

import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.event.player.PlayerEvent;

/**
 * @author Bebras
 *         2015.12.14.
 */
public class SpeedometerTickEvent extends PlayerEvent {

    private LtrpVehicle vehicle;
    private float speed;

    public SpeedometerTickEvent(LtrpPlayer player, LtrpVehicle vehicle, float speed) {
        super(player);
        this.vehicle = vehicle;
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public LtrpVehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(LtrpVehicle vehicle) {
        this.vehicle = vehicle;
    }
}
