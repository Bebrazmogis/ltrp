package lt.ltrp.event.property;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.property.Business;
import lt.ltrp.property.Property;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PlayerBusinessEvent extends PlayerPropertyEvent {

    public PlayerBusinessEvent(LtrpPlayer player, Business property) {
        super(player, property);
    }

    public Business getProperty() {
        return (Business)super.getProperty();
    }

}
