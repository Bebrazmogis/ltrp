package lt.ltrp.business.data;

import lt.ltrp.data.PlayerOffer;import lt.ltrp.object.Business;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BuyBusinessOffer extends PlayerOffer {

    private Business business;
    int price;

    public BuyBusinessOffer(LtrpPlayer player, LtrpPlayer offeredBy, EventManager eventManager, Business business, int price) {
        super(player, offeredBy, eventManager, 100, BuyBusinessOffer.class);
        this.business = business;
        this.price = price;
    }

    public Business getBusiness() {
        return business;
    }

    public int getPrice() {
        return price;
    }
}
