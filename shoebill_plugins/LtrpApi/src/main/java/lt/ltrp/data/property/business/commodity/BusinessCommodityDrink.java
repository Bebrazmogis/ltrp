package lt.ltrp.data.property.business.commodity;

import lt.ltrp.constant.ItemType;
import lt.ltrp.object.Business;
import lt.ltrp.object.DrinkItem;
import lt.ltrp.object.Item;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.30.
 *
 *         This class represents a drink commodity, it will create a {@link lt.ltrp.object.DrinkItem} instance if user purchases it
 */
public class BusinessCommodityDrink extends BusinessCommodity {

    private SpecialAction action;
    private int drunkLevelPerSip;
    private EventManager eventManager;

    public BusinessCommodityDrink(int uuid, String name, SpecialAction specialAction, int drunkLevelPerSip, EventManager eventManager) {
        super(uuid, name);
        this.action = specialAction;
        this.drunkLevelPerSip = drunkLevelPerSip;
        this.eventManager = eventManager;
    }

    public BusinessCommodityDrink(Business business, int number, BusinessCommodityDrink commodity, EventManager eventManager) {
        super(business, number, commodity);
        this.action = commodity.action;
        this.drunkLevelPerSip = commodity.drunkLevelPerSip;
        this.eventManager = eventManager;
    }

    public BusinessCommodityDrink(int uuid, Business business, String name, int price, int number, SpecialAction action, int drunkLevelPerSip, EventManager eventManager) {
        super(uuid, business, name, price, number);
        this.action = action;
        this.drunkLevelPerSip = drunkLevelPerSip;
        this.eventManager = eventManager;
    }

    public SpecialAction getAction() {
        return action;
    }

    public int getDrunkLevelPerSip() {
        return drunkLevelPerSip;
    }

    @Override
    public boolean onBuy(LtrpPlayer player) {
        if(player.getSpecialAction() != SpecialAction.NONE) {
            player.sendErrorMessage("Jūs jau kažką geriate/valgote");
            return false;
        } else {
            DrinkItem item = (DrinkItem) Item.create(ItemType.Drink, getName(), action, player, eventManager);
            item.use(player, player.getInventory());
            if(!item.isDestroyed()) item.destroy();
            return true;
        }
    }

    @Override
    public BusinessCommodityDrink clone() {
        return new BusinessCommodityDrink(getUUID(), getBusiness(), getName(), getPrice(), getNumber(), getAction(), getDrunkLevelPerSip(), eventManager);
    }
}
