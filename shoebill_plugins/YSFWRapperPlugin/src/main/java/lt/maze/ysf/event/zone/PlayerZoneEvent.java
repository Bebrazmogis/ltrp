package lt.maze.ysf.event.zone;

import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Zone;

/**
 * @author Bebras
 *         2016.04.03.
 */
public abstract class PlayerZoneEvent extends PlayerEvent {

    private Zone zone;

    public PlayerZoneEvent(Player player, Zone zone) {
        super(player);
        this.zone = zone;
    }

    public Zone getZone() {
        return zone;
    }
}
