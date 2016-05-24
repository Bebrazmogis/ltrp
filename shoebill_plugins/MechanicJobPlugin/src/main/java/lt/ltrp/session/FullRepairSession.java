package lt.ltrp.session;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.28.
 */
public class FullRepairSession extends RepairSession {

    public FullRepairSession(EventManager eventManager, LtrpPlayer player, LtrpVehicle vehicle, RepairSessionEndHandler handler) {
        super(eventManager, EngineRepairSession.REPAIR_DURATION + HullRepairSession.REPAIR_DURATION - 10, player, vehicle, handler);
    }
}
