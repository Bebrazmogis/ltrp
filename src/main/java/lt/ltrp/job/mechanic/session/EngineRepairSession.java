package lt.ltrp.job.mechanic.session;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
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
