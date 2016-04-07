package lt.maze.ysf.event.playerzone;

import lt.maze.ysf.object.YSFPlayerZone;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class PlayerPlayerZoneEvent extends PlayerEvent {

    private YSFPlayerZone zone;

    public PlayerPlayerZoneEvent(Player player, YSFPlayerZone zone) {
        super(player);
        this.zone = zone;
    }

    public YSFPlayerZone getZone() {
        return zone;
    }
}
