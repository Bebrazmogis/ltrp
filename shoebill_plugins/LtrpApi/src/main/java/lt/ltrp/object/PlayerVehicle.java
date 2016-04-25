package lt.ltrp.object;

import lt.ltrp.VehicleController;
import lt.ltrp.constant.PlayerVehiclePermission;
import lt.ltrp.data.FuelTank;
import lt.ltrp.data.VehicleLock;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;

import java.util.*;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface PlayerVehicle extends LtrpVehicle {

    static List<PlayerVehicle> get() {
        ArrayList<PlayerVehicle> vehs = new ArrayList<>();
        VehicleController.get().getVehicles().stream().filter(v -> v instanceof PlayerVehicle).forEach(v -> vehs.add((PlayerVehicle)v));
        return vehs;
    }

    static PlayerVehicle getByVehicle(Vehicle vehicle) {
        return (PlayerVehicle)VehicleController.get().getByVehicle(vehicle);
    }

    static PlayerVehicle getByUniqueId(int uniqueid) {
        LtrpVehicle vehicle = LtrpVehicle.getByUniqueId(uniqueid);
        return vehicle != null && vehicle instanceof PlayerVehicle ? (PlayerVehicle)vehicle : null;
    }

    static PlayerVehicle getClosest(LtrpPlayer player, float maxDistance) {
        Optional<PlayerVehicle> op = get()
                .stream()
                .filter(v -> v.getLocation().distance(player.getLocation()) < maxDistance)
                .min((v1, v2) -> Float.compare(v1.getLocation().distance(player.getLocation()), v2.getLocation().distance(player.getLocation())));
        return op.isPresent() ? op.get() : null;
    }

    static PlayerVehicle create(int id, int modelId, AngledLocation location, int color1, int color2, int ownerId,
                                         int deaths, FuelTank fueltank, float mileage, String license, int insurance, VehicleAlarm alarm,
                                         VehicleLock lock, int doors, int panels, int lights, int tires, float health, EventManager eventManager) {
        return VehicleController.get().createVehicle(id, modelId, location, color1, color2, ownerId, deaths, fueltank, mileage, license, insurance, alarm, lock, doors, panels, lights, tires, health, eventManager);
    }

    static PlayerVehicle create(int id, int modelId, AngledLocation location, int color1, int color2, int ownerId, FuelTank fuelTank, float mileage, String license, EventManager eventManager) {
        return VehicleController.get().createVehicle(id, modelId, location, color1, color2, ownerId, fuelTank, mileage, license, eventManager);
    }



    public void addPermission(int userId, PlayerVehiclePermission permission);

    public void addPermission(LtrpPlayer player, PlayerVehiclePermission permission);

    /**
     * Returns the user permissions for this PlayerVehicle object
     * @param userId user ID whose permissions to get
     * @return a list of user permissions, if the user has no permission an empty collection is still returned
     */
    public Collection<PlayerVehiclePermission> getPermissions(int userId);

    public Map<Integer, Collection<PlayerVehiclePermission>> getPermissions();

    public void removePermission(LtrpPlayer player, PlayerVehiclePermission permission);

    public void removePermission(int userId, PlayerVehiclePermission permission);

    public void removePermissions(int userId);

    public void setHealth(float health);

    public float getHealth();

    public int getInsurance();

    public void setInsurance(int insurance);

    public int getDeaths();

    public void setDeaths(int deaths);

    public VehicleAlarm getAlarm();

    public void setAlarm(VehicleAlarm alarm);

    public VehicleLock getLock();

    public void setLock(VehicleLock lock);

    public int getOwnerId();

    public void setOwnerId(int ownerId);

}
