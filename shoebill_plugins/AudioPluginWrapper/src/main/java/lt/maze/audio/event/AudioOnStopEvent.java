package lt.maze.audio.event;

import lt.maze.audio.AudioHandle;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.02.15.
 */
public class AudioOnStopEvent extends PlayerEvent {

    private AudioHandle handle;

    public AudioOnStopEvent(Player player, AudioHandle handle) {
        super(player);
        this.handle = handle;
    }

    public AudioHandle getHandle() {
        return handle;
    }
}
