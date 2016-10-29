package lt.maze.streamer.event;

import lt.maze.streamer.object.AbstractCheckpoint;
import net.gtaun.util.event.Event;

/**
 * @author Bebras
 *         2016.04.19.
 */
public abstract class AbstractCheckpointEvent extends Event {

    private AbstractCheckpoint checkpoint;

    public AbstractCheckpointEvent(AbstractCheckpoint checkpoint) {
        this.checkpoint = checkpoint;
    }

    public AbstractCheckpoint getCheckpoint() {
        return checkpoint;
    }
}
