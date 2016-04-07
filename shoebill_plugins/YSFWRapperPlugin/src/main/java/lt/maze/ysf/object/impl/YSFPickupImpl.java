package lt.maze.ysf.object.impl;

import lt.maze.ysf.Functions;
import lt.maze.ysf.object.YSFPickup;
import net.gtaun.shoebill.amx.types.ReferenceFloat;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Pickup;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class YSFPickupImpl implements YSFPickup {

    private Pickup pickup;

    public YSFPickupImpl(Pickup pickup) {
        this.pickup = pickup;
    }

    public Pickup getPickup() {
        return pickup;
    }

    @Override
    public boolean isValid() {
        return Functions.IsValidPickup(pickup.getId()) != 0;
    }

    @Override
    public boolean isStreamedIn(Player p) {
        return Functions.IsPickupStreamedIn(p.getId(), pickup.getId()) != 0;
    }

    @Override
    public Vector3D getPosition() {
        ReferenceFloat x = new ReferenceFloat(0f);
        ReferenceFloat y = new ReferenceFloat(0f);
        ReferenceFloat z = new ReferenceFloat(0f);
        Functions.GetPickupPos(pickup.getId(), x, y, z);
        return new Vector3D(x.getValue(), y.getValue(), z.getValue());
    }

    @Override
    public int getModel() {
        return Functions.GetPickupModel(pickup.getId());
    }

    @Override
    public int getType() {
        return Functions.GetPickupType(pickup.getId());
    }

    @Override
    public int getWorldId() {
        return Functions.GetPickupVirtualWorld(pickup.getId());
    }
}
