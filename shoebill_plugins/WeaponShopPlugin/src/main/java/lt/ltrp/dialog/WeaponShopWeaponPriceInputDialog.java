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
                .caption("Prekës kainos keitimas")
                .buttonOk("Keisti")
                .buttonCancel("Atgal")
                .line("Áveskite naujà pardavimo kainà")
                .line("Kaina negali bûti neigiama ar lygi 0.")
                .onClickOk((d, i) -> {
                    if (i <= 0)
                        d.show();
                    else {
                        weapon.setPrice(i);
                        player.sendMessage("Kaina sëkmingai pakeista");
                        WeaponShopPlugin.get(WeaponShopPlugin.class).getShopWeaponDao().update(weapon);
                        parentDialog.show();
                    }
                })
                .parentDialog(parentDialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .build();
    }
}
