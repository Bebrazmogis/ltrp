package lt.maze.streamer.event;

import lt.maze.streamer.object.AbstractCheckpoint;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.04.19.
 */
public abstract class AbstractPlayerCheckpointEvent extends PlayerEvent {

    private AbstractCheckpoint checkpoint;

    public AbstractPlayerCheckpointEvent(Player player, AbstractCheckpoint checkpoint) {
        super(player);
        this.checkpoint = checkpoint;
    }

    public AbstractCheckpoint getCheckpoint() {
        return checkpoint;
    }
}
