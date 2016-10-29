package lt.maze.ysf.event.server;

import net.gtaun.util.event.Event;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class ServerMessageEvent extends Event {

    private String message;

    public ServerMessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
