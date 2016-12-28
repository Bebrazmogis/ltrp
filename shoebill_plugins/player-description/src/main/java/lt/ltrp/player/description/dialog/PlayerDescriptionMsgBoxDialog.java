package lt.ltrp.player.description.dialog;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.01.
 *
 *         Shows the target players description
 */
public class PlayerDescriptionMsgBoxDialog {



    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, LtrpPlayer target) {
        return MsgboxDialog.create(player, eventManager)
                .caption(target.getCharName() + " veikëjo apraðymo perþiûra")
                .message(StringUtils.addLineBreaks(target.getDescription(), 40))
                .buttonOk("Gerai")
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .build();
    }

}
