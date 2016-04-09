package lt.ltrp.vehicle;

import lt.ltrp.player.data.PlayerOffer;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.09.
 */
public class BuyVehicleOffer extends PlayerOffer {

    private PlayerVehicle vehicle;
    private int price;


    public BuyVehicleOffer(LtrpPlayer player, LtrpPlayer offeredBy, EventManager eventManager, PlayerVehicle vehicle, int price) {
        super(player, offeredBy, eventManager, 90, BuyVehicleOffer.class);
        this.vehicle = vehicle;
        this.price = price;
    }

    public PlayerVehicle getVehicle() {
        return vehicle;
    }

    public int getPrice() {
        return price;
    }
}
