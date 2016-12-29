package lt.ltrp.vehicle.dao;


/**
 * @author Bebras
 *         2015.12.10.
 *
 *         Defines a method to obtain a vehicle ID unifying all vehicle IDs
 *         All vehicle DAOs must implement/extend this interface
 */
public interface VehicleDao {

    /**
     * Returns a new vehicle UUID or {@link lt.ltrp.object.Entity.Companion#INVALID_ID} if problems occur
     *
     * @return
     */
    int insert();
/*
    void update(LtrpVehicle vehicle);
    int insert(LtrpVehicle vehicle);
    int insert(int modelId, AngledLocation location, String license, int color1, int color2, float fuel, float mileage);
    void delete(LtrpVehicle vehicle);
    LtrpVehicle get();

    int obtainId();


    // General methods for vehicles
    String generateLicensePlate();
*/
}
