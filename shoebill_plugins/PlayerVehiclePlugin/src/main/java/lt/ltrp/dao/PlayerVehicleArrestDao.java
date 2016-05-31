package lt.ltrp.dao;

import lt.ltrp.data.PlayerVehicleArrest;

/**
 * @author Bebras
 *         2016.05.31.
 */
public interface PlayerVehicleArrestDao {


    PlayerVehicleArrest get(int vehicleUUID);
    void remove(PlayerVehicleArrest arrest);
    void insert(int vehicleId, int arrestedById, String reason);

}
