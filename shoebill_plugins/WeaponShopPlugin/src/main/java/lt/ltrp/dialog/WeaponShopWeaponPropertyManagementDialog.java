package lt.ltrp.dialog;

import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.WeaponShop;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class WeaponShopWeaponPropertyManagementDialog {
    public static ListDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, WeaponShop weaponShop) {
        return WeaponShopWeaponListDialog.create(player, eventManager, weaponShop.getSoldWeapons())
                .caption("Pasirinkite prekæ kurià norite redaguoti")
                .parentDialog(parentDialog)
                .buttonOk("Redaguoti")
                .buttonCancel("Atgal")
                .onClickCancel(AbstractDialog::showParentDialog)
                .onSelect((d, w) -> {
                    WeaponShopWeaponPropertyManagementDialog2.create(player, eventManager, d, w)
                            .show();
                })
                .build();
    }
}
