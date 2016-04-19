package lt.maze.streamer.object;

import lt.maze.streamer.Constants;
import lt.maze.streamer.Functions;
import lt.maze.streamer.constant.StreamerAreaType;
import net.gtaun.shoebill.exception.CreationFailedException;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.03.29.
 */
public class DynamicPolygon extends AbstractDynamicArea {

    //int CreateDynamicPolygon(float points[], float minz = -FLOAT_INFINITY, float maxz = FLOAT_INFINITY, maxpoints = sizeof points, worldid, interiorid, playerid);
    public static DynamicPolygon create(float[] points, float minz, float maxz, int worldid, int interiorid, Player p) {
        int id;
        try {
            id = Functions.CreateDynamicPolygon(points, minz, maxz, points.length, worldid, interiorid, p == null ? -1 : p.getId());
            if(id == Constants.INVALID_STREAMER_ID) {
                throw new CreationFailedException("DynamicCube could not be created");
            }
        } catch(NullPointerException e) {
            throw new CreationFailedException("CreateDynamicPolygon is not supported by Shoebill plugin");
        }
        return new DynamicPolygon(id, points);
    }

    public static DynamicPolygon create(float points[]) {
        return create(points, 0, 0);
    }

    public static DynamicPolygon create(float points[], int worldId, int interiorId) {
        return create(points, -Float.MAX_VALUE, Float.MAX_VALUE, worldId, interiorId, null);
    }

    public static DynamicPolygon create(float points[], int minz, int maxz, int worldid, int interiorid) {
        return create(points, minz, maxz, worldid, interiorid, null);
    }

    private float[] points;

    private DynamicPolygon(int id, float[] points) {
        super(id, StreamerAreaType.Polygon);
        this.points = points;
    }


    public float[] getPoints() {
        return points;
    }
}
