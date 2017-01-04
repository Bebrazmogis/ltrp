package lt.ltrp.business.event;

import lt.ltrp.object.Business;
import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessBuyEvent extends BusinessEvent {


    public BusinessBuyEvent(Business property, LtrpPlayer player) {
        super(property, player);
    }
}
