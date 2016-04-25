package lt.ltrp.object;


import lt.ltrp.data.dmv.DmvCheckpoint;
import net.gtaun.util.event.EventManager;

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
