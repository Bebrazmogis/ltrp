package lt.maze.audio.event;

import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.02.15.
 */
public class AudioClientDisconnectEvent extends PlayerEvent {

    public AudioClientDisconnectEvent(Player player) {
        super(player);
    }
}
