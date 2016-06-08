package lt.ltrp.dao;

import lt.ltrp.data.FuelStation;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.06.06.
 */
public interface FuelStationDao {

    Collection<FuelStation> get();
    FuelStation get(int uuid);
    void update(FuelStation fuelStation);
    void remove(FuelStation fuelStation);
    int insert(FuelStation fuelStation);

}
