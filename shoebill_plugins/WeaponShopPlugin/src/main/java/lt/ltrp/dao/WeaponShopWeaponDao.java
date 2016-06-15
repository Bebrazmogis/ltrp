package lt.ltrp.dao;


import lt.ltrp.data.WeaponShopWeapon;
import lt.ltrp.object.WeaponShop;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.06.14.
 */
public interface WeaponShopWeaponDao {

    Collection<WeaponShopWeapon> get(WeaponShop shop);
    WeaponShopWeapon get(int uuid);
    void update(WeaponShopWeapon weapon);
    void remove(WeaponShopWeapon weapon);
    void insert(WeaponShopWeapon weapon);

}
