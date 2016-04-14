package lt.ltrp.job.data;

import lt.maze.streamer.object.DynamicObject;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;

/**
 * @author Bebras
 *         2015.12.17.
 */
public class TrashMission {

    private static final int DEFAULT_GARBAGE_COUNT = 20;
    private static final int GARBAGE_MODEL = 1265;

    private int id;
    private String name;
    private int garbageCount;
    private DynamicObject[] garbageObjects;

    public TrashMission(int id, String name) {
        this.name = name;
        this.id = id;
        this.garbageObjects = new DynamicObject[DEFAULT_GARBAGE_COUNT];
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DynamicObject[] getGarbageObjects() {
        return garbageObjects;
    }

    public void setGarbageObjects(DynamicObject[] garbageObjects) {
        this.garbageObjects = garbageObjects;
    }

    public DynamicObject getGarbage(int index) {
        if(index < garbageCount) {
            return garbageObjects[ index ];
        } else
            return null;
    }

    public void addGarbage(Location location) {
        DynamicObject newObject = DynamicObject.create(GARBAGE_MODEL, location, new Vector3D());
        // If the array is full, we shal expand it
        if(garbageCount == garbageObjects.length-1) {
            DynamicObject[] tmp = new DynamicObject[garbageObjects.length+3];
            for(int i = 0; i < garbageObjects.length; i++) {
                tmp[i] = garbageObjects[i];
            }
            tmp[garbageCount++] = newObject;
            garbageObjects = tmp;
        } else {
            garbageObjects[ garbageCount++] = newObject;
        }
    }

}
