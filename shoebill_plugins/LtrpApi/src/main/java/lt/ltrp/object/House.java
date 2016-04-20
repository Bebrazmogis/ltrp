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

    static House create(int id, String name, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit, net.gtaun.shoebill.data.Color labelColor, EventManager eventManager) {
        return PropertyController.get().createHouse(id, name, ownerUserId, pickupModelId, price, entrance, exit, labelColor, eventManager);
    }

    static House create(int id, Location entrance, Location exit, int price, EventManager eventManager1) {
        return create(id, "", LtrpPlayer.INVALID_USER_ID, House.DEFAULT_PICKUP_MODEL, price, entrance, exit, House.DEFAULT_HOUSE_LABEL_COLOR, eventManager1);
    }

    public boolean isUpgradeInstalled(HouseUpgradeType upgradeType);
    public void addUpgrade(HouseUpgradeType upgradeType);
    public List<HouseWeedSapling> getWeedSaplings();
    public void setWeedSaplings(List<HouseWeedSapling> weedSaplings);
    HouseRadio getRadio();

    // Legacy code for Pawn
    public int GetHouseIndex();
    public int getHouseRent();
    public void addBankMoney(int amount);


}
