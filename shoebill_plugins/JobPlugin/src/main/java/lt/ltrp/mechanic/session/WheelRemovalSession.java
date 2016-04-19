package lt.ltrp.mechanic.session;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.28.
 */
public class WheelRemovalSession extends AbstractRepairSession {

    static final int DURATION = 60;

    public WheelRemovalSession(EventManager eventManager, LtrpPlayer player, LtrpVehicle vehicle, RepairSessionEndHandler handler) {
        super(eventManager, DURATION, player, vehicle, handler);
    }
}
