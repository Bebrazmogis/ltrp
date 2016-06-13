package lt.maze.event;

import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.06.13.
 */
public class PlayerAttemptDriveByEvent extends PlayerEvent {

    public PlayerAttemptDriveByEvent(Player player) {
        super(player);
    }
}
