package lt.ltrp.property.event;

import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.property.Business;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PlayerExitBusinessEvent extends PlayerBusinessEvent {

    public PlayerExitBusinessEvent(LtrpPlayer player, Business property) {
        super(player, property);
    }
}
