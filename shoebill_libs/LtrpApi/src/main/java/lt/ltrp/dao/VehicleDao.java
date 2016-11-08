package lt.ltrp.dao;

import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.data.AngledLocation;

/**
 * @author Bebras
 *         2015.12.10.
 */
public interface VehicleDao {

    void update(LtrpVehicle vehicle);
    int insert(LtrpVehicle vehicle);
    int insert(int modelId, AngledLocation location, String license, int color1, int color2, float fuel, float mileage);
    void delete(LtrpVehicle vehicle);


    // General methods for vehicles
    String generateLicensePlate();

}
