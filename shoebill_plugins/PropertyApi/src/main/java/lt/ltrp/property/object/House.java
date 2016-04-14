package lt.ltrp.property.object;


import lt.ltrp.item.object.InventoryEntity;
import lt.ltrp.property.PropertyController;
import lt.ltrp.property.constant.HouseUpgradeType;
import lt.ltrp.property.data.HouseRadio;
import lt.ltrp.property.data.HouseWeedSapling;
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

    static House create(int uniqueid, String name, EventManager eventManager1) {
        return create(uniqueid, name, null, null, eventManager1);
    }

    static House create(int uniqueid, String name, Location entrance, Location exit, EventManager eventManager) {
        return PropertyController.get().createHouse(uniqueid, name, entrance, exit, eventManager);
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
