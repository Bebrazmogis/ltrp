package lt.ltrp.player.event;

import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.06.06.
 */
public class PlayerUnMuteEvent extends PlayerEvent {

    public PlayerUnMuteEvent(LtrpPlayer player) {
        super(player);
    }
}
