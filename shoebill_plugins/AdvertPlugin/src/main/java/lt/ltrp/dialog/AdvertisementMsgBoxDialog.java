package lt.ltrp.dialog;

import lt.ltrp.data.Advert;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManagerNode;

import java.text.SimpleDateFormat;

/**
 * @author Bebras
 *         2016.06.01.
 */
public class AdvertisementMsgBoxDialog {
    public static MsgboxDialog create(LtrpPlayer player, EventManagerNode eventManager, AbstractDialog parent, Advert ad) {
        return MsgboxDialog.create(player, eventManager)
                .caption("Reklama")
                .line("Reklamos data: " + new SimpleDateFormat().format(ad.getCreatedAt()))
                .line("Kontaktinis telefonas: " + ad.getPhoneNumber())
                .line("\n")
                .line(StringUtils.addLineBreaks(ad.getAdText(), 40))
                .buttonOk("Gerai")
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .onClickOk(AbstractDialog::showParentDialog)
                .build();
    }
}
