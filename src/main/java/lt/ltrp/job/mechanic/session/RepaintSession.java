package lt.ltrp.job.mechanic.session;

import lt.ltrp.player.data.Animation;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.08.
 */
public class RepaintSession extends AbstractRepairSession {



    public RepaintSession(LtrpPlayer player, LtrpVehicle vehicle, RepairSessionEndHandler handler, EventManager eventManager) {
        super(eventManager, 120, player, vehicle, handler);
        super.addAnimation(PlayerKey.FIRE, new Animation("SPRAYCAN", "spraycan_fire", true, 1000));
        super.addAnimation(PlayerKey.HANDBRAKE, new Animation("SPRAYCAN", "spraycan_full", true, 1000));
    }





}
