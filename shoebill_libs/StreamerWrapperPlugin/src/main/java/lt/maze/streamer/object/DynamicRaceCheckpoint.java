package lt.maze.streamer.object;

import lt.maze.streamer.Constants;
import lt.maze.streamer.Functions;
import lt.maze.streamer.constant.StreamerType;
import net.gtaun.shoebill.constant.RaceCheckpointType;
import net.gtaun.shoebill.data.Radius;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.exception.CreationFailedException;
import net.gtaun.shoebill.entities.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class DynamicRaceCheckpoint extends AbstractCheckpoint {

    private static final Collection<DynamicRaceCheckpoint> raceCheckpoints = new ArrayList<>();


    public static Collection<DynamicRaceCheckpoint> get() {
        return raceCheckpoints;
    }

    public static DynamicRaceCheckpoint get(int id) {
        Optional<DynamicRaceCheckpoint> co = raceCheckpoints.stream().filter(l -> l.getId() == id).findFirst();
        return co.isPresent() ? co.get() : null;
    }


    //CreateDynamicRaceCP(int type, float x, float y, float z, float nextx, float nexty, float nextz, float size, int worldid, int interiorid, int playerid, float streamdistance);
    public static DynamicRaceCheckpoint create(RaceCheckpointType type, float x, float y, float z, float nextx, float nexty, float nextz, float size, int worldid, int ineriorid, Player p, float streamdistance) {
        int id = Functions.CreateDynamicRaceCP(type.getValue(), x, y, z, nextx, nexty, nextz, size, worldid, ineriorid, p == null ? -1 : p.getId(), streamdistance);
        if(id == Constants.INVALID_STREAMER_ID)
            throw new CreationFailedException("DynamicRaceCheckpoint could not be created");
        DynamicRaceCheckpoint cp = new DynamicRaceCheckpoint(id);
        raceCheckpoints.add(cp);
        return cp;
    }

    public static DynamicRaceCheckpoint create(RaceCheckpointType type, float x, float y, float z, float nextx, float nexty, float nextz, float size) {
        return create(type, x, y, z, nextx, nexty, nextz, size, -1, -1, null, Constants.STREAMER_RACE_CP_SD);
    }

    public static DynamicRaceCheckpoint create(RaceCheckpointType type, Vector3D position, Vector3D nextposition, float size) {
        return create(type, position.x, position.y, position.z, nextposition.x, nextposition.y, nextposition.z, size);
    }

    public static DynamicRaceCheckpoint create(RaceCheckpointType type, Radius radius, Vector3D nextposition) {
        return create(type, radius.x, radius.y, radius.z, nextposition.x, nextposition.y, nextposition.z, radius.radius);
    }


    private DynamicRaceCheckpoint(int id) {
        super(id, StreamerType.RaceCheckpoint);
    }

    @Override
    public boolean isValid() {
        return Functions.IsValidDynamicRaceCP(getId()) == 1;
    }

    @Override
    public void toggle(Player p, boolean toggle) {
        Functions.TogglePlayerDynamicRaceCP(p.getId(), getId(), toggle ? 1 : 0);
    }

    @Override
    public boolean isInCp(Player p) {
        return Functions.IsPlayerInDynamicRaceCP(p.getId(), getId()) == 1;
    }


    @Override
    public void destroy() {
        super.destroy();
        Functions.DestroyDynamicRaceCP(getId());
        raceCheckpoints.remove(this);
    }

}
