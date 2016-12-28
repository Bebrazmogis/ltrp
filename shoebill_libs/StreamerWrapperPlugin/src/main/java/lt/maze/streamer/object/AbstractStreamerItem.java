package lt.maze.streamer.object;

import lt.maze.streamer.Functions;
import lt.maze.streamer.constant.StreamerType;
import net.gtaun.shoebill.entities.Player;

/**
 * @author Bebras
 *         2016.04.19.
 */
public abstract class AbstractStreamerItem implements StreamerItem {

    private int id;
    private StreamerType type;
    private boolean destroyed;


    public AbstractStreamerItem(int id, StreamerType type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public StreamerType getType() {
        return type;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isVisible(Player p) {
        return Functions.Streamer_IsItemVisible(p.getId(), type.getValue(), id) == 1;
    }

    @Override
    public void destroy() {
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
