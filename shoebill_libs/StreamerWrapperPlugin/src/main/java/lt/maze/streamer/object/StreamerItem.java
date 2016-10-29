package lt.maze.streamer.object;

import lt.maze.streamer.constant.StreamerType;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.02.16.
 */
public interface StreamerItem extends Destroyable {

    /**
     * Return the streamer plugin assigned ID
     * @return
     */
    int getId();
    boolean isVisible(Player p);
    StreamerType getType();
}
