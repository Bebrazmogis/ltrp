package lt.ltrp.business.event;

import lt.ltrp.object.Business;
import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessEntrancePriceChangeEvent extends BusinessEvent {

    private int newPrice;

    public BusinessEntrancePriceChangeEvent(Business property, LtrpPlayer player, int newPrice) {
        super(property, player);
        this.newPrice = newPrice;
    }

    public int getNewPrice() {
        return newPrice;
    }
}
