package lt.maze.streamer.event;

import lt.maze.streamer.object.DynamicArea;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.entities.Player;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class PlayerLeaveDynamicAreaEvent extends PlayerEvent {

    private DynamicArea area;

    public PlayerLeaveDynamicAreaEvent(Player player, DynamicArea area) {
        super(player);
        this.area = area;
    }

    public DynamicArea getArea() {
        return area;
    }
}
