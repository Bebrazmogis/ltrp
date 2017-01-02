package lt.ltrp.house.weed.dialog;

import lt.ltrp.house.object.House;
import lt.ltrp.house.weed.object.HouseWeedSapling;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.TabListDialog;
import net.gtaun.shoebill.common.dialog.TabListDialogItem;
import net.gtaun.util.event.EventManager;

import java.lang.Integer;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.16.
 */
public class HouseWeedListDialog {

    public static ListDialog create(LtrpPlayer player, EventManager eventManager, ListDialog parnt, House house) {
        Collection<ListDialogItem> items = new ArrayList<>();
        house.getWeedSaplings().forEach(w -> {
            items.add(TabListDialogItem.create()
                            .column(0, ListDialogItem.create().itemText(Integer.toString(w.getUUID())).data(w).build())
                            .column(1, ListDialogItem.create().
                                    itemText(
                                            w.getPlantedAt().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
                                    )
                                    .build())
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
