package lt.maze.fader.event;

import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.04.05.
 */
public class PlayerFadeComplete extends PlayerEvent {


    public PlayerFadeComplete(Player player) {
        super(player);
    }
}
