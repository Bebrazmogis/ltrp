package lt.ltrp.house.object;


import lt.ltrp.house.HouseController;
import lt.ltrp.house.upgrade.constant.HouseUpgradeType;

import lt.ltrp.data.HouseRadio;
import lt.ltrp.house.rent.object.HouseTenant;
import lt.ltrp.house.upgrade.data.HouseUpgrade;
import lt.ltrp.house.weed.object.HouseWeedSapling;
import lt.ltrp.object.Entity;
import lt.ltrp.object.InventoryEntity;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.Property;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface House extends Property, InventoryEntity {

    int DEFAULT_PICKUP_MODEL = 19524;
    Color DEFAULT_HOUSE_LABEL_COLOR = new Color(0xFFA500FF);
    int MIN_NAME_LENGTH = 5;


    static Collection<House> get() {
        return HouseController.get().getAll();
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
        return HouseController.get().get(location);
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
        return HouseController.get().create(id, name, ownerUserId, pickupModelId, price, entrance, exit, labelColor, money, rentprice);
    }

    static House create(int id, Location entrance, Location exit, int price) {
        return create(id, "namas", Entity.Companion.getINVALID_ID(), House.DEFAULT_PICKUP_MODEL, price, entrance, exit, House.DEFAULT_HOUSE_LABEL_COLOR, 0, 0);
    }

    boolean isUpgradeInstalled(HouseUpgradeType upgradeType);
    void addUpgrade(HouseUpgrade upgrade);
    void removeUpgrade(HouseUpgrade upgrade);
    List<HouseWeedSapling> getWeedSaplings();
    Set<HouseUpgrade> getUpgrades();
    void setWeedSaplings(List<HouseWeedSapling> weedSaplings);
    HouseRadio getRadio();
    int getMoney();
    void setMoney(int money);
    void addMoney(int money);
    int getRentPrice();
    void setRentPrice(int price);
    Collection<HouseTenant> getTenants();
    void sendTenantMessage(String message);


}
