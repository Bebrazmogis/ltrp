package lt.maze.ysf.object;

import lt.maze.ysf.object.impl.YSFPickupImpl;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.entities.Player;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.04.03.
 */
public interface YSFPickup {

    boolean isValid();
    boolean isStreamedIn(Player p);
    Vector3D getPosition();
    int getModel();
    int getType();
    int getWorldId();

    public static Collection<YSFPickup> get() {
        return YSFObjectManager.getInstance().getPickups();
    }

    public static YSFPickup get(int id) {
        Optional<YSFPickup> op = YSFObjectManager.getInstance().getPickups()
                .stream()
                .filter(v -> v instanceof YSFPickupImpl && ((YSFPickupImpl)v).getPickup().getId() == id)
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }

    public static YSFPickup get(YSFPickup pickup) {
        Optional<YSFPickup> op = YSFObjectManager.getInstance().getPickups()
                .stream()
                .filter(v -> v instanceof YSFPickupImpl && ((YSFPickupImpl) v).getPickup().equals(pickup))
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }


}
/*
native IsValidPickup(pickupid);
native IsPickupStreamedIn(playerid, pickupid);
native GetPickupPos(pickupid, &Float:fX, &Float:fY, &Float:fZ);
native GetPickupModel(pickupid);
native GetPickupType(pickupid);
native GetPickupVirtualWorld(pickupid);

 */
