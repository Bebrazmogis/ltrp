package lt.ltrp.business.commodity;

import lt.ltrp.constant.ItemType;
import lt.ltrp.object.Business;
import lt.ltrp.object.Item;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessCommodityItem extends BusinessCommodity {

    private ItemType itemType;
    private EventManager eventManager;

    public BusinessCommodityItem(int uuid, Business business, String name, int price, int number, ItemType itemType, EventManager eventManager) {
        super(uuid, business, name, price, number);
        this.itemType = itemType;
        this.eventManager = eventManager;
    }

    public BusinessCommodityItem(int uuid, String name, ItemType itemType, EventManager eventManager) {
        super(uuid, name);
        this.itemType = itemType;
        this.eventManager = eventManager;
    }

    public BusinessCommodityItem(Business business, int number, BusinessCommodityItem commodity) {
        super(business, number, commodity);
        setItemType(commodity.getItemType());
        this.eventManager = commodity.eventManager;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    @Override
    public boolean onBuy(LtrpPlayer player) {
        if(player.getInventory().isFull()) {
            player.sendErrorMessage("Jūsų inventorius pilnas!");
            return false;
        } else {
            Item item = Item.create(this.getItemType(), this.getName(), player, eventManager);
            player.getInventory().add(item);
            return true;
        }
    }

    @Override
    public BusinessCommodityItem clone() {
        return new BusinessCommodityItem(getUUID(), getBusiness(), getName(), getPrice(), getNumber(), itemType, eventManager);
    }
}
