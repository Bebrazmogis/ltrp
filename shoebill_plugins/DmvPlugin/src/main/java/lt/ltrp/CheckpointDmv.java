package lt.ltrp;


import lt.ltrp.data.dmv.DmvCheckpoint;
import lt.ltrp.object.LtrpPlayer;import lt.ltrp.object.LtrpVehicle;import net.gtaun.util.event.EventManager;

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
