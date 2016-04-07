package lt.maze.ysf.event.playerzone;

import lt.maze.ysf.object.YSFPlayerZone;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class PlayerEnterPlayerZoneEvent extends PlayerPlayerZoneEvent {

    public PlayerEnterPlayerZoneEvent(Player player, YSFPlayerZone zone) {
        super(player, zone);
    }
}
