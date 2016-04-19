package lt.maze.streamer.object;

import lt.maze.streamer.Constants;
import lt.maze.streamer.Functions;
import lt.maze.streamer.constant.StreamerAreaType;
import net.gtaun.shoebill.data.Vector2D;
import net.gtaun.shoebill.exception.CreationFailedException;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class DynamicCircle extends AbstractDynamicArea {

    public static DynamicCircle create(float x, float y, float size, int worldid, int interiorid, Player p) {
        int id = Functions.CreateDynamicCircle(x, y, size, worldid, interiorid, p == null ? -1 : p.getId());
        if(id == Constants.INVALID_STREAMER_ID)
            throw new CreationFailedException("DynamicCircle could not be created");
         return new DynamicCircle(id);
    }

    public static DynamicCircle create(Vector2D pos, float size, int worldid, int interiorid, Player p) {
        return create(pos.x, pos.y, size, worldid, interiorid, p);
    }

    public static DynamicCircle create(Vector2D pos, float size) {
        return create(pos, size, -1, -1, null);
    }

    private DynamicCircle(int id) {
        super(id, StreamerAreaType.Circle);
    }

}
