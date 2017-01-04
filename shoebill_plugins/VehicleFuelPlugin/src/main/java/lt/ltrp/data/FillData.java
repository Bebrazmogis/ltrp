package lt.ltrp.data;

import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.object.Timer;

/**
 * @author Bebras
 *         2016.06.06.
 */
public class FillData {

    private LtrpPlayer player;
    private LtrpVehicle vehicle;
    private float fuel;
    private Timer timer;

    public FillData(LtrpVehicle vehicle, LtrpPlayer player, Timer timer) {
        this.vehicle = vehicle;
        this.player = player;
        this.timer = timer;
    }

    public float getFuel() {
         return fuel;
    }

    public void addFuel(float amount) {
        fuel += amount;
    }

    public LtrpPlayer getPlayer() {
        return player;
    }

    public void setPlayer(LtrpPlayer player) {
        this.player = player;
    }

    public LtrpVehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(LtrpVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
}
