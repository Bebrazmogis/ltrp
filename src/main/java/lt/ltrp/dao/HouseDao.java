package lt.ltrp.dao;


import lt.ltrp.property.data.HouseWeedSapling;
import lt.ltrp.property.object.House;

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
    void updateWeed(HouseWeedSapling sapling);

    /**
     * Insert a new sapling
     * @param sapling
     */
    void insertWeed(HouseWeedSapling sapling);


}
