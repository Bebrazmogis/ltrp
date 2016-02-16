package lt.maze.event;

import lt.maze.object.DynamicObject;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class PlayerSelectDynamicObjectEvent extends PlayerEvent {

    private DynamicObject object;
    private int modelId;
    private Vector3D position;

    public PlayerSelectDynamicObjectEvent(Player player, DynamicObject object, int modelId, Vector3D position) {
        super(player);
        this.object = object;
        this.modelId = modelId;
        this.position = position;
    }

    public DynamicObject getObject() {
        return object;
    }

    public int getModelId() {
        return modelId;
    }

    public Vector3D getPosition() {
        return position;
    }
}

