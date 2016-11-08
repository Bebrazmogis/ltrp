package lt.ltrp.player.dialog;

import lt.ltrp.data.Color;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.player.PlayerController;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.01.
 *
 *         This dialog appends text to a target players description
 */
public class PlayerDescriptionInputDialog {

    public static InputDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, LtrpPlayer target) {
        return InputDialog.create(player, eventManager)
                .caption(target.getCharName() + " veik�jo apra�ymo pildymas")
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .buttonOk("Saugoti")
                .buttonCancel("Atgal")
                .line("�ra�ykite, k� norite prid�ti prie veik�jo apra�ymo")
                .line("\nDabartinis tekstas:")
                .line(StringUtils.addLineBreaks(target.getDescription(), 60))
                .onClickOk((d, s) -> {
                    target.setDescription(target.getDescription() + s);
                    PlayerController.instance.update(target);
                    player.sendMessage(Color.NEWS, "Apra�ymas s�kmingai papildytas");
                    if(parent != null) parent.show();
                })
                .build();
    }
}
