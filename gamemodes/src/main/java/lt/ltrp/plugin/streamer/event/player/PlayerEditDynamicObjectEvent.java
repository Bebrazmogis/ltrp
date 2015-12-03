package lt.ltrp.plugin.streamer.event.player;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.plugin.streamer.DynamicSampObject;
import lt.ltrp.plugin.streamer.PlayerEditDynamicObjectResponse;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * Created by Justas on 2015.06.07.
 */
public class PlayerEditDynamicObjectEvent extends PlayerEvent {

    private DynamicSampObject object;
    private Vector3D newLocation, newRotation;
    private PlayerEditDynamicObjectResponse response;


    public PlayerEditDynamicObjectEvent(LtrpPlayer player, DynamicSampObject object, PlayerEditDynamicObjectResponse response, Vector3D location, Vector3D rotation) {
        super(player);
        this.object = object;
        this.newLocation = location;
        this.newRotation = rotation;
        this.response = response;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

    public DynamicSampObject getObject() {
        return object;
    }

    public Vector3D getNewLocation() {
        return newLocation;
    }

    public Vector3D getNewRotation() {
        return newRotation;
    }

    public PlayerEditDynamicObjectResponse getResponse() {
        return response;
    }
}
