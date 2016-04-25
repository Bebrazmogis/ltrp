package lt.ltrp.event.property;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.Property;


/**
 * @author Bebras
 *         2015.11.29.
 */
public class PlayerExitPropertyEvent extends PlayerPropertyEvent {
    public PlayerExitPropertyEvent(LtrpPlayer player, Property property) {
        super(player, property);
    }
}
