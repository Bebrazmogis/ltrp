package lt.ltrp.dialog;

import lt.ltrp.WeaponShopPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.event.WeaponShopUpdateEvent;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.WeaponShop;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class WeaponShopLocationDialog {
    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, WeaponShop weaponShop) {
        return MsgboxDialog.create(player, eventManager)
                .caption("\t\t\t\tDëmesio!")
                .buttonOk("Taip")
                .buttonCancel("Ne")
                .parentDialog(parentDialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .line("Pakeitus pozicijà, atstatyti buvusios bus neámanoma.")
                .line("Dëmesio. Ar tikrai norite perkelti paruodutvës \"" + weaponShop.getName() + "\" vietà á jûsø pozicijà?")
                .onClickOk(d -> {
                    weaponShop.setLocation(player.getLocation());
                    eventManager.dispatchEvent(new WeaponShopUpdateEvent(weaponShop));
                    player.sendMessage(Color.NEWS, "Parduotuvës pozicija perkelta prie jûsø.");
                    WeaponShopPlugin.get(WeaponShopPlugin.class).getShopDao().update(weaponShop);
                    parentDialog.show();
                })
                .build();
    }
}
