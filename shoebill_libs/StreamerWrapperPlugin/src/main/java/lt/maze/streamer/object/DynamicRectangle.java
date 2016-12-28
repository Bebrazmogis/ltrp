package lt.maze.streamer.object;

import lt.maze.streamer.Constants;
import lt.maze.streamer.Functions;
import lt.maze.streamer.constant.StreamerAreaType;
import net.gtaun.shoebill.data.Area;
import net.gtaun.shoebill.exception.CreationFailedException;
import net.gtaun.shoebill.entities.Player;

/**
 * @author Bebras
 *         2016.02.16.
 */
class DynamicRectangle extends AbstractDynamicArea {

    //CreateDynamicRectangle(float minx, float miny, float maxx, float maxy, int worldid, int interiorid, int playerid);
    public static DynamicRectangle create(float minx, float miny, float maxx, float maxy, int worldid, int interiorid, Player p) {
        int id = Functions.CreateDynamicRectangle(minx, miny, maxx, maxy, worldid, interiorid, p == null ? -1 : p.getId());
        if(id == Constants.INVALID_STREAMER_ID)
            throw new CreationFailedException("DynamicRectangle could not be created");
        return new DynamicRectangle(id);
    }

    public static DynamicRectangle create(Area area, int worldid, int interiorid, Player p) {
        return create(area.minX, area.minY, area.maxX, area.maxY, worldid, interiorid, p);
    }

    public static DynamicRectangle create(Area area, int worldid, int interiorid) {
        return create(area, worldid, interiorid, null);
    }

    public static DynamicRectangle create(Area area) {
        return create(area, -1, -1);
    }

    private DynamicRectangle(int id) {
        super(id, StreamerAreaType.Rectangle);
    }


}
