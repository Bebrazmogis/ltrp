package lt.maze.streamer.object;

import lt.maze.streamer.Constants;
import lt.maze.streamer.Functions;
import lt.maze.streamer.constant.StreamerAreaType;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Radius;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.exception.CreationFailedException;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class DynamicSphere extends AbstractDynamicArea {

    //CreateDynamicSphere(float x, float y, float z, float size, int worldid, int interiorid, int playerid);
    public static DynamicSphere create(float x, float y, float z, float size, int worldid, int interiorid, Player p) {
        int id = Functions.CreateDynamicSphere(x, y, z, size, worldid, interiorid, p == null ? -1 : p.getId());
        if(id == Constants.INVALID_STREAMER_ID)
            throw new CreationFailedException("DynamicSphere could not be created");
        return new DynamicSphere(id, new Radius(x, y, z, interiorid, worldid, size));
    }

    public static DynamicSphere create(Vector3D pos, float size) {
        return create(pos.x, pos.y, pos.z, size, -1, -1, null);
    }

    public static DynamicSphere create(Location location, float size, Player p) {
        return create(location.x, location.y, location.z, size, location.worldId, location.interiorId, p);
    }

    public static DynamicSphere create(Location location, float size) {
        return create(location, size, null);
    }

    private Radius radius;

    private DynamicSphere(int id, Radius radius) {
        super(id);
        this.radius = radius;
    }

    public Radius getRadius() {
        return radius;
    }

    @Override
    public StreamerAreaType getType() {
        return StreamerAreaType.Sphere;
    }
}
