package lt.ltrp.mechanic.session;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.28.
 */
public class HullRepairSession extends RepairSession {

    static final int REPAIR_DURATION = 90;

    public HullRepairSession(EventManager eventManager, LtrpPlayer player, LtrpVehicle vehicle, RepairSessionEndHandler handler) {
        super(eventManager, REPAIR_DURATION, player, vehicle, handler);
    }
}
