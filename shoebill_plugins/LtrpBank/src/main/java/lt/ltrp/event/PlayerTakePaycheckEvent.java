package lt.ltrp.event;

import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.event.player.PlayerEvent;

/**
 * @author Bebras
 *         2016.03.02.
 */
public class PlayerTakePaycheckEvent extends PlayerEvent {

    private int amount;

    public PlayerTakePaycheckEvent(LtrpPlayer player, int amount) {
        super(player);
        this.amount = amount;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }


    public int getAmount() {
        return amount;
    }
}
