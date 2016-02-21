package lt.maze.audio.event;

import lt.maze.audio.AudioHandle;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.02.15.
 */
public class AudioTrackChangeEvent extends PlayerEvent {

    private AudioHandle handle;
    private String track;

    public AudioTrackChangeEvent(Player player, AudioHandle handle, String track) {
        super(player);
        this.handle = handle;
        this.track = track;
    }

    public AudioHandle getHandle() {
        return handle;
    }

    public String getTrack() {
        return track;
    }
}
