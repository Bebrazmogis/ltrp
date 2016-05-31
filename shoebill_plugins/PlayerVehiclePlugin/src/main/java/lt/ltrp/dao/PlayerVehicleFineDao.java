package lt.ltrp.dao;

import lt.ltrp.data.VehicleFine;
import lt.ltrp.object.PlayerVehicle;

import java.util.List;

/**
 * @author Bebras
 *         2016.05.31.
 */
public interface PlayerVehicleFineDao {

    List<VehicleFine> get(PlayerVehicle vehicle);
    List<VehicleFine> getUnpaid(PlayerVehicle vehicle);
    void update(VehicleFine fine);
    void setPaid(VehicleFine fine);
    int insert(VehicleFine fine);

}
