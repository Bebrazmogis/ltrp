package lt.ltrp.boat;

import lt.ltrp.dmv.AbstractCheckpointTest;
import lt.ltrp.dmv.CheckpointDmv;
import lt.ltrp.event.PlayerBoatingTestEnd;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.27.
 */
public class BoatingTest extends AbstractCheckpointTest {


    public static BoatingTest create(LtrpPlayer p, LtrpVehicle vehicle, CheckpointDmv dmv, EventManager manager) {
        return new BoatingTest(p, vehicle, dmv, manager);
    }

    private BoatingTest(LtrpPlayer p, LtrpVehicle vehicle, CheckpointDmv dmv, EventManager manager) {
        super(p, vehicle, dmv, manager);
    }

    @Override
    protected void dispatchEvent() {
        getEventManager().dispatchEvent(new PlayerBoatingTestEnd(getPlayer(), getDmv(), this));
    }
}
