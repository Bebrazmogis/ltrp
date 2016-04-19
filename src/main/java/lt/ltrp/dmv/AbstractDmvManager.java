package lt.ltrp.dmv;


import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;


/**
 * @author Bebras
 *         2016.02.14.
 */
public abstract class AbstractDmvManager implements Destroyable {

    private EventManagerNode eventManagerNode;
    private PlayerCommandManager playerCommandManager;
    private boolean destroyed;

    public AbstractDmvManager(EventManager eventManager) {
        this.eventManagerNode = eventManager.createChildNode();
        this.playerCommandManager = new PlayerCommandManager(eventManagerNode);
        this.playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
    }

    public abstract boolean isInTest(LtrpPlayer player);
    public abstract boolean isInTest(LtrpVehicle vehicle);
    public abstract Dmv getDmv();

    protected EventManagerNode getEventManagerNode() {
        return eventManagerNode;
    }

    protected PlayerCommandManager getPlayerCommandManager() {
        return playerCommandManager;
    }

    @Override
    public void destroy() {
        eventManagerNode.cancelAll();
        this.destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

}
