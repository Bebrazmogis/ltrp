package lt.ltrp.event;

import lt.ltrp.player.BankAccount;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.event.player.PlayerEvent;

/**
 * @author Bebras
 *         2016.03.02.
 */
public class BankPlaceDepositEvent extends PlayerEvent {

    private BankAccount account;
    private int amount;
    private int interest;

    public BankPlaceDepositEvent(LtrpPlayer player, BankAccount account, int amount, int interest) {
        super(player);
        this.account = account;
        this.amount = amount;
        this.interest = interest;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }


    public BankAccount getAccount() {
        return account;
    }

    public int getAmount() {
        return amount;
    }

    public int getInterest() {
        return interest;
    }
}
