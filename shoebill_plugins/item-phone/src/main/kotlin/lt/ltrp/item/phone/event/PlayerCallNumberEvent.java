package lt.ltrp.player.event;

import lt.ltrp.object.ItemPhone;
import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.03.23.
 */
public class PlayerCallNumberEvent extends PlayerEvent {

    private int phoneNumber;
    private ItemPhone callerPhone;

    public PlayerCallNumberEvent(LtrpPlayer player, ItemPhone itemPhone, int phoneNumber) {
        super(player);
        this.callerPhone = itemPhone;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public ItemPhone getCallerPhone() {
        return callerPhone;
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }
}
