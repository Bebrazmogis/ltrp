package lt.ltrp.object;

import lt.ltrp.data.LtrpWeaponData;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface WeaponItem extends Item {

    boolean drawWeapon(LtrpPlayer player, Inventory inventory);
    void setWeaponData(LtrpWeaponData weaponData);
    LtrpWeaponData getWeaponData();


}
