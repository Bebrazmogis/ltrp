package lt.ltrp.event.property.house;

import lt.ltrp.object.House;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.20.
 */
public class HouseMoneyEvent extends HouseEvent {

    private int oldBalance;
    private int newBalance;

    public HouseMoneyEvent(House house, LtrpPlayer player, int oldBalance, int newBalance) {
        super(house, player);
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
    }

    public int getOldBalance() {
        return oldBalance;
    }

    public int getNewBalance() {
        return newBalance;
    }
}
