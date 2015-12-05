package lt.ltrp.dao;

import lt.ltrp.item.Item;

/**
 * @author Bebras
 *         2015.12.03.
 *
 * Interface defines the basic methods for {@link lt.ltrp.item.Item} CRUD operations
 */
public interface ItemDao {


    /**
     * Gets the items belonging to the specified location
     * @param locationType location class name(eg player, vehicle, etc.)
     * @param locationid location unique identifier
     * @return the array of items belonging to the location
     */
    Item[] getItems(Class locationType, int locationid);

    /**
     * Inserts a new item. Generates and sets the items' item and global id
     * @param item to insert
     * @param location class name for which the item was created(eg. player, house, etc.)
     * @param locationid the unique location identifier
     */
    void insert(Item item, Class location, int locationid);

    /**
     * Deletes the item permanently
     * @param item to delete
     */
    void delete(Item item);

    /**
     * Updates the item-specific data. See {@link lt.ltrp.item.AbstractItem} class and its children for implementation
     * @param item to be updated
     */
    void update(Item item);

    /**
     * Updates the item data and its location
     * <b>Note. This method updates data asynchronously</b>
     * @param item to be updated
     * @param location thew new location
     * @param locationid the new locations' unique identifier
     */
    void update(Item item, Class location, int locationid);

    /**
     * Generates a unused 6 digit number
     * @return a unique number
     */
    int generatePhonenumber();

}
























