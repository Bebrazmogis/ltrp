package lt.ltrp.dao;

import lt.ltrp.object.Property;

/**
 * @author Bebras
 *         2016.04.19.
 */
public interface PropertyDao {

    void insert(Property property);
    void update(Property property);
    void remove(Property property);

}
