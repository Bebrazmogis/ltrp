package lt.ltrp.dao;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.JobVehicle;
import lt.ltrp.vehicle.LtrpVehicle;
import lt.ltrp.vehicle.PlayerVehicle;
import lt.ltrp.vehicle.VehicleCrime;

import java.util.List;

/**
 * @author Bebras
 *         2015.12.10.
 */
public interface VehicleDao {

    List<PlayerVehicle> getVehicles(LtrpPlayer player);
    void update(PlayerVehicle playerVehicle);
    PlayerVehicle getPlayerVehicle(int id);
    void insert(PlayerVehicle playerVehicle);
    String generateLicensePlate();


    void insertCrime(VehicleCrime crime);

    void update(LtrpVehicle vehicle);
    void insert(LtrpVehicle vehicle);
    void delete(LtrpVehicle vehicle);

}
