package lt.maze.streamer.object;

import lt.maze.streamer.Constants;
import lt.maze.streamer.Functions;
import lt.maze.streamer.constant.StreamerAreaType;
import net.gtaun.shoebill.data.Area3D;
import net.gtaun.shoebill.exception.CreationFailedException;
import net.gtaun.shoebill.entities.Player;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class DynamicCuboid extends AbstractDynamicArea {


    //CreateDynamicCuboid(float minx, float miny, float minz, float maxx, float maxy, float maxz, int worldid, int interiorid, int playerid);
    public static DynamicCuboid create(float minx, float miny, float minz, float maxx, float maxy, float maxz, int worldid, int interiorid, Player p) {
        int id = Functions.CreateDynamicCuboid(minx, miny, minz, maxx, maxy, maxz, worldid, interiorid, p == null ? -1 : p.getId());
        if(id == Constants.INVALID_STREAMER_ID) {
            throw new CreationFailedException("DynamicCuboic could not be created");
        }
        return new DynamicCuboid(id);
    }

    public static DynamicCuboid create(Area3D area, int worldid, int interiorid, Player p) {
        return create(area.minX, area.minY, area.minZ, area.maxX, area.maxY, area.maxZ, worldid, interiorid, p);
    }

    public static DynamicCuboid create(Area3D area) {
        return create(area, -1, -1, null);
    }

    private DynamicCuboid(int id) {
        super(id, StreamerAreaType.Cuboid);
    }


}
