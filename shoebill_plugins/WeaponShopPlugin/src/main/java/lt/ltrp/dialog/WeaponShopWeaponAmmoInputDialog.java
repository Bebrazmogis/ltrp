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
                .caption("Prek�s amunicijos keitimas")
                .buttonOk("Keisti")
                .buttonCancel("Atgal")
                .line("�veskite nauj� amunicijos kiek�.")
                .line("Tai yra kiekis, kur� gaus �aid�jas nusipirk�s �� ginkl�(" + weapon.getName() + ")")
                .line("Ginklams be amunicijos tai tur�t� b�ti 1.")
                .parentDialog(parentDialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .onClickOk((d, i) -> {
                    if (i <= 0)
                        d.show();
                    else {
                        weapon.setAmmo(i);
                        player.sendMessage("Prek�s amunicijos kiekis s�kmingai atnaujintas.");
                        WeaponShopPlugin.get(WeaponShopPlugin.class).getShopWeaponDao().update(weapon);
                        parentDialog.show();
                    }
                })
                .build();
    }
}
