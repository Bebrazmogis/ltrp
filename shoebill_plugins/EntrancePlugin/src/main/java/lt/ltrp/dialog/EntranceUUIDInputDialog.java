package lt.ltrp.dialog;

import lt.ltrp.object.Entrance;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class EntranceUUIDInputDialog {

    public static InputDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, ClickOkHandler handler) {
        return IntegerInputDialog.create(player, eventManager)
                .parentDialog(parent)
                .caption("Áëjimo ID ávedimas")
                .message("Áveskite áëjimo, kurá norite redaguoti ID.")
                .buttonOk("Tæsti")
                .buttonCancel("Atgal")
                .onClickCancel(AbstractDialog::showParentDialog)
                .onClickOk((d, i) -> {
                    Entrance entrance = Entrance.get(i);
                    if (entrance == null) {
                        player.sendErrorMessage("Tokio áëjimo nëra.");
                        d.show();
                    } else {
                        if (handler != null)
                            handler.onClickOk(d, entrance);
                    }
                })
                .build();
    }

    @FunctionalInterface
    public interface ClickOkHandler {
        void onClickOk(InputDialog dialog, Entrance entrance);
    }
}
