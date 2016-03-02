package lt.ltrp.event;

import lt.ltrp.player.BankAccount;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.03.02.
 */
public class BankAccountDepositMoney extends PlayerEvent {

    private BankAccount account;
    private int amount;

    public BankAccountDepositMoney(Player player, BankAccount account, int amount) {
        super(player);
        this.account = account;
        this.amount = amount;
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

}
