package lt.ltrp.object;


import lt.ltrp.VehicleController;
import lt.ltrp.data.FuelTank;
import lt.ltrp.data.VehicleRadio;
import lt.ltrp.data.TaxiFare;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Vehicle;

import java.util.List;

/**
 * @author Bebras
 *         2016.04.12.
 */
public interface LtrpVehicle extends Vehicle, InventoryEntity {


    static final float SPEED_MAGIC_NUMBER = 170f;

    static List<? extends LtrpVehicle> get() {
        return VehicleController.get().getVehicles();
    }

    static LtrpVehicle getById(int id) {
        return VehicleController.get().getById(id);
    }

    static LtrpVehicle getByVehicle(Vehicle vehicle) {
        return VehicleController.get().getByVehicle(vehicle);
    }

    static LtrpVehicle getByUniqueId(int uniqueid) {
        return VehicleController.get().getByUniqueId(uniqueid);
    }

    static LtrpVehicle getClosest(Location loc, float distance) {
        return VehicleController.get().getClosest(loc, distance);
    }

    static LtrpVehicle getClosest(LtrpPlayer player, float distance) {
        return getClosest(player.getLocation(), distance);
    }

    static LtrpVehicle getClosest(Location loc) {
        return getClosest(loc, Float.MAX_VALUE);
    }

    FuelTank getFuelTank();
    void setFuelTank(FuelTank fuelTank);
    AngledLocation getSpawnLocation();
    void setSpawnLocation(AngledLocation spawnLocation);
    boolean isLocked();
    void setLocked(boolean locked);
    float getMileage();
    void setMileage(float mileage);
    int getSpeed();
    String getLicense();
    void setLicense(String license);
    void sendActionMessage(String s, float distance);
    void sendStateMessage(String s, float distance);
    void sendStateMessage(String s);
    void sendActionMessage(String s);
    boolean isUsed();
    VehicleRadio getRadio();
    LtrpPlayer getDriver();
    void setDriver(LtrpPlayer player);
    TaxiFare getTaxi();

}
