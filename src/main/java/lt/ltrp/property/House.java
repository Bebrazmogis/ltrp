package lt.ltrp.property;

import lt.ltrp.Util.PawnFunc;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.data.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class House extends Property {

    private static List<House> houseList = new ArrayList<>();

    public static House create(int uniqueid, String name) {
        return create(uniqueid, name, null, null);
    }

    public static House create(int uniqueid, String name, Location entrance, Location exit) {
        House property = new House(uniqueid, name);
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

    public House(int uniqueid, String name) {
        super(uniqueid, name);
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



}
