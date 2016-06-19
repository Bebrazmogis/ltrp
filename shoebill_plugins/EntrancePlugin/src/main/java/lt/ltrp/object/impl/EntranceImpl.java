package lt.ltrp.object.impl;

import lt.ltrp.event.EntranceDestroyEvent;
import lt.ltrp.object.Entrance;
import lt.ltrp.object.Job;
import lt.maze.streamer.object.DynamicLabel;
import lt.maze.streamer.object.DynamicPickup;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Color;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class EntranceImpl extends EntityImpl implements Entrance {

    private int pickupModelId;
    private Color labelColor;
    private String text;
    private AngledLocation location;
    private Job job;
    private DynamicLabel label;
    private DynamicPickup pickup;
    private boolean allowsVehicles;
    private boolean destroyed;
    private Entrance exit;
    private EventManager eventManager;

    public EntranceImpl(int id, Color labelColor, int pickupModelId, String text, AngledLocation location, Job job, boolean allowsVehicles, EventManager eventManager) {
        super(id);
        this.labelColor = labelColor;
        this.pickupModelId = pickupModelId;
        this.text = text;
        this.location = location;
        this.job = job;
        this.allowsVehicles = allowsVehicles;
        this.eventManager = eventManager;
        updateLabel();
        updatePickup();
    }

    public void updateLabel() {
        if(label != null) label.destroy();
        label = null;

        if(labelColor != null && text != null) {
            label = DynamicLabel.create(text, labelColor, location);
        }
    }

    public void updatePickup() {
        if(pickup != null) pickup.destroy();
        if(pickupModelId > 0)
            pickup = DynamicPickup.create(pickupModelId, 1, location);
    }

    @Override
    public int getPickupModelId() {
        return pickupModelId;
    }

    @Override
    public void setPickupModelId(int pickupModelId) {
        this.pickupModelId = pickupModelId;
    }

    @Override
    public Color getColor() {
        return labelColor;
    }

    @Override
    public void setColor(Color labelColor) {
        this.labelColor = labelColor;
        updateLabel();
    }

    @Override
    public boolean allowsVehicles() {
        return allowsVehicles;
    }

    @Override
    public void setAllowVehicles(boolean set) {
        this.allowsVehicles = set;
    }

    @Override
    public boolean isJob() {
        return job != null;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
        updateLabel();
    }

    @Override
    public AngledLocation getLocation() {
        return location;
    }

    @Override
    public void setLocation(AngledLocation location) {
        this.location = location;
    }

    @Override
    public Job getJob() {
        return job;
    }

    @Override
    public void setJob(Job job) {
        this.job = job;
    }

    @Override
    public Entrance getExit() {
        return exit;
    }

    @Override
    public void setExit(Entrance exit) {
        this.exit = exit;
    }

    @Override
    public DynamicLabel getLabel() {
        return label;
    }

    @Override
    public void destroy() {
        destroyed = true;
        if(label != null) label.destroy();
        if(pickup != null) pickup.destroy();
        label = null;
        pickup = null;
        eventManager.dispatchEvent(new EntranceDestroyEvent(this));
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public DynamicPickup getPickup() {
        return pickup;
    }
}
