package lt.ltrp.item.object;

import lt.ltrp.player.data.LtrpWeaponData;
import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface WeaponItem extends Item {

    boolean drawWeapon(LtrpPlayer player, Inventory inventory);
    void setWeaponData(LtrpWeaponData weaponData);
    LtrpWeaponData getWeaponData();


}
