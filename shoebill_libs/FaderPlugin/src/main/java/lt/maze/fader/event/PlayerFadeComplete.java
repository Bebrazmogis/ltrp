package lt.maze.fader.event;

import net.gtaun.shoebill.entities.Player;
import net.gtaun.shoebill.event.player.PlayerEvent;

/**
 * @author Bebras
 *         2016.04.05.
 */
public class PlayerFadeComplete extends PlayerEvent {


    public PlayerFadeComplete(Player player) {
        super(player);
    }
}
