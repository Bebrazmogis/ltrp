package lt.ltrp.item.dao;


import lt.ltrp.item.object.InventoryEntity;
import lt.ltrp.item.object.Item;

/**
 * @author Bebras
 *         2015.12.03.
 *
 * Interface defines the basic methods for {@link lt.ltrp.item.Item} CRUD operations
 */
public interface ItemDao {

    /**
     * Inserts a new item into persistent storage
     * @param item item to insert
     * @param entity the location to which the item belongs to
     * @return returns the newly inserted items UUID
     */
    int insert(Item item, InventoryEntity entity);

    /**
     * Deletes the item permanently
     * @param item to delete
     */
    void delete(Item item);

    /**
     * Updates the item-specific data.
     * @param item to be updated
     */
    void update(Item item);

    /**
     * Updates the items properties and location
     * @param item item to update
     * @param inventoryEntity entity the item belongs to
     */
    void update(Item item, InventoryEntity inventoryEntity);

    /**
     * Loads all items belonging to the specified entity
     * @param entity entity to load the items for
     * @return a list of the entities items
     */
    Item[] getItems(InventoryEntity entity);
}
























