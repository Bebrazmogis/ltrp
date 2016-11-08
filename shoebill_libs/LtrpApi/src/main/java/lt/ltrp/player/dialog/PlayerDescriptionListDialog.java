package lt.ltrp.player.dialog;

import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.01.
 *
 *         Shows the player description management dialog to user
 */
public class PlayerDescriptionListDialog {


    public static ListDialog create(LtrpPlayer player, EventManager eventManager) {
        return create(player, eventManager, player);
    }

    public static ListDialog create(LtrpPlayer player, EventManager eventManager, LtrpPlayer target) {
        return ListDialog.create(player, eventManager)
                .caption(target.getCharName() + "Veikëjo apraðymo valdymas")
                .buttonOk("Pasirinkti")
                .buttonCancel("Uþdaryti")
                .item("Perþiûrëti apraðymà",
                        () -> target.getDescription() != null,
                        i -> PlayerDescriptionMsgBoxDialog.create(player, eventManager, i.getCurrentDialog(), target).show()
                )
                .item("Pridëti teksto á apraðymà",
                        i -> PlayerDescriptionInputDialog.create(player, eventManager, i.getCurrentDialog(), target).show())
                .item("Paðalinti apraðymà",
                        () -> target.getDescription() != null,
                        i -> PlayerDescriptionConfirmDeleteDialog.create(player, eventManager, i.getCurrentDialog(), target).show())
                .build();
    }

}
