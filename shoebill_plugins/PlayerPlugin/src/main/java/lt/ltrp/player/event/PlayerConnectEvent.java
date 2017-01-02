package lt.ltrp.player.event;

import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.05.16.
 */
public class PlayerConnectEvent extends PlayerEvent {

    public PlayerConnectEvent(LtrpPlayer player) {
        super(player);
    }
}
