package lt.ltrp.vehicle;


import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Vehicle;

import java.util.List;

/**
 * @author Bebras
 *         2016.04.12.
 */
public interface VehicleController {


    lt.ltrp.vehicle.object.LtrpVehicle getById(int id);
    lt.ltrp.vehicle.object.LtrpVehicle getByVehicle(Vehicle vehicle);
    lt.ltrp.vehicle.object.LtrpVehicle getByUniqueId();
    lt.ltrp.vehicle.object.LtrpVehicle getClosest(Location loc, float distance);
    List<? extends lt.ltrp.vehicle.object.LtrpVehicle> getVehicles();
    lt.ltrp.vehicle.object.VehicleAlarm createAlarm(lt.ltrp.vehicle.object.PlayerVehicle playerVehicle, int level);

    class Instance
    {
        static VehicleController instance = null;
    }

    static VehicleController get()
    {
        return Instance.instance;
    }




}
