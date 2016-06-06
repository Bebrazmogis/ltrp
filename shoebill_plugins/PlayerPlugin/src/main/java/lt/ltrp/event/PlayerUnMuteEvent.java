package lt.ltrp.event;

import lt.ltrp.event.player.PlayerEvent;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.06.06.
 */
public class PlayerUnMuteEvent extends PlayerEvent {

    public PlayerUnMuteEvent(LtrpPlayer player) {
        super(player);
    }
}
