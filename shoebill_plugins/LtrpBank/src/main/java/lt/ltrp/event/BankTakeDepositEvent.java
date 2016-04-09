package lt.ltrp.event;

import lt.ltrp.player.BankAccount;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.03.02.
 */
public class BankTakeDepositEvent extends PlayerEvent {

    private BankAccount account;
    private int deposit;

    public BankTakeDepositEvent(Player player, BankAccount account, int deposit) {
        super(player);
        this.account = account;
        this.deposit = deposit;
    }

    public BankAccount getAccount() {
        return account;
    }

    public int getDeposit() {
        return deposit;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

}
