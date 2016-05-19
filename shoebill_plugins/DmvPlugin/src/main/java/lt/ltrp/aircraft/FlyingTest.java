package lt.ltrp.aircraft;

import lt.ltrp.dmv.AbstractCheckpointTest;
import lt.ltrp.dmv.CheckpointDmv;
import lt.ltrp.dmv.DmvCheckpoint;
import lt.ltrp.event.PlayerFlyingTestEnd;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.event.SpeedometerTickEvent;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerEntry;

import java.util.Optional;

/**
 * @author Bebras
 *         2015.12.27.
 */
public class FlyingTest extends AbstractCheckpointTest {

    /**
     * The maximum offset of the lowest checkpoints Z coordinate that's allowed to passed
     * For example, if the lowest checkpoint is at 5.0f(on Z axis) then the lowest a player is allowed to get is 5 - MIN_Z_MARGIN
     */
    private static final float MIN_Z_MARGIN = 17.0f;

    public static FlyingTest create(LtrpPlayer player, LtrpVehicle vehicle, CheckpointDmv dmv, EventManager eventManager) {
        return new FlyingTest(player, vehicle, dmv, eventManager);
    }

    private float minZ;
    private HandlerEntry speedometerTickEventHandler;


    private FlyingTest(LtrpPlayer player, LtrpVehicle vehicle, CheckpointDmv dmv, EventManager eventManager) {
        super(player, vehicle, dmv, eventManager);

        speedometerTickEventHandler = eventManager.registerHandler(SpeedometerTickEvent.class, e -> {
            LtrpVehicle v = e.getVehicle();
            if(v.getLocation().getZ() < minZ) {
                minZ = v.getLocation().getZ();
            }
        });
    }


    @Override
    protected void onFinish() {
        super.onFinish();

        speedometerTickEventHandler.cancel();

        if (minZ < getAllowedMinZ()) {
            setPassed(false);
        }

    }

    public float getAllowedMinZ() {
        float min = 0f;
        for(DmvCheckpoint checkpoint : getDmv().getCheckpoints()) {
            if(checkpoint.getRadius().getZ() < min) {
                min = checkpoint.getRadius().getZ();
            }
        }
        return min - MIN_Z_MARGIN;
    }

    public float getMinZ() {
        return minZ;
    }

    @Override
    protected void dispatchEvent() {
        getEventManager().dispatchEvent(new PlayerFlyingTestEnd(getPlayer(), getDmv(), this));
    }


}
