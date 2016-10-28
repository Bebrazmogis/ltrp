package lt.ltrp.object.impl;

import lt.ltrp.job.object.Job;
import lt.ltrp.job.object.JobGate;
import lt.ltrp.job.object.JobRank;
import lt.maze.streamer.object.DynamicObject;
import net.gtaun.shoebill.data.Vector3D;
import org.jetbrains.annotations.NotNull;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class JobGateImpl extends EntityImpl implements JobGate {

    private int modelId;
    private Job job;
    private JobRank rank;
    private DynamicObject object;
    private Vector3D openPosition, closedPosition, openRotation, closedRotation;
    private boolean destroyed, open, defaultOpen;
    private float speed;

    public JobGateImpl(int uuid, int modelId, Vector3D openPos, Vector3D openRot, Vector3D closedPos, Vector3D closedRot, float speed, Job job, JobRank requiredRank, boolean defaultOpen) {
        super(uuid);
        this.modelId = modelId;
        this.job = job;
        this.rank = requiredRank;
        this.openPosition = openPos;
        this.openRotation = openRot;
        this.closedPosition = closedPos;
        this.closedRotation = closedRot;
        this.speed = speed;
        this.open = this.defaultOpen = defaultOpen;

        if(defaultOpen)
            this.object = DynamicObject.create(modelId, openPos, openRot);
        else
            this.object = DynamicObject.create(modelId, closedPos, closedRot);
    }

    @NotNull
    public Job getJob() {
        return job;
    }

    @NotNull
    public JobRank getRank() {
        return rank;
    }

    public void close() {
        if(isOpen() && !isMoving()) {
            open = false;
            object.move(closedPosition,closedRotation, speed, null);
        }
    }

    public void open() {
        if(!isOpen() && !isMoving()) {
            open = true;
            object.move(openPosition, openRotation, speed, null);
        }
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isMoving() {
        return object.isMoving();
    }

    public int getModelId() {
        return modelId;
    }

    public boolean isDefaultOpen() {
        return defaultOpen;
    }

    @NotNull
    public Vector3D getOpenPosition() {
        return openPosition;
    }

    @NotNull
    public Vector3D getClosedPosition() {
        return closedPosition;
    }

    @NotNull
    public Vector3D getOpenRotation() {
        return openRotation;
    }

    @NotNull
    public Vector3D getClosedRotation() {
        return closedRotation;
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    protected void finalize() throws Throwable {
        if(!isDestroyed())
            destroy();
        super.finalize();
    }

    @Override
    public void destroy() {
        destroyed = true;
        object.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void setOpen(boolean b) {
        if(b) open();
        else close();
    }

    @Override
    public void setDefaultOpen(boolean b) {
        this.defaultOpen = b;
    }
}
