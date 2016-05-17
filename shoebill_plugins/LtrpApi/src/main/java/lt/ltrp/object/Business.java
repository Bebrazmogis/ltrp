package lt.ltrp.object;

import lt.ltrp.PropertyController;
import lt.ltrp.constant.BusinessType;
import lt.ltrp.data.property.business.commodity.BusinessCommodity;
import lt.ltrp.dialog.property.business.BusinessCommodityListDialog;
import lt.maze.streamer.object.DynamicPickup;
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
    static final Color DEFAULT_BUSINESS_LABEL_COLOR = Color.WHITE;
    static final int DEFAULT_RESOURCES = 1000;
    static final int DEFAULT_COMMODITY_LIMIT = 20;
    static final int MIN_NAME_LENGTH = 20;
    static final int MAX_RESOURCES = 2000;

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
        Business closest = null;
        float min = maxDistance;
        for (Business b : get()) {
            float distance = Math.min(b.getEntrance().distance(location), b.getExit() != null ? b.getExit().distance(location) : Float.POSITIVE_INFINITY);
            if(distance <= min) {
                closest = b;
                min = distance;
            }
        }
        return closest;
        /*
        Optional<Business> op = get().stream().min((b1, b2) -> {
            if(b1.getExit() == null && b2.getExit() != null) {
                return Float.compare(b1.getEntrance().distance(location),
                        Math.min(b2.getEntrance().distance(location), b2.getExit().distance(location)));
            }
            else if(b2.getExit() == null && b1.getExit() != null)
                return Float.compare(Math.min(b1.getEntrance().distance(location), b1.getExit().distance(location)),
                        b2.getEntrance().distance(location));
            else if(b1.getExit() == null && b2.getExit() == null)
                return Float.compare(b1.getEntrance().distance(location),
                        b2.getEntrance().distance(location));
            else
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
        */
    }

    static Business getClosest(Location location) {
        return getClosest(location, Float.MAX_VALUE);
    }

    static Business create(int id, String name, BusinessType type, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit, Color labelColor,
                           int money, int resources, int commodityLimit, EventManager eventManager) {
        return PropertyController.get().createBusiness(id, name, type, ownerUserId, pickupModelId, price, entrance, exit, labelColor, money, resources, commodityLimit, eventManager);
    }

    static Business create(int id, BusinessType type, Location entrance, Location exit, int price, EventManager eventManager1) {
        return create(id, "", type, LtrpPlayer.INVALID_USER_ID, DEFAULT_PICKUP_MODEL, price, entrance, exit, DEFAULT_BUSINESS_LABEL_COLOR, 0, DEFAULT_RESOURCES, DEFAULT_COMMODITY_LIMIT, eventManager1);
    }

    static Business create(String name, Location entrance, Location exit, int price, EventManager eventManager) {
        return create(0, name, BusinessType.None, LtrpPlayer.INVALID_USER_ID, DEFAULT_PICKUP_MODEL, price, entrance, exit, DEFAULT_BUSINESS_LABEL_COLOR, 0, DEFAULT_RESOURCES, DEFAULT_COMMODITY_LIMIT, eventManager);
    }

    static List<BusinessCommodity> getAvailableCommodities(BusinessType type) {
        return PropertyController.get().getAvailableCommodities(type);
    }

    int getMoney();
    void addMoney(int amount);
    BusinessType getBusinessType();
    void setBusinessType(BusinessType type);
    List<BusinessCommodity> getCommodities();
    void addCommodity(int index, BusinessCommodity commodity);
    void addCommodity(BusinessCommodity commodity);
    void removeCommodity(BusinessCommodity commodity);
    void removeCommodity(int index);

    /**
     * Not to be confused with resources
     * @return returns the amount of commodities(wares) this business sells
     */
    int getCommodityCount();

    /**
     *
     * @return returns the maximum amount of commodities(wares) this business can sell at a time
     */
    int getCommodityLimit();

    /**
     * Current stock of "resources", previously known as "products"
     * @return current resource stock
     */
    int getResources();
    void setResources(int amount);
    int getEntrancePrice();
    void setEntrancePrice(int entrancePrice);
    int getResourcePrice();
    void setResourcePrice(int price);

    void showCommodities(LtrpPlayer player);
    void showCommodities(LtrpPlayer player, BusinessCommodityListDialog.SelectCommodityHandler selectCommodityHandler);

    DynamicPickup getPickup();




}
