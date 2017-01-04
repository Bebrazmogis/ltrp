package lt.ltrp.dialog;

import lt.ltrp.constant.ItemType;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2016.04.20.
 */
public class ItemTypeListDialog {

    public static ListDialog create(LtrpPlayer player, EventManager eventManager, List<ItemType> itemTypeList, ItemTypeSelectHandler handler) {
        List<ListDialogItem> items = new ArrayList<>();
        itemTypeList.forEach(t -> {
            items.add(ListDialogItem.create()
                    .itemText(t.name())
                    .onSelect(i -> {
                        if(handler != null)
                            handler.onSelectItemType(i.getCurrentDialog(), t);
                    })
                    .build());
        });
        return ListDialog.create(player, eventManager)
                .caption("Daiktø tipo pasirinkimas")
                .buttonOk("Pasirinkti")
                .buttonCancel("Atgal")
                .onClickCancel(AbstractDialog::showParentDialog)
                .items(items)
                .build();
    }


    @FunctionalInterface
    public interface ItemTypeSelectHandler {
        void onSelectItemType(ListDialog dialog, ItemType type);
    }

}
