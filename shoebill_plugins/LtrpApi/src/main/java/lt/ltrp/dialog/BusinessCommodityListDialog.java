package lt.ltrp.dialog;

import lt.ltrp.constant.Currency;
import lt.ltrp.data.property.business.commodity.BusinessCommodity;
import lt.ltrp.event.property.SelectCommodityEvent;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.TabListDialog;
import net.gtaun.shoebill.common.dialog.TabListDialogItem;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessCommodityListDialog {

    public static TabListDialog create(LtrpPlayer player, EventManager eventManager, List<BusinessCommodity> commodities, SelectCommodityHandler handler) {
        Collection<ListDialogItem> items = new ArrayList<>();
        commodities.stream().sorted((c1, c2) -> Integer.compare(c1.getNumber(), c2.getNumber())).forEach(c -> {
            items.add(TabListDialogItem.create()
                            .column(0, ListDialogItem.create().itemText(c.getName()).build())
                            .column(1, ListDialogItem.create().itemText(String.format("%c %d", Currency.SYMBOL, c.getPrice())).build())
                            .data(c)
                            .build()
            );
        });
        return TabListDialog.create(player, eventManager)
                .caption("Verslo prekës")
                .header(0, "Pavadinimas")
                .header(1, "Kaina," + Currency.SYMBOL)
                .items(items)
                .buttonOk("Pirkti")
                .buttonCancel("Uþdaryti")
                .onClickOk((d, i) -> {
                    if(handler != null) handler.onSelectCommodity(d, player, (BusinessCommodity)i.getData());
                    eventManager.dispatchEvent(new SelectCommodityEvent(player, (BusinessCommodity)i.getData()));
                })
                .build();
    }

    @FunctionalInterface
    public interface SelectCommodityHandler {
        void onSelectCommodity(ListDialog dialog, LtrpPlayer player, BusinessCommodity commodity);
    }

}
