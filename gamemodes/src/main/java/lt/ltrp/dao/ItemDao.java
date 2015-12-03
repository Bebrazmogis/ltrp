package lt.ltrp.dao;

import lt.ltrp.item.Item;
import lt.ltrp.item.ItemType;

/**
 * @author Bebras
 *         2015.12.03.
 */
public interface ItemDao {


    int obtainItemId(String name, String classname, ItemType type, Class locationType, int locationid);
    Item[] getItems(Class locationType, int locationid);
    void updateItem(Item item);
    void updateItem(Item item, Class locationType, int locationid);

}
