package lt.ltrp.vehicle.event;


import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.object.PlayerVehicle;

/**
 * @author Bebras
 *         2016.03.09.
 */
public class PlayerVehicleSellEvent extends PlayerVehicleEvent {

    private LtrpPlayer newOwner;
    private int price;

    public PlayerVehicleSellEvent(LtrpPlayer player, PlayerVehicle vehicle, LtrpPlayer newOwner, int price) {
        super(player, vehicle);
        this.newOwner = newOwner;
        this.price = price;
    }

    public LtrpPlayer getNewOwner() {
        return newOwner;
    }

    public int getPrice() {
        return price;
    }
}
