package lt.ltrp;

import lt.ltrp.object.Job;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

/**
 * @author Bebras
 *         2016.03.01.
 */
public abstract class AbstractJobManager implements Destroyable {

    private boolean destroyed;
    protected EventManagerNode eventManagerNode;

    public AbstractJobManager(EventManager eventManager) {
        this.eventManagerNode = eventManager.createChildNode();
    }

    public abstract Job getJob();

    @Override
    public void destroy() {
        destroyed = true;
        eventManagerNode.cancelAll();
        eventManagerNode.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}

