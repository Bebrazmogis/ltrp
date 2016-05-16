package lt.ltrp.dialog.property.business;

import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
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
                .line("Dëmesio! Ğio veiksmo atstatyti neámanoma.")
                .line("\nVerslas bus pağalintas negráştamai, o jame esantys pinigai (" + b.getMoney() + ") pağalinti.")
                .line("\nAr tikrai norite pağalinti verslà \"" + b.getName() + "\"?")
                .buttonOk("Taip")
                .buttonCancel("Ne")
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .onClickOk(d -> {
                    b.destroy();
                })
                .build();
    }

}
