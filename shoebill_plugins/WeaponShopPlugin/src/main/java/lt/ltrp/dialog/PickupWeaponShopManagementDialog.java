package lt.ltrp.dialog;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.impl.PickupWeaponShopImpl;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class PickupWeaponShopManagementDialog {


    public static ListDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, PickupWeaponShopImpl weaponShop) {
        return ListDialog.create(player, eventManager)
                .caption(weaponShop.getName() + " parduotuv�s valdymas")
                .buttonOk("Pasirinkti")
                .buttonCancel("Atgal")
                .parentDialog(parentDialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .item("Keisti pavadinim�", i -> WeaponShopNameInputDialog.create(player, eventManager, i.getCurrentDialog(), weaponShop).show())
                .item("Perkelti buvimo viet� � mano pozicij�", i -> WeaponShopLocationDialog.create(player, eventManager, i.getCurrentDialog(), weaponShop).show())
                .item("Redaguoti pickup tekst�", i -> PickupWeaponShopTextInputDialog.create(player, eventManager, i.getCurrentDialog(), weaponShop).show())
                .item("Redaguoti pickup model�", i -> PickupWeaponShopModelIdInputDialog.create(player, eventManager, i.getCurrentDialog(), weaponShop).show())
                .item("Tvarkyti parduodamus ginklus", i -> WeaponShopSoldWeaponManagementDialog.create(player, eventManager, i.getCurrentDialog(), weaponShop).show())
                .build();
    }
}
