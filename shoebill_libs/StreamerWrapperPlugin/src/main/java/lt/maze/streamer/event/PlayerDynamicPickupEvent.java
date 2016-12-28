package lt.maze.streamer.event;

import lt.maze.streamer.object.DynamicPickup;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.entities.Player;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class PlayerDynamicPickupEvent extends PlayerEvent {

    private DynamicPickup pickup;

    public PlayerDynamicPickupEvent(Player player, DynamicPickup pickup) {
        super(player);
        this.pickup = pickup;
    }

    public DynamicPickup getPickup() {
        return pickup;
    }
}
