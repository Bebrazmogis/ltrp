package lt.maze.object;

import lt.maze.Functions;
import lt.maze.constant.StreamerAreaType;
import lt.maze.constant.StreamerObjectType;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2016.02.16.
 */
public interface DynamicArea extends StreamerItem {

    static Collection<DynamicArea> areas = new ArrayList<>();

    int getId();
    void toggle(Player p, boolean toggle);
    boolean isInArea(Player p);
    boolean contains(float x, float y, float z);
    boolean contains(float x, float y);
    void attachToObject(DynamicObject object, StreamerObjectType type, Player p, Vector3D offsets);
    default void attachToObject(DynamicObject object, Vector3D offsets) {
        attachToObject(object, StreamerObjectType.Dynamic, null, offsets);
    }
    void attachToPlayer(Player p, Vector3D offsets);
    void attachToVehicle(Vehicle v, Vector3D offsets);

    default boolean isAnyPlayerInArea() {
        return Functions.IsAnyPlayerInDynamicArea(getId(), 0) == 1;
    }

    default boolean isValid() {
        return Functions.IsValidDynamicArea(getId()) == 1;
    }

    /**
     * Returns a list of players that are inside this area
     * @return
     */
    List<Player> getPlayers();

    /**
     * This type corresponds to the STREAMER_AREA_TYPE constant. See {@link lt.maze.Constants} for a list
     * @return the area type
     */
    StreamerAreaType getType();


    static Collection<DynamicArea> get() {
        return areas;
    }

    static DynamicArea get(int id) {
        Optional<DynamicArea> area = areas.stream().filter(a -> a.getId() == id).findFirst();
        return area.isPresent() ? area.get() : null;
    }

    static void toggleAll(Player p, boolean toggle) {
        Functions.TogglePlayerAllDynamicAreas(p.getId(), toggle ? 1 : 0);
    }

    static boolean isInAnyArea(Player p) {
        return Functions.IsPlayerInAnyDynamicArea(p.getId(), 0) == 1;
    }

    static boolean isAnyPlayerInAnyArea() {
        return Functions.IsAnyPlayerInAnyDynamicArea(0) == 1;
    }

    static List<DynamicArea> getAreas(Player p) {
        return areas.stream().filter(a -> a.isInArea(p)).collect(Collectors.toList());
    }

    static boolean containsAny(float x, float y, float z) {
        return Functions.IsPointInAnyDynamicArea(x, y, z) == 1;
    }

    static List<DynamicArea> getAreas(float x, float y, float z) {
        return areas.stream().filter(a -> a.contains(x, y, z)).collect(Collectors.toList());
    }
}
