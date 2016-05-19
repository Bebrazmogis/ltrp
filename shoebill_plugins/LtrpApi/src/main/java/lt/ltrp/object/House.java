package lt.ltrp.object;


import lt.ltrp.HouseController;
import lt.ltrp.constant.HouseUpgradeType;
import lt.ltrp.data.Color;
import lt.ltrp.data.HouseRadio;
import lt.ltrp.data.HouseWeedSapling;
import net.gtaun.shoebill.data.Location;

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
    static final int MIN_NAME_LENGTH = 5;

    static Collection<House> get() {
        return HouseController.get().getHouses();
    }

    static House get(int id) {
        Optional<House> op = get()
                .stream()
                .filter(b -> b.getUUID() == id)
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }

    static House get(LtrpPlayer player) {
        return getHouse(player.getLocation());
    }
    static House getHouse(Location location) {
        return HouseController.get().getHouse(location);
    }

    static House getClosestHouse(LtrpPlayer player, float maxDistance) {
        return getClosest(player.getLocation(), maxDistance);
    }

    static House getClosest(Location location, float maxDistance) {
        return HouseController.get().getClosest(location, maxDistance);
    }

    static House getClosest(Location location) {
        return getClosest(location, Float.MAX_VALUE);
    }

    static House create(int id, String name, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit,
                        Color labelColor, int money, int rentprice) {
        return HouseController.get().createHouse(id, name, ownerUserId, pickupModelId, price, entrance, exit, labelColor, money, rentprice);
    }

    static House create(int id, Location entrance, Location exit, int price) {
        return create(id, "namas", LtrpPlayer.INVALID_USER_ID, House.DEFAULT_PICKUP_MODEL, price, entrance, exit, House.DEFAULT_HOUSE_LABEL_COLOR, 0, 0);
    }

    boolean isUpgradeInstalled(HouseUpgradeType upgradeType);
    void addUpgrade(HouseUpgradeType upgradeType);
    void removeUpgrade(HouseUpgradeType up);
    List<HouseWeedSapling> getWeedSaplings();
    Collection<HouseUpgradeType> getUpgrades();
    void setWeedSaplings(List<HouseWeedSapling> weedSaplings);
    HouseRadio getRadio();
    int getMoney();
    void setMoney(int money);
    void addMoney(int money);
    int getRentPrice();
    void setRentPrice(int price);
    List<Integer> getTenants();
    void sendTenantMessage(String message);


}
