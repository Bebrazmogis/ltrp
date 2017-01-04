package lt.ltrp.dialog;

import lt.ltrp.WeaponShopPlugin;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.WeaponShop;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class WeaponShopWeaponRemoveDialog {

    public static WeaponShopWeaponListDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, WeaponShop weaponShop) {
         return WeaponShopWeaponListDialog.create(player, eventManager, weaponShop.getSoldWeapons())
                 .caption("Pasirinkite prek� kuri� norite pa�alinti")
                 .buttonOk("�alinti")
                 .buttonCancel("Atgal")
                 .parentDialog(parentDialog)
                 .onClickCancel(AbstractDialog::showParentDialog)
                 .onSelect((d, i) -> {
                     weaponShop.removeSoldWeapon(i);
                     WeaponShopPlugin.get(WeaponShopPlugin.class).getShopWeaponDao().remove(i);
                     player.sendErrorMessage("Parduotuv� nebeprekiaus �ia preke.");
                     parentDialog.show();
                 })
                .build();
    }
}
