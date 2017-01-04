package lt.ltrp.dialog;

import lt.ltrp.constant.Currency;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.WeaponShop;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.TabListDialogItem;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.06.15.
 */
public class WeaponShopSoldWeaponListDialog {
    public static ListDialog create(LtrpPlayer player, EventManager eventManager, WeaponShop shop) {
        Collection<ListDialogItem> items = new ArrayList<>();
        shop.getSoldWeapons().forEach(w -> {
            items.add(TabListDialogItem.create()
                            .column(0, ListDialogItem.create().itemText(w.getName()).build())
                            .column(1, ListDialogItem.create().itemText("" + w.getPrice() + Currency.SYMBOL).build())
                            .data(w)
                            .onSelect((i) -> {
                                WeaponShopSoldWeaponConfirmDialog.create(player, eventManager, i.getCurrentDialog(), w)
                                        .show();
                            })
                            .build()
            );
        });
        return ListDialog.create(player, eventManager)
                .caption(shop.getName())
                .items(items)
                .buttonOk("Pirkti")
                .buttonCancel("Uþdaryti")
                .build();
    }
}
