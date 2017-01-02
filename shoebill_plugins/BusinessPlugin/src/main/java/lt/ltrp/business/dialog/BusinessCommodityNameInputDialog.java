package lt.ltrp.business.dialog;

import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.02.
 */
public class BusinessCommodityNameInputDialog {

    public static InputDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, InputDialog.ClickOkHandler clickOkHandler) {
        return InputDialog.create(player, eventManager)
                .caption("Prekës kûrimas: pavadinimas")
                .buttonOk("Kurti")
                .buttonCancel("Atgal")
                .parentDialog(parent)
                .message("Áveskite daikto pavadinimà.\nÁvestas pavadinimas bus rodomas versluose")
                .onClickOk((d, s) -> {
                    if(s == null || s.isEmpty()) {
                        d.show();
                    } else {
                        if(clickOkHandler != null) clickOkHandler.onClickOk(d, s);
                    }
                })
                .onClickCancel(AbstractDialog::showParentDialog)
                .build();
    }

}
