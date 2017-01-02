package lt.ltrp.dialog;

import lt.ltrp.WeaponShopPlugin;
import lt.ltrp.data.WeaponShopWeapon;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class WeaponShopWeaponAmmoInputDialog {
    public static IntegerInputDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, WeaponShopWeapon weapon) {
        return IntegerInputDialog.create(player, eventManager)
                .caption("Prekës amunicijos keitimas")
                .buttonOk("Keisti")
                .buttonCancel("Atgal")
                .line("Áveskite naujà amunicijos kieká.")
                .line("Tai yra kiekis, kurá gaus þaidëjas nusipirkæs ðá ginklà(" + weapon.getName() + ")")
                .line("Ginklams be amunicijos tai turëtø bûti 1.")
                .parentDialog(parentDialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .onClickOk((d, i) -> {
                    if (i <= 0)
                        d.show();
                    else {
                        weapon.setAmmo(i);
                        player.sendMessage("Prekës amunicijos kiekis sëkmingai atnaujintas.");
                        WeaponShopPlugin.get(WeaponShopPlugin.class).getShopWeaponDao().update(weapon);
                        parentDialog.show();
                    }
                })
                .build();
    }
}
