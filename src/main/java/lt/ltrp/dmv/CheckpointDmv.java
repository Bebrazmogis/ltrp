package lt.ltrp.dmv;


import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.util.event.Event;
import net.gtaun.util.event.EventManager;

import java.util.List;

/**
 * @author Bebras
 *         2015.12.23.
 */
public interface CheckpointDmv extends Dmv{

    List<DmvCheckpoint> getCheckpoints();
    void setCheckpoints(List<DmvCheckpoint> checkpoints);

    AbstractCheckpointTest startCheckpointTest(LtrpPlayer player, LtrpVehicle vehicle, EventManager eventManager);
    int getCheckpointTestPrice();

}
