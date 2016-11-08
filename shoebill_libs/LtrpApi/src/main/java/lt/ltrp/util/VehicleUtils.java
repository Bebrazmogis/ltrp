package lt.ltrp.util;

import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.constant.VehicleModelInfoType;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class VehicleUtils {

    public static Location getBehind(LtrpVehicle vehicle, float distance) {
        AngledLocation loc = vehicle.getLocation().clone();
        loc.x += (distance * Math.sin(Math.toRadians(-loc.angle + 180)));
        loc.y += (distance * Math.cos(Math.toRadians(-loc.angle + 180)));
        return loc;
    }

    public static Location getBehind(LtrpVehicle vehicle) {
        Vector3D size = VehicleModel.getModelInfo(vehicle.getModelId(), VehicleModelInfoType.SIZE);
        return getBehind(vehicle, size.y);
    }

}
