package lt.ltrp.dao;


import lt.ltrp.constant.HouseUpgradeType;
import lt.ltrp.data.HouseWeedSapling;
import lt.ltrp.object.House;

import java.util.Collection;
import java.util.List;

/**
 * @author Bebras
 *         2015.12.05.
 */
public interface HouseDao {


    /**
     * Retrieves the unharvested saplings for a specific house
     * @param house
     * @return returns a list of saplings
     */
    List<HouseWeedSapling> getWeed(House house);

    /**
     * Updates sapling data
     * @param sapling
     */
    void update(HouseWeedSapling sapling);

    /**
     * Insert a new sapling
     * @param sapling
     */
    void insert(HouseWeedSapling sapling);

    void remove(HouseWeedSapling weedSapling);

    void insert(House house);
    void update(House house);
    void remove(House house);
    House get(int uuid);
    Collection<House> get();

    void insert(House house, HouseUpgradeType houseUpgradeType);
    void remove(House house, HouseUpgradeType houseUpgradeType);
    List<HouseUpgradeType> get(House house);
}
