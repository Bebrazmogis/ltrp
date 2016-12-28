package lt.ltrp.business.event;

import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.27.
 */
public class BusinessCreateEvent extends BusinessEvent {
    public BusinessCreateEvent(Business property, LtrpPlayer player) {
        super(property, player);
    }
}
