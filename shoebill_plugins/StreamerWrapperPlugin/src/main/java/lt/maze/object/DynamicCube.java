package lt.maze.object;

import lt.maze.Constants;
import lt.maze.Functions;
import lt.maze.constant.StreamerAreaType;
import net.gtaun.shoebill.data.Area3D;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.exception.CreationFailedException;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class DynamicCube extends AbstractDynamicArea {

    //CreateDynamicCube(float minx, float miny, float minz, float maxx, float maxy, float maxz, int worldid, int interiorid, int playerid);
    public static DynamicCube create(float minx, float miny, float minz, float maxx, float maxy,  float maxz, int worldid, int interiorid, Player p) {
        int id = Functions.CreateDynamicCube(minx, miny, minz, maxx, maxy, maxz, worldid, interiorid, p == null ? -1 : p.getId());
        if(id == Constants.INVALID_STREAMER_ID) {
            throw new CreationFailedException("DynamicCube could not be created");
        }
        return new DynamicCube(id);
    }

    public static DynamicCube create(Area3D area, int worldid, int interiorid, Player p) {
        return create(area.getMinX(), area.getMinY(), area.getMinZ(), area.getMaxX(), area.getMaxY(), area.getMaxZ(), worldid, interiorid, p);
    }

    public static DynamicCube create(Area3D area) {
        return create(area, -1, -1, null);
    }

    /**
     * Creates a dynamic Cube
     * @param location location of the bottom corner of cube
     * @param size length of the cubes side, must be positive
     * @return the created cube
     */
    public static DynamicCube create(Location location, float size) {
        if(size <= 0f) {
            throw new CreationFailedException("Cannot create DynamicCube, size msut be greater than 0.");
        }
        return create(new Area3D(location.x, location.y, location.z, location.x + size, location.y + size, location.z + size), location.getWorldId(), location.getInteriorId(), null);
    }

    public static DynamicCube create(Vector3D position, float size) {
        return create(new Location(position, -1, -1), size);
    }

    private DynamicCube(int id) {
        super(id);
    }

    @Override
    public StreamerAreaType getType() {
        return StreamerAreaType.Cuboid;
    }
}
