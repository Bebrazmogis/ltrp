package lt.ltrp.data;

import lt.ltrp.object.Entity;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class GraffitiFont implements Entity{

    private int uuid;
    private String name;
    private int size;

    public GraffitiFont(int uuid, String name, int size) {
        this.uuid = uuid;
        this.name = name;
        this.size = size;
    }

    public GraffitiFont(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public GraffitiFont() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public void setUUID(int i) {
        this.uuid = i;
    }

    @Override
    public int getUUID() {
        return uuid;
    }
}
