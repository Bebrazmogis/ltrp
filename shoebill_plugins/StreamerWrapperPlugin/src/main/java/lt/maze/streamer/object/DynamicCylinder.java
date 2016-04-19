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
public class DynamicCylinder extends AbstractDynamicArea {

    //CreateDynamicCylinder(Float:x, Float:y, Float:minz, Float:maxz, Float:size, worldid = -1, interiorid = -1, playerid = -1);
    public static DynamicCylinder create(float x, float y, float minz, float maxz, float size, int worldid, int interiorid, Player p) {
        int id = Functions.CreateDynamicCylinder(x, y, minz, maxz, size, worldid, interiorid, p == null ? -1 : p.getId());
        if(id == Constants.INVALID_STREAMER_ID)
            throw new CreationFailedException("DynamicCylinder could not be created");
        return new DynamicCylinder(id);
    }

    public static DynamicCylinder create(Vector2D position, float minz, float maxz, float size, int worldid, int interiorid, Player p) {
        return create(position.x, position.y, minz, maxz, size, worldid, interiorid, p);
    }

    public static DynamicCylinder create(Vector2D position, float minz, float maxz, float size) {
        return create(position, minz, maxz, size, -1, -1, null);
    }

    private DynamicCylinder(int id) {
        super(id, StreamerAreaType.Cylinder);
    }


}
