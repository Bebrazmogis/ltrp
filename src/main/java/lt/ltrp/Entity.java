package lt.ltrp;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class Entity {

    private int uuid;

    public Entity(int id) {
        this.uuid = id;
    }

    public Entity() {

    }

    public int getUUID() {
        return uuid;
    }

    public void setUUID(int uuid) {
        this.uuid = uuid;
    }
}
