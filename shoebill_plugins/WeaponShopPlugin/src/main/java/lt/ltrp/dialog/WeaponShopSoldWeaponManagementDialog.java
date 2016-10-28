package lt.ltrp.dialog;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.WeaponShop;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.14.
 *
 *         This dialog must provide full {@link lt.ltrp.data.WeaponShopWeapon} management for the provided weapon shop instance
 */
public class WeaponShopSoldWeaponManagementDialog {
    public static ListDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, WeaponShop weaponShop) {
        return ListDialog.create(player, eventManager)
                .caption(weaponShop.getName() + " parduotuv�s preki� valdymas(" + weaponShop.getSoldWeaponCount() + ")")
                .buttonOk("Pasirinkti")
                .buttonCancel("Atgal")
                .parentDialog(parentDialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .item("Pa�alinti esam� daikt�",
                        () -> weaponShop.getSoldWeaponCount() > 0,
                        i -> WeaponShopWeaponRemoveDialog.create(player, eventManager, i.getCurrentDialog(), weaponShop).show())
                .item("Prid�ti dar vien� prek�", i -> WeaponShopWeaponAddDialog.INSTANCE.create(player, eventManager, null, weaponShop).show())
                .item("Redaguoti esam� prek�",
                        () -> weaponShop.getSoldWeaponCount() > 0,
                        i -> WeaponShopWeaponPropertyManagementDialog.create(player, eventManager, i.getCurrentDialog(), weaponShop).show())
                .build();
    }
}
