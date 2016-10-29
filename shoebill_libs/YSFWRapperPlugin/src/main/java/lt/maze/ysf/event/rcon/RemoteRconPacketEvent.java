package lt.maze.ysf.event.rcon;

import net.gtaun.util.event.Event;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class RemoteRconPacketEvent extends Event {

    private String ip;
    private int port;
    private String password;
    private boolean success;
    private String command;

    public RemoteRconPacketEvent(String ip, int port, String password, boolean success, String command) {
        this.ip = ip;
        this.port = port;
        this.password = password;
        this.success = success;
        this.command = command;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getPassword() {
        return password;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getCommand() {
        return command;
    }
}
