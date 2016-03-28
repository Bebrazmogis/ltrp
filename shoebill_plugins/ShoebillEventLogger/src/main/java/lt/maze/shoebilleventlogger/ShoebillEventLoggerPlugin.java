package lt.maze.shoebilleventlogger;

import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.resource.Plugin;
import org.slf4j.Logger;

/**
 * @author Bebras
 *         2016.03.18.
 */
public class ShoebillEventLoggerPlugin extends Plugin {

    private static ShoebillEventLoggerPlugin instance;

    private static Logger logger;
    private EventHandler eventHandler;
    private LoggingHandler loggingHandler;

    @Override
    protected void onEnable() throws Throwable {
        instance = this;
        logger = getLogger();
        eventHandler = new EventHandler(getEventManager());
        loggingHandler = new LoggingHandler(logger, getEventManager());
        logger.info("Shoebill event logger started");
    }

    @Override
    protected void onDisable() throws Throwable {
        eventHandler.destroy();
        loggingHandler.destroy();
        instance = null;
        logger.info("Shoebill event logger shutting down");
    }

    public void setLogUpdateEvents(boolean logUpdateEvents) {
        instance.loggingHandler.setLogUpdateEvents(logUpdateEvents);
    }

    public boolean isLogUpdateEvents() {
        return instance.loggingHandler.isLogUpdateEvents();
    }

    public void startLogging(Player p, boolean logUpdateEvents) {
        instance.loggingHandler.startLogging(p, logUpdateEvents);
    }

    public void stopLogging(Player p) {
        instance.loggingHandler.stopLogging(p);
    }

    public boolean isLoggingEnabled(Player p) {
        return loggingHandler.isLoggingEnabled(p);
    }

    public boolean isUpdateLoggingEnabled(Player p) {
        return loggingHandler.isUpdateLoggingEnabled(p);
    }
}
