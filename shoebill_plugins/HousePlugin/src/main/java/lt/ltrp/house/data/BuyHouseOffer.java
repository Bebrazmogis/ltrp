package lt.ltrp.house.data;

import lt.ltrp.data.PlayerOffer;import lt.ltrp.house.object.House;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.20.
 */
public class BuyHouseOffer extends PlayerOffer {

    private House house;
    private int price;

    public BuyHouseOffer(LtrpPlayer player, LtrpPlayer offeredBy, House house, int price, EventManager eventManager) {
        super(player, offeredBy, eventManager, 90, BuyHouseOffer.class);
        this.house = house;
        this.price = price;
    }

    public House getHouse() {
        return house;
    }

    public int getPrice() {
        return price;
    }
}
