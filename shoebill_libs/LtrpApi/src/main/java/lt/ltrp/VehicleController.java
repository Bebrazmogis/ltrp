package lt.ltrp;


import lt.ltrp.dao.VehicleDao;
import lt.ltrp.data.FuelTank;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.entities.Vehicle;

import java.util.List;

/**
 * @author Bebras
 *         2016.04.12.
 */
public interface VehicleController {


    LtrpVehicle getById(int id);
    LtrpVehicle getByVehicle(Vehicle vehicle);
    LtrpVehicle getByUniqueId(int uuid);
    LtrpVehicle getClosest(Location loc, float distance);
    List<LtrpVehicle> getVehicles();

    LtrpVehicle createVehicle(int id, int modelId, AngledLocation location, int color1, int color2, String license, float mileage);
    LtrpVehicle createVehicle(int id, int modelId, AngledLocation location, int color1, int color2, FuelTank fueltank, String license, float mileage);

    VehicleDao getDao();

    class Instance
    {
        static VehicleController instance = null;
    }

    static VehicleController get()
    {
        return Instance.instance;
    }




}
