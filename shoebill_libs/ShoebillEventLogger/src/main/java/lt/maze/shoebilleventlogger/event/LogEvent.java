package lt.maze.shoebilleventlogger.event;

import net.gtaun.util.event.Event;

/**
 * @author Bebras
 *         2016.03.18.
 */
public class LogEvent extends Event {

    private Event source;
    private String logString;

    public LogEvent(Event source, String logString) {
        this.source = source;
        this.logString = logString;
    }

    public Event getSource() {
        return source;
    }

    public String getLogString() {
        return logString;
    }
}
