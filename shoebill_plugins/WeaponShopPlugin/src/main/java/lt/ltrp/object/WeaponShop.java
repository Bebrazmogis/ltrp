package lt.ltrp.object;

import lt.ltrp.WeaponShopPlugin;
import lt.ltrp.data.WeaponShopWeapon;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.data.Location;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.06.14.
 */
public interface WeaponShop extends DestroyableEntity {

    static final int MIN_NAME_LENGTH = 15;
    static final int MAX_NAME_LENGTH = 128;

    static Collection<WeaponShop> get() {
        return WeaponShopPlugin.get(WeaponShopPlugin.class).getWeaponShops();
    }

    void setName(String name);
    String getName();


    Location getLocation();
    void setLocation(Location location);

    Collection<WeaponShopWeapon> getSoldWeapons();
    boolean isSellingWeapon(WeaponModel model);
    WeaponShopWeapon getSoldWeapon(WeaponModel model);
    void addSoldWeapon(WeaponShopWeapon weapon);
    void removeSoldWeapon(WeaponShopWeapon weapon);
    int getSoldWeaponCount();

    void showManagementDialog(LtrpPlayer player);
    void showManagementDialog(LtrpPlayer player, AbstractDialog parentDialog);

    void showSoldWeaponDialog(LtrpPlayer player);
}
