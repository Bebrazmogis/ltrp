package lt.ltrp.business.event;

import lt.ltrp.object.Business;
import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessBankChangeEvent extends BusinessEvent {

    private int newMoney, oldMoney;

    public BusinessBankChangeEvent(Business property, LtrpPlayer player, int newMoney, int oldMoney) {
        super(property, player);
        this.newMoney = newMoney;
        this.oldMoney = oldMoney;
    }

    public int getNewMoney() {
        return newMoney;
    }

    public int getOldMoney() {
        return oldMoney;
    }
}
