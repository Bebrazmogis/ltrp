package lt.ltrp.dmv;

import net.gtaun.shoebill.data.Radius;

/**
 * @author Bebras
 *         2015.12.13.
 */
public class DmvCheckpoint {

    private int id;
    private Radius radius;

    public DmvCheckpoint(int id, Radius radius) {
        this.id = id;
        this.radius = radius;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Radius getRadius() {
        return radius;
    }

    public void setRadius(Radius radius) {
        this.radius = radius;
    }
}
