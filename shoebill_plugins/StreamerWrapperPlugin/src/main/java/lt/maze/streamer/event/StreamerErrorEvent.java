package lt.maze.streamer.event;

import net.gtaun.util.event.Event;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class StreamerErrorEvent extends Event {

    private String error;

    public StreamerErrorEvent(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
