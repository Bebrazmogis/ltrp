package lt.ltrp;

import lt.ltrp.object.Job;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2016.04.14.
 */
public abstract class AbstractJob extends NamedEntityImpl implements Job {

    protected static final List<Job> jobList = new ArrayList<>();

    private Location location;
    private int basePaycheck;
    private EventManager eventManager;

    public AbstractJob(int id, String name, Location location, int basePaycheck, EventManager eventManager) {
        super(id, name);
        this.location = location;
        this.basePaycheck = basePaycheck;
        this.eventManager = eventManager;
        jobList.add(this);
    }

    public AbstractJob(EventManager eventManager) {
        this(0, null, null, 0, eventManager);
    }

    protected EventManager getEventManager() {
        return eventManager;
    }

    @Override
    protected void finalize() throws Throwable {
        jobList.remove(this);
        super.finalize();
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public int getBasePaycheck() {
        return basePaycheck;
    }

    @Override
    public void setBasePaycheck(int i) {
        this.basePaycheck = i;
    }

    @Override
    public boolean isAtWork(LtrpPlayer player) {
        return player.getLocation().distance(location) < 50.0f;
    }

    @Override
    public void sendMessage(Color color, String s) {
        LtrpPlayer.get().stream().filter(p -> this.equals(JobController.get().getJob(p))).forEach(p -> p.sendMessage(color, s));
    }
}
