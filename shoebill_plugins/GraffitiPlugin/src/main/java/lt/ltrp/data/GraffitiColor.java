package lt.ltrp.data;

import lt.ltrp.object.Entity;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class GraffitiColor implements Entity {

    private int uuid;
    private Color color;

    public GraffitiColor(int uuid, Color color) {
        this.uuid = uuid;
        this.color = color;
    }

    public GraffitiColor(Color color) {
        this.color = color;
    }

    public GraffitiColor() {
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
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
