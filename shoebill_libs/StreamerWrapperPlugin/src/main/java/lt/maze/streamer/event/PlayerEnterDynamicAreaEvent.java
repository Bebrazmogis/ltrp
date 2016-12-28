package lt.maze.streamer.event;

import lt.maze.streamer.object.DynamicArea;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.entities.Player;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class PlayerEnterDynamicAreaEvent extends PlayerEvent {

    private DynamicArea area;

    public PlayerEnterDynamicAreaEvent(Player player, DynamicArea area) {
        super(player);
        this.area = area;
    }

    public DynamicArea getArea() {
        return area;
    }
}
