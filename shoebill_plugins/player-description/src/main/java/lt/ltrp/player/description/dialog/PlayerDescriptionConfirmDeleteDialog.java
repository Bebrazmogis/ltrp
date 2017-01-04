package lt.ltrp.player.description.dialog;


import lt.ltrp.data.Color;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.PlayerController;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.01.
 *
 *         This dialog is a confirmation for deletion of target players description
 */
public class PlayerDescriptionConfirmDeleteDialog {

    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, LtrpPlayer target) {
        return MsgboxDialog.create(player, eventManager)
                .caption(target.getCharName() + " veik�jo apra�ymo �alinimas")
                .buttonOk("�alinti")
                .buttonCancel("Atgal")
                .message("Ar tikrai norite pa�alinti veik�jo " + target.getCharName() + " apra�ym�?")
                .line("Apra�ymas bus i�trintas nesugr��inamai.")
                .line("Dabartinis apra�ymas:\n")
                .line(StringUtils.addLineBreaks(target.getDescription(), 60))
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .onClickOk(d -> {
                    target.setDescription(null);
                    PlayerController.instance.update(target);
                    player.sendMessage(Color.NEWS, " Veik�jo apra�ymas s�kmingai pa�alintas.");
                    if(parent != null) parent.show();
                })
                .build();
    }

}

