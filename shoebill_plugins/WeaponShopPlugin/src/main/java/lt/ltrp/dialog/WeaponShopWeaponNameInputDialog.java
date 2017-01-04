package lt.ltrp.dialog;

import lt.ltrp.WeaponShopPlugin;
import lt.ltrp.data.WeaponShopWeapon;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class WeaponShopWeaponNameInputDialog {
    public static InputDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, WeaponShopWeapon weapon) {
        return InputDialog.create(player, eventManager)
                .caption("Prek�s pavadinimo keitimas")
                .buttonOk("Keisti")
                .buttonCancel("Atgal")
                .line("Dabartinis prek�s pavadinimas: " + weapon.getName())
                .line("Jeigu paliksite pavadinim� tu��i�, bus naudojamas ginklo pavadinimas(angli�kas)")
                .line("�veskite nauj� pavadinim�:")
                .parentDialog(parentDialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .onClickOk((d, s) -> {
                    if (s.isEmpty())
                        s = null;
                    weapon.setName(s);
                    player.sendMessage("Prek�s pavadinimas atnaujintas");
                    WeaponShopPlugin.get(WeaponShopPlugin.class).getShopWeaponDao().update(weapon);
                    parentDialog.show();
                })
                .build();
    }
}
