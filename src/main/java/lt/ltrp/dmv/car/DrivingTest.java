package lt.ltrp.dmv.car;


import lt.ltrp.dmv.AbstractCheckpointTest;
import lt.ltrp.dmv.event.PlayerDrivingTestEndEvent;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.event.SpeedometerTickEvent;

import lt.ltrp.vehicle.object.LtrpVehicle;
import net.gtaun.shoebill.object.VehicleParam;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerEntry;

public class DrivingTest extends AbstractCheckpointTest {

    public static final float MAX_SPEED = 55f;
    public static final int PRICE = 100;


    private float maxSpeed;
    private boolean seatbelt;
    private int lights;
    private HandlerEntry onSpeedometerTickEntry;


    public static DrivingTest create(LtrpPlayer p, LtrpVehicle vehicle, CarDmv dmv, EventManager manager) {
        return new DrivingTest(p, vehicle, dmv, manager);
    }


    private DrivingTest(LtrpPlayer p, LtrpVehicle vehicle, CarDmv dmv, EventManager manager) {
        super(p, vehicle, dmv, manager);
        System.out.println(String.format("DrivingTest constructor. P:%s vehicle:%s dmv:%s", p, vehicle, dmv));

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
        System.out.println("DrivingTest onFinish");
        onSpeedometerTickEntry.cancel();

        lights = getVehicle().getState().getLights();
        seatbelt = getPlayer().isSeatbelt();
        System.out.println("DrivingTest. lights" + lights + "seatbelt:" + seatbelt);

        System.out.println("MAX_SPEED:" + MAX_SPEED + " MAX_SPEED plusfive:" + (MAX_SPEED + 5f) + " actual max speed: "+ maxSpeed + "rounded max speed:" + Math.round(maxSpeed));
        // The 5 is allowed as a margin of error, even the speed limited can't keep up :(
        if (Math.round(maxSpeed) > MAX_SPEED + 5f) {
            setPassed(false);
        } else if (!seatbelt) {
            setPassed(false);
        } else if (lights != VehicleParam.PARAM_ON) {
            setPassed(false);
        }
        super.onFinish();
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

    public float getMaxAllowedSpeed() {
        return MAX_SPEED;
    }

    @Override
    protected void dispatchEvent() {
        getEventManager().dispatchEvent(new PlayerDrivingTestEndEvent(getPlayer(), getDmv(), this));
    }

}