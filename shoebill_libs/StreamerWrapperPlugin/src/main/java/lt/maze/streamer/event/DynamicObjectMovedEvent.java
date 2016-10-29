package lt.maze.streamer.event;

import lt.maze.streamer.object.DynamicObject;
import net.gtaun.util.event.Event;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class DynamicObjectMovedEvent extends Event {

    private DynamicObject object;

    public DynamicObjectMovedEvent(DynamicObject object) {
        this.object = object;
    }

    public DynamicObject getObject() {
        return object;
    }
}
