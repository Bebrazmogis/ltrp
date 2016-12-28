package lt.maze.streamer.object;

import lt.maze.streamer.constant.StreamerType;
import net.gtaun.shoebill.entities.Destroyable;
import net.gtaun.shoebill.entities.Player;

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
