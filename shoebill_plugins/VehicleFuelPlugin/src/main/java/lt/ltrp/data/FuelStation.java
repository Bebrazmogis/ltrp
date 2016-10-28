package lt.ltrp.data;


import lt.ltrp.object.impl.EntityImpl;
import net.gtaun.shoebill.data.Radius;

/**
 * @author Bebras
 *         2016.06.06.
 *
 *         Nothing special, just room for improvement
 */
public class FuelStation extends EntityImpl {

    private Radius radius;

    public FuelStation(int UUID, Radius radius) {
        super(UUID);
        this.radius = radius;
    }

    public FuelStation(Radius radius) {
        this.radius = radius;
    }

    public Radius getRadius() {
        return radius;
    }

    public void setRadius(Radius radius) {
        this.radius = radius;
    }
}
