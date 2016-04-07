package lt.maze.ysf.event.player;

import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class PlayerPauseStateChangeEvent extends PlayerEvent {

    private boolean state;

    public PlayerPauseStateChangeEvent(Player player, boolean state) {
        super(player);
        this.state = state;
    }

    public boolean getState() {
        return state;
    }
}
