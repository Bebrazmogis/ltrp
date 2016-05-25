package lt.ltrp.event;

import lt.ltrp.object.Entrance;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class PlayerEnterEntranceEvent extends EntrancePlayerEvent {


    public PlayerEnterEntranceEvent(Entrance entrance, LtrpPlayer player) {
        super(entrance, player);
    }

}
