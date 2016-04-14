package lt.ltrp.dmv;


import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.object.LtrpVehicle;
import net.gtaun.util.event.EventManager;

import java.util.List;

/**
 * @author Bebras
 *         2015.12.23.
 */
public interface CheckpointDmv extends Dmv{

    DmvCheckpoint[] getCheckpoints();
    void setCheckpoints(DmvCheckpoint[] checkpoints);

    AbstractCheckpointTest startCheckpointTest(LtrpPlayer player, LtrpVehicle vehicle, EventManager eventManager);
    int getCheckpointTestPrice();

}
