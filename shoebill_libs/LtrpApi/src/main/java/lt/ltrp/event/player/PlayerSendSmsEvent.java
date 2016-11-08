package lt.ltrp.event.player;

import lt.ltrp.object.ItemPhone;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.event.player.PlayerEvent;

/**
 * @author Bebras
 *         2016.03.29.
 */
public class PlayerSendSmsEvent extends PlayerEvent {

    private ItemPhone senderPhone;
    private int phoneNumber;
    private String text;

    public PlayerSendSmsEvent(LtrpPlayer player, ItemPhone senderPhone, int phoneNumber, String text) {
        super(player);
        this.senderPhone = senderPhone;
        this.phoneNumber = phoneNumber;
        this.text = text;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

    public ItemPhone getSenderPhone() {
        return senderPhone;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String getText() {
        return text;
    }
}
