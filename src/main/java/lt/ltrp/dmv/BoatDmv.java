package lt.ltrp.dmv;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.27.
 */
public class BoatDmv extends CheckpointDmvImpl {


    public BoatDmv(int id) {
        super(id, null, null, null);
    }

    @Override
    public AbstractCheckpointTest startCheckpointTest(LtrpPlayer player, LtrpVehicle vehicle, EventManager eventManager) {
        return BoatingTest.create(player, vehicle, this, eventManager);
    }

    @Override
    public int getCheckpointTestPrice() {
        return BoatingTest.PRICE;
    }
}
