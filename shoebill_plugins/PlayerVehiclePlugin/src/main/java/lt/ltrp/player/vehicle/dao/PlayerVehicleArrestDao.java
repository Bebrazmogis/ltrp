package lt.ltrp.player.vehicle.dao;

import lt.ltrp.player.vehicle.data.PlayerVehicleArrest;

/**
 * @author Bebras
 *         2016.05.31.
 */
public interface PlayerVehicleArrestDao {


    PlayerVehicleArrest get(int vehicleUUID);
    void remove(PlayerVehicleArrest arrest);
    void insert(int vehicleId, int arrestedById, String reason);

}
