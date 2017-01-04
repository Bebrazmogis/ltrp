package lt.maze.ysf.object;

import lt.maze.ysf.Functions;
import lt.maze.ysf.constant.YSFObjectMaterialSlotUse;
import lt.maze.ysf.data.ObjectMaterial;
import lt.maze.ysf.data.ObjectMaterialText;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.entities.Player;
import net.gtaun.shoebill.entities.SampObject;

import java.util.Optional;

/**
 * @author Bebras
 *         2016.04.03.
 */
public abstract class YSFObject extends SampObject {

    public abstract float getDrawDistance();
    public abstract void setMoveSpeed(float speed);
    public abstract float getMoveSpeed();
    //native GetObjectTarget(objectid, &Float:fX, &Float:fY, &Float:fZ);
    public abstract Vector3D getAttachedOffset();
    public abstract Vector3D getAttachedRotation();
    public abstract ObjectMaterial getMaterial(int index);
    public abstract ObjectMaterialText getMaterialText(int index);
    public abstract boolean isNoCameraCol();
    public abstract YSFObjectMaterialSlotUse isMaterialSlotUsed(int materialIndex);

    /**
     * native GetPlayerSurfingPlayerObjectID(playerid);
     * @param p
     * @return
     */
    static YSFObject getBySurfingPlayer(Player p) {
        int id = Functions.GetPlayerSurfingPlayerObjectID(p.getId());
        return YSFObject.get(id);
    }

    /**
     *  native GetPlayerCameraTargetPlayerObj(playerid);
     * @param p
     * @return
     */
    static YSFObject getCameraTarget(Player p) {
        int id = Functions.GetPlayerCameraTargetPlayerObj(p.getId());
        return YSFObject.get(id);
    }

    public static YSFObject get(int id) {
        Optional<YSFObject> op = YSFObjectManager.getInstance().getObjects()
                .stream()
                .filter(o -> o.getId() == id)
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }

}
