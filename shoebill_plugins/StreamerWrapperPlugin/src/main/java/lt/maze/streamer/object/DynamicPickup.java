package lt.maze.streamer.object;

import lt.maze.streamer.Constants;
import lt.maze.streamer.Functions;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.exception.CreationFailedException;
import net.gtaun.shoebill.object.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class DynamicPickup implements StreamerItem {

    private static final Collection<DynamicPickup> pickups = new ArrayList<>();

    public static Collection<DynamicPickup> get() {
        return pickups;
    }

    public static DynamicPickup get(int id) {
        Optional<DynamicPickup> pickup = pickups.stream().filter(p -> p.getId() == id).findFirst();
        return pickup.isPresent() ? pickup.get() : null;
    }


    // CreateDynamicPickup(int modelid, int type, float x, float y, float z, int worldid, int interiorid, int playerid, float streamdistance);
    public static DynamicPickup create(int modelid, int type, float x, float y, float z, int worldid, int interiorid, Player p, float streamdistance) {
        int id = Functions.CreateDynamicPickup(modelid, type, x, y, z, worldid, interiorid, p == null ? -1 : p.getId(), streamdistance);
        if(id == Constants.INVALID_STREAMER_ID)
            throw new CreationFailedException("DynamicPickup could not be created");
        DynamicPickup pickup = new DynamicPickup(id);
        pickups.add(pickup);
        return pickup;
    }

    public static DynamicPickup create(int modelid, int type, Vector3D position) {
        return create(modelid, type, position.x, position.y, position.z, -1, -1, null, Constants.STREAMER_PICKUP_SD);
    }

    private int id;
    private boolean destroyed;

    public DynamicPickup(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isValid() {
        return Functions.IsValidDynamicPickup(getId()) == 1;
    }

    @Override
    public void destroy() {
        destroyed = true;
        Functions.DestroyDynamicPickup(getId());
        pickups.remove(this);
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
