package lt.maze.ysf.event.zone;

import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Zone;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class PlayerLeaveZoneEvent extends PlayerZoneEvent {

    public PlayerLeaveZoneEvent(Player player, Zone zone) {
        super(player, zone);
    }
}
