package lt.ltrp.dialog;

import lt.ltrp.house.weed.data.HouseWeedSapling;
import lt.ltrp.house.object.House;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;import net.gtaun.shoebill.common.dialog.ListDialog;import net.gtaun.shoebill.common.dialog.ListDialogItem;import net.gtaun.shoebill.common.dialog.TabListDialog;import net.gtaun.shoebill.common.dialog.TabListDialogItem;import net.gtaun.util.event.EventManager;

import java.lang.Integer;import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * @author Bebras
 *         2016.05.16.
 */
public class HouseWeedListDialog {

    public static ListDialog create(LtrpPlayer player, EventManager eventManager, ListDialog parnt, House house) {
        Collection<ListDialogItem> items = new ArrayList<>();
        house.getWeedSaplings().forEach(w -> {
            items.add(TabListDialogItem.create()
                            .column(0, ListDialogItem.create().itemText(Integer.toString(w.getId())).data(w).build())
                            .column(1, ListDialogItem.create().itemText(new Date(w.getPlantTimestamp() * 1000).toString()).build())
                            .column(2, ListDialogItem.create().itemText(w.isGrown() ? "Taip" : " Ne").build())
                            .data(w)
                            .build()
            );
        });
        return TabListDialog.create(player, eventManager)
                .caption("Namo þolës valdymas")
                .header(0, "ID")
                .header(1, "Pasodintas")
                .header(2, "Uþaugæs")
                .items(items)
                .buttonOk("Pasirinkti")
                .buttonCancel("Atgal")
                .parentDialog(parnt)
                .onClickCancel(AbstractDialog::showParentDialog)
                .onClickOk((d, i) -> {
                    HouseWeedOptionDialog.create(player, eventManager, i.getCurrentDialog(), (HouseWeedSapling)i.getData()).show();
                })
                .build();
    }
}
