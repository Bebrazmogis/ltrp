package lt.ltrp.business.dialog;

import lt.ltrp.BusinessController;
import lt.ltrp.object.Business;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.27.
 */
public class BusinessDestroyMsgBoxDialog {


    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, Business b) {
        return MsgboxDialog.create(player, eventManager)
                .caption("{FF1100}Verslo naikinimas")
                .line("Dëmesio! Ðio veiksmo atstatyti neámanoma.")
                .line("\nVerslas bus paðalintas negráþtamai, o jame esantys pinigai (" + b.getMoney() + ") paðalinti.")
                .line("\nAr tikrai norite paðalinti verslà \"" + b.getName() + "\"?")
                .buttonOk("Taip")
                .buttonCancel("Ne")
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .onClickOk(d -> {
                    BusinessController.get().getBusinessDao().remove(b);
                    b.destroy();
                })
                .build();
    }

}
