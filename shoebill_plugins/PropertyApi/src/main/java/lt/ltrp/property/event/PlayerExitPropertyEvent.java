package lt.ltrp.property.event;

import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.property.object.Property;


/**
 * @author Bebras
 *         2015.11.29.
 */
public class PlayerExitPropertyEvent extends PlayerPropertyEvent {
    public PlayerExitPropertyEvent(LtrpPlayer player, Property property) {
        super(player, property);
    }
}
