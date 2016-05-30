package lt.ltrp.dialog;

import lt.ltrp.GraffitiPlugin;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class GraffitiListDialog {


    public static ListDialog create(LtrpPlayer player, EventManager eventManager) {
        Collection<ListDialogItem> items = new ArrayList<>();
        GraffitiPlugin.get(GraffitiPlugin.class).getGraffiti().forEach(g -> {
            items.add(
                    ListDialogItem.create()
                            .data(g)
                            .itemText(String.format("{%s}%s(%.1fm)",
                                    g.getColor().getColor().toRgbHexString(),
                                    StringUtils.limit(g.getText(), 16),
                                    player.getLocation().distance(g.getPosition())))
                            .onSelect(i -> {
                                // TODO
                            })
                            .build()
            );
        });
        return PageListDialog.create(player, eventManager)
                .caption("Grafiti valdymas")
                .buttonOk("Pasirinkti")
                .buttonCancel("Uþdaryti")
                .build();
    }
}
