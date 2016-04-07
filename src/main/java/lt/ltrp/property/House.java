package lt.ltrp.property;

import lt.ltrp.Util.PawnFunc;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class House extends Property {

    private static List<House> houseList = new ArrayList<>();

    public static House create(int uniqueid, String name, EventManager eventManager1) {
        return create(uniqueid, name, null, null, eventManager1);
    }

    public static House create(int uniqueid, String name, Location entrance, Location exit, EventManager eventManager) {
        House property = new House(uniqueid, name, eventManager);
        property.setEntrance(entrance);
        property.setExit(exit);

        houseList.add(property);
        return property;

    }

    public static House get(int id) {
        for (House h : houseList) {
            if (h.getUid() == id) {
                return h;
            }
        }
        return null;
    }

    private List<HouseWeedSapling> weedSaplings;
    private HouseRadio radio;

    public House(int uniqueid, String name, EventManager manager) {
        super(uniqueid, name, manager);
        radio = new HouseRadio(this, eventManager);
    }

    public HouseRadio getRadio() {
        return radio;
    }

    public boolean isUpgradeInstalled(HouseUpgradeType upgradeType) {
        int index = GetHouseIndex();
        AmxCallable isUpgradInstalled = PawnFunc.getPublicMethod("IsHouseUpgradeInstalled");
        int value = 0;
        if(index != -1 && isUpgradInstalled != null) {
            value = (Integer)isUpgradInstalled.call(index, upgradeType.id);
        }
        return value == 1;
    }

    public void addUpgrade(HouseUpgradeType upgradeType) {
        int index = GetHouseIndex();
        AmxCallable addUpgrade = PawnFunc.getPublicMethod("AddHouseUpgrade");
        if(index != -1 && addUpgrade != null) {
            addUpgrade.call(index, upgradeType.id);
        }
    }


    public List<HouseWeedSapling> getWeedSaplings() {
        return weedSaplings;
    }

    public void setWeedSaplings(List<HouseWeedSapling> weedSaplings) {
        this.weedSaplings = weedSaplings;
    }


    // Legacy code for Pawn
    public int GetHouseIndex() {
        AmxCallable getIndex = PawnFunc.getPublicMethod("GetHouseIndex");
        if(getIndex != null) {
            return (Integer)getIndex.call(this.getUid());
        }
        return -1;
    }

    public int getHouseRent() {
        AmxCallable getRent = PawnFunc.getPublicMethod("GetHouseRent");
        if(getRent != null) {
            return (Integer)getRent.call(GetHouseIndex());
        }
        return 0;
    }

    public void addBankMoney(int amount) {
        AmxCallable addBankMoney =PawnFunc.getPublicMethod("AddHouseBankMoney");
        if(addBankMoney != null) {
            addBankMoney.call(GetHouseIndex(), amount);
        }
    }


}
