package lt.ltrp.dao;

import lt.ltrp.object.Garage;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.04.20.
 */
public interface GarageDao {

    void update(Garage garage);
    void insert(Garage garage);
    void remove(Garage garage);
    Garage get(int uuid);
    Collection<Garage> get();

}
