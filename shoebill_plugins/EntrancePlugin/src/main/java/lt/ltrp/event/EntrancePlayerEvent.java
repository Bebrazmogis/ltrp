package lt.ltrp.event;

import lt.ltrp.object.Entrance;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.05.22.
 */
public abstract class EntrancePlayerEvent extends EntranceEvent {

    private LtrpPlayer player;

    public EntrancePlayerEvent(Entrance entrance, LtrpPlayer player) {
        super(entrance);
        this.player = player;
    }

    public LtrpPlayer getPlayer() {
        return player;
    }
}
