package lt.ltrp.dao;

import lt.ltrp.object.WeaponShop;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.06.14.
 */
public interface WeaponShopDao {

    Collection<WeaponShop> get();
    Collection<WeaponShop> getWithWeapons();
    WeaponShop get(int uuid);
    void update(WeaponShop shop);
    void remove(WeaponShop shop);
    void insert(WeaponShop shop);

}
