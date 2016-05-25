package lt.ltrp.object;

import lt.ltrp.EntrancePlugin;
import lt.maze.streamer.object.DynamicLabel;
import lt.maze.streamer.object.DynamicPickup;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.22.
 */
public interface Entrance extends DestroyableEntity {

    static final int DEFAULT_PICKUP_MODEL = 19606;
    static final Color DEFAULT_LABEL_COLOR = new Color(0x1299A2AA);
    static final int MIN_TEXT_LENGTH = 10;

    static Entrance create(AngledLocation location, String name) {
        return create(location, name, DEFAULT_LABEL_COLOR, DEFAULT_PICKUP_MODEL, null, false);
    }

    static Entrance create(AngledLocation location, String text, Color labelColor, int pickupModelId, Job job, boolean allowsVehicles) {
        return EntrancePlugin.get(EntrancePlugin.class).createEntrance(location, text, labelColor, pickupModelId, job, allowsVehicles);
    }

    static Collection<Entrance> get() {
        return EntrancePlugin.get(EntrancePlugin.class).getEntrances();
    }

    static Entrance get(int uuid) {
        return EntrancePlugin.get(EntrancePlugin.class).get(uuid);
    }

    static Entrance getClosest(Location location) {
        return getClosest(location, Float.POSITIVE_INFINITY);
    }

    static Entrance getClosest(Location location, float maxDistance) {
        return EntrancePlugin.get(EntrancePlugin.class).getClosest(location, maxDistance);
    }


    AngledLocation getLocation();
    void setLocation(AngledLocation angledLocation);

    DynamicPickup getPickup();
    DynamicLabel getLabel();

    String getText();
    void setText(String text);

    int getPickupModelId();
    void setPickupModelId(int modelId);

    Color getColor();
    void setColor(Color color);

    boolean allowsVehicles();
    void setAllowVehicles(boolean set);

    boolean isJob();
    Job getJob();
    void setJob(Job job);

    Entrance getExit();
    void setExit(Entrance entrance);

}
