package lt.ltrp.property.dao;

import lt.ltrp.property.object.Property;

/**
 * @author Bebras
 *         2016.04.19.
 */
public interface PropertyDao {

    void insert(Property property);
    void update(Property property);
    void remove(Property property);

}
