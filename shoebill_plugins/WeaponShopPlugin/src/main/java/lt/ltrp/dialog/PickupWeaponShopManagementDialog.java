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
                .caption(weaponShop.getName() + " parduotuvës valdymas")
                .buttonOk("Pasirinkti")
                .buttonCancel("Atgal")
                .parentDialog(parentDialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .item("Keisti pavadinimà", i -> WeaponShopNameInputDialog.create(player, eventManager, i.getCurrentDialog(), weaponShop).show())
                .item("Perkelti buvimo vietà á mano pozicijà", i -> WeaponShopLocationDialog.create(player, eventManager, i.getCurrentDialog(), weaponShop).show())
                .item("Redaguoti pickup tekstà", i -> PickupWeaponShopTextInputDialog.create(player, eventManager, i.getCurrentDialog(), weaponShop).show())
                .item("Redaguoti pickup modelá", i -> PickupWeaponShopModelIdInputDialog.create(player, eventManager, i.getCurrentDialog(), weaponShop).show())
                .item("Tvarkyti parduodamus ginklus", i -> WeaponShopSoldWeaponManagementDialog.create(player, eventManager, i.getCurrentDialog(), weaponShop).show())
                .build();
    }
}
