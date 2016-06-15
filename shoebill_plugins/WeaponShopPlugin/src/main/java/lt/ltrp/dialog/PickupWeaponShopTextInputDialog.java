package lt.ltrp.dialog;

import lt.ltrp.WeaponShopPlugin;
import lt.ltrp.event.WeaponShopUpdateEvent;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PickupWeaponShop;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class PickupWeaponShopTextInputDialog {
    public static InputDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, PickupWeaponShop weaponShop) {
        return InputDialog.create(player, eventManager)
                .caption(weaponShop.getName() + " parduotuv�s teksto keitimas")
                .parentDialog(parentDialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .buttonOk("Keisti")
                .buttonCancel("Atgal")
                .line("Pasirinktas tekstas bus rodomas �aid�jui u�lipus ant parduotuv�s pickup.")
                .line(weaponShop.getText() != null ? "Dabartinis tekstas: " + weaponShop.getText() : "")
                .line("�veskite nauj� tekst�, palikite tu��i� jei norite kad tekstas neb�t� rodomas")
                .onClickOk((d, t) -> {
                    if(t.isEmpty())
                        t = null;
                    weaponShop.setText(t);
                    player.sendMessage("Parduotuv�s tekstas pakeistas");
                    eventManager.dispatchEvent(new WeaponShopUpdateEvent(weaponShop));
                    WeaponShopPlugin.get(WeaponShopPlugin.class).getShopDao().update(weaponShop);
                    parentDialog.show();
                })
                .build();
    }
}
