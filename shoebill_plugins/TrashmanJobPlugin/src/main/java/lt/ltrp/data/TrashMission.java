package lt.ltrp.data;

import lt.maze.streamer.object.DynamicObject;

/**
 * @author Bebras
 *         2015.12.17.
 */
public class TrashMission {

    private static final int DEFAULT_GARBAGE_COUNT = 20;

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
        // We return a copy, we don't want people messing with our data!
        DynamicObject[] tmp = new DynamicObject[garbageCount];
        System.arraycopy(garbageObjects, 0, tmp, 0, garbageCount);
        return tmp;
    }

    public int getGarbageCount() {
        return garbageCount;
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

    public void addGarbage(DynamicObject garbageObject) {
        // If the array is full, we shall expand it
        if(garbageCount == garbageObjects.length-1) {
            DynamicObject[] tmp = new DynamicObject[garbageObjects.length+3];
            for(int i = 0; i < garbageObjects.length; i++) {
                tmp[i] = garbageObjects[i];
            }
            tmp[garbageCount++] = garbageObject;
            garbageObjects = tmp;
        } else {
            garbageObjects[ garbageCount++] = garbageObject;
        }
    }

}
