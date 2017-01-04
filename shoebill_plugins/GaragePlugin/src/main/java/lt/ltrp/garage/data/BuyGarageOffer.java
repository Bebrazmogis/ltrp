package lt.ltrp.garage.data;

import lt.ltrp.data.PlayerOffer;import lt.ltrp.object.Garage;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.20.
 */
public class BuyGarageOffer extends PlayerOffer {

    private Garage garage;
    private int price;

    public BuyGarageOffer(LtrpPlayer player, LtrpPlayer offeredBy, Garage garage, int price,  EventManager eventManager) {
        super(player, offeredBy, eventManager, 90, BuyGarageOffer.class);
        this.garage = garage;
        this.price = price;
    }

    public Garage getGarage() {
        return garage;
    }

    public int getPrice() {
        return price;
    }
}
