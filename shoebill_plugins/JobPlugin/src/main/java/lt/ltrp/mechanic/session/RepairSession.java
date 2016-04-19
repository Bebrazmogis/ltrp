package lt.ltrp.mechanic.session;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.25.
 */
public abstract class RepairSession extends AbstractRepairSession {


    public RepairSession(EventManager eventManager, int duration, LtrpPlayer player, LtrpVehicle vehicle, RepairSessionEndHandler handler) {
        super(eventManager, duration, player, vehicle, handler);
    }
}
