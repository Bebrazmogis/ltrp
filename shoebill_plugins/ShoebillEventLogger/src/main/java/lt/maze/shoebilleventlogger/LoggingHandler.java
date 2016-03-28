package lt.maze.shoebilleventlogger;

import lt.maze.shoebilleventlogger.event.LogEvent;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent;
import net.gtaun.shoebill.event.player.PlayerUpdateEvent;
import net.gtaun.shoebill.event.vehicle.UnoccupiedVehicleUpdateEvent;
import net.gtaun.shoebill.event.vehicle.VehicleUpdateEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2016.03.19.
 */
public class LoggingHandler  implements Destroyable{

    private static final Color PLAYER_LOG_COLOR = Color.OLIVE;
    private static final Class[] UPDATE_EVENTS = new Class[] {
            PlayerUpdateEvent.class,
            VehicleUpdateEvent.class,
            UnoccupiedVehicleUpdateEvent.class,
            PlayerKeyStateChangeEvent.class
    } ;

    private EventManagerNode eventManager;
    private Map<Player, Boolean> logginEnabled;
    private boolean destroyed;
    private boolean logUpdateEvents;
    private Logger logger;

    public LoggingHandler(Logger logger, EventManager eventManager) {
        this.eventManager = eventManager.createChildNode();
        this.logginEnabled = new HashMap<>();
        this.logger = logger;

        this.eventManager.registerHandler(LogEvent.class, e -> {
            String logString = String.format("[%s]%s", e.getSource().getClass().getSimpleName(), e.getLogString());
            for(Class clz : UPDATE_EVENTS) {
                if (clz.equals(e.getSource().getClass())) {
                    if(logUpdateEvents) {
                        log(logString, true);
                    }
                    return;
                }
            }
            log(logString, false);
        });

        this.eventManager.registerHandler(PlayerDisconnectEvent.class, e -> {
            logginEnabled.remove(e.getPlayer());
        });
    }

    private void log(String s, boolean update) {
        logger.info(s);
        logginEnabled.forEach((p, b) -> {
             if(b || !update) {
                 p.sendMessage(PLAYER_LOG_COLOR, s);
             }
        });
    }

    protected void setLogUpdateEvents(boolean logUpdateEvents) {
        this.logUpdateEvents = logUpdateEvents;
    }

    protected boolean isLogUpdateEvents() {
        return logUpdateEvents;
    }

    protected void startLogging(Player p, boolean logUpdateEvents) {
        logginEnabled.put(p, logUpdateEvents);
    }

    protected void stopLogging(Player p) {
        logginEnabled.remove(p);
    }

    protected boolean isLoggingEnabled(Player p) {
        return logginEnabled.containsKey(p);
    }

    protected boolean isUpdateLoggingEnabled(Player p) {
        return logginEnabled.containsKey(p) ? logginEnabled.get(p) : false;
    }


    @Override
    public void destroy() {
        logginEnabled.clear();
        logginEnabled = null;
        destroyed = true;
        eventManager.cancelAll();
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
