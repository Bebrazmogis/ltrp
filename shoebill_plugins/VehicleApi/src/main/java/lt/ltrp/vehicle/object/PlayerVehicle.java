package lt.ltrp.vehicle.object;

import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.VehicleController;
import lt.ltrp.vehicle.constant.PlayerVehiclePermission;
import lt.ltrp.vehicle.data.VehicleLock;
import net.gtaun.shoebill.object.Vehicle;

import java.util.*;
import java.util.stream.Collectors;

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
        return (PlayerVehicle)VehicleController.get().getByUniqueId();
    }

    static PlayerVehicle getClosest(LtrpPlayer player, float maxDistance) {
        Optional<PlayerVehicle> op = get()
                .stream()
                .filter(v -> v.getLocation().distance(player.getLocation()) < maxDistance)
                .min((v1, v2) -> Float.compare(v1.getLocation().distance(player.getLocation()), v2.getLocation().distance(player.getLocation())));
        return op.isPresent() ? op.get() : null;
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
