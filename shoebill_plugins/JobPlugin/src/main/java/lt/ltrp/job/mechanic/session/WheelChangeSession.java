package lt.ltrp.job.mechanic.session;

import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.28.
 */
public class WheelChangeSession extends AbstractRepairSession {

    static final int DURATION = 120;

    public WheelChangeSession(EventManager eventManager, LtrpPlayer player, LtrpVehicle vehicle, RepairSessionEndHandler handler) {
        super(eventManager, DURATION, player, vehicle, handler);
    }
}
