package lt.ltrp.event;

import lt.ltrp.object.WeaponShop;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class WeaponShopUpdateEvent extends WeaponShopEvent {
    public WeaponShopUpdateEvent(WeaponShop weaponShop) {
        super(weaponShop);
    }
}
