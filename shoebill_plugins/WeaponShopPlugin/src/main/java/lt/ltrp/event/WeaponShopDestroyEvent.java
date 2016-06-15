package lt.ltrp.event;

import lt.ltrp.object.WeaponShop;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class WeaponShopDestroyEvent extends WeaponShopEvent {

    public WeaponShopDestroyEvent(WeaponShop weaponShop) {
        super(weaponShop);
    }
}
