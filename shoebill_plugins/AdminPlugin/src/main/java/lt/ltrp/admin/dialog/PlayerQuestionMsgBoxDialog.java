package lt.ltrp.dialog;

import lt.ltrp.data.PlayerQuestion;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author Bebras
 *         2016.05.25.
 */
public class PlayerQuestionMsgBoxDialog {
    public static MsgboxDialog create(Player player, EventManager eventManager, AbstractDialog parentDialog, PlayerQuestion question) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("EEE HHmmss").withZone(ZoneId.systemDefault());
        return MsgboxDialog.create(player, eventManager)
                .caption("Raporto perþiûra")
                .buttonOk("Atgal")
                .line("Praneðëjas: " + question.getPlayer().getName())
                .line("Data: " + formatter.format(question.getInstant()))
                .line("Atsakytas: " + (question.isAnswered() ? "Taip" : "Ne"))
                .line("Klausimas: ")
                .line(StringUtils.addLineBreaks(question.getQuestion(), 40))
                .parentDialog(parentDialog)
                .onClickOk(AbstractDialog::showParentDialog)
                .build();
    }
}
