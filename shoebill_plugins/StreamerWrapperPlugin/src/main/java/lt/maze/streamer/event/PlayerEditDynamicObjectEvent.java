package lt.maze.streamer.event;

import lt.maze.streamer.object.DynamicObject;
import net.gtaun.shoebill.constant.ObjectEditResponse;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;


/**
 * @author Bebras
 *         2016.02.16.
 */
public class PlayerEditDynamicObjectEvent extends PlayerEvent {

    private Vector3D newLocation, newRotation;
    private ObjectEditResponse response;
    private DynamicObject object;

    public PlayerEditDynamicObjectEvent(Player player, Vector3D newLocation, Vector3D newRotation, ObjectEditResponse response, DynamicObject object) {
        super(player);
        this.newLocation = newLocation;
        this.newRotation = newRotation;
        this.response = response;
        this.object = object;
    }

    public Vector3D getNewLocation() {
        return newLocation;
    }

    public Vector3D getNewRotation() {
        return newRotation;
    }

    public ObjectEditResponse getResponse() {
        return response;
    }

    public DynamicObject getObject() {
        return object;
    }
}
