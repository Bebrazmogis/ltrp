package lt.ltrp.session;

import lt.ltrp.player.object.LtrpPlayer;

import lt.ltrp.object.LtrpVehicle;
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
