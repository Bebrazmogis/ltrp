package lt.ltrp.dialog;

import lt.ltrp.LtrpWorld;
import lt.ltrp.constant.ItemType;
import lt.ltrp.data.WeaponShopWeapon;
import lt.ltrp.item.ItemFactory;
import lt.ltrp.object.Item;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.WeaponItem;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.15.
 */
public class WeaponShopSoldWeaponConfirmDialog {
    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog dialog, WeaponShopWeapon weapon) {
        String name = weapon.getName() != null ? weapon.getName() : weapon.getWeaponModel().getName();
        return MsgboxDialog.create(player, eventManager)
                .caption(name + " pirkimas")
                .buttonOk("Pirkti")
                .buttonCancel("Atgal")
                .line("Kaina: " + (weapon.getPrice() < player.getMoney() ? "{00AA00}" : "{AA0000}") + weapon.getPrice() + "{FFFFFF}")
                .line("Kiekis:" +  weapon.getAmmo())
                .line("Ar tikrai norite pirkti \"" + name + "\"")
                .parentDialog(dialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .onClickOk(d -> {
                    if (player.getMoney() < weapon.getPrice())
                        player.sendErrorMessage("Jums neuþtenka pinigø!");
                    else if(player.getInventory().isFull())
                        player.sendErrorMessage("Jûsø inventorius pilnas, atlaisvinkite vietos ir bandykite ið naujo");
                    else {
                        player.giveMoney(-weapon.getPrice());
                        LtrpWorld.get().addMoney(weapon.getPrice());
                        player.sendMessage("Sëkmingai nusipirkote " + name);
                        player.getInventory().add(ItemFactory.Companion.getInstance().createWeapon(weapon.getWeaponModel(), weapon.getAmmo(), player));
                    }
                    dialog.show();
                })
                .build();
    }
}
