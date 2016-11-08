package lt.ltrp.event.property;

import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.26.
 */
public class BusinessEditEvent extends BusinessEvent {


    public BusinessEditEvent(Business property, LtrpPlayer player) {
        super(property, player);
    }
}
