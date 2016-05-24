package lt.ltrp;


import lt.ltrp.dao.PlayerVehicleDao;
import lt.ltrp.dao.VehicleDao;
import lt.ltrp.data.FuelTank;
import lt.ltrp.data.VehicleLock;
import lt.ltrp.object.LtrpVehicle;
import lt.ltrp.object.PlayerVehicle;
import lt.ltrp.object.VehicleAlarm;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;

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
    VehicleAlarm createAlarm(PlayerVehicle playerVehicle, int level);

    LtrpVehicle createVehicle(int id, int modelId, AngledLocation location, int color1, int color2, String license, float mileage);
    LtrpVehicle createVehicle(int id, int modelId, AngledLocation location, int color1, int color2, FuelTank fueltank, String license, float mileage);

    PlayerVehicle createVehicle(int id, int modelId, AngledLocation location, int color1, int color2, int ownerId,
                                int deaths, FuelTank fueltank, float mileage, String license, int insurance, VehicleAlarm alarm,
                                VehicleLock lock, int doors, int panels, int lights, int tires, float health, EventManager eventManager);

    default PlayerVehicle createVehicle(int id, int modelId, AngledLocation location, int color1, int color2, int ownerId, FuelTank fuelTank, float mileage, String license, EventManager eventManager) {
        return createVehicle(id, modelId, location, color1, color2, ownerId, 0, fuelTank, mileage, license, 0, null, null, 0, 0, 0, 0, 1000f, eventManager);
    }

    VehicleDao getDao();
    PlayerVehicleDao getPlayerVehicleDao();

    class Instance
    {
        static VehicleController instance = null;
    }

    static VehicleController get()
    {
        return Instance.instance;
    }




}
