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
                .caption("\t\t\t\tD�mesio!")
                .buttonOk("Taip")
                .buttonCancel("Ne")
                .parentDialog(parentDialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .line("Pakeitus pozicij�, atstatyti buvusios bus ne�manoma.")
                .line("D�mesio. Ar tikrai norite perkelti paruodutv�s \"" + weaponShop.getName() + "\" viet� � j�s� pozicij�?")
                .onClickOk(d -> {
                    weaponShop.setLocation(player.getLocation());
                    eventManager.dispatchEvent(new WeaponShopUpdateEvent(weaponShop));
                    player.sendMessage(Color.NEWS, "Parduotuv�s pozicija perkelta prie j�s�.");
                    WeaponShopPlugin.get(WeaponShopPlugin.class).getShopDao().update(weaponShop);
                    parentDialog.show();
                })
                .build();
    }
}
