package lt.ltrp.event.property;

import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessBuyEvent extends BusinessEvent {


    public BusinessBuyEvent(Business property, LtrpPlayer player) {
        super(property, player);
    }
}
