package lt.maze.audio.event;

import lt.maze.audio.AudioHandle;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.02.15.
 */
public class AudioPositionGetEvent extends PlayerEvent {

    private AudioHandle handle;
    private int seconds;

    public AudioPositionGetEvent(Player player, AudioHandle handle, int seconds) {
        super(player);
        this.handle = handle;
        this.seconds = seconds;
    }

    public AudioHandle getHandle() {
        return handle;
    }
}
