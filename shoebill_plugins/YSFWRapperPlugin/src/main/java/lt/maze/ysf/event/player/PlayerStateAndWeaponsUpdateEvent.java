package lt.maze.ysf.event.player;

import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class PlayerStateAndWeaponsUpdateEvent extends PlayerEvent{

    public PlayerStateAndWeaponsUpdateEvent(Player player) {
        super(player);
    }
}
