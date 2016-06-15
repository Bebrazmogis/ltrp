package lt.ltrp.event;

import lt.ltrp.object.WeaponShop;
import net.gtaun.util.event.Event;

/**
 * @author Bebras
 *         2016.06.14.
 */
public abstract class WeaponShopEvent extends Event {

    private WeaponShop weaponShop;

    public WeaponShopEvent(WeaponShop weaponShop) {
        this.weaponShop = weaponShop;
    }

    public WeaponShop getWeaponShop() {
        return weaponShop;
    }
}
