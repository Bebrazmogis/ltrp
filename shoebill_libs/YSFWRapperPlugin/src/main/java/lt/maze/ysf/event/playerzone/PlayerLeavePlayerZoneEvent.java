package lt.maze.ysf.event.playerzone;

import lt.maze.ysf.object.YSFPlayerZone;
import net.gtaun.shoebill.entities.Player;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class PlayerLeavePlayerZoneEvent extends PlayerPlayerZoneEvent {
    public PlayerLeavePlayerZoneEvent(Player player, YSFPlayerZone zone) {
        super(player, zone);
    }
}
