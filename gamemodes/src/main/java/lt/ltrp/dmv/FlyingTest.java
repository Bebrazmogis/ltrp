package lt.ltrp.dmv;

import lt.ltrp.dmv.event.PlayerFlyingTestEnd;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import lt.ltrp.vehicle.event.SpeedometerTickEvent;
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
    public static final int PRICE = 300;

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
        Optional<DmvCheckpoint> optional = getDmv().getCheckpoints().stream().min((o1, o2) -> (int)(o2.getRadius().getZ() - o1.getRadius().getZ()));
        if(optional.isPresent()) {
            return optional.get().getRadius().getZ() - MIN_Z_MARGIN;
        } else {
            return 0.0f;
        }
    }

    @Override
    protected void dispatchEvent() {
        getEventManager().dispatchEvent(new PlayerFlyingTestEnd(getPlayer(), getDmv(), this));
    }


}
