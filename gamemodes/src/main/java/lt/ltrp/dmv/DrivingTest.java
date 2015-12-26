package lt.ltrp.dmv;


import lt.ltrp.dmv.event.PlayerDrivingTestEndEvent;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import lt.ltrp.vehicle.event.SpeedometerTickEvent;

import net.gtaun.shoebill.object.VehicleParam;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerEntry;

public class DrivingTest extends AbstractCheckpointTest {

    public static final float MAX_SPEED = 50f;
    public static final int PRICE = 100;


    private float maxSpeed;
    private boolean seatbelt;
    private int lights;
    private HandlerEntry onSpeedometerTickEntry;


    public static DrivingTest create(LtrpPlayer p, LtrpVehicle vehicle, CheckpointDmv dmv, EventManager manager) {
        return new DrivingTest(p, vehicle, dmv, manager);
    }


    private DrivingTest(LtrpPlayer p, LtrpVehicle vehicle, CheckpointDmv dmv, EventManager manager) {
        super(p, vehicle, dmv, manager);

        onSpeedometerTickEntry = getEventManager().registerHandler(SpeedometerTickEvent.class, e -> {
            LtrpVehicle v = e.getVehicle();
            if (v.equals(vehicle)) {
                float speed = v.getSpeed();
                if (speed > maxSpeed) {
                    maxSpeed = speed;
                }
            }
        });
    }


    @Override
    protected void onFinish() {
        super.onFinish();
        onSpeedometerTickEntry.cancel();

        lights = getVehicle().getState().getLights();
        seatbelt = getPlayer().getSeatbelt();

        if (maxSpeed < MAX_SPEED) {
            setPassed(false);
        } else if (!seatbelt) {
            setPassed(false);

        } else if (lights != VehicleParam.PARAM_ON) {
            setPassed(false);
        }
    }


    public boolean isSeatbelt() {
        return seatbelt;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public int getLights() {
        return lights;
    }

    @Override
    protected void dispatchEvent() {
        getEventManager().dispatchEvent(new PlayerDrivingTestEndEvent(getPlayer(), getDmv(), this));
    }

}