package lt.ltrp.data;

import lt.maze.streamer.object.DynamicObject;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Destroyable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.12.27.
 */
public class Roadblock implements Destroyable{

    private static final List<Roadblock> roadblocks = new ArrayList<>();

    public static List<Roadblock> get() {
        return roadblocks;
    }


    private int id, modelId;
    private DynamicObject object;
    private boolean destroyed;

    public Roadblock(int id, int modelid, Location location, Vector3D rotation) {
        this.id = id;
        this.modelId = modelid;
        this.object = DynamicObject.create(modelid, location, rotation);
        roadblocks.add(this);
    }

    public Roadblock(int modelid, Location location, Vector3D rotation) {
        this(0, modelid, location, rotation);
    }

    public Roadblock(int modelid, Location location) {
        this(0, modelid, location, new Vector3D());
    }


    public int getId() {
        return id;
    }

    public int getModelId() {
        return modelId;
    }

    public DynamicObject getObject() {
        return object;
    }

    public Location getLocation() {
        if(object != null) {
            return object.getLocation();
        } else {
            return null;
        }
    }

    @Override
    public void destroy() {
        if(object != null) {
            object.destroy();
        }
        destroyed = true;
        roadblocks.remove(this);
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
