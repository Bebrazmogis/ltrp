package lt.ltrp.house.dao;


import lt.ltrp.house.object.House;

import java.util.Collection;

/**
 * @author Bebras
 *         2015.12.05.
 */
public interface HouseDao {

    void insert(House house);
    void update(House house);
    void remove(House house);
    House get(int uuid);
    Collection<House> get();

}
