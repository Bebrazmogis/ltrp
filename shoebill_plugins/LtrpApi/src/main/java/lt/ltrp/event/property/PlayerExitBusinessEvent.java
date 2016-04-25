package lt.ltrp.event.property;


import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.Business;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PlayerExitBusinessEvent extends PlayerBusinessEvent {

    public PlayerExitBusinessEvent(LtrpPlayer player, Business property) {
        super(player, property);
    }
}
