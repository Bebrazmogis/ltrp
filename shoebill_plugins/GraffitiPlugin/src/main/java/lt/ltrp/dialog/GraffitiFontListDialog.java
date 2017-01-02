package lt.ltrp.dialog;

import lt.ltrp.GraffitiPlugin;
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
public class GraffitiFontListDialog {
    public static ListDialog create(LtrpPlayer player, EventManager eventManager, Graffiti graffiti) {
        Collection<ListDialogItem> items = new ArrayList<>();
        GraffitiPlugin.get(GraffitiPlugin.class).getGraffitiFontCollection().forEach(f -> {
            items.add(
                    ListDialogItem.create()
                            .data(f)
                            .itemText(String.format("{%s}%s(%d)", graffiti.getFont().equals(f) ? "111111" : "FFFFFF", f.getName(), f.getSize()))
                            .onSelect(i -> {
                                graffiti.setFont(f);
                                player.sendMessage("Ðriftas pakeistas á " + graffiti.getFont().getName());
                                eventManager.dispatchEvent(new GraffitiEditEvent(graffiti, player));
                            })
                            .build()
            );
        });
        return ListDialog.create(player, eventManager)
                .caption("Grafiti ðrifto keitimas")
                .items(items)
                .buttonOk("Pasirinkti")
                .buttonCancel("Uþdaryti")
                .build();
    }
}
