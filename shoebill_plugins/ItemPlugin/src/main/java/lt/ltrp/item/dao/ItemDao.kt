package lt.ltrp.dao


import lt.ltrp.`object`.InventoryEntity
import lt.ltrp.`object`.Item
import lt.ltrp.`object`.WeaponItem
import net.gtaun.shoebill.constant.WeaponModel

/**
 * @author Bebras
 * *         2015.12.03.
 * *
 * * Interface defines the basic methods for [lt.ltrp.object.Item] CRUD operations
 */
interface ItemDao {

    /**
     * Inserts an item that does **not** belong to anyone/anything.
     * @param item item to insert
     * *
     * @return the inserted objects' UUID
     */
    fun insert(item: Item): Int

    /**
     * Inserts a new item into persistent storage
     * @param item item to insert
     * *
     * @param entity the location to which the item belongs to
     * *
     * @return returns the newly inserted items UUID
     */
    fun insert(item: Item, entity: InventoryEntity): Int

    /**
     * Deletes the item permanently
     * @param item to delete
     */
    fun delete(item: Item)

    /**
     * Updates the item-specific data.
     * @param item to be updated
     */
    fun update(item: Item)

    /**
     * Updates the items properties and location
     * @param item item to update
     * *
     * @param inventoryEntity entity the item belongs to
     */
    fun update(item: Item, inventoryEntity: InventoryEntity)

    /**
     * Loads all items belonging to the specified entity
     * @param entity entity to load the items for
     * *
     * @return a list of the entities items
     */
    fun getItems(entity: InventoryEntity): Array<Item>

    /**

     * @param locationType location type class
     * *
     * @return Returns all the weapon data
     */
    fun getWeaponItems(locationType: Class<out InventoryEntity>): Array<WeaponItem>

    /**
     * Loads all the weapons with the specified model ID
     * @param weaponModel model
     * *
     * @param locationType location type class
     * *
     * @return an array of weapon items
     */
    fun getWeaponItems(weaponModel: WeaponModel, locationType: Class<out InventoryEntity>): Array<WeaponItem>
}
























