package lt.ltrp.dialog;

import lt.ltrp.data.WeaponShopWeapon;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class WeaponShopWeaponPropertyManagementDialog2 {
    public static ListDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, WeaponShopWeapon weapon) {
        return ListDialog.create(player, eventManager)
                .caption("Prekës " + weapon.getName() + " redagavimas")
                .buttonOk("Pasirinkti")
                .buttonCancel("Atgal")
                .parentDialog(parentDialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .item("Keisti pavadinimà", i -> WeaponShopWeaponNameInputDialog.create(player, eventManager, i.getCurrentDialog(), weapon).show())
                .item("Keisti kainà", i -> WeaponShopWeaponPriceInputDialog.create(player, eventManager, i.getCurrentDialog(), weapon).show())
                .item("Keisti parduodamà kieká", i -> WeaponShopWeaponAmmoInputDialog.create(player, eventManager, i.getCurrentDialog(), weapon).show())
                .build();
    }
}
