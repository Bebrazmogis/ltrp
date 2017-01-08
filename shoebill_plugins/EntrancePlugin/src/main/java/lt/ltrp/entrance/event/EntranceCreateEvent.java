package lt.ltrp.event;

import lt.ltrp.object.Entrance;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class EntranceCreateEvent extends EntranceEvent {
    public EntranceCreateEvent(Entrance entrance) {
        super(entrance);
    }
}
