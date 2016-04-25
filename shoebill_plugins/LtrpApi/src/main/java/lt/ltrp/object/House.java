package lt.ltrp.object;


import lt.ltrp.PropertyController;
import lt.ltrp.constant.HouseUpgradeType;
import lt.ltrp.data.Color;
import lt.ltrp.data.HouseRadio;
import lt.ltrp.data.HouseWeedSapling;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface House extends Property, InventoryEntity {

    static final int DEFAULT_PICKUP_MODEL = 19524;
    static final Color DEFAULT_HOUSE_LABEL_COLOR = new Color(0xFFA500FF);

    static Collection<House> get() {
        return PropertyController.get().getHouses();
    }

    static House get(int id) {
        Optional<House> op = get()
                .stream()
                .filter(b -> b.getUUID() == id)
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }


    static House getClosest(Location location, float maxDistance) {
        Optional<House> op = get().stream().min((b1, b2) -> {
            return Float.compare(
                    Math.min(b1.getEntrance().distance(location), b1.getExit().distance(location)),
                    Math.min(b2.getEntrance().distance(location), b2.getExit().distance(location))
            );
        });
        if(op.isPresent()) {
            float distanceToEntrance = op.get().getEntrance().distance(location);
            float distanceToExit = op.get().getExit().distance(location);
            if(distanceToEntrance <= maxDistance || distanceToExit <= 80f) {
                return op.get();
            }
        }
        return null;
    }

    static House create(int id, String name, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit,
                        Color labelColor, int money, int rentprice, EventManager eventManager) {
        return PropertyController.get().createHouse(id, name, ownerUserId, pickupModelId, price, entrance, exit, labelColor, money, rentprice, eventManager);
    }

    static House create(int id, Location entrance, Location exit, int price, EventManager eventManager1) {
        return create(id, "", LtrpPlayer.INVALID_USER_ID, House.DEFAULT_PICKUP_MODEL, price, entrance, exit, House.DEFAULT_HOUSE_LABEL_COLOR, 0, 0, eventManager1);
    }

    public boolean isUpgradeInstalled(HouseUpgradeType upgradeType);
    public void addUpgrade(HouseUpgradeType upgradeType);
    public List<HouseWeedSapling> getWeedSaplings();
    public void setWeedSaplings(List<HouseWeedSapling> weedSaplings);
    HouseRadio getRadio();
    int getMoney();
    void setMoney(int money);
    void addMoney(int money);
    int getRentPrice();
    void setRentPrice(int price);
    List<Integer> getTenants();
    void sendTenantMessage(String message);


}
