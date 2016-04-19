package lt.ltrp.dmv;

import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.data.Radius;
import net.gtaun.shoebill.object.Checkpoint;
import net.gtaun.shoebill.object.Player;

import java.util.function.Consumer;

/**
 * @author Bebras
 *         2015.12.13.
 */
public class DmvCheckpoint {

    private int id;
    private Radius radius;
    protected Checkpoint checkpoint;

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


    public Checkpoint create(Consumer<Player> enterConsumer) {
        if(checkpoint == null) {
            checkpoint = Checkpoint.create(this.radius, enterConsumer, null);
        }
        return checkpoint;
    }

    public void set(LtrpPlayer player) {
        if(checkpoint != null)
            checkpoint.set(player);
    }

    public void disable(LtrpPlayer player) {
        if(checkpoint != null) {
            checkpoint.disable(player);
        }
    }

    protected Checkpoint getCheckpoint() {
        return checkpoint;
    }
}
