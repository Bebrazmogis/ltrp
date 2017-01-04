package lt.ltrp.dialog;

import lt.ltrp.WeaponShopPlugin;
import lt.ltrp.colorpicker.ColorPicker;
import lt.ltrp.colorpicker.VehicleColorPicker;
import lt.ltrp.data.Color;
import lt.ltrp.object.LabelWeaponShop;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class LabelWeaponShopColorInputDialog {
    public static ColorPicker create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, LabelWeaponShop weaponShop) {
        return VehicleColorPicker.create(player, eventManager)
                .onCancel(vc -> {
                    if (parentDialog != null)
                        parentDialog.show();
                })
                .onSelectColor((cp, c) -> {
                    weaponShop.setColor(new Color(c));
                    player.sendMessage("Parduotuvës etiketës spalva sëkmingai pakeista");
                    WeaponShopPlugin.get(WeaponShopPlugin.class).getShopDao().update(weaponShop);
                    parentDialog.show();
                })
                .build();
    }
}
