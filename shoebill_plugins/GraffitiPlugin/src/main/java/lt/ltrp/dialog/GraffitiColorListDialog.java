package lt.ltrp.dialog;

import lt.ltrp.GraffitiPlugin;
import lt.ltrp.data.GraffitiColor;
import lt.ltrp.event.GraffitiEditEvent;
import lt.ltrp.object.Graffiti;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class GraffitiColorListDialog {
    public static ListDialog create(LtrpPlayer player, EventManager eventManager, Graffiti graffiti) {
        Collection<ListDialogItem> items = new ArrayList<>();
        int count = 0;
        for(GraffitiColor c : GraffitiPlugin.get(GraffitiPlugin.class).getGraffitiColors()) {
            items.add(
                    ListDialogItem.create()
                            .data(c)
                            .itemText(String.format("%d. {%s}%s", count++, c.getColor().toRgbHexString(), graffiti.getText()))
                            .onSelect(i -> {
                                graffiti.setColor(c);
                                player.sendMessage(c.getColor(), "Spalva pakeistas sëkmingai");
                                eventManager.dispatchEvent(new GraffitiEditEvent(graffiti, player));
                            })
                            .build()
            );
        }
        return ListDialog.create(player, eventManager)
                .caption(() -> String.format("{%s}Grafiti teksto keitimas", graffiti.getColor().getColor().toRgbHexString()))
                .buttonOk("Keisti")
                .buttonCancel("Uþdaryti")
                .items(items)
                .build();
    }
}
