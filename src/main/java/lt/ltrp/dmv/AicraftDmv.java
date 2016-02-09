package lt.ltrp.dmv;

import lt.ltrp.constant.LicenseType;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;

/**
 * @author Bebras
 *         2015.12.27.
 */
public class AicraftDmv extends CheckpointDmvImpl {

    private static final ArrayList<LicenseType> LICENSE_TYPES = new ArrayList<>(1);

    static {
        LICENSE_TYPES.add(LicenseType.Aircraft);
    }

    public AicraftDmv(int id) {
        super(id, null, null, null);
    }



    @Override
    public AbstractCheckpointTest startCheckpointTest(LtrpPlayer player, LtrpVehicle vehicle, EventManager eventManager) {
        return FlyingTest.create(player, vehicle, this, eventManager);
    }

    @Override
    public int getCheckpointTestPrice() {
        return FlyingTest.PRICE;
    }

    @Override
    public ArrayList<LicenseType> getLicenseType() {
        return LICENSE_TYPES;
    }
}
