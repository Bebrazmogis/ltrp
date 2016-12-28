package lt.maze.streamer.object;

import lt.maze.streamer.constant.StreamerType;
import net.gtaun.shoebill.entities.Player;

/**
 * @author Bebras
 *         2016.04.19.
 */
public abstract class AbstractCheckpoint extends AbstractStreamerItem {

    public AbstractCheckpoint(int id, StreamerType type) {
        super(id, type);
    }

    public abstract boolean isValid();
    public abstract void toggle(Player p, boolean toggle);
    public abstract boolean isInCp(Player p);

}
