package lt.ltrp.event;

import lt.ltrp.object.Entrance;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class EntranceDestroyEvent extends EntranceEvent {


    public EntranceDestroyEvent(Entrance entrance) {
        super(entrance);
    }
}
