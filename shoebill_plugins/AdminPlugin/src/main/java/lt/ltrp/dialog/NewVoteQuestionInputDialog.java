package lt.ltrp.dialog;

import lt.ltrp.AdminPlugin;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.13.
 */
public class NewVoteQuestionInputDialog {

    public static InputDialog create(LtrpPlayer player, AbstractDialog dialog, EventManager eventManager) {
        return InputDialog.create(player, eventManager)
                .caption("Naujo balsavimo k�rimas")
                .parentDialog(dialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .line("�veskite naujo balsavimo klausim�")
                .line("Vieninteliai galimi klausimo atsakymai: taip ir ne, tad ra�ykite klausim� atitinkamai.")
                .buttonOk("Prad�ti")
                .buttonCancel("Atgal")
                .onClickOk((d, q) -> {
                    if (q == null || q.isEmpty())
                        d.show();
                    else {
                        AdminPlugin.get(AdminPlugin.class).startVote(player, q);
                    }
                })
                .build();
    }


}
