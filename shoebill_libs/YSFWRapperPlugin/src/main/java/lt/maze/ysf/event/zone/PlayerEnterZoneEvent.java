package lt.maze.ysf.event.zone;

import net.gtaun.shoebill.entities.Player;
import net.gtaun.shoebill.object.Zone;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class PlayerEnterZoneEvent extends PlayerZoneEvent {


    public PlayerEnterZoneEvent(Player player, Zone zone) {
        super(player, zone);
    }
}
