package lt.ltrp.dialog;

import lt.ltrp.WeaponShopPlugin;
import lt.ltrp.data.WeaponShopWeapon;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class WeaponShopWeaponPriceInputDialog {
    public static IntegerInputDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, WeaponShopWeapon weapon) {
        return IntegerInputDialog.create(player, eventManager)
                .caption("Prek�s kainos keitimas")
                .buttonOk("Keisti")
                .buttonCancel("Atgal")
                .line("�veskite nauj� pardavimo kain�")
                .line("Kaina negali b�ti neigiama ar lygi 0.")
                .onClickOk((d, i) -> {
                    if (i <= 0)
                        d.show();
                    else {
                        weapon.setPrice(i);
                        player.sendMessage("Kaina s�kmingai pakeista");
                        WeaponShopPlugin.get(WeaponShopPlugin.class).getShopWeaponDao().update(weapon);
                        parentDialog.show();
                    }
                })
                .parentDialog(parentDialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .build();
    }
}
