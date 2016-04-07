package lt.maze.ysf.object;

import lt.maze.ysf.Functions;
import lt.maze.ysf.constant.YSFObjectMaterialSlotUse;
import lt.maze.ysf.data.ObjectMaterial;
import lt.maze.ysf.data.ObjectMaterialText;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.SampObject;

import java.util.Optional;

/**
 * @author Bebras
 *         2016.04.03.
 */
public interface YSFObject extends SampObject {

    float getDrawDistance();
    void setMoveSpeed(float speed);
    float getMoveSpeed();
    //native GetObjectTarget(objectid, &Float:fX, &Float:fY, &Float:fZ);
    Vector3D getAttachedOffset();
    Vector3D getAttachedRotation();
    ObjectMaterial getMaterial(int index);
    ObjectMaterialText getMaterialText(int index);
    boolean isNoCameraCol();
    YSFObjectMaterialSlotUse isMaterialSlotUsed(int materialIndex);

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
