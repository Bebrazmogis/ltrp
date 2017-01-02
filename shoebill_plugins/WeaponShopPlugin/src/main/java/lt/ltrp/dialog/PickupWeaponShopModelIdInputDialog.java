package lt.ltrp.dialog;

import lt.ltrp.WeaponShopPlugin;
import lt.ltrp.event.WeaponShopUpdateEvent;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.PickupWeaponShop;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class PickupWeaponShopModelIdInputDialog {
    public static IntegerInputDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, PickupWeaponShop weaponShop) {
        return SampModelInputDialog.create(player, eventManager)
                .caption(weaponShop.getName() + " parduotuv�s pickup modelio keitimas")
                .parentDialog(parentDialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .buttonOk("Keisti")
                .buttonCancel("Atgal")
                .line("Pickup modelis gali b�ti bet koks objektas")
                .line("Populiar�s")
                .line("� 954 - pasaga")
                .line("� 1210 - lagaminas")
                .line("� 1212 - pinig� kr�va")
                .line("� 1239 - informacijos ikona")
                .line("� 1240 - gyvyb�s ikona")
                .line("� 1241 - adrenalino ikona")
                .line("� 1242 - �arv� ikona")
                .line("� 1247 - �vaig�d�s ikona")
                .line("� 1254 - kaukuol�s ikona")
                .line("� 1272 - m�lyno namelio ikona")
                .line("� 1273 - �alio namelio ikona")
                .line("� 19522 - raudono namelio ikona")
                .line("� 19523 - oran�inio namelio ikona")
                .line("� 19524 - geltono namelio ikona")
                .line("� 1274 - dolerio simbolio ikona")
                .line("� 1318 - balta rodykl� rodanti �emyn")
                .line("� 1650 - kuro bakelis")
                .line("� 1654 - dinamintas")
                .line("� 2035 - M4 ginklas")
                .line("� 2061 - du sviediniai")
                .line("� 2690 - gesintuvas")
                .line("� 11738 - medik� krep�ys")
                .line("�veskite nauj� model�.")
                .onClickOk((d, m) -> {
                    weaponShop.setModelId(m);
                    player.sendMessage("Parduotuv�s pickup modelis s�kmingai atnaujintas");
                    eventManager.dispatchEvent(new WeaponShopUpdateEvent(weaponShop));
                    WeaponShopPlugin.get(WeaponShopPlugin.class).getShopDao().update(weaponShop);
                    parentDialog.show();
                })
                .build();
    }
}
