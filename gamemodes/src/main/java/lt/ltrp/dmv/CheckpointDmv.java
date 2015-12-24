package lt.ltrp.dmv;

import lt.ltrp.player.LtrpPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.12.23.
 */
public abstract class CheckpointDmv {

    private List<DmvCheckpoint> checkpoints;


    public CheckpointDmv() {
        this.checkpoints = new ArrayList<>();
    }


    public void startCheckpointCourse(LtrpPlayer player) {

    }

}
