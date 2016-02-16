package lt.maze.object;

import lt.maze.Constants;
import lt.maze.Functions;
import net.gtaun.shoebill.data.Radius;
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
public class DynamicCheckpoint implements StreamerItem {

    private static Collection<DynamicCheckpoint> checkpoints = new ArrayList<>();

    public static Collection<DynamicCheckpoint> get() {
        return checkpoints;
    }

    public static DynamicCheckpoint get(int id) {
        Optional<DynamicCheckpoint> co = checkpoints.stream().filter(l -> l.getId() == id).findFirst();
        return co.isPresent() ? co.get() : null;
    }

    public static void toggleAll(Player p, boolean toggle) {
        checkpoints.forEach(cp -> cp.toggle(p, toggle));
    }

    public static DynamicCheckpoint getVisible(Player p) {
        return  get(Functions.GetPlayerVisibleDynamicCP(p.getId()));
    }

    //CreateDynamicCP(float x, float y, float z, float size, int worldid, int interiorid, int playerid, float streamdistance);
    public static DynamicCheckpoint create(float x, float y, float z, float size, int worldid, int ineriorid, Player p, float streamdistance) {
        int id = Functions.CreateDynamicCP(x, y, z, size, worldid, ineriorid, p == null ? -1 : p.getId(), streamdistance);
        if(id == Constants.INVALID_STREAMER_ID)
            throw new CreationFailedException("DynamicCheckpoint could not be created");
        DynamicCheckpoint cp = new DynamicCheckpoint(id);
        checkpoints.add(cp);
        return cp;
    }

    public static DynamicCheckpoint create(float x, float y, float z, float size) {
        return create(x, y, z, size, -1, -1, null, Constants.STREAMER_CP_SD);
    }

    public static DynamicCheckpoint create(Vector3D position, float size) {
        return create(position.x, position.y, position.z, size);
    }

    public static DynamicCheckpoint create(Radius radius) {
        return create(radius.x, radius.y, radius.z, radius.radius);
    }

    private int id;
    private boolean destroyed;

    protected DynamicCheckpoint(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isValid() {
        return Functions.IsValidDynamicCP(getId()) == 1;
    }

    public void toggle(Player p, boolean toggle) {
        Functions.TogglePlayerDynamicCP(p.getId(), getId(), toggle ? 1 : 0);
    }

    public boolean isInCp(Player p) {
        return Functions.IsPlayerInDynamicCP(p.getId(), getId()) == 1;
    }

    protected void setDestroyed(boolean set) {
        this.destroyed = set;
    }

    @Override
    public void destroy() {
        this.destroyed = true;
        Functions.DestroyDynamicCP(getId());
        checkpoints.remove(this);
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
