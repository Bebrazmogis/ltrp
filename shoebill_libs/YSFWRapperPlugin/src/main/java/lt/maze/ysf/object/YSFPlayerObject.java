package lt.maze.ysf.object;

import lt.maze.ysf.constant.YSFObjectMaterialSlotUse;

import java.util.Optional;

/**
 * @author Bebras
 *         2016.04.03.
 */
public abstract class YSFPlayerObject extends PlayerObject {

   //native Float:GetPlayerObjectTarget(playerid, objectid, &Float:fX, &Float:fY, &Float:fZ);

    public abstract YSFObjectMaterialSlotUse isMaterialSlotUsed(int materialIndex);
    public abstract int getObjectType();



    public static YSFPlayerObject get(int id) {
        Optional<YSFPlayerObject> op = YSFObjectManager.getInstance().getPlayerObjects()
                .stream()
                .filter(o -> o.getId() == id)
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }
}

