package lt.ltrp.session;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.25.
 */
public class EngineRepairSession extends RepairSession {

    static final int REPAIR_DURATION = 120;

    public EngineRepairSession(EventManager eventManager, LtrpPlayer player, LtrpVehicle vehicle, RepairSessionEndHandler handler) {
        super(eventManager, REPAIR_DURATION, player, vehicle, handler);

    }
}
