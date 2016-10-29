package lt.maze.audio.event;

import lt.maze.audio.RadioStation;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.02.15.
 */
public class AudioStationChangeEvent extends PlayerEvent {

    private RadioStation station;

    public AudioStationChangeEvent(Player player, RadioStation station) {
        super(player);
        this.station = station;
    }

    public RadioStation getStation() {
        return station;
    }
}
