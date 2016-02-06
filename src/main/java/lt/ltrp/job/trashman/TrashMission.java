package lt.ltrp.job.trashman;

import lt.ltrp.plugin.streamer.DynamicSampObject;
import net.gtaun.shoebill.data.Location;

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
    private DynamicSampObject[] garbageObjects;

    public TrashMission(int id, String name) {
        this.name = name;
        this.id = id;
        this.garbageObjects = new DynamicSampObject[DEFAULT_GARBAGE_COUNT];
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

    public DynamicSampObject[] getGarbageObjects() {
        return garbageObjects;
    }

    public void setGarbageObjects(DynamicSampObject[] garbageObjects) {
        this.garbageObjects = garbageObjects;
    }

    public DynamicSampObject getGarbage(int index) {
        if(index < garbageCount) {
            return garbageObjects[ index ];
        } else
            return null;
    }

    public void addGarbage(Location location) {
        DynamicSampObject newObject = DynamicSampObject.create(GARBAGE_MODEL, location, 0.0f, 0.0f, 0.0f);
        // If the array is full, we shal expand it
        if(garbageCount == garbageObjects.length-1) {
            DynamicSampObject[] tmp = new DynamicSampObject[garbageObjects.length+3];
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
