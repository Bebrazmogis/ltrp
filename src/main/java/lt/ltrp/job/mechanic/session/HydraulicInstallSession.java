package lt.ltrp.job.mechanic.session;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.28.
 */
public class HydraulicInstallSession extends AbstractRepairSession {

    public static final int DURATION = 140;

    public HydraulicInstallSession(EventManager eventManager, LtrpPlayer player, LtrpVehicle vehicle, RepairSessionEndHandler handler) {
        super(eventManager, DURATION, player, vehicle, handler);
    }
}
