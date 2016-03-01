package lt.ltrp.job.mechanic.session;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
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
