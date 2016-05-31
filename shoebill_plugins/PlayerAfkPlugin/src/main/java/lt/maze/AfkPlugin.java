package lt.maze;

import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.player.PlayerTextEvent;
import net.gtaun.shoebill.event.player.PlayerUpdateEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManagerNode;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2016.06.01.
 */
public class AfkPlugin extends Plugin {

    private EventManagerNode eventManager;
    private Map<Player, Instant> lastPlayerAction;

    @Override
    protected void onEnable() throws Throwable {
        this.lastPlayerAction = new HashMap<>();
        eventManager = getEventManager().createChildNode();

        eventManager.registerHandler(PlayerDisconnectEvent.class, e -> {
            lastPlayerAction.remove(e.getPlayer());
        });

        eventManager.registerHandler(PlayerUpdateEvent.class, e -> lastPlayerAction.put(e.getPlayer(), Instant.now()));
        eventManager.registerHandler(PlayerTextEvent.class, e -> lastPlayerAction.put(e.getPlayer(), Instant.now()));
    }

    @Override
    protected void onDisable() throws Throwable {
        eventManager.cancelAll();
        lastPlayerAction.clear();
    }

    /**
     * Returns the time in seconds since last player actiion(move or text)
     * @param p player
     * @return seconds afk
     */
    public int getPlayerAfkSeconds(Player p) {
        return (int) (Instant.now().getEpochSecond() - Instant.now().getEpochSecond());
    }

    /**
     * Checks whether the given player is afk
     * <b>This is not accurate. If a player stop doing something for more than a second, this will assume he's AFK</b>
     * @param p player
     * @return true if the player is afk, false otherwise
     */
    public boolean isPlayerAfk(Player p) {
        return lastPlayerAction.get(p).getEpochSecond() - Instant.now().getEpochSecond() > 1;
    }
}
