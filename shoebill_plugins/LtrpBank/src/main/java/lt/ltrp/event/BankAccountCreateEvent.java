package lt.ltrp.event;

import lt.ltrp.player.BankAccount;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.event.player.PlayerEvent;

/**
 * @author Bebras
 *         2016.03.02.
 */
public class BankAccountCreateEvent extends PlayerEvent {

    private BankAccount bankAccount;

    public BankAccountCreateEvent(LtrpPlayer player, BankAccount bankAccount) {
        super(player);
        this.bankAccount = bankAccount;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }
}
