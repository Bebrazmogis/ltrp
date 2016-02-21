package lt.maze.audio.event;

import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.02.15.
 */
public class AudioClientConnectEvent extends PlayerEvent {

    public AudioClientConnectEvent(Player player) {
        super(player);
    }
}
