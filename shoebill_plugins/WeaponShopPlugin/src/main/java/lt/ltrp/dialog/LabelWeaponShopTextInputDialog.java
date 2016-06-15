package lt.ltrp.dialog;

import lt.ltrp.WeaponShopPlugin;
import lt.ltrp.event.WeaponShopUpdateEvent;
import lt.ltrp.object.LabelWeaponShop;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class LabelWeaponShopTextInputDialog {
    public static InputDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, LabelWeaponShop weaponShop) {
        return InputDialog.create(player, eventManager)
                .caption("Parduotuvës teksto keitimas")
                .buttonOk("Keisti")
                .buttonCancel("Atgal")
                .line(weaponShop.getText() != null ? "Dabartinis tekstas: " + StringUtils.addLineBreaks(weaponShop.getText(), 40) : "")
                .line("Áveskite naujà tekstà")
                .onClickCancel(AbstractDialog::showParentDialog)
                .onClickOk((d, s) -> {
                    if (s.isEmpty())
                        s = null;
                    weaponShop.setText(s);
                    player.sendMessage("Parduotuvës tekstas sëkmingai atnaujintas");
                    eventManager.dispatchEvent(new WeaponShopUpdateEvent(weaponShop));
                    WeaponShopPlugin.get(WeaponShopPlugin.class).getShopDao().update(weaponShop);
                    parentDialog.show();
                })
                .build();
    }
}
