package lt.ltrp.dao;

import lt.ltrp.object.VehicleThiefJob;

/**
 * @author Bebras
 *         2016.05.23.
 */
public interface VehicleThiefDao extends JobDao {

    VehicleThiefJob get(int id);
    void update(VehicleThiefJob vehicleThiefJob);

}
