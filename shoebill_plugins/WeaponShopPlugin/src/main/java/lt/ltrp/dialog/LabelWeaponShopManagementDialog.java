package lt.ltrp.dialog;

import lt.ltrp.object.LabelWeaponShop;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class LabelWeaponShopManagementDialog {

    public static ListDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog dialog, LabelWeaponShop weaponShop) {
        return ListDialog.create(player, eventManager)
                .caption(weaponShop.getName() + " parduotuvës redagavimas.")
                .buttonOk("Pasirinkti")
                .parentDialog(dialog)
                .buttonCancel("Atgal")
                .onClickCancel(AbstractDialog::showParentDialog)
                .item("Keisti pavadinimà", i -> WeaponShopNameInputDialog.create(player, eventManager, i.getCurrentDialog(), weaponShop).show())
                .item("Perkelti buvimo vietà á mano pozicijà", i -> WeaponShopLocationDialog.create(player, eventManager, i.getCurrentDialog(), weaponShop).show())
                .item("Redaguoti 3D etiketës tekstà", i -> LabelWeaponShopTextInputDialog.create(player, eventManager, i.getCurrentDialog(), weaponShop).show())
                .item("Redaguoti 3D teksto etiketës spalvà", i -> LabelWeaponShopColorInputDialog.create(player, eventManager, i.getCurrentDialog(), weaponShop).show())
                .item("Tvarkyti parduodamus ginklus", i -> WeaponShopSoldWeaponManagementDialog.create(player, eventManager, i.getCurrentDialog(), weaponShop).show())
                .build();
    }

}
