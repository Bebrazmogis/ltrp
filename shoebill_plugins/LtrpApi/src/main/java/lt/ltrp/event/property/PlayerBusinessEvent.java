package lt.ltrp.event.property;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.Business;


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
