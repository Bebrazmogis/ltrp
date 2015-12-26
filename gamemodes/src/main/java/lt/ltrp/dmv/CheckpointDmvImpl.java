package lt.ltrp.dmv;

import net.gtaun.shoebill.data.Location;

import java.util.List;

/**
 * @author Bebras
 *         2015.12.25.
 */
public abstract class CheckpointDmvImpl extends DmvImpl implements CheckpointDmv {

    private List<DmvCheckpoint> checkpoints;

    public CheckpointDmvImpl(Dmv dmv, List<DmvCheckpoint> checkpoints) {
        super(dmv);
        this.checkpoints = checkpoints;
    }

    public CheckpointDmvImpl(int id, String name, Location location, List<DmvCheckpoint> checkpoints) {
        super(id, name, location);
        this.checkpoints = checkpoints;
    }

    @Override
    public List<DmvCheckpoint> getCheckpoints() {
        return checkpoints;
    }

    @Override
    public void setCheckpoints(List<DmvCheckpoint> checkpoints) {
        this.checkpoints = checkpoints;
    }

}
