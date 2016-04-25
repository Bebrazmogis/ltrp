package lt.ltrp.object;

import lt.ltrp.PropertyController;
import lt.ltrp.constant.BusinessType;
import lt.ltrp.data.BusinessCommodity;
import lt.ltrp.dialog.property.BusinessCommodityListDialog;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface Business extends Property {

    static final int DEFAULT_PICKUP_MODEL = 1239;
    static final Color DEFAULT_HOUSE_LABEL_COLOR = Color.WHITE;
    static final int DEFAULT_RESOURCES = 1000;
    static final int DEFAULT_COMMODITY_LIMIT = 20;
    static final int MIN_NAME_LENGTH = 20;

    static Collection<Business> get() {
        return PropertyController.get().getBusinesses();
    }

    static Business get(int id) {
        Optional<Business> op = get()
                .stream()
                .filter(b -> b.getUUID() == id)
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }

    static Business getClosest(Location location, float maxDistance) {
        Optional<Business> op = get().stream().min((b1, b2) -> {
            return Float.compare(Math.min(b1.getEntrance().distance(location), b1.getExit().distance(location)),
                    Math.min(b2.getEntrance().distance(location), b2.getExit().distance(location)));
        });
        if(op.isPresent()) {
            float distance = op.get().getEntrance().distance(location);
            if(distance <= maxDistance) {
                return op.get();
            }
        }
        return null;
    }

    static Business getClosest(Location location) {
        return getClosest(location, Float.MAX_VALUE);
    }

    static Business create(int id, String name, BusinessType type, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit, Color labelColor,
                           int money, int resources, int commodityLimit, EventManager eventManager) {
        return PropertyController.get().createBusiness(id, name, type, ownerUserId, pickupModelId, price, entrance, exit, labelColor, money, resources, commodityLimit, eventManager);
    }

    static Business create(int id, BusinessType type, Location entrance, Location exit, int price, EventManager eventManager1) {
        return create(id, "", type, LtrpPlayer.INVALID_USER_ID, DEFAULT_PICKUP_MODEL, price, entrance, exit, DEFAULT_HOUSE_LABEL_COLOR, 0, DEFAULT_RESOURCES, DEFAULT_COMMODITY_LIMIT, eventManager1);
    }

    static List<BusinessCommodity> getAvailableCommodities(BusinessType type) {
        return PropertyController.get().getAvailableCommodities(type);
    }

    int getMoney();
    void addMoney(int amount);
    BusinessType getBusinessType();
    List<BusinessCommodity> getCommodities();
    void addCommodity(int index, BusinessCommodity commodity);
    void addCommodity(BusinessCommodity commodity);
    void removeCommodity(BusinessCommodity commodity);
    void removeCommodity(int index);
    int getCommodityCount();
    int getCommodityLimit();
    int getResources();
    void setResources(int amount);
    int getEntrancePrice();
    void setEntrancePrice(int entrancePrice);
    int getResourcePrice();
    void setResourcePrice(int price);
    void setPickupModelId(int modelId);
    int getPickupModelId();

    void showCommodities(LtrpPlayer player);
    void showCommodities(LtrpPlayer player, BusinessCommodityListDialog.SelectCommodityHandler selectCommodityHandler);




}
