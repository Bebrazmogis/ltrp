package lt.ltrp.dialog;

import lt.ltrp.WeaponShopPlugin;
import lt.ltrp.data.WeaponShopWeapon;
import lt.ltrp.object.Entity;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.WeaponShop;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class WeaponShopWeaponAddDialog {
    public static WeaponModelSelectDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, WeaponShop weaponShop) {
        return WeaponModelSelectDialog.create(player, eventManager)
                .caption("Pasirinkite ginklà kurá norite pridët kaip prekæ")
                .buttonOk("Pridëti")
                .buttonCancel("Atgal")
                .parentDialog(parentDialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .onSelectWeapon((d, w) -> {
                    WeaponShopWeapon weapon = new WeaponShopWeapon(Entity.Companion.getINVALID_ID(), weaponShop, null, w, 0, 0);
                    WeaponShopPlugin.get(WeaponShopPlugin.class).getShopWeaponDao().insert(weapon);
                })
                .build();
    }
}
