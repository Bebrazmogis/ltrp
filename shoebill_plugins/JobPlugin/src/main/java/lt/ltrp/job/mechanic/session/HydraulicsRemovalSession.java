package lt.ltrp.job.mechanic.session;

import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.28.
 */
public class HydraulicsRemovalSession extends AbstractRepairSession {

    public static final int DURATION = 100;

    public HydraulicsRemovalSession(EventManager eventManager, LtrpPlayer player, LtrpVehicle vehicle, RepairSessionEndHandler handler) {
        super(eventManager, DURATION, player, vehicle, handler);
    }
}
