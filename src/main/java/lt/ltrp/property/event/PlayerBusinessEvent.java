package lt.ltrp.property.event;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.property.Business;

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
