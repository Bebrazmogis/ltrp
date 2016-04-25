package lt.ltrp.dao;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.constant.PlayerVehiclePermission;
import lt.ltrp.data.PlayerVehicleArrest;
import lt.ltrp.data.PlayerVehicleMetadata;
import lt.ltrp.data.VehicleCrime;
import lt.ltrp.object.LtrpVehicle;
import lt.ltrp.object.PlayerVehicle;
import net.gtaun.shoebill.data.AngledLocation;

import java.util.Collection;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.10.
 */
public interface VehicleDao {

    void update(LtrpVehicle vehicle);
    int insert(LtrpVehicle vehicle);
    int insert(int modelId, AngledLocation location, String license, int color1, int color2, float fuel, float mileage);
    void delete(LtrpVehicle vehicle);

    // Player vehicles
    int[] getPlayerVehicles(LtrpPlayer player);
    int[] getArrestedPlayerVehicles(LtrpPlayer player);
    int insert(int modelId, AngledLocation spawnLocation, String license, int color1, int color2, float mileage, float fuel, int ownerId,
                int deaths, String alarm, String lock, int lockCrackTime, int lockPrice, int insurance, int doors, int panels, int lights,
                int tires, float health);
    PlayerVehicle get(int vehicleId);
    void update(PlayerVehicle playerVehicle);
    void delete(PlayerVehicle vehicle);
    int getPlayerVehicleCount(LtrpPlayer player);
    int getPlayerVehicleByLicense(String licensePlate);
    PlayerVehicleMetadata getPlayerVehicleMeta(int vehicleId);

    void setOwner(PlayerVehicle vehicle, LtrpPlayer owner);


    // Player vehicle permissions
    void removePermissions(PlayerVehicle vehicle, int userId);
    void removePermissions(PlayerVehicle vehicle);
    void removePermission(PlayerVehicle vehicle, int userId, PlayerVehiclePermission permission);
    void addPermission(PlayerVehicle vehicle, LtrpPlayer player, PlayerVehiclePermission permission);
    void addPermission(PlayerVehicle vehicle, int userId, PlayerVehiclePermission permission);
    void addPermission(int vehicleId, int userId, PlayerVehiclePermission permission);
    Collection<PlayerVehiclePermission> getPermissions(int vehicleId, int userId);
    Map<Integer, PlayerVehiclePermission> getPermissions(int vehicleId);


    void insertCrime(VehicleCrime crime);

    // Vehicle arrests
    void insertArrest(int vehicleId, int arrestedById, String reason);
    PlayerVehicleArrest getArrest(int vehicleId);
    void removeArrest(PlayerVehicleArrest arrest);

    // General methods for vehicles
    String generateLicensePlate();

}
