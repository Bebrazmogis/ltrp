package lt.maze.streamer.event;

import lt.maze.streamer.object.DynamicObject;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class PlayerShootDynamicObjectEvent extends PlayerEvent {

    private DynamicObject object;
    private WeaponModel weaponModel;
    private Vector3D position;

    public PlayerShootDynamicObjectEvent(Player player, DynamicObject object, WeaponModel weaponModel, Vector3D position) {
        super(player);
        this.object = object;
        this.weaponModel = weaponModel;
        this.position = position;
    }

    public DynamicObject getObject() {
        return object;
    }

    public WeaponModel getWeaponModel() {
        return weaponModel;
    }

    public Vector3D getPosition() {
        return position;
    }
}
