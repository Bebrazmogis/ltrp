package lt.ltrp.player.vehicle.dao;

import lt.ltrp.player.vehicle.data.PlayerVehicleMetadata;
import lt.ltrp.data.VehicleCrime;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.player.vehicle.object.PlayerVehicle;
import net.gtaun.shoebill.data.AngledLocation;

/**
 * @author Bebras
 *         2016.05.23.
 */
public interface PlayerVehicleDao extends VehicleDao {

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


    void insertCrime(VehicleCrime crime);

}
