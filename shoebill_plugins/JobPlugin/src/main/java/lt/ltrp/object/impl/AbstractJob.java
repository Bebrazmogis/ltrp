package lt.ltrp.object.impl;

import lt.ltrp.JobContainer;
import lt.ltrp.job.object.Job;
import lt.ltrp.job.object.JobGate;
import lt.ltrp.job.object.JobRank;
import lt.ltrp.job.object.JobVehicle;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2016.04.14.
 */
public abstract class AbstractJob extends NamedEntityImpl implements Job {

    public static final List<Job> jobList = new ArrayList<>();

    private Location location;
    private int basePaycheck;
    private EventManager eventManager;
    private Collection<JobGate> jobGates;
    private Collection<JobVehicle> vehicles;

    public AbstractJob(int id, String name, Location location, int basePaycheck, EventManager eventManager) {
        super(id, name);
        this.location = location;
        this.basePaycheck = basePaycheck;
        this.eventManager = eventManager;
        this.jobGates = new ArrayList<>();
        this.vehicles = new ArrayList<>();
        JobContainer.INSTANCE.add(this);
    }

    public AbstractJob(int uuid, EventManager eventManager) {
        this(uuid, null, null, 0, eventManager);
    }

    protected EventManager getEventManager() {
        return eventManager;
    }

    @Override
    protected void finalize() throws Throwable {
        JobContainer.INSTANCE.remove(this);
        super.finalize();
    }

    @NotNull
    @Override
    public Collection<JobVehicle> getVehicles() {
        return vehicles;
    }

    @NotNull
    @Override
    public Collection<JobGate> getGates() {
        return jobGates;
    }


    @NotNull
    @Override
    public Collection<JobVehicle> getVehicles(@NotNull JobRank rank) {
        Collection<JobVehicle> collection = new ArrayList<>();
        vehicles.stream().filter(v -> v.getRequiredRank().equals(rank)).collect(Collectors.toCollection(() -> collection));
        return collection;
    }

    @NotNull
    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(@NotNull Location location) {
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
    public boolean isAtWork(@NotNull LtrpPlayer player) {
        return player.getLocation().distance(location) < 50.0f;
    }

    @Override
    public void sendMessage(@NotNull Color color, @NotNull String s) {
        LtrpPlayer.get().stream()
                .filter(p -> p.getJobData() != null && this.equals(p.getJobData().getJob()))
                .forEach(p -> p.sendMessage(color, s));
    }
}
