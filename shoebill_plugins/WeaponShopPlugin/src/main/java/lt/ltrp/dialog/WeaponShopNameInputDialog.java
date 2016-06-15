package lt.ltrp.dialog;

import lt.ltrp.WeaponShopPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.event.WeaponShopUpdateEvent;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.WeaponShop;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class WeaponShopNameInputDialog {
    public static InputDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, WeaponShop weaponShop) {
        return InputDialog.create(player, eventManager)
                .caption("Ginkl� parduotuv�s pavadinimo keitimas")
                .parentDialog(parentDialog)
                .buttonOk("Keisti")
                .buttonCancel("Atgal")
                .onClickCancel(AbstractDialog::showParentDialog)
                .line("Dabartinis parduotuv�s pavadinimas:")
                .line(StringUtils.addLineBreaks(weaponShop.getName(), 40))
                .line("\n")
                .line("�veskite nauj� ginkl� parduotuv�s pavadinim�")
                .line("Minimalus simboli� skai�ius: " + WeaponShop.MIN_NAME_LENGTH + " maksimalus simboli� skai�ius: " + WeaponShop.MAX_NAME_LENGTH)
                .onClickOk((d, n) -> {
                    if (n.length() < WeaponShop.MIN_NAME_LENGTH || n.length() > WeaponShop.MAX_NAME_LENGTH)
                        d.show();
                    else {
                        weaponShop.setName(n);
                        eventManager.dispatchEvent(new WeaponShopUpdateEvent(weaponShop));
                        WeaponShopPlugin.get(WeaponShopPlugin.class).getShopDao().update(weaponShop);
                        player.sendMessage(Color.NEWS, "Parduotuv�s pavadinimas s�kmingai atnaujintas.");
                        parentDialog.show();
                    }
                })
                .build();
    }
}
