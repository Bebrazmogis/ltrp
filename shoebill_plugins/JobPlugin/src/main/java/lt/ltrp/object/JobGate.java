package lt.ltrp.object;

import lt.maze.streamer.object.DynamicObject;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Destroyable;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class JobGate implements Destroyable, Entity {

    private int uuid;
    private int modelId;
    private Job job;
    private Rank rank;
    private DynamicObject object;
    private Vector3D openPosition, closedPosition, openRotation, closedRotation;
    private boolean destroyed, open, defaultOpen;
    private float speed;

    public JobGate(int uuid, int modelId, Vector3D openPos, Vector3D openRot, Vector3D closedPos, Vector3D closedRot, float speed, Job job, Rank requiredRank, boolean defaultOpen) {
        this.uuid = uuid;
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

    public Job getJob() {
        return job;
    }

    public Rank getRank() {
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

    public Vector3D getOpenPosition() {
        return openPosition;
    }

    public Vector3D getClosedPosition() {
        return closedPosition;
    }

    public Vector3D getOpenRotation() {
        return openRotation;
    }

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
    public void setUUID(int i) {
        this.uuid = i;
    }

    @Override
    public int getUUID() {
        return uuid;
    }
}
