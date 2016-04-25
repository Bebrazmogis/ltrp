package lt.ltrp.data;

import lt.ltrp.constant.ItemType;
import lt.ltrp.object.Business;import java.lang.String;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class ShopBusinessCommodity extends BusinessCommodity {

    private ItemType itemType;

    public ShopBusinessCommodity(int uuid, Business business, String name, int price, int number, ItemType itemType) {
        super(uuid, business, name, price, number);
        this.itemType = itemType;
    }

    public ShopBusinessCommodity(int uuid, String name, ItemType itemType) {
        super(uuid, name);
        this.itemType = itemType;
    }

    public ShopBusinessCommodity(Business business, int number, ShopBusinessCommodity commodity) {
        super(business, number, commodity);
        setItemType(commodity.getItemType());
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }
}
