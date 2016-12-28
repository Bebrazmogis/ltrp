package lt.ltrp.event;

import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.entities.Player;

/**
 * @author Bebras
 *         2015.11.12.
 */
public class PlayerDataLoadEvent extends PlayerEvent{


    public PlayerDataLoadEvent(Player player) {
        super(player);
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer) super.getPlayer();
    }
}
