package lt.ltrp.event.property;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.property.Property;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PlayerEnterPropertyEvent extends PlayerPropertyEvent {
    public PlayerEnterPropertyEvent(LtrpPlayer player, Property property) {
        super(player, property);
    }
}
