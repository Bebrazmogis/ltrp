package lt.ltrp.event;

import lt.ltrp.object.Entrance;
import net.gtaun.util.event.Event;

/**
 * @author Bebras
 *         2016.05.22.
 */
public abstract class EntranceEvent extends Event {

    private Entrance entrance;

    public EntranceEvent(Entrance entrance) {
        this.entrance = entrance;
    }

    public Entrance getEntrance() {
        return entrance;
    }
}
