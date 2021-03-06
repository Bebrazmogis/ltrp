package lt.maze.audio.event;

import lt.maze.audio.AudioHandle;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.entities.Player;

/**
 * @author Bebras
 *         2016.02.15.
 */
public class AudioOnPlayEvent extends PlayerEvent {

    private AudioHandle handle;

    public AudioOnPlayEvent(Player player, AudioHandle handle) {
        super(player);
        this.handle = handle;
    }

    public AudioHandle getHandle() {
        return handle;
    }
}
