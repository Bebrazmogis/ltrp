package lt.ltrp.dmv;

import net.gtaun.shoebill.object.Checkpoint;

/**
 * @author Bebras
 *         2015.12.13.
 */
public class DmvCheckpoint {

    private int id;
    private Checkpoint checkpoint;

    public DmvCheckpoint(int id, Checkpoint checkpoint) {
        this.id = id;
        this.checkpoint = checkpoint;
    }

    public DmvCheckpoint() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Checkpoint getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(Checkpoint checkpoint) {
        this.checkpoint = checkpoint;
    }
}
