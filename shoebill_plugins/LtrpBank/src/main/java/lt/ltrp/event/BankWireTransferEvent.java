package lt.ltrp.event;

import lt.ltrp.player.BankAccount;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.event.player.PlayerEvent;

/**
 * @author Bebras
 *         2016.03.02.
 */
public class BankWireTransferEvent extends PlayerEvent {

    private BankAccount account;
    private String recipientNumber;
    private int amount;

    public BankWireTransferEvent(LtrpPlayer p, BankAccount account, String recipientNumber, int amount) {
        super(p);
        this.account = account;
        this.recipientNumber = recipientNumber;
        this.amount = amount;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }


    public BankAccount getAccount() {
        return account;
    }

    public String getRecipientNumber() {
        return recipientNumber;
    }

    public int getAmount() {
        return amount;
    }
}
