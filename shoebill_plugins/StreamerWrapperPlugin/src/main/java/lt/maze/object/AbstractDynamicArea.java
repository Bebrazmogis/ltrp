package lt.maze.object;

import lt.maze.Functions;
import lt.maze.StreamerPlugin;
import lt.maze.constant.StreamerObjectType;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2016.02.16.
 */
public abstract class AbstractDynamicArea implements DynamicArea {

    private int id;
    private boolean destroyed;

    protected AbstractDynamicArea(int id) {
        this.id = id;
        areas.add(this);
    }

    public int getId() {
        return id;
    }

    @Override
    public void toggle(Player p, boolean toggle) {
        Functions.TogglePlayerDynamicArea(p.getId(), id, toggle ? 1 : 0);
    }

    @Override
    public boolean isInArea(Player p) {
        return Functions.IsPlayerInDynamicArea(p.getId(), getId(), 1) == 1;
    }

    @Override
    public boolean contains(float x, float y, float z) {
        return Functions.IsPointInDynamicArea(getId(), x, y, z) == 1;
    }

    @Override
    public boolean contains(float x, float y) {
        return Functions.IsPointInDynamicArea(getId(), x, y, 0f) == 1;
    }

    @Override
    public void attachToObject(DynamicObject object, StreamerObjectType type, Player p, Vector3D offsets) {
        Functions.AttachDynamicAreaToObject(getId(), object.getId(), type.getId(), p.getId(), offsets.x, offsets.y, offsets.z);
    }

    @Override
    public void attachToPlayer(Player p, Vector3D offsets) {
        Functions.AttachDynamicAreaToPlayer(getId(), p.getId(), offsets.x, offsets.y, offsets.z);
    }

    @Override
    public void attachToVehicle(Vehicle v, Vector3D offsets) {
        Functions.AttachDynamicAreaToVehicle(getId(), v.getId(), offsets.x, offsets.y, offsets.z);
    }

    @Override
    public List<Player> getPlayers() {
        return Player.get().stream().filter(this::isInArea).collect(Collectors.toList());
    }

    @Override
    public void destroy() {
        destroyed = true;
        areas.remove(this);
        Functions.DestroyDynamicArea(getId());
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
